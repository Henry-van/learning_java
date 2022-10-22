package com.guangh.fan.mailUtils;

import com.guangh.fan.entity.Mail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static com.guangh.fan.consts.Consts.RECEIVE_PATH;

public final class mailUtil {
    private static String dateformate = "yy-MM-dd HH:mm";

    public static Session getSession() {
        // 创建邮件对象
        Properties props = System.getProperties();
        props.put("mail.host", "smtp.dummydomain.com");
        props.put("mail.transport.protocol", "smtp");
        Session mailSession = Session.getDefaultInstance(props, null);
        return mailSession;
    }

    /**
     * 获得发件人的地址
     * @param message：Message
     * @return 发件人的地址
     */
    public static String getFrom(Message message) throws MessagingException {
        InternetAddress[] address = (InternetAddress[]) ((MimeMessage) message).getFrom();
        String from = address[0].getAddress();
        if (from == null){
            from = "";
        }
        String personal = address[0].getPersonal();
        if (personal == null) {
            personal = "";
        }
        String fromaddr = personal + "<" + from + ">";
        return fromaddr;
    }

    /**
     * 获得邮件主题
     * @param message：Message
     * @return 邮件主题
     */
    public static String getSubject(Message message) throws Exception {
        String subject = "";
        if(((MimeMessage) message).getSubject() != null){
            subject = MimeUtility.decodeText(((MimeMessage) message).getSubject());// 将邮件主题解码
        }
        return subject;
    }

