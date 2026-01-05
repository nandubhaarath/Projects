package SwingForms;

import javax.swing.*;

import DatabaseManagement.DatabaseService;
import UserManagement.Address;

public class GenerateRouteMapForm extends  JFrame {
    private JTextField txtStartPostcode;
    private JPanel MainPanel;
    private JLabel lblStartPostCode;
    private JTextField textField1;
    private JLabel lblEndPostcode;
    private JLabel lblheader;

    public GenerateRouteMapForm() {
        setContentPane(MainPanel);
        setTitle("Route Map Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,350);
        setLocationRelativeTo(null);
        setVisible(true);

        loadFoodBankAddress(); // Automatically load the food bank address on form load
    }

    private void loadFoodBankAddress() {
        DatabaseService databaseService = new DatabaseService();
        Address foodBankAddress = databaseService.GetFoodBankAddress();

        if (foodBankAddress != null) {

            String fullAddress=foodBankAddress.getFullAddress();
            txtStartPostcode.setText(fullAddress);

        } else {
            JOptionPane.showMessageDialog(MainPanel, "Error: Could not fetch the food bank address.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new GenerateRouteMapForm();
    }


}
