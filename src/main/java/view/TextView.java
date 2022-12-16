package main.java.view;

import java.io.PrintStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import main.java.model.entities.StockPriceResponseWrapper;
import main.java.model.util.Util;

/**
 * This class represents implementation of the IView interface. The implementation focuses on
 * how to display the inputs processed by Model and passed by Controller to the user.
 */
public class TextView implements IView {
  private final PrintStream out;

  /**
   * Constructs a TextView object which can be used by the controller to give the outputs to
   * display.
   *
   * @param out Printstream object which would used to give an output stream to store and
   *            display output of the project.
   */
  public TextView(PrintStream out) {
    this.out = out;
  }

  /**
   * This method displays the string received from the controller to the user in text format.
   *
   * @param text text string that needs to be displayed in the view.
   */
  @Override
  public void displayText(String text) {
    if (text == null) {
      throw new IllegalArgumentException("Text cannot be null");
    }
    out.println(text);
  }

  private void displayMessageInFormat(String format, String[] message) {
    if (message == null) {
      throw new IllegalArgumentException("Message cannot be null");
    }
    if (format == null) {
      throw new IllegalArgumentException("Format cannot be null");
    }
    out.printf(format, message);
  }

  private void displayTable(String[][] table) {

    boolean leftJustifiedRows = false;
    Map<Integer, Integer> columnLengths = new HashMap<>();
    Arrays.stream(table)
            .forEach(a -> Stream.iterate(0, (i -> i < a.length), (i -> ++i)).forEach(i -> {
              if (columnLengths.get(i) == null) {
                columnLengths.put(i, 0);
              }
              if (columnLengths.get(i) < a[i].length()) {
                columnLengths.put(i, a[i].length());
              }
            }));

    final StringBuilder formatString = new StringBuilder();
    String flag = leftJustifiedRows ? "-" : "";
    columnLengths.entrySet().stream()
            .forEach(e -> formatString.append("| %" + flag + e.getValue() + "s "));
    formatString.append("|\n");

    Stream.iterate(0, (i -> i < table.length), (i -> ++i))
            .forEach(a -> this.displayMessageInFormat(formatString.toString(), table[a]));
    out.println();
  }


  /**
   * This method displays the portfolio table in a formatted output to the user in text format.
   *
   * @param listOfPortfolio List of strings, wherein each string contains a portfolioName
   *                        and the date of creation.
   */
  @Override
  public void createPortfolioTable(List<String> listOfPortfolio) {
    if (listOfPortfolio == null) {
      throw new IllegalArgumentException("List of portfolio cannot be null");
    }
    int portfolioSize = listOfPortfolio.size();
    if (portfolioSize == 0) {
      displayText("There is no list of portfolios.");
      return;
    }
    String[][] table = new String[portfolioSize + 1][4];
    table[0][0] = "id";
    table[0][1] = "Portfolio Name";
    table[0][2] = "Number of Stocks";
    table[0][3] = "Date of Creation";

    for (int index = 0; index < listOfPortfolio.size(); index++) {
      String[] listOfSplittedstrings = listOfPortfolio.get(index).split("\\|");
      table[index + 1][0] = String.valueOf(index + 1);
      table[index + 1][1] = listOfSplittedstrings[0];
      table[index + 1][2] = listOfSplittedstrings[2];
      table[index + 1][3] = listOfSplittedstrings[1];
    }

    displayTable(table);
  }

  /**
   * This method displays the portfolio composition to the user in a text formatted output.
   *
   * @param listOfStocks list of strings, where each string represent the stock information
   *                     like symbol, Quantity, name etc.
   */
  @Override
  public void getPortfolioComposition(List<String> listOfStocks) {
    if (listOfStocks == null) {
      throw new IllegalArgumentException("List of Purchased of stocks cannot be null");
    }

    if (listOfStocks.size() == 0) {
      displayText("No stocks to display in this Portfolio");
      return;
    }
    String[][] table = new String[listOfStocks.size() + 1][4];

    table[0][0] = "id";
    table[0][1] = "Stock Code";
    table[0][2] = "Name of Company";
    table[0][3] = "Number of Shares Brought";

    for (int index = 0; index < listOfStocks.size(); index++) {
      String[] listOfSplittedstrings = listOfStocks.get(index).split("\\|");

      table[index + 1][0] = String.valueOf(index + 1);
      table[index + 1][1] = listOfSplittedstrings[1];
      table[index + 1][2] = listOfSplittedstrings[0];
      table[index + 1][3] = listOfSplittedstrings[2];
    }

    displayTable(table);

  }

