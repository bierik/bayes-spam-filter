import sun.tools.jstat.Token;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Classifier {

    private Database db;

    public Classifier(Database db) {
        this.db = db;
    }

    private void learnSpam(HashSet<String> words) {
        db.incrementSpamCount();
        for(String word : words) {
            db.insertSpam(word);
        }
    }

    private void learnHam(HashSet<String> words) {
        db.incrementHamCount();
        for(String word : words) {
            db.insertHam(word);
        }
    }

    public void learnSpam(File file) {
        try {
            String text = EmlReader.read(file);
            HashSet<String> words = Tokenizer.words(text);
            System.out.println(String.format("Learning spam: %s", file.getAbsolutePath()));
            this.learnSpam(words);
        } catch (MessagingException | IOException e) {
            System.err.println(String.format("Error parsing file: %s", file.getAbsolutePath()));
        }
    }

    public void learnHam(File file) {
        try {
            String text = EmlReader.read(file);
            HashSet<String> words = Tokenizer.words(text);
            System.out.println(String.format("Learning ham: %s", file.getAbsolutePath()));
            this.learnHam(words);
        } catch (MessagingException | IOException e) {
            System.err.println(String.format("Error parsing file: %s", file.getAbsolutePath()));
        }
    }

    public double classify(File file) {
        try {
            String text = EmlReader.read(file);
            int spamCount = this.db.countSpam();
            int hamCount = this.db.countHam();
            HashSet<String> words = Tokenizer.words(text);
            double divident = words
                    .stream()
                    .map(w -> this.db.countWordInSpam(w) / spamCount)
                    .reduce(1.0, (a, b) -> a * b);
            double divisor = words
                    .stream()
                    .map(w -> this.db.countWordInHam(w) / hamCount)
                    .reduce(1.0, (a, b) -> a * b) + divident;
            return divident / divisor;
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
