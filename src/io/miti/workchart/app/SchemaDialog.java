package io.miti.workchart.app;

import java.awt.Component;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import io.miti.workchart.cache.ConnectionCache;
import io.miti.workchart.cache.SchemaCache;
import io.miti.workchart.dbutil.Database;
import io.miti.workchart.model.Node;
import io.miti.workchart.util.WindowState;

public final class SchemaDialog extends JDialog
{
  /** Default serial ID. */
  private static final long serialVersionUID = 1L;
  
  private JComboBox<String> cbEnvs = null;
  private JComboBox<String> cbNodes = null;
  
  private JTextField tfUser = null;
  
  private JButton btnTestConn = null;
  
  private JButton btnSave = null;
  
  private static final String CB_PROTOTYPE  = "XXXXXXXXX";
  
  /**
   * Default constructor.
   */
  public SchemaDialog()
  {
    super();
  }
  
  
  /**
   * Constructor taking the required fields.
   * 
   * @param owner the owner
   * @param title the title string
   * @param modal whether this is modal
   */
  public SchemaDialog(final Frame owner, final String title,
                      final boolean modal, final boolean connAlreadySet)
  {
    super(owner, title, modal);
    init(connAlreadySet);
    
    pack();
  }
  
  
  private void init(final boolean connAlreadySet)
  {
    // Create the panel and populate it
    final JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    final int bsize = 10;
    panel.setBorder(BorderFactory.createEmptyBorder(bsize, bsize, bsize, bsize));
    
    addTopGrid(panel);
    addButtonPanel(panel, connAlreadySet);
    
    setContentPane(panel);
    setResizable(false);
    
    // Select the saved values in the combo boxes
    showSavedSchemas();
  }
  
  
  private void addButtonPanel(JPanel panel, final boolean connAlreadySet)
  {
    // Create the Save button
    Action saveAction = new AbstractAction("  Save  ")
    {
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent e)
      {
        saveSchemaChanges();
        
        // Close the window
        dispose();
      }
    };
    btnSave = new JButton(saveAction);
    btnSave.setMnemonic(KeyEvent.VK_S);
    btnSave.getActionMap().put("saveSources", saveAction);
    btnSave.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK), "saveSources");
    
    // Create the close button
    Action closeAction = new AbstractAction("Cancel")
    {
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent e)
      {
        dispose();
      }
    };
    JButton btnClose = new JButton(closeAction);
    btnClose.setMnemonic(KeyEvent.VK_C);
    btnClose.getActionMap().put("closeSources", closeAction);
    btnClose.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK), "closeSources");
    btnClose.setEnabled(connAlreadySet);
    
    panel.add(Box.createVerticalStrut(20));
    btnClose.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
    panelButtons.add(btnSave);
    panelButtons.add(btnClose);
    panel.add(panelButtons);
  }
  
  
  protected void saveSchemaChanges()
  {
    // Get the selected info
    final String envName = (String) cbEnvs.getSelectedItem();
    final String schemaName = (String) cbNodes.getSelectedItem();
    
    // Save the changes to the properties file
    WindowState.getInstance().setEnvironment(envName);
    WindowState.getInstance().setSchema(schemaName);
    
    // Reset the charts (drop the cached results)
    WorkChart.getApp().resetCharts();
    
    // Save the connection parameters to ConnectionCache
    final Node selNode = SchemaCache.getCache().getNode(envName, schemaName);
    WorkChart.getApp().setStatusBarText(selNode.getStatusString());
    ConnectionCache.getConnectionCache().setConnection(selNode);
  }
  
  
  private void showSavedSchemas()
  {
    // If schemas were saved previously, select them now
    final String env = WindowState.getInstance().getEnvironment();
    final String olap = WindowState.getInstance().getSchema();
    
    // Only continue if the environment variable is set to a valid value
    if ((env != null) && (env.length() > 0))
    {
      // Select the string
      cbEnvs.setSelectedItem(env);
      
      // Enable the Save button if anything other than the first environment is selected
      final int selectedIndex = cbEnvs.getSelectedIndex();
      btnSave.setEnabled(selectedIndex > 0);
      
      if ((olap != null) && (olap.length() > 0))
      {
        cbNodes.setSelectedItem(olap);
      }
    }
    else
    {
      btnSave.setEnabled(false);
    }
  }
  
  
  private void addTopGrid(final JPanel topPanel)
  {
    // Create the panel - 2 columns, 3 hgap, 5 vgap
    // final JPanel mainPanel = new JPanel(new GridLayout(0, 1, 3, 5));
    JPanel mainPanel = new JPanel();
    //mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setLayout(new GridLayout(4, 2, 5, 8));
    
    // Get the list of schemas
    final String[] envNames = getEnvironments();
    
    // Get the environments panel
    getEnvironmentsPanel(mainPanel, envNames);
    
    // Set up the OLAP section
    getOlapPanel(mainPanel);
    
    topPanel.add(mainPanel);
  }
  
  
  private JPanel getEnvironmentsPanel(final JPanel mainPanel, final String[] envNames)
  {
    // Get the environments panel
    cbEnvs = new JComboBox<String>(envNames);
    cbEnvs.setPrototypeDisplayValue(CB_PROTOTYPE);
    cbEnvs.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent arg0)
      {
        final String src = (String) cbEnvs.getSelectedItem();
        final int srcIndex = cbEnvs.getSelectedIndex();
        loadConnInfoFor(src, srcIndex);
        
        if (btnSave != null)
        {
          final int selectedIndex = cbEnvs.getSelectedIndex();
          btnSave.setEnabled(selectedIndex > 0);
        }
      }
    });
    
    cbEnvs.setAlignmentX(LEFT_ALIGNMENT);
    JLabel lblEnv = new JLabel("Environments:", JLabel.RIGHT);
    // JPanel envPanel = new JPanel();
    mainPanel.add(lblEnv);
    mainPanel.add(cbEnvs);
    
    return mainPanel;
  }
  
  
  private String[] getEnvironments()
  {
    // Create the combo box
    final List<String> envs = SchemaCache.getCache().getEnvironments();
    envs.add(0, "Select one");
    String[] envNames = new String[envs.size()];
    int i = 0;
    for (String env : envs)
    {
      envNames[i++] = env;
    }
    
    return envNames;
  }
  
  
  private JPanel getOlapPanel(final JPanel olapPanel)
  {
    // JPanel olapPanel = new JPanel(new GridLayout(0, 2, 3, 5));
    // olapPanel.setBorder(BorderFactory.createTitledBorder("User"));
    olapPanel.add(new JLabel("Name:", JLabel.RIGHT));
    Vector<String> olapItems = new Vector<String>();
    olapItems.add("Select one");
    cbNodes = new JComboBox<String>(new DefaultComboBoxModel<String>(olapItems));
    cbNodes.setPrototypeDisplayValue(CB_PROTOTYPE);
    cbNodes.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent arg0)
      {
        updateDisplayedUserName();
      }
    });
    
    cbNodes.setEnabled(false);
    olapPanel.add(cbNodes);
    
    olapPanel.add(new JLabel("User ID:", JLabel.RIGHT));
    tfUser = new JTextField("Select one");
    tfUser.setEnabled(false);
    olapPanel.add(tfUser);
    
    Action atnTestOlap = new AbstractAction("Test")
    {
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent e)
      {
        testConn();
      }
    };
    btnTestConn = new JButton(atnTestOlap);
    btnTestConn.setEnabled(false);
    olapPanel.add(new JLabel("Test:", JLabel.RIGHT));
    olapPanel.add(btnTestConn);
    
    return olapPanel;
  }
  
  
  /**
   * When the user changes the selected OLAP node, update the
   * displayed user ID.
   * 
   * @param isOlap whether this is OLAP
   */
  protected void updateDisplayedUserName()
  {
    String schema = (String) cbEnvs.getSelectedItem();
    String name = (String) cbNodes.getSelectedItem();
    Node node = SchemaCache.getCache().getNode(schema, name);
    if (node == null)
    {
      return;
    }
    
    tfUser.setText(node.getId());
  }
  
  
  private void testConn()
  {
    // Get the parameters for the connection - url, ID, PW
    String schema = (String) cbEnvs.getSelectedItem();
    String name = (String) cbNodes.getSelectedItem();
    Node node = SchemaCache.getCache().getNode(schema, name);
    // System.out.println(node.toString());
    
    // Check the connection
    Connection conn = Database.createConnection(node.getUrl(), node.getId(), node.getPw());
    boolean isValid = (conn != null);
    if (isValid)
    {
      Database.closeConnection(conn);
      conn = null;
    }
    
    if (isValid)
    {
      JOptionPane.showMessageDialog(this, "The database connection is valid", "DB Status",
                                    JOptionPane.INFORMATION_MESSAGE);
    }
    else
    {
      JOptionPane.showMessageDialog(this, "The database connection is NOT valid", "DB Status",
                                    JOptionPane.ERROR_MESSAGE);
    }
  }
  
  
  private void loadConnInfoFor(String src, int srcIndex)
  {
    if (srcIndex == 0)
    {
      // Disable everything
      cbNodes.setEnabled(false);
      btnTestConn.setEnabled(false);
      
      tfUser.setText("Select one");
      
      cbNodes.removeAllItems();
      DefaultComboBoxModel<String> olapModel = (DefaultComboBoxModel<String>) cbNodes.getModel();
      olapModel.addElement("Select one");
    }
    else
    {
      cbNodes.removeAllItems();
      
      // Populate cbOlap and tfOlapUser, enable test
      cbNodes.setEnabled(true);
      btnTestConn.setEnabled(true);
      tfUser.setText("");
      
      // Iterate over the nodes
      List<Node> olapSchemas = SchemaCache.getCache().getSchemas(src);
      DefaultComboBoxModel<String> olapModel = (DefaultComboBoxModel<String>) cbNodes.getModel();
      for (Node node : olapSchemas)
      {
        olapModel.addElement(node.getNode());
      }
      tfUser.setText(olapSchemas.get(0).getId());
    }
  }
}
