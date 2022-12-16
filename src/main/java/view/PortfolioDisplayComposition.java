package main.java.view;

import java.awt.Dimension;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.DocumentListener;

/**
 * This class represent the view implementation to display the composition of stocks and number of
 * shares. This would show the composition to the user.
 */
public class PortfolioDisplayComposition extends JFrame implements IFrameView {
  private final JButton submitButton;

  private final JPanel mainPanel;

  private final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

  /**
   * Constructor would take the output list split into the individual component and create a table
   * which would be then embedded onto the frame.
   *
   * @param caption       The title of the frame
   * @param output        List of string which has output separated by '|'
   * @param columnHeaders The column headers for the table
   */
  public PortfolioDisplayComposition(String caption, List<String> output,
                                     String[] columnHeaders) {
    this.getContentPane().removeAll();
    this.setTitle(caption);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    int outputSize = output.size();

    int columnSize = output.get(0).split("\\|").length;

    String[][] table = new String[outputSize][columnSize + 1];


    for (int index = 0; index < output.size(); index++) {
      String[] listOfSplittedstrings = output.get(index).split("\\|");
      table[index][0] = String.valueOf(index + 1);
      table[index][1] = listOfSplittedstrings[1];
      table[index][2] = listOfSplittedstrings[0];
      table[index][3] = listOfSplittedstrings[2];
      if (listOfSplittedstrings.length > 3) {
        table[index][4] = listOfSplittedstrings[3];
        table[index][5] = listOfSplittedstrings[4];
      }
    }


    submitButton = new JButton("Main Menu");
    submitButton.setActionCommand("Main Menu");
    submitButton.setEnabled(true);
    submitButton.setToolTipText("Click to return to Main Menu");

    mainPanel = new JPanel(new GridLayout(3, 1));

    addToMainPanel(List.of(new JLabel(caption)));

    JTable composition = new JTable(table, columnHeaders);
    composition.setBounds(30, 40, 200, 300);


    addToMainPanel(List.of(new JScrollPane(composition)));

    addToMainPanel(Collections.singletonList(submitButton));

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
   * This would allow us to dynamically set the size of the bar chart.
   *
   * @return Dimension object which sets the size
   */
  @Override
  public Dimension getPreferredSize() {
    return new Dimension(500, 400);
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

  @Override
  public void addDocumentListener(DocumentListener listener) {
    // There are no fields to listen to.
  }

  @Override
  public Map<String, String> getFields() {
    return null;
  }

  @Override
  public void setSubmit(boolean enable) {
    // The enbaling of submit button is not applicable in this view
  }

  @Override
  public void setErrorLabels(String component, String error) {
    // There are no error labels to set
  }
}
