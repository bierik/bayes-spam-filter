import javax.mail.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class EmlReader {

    public static String read(File eml) throws MessagingException, IOException {
        Session mailSession = Session.getDefaultInstance(new Properties());
        Message message = new Message(mailSession, new FileInputStream(eml));
        return message.extractText();
    }
}
