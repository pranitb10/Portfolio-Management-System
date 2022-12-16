package main.java.model.entities;

import java.time.LocalDate;
import java.util.List;

/**
 * This is an entity class representing a particular strategy associated with a portfolio.
 */
public class Strategy {
  private final LocalDate startDate;
  private final LocalDate endDate;
  private final Double investmentAmount;
  private final Double commission;
  private final Integer frequency;
  private final List<StockWeight> stockWeights;
  private LocalDate dateUntilCalculated;

  /**
   * This constructs a strategy object based on the information given to the function.
   *
   * @param startDate           startDate from which the strategy needs to be executed
   * @param endDate             endDate from which the strategy needs to be executed
   * @param dateUntilCalculated date until which the strategy was successfully executed.
   *                            This will be used when the strategy execution failed due to price
   *                            unavailability. Next time when program is loaded it will retry the
   *                            strategy from the date in this particular field.
   * @param investmentAmount    amount that needs to invested as part of the current strategy.
   * @param commission          commission associated with the current strategy.
   * @param frequency           frequency of the recurring dollar cost averaging strategy.
   * @param stockWeights        stock weights of all the stocks that needs to be invested as part
   *                            of the current strategy.
   */
  public Strategy(LocalDate startDate, LocalDate endDate, LocalDate dateUntilCalculated,
                  Double investmentAmount, Double commission, Integer frequency,
                  List<StockWeight> stockWeights) {
    this.startDate = startDate;
    this.endDate = endDate;
    this.dateUntilCalculated = dateUntilCalculated;
    this.investmentAmount = investmentAmount;
    this.commission = commission;
    this.frequency = frequency;
    this.stockWeights = stockWeights;
  }

  /**
   * Returns stock weights in the current strategy.
   *
   * @return stock weights present in the strategy.
   */
  public List<StockWeight> getStockWeights() {
    return stockWeights;
  }

  /**
   * Returns startDate of the strategy.
   *
   * @return startDate from which strategy should be executed.
   */
  public LocalDate getStartDate() {
    return startDate;
  }

  /**
   * Returns endDate of the strategy.
   *
   * @return endDate to which strategy should be executed.
   */
  public LocalDate getEndDate() {
    return endDate;
  }

  /**
   * Date until which the strategy was successfully executed. This will be used when the strategy
   * execution failed due to price unavailability. Next time when program is loaded it will retry
   * the strategy from the date in this particular field.
   *
   * @return date until which strategy was successfully executed
   */
  public LocalDate getDateUntilCalculated() {
    return dateUntilCalculated;
  }

  /**
   * A method to set the value of the strategy is executed successfully on a particular date.
   *
   * @param dateUntilCalculated date until which the strategy was successfully executed.
   */

  public void setDateUntilCalculated(LocalDate dateUntilCalculated) {
    this.dateUntilCalculated = dateUntilCalculated;
  }

  /**
   * Returns the investment amount of the particular strategy.
   *
   * @return amount that needs to be invested in the strategy
   */
  public Double getInvestmentAmount() {
    return investmentAmount;
  }

  /**
   * Returns commission associated with the strategy.
   *
   * @return the commission that should be considered while executing the strategy.
   */

  public Double getCommission() {
    return commission;
  }

  /**
   * Returns the frequency on when the strategy should be executed in a recurring manner.
   *
   * @return returns the frequency on when the strategy should be executed in a recurring manner.
   */
  public Integer getFrequency() {
    return frequency;
  }
}
