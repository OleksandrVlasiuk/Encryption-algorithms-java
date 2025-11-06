import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.awt.print.*;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.regex.Pattern;



public class Main {
    private static JTextArea startTextArea;
    private static JTextArea gamaTextArea;
    private static JTextArea resultTextArea;
    private static File currentFile;
    private static String savedText;
    private static JLabel fileNameLabel;
    private static JComboBox<String> alphabetComboBox;
    private static Map<String, String> alphabets = new HashMap<>();
    private static byte[] gama;


    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame mainWindow = new JFrame("Caesar's cipher");
            mainWindow.setSize(800, 800);
            mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


            // Меню
            JMenuBar menuBar = new JMenuBar();
            JMenu fileMenu = new JMenu("Файл");
            JMenuItem createItem = new JMenuItem("Створити .txt");
            JMenuItem openItem = new JMenuItem("Відкрити .txt");
            JMenuItem saveItem = new JMenuItem("Зберегти .txt");

            JMenuItem printItem = new JMenuItem("Друкувати .txt");


            // Додав обробники подій для пунктів меню
            createItem.addActionListener(e -> createFile());
            openItem.addActionListener(e -> openFile());
            saveItem.addActionListener(e -> saveFile());
            printItem.addActionListener(e -> printFile());

            fileMenu.add(createItem);
            fileMenu.add(openItem);
            fileMenu.add(saveItem);
            fileMenu.add(printItem);

            menuBar.add(fileMenu);
            mainWindow.setJMenuBar(menuBar);

            //////////////////////////////////////////////

            //Панель інструментів
            JToolBar toolbar = new JToolBar();
            // Додав кнопки для інструментів
            JButton gammingEncryptButton = new JButton("Шифрування гамування");
            JButton gammingDecryptButton = new JButton("Розшифрування гамування");
            JButton vernamEncryptButton = new JButton("Шифрування Вернама");
            JButton vernamDecryptButton = new JButton("Розшифрування Вернама");
            JButton ganerateGamaButton = new JButton("Генерація гами");

            gammingEncryptButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    resultTextArea.setText( new String(EncryptGamming(startTextArea.getText().getBytes() , gama) ));
                }
            });

            JFrame frame = new JFrame("Vernam Cipher");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            vernamEncryptButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Відображення діалогового вікна для вводу даних про ключ
                   /* String input = JOptionPane.showInputDialog(frame, "Введіть довжину ключа та номер початкового символу (у форматі: довжина, початковий номер)", "Введіть ключ", JOptionPane.PLAIN_MESSAGE);
                    if (input != null) {
                        String[] parts = input.split(",");
                        if (parts.length == 2) {
                            int keyLength = Integer.parseInt(parts[0].trim());
                            int startIndex = Integer.parseInt(parts[1].trim());

                            String key = generateKeyVernam(savedText,startIndex,keyLength);
                            gamaTextArea.setText(key);
                            // Задаємо ключ і виконуємо розшифрування
                            resultTextArea.setText(byteArrayToBinaryString( EncryptVernam(startTextArea.getText().getBytes(), key.getBytes())));
                        } else {
                            JOptionPane.showMessageDialog(frame, "Неправильний формат введених даних", "Помилка", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    */
                    resultTextArea.setText( new String( EncryptVernam(gamaTextArea.getText().getBytes(),startTextArea.getText().toString().getBytes())));
                }
            });

            gammingDecryptButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    resultTextArea.setText( new String(DecryptGamming(startTextArea.getText().getBytes() ,gama) ));
                }
            });

            ganerateGamaButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    gama = GenerateKey(startTextArea.getText().length());
                    gamaTextArea.setText(byteArrayToBinaryString(gama));
                }
            });

            toolbar.add(gammingEncryptButton);
            toolbar.add(gammingDecryptButton);
            toolbar.add(vernamEncryptButton);
            toolbar.add(vernamDecryptButton);
            toolbar.add(ganerateGamaButton);

            //////////////////////////////////////////////

            // Менеджер компонування для центральної панелі
            JPanel centerPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.gridx = 0;
            gbc.gridy = 0;

            /////////////////////////////////////////////

            //alphabets.put("АБВГД...", "АБВГДЕЄЖЗИІЇКЛМНОПРСТУФХЦЧШЩЬЮЯ");
            //alphabets.put("абвгд...", "абвгдеєжзиіїклмнопрстуфхцчшщьюя");
            alphabets.put("ABCD...", "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            alphabets.put("abcd...", "abcdefghijklmnopqrstuvwxyz");

            alphabetComboBox = new JComboBox<>(alphabets.keySet().toArray(new String[0]));

            centerPanel.add(alphabetComboBox, gbc);
            gbc.gridy++;


            JLabel startLabel = new JLabel("Початкова інформація :");
            startTextArea = new JTextArea();
            JScrollPane startScrollPane = new JScrollPane(startTextArea);
            startScrollPane.setPreferredSize(new Dimension(400, 200));
            centerPanel.add(startLabel, gbc);
            gbc.gridy++;
            centerPanel.add(startScrollPane, gbc);
            gbc.gridy++;

            JLabel gamaLabel = new JLabel("Згенерована гама : ");
            gamaTextArea = new JTextArea();
            JScrollPane gamaScrollPane = new JScrollPane(gamaTextArea);
            gamaScrollPane.setPreferredSize(new Dimension(400, 20));
            gbc.gridy++;
            centerPanel.add(gamaLabel, gbc);
            gbc.gridy++;
            centerPanel.add(gamaScrollPane, gbc);

            JLabel resultLabel = new JLabel("Інформація перетворення : ");
            resultTextArea = new JTextArea();
            JScrollPane resultScrollPane = new JScrollPane(resultTextArea);
            resultScrollPane.setPreferredSize(new Dimension(400, 200));
            gbc.gridy++;
            centerPanel.add(resultLabel, gbc);
            gbc.gridy++;
            centerPanel.add(resultScrollPane, gbc);



            // Створення панелі для розміщення результатів валідації
            JPanel validationPanel = new JPanel(new GridLayout(5, 1)); // Використовуємо GridLayout для розміщення компонентів один за одним


            // Label для назви файлу
            fileNameLabel = new JLabel();
            validationPanel.add(fileNameLabel);


            // Додаємо панель з результатами валідації на південну частину головного вікна
            mainWindow.add(validationPanel, BorderLayout.SOUTH);

            mainWindow.add(toolbar, BorderLayout.NORTH);
            mainWindow.add(centerPanel, BorderLayout.CENTER);

            mainWindow.setVisible(true);
        });
    }
