package SwingForms;

import javax.swing.*;

public class Main extends JFrame
{

    private JButton button1;
    private JPanel MainPanel;

    public Main()
    {
        setContentPane(MainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args)
    {
        new Main();
    }
}
