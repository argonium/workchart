package io.miti.workchart.model;

public final class Node implements Comparable<Node>
{
  private String schema = null;
  private String node = null;
  private String url = null;
  private String id = null;
  private String pw = null;
  
  
  public Node()
  {
    super();
  }
  
  
  public Node(final Node n)
  {
    schema = n.schema;
    node = n.node;
    url = n.url;
    id = n.id;
    pw = n.pw;
  }


  public String getSchema() {
    return schema;
  }


  public void setSchema(final String schema) {
    this.schema = schema;
  }


  public String getNode() {
    return node;
  }


  public void setNode(final String node) {
    this.node = node;
  }


  public String getUrl() {
    return url;
  }


  public void setUrl(final String url) {
    this.url = url;
  }


  public String getId() {
    return id;
  }


  public void setId(final String id) {
    this.id = id;
  }


  public String getPw() {
    return pw;
  }


  public void setPw(final String pw) {
    this.pw = pw;
  }


  @Override
  public int compareTo(Node n)
  {
    int val = compareStrings(schema, n.schema);
    if (val == 0)
    {
      val = compareStrings(node, n.node);
    }
    
    return val;
  }
  
  
  private static int compareStrings(final String s1, final String s2)
  {
    if ((s1 == null) && (s2 == null))
    {
      return 0;
    }
    else if (s1 == null)
    {
      return -1;
    }
    else if (s2 == null)
    {
      return 1;
    }
    
    return s1.compareTo(s2);
  }


  @Override
  public String toString() {
    return "Node [schema=" + schema + ", node="
        + node + ", url=" + url + ", id=" + id + ", pw=" + pw + "]";
  }
  
  
  public String getStatusString()
  {
    return String.format("Source: %s (%s)", schema, node);
  }
}
