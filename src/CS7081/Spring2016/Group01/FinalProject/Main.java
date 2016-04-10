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
			

			
			HashMap<Integer,Integer> itemHash = myClass.loadItemHashTable();
			System.out.println(itemHash.size() + " items to be processed.");
			HashMap<Integer, Integer> clientHash = myClass.loadClientHashTable();
			System.out.println(clientHash.size() + " clients to be processed.");
			
			// We need a matrix. Rows are Items, columns are Clients
			int clientCount = clientHash.size();
			int itemCount = itemHash.size();
			
			int dimension = itemCount + clientCount;
			double m[][] = myClass.buildRandomWalkMatrix(clientCount, itemCount);
			Matrix myMatrix = new Matrix(m);
			myMatrix.print(5,3);
			// Now we need a vector that will iterate over our matrix
			double[][] rank = new double[dimension][1];	// This is a vector: 1 column, n rows. It will eventually contain the rankings of the rows.
		    for (int i = 0; i < dimension; i++) {rank[i][0] = 1./dimension;}
		    Matrix rankVector = new Matrix(rank);
		    // Power Method. Will converge to the vector of rankings. 
			for (int i = 0; i < 21; i++) {							// # of iterations is arbitrary. 21 seems sufficient
				//rankVector.print(6, 4);							// column width , # digits after decimal
				rankVector = myMatrix.times(rankVector);			// columns in A must equal rows in B
			}
			System.out.println("Ranking:");
			rankVector.print(8, 5);
			
			// Just for fun, sum the rankings to see if we get 1.
			double sum = 0;
			for (int i = 0; i < dimension; i++) {
				sum += rankVector.get(itemCount, 0);
			}
			System.out.printf("\n Sum of rankings = %8.6f", sum);
			
			
			/*
			double salesData[][] = new double[itemCount][clientCount];
			// Now we populate the matrix. The ordering will mirror our hash tables. 
			int recordsProcessed = myClass.loadMatrix(salesData, itemHash, clientHash);
			System.out.println(recordsProcessed + " records procesessed into data matrix");
//			Debugging
			Set<Integer> enumKey = itemHash.keySet();
			for (Integer itemID : enumKey) {
			    int rowIdx = itemHash.get(itemID);
			    System.out.println("Row Index = " + rowIdx + ", " + "ItemID = " + itemID);
			}			
			enumKey = clientHash.keySet();
			for (Integer clientID : enumKey) {
			    int columnIdx = clientHash.get(clientID);
			    System.out.println("Column Index = " + columnIdx + ", " + "ClientID = " + clientID);
			}
			
			// Now we need a vector that will iterate over our matrix
			double[][] rank = new double[dimension][1];	// This is a vector: 1 column, n rows. It will eventually contain the rankings of the rows.
		    for (int i = 0; i < dimension; i++) {rank[i][0] = 1./dimension;}
		    Matrix rankVector = new Matrix(rank);
		    // The sales data must be normalized: each column must add up to one or zero
		    int saleCountByClient = 0;
		    for (int i = 0; i < clientCount; i++) {
			    saleCountByClient = 0;
		    	for (int j = 0; j < itemCount; j++) {
				    saleCountByClient += salesData[j][i];
		    	}
		    	// Normalize the column
		    	if (saleCountByClient > 0) {
			    	for (int k = 0; k < itemCount; k++) {
					    salesData[k][i] /= saleCountByClient;
			    	}		    		
		    	}
		    }
		    // Put the data array into a Matrix object
		    Matrix sales = new Matrix(salesData);
		    sales.print(5,4);
			rankVector.print(6, 4);		// column width , # digits after decimal
		    // Power Method. Will converge to the vector of rankings. 
			for (int i = 0; i < 11	; i++) {
				//rankVector.print(6, 4);		// column width , # digits after decimal
				rankVector = sales.times(rankVector);			// columns in A must equal rows in B
			}
			System.out.println("Ranking of Items using Client Votes:");
			System.out.println("Item ID \t Rank");
			enumKey = itemHash.keySet();
			for (Integer itemID : enumKey) {
			    int rowIdx = itemHash.get(itemID);
			    //System.out.println("Row Index = " + rowIdx + ", " + "ItemID = " + itemID);
				System.out.printf("%d \t\t %4.3f\n", itemID, rankVector.get(rowIdx, 0));		// column width , # digits after decimal
			}
			*/
		} catch (Exception ex) {
			System.out.println("main(): " + ex.getLocalizedMessage());
		}
		
	}

	private double[][] buildRandomWalkMatrix(int clientCount, int itemCount) {
		// This only works if every client has purchased every item at least once. It's just easier to process the data in the tables. The ranking algo doesn't change. 
		int dimension = clientCount + itemCount;
		double m[][] = new double[dimension][dimension];
		Connection con = null;
		try {
			con = db.Connect("il-server-001.uccc.uc.edu\\mssqlserver2012", "7081FinalProjectLogin", "P@ssword");
			Statement stmt = con.createStatement();
			// left side of the matrix -- Items walking to customers
			String sql = "SELECT * FROM vProbabilityOfItemWalkingToCustomer" + " ORDER BY ItemID, ClientNameID";
			ResultSet rs = stmt.executeQuery(sql);
			for (int col = 0; col < clientCount; col++) {
				for (int row = 0; row < itemCount; row++) { m[row][col] = 0; }
				for (int row = itemCount; row < dimension; row++) {
					rs.next();
					int countOfItemID = rs.getInt("CountOfItemID");
					int totalSalesForThisItemID = rs.getInt("TotalSalesForThisItemID");
					int clientNameID = rs.getInt("ClientNameID");
					int itemID = rs.getInt("ItemID");
					m[row][col] = ((double)countOfItemID) / totalSalesForThisItemID;
				}
			}
			con.close();
			con = db.Connect("il-server-001.uccc.uc.edu\\mssqlserver2012", "7081FinalProjectLogin", "P@ssword");
			// right side of the matrix -- customers walking to items
			sql = "SELECT * FROM vProbabilityOfCustomerWalkingToItem" + " ORDER BY ClientNameID, ItemID";
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			for (int col = clientCount; col < dimension; col++) {
				for (int row = 0; row < itemCount; row++) {
					rs.next();
					m[row][col] = ((double)rs.getInt("CountOfItemID")) / rs.getInt("CountOfSaleDetailID");
				}
				for (int row = itemCount; row < dimension; row++) { m[row][col] = 0; }
			}
		
		} catch (Exception ex) {
			System.out.println("buildRandomWalkMatrix(): " + ex.getLocalizedMessage());
		}
		
		return m;
	}
	
	
	private int loadMatrix(double myData[][], HashMap<Integer,Integer> itemHash, HashMap<Integer,Integer> clientHash) {
		Connection con = null;
		int recordsProcessed = 0;
		try {
			con = db.Connect("il-server-001.uccc.uc.edu\\mssqlserver2012", "7081FinalProjectLogin", "P@ssword");
			//System.out.println("loadItemHashTable(): Connected to DB");
			Statement stmt = con.createStatement();
			String sql = getQuery();
			ResultSet rs = stmt.executeQuery(sql);
			int clientID = 0, itemID = 0, countOfItem = 0, rowIdx = 0, columnIdx = 0;
			while (rs.next()) {
				clientID = rs.getInt("myClientNameID");
				itemID = rs.getInt("myItemID");
				countOfItem = rs.getInt("CountOfItem");
				// Look up the row and column corresponding to this client and item
				rowIdx = itemHash.get(itemID);
				columnIdx = clientHash.get(clientID);
				myData[rowIdx][columnIdx] = countOfItem;
				recordsProcessed++;
			}
		} catch (Exception ex) {
			System.out.println("loadMatrix(): " + ex.getLocalizedMessage());
		} finally {
			try { con.close(); } catch(Exception ex) {}
		}
		return recordsProcessed;
	}
	
	
	/**
	 * Load the items from the aggregate view into a hash map so we can build the matrix
	 * @return The hash map of items. The key is the ItemID from the database, the value is the row index in our matrix
	 */
	private HashMap<Integer,Integer> loadItemHashTable() {
		Connection con = null;
		HashMap<Integer,Integer> itemHash = new HashMap<Integer,Integer>();		// <key, value> are the parameters
		try {
			con = db.Connect("il-server-001.uccc.uc.edu\\mssqlserver2012", "7081FinalProjectLogin", "P@ssword");
			//System.out.println("loadItemHashTable(): Connected to DB");
			//String item;
			int itemID;
			Statement stmt = con.createStatement();
			String sql = getQuery();
			ResultSet rs = stmt.executeQuery(sql);
			int rowIdx = 0;
			while (rs.next()) {		
				//item = rs.getString("myItem");
				itemID = rs.getInt("myItemID");
				// If the itemID is not in the hash map, add it now
				Object foo = itemHash.get(itemID);
				if (foo == null) {
					itemHash.put(itemID, rowIdx);
					rowIdx++;
				}
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
	 * @return The hash map of client IDs and indices into the data matrix.  The key is the clientID from the database, the value is the column index in our matrix
	 */
	private HashMap<Integer, Integer> loadClientHashTable() {
		Connection con = null;
		HashMap<Integer, Integer> clientHash = new HashMap<Integer, Integer>();		// <key, value> are the parameters
		try {
			con = db.Connect("il-server-001.uccc.uc.edu\\mssqlserver2012", "7081FinalProjectLogin", "P@ssword");
			//System.out.println("loadItemHashTable(): Connected to DB");
//			String name;
			int clientNameID;
			Statement stmt = con.createStatement();
			String sql = getQuery();
			ResultSet rs = stmt.executeQuery(sql);
			int columnIdx = 0;
			while (rs.next()) {		
				//name = rs.getString("name");
				clientNameID = rs.getInt("myClientNameID");
				// If the clientNameID is not in the hash map, add it now
				Object foo = clientHash.get(clientNameID);
				if (foo == null) {
					clientHash.put(clientNameID, columnIdx);
					columnIdx++;
				}
			}
		} catch (Exception ex) {
			System.out.println("loadClientHashTable(): " + ex.getLocalizedMessage());
		} finally {
			try {con.close();} catch (Exception ex) {}
		}
		return clientHash;
	}
	
	private static String getQuery() {
		return "SELECT * FROM vAggregateSalesByCustomerAndItem" + " ORDER BY myClientNameID, myItemID";
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
	
			// Power Method for convergence to the rankings.
			double[][] move = new double[data.length][1];	// This is a vector: 1 column, n rows. It will eventually contain the rankings of the rows.
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
	public void testDatabaseConnection() throws Exception {
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
}
