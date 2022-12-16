package main.java.model;

import main.java.model.entities.Strategy;

/**
 * An interface that can be used to execute high-level investment strategies for any portfolio.
 */
public interface StrategyExecutor {
  /**
   * A method to execute a high-level investment strategy on a portfolio, based on the
   * parameters provided to the function.
   *
   * @param portfolio     portfolio object on which the strategy needs to be executed.
   * @param strategy      strategy object which holds all the information related to the strategy.
   * @param priceProvider priceProvider object which should be used to fetch the price for the
   *                      stocks.
   * @return a response string on whether the strategy was successfully executed on the portfolio
   *         or not.
   */
  String runStrategy(Portfolio portfolio, Strategy strategy, PriceProvider priceProvider);
}
