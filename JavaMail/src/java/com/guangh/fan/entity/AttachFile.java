package com.guangh.fan.entity;

import javax.swing.*;

public class AttachFile {
    private String fileName;
    private Icon icon;

    public AttachFile(String fileName, Icon icon) {
        this.fileName = fileName;
        this.icon = icon;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }
}
