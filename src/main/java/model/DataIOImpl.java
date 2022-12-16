package main.java.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import main.java.model.entities.Stock;
import main.java.model.util.Util;

/**
 * This class represents an implementation of the DataIO Interface.
 * Majority of the methods deals with doing input/output of data from static Json/CSV files.
 */
public class DataIOImpl implements DataIO {
  private static final String PORTFOLIO_FILE_PATH = "res/portfolio.json";
  private static final String MASTER_STOCKS_FILE_PATH = "res/NYSE_ALL_STOCKS.csv";

  /**
   * Reads persisted portfolios from the json file stored at the declared location.
   *
   * @return List of portfolio objects and their stocks.
   */
  @Override
  public List<Portfolio> readPersistedPortfolios() {
    List<FlexiblePortfolio> flexiblePortfolios = new ArrayList<>();
    List<InFlexiblePortfolio> inFlexiblePortfolios = new ArrayList<>();


    try {
      Reader reader = Files.newBufferedReader(Paths.get("res/portfolio.json"));
      JsonArray portfolioArray = Util.IN_GSON.fromJson(reader, JsonArray.class);
      for (JsonElement p : portfolioArray) {
        if (p.getAsJsonObject().get("portfolioType").getAsString().equals("inflexible")) {
          inFlexiblePortfolios.add(Util.IN_GSON.fromJson(p, InFlexiblePortfolio.class));
        } else {
          flexiblePortfolios.add(Util.IN_GSON.fromJson(p, FlexiblePortfolio.class));
        }
      }
    } catch (Exception e) {
      // do nothing...this will just return empty list
    }

    List<Portfolio> finalList = new ArrayList<>();
    finalList.addAll(flexiblePortfolios);
    finalList.addAll(inFlexiblePortfolios);
    return finalList;
  }


  /**
   * Reads stocks from the csv file stored at the declared location.
   *
   * @return List of stocks that are supported in the application.
   */
  public List<Stock> readMasterStockList() {
    List<Stock> masterList = new ArrayList<>();
    Scanner sc = null;
    try {
      sc = new Scanner(new File(MASTER_STOCKS_FILE_PATH));
    } catch (FileNotFoundException e) {
      //  If no file exists return an empty list
      return new ArrayList<>();
    }
    while (sc.hasNext()) {
      String rowText = sc.nextLine();
      masterList.add(new Stock(rowText.split(Util.FILE_SEPARATOR)[1],
              rowText.split(Util.FILE_SEPARATOR)[0]));
    }
    sc.close();
    return masterList;
  }


  /**
   * This method processes the CSV file given by the user and returns  the data to the model.
   *
   * @param filePath filePath of the file that needs to processed.
   * @return List of string each representing a row in the user provided file.
   * @throws RuntimeException 1.If File not found in the given path.
   *                          2.File is empty.
   */
  @Override
  public List<String> readUserGivenFile(String filePath) throws RuntimeException {
    Scanner sc = null;
    try {
      sc = new Scanner(new File(filePath));
    } catch (FileNotFoundException e) {
      throw new RuntimeException("File not found at path" + filePath);
    }
    if (!sc.hasNext()) {
      throw new RuntimeException("File is empty");
    }
    //ignore header
    sc.nextLine();
    List<String> outputList = new ArrayList<>();
    while (sc.hasNext()) {
      outputList.add(sc.nextLine());
    }
    return outputList;
  }

  /**
   * This method allows the application to save the portfolio in a json file.
   *
   * @param portfolios The portfolio that needs be saved
   * @throws IOException 1. The file-writer fails
   */
  @Override
  public void savePortfolios(List<Portfolio> portfolios) throws IOException {
    FileWriter writer = new FileWriter(PORTFOLIO_FILE_PATH);
    Util.OUT_GSON.toJson(portfolios, writer);
    writer.flush();
    writer.close();
  }

}
