import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;

/**
 * Represents the classifier to train for spams and hams.
 * Is also able to classify a file
 */
public class Classifier {

    class ClassificationException extends Exception {
        ClassificationException(String msg) { super(msg); }
    }

    private Database db;
    private double alpha;

    /**
     * Classifier instance
     * @param db set the database file name
     * @param alpha set the alpha value
     */
    public Classifier(Database db, double alpha) {
        this.db = db;
        this.alpha = alpha;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    /**
     * Inserts a set of words in the database as spam
     * @param words
     */
    private void learnSpam(HashSet<String> words) {
        db.incrementSpamCount();
        words.stream().forEach(db::insertSpam);
    }

    /**
     * Inserts a set of words in the database as ham
     * @param words
     */
    private void learnHam(HashSet<String> words) {
        db.incrementHamCount();
        words.stream().forEach(db::insertHam);
    }

    /**
     * Generates a unique set of words from an .eml file
     * @param file
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    private HashSet<String> fileToWords(File file) throws MessagingException, IOException {
        // Get the text content of the .eml file
        String text = EmlReader.read(file);
        // Get a unique set of words in the text
        return Tokenizer.words(text);
    }

    /**
     * Inserts a whole file in the database as spam
     * @param file The file to insert as .eml
     */
    public void learnSpam(File file) {
        try {
            HashSet<String> words = this.fileToWords(file);
            System.out.println(String.format("Learning spam: %s", file.getAbsolutePath()));
            this.learnSpam(words);
        } catch (MessagingException | IOException e) {
            System.err.println(String.format("Error parsing file: %s", file.getAbsolutePath()));
        }
    }

    /**
     * Inserts a whole file in the database as ham
     * @param file The file to insert as .eml
     */
    public void learnHam(File file) {
        try {
            HashSet<String> words = this.fileToWords(file);
            System.out.println(String.format("Learning ham: %s", file.getAbsolutePath()));
            this.learnHam(words);
        } catch (MessagingException | IOException e) {
            System.err.println(String.format("Error parsing file: %s", file.getAbsolutePath()));
        }
    }

    /**
     * Classifies a .eml as spam
     * @param file The .eml file to classify
     * @return A number between 0 and 1 which indicates the probability to be spam
     * @throws ClassificationException
     */
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

        // Calculate probability using naive Bayes http://www.math.kit.edu/ianm4/~ritterbusch/seite/spam/de
        for (String w : words) {
            Integer wordInSpam = this.db.countWordInSpam(w);
            Integer wordInHam = this.db.countWordInHam(w);

            if (wordInSpam == null && wordInHam == null) {
                continue;
            }

            double probSpam = (wordInSpam == null ? this.alpha : wordInSpam) / spamCount;
            double probHam = (wordInHam == null ? this.alpha : wordInHam) / hamCount;

            res *= probHam / probSpam;
        }

        return 1 / (1 + res);
    }

    /**
     * Determens if file is spam
     * @param file File to classify
     * @return true if file is classified as spam
     * @throws ClassificationException
     */
    public boolean isSpam(File file) throws ClassificationException {
        return this.classify(file) >= 0.5;
    }

    /**
     * Determens if file is ham
     * @param file File to classify
     * @return true if file is classified as ham
     * @throws ClassificationException
     */
    public boolean isHam(File file) throws ClassificationException {
        return this.classify(file) < 0.5;
    }
}
