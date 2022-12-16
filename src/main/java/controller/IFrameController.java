package main.java.controller;

import java.util.List;

import main.java.view.IFrameView;

/**
 * This interface represents the controller part of the MVC architecture of the application.
 * It is responsible for handling the flow of the application. It is responsible for taking inputs
 * from the user, and directs view to show output to the user. Once it receives input from user
 * it can ask model for any validation or any processing that needs to be done in the application.
 * This controller is specifically designed for the GUI interface.
 */
public interface IFrameController {

  /**
   * It allows the controller to send in menu items to the view to show the operations that can
   * be performed by the portfolio management system.
   *
   * @return List of menu items in string format
   */
  List<String> getMenuItems();


  /**
   * This method allows the controller to exit the application.
   */
  void exitProgram();

  /**
   * This method allows the controller to set the current view to start the program. Along with
   * setting the start view it also to configure listeners for each button and actions performed
   * upon clicking those buttons.
   *
   * @param v The view which needs to be set as the start view
   */
  void setView(IFrameView v);

  /**
   * This method takes the string which can be a portfolio name and checks if the portfolio name
   * is valid or not. This would allow controller to provide feedback to the view.
   *
   * @param s The candidate portfolio name that needs to checked.
   * @return boolean on whether the portfolio name is valid.
   */
  boolean portfolioNameChecker(String s);

  /**
   * This method checks if the string entered is a valid number of stock, this would allow
   * controller to provide feedback to the view.
   *
   * @param s The candidate string which may or may not be number of stocks
   * @return boolean on which the number of stocks is valid or not
   */
  boolean checkNumberOfStocks(String s);

  /**
   * This method checks if the string entered is a valid quantity of stock, this would
   * allow controller to provide feedback to the view.
   *
   * @param s The candidate string which may or may not be a valid quantity of stock
   * @return boolean on whether the input is a valid quantity of stocks.
   */
  boolean checkQuantityOfStocks(String s);

  /**
   * This method checks if the string entered is a valid weightage of stock, this would
   * allow controller to provide feedback to the view.
   *
   * @param s The candidate string which may or may not be a valid weightage of stock
   * @return boolean on whether the input is a valid weightage of stocks.
   */
  boolean checkWeightageOfStocks(String s);

  /**
   * This method checks if the string entered is a valid amount, this would
   * allow controller to provide feedback to the view.
   *
   * @param s The candidate string which may or may not be a valid amount
   * @return boolean on whether the input is a valid amount.
   */
  boolean checkAmount(String s);

  /**
   * This method checks if the string entered is a valid stock name, this would
   * allow controller to provide feedback to the view.
   *
   * @param s The candidate string which may or may not be a valid stock name
   * @return boolean on whether the input is a valid stock name.
   */
  boolean stockNameChecker(String s);

  /**
   * This method checks if the string entered is a valid commission, this would
   * allow controller to provide feedback to the view.
   *
   * @param s The candidate string which may or may not be a valid commission
   * @return boolean on whether the input is a valid commission.
   */
  boolean checkCommission(String s);
}
