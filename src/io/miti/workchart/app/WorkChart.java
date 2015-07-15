package io.miti.workchart.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import io.miti.workchart.cache.ConnectionCache;
import io.miti.workchart.cache.SchemaCache;
import io.miti.workchart.model.Node;
import io.miti.workchart.model.PopChart;
import io.miti.workchart.model.StringNumChart;
import io.miti.workchart.util.Content;
import io.miti.workchart.util.OSXAdapter;
import io.miti.workchart.util.Utility;
import io.miti.workchart.util.WindowState;

public class WorkChart
{
  /** The name of the properties file. */
  public static final String PROPS_FILE_NAME = "wchart.prop";
  
  /** The one instance of this class. */
  private static final WorkChart app;
  
  /** The application frame. */
  public JFrame frame = null;
  
  /** The status bar. */
  private JLabel statusBar = null;
  
  /** The chart panel. */
  private JPanel chartPanel = null;
  
  /** The array of charts. */
  private List<PopChart> chartData = null;
  
  /** The window state (position and size). */
  private WindowState windowState = null;
  
  /** The combo box of charts. */
  private JComboBox<String> cbCharts = null;
  
  static
  {
    app = new WorkChart();
  }
  
  /**
   * Default constructor.
   */
  private WorkChart()
  {
    super();
  }
  
  
  /**
   * Create the application's GUI.
   */
  private void createGUI()
  {
    // Load the properties file
    windowState = WindowState.getInstance();
    
    // Determine whether we're running in Eclipse or as a stand-alone jar
    checkInputFileSource();
    
    // Load the schemas
    SchemaCache.getCache().loadCache();
    
    // Create the chart array
    populateCharts();
    
    // Set up the frame
    setupFrame();
    
    // Create the empty middle window
    initScreen();
    
    // Set up the status bar
    initStatusBar();
    
    try
    {
      // If the user closes the app on OS X via ctrl-Q, call the exitApp() method
      OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("exitApp", (Class[]) null));
    }
    catch (NoSuchMethodException nsme)
    {
      System.err.println("Error: Unable to find the exit method");
    }
    
    // Display the window.
    frame.pack();
    frame.setVisible(true);
    frame.setIconImage(Content.getIcon("appicon.png").getImage());
    
