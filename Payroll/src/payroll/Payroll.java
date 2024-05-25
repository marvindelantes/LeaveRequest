/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package payroll;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.DefaultCellEditor;
import javax.swing.border.LineBorder;

public class Payroll extends JFrame {

    private JPanel leaveRequestPanel;
    private JTable leaveRequestsTable;
    private static final String filePath = "C:\\Users\\Windows\\Downloads\\MotorPH-App-master\\Leave Request.csv";

    public Payroll() {
        initComponents();
        loadLeaveRequests();
        addButtonsToTable();
    }

    private void initComponents() {
        leaveRequestPanel = new JPanel();
        leaveRequestsTable = new JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Payroll Page");

        leaveRequestPanel.setLayout(new java.awt.BorderLayout());
        leaveRequestPanel.setBackground(Color.WHITE);

        // Get screen width and set preferred size to 1/2 of the screen width
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int panelWidth = screenWidth / 2;

        leaveRequestPanel.setPreferredSize(new Dimension(panelWidth, 800));

        leaveRequestsTable.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Employee ID", "Name", "Leave Date", "Reason", "Status", "Action"
                }
        ));
        leaveRequestsTable.setRowHeight(45); // Increase row height

        JScrollPane scrollPane = new JScrollPane(leaveRequestsTable);
        scrollPane.setPreferredSize(new Dimension(panelWidth, 800));

        leaveRequestPanel.add(scrollPane, java.awt.BorderLayout.CENTER);

        add(leaveRequestPanel, java.awt.BorderLayout.CENTER); // Changed to CENTER

        // Set preferred widths for specific columns
        int[] preferredWidths = {500, 500, 500, 500, 500, 800};
        for (int i = 0; i < preferredWidths.length; i++) {
            TableColumn column = leaveRequestsTable.getColumnModel().getColumn(i);
            column.setPreferredWidth(preferredWidths[i]);
        }

        pack();
    }

    private void loadLeaveRequests() {
        DefaultTableModel model = (DefaultTableModel) leaveRequestsTable.getModel();
        model.setRowCount(0);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                model.addRow(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addButtonsToTable() {
        TableColumn actionColumn = leaveRequestsTable.getColumn("Action");
        actionColumn.setCellRenderer(new ButtonRenderer());
        actionColumn.setCellEditor(new ButtonEditor(new JCheckBox()));
        actionColumn.setPreferredWidth(800); // Increase column width
    }

    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private final JButton approveButton;
        private final JButton rejectButton;

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
            approveButton = new JButton("Approve");
            rejectButton = new JButton("Reject");
            setButtonProperties(approveButton, Color.decode("#FF8C32"));
            setButtonProperties(rejectButton, Color.decode("#FF9898"));
            add(approveButton);
            add(rejectButton);
        }

        private void setButtonProperties(JButton button, Color color) {
            button.setBackground(color);
            button.setForeground(Color.WHITE);
            button.setOpaque(true);
            button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.BLACK), 
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            button.setFocusPainted(false);
            button.setFont(new Font("Arial", Font.BOLD, 12));
            button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private final JPanel panel;
        private final JButton approveButton;
        private final JButton rejectButton;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new GridLayout(1, 2));
            approveButton = new JButton("Approve");
            rejectButton = new JButton("Reject");
            setButtonProperties(approveButton, Color.decode("#FF8C32"));
            setButtonProperties(rejectButton, Color.decode("#FF9898"));

            panel.add(approveButton);
            panel.add(rejectButton);

            approveButton.addActionListener(e -> {
                updateLeaveRequestStatus(leaveRequestsTable.getSelectedRow(), "Approved");
                fireEditingStopped();
            });

            rejectButton.addActionListener(e -> {
                updateLeaveRequestStatus(leaveRequestsTable.getSelectedRow(), "Rejected");
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }

        private void setButtonProperties(JButton button, Color color) {
            button.setBackground(color);
            button.setForeground(Color.WHITE);
            button.setOpaque(true);
            button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.BLACK), 
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            button.setFocusPainted(false);
            button.setFont(new Font("Arial", Font.BOLD, 12));
            button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        }
    }

    private void updateLeaveRequestStatus(int row, String status) {
        DefaultTableModel model = (DefaultTableModel) leaveRequestsTable.getModel();
        model.setValueAt(status, row, 4); // Assuming the status column is the 5th column

        try (FileWriter fw = new FileWriter(filePath)) {
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    if (j != 5) { // Skip the Action column
                        fw.write(model.getValueAt(i, j).toString() + (j == model.getColumnCount() - 1 ? "" : ","));
                    }
                }
                fw.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new Payroll().setVisible(true));
    }
}
