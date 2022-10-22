package com.guangh.fan.util;

import com.guangh.fan.entity.AttachFile;

import javax.swing.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Font;

public class MyCellRenderer extends JLabel implements ListCellRenderer<Object> {
    //记录背景色
    private Color backGround;
    //记录前景色
    private Color foreGround;

    private AttachFile attachFile;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        //重置成员变量
        this.attachFile = (AttachFile) value;
        setIcon(attachFile.getIcon());
        setText(attachFile.getFileName());

        this.backGround=isSelected? list.getSelectionBackground():list.getBackground();
        this.foreGround=isSelected? list.getSelectionForeground():list.getForeground();
        return this;
    }

    @Override
    public Dimension getPreferredSize() {
//        return new Dimension(160,80);
        String fileName = attachFile.getFileName();
        Icon iconObj = attachFile.getIcon();

        return new Dimension(iconObj.getIconWidth() + fileName.length()*10 + 20,20);
    }

    //绘制列表内容
    @Override
    public void paint(Graphics g) {
//        int imageWidth=icon.getImage().getWidth(null);
//        int imageHeight=icon.getImage().getHeight(null);
        String fileName = attachFile.getFileName();
        Icon iconObj = attachFile.getIcon();
        int imageWidth = iconObj.getIconWidth();
        int imageHeight = iconObj.getIconHeight();

        //填充背景矩形
        g.setColor(backGround);
        g.fillRect(0,0,this.getWidth(),this.getHeight());
//        //绘制头像
//        g.drawImage(icon.getImage(),this.getWidth()/2-imageWidth/2,10,null);
//        iconObj.paintIcon(this, g, this.getWidth()/2-imageWidth/2,10);
        iconObj.paintIcon(this, g, 0,0);
        //绘制昵称
        //设置前景色
        g.setColor(foreGround);
        g.setFont(new Font("StSong",Font.BOLD,12));
//        g.drawString(this.name,this.getWidth()/2-this.name.length()*10/2,10+imageHeight+20);
//        g.drawString(fileName,this.getWidth()/2-fileName.length()*10/2,10+imageHeight+20);
        g.drawString(fileName,imageWidth + 3,13);
    }
}
