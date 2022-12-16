package main.java.model;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import main.java.model.entities.GraphResponseWrapper;
import main.java.model.entities.PurchasedStock;
import main.java.model.entities.Stock;
import main.java.model.entities.StockPriceResponseWrapper;
import main.java.model.entities.StockTransaction;
import main.java.model.entities.StockWeight;
import main.java.model.entities.Strategy;
import main.java.model.util.Util;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

/**
 * This class represents implementation of the IModel interface. Some of the data that is required
 * by this model(prices of stocks,supported stock list,persisted portfolios) is dependent on the
 * DataIO and the PriceProvider object that is passed to the model constructor.
 */
public class Model implements IModel {

  private static List<Stock> stockMasterList;
  final DayOfWeek firstDayOfWeek = WeekFields.of(Locale.US).getFirstDayOfWeek();
  final DayOfWeek lastDayOfWeek =
          DayOfWeek.of(((firstDayOfWeek.getValue() + 5) % DayOfWeek.values().length)
                  + 1);
  Util.TransactionType transactionTypes;
  private final DataIO dataIO;
  private final List<Portfolio> portfolioList;

  //by default the commission for the system would be 5$ if not updated by the user
  private Double commission = 5d;
  private final PriceProvider priceProvider;

  /**
   * Constructs a Model object which can be used by the controller. The data for the model is
   * updated from the DataIO and PriceProvider object passed to it.
   *
   * @param dataIO        DataIo object which needs to be used to get the data in the model
   * @param priceProvider PriceProvider object which needs to be used to get prices of the
   *                      stocks on particular dates.
   * @throws IllegalArgumentException If any of the data returned from the DataIO object is null.
   */
  public Model(DataIO dataIO, PriceProvider priceProvider) throws IllegalArgumentException {
    this.dataIO = dataIO;
    this.priceProvider = priceProvider;
    stockMasterList = dataIO.readMasterStockList();
    portfolioList = dataIO.readPersistedPortfolios();
    if (stockMasterList == null || portfolioList == null) {
      throw new IllegalArgumentException("List should not be null return an empty list instead");
    }

    for (Portfolio portfolio : portfolioList) {
      portfolio.executePendingTransactions(priceProvider);
    }
    try {
      dataIO.savePortfolios(portfolioList);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static LocalDate lastDayOfQuarter(String quarterRepresentation) {
    Map<String, String> quarterLastDayMap = Map.of(
            "Q1", "03-31",
            "Q2", "06-30",
            "Q3", "09-30",
            "Q4", "12-31"
    );
    String[] splitOfQuarterRepresentation = quarterRepresentation.split("-");
    return LocalDate.parse(splitOfQuarterRepresentation[1] + "-"
            + quarterLastDayMap.get(splitOfQuarterRepresentation[0]));
  }

  private static List<LocalDate> getDatesToHitAPI(List<String> heading, Scales scale) {
    switch (scale) {
      case DAY:
        return heading.stream().map(LocalDate::parse).collect(Collectors.toList());
      case WEEK:
        return heading.stream().map(o -> o.substring(o.length() - 11).trim())
                .map(LocalDate::parse).collect(Collectors.toList());
      case MONTH:
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd MMM-uuuu");
        List<LocalDate> dates =
                heading.stream().map(o -> LocalDate.parse("01 " + o, format)
                                .with(lastDayOfMonth()))
                        .collect(Collectors.toList());
        return dates;
      case QUARTER:
        return heading.stream().map(o -> lastDayOfQuarter(o)).collect(Collectors.toList());
      case YEAR:
        return heading.stream().map(o -> LocalDate.parse(o + "-12-31"))
                .collect(Collectors.toList());
      default:
        return heading.stream().map(o -> LocalDate.parse(o.split("-")[1] + "-12-31"))
                .collect(Collectors.toList());
    }
  }

  @Override
  public Predicate<String> isValidPortfolioName() {
    return (s -> Util.collectionToParallelStream(portfolioList)
            .filter(o -> o.getPortfolioName().equalsIgnoreCase(s)).findAny().isPresent()
            || !s.matches("^\\w+( \\w+)*$"));
  }

  @Override
  public Predicate<String> validateStockNameSymbol() {
    return (s -> getStockFromNameOrSymbol(s) == null);
  }


  @Override
  public String loadFlexiblePortfolioFromFile(String portfolioName, String filePath)
          throws IllegalArgumentException {
    // validation
    if (isValidPortfolioName().test(portfolioName)) {
      throw new IllegalArgumentException("Portfolio Name already Exists");
    }

    //create a flexible portfolio and call the transact method for each entry in the file
    List<String> fileContent = dataIO.readUserGivenFile(filePath);
    Portfolio newPortfolio = new FlexiblePortfolio(portfolioName, LocalDate.now(),
            new ArrayList<>());
    portfolioList.add(newPortfolio);
    int portfolioNumber = portfolioList.indexOf(newPortfolio);
    try {
      for (String stock : fileContent) {
        String[] arr = stock.split(Util.FILE_SEPARATOR);
        transactStock(portfolioNumber + 1, arr[0], Double.parseDouble(arr[1]),
                arr[3], Util.getDateFromString(arr[2]));
      }
    } catch (Exception e) {
      portfolioList.remove(portfolioNumber);
      throw new RuntimeException("Unable to load the portfolio,Please verify the file data\n"
              + e.getMessage());
    }
    return "Successfully created the portfolio";
  }

  @Override
  public String loadInFlexiblePortfolioFromFile(String portfolioName)
          throws IllegalArgumentException {
    // validation
    if (isValidPortfolioName().test(portfolioName)) {
      throw new IllegalArgumentException("Portfolio Name already Exists");
    }

    List<String> fileContent = dataIO.readUserGivenFile("res/user_inflexible.csv");
    try {
      createInflexiblePortfolio(portfolioName, fileContent);
    } catch (Exception e) {
      throw new RuntimeException("Unable to load the portfolio,Please verify the file data\n"
              + e.getMessage());
    }
    return "Successfully created the portfolio";
  }

  private Stock getStockFromNameOrSymbol(String stockSymbol) {
    return Util.collectionToParallelStream(stockMasterList)
            .filter(o -> o.getStockSymbol().equalsIgnoreCase(stockSymbol)
                    || o.getStockName().equalsIgnoreCase(stockSymbol)).findAny().orElse(null);
  }

  @Override
  public List<String> getPortfolioComposition(int index, LocalDate date)
          throws IllegalArgumentException {
    if (index < 1 || index > portfolioList.size()) {
      throw new IllegalArgumentException("Invalid portfolio Index provided");
    }
    return portfolioList.get(index - 1).getPortfolioComposition(date);
  }


  @Override
  public void transactStock(int portfolioIndex, String stockSymbol, Double stockQty,
                            String transactionType, LocalDate transactionDate)
          throws RuntimeException {
    //validate input args
    if ((portfolioIndex < 1 || portfolioIndex > portfolioList.size())
            || validateStockNameSymbol().test(stockSymbol) || stockQty < 1 || (!transactionType
            .equalsIgnoreCase("b") && !transactionType
            .equalsIgnoreCase("s")) || transactionDate.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Invalid Arguments,Please verify the input arguments");
    }

    // if args are valid proceed with transaction
    Stock stock = getStockFromNameOrSymbol(stockSymbol);
    Util.TransactionType transactionTypeEnum = transactionType.equalsIgnoreCase("b")
            ? Util.TransactionType.BUY : Util.TransactionType.SELL;
    Portfolio portfolio = portfolioList.get(portfolioIndex - 1);

    Portfolio editedPortfolio = portfolio.transactStock(stock.getStockSymbol(),
            stock.getStockName(),
            stockQty, transactionTypeEnum,
            transactionDate, commission);
    portfolioList.set(portfolioIndex - 1, editedPortfolio);
    try {
      dataIO.savePortfolios(portfolioList);
    } catch (IOException e) {
      throw new RuntimeException("Unable to save the portfolio changes\n" + e.getMessage());
    }
  }

  @Override
  public double getCommission() {
    return this.commission;
  }

  @Override
  public void setCommission(Double commission) throws IllegalArgumentException {
    if (commission < 0) {
      throw new IllegalArgumentException("Invalid commission value, it cannot be negative");
    }
    this.commission = commission;
  }

  @Override
  public double getCostBasis(int portfolioIndex, LocalDate date) throws RuntimeException {
    if ((portfolioIndex < 1 || portfolioIndex > portfolioList.size())
            || date.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Invalid arguments provided to the method");
    }
    Portfolio portfolio = portfolioList.get(portfolioIndex - 1);
    return portfolio.getCostBasis(date, priceProvider);
  }

  @Override
  public String executeStrategy(int portfolioIndex, List<String> stockAndWeights,
                                Double investmentAmount, LocalDate startDate, LocalDate endDate,
                                Double commission, int frequency) throws RuntimeException {
    String response = "";
    // Args validations
    // portfolio Index
    if ((portfolioIndex < 1 || portfolioIndex > portfolioList.size())) {
      throw new IllegalArgumentException("Invalid portfolio Index");
    }
    //stock Symbols
    if (stockAndWeights.parallelStream().anyMatch(stock ->
            validateStockNameSymbol().test(stock.split(Util.FILE_SEPARATOR)[0]))) {
      throw new IllegalArgumentException("Invalid Stock Name or Symbol");
    }

    // weights
    try {
      List<Double> weights = stockAndWeights.stream().map(s -> Double.parseDouble(
              s.split(Util.FILE_SEPARATOR)[1])).collect(Collectors.toList());
      if (weights.parallelStream().anyMatch(w -> w < 0)) {
        throw new IllegalArgumentException("weight cannot be negative");
      }
      Double addedWeights = weights.stream().reduce(0d, (a, b) -> a + b);
      if (addedWeights > 100d || addedWeights < 99.9) {
        throw new IllegalArgumentException("Addition of weights must be 100");
      }
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid weight passed, please pass a valid float number");
    }

    //investment amount
    if (investmentAmount <= 0) {
      throw new IllegalArgumentException("Please enter a valid positive Investment amount");
    }
    if (investmentAmount < stockAndWeights.size() * commission) {
      throw new IllegalArgumentException("The total commission for doing the above transactions "
              + "would be " + stockAndWeights.size() * commission + " please enter an investment "
              + "amount greater than the total commissions");
    }

    if (startDate == null || endDate == null) {
      throw new IllegalArgumentException("Start date cannot be null,please enter a valid date");
    }
    // startDate
    if (startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("Start date cannot be greater than endDate");
    }
    //commission
    if (commission < 0) {
      throw new IllegalArgumentException("Commission cannot be negative");
    }

    //frequency
    if (frequency < 0) {
      throw new IllegalArgumentException("Please enter a valid positive frequency");
    }

    // call the execute function once all validations are passed
    Portfolio portfolio = portfolioList.get(portfolioIndex - 1);
    List<StockWeight> stockWeights = stockAndWeights.stream().map(s -> {
      String[] arr = s.split(Util.FILE_SEPARATOR);
      Stock stockObj = getStockFromNameOrSymbol(arr[0]);
      return new StockWeight(stockObj.getStockSymbol(), stockObj.getStockName(),
              Double.parseDouble(arr[1]));
    }).collect(Collectors.toList());
    Strategy strategy = new Strategy(startDate, endDate, null, investmentAmount,
            commission, frequency, stockWeights);
    StrategyExecutor strategyExecutor = new DollarCostAveragingImpl();
    response = strategyExecutor.runStrategy(portfolio, strategy, priceProvider);
    // save strategy if recurring
    if (DAYS.between(strategy.getStartDate(), strategy.getEndDate()) > 1) {
      portfolio.getStrategies().add(strategy);
    }
    try {
      dataIO.savePortfolios(portfolioList);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return response;
  }

  @Override
  public void createInflexiblePortfolio(String portfolioName, List<String> stocks)
          throws RuntimeException {
    //validate incoming args
    boolean isInValid = stocks.parallelStream().anyMatch(stock -> {
      String[] arr = stock.split(Util.FILE_SEPARATOR);
      String name = arr[0];
      Double stockQty = Double.parseDouble(arr[1]);
      LocalDate dateOfTransaction = Util.getDateFromString(arr[2]);
      return validateStockNameSymbol().test(name) || isValidPortfolioName().test(portfolioName)
              || stockQty < 0 || dateOfTransaction.isAfter(LocalDate.now());
    });
    if (isInValid) {
      throw new IllegalArgumentException("Please verify the input arguments");
    }

    //if valid proceed with the creation
    List<PurchasedStock> purchasedStockList = new ArrayList<>();
    for (String stockString : stocks) {
      String[] stockValues = stockString.split(Util.FILE_SEPARATOR);
      LocalDate dateOfTransaction = Util.getDateFromString(stockValues[2]);
      Double stockQty = Double.parseDouble(stockValues[1]);
      Stock stockObj = getStockFromNameOrSymbol(stockValues[0]);

      PurchasedStock existingStock = purchasedStockList.parallelStream().filter((o ->
                      stockObj.getStockSymbol().equalsIgnoreCase(
                              o.getStockSymbol()))).findAny()
              .orElse(null);

      //if stock already exists edit the same stock
      if (existingStock != null) {
        StockTransaction floorTransaction = existingStock.getStockTransactions().
                floor(new StockTransaction(
                        dateOfTransaction));
        Double previousCumulativeQty = floorTransaction == null ? 0
                : floorTransaction.getQuantityAfterTransaction();
        boolean isDifferentDate =
                existingStock.getStockTransactions().add(new StockTransaction(dateOfTransaction,
                        previousCumulativeQty
                                + stockQty,
                        commission,
                        Util.TransactionType.BUY));
        if (!isDifferentDate) {
          floorTransaction.setQuantityAfterTransaction(previousCumulativeQty + stockQty);
        }
      } else {
        // if not exists create a new stock and add to the list
        purchasedStockList.add(new PurchasedStock(stockObj.getStockName(),
                stockObj.getStockSymbol(),
                new TreeSet<>(List.of(new StockTransaction(
                        dateOfTransaction, stockQty, commission,
                        Util.TransactionType.BUY)))));
      }
    }
    InFlexiblePortfolio inFlexiblePortfolio = new InFlexiblePortfolio(portfolioName,
            LocalDate.now(),
            purchasedStockList);
    portfolioList.add(inFlexiblePortfolio);
    try {
      dataIO.savePortfolios(portfolioList);
    } catch (IOException e) {
      throw new RuntimeException("Unable to save portfolio");
    }
  }

  @Override
  public int createFlexiblePortfolio(String portfolioName) throws IllegalArgumentException {
    if (isValidPortfolioName().test(portfolioName)) {
      throw new IllegalArgumentException("Portfolio name already exists");
    }
    Portfolio newPortfolio = new FlexiblePortfolio(portfolioName, LocalDate.now(),
            new ArrayList<>());
    portfolioList.add(newPortfolio);
    return portfolioList.indexOf(newPortfolio) + 1;
  }


  @Override
  public StockPriceResponseWrapper getPortfolioValueOnDate(int portfolioIndex, LocalDate date)
          throws IllegalArgumentException {
    if (portfolioIndex < 1 || portfolioIndex > portfolioList.size()) {
      throw new IllegalArgumentException("invalid portfolio index");
    }
    return portfolioList.get(portfolioIndex - 1).getPortfolioValueOnDate(date, priceProvider);
  }

  @Override
  public void rebalancePortfolio(int portfolioIndex, LocalDate date, Map<String, Double> percentMap)
          throws RuntimeException {
    if (portfolioIndex < 1 || portfolioIndex > portfolioList.size()) {
      throw new IllegalArgumentException("Invalid portfolio index");
    }
    if (date == null) {
      throw new IllegalArgumentException("Date cannot be null. Please enter a valid date");
    }
    if (date.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Date cannot be after today's date.");
    }
    //    if (percentMap == null || percentMap.get(this) < 0) {
    //      throw new IllegalArgumentException("Entered percent values cannot be null.");
    //    }
    portfolioList.get(portfolioIndex - 1).rebalancePortfolio(date, percentMap, priceProvider);
  }


  @Override
  public List<String> getAllPortfolios() {
    List<String> portfolioNames =
            portfolioList.stream().map(p -> p.getPortfolioName() + Util.FILE_OUTPUT_SEPARATOR
                    + p.getDateAdded().toString() + Util.FILE_OUTPUT_SEPARATOR
                    + p.getStocks().size()).collect(Collectors.toList());
    return portfolioNames;
  }

  private String getScaledFormat(LocalDate currentDate, Scales scale) {

    switch (scale) {
      case DAY:
        return currentDate.toString();
      case WEEK:
        String startOfWeek =
                currentDate.with(TemporalAdjusters.previousOrSame(this.firstDayOfWeek)).toString();
        String endOfWeek =
                currentDate.with(TemporalAdjusters.nextOrSame(this.lastDayOfWeek)).toString();
        return String.format("Week: %s to %s ", startOfWeek, endOfWeek);
      case MONTH:
        return currentDate.format(DateTimeFormatter.ofPattern("MMM-uuuu", Locale.ENGLISH));
      case QUARTER:
        return currentDate.format(DateTimeFormatter.ofPattern("QQQ-uuuu", Locale.ENGLISH));
      case YEAR:
        return "" + currentDate.getYear();
      case LEAP_YEAR:
        while (!currentDate.isLeapYear()) {
          currentDate = currentDate.minusYears(1);
        }
        return "" + currentDate.getYear() + "-" + currentDate.plusYears(3).getYear();
      case DECADE:
        while (currentDate.getYear() % 10 != 0) {
          currentDate = currentDate.minusYears(1);
        }
        return "" + currentDate.getYear() + "-" + currentDate.plusYears(9).getYear();
      case SEMI_CENTENNIAL:
        while (currentDate.getYear() % 50 != 0) {
          currentDate = currentDate.minusYears(1);
        }
        return "" + currentDate.getYear() + "-" + currentDate.plusYears(49).getYear();
      case CENTURY:
        while (currentDate.getYear() % 100 != 0) {
          currentDate = currentDate.minusYears(1);
        }
        return "" + currentDate.getYear() + "-" + currentDate.plusYears(99).getYear();
      default:
        return "";
    }
  }

  private Float findTotalPriceOfPortfolioOnDate(List<String> successList) {
    float totalPrice = 0;
    for (int i = 0; i < successList.size(); i++) {
      totalPrice += Float.parseFloat(successList.get(i).split("\\|")[4]);
    }
    return Float.valueOf(totalPrice);
  }

  /**
   * This method fetches the graph date for given interval of date and formats the data in order for
   * the view to display it.
   *
   * @param portfolioNumber The portfolio number from the list
   * @param startDate       The start date of the scale on which needs to be shown
   * @param endDate         The end date of the scale which needs to be shown.
   * @return List of strings of the data with scale, price and normalized price.
   */
  @Override
  public List<String> fetchGraphData(int portfolioNumber, LocalDate startDate,
                                     LocalDate endDate) {
    Portfolio portfolio = portfolioList.get(portfolioNumber - 1);

    long daysBetween = DAYS.between(startDate, endDate);
    List<LocalDate> datesList =
            Stream.iterate(startDate, date -> date.plusDays(1))
                    .limit(daysBetween + 1).collect(Collectors.toList());

    Map<String, List<LocalDate>> result_map = new TreeMap<>();
    Scales scaleDecided = null;
    for (Scales scale : Scales.values()) {
      if (Period.between(startDate, endDate).getYears() > 10) {
        if (scale.equals(Scales.DAY) || scale.equals(Scales.WEEK)
                || scale.equals(Scales.MONTH) || scale.equals(Scales.QUARTER)) {
          continue;
        }
      }
      result_map =
              datesList.stream().collect(Collectors.groupingBy(item -> getScaledFormat(item, scale),
                      LinkedHashMap::new,
                      Collectors.toList()));
      int minFactor = 5;
      if (scale.equals(Scales.DAY)) {
        minFactor = 0;
      }
      if (result_map.size() >= minFactor && result_map.size() < 31) {
        scaleDecided = scale;
        break;
      }
    }

    List<String> dateScales = new ArrayList<>(result_map.keySet());
    List<LocalDate> dates = getDatesToHitAPI(dateScales, scaleDecided);
    dates.remove(dates.size() - 1);
    dates.add(endDate);
    List<StockPriceResponseWrapper> responses =
            dates.stream().map(o -> portfolio.getPortfolioValueOnDate(o, this.priceProvider))
                    .collect(Collectors.toList());

    List<Float> prices =
            responses.stream().map(o -> findTotalPriceOfPortfolioOnDate(o.getSuccessList()))
                    .collect(Collectors.toList());
    Float max = Collections.max(prices);
    List<String> graphOutput = IntStream.range(0, prices.size())
            .mapToObj(i -> new GraphResponseWrapper(dateScales.get(i),
                    prices.get(i)))
            .map(o -> o.getString(max))
            .collect(Collectors.toList());
    return graphOutput;

  }

  private enum Scales {
    DAY, WEEK, MONTH, QUARTER, YEAR, LEAP_YEAR, DECADE, SEMI_CENTENNIAL, CENTURY
  }
}
