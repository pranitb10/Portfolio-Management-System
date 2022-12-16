package main.java.model;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import main.java.model.entities.PurchasedStock;
import main.java.model.entities.StockPriceResponseWrapper;
import main.java.model.entities.StockTransaction;
import main.java.model.entities.StockWeight;
import main.java.model.entities.Strategy;
import main.java.model.util.Util;
import main.java.model.util.Util.TransactionType;

/**
 * This class represents a flexible portfolio that extends the abstractPortfolio class.
 * <p>A Flexible portfolio is basically a portfolio on which any number of buy sell transactions
 * can be done. It overrides all the methods that are specific to flexible portfolio like
 * getComposition, getValueOnDate,Transact a stock etc. </p>
 */
public class FlexiblePortfolio extends AbstractPortfolio {
  private final String portfolioType = "flexible";

  /**
   * Constructs a flexible portfolio object based on the values passed to the function.
   *
   * @param portfolioName portfolioName that should be assigned to the portfolio.
   * @param dateAdded     date on which the portfolio was created.
   * @param stocks        stocks that should be added to the portfolio.
   */
  public FlexiblePortfolio(String portfolioName, LocalDate dateAdded, List<PurchasedStock> stocks) {
    super(portfolioName, dateAdded, stocks);
  }


  @Override
  public void executePendingTransactions(PriceProvider priceProvider) {
    for (Strategy strategy : this.strategies) {
      LocalDate backLogStartingDate = strategy.getDateUntilCalculated() == null
              ? strategy.getStartDate() : strategy.getDateUntilCalculated()
              .plusDays(strategy.getFrequency());

      for (LocalDate date = backLogStartingDate; date.isBefore(strategy.getEndDate()
              .plusDays(1)); date = date.plusDays(strategy.getFrequency())) {
        try {
          executeStrategy(strategy.getStockWeights(), strategy.getInvestmentAmount(), date,
                  strategy.getCommission(), priceProvider);
          strategy.setDateUntilCalculated(date);
        } catch (RuntimeException e) {
          break;
        }
      }
    }
  }

  @Override
  public void executeStrategy(List<StockWeight> stockAndWeights, Double investmentAmount,
                              LocalDate dateOfTransaction, Double commission,
                              PriceProvider priceProvider)
          throws RuntimeException {
    if (dateOfTransaction.isAfter(LocalDate.now())) {
      throw new DateTimeException("Future date not allowed for transactions");
    }
    investmentAmount -= stockAndWeights.size() * commission;
    Map<String, Double> priceMap = new HashMap<>();
    try {
      for (StockWeight stockWeight : stockAndWeights) {
        priceMap.put(stockWeight.getStockSymbol(),
                priceProvider.getPriceOfStock(stockWeight.getStockSymbol(), dateOfTransaction));
      }
    } catch (RuntimeException e) {
      throw new RuntimeException("Cannot retrieve price few stocks hence not executing the "
              + "strategy");
    }

    for (StockWeight stockWeight : stockAndWeights) {
      double valueForStock = (stockWeight.getWeight() / 100) * investmentAmount;
      double quantityToBeBought = valueForStock / priceMap.get(stockWeight.getStockSymbol());
      transactStock(stockWeight.getStockSymbol(), stockWeight.getStockName(), quantityToBeBought,
              TransactionType.BUY, dateOfTransaction, commission);
    }

  }

  /**
   * Returns composition of the portfolio on that particular date.
   * <p>It returns "NA" if stock wasn't bought until the given date</p>
   *
   * @param date Date on which portfolio composition needs to be shown.
   * @return list of string where each string represents a stock within the portfolio.
   */
  @Override
  public List<String> getPortfolioComposition(LocalDate date) {
    return stocks.stream().map(p -> {
      StockTransaction floor = p.getStockTransactions().floor(new StockTransaction(date));
      String qty = floor == null ? "NA" : String.valueOf(floor.getQuantityAfterTransaction());
      return p.getStockName() + Util.FILE_OUTPUT_SEPARATOR + p.getStockSymbol()
              + Util.FILE_OUTPUT_SEPARATOR + qty;
    }).collect(Collectors.toList());
  }

