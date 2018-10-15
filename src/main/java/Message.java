import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.io.InputStream;

/**
 * Extends base MimeMessage class from javax.mail which is able to recursively extract multiparts to text
 */
public class Message extends MimeMessage {

    public Message(Session session, InputStream is) throws MessagingException {
        super(session, is);
    }

    /**
     * Check if Message is a Multipart
     * @return true if message is multipart
     * @throws MessagingException
     */
    private boolean isMultipart() throws MessagingException {
        return this.isMimeType("multipart/*");
    }

    /**
     * Check if Message is a text
     * @return true if message is text
     * @throws MessagingException
     */
    private boolean isText() throws MessagingException {
        return this.isMimeType("text/plain");
    }

    /**
     * Check if Message is a html
     * @return true if message is html
     * @throws MessagingException
     */
    private boolean isHTML() throws MessagingException {
        return this.isMimeType("text/html");
    }

    /**
     * Recursively extract text from MimeMultipart
     * @param multipart The Multipart to extract
     * @return Extracted text from MultiPart
     * @throws MessagingException
     * @throws IOException
     */
    private String extractTextFromMultipart(MimeMultipart multipart) throws MessagingException, IOException {
        String res = "";
        int count = multipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);

            if (bodyPart.isMimeType("text/plain")) {
                res = res + bodyPart.getContent();
            } else if (bodyPart.isMimeType("multipart/*")){
                res = res + extractTextFromMultipart((MimeMultipart)bodyPart.getContent());
            }
        }
        return res;
    }

    /**
     * Uses Jsoup to extract text from HTML
     * @param htmlString The HTML String to extract
     * @return Extracted text from HTML string
     */
    private String extractTextFromHTML(String htmlString) {
        Document doc = Jsoup.parse(htmlString);
        return doc.body().text();
    }

    /**
     * Extract text from the Message
     * @return Extracted text from the message containing all multiparts
     * @throws MessagingException
     * @throws IOException
     */
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
