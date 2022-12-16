package main.java.view;

import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentListener;

/**
 * This interface represents the view part of the MVC architecture for portfolio management
 * application. View is responsible for showing the output to the User. View formats the data
 * received  from the controller into a human-readable format. This interface focuses on the
 * graphical interface for the user
 */
public interface IFrameView {

  /**
   * This method gives the current frame of the view which is being displayed to the user.
   *
   * @return The present frame of the view
   */
  JFrame getFrame();

  /**
   * This method allows the controller listeners to the view buttons and fields. The change in the
   * elements in which the listener is attached the controller can make a decision on what to show
   * to the user.
   *
   * @param listener The action listener from controller that needs to be attached
   */
  void addActionListener(ActionListener listener);

  /**
   * This method allows the controller to listen to input fields, validate them and provide
   * appropriate responses to the user depending upon the field change.
   *
   * @param listener The document listener from controller that needs to be attached
   */
  void addDocumentListener(DocumentListener listener);

  /**
   * This method is used to get the values of fields in the view, which controller can use to
   * determine the course of actions.
   *
   * @return It returns the values of the fields in the view.
   */
  Map<String, String> getFields();

  /**
   * This method allows us to manipulate the enabling of the submit button, to make sure the state
   * of view is maintained.
   *
   * @param enable Boolean to enable button or not.
   */
  void setSubmit(boolean enable);


  /**
   * This method allows us to generate the dialog box and decide if its an error or an informational
   * message.
   *
   * @param caption The message that user needs to be known
   * @param title   The Title of the Dialog box
   * @param isError Is it an error box
   */
  default void showDialogBox(String caption, String title, boolean isError) {
    JOptionPane.showMessageDialog(this.getFrame(), caption, title,
            isError ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * This method allows us to the set Error message on a specific component in order to user to
   * realize that the input given by the user is invalid.
   *
   * @param component The component that needs to show the error
   * @param error     The error string.
   */
  void setErrorLabels(String component, String error);


}
