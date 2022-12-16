package main.java.controller;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;

import main.java.model.IModel;
import main.java.model.entities.StockPriceResponseWrapper;
import main.java.model.util.Util;
import main.java.view.IView;

/**
 * This class represents the text based implementation of the IController Interface. All the methods
 * in the class are implemented considering text based user inputs and outputs.
 */
public class TextController implements IController {

  private final IModel iModel;

  private final Function<String, Integer> intParsingFn = (s -> Integer.parseInt(s));
  private final Function<String, Double> doubleParsingFn = (s -> Double.parseDouble(s));
  private final IView iView;
  private final Scanner in;
  private final String MAIN_MENU_ITEMS = "==================================\n" + "What would you"
          + " like to do today?\n" + "1.Create a InFlexible PortFolio\n" + "2.Create a Flexible "
          + "PortFolio\n3.Get Portfolio composition\n4.Get value of Portfolio on a "
          + "given Date\n5.Load Flexible Portfolio\n6.Load InFlexible Portfolio\n"
          + "7.Buy/Sell a stock in an portfolio\n8.Get cost basis of a Portfolio\n"
          + "9.Update Commission of the system\n10.Show Graphical Representation of Portfolio\n"
          + "11. Rebalance A Portfolio.\n" + "12.Quit";

  /**
   * This constructs the TextController Object and behaves according the model and view passed to
   * the method.
   *
   * @param iModel Object of the model that the controller will interact with for the
   *               processing.
   * @param iView  Object of the View that the controller will interact with for displaying
   *               the output.
   * @param in     InputStream from which the user input should be taken inside the controller.
   */
  public TextController(IModel iModel, IView iView, InputStream in) {
    this.iModel = iModel;
    this.iView = iView;
    this.in = new Scanner(in);
  }

  /**
   * A generic method which takes string input from user until the given validation(predicate) is
   * passed.
   *
   * @param predicate     predicate that needs to be run on the string input received from the
   *                      user.
   * @param outputDisplay display String that needs to be displayed before taking the user
   *                      input.
   * @param errorString   error string that needs to be displayed if the input string fails
   *                      the validation
   * @return String which was received as an input from user and passed the validation.
   */
  private String takeStringInput(Predicate<String> predicate, String outputDisplay,
                                 String errorString) {
    iView.displayText(outputDisplay);
    String inputString = in.nextLine();
    // take input until validation is passed
    while (predicate.test(inputString)) {
      iView.displayText(errorString);
      inputString = in.nextLine();
    }
    return inputString;
  }

  /**
   * A generic method which takes file path as input from user until the path entered is valid.
   *
   * @param displayString display String that needs to be displayed before taking the user
   *                      input.
   * @param errorString   error string that needs to be displayed if the input string fails
   *                      the validation
   * @return final validated file path
   */
  private String takeCSVFilePathInput(String displayString, String errorString) {

    String inputString = "";
    iView.displayText(displayString);
    boolean isError = false;
    int index = 0;
    // take input until validation is passed
    do {
      if (isError) {
        iView.displayText(errorString);
      }

      inputString = in.nextLine();
      isError = !inputString.endsWith(".csv");

      if (isError) {
        isError = true;
        errorString = "File doesn't have csv extension, please reenter the path";
        continue;
      }
      File csvFile = new File(inputString);
      if (!csvFile.exists() || csvFile.isDirectory()) {
        isError = true;
        errorString = "File doesn't exist, please reenter the path";
      }
    }
    while (isError);

    return inputString;
  }


  /**
   * A generic method which takes number input from user until the given validation(predicate) is
   * passed.
   *
   * @param outputDisplay display String that needs to be displayed before taking the user
   *                      input.
   * @param errorString   error string that needs to be displayed if the input string fails
   *                      the validation
   * @return number which was received as an input from user and passed the validation.
   */
  private <R> R takeNumericInput(Predicate<R> predicate, Function<String, R> parsingFunction,
                                 String outputDisplay, String errorString) {
    iView.displayText(outputDisplay);
    boolean isError = false;
    R number = null;
    // take input until validation is passed
    do {
      if (isError) {
        iView.displayText(errorString);
      }
      try {
        String inputString = in.nextLine();

        number = parsingFunction.apply(inputString);
        // test the input string on the predicate
        isError = predicate.test(number);
      } catch (NumberFormatException e) {
        // if unable to parse again ask for input
        isError = true;
        continue;
      }

    }
    while (isError);
    return number;
  }

