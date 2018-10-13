import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.io.InputStream;

public class Message extends MimeMessage {

    public Message(Session session, InputStream is) throws MessagingException {
        super(session, is);
    }

    private boolean isMultipart() throws MessagingException {
        return this.isMimeType("multipart/*");
    }

    private boolean isText() throws MessagingException {
        return this.isMimeType("text/plain");
    }

    private boolean isHTML() throws MessagingException {
        return this.isMimeType("text/html");
    }

    private String extractTextFromMultipart(MimeMultipart multipart) throws MessagingException, IOException {
        String res = "";
        int count = multipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);

            if (bodyPart.isMimeType("text/plain")) {
                res = res + "\n" + bodyPart.getContent();
            } else if (bodyPart.isMimeType("multipart/*")){
                res = res + extractTextFromMultipart((MimeMultipart)bodyPart.getContent());
            }
        }
        return res;
    }

    private String extractTextFromHTML(String htmlString) {
        Document doc = Jsoup.parse(htmlString);
        return doc.body().text();
    }

    public String extractText() throws MessagingException, IOException {
        if (this.isText()) {
            return this.getContent().toString();
        } else if (this.isHTML()) {
            return this.extractTextFromHTML(this.getContent().toString());
        } else if (this.isMultipart()) {
            return extractTextFromMultipart((MimeMultipart) this.getContent());
        } else {
            return "";
        }
    }


}
