package main.java.view;

import org.jdesktop.swingx.JXDatePicker;

import java.awt.Color;
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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.event.DocumentListener;


/**
 * This class is used to give to allow user to enter the stock name/ show the stock name based on
 * the situation (on a date it shows the stock name and on a date range it allows user to enter the
 * stock name) and weightage of the input.
 */
public class StrategyDistribution extends JFrame implements IFrameView {
  private final Map fields;
  private final JButton mainMenuButton;
  private final JXDatePicker calendar;
  private final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
  private final JTextField weightageOfStocks;
  private final JTextField stockName;
  private final JButton submitButton;

  private final JPanel mainPanel;

  private JLabel stockNameErrorLabel;

  private JLabel weightageOfStocksErrorLabel;

  /**
   * Constructor takes the number of stock, max number of stock, stock name and portfolio number and
   * gets the input from the user.
   *
   * @param numStock        the stock number for which the details need to take
   * @param maxNumberStocks the maximum number of stocks for which the details need to be
   *                        taken
   * @param stockNameInput  The stock name from controller will be empty if user needs to
   *                        enter
   * @param portfolioNumber The portfolio number on which stock details are entered.
   */
  public StrategyDistribution(int numStock, int maxNumberStocks, String stockNameInput,
                              int portfolioNumber) {
    fields = new HashMap<>();
    fields.put("numStock", "" + numStock);
    fields.put("maxStocks", "" + maxNumberStocks);
    fields.put("portfolioNumber", "" + portfolioNumber);
    stockName = new JTextField(10);
    weightageOfStocks = new JTextField(10);
    weightageOfStocks.setText(String.format("%.3f", 100f / maxNumberStocks));
    stockName.setToolTipText("Stock Name should be from List of Validated Stocks");
    weightageOfStocks.setToolTipText(
            "Enter Number between 0 - 100, by default its divided by " + "equal weightage");
    stockNameErrorLabel = addErrorLabel(stockNameErrorLabel);
    weightageOfStocksErrorLabel = addErrorLabel(weightageOfStocksErrorLabel);
    submitButton = new JButton("Submit");

    calendar = new JXDatePicker();
    calendar.setDate(Calendar.getInstance().getTime());
    calendar.setFormats(dateformat);
    calendar.getMonthView().setUpperBound(Calendar.getInstance().getTime());
    calendar.getEditor().setEditable(false);

    this.getContentPane().removeAll();
    this.setTitle("Stock Detail " + numStock);
    this.setSize(1000, 1000);
    this.setLocation(100, 100);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    mainPanel = new JPanel(new GridLayout(9, 1));
    JLabel mainHeading = new JLabel("Stock Details " + numStock);
    addToMainPanel(List.of(mainHeading));

    if (stockNameInput.equals("")) {
      JLabel stockNameLabel = new JLabel("Stock Name:");
      addToMainPanel(Arrays.asList(stockNameLabel, stockName));
      addToMainPanel(Collections.singletonList(stockNameErrorLabel));
    } else {
      stockName.setText(stockNameInput);
      JLabel stockNameInputLabel = new JLabel("Stock Name: " + stockNameInput);
      addToMainPanel(List.of(stockNameInputLabel));
    }

    JLabel weightageOfStocksLabel = new JLabel("Weightage: ");
    addToMainPanel(Arrays.asList(weightageOfStocksLabel, weightageOfStocks));

    addToMainPanel(Collections.singletonList(weightageOfStocksErrorLabel));


    submitButton.setActionCommand("Submit Weightage");
    addToMainPanel(Collections.singletonList(submitButton));

    mainMenuButton = new JButton("Main Menu");
    mainMenuButton.setActionCommand("Main Menu");
    mainMenuButton.setEnabled(true);
    mainMenuButton.setToolTipText("Main Menu");
    addToMainPanel(Collections.singletonList(mainMenuButton));

    this.getContentPane().add(mainPanel);
    pack();
    setVisible(true);
  }

  private JLabel addErrorLabel(JLabel errorLabel) {
    errorLabel = new JLabel("");
    errorLabel.setForeground(Color.RED);
    return errorLabel;
  }

  private void addToMainPanel(List<Component> listOfComponents) {
    JPanel panel = new JPanel(new FlowLayout());
    for (Component component : listOfComponents) {
      panel.add(component);
    }
    mainPanel.add(panel);
  }

  /**
   * This method adds listener to submit and the main menu button.
   *
   * @param listener The action listener from controller that needs to be attached
   */
  @Override
  public void addActionListener(ActionListener listener) {
    submitButton.addActionListener(listener);
    mainMenuButton.addActionListener(listener);
  }

  /**
   * This method adds listener to the stock Name and weightage of stocks text fields.
   *
   * @param listener The document listener from controller that needs to be attached
   */
  @Override
  public void addDocumentListener(DocumentListener listener) {
    stockName.getDocument().addDocumentListener(listener);
    weightageOfStocks.getDocument().addDocumentListener(listener);
  }

  /**
   * It returns current stock name, weightage of stocks and date upon the call of controller.
   *
   * @return map of strings
   */
  @Override
  public Map<String, String> getFields() {

    fields.put("stockName", stockName.getText());
    fields.put("weightageOfStocks", weightageOfStocks.getText());
    fields.put("date1", calendar.getDate() == null ? "" : dateformat.format(calendar.getDate()));
    return fields;
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
   * It sets error label in stock name and weightage of stocks label.
   *
   * @param component The component that needs to show the error
   * @param error     The error string.
   */
  @Override
  public void setErrorLabels(String component, String error) {
    if (component.equalsIgnoreCase("stockName")) {
      stockNameErrorLabel.setText(error);
    } else {
      weightageOfStocksErrorLabel.setText(error);

    }
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
}
