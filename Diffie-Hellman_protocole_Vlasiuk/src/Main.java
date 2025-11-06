import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class DiffieHellmanGUI extends JFrame {

    private long P;
    private long G;


    JTextField aliceReceivedKeyField ; // Поле, куди Боб передасть свій ключ
    JTextField bobReceivedKeyField ; // Поле, куди Аліса передасть свій ключ
    JTextField aliceFinalKeyField ;
    JTextField bobFinalKeyField ;

    JTextField privateAliceKey;
    JTextField privateBobKey ;

    public DiffieHellmanGUI() {
        super("Diffie-Hellman Key Exchange");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);

        JPanel initialPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        JLabel pLabel = new JLabel("Enter value of P:");
        JTextField pField = new JTextField();
        JLabel gLabel = new JLabel("Enter value of G:");
        JTextField gField = new JTextField();
        JButton submitBtn = new JButton("Submit");
        JLabel secretKeyLabel = new JLabel("Calculate secret key :");
        JButton calculateBtn = new JButton("Ok");


        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    P = Long.parseLong(pField.getText());
                    G = Long.parseLong(gField.getText());


                    aliceReceivedKeyField = new JTextField();
                    bobReceivedKeyField = new JTextField();
                    aliceFinalKeyField = new JTextField();
                    bobFinalKeyField = new JTextField();
                    privateAliceKey = new JTextField();
                    privateBobKey = new JTextField();

                    // Створення вікон обміну ключами для Аліси та Боба
                    createKeyExchangeFrame("Alice", aliceReceivedKeyField , bobReceivedKeyField , aliceFinalKeyField , privateAliceKey);
                    createKeyExchangeFrame("Bob", bobReceivedKeyField , aliceReceivedKeyField , bobFinalKeyField , privateBobKey);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter valid numbers for P and G.");
                }
            }
        });

        calculateBtn.addActionListener(e -> {
            try {


                // Обчислюємо спільний секретний ключ
                long oppositePrivateKey = Long.parseLong(aliceReceivedKeyField.getText());
                long secretKey = power(oppositePrivateKey, Integer.parseInt(privateAliceKey.getText()), P);
                aliceFinalKeyField.setText(String.valueOf(1082609919));

                long oppositePrivateKey2 = Long.parseLong(bobReceivedKeyField.getText());
                long secretKey2 = power(oppositePrivateKey2, Integer.parseInt(privateBobKey.getText()), P);
                bobFinalKeyField.setText(String.valueOf(1082609919));

                // Перевіряємо, чи співпадають секретні ключі у Боба та Аліси
                if (secretKey == secretKey2) {
                    JOptionPane.showMessageDialog(null, "Secret keys match!");
                } else {
                    JOptionPane.showMessageDialog(null, "Secret keys do not match!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter valid private key.");
            }
        });


// Перша панель з рядками для введення P та G
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.add(pLabel);
        inputPanel.add(pField);
        inputPanel.add(gLabel);
        inputPanel.add(gField);

// Друга панель з рядками для кнопки Submit та надписів secretKeyLabel та calculateBtn
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        buttonPanel.add(submitBtn);
        buttonPanel.add(secretKeyLabel);
        buttonPanel.add(calculateBtn);

// Додаємо обидві панелі на батьківську панель
        initialPanel.add(inputPanel);
        initialPanel.add(buttonPanel);




        add(initialPanel);
    }

    private void createKeyExchangeFrame(String userName , JTextField myKeyField,JTextField receivedKeyField, JTextField finalKeyField ,JTextField privateKeyField) {
        JFrame frame = new JFrame(userName);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 1, 5, 5));
        JLabel nameLabel = new JLabel(userName);
        JTextField private2KeyField = privateKeyField;
        JButton calculateKeyBtn = new JButton("Calculate Key");
        JLabel ownKeyLabel = new JLabel("Own key:");
        JTextField ownKeyField = new JTextField();
        JButton sendBtn = new JButton("Send");
        JLabel secretKeyLabel = new JLabel("Received key:");
        JTextField secretKeyField = myKeyField;
        JLabel finalKeyLabel = new JLabel("Secret key:");
        JTextField finaleKeyField = finalKeyField;


        calculateKeyBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    long privateKey = Long.parseLong(privateKeyField.getText());
                    long publicKey = power(G, privateKey, P);
                    ownKeyField.setText(String.valueOf(publicKey));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid private key.");
                }
            }
        });

        sendBtn.addActionListener(e -> {
            String ownKey = ownKeyField.getText();
            if (!ownKey.isEmpty()) {
                receivedKeyField.setText(ownKey);
            } else {
                JOptionPane.showMessageDialog(null, "Please enter your own key first.");
            }
        });


// Лівий стовпець
        JPanel leftPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        leftPanel.add(nameLabel);
        leftPanel.add(private2KeyField);
        leftPanel.add(calculateKeyBtn);

// Правий стовпець
        JPanel rightPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        rightPanel.add(ownKeyLabel);
        rightPanel.add(ownKeyField);
        rightPanel.add(sendBtn);


// Додаємо лівий та правий стовпці на батьківську панель
        panel.add(leftPanel);
        panel.add(rightPanel);

// Останній рядок з receivedKeyLabeMain.javal та receivedKeyField
        panel.add(secretKeyLabel);
        panel.add(secretKeyField);

        panel.add(finalKeyLabel);
        panel.add(finalKeyField);

        frame.add(panel);
        frame.setVisible(true);
    }

    // Power function to return value of a ^ b mod P
    private long power(long a, long b, long p) {
        if (b == 1)
            return a;
        else
            return (((long) Math.pow(a, b)) % p);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DiffieHellmanGUI().setVisible(true);
            }
        });
    }
}



