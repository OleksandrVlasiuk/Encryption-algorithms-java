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
    private static JTextArea mTextArea;
    private static JTextArea tTextArea;

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame mainWindow = new JFrame("Knapsack's cipher");
            mainWindow.setSize(800, 800);
            mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


            //////////////////////////////////////////////

            //Панель інструментів
            JToolBar toolbar = new JToolBar();
            // Додав кнопки для інструментів
            JButton knapsackEncryptButton = new JButton("Шифрування рюдзака");
            JButton knapsackDecryptButton = new JButton("Розшифрування рюдзака");
            JButton openKeyButton = new JButton("Відкритий ключ");

            knapsackEncryptButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    resultTextArea.setText( new String(Arrays.toString(knapsackEncrypt(startTextArea.getText().getBytes() , parseStringToList(closedKeyTextArea.getText())))));
                }
            });

            knapsackDecryptButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int t = Integer.parseInt(tTextArea.getText());
                    int m = Integer.parseInt(mTextArea.getText());
                    resultTextArea.setText( knapsackDecrypt(parseStringToList(startTextArea.getText()) , parseStringToList(openKeyTextArea.getText()),t,m));
                }
            });

            openKeyButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Створення вікна для введення або генерації відкритого ключа
                    String openKeyInput = JOptionPane.showInputDialog(mainWindow,
                            "Введіть відкритий ключ або натисніть ОК для генерації");

                    if (openKeyInput != null && !openKeyInput.isEmpty()) {
                        openKeyTextArea.setText(openKeyInput);
                        List<Integer> publicKey = parseStringToList(openKeyInput);
                        int m = 450; // Modulus
                        int t = 31;   // Multiplier
                        mTextArea.setText(String.valueOf( m )); // Modulus
                        tTextArea.setText("31");
                        List<Integer> privateKey = generatePrivateKey(publicKey, t, m);

                        closedKeyTextArea.setText(privateKey.toString());
                    } else {
                        // Генерування нового випадкового відкритого ключа
                        List<Integer> publicKey = generateSuperIncreasingSequence(8); // Наприклад, 8 елементів
                        openKeyTextArea.setText(publicKey.toString());
                        mTextArea.setText(String.valueOf( generateM(publicKey) )); // Modulus
                        tTextArea.setText("31");   // Multiplier
                        int m = generateM(publicKey); // Modulus
                        int t = 31;   // Multiplier
                        List<Integer> privateKey = generatePrivateKey(publicKey, t, m);

                        closedKeyTextArea.setText(privateKey.toString());
                    }
                }
            });


            toolbar.add(knapsackEncryptButton);
            toolbar.add(knapsackDecryptButton);
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

            JLabel tLabel = new JLabel("Значення t  ");
            tTextArea = new JTextArea();
            JScrollPane tScrollPane = new JScrollPane(tTextArea);
            tScrollPane.setPreferredSize(new Dimension(100, 20));
            JLabel mLabel = new JLabel("Значення m ");
            mTextArea = new JTextArea();
            JScrollPane mScrollPane = new JScrollPane(mTextArea);
            mScrollPane.setPreferredSize(new Dimension(100, 20));
            gbc.gridy++;
            centerPanel.add(tLabel, gbc);
            centerPanel.add(tScrollPane, gbc);
            gbc.gridy++;
            centerPanel.add(mLabel, gbc);
            centerPanel.add(mScrollPane, gbc);

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

    // Function to generate a super-increasing sequence for the public key
    public static List<Integer> generateSuperIncreasingSequence(int n) {
        List<Integer> sequence = new ArrayList<>();
        Random random = new Random();

        sequence.add(random.nextInt(200) + 1);
        while (sequence.size() < n) {
            int nextElement = sequence.stream().reduce(0, Integer::sum) + random.nextInt(10) + 1;
            sequence.add(nextElement);
        }

        return sequence;
    }

    // Function to generate the private key from the public key
    public static List<Integer> generatePrivateKey(List<Integer> publicKey, int t, int m) {
        List<Integer> privateKey = new ArrayList<>();
        for (Integer element : publicKey) {
            privateKey.add((t * element) % m);
        }
        return privateKey;
    }
    // Function to encrypt the plaintext using the public key
    public static int knapsackEncryptSmall(String plaintext, List<Integer> privateKey) {
        int encryptedMessage = 0;
        for (int i = 0; i < plaintext.length(); i++) {
            if (plaintext.charAt(i) == '1') {
                encryptedMessage += privateKey.get(i);
            }
        }
        return encryptedMessage;
    }
    public static int[] knapsackEncrypt(byte[] plaintextBytes, List<Integer> privateKey) {
        int[] encryptedText = new int[plaintextBytes.length];
        String[] binaryStrings = convertBytesToBinaryStrings(plaintextBytes);
        System.out.println(Arrays.toString(binaryStrings));
        for (int i = 0; i < binaryStrings.length; i++) {
            String currentByte = binaryStrings[i];
            encryptedText[i] = knapsackEncryptSmall(currentByte,privateKey);
        }
        return encryptedText;
    }

    // Function to decrypt the ciphertext using the private key
    public static String knapsackDecryptSmall(int ciphertext, List<Integer> publicKey, int rInverse , int m) {
        StringBuilder decryptedMessage = new StringBuilder();
       int number = ciphertext * rInverse % m;
        for (int i = publicKey.size() - 1; i >= 0; i--) {
            System.out.println("Залишок "+ i + " :" + number);
            System.out.println(publicKey.get(i));
            if (number >= publicKey.get(i)) {
                decryptedMessage.insert(0, '1');
                //int r = ciphertext- publicKey.get(i);
                number -= publicKey.get(i);

                //System.out.println("Віднімаю зараз " + publicKey.get(i) + " , отримую " + r);
            } else {
                decryptedMessage.insert(0, '0');
            }
        }

        return decryptedMessage.toString();
    }
    public static String knapsackDecrypt(List<Integer> cipherText, List<Integer> publicKey, int t, int m) {
        int rInverse = modInverse(t, m); // Modular multiplicative inverse of t

        System.out.println(rInverse);
        String decryptedText = new String();
        System.out.println("t^-1 = " + rInverse);
        for (int i = 0; i < cipherText.size(); i++) {
            int ciphertext = cipherText.get(i);
            decryptedText += (char) Integer.parseInt(knapsackDecryptSmall(ciphertext, publicKey , rInverse , m) , 2);
        }
        return decryptedText;
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

    // Генерація числа m для закритого ключа
    public static int generateM(List<Integer> publicKey) {
        int sum = publicKey.stream().mapToInt(Integer::intValue).sum();
        int m = sum + 1; // Повинно бути більше за суму елементів у відкритому ключі
        return m;
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
    // Метод для перетворення масиву байтів у масив рядків у бінарному форматі
    public static String[] convertBytesToBinaryStrings(byte[] bytes) {
        String[] binaryStrings = new String[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            // Отримання бінарного представлення байту у вигляді рядка з 8 бітами
            String binaryString = String.format("%8s", Integer.toBinaryString(bytes[i] & 0xFF)).replace(' ', '0');
            binaryStrings[i] = binaryString;
        }
        return binaryStrings;
    }
}