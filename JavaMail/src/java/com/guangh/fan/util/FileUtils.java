package com.guangh.fan.util;

import com.guangh.fan.consts.Consts;
import com.guangh.fan.entity.Contacts;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    /**
     * 加载联系人
     * @return
     */
    public static List<Contacts> loadContacts() {
        String fileName = "." + Consts.CONTACTS_FILE;
        List<Contacts> list = new ArrayList<>();

        File file = new File(fileName);
        if (!file.exists()) { return list; }

        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                String line[] = tempStr.split(Consts.SEP);
                Contacts contacts = new Contacts(line[0].trim(), line[1].trim(), line[2].trim());
                list.add(contacts);
            }
            reader.close();
            return list;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return list;
    }

    /**
     * 保存联系人
     */
    public static void saveContacts(List<Contacts> list) {
        String fileName = "." + Consts.CONTACTS_FILE;
        File file = new File(fileName);

        BufferedWriter writer = null;
        StringBuffer sbf = new StringBuffer();
        try {
            writer = new BufferedWriter(new FileWriter(file));
            String tempStr;
            if ( list.size() == 0 ) {
                writer.write("");
            } else {
                for(Contacts item : list) {
                    String line = item.getName() + Consts.SEP + item.getNick()
                            + Consts.SEP + item.getMail() + Consts.NEW_LINE;
                    writer.write(line);
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
}
