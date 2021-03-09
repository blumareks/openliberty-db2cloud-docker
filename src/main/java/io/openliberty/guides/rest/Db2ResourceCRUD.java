// tag::comment[]
/*******************************************************************************
 * Copyright (c) 2017, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
 // end::comment[]
package io.openliberty.guides.rest;

import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.Json;

//DB2
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;


// tag::Path[]
@Path("db2-crud")
// end::Path[]
public class Db2ResourceCRUD {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getProperties() {

        JsonObjectBuilder builder = Json.createObjectBuilder();

        
        

        try{
            Class.forName("COM.ibm.db2.jdbc.app.DB2Driver");
            //"COM.ibm.db2.jcc.DB2Driver");
            //"com.ibm.db2.jdbc.app.DB2Driver");
        }
        catch (ClassNotFoundException e) {
            System.out.println("no classpath to DB2 Driver (where the jar is located?) - COM.ibm.db2.jdbc.app.DB2Driver");
            e.printStackTrace();
            builder.add((String)"no-class error", (String) "no classpath to DB2 Driver (where the jar to COM.ibm.db2.jdbc.app.DB2Driver is located?)" );
            return builder.build();
        }

        System.out.println("DB2 driver is loaded");
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset=null;
        boolean found=false;
        try {
            conn = DriverManager.getConnection("jdbc:db2://dashdb-txn-sbox-yp-dal09-11.services.dal.bluemix.net:50001/BLUDB:sslConnection=true;",
        "jqs67035","dc3r9vmn^8t506cm");
            if (conn != null)
            {
                System.out.println("DB2 Database Connected");
            }
            else
            {
                System.out.println("Db2 connection Failed ");
            }
            pstmt=conn.prepareStatement("Select * from test");
            rset=pstmt.executeQuery();
            if(rset!=null)
            {

                while(rset.next())
                {
                    found=true;
                    System.out.println("Class Code: "+rset.getString("clcode"));
                    System.out.println("Name: "+rset.getString("name"));
                }
            }
            if (found ==false)
            {
                System.out.println("No Information Found");
                builder.add((String)"empty error", (String) "No Information Found");
                return builder.build();
            } else {
                builder.add((String)"ok", (String) "something found");
                return builder.build();
            }
        } catch (SQLException e) {
            System.out.println("DB2 Database connection Failed");
            e.printStackTrace();
            builder.add((String)"conn error", (String) "DB2 Database connection Failed");
            return builder.build();
        }

        

    }

        /**
         * String urlPrefix = "jdbc:db2:";
    String url;
    String user;
    String password;
    String empNo;                                                             // 2 
    Connection con;
    Statement stmt;
    ResultSet rs;
    
    System.out.println ("**** Enter class EzJava");
    
    // Check the that first argument has the correct form for the portion
    // of the URL that follows jdbc:db2:,
    // as described
    // in the Connecting to a data source using the DriverManager 
    // interface with the IBM Data Server Driver for JDBC and SQLJ topic.
    // For example, for IBM Data Server Driver for 
    // JDBC and SQLJ type 2 connectivity, 
    // args[0] might be MVS1DB2M. For 
    // type 4 connectivity, args[0] might
    // be //stlmvs1:10110/MVS1DB2M.


    if (args.length!=3)
    {
      System.err.println ("Invalid value. First argument appended to "+
       "jdbc:db2: must specify a valid URL.");
      System.err.println ("Second argument must be a valid user ID.");
      System.err.println ("Third argument must be the password for the user ID.");
      System.exit(1);
    }
    url = urlPrefix + args[0];
    user = args[1];
    password = args[2];
    try 
    {                                                                        
      // Load the driver
      // local db2:
      // Class.forName("com.ibm.db2.jcc.DB2Driver");                             // 3a 
      // remote db2:
      Class.forName("com.ibm.db2.jdbc.app.DB2Driver");
      System.out.println("**** Loaded the JDBC driver");

      // Create the connection using the IBM Data Server Driver for JDBC and SQLJ
      con = DriverManager.getConnection (url, user, password);                // 3b 
      // Commit changes manually
      con.setAutoCommit(false);
      System.out.println("**** Created a JDBC connection to the data source");

      // Create the Statement
      stmt = con.createStatement();                                           // 4a 
      System.out.println("**** Created JDBC Statement object");

      // Execute a query and generate a ResultSet instance
      rs = stmt.executeQuery("SELECT EMPNO FROM EMPLOYEE");                   // 4b 
      System.out.println("**** Created JDBC ResultSet object");

      // Print all of the employee numbers to standard output device
      while (rs.next()) {
        empNo = rs.getString(1);
        System.out.println("Employee number = " + empNo);
      }
      System.out.println("**** Fetched all rows from JDBC ResultSet");
      // Close the ResultSet
      rs.close();
      System.out.println("**** Closed JDBC ResultSet");
      
      // Close the Statement
      stmt.close();
      System.out.println("**** Closed JDBC Statement");

      // Connection must be on a unit-of-work boundary to allow close
      con.commit();
      System.out.println ( "**** Transaction committed" );
      
      // Close the connection
      con.close();                                                           // 6 
      System.out.println("**** Disconnected from data source");

      System.out.println("**** JDBC Exit from class EzJava - no errors");

    }
    
    catch (ClassNotFoundException e)
    {
      System.err.println("Could not load JDBC driver");
      System.out.println("Exception: " + e);
      e.printStackTrace();
    }

    catch(SQLException ex)                                                   // 5  
    {
      System.err.println("SQLException information");
      while(ex!=null) {
        System.err.println ("Error msg: " + ex.getMessage());
        System.err.println ("SQLSTATE: " + ex.getSQLState());
        System.err.println ("Error code: " + ex.getErrorCode());
        ex.printStackTrace();
        ex = ex.getNextException(); // For drivers that support chained exceptions
      }
    }
         */

     

}