  /**
   * A generic method which takes Date input from user until the given validation(predicate) is
   * passed.
   *
   * @param outputDisplay display String that needs to be displayed before taking the user
   *                      input.
   * @param errorString   error string that needs to be displayed if the input string fails
   *                      the validation
   * @return Date which was received as an input from user and passed the validation.
   */
  private LocalDate takeDateInput(Predicate<LocalDate> predicate, String outputDisplay,
                                  String errorString) {
    String inputString;
    LocalDate inputDate = null;
    iView.displayText(outputDisplay);
    boolean isError = false;
    // take input until validation is passed
    do {
      if (isError) {
        iView.displayText(errorString);
      }
      try {
        inputString = in.nextLine();
        inputDate = Util.getDateFromString(inputString);
        // test given date on the predicate
        isError = predicate.test(inputDate);
        //System.out.println("predicate is: "+isError);
      } catch (DateTimeParseException ex) {
        isError = true;
        // if parsing exception again ask for input
        continue;
      }

    }
    while (isError);
    return inputDate;
  }

  /**
   * This method allows user to select from the portfolio List and return the portfolio number.
   *
   * @param inputMessage display String that needs to be displayed before taking the user
   *                     input.
   * @return The portfolio index from the list of portfolios.
   */
  private int showPortfoliosAndTakeUserInput(String inputMessage) throws RuntimeException {
    List<String> persistedPortfolio = iModel.getAllPortfolios();
    if (persistedPortfolio.size() == 0) {
      throw new RuntimeException("No portfolios exists in the system, please create one");
    }
    iView.createPortfolioTable(persistedPortfolio);
    // get the index of the portfolio to be displayed from the user
    return takeNumericInput((s -> s < 1 || s > persistedPortfolio.size()), intParsingFn,
            inputMessage, "Invalid number, please enter a valid portfolio number.");
  }

  private void transactStock(int portfolioNumber) {
    // take stock name/symbol
    String stockName = takeStringInput(iModel.validateStockNameSymbol(),
            "Enter the name or symbol of the stock :",
            "No such stock name or stock Symbol found please enter a "
                    + "valid input");

    String transactionType = takeStringInput((s -> !(s.equalsIgnoreCase("b")
                    || s.equalsIgnoreCase("s"))),
            "Do want to buy or sell the stock? Enter b for buy "
                    + "or s for sell.",
            "Invalid input please enter valid transaction type");
    Integer stockQty = takeNumericInput((s -> s <= 0), intParsingFn,
            "Enter the quantity of stocks you wish to transact?",
            "Invalid number, please enter a valid positive number.");
    LocalDate transactionDate = takeDateInput((d -> d.isAfter(LocalDate.now())),
            "Enter the date of the transaction(yyyy-mm-dd):",
            "Please enter a valid date format.");
    try {
      iModel.transactStock(portfolioNumber, stockName, stockQty.doubleValue(), transactionType,
              transactionDate);
    } catch (Exception e) {
      iView.displayText(e.getMessage());
    }
  }

  private void createFlexiblePortfolio() {

    String portfolioName = takeStringInput(iModel.isValidPortfolioName(),
            "Enter the name of the Portfolio:",
            "Portfolio name is invalid or already exists please "
                    + "enter a new name:");
    int portfolioNumber = 0;
    try {
      portfolioNumber = iModel.createFlexiblePortfolio(portfolioName);
    } catch (IllegalArgumentException e) {
      iView.displayText(e.getMessage());
      return;
    }
    // Number Of Stocks input
    int numberOfStocks = takeNumericInput((s -> s <= 0), intParsingFn,
            "How many stocks do you want to add to the portfolio?",
            "Please enter valid positive number");

    for (int i = 1; i <= numberOfStocks; i++) {
      transactStock(portfolioNumber);
    }
  }

  private void createInflexiblePortfolio() {

    String portfolioName = takeStringInput(iModel.isValidPortfolioName(),
            "Enter the name of the Portfolio:",
            "Portfolio name is invalid or already exists please "
                    + "enter a new name:");

    // Number Of Stocks input
    Integer numberOfStocks = takeNumericInput((s -> s <= 0), intParsingFn,
            "How many stocks do you want to add to the "
                    + "portfolio?",
            "Please enter valid positive number");

    // Get stock information
    List<String> stocks = new ArrayList<>();
    for (int i = 1; i <= numberOfStocks; i++) {
      // take stock name/symbol
      String stockName = takeStringInput(iModel.validateStockNameSymbol(),
              "Enter the name or symbol for stock " + i + ":",
              "No such stock name or stock Symbol found please enter a" + " valid input");

      // take quantity of stocks as input
      Integer stockQty = takeNumericInput((s -> s <= 0), intParsingFn,
              "Enter the quantity of shares purchased for stock " + i + ":",
              "Please enter a valid positive number for the quantity of stocks");
      LocalDate dateAdded = takeDateInput((d -> d.isAfter(LocalDate.now())),
              "Please Enter the date at which you bought the stock " + i
                      + "(yyyy-mm-dd) ",
              "Please Enter a valid date");
      // once all validations are passed add it to the list
      stocks.add(stockName + Util.FILE_OUTPUT_SEPARATOR + stockQty.doubleValue()
              + Util.FILE_OUTPUT_SEPARATOR
              + dateAdded);
    }
    try {
      iModel.createInflexiblePortfolio(portfolioName, stocks);
    } catch (RuntimeException e) {
      iView.displayText(e.getMessage());
    }
  }

