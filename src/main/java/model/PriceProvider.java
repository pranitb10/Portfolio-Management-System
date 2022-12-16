package main.java.model;

import java.time.LocalDate;

/**
 * This interface is responsible for providing prices for the stocks on given dates to the Model.
 */
public interface PriceProvider {

  /**
   * returns the price of a particular stock on a given date.
   *
   * @param stockSymbol The stock symbol which the price needs to fetched
   * @param date        the date on which price needs to fetched
   * @return The price of the stock on a given date
   * @throws RuntimeException 1. If the price is not found for the date
   */
  Double getPriceOfStock(String stockSymbol, LocalDate date) throws RuntimeException;

}
