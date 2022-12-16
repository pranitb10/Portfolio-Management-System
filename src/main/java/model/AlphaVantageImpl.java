package main.java.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import main.java.model.entities.StockPrice;
import main.java.model.util.Util;

/**
 * This method extends the abstractPriceProvider and is responsible for providing the price of a
 * particular stock on a given date from the AlphaVantageAPI.
 */
public class AlphaVantageImpl extends AbstractPriceProvider {

  private static final String API_KEY = "4UG9PSAUSGKVJJHD";
  private static final String TEMPLATE_URL = "https://www.alphavantage.co/query?function=TIME_"
          + "SERIES_DAILY&symbol=stockSymbolParam&outputsize=full&datatype=csv&apikey=" + API_KEY;

  /**
   * Returns all the historical prices of the given stock using the alphaVantageAPI.
   *
   * @param stockSymbol stock for which prices are required.
   * @return List of StockPrice objects wherein each object represent price information of the
   *         stock for a particular day.
   */
  @Override
  public List<StockPrice> getPricesFromAPI(String stockSymbol) {
    // Hit alpha vantage api
    List<StockPrice> apiPrices = new ArrayList<>();
    String stringUrl = TEMPLATE_URL.replace("stockSymbolParam", stockSymbol);
    URL url = null;
    try {
      url = new URL(stringUrl);
    } catch (MalformedURLException e) {
      throw new RuntimeException("the alphavantage API has either changed or no longer works");
    }
    InputStream in = null;
    StringBuilder output = new StringBuilder();

    try {
      in = url.openStream();
      int b;
      while ((b = in.read()) != -1) {
        output.append((char) b);
      }
    } catch (IOException e) {
      throw new RuntimeException("Exception in fetching data from "
              + "AlphaVantage API for stock" + stockSymbol);
    }
    String[] pricesArr = output.toString().split(System.lineSeparator());
    for (int j = 1; j < pricesArr.length; j++) {
      LocalDate dateFromAPI = Util.getDateFromString(pricesArr[j].split(",")[0]);
      Double priceFromAPI = Double.valueOf(pricesArr[j].split(",")[4]);
      apiPrices.add(new StockPrice(dateFromAPI, priceFromAPI));
    }
    return apiPrices;
  }
}
