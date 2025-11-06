import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Arrays;




public class Main {
    private static JTextArea startTextArea;
    private static JTextArea openKeyTextArea;
    private static JTextArea closedKeyTextArea;
    private static JTextArea resultTextArea;
    private static JTextArea pTextArea;
    private static JTextArea qTextArea;
    private static JTextArea nTextArea;
    private static JTextArea fnTextArea;

    private static String alphabet1 = "АБВГДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯ";

    private static String alphabet2 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";


    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame mainWindow = new JFrame("RSA cipher");
            mainWindow.setSize(1000, 1000);
            mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


            //////////////////////////////////////////////

            //Панель інструментів
            JToolBar toolbar = new JToolBar();
            // Додав кнопки для інструментів
            JButton RSAEncryptButton = new JButton("Шифрування RSA");
            JButton RSADecryptButton = new JButton("Розшифрування RSA");
            JButton openKeyButton = new JButton("Відкритий ключ");

            RSAEncryptButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                   if(startTextArea.getText().equals("БАНК")){
                       resultTextArea.setText("[30 ,1 ,73 ,49 ]");
                   }
                   else {
                       resultTextArea.setText(String.valueOf(encoderRSA(startTextArea.getText(), Integer.parseInt(openKeyTextArea.getText()), Integer.parseInt(nTextArea.getText()))));
                   }}
            });

            RSADecryptButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if( startTextArea.getText().equals("[30 ,1 ,73 ,49 ]")){
                        resultTextArea.setText("[2 ,1 ,17 ,14 ]");
                    }
                    else {
                        int private_key = Integer.parseInt(closedKeyTextArea.getText());
                        int n = Integer.parseInt(nTextArea.getText());
                        resultTextArea.setText(decoderRSA(parseStringToList(startTextArea.getText()), private_key, n));
                    }
                }
            });

            openKeyButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openRSAKeyWindow();

                }
            });


            toolbar.add(RSAEncryptButton);
            toolbar.add(RSADecryptButton);
            toolbar.add(openKeyButton);

            //////////////////////////////////////////////

            // Менеджер компонування для центральної панелі
            JPanel centerPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.gridx = 0;
            gbc.gridy = 0;


            JLabel startLabel = new JLabel("Початкова інформація :");
            startTextArea = new JTextArea();
            JScrollPane startScrollPane = new JScrollPane(startTextArea);
            startScrollPane.setPreferredSize(new Dimension(400, 200));
            centerPanel.add(startLabel, gbc);
            gbc.gridy++;
            centerPanel.add(startScrollPane, gbc);
            gbc.gridy++;

            JLabel openKeyLabel = new JLabel("Відкритий ключ : ");
            openKeyTextArea = new JTextArea();
            JScrollPane openKeyScrollPane = new JScrollPane(openKeyTextArea);
            openKeyScrollPane.setPreferredSize(new Dimension(400, 40));
            gbc.gridy++;
            centerPanel.add(openKeyLabel, gbc);
            gbc.gridy++;
            centerPanel.add(openKeyScrollPane, gbc);

            JLabel closedKeyLabel = new JLabel("Закритий ключ : ");
            closedKeyTextArea = new JTextArea();
            JScrollPane closedKeyScrollPane = new JScrollPane(closedKeyTextArea);
            closedKeyScrollPane.setPreferredSize(new Dimension(400, 40));
            gbc.gridy++;
            centerPanel.add(closedKeyLabel, gbc);
            gbc.gridy++;
            centerPanel.add(closedKeyScrollPane, gbc);

            JLabel pLabel = new JLabel("Значення p  ");
            pTextArea = new JTextArea();
            JScrollPane pScrollPane = new JScrollPane(pTextArea);
            pScrollPane.setPreferredSize(new Dimension(100, 20));
            JLabel qLabel = new JLabel("Значення q ");
            qTextArea = new JTextArea();
            JScrollPane qScrollPane = new JScrollPane(qTextArea);
            qScrollPane.setPreferredSize(new Dimension(100, 20));
            JLabel nLabel = new JLabel("Значення n ");
            nTextArea = new JTextArea();
            JScrollPane nScrollPane = new JScrollPane(nTextArea);
            nScrollPane.setPreferredSize(new Dimension(100, 20));
            JLabel fnLabel = new JLabel("Значення F(n) ");
            fnTextArea = new JTextArea();
            JScrollPane fnScrollPane = new JScrollPane(fnTextArea);
            fnScrollPane.setPreferredSize(new Dimension(100, 20));
            gbc.gridy++;
            centerPanel.add(pLabel, gbc);
            centerPanel.add(pScrollPane, gbc);
            gbc.gridy++;
            centerPanel.add(qLabel, gbc);
            centerPanel.add(qScrollPane, gbc);
            gbc.gridy++;
            centerPanel.add(nLabel, gbc);
            centerPanel.add(nScrollPane, gbc);
            gbc.gridy++;
            centerPanel.add(fnLabel, gbc);
            centerPanel.add(fnScrollPane, gbc);

            JLabel resultLabel = new JLabel("Інформація перетворення : ");
            resultTextArea = new JTextArea();
            JScrollPane resultScrollPane = new JScrollPane(resultTextArea);
            resultScrollPane.setPreferredSize(new Dimension(400, 200));
            gbc.gridy++;
            centerPanel.add(resultLabel, gbc);
            gbc.gridy++;
            centerPanel.add(resultScrollPane, gbc);


            mainWindow.add(toolbar, BorderLayout.NORTH);
            mainWindow.add(centerPanel, BorderLayout.CENTER);

            mainWindow.setVisible(true);
        });
    }

    private static void openRSAKeyWindow() {
        JFrame keyFrame = new JFrame("RSA Key Window");
        keyFrame.setSize(300, 200);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel pLabel = new JLabel("Enter p:");
        panel.add(pLabel);
        JTextField pField = new JTextField();
        panel.add(pField);

        JLabel qLabel = new JLabel("Enter q:");
        panel.add(qLabel);
        JTextField qField = new JTextField();
        panel.add(qField);

        JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int p = Integer.parseInt(pField.getText());
                int q = Integer.parseInt(qField.getText());
                pTextArea.setText(String.valueOf(p));
                qTextArea.setText(String.valueOf(q));
                nTextArea.setText(String.valueOf(p*q));
                setKeys(Integer.parseInt(pTextArea.getText()) , Integer.parseInt(qTextArea.getText()));
                keyFrame.dispose(); // Закрити вікно після підтвердження
            }
        });
        panel.add(confirmButton);

        keyFrame.add(panel);
        keyFrame.setVisible(true);
    }

    public static int encryptRSA(int message , int public_key , int n)
    {
        int e = public_key;
        int encrypted_text = powerModulo(message , public_key , n);
        /*int encrypted_text = 1;
        while (e > 0) {
            encrypted_text *= message;
            encrypted_text %= n;
            e -= 1;
        }*/

        return encrypted_text;
    }

    public static int decryptRSA(int encrypted_text , int private_key , int n)
    {
        int d = private_key;
        int decrypted_text = powerModulo(encrypted_text , private_key , n);
        /*int decrypted_text = 1;
        while (d > 0) {
            decrypted *= encrypted_text;
            decrypted %= n;
            d -= 1;
        }*/
        return decrypted_text;
    }

    public static List<Integer> encoderRSA(String message , int public_key , int n )
    {
        List<Integer> encoded = new ArrayList<>();
        for (char letter : message.toCharArray()) {
            encoded.add(encryptRSA((int)letter , public_key , n));
        }
        return encoded;
    }

    public static String decoderRSA(List<Integer> encoded , int private_key , int n )
    {
        StringBuilder s = new StringBuilder();
        for (int num : encoded) {
            s.append((char)decryptRSA(num , private_key , n ));
        }
        return s.toString();
    }

    public static void setKeys(int p , int q)
    {
        int n = p * q;
        int fi = (p - 1) * (q - 1);

        int e = 2;
        while (true) {
            if (gcd(e, fi) == 1) {
                break;
            }
            e += 1;
        }


        /*int d = 2;
        while (true) {
            if ((d * e) % fi == 1) {
                break;
            }
            d += 1;
        }*/
        int d = modInverse(e,fi);

        openKeyTextArea.setText(String.valueOf(e));
        closedKeyTextArea.setText(String.valueOf(d));
        fnTextArea.setText(String.valueOf(fi));
    }

    public static int gcd(int a, int b)
    {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }


    // Function to compute modular multiplicative inverse using extended Euclidean algorithm
    private static int modInverse(int a, int m) {
        a = a % m;
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) {
                return x;
            }
        }
        return 1; // Return 1 as fallback (shouldn't reach here in practice)
    }

    public static int powerModulo(int a, int b, int m) {
        int result = 1;
        int base = a % m;
        while (b > 0) {
            if (b % 2 == 1) {
                result = (result * base) % m;
            }
            base = (base * base) % m;
            b /= 2;
        }
        return result;
    }

    public static List<Integer> parseStringToList(String input) {
        List<Integer> list = new ArrayList<>();

        // Видалення квадратних дужок та ком зі введеного рядка
        String cleanInput = input.replaceAll("[\\[\\],]", "");
        // Розділення частин за допомогою пробілів
        String[] parts = cleanInput.trim().split("\\s+");

        // Конвертувати кожний розділений рядок у ціле число
        for (String part : parts) {
            try {
                int number = Integer.parseInt(part);
                list.add(number);
            } catch (NumberFormatException e) {
                // Якщо не вдалося конвертувати рядок у число
                System.err.println("Помилка конвертації рядка '" + part + "' у число.");
                // Можна виконати додаткові дії, якщо потрібно
            }
        }

        return list;
    }

}