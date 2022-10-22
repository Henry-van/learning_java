package com.guangh.fan.frame;

import com.guangh.fan.entity.Contacts;
import com.guangh.fan.util.EditorUtils;
import com.guangh.fan.util.FileUtils;
import com.guangh.fan.util.MyTable;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

public class ContactsFrame extends JInternalFrame implements ActionListener, MouseListener, FocusListener {
    private JPanel inputJpanel;
    private JPanel tableJpanel;
    private JPanel buttonJpanel;
    private JLabel nameLabel = null, nickLabel = null, mailLabel = null;
    private JTextField nameInput = null, nickInput = null, mailInput = null;
    private JButton addButton = null; //
    private JButton delButton = null; //
    private JButton okButton = null; //
    private JButton cancelButton = null; //
    private JTable mailTable = null; //
    private Object[] columnTitle = {"姓名", "昵称", "邮箱"}; // 定义邮件Table的列标题
    private Object[][] tableData = null; // 邮件Table数据
    private List<Contacts> contractsList;
    private DefaultTableModel tableModel;
    private int selectedRow = -1;
    private MainFrame parentFrame;

    public ContactsFrame(MainFrame parentFrame) {
        super("添加联系人");

        this.parentFrame = parentFrame;

        initData();
        initUI();
    }

    private void initData() {
        contractsList = FileUtils.loadContacts();
        tableData = new Object[contractsList.size()][3];
        for (int i = 0; i < contractsList.size(); i++) {
            tableData[i][0] = contractsList.get(i).getName();
            tableData[i][1] = contractsList.get(i).getNick();
            tableData[i][2] = contractsList.get(i).getMail();
        }
    }

