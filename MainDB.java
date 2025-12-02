import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import java.util.Scanner;

public class MainDB {
	private Scanner mySC = new Scanner (System.in);
	private MyDBMagic DBmanager =new MyDBMagic();
	public static void main(String[] args) {
		MainDB mainDB = new MainDB();
		mainDB.run();
	}
	public void run() {
		
		int option=0;
				
		while (option !=9) {
			System.out.println("\n=== Database Management Menu ===");
			System.out.println("(1) Test connection");
			System.out.println("(2) Create table");
			System.out.println("(3) Delete table");
			System.out.println("(4) Insert record");
			System.out.println("(5) Show tables");
			System.out.println("(6) Show table fields");
			System.out.println("(9) Exit");
			System.out.print("Choose option: ");
			option = mySC.nextInt();
			mySC.nextLine();
			
			switch(option){
			case 1:
				DBInfo();
				break;
			case 2:
				if (DBmanager != null) {
					DBmanager.CreateTable(); // Call on instance, not class
					} else {
					System.out.println("Please connect to database first (option 1)");
					}
					
				break;
			case 3:
				if (DBmanager != null) {
					System.out.print("Enter table's name you want to delete:");
					String TBName=mySC.nextLine();
					DBmanager.DeleteTB(TBName);
					} else {
					System.out.println("Please connect to database first (option 1)");
					}
				
				break;
				
			case 4:
				if (DBmanager != null) {
					System.out.print("Enter table's name where you want to insert the record");
					String TB = mySC.nextLine();
					DBmanager.ShowTBFields(TB);
					} else {
					System.out.println("Please connect to database first (option 1)");
					}
				
				break;
			
			case 5:
				if (DBmanager != null) {DBmanager.showTBs();}
				else {
					System.out.println("Please connect to database first (option 1)");
					}
				break;
				
			case 6:
				if (DBmanager != null) {
					System.out.print("Enter table name: ");
					String tableName = mySC.nextLine();
					Field[] myfields= DBmanager.ReturnTBFields(tableName);
					//DBmanager.ShowTBFields(tableName);
					} else {
					System.out.println("Please connect to database first (option 1)");
					}
				break;
			case 9:
				System.out.println("BYE BYE");
				break;
			}
		}
		
		mySC.close();

	}

	public void DBInfo() {
		System.out.println("Press (y) for default connection or (n) for entering connections values");
		String connectChoice= mySC.nextLine();
		
		if(connectChoice.equals("n") || connectChoice.toLowerCase().equals("no")) {
			String port, DB,User,PassW;
			//We need an object with the values of DB: port, DB,UName,PW
			System.out.println("We need some info to connect to your DB");
			System.out.print("Insert the DB port (ex. 5433):");
			port=mySC.nextLine();
			
			System.out.print("Insert the DB name(ex. postgres):");
			DB=mySC.nextLine();
			
			System.out.print("Insert the DB username (ex. postgres):");
			User=mySC.nextLine();
			
			System.out.print("Insert the DB User's password:");
			PassW=mySC.nextLine();
			
			//DBmanager DB1 = new MyDBMagic(port, DB, User, PassW);
			DBmanager.ConnectDB();
			
		}else {
			MyDBMagic DB1 = new MyDBMagic("5433", "postgres","postgres", "1234");
			DBmanager.ConnectDB();
			
		}
		
	}
	
}