  private void showPortfolioComp() {
    int portfolioNumber = 0;
    try {
      portfolioNumber = showPortfoliosAndTakeUserInput("Enter id of the portfolio you "
              + "want see the composition for:");
    } catch (RuntimeException e) {
      iView.displayText(e.getMessage());
      return;
    }
    LocalDate compositionDate = takeDateInput((d -> d.isAfter(LocalDate.now())),
            "Enter the date for which you want to see the composition(yyyy-mm-dd):",
            "Please enter a valid date format.");
    List<String> portfolioComp = null;
    try {
      portfolioComp = iModel.getPortfolioComposition(portfolioNumber, compositionDate);
    } catch (IllegalArgumentException e) {
      iView.displayText(e.getMessage());
      return;
    }
    iView.getPortfolioComposition(portfolioComp);
  }

  private void showPortfolioValueOnADate() {
    int portfolioNumber = 0;
    try {
      portfolioNumber = showPortfoliosAndTakeUserInput("Enter id of the portfolio you "
              + "want see the value for:");
    } catch (RuntimeException e) {
      iView.displayText(e.getMessage());
      return;
    }
    try {
      LocalDate inputDate = takeDateInput(d -> d.isAfter(LocalDate.now()), "Enter the "
                      + "date when the stock price needs to displayed(yyyy-mm-dd):",
              "Please enter a valid date.");

      // get the prices of the stocks for the given date
      StockPriceResponseWrapper output = iModel.getPortfolioValueOnDate(portfolioNumber, inputDate);
      iView.showStockResponseWrapper(output, inputDate);
    } catch (IllegalArgumentException iag) {
      iView.displayText(iag.getMessage());
    }
  }

  private void rebalanceAPortfolioOnADate() {
    int portfolioNumber = 0;
    try {
      portfolioNumber = showPortfoliosAndTakeUserInput("Enter id of the portfolio you "
              + "want to rebalance: ");
    } catch (RuntimeException e) {
      iView.displayText(e.getMessage());
      return;
    }
    LocalDate rebalanceDate = takeDateInput((d -> d.isAfter(LocalDate.now())),
            "Enter the date on which you want to rebalance the portfolio(yyyy-mm-dd):",
            "Please enter a valid date format.");

    //diaplaying the current view
    //iView.getPortfolioComposition(portfolioComp);
    //getPortfolioCompositionWithPrice
    //portfolio get stocks -> map it like stock.stream.map(..)
    List<String> portfolioComp = null;
    try {
      portfolioComp = iModel.getPortfolioComposition(portfolioNumber, LocalDate.now());
    } catch (IllegalArgumentException e) {
      iView.displayText(e.getMessage());
      return;
    }

    iView.getPortfolioComposition(portfolioComp);
    //iModel.setCommission(commission);
    Map<String, Double> percentMap = new HashMap<String, Double>();


    for (int i = 0; i < portfolioComp.size(); i++) {
      String stockName = takeStringInput(iModel.validateStockNameSymbol(),
              "Enter the name or symbol of the stock :",
              "No such stock name or stock Symbol found please enter a "
                      + "valid input");
      Double percent = takeNumericInput((s -> s < 0), doubleParsingFn,
              "Enter the new percentage of the stock " + stockName,
              "Commission fee cannot be negative, please enter a valid "
                      + "positive number");
      percentMap.put(stockName.toUpperCase(), percent);
    }

    iModel.rebalancePortfolio(portfolioNumber, rebalanceDate, percentMap);
  }

  private void editPortfolio() {
    int portfolioNumber = 0;
    try {
      portfolioNumber = showPortfoliosAndTakeUserInput("Enter id of the portfolio you "
              + "want to edit:");
    } catch (RuntimeException e) {
      iView.displayText(e.getMessage());
      return;
    }
    transactStock(portfolioNumber);
  }

