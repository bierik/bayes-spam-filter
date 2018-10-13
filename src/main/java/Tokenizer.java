import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Tokenizer {

    public static HashSet<String> words(String str) {
        return new HashSet<String>(Arrays.asList(str.split("\\W+")));
    }

}
