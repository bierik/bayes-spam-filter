import java.sql.*;

public class Database {

    private Connection connection;
    private Statement statement;

    public Database(String databasename) {
        try {
            this.connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s.db", databasename));
            this.statement = connection.createStatement();
            this.statement.setQueryTimeout(30);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setup() {
        try {
            this.statement.executeUpdate("DROP TABLE IF EXISTS SPAM");
            this.statement.executeUpdate("CREATE TABLE IF NOT EXISTS SPAM (id INTEGER PRIMARY KEY AUTOINCREMENT, word TEXT, amount INTEGER)");
            this.statement.executeUpdate("DROP TABLE IF EXISTS HAM");
            this.statement.executeUpdate("CREATE TABLE IF NOT EXISTS HAM (id INTEGER PRIMARY KEY AUTOINCREMENT, word TEXT, amount INTEGER)");
            this.statement.executeUpdate("DROP TABLE IF EXISTS COUNTER");
            this.statement.executeUpdate("CREATE TABLE IF NOT EXISTS COUNTER (id INTEGER PRIMARY KEY AUTOINCREMENT, spam INTEGER, ham INTEGER)");
            this.statement.executeUpdate("INSERT INTO COUNTER (spam, ham) values (0, 0)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertSpam(String word) {
        try {
            Boolean wordExists = this.statement.executeQuery(String.format("SELECT EXISTS(SELECT 1 FROM SPAM WHERE word='%s');", word)).getBoolean(1);
            if (wordExists) {
                this.statement.executeUpdate(String.format("UPDATE SPAM SET amount = amount + 1 WHERE word = '%s'", word));
            } else {
                this.statement.executeUpdate(String.format("INSERT INTO SPAM (word, amount) values('%s', 1)", word));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertHam(String word) {
        try {
            Boolean wordExists = this.statement.executeQuery(String.format("SELECT EXISTS(SELECT 1 FROM HAM WHERE word='%s');", word)).getBoolean(1);
            if (wordExists) {
                this.statement.executeUpdate(String.format("UPDATE HAM SET amount = amount + 1 WHERE word = '%s'", word));
            } else {
                this.statement.executeUpdate(String.format("INSERT INTO HAM (word, amount) values('%s', 1)", word));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void incrementHamCount() {
        try {
            this.statement.executeUpdate("UPDATE COUNTER SET ham = ham + 1");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void incrementSpamCount() {
        try {
            this.statement.executeUpdate("UPDATE COUNTER SET spam = spam + 1");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Integer countHam() {
        try {
            return this.statement.executeQuery("SELECT ham FROM COUNTER LIMIT 1").getInt(1);
        } catch (SQLException e) {
            return null;
        }
    }

    public Integer countSpam() {
        try {
            return this.statement.executeQuery("SELECT spam FROM COUNTER LIMIT 1").getInt(1);
        } catch (SQLException e) {
            return null;
        }
    }

    public Integer countWordInHam(String word) {
        try {
            return this.statement.executeQuery(String.format("SELECT amount FROM ham WHERE word = '%s' LIMIT 1", word)).getInt(1);
        } catch (SQLException e) {
            return null;
        }
    }

    public Integer countWordInSpam(String word) {
        try {
            return this.statement.executeQuery(String.format("SELECT amount FROM spam WHERE word = '%s' LIMIT 1", word)).getInt(1);
        } catch (SQLException e) {
            return null;
        }
    }
}