  /**
   * Gets the portfolio value on the given date. It factors in the quantity of each stock that was
   * present with the user until the given date.
   *
   * @param date          The date on which value needs to be determined.
   * @param priceProvider The price provider object which provides the price of stocks on
   *                      given date
   * @return StockPriceResponseWrapper which internally has list of success and failure list of the
   *         stocks depending on whether the price fetch failed or passed.
   */
  @Override
  public StockPriceResponseWrapper getPortfolioValueOnDate(LocalDate date,
                                                           PriceProvider priceProvider) {
    List<String> successList = new ArrayList<>();
    List<String> failureList = new ArrayList<>();
    Double portfolioValue = 0d;
    for (PurchasedStock purchasedStock : stocks) {
      try {
        StockTransaction t = new StockTransaction(date);
        System.out.println(purchasedStock.getStockTransactions().floor(t)
                .getQuantityAfterTransaction());
        Double price = priceProvider.getPriceOfStock(purchasedStock.getStockSymbol(), date);
        if (purchasedStock.getStockTransactions().floor(t) == null || purchasedStock
                .getStockTransactions().floor(t).getQuantityAfterTransaction() == 0) {
          successList.add(purchasedStock.getStockName() + Util.FILE_OUTPUT_SEPARATOR
                  + purchasedStock.getStockSymbol() + Util.FILE_OUTPUT_SEPARATOR
                  + "0"
                  + Util.FILE_OUTPUT_SEPARATOR + price + Util.FILE_OUTPUT_SEPARATOR
                  + "0");
        } else {
          portfolioValue += price * purchasedStock.getStockTransactions().floor(t)
                  .getQuantityAfterTransaction();
          successList.add(purchasedStock.getStockName() + Util.FILE_OUTPUT_SEPARATOR
                  + purchasedStock.getStockSymbol() + Util.FILE_OUTPUT_SEPARATOR
                  + purchasedStock.getStockTransactions().floor(t)
                  .getQuantityAfterTransaction()
                  + Util.FILE_OUTPUT_SEPARATOR + price + Util.FILE_OUTPUT_SEPARATOR
                  + price
                  * purchasedStock.getStockTransactions().floor(t).getQuantityAfterTransaction());
        }

      } catch (RuntimeException e) {
        failureList.add(purchasedStock.getStockName() + Util.FILE_OUTPUT_SEPARATOR
                + purchasedStock.getStockSymbol() + Util.FILE_OUTPUT_SEPARATOR
                + purchasedStock.getStockTransactions().last()
                .getQuantityAfterTransaction());
      }
    }
    return new StockPriceResponseWrapper(successList, failureList, portfolioValue);
  }

  @Override
  public void rebalancePortfolio(LocalDate date,
                                 Map<String, Double> percentMap, PriceProvider provider)
          throws RuntimeException {
    Double portfolioValue = this.getPortfolioValueOnDate(date, provider).getPortfolioValue();
    for (PurchasedStock purchasedStock : stocks) {
      StockTransaction t = new StockTransaction(date);
      String ticker = purchasedStock.getStockSymbol();
      Double price = provider.getPriceOfStock(purchasedStock.getStockSymbol(), date);
      Double stockQuantity = purchasedStock.
              getStockTransactions().floor(t).getQuantityAfterTransaction();
      Double valueOfStock = price * stockQuantity;
      Double targetValueOfStock = portfolioValue * (percentMap.get(ticker) / 100);
      Double valueDifference = targetValueOfStock - valueOfStock;

      if (valueDifference < 0) {
        this.transactStock(ticker, purchasedStock.getStockName(),
                -(valueDifference) / price, TransactionType.SELL, date, 0.0);
      } else if (valueDifference > 0) {
        this.transactStock(ticker, purchasedStock.getStockName(),
                (valueDifference) / price, TransactionType.BUY, date, 0.0);
      }
    }
  }


