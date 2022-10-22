package com.guangh.fan.frame;

import com.guangh.fan.entity.AttachFile;
import com.guangh.fan.mailUtils.SendMail;
import com.guangh.fan.util.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

/**
 * 发送邮件窗口
 */
public class SendFrame extends JInternalFrame implements ActionListener, MouseListener, MouseMotionListener, FocusListener {
    private JComboBox fontSizeCB; // 字体大小列表
    private JComboBox fontCB; // 字体列表
    private JTextPane sendCotent; // 发送内容面板
    private JTextField subjectTF; // 邮件主体文本框
    private JTextField copyTo; // 抄送
    private JTextField toMail; // 收件人
    private JList attachmentList = null; // 附件列表，最多能添加三个附件
    private JScrollPane scrollPane = null; // 正文编辑窗口
    private JScrollPane jsp = null; // 用于显示附件
    private DefaultListModel listModel = null; // 附件列表模型
    private JLabel adjunctL = null; // 附件标签
    private JLabel toMailLabel = null, copyToLabel = null, subjectLabel = null;
    private JButton sendButton = null; // 发送按钮
    private JButton resetButton = null; // 重置
    private JButton attachmentButton = null; // 插入附件按钮
    private JButton selectColorButton = null; // 颜色选择按钮
    private Box baseBox = null,
            boxLabel = null, // 地址栏的标题区域
            boxInput = null; // 地址栏的输入框区域
    private ArrayList<String> attachArrayList = new ArrayList<String>(); // 用于存储附件路径的链表
    private Color color = Color.black;
    // 属性定义
    private Action boldAction = new StyledEditorKit.BoldAction(); // 添加加粗侦听器
    private Action underlineAction = new StyledEditorKit.UnderlineAction(); // 添加下划线侦听器
    private Action italicAction = new StyledEditorKit.ItalicAction(); // 添加斜体侦听器
    private HTMLDocument document = null; // 声明一个网页文档对象变量
    private SendMail sendMailService = null; //
    private ProgressFrame progressBar = null; // 进度条实例

