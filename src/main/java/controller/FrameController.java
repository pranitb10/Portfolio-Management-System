package main.java.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.JFileChooser;

import main.java.model.IModel;
import main.java.model.entities.StockPriceResponseWrapper;
import main.java.model.util.Util;
import main.java.view.CreateFlexiblePortfolioView;
import main.java.view.FlexibleStockDetails;
import main.java.view.GraphDisplayView;
import main.java.view.IFrameView;
import main.java.view.LoadPortfolio;
import main.java.view.MainMenuView;
import main.java.view.PortfolioCompositionSelection;
import main.java.view.PortfolioDisplayComposition;
import main.java.view.RebalanceView;
import main.java.view.StockPercentView;
import main.java.view.StrategyDistribution;
import main.java.view.StrategyOnDate;
import main.java.view.UpdateCommissionView;

/**
 * This class implements the IFrameController. All the methods are implemented considering GUI based
 * inputs and outputs.
 */
public class FrameController implements IFrameController {
  private final IModel model;
  private final Map<String, Runnable> buttonClickedMap = new HashMap<String, Runnable>();
  Map<Integer, List<String>> stockDetails;
  private IFrameView view;
  DocumentListener portfolioListener = new DocumentListener() {
    @Override
    public void insertUpdate(DocumentEvent e) {
      checkPortfolio();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      checkPortfolio();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
      checkPortfolio();
    }

    private void checkPortfolio() {

      Map<String, String> fieldData = view.getFields();
      boolean isValidPortfolioName = portfolioNameChecker(fieldData.get("portfolioName"));
      boolean isValidNumberOfStocks = checkNumberOfStocks(fieldData.get("numberOfStocks"));

      view.setErrorLabels("portfolio", isValidPortfolioName ? ""
              : "Invalid Portfolio Name");
      view.setErrorLabels("numStocks", isValidNumberOfStocks ? ""
              : "Invalid Stocks Number");

      view.setSubmit(false);
      if (isValidPortfolioName && isValidNumberOfStocks) {
        view.setSubmit(true);
      }
    }

  };
  DocumentListener portfolioListenerLoad = new DocumentListener() {
    @Override
    public void insertUpdate(DocumentEvent e) {
      checkPortfolioListener();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      checkPortfolioListener();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
      checkPortfolioListener();
    }

    private void checkPortfolioListener() {

      Map<String, String> fieldData = view.getFields();
      boolean isValidPortfolioName = portfolioNameChecker(fieldData.get("portfolioName"));

      view.setErrorLabels("portfolio", isValidPortfolioName ? ""
              : "Invalid Portfolio Name");

      view.setSubmit(false);
      if (isValidPortfolioName) {
        view.setSubmit(true);
      }
    }

  };
  DocumentListener stockListener = new DocumentListener() {
    DocumentEvent event;

    @Override
    public void insertUpdate(DocumentEvent e) {
      this.event = e;
      checkStockDetails();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      this.event = e;
      checkStockDetails();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
      this.event = e;
      checkStockDetails();
    }

    private void checkStockDetails() {

      Map<String, String> fieldData = view.getFields();

      boolean isValidStockName = stockNameChecker(fieldData.get("stockName"));
      boolean isValidQuantityOfStocks = checkQuantityOfStocks(fieldData.get("quantityOfStocks"));

      view.setErrorLabels("stockName", isValidStockName ? "" : "Invalid Stock Name");
      view.setErrorLabels("quantity", isValidQuantityOfStocks ? ""
              : "Invalid Stock Quantity");

      view.setSubmit(false);
      if (isValidStockName && isValidQuantityOfStocks) {
        view.setSubmit(true);
      }
    }

  };
  DocumentListener weightageListener = new DocumentListener() {
    DocumentEvent event;

    @Override
    public void insertUpdate(DocumentEvent e) {
      this.event = e;
      checkData();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      this.event = e;
      checkData();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
      this.event = e;
      checkData();
    }

    private void checkData() {

      Map<String, String> fieldData = view.getFields();

      boolean isValidStockName = stockNameChecker(fieldData.get("stockName"));
      boolean isValidQuantityOfStocks = checkWeightageOfStocks(fieldData.get("weightageOfStocks"));

      view.setErrorLabels("stockName", isValidStockName ? "" : "Invalid Stock Name");
      view.setErrorLabels("weightageOfStocks", isValidQuantityOfStocks ? ""
              : "Invalid Weightage");

      view.setSubmit(false);
      if (isValidStockName && isValidQuantityOfStocks) {
        view.setSubmit(true);
      }
    }

  };
  DocumentListener updateCommissionListener = new DocumentListener() {
    DocumentEvent event;

    @Override
    public void insertUpdate(DocumentEvent e) {
      this.event = e;
      checkCommissionListener();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      this.event = e;
      checkCommissionListener();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
      this.event = e;
      checkCommissionListener();
    }

    private void checkCommissionListener() {

      Map<String, String> fieldData = view.getFields();

      boolean isValidCommission = checkCommission(fieldData.get("commissionText"));

      view.setErrorLabels("commissionText", isValidCommission ? ""
              : "Invalid Commission");

      view.setSubmit(false);
      if (isValidCommission) {
        view.setSubmit(true);
      }
    }

  };
  DocumentListener executeStrategyDateListener = new DocumentListener() {
    DocumentEvent event;

    @Override
    public void insertUpdate(DocumentEvent e) {
      this.event = e;
      checkStrategyParameters();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      this.event = e;
      checkStrategyParameters();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
      this.event = e;
      checkStrategyParameters();
    }

    private void checkStrategyParameters() {

      Map<String, String> fieldData = view.getFields();

      boolean isValidPrice = checkAmount(fieldData.get("amount"));
      boolean isValidStockQuantity = checkNumberOfStocks(fieldData.get("numStocks"));
      boolean isValidFrequency = true;

      if (fieldData.get("onADate").equals("false")) {
        isValidFrequency = checkQuantityOfStocks(fieldData.get("frequency"));
      }

      view.setErrorLabels("numStocks", isValidStockQuantity ? ""
              : "Invalid Stock Numbers");
      view.setErrorLabels("amount", isValidPrice ? "" : "Invalid Amount");
      view.setErrorLabels("frequency", isValidFrequency ? "" : "Invalid Frequency");


      view.setSubmit(false);
      if (isValidPrice & isValidStockQuantity & isValidFrequency) {
        view.setSubmit(true);
      }
    }

  };
  private int portFolioIndex;
  private ButtonListener buttonListener;
  private String portfolioName;
  private int maxStocks;
  private LocalDate date1;
  private LocalDate date2 = LocalDate.MAX.minusYears(1);
  private List<String> stocksList;
  private int flexiblePortfolionumber;
  private double amount;
  private boolean fromStrategy;
  private int frequency = 1;
  private List<String> stocksDetails;
  private boolean isFromDateRange;

