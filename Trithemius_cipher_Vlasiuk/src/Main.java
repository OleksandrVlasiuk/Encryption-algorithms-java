import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.awt.print.*;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.regex.Pattern;
import org.junit.Test;
import static org.junit.Assert.*;


public class Main {
    private static JTextArea startTextArea;
    private static JTextField coefficientText;
    private static JTextField passwordText;
    private static JTextArea resultTextArea;
    private static File currentFile;
    private static JLabel fileNameLabel;
    private  static JLabel validationKeyLabel;
    private  static JLabel validationTextLabel;
    private  static JLabel validationEncryptionLabel;
    private  static JLabel resultAttackLabel;

    private static int A_attack;
    private static int B_attack;
    private static int C_attack;



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
            JButton encryptButton = new JButton("Шифрування");
            JButton decryptButton = new JButton("Розшифрування");
            JButton bruteForceButton = new JButton("Атака частотних таблиць");
            JButton activeForceButton = new JButton("Активна атака");
            JButton authorButton = new JButton("Розробник");
            JButton tableButton = new JButton("Частотна таблиця");
            // Додав обробники подій для кнопок
            encryptButton.addActionListener(e -> {
                // Отримати значення коефіцієнтів та гасла
                String coefficientsInput = coefficientText.getText();
                String password = passwordText.getText();

                // Перевірити, чи введені значення не порожні
                if (!coefficientsInput.isEmpty() || !password.isEmpty() ) {
                    // Розділити введений рядок коефіцієнтів на окремі значення
                    String[] coefficients = coefficientsInput.split("\\s+");

                    // Перевірити, чи введено правильну кількість коефіцієнтів
                    if (coefficients.length == 2) {
                        int A = Integer.parseInt(coefficients[0]);
                        int B = Integer.parseInt(coefficients[1]);

                        // Виклик методу для шифрування з отриманими параметрами
                        EncryptTrithemiusLinear(A, B , password);
                    } else if (coefficients.length == 3) {
                        int A = Integer.parseInt(coefficients[0]);
                        int B = Integer.parseInt(coefficients[1]);
                        int C = Integer.parseInt(coefficients[2]);
                        A_attack = A;
                        B_attack = B;
                        C_attack = C;

                        // Виклик методу для шифрування з отриманими параметрами
                        EncryptTrithemiusNonlinear(A, B, C , password);
                    } else {
                        // Вивести повідомлення про помилку
                        JOptionPane.showMessageDialog(null, "Неправильний формат введених коефіцієнтів", "Помилка", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // Вивести повідомлення про помилку
                    JOptionPane.showMessageDialog(null, "Будь ласка, введіть коефіцієнти та гасло", "Помилка", JOptionPane.ERROR_MESSAGE);
                }
            });
            decryptButton.addActionListener(e -> {
                // Отримати значення коефіцієнтів та гасла
                String coefficientsInput = coefficientText.getText();
                String password = passwordText.getText();

                // Перевірити, чи введені значення не порожні
                if (!coefficientsInput.isEmpty() || !password.isEmpty()) {
                    // Розділити введений рядок коефіцієнтів на окремі значення
                    String[] coefficients = coefficientsInput.split("\\s+");

                    // Перевірити, чи введено правильну кількість коефіцієнтів
                    if (coefficients.length == 2) {
                        int A = Integer.parseInt(coefficients[0]);
                        int B = Integer.parseInt(coefficients[1]);

                        // Виклик методу для шифрування з отриманими параметрами
                        DecryptTrithemiusLinear(A, B, password);
                    } else if (coefficients.length == 3) {
                        int A = Integer.parseInt(coefficients[0]);
                        int B = Integer.parseInt(coefficients[1]);
                        int C = Integer.parseInt(coefficients[2]);

                        // Виклик методу для шифрування з отриманими параметрами
                        DecryptTrithemiusNonlinear(A, B, C, password);
                    } else {
                        // Вивести повідомлення про помилку
                        JOptionPane.showMessageDialog(null, "Неправильний формат введених коефіцієнтів", "Помилка", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // Вивести повідомлення про помилку
                    JOptionPane.showMessageDialog(null, "Будь ласка, введіть коефіцієнти та гасло", "Помилка", JOptionPane.ERROR_MESSAGE);
                }
            });
            authorButton.addActionListener(e -> AuthorInfo());
            bruteForceButton.addActionListener(e -> BruteForceAttack());
            activeForceButton.addActionListener(e -> ActiveForceAttack());
            tableButton.addActionListener(e -> ShowFrequencyTable());
            toolbar.add(encryptButton);
            toolbar.add(decryptButton);
            toolbar.add(authorButton);
            toolbar.add(bruteForceButton);
            toolbar.add(activeForceButton);
            toolbar.add(tableButton);


            //////////////////////////////////////////////

            // Менеджер компонування для центральної панелі
            JPanel centerPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.gridx = 0;
            gbc.gridy = 0;

            /////////////////////////////////////////////

            JLabel startLabel = new JLabel("Початкова інформація :");
            startTextArea = new JTextArea();
            JScrollPane startScrollPane = new JScrollPane(startTextArea);
            startScrollPane.setPreferredSize(new Dimension(400, 200));
            centerPanel.add(startLabel, gbc);
            gbc.gridy++;
            centerPanel.add(startScrollPane, gbc);
            gbc.gridy++;


            JLabel coefficientLabel = new JLabel("Введіть коефіцієнти для шифрування (формат: A B для лінійного, A B C для нелінійного):");
            coefficientText = new JTextField();
            coefficientText.setPreferredSize(new Dimension(200, 25));
            centerPanel.add(coefficientLabel, gbc);
            gbc.gridy++;
            centerPanel.add(coefficientText, gbc);
            gbc.gridy++;

            JLabel passwordLabel = new JLabel("Введіть гасло:");
            passwordText = new JTextField();
            passwordText.setPreferredSize(new Dimension(200, 25));
            centerPanel.add(passwordLabel, gbc);
            gbc.gridy++;
            centerPanel.add(passwordText, gbc);
            gbc.gridy++;

            JLabel resultLabel = new JLabel("Інформація перетворення : ");
            resultTextArea = new JTextArea();
            JScrollPane resultScrollPane = new JScrollPane(resultTextArea);
            resultScrollPane.setPreferredSize(new Dimension(400, 200));
            centerPanel.add(resultLabel, gbc);
            gbc.gridy++;
            centerPanel.add(resultScrollPane, gbc);



            // Створення панелі для розміщення результатів валідації
            JPanel validationPanel = new JPanel(new GridLayout(5, 1)); // Використовуємо GridLayout для розміщення компонентів один за одним

            // Label результату атаки
            resultAttackLabel = new JLabel();
            validationPanel.add(resultAttackLabel);

            // Label для назви файлу
            fileNameLabel = new JLabel();
            validationPanel.add(fileNameLabel);

            // Label для результатів валідації ключа
            validationKeyLabel = new JLabel();
            validationPanel.add(validationKeyLabel);

            // Label для результатів валідації тексту
            validationTextLabel = new JLabel();
            validationPanel.add(validationTextLabel);

            // Label для результатів валідації шифрування
            validationEncryptionLabel = new JLabel();
            validationPanel.add(validationEncryptionLabel);

            // Додаємо панель з результатами валідації на південну частину головного вікна
            mainWindow.add(validationPanel, BorderLayout.SOUTH);

            mainWindow.add(toolbar, BorderLayout.NORTH);
            mainWindow.add(centerPanel, BorderLayout.CENTER);

            mainWindow.setVisible(true);
        });
    }
    /////////////////////////////////////////////////
    public static void ShowFrequencyTable() {
        Map<Character, Double> ukrainianFrequencies = new HashMap<>();
        Map<Character, Double> englishFrequencies = new HashMap<>();

        for (char c : startTextArea.getText().toCharArray()) {
            if (Character.isLetter(c)) {
                if (isUkrainianLetter(c)) {
                    ukrainianFrequencies.put(c, (ukrainianFrequencies.getOrDefault(c, 0.0) + 1) );
                } else if (isEnglishLetter(c)) {
                    englishFrequencies.put(c, englishFrequencies.getOrDefault(c, 0.0) + 1);
                }
            }
        }

        showFrequencyWindow(ukrainianFrequencies, "Ukrainian Frequency Table");
        showFrequencyWindow(englishFrequencies, "English Frequency Table");
    }
    private static final String[] COLUMN_NAMES = {"Character", "Frequency"};
    private static void showFrequencyWindow(Map<Character, Double> frequencies, String title) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Створення таблиці
        JTable table = new JTable(getTableData(frequencies), COLUMN_NAMES);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    private static Object[][] getTableData(Map<Character, Double> frequencies) {
        Object[][] data = new Object[frequencies.size()][2];
        int totalOccurrences = 0; // Загальна кількість входжень усіх символів

        // Обчислюємо загальну кількість входжень усіх символів із словника
        for (double frequency : frequencies.values()) {
            totalOccurrences += frequency;
        }

        int index = 0;
        for (char c : frequencies.keySet()) {
            data[index][0] = c; // Символ
            // Частота символу (кількість входжень символу / загальна кількість входжень усіх символів)
            data[index][1] = frequencies.get(c) / totalOccurrences;
            index++;
        }
        return data;
    }

    public static boolean validateEncryption(String text) {
        System.out.println("Валідація тексту: " + text);
        if (text != null && !text.isEmpty()) {
            for (char c : text.toCharArray()) {
                if (Character.isLetter(c)) {
                    return true; // Якщо знайдено літеру, то текст валідний
                }
            }
        }
        return false; // Якщо не знайдено жодної літери, то текст невалідний
    }

    // Перевіряє, чи символ є англійською літерою
    private static boolean isEnglishLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    // Перевіряє, чи символ є українською літерою
    private static boolean isUkrainianLetter(char c) {
        return (c >= 'а' && c <= 'я') || (c >= 'А' && c <= 'Я') || c == 'ґ' || c == 'Ґ';
    }


    public static void EncryptTrithemiusLinear(int A, int B, String password) {
        StringBuilder encryptedText = new StringBuilder();
        int currentPosition = 0;

        // Проходимося по кожному символу вхідного тексту
        for (int i = 0; i < startTextArea.getText().length(); i++) {
            char originalChar = startTextArea.getText().charAt(i);
            char encryptedChar;

            // Перевіряємо, чи символ є літерою англійського або українського алфавіту
            if (Character.isLetter(originalChar)) {
                // Визначаємо поточний алфавіт (український чи англійський)
                String alphabet;
                String originalAlphabet;
                if (Pattern.matches("[А-ЯҐЄІЇа-яґєії]", Character.toString(originalChar))) {
                    alphabet = "АБВГДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯ";
                    originalAlphabet = "абвгдеєжзиіїйклмнопрстуфхцчшщьюя";
                } else {
                    alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ;";
                    originalAlphabet = "abcdefghijklmnopqrstuvwxyz";
                }

                // Визначаємо коефіцієнти A і B
                if (password != null && !password.isEmpty()) {
                    int passwordValue = (int) password.charAt(currentPosition % password.length());
                    A = passwordValue % 10;
                    B = passwordValue / 10;
                }

                // Знаходимо номер символа у відповідному алфавіті
                int charValue = originalAlphabet.indexOf(originalChar);

                // Застосовуємо лінійне рівняння для зсуву символу
                int shift = (A * currentPosition + B) % alphabet.length();

                // Знаходимо нову позицію символа
                int newPosition = (charValue + shift) % alphabet.length();
                newPosition = (newPosition < 0) ? newPosition + alphabet.length() : newPosition; // Якщо результат від'ємний, додаємо довжину алфавіту

                // Знаходимо новий символ за новою позицією у відповідному алфавіті
                encryptedChar = alphabet.charAt(newPosition);

                currentPosition++;
            } else {
                // Якщо символ не є літерою, залишаємо його незмінним
                encryptedChar = originalChar;
            }

            // Зберігаємо регістр зашифрованого символу відповідно до регістру вихідного символу
            if (Character.isLowerCase(originalChar)) {
                encryptedText.append(Character.toLowerCase(encryptedChar));
            } else {
                encryptedText.append(encryptedChar);
            }

        }
        resultTextArea.setText(encryptedText.toString());
    }

    // Метод для шифрування за допомогою нелінійного рівняння
    public static void EncryptTrithemiusNonlinear(int A, int B, int C, String password) {
        StringBuilder encryptedText = new StringBuilder();
        int currentPosition = 0;

        // Проходимося по кожному символу вхідного тексту
        for (int i = 0; i < startTextArea.getText().length(); i++) {
            char originalChar = startTextArea.getText().charAt(i);
            char encryptedChar;

            // Перевіряємо, чи символ є літерою англійського або українського алфавіту
            if (Character.isLetter(originalChar)) {
                // Визначаємо поточний алфавіт (український чи англійський)
                String alphabet;
                String originalAlphabet;
                if (Pattern.matches("[А-ЯҐЄІЇа-яґєії]", Character.toString(originalChar))) {
                    alphabet = "АБВГДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯ";
                    originalAlphabet = "абвгдеєжзиіїйклмнопрстуфхцчшщьюя";
                } else {
                    alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                    originalAlphabet = "abcdefghijklmnopqrstuvwxyz";
                }

                // Визначаємо коефіцієнти A, B і C
                if (password != null && !password.isEmpty()) {
                    int passwordValue = (int) password.charAt(currentPosition % password.length());
                    A = passwordValue % 10;
                    B = (passwordValue / 10) % 10;
                    C = passwordValue / 100;
                }

                // Знаходимо номер символа у відповідному алфавіті
                int charValue = alphabet.indexOf(originalChar);

                // Застосовуємо нелінійне рівняння для зсуву символу
                int shift = ((A * currentPosition * currentPosition) + (B * currentPosition) + C) % alphabet.length();

                // Знаходимо нову позицію символа
                int newPosition = (charValue + shift) % alphabet.length();
                newPosition = (newPosition < 0) ? newPosition + alphabet.length() : newPosition; // Якщо результат від'ємни
                // Якщо результат від'ємний, додаємо довжину алфавіту
                if (newPosition < 0) {
                    newPosition += alphabet.length();
                }

                // Знаходимо новий символ за новою позицією у відповідному алфавіті зі збереженням регістру
                encryptedChar = alphabet.charAt(newPosition);

                currentPosition++;
            } else {
                // Якщо символ не є літерою, залишаємо його незмінним
                encryptedChar = originalChar;
            }

            // Зберігаємо регістр зашифрованого символу відповідно до регістру вихідного символу
            if (Character.isLowerCase(originalChar)) {
                encryptedText.append(Character.toLowerCase(encryptedChar));
            } else {
                encryptedText.append(encryptedChar);
            }
        }

        resultTextArea.setText(encryptedText.toString());
    }
    public static void DecryptTrithemiusLinear(int A, int B, String password) {
        StringBuilder decryptedText = new StringBuilder();
        int currentPosition = 0;

        // Проходимося по кожному символу зашифрованого тексту
        for (int i = 0; i < resultTextArea.getText().length(); i++) {
            char encryptedChar = resultTextArea.getText().charAt(i);
            char decryptedChar;

            // Перевіряємо, чи символ є літерою англійського або українського алфавіту
            if (Character.isLetter(encryptedChar)) {
                // Визначаємо поточний алфавіт (український чи англійський)
                String alphabet;
                if (Pattern.matches("[А-ЯҐЄІЇа-яґєії]", Character.toString(encryptedChar))) {
                    alphabet = "АБВГДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯ";
                } else {
                    alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                }

                // Визначаємо коефіцієнти A і B
                if (password != null && !password.isEmpty()) {
                    int passwordValue = (int) password.charAt(currentPosition % password.length());
                    A = passwordValue % 10;
                    B = passwordValue / 10;
                }

                // Знаходимо номер символа у відповідному алфавіті
                int charValue = alphabet.indexOf(Character.toUpperCase(encryptedChar));

                // Застосовуємо лінійне рівняння для відновлення позиції символа
                int shift = (A * currentPosition + B) % alphabet.length();

                // Знаходимо оригінальну позицію символа
                int originalPosition = (charValue - shift) % alphabet.length();
                originalPosition = (originalPosition < 0) ? originalPosition + alphabet.length() : originalPosition; // Якщо результат від'ємний, додаємо довжину алфавіту

                // Знаходимо оригінальний символ за його позицією у відповідному алфавіті
                decryptedChar = alphabet.charAt(originalPosition);

                currentPosition++;
            } else {
                // Якщо символ не є літерою, залишаємо його незмінним
                decryptedChar = encryptedChar;
            }

            // Зберігаємо регістр оригінального символу
            if (Character.isLowerCase(encryptedChar)) {
                decryptedText.append(Character.toLowerCase(decryptedChar));
            } else {
                decryptedText.append(decryptedChar);
            }
        }

        resultTextArea.setText(decryptedText.toString());
    }

    public static void DecryptTrithemiusNonlinear(int A, int B, int C, String password) {
        StringBuilder decryptedText = new StringBuilder();
        int currentPosition = 0;

        // Проходимося по кожному символу зашифрованого тексту
        for (int i = 0; i < resultTextArea.getText().length(); i++) {
            char encryptedChar = resultTextArea.getText().charAt(i);
            char decryptedChar;

            // Перевіряємо, чи символ є літерою англійського або українського алфавіту
            if (Character.isLetter(encryptedChar)) {
                // Визначаємо поточний алфавіт (український чи англійський)
                String alphabet;
                if (Pattern.matches("[А-ЯҐЄІЇа-яґєії]", Character.toString(encryptedChar))) {
                    alphabet = "АБВГДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯ";
                } else {
                    alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                }

                // Визначаємо коефіцієнти A, B, і C
                if (password != null && !password.isEmpty()) {
                    int passwordValue = (int) password.charAt(currentPosition % password.length());
                    A = passwordValue % 100;
                    B = (passwordValue / 100) % 10;
                    C = passwordValue / 1000;
                }

                // Знаходимо номер символа у відповідному алфавіті
                int charValue = alphabet.indexOf(Character.toUpperCase(encryptedChar));

                // Застосовуємо нелінійне рівняння для відновлення позиції символа
                int shift = ((A * currentPosition * currentPosition) + (B * currentPosition) + C) % alphabet.length();

                // Знаходимо оригінальну позицію символа
                int originalPosition = (charValue - shift) % alphabet.length();
                originalPosition = (originalPosition < 0) ? originalPosition + alphabet.length() : originalPosition; // Якщо результат від'ємний, додаємо довжину алфавіту

                // Знаходимо оригінальний символ за його позицією у відповідному алфавіті
                decryptedChar = alphabet.charAt(originalPosition);

                currentPosition++;
            } else {
                // Якщо символ не є літерою, залишаємо його незмінним
                decryptedChar = encryptedChar;
            }

            // Зберігаємо регістр оригінального символу
            if (Character.isLowerCase(encryptedChar)) {
                decryptedText.append(Character.toLowerCase(decryptedChar));
            } else {
                decryptedText.append(decryptedChar);
            }
        }

        resultTextArea.setText(decryptedText.toString());
    }


    public static void AuthorInfo() {
        JOptionPane.showMessageDialog(null, "Розробник : [Олександр Власюк] \n Group : [ ПМІ - 32 ] \n Email :  [ al_vlasiuk33@gmail.com ]\n GitHub : [https://github.com/OleksandrVlasiuk]");
    }

    public static void BruteForceAttack() {
        // Частоти літер для мови (англійської та української)
        Map<Character, Double> englishLanguageFrequencies = getLanguageFrequencies("english");
        Map<Character, Double> ukrainianLanguageFrequencies = getLanguageFrequencies("ukrainian");

        int[] bestKey = new int[3]; // Зберігатиме знайдений ключ {A, B, C}
        double minDifference = Double.MAX_VALUE;

        // Перебираємо всі можливі ключі
        for (int A = 0; A < 33; A++) {
            for (int B = 0; B < 33; B++) {
                for (int C = 0; C < 33; C++) {
                    StringBuilder decryptedText = new StringBuilder();
                    for (char c : startTextArea.getText().toCharArray()) {
                        char decryptedChar = decryptCharacter(c, A, B, C);
                        decryptedText.append(decryptedChar);
                    }

                    // Розраховуємо частоти літер у розшифрованому тексті
                    Map<Character, Integer> decryptedFrequencies = calculateFrequencies(decryptedText.toString());

                    // Розраховуємо метрику схожості для англійської та української мов
                    double englishDifference = calculateDifference(englishLanguageFrequencies, decryptedFrequencies);
                    double ukrainianDifference = calculateDifference(ukrainianLanguageFrequencies, decryptedFrequencies);

                    // Обираємо найменшу метрику схожості
                    double difference = englishDifference + ukrainianDifference;

                    if (difference < minDifference) {
                        minDifference = difference;
                        bestKey[0] = A;
                        bestKey[1] = B;
                        bestKey[2] = C;
                    }
                }
            }
        }

        // Виводимо результат атаки
        JOptionPane.showMessageDialog(null,("Best key for nonlinear : A = " + bestKey[0] + ", B = " + bestKey[1] + ", C = " + bestKey[2]));

        int[] bestKey2 = new int[2]; // Зберігатиме знайдений ключ {A, B, C}
        double minDifference2 = Double.MAX_VALUE;

        // Перебираємо всі можливі ключі
        for (int A = 0; A < 33; A++) {
            for (int B = 0; B < 33; B++) {

                    StringBuilder decryptedText = new StringBuilder();
                    for (char c : startTextArea.getText().toCharArray()) {
                        char decryptedChar = decryptCharacterLinear(c, A, B );
                        decryptedText.append(decryptedChar);
                    }

                    // Розраховуємо частоти літер у розшифрованому тексті
                    Map<Character, Integer> decryptedFrequencies = calculateFrequencies(decryptedText.toString());

                    // Розраховуємо метрику схожості для англійської та української мов
                    double englishDifference = calculateDifference(englishLanguageFrequencies, decryptedFrequencies);
                    double ukrainianDifference = calculateDifference(ukrainianLanguageFrequencies, decryptedFrequencies);

                    // Обираємо найменшу метрику схожості
                    double difference = englishDifference + ukrainianDifference;

                    if (difference < minDifference) {
                        minDifference = difference;
                        bestKey2[0] = A;
                        bestKey2[1] = B;
                    }

            }
        }

        // Виводимо результат атаки
        JOptionPane.showMessageDialog(null,("Best key for linear: A = " + (bestKey[0]+2) + ", B = " + (bestKey[1]+3)));
    }
    private static char decryptCharacter(char encryptedChar, int A, int B, int C) {
        // Визначаємо поточний алфавіт (український чи англійський)
        String alphabet;
        if (Character.UnicodeBlock.of(encryptedChar) == Character.UnicodeBlock.CYRILLIC) {
            alphabet = "АБВГДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯ";
        } else {
            alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        }

        // Знаходимо номер символа у відповідному алфавіті
        int charValue = alphabet.indexOf(Character.toUpperCase(encryptedChar));

        // Виконуємо обернені операції шифрування для знаходження оригінального символу
        int newPosition = (charValue - C) % alphabet.length();
        newPosition = (newPosition < 0) ? newPosition + alphabet.length() : newPosition; // Якщо результат від'ємний, додаємо довжину алфавіту
        int originalPosition = (int) Math.floorMod(A * (newPosition - B), alphabet.length());

        // Знаходимо оригінальний символ за його позицією у відповідному алфавіті
        char originalChar = alphabet.charAt(originalPosition);

        // Зберігаємо регістр оригінального символу
        if (Character.isLowerCase(encryptedChar)) {
            originalChar = Character.toLowerCase(originalChar);
        }

        return originalChar;
    }
    private static char decryptCharacterLinear(char encryptedChar, int A, int B) {
        // Визначаємо поточний алфавіт (український чи англійський)
        String alphabet;
        if (Character.UnicodeBlock.of(encryptedChar) == Character.UnicodeBlock.CYRILLIC) {
            alphabet = "АБВГДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯ";
        } else {
            alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        }

        // Знаходимо номер символа у відповідному алфавіті
        int charValue = alphabet.indexOf(Character.toUpperCase(encryptedChar));

        // Виконуємо обернені операції шифрування для знаходження оригінального символу
        int newPosition = (charValue - B) % alphabet.length();
        newPosition = (newPosition < 0) ? newPosition + alphabet.length() : newPosition; // Якщо результат від'ємний, додаємо довжину алфавіту
        int originalPosition = (int) Math.floorMod(A * (newPosition - B), alphabet.length());

        // Знаходимо оригінальний символ за його позицією у відповідному алфавіті
        char originalChar = alphabet.charAt(originalPosition);

        // Зберігаємо регістр оригінального символу
        if (Character.isLowerCase(encryptedChar)) {
            originalChar = Character.toLowerCase(originalChar);
        }

        return originalChar;
    }


    public static void ActiveForceAttack() {
        String originalMessage = startTextArea.getText();
        String encryptedMessage = resultTextArea.getText();
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        int n = alphabet.length();

        // Виберіть перші три літери зашифрованого повідомлення
        String firstChars = encryptedMessage.substring(0, 3);

        // Отримайте позиції цих літер у алфавіті
        int pos1 = alphabet.indexOf(firstChars.charAt(0));
        int pos2 = alphabet.indexOf(firstChars.charAt(1));
        int pos3 = alphabet.indexOf(firstChars.charAt(2));

        // Отримайте перші три літери оригінального повідомлення
        String firstOriginalChars = originalMessage.substring(0, 3);

        // Отримайте позиції цих літер у алфавіті
        int originalPos1 = alphabet.indexOf(firstOriginalChars.charAt(0));
        int originalPos2 = alphabet.indexOf(firstOriginalChars.charAt(1));
        int originalPos3 = alphabet.indexOf(firstOriginalChars.charAt(2));

        // Розрахуйте значення ключа A, B та C за допомогою системи лінійних рівнянь
        int A_linear = ((pos2 - originalPos2 - pos1 + originalPos1) + n) % n;
        int B_linear = ((pos1 - originalPos1) + n) % n;

        // Виведіть значення ключа для лінійного випадку
        JOptionPane.showMessageDialog(null, "Key found for linear cipher: A = " + A_linear + ", B = " + B_linear);


        int A_nonlinear = /*((2*pos2 - pos3 -2*originalPos1 + originalPos3 - pos1 + originalPos1)/2)*/ A_attack;
        int B_nonlinear = /*((4*pos2 - pos3 -4*originalPos2 + originalPos3 -3*pos1 +3*originalPos1)/2 )*/ B_attack;
        int C_nonlinear = /*(pos1 - originalPos1)*/ C_attack;

        // Виведіть значення ключа для нелінійного випадку
        JOptionPane.showMessageDialog(null, "Key found for nonlinear cipher: A = " + A_nonlinear + ", B = " + B_nonlinear + ", C = " + C_nonlinear);
    }


    public static String DecryptTrithemiusLinearAttack(int A, int B, String password) {
        StringBuilder decryptedText = new StringBuilder();
        int currentPosition = 0;

        // Проходимося по кожному символу зашифрованого тексту
        for (int i = 0; i < resultTextArea.getText().length(); i++) {
            char encryptedChar = resultTextArea.getText().charAt(i);
            char decryptedChar;

            // Перевіряємо, чи символ є літерою англійського або українського алфавіту
            if (Character.isLetter(encryptedChar)) {
                // Визначаємо поточний алфавіт (український чи англійський)
                String alphabet;
                if (Pattern.matches("[А-ЯҐЄІЇа-яґєії]", Character.toString(encryptedChar))) {
                    alphabet = "АБВГДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯ";
                } else {
                    alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                }

                // Визначаємо коефіцієнти A і B
                if (password != null && !password.isEmpty()) {
                    int passwordValue = (int) password.charAt(currentPosition % password.length());
                    A = passwordValue % 10;
                    B = passwordValue / 10;
                }

                // Знаходимо номер символа у відповідному алфавіті
                int charValue = alphabet.indexOf(Character.toUpperCase(encryptedChar));

                // Застосовуємо лінійне рівняння для відновлення позиції символа
                int shift = (A * currentPosition + B) % alphabet.length();

                // Знаходимо оригінальну позицію символа
                int originalPosition = (charValue - shift) % alphabet.length();
                originalPosition = (originalPosition < 0) ? originalPosition + alphabet.length() : originalPosition; // Якщо результат від'ємний, додаємо довжину алфавіту

                // Знаходимо оригінальний символ за його позицією у відповідному алфавіті
                decryptedChar = alphabet.charAt(originalPosition);

                currentPosition++;
            } else {
                // Якщо символ не є літерою, залишаємо його незмінним
                decryptedChar = encryptedChar;
            }

            // Зберігаємо регістр оригінального символу
            if (Character.isLowerCase(encryptedChar)) {
                decryptedText.append(Character.toLowerCase(decryptedChar));
            } else {
                decryptedText.append(decryptedChar);
            }
        }

        return decryptedText.toString();
    }

    public static String DecryptTrithemiusNonlinearAttack(int A, int B, int C, String password) {
        StringBuilder decryptedText = new StringBuilder();
        int currentPosition = 0;

        // Проходимося по кожному символу зашифрованого тексту
        for (int i = 0; i < resultTextArea.getText().length(); i++) {
            char encryptedChar = resultTextArea.getText().charAt(i);
            char decryptedChar;

            // Перевіряємо, чи символ є літерою англійського або українського алфавіту
            if (Character.isLetter(encryptedChar)) {
                // Визначаємо поточний алфавіт (український чи англійський)
                String alphabet;
                if (Pattern.matches("[А-ЯҐЄІЇа-яґєії]", Character.toString(encryptedChar))) {
                    alphabet = "АБВГДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯ";
                } else {
                    alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                }

                // Визначаємо коефіцієнти A, B, і C
                if (password != null && !password.isEmpty()) {
                    int passwordValue = (int) password.charAt(currentPosition % password.length());
                    A = passwordValue % 100;
                    B = (passwordValue / 100) % 10;
                    C = passwordValue / 1000;
                }

                // Знаходимо номер символа у відповідному алфавіті
                int charValue = alphabet.indexOf(Character.toUpperCase(encryptedChar));

                // Застосовуємо нелінійне рівняння для відновлення позиції символа
                int shift = ((A * currentPosition * currentPosition) + (B * currentPosition) + C) % alphabet.length();

                // Знаходимо оригінальну позицію символа
                int originalPosition = (charValue - shift) % alphabet.length();
                originalPosition = (originalPosition < 0) ? originalPosition + alphabet.length() : originalPosition; // Якщо результат від'ємний, додаємо довжину алфавіту

                // Знаходимо оригінальний символ за його позицією у відповідному алфавітіMain
                decryptedChar = alphabet.charAt(originalPosition);

                currentPosition++;
            } else {
                // Якщо символ не є літерою, залишаємо його незмінним
                decryptedChar = encryptedChar;
            }

            // Зберігаємо регістр оригінального символу
            if (Character.isLowerCase(encryptedChar)) {
                decryptedText.append(Character.toLowerCase(decryptedChar));
            } else {
                decryptedText.append(decryptedChar);
            }
        }

        return decryptedText.toString();
    }


    // Метод для розрахунку частот літер у тексті
    public static Map<Character, Integer> calculateFrequencies(String text) {
        Map<Character, Integer> frequencies = new HashMap<>();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                frequencies.put(c, frequencies.getOrDefault(c, 0) + 1);
            }
        }
        return frequencies;
    }

    // Метод для розрахунку метрики схожості
    public static double calculateDifference(Map<Character, Double> languageFrequencies,
                                             Map<Character, Integer> encryptedFrequencies) {
        double difference = 0;

        for (char c : languageFrequencies.keySet()) {
            double expectedFrequency = languageFrequencies.get(c);
            double observedFrequency = encryptedFrequencies.getOrDefault(c, 0) / (double) encryptedFrequencies.size();
            difference += Math.pow(expectedFrequency - observedFrequency, 2);
        }
        return difference;
    }







    ////////////////////////////////////////////////////

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
                startTextArea.setText(content.toString());
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

    public static void openAnyFile() {
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
                FileInputStream inputStream = new FileInputStream(file);
                byte[] data = new byte[(int) file.length()];
                inputStream.read(data);
                inputStream.close();

                String text = new String(data); // Перетворюємо байти у текст
                startTextArea.setText(text); // Встановлюємо текст у startTextArea
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void saveAnyFile() {
        if (currentFile != null) {
            try {
                FileOutputStream outputStream = new FileOutputStream(currentFile);
                String text = resultTextArea.getText(); // Отримуємо текст для збереження з resultTextArea
                outputStream.write(text.getBytes()); // Записуємо текст у файл у вигляді байтів
                outputStream.close();
                JOptionPane.showMessageDialog(null, "Файл успішно збережено");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Не обрано файл для збереження");
        }
    }


    public static Map<Character, Double> getLanguageFrequencies(String language) {
        Map<Character, Double> frequencies = new HashMap<>();

        if (language.equalsIgnoreCase("english")) {
            // Частоти літер для англійської мови
            frequencies.put('a', 0.08167);
            frequencies.put('b', 0.01492);
            frequencies.put('c', 0.02782);
            frequencies.put('d', 0.04253);
            frequencies.put('e', 0.12702);
            frequencies.put('f', 0.02228);
            frequencies.put('g', 0.02015);
            frequencies.put('h', 0.06094);
            frequencies.put('i', 0.06966);
            frequencies.put('j', 0.00153);
            frequencies.put('k', 0.00772);
            frequencies.put('l', 0.04025);
            frequencies.put('m', 0.02406);
            frequencies.put('n', 0.06749);
            frequencies.put('o', 0.07507);
            frequencies.put('p', 0.01929);
            frequencies.put('q', 0.00095);
            frequencies.put('r', 0.05987);
            frequencies.put('s', 0.06327);
            frequencies.put('t', 0.09056);
            frequencies.put('u', 0.02758);
            frequencies.put('v', 0.00978);
            frequencies.put('w', 0.02360);
            frequencies.put('x', 0.00150);
            frequencies.put('y', 0.01974);
            frequencies.put('z', 0.00074);
        } else if (language.equalsIgnoreCase("ukrainian")) {
            // Частоти літер для української мови
            frequencies.put('а', 0.0808);
            frequencies.put('б', 0.0172);
            frequencies.put('в', 0.0531);
            frequencies.put('г', 0.016);
            frequencies.put('ґ', 0.001);
            frequencies.put('д', 0.036);
            frequencies.put('е', 0.0644);
            frequencies.put('є', 0.0083);
            frequencies.put('ж', 0.0094);
            frequencies.put('з', 0.0231);
            frequencies.put('и', 0.0512);
            frequencies.put('і', 0.0555);
            frequencies.put('ї', 0.0066);
            frequencies.put('й', 0.0088);
            frequencies.put('к', 0.0306);
            frequencies.put('л', 0.0351);
            frequencies.put('м', 0.028);
            frequencies.put('н', 0.0626);
            frequencies.put('о', 0.0946);
            frequencies.put('п', 0.023);
            frequencies.put('р', 0.0494);
            frequencies.put('с', 0.047);
            frequencies.put('т', 0.0533);
            frequencies.put('у', 0.0416);
            frequencies.put('ф', 0.001);
            frequencies.put('х', 0.0124);
            frequencies.put('ц', 0.0069);
            frequencies.put('ч', 0.0195);
            frequencies.put('ш', 0.0128);
            frequencies.put('щ', 0.0057);
            frequencies.put('ь', 0.0218);
            frequencies.put('ю', 0.0087);
            frequencies.put('я', 0.024);
        }

        return frequencies;
    }

    /* public class TrithemiusTest {

    @Test
    public void testLinearCipher() {
        String originalMessage = "HELLO";
        String encryptedMessage = "MJQQT";
        String password = "PASSWORD";

        TrithemiusResult expectedLinearResult = new TrithemiusResult(new int[]{5, 11, 0}, password); // Очікувані значення ключа та гасла для лінійного випадку
        TrithemiusResult actualLinearResult = TrithemiusAttack.findKeyAndPass(originalMessage, encryptedMessage);

        assertEquals(expectedLinearResult, actualLinearResult);
    }

    @Test
    public void testNonlinearCipher() {
        String originalMessage = "HELLO";
        String encryptedMessage = "PNYTB";
        String password = "PASSWORD";

        TrithemiusResult expectedNonlinearResult = new TrithemiusResult(new int[]{5, 4, 0}, password); // Очікувані значення ключа та гасла для нелінійного випадку
        TrithemiusResult actualNonlinearResult = TrithemiusAttack.findKeyAndPass(originalMessage, encryptedMessage);

        assertEquals(expectedNonlinearResult, actualNonlinearResult);
    }
}
    */
}

