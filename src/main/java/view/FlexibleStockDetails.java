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
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ButtonGroup;
import javax.swing.event.DocumentListener;

import main.java.controller.IFrameController;

/**
 * This class represent the graphical user interface of the flexible input stock details. It takes
 * stock name, date, transaction type and quantity of stocks. Each field state is stored and upon
 * change the document listener would alert the controller on the change. The controller would then
 * validate the inputs and this view would display the feedback by using error labels.
 */
public class FlexibleStockDetails extends JFrame implements IFrameView {
  private final JButton mainMenuButton;
  private final JXDatePicker calendar;
  private final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
  private final JRadioButton buy;
  private final JRadioButton sell;
  private final JTextField quantityOfStocks;
  private final JTextField stockName;
  private final JButton submitButton;
  private final JPanel mainPanel;
  Map fields;
  private JLabel stockNameErrorLabel;

  private JLabel quantityOfStocksErrorLabel;

  /**
   * Constructor takes number of stock, max number of stocks, portfolio number and controller to
   * make sure there is input taken of stock details and mapped to the concerned portfolio number.
   *
   * @param numStock        the stock number which on which detail must be taken
   * @param maxNumberStocks the max number of stock which needs to be taken
   * @param portfolioNumber The portfolio number
   * @param controller      The instance of controller
   */
  public FlexibleStockDetails(int numStock, int maxNumberStocks, int portfolioNumber,
                              IFrameController controller) {
    fields = new HashMap<>();
    fields.put("numStock", "" + numStock);
    fields.put("maxStocks", "" + maxNumberStocks);
    fields.put("portfolioNumber", "" + portfolioNumber);
    stockName = new JTextField(10);
    quantityOfStocks = new JTextField(10);
    stockName.setToolTipText("Stock Name should be from List of Validated Stocks");
    quantityOfStocks.setToolTipText("Enter any Positive Integer");
    stockNameErrorLabel = addErrorLabel(stockNameErrorLabel);
    quantityOfStocksErrorLabel = addErrorLabel(quantityOfStocksErrorLabel);
    submitButton = new JButton("Submit");

    calendar = new JXDatePicker();
    calendar.setDate(Calendar.getInstance().getTime());
    calendar.setFormats(dateformat);
    calendar.getMonthView().setUpperBound(Calendar.getInstance().getTime());
    calendar.getEditor().setEditable(false);

    buy = new JRadioButton("Buy", true);
    sell = new JRadioButton("Sell");
    ButtonGroup group = new ButtonGroup();
    group.add(buy);
    group.add(sell);

    this.getContentPane().removeAll();
    this.setTitle("Stock Detail " + numStock);
    this.setSize(1000, 1000);
    this.setLocation(100, 100);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    mainPanel = new JPanel(new GridLayout(9, 1));
    JLabel mainHeading = new JLabel("Stock Details " + numStock);
    //    mainHeading.setFont(new Font(mainHeading.getFont().getFontName(),Font.PLAIN,10));
    addToMainPanel(List.of(mainHeading));

    JLabel stockNameLabel = new JLabel("Stock Name:");
    addToMainPanel(Arrays.asList(stockNameLabel, stockName));

    addToMainPanel(Collections.singletonList(stockNameErrorLabel));


    JLabel numStocksLabel = new JLabel("Number of Stocks:");
    addToMainPanel(Arrays.asList(numStocksLabel, quantityOfStocks));

    addToMainPanel(Collections.singletonList(quantityOfStocksErrorLabel));

    JLabel buyDateLabel = new JLabel("Date of Purchase");
    addToMainPanel(Arrays.asList(buyDateLabel, calendar));

    JLabel radioButtonLabel = new JLabel("Buy/Sell");
    addToMainPanel(Arrays.asList(radioButtonLabel, buy, sell));

    submitButton.setActionCommand("Submit Stock Details");
    addToMainPanel(List.of(submitButton));

    mainMenuButton = new JButton("Main Menu");
    mainMenuButton.setActionCommand("Main Menu");
    mainMenuButton.setEnabled(true);
    mainMenuButton.setToolTipText("Main Menu");
    addToMainPanel(List.of(mainMenuButton));

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
   * This method adds listener to the stock Name and quantity of stocks text fields.
   *
   * @param listener The document listener from controller that needs to be attached
   */
  @Override
  public void addDocumentListener(DocumentListener listener) {
    stockName.getDocument().putProperty("name", "stockName");
    quantityOfStocks.getDocument().putProperty("name", "quantityOfStocks");
    stockName.getDocument().addDocumentListener(listener);
    quantityOfStocks.getDocument().addDocumentListener(listener);
  }

  /**
   * It returns current stock name, quantity, datePurchased and transaction type upon the call of
   * controller.
   *
   * @return map of strings
   */
  @Override
  public Map<String, String> getFields() {

    fields.put("stockName", stockName.getText());
    fields.put("quantityOfStocks", quantityOfStocks.getText());
    fields.put("datePurchased",
            calendar.getDate() == null ? "" : dateformat.format(calendar.getDate()));
    String selected = buy.isSelected() ? buy.getText().toLowerCase() : sell.getText().toLowerCase();
    fields.put("transactionType", "" + selected.charAt(0));
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
   * It sets error label in portfolio name and quantity label.
   *
   * @param component The component that needs to show the error
   * @param error     The error string.
   */
  @Override
  public void setErrorLabels(String component, String error) {
    if (component.equalsIgnoreCase("stockName")) {
      stockNameErrorLabel.setText(error);
    } else {

      quantityOfStocksErrorLabel.setText(error);
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
