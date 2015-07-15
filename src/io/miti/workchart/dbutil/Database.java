package io.miti.workchart.dbutil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import io.miti.workchart.model.UrlInfo;
import io.miti.workchart.util.Logger;

/**
 * Encapsulate the database functionality.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class Database
{
  /**
   * Whether the database driver has been loaded into memory.
   */
  private static boolean driverLoaded = false;
  
  
  /**
   * Default constructor.
   */
  private Database()
  {
    super();
  }
  
  
  public static Map<String, Integer> executeSelectForStringIntMap(final String sqlCmd,
                                                                  final Connection dbConn)
  {
    Map<String, Integer> map = new HashMap<String, Integer>(10);
    
    // Check the SQL command
    if ((sqlCmd == null) || (sqlCmd.length() < 1))
    {
      System.err.println("The SQL cmd is empty or null");
      return map;
    }
    else if (dbConn == null)
    {
      System.err.println("The connection is null for selecting string/int maps");
      return map;
    }
    
    // Execute the statement and get the returned ID
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try
    {
      // Create the Statement object from the connection
      stmt = dbConn.prepareStatement(sqlCmd);
      if (null != stmt)
      {
        // Now execute the query and save the result set
        rs = stmt.executeQuery();
        
        // Check for a result
        if (rs != null)
        {
          // Check for a result
          while (rs.next())
          {
            // Save the value
            String str = rs.getString(1);
            int num = rs.getInt(2);
            map.put(str, Integer.valueOf(num));
          }
          
          // Close the result set
          rs.close();
          rs = null;
        }
        
        // Close the statement
        stmt.close();
        stmt = null;
      }
    }
    catch (SQLException sqlex)
    {
      Logger.error(sqlex);
    }
    catch (Exception ex)
    {
      Logger.error(ex, -1);
    }
    finally
    {
      // Close the ResultSet if it's not null
      try
      {
        if (rs != null)
        {
          rs.close();
          rs = null;
        }
      }
      catch (SQLException sqle)
      {
        Logger.error(sqle);
      }
      
      // Close the Statement if it's not null
      try
      {
        if (stmt != null)
        {
          stmt.close();
          stmt = null;
        }
      }
      catch (SQLException sqle)
      {
        Logger.error(sqle);
      }
    }
    
    // Return the result of the operation
    return map;
  }
  
  
  /**
   * Load the database driver.
   */
  public static boolean loadDriver()
  {
    // See if the class needs to be loaded
    boolean found = false;
    if (!driverLoaded)
    {
      // Mark the driver as loaded, whether it's loaded or not
      driverLoaded = true;
      
      try
      {
        Class.forName("org.postgresql.Driver");
        found = true;
      }
      catch (ClassNotFoundException cnfe)
      {
        Logger.error(cnfe);
        cnfe.printStackTrace();
      }
    }
    
    return found;
  }
  
  
  public static Connection createConnection(final String fullUrl,
                                            final String username,
                                            final String password)
  {
    // Make sure the driver is loaded
    loadDriver();
    
    // This is the connection that gets returned
    Connection dbConn = null;
    
    try
    {
      UrlInfo urlInfo = UrlInfo.createFromString(fullUrl);
      dbConn = DriverManager.getConnection(urlInfo.url, username, password);
      
      // Connect to a schema
      connectToSchema(urlInfo.schema, dbConn);
    }
    catch (SQLException sqle)
    {
      String errMsg = sqle.getMessage();
      System.err.println("Exception creating DB connection: " + errMsg);
      Logger.error(sqle);
    }
    
    return dbConn;
  }
  
  
  private static void connectToSchema(String schema, final Connection conn) {
	  if ((schema == null) || schema.trim().isEmpty()) {
		  return;
	  }
	  
	  Statement statement = null;
	  try { 
		  statement = conn.createStatement();
		  statement.execute("set search_path to '" + schema + "'");
	  } catch (SQLException ex) {
		  ex.printStackTrace();
	  }
	  finally {
		  try {
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	  }
    }


  /**
   * Close the database connection.
   */
  public static void closeConnection(final Connection dbConn)
  {
    // Check if the database connection is null
    if (dbConn == null)
    {
      return;
    }
    
    try
    {
      // Close the connection
      dbConn.close();
    }
    catch (SQLException sqle)
    {
      System.err.println(sqle.getMessage());
      Logger.error(sqle);
    }
  }
}