    public SendFrame() {
        super("新邮件");
        this.setFrameIcon(EditorUtils.createIcon("newMail.png"));
        // 初始化基本项
        getContentPane().setLayout(new BorderLayout()); // 设置空布局
        setIconifiable(true); // 是否使 JInternalFrame 变成一个图标
        setClosable(true); // 是否关闭
        setMaximizable(true); // 窗口最大化设置
        setResizable(true); // 设置窗口可以调整大小
        setBounds(10,10,1024,768); // 设置界面大小
        setVisible(true);
        // 设置收件人标签
        toMailLabel = new JLabel();
        toMailLabel.setText("收件人：");
        // 抄送标签
        copyToLabel = new JLabel();
        copyToLabel.setText("抄送：");
        // 主题标签
        subjectLabel = new JLabel();
        subjectLabel.setText("主题：");
        // 收件人文本框
        toMail = new JTextField(60);
        toMail.addFocusListener(this);
        toMail.setToolTipText("将收件人地址以逗号分隔");
        // 抄送文本框
        copyTo = new JTextField(60);
        copyTo.addFocusListener(this);
        // 主题文本框
        subjectTF = new JTextField(60);
        JPanel setPanel = new JPanel(); // 上半部
        setPanel.add(AddressAreaBox());
        setPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
//        setPanel.setPreferredSize(new Dimension(1024, 200));
        scrollPane = new JScrollPane();
        sendCotent = new JTextPane();
        sendCotent.setContentType("text/html");
        HTMLEditorKit editorKit = new HTMLEditorKit();
        document = (HTMLDocument) editorKit.createDefaultDocument(); // 创建默认文档指向网页引用 document
        sendCotent.setEditorKit(editorKit); // 设置为html格式的编辑器
        sendCotent.setDocument(document);
        sendCotent.addMouseListener(this);
        scrollPane.setViewportView(sendCotent);

        // 工具条
        final  JToolBar toolBar = new JToolBar();
        getContentPane().add(toolBar);

        sendButton = new JButton("发送", EditorUtils.createIcon("newMailTool.png"));
        sendButton.addActionListener(this);
        toolBar.add(sendButton);

        resetButton = new JButton("重写", EditorUtils.createIcon("rewrite.png"));
        resetButton.addActionListener(this);
        toolBar.add(resetButton);

        // 附件列表
        listModel = new DefaultListModel();
        adjunctL = new JLabel("附件：");
        jsp = new JScrollPane(); // 用于显示JList
//        jsp.setPreferredSize(new Dimension(350,20));
        jsp.setPreferredSize(new Dimension(350,80));
        attachmentList = new JList(listModel);
        attachmentList.addMouseListener(this); // 为邮件列表添加鼠标事件
        jsp.setViewportView(attachmentList); // 设置JScrollPanel的视图为JList
        attachmentList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        attachmentList.setVisibleRowCount(1);
        attachmentList.setLayoutOrientation(JList.VERTICAL_WRAP);

        // 插入附件按钮
        attachmentButton = new JButton("插入附件", EditorUtils.createIcon("attach.png"));
        attachmentButton.addActionListener(this);
        toolBar.add(attachmentButton);

        // 斜体按钮
        JButton italicButton=new JButton(italicAction);
        italicButton.setIcon(EditorUtils.createIcon("italic.png"));
        italicButton.setText("");
        italicButton.setPreferredSize(new Dimension(22,22));
        // 粗体按钮
        JButton blodButton=new JButton(boldAction);
        blodButton.setIcon(EditorUtils.createIcon("bold.png"));
        blodButton.setText("");
        blodButton.setPreferredSize(new Dimension(22,22));
        // 下划线按钮
        JButton underlineButton=new JButton(underlineAction);
        underlineButton.setIcon(EditorUtils.createIcon("underline.png"));
        underlineButton.setText("");
        underlineButton.setPreferredSize(new Dimension(22,22));
        // 字体
        final JLabel fontLabel=new JLabel("字体");
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment(); // 获得本地计算机上字体可用的名称
        String font[] = ge.getAvailableFontFamilyNames();
        fontCB = new JComboBox(font);
        fontCB.addActionListener(this);// 字号列表
        final JLabel fontSizeLabel=new JLabel("字号");
        String fontSize[] = { "10", "11", "12", "13","14","16", "18","20",
            "22", "24", "26", "28", "36","48" };
        fontSizeCB = new JComboBox(fontSize);
        fontSizeCB.addActionListener(this);
        fontSizeCB.setPreferredSize(new Dimension(100,23));
        // 颜色
        final JLabel colorLabel = new JLabel("颜色");
        selectColorButton = new JButton("选 色");
        selectColorButton.addActionListener(this);
        JPanel editorToolBarPanel = new JPanel();

        //编辑区工具条
        editorToolBarPanel.add(italicButton);
        editorToolBarPanel.add(blodButton);
        editorToolBarPanel.add(underlineButton);
        editorToolBarPanel.add(new JLabel("  "));
        editorToolBarPanel.add(fontLabel);
        editorToolBarPanel.add(fontCB);
        editorToolBarPanel.add(new JLabel("  "));
        editorToolBarPanel.add(fontSizeLabel);
        editorToolBarPanel.add(fontSizeCB);
        editorToolBarPanel.add(new JLabel("  "));
        editorToolBarPanel.add(colorLabel);
        editorToolBarPanel.add(selectColorButton);
        editorToolBarPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        //编辑区面板
        JPanel editorPanel = new JPanel(new BorderLayout());
        // 编辑区
        editorPanel.add(editorToolBarPanel, BorderLayout.NORTH);
        editorPanel.add(scrollPane, BorderLayout.CENTER);
        // 添加一个分割窗口
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, setPanel, editorPanel);
        splitPane.setOneTouchExpandable(true); // 在分隔条上提供一个 UI 小部件来快速展开/折叠分隔条
        splitPane.setDividerLocation(0.5);
        splitPane.setDividerSize(10); // 设置分隔条的大小

