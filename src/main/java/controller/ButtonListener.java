package main.java.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

/**
 * This class represents the button listener implementation of the Action Listener
 * Interface. This allows the buttons from have listen from controller to perform the
 * respective operations.
 */
public class ButtonListener implements ActionListener {
  Map<String, Runnable> buttonClickedActions;

  /**
   * This is a default constructor for button listener.
   */
  public ButtonListener() {
    // default constructor
  }


  /**
   * This function links between a command and the runnable function.
   *
   * @param map The mapping between the command and runnable function.
   */
  public void setButtonClickedActionMap(Map<String, Runnable> map) {
    buttonClickedActions = map;
  }

  /**
   * Set the action of the command to the respective button.
   *
   * @param e the event to be processed
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    if (buttonClickedActions.containsKey(e.getActionCommand())) {

      buttonClickedActions.get(e.getActionCommand()).run();
    }
  }
}
