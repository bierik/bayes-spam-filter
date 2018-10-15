import java.util.Arrays;
import java.util.HashSet;

/**
 * Represents tokenizer class
 */
public class Tokenizer {

    /**
     * Simply splits a string of text in a unique set of words
     * @param str
     * @return
     */
    public static HashSet<String> words(String str) {
        return new HashSet<>(Arrays.asList(str.split("\\W+")));
    }

}
