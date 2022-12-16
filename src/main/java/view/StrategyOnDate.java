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
import javax.swing.JCheckBox;
import javax.swing.event.DocumentListener;

import main.java.controller.FrameController;

/**
 * This class is used to get the investment amount, frequency and dates from the user then validate
 * each of the input from user before sending it to the controller. This also takes into account for
 * the recurring transactions.
 */
public class StrategyOnDate extends JFrame implements IFrameView {
  private final JButton mainMenuButton;
  private final JTextField amount;
  private final JTextField numberOfStocks;
  private final JTextField frequency;
  private final JButton submitButton;
  private final JPanel mainPanel;
  private final Map fields = new HashMap<>();
  private final JXDatePicker anotherCalendar;
  private final int portfolioNumber;
  private final boolean isOnADate;
  private final JXDatePicker calendar;
  private final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
  private JLabel frequencyErrorLabel;
  private JCheckBox checkBox;
  private JLabel amountErrorLabel;
  private JLabel numStocksErrorLabel;

  /**
   * Constructor is used the display the amount field, commission, two dates if on date range, check
   * box on recurring. This would mean user can have option to get the strategy on a date, between
   * two date ranges and recurring dates using the same view.
   *
   * @param commission      The commission of the system
   * @param numStocks       The number of stocks
   * @param controller      The instance of controller
   * @param portfolioNumber The portfolio number
   * @param isOnaDate       boolean whether it is on a date range
   */
  public StrategyOnDate(double commission, int numStocks, FrameController controller,
                        int portfolioNumber,
                        boolean isOnaDate) {
    fields.put("numStocks", "" + numStocks);
    this.getContentPane().removeAll();
    this.setTitle("Execute Strategy on a Date");
    this.setSize(1000, 1000);
    this.setLocation(100, 100);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    submitButton = new JButton("Submit");
    this.portfolioNumber = portfolioNumber;
    this.isOnADate = isOnaDate;
    amount = new JTextField(10);
    numberOfStocks = new JTextField(10);
    frequency = new JTextField(10);

    amountErrorLabel = addErrorLabel(amountErrorLabel);
    numStocksErrorLabel = addErrorLabel(numStocksErrorLabel);
    frequencyErrorLabel = addErrorLabel(frequencyErrorLabel);

    calendar = new JXDatePicker();
    calendar.setDate(Calendar.getInstance().getTime());
    calendar.setFormats(dateformat);
    calendar.getEditor().setEditable(false);

    anotherCalendar = new JXDatePicker();
    anotherCalendar.setDate(Calendar.getInstance().getTime());
    anotherCalendar.setFormats(dateformat);
    anotherCalendar.getEditor().setEditable(false);

    mainPanel = new JPanel(new GridLayout(isOnaDate ? 7 : 11, 1, 1, 4));

    amount.setToolTipText("Enter Amount more than 1$");
    numberOfStocks.setToolTipText("Enter any Positive Integer");

    JLabel amountLabel = new JLabel("Enter Amount:");
    addToMainPanel(Arrays.asList(amountLabel, amount));

    addToMainPanel(Collections.singletonList(amountErrorLabel));

    if (isOnaDate) {
      JLabel numberOfStocksLabel =
              new JLabel("Number of existing stocks in portfolio: " + numStocks);
      addToMainPanel(List.of(numberOfStocksLabel));
      numberOfStocks.setText("" + numStocks);
    } else {
      JLabel numberOfStocksLabel = new JLabel("Number of Stocks:");
      addToMainPanel(Arrays.asList(numberOfStocksLabel, numberOfStocks));
    }


    JLabel dateLabel = new JLabel("Date of Execution");
    addToMainPanel(Arrays.asList(dateLabel, calendar));

    if (!isOnaDate) {
      JLabel dateLabel2 = new JLabel("End Date of Execution");
      addToMainPanel(Arrays.asList(dateLabel2, anotherCalendar));
      checkBox = new JCheckBox("Tick for Recurring Strategy", false);
      addToMainPanel(List.of(checkBox));
      JLabel frequencyLabel = new JLabel("Enter The Frequency:");
      addToMainPanel(Arrays.asList(frequencyLabel, frequency));
      addToMainPanel(Collections.singletonList(frequencyErrorLabel));

    }


    addToMainPanel(List.of(new JLabel("Commission for this strategy is: " + commission)));

    submitButton.setActionCommand(isOnaDate ? "Submit on Date Strategy" : "Submit on Date Range");
    submitButton.setEnabled(false);
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
   * returns the frame object of the view.
   *
   * @return the frame of the view.
   */
  @Override
  public JFrame getFrame() {
    return this;
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
   * This method adds listener to the invested amount, number of stocks and frequency text fields.
   *
   * @param listener The document listener from controller that needs to be attached
   */
  @Override
  public void addDocumentListener(DocumentListener listener) {
    amount.getDocument().addDocumentListener(listener);
    numberOfStocks.getDocument().addDocumentListener(listener);
    frequency.getDocument().addDocumentListener(listener);
  }

  /**
   * It returns current invested amount, num stocks, date, whethere its on a date, portfolio number,
   * if it is not on a date the is it recurring, the frequency of investment and second date upon
   * the call of controller.
   *
   * @return map of strings
   */
  @Override
  public Map<String, String> getFields() {
    fields.put("amount", amount.getText());
    fields.put("numStocks", numberOfStocks.getText());
    fields.put("date", calendar.getDate() == null ? "" : dateformat.format(calendar.getDate()));
    fields.put("onADate", isOnADate ? "true" : "false");
    fields.put("portfolioNumber", "" + portfolioNumber);
    if (!isOnADate) {
      fields.put("anotherDate", anotherCalendar.getDate() == null ? "" :
              dateformat.format(anotherCalendar.getDate()));
      fields.put("recurring", checkBox.isSelected() ? "true" : "false");
      fields.put("frequency", frequency.getText());
    }
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
   * It sets error label in amount, number of stocks and frequency label.
   *
   * @param component The component that needs to show the error
   * @param error     The error string.
   */
  @Override
  public void setErrorLabels(String component, String error) {
    if (component.equals("amount")) {
      amountErrorLabel.setText(error);
    } else if (component.equals("numStocks")) {
      numStocksErrorLabel.setText(error);
    } else {
      frequencyErrorLabel.setText(error);
    }
  }
}
