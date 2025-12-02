import java.sql.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * MyDBMagic - A PostgreSQL Database Management Utility
 * 
 * This class provides an easy-to-use interface for students and educators
 * to connect to PostgreSQL databases and perform common operations like
 * creating tables, inserting records, and querying data.
 * 
 * @author John Hernandez
 * @version 1.0
 */
public class MyDBMagic implements AutoCloseable {
    
    private static final Logger LOGGER = Logger.getLogger(MyDBMagic.class.getName());
    
    // Database connection parameters
    private final String port;
    private final String database;
    private final String username;
    private final String password;
    
    // Connection management
    private Connection connection = null;
    private Field[] currentTableFields;
    
    // Default constructor with common PostgreSQL defaults
    public MyDBMagic() {
        this.port = "5432"; // Standard PostgreSQL port
        this.database = "postgres";
        this.username = "postgres";
        this.password = "password"; // Consider using environment variables
        LOGGER.info("MyDBMagic initialized with default settings");
    }
    
    /**
     * Constructor with custom database connection parameters
     * 
     * @param port Database port
     * @param database Database name
     * @param username Database username
     * @param password Database password
     */
    public MyDBMagic(String port, String database, String username, String password) {
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        LOGGER.info("MyDBMagic initialized with custom settings for database: " + database);
    }
    
    /**
     * Establishes connection to the PostgreSQL database
     * 
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public Connection connectDB() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return connection;
        }
        
        String url = "jdbc:postgresql://localhost:" + this.port + "/" + this.database;
        
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, this.username, this.password);
            LOGGER.info("✓ Connected successfully to: " + url);
            return connection;
        } catch (ClassNotFoundException e) {
            LOGGER.severe("PostgreSQL JDBC Driver not found");
            throw new SQLException("PostgreSQL JDBC Driver not found", e);
        } catch (SQLException e) {
            LOGGER.severe("Failed to connect to database: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Gets the current database name
     * 
     * @return database name
     */
    public String getDatabase() {
        return this.database;
    }
    
    /**
     * Utility method to check boolean values from user input
     */
    private boolean isTrue(String value) {
        return "true".equalsIgnoreCase(value) || "1".equals(value) || "yes".equalsIgnoreCase(value);
    }
    
    /**
     * Interactive method to create a new table
     * Guides user through table creation process
     */
    public void createTable() {
        try (Scanner scanner = new Scanner(System.in)) {
            boolean hasPrimaryKey = false;
            
            System.out.print("Enter table name: ");
            String tableName = scanner.nextLine().trim();
            
            if (tableName.isEmpty()) {
                System.err.println("Table name cannot be empty!");
                return;
            }
            
            System.out.print("Enter number of fields: ");
            int fieldCount;
            try {
                fieldCount = scanner.nextInt();
                scanner.nextLine(); // consume newline
                
                if (fieldCount <= 0) {
                    System.err.println("Number of fields must be positive!");
                    return;
                }
            } catch (InputMismatchException e) {
                System.err.println("Please enter a valid number!");
                return;
            }
            
            String[][] tableData = new String[3][fieldCount];
            
            for (int i = 0; i < fieldCount; i++) {
                System.out.println("\n--- Field " + (i + 1) + " ---");
                System.out.print("Field name: ");
                tableData[0][i] = scanner.nextLine().trim();
                
                System.out.print("Field type (INTEGER, VARCHAR, TEXT, etc.): ");
                tableData[1][i] = scanner.nextLine().trim().toUpperCase();
                
                if (!hasPrimaryKey) {
                    System.out.print("Is primary key? (true/false, 1/0, yes/no): ");
                    String pkInput = scanner.nextLine().trim();
                    if (isTrue(pkInput)) {
                        tableData[2][i] = "true";
                        hasPrimaryKey = true;
                    } else {
                        tableData[2][i] = "false";
                    }
                } else {
                    tableData[2][i] = "false";
                }
            }
            
            // Display table summary
            System.out.println("\n--- Table Summary ---");
            System.out.println("Table: " + tableName);
            for (int i = 0; i < fieldCount; i++) {
                System.out.printf("Field %d: %s %s %s%n",
                    i + 1,
                    tableData[0][i],
                    tableData[1][i],
                    isTrue(tableData[2][i]) ? "(PRIMARY KEY)" : "");
            }
            
            System.out.print("Create this table? (y/n): ");
            String confirm = scanner.nextLine().trim();
            if (confirm.equalsIgnoreCase("y") || confirm.equalsIgnoreCase("yes")) {
                createTableFromData(tableName, tableData);
            } else {
                System.out.println("Table creation cancelled.");
            }
            
        } catch (Exception e) {
            LOGGER.severe("Error in createTable: " + e.getMessage());
            System.err.println("Error creating table: " + e.getMessage());
        }
    }
    
