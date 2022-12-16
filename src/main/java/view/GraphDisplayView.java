package main.java.view;

import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.DocumentListener;

/**
 * This class represent the graphical user interface of the inputs to assist the graph generation.
 * This is main panel of the graph feature it would contain two main panel one is the chart panel
 * and another one is submit button panel.
 */
public class GraphDisplayView extends JFrame implements IFrameView {

  private final JButton submitButton;

  private final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");


  /**
   * Constructor would take the data and create two panels one of of which is the bar chart and
   * second is main menu button.
   *
   * @param barData List of data taken to create bar chart
   * @param caption The title of the window.
   */
  public GraphDisplayView(List<String> barData, String caption) {
    this.getContentPane().removeAll();
    this.setTitle(caption);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(800, 500);
    setTitle("Bar Chart");
    System.out.println(barData);
    BarChartGraphics chart = new BarChartGraphics(barData, "Time Period", "Portfolio Value");
    JPanel mainPanel = new JPanel(new GridLayout(2, 1));
    this.setLayout(new BorderLayout(2, 2));
    submitButton = new JButton("Main Menu");
    submitButton.setActionCommand("Main Menu");
    submitButton.setEnabled(true);
    submitButton.setToolTipText("Main Menu");

    JPanel flowPanel = new JPanel(new FlowLayout());
    flowPanel.add(submitButton);

    this.add(chart, BorderLayout.CENTER);


    this.add(flowPanel, BorderLayout.SOUTH);
    repaint();


    pack();
    setVisible(true);
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
   * This method adds listener to submit button.
   *
   * @param listener The action listener from controller that needs to be attached
   */
  @Override
  public void addActionListener(ActionListener listener) {
    submitButton.addActionListener(listener);
  }

  /**
   * This method adds listener to the portfolio Name and number of stocks text fields.
   *
   * @param listener The document listener from controller that needs to be attached
   */
  @Override
  public void addDocumentListener(DocumentListener listener) {
    // There are no field to listen for input.
  }

  /**
   * It returns null upon the call of controller as it has no field.
   *
   * @return map of strings
   */
  @Override
  public Map<String, String> getFields() {
    return null;
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
   * It sets error label in the view.
   *
   * @param component The component that needs to show the error
   * @param error     The error string.
   */
  @Override
  public void setErrorLabels(String component, String error) {
    // There are no error labels to set.
  }
}
