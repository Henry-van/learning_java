package com.guangh.fan.frame;

import com.guangh.fan.action.LoginAction;
import com.guangh.fan.mailUtils.ReceiveMail;
import com.guangh.fan.util.EditorUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;

public class LoginFrame extends JFrame implements ActionListener, ItemListener {
    private static final long serialVersionUID = 1L;

    private JComboBox pop3CB; // 收件服务器下拉列表
    private JComboBox smtpCB; // 发邮件服务器下拉列表
    private JTextField nameTF;
    private JPasswordField passwordTF;
    private JButton loginButton = null, resetButton = null;
    private String userName = null, password = null, popHost = null,
                    smtpHost = null; // SMTP服务器
    private ProgressFrame progressBar = null; // 进度条实例

    public LoginFrame() {
        super("登录邮箱");
        this.setIconImage(EditorUtils.createIcon("email.png").getImage()); //
        getContentPane().setLayout(null);

        jFrameValidate(); // 初始化界面配置
        // 设置登录窗口的背景图片
        JLabel backgroundLabel = new JLabel();
        backgroundLabel.setBounds(0,0,798,698);
        backgroundLabel.setText("<html><img width=798 height=698 src='"
                + this.getClass().getResource("/assets/image/loginBg.png")
                + "'></html>");
        backgroundLabel.setLayout(null);

        final JLabel smtpLabel = new JLabel();
        smtpLabel.setText("SMTP 服务器");
        smtpLabel.setForeground(Color.WHITE);
        smtpLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        smtpLabel.setBounds(450,203,100,18);
        backgroundLabel.add(smtpLabel);

        final JLabel pop3Label = new JLabel();
        pop3Label.setText("POP3 服务器");
        pop3Label.setForeground(Color.WHITE);
        pop3Label.setHorizontalAlignment(SwingConstants.RIGHT);
        pop3Label.setBounds(450,243,100,18);
        backgroundLabel.add(pop3Label);

        final JLabel nameLabel = new JLabel();
        nameLabel.setText("邮箱账号");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        nameLabel.setBounds(450,283,100,18);
        backgroundLabel.add(nameLabel);

        final JLabel passwordLabel = new JLabel();
        passwordLabel.setText("邮箱密码");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        passwordLabel.setBounds(450,323,100,18);
        backgroundLabel.add(passwordLabel);

        // 发件箱服务器地址列表
        String[] smtpAdd = { "smtp.qq.com", "smtp.163.com", "smtp.126.com", "smtp.sina.com", "smtp.139.com" };

        smtpCB = new JComboBox(smtpAdd);
        smtpCB.setSelectedIndex(0);
        smtpCB.setEditable(true);
        smtpCB.addItemListener(this);
        smtpCB.setBounds(570,203,180,22);
        backgroundLabel.add(smtpCB);

        // 收件箱服务器地址列表
        String[] pop3Add = { "pop.qq.com", "pop.163.com", "pop.126.com", "pop.sina.com", "pop.139.com" };

        pop3CB = new JComboBox(pop3Add);
        pop3CB.setSelectedIndex(0);
        pop3CB.setEditable(true);
        pop3CB.addItemListener(this);
        pop3CB.setBounds(570,243,180,22);
        backgroundLabel.add(pop3CB);

        nameTF = new JTextField();
        nameTF.setBounds(570,283,180,22);
        backgroundLabel.add(nameTF);

        passwordTF = new JPasswordField();
        passwordTF.setBounds(570,323,180,22);
        backgroundLabel.add(passwordTF);

        loginButton = new JButton("登录");
        resetButton = new JButton("重置");
        backgroundLabel.add(loginButton);
        backgroundLabel.add(resetButton);
        loginButton.setBounds(480,360,80,30);
        resetButton.setBounds(600,360,80,30);
        loginButton.addActionListener(this);
        resetButton.addActionListener(this);
        getContentPane().add(backgroundLabel);
        // 创建进度提示框
        progressBar = new ProgressFrame(this, "登录", "登录中，请稍后...");

        reset(); // 默认初始值
    }

    // 初始化界面配置
    public void jFrameValidate() {
        Toolkit tk = getToolkit(); // 获取屏幕的宽度和高度
        Dimension sz = tk.getScreenSize();
        this.setResizable(false);
        this.setBounds(sz.width / 2 - 380, sz.height/2 - 270,776,574);
        validate();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // 获取界面中的所有项值
    private void getValues() {
        smtpHost = (String) smtpCB.getSelectedItem();
        popHost = (String) pop3CB.getSelectedItem();
        userName = nameTF.getText().trim();
        password = new String(passwordTF.getPassword());
    }

    // 默认初始值
    private void reset() {
        smtpCB.setSelectedIndex(0);
        pop3CB.setSelectedIndex(0);
        nameTF.setText("1234@qq.com");
        passwordTF.setText("sdfdddd"); //pop3
    }

    // 登录验证
    private void checkUser() {
        LoginAction login = new LoginAction(smtpHost, popHost, userName, password);
        if ( login.isLogin()) { // 登录成功
            // pop3协议message执行saveMessage出错。imap协议保存邮件存在问题（JavaMail BaseEncode64 Error）
            progressBar.dispose();
            // 如果收件,已发送,已删除每次都从服务器获取,注释掉如下几行代码 start
            // 创建获取邮件进度提示框
            progressBar = new ProgressFrame(this, "新邮件", "正在获取新邮件，请稍后...");
            progressBar.setVisible(true);
            new ReceiveMail("pop3", "inbox", false).checkNewMail(); // 检测新邮件并保存到本地receive目录
            progressBar.dispose();
            // 如果收件,已发送,已删除每次都从服务器获取,注释掉如下几行代码 end
            this.dispose(); // 释放本窗口资源
            new MainFrame().setVisible(true);
        } else { // 登录失败
            progressBar.setVisible(false);//没破坏进度条
            JOptionPane.showMessageDialog(this, "<html><h4>"
                    + "登录失败，请检查主机、用户名、密码是否正确！"
                    + "</h4></html>", "警告", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) { // 登录
            progressBar.setVisible(true);
            new Thread() {
                public void run() {//使用多线程否则进度条字不渲染了
                    getValues(); // 得到界面中的所有项的值
                    checkUser(); // 登录验证
                }
            }.start();
        } else if ( e.getSource() == resetButton) { // 重置
            reset(); // 重新设置各项的值
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if ( e.getSource() == smtpCB ) {
            if ( e.getStateChange() == ItemEvent.SELECTED && smtpCB.getSelectedIndex() != -1 ) {
                pop3CB.setSelectedIndex(smtpCB.getSelectedIndex());
            }
        } else if ( e.getSource() == pop3CB ) {
            if ( e.getStateChange() == ItemEvent.SELECTED && pop3CB.getSelectedIndex() != -1 ) {
                smtpCB.setSelectedIndex(pop3CB.getSelectedIndex());
            }
        }
    }
}
