package medicine;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

class Forget {
    Forget() {
        JFrame frame2 = new JFrame("FORGET PASSWORD");
        frame2.setLayout(null);

        JLabel label1 = new JLabel("FORGET PASSWORD", JLabel.CENTER);
        label1.setFont(new Font("Arial", Font.BOLD, 24));
        label1.setBounds(0, 25, 800, 50);
        frame2.add(label1);

        JLabel name = new JLabel("Name");
        name.setFont(new Font("Arial", Font.BOLD, 18));
        name.setBounds(200, 120, 100, 30);
        frame2.add(name);

        JTextField t1 = new JTextField();
        t1.setBounds(310, 120, 200, 30);
        frame2.add(t1);

        JLabel username = new JLabel("Username");
        username.setFont(new Font("Arial", Font.BOLD, 18));
        username.setBounds(200, 170, 100, 30);
        frame2.add(username);

        JTextField t2 = new JTextField();
        t2.setBounds(310, 170, 200, 30);
        frame2.add(t2);

        JButton next = new JButton("NEXT");
        next.setBounds(300, 250, 200, 30);
        frame2.add(next);

        next.addActionListener(e -> {
            String a = t1.getText(); // name
            String b = t2.getText(); // username

            t1.setText("");
            t2.setText("");

            try {
                Connection con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/medicine_inventory", "root", "radicals133@");
                String checkQuery = "SELECT password FROM detail WHERE user_id = ? AND name = ?";
                PreparedStatement ptt = con.prepareStatement(checkQuery);
                ptt.setString(1, b); // user_id
                ptt.setString(2, a); // name

                ResultSet rs = ptt.executeQuery();

                if (rs.next()) {
                    String pwd = rs.getString("password");
                    JOptionPane.showMessageDialog(frame2, "Your password is: " + pwd);
                    Sign_in p =new Sign_in();
                    frame2.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame2, "Invalid Username or Name.");
                }

                rs.close();
                ptt.close();
                con.close();

            } catch (SQLException we) {
                JOptionPane.showMessageDialog(frame2, "SQL Exception: " + we.getMessage());
            }
        });

        frame2.setSize(800, 600);
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.setLocationRelativeTo(null);
        frame2.setVisible(true);
    }
}
