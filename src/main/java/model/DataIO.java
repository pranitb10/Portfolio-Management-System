package main.java.model;

import java.io.IOException;
import java.util.List;

import main.java.model.entities.Stock;

/**
 * This interface provides all the data input and output functionality to the Model. It has
 * functions like readPersistedPortfolios, readPriceCache, savingPortfolio etc.
 */
public interface DataIO {
  /**
   * This method reads all the portfolios that are persisted in the application.
   *
   * @return List of Portfolio objects that are persisted in the application.
   */
  List<Portfolio> readPersistedPortfolios();

  /**
   * This method reads all the stocks that are supported by the application.
   *
   * @return List of Stock objects that were read from the master config file.
   */
  List<Stock> readMasterStockList();


  /**
   * This method processes the file given by the user and returns it to the model.
   *
   * @param filePath filePath of the file that needs to processed.
   * @return List of string each representing a row in the user provided file.
   * @throws RuntimeException 1.If File not found in the given path.
   *                          2.File is empty.
   */
  List<String> readUserGivenFile(String filePath) throws RuntimeException;

  /**
   * This method allows the to save the portfolio in a json file.
   *
   * @param portfolios The portfolio that needs be saved
   * @throws IOException 1. The file writer fails
   */
  void savePortfolios(List<Portfolio> portfolios) throws IOException;
}
