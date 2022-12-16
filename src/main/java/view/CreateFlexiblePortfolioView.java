package main.java.view;

import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.event.DocumentListener;

import main.java.controller.IFrameController;

/**
 * This class represent the graphical user interface of the creation of flexible portfolio flow. It
 * takes the name of portfolio and number of portfolio as input. Each field state is stored and upon
 * change the document listener would alert the controller on the change. The controller would then
 * validate the inputs and this view would display the feedback by using error labels.
 */
public class CreateFlexiblePortfolioView extends JFrame implements IFrameView {
  private final JButton mainMenuButton;
  private final JTextField portfolioName;
  private final JTextField numberOfStocks;
  private final JButton submitButton;

  private final JPanel mainPanel;

  private JLabel portfolioErrorLabel;

  private JLabel numStocksErrorLabel;

  /**
   * Constructor is the implementation of JFrame which is used to display the creation of flexible
   * portfolio user flow.
   *
   * @param controller the instance of controller to interact with the view.
   */
  public CreateFlexiblePortfolioView(IFrameController controller) {
    this.getContentPane().removeAll();
    this.setTitle("Create Flexible Portfolio");
    this.setSize(1000, 1000);
    this.setLocation(100, 100);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    submitButton = new JButton("Submit");
    portfolioName = new JTextField(10);
    numberOfStocks = new JTextField(10);
    portfolioErrorLabel = addErrorLabel(portfolioErrorLabel);
    numStocksErrorLabel = addErrorLabel(numStocksErrorLabel);

    mainPanel = new JPanel(new GridLayout(6, 1, 1, 4));

    portfolioName.setToolTipText("Enter The Portfolio Name");
    numberOfStocks.setToolTipText("Enter any Positive Integer");

    JLabel portfolioNameLabel = new JLabel("Enter Portfolio Name:");
    addToMainPanel(Arrays.asList(portfolioNameLabel, portfolioName));

    addToMainPanel(Collections.singletonList(portfolioErrorLabel));


    JLabel numberOfStocksLabel = new JLabel("Enter Number of Stocks:");
    addToMainPanel(Arrays.asList(numberOfStocksLabel, numberOfStocks));

    addToMainPanel(Collections.singletonList(numStocksErrorLabel));

    submitButton.setActionCommand("Submit Portfolio");
    submitButton.setEnabled(false);
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
   * This method adds listener to the portfolio Name and number of stocks text fields.
   *
   * @param listener The document listener from controller that needs to be attached
   */
  @Override
  public void addDocumentListener(DocumentListener listener) {
    portfolioName.getDocument().putProperty("name", "portfolioName");
    numberOfStocks.getDocument().putProperty("name", "numberOfStocks");
    portfolioName.getDocument().addDocumentListener(listener);
    numberOfStocks.getDocument().addDocumentListener(listener);
  }

  /**
   * It returns current portfolio name and number of stocks upon the call of controller.
   *
   * @return map of strings
   */
  @Override
  public Map<String, String> getFields() {
    Map fields = new HashMap<>();
    fields.put("portfolioName", portfolioName.getText());
    fields.put("numberOfStocks", numberOfStocks.getText());
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
   * It sets error label in portfolio name and number of stocks label.
   *
   * @param component The component that needs to show the error
   * @param error     The error string.
   */
  @Override
  public void setErrorLabels(String component, String error) {
    switch (component) {
      case "portfolio":
        portfolioErrorLabel.setText(error);
        break;
      case "numStocks":
        numStocksErrorLabel.setText(error);
        break;
      default:
        break;
    }
  }

}