    /**
     * 获取邮件内容
     * @param part：Part
     */
//    public static String getMailContent(Part part) throws Exception {
    public static String getMailContent(Part part) {
        StringBuffer bodytext = new StringBuffer();//存放邮件内容
        try {
            //判断邮件类型,不同类型操作不同
            if (part.isMimeType("text/plain")) {
                bodytext.append((String) part.getContent());
            } else if (part.isMimeType("text/html")) {
                bodytext.append((String) part.getContent());
            } else if (part.isMimeType("multipart/*")) {
                Multipart multipart = (Multipart) part.getContent();
                int counts = multipart.getCount();
                for (int i = 0; i < counts; i++) {
                    bodytext.append(getMailContent(multipart.getBodyPart(i)));
                }
            } else if (part.isMimeType("message/rfc822")) {
                bodytext.append(getMailContent((Part) part.getContent()));
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bodytext.toString();
    }


    /**
     * 获取邮件收件人，抄送，密送的地址和信息。根据所传递的参数不同 "to"-->收件人,"cc"-->抄送人地址,"bcc"-->密送地址
     *
     * @param type
     * @return
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    public static String getMailAddress(String type, Message msg) throws MessagingException, UnsupportedEncodingException {
        String mailaddr = "";
        String addrType = type.toUpperCase();
        InternetAddress[] address = null;

        if (addrType.equals("TO") || addrType.equals("CC") || addrType.equals("BCC")) {
            if (addrType.equals("TO")) {
                address = (InternetAddress[]) msg.getRecipients(Message.RecipientType.TO);
            }
            if (addrType.equals("CC")) {
                address = (InternetAddress[]) msg.getRecipients(Message.RecipientType.CC);
            }
            if (addrType.equals("BCC")) {
                address = (InternetAddress[]) msg.getRecipients(Message.RecipientType.BCC);
            }

            if (address != null) {
                for (int i = 0; i < address.length; i++) {
                    String mail = address[i].getAddress();
                    if (mail == null) {
                        mail = "";
                    } else {
                        mail = MimeUtility.decodeText(mail);
                    }
                    String personal = address[i].getPersonal();
                    if (personal == null) {
                        personal = "";
                    } else {
                        personal = MimeUtility.decodeText(personal);
                    }
                    String compositeto = personal + "<" + mail + ">";
                    mailaddr += "," + compositeto;
                }
                mailaddr = mailaddr.substring(1);
            }
        } else {
            throw new RuntimeException("Error email Type!");
        }
        return mailaddr;
    }


    /**
     * 获取邮件发送日期
     *
     * @return
     * @throws MessagingException
     */
    public static String getSendDate(Message msg) throws MessagingException {
        Date sendDate = msg.getSentDate();
        if ( sendDate != null ) {
            SimpleDateFormat smd = new SimpleDateFormat(dateformate);
            return smd.format(sendDate);
        }
        return "";
    }

    /**
     * 判断邮件是否需要回执，如需回执返回true，否则返回false
     *
     * @return
     * @throws MessagingException
     */
    public static boolean getReplySign(Message msg) throws MessagingException {
        boolean replySign = false;
        String needreply[] = msg.getHeader("Disposition-Notification-TO");
        if (needreply != null) {
            replySign = true;
        }
        return replySign;
    }
    /**
     * 判断此邮件是否已读，如果未读则返回false，已读返回true
     *
     * @return
     * @throws MessagingException
     */
    public static boolean isSeen(Message msg) throws MessagingException {
        boolean isSeen = false;
        Flags flags = msg.getFlags();
        Flags.Flag[] flag = flags.getSystemFlags();

        for (int i = 0; i < flag.length; i++) {
            if (flag[i] == Flags.Flag.SEEN) {
                isSeen = true;
                break;
            }
        }

        return isSeen;
    }

    /**
     * 判断此邮件是否包含附件
     * @param part：Part
     * @return 包含附件
     */
    public static boolean isContainAttach(Part part, List<String> fileList) throws Exception {
        boolean attachflag = false;
        if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart mpart = mp.getBodyPart(i);
                String disposition = mpart.getDisposition();
                if ((disposition != null) && ((disposition.equals(Part.ATTACHMENT)) || (disposition.equals(Part.INLINE)))) {
                    attachflag = true;
                    String fileName = mpart.getFileName();
                    if (fileName != null) {
                        fileName = MimeUtility.decodeText(fileName);
                        fileList.add(fileName);
                    }

                } else if (mpart.isMimeType("multipart/*")) {
                    attachflag = isContainAttach((Part) mpart, fileList);
                } else {
                    String contype = mpart.getContentType();
                    if (contype.toLowerCase().indexOf("application") != -1) {
                        attachflag = true;
                    }
                    if (contype.toLowerCase().indexOf("name") != -1) {
                        attachflag = true;
                    }
                    String fileName1 = mpart.getFileName();
                    if (fileName1 != null) {
                        fileName1 = MimeUtility.decodeText(fileName1);
                        fileList.add(fileName1);
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            attachflag = isContainAttach((Part) part.getContent(), fileList);
        }
        return attachflag;
    }


    /**
     * 保存附件
     * @param part：Part
     * @param filePath：邮件附件存放路径
     */
    public static void saveAttachMent(Part part,String filePath) throws Exception {
        String fileName = "";
        //保存附件到服务器本地
        if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart mpart = mp.getBodyPart(i);
                String disposition = mpart.getDisposition();
                String contentType = mpart.getContentType();
                if ((disposition != null) && ((disposition.equals(Part.ATTACHMENT)) || (disposition.equals(Part.INLINE)))) {
                    fileName = mpart.getFileName();
                    if (fileName != null) {
                        fileName = MimeUtility.decodeText(fileName);
                        saveFile(fileName, mpart.getInputStream(),filePath);
                    }
                } else if (mpart.isMimeType("multipart/*")) {
                    saveAttachMent(mpart,filePath);
                } else {
                    fileName = mpart.getFileName();
                    if (fileName != null) {
                        fileName = MimeUtility.decodeText(fileName);
                        //name:图片
                        if (contentType.indexOf("name") != -1 || contentType.indexOf("application") != -1) {
                            saveFile(fileName, mpart.getInputStream(),filePath);
                        }
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            saveAttachMent((Part) part.getContent(),filePath);
        }

    }

    /**
     * 保存附件到指定目录里
     * @param fileName：附件名称
     * @param in：文件输入流
     * @param filePath：邮件附件存放基路径
     */
    public static void saveFile(String fileName, InputStream in, String filePath) throws Exception {
        File storefile = new File(filePath);
        if(!storefile.exists()){
            storefile.mkdirs();
        }
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(filePath + fileName));
            bis = new BufferedInputStream(in);
            int c;
            while ((c = bis.read()) != -1) {
                bos.write(c);
                bos.flush();
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if(bos != null){
                bos.close();
            }
            if(bis != null){
                bis.close();
            }
        }
    }

    /**
     * 获取附件
     *
     * @param part
     * @throws MessagingException
     * @throws IOException
     */
    public static InputStream getFile(Part part) throws Exception {
        if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart mpart = mp.getBodyPart(i);
                String dispostion = mpart.getDisposition();
                String contentType = mpart.getContentType();
                if ((dispostion != null) && (dispostion.equals(Part.ATTACHMENT) || dispostion.equals(Part.INLINE))) {
                    if (contentType.indexOf("application") != -1) {
                        return mpart.getInputStream();
                    }
                } else if (mpart.isMimeType("multipart/*")) {
                    saveAttachMent(mpart,"." + RECEIVE_PATH);
                } else {
                    //name:图片
                    if (contentType.indexOf("application") != -1) {
                        return mpart.getInputStream();
                    }
                }
            }

        } else if (part.isMimeType("message/rfc822")) {
            saveAttachMent((Part) part.getContent(),"." + RECEIVE_PATH);
        }
        return null;
    }


    /**
     * 获得邮件的优先级
     * @param msg 邮件内容
     * @return 1(High):紧急  3:普通(Normal)  5:低(Low)
     * @throws MessagingException
     */
    public static String getPriority(MimeMessage msg) throws MessagingException {
        String priority = "普通";
        String[] headers = msg.getHeader("X-Priority");
        if (headers != null) {
            String headerPriority = headers[0];
            if (headerPriority.indexOf("1") != -1 || headerPriority.indexOf("High") != -1)
                priority = "紧急";
            else if (headerPriority.indexOf("5") != -1 || headerPriority.indexOf("Low") != -1)
                priority = "低";
            else
                priority = "普通";
        }
        return priority;
    }

}