  /**
   * Constructor takes in the model and initiate the model instance for the graphical user
   * interface. In this way the controller can interact with the model.
   *
   * @param m The instance of the model
   */
  public FrameController(IModel m) {
    model = m;
  }

  /**
   * It allows the controller to send in menu items to the view to show the operations that can be
   * performed by the portfolio management system.
   *
   * @return List of menu items in string format
   */
  @Override
  public List<String> getMenuItems() {
    return Arrays.asList("Create Portfolio",
            "Get Portfolio Composition",
            "Get Portfolio Value on a Date",
            "Load Portfolio",
            "Initiate Transaction",
            "Determine Cost Basis",
            "Update Commission of the system",
            "Show Graphical Representation of Portfolio",
            "Execute Strategy on a Date",
            "Execute Strategy on a Date Range",
            "Rebalance a Portfolio",
            "Exit Menu");
  }

  /**
   * This method allows the controller to exit the application.
   */
  @Override
  public void exitProgram() {
    System.exit(0);
  }

  private void createFlexiblePortfolio(boolean fromStrategy) {
    this.fromStrategy = fromStrategy;
    this.view.getFrame().dispose();
    this.view = new CreateFlexiblePortfolioView(this);
    this.view.addDocumentListener(portfolioListener);
    this.view.addActionListener(buttonListener);
  }

