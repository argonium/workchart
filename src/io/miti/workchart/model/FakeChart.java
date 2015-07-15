package io.miti.workchart.model;

import io.miti.workchart.util.Utility;

import org.jfree.data.general.DefaultPieDataset;

public class FakeChart extends PopChart
{
  public FakeChart()
  {
    super();
  }
  
  public FakeChart(final String sTitle, final boolean bTooltips,
                   final boolean bLegend, final boolean bURLs)
  {
    super(sTitle, bTooltips, bLegend, bURLs);
    
    // Creating the fake data is quick, so go ahead and do it
    addFakeData();
  }
  
  
  private void addFakeData()
  {
    // Create a dataset
    dataset = new DefaultPieDataset();
    final int numCats = Utility.getRandomNumber(1, 10);
    for (int i = 0; i < numCats; ++i)
    {
      ((DefaultPieDataset) dataset).setValue("Category " + (i + 1), Utility.getRandomNumber(10, 40));
    }
  }
  
  
  @Override
  public String toString()
  {
    String msg = "Fake chart: " + title + " / " + getPieDataset().getItemCount() + " categories";
    return msg;
  }
}