    /**
     * Creates table from provided data array
     */
    private void createTableFromData(String tableName, String[][] tableData) {
        if (tableData.length != 3) {
            throw new IllegalArgumentException("Array must have exactly 3 rows: [names][types][isPrimaryKey]");
        }
        
        String[] fieldNames = tableData[0];
        String[] fieldTypes = tableData[1];
        String[] isPrimaryKey = tableData[2];
        
        if (fieldNames.length != fieldTypes.length || fieldNames.length != isPrimaryKey.length) {
            throw new IllegalArgumentException("All arrays must have the same length");
        }
        
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ").append(tableName).append(" (");
        
        for (int i = 0; i < fieldNames.length; i++) {
            if (i > 0) sql.append(", ");
            
            sql.append(fieldNames[i]).append(" ").append(fieldTypes[i]);
            if (isTrue(isPrimaryKey[i])) {
                sql.append(" PRIMARY KEY");
            }
        }
        sql.append(")");
        
        runUpdate(sql.toString());
    }
    
    /**
     * Executes UPDATE, INSERT, DELETE, or DDL statements
     * 
     * @param query SQL query to execute
     * @return true if successful, false otherwise
     */
    public boolean runUpdate(String query) {
        try (Connection conn = connectDB();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(query);
            System.out.println("✓ Query executed successfully");
            LOGGER.info("Query executed: " + query);
            return true;
            
        } catch (SQLException e) {
            LOGGER.severe("Error executing update: " + e.getMessage());
            System.err.println("Error executing query: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Executes SELECT queries
     * 
     * @param query SQL SELECT query
     * @return ResultSet containing query results
     * @throws SQLException if query execution fails
     */
    public ResultSet runQuery(String query) throws SQLException {
        Connection conn = connectDB();
        Statement stmt = conn.createStatement();
        LOGGER.info("Executing query: " + query);
        return stmt.executeQuery(query);
    }
    
    /**
     * Drops a table from the database
     * 
     * @param tableName Name of table to drop
     */
    public void deleteTable(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            System.err.println("Table name cannot be empty!");
            return;
        }
        
        String query = "DROP TABLE IF EXISTS " + tableName;
        System.out.println("Query to run: " + query);
        runUpdate(query);
    }
    
    /**
     * Displays all tables in the current database
     */
    public void showTables() {
        try (Connection conn = connectDB()) {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet tables = meta.getTables(null, "public", "%", new String[]{"TABLE"})) {
                System.out.println("\n=== Tables in database: " + this.database + " ===");
                boolean hasResults = false;
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    System.out.println("• " + tableName);
                    hasResults = true;
                }
                if (!hasResults) {
                    System.out.println("No tables found in the database.");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            LOGGER.severe("Error showing tables: " + e.getMessage());
            System.err.println("Error retrieving tables: " + e.getMessage());
        }
    }
    
    /**
     * Displays field information for a specific table
     * 
     * @param tableName Name of the table
     */
    public void showTableFields(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            System.err.println("Table name cannot be empty!");
            return;
        }
        
        try (Connection conn = connectDB()) {
            DatabaseMetaData meta = conn.getMetaData();
            
            System.out.println("\n=== Fields in table: " + tableName + " ===");
            
            // Get primary keys
            Set<String> primaryKeys = new HashSet<>();
            try (ResultSet pkRs = meta.getPrimaryKeys(null, null, tableName)) {
                while (pkRs.next()) {
                    primaryKeys.add(pkRs.getString("COLUMN_NAME"));
                }
            }
            
            // Get columns
            try (ResultSet rs = meta.getColumns(null, null, tableName, null)) {
                boolean hasResults = false;
                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    String dataType = rs.getString("TYPE_NAME");
                    int columnSize = rs.getInt("COLUMN_SIZE");
                    boolean isNullable = rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable;
                    boolean isPK = primaryKeys.contains(columnName);
                    
                    String sizeInfo = (columnSize > 0 && 
                        (dataType.equalsIgnoreCase("VARCHAR") || dataType.equalsIgnoreCase("CHAR"))) 
                        ? "(" + columnSize + ")" : "";
                    
                    System.out.printf("• %-20s %-15s %s %s%n",
                        columnName,
                        dataType + sizeInfo,
                        isPK ? "[PRIMARY KEY]" : "",
                        !isNullable ? "[NOT NULL]" : "[NULLABLE]");
                    
                    hasResults = true;
                }
                if (!hasResults) {
                    System.out.println("Table '" + tableName + "' not found or has no columns.");
                }
            }
            System.out.println();
            
        } catch (SQLException e) {
            LOGGER.severe("Error showing table fields: " + e.getMessage());
            System.err.println("Error retrieving table fields: " + e.getMessage());
        }
    }
    
    /**
     * Returns Field objects for a specific table
     * 
     * @param tableName Name of the table
     * @return Array of Field objects
     */
    public Field[] getTableFields(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            return new Field[0];
        }
        
        List<Field> fieldsList = new ArrayList<>();
        
        try (Connection conn = connectDB()) {
            DatabaseMetaData meta = conn.getMetaData();
            
            // Get primary keys
            Set<String> primaryKeys = new HashSet<>();
            try (ResultSet pkRs = meta.getPrimaryKeys(null, null, tableName)) {
                while (pkRs.next()) {
                    primaryKeys.add(pkRs.getString("COLUMN_NAME"));
                }
            }
            
            // Get columns
            try (ResultSet rs = meta.getColumns(null, null, tableName, null)) {
                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    String dataType = rs.getString("TYPE_NAME");
                    int columnSize = rs.getInt("COLUMN_SIZE");
                    boolean isNullable = rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable;
                    boolean isPK = primaryKeys.contains(columnName);
                    
                    Field field = new Field(columnName, dataType);
                    if (isPK) field.primaryKey();
                    if (!isNullable) field.notNull();
                    if (dataType.equalsIgnoreCase("VARCHAR") || dataType.equalsIgnoreCase("CHAR")) {
                        field.maxLength(columnSize);
                    }
                    
                    fieldsList.add(field);
                }
            }
            
            currentTableFields = fieldsList.toArray(new Field[0]);
            
        } catch (SQLException e) {
            LOGGER.severe("Error getting table fields: " + e.getMessage());
            currentTableFields = new Field[0];
        }
        
