package main.java.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import main.java.model.entities.PurchasedStock;
import main.java.model.entities.StockPriceResponseWrapper;
import main.java.model.entities.StockWeight;
import main.java.model.entities.Strategy;
import main.java.model.util.Util;

/**
 * This interface represents a portfolio, it has all the methods that are required by the model to
 * operate on it.
 */
public interface Portfolio {
  /**
   * returns the strategies that are associated with the given portfolio.
   *
   * @return returns the strategies that are associated with the given portfolio.
   */
  List<Strategy> getStrategies();

  /**
   * A method which executes pending transactions of all the strategies. It uses the
   * dateUntilCalculated field and starts executing strategy from that date.
   *
   * @param priceProvider priceProvider that should be used to get the prices of the stock.
   */
  void executePendingTransactions(PriceProvider priceProvider);

  /**
   * This method is used to execute a strategy on a particular portfolio.
   *
   * @param stockAndWeights stockWeights of the stocks that needs to be invested as part of the
   *                        strategy.
   * @param investmentAmount amount that needs to be invested as part of the strategy.
   * @param dateOfTransaction date on which the strategy needs to be executed.
   * @param commission  commission associated with the strategy.
   * @param priceProvider priceProvider that should be used to fetch the prices of the stock.
   * @throws RuntimeException 1.if the strategy cannot be executed for the given date
   *                          2.Arguments passed to the method are invalid
   */
  void executeStrategy(List<StockWeight> stockAndWeights, Double investmentAmount,
          LocalDate dateOfTransaction, Double commission, PriceProvider priceProvider)
          throws RuntimeException;

  /**
   * This method gives the composition of a given portfolio.
   *
   * <p>A composition is basically how many stocks(quantity) of a particular company
   * (ticker) are owned by a user.</p>
   *
   * @param date Date on which portfolio composition needs to be shown.
   *
   * @return List of formatted strings, where each string represents a stock and its quantity
   *         present in the portfolio.
   */
  List<String> getPortfolioComposition(LocalDate date);

  /**
   * This method gives the name of a given portfolio.
   *
   * @return The name of portfolio in String
   */
  String getPortfolioName();

  /**
   * This method gives the date added of a given portfolio.
   *
   * @return The date at which the portfolio is added.
   */
  LocalDate getDateAdded();

  /**
   * This method gives the list of stocks present in the given portfolio.
   *
   * @return List of purchased stock for the portfolio
   */
  List<PurchasedStock> getStocks();


  /**
   * returns the portfolio value on a given date.
   *
   * <p>It returns a stock response wrapper, which contains a two list. A list of success which
   * have been successfully queries and list of failures which failed while querying.
   * </p>
   *
   * @param date The date on which value needs to be determined.
   * @param priceProvider The price provider object which provides the price of stocks on
   *         given date
   *
   * @return StockPriceResponseWrapper object which internally has two list:- 1.List of Successful
   *         stock records for which price was fetched successfully. 2.List of unsuccessful records
   *         for which the price fetching failed.
   */
  StockPriceResponseWrapper getPortfolioValueOnDate(LocalDate date, PriceProvider priceProvider);

  /**
   * This method is used to transact(buy/sell) a stock on a portfolio.
   *
   * @param stockSymbol stockSymbol that needs to be transacted.
   * @param stockName name of the stock that needs to be transacted.
   * @param stockQty Quantity of the stock transacted.
   * @param transactionType type of the transaction(buy/sell).
   * @param transactionDate date of the transaction.
   * @param commission commission associated with the transaction.
   *
   * @return Portfolio object after doing transaction of the given stock in the portfolio.
   *
   * @throws RuntimeException 1.If the transaction is invalid or if it cannot co-exist with the
   *                          previously mentioned transactions for a particular stock.
   */
  Portfolio transactStock(String stockSymbol, String stockName, Double stockQty,
          Util.TransactionType transactionType, LocalDate transactionDate,
          Double commission) throws RuntimeException;

  /**
   * This method allows us to return the cost basis on a portfolio until a date specified by the
   * user.
   * <p>
   * For a given set of transactions, it would basically calculate the buy order until the day which
   * user specifies. If there are no transactions the cost basis is zero. It will also consider all
   * the commissions which where paid by the user for all the previous transactions.
   * </p>
   *
   * @param date date on which cost basis needs to be determined
   * @param priceProvider the price provider through which prices would be determined.
   *
   * @return the cost basis of portfolio until that date
   *
   * @throws RuntimeException 1.If some error occurred while fetching the price of stock on a
   *                          particular date.
   */
  double getCostBasis(LocalDate date, PriceProvider priceProvider) throws RuntimeException;

  /**
   * This method allows the user to rebalance the portfolio on a given date.
   *
   * @param date the date on which the portfolio is to be rebalanced.
   * @param percentMap the percent map.
   * @param provider an object of PriceProvider to get information related stocks Price.
   * @throws RuntimeException when the code comes across a run time exception.
   */
  void rebalancePortfolio(LocalDate date, Map<String, Double> percentMap,
                          PriceProvider provider) throws RuntimeException;

}
