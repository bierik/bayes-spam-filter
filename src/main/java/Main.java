import sun.tools.jstat.Token;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        Session mailSession = Session.getDefaultInstance(new Properties());
        Database db = new Database("anlern");
        // db.setup();

        int c = 1;
        File folder = null;
        try {
            folder = new File(Main.class.getResource("spam-anlern").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Classifier classifier = new Classifier(db);
        for (final File fileEntry : folder.listFiles()) {
            String message = null;
            try {
                message = EmlReader.read(fileEntry);
            } catch (MessagingException e) {
                System.out.println(String.format("Error processing file", fileEntry.getAbsolutePath()));
                continue;
            } catch (IOException e) {
                continue;
            }
            String[] words = Tokenizer.words(message);
            classifier.learnSpam(words);
            System.out.println(String.format("Processing Mail: %d Percent", Math.round((c / 249.0) * 100 )));
            c++;
        }
    }
}
