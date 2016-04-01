package CS7081.Spring2016.Group01.FinalProject;

import java.sql.Connection;

import DatabaseUtilities.DatabaseInterface;
import DatabaseUtilities.Utilities;

public class Main {

	public static void main(String[] args) {

		Main myClass = new Main();
		try {
			myClass.Go();
		} catch (Exception ex) {
			
		}
	
		
		
		
	}

	public void Go() throws Exception {
		try {

			DatabaseInterface db = new DatabaseInterface();
			Connection con = db.Connect("il-server-001.uccc.uc.edu\\mssqlserver2012", "7081FinalProjectLogin", "P@ssword");
			System.out.println("Connected to DB");
			
			// Test the database connection
			String lastName = Utilities.ReadStringFromQuery(con, "SELECT * FROM tClientName ORDER BY ClientNameID ASC", "lastName", true);
			System.out.println("Test read from tClientName : Last Name = " + lastName);
			String firstName = Utilities.ReadStringFromQuery(con, "SELECT * FROM tClientName ORDER BY ClientNameID ASC", "firstName", true);
			System.out.println("Test read from tClientName : First Name = " + firstName);
			
		} catch (Exception ex) {System.out.println("Main.Go(): " + ex.getLocalizedMessage());}
	}

}
