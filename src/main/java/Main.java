import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Collection;

public class Main {

    public static void main(String[] args) {

        /*
        Value which indicates absence of an entry in the database
        May be something between 0 and 1.
        Must not be 0 because then the whole classification process fails
        A probability of 0 turns the whole classification to 0

        The Classifier::calibrate method will help to determine this value
        */
        final double ALPHA = 0.0008;

        if (args.length == 2) {
            // Read the data database
            Database db = new Database("data");
            // Initialize classifier with database and ALPHA
            Classifier classifier = new Classifier(db, ALPHA);

            // Get input file from command line arguments
            File input = new File(args[1]);

            // Command to classify .eml file
            if (args[0].equals("classify")) {

                try {
                    System.out.println(classifier.classify(input));
                } catch (Classifier.ClassificationException e) {
                    e.printStackTrace();
                }

            // Resets the database
            } else if (args[0].equals("reset")) {

                db.setup();

            // Outputs precision that files have been correctly detected as spam
            } else if (args[0].equals("test")) {

                Collection<File> files = FileUtils.listFiles(input, null, true);
                System.out.println(String.format("Testing %s files...", files.size()));
                double countSpams = files.stream().map(f -> {
                    try {
                        return classifier.isSpam(f);
                    } catch (Classifier.ClassificationException e) {
                        e.printStackTrace();
                        return false;
                    }
                }).filter(v -> v).count();

                System.out.println(String.format("%s from %s where detected as spam", countSpams, files.size()));
                System.out.println(String.format("%s precision", (countSpams / files.size()) * 100));

            // Command to train the database with ham files
            } else if (args[0].equals("trainHam")) {

                Collection<File> files = FileUtils.listFiles(input, null, true);
                System.out.println(String.format("Parsing %s ham-files...", files.size()));
                files.stream().forEach(classifier::learnHam);
                System.out.println("Files successfully parsed");

                // Command to train the database with spam files
            } else if (args[0].equals("trainSpam")) {

                Collection<File> files = FileUtils.listFiles(input, null, true);
                System.out.println(String.format("Parsing %s spam-files...", files.size()));
                files.stream().forEach(classifier::learnSpam);
                System.out.println("Files successfully parsed");

            }
        }
    }
}
