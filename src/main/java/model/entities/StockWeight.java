package main.java.model.entities;

/**
 * This is an entity class representing a stock and its weight, that is required while executing a
 * strategy(single date or dollar cost averaging strategy).
 */
public class StockWeight {
  private final String stockSymbol;
  private final String stockName;
  private final Double weight;

  /**
   * Creates a stock weight object based in the stock and the weight specified.
   *
   * @param stockSymbol stock symbol of the stock that needs to be invested.
   * @param stockName   stock name of the stock that needs to be invested.
   * @param weight      weight of the stock that should be considered while investing.
   */
  public StockWeight(String stockSymbol, String stockName, Double weight) {
    this.stockSymbol = stockSymbol;
    this.stockName = stockName;
    this.weight = weight;
  }

  /**
   * Returns the stock symbol of the current stock.
   *
   * @return returns the stock symbol of the current stock
   */
  public String getStockSymbol() {
    return stockSymbol;
  }

  /**
   * Returns the weight of the stock that needs to be considered while investing.
   *
   * @return returns the weight of the stock that needs to be considered while investing.
   */
  public Double getWeight() {
    return weight;
  }

  /**
   * Returns stock name of the stock under consideration.
   *
   * @return returns stock name of the stock under consideration.
   */
  public String getStockName() {
    return stockName;
  }
}
