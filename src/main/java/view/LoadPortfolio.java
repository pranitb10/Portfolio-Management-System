package main.java.view;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.event.DocumentListener;

import main.java.controller.IFrameController;

/**
 * This class represent the implementation of load portfolio functionality for felxible portfolio In
 * this we take a csv file from user and load portfolio upon submission.
 */
public class LoadPortfolio extends JFrame implements IFrameView {

  private final JButton mainMenuButton;
  private final JTextField portfolioName;
  private final JButton submitButton;
  private final JPanel mainPanel;
  private final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
  private JLabel portfolioErrorLabel;
  private JFileChooser fileUpload;

  /**
   * Constructor would take the caption and controller instance and then use file chooser
   * functionality of java swing to take file as input.
   *
   * @param controller instance of controller
   * @param caption    The title of the window.
   */
  public LoadPortfolio(String caption, IFrameController controller) {
    this.getContentPane().removeAll();
    this.setTitle(caption);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    String[] columnHeaders = {"StockName", "Quantity", "TransactionDate", "TransactionType"};


    String[][] table = {
            {"ABBV", "35", "2022-04-12", "b"},
            {"AAPL", "100", "2022-05-10", "b"},
            {"MSFT", "100", "2022-11-11", "b"},
            {"MSFT", "40", "2022-11-12", "s"},
    };
    mainPanel = new JPanel(new GridLayout(6, 1));


    submitButton = new JButton("Choose File");
    submitButton.setActionCommand("Submit Loaded File");
    submitButton.setEnabled(false);
    submitButton.setToolTipText("Choose the ID that you want to select");


    JTable dataFormat = new JTable(table, columnHeaders);
    dataFormat.setBounds(100, 100, 100, 100);

    portfolioName = new JTextField(10);
    portfolioErrorLabel = addErrorLabel(portfolioErrorLabel);


    portfolioName.setToolTipText("Enter The Portfolio Name in ");

    addToMainPanel(List.of(new JLabel("Please make sure the data in the CSV file is in "
            + "format:")));

    addToMainPanel(List.of(new JLabel(
            "<html>StockName|Quantity|TransactionDate|TransactionType <br>"
                    + "ABBV|35|2022-04-12|b <br>" + "AAPL|100|2022-05-10|b <br>"
                    + "MSFT|100|2022-11-11|b <br>" + "MSFT|40|2022-11-12|s <br>")));


    //    AddToMainPanel(Arrays.asList(fileUpload));

    JLabel portfolioNameLabel = new JLabel("Enter Portfolio Name:");
    addToMainPanel(Arrays.asList(portfolioNameLabel, portfolioName));

    addToMainPanel(Collections.singletonList(portfolioErrorLabel));

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

  /**
   * This method takes an error Label field and add customization the field.
   *
   * @param errorLabel shows the error.
   * @return The customized label field
   */
  private JLabel addErrorLabel(JLabel errorLabel) {
    errorLabel = new JLabel("");
    errorLabel.setForeground(Color.RED);
    return errorLabel;
  }

  /**
   * This method creates a flow layout and add components to the main panel which the goes unto the
   * frame.
   *
   * @param listOfComponents The list of components that needs to be added to the main
   *                         panel
   */
  private void addToMainPanel(java.util.List<Component> listOfComponents) {
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
   * This method adds listener to the portfolio Name text fields.
   *
   * @param listener The document listener from controller that needs to be attached
   */
  @Override
  public void addDocumentListener(DocumentListener listener) {
    portfolioName.getDocument().putProperty("name", "portfolioName");
    portfolioName.getDocument().addDocumentListener(listener);
  }

  /**
   * It returns current portfolio name upon the call of controller.
   *
   * @return map of strings
   */
  @Override
  public Map<String, String> getFields() {
    Map fields = new HashMap<>();
    fields.put("portfolioName", portfolioName.getText());
    return fields;
  }

  /**
   * This would allow us to dynamically set the size of the bar chart.
   *
   * @return Dimension object which sets the size
   */
  @Override
  public Dimension getPreferredSize() {
    return new Dimension(600, 500);
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
   * It sets error label in portfolio name label.
   *
   * @param component The component that needs to show the error
   * @param error     The error string.
   */
  @Override
  public void setErrorLabels(String component, String error) {
    if ("portfolio".equals(component)) {
      portfolioErrorLabel.setText(error);
    }
  }
}

