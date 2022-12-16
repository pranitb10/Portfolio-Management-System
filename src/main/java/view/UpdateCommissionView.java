package main.java.view;

import org.jdesktop.swingx.JXDatePicker;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.event.DocumentListener;

/**
 * This class is used to the display the view in which the user can update the commission of the
 * system.
 */
public class UpdateCommissionView extends JFrame implements IFrameView {
  private final JButton mainMenuButton;
  private final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
  private final JTextField commissionText;
  private final JButton submitButton;
  private final JPanel mainPanel;
  Map fields;
  private JXDatePicker calendar;
  private String date;
  private JRadioButton buy;
  private JRadioButton sell;
  private JLabel commissionTextErrorLabel;

  /**
   * Constructor to display the view to the user for updation of commission.
   *
   * @param commission The commission of the system
   */
  public UpdateCommissionView(double commission) {
    fields = new HashMap<>();
    commissionText = new JTextField("" + commission, 10);
    commissionText.setToolTipText("Enter any non-negative number");
    commissionTextErrorLabel = addErrorLabel(commissionTextErrorLabel);
    submitButton = new JButton("Submit");


    this.getContentPane().removeAll();
    this.setTitle("Commission Update");
    this.setSize(1000, 1000);
    this.setLocation(100, 100);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    mainPanel = new JPanel(new GridLayout(5, 1));
    JLabel mainHeading = new JLabel("Update the Commission of the System");
    addToMainPanel(List.of(mainHeading));

    JLabel commissionTextLabel = new JLabel("Enter the Commission:");
    addToMainPanel(Arrays.asList(commissionTextLabel, commissionText));

    addToMainPanel(Collections.singletonList(commissionTextErrorLabel));


    submitButton.setActionCommand("Submit Commission");
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
   * This method adds listener to the commission text field.
   *
   * @param listener The document listener from controller that needs to be attached
   */
  @Override
  public void addDocumentListener(DocumentListener listener) {
    commissionText.getDocument().putProperty("name", "stockName");
    commissionText.getDocument().addDocumentListener(listener);
  }

  /**
   * It returns current commission upon the call of controller.
   *
   * @return map of strings
   */
  @Override
  public Map<String, String> getFields() {

    fields.put("commissionText", commissionText.getText());
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
    if ("commissionText".equals(component)) {
      commissionTextErrorLabel.setText(error);
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
