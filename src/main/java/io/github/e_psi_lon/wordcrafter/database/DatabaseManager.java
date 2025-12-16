package io.github.e_psi_lon.wordcrafter.database;

import io.github.e_psi_lon.wordcrafter.model.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages database connections and operations using JDBC.
 * Supports both MySQL/MariaDB (production) and SQLite (development).
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;
    private DatabaseType dbType;

    // Database configuration - use environment variables or defaults
    private static final String DB_TYPE = System.getenv().getOrDefault("DB_TYPE", "SQLITE"); // SQLITE or MYSQL
    private static final String DB_HOST = System.getenv().getOrDefault("DB_HOST", "localhost");
    private static final String DB_PORT = System.getenv().getOrDefault("DB_PORT", "3306");
    private static final String DB_NAME = System.getenv().getOrDefault("DB_NAME", "wordcrafter");
    private static final String DB_USER = System.getenv().getOrDefault("DB_USER", "wordcrafter");
    private static final String DB_PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "");

    private enum DatabaseType {
        SQLITE, MYSQL
    }

    private DatabaseManager() {}

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public void initialize() {
        try {
            // Determine database type
            dbType = DB_TYPE.equalsIgnoreCase("MYSQL") ? DatabaseType.MYSQL : DatabaseType.SQLITE;

            // Create connection based on type
            if (dbType == DatabaseType.MYSQL) {
                String mysqlUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                        DB_HOST, DB_PORT, DB_NAME);
                connection = DriverManager.getConnection(mysqlUrl, DB_USER, DB_PASSWORD);
                System.out.println("Connected to MySQL/MariaDB database");
            } else {
                String sqliteUrl = "jdbc:sqlite:wordcrafter.db";
                connection = DriverManager.getConnection(sqliteUrl);
                System.out.println("Connected to SQLite database (development mode)");
            }

            createTables();
            insertDefaultData();
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
        String createUsersTable;
        String createMorphemesTable;
        String createWordsTable;
        String createWordMorphemesTable;
        String createPlayerWordsTable;

        if (dbType == DatabaseType.MYSQL) {
            // MySQL/MariaDB syntax with proper ENUMs and VARCHARs
            createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password_hash VARCHAR(255) NOT NULL,
                    role ENUM('PLAYER', 'ADMIN') NOT NULL DEFAULT 'PLAYER',
                    score INT DEFAULT 0
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """;

            createMorphemesTable = """
                CREATE TABLE IF NOT EXISTS morphemes (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    text VARCHAR(100) NOT NULL,
                    definition TEXT NOT NULL
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """;

            createWordsTable = """
                CREATE TABLE IF NOT EXISTS words (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    text VARCHAR(100) UNIQUE NOT NULL,
                    points INT NOT NULL,
                    definition TEXT NOT NULL DEFAULT ''
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """;

            createWordMorphemesTable = """
                CREATE TABLE IF NOT EXISTS word_morphemes (
                    word_id INT NOT NULL,
                    morpheme_id INT NOT NULL,
                    position INT NOT NULL,
                    FOREIGN KEY (word_id) REFERENCES words(id) ON DELETE CASCADE,
                    FOREIGN KEY (morpheme_id) REFERENCES morphemes(id) ON DELETE CASCADE,
                    PRIMARY KEY (word_id, morpheme_id, position)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """;

            createPlayerWordsTable = """
                CREATE TABLE IF NOT EXISTS player_words (
                    user_id INT NOT NULL,
                    word_id INT NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                    FOREIGN KEY (word_id) REFERENCES words(id) ON DELETE CASCADE,
                    PRIMARY KEY (user_id, word_id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                """;
        } else {
            // SQLite syntax
            createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password_hash VARCHAR(255) NOT NULL,
                    role VARCHAR(10) NOT NULL CHECK(role IN ('PLAYER', 'ADMIN')) DEFAULT 'PLAYER',
                    score INTEGER DEFAULT 0
                )
                """;

            createMorphemesTable = """
                CREATE TABLE IF NOT EXISTS morphemes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    text VARCHAR(100) NOT NULL,
                    definition TEXT NOT NULL
                )
                """;

            createWordsTable = """
                CREATE TABLE IF NOT EXISTS words (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    text VARCHAR(100) UNIQUE NOT NULL,
                    points INTEGER NOT NULL,
                    definition TEXT NOT NULL DEFAULT ''
                )
                """;

            createWordMorphemesTable = """
                CREATE TABLE IF NOT EXISTS word_morphemes (
                    word_id INTEGER NOT NULL,
                    morpheme_id INTEGER NOT NULL,
                    position INTEGER NOT NULL,
                    FOREIGN KEY (word_id) REFERENCES words(id) ON DELETE CASCADE,
                    FOREIGN KEY (morpheme_id) REFERENCES morphemes(id) ON DELETE CASCADE,
                    PRIMARY KEY (word_id, morpheme_id, position)
                )
                """;

            createPlayerWordsTable = """
                CREATE TABLE IF NOT EXISTS player_words (
                    user_id INTEGER NOT NULL,
                    word_id INTEGER NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                    FOREIGN KEY (word_id) REFERENCES words(id) ON DELETE CASCADE,
                    PRIMARY KEY (user_id, word_id)
                )
                """;
        }

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createMorphemesTable);
            stmt.execute(createWordsTable);
            stmt.execute(createWordMorphemesTable);
            stmt.execute(createPlayerWordsTable);
        }
    }

    private void insertDefaultData() throws SQLException {
        // Check if data already exists
        String checkQuery = "SELECT COUNT(*) FROM users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(checkQuery)) {
            if (rs.next() && rs.getInt(1) > 0) {
                return; // Data already exists
            }
        }

        // Insert default admin user (password: admin)
        String insertAdmin = "INSERT INTO users (username, password_hash, role, score) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertAdmin)) {
            pstmt.setString(1, "admin");
            pstmt.setString(2, hashPassword("admin"));
            pstmt.setString(3, "ADMIN");
            pstmt.setInt(4, 0);
            pstmt.executeUpdate();
        }

        // Insert some sample morphemes
        String insertMorpheme = "INSERT INTO morphemes (text, definition) VALUES (?, ?)";
        String[][] sampleMorphemes = {
            {"dé", "Enlever ou inverser"},
            {"re", "De nouveau ou en arrière"},
            {"pré", "Avant"},
            {"jouer", "Pratiquer un jeu ou un divertissement"},
            {"faire", "Accomplir une action"},
            {"voir", "Percevoir avec les yeux"},
            {"able", "Capable d'être"},
            {"ment", "De manière"},
            {"tion", "Action ou procédé"}
        };

        try (PreparedStatement pstmt = connection.prepareStatement(insertMorpheme)) {
            for (String[] morpheme : sampleMorphemes) {
                pstmt.setString(1, morpheme[0]);
                pstmt.setString(2, morpheme[1]);
                pstmt.executeUpdate();
            }
        }

        // Insert some sample words (French) using proper junction table
        String insertWord = "INSERT INTO words (text, points, definition) VALUES (?, ?, ?)";
        String insertWordMorpheme = "INSERT INTO word_morphemes (word_id, morpheme_id, position) VALUES (?, ?, ?)";

        try (PreparedStatement wordStmt = connection.prepareStatement(insertWord, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement morphemeStmt = connection.prepareStatement(insertWordMorpheme)) {
            wordStmt.setString(1, "refaire");
            wordStmt.setInt(2, 5);
            wordStmt.setString(3, "Faire de nouveau, recommencer une action");
            wordStmt.executeUpdate();

            int wordId = getGeneratedKey(wordStmt);
            morphemeStmt.setInt(1, wordId);
            morphemeStmt.setInt(2, 2);
            morphemeStmt.setInt(3, 0);
            morphemeStmt.executeUpdate();

            morphemeStmt.setInt(1, wordId);
            morphemeStmt.setInt(2, 5);
            morphemeStmt.setInt(3, 1);
            morphemeStmt.executeUpdate();

            // Word: prévoir = pré (id 3) + voir (id 6)
            wordStmt.setString(1, "prévoir");
            wordStmt.setInt(2, 5);
            wordStmt.setString(3, "Anticiper ou prévoir ce qui va se passer");
            wordStmt.executeUpdate();

            wordId = getGeneratedKey(wordStmt);
            morphemeStmt.setInt(1, wordId);
            morphemeStmt.setInt(2, 3);
            morphemeStmt.setInt(3, 0);
            morphemeStmt.executeUpdate();

            morphemeStmt.setInt(1, wordId);
            morphemeStmt.setInt(2, 6);
            morphemeStmt.setInt(3, 1);
            morphemeStmt.executeUpdate();
        }
    }

    private String hashPassword(String password) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            byte[] saltAndHash = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, saltAndHash, 0, salt.length);
            System.arraycopy(hashedPassword, 0, saltAndHash, salt.length, hashedPassword.length);
            return bytesToHex(saltAndHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    private boolean verifyPassword(String password, String hash) {
        try {
            byte[] saltAndHash = hexToBytes(hash);
            byte[] salt = new byte[16];
            System.arraycopy(saltAndHash, 0, salt, 0, 16);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            byte[] storedHash = new byte[32];
            System.arraycopy(saltAndHash, 16, storedHash, 0, 32);
            for (int i = 0; i < hashedPassword.length; i++)
                if (hashedPassword[i] != storedHash[i])
                    return false;
            return true;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private byte[] hexToBytes(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return bytes;
    }

    public User authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Verify password using salt from stored hash
                String storedHash = rs.getString("password_hash");
                if (verifyPassword(password, storedHash)) {
                    if (rs.getString("role").equals("ADMIN")) {
                        return new Admin(
                            rs.getInt("id"),
                            rs.getString("username"),
                            storedHash
                        );
                    }
                    else {
                        return new Player(
                            rs.getInt("id"),
                            rs.getString("username"),
                            storedHash,
                            rs.getInt("score")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createPlayer(String username, String password) {
        String query = "INSERT INTO users (username, password_hash, role, score) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashPassword(password));
            pstmt.setString(3, "PLAYER");
            pstmt.setInt(4, 0);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createAdmin(String username, String password) {
        String query = "INSERT INTO users (username, password_hash, role, score) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashPassword(password));
            pstmt.setString(3, "ADMIN");
            pstmt.setInt(4, 0);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Morpheme> getAllMorphemes() {
        List<Morpheme> morphemes = new ArrayList<>();
        String query = "SELECT * FROM morphemes";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                morphemes.add(new Morpheme(
                    rs.getInt("id"),
                    rs.getString("text"),
                    rs.getString("definition")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return morphemes;
    }

    public Word validateWord(String text, List<Integer> morphemeIds) {
        // First, find the word by text
        String wordQuery = "SELECT * FROM words WHERE text = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(wordQuery)) {
            pstmt.setString(1, text);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int wordId = rs.getInt("id");
                String wordText = rs.getString("text");
                int points = rs.getInt("points");
                String definition = rs.getString("definition");

                // Get the morphemes for this word in order
                String morphemesQuery = "SELECT morpheme_id FROM word_morphemes WHERE word_id = ? ORDER BY position";
                List<Integer> wordMorphemeIds = new ArrayList<>();

                try (PreparedStatement morphStmt = connection.prepareStatement(morphemesQuery)) {
                    morphStmt.setInt(1, wordId);
                    ResultSet morphRs = morphStmt.executeQuery();

                    while (morphRs.next()) {
                        wordMorphemeIds.add(morphRs.getInt("morpheme_id"));
                    }
                }

                // Check if the morpheme lists match
                if (wordMorphemeIds.equals(morphemeIds)) {
                    return new Word(wordId, wordText, wordMorphemeIds, points, definition);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void updateUserScore(int userId, int additionalPoints) {
        String query = "UPDATE users SET score = score + ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, additionalPoints);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPlayerWord(int userId, int wordId) {
        String query;
        if (dbType == DatabaseType.MYSQL) {
            query = "INSERT IGNORE INTO player_words (user_id, word_id) VALUES (?, ?)";
        } else {
            query = "INSERT OR IGNORE INTO player_words (user_id, word_id) VALUES (?, ?)";
        }
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, wordId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMorpheme(String text, String definition) {
        String query = "INSERT INTO morphemes (text, definition) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, text);
            pstmt.setString(2, definition);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addWord(String text, List<Integer> morphemeIds, int points, String definition) {
        String insertWordQuery = "INSERT INTO words (text, points, definition) VALUES (?, ?, ?)";
        String insertWordMorphemeQuery = "INSERT INTO word_morphemes (word_id, morpheme_id, position) VALUES (?, ?, ?)";

        try (PreparedStatement wordStmt = connection.prepareStatement(insertWordQuery, Statement.RETURN_GENERATED_KEYS)) {
            wordStmt.setString(1, text);
            wordStmt.setInt(2, points);
            wordStmt.setString(3, definition);
            wordStmt.executeUpdate();

            ResultSet rs = wordStmt.getGeneratedKeys();
            if (rs.next()) {
                int wordId = rs.getInt(1);

                try (PreparedStatement morphemeStmt = connection.prepareStatement(insertWordMorphemeQuery)) {
                    for (int position = 0; position < morphemeIds.size(); position++) {
                        morphemeStmt.setInt(1, wordId);
                        morphemeStmt.setInt(2, morphemeIds.get(position));
                        morphemeStmt.setInt(3, position);
                        morphemeStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getGeneratedKey(PreparedStatement stmt) throws SQLException {
        if (dbType == DatabaseType.MYSQL) {
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            throw new SQLException("No generated keys returned.");
        } else {
            try (Statement s = connection.createStatement();
                 ResultSet rs = s.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) return rs.getInt(1);
            }
            throw new SQLException("Could not get last_insert_rowid() from SQLite.");
        }
    }


    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

