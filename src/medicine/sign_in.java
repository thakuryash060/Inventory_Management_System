package medicine;
import javax.swing.*;
import javax.swing.JTextField;
import java.awt.*;
import java.sql.*;

class Sign_in {
    JFrame frame1 = new JFrame("Sign In");
    Sign_in() {

        frame1.setLayout(null);
        frame1.setSize(800, 600);
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        JLabel label1 = new JLabel("MEDICINE INVENTORY MANAGEMENT SYSTEM", JLabel.CENTER);
        label1.setFont(new Font("Arial", Font.BOLD, 24));
        label1.setBounds(0, 25, 800, 50);
        frame1.add(label1);


        JLabel username = new JLabel("Username");
        username.setFont(new Font("Arial", Font.BOLD, 18));
        username.setBounds(200, 120, 100, 30);
        frame1.add(username);



        JTextField t1 = new JTextField();
        t1.setBounds(310, 120, 200, 30);
        frame1.add(t1);




        JLabel password = new JLabel("Password");
        password.setFont(new Font("Arial", Font.BOLD, 18));
        password.setBounds(200, 170, 100, 30);
        frame1.add(password);

        JPasswordField t2 = new JPasswordField();
        t2.setBounds(310, 170, 200, 30);
        frame1.add(t2);




        JButton signin = new JButton("SIGN IN");
        signin.setBounds(300,250,200,30);
        frame1.add(signin);
        signin.addActionListener(e -> {
            String tt1 = t1.getText();
            String tt2 = new String(t2.getPassword());
            t1.setText("");
            t2.setText("");
            if(validate(tt1,tt2)){
            JOptionPane.showMessageDialog(frame1,"LOGIN SUCCESSFUL");
            frame1.dispose();
            }

            else{
                JOptionPane.showMessageDialog(frame1,"NOT FOUND IN DATABASE");
            }
        });

        JButton forget = new JButton("FORGET PASSWORD");
        forget.setBounds(50, 500, 200, 30);
        forget.addActionListener(e -> {
            Forget h = new Forget();
            frame1.dispose();
        });
        frame1.add(forget);




        JButton signup = new JButton("SIGN UP");
        signup.setBounds(550, 500, 200, 30);
        frame1.add(signup);
        signup.addActionListener(e -> {
            Sign_up u = new Sign_up();
            frame1.dispose();
        });


        frame1.setLocationRelativeTo(null);
        frame1.setVisible(true);
    }


    public boolean validate(String t1,String t2){
    if (t1.length()== 0 || t2.length()==0){
        return false;
    }

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/medicine_inventory", "root", "radicals133@");
            String checkQuery = "SELECT * FROM detail WHERE user_id = ? AND password = ?";
            PreparedStatement ptt = con.prepareStatement(checkQuery);
            ptt.setString(1, t1);
            ptt.setString(2, t2);

            ResultSet rs = ptt.executeQuery();

            if (rs.next()) {
               String ro = rs.getString("role");
               if(ro.equalsIgnoreCase("Customer")){
                  Customer r = new Customer(t1);
                   return true;
               }
               else{Seller a = new Seller(t1);
                   return true;}
            } else {
                JOptionPane.showMessageDialog(frame1, "Invalid Username or Name.");
            }

            rs.close();
            ptt.close();
            con.close();

        } catch (SQLException we) {
        }
    return false;
    }


    public static void main(String[] args) {
      Sign_in a = new Sign_in();
    }
}
