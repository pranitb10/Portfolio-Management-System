package main.java.model.entities;


import main.java.model.util.Util;

/**
 * A wrapper response class that is used for communication between model and the controller to
 * display the graph. It internally has, the scale on which the graph is displayed(weekly,monthly
 * etc.), the number of bars in the graph, and the actual price on the given unit.
 */
public class GraphResponseWrapper {

  private final String dateScale;
  private final Float price;

  /**
   * Constructs object of the wrapper response object.
   *
   * @param dateScale scale of the data to be displayed on graph(weekly,monthly, etc.).
   * @param price     The price of the portfolio on the scaled date.
   */
  public GraphResponseWrapper(String dateScale, Float price) {
    this.dateScale = dateScale;
    this.price = price;
  }

  /**
   * Returns Scale of the grouped dates .
   *
   * @return String value of the scaled date.
   */
  public String getDateScale() {
    return this.dateScale;
  }

  /**
   * Returns the price of the portfolio on the given interval.
   *
   * @return The price of portfolio on given interval.
   */
  public Float getPrice() {
    return this.price;
  }

  /**
   * Returns the normalized value to represent in the graph. Since there is a cap of 50 stars to
   * be showed on graph, the value should be normalized to have a cap of 50.
   *
   * @param max the max price
   * @return The normalised price that needs to be displayed on the graph.
   */
  public Integer getNormalizedPrice(Float max) {
    Float scaled = (this.price * 50) / (max);
    return scaled.intValue();
  }

  /**
   * Returns the information of the graph in a formatted output.
   *
   * @param max The maximum price from the list of price
   * @return string of the max price.
   */
  public String getString(Float max) {
    return dateScale + Util.FILE_OUTPUT_SEPARATOR + price + Util.FILE_OUTPUT_SEPARATOR
            + this.getNormalizedPrice(max);
  }
}
