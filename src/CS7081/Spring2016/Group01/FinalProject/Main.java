/****************************************************************
 * CS 7081 Group Project                                        *
 * Spring, 2016                                                 *
 * Compute Page Rank                                            *
 * Christopher Broderick, Rohit Dureja, Akash Mohapatra,        * 
 * Bill Nicholson, Asrith Singa Reddy.                          *
 ****************************************************************/
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
	
	// (1-Damping Factor) is the likelihood that random page will not be chosen from the entire universe of nodes.
	//.85 is the assumed Google default. Use 1.0 for no damping, 0. for total damping (makes no sense);
	private static double dampingFactor = .15;
	private static boolean verbose = false;
	public static void main(String[] args) {

		Main myClass = new Main();
		try {

			//myClass.Go();
			//myClass.testPageRankPowerMethod();

			//HashMap<Integer,Integer> itemHash = myClass.loadItemHashTable();
			//System.out.println(itemHash.size() + " items to be processed.");
			//HashMap<Integer, Integer> clientHash = myClass.loadClientHashTable();
			//System.out.println(clientHash.size() + " clients to be processed.");
			System.out.println("Damping Factor = " + dampingFactor);
			// We need a transition matrix. Top 1/2 is clients ranking items, bottom 1/2 is items ranking clients
			int clientCount = myClass.countClientNamesThatBoughtSomething();
			int itemCount = myClass.countItemsThatWereBought();
			System.out.println(itemCount + " items to be processed.");
			System.out.println(clientCount + " clients to be processed.");

			int dimension = itemCount + clientCount;
			double m[][] = myClass.buildRandomWalkMatrix(clientCount, itemCount);
//			double m[][] = myClass.buildRandomWalkMatrix_test01();
//			double m[][] = myClass.buildRandomWalkMatrix_test02();
//			double m[][] = myClass.buildRandomWalkMatrix_test03();
			
			Matrix myMatrix = new Matrix(m);
			if (verbose) {myMatrix.print(10,8);}
			/*
			for (int i = itemCount; i < myMatrix.getRowDimension(); i++) {
				System.out.println();
				for (int ii = 0; ii < clientCount; ii++) {
					System.out.printf("%6.5f ", myMatrix.get(i, ii)*10);
				}
			}
			*/
			//myMatrix.print(20,10);
			//myClass.printMatrix(myMatrix, dimension);
			// Now we need a ranking vector that will iterate over our matrix
			double[][] rank = new double[dimension][1];	// This is a vector: 1 column, n rows. It will eventually contain the rankings of the rows.
		    for (int i = 0; i < dimension; i++) {rank[i][0] = 1./dimension;}
//		    for (int i = 0; i < dimension; i++) {rank[i][0] = 0;} rank[0][0] = 1.;						// Doesn't work
//		    for (int i = 0; i < dimension; i++) {rank[i][0] = 0;} rank[0][0] = .5;	rank[1][0] = .5;	// Doesn't work
		    Matrix rankVector = new Matrix(rank);
		    
		    //if (verbose) {System.out.println("Initial state of Ranking vector:"); rankVector.print(8, 5);}
			// Create the one matrix and damping matrix 
			double oneVector[][] = new double[dimension][1];
			for (int i = 0; i < dimension; i++) {oneVector[i][0] = 1.;} Matrix oneMatrix = new Matrix(oneVector);
			Matrix dampingMatrix = oneMatrix.times(((1-dampingFactor)/dimension));
			
		    // Power Method. Will converge to the vector of rankings.
			// See https://en.wikipedia.org/wiki/PageRank#Damping_factor
			for (int i = 0; i < 20; i++) {							// # of iterations is arbitrary. 21 seems sufficient
				//System.out.print("iteration " + (i+1) + ":");
				//rankVector.print(6, 4);								// column width , # digits after decimal
				//for (int ii = 0; ii < rankVector.getRowDimension(); ii++) { System.out.printf("%f\n", rankVector.get(ii, 0));}
				rankVector = myMatrix.times(rankVector);			// columns in A must equal rows in B
				rankVector = rankVector.times(dampingFactor);
				rankVector = rankVector.plus(dampingMatrix);
			}
			System.out.println("Final state of Ranking vector:");
			for (int i = 0; i < rankVector.getRowDimension(); i++) { System.out.printf("%f\n", rankVector.get(i, 0));}
			
			// Just for fun, sum the rankings to see if we get 1-damping.
			double sum = 0;
			for (int i = 0; i < dimension; i++) {
				sum += rankVector.get(itemCount, 0);
			}
			System.out.printf("\n Sum of rankings = %8.6f", sum);
		} catch (Exception ex) {
			System.out.println("main(): " + ex.getLocalizedMessage());
		}
	}
	
	/***
	 * A test case where the transition matrix is normalized across all clients (un-weighted edges) and everything else is zero.
	 * @return The loaded-up transition matrix
	 */
	private  double[][] buildRandomWalkMatrix_test01() {
		double[][] myMatrix = { { 0,   0,   0,   0, .25, .25, .25, .25  }, 
								{ 0,   0,   0,   0, .25, .25, .25, .25  }, 
								{ 0,   0,   0,   0, .25, .25, .25, .25  }, 
								{ 0,   0,   0,   0, .25, .25, .25, .25  }, 
								{ 0,   0,   0,   0,   0,   0,   0,   0  }, 
								{ 0,   0,   0,   0,   0,   0,   0,   0  }, 
								{ 0,   0,   0,   0,   0,   0,   0,   0  }, 
								{ 0,   0,   0,   0,   0,   0,   0,   0  }
		};
		return myMatrix;
	}
	/***
	 * A test case where the transition matrix is weighted from Clients to Items and everything else is zero.
	 * @return The loaded-up transition matrix
	 */
	private  double[][] buildRandomWalkMatrix_test02() {
		double[][] myMatrix = { { 0,   0,   0,   0,  10./13, .25,  .25, .25  }, 
								{ 0,   0,   0,   0,   1./13, .25,  .25, .25  }, 
								{ 0,   0,   0,   0,   1./13, .25,  .25, .25  }, 
								{ 0,   0,   0,   0,   1./13, .25,  .25, .25  }, 
								{ 0,   0,   0,   0,       0,   0,    0,   0  }, 
								{ 0,   0,   0,   0,       0,   0,    0,   0  }, 
								{ 0,   0,   0,   0,       0,   0,    0,   0  }, 
								{ 0,   0,   0,   0,       0,   0,    0,   0  }
		};
		return myMatrix;
	}
	/***
	 * A test case where the transition matrix is weighted from Clients to Items and everything else is zero.
	 * @return The loaded-up transition matrix
	 */
	private  double[][] buildRandomWalkMatrix_test03() {
		double[][] myMatrix = { { 0,      0,    0,    0,  10./13, .25,  .25, .25  }, 
								{ 0,      0,    0,    0,   1./13, .25,  .25, .25  }, 
								{ 0,      0,    0,    0,   1./13, .25,  .25, .25  }, 
								{ 0,      0,    0,    0,   1./13, .25,  .25, .25  }, 
								{ 10./12, 2./6, 3./9, 0,       0,   0,    0,   0  }, 
								{  1./12, 2./6, 0,    4./12,   0,   0,    0,   0  }, 
								{  1./12, 0,    3./9, 4./12,   0,   0,    0,   0  }, 
								{ 0,      2./6, 3./9, 4./12,   0,   0,    0,   0  }
		};
		return myMatrix;
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
			String sql = "SELECT * FROM vLoadTransitionMatrix" + " ORDER BY myItemID, myClientNameID";
			ResultSet rs = stmt.executeQuery(sql);
			int counter = 0;
			int itemID = 0, clientNameID = 0;
			double totalSalePriceByItem;
			double SumOfSalePriceOfEachItemByCustomer;
			double weight;
			for (int row = 0; row < itemCount; row++) {
				for (int col = 0; col < itemCount; col++) { m[row][col] = 0; }
				for (int col = itemCount; col < dimension; col++) {
					counter ++;
					rs.next();
					itemID = rs.getInt("myItemID");
					clientNameID = rs.getInt("myClientNameID");
					totalSalePriceByItem = rs.getDouble("totalSalePriceByItem");
					SumOfSalePriceOfEachItemByCustomer = rs.getDouble("SumOfSalePriceOfEachItemByCustomer");
					weight = ((double)SumOfSalePriceOfEachItemByCustomer) / totalSalePriceByItem;
					if (Double.isNaN(weight)){weight = 0;}
					//System.out.println("counter = " + counter + " row = " + row + " col = " + col + " clientNameID = " + clientNameID + " itemID = " + itemID + " weight = " + weight);
					m[row][col] = weight;			// This is another way to calculate the arbitrary weights for items back to clients
//					m[row][col] = (double)1 / clientCount;								// This weights each client probability equally. 
				}
				//if (counter == 1847) {
				//	System.out.println("!");
				//}

			}
			con.close();
			con = db.Connect("il-server-001.uccc.uc.edu\\mssqlserver2012", "7081FinalProjectLogin", "P@ssword");
			// right side of the matrix -- customers walking to items
			sql = "SELECT * FROM vLoadTransitionMatrix" + " ORDER BY myClientNameID, myItemID";
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			for (int row = itemCount; row < dimension; row++) {
				for (int col = 0; col < itemCount; col++) {
					rs.next();
					double SumOfEachItemPurchasedByCustomer = rs.getInt("SumOfEachItemPurchasedByCustomer");
					double QtyPurchasedByItem = rs.getInt("QtyPurchasedByItem");
					weight = SumOfEachItemPurchasedByCustomer / QtyPurchasedByItem;
					if (Double.isNaN(weight)){weight = 0;}
					m[row][col] = weight;		// This is another possibility for the arbitrary weights from Items to Clients
				}
				for (int col = itemCount; col < dimension; col++) { m[row][col] = 0; }
			}
		} catch (Exception ex) {
			System.out.println("buildRandomWalkMatrix(): " + ex.getLocalizedMessage());
		}
		return m;
	}

	private void printMatrix(Matrix m, int dimension) {
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; i < dimension; i++) {
				System.out.printf("%f " , m.get(i, j));
			}
			System.out.println();
		}
	}
	private int countClientNamesThatBoughtSomething() {
		String sql = "SELECT COUNT(ClientNameID) AS ClientNameCount FROM dbo.vClientsThatBoughtSomething";
		Connection con = null;
		int clientNameCount = 0;
		try {
			con = db.Connect("il-server-001.uccc.uc.edu\\mssqlserver2012", "7081FinalProjectLogin", "P@ssword");
			//System.out.println("loadItemHashTable(): Connected to DB");
			//String item;
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			clientNameCount = rs.getInt("ClientNameCount");
		} catch (Exception ex) {
			System.out.println("countClientNamesThatBoughtSomething(): " + ex.getLocalizedMessage());
		} finally {
			try {con.close();} catch (Exception ex) {}
		}
		return clientNameCount;
	}
	private int countItemsThatWereBought() {
		String sql = "SELECT COUNT(ItemID) AS ItemCount FROM dbo.vItemsThatWereBought";
		Connection con = null;
		int itemCount = 0;
		try {
			con = db.Connect("il-server-001.uccc.uc.edu\\mssqlserver2012", "7081FinalProjectLogin", "P@ssword");
			//System.out.println("loadItemHashTable(): Connected to DB");
			//String item;
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			itemCount = rs.getInt("itemCount");
		} catch (Exception ex) {
			System.out.println("countItemsThatWereBought(): " + ex.getLocalizedMessage());
		} finally {
			try {con.close();} catch (Exception ex) {}
		}
		return itemCount;
	}
	
	/************************************ Ignore all this code **********************************************************************************/
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
	
	
	private static String getQuery() {
		
		return "SELECT * FROM vLoadTransitionMatrix" + " ORDER BY myClientNameID, myItemID";
//		return "SELECT * FROM vAggregateSalesByCustomerAndItem" + " ORDER BY myClientNameID, myItemID";
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

