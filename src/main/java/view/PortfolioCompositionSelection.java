package main.java.view;

import org.jdesktop.swingx.JXDatePicker;

import java.awt.Dimension;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.DocumentListener;

import main.java.controller.IFrameController;

/**
 * This class represents the view class for portfolio composition, this will help use user to select
 * from existing portfolios and conduct operation on the selected composition.
 */
public class PortfolioCompositionSelection extends JFrame implements IFrameView {

  private final JButton mainMenuButton;
  private final JButton createButton;
  private final JButton submitButton;

  private final JPanel mainPanel;

  private final JComboBox<String> dropDown;

  private final JXDatePicker calendar;
  private final JXDatePicker anotherCalendar;

  private final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

  private final String mode;

  /**
   * This view would take method which needs to show portfolio composition and manipulate the
   * internal field of button/text fields based on the given method. For e.g: in 'graph' method we
   * want two date fields with the composition; 'executeOnDateRange' we need two dates and a
   * checkbox  along with the composition.
   *
   * @param listOfPortfolio List of portfolio details
   * @param caption         the title of frame
   * @param method          The method which functionality need to reuse this functionality
   * @param controller      The instance of controller
   */
  public PortfolioCompositionSelection(List<String> listOfPortfolio, String caption,
                                       String method, IFrameController controller) {
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

    int portfolioSize = listOfPortfolio.size();

    String[] columnHeaders = new String[4];
    columnHeaders[0] = "id";
    columnHeaders[1] = "Portfolio Name";
    columnHeaders[2] = "Number of Stocks";
    columnHeaders[3] = "Date of Creation";

    String[][] table = new String[portfolioSize][4];


    String[] choices = new String[portfolioSize];

    for (int index = 0; index < listOfPortfolio.size(); index++) {
      String[] listOfSplittedstrings = listOfPortfolio.get(index).split("\\|");
      table[index][0] = String.valueOf(index + 1);
      table[index][1] = listOfSplittedstrings[0];
      table[index][2] = listOfSplittedstrings[2];
      table[index][3] = listOfSplittedstrings[1];
      choices[index] = "" + (index + 1);
    }


    submitButton = new JButton("Submit");
    submitButton.setActionCommand("Submit Portfolio Choice");
    submitButton.setEnabled(true);
    submitButton.setToolTipText("Choose the ID that you want to select");

    createButton = new JButton("Click Here");
    createButton.setActionCommand("Create Portfolio from Strategy");
    createButton.setEnabled(true);
    createButton.setToolTipText("Create Portfolio and then apply strategy");

    dropDown = new JComboBox<>(choices);
    mainPanel = new JPanel(new GridLayout(mode.equals("graph") ? 6 : 5, 1));

    JTable composition = new JTable(table, columnHeaders);
    composition.setBounds(100, 100, 100, 100);


    addToMainPanel(List.of(new JScrollPane(composition)));

    addToMainPanel(Arrays.asList(new JLabel("Select the Portfolio of your choice"), dropDown));

    if (Objects.equals("executeOnADateRange", method)) {
      addToMainPanel(Arrays.asList(new JLabel("To apply strategy on new Portfolio:"),
              createButton));
    }

    if (Arrays.asList("composition", "value", "costBasis").contains(method)) {
      addToMainPanel(Arrays.asList(new JLabel("Select corresponding Date"), calendar));
    }

    if (Objects.equals("graph", method)) {
      addToMainPanel(Arrays.asList(new JLabel("Select Beginning Date:"), calendar));
      addToMainPanel(Arrays.asList(new JLabel("Select End Date:"), anotherCalendar));
    }
    List<Component> components = Arrays.asList(new JLabel(method.equals("executeOnADateRange") ?
            "To " +
                    "apply " +
                    "strategy on selected Portfolio:" : ""), submitButton);
    addToMainPanel(components);

    mainMenuButton = new JButton("Main Menu");
    mainMenuButton.setActionCommand("Main Menu");
    mainMenuButton.setEnabled(true);
    mainMenuButton.setToolTipText("Main Menu");
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
   * returns the frame object of the view.
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
    createButton.addActionListener(listener);
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
    Map fields = new HashMap<>();
    fields.put("selectedPortfolio", dropDown.getSelectedItem());
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