  private boolean validateSellTransaction(SortedSet transactionSet, Double sellQty) {
    if (transactionSet == null || transactionSet.size() == 0) {
      return true;
    }
    Iterator<StockTransaction> itr = transactionSet.iterator();
    while (itr.hasNext()) {
      Double qty = itr.next().getQuantityAfterTransaction();
      if (qty < sellQty) {
        return false;
      }
    }
    return true;
  }

  private void updateFutureDateTransactions(SortedSet transactionSet, Double stockQty) {
    if (transactionSet == null || transactionSet.size() == 0) {
      return;
    }
    Iterator<StockTransaction> itr = transactionSet.iterator();
    while (itr.hasNext()) {
      StockTransaction transaction = itr.next();
      Double oldQty = transaction.getQuantityAfterTransaction();
      transaction.setQuantityAfterTransaction(oldQty + stockQty);
    }
  }

  @Override
  public Portfolio transactStock(String stockSymbol, String stockName, Double stockQty,
                                 Util.TransactionType transactionType, LocalDate transactionDate,
                                 Double commission) throws RuntimeException {
    // check if stock already exist in the portfolio
    PurchasedStock stockToTransact =
            Util.collectionToParallelStream(getStocks()).filter(o -> o.getStockSymbol()
                    .equalsIgnoreCase(stockSymbol)
                    || o.getStockName().equalsIgnoreCase(
                    stockSymbol)).findAny().orElse(null);
    // if not already present in the portfolio create a new one
    if (stockToTransact == null) {
      stockToTransact = new PurchasedStock(stockName, stockSymbol, new TreeSet<>());
      stocks.add(stockToTransact);
    }

    NavigableSet<StockTransaction> previousTransactions = stockToTransact.getStockTransactions();
    StockTransaction testTransactionSameDate = new StockTransaction(transactionDate);
    StockTransaction floorTransaction = previousTransactions.floor(testTransactionSameDate);


    if (transactionType == Util.TransactionType.SELL) {
      if (floorTransaction == null) {
        throw new RuntimeException("Invalid Transaction, stock cannot be sold unless it is "
                + "bought" + "first");
      }
      if (!validateSellTransaction(stockToTransact.getStockTransactions().tailSet(floorTransaction),
              stockQty)) {
        throw new RuntimeException("This sell transaction cannot co-exist with the previously "
                + "entered transactions");
      }
      stockQty = -stockQty;
    }

    Double previousCumulativeQty = 0d;
    if (floorTransaction != null) {
      previousCumulativeQty = floorTransaction.getQuantityAfterTransaction();
    }
    updateFutureDateTransactions(previousTransactions.tailSet(testTransactionSameDate, false),
            stockQty);
    // if transaction already existed for same update than set the Qty...else create new transcation
    boolean isDifferentDate = previousTransactions.add(new StockTransaction(transactionDate,
            previousCumulativeQty
                    + stockQty,
            commission,
            transactionType));
    if (!isDifferentDate) {
      floorTransaction.setQuantityAfterTransaction(previousCumulativeQty + stockQty);
    }
    return this;
  }

  @Override
  public double getCostBasis(LocalDate date, PriceProvider priceProvider) throws RuntimeException {
    double costBasis = 0f;
    for (PurchasedStock purchasedStock : stocks) {
      SortedSet<StockTransaction> transactionsTillDate =
              purchasedStock.getStockTransactions().headSet(new StockTransaction(date), true);
      if (transactionsTillDate.size() == 0) {
        continue;
      }
      for (StockTransaction transaction : transactionsTillDate) {
        // irrespective of buy or sell first add commission of the transaction to the cost basis
        costBasis += transaction.getCommission();
        if (transaction.getTransactionType().equals(Util.TransactionType.BUY)) {
          StockTransaction previousTransaction =
                  purchasedStock.getStockTransactions().lower(transaction);
          Double qty = transaction.getQuantityAfterTransaction();
          if (previousTransaction != null) {
            qty -= previousTransaction.getQuantityAfterTransaction();
          }
          Double price = priceProvider.getPriceOfStock(purchasedStock.getStockSymbol(),
                  transaction.getTransactionDate());
          costBasis += qty * price;
        }
      }

    }
    return costBasis;
  }

}
