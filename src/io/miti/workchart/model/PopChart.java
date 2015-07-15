package io.miti.workchart.model;

import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleEdge;

public class PopChart
{
  protected String title = null;
  protected boolean tooltips = false;
  protected boolean legend = false;
  protected boolean urls = false;
  protected AbstractDataset dataset = null;
  protected boolean dataLoaded = false;
  protected boolean isPieChart = true;
  
  public PopChart()
  {
    super();
  }
  
  public PopChart(final String sTitle, final boolean bTooltips, final boolean bLegend, final boolean bURLs)
  {
    title = sTitle;
    tooltips = bTooltips;
    legend = bLegend;
    urls = bURLs;
  }
  
  public String getTitle()
  {
    return title;
  }
  
  public boolean useTooltips()
  {
    return tooltips;
  }
  
  public boolean useLegend()
  {
    return legend;
  }
  
  public boolean useURLs()
  {
    return urls;
  }
  
  public AbstractDataset getDataset()
  {
    return dataset;
  }
  
  public boolean isPieChart()
  {
    return isPieChart;
  }
  
  public boolean isCategoryDatasetEmpty()
  {
    final DefaultCategoryDataset cd = getCategoryDataset();
    return ((cd == null) || (cd.getRowCount() == 0));
  }
  
  public boolean isPieDatasetEmpty()
  {
    final DefaultPieDataset cd = getPieDataset();
    return ((cd == null) || (cd.getItemCount() == 0));
  }
  
  public DefaultPieDataset getPieDataset()
  {
    return (DefaultPieDataset) dataset;
  }
  
  public DefaultCategoryDataset getCategoryDataset()
  {
    return (DefaultCategoryDataset) dataset;
  }
  
  
  
  /**
   * Get the titles of the charts, with an optional header at the start.
   * 
   * @param chartData the array of charts
   * @param addHeader whether to start the list with a header string
   * @return the list of titles for the charts
   */
  public static String[] getChartTitles(List<PopChart> chartData, boolean addHeader)
  {
    String[] list = new String[!addHeader ? chartData.size() : (1 + chartData.size())];
    int index = 0;
    if (addHeader)
    {
      list[index++] = "Select a chart";
    }
    
    for (PopChart chart : chartData)
    {
      list[index++] = chart.getTitle();
    }
    
    return list;
  }
  
  
  protected void loadData()
  {
    // Nothing to do here, but declare it for subclasses that load data on demand
  }
  
  
  private JFreeChart getBarChart()
  {
    // Load the data, if necessary
    loadData();
    
    // Create the vertical bar chart
    final JFreeChart chart = ChartFactory.createBarChart(getTitle(), "", "", getCategoryDataset(),
        PlotOrientation.HORIZONTAL, false, true, false);
    final CategoryPlot categoryplot = (CategoryPlot) chart.getPlot();
    categoryplot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
    categoryplot.setRangePannable(true);
    categoryplot.setNoDataMessage("No data available");
    
    final BarRenderer barrenderer = (BarRenderer) categoryplot.getRenderer();
    barrenderer.setItemLabelAnchorOffset(9D);
    barrenderer.setBaseItemLabelsVisible(true);
    barrenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
    // barrenderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator("{1} = {2}", new DecimalFormat("0")));
    final CategoryAxis categoryaxis = categoryplot.getDomainAxis();
    categoryaxis.setCategoryMargin(0.25D);
    
    categoryaxis.setUpperMargin(0.02D);
    categoryaxis.setLowerMargin(0.02D);
    final NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
    numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    numberaxis.setUpperMargin(0.10000000000000001D);
    ChartUtilities.applyCurrentTheme(chart);
    
//    if (useLegend())
//    {
//      // pieplot.setLegendLabelGenerator(new StandardPieSectionLabelGenerator("{0} ({1})"));
//      chart.getLegend().setPosition(RectangleEdge.RIGHT);
//      // pieplot.setLegendLabelToolTipGenerator(new StandardPieSectionLabelGenerator("{0}: {2}%"));
//    }
    
    return chart;
  }
  
  
  public JFreeChart getFreeChart()
  {
    return (isPieChart() ? getPieChart() : getBarChart());
  }
  
  
  private JFreeChart getPieChart()
  {
    // Load the data, if necessary
    loadData();
    
    // Create a chart and return it
    final JFreeChart chart = ChartFactory.createPieChart(getTitle(), getPieDataset(),
        useLegend(), useTooltips(), useURLs());
    
    // Customize the labels and legend
    final PiePlot pieplot = (PiePlot) chart.getPlot();
    pieplot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} ({2})"));
    // pieplot.setSimpleLabels(true);
    pieplot.setNoDataMessage("No data available");
    
    if (useLegend())
    {
      pieplot.setLegendLabelGenerator(new StandardPieSectionLabelGenerator("{0} ({1})"));
      chart.getLegend().setPosition(RectangleEdge.RIGHT);
      // pieplot.setLegendLabelToolTipGenerator(new StandardPieSectionLabelGenerator("{0}: {2}%"));
    }
    
    return chart;
  }
  
  
  public void clearCache()
  {
    dataLoaded = false;
    
    if (dataset != null)
    {
      if (isPieChart)
      {
        getPieDataset().clear();
      }
      else
      {
        getCategoryDataset().clear();
      }
      
      dataset = null;
    }
  }
  
  
  @Override
  public String toString()
  {
    String msg = "Pop chart: " + title; // + " / " + dataset.getItemCount() + " categories";
    return msg;
  }
}
