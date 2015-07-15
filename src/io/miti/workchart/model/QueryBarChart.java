package io.miti.workchart.model;

import java.util.List;

import org.jfree.data.category.DefaultCategoryDataset;

public class QueryBarChart extends QueryStringNumChart {

  public QueryBarChart()
  {
    super();
    isPieChart = false;
  }
  
  public QueryBarChart(final String sTitle,
                       final String sQuery, final boolean bTooltips,
                       final boolean bLegend, final boolean bURLs)
  {
    // Initialize the class, but don't load the data yet
    super(sTitle, "", null, bTooltips, bLegend, bURLs);
    query = sQuery;
    isPieChart = false;
  }
  
  
  @Override
  protected String genQuery()
  {
  return query;
  }
  
  
  @Override
  protected void buildDataSet(final List<DataSample> data)
  {
    dataset = new DefaultCategoryDataset();
    for (DataSample item : data)
    {
      final String label = getLabel(item.name);
      ((DefaultCategoryDataset) dataset).addValue(item.count, title, label);
    }
  }
}
