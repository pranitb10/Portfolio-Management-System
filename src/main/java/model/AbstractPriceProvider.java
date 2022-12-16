package main.java.model;

import com.google.gson.reflect.TypeToken;

import java.io.FileWriter;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import main.java.model.entities.StockPrice;
import main.java.model.util.Util;

/**
 * Abstract Price Provider that implements the PriceProvider interface.
 *
 * <p>It has an abstract method getPricesFromAPI which has to be compulsorily implemented by the
 * child classes extending it.</p>
 * <p>This AbstractClass handles the caching logic, it holds the historical prices of the
 * stocks in a cache, it first checks the price in the cache if not than only calls the
 * getPricesFromAPI method from the child class. Once prices are retrieved it saves it to the
 * cache.</p>
 */
public abstract class AbstractPriceProvider implements PriceProvider {
  private final static String CACHE_PATH = "res/PRICE_CACHE.json";
  protected Map<String, TreeMap<LocalDate, Double>> priceCache;

  /**
   * Initialize the priceProvider, by reading the cache from the PRICE_CACHE.json and loading it in
   * the memory in a HashMap.
   *
   * <p>
   * Before hitting the API it checks the cache, and checks whether the hit is even necessary.
   * </p>
   */
  protected AbstractPriceProvider() {
    this.priceCache = readPriceCache();
  }

  /**
   * A method that gives historical prices of the given stock.
   *
   * @param stockSymbol stock for which prices are required.
   * @return List of StockPrice objects wherein each object represent price information of the stock
   *         for a particular day.
   */
  protected abstract List<StockPrice> getPricesFromAPI(String stockSymbol);

  private void saveCache() {
    try {
      FileWriter writer = new FileWriter(CACHE_PATH);
      Util.OUT_GSON.toJson(priceCache, writer);
      writer.flush();
      writer.close();
    } catch (Exception e) {
      // do nothing if cache update fails
    }
  }

  private Map<String, TreeMap<LocalDate, Double>> readPriceCache() {
    Map<String, TreeMap<LocalDate, Double>> outputMap = new HashMap<>();
    try {
      Reader reader = Files.newBufferedReader(Paths.get("res/PRICE_CACHE.json"));
      Type cacheMapType = new TypeToken<Map<String, TreeMap<LocalDate, Double>>>() {
      }.getType();
      outputMap = Util.IN_GSON.fromJson(reader, cacheMapType);
      reader.close();
    } catch (Exception e) {
      // in case of error return empty cache
      return outputMap;
    }
    return outputMap;
  }

  /**
   * This method gives the price of the stock on the given date. It first checks in the cache if
   * price is not found than calls the child class method getPricesFromAPI to get the prices.
   *
   * <p>
   * For a given stock it queries the date until the program is executed and the price will be
   * cached so that for subsequent dates the cache is called without utilizing the API hit. It
   * internally has 3 retries to ensure teh call does not fail bcz of some transient failure from
   * the API side.
   * </p>
   *
   * @param stockSymbol The stock symbol which the price needs to fetched
   * @param date        the date on which price needs to fetched
   * @return The price of the stock on a given date
   * @throws RuntimeException 1. If the price is not found for the date 2. If price is unable to be
   *                          fetched due to some API failure from the child class.
   */
  public Double getPriceOfStock(String stockSymbol, LocalDate date) throws RuntimeException {
    //check in the cache
    // floor is used to get the price of last available date(to handle scenarios like public
    // holidays)
    if (priceCache.containsKey(stockSymbol)
            && priceCache.get(stockSymbol).ceilingKey(date) != null) {
      return priceCache.get(stockSymbol).ceilingEntry(date).getValue();
    } else {
      // call specific class api implementations methods....with 2 retries if api fails
      for (int i = 0; i < 3; i++) {
        try {
          Double priceOfStock = null;
          List<StockPrice> prices = getPricesFromAPI(stockSymbol);
          // add stock key to the cacheMap...if it doesn't exist
          if (!priceCache.containsKey(stockSymbol)) {
            priceCache.put(stockSymbol, new TreeMap<>());
          }

          // update price cache...also assign to variable priceOfStock if date matches
          for (int j = 0; j < prices.size(); j++) {
            LocalDate dateFromAPI = prices.get(j).getDate();
            Double priceFromAPI = prices.get(j).getPrice();
            if (dateFromAPI.equals(date)) {
              priceOfStock = priceFromAPI;
            }
            // break from whichever date price already exists in the cache
            if (priceCache.get(stockSymbol).containsKey(dateFromAPI)) {
              break;
            }
            priceCache.get(stockSymbol).put(dateFromAPI, priceFromAPI);
          }
          try {
            saveCache();
          } catch (Exception e) {
            //do nothing if unable to save cache
          }

          if (priceOfStock == null) {
            Double checkFloorPrice = priceCache.get(stockSymbol).ceilingEntry(date).getValue();
            if (checkFloorPrice == null) {
              throw new RuntimeException("Price not found for the date");
            }
            priceOfStock = checkFloorPrice;
          }
          return priceOfStock;
        } catch (Exception e) {
          continue;
        }
      }
    }
    throw new RuntimeException("Unable to fetch Price for the given stock and date");
  }

}