  private void showCostBasis() {
    int portfolioNumber = 0;
    try {
      portfolioNumber = showPortfoliosAndTakeUserInput("Enter id of the portfolio you "
              + "want to see the cost basis for:");
    } catch (RuntimeException e) {
      iView.displayText(e.getMessage());
      return;
    }
    LocalDate transactionDate = takeDateInput((d -> d.isAfter(LocalDate.now())),
            "Enter the date for which you want to see the cost basis for(yyyy-mm-dd):",
            "Please enter a valid date format.");
    try {
      double costBasis = iModel.getCostBasis(portfolioNumber, transactionDate);
      iView.displayText(String.format("Cost basis for the portfolio on " + transactionDate
              + "is : $%.2f", costBasis));
    } catch (Exception e) {
      iView.displayText(e.getMessage());
    }
  }

  private void displayGraph() {
    int portfolioNumber = 0;
    try {
      portfolioNumber = showPortfoliosAndTakeUserInput("Enter id of the portfolio you "
              + "want to see the graph for:");
    } catch (RuntimeException e) {
      iView.displayText(e.getMessage());
      return;
    }
    try {
      LocalDate startDate = LocalDate.now();
      LocalDate endDate = LocalDate.now();
      do {
        if (startDate.isAfter(endDate)) {
          iView.displayText("Start Date cannot be after End Date, please reenter the " + "dates\n");
        }
        startDate = takeDateInput(d -> d.isAfter(LocalDate.now()), "Enter the start "
                + "date to display the graph(yyyy-mm-dd)", "Please enter a valid date.");

        endDate = takeDateInput(d -> d.isAfter(LocalDate.now()), "Enter the end date "
                + "to display the graph(yyyy-mm-dd)", "Please enter a valid date.");
      }
      while (startDate.isAfter(endDate));

      List<String> graphOutput = iModel.fetchGraphData(portfolioNumber, startDate, endDate);
      iView.displayGraph(graphOutput);
    } catch (IllegalArgumentException iag) {
      iView.displayText(iag.getMessage());
    }

  }

  @Override
  public void start() {
    boolean quit = false;
    while (!quit) {
      iView.displayText(MAIN_MENU_ITEMS);
      String option = in.nextLine();
      switch (option) {
        case "1":
          createInflexiblePortfolio();
          break;
        case "2":
          createFlexiblePortfolio();
          break;
        case "3":
          showPortfolioComp();
          break;
        case "4":
          showPortfolioValueOnADate();
          break;
        case "5":
          // load portfolio from a file
          iView.displayText("Please place your file at the path res/user_flexible.csv\n"
                  + "Please make sure the data in the file is in format:\n"
                  + "Stock Details|Quantity|Date\nAAPL|34|2022-05-12\nCarvana "
                  + "Co|87|2022-05-12\n");

          String portfolioName = takeStringInput(iModel.isValidPortfolioName(), "What "
                          + "would you like your portfolio to be called?",
                  "Portfolio name is invalid or is already used");
          try {
            iView.displayText(iModel.loadFlexiblePortfolioFromFile(portfolioName, "res"
                    + "/user_flexible.csv"));
          } catch (IllegalArgumentException e) {
            iView.displayText(e.getMessage());
          }
          break;
        case "6":
          // load portfolio from a file
          iView.displayText("Please place your file at the path res/user_inflexible.csv\n"
                  + "Please make sure the data in the file is in format:\n"
                  + "StockName|Quantity|TransactionDate|TransactionType\n"
                  + "ABBV|35|2022-04-12|b\n"
                  + "AAPL|100|2022-05-10|b");

          String portfolioNameInFlexible = takeStringInput(iModel.isValidPortfolioName(),
                  "What would you like your portfolio to be called?",
                  "Portfolio name is invalid or is already used");
          try {
            iView.displayText(iModel.loadInFlexiblePortfolioFromFile(portfolioNameInFlexible));
          } catch (Exception e) {
            iView.displayText(e.getMessage());
          }
          break;
        case "7":
          editPortfolio();
          break;
        case "8":
          showCostBasis();
          break;
        case "9":
          Double commission = takeNumericInput((s -> s < 0), doubleParsingFn,
                  "Enter the new commission that you want to configure in the system",
                  "Commission fee cannot be negative, please enter a valid "
                          + "positive number");
          iModel.setCommission(commission);
          break;
        case "10":
          displayGraph();
          break;
        case "11":
          rebalanceAPortfolioOnADate();
          break;
        case "12":
          quit = true;
          break;
        default:
          iView.displayText("Invalid option, please enter a valid option");
      }
    }
  }


}
