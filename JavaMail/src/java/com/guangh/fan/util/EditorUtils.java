package com.guangh.fan.util;

import javax.swing.*;


import static com.guangh.fan.consts.Consts.ASSETS_PATH;

public class EditorUtils {
    public static ImageIcon createIcon( String iconName ) {
        return new ImageIcon("." + ASSETS_PATH + iconName);
    }
}
