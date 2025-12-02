import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * MainDB - Main application class for the Database Management System
 * 
 * Provides a console-based menu interface for interacting with PostgreSQL databases
 * using the MyDBMagic utility class.
 * 
 * @author John Hernandez 
 * @version 2.0
 */
public class MainDB {
    
    private static final Logger LOGGER = Logger.getLogger(MainDB.class.getName());
    private final Scanner scanner = new Scanner(System.in);
    private MyDBMagic dbManager;
    
    public static void main(String[] args) {
        MainDB mainDB = new MainDB();
        try {
            mainDB.run();
        } catch (Exception e) {
            System.err.println("Application error: " + e.getMessage());
            LOGGER.severe("Application error: " + e.getMessage());
        }
    }
    
    /**
     * Main application loop - displays menu and handles user choices
     */
    public void run() {
        System.out.println("=== Welcome to MyDBMagic Database Manager ===");
        System.out.println("A simple PostgreSQL database management tool for students and educators\n");
        
        int option = 0;
        
        while (option != 9) {
            displayMenu();
            
            try {
                option = scanner.nextInt();
                scanner.nextLine(); // consume newline
                
                handleMenuChoice(option);
                
            } catch (InputMismatchException e) {
                System.err.println("Please enter a valid number!");
                scanner.nextLine(); // clear invalid input
            }
        }
        
        cleanup();
    }
    
    /**
     * Displays the main menu options
     */
    private void displayMenu() {
        System.out.println("\n=== Database Management Menu ===");
        System.out.println("(1) Connect to database");
        System.out.println("(2) Create table");
        System.out.println("(3) Delete table");
        System.out.println("(4) Show table structure");
        System.out.println("(5) Show all tables");
        System.out.println("(6) Show table records");
        System.out.println("(7) Test login system");
        System.out.println("(9) Exit");
        System.out.print("Choose option: ");
    }
    
    /**
     * Handles the user's menu choice
     * 
     * @param option The selected menu option
     */
    private void handleMenuChoice(int option) {
        switch (option) {
            case 1:
                connectToDatabase();
                break;
            case 2:
                if (checkConnection()) {
                    dbManager.createTable();
                }
                break;
            case 3:
                if (checkConnection()) {
                    deleteTable();
                }
                break;
            case 4:
                if (checkConnection()) {
                    showTableStructure();
                }
                break;
            case 5:
                if (checkConnection()) {
                    dbManager.showTables();
                }
                break;
            case 6:
                if (checkConnection()) {
                    showTableRecords();
                }
                break;
            case 7:
                if (checkConnection()) {
                    testLogin();
                }
                break;
            case 9:
                System.out.println("Thank you for using MyDBMagic! Goodbye!");
                break;
            default:
                System.out.println("Invalid option. Please choose a number from the menu.");
        }
    }
    
    /**
     * Handles database connection setup
     */
    private void connectToDatabase() {
        System.out.println("\n=== Database Connection Setup ===");
        System.out.print("Use default connection settings? (y/n): ");
        String choice = scanner.nextLine().trim().toLowerCase();
        
        try {
            if (choice.equals("n") || choice.equals("no")) {
                setupCustomConnection();
            } else {
                setupDefaultConnection();
            }
            
            // Test the connection
            dbManager.connectDB();
            System.out.println("✓ Database connection established successfully!");
            
        } catch (Exception e) {
            System.err.println("✗ Failed to connect to database: " + e.getMessage());
            System.out.println("Please check your connection settings and try again.");
            dbManager = null;
        }
    }
    
    /**
     * Sets up custom database connection parameters
     */
    private void setupCustomConnection() {
        System.out.println("Enter your database connection details:");
        
        System.out.print("Database port (default 5432): ");
        String port = scanner.nextLine().trim();
        if (port.isEmpty()) port = "5432";
        
        System.out.print("Database name (default postgres): ");
        String database = scanner.nextLine().trim();
        if (database.isEmpty()) database = "postgres";
        
        System.out.print("Username (default postgres): ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) username = "postgres";
        
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        dbManager = new MyDBMagic(port, database, username, password);
        System.out.println("Custom connection configured.");
    }
    
    /**
     * Sets up default database connection
     */
    private void setupDefaultConnection() {
        dbManager = new MyDBMagic();
        System.out.println("Default connection configured (localhost:5432, database: postgres).");
    }
    
    /**
     * Handles table deletion
     */
    private void deleteTable() {
        System.out.println("\n=== Delete Table ===");
        dbManager.showTables();
        
        System.out.print("Enter the name of the table to delete: ");
        String tableName = scanner.nextLine().trim();
        
        if (tableName.isEmpty()) {
            System.err.println("Table name cannot be empty!");
            return;
        }
        
        System.out.print("Are you sure you want to delete table '" + tableName + "'? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (confirm.equals("y") || confirm.equals("yes")) {
            dbManager.deleteTable(tableName);
        } else {
            System.out.println("Table deletion cancelled.");
        }
    }
    
    /**
     * Shows the structure of a selected table
     */
    private void showTableStructure() {
        System.out.println("\n=== Table Structure ===");
        dbManager.showTables();
        
        System.out.print("Enter table name to view structure: ");
        String tableName = scanner.nextLine().trim();
        
        if (!tableName.isEmpty()) {
            dbManager.showTableFields(tableName);
            
            // Also get Field objects for demonstration
            Field[] fields = dbManager.getTableFields(tableName);
            if (fields.length > 0) {
                System.out.println("Field objects created: " + fields.length + " fields");
            }
        } else {
            System.err.println("Table name cannot be empty!");
        }
    }
    
    /**
     * Shows records from a selected table
     */
    private void showTableRecords() {
        System.out.println("\n=== Table Records ===");
        dbManager.showTables();
        
        System.out.print("Enter table name to view records: ");
        String tableName = scanner.nextLine().trim();
        
        if (!tableName.isEmpty()) {
            dbManager.showRecords(tableName);
        } else {
            System.err.println("Table name cannot be empty!");
        }
    }
    
    /**
     * Tests the login system functionality
     */
    private void testLogin() {
        System.out.println("\n=== Login System Test ===");
        System.out.println("Note: This requires a 'userscredentials' table with 'username' and 'passw' columns");
        
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        boolean loginSuccess = dbManager.login(username, password);
        
        if (loginSuccess) {
            System.out.println("Login test completed successfully!");
        } else {
            System.out.println("Login test failed. Check your credentials or table structure.");
        }
    }
    
    /**
     * Checks if database connection is established
     * 
     * @return true if connected, false otherwise
     */
    private boolean checkConnection() {
        if (dbManager == null) {
            System.err.println("⚠ Please connect to database first (option 1)");
            return false;
        }
        return true;
    }
    
    /**
     * Cleanup resources before application exit
     */
    private void cleanup() {
        if (scanner != null) {
            scanner.close();
        }
        if (dbManager != null) {
            dbManager.close();
        }
        LOGGER.info("Application shutdown completed");
    }
}