        // 整个界面编辑区
        JPanel framePanel = new JPanel(new BorderLayout());
        // 编辑区
        framePanel.add(splitPane, BorderLayout.CENTER);
        this.add(framePanel, BorderLayout.CENTER);
        this.add(toolBar, BorderLayout.NORTH);
    }

    private Box AddressAreaBox() {
        // 创建标签 box
        boxLabel=Box.createVerticalBox();
        boxLabel.add(toMailLabel);
        boxLabel.add(Box.createVerticalStrut(10));
        boxLabel.add(copyToLabel);
        boxLabel.add(Box.createVerticalStrut(16));
        boxLabel.add(subjectLabel);
        boxLabel.add(Box.createVerticalStrut(12));

        // 创建文本框 box
        boxInput=Box.createVerticalBox();
        boxInput.add(toMail);
        boxInput.add(Box.createVerticalStrut(8));
        boxInput.add(copyTo);
        boxInput.add(Box.createVerticalStrut(8));
        boxInput.add(subjectTF);
        boxInput.add(Box.createVerticalStrut(8));
        // 创建基本 box
        baseBox=Box.createHorizontalBox();
        baseBox.add(boxLabel);
        baseBox.add(Box.createHorizontalStrut(20));
        baseBox.add(boxInput);
        return baseBox;
    }

    // 按钮事件的处理
    @Override
    public void actionPerformed(ActionEvent e) {
        if( e.getSource() == selectColorButton){
            // 选择颜色
            color = JColorChooser.showDialog(this,"请选择颜色",Color.BLACK);
            Action colorAction = new StyledEditorKit.ForegroundAction("set-foreground-", color); // 添加颜色侦听器
            // 添加颜色侦听器
            if(color!=null)
                colorAction.actionPerformed(new ActionEvent( color, 0, sendCotent.getSelectedText()));
        } else if( e.getSource() == fontCB ){
            // 字体设置
            String font= (String)fontCB.getSelectedItem();
            Action fontAction = new StyledEditorKit.FontFamilyAction(font,font);
            fontAction.actionPerformed(new ActionEvent(fontAction, 0, sendCotent.getSelectedText()));
        } else if( e.getSource() == fontSizeCB) {
            //字体大小设置
            String fontsize = (String) fontSizeCB.getSelectedItem();
            Action fontSizeAction = new StyledEditorKit.FontSizeAction(fontsize, Integer.parseInt(fontsize));
            fontSizeAction.actionPerformed(new ActionEvent(fontSizeAction, 0, sendCotent.getSelectedText()));
        } else if( e.getSource()==resetButton){
            //重置按钮事件
            reset();
        } else if( e.getSource() == attachmentButton){
            //插入附件
            addAttachment(); // 插入附件
        } else if( e.getSource() == sendButton) {// 发送邮件
            sendMail();// 发送邮件
        }
    }

    // 添加附件
    private void addAttachment() {
        if( listModel.getSize() >= 3 ){
            JOptionPane.showMessageDialog(this,"本邮件客户端最多只能添加三个附件！");
            return;
        }
        File f = new File("."); // 得到当前目录
        JFileChooser chooser = new JFileChooser(f); // 构造一个当前路径的文件选择器
        if( chooser.showOpenDialog(getContentPane()) == JFileChooser.APPROVE_OPTION ) { // 如果选择确定键
            File file = chooser.getSelectedFile();
            Icon icon = chooser.getIcon(file);
//            attachmentList.setCellRenderer(new CellRenderer(icon));
            attachmentList.setVisibleRowCount(listModel.getSize());
            attachmentList.setCellRenderer(new MyCellRenderer());
//            listModel.addElement(file.getName()); //将附件添加到JLIST中
            listModel.addElement(new AttachFile(file.getName(), icon));
            attachArrayList.add(file.getPath()); //将附件的路径添加到附件列表中
        }
        if (listModel.getSize() <= 1) {
            boxLabel.add(Box.createVerticalStrut(62));
            boxLabel.add(adjunctL);
            boxInput.add(jsp);
        }
        validate();
        repaint();
    }

    // 删除附件
    private void deleteAttachment(MouseEvent e){
        final JPopupMenu popup = new JPopupMenu();
        JMenuItem itemdel = new JMenuItem("删除");
        itemdel.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (attachmentList.getSelectedValue() == null) {
                    JOptionPane.showMessageDialog(SendFrame.this, "请您选择列表中需要删除的附件");
                    return;
                }
                int attachmentIndex = attachmentList.getSelectedIndex(); // 得到选择附件的索引号
                attachArrayList.remove(attachmentIndex); // 将附件路径链表中的对应值删除
                listModel.remove(attachmentIndex); //将列表模型中的附件删除
            }
        });
        popup.add(itemdel);
        popup.show(e.getComponent(), e.getX(), e.getY()); // 显示弹出菜单
    }

    /**
     *发送邮件
     **/
    public void sendMail() {

        String subject = subjectTF.getText().trim(); // 主题
        String text = sendCotent.getText().trim(); // 正文
        String toP = toMail.getText().trim(); // 收件人
        String copyP = copyTo.getText().trim(); // 抄送到

        if ( subject == null || "".equals(subject) ) {
            JOptionPane.showMessageDialog(this,"邮件标题不能为空！");
            return;
        }
        if ( toP == null || "".equals(toP) ) {
            JOptionPane.showMessageDialog(this,"收件人不能为空！");
            return;
        }

        // 初始化发送邮件对象
        sendMailService = new SendMail(subject, text, attachArrayList, toP, copyP);

        if (progressBar == null) {
            progressBar = new ProgressFrame(MainFrame.mainFrame, "发送邮件",
                    "正在发送邮件，请稍后...");
        }
        progressBar.setVisible(true);

        new Thread() { //开启一个新的线程发送邮件
            public void run() {
                String message = "";
                if ("".equals(message = sendMailService.send())) {
                    message = "邮件已发送成功！";
                } else {
                    message = "<html><h4>邮件发送失败！失败原因：</h4></html>\n" + message;
                }
                progressBar.dispose(); // 关闭发送邮件窗体
                JOptionPane.showMessageDialog(SendFrame.this, message, "提示",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }.start();

        this.dispose(); // 关闭发送邮件窗体
    }

    // 清空各种属性值
    private void reset() {
        sendCotent.setText("");
        subjectTF.setText("");
        copyTo.setText("");
        toMail.setText("");
        attachArrayList.clear();
        listModel.clear();
    }

    // 添加联系人到收件人
    public void addLinkman(String linkman) {
        if (focusStatic == 2) {// 判断抄送文本框是否得到焦点
            setJTextFieldString(copyTo, linkman);
            copyTo.requestFocus();// 抄送人文本框得到焦点
        } else {
            toMail.requestFocus();// 收件人文本框得到焦点
            setJTextFieldString(toMail, linkman);
        }
    }

    // 设置文本框中的字符串
    private void setJTextFieldString(JTextField jt, String linkman) {
        String copy_toString = jt.getText();
        if (!copy_toString.endsWith(";") && !copy_toString.equals(""))
            copy_toString += ";";
        copy_toString += linkman;
        jt.setText(copy_toString);
    }

    private int focusStatic = 1;// 1 代表收件人得到焦点，2代表抄送人得到焦点

    @Override
    public void focusGained(FocusEvent e) {
        if (e.getSource() == toMail)
            focusStatic = 1;
        else
            focusStatic = 2;
    }

    @Override
    public void focusLost(FocusEvent e) {

    }

    // 鼠标事件处理
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == attachmentList && e.getButton() == 3) {
            //鼠标按键getButton() 方法返回1表示按了左键盘，2表示按了中键盘，3表示按了右键盘
            deleteAttachment(e);// 删除附件
        }
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

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
