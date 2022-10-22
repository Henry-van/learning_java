package com.guangh.fan.frame;

import javax.swing.*;
import java.awt.*;

public class ProgressFrame extends JFrame {
    private JFrame parentFrame;
    private String title;
    private String content;

    public ProgressFrame(JFrame frame, String title, String content) {
        this.parentFrame = frame;
        this.title = title;
        this.content = content;
        init();
    }

    private void init() {
        int width = 400;
        int height =100;

        setTitle(title);
        setSize(width,height);
        setResizable(false);
        setLocationRelativeTo(null);//null是相对于屏幕中间
        setAlwaysOnTop(true);

//        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // 关闭按钮不可用
//        int vgap = 30; // 关闭按钮不可用时，设置为30
        setUndecorated(true); // 不显示Title框
        int vgap = 40; // 不显示Title框时，设置为40

        JLabel labelContent = new JLabel(content);
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new FlowLayout(FlowLayout.CENTER,0,vgap));
        contentPane.add(labelContent);
        add(contentPane);
    }
}
