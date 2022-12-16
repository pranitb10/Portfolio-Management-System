package main.java.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import main.java.model.entities.PurchasedStock;
import main.java.model.entities.Strategy;

/**
 * Abstract Portfolio class that implements Portfolio interface. This class holds the main data
 * associated with the portfolio like list of stocks,portfolioName,dateAdded etc.
 */
public abstract class AbstractPortfolio implements Portfolio {
  protected String portfolioName;
  protected LocalDate dateAdded;
  protected List<PurchasedStock> stocks;
  protected List<Strategy> strategies = new ArrayList<>();

  /**
   * Initialises the portfolio with the given portfolio name,date,and list of stocks.
   *
   * @param portfolioName The name of the portfolio.
   * @param dateAdded     Date when the Portfolio was added.
   * @param stocks        List of purchased stock to create Portfolio.
   */
  protected AbstractPortfolio(String portfolioName, LocalDate dateAdded,
                              List<PurchasedStock> stocks) {
    this.portfolioName = portfolioName;
    this.dateAdded = dateAdded;
    this.stocks = stocks;
  }

  /**
   * returns the list of strategies associated with the current portfolio.
   *
   * @return list of strategies that are part of the current portfolio.
   */
  public List<Strategy> getStrategies() {
    return strategies;
  }

  /**
   * A method to set the strategy in a particular portfolio.
   *
   * @param strategies strategies that should be assigned to the particular portfolio.
   */
  public void setStrategies(List<Strategy> strategies) {
    this.strategies = strategies;
  }

  /**
   * This method allows us to get the portfolio name.
   *
   * @return The portfolio name in string
   */
  public String getPortfolioName() {
    return portfolioName;
  }

  /**
   * This method allows us to get the purchased stocks.
   *
   * @return The list of the purchased stocks for the portfolio.
   */
  public List<PurchasedStock> getStocks() {
    return stocks;
  }

  /**
   * This method allows us to get date when the portfolio is added.
   *
   * @return The Localdate when the portfolio was created
   */
  public LocalDate getDateAdded() {
    return dateAdded;
  }
}