////////////////////////////////////////////////////

    public static byte[] GenerateKey(int length) {
        byte[] key = new byte[length];
        new SecureRandom().nextBytes(key); // Генерація випадкових байтів
        return key;
    }

    public static byte[] EncryptGamming(byte[] plaintext, byte[] key) {
        byte[] ciphertext = new byte[plaintext.length];
        byte[] alphabetBytes = ((String) alphabets.get(alphabetComboBox.getSelectedItem())).getBytes();
        int alphabetLength = alphabets.get(alphabetComboBox.getSelectedItem()).length();
        System.out.println(plaintext);
        // Шифрування
        for (int i = 0; i < plaintext.length; i++) {
            byte currentChar = plaintext[i];

            // Перевірка, чи символ належить алфавіту
            boolean isLetter = false;
            for (byte letter : alphabetBytes) {
                if (currentChar == letter) {
                    isLetter = true;
                    break;
                }
            }

            if (isLetter) {
                byte base = alphabetBytes[0];
                int index = (currentChar - base + key[i % key.length]) % alphabetLength;
                if (index < 0) index += alphabetLength;
                ciphertext[i] = alphabetBytes[index];
            } else {
                ciphertext[i] = currentChar; // Залишаємо символ незмінним, якщо він не належить алфавіту
            }
        }
        System.out.println(ciphertext);
        System.out.println(plaintext);

      /*  // Дешифрування (за допомогою того самого ключа)
        for (int i = 0; i < ciphertext.length; i++) {
            byte currentChar = ciphertext[i];

            // Перевірка, чи символ належить алфавіту
            boolean isLetter = false;
            for (byte letter : alphabetBytes) {
                if (currentChar == letter) {
                    isLetter = true;
                    break;
                }
            }

            if (isLetter) {
                byte base = alphabetBytes[0];
                int index = (currentChar - base - key[i % key.length]) % alphabetLength;
                if (index < 0) index += alphabetLength;
                ciphertext[i] = alphabetBytes[index];
            } else {
                ciphertext[i] = currentChar; // Залишаємо символ незмінним, якщо він не належить алфавіту
            }
        }*/

        return ciphertext;
    }
    public static byte[] DecryptGamming(byte[] plaintext, byte[] key) {
        byte[] ciphertext = new byte[plaintext.length];
        byte[] alphabetBytes = ((String) alphabets.get(alphabetComboBox.getSelectedItem())).getBytes();
        int alphabetLength = alphabets.get(alphabetComboBox.getSelectedItem()).length();



        // Дешифрування (за допомогою того самого ключа)
        for (int i = 0; i < plaintext.length; i++) {
            byte currentChar = ciphertext[i];

            // Перевірка, чи символ належить алфавіту
            boolean isLetter = false;
            for (byte letter : alphabetBytes) {
                if (currentChar == letter) {
                    isLetter = true;
                    break;
                }
            }

            if (isLetter) {
                byte base = alphabetBytes[0];
                int index = (currentChar - base - key[i % key.length]) % alphabetLength;
                if (index < 0) index += alphabetLength;
                ciphertext[i] = alphabetBytes[index];
            } else {
                ciphertext[i] = currentChar; // Залишаємо символ незмінним, якщо він не належить алфавіту
            }
        }

        return ciphertext;
    }

    // Метод для генерації ключа з тексту
    private static String generateKeyVernam(String text, int position , int keyLength) {
        if (text.length() < keyLength) {
            throw new IllegalArgumentException("Довжина тексту менша за довжину ключа");
        }
        return text.substring(position, keyLength);
    }

    // Метод для шифрування тексту шифром Вернама
    private static byte[] EncryptVernam(byte[] plaintextBytes, byte[] keyBytes) {
        byte[] ciphertextBytes = new byte[plaintextBytes.length];
        int alphabetLength = 26; // Довжина алфавіту (ключа)

        for (int i = 0; i < plaintextBytes.length; i++) {
            byte currentByte = plaintextBytes[i];
            byte base = keyBytes[0]; // Перший символ алфавіту (ключа)

            // Перевірка, чи байт належить літері
            boolean isLetter = (currentByte >= base && currentByte < base + alphabetLength);

            if (isLetter) {
                int index = (currentByte - base + keyBytes[i % keyBytes.length]) % alphabetLength;
                if (index < 0) index += alphabetLength;
                byte encryptedByte = (byte) (base + index);
                ciphertextBytes[i] = encryptedByte;
            } else {
                ciphertextBytes[i] = currentByte; // Залишаємо байт незмінним, якщо він не належить літері
            }
        }
        return ciphertextBytes;
    }



    // Конвертація байтового масиву у бінарний рядок
    public static String byteArrayToBinaryString(byte[] array) {
        StringBuilder binaryString = new StringBuilder();
        for (byte b : array) {
            binaryString.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }
        return binaryString.toString();
    }
    public static void createFile() {
        // Відкрити діалог для вибору місця та імені нового файлу
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Створити файл");
        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            // Додати розширення .txt, якщо його немає
            if (!filePath.endsWith(".txt")) {
                fileToSave = new File(filePath + ".txt");
            }
            try {
                // Створити новий файл
                if (fileToSave.createNewFile()) {
                    JOptionPane.showMessageDialog(null, "Файл успішно створено: " + fileToSave.getName());
                } else {
                    JOptionPane.showMessageDialog(null, "Не вдалося створити файл.");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void openFile() {
        // Логіка для відкриття файлу
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            currentFile = file;
            String fileName = file.getName();
            if (fileNameLabel != null) {
                fileNameLabel.setText("    Назва файлу  :  " + fileName);
            }
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                StringBuilder content = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                reader.close();
                savedText = content.toString();
                //startTextArea.setText(content.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    public static void saveFile() {
        // Логіка для збереження файлу
        if (currentFile != null) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile));
                writer.write(startTextArea.getText());
                writer.close();
                JOptionPane.showMessageDialog(null, "Файл успішно збережено");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Не обрано файл для збереження");
        }
    }

    public static void printFile() {
        // Логіка для друку файлу
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        if (printerJob.printDialog()) {
            try {
                printerJob.setPrintable(new Printable() {
                    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                        if (pageIndex > 0) {
                            return Printable.NO_SUCH_PAGE;
                        }
                        Graphics2D g2d = (Graphics2D) graphics;
                        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                        resultTextArea.printAll(graphics);
                        return Printable.PAGE_EXISTS;
                    }
                });
                printerJob.print();
            } catch (PrinterException ex) {
                ex.printStackTrace();
            }
        }
    }



}

/*
public class GammingEncryptionTest {
    @Test
    public void testEncryptionAndDecryption() {
        // Приклад алфавіту
        alphabets.put("абвгде", "абвгде");
        alphabets.put("ABCD", "ABCDEFGHIJKLMNOPQRSTUVWXYZ");

        // Початковий текст
        String plaintext = "абвгд";

        // Приклад ключа
        byte[] key = GenerateKey(plaintext);

        // Шифруємо та дешифруємо текст
        byte[] encryptedText = EncryptGamming(plaintext.getBytes(), key);
        byte[] decryptedText = DecryptGamming(encryptedText, key);

        // Перевіряємо, чи відновлений текст співпадає з початковим
        assertArrayEquals(plaintext.getBytes(), decryptedText);
    }

} */