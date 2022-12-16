package main.java.view;

import java.time.LocalDate;
import java.util.List;

import main.java.model.entities.StockPriceResponseWrapper;

/**
 * This interface represents the view part of the MVC architecture for portfolio management
 * application. View is responsible for showing the output to the User. View formats the data
 * received  from the controller into a human-readable format.
 */
public interface IView {
  /**
   * This method displays the string received from the controller to the user.
   *
   * @param text text string that needs to be displayed in the view.
   */
  void displayText(String text);

  /**
   * This method displays the portfolio table in a formatted output to the user.
   *
   * @param listOfPortfolio List of strings, wherein each string contains a portfolioName and the
   *                        date of creation.
   */
  void createPortfolioTable(List<String> listOfPortfolio);

  /**
   * This method displays the portfolio value in a formatted output.
   *
   * @param response  StockPriceResponseWrapper object which internally has successList consisting
   *                  of portfolio stocks and its value at a currentDate...along with it failure
   *                  stocks for which price retrieval failed
   * @param inputDate date for which the portfolio value is being displayed
   */
  void showStockResponseWrapper(StockPriceResponseWrapper response, LocalDate inputDate);

  /**
   * This method displays the portfolio composition to the user in a formatted output.
   *
   * @param listOfStocks list of strings, where each string represent the stock information like
   *                     symbol, Quantity, name etc.
   */
  void getPortfolioComposition(List<String> listOfStocks);

  /**
   * This method displays the graph output.
   *
   * @param graphOutput list of strings wherein each string in the list has the following
   *                    information
   *                    1. Scale of the time
   *                    2. The normalized price
   *                    3. The actual price that needs to be displayed
   */
  void displayGraph(List<String> graphOutput);
}
