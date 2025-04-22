package medicine;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;

class Customer {
    JFrame frame;
    JComboBox<String> medicine;
    HashMap<String, Integer> stockMap = new HashMap<>();

    Customer(String t1) {
        frame = new JFrame("Customer Dashboard");
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel heading = new JLabel("Select Medicine:");
        heading.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(heading);

        medicine = new JComboBox<>();
        loadMedicines();
        frame.add(medicine);

        JButton b1 = new JButton("Order");
        frame.add(b1);

        b1.addActionListener(e -> {
            String selectedMed = (String) medicine.getSelectedItem();
            if (selectedMed != null) {
                String quantityInput = JOptionPane.showInputDialog(frame, "Enter quantity for " + selectedMed + ":");

                if (quantityInput != null && !quantityInput.isEmpty()) {
                    try {
                        int userQty = Integer.parseInt(quantityInput);
                        int availableQty = stockMap.get(selectedMed);

                        if (userQty <= availableQty && userQty > 0) {

                            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/medicine_inventory", "root", "radicals133@")) {


                                String updateStockQuery = "UPDATE stock SET quantity = quantity - ? WHERE med_name = ? AND quantity >= ? LIMIT 1";
                                try (PreparedStatement updateStmt = con.prepareStatement(updateStockQuery)) {
                                    updateStmt.setInt(1, userQty);
                                    updateStmt.setString(2, selectedMed);
                                    updateStmt.setInt(3, userQty);
                                    int updated = updateStmt.executeUpdate();

                                    if (updated > 0) {
                                        // Step 2: Insert transaction
                                        String insertQuery = "INSERT INTO transaction (user_id, med_name, quantity) VALUES (?, ?, ?)";
                                        try (PreparedStatement stmt = con.prepareStatement(insertQuery)) {
                                            stmt.setString(1, t1);
                                            stmt.setString(2, selectedMed);
                                            stmt.setInt(3, userQty);
                                            stmt.executeUpdate();
                                        }

                                        JOptionPane.showMessageDialog(frame, "Order confirmed for " + selectedMed + " (Qty: " + userQty + ")");
                                        stockMap.put(selectedMed, availableQty - userQty); // update local copy
                                    } else {
                                        JOptionPane.showMessageDialog(frame, "Failed to update stock. Please try again.");
                                    }
                                }

                            } catch (SQLException ex) {
                                JOptionPane.showMessageDialog(frame, "SQL Error: " + ex.getMessage());
                            }

                        } else {
                            JOptionPane.showMessageDialog(frame, "Only " + availableQty + " available. Please enter a valid quantity.");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Please enter a valid number.");
                    }
                }
            }
        });

        JButton logout = new JButton("LOGOUT");
        frame.add(logout);

        logout.addActionListener(e -> {
            new Sign_in();
            frame.dispose();
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    void loadMedicines() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/medicine_inventory", "root", "radicals133@")) {
            String query = "SELECT med_name, SUM(quantity) as total_quantity FROM stock GROUP BY med_name";
            try (PreparedStatement pst = con.prepareStatement(query); ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String medName = rs.getString("med_name");
                    int quantity = rs.getInt("total_quantity");
                    medicine.addItem(medName);
                    stockMap.put(medName, quantity);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error loading stock: " + e.getMessage());
        }
    }
}
