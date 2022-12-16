package main.java.view;

import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

/**
 * This class represents the drawing of graph on a panel which is used by the GraphDisplayView and
 * based on the intervals and values it plots the graph.
 */
class BarChartGraphics extends JPanel {

  public static final int TOP_BUFFER = 35;
  public static final int AXIS_PAD = 100;
  private final List<String> data;
  private final Map<String, String> axisMapping = new LinkedHashMap<>();
  private final String xLabel;
  private final String yLabel;
  private int charWidth;
  private int chartheight;
  private int chartX;
  private int chartY;

  /**
   * Constructs a BarChartGraphics object which can takes the data, xlabel and ylabel to construct a
   * graph on the main panel.
   *
   * @param data List of strings divided by '|' which would contain time period separated by
   *             the value
   * @param xl   The label of x-axis
   * @param yl   The label of y-axis
   */
  public BarChartGraphics(List<String> data, String xl, String yl) {
    super();
    this.data = data;

    xLabel = xl;
    yLabel = yl;

  }

  private void setupCounts() {
    axisMapping.clear();

    for (String dataString : data) {
      String[] lineSplitted = dataString.split("\\|");
      axisMapping.put(lineSplitted[0], lineSplitted[1]);
    }
  }

  /**
   * This method initializes the chart with axes, text and graphics object.
   *
   * @param g the <code>Graphics</code> object to protect
   */
  public void paintComponent(Graphics g) {

    setupCounts();
    computeSize();

    Graphics2D g2 = (Graphics2D) g;
    drawBars(g2);
    drawAxes(g2);
    drawText(g2);
  }

  private void computeSize() {

    int width = this.getWidth();
    int height = this.getHeight();

    // chart area size
    charWidth = width - 2 * AXIS_PAD;
    chartheight = height - 4 * AXIS_PAD - TOP_BUFFER;

    // Chart origin coords
    chartX = AXIS_PAD;
    chartY = height - 120 - AXIS_PAD;

  }

  /**
   * This method creates a bar graph with given data. It creates the labels with affine
   * transformation.
   *
   * @param g2 2D Graphics object
   */
  public void drawBars(Graphics2D g2) {

    Color original = g2.getColor();

    double numBars = axisMapping.keySet().size();
    double max = 0.;

    for (String portfolioValueString : axisMapping.values()) {
      if (max < Double.parseDouble(portfolioValueString)) {
        max = Double.parseDouble(portfolioValueString);
      }
    }
    int barWidth = (int) (charWidth / numBars);

    int height;
    int xLeft;
    int yTopLeft;
    double valueDouble;
    int counter = 0;
    for (String label : axisMapping.keySet()) {
      valueDouble = Double.parseDouble(axisMapping.get(label));
      double height2 = (valueDouble / max) * chartheight;
      height = (int) height2;

      xLeft = AXIS_PAD + counter * barWidth;
      yTopLeft = chartY - height;
      Rectangle rec = new Rectangle(xLeft, yTopLeft, barWidth, height);

      g2.setColor(Color.BLUE);
      //g2.draw(rec);
      g2.fill(rec);
      g2.setColor(Color.BLACK);
      Font originalFont = g2.getFont();

      Font font = new Font(null, originalFont.getStyle(), originalFont.getSize() - 5);
      AffineTransform affineTransform = new AffineTransform();
      affineTransform.rotate(Math.toRadians(90), 0, 0);
      Font rotatedFontClockwise = font.deriveFont(affineTransform);
      affineTransform.rotate(Math.toRadians(-180), 0, 0);
      Font rotatedFontAntiClockwise = font.deriveFont(affineTransform);

      g2.setFont(rotatedFontClockwise);
      g2.drawString(label, xLeft + barWidth / 2, chartY + 13);

      g2.setFont(rotatedFontAntiClockwise);
      g2.drawString(String.format("$" + axisMapping.get(label)), xLeft + barWidth / 2,
              yTopLeft - 2);
      g2.setFont(originalFont);

      g2.drawLine(xLeft, yTopLeft, xLeft, yTopLeft + height);
      g2.drawLine(xLeft, yTopLeft + height, xLeft + barWidth, yTopLeft + height);
      g2.drawLine(xLeft + barWidth, yTopLeft + height, xLeft + barWidth, yTopLeft);
      g2.drawLine(xLeft + barWidth, yTopLeft, xLeft, yTopLeft);
      counter++;
    }

    g2.setColor(original);
  }

  /**
   * This would allow us to dynamically set the size of the bar chart.
   *
   * @return Dimension object which sets the size
   */
  @Override
  public Dimension getPreferredSize() {
    return new Dimension(data.size() * 100 + 2, 1000);
  }

  private void drawAxes(Graphics2D g2) {


    int rightX = chartX + charWidth;
    int topY = chartY - chartheight;

    g2.drawLine(chartX, chartY, rightX, chartY);

    g2.drawLine(chartX, chartY, chartX, topY);

    g2.drawString(xLabel, chartX + charWidth, chartY + (AXIS_PAD / 2) - 5);

    // draw vertical string

    Font original = g2.getFont();

    Font font = new Font(null, original.getStyle(), original.getSize());
    AffineTransform affineTransform = new AffineTransform();
    affineTransform.rotate(Math.toRadians(-90), 0, 0);
    Font rotatedFont = font.deriveFont(affineTransform);
    g2.setFont(rotatedFont);
    g2.drawString(yLabel, AXIS_PAD / 2 + 3, chartY - chartheight / 2);
    g2.setFont(original);


  }

  private void drawText(Graphics2D g2) {


    g2.drawString("X-Axis Time Intervals: Dynamically alloted in Days, Weeks, Months, Quarters,"
            + "Years", AXIS_PAD + 10, 15);
    g2.drawString("Y-Axis Portfolio Value: Unit 1$ USD ", AXIS_PAD + 10, 30);

  }
}