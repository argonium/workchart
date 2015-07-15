package io.miti.workchart.model;

public class QueryStringNumChart extends StringNumChart
{
  protected String query = null;
  
  public QueryStringNumChart()
  {
    super();
  }
  
  
  public QueryStringNumChart(final String sTitle, final String sTable,
                             final String sQuery, final boolean bTooltips,
                             final boolean bLegend, final boolean bURLs)
  {
    // Initialize the class, but don't load the data yet
    super(sTitle, sTable, null, bTooltips, bLegend, bURLs);
    query = sQuery;
  }
  
  
  @Override
  protected String genQuery()
  {
    return query;
  }
  
  
  @Override
  public String toString()
  {
    String msg = "Q/S/N chart: " + title + " / " + getPieDataset().getItemCount() + " categories";
    return msg;
  }
}
