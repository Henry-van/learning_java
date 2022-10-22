package com.guangh.fan.frame;

import com.guangh.fan.entity.Mail;
import com.guangh.fan.mailUtils.ReceiveMail;
import com.guangh.fan.mailUtils.mailUtil;
import com.guangh.fan.util.*;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

import static com.guangh.fan.consts.Consts.DELETE_PATH;
import static com.guangh.fan.consts.Consts.SENDED_PATH;

public class SendedFrame extends JInternalFrame implements MouseListener {
    private JScrollPane scrollPane = null; // 正文编辑窗口
    private JTextPane sendCotent; // 发送内容面板
    // 属性定义
    private HTMLDocument document = null; // 声明一个网页文档对象变量
    private JTable mailTable = null; //
    private DefaultTableModel tableModel = null;

    private ReceiveMail receiveMail;
    private Object[] columnTitle = {"收件人" , "主题" , "发送时间", "附件"}; // 定义邮件Table的列标题
    private Object[][] tableData = null; // 邮件Table数据

    List<Mail> msgList = null;
    Message[] msgArray = null;
    String [] fileName = null;
    String curDir = "";

    public SendedFrame() {
        super("已发送邮件");

        initData();
        initUI();
    }

    private void initData() {
        // 本地获取已发送邮件 Start ************************************
        int messageCount = initFromLocal();
        // 本地获取已发送邮件 End ************************************

        // 服务器获取已发送邮件 Start ################################
//        receiveMail = new ReceiveMail("imap", "sent", false);
//        receiveMail.receive();
//        int messageCount = receiveMail.getMessageCount();
//        msgList = receiveMail.getMailList();
        // 服务器获取已发送邮件 End ################################

        //定义一维数据作为列标题
        tableData = new Object[messageCount][4];

        for (int i = 0; i < messageCount; i++) {
            Mail mail = msgList.get(i);
            try {
                tableData[i][0] = mail.getTo();
                tableData[i][1] = mail.getSubject();
                tableData[i][2] = mail.getSentDate();
                tableData[i][3] = mail.getAttachFileName();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int initFromLocal() {
        File f = new File("."); // 得到当前目录
        curDir = f.getPath();
        File dir = new File(curDir + SENDED_PATH);
        File[] temp = dir.listFiles();
        ArrayList<String> tempList = new ArrayList<>();

        for (int i = 0; i < temp.length; i++) {
            File file = temp[i];
            if ( file.isFile() && file.getName().endsWith(".eml") ) {
                tempList.add(file.getName());
            }
        }
        if (tempList.size()>0) {
            fileName = tempList.toArray(new String[tempList.size()]);
        }

        int messageCount = fileName.length;

        Session mailSession = mailUtil.getSession();
        msgList = new ArrayList<Mail>();
        msgArray = new Message[messageCount];

        for (int i = 0; i < messageCount; i++) {
            try {
                List<String> fileList = new ArrayList<>();
                File file = new File(fileName[i]);
                // 创建邮件对象
                FileInputStream fis = new FileInputStream(curDir + SENDED_PATH + file);
                Message msg = new MimeMessage(mailSession, fis);
                Mail mail = new Mail();
                mail.setFrom(mailUtil.getFrom(msg));
                mail.setSubject(mailUtil.getSubject(msg));
                mail.setSentDate(mailUtil.getSendDate(msg));

                mail.setContent(mailUtil.getMailContent(msg));
                mail.setHasAttach(mailUtil.isContainAttach(msg, fileList));
                if ( fileList != null && fileList.size() > 0) {
                    mail.setAttachFileName(fileList);
                }

                mail.setMsgNumber(msg.getMessageNumber());
                mail.setFlags(msg.getFlags());
                mail.setTo(mailUtil.getMailAddress("to", msg));
                msgList.add(mail);
                msgArray[i] = msg;
                fis.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return messageCount;
    }

    private void initUI() {
        this.setFrameIcon(EditorUtils.createIcon("send.png"));
        // 初始化基本项
        getContentPane().setLayout(new BorderLayout()); // 设置空布局
        setIconifiable(true); // 是否使 JInternalFrame 变成一个图标
        setClosable(true); // 是否关闭
        setMaximizable(true); // 窗口最大化设置
        setResizable(true); // 设置窗口可以调整大小
        setBounds(10,10,1024,768); // 设置界面大小
        setVisible(true);

        JScrollPane mailListPanel = new JScrollPane(); // 收件箱邮件列表显示面板

//        mailTable = new MyTable(tableData, columnTitle);
        tableModel = new DefaultTableModel(tableData, columnTitle);
        mailTable = new MyTable(tableModel);
        mailTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader tableHeader = mailTable.getTableHeader();
        DefaultTableCellRenderer dHr = (DefaultTableCellRenderer)tableHeader.getDefaultRenderer();
        dHr.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        mailListPanel.setViewportView(mailTable);
        mailListPanel.setPreferredSize(new Dimension(1024, 200));

        scrollPane = new JScrollPane(); // 邮件内容显示面板

        sendCotent = new JTextPane(); // html内容转换用面板，不显示
        sendCotent.setContentType("text/html");
        HTMLEditorKit editorKit = new HTMLEditorKit();
        document = (HTMLDocument) editorKit.createDefaultDocument(); // 创建默认文档指向网页引用 document
        sendCotent.setEditorKit(editorKit); // 设置为html格式的编辑器
        sendCotent.setDocument(document);
        sendCotent.addMouseListener(this);
        sendCotent.setEnabled(false);
        JLabel mailContentP = new JLabel();
        mailContentP.setVerticalAlignment(JLabel.TOP);
        mailContentP.setHorizontalAlignment(JLabel.LEFT);
        mailContentP.setBounds(0,0,1024,200);

        //        scrollPane.setViewportView(sendCotent);
        mailTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = mailTable.rowAtPoint(e.getPoint());
                String content = "";
                if (e.getButton() == 1) {
                    try {
                        content = msgList.get(row).getContent();

                        int index = content.indexOf("<html");
                        if (index > 0) {
                            content = content.substring(index);
                        } else if (index == -1) {
                            content = "<html>" + content + "</html>";
                        }
                        int index1 = content.indexOf("</html>");
                        int len = content.length();
                        if (index1 > 0 && index1 + 7 < content.length()) {
                            content = content.substring(0, index1 + 7);
                        }
                        document.setInnerHTML(document.getDefaultRootElement(), content);
                        mailContentP.setText(sendCotent.getText());
                        scrollPane.setViewportView(mailContentP);
                    } catch (EmptyStackException e2) {
                        mailContentP.setText(content);
                        scrollPane.setViewportView(mailContentP);
                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                } else if (e.getButton()==3) {
                    // 从服务器获取内容 Start
//                    Message[] msgArray = receiveMail.getMessages();
                    // 从服务器获取内容 End
                    Message clickedMsg = msgArray[row];

                    try {
                        final JPopupMenu popup = new JPopupMenu();
                        JMenuItem itemdel = new JMenuItem("删除");
                        itemdel.addActionListener( new ActionListener() {
                            public void actionPerformed(ActionEvent arg0) {
                                if (JOptionPane.showConfirmDialog(SendedFrame.this, "确定要删除该邮件吗？") == 0) {
                                    // 从本地删除邮件 Start ************************
                                    File delFile = new File(curDir + SENDED_PATH + fileName[row]);
                                    File objFile = new File(curDir + DELETE_PATH + fileName[row]);
                                    delFile.renameTo(objFile); // 把本地删除的邮件移动到 delete目录下
                                    mailContentP.setText(""); // 清空邮件正文显示区域
                                    tableModel.removeRow(row); // 从画面上邮件列表中删除
                                    // 从本地删除邮件 End ************************

                                    // 从服务器删除邮件 Start
//                                    try {
//                                        clickedMsg.setFlag(Flags.Flag.DELETED, true);
//                                        receiveMail.close();
//                                    } catch (MessagingException ex) {
//                                        ex.printStackTrace();
//                                    }
                                    // 从服务器删除邮件 End
                                }
                            }
                        });

                        popup.add(itemdel);
                        popup.show(e.getComponent(), e.getX(), e.getY()); // 显示弹出菜单
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        //编辑区面板
        JPanel editorPanel = new JPanel(new BorderLayout());
        // 编辑区
        editorPanel.add(scrollPane, BorderLayout.CENTER);
        // 添加一个分割窗口
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, mailListPanel, editorPanel);
        splitPane.setOneTouchExpandable(true); // 在分隔条上提供一个 UI 小部件来快速展开/折叠分隔条
        splitPane.setDividerSize(10); // 设置分隔条的大小

        // 整个界面编辑区
        JPanel framePanel = new JPanel(new BorderLayout());
        // 编辑区
        framePanel.add(splitPane, BorderLayout.CENTER);
        this.add(framePanel, BorderLayout.CENTER);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
