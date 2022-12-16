package main.java.view;

import com.google.gson.JsonObject;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.event.DocumentListener;

import main.java.controller.IFrameController;

/**
 * This class is the representation for graphical interface of main menu. The main menu list items
 * are obtained from the controller and then each item becomes a button with the list name itself
 * being the action command.
 */
public class MainMenuView extends JFrame implements IFrameView {
  private final List<JButton> menuButtons;

  private final JButton submit;

  private JTextField textField;

  /**
   * Constructor takes instance of controller and caption; using the controller the menu items will
   * be fetched and then each menu item will become button of each feature that user can execute.
   *
   * @param caption    The title of the Frame window
   * @param controller The instance of controller
   */
  public MainMenuView(String caption, IFrameController controller) {
    super(caption);

    JsonObject data = new JsonObject();

    menuButtons = new ArrayList<>();


    setSize(1000, 1000);
    setLocation(100, 100);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    List<String> menuItems = controller.getMenuItems();
    JPanel panel = new JPanel(new GridLayout(menuItems.size(), 1));

    for (int i = 0; i < menuItems.size(); i++) {
      JButton currentButton = new JButton(menuItems.get(i));
      currentButton.setActionCommand(menuItems.get(i));
      menuButtons.add(currentButton);
      panel.add(currentButton);
    }
    submit = new JButton("Submit");

    this.add(panel);

    pack();
    setVisible(true);

  }

  /**
   * This method adds listener to all the menu items button.
   *
   * @param listener The action listener from controller that needs to be attached
   */
  @Override
  public void addActionListener(ActionListener listener) {
    for (JButton button : menuButtons) {
      button.addActionListener(listener);
    }
    submit.addActionListener(listener);
  }

  /**
   * This method adds listener to text fields.
   *
   * @param listener The document listener from controller that needs to be attached
   */
  @Override
  public void addDocumentListener(DocumentListener listener) {
    // There are no fields for documents to listen on.
  }

  /**
   * It returns null upon the call of controller.
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
    // Submit button is always enabled for this view.
  }

  /**
   * It sets error labels in this case we don't set any error label.
   *
   * @param component The component that needs to show the error
   * @param error     The error string.
   */
  @Override
  public void setErrorLabels(String component, String error) {
    // There are no error labels to set hence this method is not implemented
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
