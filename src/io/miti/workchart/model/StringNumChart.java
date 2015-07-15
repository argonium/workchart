package io.miti.workchart.model;

import io.miti.workchart.cache.ConnectionCache;
import io.miti.workchart.dbutil.Database;
import io.miti.workchart.util.BusyScreen;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jfree.data.general.DefaultPieDataset;

public class StringNumChart extends PopChart
{
  protected String table = null;
  protected String strCol = null;
  protected Map<String, String> labels = null;
  protected final int DEFAULT_MAX_LABELS = 15;
  
  public StringNumChart()
  {
    super();
  }
  
  
  public StringNumChart(final String sTitle, final String sTable, final String sStrCol,
                        final boolean bTooltips, final boolean bLegend, final boolean bURLs)
  {
    // Initialize the class, but don't load the data yet
    super(sTitle, bTooltips, bLegend, bURLs);
    table = sTable;
    strCol = sStrCol;
  }
  
  
  public StringNumChart(final String sTitle, final String sTable, final String sStrCol,
                        final boolean bTooltips, final boolean bLegend, final boolean bURLs,
                        final Map<String, String> labelMap)
  {
    // Initialize the class, but don't load the data yet
    super(sTitle, bTooltips, bLegend, bURLs);
    table = sTable;
    strCol = sStrCol;
    labels = labelMap;
  }
  
  
  @Override
  protected void loadData()
  {
    // Check if the data has been loaded
    if (dataLoaded)
    {
      return;
    }
    
    // Show a wait icon while the data is loading
    BusyScreen busy = new BusyScreen().startWork();
    
    // Start the data load
    dataLoaded = true;
    String qry = genQuery();
    
    // Load the data
    Connection dbConn = ConnectionCache.getConnectionCache().getConnection();
    Map<String, Integer> map = Database.executeSelectForStringIntMap(qry, dbConn);
    
    // Save the data from the map
    if (map != null)
    {
      // Build the data set
      List<DataSample> data = buildSamples(map);
      buildDataSet(data);
    }
    
    // Reset the icon
    busy.stopWork();
  }
  
  
  protected void buildDataSet(final List<DataSample> data)
  {
    dataset = new DefaultPieDataset();
    for (DataSample item : data)
    {
      final String label = getLabel(item.name);
      ((DefaultPieDataset) dataset).setValue(label == null ? "<Null>" : label, item.count);
    }
  }
  
  
  private List<DataSample> buildSamples(final Map<String, Integer> map)
  {
    final List<DataSample> list = new ArrayList<DataSample>(20);
    for (Entry<String, Integer> entry : map.entrySet())
    {
      DataSample sample = new DataSample(entry.getKey(), entry.getValue().intValue());
      list.add(sample);
    }
    
    // Sort by count (descending)
    Collections.sort(list);
    
    // Remove rows with zero count
    if (list.size() > 0)
    {
      // Get the index of the last element, since we traverse the
      // list in reverse order
      final int starting = list.size() - 1;
      for (int i = starting; i >= 0; --i)
      {
        // If this element has a zero count, remove it
        if (list.get(i).count == 0)
        {
          list.remove(i);
        }
        else
        {
          // We hit a positive count, so no more zero-counts
          break;
        }
      }
    }
    
    // If there are too many items in the list, pare the list down
    final int size = list.size();
    if (size > DEFAULT_MAX_LABELS)
    {
      // Traverse the extras in reverse order so we can remove them
      // without changing the order of future rows to be removed
      int otherCount = 0;
      for (int i = (size - 1); i >= DEFAULT_MAX_LABELS; --i)
      {
        otherCount += list.get(i).count;
        list.remove(i);
      }
      
      if (otherCount > 0)
      {
        list.add(new DataSample("(Others)", otherCount));
        
        // Re-sort the list
        Collections.sort(list);
      }
    }
    
    return list;
  }
  
  
  protected String getLabel(final String key)
  {
    if ((labels == null) || labels.isEmpty())
    {
      return key;
    }
    
    if (labels.containsKey(key))
    {
      return labels.get(key);
    }
    
    return key;
  }
  
  
  protected String genQuery()
  {
    String qry = String.format(
      "select %s, count(*) from %s group by %s order by %s",
      strCol, table, strCol, strCol);
    return qry;
  }
  
  
  @Override
  public String toString()
  {
    String msg = "S/N chart: " + title + " / " + getPieDataset().getItemCount() + " categories";
    return msg;
  }
}
