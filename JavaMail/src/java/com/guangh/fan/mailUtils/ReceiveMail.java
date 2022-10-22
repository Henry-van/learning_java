package com.guangh.fan.mailUtils;

import com.guangh.fan.action.LoginAction;
import com.guangh.fan.entity.Mail;

import javax.mail.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.guangh.fan.consts.Consts.*;

public final class ReceiveMail {
    private Session session;
    private String mailBox; // 文件夹
    private String protocol; // 协议
    private Boolean readOnly; // 只读

    private Store store = null;
    private Folder folder = null;

    private Message[] messages;
    private int messageCount;
    private List<Mail> mailList;

//    private ArrayList<String> fileList;

    /**
     * 构造函数
     * @param protocol（pop3,imap）
     * @param mailBox
     */
    public ReceiveMail(String protocol, String mailBox, Boolean readOnly) {
        this.protocol = protocol;
        this.mailBox = mailBox;
        this.readOnly = readOnly;
    }

    private Session getSession() throws Exception {
        Properties props = new Properties(); //配置发送邮件的环境属性
        props.put("mail.store.protocol", protocol); // imap
        props.put("mail." + protocol + ".host", LoginAction.getPopHost());

        return Session.getInstance(props);
    }

    // 检测新邮件
    public void checkNewMail() {
        try {
            // 利用Session对象获得Store对象，并连接pop3服务器
            session = getSession();
            // pop3协议只能使用inbox文件夹，imap协议可以使用其他文件夹
            store = session.getStore(protocol);
//            // 开启Debug
//            session.setDebug(true);

            //进行用户邮箱连接
            store.connect(LoginAction.getPopHost(), LoginAction.getUserName(), LoginAction.getPassword());

            // INBOX：收件夹；Trash：已删除；Sent：已发送；Drafts：；Notice：
            folder = store.getFolder(mailBox);
            // 获得邮箱内的邮件夹Folder对象，以"只读"打开
            if ( readOnly ) {
                folder.open(Folder.READ_ONLY);
            } else {
                folder.open(Folder.READ_WRITE);
            }

            // imap协议保存邮件存在问题。读取时发生com.sun.mail.util.DecodingException: BASE64Decoder错误。
            // 为解决此问题，使用pop3和imap两次读取邮件，根据imap判断邮件是否为未读邮件，如未读用pop3保存邮件
            Store storeTemp = session.getStore("imap");
            storeTemp.connect(LoginAction.getPopHost(), LoginAction.getUserName(), LoginAction.getPassword());
            Folder folderTemp = storeTemp.getFolder(mailBox);
            folderTemp.open(Folder.READ_WRITE);
            Message[] messagesTemp = folderTemp.getMessages();

            // 获得邮件夹Folder内的所有邮件Message对象
            messages = folder.getMessages();
            messageCount = folder.getMessageCount(); // 获取所有邮件个数

            for (int i = 0; i < messageCount; i++) {
                Message msg = messages[i];
                Message msgTemp = messagesTemp[i];

                //将该邮件保存到本地
                File f = new File("." + RECEIVE_PATH + System.currentTimeMillis() + ".eml"); // 得到当前目录

//                Flags flags = msg.getFlags();
                if ( msg.getMessageNumber() == msgTemp.getMessageNumber() ) {
                    Flags flags = messagesTemp[i].getFlags();
                    if ( flags.contains(Flags.Flag.SEEN) ) {
                        // 邮件标记为已读，不处理
                    } else {
                        // 把邮件保存到本地 receive 文件夹中
                        // 获得输出流
//                    OutputStream out = new FileOutputStream(curDir + RECEIVE_PATH + System.currentTimeMillis() + ".eml");
                        OutputStream out = new FileOutputStream(f);
                        // 把邮件内容写入到文件
                        msg.writeTo(out);
                        out.flush();
                        // 关闭流
                        out.close();

                        // 把邮件标记为已读，下次不再处理
                        msgTemp.setFlag(Flags.Flag.SEEN, true);
//                    msgTemp.saveChanges(); // 无法保存，会抛异常
                    }
                }

            }
            // message是只读的，无法保存。替代方案对整个folder进行设置为已读
//            folder.setFlags(messages, new Flags(Flags.Flag.SEEN), true );
        } catch (Exception e) {
            e.printStackTrace();
        } finally{ }
    }

    // 收邮件
    public String receive() {
        String strMsg = "";

        try {
            // 利用Session对象获得Store对象，并连接pop3服务器
            session = getSession();
            // pop3协议只能使用inbox文件夹，imap协议可以使用其他文件夹
            store = session.getStore(protocol);
//            // 开启Debug
//            session.setDebug(true);

            //进行用户邮箱连接
            store.connect(LoginAction.getPopHost(), LoginAction.getUserName(), LoginAction.getPassword());

            // INBOX：收件夹；Trash：已删除；Sent：已发送；Drafts：；Notice：
            folder = store.getFolder(mailBox);
            // 获得邮箱内的邮件夹Folder对象，以"只读"打开
            if ( readOnly ) {
                folder.open(Folder.READ_ONLY);
            } else {
                folder.open(Folder.READ_WRITE);
            }

            // 获得邮件夹Folder内的所有邮件Message对象
            messages = folder.getMessages();
            messageCount = folder.getMessageCount(); // 获取所有邮件个数
            mailList = new ArrayList<Mail>();
            for (int i = 0; i < messageCount; i++) {
                List<String> fileList = new ArrayList<>();

                Message msg = messages[i];
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
                mailList.add(mail);
            }
//            ((POP3Message) message[i]).invalidate(true);
        } catch (Exception e) {
            strMsg = e.getMessage();
            e.printStackTrace();
        } finally{ }

        return strMsg;
    }

    // 关闭邮件
    public String close() {
        String strMsg = "";

        if(folder != null && folder.isOpen()){
            try {
                folder.close(true);
            } catch (MessagingException e) {
                strMsg = e.getMessage();
                e.printStackTrace();
            }
        }
        if(store.isConnected()){
            try {
                store.close();
            } catch (MessagingException e) {
                strMsg = e.getMessage();
                e.printStackTrace();
            }
        }

        return strMsg;
    }

    public Message[] getMessages() {
        return messages;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public List<Mail> getMailList() {
        return mailList;
    }

    public void setMailList(List<Mail> mailList) {
        this.mailList = mailList;
    }
}
