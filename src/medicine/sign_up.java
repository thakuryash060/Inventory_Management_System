package medicine;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.regex.*;

class Sign_up {
    JFrame frame3 = new JFrame("SIGN UP");
    Sign_up() {

        frame3.setLayout(new GridLayout(7,2,10,10));
        frame3.setSize(800, 600);
        frame3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label1 = new JLabel("SIGN UP", JLabel.CENTER);
        label1.setFont(new Font("Arial", Font.BOLD, 24));
        frame3.add(label1);
        frame3.add(new JLabel());

        frame3.add(new JLabel("Name"));
        JTextField name = new JTextField();
        frame3.add(name);


        frame3.add(new JLabel("Age"));
        String agge[] = new String[84];
        agge[0] = "ENTER AGE";
        for (int i = 1; i < agge.length; i++) {
            agge[i] = String.valueOf(17 + i);
        }
        JComboBox age = new JComboBox<>(agge);
        frame3.add(age);


        frame3.add(new JLabel("Role"));
        String rolle[] = {"Choose Option","Seller", "Customer"};
        JComboBox<String> role = new JComboBox<>(rolle);
        frame3.add(role);


        frame3.add(new JLabel("Username"));
        JTextField username= new JTextField();
        frame3.add(username);


        frame3.add(new JLabel("Password"));
        JPasswordField password = new JPasswordField();
        frame3.add(password);


        frame3.add(new JLabel(""));
        JButton b1= new JButton("SIGN UP");
        frame3.add(b1);

        b1.addActionListener(e -> {
            String a = name.getText().trim();
            String b = username.getText().trim();
            String c = new String(password.getPassword());
            String d = (String)age.getSelectedItem();
            String ee = (String)role.getSelectedItem();
            username.setText("");
            name.setText("");
            password.setText("");
            age.setSelectedIndex(0);
            role.setSelectedIndex(0);

            try {
                if (validate(a, b, c, d, ee)) {
                    JOptionPane.showMessageDialog(frame3, "SIGNED UP SUCCESSFULLY");
                    Sign_in bb = new Sign_in();
                    frame3.dispose();
                }
            } catch (SQLException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(frame3, "SQL Exception or ClassNotFoundException: " + ex.getMessage());
            }
        });

        frame3.setVisible(true);
        frame3.setLocationRelativeTo(null);
    }

    boolean validate(String a, String b, String c, String d, String ee) throws SQLException, ClassNotFoundException {
        if (a.length() == 0 || b.length() == 0 || c.length() == 0) {
            JOptionPane.showMessageDialog(frame3, "Can't be stored empty value");
            return false;
        }

        if (b.length() < 2 || b.length() > 8 || c.length() < 2 || c.length() > 9) {
            JOptionPane.showMessageDialog(frame3, "For username, Minimum length should be 3 (e.g., Yash123) and maximum 8. For password, min 3 (e.g., Yash@123), max 9, and include exactly one special character.");
            return false;
        }

        if (!a.matches("[a-zA-Z ]+")) {
            JOptionPane.showMessageDialog(frame3, "INVALID NAME");
            return false;
        }

        Pattern p = Pattern.compile("^(?=[a-zA-Z\\d]*[^a-zA-Z\\d][a-zA-Z\\d]*$)[a-zA-Z\\d]*[^a-zA-Z\\d][a-zA-Z\\d]*$");
        Matcher m = p.matcher(c);
        if (!m.matches()) {
            JOptionPane.showMessageDialog(frame3, "Password must contain exactly one special character.");
            return false;
        }

        Pattern pp = Pattern.compile("[a-zA-Z]+\\d+");
        Matcher r = pp.matcher(b);
        if (!r.matches()) {
            JOptionPane.showMessageDialog(frame3, "Username must be like Yash123 (letters followed by numbers).");
            return false;
        }

        if (ee.equals("Choose Option")) {
            JOptionPane.showMessageDialog(frame3, "Choose valid role");
            return false;
        }

        if (d.equals("ENTER AGE")) {
            JOptionPane.showMessageDialog(frame3, "Choose valid age");
            return false;
        }

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/medicine_inventory", "root", "radicals133@")){
            String checkQuery = "SELECT user_id from detail WHERE user_id = ?";
            try(PreparedStatement ptt = con.prepareStatement(checkQuery)){
                ptt.setString(1,b);
                ResultSet rs = ptt.executeQuery();
                if(rs.next()){
                    JOptionPane.showMessageDialog(frame3,"Username is already registered");
                    return false;
                }
            }
        }


        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/medicine_inventory", "root", "radicals133@")) {
            String insertQuery = "INSERT INTO detail (name, user_id, password, role, age) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pt = con.prepareStatement(insertQuery)) {
                pt.setString(1, a);
                pt.setString(2, b);
                pt.setString(3, c);
                pt.setString(4, ee);
                pt.setString(5, d);
                pt.executeUpdate();
            }
        }
        return true;
    }
}
