package com.guangh.fan.action;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.util.Properties;

public class LoginAction {
    private static String smtpHost;
    private static String popHost;
    private static String userName;
    private static String password;
    private static Boolean isLogin = false;

    public LoginAction(String smtpHost, String popHost, String userName, String password){
        this.smtpHost = smtpHost;
        this.popHost = popHost;
        this.userName = userName;
        this.password = password;

        final Properties props = new Properties(); //配置发送邮件的环境属性

        props.put("mail.smtp.auth", "true"); // 表示SMTP发送邮件，需要进行身份验证
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.user", userName); //smtp登陆的账号、密码 ；需开启smtp登陆
        props.put("mail.password", password); // 访问SMTP服务时需要提供的密码,不是邮箱登陆密码，一般都有独立smtp的登陆密码

        //关于QQ邮箱，还要设置SSL加密，加上以下代码即可,网上看的
        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        sf.setTrustAllHosts(true);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.socketFactory", sf);

        //使⽤JavaMail发送邮件的5个步骤
        //1.txt、创建定义整个应⽤程序所需的环境信息的Session对象
        Session session = Session.getInstance(props);

        //开启Session的debug模式，这样就可以查看到程序发送Email的运⾏状态
//        session.setDebug(true);

        //2、通过session得到transport对象
        Transport ts = null;
        try {
            ts = session.getTransport();
            //3、使⽤邮箱的⽤户名和授权码连上邮件服务器
            ts.connect(smtpHost, userName, password);
            isLogin = true;
            ts.close();
        } catch (AuthenticationFailedException e) {
            isLogin = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean isLogin() {
        return isLogin;
    }

    public static String getSmtpHost() {
        return smtpHost;
    }

    public static void setSmtpHost(String smtpHost) {
        LoginAction.smtpHost = smtpHost;
    }

    public static String getPopHost() {
        return popHost;
    }

    public static void setPopHost(String popHost) {
        LoginAction.popHost = popHost;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        LoginAction.userName = userName;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        LoginAction.password = password;
    }

    //    public static GetUserInfo() {
//
//    }

//    //设置QQ邮件服务器
//        prop.setProperty("mail.host", "smtp.qq.com");
//    //邮件发送协议
//        prop.setProperty("mail.transport.protocol", "smtp");
//    //需要验证⽤户名密码
//        prop.setProperty("mail.smtp.auth", "true");

}
