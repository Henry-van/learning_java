package com.guangh.fan.frame;

import com.guangh.fan.mailUtils.ReceiveMail;
import com.guangh.fan.util.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;

import static com.guangh.fan.consts.Consts.*;

public class MainFrame extends JFrame implements ActionListener, MouseListener {
    private static final long serialVersionUID = 1L;

    private static JDesktopPane desktopPane = null; // 用于创建多文档界面或虚拟桌面的容器
    public static MainFrame mainFrame; // 主窗体
    private JTree tree; // 树形图
    private JList contactsList; // 联系人列表
    private JPanel panel, panelFrame; // panelFrame左半部界面
    private JLabel mainFrameLabelBackground; // 主窗口区域背景Label
    private JScrollPane contactsPanel; // 联系人滚动框
    // 菜单项
    private JMenuItem exitMi = null, // 文件-退出
            newMainMi = null, // 邮件-新建邮件
            sendedMi = null, // 邮件-已发送
            receiveMi = null, // 邮件-收件箱
            recycleMi = null, // 邮件-已删除
            refreshMI = null; // 刷新收件箱
    private JButton addLinkmanButton = null; // 添加联系人按钮
    private JMenu mailMenu = null; // 主菜单
    private GetContractsList getContracts = null; // 联系人列表类
    private ProgressFrame progressBar = null; // 进度条实例
    private DefaultListModel listModel = null; // 联系人列表模型
    private Boolean clicked = false;

    // 初始化界面配置
    public void jFrameValidate() {
        Toolkit tk = getToolkit(); // 获取屏幕的宽度和高度
        Dimension sz = tk.getScreenSize();
        this.setBounds(0, 0, sz.width / 2, sz.height/2);
        validate();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // 主窗体
    public MainFrame() {
        super("邮件客户端");
        mainFrame = this;
        Toolkit toolkit=Toolkit.getDefaultToolkit(); // 获取Toolkit对象
        Image icon = toolkit.getImage(this.getClass().getResource("/assets/Icon/email.png")); // 获取图片对象
        this.setIconImage(icon); // 设置图标
//        this.setIconImage(EditorUtils.createIcon("email.png").getImage());
        desktopPane = new JDesktopPane();
        jFrameValidate();
        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);
        // 一级菜单
        final JMenu fileMenu = new JMenu("文件(F)");
        mailMenu = new JMenu("邮件(M)");
        menuBar.add(fileMenu);
        menuBar.add(mailMenu);
        // 二级菜单
        exitMi = addMenuItem(fileMenu, "退出", "exit.png"); // 退出菜单项的初始化
        newMainMi = addMenuItem(mailMenu, "发邮件", "newMail.png");
        sendedMi =  addMenuItem(mailMenu, "已发送", "send.png");
        receiveMi =  addMenuItem(mailMenu, "收件箱", "receive.png");
        recycleMi =  addMenuItem(mailMenu, "已删除", "delete.png");
        refreshMI = addMenuItem(mailMenu, "刷新收件箱", "refresh.png");


        // 构建树形节点
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("我的邮件");
        DefaultMutableTreeNode inbox = new DefaultMutableTreeNode("收件夹");
        DefaultMutableTreeNode outbox = new DefaultMutableTreeNode("发件夹");
        DefaultMutableTreeNode sent = new DefaultMutableTreeNode("已发送");
        DefaultMutableTreeNode delete = new DefaultMutableTreeNode("已删除");
        root.add(inbox);
        root.add(outbox);
        root.add(sent);
        root.add(delete);
        tree = new JTree(root);
        tree.addMouseListener(this); // 为树形节点注册鼠标事件
        tree.setPreferredSize(new Dimension(200,150));
        // 重新渲染树形节点
        MyTreeCellRenderer render = new MyTreeCellRenderer();
        tree.setCellRenderer(render);

        // 联系人面板
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(200,300));
        // 界面左半部面板
        panelFrame = new JPanel();
        panelFrame.setLayout(new BorderLayout());
        panelFrame.add(panel, BorderLayout.CENTER);
        panelFrame.add(tree, BorderLayout.NORTH);

