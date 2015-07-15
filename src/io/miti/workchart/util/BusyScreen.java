package io.miti.workchart.util;

import java.awt.Cursor;

import io.miti.workchart.app.WorkChart;

public final class BusyScreen
{
  private Cursor lastCursor = null;
  
  public BusyScreen()
  {
    super();
  }
  
  
  public BusyScreen startWork()
  {
    // Get the current cursor, and set the cursor to a wait cursor
    lastCursor = WorkChart.getApp().frame.getCursor();
    WorkChart.getApp().frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    
    return this;
  }
  
  
  public void stopWork()
  {
    if (lastCursor != null)
    {
      WorkChart.getApp().frame.setCursor(lastCursor);
    }
    
    lastCursor = null;
  }
}
