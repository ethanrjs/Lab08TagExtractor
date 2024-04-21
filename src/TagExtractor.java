import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class TagExtractor extends JFrame {
    private final JFileChooser fileChooser;
    private final JTextArea textArea;
    private final Map<String, Integer> wordFrequency;
    private final Set<String> stopWords;
    private Collectors Collectors;

    public TagExtractor() {
        // Initialize components
        fileChooser = new JFileChooser();
        textArea = new JTextArea();
        wordFrequency = new HashMap<>();
        stopWords = new TreeSet<>();

        // Load stop words from file
        loadStopWords();

        // Create GUI
        createGUI();
    }

    private void loadStopWords() {
        // Load stop words from file
        try (BufferedReader reader = new BufferedReader(new FileReader("stopwords.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stopWords.add(line.toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);

        JButton openButton = new JButton("Open File");
        JButton saveButton = new JButton("Save Tags");
        JScrollPane scrollPane = new JScrollPane(textArea);

        add(openButton, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);

        openButton.addActionListener(e -> openFile());
        saveButton.addActionListener(e -> saveTags());

        setVisible(true);
    }

    private void openFile() {
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            extractTags(file);
        }
    }

    private void extractTags(File file) {
        wordFrequency.clear();
        textArea.setText("");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    word = word.toLowerCase().replaceAll("[^a-zA-Z]", "");
                    if (!word.isEmpty() && !stopWords.contains(word)) {
                        wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        displayTags();
    }

    private void displayTags() {
        List<Map.Entry<String, Integer>> sortedEntries = wordFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .toList();

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            sb.append(entry.getKey()).append(" (").append(entry.getValue()).append(")\n");
        }
        textArea.setText(sb.toString());
    }

    private void saveTags() {
        List<Map.Entry<String, Integer>> sortedEntries = wordFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("tags.txt"))) {
            for (Map.Entry<String, Integer> entry : sortedEntries) {
                writer.write(entry.getKey() + " " + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(TagExtractor::new);
    }
}
