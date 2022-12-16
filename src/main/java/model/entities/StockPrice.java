package main.java.model.entities;

import java.time.LocalDate;

/**
 * An entity class that is used for communication between the abstractPriceProvider and its child
 * classes to get the price of a stock on a particular date.
 */
public class StockPrice {
  private final LocalDate date;
  private final Double price;

  /**
   * Constructs a StockPrice object based on the price and date passed to the function.
   *
   * @param date  date for price is requested.
   * @param price price of the stock on the date.
   */
  public StockPrice(LocalDate date, Double price) {
    this.date = date;
    this.price = price;
  }

  /**
   * Returns date on which the price is fetched.
   *
   * @return date on which the price is associated.
   */
  public LocalDate getDate() {
    return date;
  }

  /**
   * Returns price of the stock on that date.
   *
   * @return price of the stock on that particular date.
   */
  public Double getPrice() {
    return price;
  }
}
