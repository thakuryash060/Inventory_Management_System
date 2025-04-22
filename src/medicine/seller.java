package medicine;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

class Seller {
    JFrame frame = new JFrame("Seller");

    Seller(String t1) {
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel label = new JLabel("Welcome, Seller!");
        label.setFont(new Font("Arial", Font.BOLD, 22));
        frame.add(label);


        JButton add = new JButton("ADD");
        frame.add(add);

        add.addActionListener(e -> {
            String medname = JOptionPane.showInputDialog(frame, "ENTER MEDICINE NAME", "MEDICINE", JOptionPane.QUESTION_MESSAGE);

            if (medname != null && !medname.isEmpty()) {
                if (medname.matches("[a-zA-Z ]+")) {
                    String quant = JOptionPane.showInputDialog(frame, "ENTER QUANTITY (10000 - 20000)", "QUANTITY", JOptionPane.QUESTION_MESSAGE);

                    if (quant != null && !quant.isEmpty()) {
                        try {
                            int quantity = Integer.parseInt(quant);
                            if (quantity >= 10000 && quantity <= 20000) {
                                if (!cross_check(t1, medname, quantity)) {
                                    adding_db(t1, medname, quantity);
                                }
                            } else {
                                JOptionPane.showMessageDialog(frame, "Please enter quantity between 10000 and 20000.");
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(frame, "Invalid quantity. Please enter numbers only.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "Quantity cannot be empty.");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Medicine name must contain only letters and spaces.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Medicine name cannot be empty.");
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

    void adding_db(String usr, String med, int quantity) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/medicine_inventory", "root", "radicals133@")) {
            String insertQuery = "INSERT INTO transaction (user_id, med_name, quantity) VALUES (?, ?, ?)";
            try (PreparedStatement pt = con.prepareStatement(insertQuery)) {
                pt.setString(1, usr);
                pt.setString(2, med);
                pt.setInt(3, quantity);
                pt.executeUpdate();
            }

            String insertStockQuery = "INSERT INTO stock (user_id, med_name, quantity) VALUES (?, ?, ?)";
            try (PreparedStatement pt = con.prepareStatement(insertStockQuery)) {
                pt.setString(1, usr);
                pt.setString(2, med);
                pt.setInt(3, quantity);
                pt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error adding stock to the database.");
        }
    }

    boolean cross_check(String t1, String med, int quantt) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/medicine_inventory", "root", "radicals133@")) {
            String checkQuery = "SELECT * FROM stock WHERE user_id = ? AND med_name = ?";
            try (PreparedStatement ptt = con.prepareStatement(checkQuery)) {
                ptt.setString(1, t1);
                ptt.setString(2, med);

                try (ResultSet rs = ptt.executeQuery()) {
                    if (rs.next()) {

                        int cquany = rs.getInt("quantity");
                        int nquany = cquany + quantt;


                        String updateQuery = "UPDATE stock SET quantity = ? WHERE user_id = ? AND med_name = ?";
                        try (PreparedStatement updateStmt = con.prepareStatement(updateQuery)) {
                            updateStmt.setInt(1, nquany);
                            updateStmt.setString(2, t1);
                            updateStmt.setString(3, med);
                            updateStmt.executeUpdate();
                        }


                        String insertTransactionQuery = "INSERT INTO transaction (user_id, med_name, quantity) VALUES (?, ?, ?)";
                        try (PreparedStatement insertStmt = con.prepareStatement(insertTransactionQuery)) {
                            insertStmt.setString(1, t1);
                            insertStmt.setString(2, med);
                            insertStmt.setInt(3, quantt);
                            insertStmt.executeUpdate();
                        }

                        JOptionPane.showMessageDialog(frame, "Stock updated and transaction recorded successfully!");
                        return true;
                    } else {

                        return false;
                    }
                }
            }
        } catch (SQLException we) {
            JOptionPane.showMessageDialog(frame, "SQL Exception: " + we.getMessage());
            return false;
        }
    }

}