    // Check if a schema needs to be set
    checkSchema();
  }
  
  
  private void checkSchema()
  {
    // See if a schema has been set
    final String env = WindowState.getInstance().getEnvironment();
    final String schema = WindowState.getInstance().getSchema();
    boolean selectSchema = false;
    if ((env == null) || (env.length() < 1) || (env.startsWith("Select")))
    {
      selectSchema = true;
    }
    else
    {
      final Node node = SchemaCache.getCache().getNode(env, schema);
      if (node == null)
      {
        selectSchema = true;
      }
      else
      {
        WorkChart.getApp().setStatusBarText(node.getStatusString());
        ConnectionCache.getConnectionCache().setConnection(node);
      }
    }
    
    // Either there was no saved schema, or the schema could not be found
    if (selectSchema)
    {
      JOptionPane.showMessageDialog(frame, "Please select a database on the next screen",
                  "Database Selection Required", JOptionPane.WARNING_MESSAGE);
      showSchemaDialog(false);
    }
  }
  
  
  public void resetCharts()
  {
    cbCharts.setSelectedIndex(0);
    
    // Clear the cache for the charts
    if (chartData != null)
    {
      for (PopChart pc : chartData)
      {
        pc.clearCache();
      }
    }
  }
  
  
  /**
   * Create some chart objects.
   */
  private void populateCharts()
  {
    // Create some chart options for the dropdown combo box
    chartData = new ArrayList<PopChart>(5);
    final boolean legend = true;
    final boolean urls = false;
    
    chartData.add(new StringNumChart("Field1 in Table1",
        "table1", "field1", true, legend, urls));
    chartData.add(new StringNumChart("Field2 in Tabl2",
        "table2", "field2", true, legend, urls));
  }
  
  
  /**
   * Set up the application frame.
   */
  private void setupFrame()
  {
    // Create and set up the window.
    frame = new JFrame(Utility.getAppName());
    
    // Have the frame call exitApp() whenever it closes
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    frame.addWindowListener(new WindowAdapter()
    {
      @Override
      public void windowClosing(final WindowEvent e)
      {
        exitApp();
      }
    });
    
    // Set up the size of the frame
    frame.setPreferredSize(windowState.getSize());
    frame.setSize(windowState.getSize());
    
    // Set the position
    if (windowState.shouldCenter())
    {
      frame.setLocationRelativeTo(null);
    }
    else
    {
      frame.setLocation(windowState.getPosition());
    }
  }
  
  
  /**
   * Initialize the main screen (middle window).
   */
  private void initScreen()
  {
    // Get the titles of the charts
    String[] chartTitles = PopChart.getChartTitles(chartData, true);
    
    // Set up the top panel
    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 0));
    topPanel.setBackground(Color.WHITE);
    final int spacing = 15;
    topPanel.setBorder(BorderFactory.createEmptyBorder(spacing, spacing, spacing, spacing));
    cbCharts = new JComboBox<String>(chartTitles);
    cbCharts.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent arg0)
      {
        final int chartIndex = cbCharts.getSelectedIndex();
        loadChartFor(chartIndex);
      }
    });
    topPanel.add(cbCharts);
    
    // Add a button to set the database sources
    Action srcAction = new AbstractAction("Source")
    {
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent e)
      {
        showSchemaDialog(true);
      }
    };
    final JButton btnSources = new JButton(srcAction);
    btnSources.setMnemonic(KeyEvent.VK_S);
    btnSources.getActionMap().put("openSources", srcAction);
    btnSources.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK), "openSources");
    
    topPanel.add(btnSources);
    
    // Set up the chart panel
    chartPanel = new JPanel(new BorderLayout());
    chartPanel.setBackground(Color.WHITE);
    chartPanel.add(new JLabel(""), BorderLayout.CENTER);
    
    // Set up the middle panel
    JPanel appPanel = new JPanel(new BorderLayout());
    appPanel.add(chartPanel, BorderLayout.CENTER);
    appPanel.add(topPanel, BorderLayout.NORTH);
    frame.getContentPane().add(appPanel, BorderLayout.CENTER);
  }
  
  
  protected void showSchemaDialog(final boolean connAlreadySet)
  {
    // Show the dialog box to select database sources
    SchemaDialog dlg = new SchemaDialog(frame, "Select Sources", true, connAlreadySet);
    dlg.pack();
    dlg.setLocationRelativeTo(frame);
    dlg.setVisible(true);
  }
  
  
  private void loadChartFor(final int chartNum)
  {
    // Remove any existing chart
    chartPanel.removeAll();
    if (chartNum < 1)
    {
      frame.invalidate();
      frame.repaint();
      frame.pack();
      return;
    }
    
    PopChart root = chartData.get(chartNum - 1);
    JFreeChart chart = root.getFreeChart();
    
    // Create a dataset
//    DefaultPieDataset dataset = new DefaultPieDataset();
//    dataset.setValue("Category 1", 43.2);
//    dataset.setValue("Category 2", 27.9);
//    dataset.setValue("Category 3", 79.5);
    
    // Create a pie chart - with legend and tooltips, but no URLs
//    JFreeChart chart = ChartFactory.createPieChart("Sample Pie Chart " + chartNum,
//                          dataset, true, true, false);
    
    // Add the chart
    chartPanel.add(new ChartPanel(chart), BorderLayout.CENTER);
    
    // Update the frame layout
    frame.pack();
  }
  
  
  /**
   * Initialize the status bar.
   */
  private void initStatusBar()
  {
    // Instantiate the status bar
    statusBar = new JLabel("Ready");
    
    // Set the color and border
    statusBar.setForeground(Color.black);
    statusBar.setBorder(new CompoundBorder(new EmptyBorder(2, 2, 2, 2),
                              new SoftBevelBorder(SoftBevelBorder.LOWERED)));
    
    // Add to the content pane
    frame.getContentPane().add(statusBar, BorderLayout.SOUTH);
  }
  
  
  public void setStatusBarText(final String msg)
  {
    if (msg == null)
    {
      statusBar.setText("Ready");
    }
    else
    {
      statusBar.setText(msg);
    }
  }
  
  
  /**
   * Exit the application.
   */
  public void exitApp()
  {
    // System.out.println("Exiting the application...");
    
    // Close any connections
    ConnectionCache.getConnectionCache().closeConnection();
    
    // Store the window state in the properties file
    windowState.update(frame.getBounds());
    windowState.saveToFile(PROPS_FILE_NAME);
    
    // Close the application by disposing of the frame
    frame.dispose();
  }
  
  
  /**
   * Return the one instance of this class.
   * 
   * @return this instance
   */
  public static WorkChart getApp()
  {
    return app;
  }
  
  
  /**
   * Check how the application is run and save information
   * about the input file.
   */
  private void checkInputFileSource()
  {
    final java.net.URL url = getClass().getResource("/appicon.png");
    if (url != null)
    {
      // We're running in a jar file
      Utility.readFilesAsStream(true);
    }
    else
    {
      // We're not running in a jar file
      Utility.readFilesAsStream(false);
    }
  }
  
  
  /**
   * Entry point to the application.
   * 
   * @param args arguments passed to the application
   */
  public static void main(final String[] args)
  {
    // Make the application Mac-compatible
    Utility.makeMacCompatible();
    
    // Load the properties file data
    WindowState.load(PROPS_FILE_NAME);
    
    // Schedule a job for the event-dispatching thread:
    // creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        // Run the application
        app.createGUI();
      }
    });
  }
}
