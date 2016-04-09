package CS7081.Spring2016.Group01.FinalProject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;

import DatabaseUtilities.DatabaseInterface;
import DatabaseUtilities.Utilities;
import Jama.Matrix;

public class Main {
	private static DatabaseInterface db = new DatabaseInterface();

	public static void main(String[] args) {

		Main myClass = new Main();
		try {
			
			//myClass.Go();
			//myClass.testPageRankPowerMethod();
			
			HashMap<String, Integer> itemHash = myClass.loadItemHashTable();
			System.out.println(itemHash.size() + " items to be processed.");
			Set<String> enumKey = itemHash.keySet();
			for (String item : enumKey) {
			    int itemID = itemHash.get(item);
			    //System.out.println("Item = " + item + ", " + "ItemID = " + itemID);
			}			
			HashMap<Integer, String> clientHash = myClass.loadClientHashTable();
			System.out.println(clientHash.size() + " clients to be processed.");
			
			
		} catch (Exception ex) {
			System.out.println("main(): " + ex.getLocalizedMessage());
		}
	}

	public void Go() throws Exception {
		try {

			Connection con = db.Connect("il-server-001.uccc.uc.edu\\mssqlserver2012", "7081FinalProjectLogin", "P@ssword");
			System.out.println("Connected to DB");
			
			// Test the database connection
			String lastName = Utilities.ReadStringFromQuery(con, "SELECT * FROM tClientName ORDER BY ClientNameID ASC", "lastName", true);
			System.out.println("Test read from tClientName : Last Name = " + lastName);
			String firstName = Utilities.ReadStringFromQuery(con, "SELECT * FROM tClientName ORDER BY ClientNameID ASC", "firstName", true);
			System.out.println("Test read from tClientName : First Name = " + firstName);
			
		} catch (Exception ex) {System.out.println("Main.Go(): " + ex.getLocalizedMessage());}
	}
	private void testPageRankPowerMethod() {
		// Test JAMA matrix stuff
//		double data[][] = {{1.,2.,3},{4.,5.,6.},{7.,8.,10.}};
/*			double data[][] = {{1,0,0,0},
				           {1,0,0,0},
				           {1,0,0,0},
				           {1,0,0,0}};
*/
		
		// See http://introcs.cs.princeton.edu/java/16pagerank/ for this data and the expected result of the Power Method convergence   
/*		double data[][] = { {.02, .92, .02, .02, .02},		
							{.02, .02, .38, .38, .20},
							{.02, .02, .02, .92, .02},
							{.92, .02, .02, .02, .02},
							{.47, .02, .47, .02, .02}				
		};
*/
		// See http://www.ams.org/samplings/feature-column/fcarc-pagerank
//								0     1     2     3     4     5     6     7     		
		double data[][] = {{	0,	  0,	0,	  0,	0,	  0, 1./3, 	  0       },
						   {   .5,	  0,   .5, 1./3,    0,	  0,	0,    0       },
						   {   .5,	  0,    0,    0.,   0,    0,	0,    0       },
						   {    0,	  1,    0,    0.,   0,	  0,	0,    0       },
						   {    0,	  0,   .5, 1./3.,   0, 	  0, 1./3,    0       },
						   {    0,	  0,    0, 1./3., 1./3,	  0,	0,   .5       },
						   {    0,	  0,    0,    0., 1./3,	  0,	0,   .5       },
						   {    0,	  0,    0,    0., 1./3,	  1, 1./3,    0       },
		};

		try {
			Jama.Matrix m = new Jama.Matrix(data);
	
			// Power Method for convergence to the rankings
			double[][] move = new double[data.length][1]; 
		    for (int i = 0; i < move.length; i++) {
		    	move[i][0] = 0; 		//1./move.length;
		    }
	    	move[0][0] = 1;
		    Matrix moveMatrix = new Matrix(move);
		    m.print(6, 4);
		    System.out.println("-----------------------");
		    // Power Method. Will converge to the vector of probabilities. 
			for (int i = 0; i < 61	; i++) {
				moveMatrix.print(6, 4);		// column width , # digits after decimal
				moveMatrix = m.times(moveMatrix);	//   moveMatrix.times(m);
			}
		} catch (Exception ex) {
			System.out.println(ex.getLocalizedMessage());
		}
	}
	
	/**
	 * Load the items from the aggregate view into a hash map so we can build the matrix
	 * @return The hash map of items
	 */
	private HashMap<String, Integer> loadItemHashTable() {
		Connection con = null;
		HashMap<String, Integer> itemHash = new HashMap<String, Integer>();		// <key, value> are the parameters
		try {
			con = db.Connect("il-server-001.uccc.uc.edu\\mssqlserver2012", "7081FinalProjectLogin", "P@ssword");
			System.out.println("loadItemHashTable(): Connected to DB");
			String item;
			int itemID;
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM vAggregateSalesByCustomerAndItem";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {		
				item = rs.getString("myItem");
				itemID = rs.getInt("myItemID");
				itemHash.put(item,  itemID);
			}
		} catch (Exception ex) {
			System.out.println("loadItemHashTable(): " + ex.getLocalizedMessage());
		} finally {
			try {con.close();} catch (Exception ex) {}
		}
		return itemHash;
	}
	/**
	 * Load the items from the aggregate view into a hash map so we can build the matrix
	 * @return The hash map of items
	 */
	private HashMap<Integer, String> loadClientHashTable() {
		Connection con = null;
		HashMap<Integer, String> clientHash = new HashMap<Integer, String>();		// <key, value> are the parameters
		try {
			con = db.Connect("il-server-001.uccc.uc.edu\\mssqlserver2012", "7081FinalProjectLogin", "P@ssword");
			System.out.println("loadItemHashTable(): Connected to DB");
			String name;
			int clientNameID;
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM vAggregateSalesByCustomerAndItem";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {		
				name = rs.getString("name");
				clientNameID = rs.getInt("clientNameID");
				clientHash.put(clientNameID, name);
			}
		} catch (Exception ex) {
			System.out.println("loadClientHashTable(): " + ex.getLocalizedMessage());
		} finally {
			try {con.close();} catch (Exception ex) {}
		}
		return clientHash;
	}
	
	
}
