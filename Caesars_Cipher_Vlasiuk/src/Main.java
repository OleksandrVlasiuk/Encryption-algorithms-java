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


public class Main {
    private static JTextArea startTextArea;
    private static JTextField shiftText;
    private static JTextArea resultTextArea;
    private static File currentFile;
    private static JLabel fileNameLabel;
    private  static JLabel validationKeyLabel;
    private  static JLabel validationTextLabel;
    private  static JLabel validationEncryptionLabel;
    private  static JLabel resultAttackLabel;


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
            JMenuItem openAnyItem = new JMenuItem("Відкрити (любий формат)");
            JMenuItem saveAnyItem = new JMenuItem("Зберегти (любий формат)");
            JMenuItem printItem = new JMenuItem("Друкувати .txt");


            // Додав обробники подій для пунктів меню
            createItem.addActionListener(e -> createFile());
            openItem.addActionListener(e -> openFile());
            saveItem.addActionListener(e -> saveFile());
            printItem.addActionListener(e -> printFile());
            openAnyItem.addActionListener(e -> openAnyFile());
            saveAnyItem.addActionListener(e -> saveAnyFile());
            fileMenu.add(createItem);
            fileMenu.add(openItem);
            fileMenu.add(saveItem);
            fileMenu.add(printItem);
            fileMenu.add(openAnyItem);
            fileMenu.add(saveAnyItem);
            menuBar.add(fileMenu);
            mainWindow.setJMenuBar(menuBar);

            //////////////////////////////////////////////

            //Панель інструментів
            JToolBar toolbar = new JToolBar();
            // Додав кнопки для інструментів
            JButton encryptButton = new JButton("Шифрування");
            JButton decryptButton = new JButton("Розшифрування");
            JButton bruteForceButton = new JButton("Атака грубої сили");
            JButton authorButton = new JButton("Розробник");
            JButton tableButton = new JButton("Частотна таблиця");
            JButton encryptAnyButton = new JButton("Шифрування (розширене)");
            JButton decryptAnyButton = new JButton("Розшифрування (розширене)");
            // Додав обробники подій для кнопок
            encryptButton.addActionListener(e -> Encrypt(shiftText.getText(),true));
            decryptButton.addActionListener(e -> Decrypt(shiftText.getText()));
            authorButton.addActionListener(e -> AuthorInfo());
            bruteForceButton.addActionListener(e -> BruteForceAttack());
            tableButton.addActionListener(e -> ShowFrequencyTable());
            encryptAnyButton.addActionListener(e -> EncryptAny(shiftText.getText()));
            decryptAnyButton.addActionListener(e -> DecryptAny(shiftText.getText()));
            toolbar.add(encryptButton);
            toolbar.add(decryptButton);
            toolbar.add(authorButton);
            toolbar.add(bruteForceButton);
            toolbar.add(tableButton);
            toolbar.add(encryptAnyButton);
            toolbar.add(decryptAnyButton);

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


