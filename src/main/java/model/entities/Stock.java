package main.java.model.entities;

/**
 * Represents a stock which is internally used by the model. It represents all the supported
 * stocks in the application.
 */
public class Stock {
  private final String stockName;
  private final String stockSymbol;

  /**
   * Constructs a stock object.
   *
   * @param stockName   stockName of the stock.
   * @param stockSymbol Stock symbol of the stock.
   */
  public Stock(String stockName, String stockSymbol) {
    this.stockName = stockName;
    this.stockSymbol = stockSymbol;
  }

  /**
   * Returns the name of the stock.
   *
   * @return name of the stock.
   */
  public String getStockName() {
    return stockName;
  }

  /**
   * Returns symbol of the stock.
   *
   * @return symbol of the stock.
   */
  public String getStockSymbol() {
    return stockSymbol;
  }
}
