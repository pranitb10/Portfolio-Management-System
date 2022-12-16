package main.java.model.entities;

import java.util.NavigableSet;
import java.util.TreeSet;

/**
 * This class extends the Stock class...and represents a stock that is bought by the user. Apart
 * from the inherited properties from the stock class it also holds all the transactions that
 * happened for a stock.
 *
 * <p>Stock Transactions are in the form of treeSet sorted by the date of transaction
 * This helps in doing validations and determining cost basis in the most efficient way possible
 * .</p>
 */
public class PurchasedStock extends Stock {

  private NavigableSet<StockTransaction> stockTransactions = new TreeSet<>();

  /**
   * Constructs a purchased stock object.
   *
   * @param stockName         stockName of the stock.
   * @param stockSymbol       symbol of the stock.
   * @param stockTransactions Set of transactions associated with the stock.
   */
  public PurchasedStock(String stockName, String stockSymbol,
                        NavigableSet<StockTransaction> stockTransactions) {
    super(stockName, stockSymbol);
    this.stockTransactions = stockTransactions;
  }

  /**
   * Returns all the transactions that are associated with the current stock.
   *
   * @return A Navigable Set of transactions that are sorted by dateOfTransaction.
   */
  public NavigableSet<StockTransaction> getStockTransactions() {
    return stockTransactions;
  }
}
