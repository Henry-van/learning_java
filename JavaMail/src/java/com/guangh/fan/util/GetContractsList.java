package com.guangh.fan.util;

import com.guangh.fan.entity.Contacts;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class GetContractsList {
    private static JList jList;
    private static List<Contacts> contractsList;
    private static DefaultListModel listModel; // 联系人列表模型
//
//    public GetContractsList() {
//        this.contractsList = FileUtils.loadContacts();
//    }
//
//    public JList makeList() {
//        DefaultListModel listModel = new DefaultListModel(); // 列表模型
//        jList = new JList(listModel);
//        jList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
//        jList.setLayoutOrientation(JList.VERTICAL);
//        for (int i = 0; i < contractsList.size(); i++) {
//            String display = contractsList.get(i).getName() + " <\"" + contractsList.get(i).getMail() + "\">";
//            listModel.addElement(display);
//        }
//
//        return jList;
//    }

    public static DefaultListModel makeModel() {
        contractsList = FileUtils.loadContacts();
        listModel = new DefaultListModel(); // 列表模型
        for (int i = 0; i < contractsList.size(); i++) {
            String display = contractsList.get(i).getName() + " <\"" + contractsList.get(i).getMail() + "\">";
            listModel.addElement(display);
        }

        return listModel;
    }

    public static DefaultListModel updateModel(DefaultListModel listModel) {
        contractsList = FileUtils.loadContacts();
        listModel.removeAllElements();
        for (int i = 0; i < contractsList.size(); i++) {
            String display = contractsList.get(i).getName() + " <\"" + contractsList.get(i).getMail() + "\">";
            listModel.addElement(display);
        }

        return listModel;
    }

    public static String getOneContacts( int index ) {
        contractsList = FileUtils.loadContacts();
        return contractsList.get(index).getMail();
    }

}
