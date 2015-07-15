package io.miti.workchart.cache;

import java.sql.Connection;

import io.miti.workchart.dbutil.Database;
import io.miti.workchart.model.Node;

public final class ConnectionCache
{
  private static final ConnectionCache cache;
  private Connection conn = null;
  
  static
  {
    cache = new ConnectionCache();
  }
  
  private ConnectionCache()
  {
    super();
  }
  
  
  public static ConnectionCache getConnectionCache()
  {
    return cache;
  }
  
  
  public Connection getConnection()
  {
    // Get the connection for the schema name
    if (conn == null)
    {
      System.err.println("Error: No connection found");
    }
    
    return conn;
  }
  
  
  public void setConnection(final Node node)
  {
    closeConnection();
    
    // Check if node is null
    if (node == null)
    {
      System.err.println("Turning off the connection (node is null)");
      return;
    }
    
    // Create the connection
    conn = Database.createConnection(node.getUrl(), node.getId(), node.getPw());
  }


  public void closeConnection()
  {
    // Close any database connections
    if (conn != null)
    {
      Database.closeConnection(conn);
      conn = null;
    }
  }
}
