package CS7081.Spring2016.Group01.FinalProject;

import java.sql.Connection;

import DatabaseUtilities.DatabaseInterface;
import DatabaseUtilities.Utilities;
import Jama.Matrix;

public class Main {

	public static void main(String[] args) {

		Main myClass = new Main();
		try {
			
			//myClass.Go();
			
			// Test JAMA matrix stuff
//			double data[][] = {{1.,2.,3},{4.,5.,6.},{7.,8.,10.}};
/*			double data[][] = {{1,0,0,0},
					           {1,0,0,0},
					           {1,0,0,0},
					           {1,0,0,0}};
*/
			
 			// See http://introcs.cs.princeton.edu/java/16pagerank/ fot this data and the expected result of the Power Method convergence   
			double data[][] = { {.02, .92, .02, .02, .02},		
								{.02, .02, .38, .38, .20},
								{.02, .02, .02, .92, .02},
								{.92, .02, .02, .02, .02},
								{.47, .02, .47, .02, .02}				
			};
			
//			double data[][] = {{1,2,1},{6,-1,0},{-1,2,-1}};
			Jama.Matrix m = new Jama.Matrix(data);

			// Power Method for convergence to the probabilities
			double[][] move = new double[1][data.length]; 
		    for (int i = 0; i < move.length; i++) {
		    	move[0][i] = 0; 		//1./move.length;
		    }
	    	move[0][0] = 1;
		    Matrix moveMatrix = new Matrix(move);
		    // Power Method. Will converge to the vector of probabilities. 
			for (int i = 0; i < 20	; i++) {
				moveMatrix = moveMatrix.times(m);
				moveMatrix.print(4, 2);		// column width , # digits after decimal
			}

		} catch (Exception ex) {
			System.out.println("main(): " + ex.getLocalizedMessage());
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