    private void initUI() {

        setFrameIcon(EditorUtils.createIcon("contacts.png"));
        // 初始化基本项
        getContentPane().setLayout(new BorderLayout()); // 设置空布局
        setIconifiable(true); // 是否使 JInternalFrame 变成一个图标
//        setClosable(true); // 是否关闭
//        setMaximizable(true); // 窗口最大化设置
//        setResizable(true); // 设置窗口可以调整大小
        setBounds(10, 10, 800, 568); // 设置界面大小
        setVisible(true);

        nameLabel = new JLabel("姓名");
        nameInput = new JTextField(10);
        nameInput.addFocusListener(this);
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.add(nameLabel);
        namePanel.add(nameInput);

        nickLabel = new JLabel("昵称");
        nickInput = new JTextField(10);
        nickInput.addFocusListener(this);
        JPanel nickPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nickPanel.add(nickLabel);
        nickPanel.add(nickInput);

        mailLabel = new JLabel("邮箱");
        mailInput = new JTextField(30);
        mailInput.addFocusListener(this);
        mailInput.setToolTipText("格式：xxx@yyy.com");
        JPanel mailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mailPanel.add(mailLabel);
        mailPanel.add(mailInput);

        JPanel editPanel = new JPanel(new BorderLayout());
        editPanel.add(namePanel, BorderLayout.NORTH);
        editPanel.add(nickPanel, BorderLayout.CENTER);
        editPanel.add(mailPanel, BorderLayout.SOUTH);

        inputJpanel = new JPanel();
        TitledBorder inputPanelTitle = BorderFactory.createTitledBorder("联系人");
        inputJpanel.setBorder(inputPanelTitle);
        inputJpanel.add(editPanel);

        tableJpanel = new JPanel();
        TitledBorder tablePanelTitle = BorderFactory.createTitledBorder("联系人列表");
        tableJpanel.setBorder(tablePanelTitle);

        tableModel = new DefaultTableModel(tableData, columnTitle);
        mailTable = new MyTable(tableModel);
        mailTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JTableHeader tableHeader = mailTable.getTableHeader();
        DefaultTableCellRenderer dHr = (DefaultTableCellRenderer) tableHeader.getDefaultRenderer();
        dHr.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        mailTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedRow = mailTable.getSelectedRow();
                String name = (String) tableModel.getValueAt(selectedRow, 0);
                String nick = (String) tableModel.getValueAt(selectedRow, 1);
                String mail = (String) tableModel.getValueAt(selectedRow, 2);
                nameInput.setText(name);
                nickInput.setText(nick);
                mailInput.setText(mail);
            }
        });

        JScrollPane contactsListPanel = new JScrollPane(); //
        contactsListPanel.setViewportView(mailTable);
        contactsListPanel.setPreferredSize(new Dimension(400, 300));

        JPanel buttonPanel = new JPanel(new BorderLayout());
        addButton = new JButton("添加");
        addButton.addActionListener(this);
        delButton = new JButton("删除");
        delButton.addActionListener(this);
        buttonPanel.add(addButton, BorderLayout.NORTH);
        buttonPanel.add(delButton, BorderLayout.SOUTH);

        tableJpanel.add(contactsListPanel);
        tableJpanel.add(buttonPanel);

        // 确定、取消按钮
        okButton = new JButton("确定");
        okButton.addActionListener(this);
        cancelButton = new JButton("取消");
        cancelButton.addActionListener(this);
        buttonJpanel = new JPanel();
        buttonJpanel.add(okButton);
        buttonJpanel.add(cancelButton);

        // 整个界面区
        JPanel framePanel = new JPanel(new BorderLayout());
        // 编辑区
        framePanel.add(inputJpanel, BorderLayout.NORTH);
        framePanel.add(tableJpanel, BorderLayout.CENTER);
        framePanel.add(buttonJpanel, BorderLayout.SOUTH);
        this.add(framePanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            String name = nameInput.getText();
            String nick = nickInput.getText();
            String mail = mailInput.getText();
            if ( name == null || "".equals(name) ) {
                JOptionPane.showMessageDialog(this,"姓名必须输入！");
                return;
            }
            if (nick == null || "".equals(nick)) {
                JOptionPane.showMessageDialog(this,"昵称必须输入！");
                return;
            }
            if (mail==null || "".equals(mail)) {
                JOptionPane.showMessageDialog(this,"邮箱必须输入！");
                return;
            }
            String[] rowData = {name, nick, mail};
            tableModel.addRow(rowData);
            clearInputArea();
        } else if (e.getSource() == delButton) {
            if ( tableModel.getRowCount() == 0 ) {
                JOptionPane.showMessageDialog(this,"已经没有联系人了！");
            } else if ( selectedRow == -1 ) {
                JOptionPane.showMessageDialog(this,"请选择要删除的联系人！");
            } else if ( selectedRow <= tableModel.getRowCount()) {
                tableModel.removeRow(selectedRow);
                selectedRow = -1;
                clearInputArea();
            } else {

            }
        } else if (e.getSource() == okButton) {
            int rowCount = tableModel.getRowCount();
            List<Contacts> list = new ArrayList<>();

            for (int i = 0; i < rowCount; i++) {
                String name = (String) tableModel.getValueAt(i,0);
                String nick = (String) tableModel.getValueAt(i,1);
                String mail = (String) tableModel.getValueAt(i,2);
                Contacts contacts = new Contacts(name, nick, mail);
                list.add(contacts);
            }
            FileUtils.saveContacts(list);
            this.dispose();
//            new MainFrame().setVisible(true); // TODO:
            parentFrame.reloadContactsList();
        } else if (e.getSource() == cancelButton) {
            //关闭的提示选择
            int result = JOptionPane.showConfirmDialog(this, ("确认要关闭吗？"), ("关闭"), JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.NO_OPTION) {
                //不关闭
            } else {
                //关闭的处理
                this.dispose();
            }
        }
    }

    private void clearInputArea() {
        nameInput.setText("");
        nickInput.setText("");
        mailInput.setText("");
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void focusGained(FocusEvent e) {

    }

    @Override
    public void focusLost(FocusEvent e) {

    }
}
