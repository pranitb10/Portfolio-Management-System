package main.java.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import main.java.model.entities.StockPriceResponseWrapper;

/**
 * This interface represents model part of the MVC architecture for the portfolio management system
 * application. It provides features like creating a portfolio, getting all portfolios, loading a
 * portfolio, viewing portfolio compositions.
 */
public interface IModel {
  /**
   * A method to execute a strategy on any given portfolio.
   *
   * <p>This is a common method which can be used to execute dollar cost averaging as well as
   * executing a strategy(weighted) on a specific date. In case it needs to be executed only for a
   * specific date start and end date would be same.
   * </p>
   *
   * @param portfolioIndex   portfolioIndex for which the strategy needs to be executed.
   * @param stockAndWeights  stockWeights that should be used for investing while executing the
   *                         strategy.
   * @param investmentAmount amount that needs to be invested in the current strategy.
   * @param startDate        startDate from which the strategy execution should start.
   * @param endDate          endDate until which the strategy should be executed.
   * @param commission       commission that should be used to execute the transactions mentioned in
   *                         the strategy.
   * @param frequency        frequency on how often the investment should be made according to the
   *                         stocks and there weights provided.
   * @return response string on whether the strategy was executed successfully or not.
   * @throws RuntimeException 1.If the arguments passed to the method are invalid.
   *                          2. If the strategy cannot be executed for the given dates.
   */
  String executeStrategy(int portfolioIndex, List<String> stockAndWeights, Double investmentAmount,
                         LocalDate startDate, LocalDate endDate, Double commission, int frequency)
          throws RuntimeException;

  /**
   * This method creates an inflexible portfolio for the user and persists it in the application.
   *
   * @param portfolioName The portfolioName that should be assigned to the portfolio.
   * @param stocks        A list Strings where each string contains the stockName/symbol, the
   *                      quantity associated with the stock and date at which it was purchased.
   * @throws RuntimeException 1.if an exception is occurred while storing the portfolio in the
   *                          application. 2.If the arguments provided to the method are invalid.
   */
  void createInflexiblePortfolio(String portfolioName, List<String> stocks) throws RuntimeException;

  /**
   * This method creates a flexible portfolio for the user and persists it in the application.
   *
   * @param portfolioName The portfolioName that should be assigned to the portfolio.
   * @return the index of the portfolio in the portfolioList, after its creation.
   * @throws IllegalArgumentException if the portfolioName passed to the method is invalid.
   */
  int createFlexiblePortfolio(String portfolioName) throws IllegalArgumentException;

  /**
   * Returns the value of the portfolio on a particular given date.
   *
   * @param portfolioIndex portfolioIndex for which the portfolio value needs to be
   *                       displayed for the given date
   * @param date           date for which the portfolio value needs to be displayed.
   * @return StockPriceResponseWrapper object which internally has two list:- 1.List of Successful
   *         stock records for which price was fetched successfully. 2.List of unsuccessful records
   *         for which the price fetching failed.
   * @throws IllegalArgumentException if the arguments passed to the method are invalid.
   */

  StockPriceResponseWrapper getPortfolioValueOnDate(int portfolioIndex, LocalDate date)
          throws IllegalArgumentException;

  /**
   * This method provides all the portfolios that are currently persisted in the application.
   *
   * @return List of formatted strings, wherein each string represent a portfolio and its date of
   *         creation.
   */
  List<String> getAllPortfolios();

  /**
   * A method to validate whether a given portfolio name is valid or not.
   *
   * <p>It does two kinds of validations:- 1.The portfolio name should not be already present in
   * the application. 2.The name cannot be an empty string. It only allows alpha Numeric
   * characters(A-Z 0-9) and spaces in between the letters.
   * </p>
   *
   * @return a predicate object which can be used to test user input by the controller whether a
   *         given portfolio name is valid or invalid.
   */
  Predicate<String> isValidPortfolioName();

  /**
   * A method to validate whether a given String is a valid stockName or stockSymbol.
   *
   * <p>The given string is validated against the supported list of stocks(masterList).
   * The predicate returns true if it matches to any of the supported stocks name or the stock
   * symbol. The predicate returns false if it does not matches neither stock name nor stock symbol
   * for any of the supported stock </p>
   *
   * @return a predicate object which can be used to test user input by the controller ,whether a
   *         given stock Name/Symbol is valid or invalid.
   */
  Predicate<String> validateStockNameSymbol();

