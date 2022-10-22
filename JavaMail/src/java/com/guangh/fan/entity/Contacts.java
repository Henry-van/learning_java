package com.guangh.fan.entity;

import java.sql.Connection;

public class Contacts {
    private String name; // 姓名
    private String nick; // 昵称
    private String mail; // 邮箱

    public Contacts(String name, String nick, String mail) {
        this.name = name;
        this.nick = nick;
        this.mail = mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