  private void updateCommission() {
    this.view.getFrame().dispose();
    this.view = new UpdateCommissionView(model.getCommission());
    this.view.addDocumentListener(updateCommissionListener);
    this.view.addActionListener(buttonListener);
  }

  private void mainMenu() {
    this.view.getFrame().dispose();
    this.view = new MainMenuView("Portfolio Management System", this);
    this.view.addActionListener(buttonListener);
  }


  private void flexibleStockDetails(int numStock, int maxStocks, int portfolioNumber) {
    this.view.getFrame().dispose();
    this.view = new FlexibleStockDetails(numStock, maxStocks, portfolioNumber, this);
    this.view.addDocumentListener(stockListener);
    this.view.addActionListener(buttonListener);
  }

  private void portfolioComposition(String method) {
    if (model.getAllPortfolios().size() == 0) {
      this.view.showDialogBox("There are no portfolios to show composition on",
              "Error", true);
      return;
    }
    this.view.getFrame().dispose();
    this.view = new PortfolioCompositionSelection(model.getAllPortfolios(), "Portfolio "
            + "Selection", method, this);
    this.view.addActionListener(buttonListener);
  }

  private void rebalancePortfolio(String method) {
    if (model.getAllPortfolios().size() == 0) {
      this.view.showDialogBox("There are no portfolios to show composition on",
              "Error", true);
      return;
    }
    this.view.getFrame().dispose();
    this.view = new RebalanceView(model.getAllPortfolios(), "Portfolio "
            + "Selection For Rebalance", method, this);
    this.view.addActionListener(buttonListener);
  }


  private void showPortfolioComposition(List<String> output, String[] columnHeaders) {
    this.view.getFrame().dispose();
    this.view = new PortfolioDisplayComposition("Portfolio Composition", output,
            columnHeaders);
    this.view.addActionListener(buttonListener);

  }


  private boolean transactionView(Map<String, String> data, int portfolioNumber) {
    try {
      model.transactStock(portfolioNumber, data.get("stockName"),
              Double.parseDouble(data.get(
                      "quantityOfStocks")), data.get("transactionType"),
              LocalDate.parse(data.get(
                      "datePurchased")));
      return true;
    } catch (RuntimeException e) {
      String message = e instanceof DateTimeParseException ? "Please enter a Valid Date"
              : e.getMessage();
      this.view.showDialogBox(message, "Uh Oh..", true);
      return false;
    }
  }

  private void createBarChart(List<String> data) {
    this.view.getFrame().dispose();
    this.view = new GraphDisplayView(data, "Graphical Representation");
    this.view.addActionListener(buttonListener);
  }

  private void loadPortfolioView() {
    this.view.getFrame().dispose();
    this.view = new LoadPortfolio("Load Custom Portfolio", this);
    this.view.addActionListener(buttonListener);
    this.view.addDocumentListener(portfolioListenerLoad);

  }

  private void executeStrategy(int portfolioNumber, int numStocks, boolean isOnaDate) {
    this.view.getFrame().dispose();
    this.view = new StrategyOnDate(model.getCommission(), numStocks, this, portfolioNumber,
            isOnaDate);
    this.view.addDocumentListener(executeStrategyDateListener);
    this.view.addActionListener(buttonListener);
  }

  private void getGetStrategyWeightage(int numStock, int maxStocks, int portfolioNumber,
                                       String stockName) {
    this.view.getFrame().dispose();
    this.view = new StrategyDistribution(numStock, maxStocks, stockName, portfolioNumber);
    this.view.addDocumentListener(weightageListener);
    this.view.addActionListener(buttonListener);

  }