        addLinkmanButton = new JButton();
        addLinkmanButton.setText("联系人(C)");
        addLinkmanButton.setIcon(EditorUtils.createIcon("contacts.png"));
        panel.add(addLinkmanButton, BorderLayout.NORTH);
        addLinkmanButton.addActionListener(this); // 注册添加联系人事件

        // TODO
//        getContracts = new GetContractsList();
//        listModel = getContracts.makeModel();
        listModel = GetContractsList.makeModel();
        contactsList = new JList(listModel);

        contactsList.addMouseListener(this); // 添加联系人列表双击事件
        contactsPanel = new JScrollPane();
        panel.add(contactsPanel, BorderLayout.CENTER);
        contactsPanel.setViewportView(contactsList); // 在滚动面板中添加联系人
        validate();
        // 主窗体
        mainFrameLabelBackground = new JLabel();
        mainFrameLabelBackground.setIcon(null); // 窗体背景
        desktopPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                Dimension size = e.getComponent().getSize();
                mainFrameLabelBackground.setSize(e.getComponent().getSize());
                mainFrameLabelBackground.setText("<html><img width=" + size.width
                        + " height=" + size.height + " src='"
                        + this.getClass().getResource(IMAGE_PATH + "mailboxBack9.jpeg")
                        + "'></html>");
            }
        });
        desktopPane.add(mainFrameLabelBackground, new Integer(Integer.MIN_VALUE));
        // 添加一个分割窗口
        JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelFrame, desktopPane);
        // 在分隔条上提供一个UI小部件来快速展开/折叠分隔条
        jSplitPane.setOneTouchExpandable(true);
        jSplitPane.setDividerSize(10); // 设置分隔条的大小
        getContentPane().add(jSplitPane, BorderLayout.CENTER);

    }

    // 返回新建菜单项
    private JMenuItem addMenuItem(JMenu menu, String name, String icon) {
        //新建邮件菜单项的初始化
        JMenuItem menuItem = new JMenuItem(name, EditorUtils.createIcon(icon));
        menuItem.addActionListener(this); // 监听退出菜单项事件
        menu.add(menuItem);

        return menuItem;
    }

    // 添加子窗体的方法
    public static void addIFrame(JInternalFrame iFrame) {

        JInternalFrame[] frames = desktopPane.getAllFrames();
        try {
            for (JInternalFrame item : frames) {
                if ( item.getTitle().equals(iFrame.getTitle())) {
                    desktopPane.selectFrame(true);
                    item.toFront();
                    item.setSelected(true);
                    return;
                }
            }
            desktopPane.add(iFrame);
            iFrame.setSelected(true);
            iFrame.toFront();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ( e.getSource() == exitMi ) { // 退出系统
            System.exit(0);
        } else if (e.getSource() == addLinkmanButton ) {
            addIFrame(FrameFactory.getFrameFactory().getContactsFrame(this)); // 联系人列表
        } else if (e.getSource() == newMainMi) { // 新建邮件
            addIFrame(FrameFactory.getFrameFactory().getSendFrame()); // 发件夹
        } else if (e.getSource() == itemPopupOne || e.getSource() == refreshMI) {// 右键刷新收件列表
            progressBar = new ProgressFrame(this, "收件夹", "正在收取'新邮件'，请稍后...");
            progressBar.setVisible(true);
            new Thread() {
                public void run() {
                    new ReceiveMail("pop3", "inbox", false).checkNewMail(); // 检测新邮件并保存到本地receive目录
                    progressBar.dispose();
                }
            }.start();
        } else if ( e.getSource() == sendedMi ) { // 已发送
            progressBar = new ProgressFrame(this, "已发送", "正在收取'已发送'邮件，请稍后...");
            progressBar.setVisible(true);
            new Thread() {
                public void run() {
                    addIFrame(FrameFactory.getFrameFactory().getSendedFrame()); // 已发送邮件
                    progressBar.dispose();
                }
            }.start();
        } else if ( e.getSource() == receiveMi ) { // 收邮件
            progressBar = new ProgressFrame(this, "收件夹", "正在收取'新邮件'，请稍后...");
            progressBar.setVisible(true);
            new Thread() {
                public void run() {
                    addIFrame(FrameFactory.getFrameFactory().getReceiveFrame()); // 收件夹
                    progressBar.dispose();
                }
            }.start();
        } else if ( e.getSource() == recycleMi ) { // 已删除
            progressBar = new ProgressFrame(this, "已删除", "正在收取'已删除'邮件，请稍后...");
            progressBar.setVisible(true);
            new Thread() {
                public void run() {
                    addIFrame(FrameFactory.getFrameFactory().getRecycleFrame()); // 已删除邮件
                    progressBar.dispose();
                }
            }.start();
        }
    }

    private SendFrame sendFrame = null; // 发送邮件对象
    public JMenuItem itemPopupOne = null;// 鼠标右键第一个选项

    @Override
    public void mouseClicked(MouseEvent e) {
        if ( progressBar != null && progressBar.isVisible()) {
            return;
        }
        //树形节点中的单击事件
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if ( e.getSource() == tree && e.getButton() != 3 && e.getButton() !=2 ) {
            if ( selectedNode == null ) {
                return;
            } else if ( selectedNode.toString().equals("发件夹") ) {
                sendFrame = FrameFactory.getFrameFactory().getSendFrame(); // 发件夹
                addIFrame(sendFrame);
            } else if ( selectedNode.toString().equals("收件夹") ) {
                progressBar = new ProgressFrame(this, "收件夹", "正在收取'新邮件'，请稍后...");
                progressBar.setVisible(true);
                new Thread() {
                    public void run() {
                        addIFrame(FrameFactory.getFrameFactory().getReceiveFrame()); // 收件夹
                        progressBar.dispose();
                    }
                }.start();
            } else if ( selectedNode.toString().equals("已发送") ) {
                progressBar = new ProgressFrame(this, "已发送", "正在收取'已发送'邮件，请稍后...");
                progressBar.setVisible(true);
                new Thread() {
                    public void run() {
                        addIFrame(FrameFactory.getFrameFactory().getSendedFrame()); // 已发送邮件
                        progressBar.dispose();
                    }
                }.start();
            } else if ( selectedNode.toString().equals("已删除") ) {
                progressBar = new ProgressFrame(this, "已删除", "正在收取'已删除'邮件，请稍后...");
                progressBar.setVisible(true);
                new Thread() {
                    public void run() {
                        addIFrame(FrameFactory.getFrameFactory().getRecycleFrame()); // 已删除邮件
                        progressBar.dispose();
                    }
                }.start();
            }
        } else if ( e.getSource() == contactsList && e.getClickCount() == 2 ) { // 双击联系人事件
            int index = contactsList.getSelectedIndex();
            if ( sendFrame != null && sendFrame.isSelected() ) { // 如果发送邮件界面被初始化并被激活
                sendFrame.addLinkman(GetContractsList.getOneContacts(index));
            }
        } else if (e.getButton() == MouseEvent.BUTTON3 && e.getSource() == tree) {// 收件箱右键刷新
            if (selectedNode == null)
                return;
            else if ("收件夹".equals(selectedNode.toString())) {
                JPopupMenu popup = new JPopupMenu();
                itemPopupOne = new JMenuItem("刷新收件箱",
                        EditorUtils.createIcon("refresh.png"));
                itemPopupOne.addActionListener(this);
                popup.add(itemPopupOne);
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
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

    public void reloadContactsList() {
        listModel = GetContractsList.updateModel(listModel);
    }
}