  /**
   * A method to load a flexible portfolio from a user provided file.
   *
   * <p>This method allows user to provide a file with a particular format on which the code
   * will parse and get the portfolio name, stock Name, stock Quantity, transaction type and date on
   * which transaction happened. In case the transaction is not consistent it notifies the user and
   * doesn't consider the transaction.</p>
   *
   * @param portfolioName the name of the portfolio
   * @param filePath the file path of the file that is to be loaded
   * @return The response(success/failure) after performing the load operation.
   * @throws IllegalArgumentException if the arguments passed to the method are invalid.
   */
  String loadFlexiblePortfolioFromFile(String portfolioName, String filePath)
          throws IllegalArgumentException;

  /**
   * A method to load an inflexible portfolio from a user provided file.
   *
   * <p>This method allows user to provide file with a particular format on which the code will
   * parse and get the portfolio name, stock Name, stock Quantity, date on which transaction
   * happened. In case the transaction is not consistent it notifies the user and doesn't consider
   * the transaction </p>
   *
   * @param portfolioName the name of the portfolio
   * @return The response(success/failure) after performing the load operation.
   */
  String loadInFlexiblePortfolioFromFile(String portfolioName);

  /**
   * This method gives the composition of a given portfolio.
   *
   * <p>A composition is basically how many stocks(quantity) of a particular company
   * (ticker) are owned by a user. .</p>
   *
   * @param index index of the portfolio for which the composition needs to be displayed.
   * @param date  date at which the composition needs to be displayed.
   * @return List of formatted strings, where each string represents a stock and its quantity
   *         present in the portfolio.
   * @throws IllegalArgumentException if the arguments passed to the method are invalid.
   */
  List<String> getPortfolioComposition(int index, LocalDate date) throws IllegalArgumentException;

  /**
   * This method is used to transact(buy/sell) a stock on a particular portfolio.
   *
   * @param portfolioIndex  portfolio index on which the transaction needs to be updated.
   * @param stockSymbol     stockSymbol that needs to be transacted.
   * @param stockQty        Quantity of the stock that needs to be transacted.
   * @param transactionType type of the transaction(buy/sell).
   * @param transactionDate date of the transaction.
   * @throws RuntimeException 1.If the transaction is invalid or if it cannot co-exist with the
   *                          previously mentioned transactions for a particular stock.
   */
  void transactStock(int portfolioIndex, String stockSymbol, Double stockQty,
                     String transactionType, LocalDate transactionDate) throws RuntimeException;

  double getCommission();

  /**
   * This method allows the user to configure a particular commission value in the application.
   *
   * @param commission value of the commission that needs to be configured in the system.
   * @throws IllegalArgumentException if the commission passed is a negative invalid number.
   */
  void setCommission(Double commission) throws IllegalArgumentException;

  /**
   * This method allows us to return the cost basis on a portfolio until a date specified by the
   * user.
   * <p>
   * For a given set of transactions, it would basically calculate the buy order until the day which
   * user specifies. If there are no transactions the cost basis is zero. It will also consider all
   * the commissions which where paid by the user for all the previous transactions.
   * </p>
   *
   * @param portfolioIndex the index of a portfolio that is to be selected
   * @param date the date on which the cost basis is required
   * @return the cost basis of the portfolio
   * @throws RuntimeException 1.If some error occurred while fetching the price of stock on a
   *                          particular date. 2.If the arguments passed to the method are invalid.
   */
  double getCostBasis(int portfolioIndex, LocalDate date) throws RuntimeException;

  /**
   * A method allows us to get graph data that needs to displayed in the application.
   *
   * <p>This method takes the portfolio number and two dates,and groups them by
   * the a particular scale(weeks/months/years) between the two user given dates. It only displays
   * between 5-30 lines and the maximum price would 50 asterisks and rest prices would be normalized
   * according to the maximum price
   * </p>
   *
   * @param portfolioNumber the number of the portfolio for which graph data is to be fetched
   * @param startDate the start date for the graph
   * @param endDate the end date for the graph
   * @return List of string to display the graph data.
   */
  List<String> fetchGraphData(int portfolioNumber, LocalDate startDate, LocalDate endDate);

  /**
   * This method allows the user to rebalance the portfolio on a given date.
   *
   * @param portfolioIndex the index of the portfolio that is to be rebalanced.
   * @param date           the date on which the portfolio is to be rebalanced.
   * @param percentMap the percent map.
   * @throws RuntimeException when the code comes across a run time exception.
   */
  void rebalancePortfolio(int portfolioIndex, LocalDate date, Map<String, Double> percentMap)
          throws RuntimeException;
}
