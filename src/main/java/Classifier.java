public class Classifier {

    private Database db;

    public Classifier(Database db) {
        this.db = db;
    }

    public void learnSpam(String [] words) {
        for(String word : words) {
            db.insertSpam(word);
        }
    }

    public void learnHam(String [] words) {
        for(String word : words) {
            db.insertHam(word);
        }
    }

    public void classify(String str) {
        
    }
}
