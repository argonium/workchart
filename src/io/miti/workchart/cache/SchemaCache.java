package io.miti.workchart.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import io.miti.workchart.model.Node;
import io.miti.workchart.util.SchemaParser;

public final class SchemaCache
{
  private static final SchemaCache cache;
  private boolean dataLoaded = false;
  private List<Node> schemas = null;
  
  static
  {
    cache = new SchemaCache();
  }
  
  
  private SchemaCache()
  {
    super();
  }
  
  
  public static SchemaCache getCache()
  {
    return cache;
  }
  
  
  public void loadCache()
  {
    if (dataLoaded)
    {
      return;
    }
    
    dataLoaded = true;
    List<Node> nodes = new SchemaParser().getSchemas();
    
    schemas = new ArrayList<Node>(nodes.size());
    for (Node node : nodes)
    {
      schemas.add(new Node(node));
    }
  }
  
  
  /**
   * Create a unique list of the environments.
   * 
   * @return a unique list of the environments
   */
  public List<String> getEnvironments()
  {
    Set<String> set = new LinkedHashSet<String>(10);
    for (Node node : schemas)
    {
      set.add(node.getSchema());
    }
    
    return Collections.list(Collections.enumeration(set));
  }
  
  
  public List<Node> getSchemas(final String schema)
  {
    List<Node> nodes = new ArrayList<Node>(10);
    for (Node node : schemas)
    {
      if (node.getSchema().equals(schema))
      {
        nodes.add(node);
      }
    }
    
    return Collections.list(Collections.enumeration(nodes));
  }
  
  
  public Node getNode(final String schemaName, final String nodeName)
  {
    Node node = null;
    for (Node iNode : schemas)
    {
      if ((iNode.getSchema().equals(schemaName)) && (iNode.getNode().equals(nodeName)))
      {
        node = iNode;
      }
    }
    
    return node;
  }
}