        return currentTableFields;
    }
    
    /**
     * Authenticates user against userscredentials table
     * 
     * @param username Username to authenticate
     * @param password Password to authenticate
     * @return true if authentication successful, false otherwise
     */
    public boolean login(String username, String password) {
        if (username == null || password == null || username.trim().isEmpty()) {
            System.out.println("✗ Username and password cannot be empty.");
            return false;
        }
        
        String sql = "SELECT username FROM userscredentials WHERE username = ? AND passw = ?";
        
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username.trim());
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String dbUsername = rs.getString("username");
                    System.out.println("✓ Welcome, " + dbUsername + "!");
                    LOGGER.info("Successful login for user: " + dbUsername);
                    return true;
                } else {
                    System.out.println("✗ Invalid username or password. Access denied.");
                    LOGGER.warning("Failed login attempt for username: " + username);
                    return false;
                }
            }
            
        } catch (SQLException e) {
            LOGGER.severe("Login error: " + e.getMessage());
            System.err.println("Login error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Displays all records from a specified table
     * Note: This is a generic method that works with any table structure
     * 
     * @param tableName Name of the table to display
     */
    public void showRecords(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            System.err.println("Table name cannot be empty!");
            return;
        }
        
        String query = "SELECT * FROM " + tableName;
        
        try (Connection conn = connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            // Print header
            System.out.println("\n=== Records in table: " + tableName + " ===");
            for (int i = 1; i <= columnCount; i++) {
                System.out.printf("%-15s ", metaData.getColumnName(i).toUpperCase());
            }
            System.out.println("\n" + "=".repeat(columnCount * 16));
            
            // Print data
            boolean hasResults = false;
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    Object value = rs.getObject(i);
                    System.out.printf("%-15s ", value != null ? value.toString() : "NULL");
                }
                System.out.println();
                hasResults = true;
            }
            
            if (!hasResults) {
                System.out.println("No records found in table '" + tableName + "'.");
            }
            System.out.println();
            
        } catch (SQLException e) {
            LOGGER.severe("Error showing records: " + e.getMessage());
            System.err.println("Error retrieving records: " + e.getMessage());
        }
    }
    
    /**
     * Closes the database connection
     */
    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                LOGGER.info("Database connection closed");
            } catch (SQLException e) {
                LOGGER.warning("Error closing connection: " + e.getMessage());
            }
        }
    }
}