  private void configureButtonListener() {

    buttonListener = new ButtonListener();

    this.buttonClickedMap.put("Main Menu", () -> {
      this.mainMenu();
    });


    this.buttonClickedMap.put("Submit Stock Details", () -> {

      Map<String, String> data = this.view.getFields();

      int stockNum = Integer.parseInt(data.get("numStock"));
      maxStocks = Integer.parseInt(data.get("maxStocks"));
      int portfolioNumber = Integer.parseInt(data.get("portfolioNumber"));
      if (!transactionView(data, portfolioNumber)) {
        return;
      }
      if (stockNum == maxStocks) {
        this.mainMenu();
        return;
      }
      flexibleStockDetails(stockNum + 1, maxStocks, portfolioNumber);
    });

    this.buttonClickedMap.put("Submit Portfolio", () -> {
      stocksList = new ArrayList<>();
      Map<String, String> data = this.view.getFields();
      portfolioName = data.get("portfolioName");
      maxStocks = Integer.parseInt(data.get("numberOfStocks"));
      if (fromStrategy) {
        this.executeStrategy(model.createFlexiblePortfolio(portfolioName), maxStocks,
                false);
      } else {
        flexibleStockDetails(1, maxStocks, model.createFlexiblePortfolio(portfolioName));
      }
    });

    this.buttonClickedMap.put("Submit Commission", () -> {
      Map<String, String> data = this.view.getFields();
      String commissionText = data.get("commissionText");
      try {
        model.setCommission(Double.parseDouble(commissionText));
        this.view.showDialogBox("Commission Updated Successfully", "Successful",
                false);
        this.mainMenu();
      } catch (IllegalArgumentException iag) {
        this.view.showDialogBox(iag.getMessage(), "Failure", true);
      }
    });

    this.buttonClickedMap.put("Submit Loaded File", () -> {

      JFileChooser fileUpload =
              new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

      int response = fileUpload.showOpenDialog(null);

      if (response != JFileChooser.APPROVE_OPTION) {
        this.view.showDialogBox("File Not uploaded correctly", "Error", true);
        return;
      }
      Map<String, String> data = this.view.getFields();
      portfolioName = data.get("portfolioName");

      try {
        this.model.loadFlexiblePortfolioFromFile(portfolioName,
                fileUpload.getSelectedFile().getAbsolutePath());
        this.view.showDialogBox(portfolioName + " has been Loaded successfully",
                "Success", false);
        this.mainMenu();
      } catch (IllegalArgumentException iag) {
        this.view.showDialogBox(iag.getMessage(), "Error", true);
      } catch (RuntimeException e) {
        this.view.showDialogBox(e.getMessage(), "Error", true);
      }
    });

    this.buttonClickedMap.put("Submit on Date Strategy", () -> {
      Map<String, String> data = this.view.getFields();
      int numStocks = Integer.parseInt(data.get("numStocks"));
      int portfolioNumber = Integer.parseInt(data.get("portfolioNumber"));
      amount = Double.parseDouble(data.get("amount"));
      if (data.get("date").equals("")) {
        this.view.showDialogBox("The Date Entered is Invalid", "Invalid Date",
                true);
        return;
      }
      date1 = LocalDate.parse(data.get("date"));
      date2 = date1;
      stocksDetails = new ArrayList<>();
      stocksDetails = model.getPortfolioComposition(portfolioNumber, LocalDate.now());
      String stockName = stocksDetails.get(0).split("\\|")[1];
      this.getGetStrategyWeightage(1, numStocks, portfolioNumber, stockName);

    });

    this.buttonClickedMap.put("Submit on Date Range", () -> {
      Map<String, String> data = this.view.getFields();
      int numStocks = Integer.parseInt(data.get("numStocks"));
      int portfolioNumber = Integer.parseInt(data.get("portfolioNumber"));
      amount = Double.parseDouble(data.get("amount"));
      frequency = Integer.parseInt(data.get("frequency"));
      if (data.get("date").equals("")) {
        this.view.showDialogBox("The Start Date Entered is Invalid", "Invalid Date",
                true);
        return;
      }
      if (data.get("anotherDate").equals("")) {
        this.view.showDialogBox("The End Date Entered is Invalid", "Invalid Date",
                true);
        return;
      }
      date1 = LocalDate.parse(data.get("date"));

      if (data.get("recurring").equals("false")) {
        date2 = LocalDate.parse(data.get("anotherDate"));
      }

      if (date2.isBefore(date1)) {
        this.view.showDialogBox("Start Date cannot be after End Date", "Invalid Date",
                true);
        return;
      }
      stocksDetails = new ArrayList<>();
      this.getGetStrategyWeightage(1, numStocks, portfolioNumber, "");
    });

    this.buttonClickedMap.put("Submit Weightage", () -> {
      Map<String, String> data = this.view.getFields();
      int numStock = Integer.parseInt(data.get("numStock"));
      maxStocks = Integer.parseInt(data.get("maxStocks"));
      String stockName = data.get("stockName");
      String weightage = data.get("weightageOfStocks");
      stocksList.add(stockName + "|" + weightage);


      int portfolioNumber = Integer.parseInt(data.get("portfolioNumber"));
      if (numStock == maxStocks) {
        isFromDateRange = false;
        String portfolioName = model.getAllPortfolios().get(portfolioNumber - 1).split("\\|")[0];
        try {
          String response = model.executeStrategy(portfolioNumber, stocksList, amount, date1, date2,
                  model.getCommission(), frequency);
          this.view.showDialogBox(response, "Outcome of Strategy", false);
        } catch (RuntimeException e) {
          this.view.showDialogBox(e.getMessage(), "Error", true);
        }
        fromStrategy = false;
        this.mainMenu();
        return;
      }
      if (!isFromDateRange) {
        stocksDetails = model.getPortfolioComposition(portfolioNumber, LocalDate.now());
        stockName = (stockName.equals("") && stockDetails.size() < numStock) ? "" :
                stocksDetails.get(numStock).split("\\|")[1];
      }
      this.getGetStrategyWeightage(numStock + 1, maxStocks, portfolioNumber,
              isFromDateRange ? "" : stockName);

    });

    this.buttonClickedMap.put("Rebalance portfolio", () -> {
      Map<String, String> data = this.view.getFields();
      String method = data.get("method");
      String percentString = data.get("percentages");
      System.out.println("percent string: " + percentString);
      //this.view.getFrame().dispose();

      try {
        Map<String, Double> percentMap = new HashMap<String, Double>();
        String[] listOfSplittedstrings = percentString.split("\\|");
        for (int i = 0; i < listOfSplittedstrings.length; i++) {
          String[] pMap = listOfSplittedstrings[i].split("\\:");
          percentMap.put(pMap[0].toUpperCase(), Double.parseDouble(pMap[1]));

        }
        System.out.println("percentMap: " + percentMap);
        model.rebalancePortfolio(portFolioIndex, LocalDate.now(), percentMap);
        this.view.showDialogBox("Portfolio Rebalanced", "Portfolio Rebalanced",
                false);
        this.mainMenu();
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    this.buttonClickedMap.put("Submit Portfolio Choice", () -> {
      stocksList = new ArrayList<>();
      Map<String, String> data = this.view.getFields();
      String dateOpted = data.get("dateOpted1");
      String method = data.get("method");
      //String percentString = data.get("percentages");
      //System.out.println("percent string: "+ percentString);

      if (dateOpted.equals("")) {
        this.view.showDialogBox("The Date Entered is Invalid", "Invalid Date",
                true);
        return;
      }
      Integer portfolioNumber = Integer.parseInt(data.get("selectedPortfolio"));
      portFolioIndex = portfolioNumber;
      System.out.println("portfolio number: " + portfolioNumber);
      System.out.println("method: " + method);
      switch (method) {
        case "composition":
          String[] columnHeaders = {"id", "Stock Code", "Name of Company",
                                    "Number of Shares Brought"};
          try {
            List<String> output = model.getPortfolioComposition(portfolioNumber,
                    LocalDate.parse(dateOpted));
            this.showPortfolioComposition(output, columnHeaders);
          } catch (IllegalArgumentException e) {
            this.view.showDialogBox(e.getMessage(), "Error", true);
          }
          break;
        case "value":
          try {
            StockPriceResponseWrapper responseWrapper =
                    model.getPortfolioValueOnDate(portfolioNumber,
                            LocalDate.parse(dateOpted));
            this.view.showDialogBox(String.format("The total value of the portfolio on %s: $%.2f",
                            dateOpted,
                            responseWrapper.getPortfolioValue()), "Portfolio Value",
                    false);

          } catch (IllegalArgumentException e) {
            this.view.showDialogBox(e.getMessage(), "Error", true);
          }
          this.mainMenu();
          break;
        case "transaction":
          flexibleStockDetails(1, 1, portfolioNumber.intValue());
          break;
        case "costBasis":
          try {
            double costBasis = model.getCostBasis(portfolioNumber.intValue(),
                    LocalDate.parse(dateOpted));
            this.view.showDialogBox(String.format("Cost basis for the portfolio on " + dateOpted
                    + " is : $%.2f", costBasis), "Cost Basis", false);
            this.mainMenu();
          } catch (Exception e) {
            this.view.showDialogBox(e.getMessage(), "Error", true);
          }
          break;
        case "graph":
          String anotherDateOpted = data.get("dateOpted2");
          if (anotherDateOpted.equals("")) {
            this.view.showDialogBox("The Date Entered is Invalid", "Invalid Date",
                    true);
            return;
          }
          try {
            LocalDate beginDate = LocalDate.parse(dateOpted);
            LocalDate endDate = LocalDate.parse(anotherDateOpted);
            if (beginDate.isAfter(endDate)) {
              this.view.showDialogBox("Start Date cannot be after End Date", "Error",
                      true);
              return;
            }
            List<String> barData = model.fetchGraphData(portfolioNumber.intValue(), beginDate,
                    endDate);
            this.createBarChart(barData);
          } catch (IllegalArgumentException e) {
            this.view.showDialogBox(e.getMessage(), "Error",
                    true);
            return;
          }
          break;
        case "executeOnADate":
          String portfolioDetails = model.getAllPortfolios().get(portfolioNumber - 1);

          List<String> portfolioDetailsSeparated =
                  List.of(portfolioDetails.split(Util.FILE_SEPARATOR));
          String numStocks = portfolioDetailsSeparated.get(portfolioDetailsSeparated.size() - 1);
          this.executeStrategy(portfolioNumber, Integer.parseInt(numStocks), true);
          break;
        case "executeOnADateRange":
          String portfolioDetailsRange = model.getAllPortfolios().get(portfolioNumber - 1);

          List<String> portfolioDetailsSeparatedRange =
                  List.of(portfolioDetailsRange.split(Util.FILE_SEPARATOR));
          String numStocksRange =
                  portfolioDetailsSeparatedRange.get(portfolioDetailsSeparatedRange.size() - 1);
          this.executeStrategy(portfolioNumber, Integer.parseInt(numStocksRange), false);
          break;
        case "rebalance":
          this.view.getFrame().dispose();
          System.out.println(model.getPortfolioComposition(portFolioIndex, LocalDate.now()));

          this.view = new StockPercentView(model.getPortfolioComposition(portFolioIndex,
                  LocalDate.now()), "Set Stock "
                  + " Percent ", method, this);
          this.view.addActionListener(buttonListener);
          System.out.println("Rebalancing portfolio " + portFolioIndex);
          break;
        default:
          break;

      }


    });


    this.buttonClickedMap.put("Get Portfolio Composition", () -> {
      this.portfolioComposition("composition");
    });

    this.buttonClickedMap.put("Rebalance a Portfolio", () -> {
      this.rebalancePortfolio("rebalance");
    });


    this.buttonClickedMap.put("Load Portfolio", () -> {
      this.loadPortfolioView();
    });

    this.buttonClickedMap.put("Create Portfolio", () -> {
      this.createFlexiblePortfolio(false);
    });

    this.buttonClickedMap.put("Create Portfolio from Strategy", () -> {
      this.createFlexiblePortfolio(true);
    });

    this.buttonClickedMap.put("Get Portfolio Value on a Date", () -> {
      this.portfolioComposition("value");
    });

    this.buttonClickedMap.put("Determine Cost Basis", () -> {
      this.portfolioComposition("costBasis");
    });

    this.buttonClickedMap.put("Initiate Transaction", () -> {
      this.portfolioComposition("transaction");
    });

    this.buttonClickedMap.put("Update Commission of the system", () -> {
      this.updateCommission();
    });

    this.buttonClickedMap.put("Show Graphical Representation of Portfolio", () -> {
      this.portfolioComposition("graph");
    });

    this.buttonClickedMap.put("Execute Strategy on a Date", () -> {
      this.portfolioComposition("executeOnADate");
    });

    this.buttonClickedMap.put("Execute Strategy on a Date Range", () -> {
      this.portfolioComposition("executeOnADateRange");
      isFromDateRange = true;
    });

    buttonClickedMap.put("Exit Menu", () -> {
      this.exitProgram();
    });

    buttonListener.setButtonClickedActionMap(buttonClickedMap);
    this.view.addActionListener(buttonListener);
  }

  /**
   * This method allows to take in a view and sets as start point for the application, assigns the
   * button listeners to the entire application.
   *
   * @param v The view which needs to be set as the start view
   */
  @Override
  public void setView(IFrameView v) {
    view = v;
    configureButtonListener();
  }


  /**
   * This method takes string which needs to validated and check if the portfolio name is not
   * integer and the number is less than 0.
   *
   * @param s The candidate portfolio name that needs to checked.
   * @return boolean which would convey portfolio name is valid or not
   */
  @Override
  public boolean portfolioNameChecker(String s) {
    return !model.isValidPortfolioName().test(s);
  }

  /**
   * This method takes string which needs to validated and check if the number is not integer and
   * the number is less than 0.
   *
   * @param s The candidate string which may or may not be number of stocks
   * @return boolean which would convey number of stock is valid or not
   */
  @Override
  public boolean checkNumberOfStocks(String s) {
    try {
      Integer number = Integer.parseInt(s);
      // test the input string on the predicate
      return number > 0;
    } catch (NumberFormatException e) {
      // if unable to parse again ask for input
      return false;
    }
  }

  /**
   * This method takes string which needs to validated and check if the number is not integer and
   * the number is less than 0.
   *
   * @param s The candidate string which may or may not be a valid quantity of stock
   * @return boolean which would convey quantity of stock is valid or not
   */
  @Override
  public boolean checkQuantityOfStocks(String s) {
    try {
      Integer number = Integer.parseInt(s);
      // test the input string on the predicate
      return number > 0;
    } catch (NumberFormatException e) {
      // if unable to parse again ask for input
      return false;
    }
  }

  /**
   * This takes input from user on the weightage of stock in a particular strategy and validates if
   * the individual weightage is valid or not. The criteria is the number should between 0-100.
   *
   * @param s The candidate string which may or may not be a valid weightage of stock
   * @return boolean which would convey weightage is valid or not
   */
  @Override
  public boolean checkWeightageOfStocks(String s) {
    try {
      Double number = Double.parseDouble(s);
      // test the input string on the predicate
      return number > 0 && number <= 100;
    } catch (NumberFormatException e) {
      // if unable to parse again ask for input
      return false;
    }
  }

  /**
   * This takes input from user and parses it as Double and if the amount if more than 0 then it is
   * valid else not. if the parsing fails its not valid.
   *
   * @param s The candidate string which may or may not be a valid amount
   * @return boolean which would convey amount is valid or not
   */
  @Override
  public boolean checkAmount(String s) {
    try {
      Double number = Double.parseDouble(s);
      // test the input string on the predicate
      return number > 0;
    } catch (NumberFormatException e) {
      // if unable to parse again ask for input
      return false;
    }
  }

  /**
   * This takes input from user and check from model if it is a valid stock name or symbol or not.
   *
   * @param s The candidate string which may or may not be a valid stock name
   * @return boolean which would convey stock name is valid or not
   */
  @Override
  public boolean stockNameChecker(String s) {
    return !model.validateStockNameSymbol().test(s);
  }

  /**
   * This takes input from user and parses it as Double and if the commission if more than 0 then it
   * is valid else not. if the parsing fails its not valid.
   *
   * @param s The candidate string which may or may not be a valid commission
   * @return boolean which would convey commission is valid or not
   */
  @Override
  public boolean checkCommission(String s) {
    try {
      Double number = Double.parseDouble(s);
      // test the input string on the predicate
      return number > 0;
    } catch (NumberFormatException e) {
      // if unable to parse again ask for input
      return false;
    }
  }
}
