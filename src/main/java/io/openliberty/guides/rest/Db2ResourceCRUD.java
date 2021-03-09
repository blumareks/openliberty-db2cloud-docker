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
            Class.forName("com.ibm.db2.jdbc.app.DB2Driver");
            
        }
        catch (ClassNotFoundException e) {
            System.out.println("no classpath to DB2 Driver (where the jar is located?) - com.ibm.db2.jdbc.app.DB2Driver");
            e.printStackTrace();
            builder.add((String)"no-class error", (String) "no classpath to DB2 Driver (where the jar to com.ibm.db2.jdbc.app.DB2Driver is located?)" );
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
}
