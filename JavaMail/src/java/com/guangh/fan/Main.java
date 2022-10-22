package com.guangh.fan;

import com.guangh.fan.frame.LoginFrame;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        try {
            // 这是设置图形界面外观的
            // java的图形界面外观有3种,默认是java的金属外观,还有就是windows系统,motif系统外观
            // 把外观设置成所使用的平台的外观
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 请求事件分发线程以运行某段代码。
        // invokeLater函数会立即返回，不会等到事件分发线程执行完这段代码。保证GUI在事件分发线程中创建
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 登录窗口
                new LoginFrame().setVisible(true);
            }
        });
    }
}

