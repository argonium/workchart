package io.miti.workchart.model;

public final class DataSample implements Comparable<DataSample>
{
  public String name = null;
  public int count = 0;
  
  public DataSample()
  {
    super();
  }
  
  
  public DataSample(final String sName, final int nCount)
  {
    name = sName;
    count = nCount;
  }
  
  
  @Override
  public int compareTo(DataSample o)
  {
    // Sort in count order (descending)
    return o.count - count;
  }
}
