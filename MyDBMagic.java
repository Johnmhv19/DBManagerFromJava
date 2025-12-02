import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
//Connection with database + table creation + Insert records + See Tables's records + Manage Records

public class MyDBMagic {
	private final String port;
	private final String DB;
	private final String DBUName;
	private final String DBPW; 
	private Connection c = null;
	private Field[] currentTableFields;
	//constructors
	MyDBMagic(){
		port= "5433";
		DB ="postgres";
		DBUName = "postgres";
		DBPW= "1234";
		
	}
	MyDBMagic(String Port, String DB, String DBUName, String DBPW){
		this.port= Port;
		this.DB =DB;
		this.DBUName = DBUName;
		this.DBPW=DBPW;
		
	}
	
	public Connection ConnectDB() {
		
		String link = "jdbc:postgresql://localhost:"+this.port+"/"+this.DB;
		
		try {
	         Class.forName("org.postgresql.Driver");
	         c = DriverManager.getConnection(link,this.DBUName,this.DBPW);
	         System.out.println("✓ Connected successfully to: " + link); 
	      } catch (Exception e) {
	         e.printStackTrace();
	         System.err.println(e.getClass().getName()+": "+e.getMessage());
	         System.exit(0);
	      }
	      return c;
		
	}
	
	public String getDB() {
		return this.DB;
	}
	private boolean isTrue(String value) {
		 return "true".equalsIgnoreCase(value) || "1".equals(value);
	 }
	
	public void CreateTable() {
		Scanner mySC = new Scanner (System.in);
		boolean isTherePkey= false;
		System.out.print("Enter table name: ");
		String tableName = mySC.nextLine();
		
		System.out.print("Enter number of fields: ");
		 int fieldCount = mySC.nextInt();
		 mySC.nextLine(); // consume newline
		 
		 String[][] tableData = new String[3][fieldCount];
		 
		 for (int i = 0; i < fieldCount; i++) {
			 System.out.println("\n--- Field " + (i + 1) + " ---");
			 System.out.print("Field name: ");
			 tableData[0][i] = mySC.nextLine();
			 System.out.print("Field type (INTEGER, VARCHAR, TEXT, etc.): ");
			 tableData[1][i] = mySC.nextLine().toUpperCase();
			 if(!isTherePkey) {
				 System.out.print("Is primary key? (true/false or 1/0): ");
				 
				 if(isTrue(mySC.nextLine())) { tableData[2][i] = "true"; }
				 isTherePkey = true;			 		
			 }
			 else {tableData[2][i]="false";}
			 
		 }
		 
		 System.out.println("\n--- Table Summary ---");
		 System.out.println("Table: " + tableName);
		 for (int i = 0; i < fieldCount; i++) {
		        System.out.printf("Field %d: %s %s %s%n", 
		        i + 1, 
		        tableData[0][i], 
		        tableData[1][i], 
		        isTrue(tableData[2][i]) ? "(PRIMARY KEY)" : "" );
		  }
		StringBuilder sql = new StringBuilder();
		
		if (tableData.length != 3) {
			throw new IllegalArgumentException("Array must have exactly 3 rows: [names][types][isPrimaryKey]");
        }
		
		String[] fieldNames = tableData[0];
		String[] fieldTypes = tableData[1];
		String[] isPrimaryKey = tableData[2];
		
		//Making sure the array is well filled
		if (fieldNames.length != fieldTypes.length || fieldNames.length != isPrimaryKey.length) {
			throw new IllegalArgumentException("All arrays must have the same length");
		}
		
	//Creating my query for the table
		sql.append("CREATE TABLE ").append(tableName).append(" (");
		
		for (int i = 0; i < fieldNames.length; i++) {
			 if (i > 0) sql.append(", ");
			 
			 sql.append(fieldNames[i]).append(" ").append(fieldTypes[i]);
			 if ("true".equalsIgnoreCase(isPrimaryKey[i]) || "1".equals(isPrimaryKey[i])) {
				 sql.append(" PRIMARY KEY");
			 }
		}
		sql.append(")");
	//query created
		
		//Lets run the query with the other method
		String query = sql.toString();
		RunUpdate(query);
		
	}
	
	//Method to run queries, that doesnt return anything, given the String
	public void RunUpdate(String query) {
		 Statement stmt = null;
		 try {
			 Connection c= ConnectDB();
	         stmt = c.createStatement();
	         stmt.executeUpdate(query);
	         System.out.println("Query ran successfully");
	         System.out.println("");
		 } catch (Exception e) {
			 
			 e.printStackTrace();
			 System.err.println(e.getClass().getName()+": "+e.getMessage());
			 System.exit(0);
       }
		 
	}
	
	public ResultSet RunQuery(String query) throws SQLException {
		Connection c= ConnectDB();
		Statement stmt = c.createStatement();       
	    return stmt.executeQuery(query);	
	}
	
