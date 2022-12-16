package main.java.model;

import java.time.LocalDate;

import main.java.model.entities.Strategy;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * This class implements the strategyExecutor interface and implements the runStrategy method.
 */
public class DollarCostAveragingImpl implements StrategyExecutor {
  /**
   * This method executes transactions based on the startDate and endDate of the strategy for
   * each of the date as per the frequency provided by the user.
   *
   * @param portfolio     portfolio object on which the strategy needs to be executed.
   * @param strategy      strategy object which holds all the information related to the strategy.
   * @param priceProvider priceProvider object which should be used to fetch the price for the
   *                      stocks.
   * @return String response of whether the strategy was successfully executed or not.
   */
  @Override
  public String runStrategy(Portfolio portfolio, Strategy strategy, PriceProvider priceProvider) {
    String response = "";

    for (LocalDate date = strategy.getStartDate(); date.isBefore(strategy.getEndDate().plusDays(1));
         date =
                 date.plusDays(strategy.getFrequency())) {
      try {
        portfolio.executeStrategy(strategy.getStockWeights(), strategy.getInvestmentAmount(), date,
                strategy.getCommission(), priceProvider);
        strategy.setDateUntilCalculated(date);
        response = (DAYS.between(strategy.getStartDate(), strategy.getEndDate()) > 1) ?
                "Strategy successfully executed till " + date : "Strategy successfully executed "
                + "for date " + date;
      } catch (RuntimeException e) {
        if (response.equals("")) {
          if (DAYS.between(strategy.getStartDate(), strategy.getEndDate()) > 1) {
            response = "No transactions were executed,your strategy is saved and will be executed"
                    + " when the appropriate day arrives.";
          } else {
            response = "Failed to execute the strategy";

          }
        }
        break;
      }
    }
    return response;
  }
}
