package main.java.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import main.java.model.entities.PurchasedStock;
import main.java.model.entities.StockPriceResponseWrapper;
import main.java.model.entities.StockWeight;
import main.java.model.util.Util;

/**
 * This class represents an Inflexible Portfolio. An Inflexible portfolio is a portfolio which
 * cannot be edited by the user once created.Moreover, only purchasing of stocks is allowed in an
 * inflexible portfolio.
 */
public class InFlexiblePortfolio extends AbstractPortfolio {
  private final String portfolioType = "inflexible";

  /**
   * Constructs an object of the Inflexible portfolio for given list of  purchased stocks given by
   * the user.
   *
   * @param portfolioName Portfolio name provided by the user
   * @param dateAdded     date when the portfolio was added
   * @param stocks        list of purchased stocks by the user
   */
  public InFlexiblePortfolio(String portfolioName, LocalDate dateAdded,
                             List<PurchasedStock> stocks) {
    super(portfolioName, dateAdded, stocks);
  }

  /**
   * As inflexible portfolio cannot be edited, it doesn't support any strategy execution after its
   * creation.
   *
   * @param priceProvider priceProvider that should be used to get the prices of the stock.
   */
  @Override
  public void executePendingTransactions(PriceProvider priceProvider) {
    // As inflexible portfolio cannot be edited, it doesn't support any strategy execution after its
    // creation.
  }

  /**
   * As inflexible portfolio cannot be edited, it doesn't support any strategy execution after its
   * creation.
   *
   * @param stockAndWeights   stockWeights of the stocks that needs to be invested as part of
   *                          the strategy.
   * @param investmentAmount  amount that needs to be invested as part of the strategy.
   * @param dateOfTransaction date on which the strategy needs to be executed.
   * @param commission        commission associated with the strategy.
   * @param priceProvider     priceProvider that should be used to fetch the prices of the
   *                          stock.
   * @throws RuntimeException if executeStrategy is called for an Inflexible portfolio.
   */
  @Override
  public void executeStrategy(List<StockWeight> stockAndWeights, Double investmentAmount,
                              LocalDate dateOfTransaction, Double commission,
                              PriceProvider priceProvider)
          throws RuntimeException {
    throw new RuntimeException("Execution of Strategy not supported for InflexiblePortfolio");
  }

  /**
   * This method gives the composition of a given portfolio.
   *
   * <p>A composition is basically how many stocks(quantity) of a particular company
   * (ticker) are owned by a user. For an inflexible portfolio if same stock is bought on multiple
   * dates, than it returns the latest quantity of the stocks left with the user, irrespective of
   * the date provided in the argument. .</p>
   *
   * @param date the date on which portfolio composition is required.
   * @return List of string containing composition of portfolio
   */
  @Override
  public List<String> getPortfolioComposition(LocalDate date) {
    return stocks.stream().map(p -> p.getStockName() + Util.FILE_OUTPUT_SEPARATOR
                    + p.getStockSymbol() + Util.FILE_OUTPUT_SEPARATOR
                    + p.getStockTransactions().last().getQuantityAfterTransaction())
            .collect(Collectors.toList());
  }

  /**
   * This method allows us to get the value of portfolio on a particular date.
   *
   * @param date          date on which portfolio value needs to decided.
   * @param priceProvider Price provider which will be used to get the prices of stocks on
   *                      particular dates.
   * @return StockPriceResponseWrapper object which internally has two list:- 1.List of Successful
   *         stock records for which price was fetched successfully. 2.List of unsuccessful records
   *         for which the price fetching failed.
   */
  @Override
  public StockPriceResponseWrapper getPortfolioValueOnDate(LocalDate date,
                                                           PriceProvider priceProvider) {
    List<String> successList = new ArrayList<>();
    List<String> failureList = new ArrayList<>();
    Double portfolioValue = 0d;
    for (PurchasedStock purchasedStock : stocks) {
      try {
        Double price = priceProvider.getPriceOfStock(purchasedStock.getStockSymbol(), date);
        portfolioValue += price * purchasedStock.getStockTransactions().last()
                .getQuantityAfterTransaction();
        successList.add(purchasedStock.getStockName() + Util.FILE_OUTPUT_SEPARATOR
                + purchasedStock.getStockSymbol() + Util.FILE_OUTPUT_SEPARATOR
                + purchasedStock
                .getStockTransactions().last().getQuantityAfterTransaction()
                + Util.FILE_OUTPUT_SEPARATOR + price + Util.FILE_OUTPUT_SEPARATOR
                + price
                * purchasedStock.getStockTransactions().last().getQuantityAfterTransaction());
      } catch (RuntimeException e) {
        failureList.add(purchasedStock.getStockName() + Util.FILE_OUTPUT_SEPARATOR
                + purchasedStock.getStockSymbol() + Util.FILE_OUTPUT_SEPARATOR
                + purchasedStock
                .getStockTransactions().last().getQuantityAfterTransaction());
      }
    }
    return new StockPriceResponseWrapper(successList, failureList, portfolioValue);
  }

  /**
   * Since editing a portfolio is not allowed for an inflexible portfolio. This method throws an
   * exception when called for an inflexible portfolio.
   *
   * @param stockSymbol     the stock symbol which needs to transacted
   * @param stockName       the stock name which needs to transacted
   * @param stockQty        the quantity which needs to transacted
   * @param transactionType the transaction type
   * @param transactionDate the transaction date
   * @param commission      the commission allocated on transaction
   * @return portfolio after adding the transactions
   * @throws RuntimeException If this method is called for inflexible portfolio.
   */
  @Override
  public Portfolio transactStock(String stockSymbol, String stockName, Double stockQty,
                                 Util.TransactionType transactionType, LocalDate transactionDate,
                                 Double commission) throws RuntimeException {
    throw new RuntimeException("Transaction is not allowed in a Inflexible Portfolio");
  }

  /**
   * This method throws an exception when called for an inflexible portfolio, as cost basis is not
   * supported for the same.
   *
   * @param date          date on which cost basis needs to be determined
   * @param priceProvider the price provider through prices would be determined.
   * @return the cost basis of portfolio until that date
   * @throws RuntimeException If this method is called for an inflexible portfolio.
   */
  @Override
  public double getCostBasis(LocalDate date, PriceProvider priceProvider) throws RuntimeException {
    String outputMsg = "Displaying Cost basis is not supported for inflexible portfolios, "
            + "please call other supported operations like get value, get composition etc "
            + "according to your liking";
    throw new RuntimeException(outputMsg);
  }

  @Override
  public void rebalancePortfolio(LocalDate date,
                                 Map<String, Double> percentMap, PriceProvider provider)
          throws RuntimeException {
    String outputMsg = "Rebalancing a portfolio is not supported for inflexible portfolios, "
            + "please call other supported operations like get value, get composition etc "
            + "according to your liking";
    throw new RuntimeException(outputMsg);
  }

}
