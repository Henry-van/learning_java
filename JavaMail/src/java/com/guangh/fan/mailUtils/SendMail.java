package com.guangh.fan.mailUtils;

import com.guangh.fan.action.LoginAction;
import com.sun.mail.util.MailSSLSocketFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Properties;

import static com.guangh.fan.consts.Consts.SENDED_PATH;

public class SendMail {
    private String subject;
    private String content;
    private ArrayList<String> fileList;
    private String toP;
    private String copyP;
    private String sentDate;

    private Session session;

    public SendMail(String subject, String content, ArrayList<String> fileList, String toP, String copyP) {
        this.subject = subject;
        this.content = content;
        this.fileList = fileList;
        this.toP = toP;
        this.copyP = copyP;
    }

    private Session getSession() throws Exception {

        final Properties props = new Properties(); //配置发送邮件的环境属性

        props.put("mail.smtp.auth", "true"); // 表示SMTP发送邮件，需要进行身份验证
        props.put("mail.smtp.host", LoginAction.getSmtpHost());
        props.put("mail.user", LoginAction.getUserName()); //smtp登陆的账号、密码 ；需开启smtp登陆
        props.put("mail.password", LoginAction.getPassword()); // 访问SMTP服务时需要提供的密码,不是邮箱登陆密码，一般都有独立smtp的登陆密码

        //关于QQ邮箱，还要设置SSL加密，加上以下代码即可
        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        sf.setTrustAllHosts(true);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.socketFactory", sf);

        return Session.getInstance(props);
    }

    public String send() {
        String strMsg = "";

        try {
            // 利用Session对象，连接smtp服务器
            session = getSession();

            //开启Session的debug模式，这样就可以查看到程序发送Email的运⾏状态
//            session.setDebug(true);

            Transport ts = null;
            //2、通过session得到transport对象
            ts = session.getTransport();
            //使⽤邮箱的⽤户名和授权码连上邮件服务器
            //    PS_01: 成败的判断关键在此一句, 如果连接服务器失败, 都会在控制台输出相应失败原因的 log,
            //           仔细查看失败原因, 有些邮箱服务器会返回错误码或查看错误类型的链接, 根据给出的错误
            //           类型到对应邮件服务器的帮助网站上查看具体失败原因。
            //
            //    PS_02: 连接失败的原因通常为以下几点, 仔细检查代码:
            //           (1) 邮箱没有开启 SMTP 服务;
            //           (2) 邮箱密码错误, 例如某些邮箱开启了独立密码;
            //           (3) 邮箱服务器要求必须要使用 SSL 安全连接;
            //           (4) 请求过于频繁或其他原因, 被邮件服务器拒绝服务;
            //           (5) 如果以上几点都确定无误, 到邮件服务器网站查找帮助。
            //
            //    PS_03: 仔细看log, 认真看log, 看懂log, 错误原因都在log已说明。
            ts.connect(LoginAction.getSmtpHost(), LoginAction.getUserName(), LoginAction.getPassword());
            //创建邮件
            //创建邮件对象
            MimeMessage message = new MimeMessage(session);
            //指明邮件的发件⼈
            message.setFrom(new InternetAddress(LoginAction.getUserName(),"GuangHe", "UTF-8"));
            //指明邮件的收件⼈
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(toP));
            if ( !copyP.equals("")) {
                message.setRecipient(Message.RecipientType.CC, new InternetAddress(copyP));
            }
            //邮件标题
            message.setSubject(subject);
            //邮件⽂本内容
            if ( fileList.size() > 0) { // 有附件
                // 设置（文本+图片）和 附件 的关系（合成一个大的混合“节点” / Multipart ）
                MimeMultipart mm = new MimeMultipart();

                //1. 创建文本“节点”
                BodyPart text = new MimeBodyPart();
                text.setContent(content, "text/html;charset=UTF-8");
                mm.addBodyPart(text);

                //2.附件
                for (String s : fileList) { // 多个附件，可以创建多个多次添加
                    MimeBodyPart attachment = new MimeBodyPart();
                    DataHandler dh = new DataHandler(new FileDataSource(s));
                    attachment.setDataHandler(dh);
                    attachment.setFileName(MimeUtility.encodeText(dh.getName()));
                    mm.addBodyPart(attachment);
                    mm.setSubType("mixed");			// 混合关系
                }
                // 设置整个邮件的关系（将最终的混合“节点”作为邮件的内容添加到邮件对象）
                message.setContent(mm);
            } else { // 无附件
                message.setContent(content, "text/html;charset=UTF-8");
            }

            //发送邮件
            ts.sendMessage(message, message.getAllRecipients());
            sentDate = message.getSentDate() +"";

            // 在服务器端，保存该已发送邮件。（如不需要保存，可以注释掉）
            message.saveChanges();

            //将该邮件保存到本地
            File f = new File("."); // 得到当前目录
            String curDir = f.getPath();
            // 获得输出流
            OutputStream out = new FileOutputStream(curDir + SENDED_PATH + System.currentTimeMillis() + ".eml");
            // 把邮件内容写入到文件
            message.writeTo(out);
            out.flush();
            // 关闭流
            out.close();

            //9、关闭连接
            ts.close();
        } catch (Exception e) {
            strMsg = e.getMessage();
            e.printStackTrace();
        }
        return strMsg;
    }
}
