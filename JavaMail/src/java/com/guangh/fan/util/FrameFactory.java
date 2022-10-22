package com.guangh.fan.util;

import com.guangh.fan.frame.*;

import javax.swing.*;

public class FrameFactory {
    public static FrameFactory getFrameFactory() {
        return new FrameFactory();
    }

    public JInternalFrame getContactsFrame(MainFrame pF) {
        return new ContactsFrame(pF);
    }

    public SendFrame getSendFrame() {
        return new SendFrame();
    }

    public JInternalFrame getSendedFrame() {
        return new SendedFrame();
    }

    public JInternalFrame getReceiveFrame() {
        return new ReceiveFrame();
    }

    public JInternalFrame getRecycleFrame() {
        return new RecycleFrame();
    }
}
