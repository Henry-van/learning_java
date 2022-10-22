package com.guangh.fan.entity;

import com.sun.xml.internal.ws.util.StringUtils;

import javax.mail.Flags;
import java.util.List;

public class Mail {
    private String from;
    private String Subject;
    private String SentDate;
    private String Content;
    private Boolean hasAttach;
    private List<String> attachFileName;
    private int msgNumber;
    private Flags flags;//javamail标志
    private String to;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getSentDate() {
        return SentDate;
    }

    public void setSentDate(String sentDate) {
        SentDate = sentDate;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public Boolean getHasAttach() {
        return hasAttach;
    }

    public void setHasAttach(Boolean hasAttach) {
        this.hasAttach = hasAttach;
    }

    public List<String> getAttachFileName() {
        return attachFileName;
    }

    public void setAttachFileName(List<String> attachFileName) {
        this.attachFileName = attachFileName;
    }

    public int getMsgNumber() {
        return msgNumber;
    }

    public void setMsgNumber(int msgNumber) {
        this.msgNumber = msgNumber;
    }

    public Flags getFlags() {
        return flags;
    }

    public void setFlags(Flags flags) {
        this.flags = flags;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
