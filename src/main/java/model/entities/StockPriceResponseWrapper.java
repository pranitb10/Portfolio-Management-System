package main.java.model.entities;

import java.util.List;

/**
 * A wrapper response class that is used for communication between model and the controller. It
 * internally has a string of successful and unsuccessful records that were processed while
 * fetching the prices of the stocks.
 */
public class StockPriceResponseWrapper {
  private final List<String> successList;
  private final List<String> failureList;

  private final Double portfolioValue;

  /**
   * Constructs object of the wrapper response object.
   *
   * @param successList    list of successful records that were processed.
   * @param failureList    list of unsuccessful records that failed while processing.
   * @param portfolioValue portfolio Value on the user given date.
   */
  public StockPriceResponseWrapper(List<String> successList, List<String> failureList,
                                   Double portfolioValue) {
    this.successList = successList;
    this.failureList = failureList;
    this.portfolioValue = portfolioValue;
  }

  /**
   * Returns portfolio value on the user given date.
   *
   * @return value of portfolio on the user provided date.
   */
  public Double getPortfolioValue() {
    return portfolioValue;
  }

  /**
   * Returns list of successful records.
   *
   * @return successfully processed stocks list.
   */
  public List<String> getSuccessList() {
    return successList;
  }

  /**
   * Returns failure list.
   *
   * @return List of failed records while processing.
   */
  public List<String> getFailureList() {
    return failureList;
  }
}
