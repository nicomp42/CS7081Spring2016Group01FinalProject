package DatabaseUtilities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;


public class TableUtilities {
	private int totalRecords;
	private String entityName;
	private Random myRandom;
	
	public TableUtilities(String tableName) {
		this.entityName = tableName;
		myRandom = new Random();
	}
	
	/**
	 * Get a random record from a table that follows my naming conventions
	 * @param con An open database connection
	 * @return the value of the column {entityName}ID of the unique record: this will be the value of the primary key.
	 */
	public int getRandomID(Connection con) {
		int ID = -1;
		//if (totalRecords == 0) {
		//	totalRecords = Utilities.ReadIntFromQuery(con, "SELECT COUNT(" + entityName + "ID) as CountOfID from t" + entityName + ";", "CountOfID", true);
		//}
		//System.out.println("getRandomID(): toalRecords for entity " + entityName + " = " + totalRecords);
		// Generate a record number from the physical order.
		//int recordTarget = myRandom.nextInt(totalRecords + 1);
		String sql = "SELECT TOP 1 " 
                     + entityName + "ID" 
                     + " AS ID "
                     + " FROM t"+ entityName
                     + " ORDER BY NEWID()"
                     + ";";
		//System.out.println("getRandomID(): sql = " + sql);

		// Get the random record so we can extract the ID
		ID = Utilities.ReadIntFromQuery(con, sql, "ID", false);
		return ID;
	}
}
