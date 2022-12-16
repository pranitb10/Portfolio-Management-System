package main.java.view;

import org.jdesktop.swingx.JXDatePicker;

import java.awt.GridLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentListener;

import main.java.controller.IFrameController;
import main.java.model.util.Util;

/**
 * This class represents the view class for portfolio composition, this will help use user to select
 * from existing portfolios and conduct operation on the selected composition.
 */
public class StockPercentView extends JFrame implements IFrameView {

  private final JButton mainMenuButton;
  private final JButton submitButton;
  private final JPanel mainPanel;
  private final JXDatePicker calendar;
  private final JXDatePicker anotherCalendar;
  private final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
  private final String mode;
  private final JTable dTable;

  /**
   * This view would take method which needs to show portfolio composition and manipulate the
   * internal field of button/text fields based on the given method. For e.g: in 'graph' method we
   * want two date fields with the composition; 'executeOnDateRange' we need two dates and a
   * checkbox  along with the composition.
   *
   * @param stocks     List of portfolio details
   * @param caption    the title of frame
   * @param method     The method which functionality need to reuse this functionality
   * @param controller The instance of controller
   */
  public StockPercentView(List<String> stocks, String caption, String method,
                          IFrameController controller) {
    mode = method;
    this.getContentPane().removeAll();
    this.setTitle(caption);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    calendar = new JXDatePicker();
    calendar.setDate(Calendar.getInstance().getTime());
    calendar.setFormats(dateformat);
    calendar.getMonthView().setUpperBound(Calendar.getInstance().getTime());
    calendar.getEditor().setEditable(false);

    anotherCalendar = new JXDatePicker();
    anotherCalendar.setDate(Calendar.getInstance().getTime());
    anotherCalendar.setFormats(dateformat);
    anotherCalendar.getMonthView().setUpperBound(Calendar.getInstance().getTime());
    anotherCalendar.getEditor().setEditable(false);

    int stockSize = stocks.size();

    String[] columnHeaders = new String[5];
    columnHeaders[0] = "id";
    columnHeaders[1] = "Stock name";
    columnHeaders[2] = "Stock symbol";
    columnHeaders[3] = "Stock quantity";
    columnHeaders[4] = "New percentage of Stock(%)";


    String[][] table = new String[stockSize][5];


    String[] choices = new String[stockSize];

    for (int index = 0; index < stockSize; index++) {
      String[] listOfSplittedstrings = stocks.get(index).split("\\|");
      table[index][0] = String.valueOf(index + 1);
      table[index][1] = listOfSplittedstrings[0];
      table[index][2] = listOfSplittedstrings[1];
      table[index][3] = listOfSplittedstrings[2];
      table[index][4] = "";

      //choices[index] = "" + (index + 1);
    }


    submitButton = new JButton("Submit ");
    submitButton.setActionCommand("Rebalance portfolio");
    submitButton.setEnabled(true);
    submitButton.setToolTipText("Set the percentage for each stock");

    mainPanel = new JPanel(new GridLayout(mode.equals("graph") ? 6 : 5, 1));

    dTable = new JTable(table, columnHeaders);

    dTable.setBounds(100, 100, 100, 100);


    addToMainPanel(List.of(new JScrollPane(dTable)));


    mainMenuButton = new JButton("Main Menu");
    mainMenuButton.setActionCommand("Main Menu");
    mainMenuButton.setEnabled(true);
    mainMenuButton.setToolTipText("Main Menu");
    addToMainPanel(Collections.singletonList(submitButton));

    addToMainPanel(Collections.singletonList(mainMenuButton));

    this.getContentPane().add(mainPanel);
    pack();
    setVisible(true);
  }

  private void addToMainPanel(List<Component> listOfComponents) {
    JPanel panel = new JPanel(new FlowLayout());
    for (Component component : listOfComponents) {
      panel.add(component);
    }
    mainPanel.add(panel);
  }

  /**
   * Returns the frame object of the view.
   *
   * @return the frame of the view.
   */
  @Override
  public JFrame getFrame() {
    return this;
  }

  /**
   * This method adds listener to submit, create and the main menu button.
   *
   * @param listener The action listener from controller that needs to be attached
   */
  @Override
  public void addActionListener(ActionListener listener) {
    submitButton.addActionListener(listener);
    mainMenuButton.addActionListener(listener);
  }

  /**
   * This method adds listener to text fields.
   *
   * @param listener The document listener from controller that needs to be attached
   */
  @Override
  public void addDocumentListener(DocumentListener listener) {
    // There are no fields in documents to listen to.
  }

  /**
   * It returns selected portfolio, two dates and method in which composition is called upon the
   * call of controller.
   *
   * @return map of strings
   */
  @Override
  public Map<String, String> getFields() {
    Map<String, String> fields = new HashMap<String, String>();
    //fields.put("selectedPortfolio", dropDown.getSelectedItem().toString());
    String newPercentageTable = "";
    for (int i = 0; i < dTable.getRowCount(); i++) {
      newPercentageTable += dTable.getModel().getValueAt(i, 2) + ":"
              + dTable.getModel().getValueAt(i, 3) + Util.FILE_OUTPUT_SEPARATOR;
    }
    fields.put("percentages", newPercentageTable);
    fields.put("dateOpted1",
            calendar.getDate() == null ? "" : dateformat.format(calendar.getDate()));
    fields.put("dateOpted2",
            anotherCalendar.getDate() == null ? "" : dateformat.format(anotherCalendar.getDate()));
    fields.put("method", mode);
    return fields;
  }

  /**
   * This would allow us to dynamically set the size of the bar chart.
   *
   * @return Dimension object which sets the size
   */
  @Override
  public Dimension getPreferredSize() {
    return new Dimension(500, 400);
  }

  /**
   * This function is used to enable and disable submit button.
   *
   * @param enable Boolean to enable button or not.
   */
  @Override
  public void setSubmit(boolean enable) {
    this.submitButton.setEnabled(enable);
  }

  /**
   * It sets error labels in this case we don't set any error label.
   *
   * @param component The component that needs to show the error
   * @param error     The error string.
   */
  @Override
  public void setErrorLabels(String component, String error) {
    // There are no error labels to set in the field.
  }
}
