import javax.mail.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Static class for reading .eml files
 */
public class EmlReader {

    /**
     * Reads an .eml file and extracts its text
     * @param eml The .eml file to extract
     * @return The extracted text from the .eml file with all multiparts
     * @throws MessagingException
     * @throws IOException
     */
    public static String read(File eml) throws MessagingException, IOException {
        Session mailSession = Session.getDefaultInstance(new Properties());
        Message message = new Message(mailSession, new FileInputStream(eml));
        return message.extractText();
    }
}
