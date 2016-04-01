package DatabaseUtilities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;


public class Utilities {
	private static boolean debug = true;
	
	public Utilities () {
		debug = true;
	}
	
	public static void setDebug(boolean debug){Utilities.debug = debug;}

	public static String ReadStringFromQuery(Connection con, String sql, String column, boolean debug) {
		String result = "";
		try {
			// Create a statement object that will return a forward-only
			// readable result set that is also read-only. Quick and dirty.
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			//System.out.println("# of columns in resultset: " + rs.getMetaData().getColumnCount());
			// This logic only works if we use this while loop. It's weird because all we want 
			//  is the first record (there is only one record) but calling the getString() method
			//  without the rs.next() loop just results in an exception. 
			while (rs.next()) {		
				result = rs.getString(column);
				break;			// e want the first row in the resultset. That's all. 
			}
		} catch (Exception ex) {
			if (debug) System.out.println("ReadStringFromQuery: " + ex.getLocalizedMessage());
		}
		return result;
	}
	
	
	public static int ReadIntFromQuery(Connection con, String sql, String column, boolean debug) {
		int result = 0;
		try {
			// Create a statement object that will return a forward-only
			// readable result set that is also read-only. Quick and dirty.
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			//System.out.println("# of columns in resultset: " + rs.getMetaData().getColumnCount());
			// This logic only works if we use this while loop. It's weird because all we want 
			//  is the first record (there is only one record) but calling the getInt() method
			//  without the rs.next() loop just results in an exception. 
			while (rs.next()) {		
				result = rs.getInt(column);
				break;			// e want the first row in the resultset. That's all. 
			}
		} catch (Exception ex) {
			if (debug) System.out.println("ReadIntFromQuery: " + ex.getLocalizedMessage());
		}
		return result;
	}
	/**
	 * Same as ReadIntFromQuery
	 * @param con
	 * @param sql
	 * @param column
	 * @param debug
	 * @return The double read from the query
	 */
	public static double ReadDoubleFromQuery(Connection con, String sql, String column, boolean debug) {
		double result = 0;
		try {
			// Create a statement object that will return a forward-only
			// readable result set that is also read-only. Quick and dirty.
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			//System.out.println("# of columns in resultset: " + rs.getMetaData().getColumnCount());
			// This logic only works if we use this while loop. It's weird because all we want 
			//  is the first record (there is only one record) but calling the getInt() method
			//  without the rs.next() loop just results in an exception. 
			while (rs.next()) {		
				result = rs.getInt(column);
				break;			// e want the first row in the resultset. That's all. 
			}
		} catch (Exception ex) {
			if (debug) System.out.println("ReadIntFromQuery: " + ex.getLocalizedMessage());
		}
		return result;
	}
	public static boolean ExecuteActionQuery(Connection con, String sql){
		boolean status = true;	// Hope for the best
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
		} catch(Exception ex) {
			status = false;		
//			if (debug) System.out.println("ExecuteActionQuery: " + ex.getLocalizedMessage());
		}
		return status;
	}
}
