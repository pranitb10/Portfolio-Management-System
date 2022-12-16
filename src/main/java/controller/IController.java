package main.java.controller;

/**
 * This interface represents the controller part of the MVC architecture of the application.
 * It is responsible for handling the flow of the application. It is responsible for taking inputs
 * from the user, and directs view to show output to the user. Once it receives input from user
 * it can ask model for any validation or any processing that needs to be done in the application.
 */
public interface IController {
  /**
   * This method is the start of the program. Once started depending on the user inputs it will
   * handle the complete flow of the application.
   */
  void start();
}
