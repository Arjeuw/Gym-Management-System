import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class GymGUI extends JFrame {

    // --- THEME COLORS ---
    private final Color HEADER_BG = new Color(21, 101, 192);   // Royal Blue
    private final Color HEADER_TEXT = Color.WHITE;
    private final Color BG_COLOR = new Color(245, 247, 250);   // Light Grey
    private final Color CARD_BG = Color.WHITE;
    private final Color ACCENT_COLOR = new Color(13, 71, 161); // Darker Blue
    private final Color DELETE_COLOR = new Color(198, 40, 40); // Red
    private final Color UPDATE_COLOR = new Color(255, 143, 0); // Amber/Orange

    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    private JTextField nameField, phoneField, searchField;
    private JComboBox<String> planBox;
    private JTable memberTable;
    private DefaultTableModel tableModel;

    public GymGUI() {
        setTitle("Gym Management System - Enterprise Edition");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. TOP NAVIGATION
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BG);
        headerPanel.setPreferredSize(new Dimension(1000, 60));
        headerPanel.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel titleLabel = new JLabel("GYM MANAGER");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(HEADER_TEXT);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        navPanel.setOpaque(false);

        JButton btnRegister = createNavButton("New Member", "REGISTER");
        JButton btnView = createNavButton("View List", "VIEW");

        navPanel.add(btnRegister);
        navPanel.add(btnView);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(navPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // 2. MAIN CONTENT
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(BG_COLOR);

        mainContentPanel.add(createRegisterScreen(), "REGISTER");
        mainContentPanel.add(createViewScreen(), "VIEW");

        add(mainContentPanel, BorderLayout.CENTER);

        DatabaseHandler.initDatabase();

        setVisible(true);
    }

    // --- SCREEN 1: REGISTER ---
    private JPanel createRegisterScreen() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(BG_COLOR);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        JLabel formTitle = new JLabel("Add New Member");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        formTitle.setForeground(ACCENT_COLOR);
        gbc.gridwidth = 2;
        card.add(formTitle, gbc);

        gbc.gridy++; gbc.gridwidth = 1;
        card.add(createLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        nameField = createField();
        card.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        card.add(createLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        phoneField = createField();
        card.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        card.add(createLabel("Membership Plan:"), gbc);
        gbc.gridx = 1;
        String[] plans = {"Monthly - ₹1,000", "Quarterly - ₹2,500", "Yearly - ₹8,000"};
        planBox = new JComboBox<>(plans);
        planBox.setBackground(Color.WHITE);
        planBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        card.add(planBox, gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 0, 10);
        JButton saveBtn = new JButton("SAVE MEMBER");
        saveBtn.setBackground(ACCENT_COLOR);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveBtn.setPreferredSize(new Dimension(200, 40));

        saveBtn.addActionListener(e -> {
            DatabaseHandler.addMember(nameField.getText(), phoneField.getText(), (String) planBox.getSelectedItem());
            JOptionPane.showMessageDialog(this, "Success!");
            nameField.setText(""); phoneField.setText("");
        });

        card.add(saveBtn, gbc);
        container.add(card);
        return container;
    }

    // --- SCREEN 2: VIEW / UPDATE / DELETE ---
    private JPanel createViewScreen() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Top: Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(BG_COLOR);

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> DatabaseHandler.loadMembers(tableModel, searchField.getText()));
        JButton refreshBtn = new JButton("Refresh List");
        refreshBtn.addActionListener(e -> DatabaseHandler.loadMembers(tableModel, ""));

        searchPanel.add(new JLabel("Find Member: "));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(refreshBtn);

        // Center: Table
        String[] cols = {"ID", "Name", "Phone", "Plan"};
        tableModel = new DefaultTableModel(cols, 0);
        memberTable = new JTable(tableModel);
        memberTable.setRowHeight(35);
        memberTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        memberTable.setShowVerticalLines(false);
        memberTable.setIntercellSpacing(new Dimension(0,0));

        JTableHeader header = memberTable.getTableHeader();
        header.setBackground(HEADER_BG);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(100, 40));

        // Bottom: Action Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        bottomPanel.setBackground(BG_COLOR);

        // 1. UPDATE BUTTON
        JButton updateBtn = new JButton("UPDATE SELECTED");
        updateBtn.setBackground(UPDATE_COLOR);
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        updateBtn.setPreferredSize(new Dimension(150, 40));

        updateBtn.addActionListener(e -> {
            int row = memberTable.getSelectedRow();
            if (row != -1) {
                // Get current values
                int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                String currentName = (String) tableModel.getValueAt(row, 1);
                String currentPhone = (String) tableModel.getValueAt(row, 2);
                String currentPlan = (String) tableModel.getValueAt(row, 3);

                // Create a Mini-Form for the popup
                JTextField editName = new JTextField(currentName);
                JTextField editPhone = new JTextField(currentPhone);
                String[] plans = {"Monthly - ₹1,000", "Quarterly - ₹2,500", "Yearly - ₹8,000"};
                JComboBox<String> editPlan = new JComboBox<>(plans);
                editPlan.setSelectedItem(currentPlan);

                Object[] message = {
                        "Name:", editName,
                        "Phone:", editPhone,
                        "Plan:", editPlan
                };

                int option = JOptionPane.showConfirmDialog(this, message, "Update Member Details", JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    DatabaseHandler.updateMember(id, editName.getText(), editPhone.getText(), (String) editPlan.getSelectedItem());
                    DatabaseHandler.loadMembers(tableModel, ""); // Refresh table
                    JOptionPane.showMessageDialog(this, "Member Updated Successfully!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "⚠️ Please select a row to update.");
            }
        });

        // 2. DELETE BUTTON
        JButton deleteBtn = new JButton("DELETE SELECTED");
        deleteBtn.setBackground(DELETE_COLOR);
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        deleteBtn.setPreferredSize(new Dimension(150, 40));

        deleteBtn.addActionListener(e -> {
            int row = memberTable.getSelectedRow();
            if (row != -1) {
                int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this member?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    DatabaseHandler.deleteMember(id);
                    DatabaseHandler.loadMembers(tableModel, "");
                    JOptionPane.showMessageDialog(this, "Member Deleted.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "⚠️ Please select a row to delete.");
            }
        });

        bottomPanel.add(updateBtn);
        bottomPanel.add(deleteBtn);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(memberTable), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    // --- HELPER COMPONENTS ---
    private JButton createNavButton(String text, String screenName) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(ACCENT_COLOR);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            cardLayout.show(mainContentPanel, screenName);
            if(screenName.equals("VIEW")) DatabaseHandler.loadMembers(tableModel, "");
        });
        return btn;
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        l.setForeground(Color.GRAY);
        return l;
    }

    private JTextField createField() {
        JTextField f = new JTextField(15);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setPreferredSize(new Dimension(200, 30));
        return f;
    }
}