	//delete table
	public void DeleteTB(String TBName) {
		//MyDBMagic DB1 = new MyDBMagic("5433", "postgres","postgres", "1234");

		String query = "DROP TABLE "+ TBName +";";
		System.out.println("Query to run:"+query);
		RunUpdate(query);
	}
	//Check if table exists
	
		
	public void showTBs() {
	    String schema = "public";
	    String catalog = null; // not used for PostgreSQL
	    String tableNamePattern = "%";
	    String[] types = { "TABLE" };
	    MyDBMagic DB1 = new MyDBMagic("5433", "postgres","postgres", "1234");

	    try (Connection conn = ConnectDB()) {
	        DatabaseMetaData meta = conn.getMetaData();
	        try (ResultSet tables = meta.getTables(catalog, schema, tableNamePattern, types)) {
	        	System.out.println(" ");
	        	System.out.println("Tables in database:"+DB1.getDB());
	            while (tables.next()) {
	                String tableName = tables.getString("TABLE_NAME");
	                System.out.println(tableName);
	            }
	            System.out.println("");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	//Check fields
	public void ShowTBFields(String tableName) {
		//MyDBMagic DB1 = new MyDBMagic("5433", "postgres","postgres", "1234");
		MyDBMagic DB1 = new MyDBMagic();
		
	    try (Connection conn = ConnectDB()) {
	        DatabaseMetaData meta = conn.getMetaData();
	        //schema/catalog are null as defaults
	        System.out.println(" ");
	        System.out.println("Fields info on table:" + tableName );
	        
	        try (ResultSet rs = meta.getColumns(null, null, tableName, null)) {
	            while (rs.next()) {
	                String columnName = rs.getString("COLUMN_NAME");
	                String dataType   = rs.getString("TYPE_NAME");
	                //int    size         = rs.getInt("COLUMN_SIZE");
	                System.out.println("Name:" +columnName + "    type:" + dataType);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    System.out.println(" ");
	}
	
	public Field[] ReturnTBFields(String tableName) {
		 ArrayList<Field> fieldsList = new ArrayList<>();
		 try (Connection conn = ConnectDB()) {
			 DatabaseMetaData meta = conn.getMetaData();
			 System.out.println(" ");
			 System.out.println("Fields info on table: " + tableName);
			 
			 Set<String> primaryKeys = new HashSet<>();
			 try (ResultSet pkRs = meta.getPrimaryKeys(null, null, tableName)) {
				 while (pkRs.next()) {
					 primaryKeys.add(pkRs.getString("COLUMN_NAME"));
				 }
			 }
			 
			 try (ResultSet rs = meta.getColumns(null, null, tableName, null)) {
				 while (rs.next()) {
					 String columnName = rs.getString("COLUMN_NAME");
					 String dataType = rs.getString("TYPE_NAME");
					 int columnSize = rs.getInt("COLUMN_SIZE");
					 boolean isNullable = rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable;
					 boolean isPK = primaryKeys.contains(columnName);
					 
					// Create Field object
					 Field field = new Field(columnName, dataType);
					 if (isPK) {
						 field.primaryKey();
					 }
					 if (!isNullable) {
						 field.notNull();
					 }
					 if (dataType.equalsIgnoreCase("VARCHAR") || dataType.equalsIgnoreCase("CHAR")) {
						 field.maxLength(columnSize);
					 }
					 
					 fieldsList.add(field);
					 
					// Display field info
					 System.out.printf("Name: %-15s Type: %-15s %s %s%n", 
							  columnName, 
							  dataType + (columnSize > 0 && (dataType.equalsIgnoreCase("VARCHAR") ||
									  dataType.equalsIgnoreCase("CHAR")) ? 
											  "(" + columnSize + ")" : ""),
							  isPK ? "[PRIMARY KEY]" : "",
									  !isNullable ? "[NOT NULL]" : "[NULLABLE]"
							 );
					 
				 }
			 }
			 currentTableFields = fieldsList.toArray(new Field[0]);
			 
		 } catch (SQLException e) {
			 e.printStackTrace();
			 currentTableFields = new Field[0]; // Empty array on error
		 }
		 System.out.println(" ");
		 
		 return currentTableFields;
	}
	
	
	
	//Show records [to be completed]
	public void ShowRecords(String tableName) throws SQLException{
		
		//Print the table fields
		
		String query = "SELECT * FROM "+tableName + ";";
		ResultSet rs = RunQuery(query);
		
		while ( rs.next() ) {
            int id = rs.getInt("id");
            String  name = rs.getString("name");
            int age  = rs.getInt("age");
            String  address = rs.getString("address");
            float salary = rs.getFloat("salary");
            System.out.println( "ID = " + id );
            System.out.println( "NAME = " + name );
            System.out.println( "AGE = " + age );
            System.out.println( "ADDRESS = " + address );
            System.out.println( "SALARY = " + salary );
            System.out.println();
         }
	}
	
	//LoginScreen
	public boolean login(String username, String password) {
	    String sql = "SELECT username, passw FROM userscredentials WHERE username = ? AND password = ?";
	    
	    try (Connection conn = ConnectDB();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        
	        // Set parameters to prevent SQL injection
	        pstmt.setString(1, username);
	        pstmt.setString(2, password);
	        
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                // User found with matching credentials
	                String dbUsername = rs.getString("username");
	                System.out.println("✓ Welcome, " + dbUsername + "!");
	                return true;
	            } else {
	                // No matching user found
	                System.out.println("✗ Invalid username or password. Access denied.");
	                return false;
	            }
	        }
	        
	    } catch (SQLException e) {
	        System.err.println("Login error: " + e.getMessage());
	        return false;
	    }
	}
	
	//Insert records [to be completed]
	public void InRecord( String TBName) {
		Scanner mySC = new Scanner (System.in);
		
		System.out.println("Here is the fields on the table:");
		ShowTBFields(TBName);
		boolean finish = false;
		
		
		
		//Insert the values
		while(!finish) {
			try {
				
			}catch (Exception e) {
		         System.err.println( e.getClass().getName()+": "+ e.getMessage() );
		         System.exit(0);
						
			
		}
		
		
		mySC.nextLine();
		String query = "INSERT INTO "+TBName+" (ID,NAME,AGE,ADDRESS,SALARY) "
	            + "VALUES (1, 'Paul', 32, 'California', 20000.00 );";
		
		
	}
}


}