  private void getPortfolioCompositionWithPrice(List<String> listOfStocks) {

    String[][] table = new String[listOfStocks.size() + 1][6];

    table[0][0] = "id";
    table[0][1] = "Stock Code";
    table[0][2] = "Name of Company";
    table[0][3] = "Number of Shares Brought";
    table[0][4] = "Price";
    table[0][5] = "Value";

    for (int index = 0; index < listOfStocks.size(); index++) {
      String[] listOfSplittedstrings = listOfStocks.get(index).split("\\|");
      table[index + 1][0] = String.valueOf(index + 1);
      table[index + 1][1] = listOfSplittedstrings[1];
      table[index + 1][2] = listOfSplittedstrings[0];
      table[index + 1][3] = listOfSplittedstrings[2];
      table[index + 1][4] = listOfSplittedstrings[3];
      table[index + 1][5] = String.format("%.2f", Double.parseDouble(listOfSplittedstrings[4]));

    }

    displayTable(table);

  }

  /**
   * This method displays the portfolio value in a text formatted output.
   *
   * @param response  StockPriceResponseWrapper object which internally has successList
   *                  consisting of portfolio stocks and its value at a currentDate...along with
   *                  it failure
   *                  stocks for which price retrieval failed
   * @param inputDate Date for which the portfolio is being displayed.
   */
  @Override
  public void showStockResponseWrapper(StockPriceResponseWrapper response, LocalDate inputDate) {
    List<String> successList = response.getSuccessList();
    List<String> failureList = response.getFailureList();
    Double totalPrice = response.getPortfolioValue();
    if (successList == null) {
      throw new IllegalArgumentException("Success List in response wrapper cannot be null");
    }
    if (failureList == null) {
      throw new IllegalArgumentException("Failure List in response wrapper cannot be null");
    }
    if (inputDate == null) {
      throw new IllegalArgumentException("Input Date cannot be null");
    }
    if (successList.size() == 0 && failureList.size() == 0) {
      displayText("No response found by the application.");
      return;
    }

    if (successList.size() > 0) {
      displayText("We were able to successfully get values of these stocks");
      getPortfolioCompositionWithPrice(successList);
      displayText(String.format("The total value of the portfolio on %s: $%.2f", inputDate,
              totalPrice));
    }
    if (failureList.size() > 0) {
      displayText("");
      displayText("Alas ! We fail to fetch the prices for these stocks");
      getPortfolioComposition(failureList);
    }
  }

  /**
   * This method displays the graph output.
   *
   * <p>
   * It receives three inputs: 1. Scale of the time 2. The normalized price 3. The actual price
   * that
   * needs to be displayed
   * </p>
   * .
   */
  public void displayGraph(List<String> graphOutput) {
    float max_price = 0;
    List<Float> floats = new ArrayList<>();
    for (int index = 0; index < graphOutput.size(); index++) {
      String[] splittedString = graphOutput.get(index).split("\\"
              + Util.FILE_OUTPUT_SEPARATOR);
      out.printf("%s:", splittedString[0]);
      floats.add(Float.parseFloat(splittedString[1]));
      for (int k = 0; k < Integer.parseInt(splittedString[2]); k++) {
        out.print("*");
      }
      displayText(String.format(" [$ " + splittedString[1] + "]"));
    }
    Float max = floats.stream().max(Float::compare).get();
    float scale = max / 50;
    displayText(String.format("Scale: * = %f", scale));
  }
}

