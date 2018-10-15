import sun.tools.jstat.Token;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class Classifier {

    class ClassificationException extends Exception {
        ClassificationException(String msg) { super(msg); }
    }

    private Database db;

    /*
        Value which indicates absence of an entry in the database
        May be something between 0 and 1.
        Must not be 0 because then the whole classification process fails
        A probability of 0 turns the whole classification to 0

        The Classifier::calibrate method will help to determine this value
     */
    private static final double ALPHA = 0.0008;

    public Classifier(Database db) {
        this.db = db;
    }

    private void learnSpam(HashSet<String> words) {
        db.incrementSpamCount();
        words.stream().forEach(db::insertSpam);
    }

    private void learnHam(HashSet<String> words) {
        db.incrementHamCount();
        words.stream().forEach(db::insertHam);
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

    public void calibrate() {

    }

    public double classify(File file) throws ClassificationException {
        String text = "";

        try {
            text = EmlReader.read(file);
        } catch (MessagingException | IOException e) {
            throw new ClassificationException(e.getMessage());
        }

        int spamCount = this.db.countSpam();
        int hamCount = this.db.countHam();
        HashSet<String> words = Tokenizer.words(text);

        double res = 1;

        for (String w : words) {
            Integer wordInSpam = this.db.countWordInSpam(w);
            Integer wordInHam = this.db.countWordInHam(w);

            if (wordInSpam == null && wordInHam == null) {
                continue;
            }

            double probSpam = (wordInSpam == null ? ALPHA : wordInSpam) / spamCount;
            double probHam = (wordInHam == null ? ALPHA : wordInHam) / hamCount;

            res *= probHam / probSpam;
        }

        return 1 / (1 + res);
    }

    public boolean isSpam(File file) throws ClassificationException {
        return this.classify(file) >= 0.5;
    }

    public boolean isHam(File file) throws ClassificationException {
        return this.classify(file) < 0.5;
    }
}