            JLabel shiftLabel = new JLabel("Введіть зсув (1-33 для укр, 1-26 для англ) :");
            shiftText = new JTextField();
            shiftText.setPreferredSize(new Dimension(200, 25));
            centerPanel.add(shiftLabel, gbc);
            gbc.gridy++;
            centerPanel.add(shiftText, gbc);
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
    public static void Encrypt(String shiftStr , boolean state) {
        int shift;
        try {
            shift = Integer.parseInt(shiftStr); // Конвертуємо текст з поля величини зсуву у ціле число
            if (state) {
                if (validateShift(shift)) {
                    validationKeyLabel.setText("    Валідація ключа пройшла успішно.\n");
                } else {
                    validationKeyLabel.setText("    Помилка валідації ключа. Ключ має бути від 1 до 33.\n");
                }
            }
            else{
                if (validateShift(-shift)) {
                    validationKeyLabel.setText("    Валідація ключа пройшла успішно.\n");
                } else {
                    validationKeyLabel.setText("    Помилка валідації ключа. Ключ має бути від 1 до 33.\n");
                }
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Введіть число зсуву");
            return;
        }
        StringBuilder result = new StringBuilder();
        String inputText = startTextArea.getText(); // Отримуємо вхідний текст з текстового поля

        // Перевірка валідації шифрування

        if (validateEncryption(inputText)) {
            validationTextLabel.setText("    Валідація вхідного тексту пройшла успішно.\n");
        } else {
            validationTextLabel.setText("    Помилка валідації вхідного тексту. Текст не може бути порожнім і має містити хоча б одну літеру або символ.\n");
        }

        for (char c : inputText.toCharArray()) {
            if (isEnglishLetter(c)) {
                if (Character.isUpperCase(c)) {
                    int newChar = 'A' + ((c - 'A' + shift) % 26 + 26) % 26; // Додаємо 26 перед %, щоб зробити зсув положним перед обчисленням остачі
                    result.append((char) newChar);
                } else {
                    int newChar = 'a' + ((c - 'a' + shift) % 26 + 26) % 26;
                    result.append((char) newChar);
                }
            } else if (isUkrainianLetter(c)) {
                int cyrillicShift = shift % 33;
                int newChar = c + cyrillicShift;
                if (c >= 'а' && c <= 'я') { // Перевірка чи символ належить українському алфавіту
                    if (newChar > 'я') {
                        newChar = 'а' + (newChar - 'я' - 1);
                    } else if (newChar < 'а') {
                        newChar = 'я' - ('а' - newChar - 1);
                    }
                }
                result.append((char) newChar);
            } else {
                result.append(c); // зберігаємо символи, які не є літерами
            }
        }


        resultTextArea.setText(result.toString()); // Встановлюємо зашифрований текст у текстове поле resultTextArea



    }

    public static boolean validateShift(int shift) {
        if (shift >= 1 && shift <= 33) {
            return true;
        } else {
            return false;
        }
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


    public static void Decrypt(String shiftStr) {
        int shift ;
        try {
            shift = -1 * Integer.parseInt(shiftStr); // Конвертуємо текст з поля величини зсуву у ціле число
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Введіть число зсуву");
            return;
        }
        Encrypt(String.valueOf(shift),false);
    }

    public static void AuthorInfo() {
        JOptionPane.showMessageDialog(null, "Розробник : [Олександр Власюк] \n Group : [ ПМІ - 32 ] \n Email :  [ al_vlasiuk33@gmail.com ]\n GitHub : [https://github.com/OleksandrVlasiuk]");
    }

    public static void BruteForceAttack() {
        // Частоти літер для мови (англійської та української)
        Map<Character, Double> englishLanguageFrequencies = getLanguageFrequencies("english");
        Map<Character, Double> ukrainianLanguageFrequencies = getLanguageFrequencies("ukrainian");

        int bestShift = -1;
        double minDifference = Double.MAX_VALUE;

        // Перебираємо всі можливі зсуви
        for (int shift = 1; shift <= 33; shift++) {
            StringBuilder decryptedText = new StringBuilder();
            for (char c : startTextArea.getText().toCharArray()) {
                char decryptedChar = decryptCharacter(c, shift);
                decryptedText.append(decryptedChar);
            }


            // Розраховуємо частоти літер у розшифрованому тексті
            Map<Character, Integer> decryptedFrequencies = calculateFrequencies(decryptedText.toString());

            // Розраховуємо метрику схожості для англійської та української мов
            double englishDifference = calculateDifference(englishLanguageFrequencies, decryptedFrequencies);
            double ukrainianDifference = calculateDifference(ukrainianLanguageFrequencies, decryptedFrequencies);

            // Обираємо найменшу метрику схожості

            double difference = englishDifference + ukrainianDifference;

            System.out.println(difference);


            if (difference <= minDifference) {
                minDifference = difference;
                bestShift = shift;
            }

        }

        // Виводимо результат атаки
        resultAttackLabel.setText("    Best shift: " + bestShift);
        System.out.println("    Shoft of attack " + bestShift);
    }

    public static char decryptCharacter(char c, int shift) {
        if (isEnglishLetter(c)) {
            if (Character.isUpperCase(c)) {
                int newChar = 'A' + ((c - 'A' - shift + 26) % 26);
                return (char) newChar;
            } else {
                int newChar = 'a' + ((c - 'a' - shift + 26) % 26);
                return (char) newChar;
            }
        } else if (isUkrainianLetter(c)) {
            int cyrillicShift = shift % 33;
            int newChar = c - cyrillicShift;
            if (c >= 'а' && c <= 'я') {
                if (newChar < 'а') {
                    newChar = 'я' - ('а' - newChar - 1);
                }
            }
            return (char) newChar;
        } else {
            return c;
        }
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

    public static void EncryptAny(String shiftStr) {
        int shift;
        try {
            shift = Integer.parseInt(shiftStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Введіть число зсуву");
            return;
        }

        // Отримуємо вміст з startTextArea
        String inputText = startTextArea.getText();

        StringBuilder result = new StringBuilder();

        // Шифруємо кожен символ вхідного тексту
        for (char c : inputText.toCharArray()) {
            int encryptedData = c + shift;
            result.append((char) encryptedData);
        }

        // Встановлюємо зашифрований текст у resultTextArea
        resultTextArea.setText(result.toString());

        JOptionPane.showMessageDialog(null, "Текст успішно зашифровано");
    }


    public static void DecryptAny(String shiftStr) {
        int shift;
        try {
            shift = -1 * Integer.parseInt(shiftStr); // Зміна напрямку зсуву для розшифрування
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Введіть число зсуву");
            return;
        }

        EncryptAny(String.valueOf(shift));
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

}
