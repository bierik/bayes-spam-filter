import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Collection;

public class Main {

    public static void main(String[] args) {
        if (args.length == 2) {
            Database db = new Database("data");
            Classifier classifier = new Classifier(db);

            File input = new File(args[1]);

            if (args[0].equals("classify")) {

                try {
                    System.out.println(classifier.classify(input));
                } catch (Classifier.ClassificationException e) {
                    e.printStackTrace();
                }

            } else if (args[0].equals("reset")) {

                db.setup();

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

            } else if (args[0].equals("trainHam")) {

                Collection<File> files = FileUtils.listFiles(input, null, true);
                System.out.println(String.format("Parsing %s ham-files...", files.size()));
                files.stream().forEach(classifier::learnHam);
                System.out.println("Files successfully parsed");

            } else if (args[0].equals("trainSpam")) {

                Collection<File> files = FileUtils.listFiles(input, null, true);
                System.out.println(String.format("Parsing %s spam-files...", files.size()));
                files.stream().forEach(classifier::learnSpam);
                System.out.println("Files successfully parsed");

            }
        }
    }
}
