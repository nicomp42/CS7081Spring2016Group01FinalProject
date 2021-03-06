/*=====================================================================
File: 	 basicDT.java
Summary: This Microsoft SQL Server JDBC Driver sample application
         demonstrates how to use result set getter methods to retrieve
         basic SQL Server data type values, and how to use result set
         update methods to update those values.
---------------------------------------------------------------------
This file is part of the Microsoft SQL Server JDBC Driver Code Samples.
Copyright (C) Microsoft Corporation.  All rights reserved.
 
This source code is intended only as a supplement to Microsoft
Development Tools and/or on-line documentation.  See these other
materials for detailed information regarding Microsoft code samples.
 
THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF
ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
PARTICULAR PURPOSE.
=====================================================================*/

import java.sql.*;

public class basicDT {
   
   public static void main(String[] args) {
      
      // Create a variable for the connection string.
      String connectionUrl = "jdbc:sqlserver://localhost:1433;" +
            "databaseName=AdventureWorks;integratedSecurity=true;";
      
      // Declare the JDBC objects.
      Connection con = null;
      Statement stmt = null;
      ResultSet rs = null;
      
      try {
         // Establish the connection.
         Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
         con = DriverManager.getConnection(connectionUrl);
         
         // Create and execute an SQL statement that returns some data
         // and display it.
         String SQL = "SELECT * FROM DataTypesTable;";
         stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
         rs = stmt.executeQuery(SQL);
         rs.next();
         displayRow("ORIGINAL DATA", rs);
         
         // Update the data in the result set.
         rs.updateString(2, "B");
         rs.updateString(3, "Some updated text.");
         rs.updateBoolean(4, true);
         rs.updateDouble(5, 77.89);
         rs.updateDouble(6, 1000.01);
         rs.updateTimestamp(7, new Timestamp(System.currentTimeMillis()));
         rs.updateRow();
         
         // Get the updated data from the database and display it.
         rs = stmt.executeQuery(SQL);
         rs.next();
         displayRow("UPDATED DATA", rs);
      }
      
      // Handle any errors that may have occurred.
      catch (Exception e) {
         e.printStackTrace();
      }
      
      finally {
         if (rs != null) try { rs.close(); } catch(Exception e) {}
         if (stmt != null) try { stmt.close(); } catch(Exception e) {}
         if (con != null) try { con.close(); } catch(Exception e) {}
      }
   }
   
   private static void displayRow(String title, ResultSet rs) {
      try {
         System.out.println(title);
         System.out.println(rs.getInt(1) + " " +  // SQL integer type.
               rs.getString(2) + " " +            // SQL char type.
               rs.getString(3) + " " +            // SQL varchar type.
               rs.getBoolean(4) + " " +           // SQL bit type.
               rs.getDouble(5) + " " +            // SQL decimal type.
               rs.getDouble(6) + " " +            // SQL money type.
               rs.getTimestamp(7));               // SQL datetime type.
         System.out.println();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
