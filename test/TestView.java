//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.ByteArrayOutputStream;
//import java.io.PrintStream;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import model.entities.dataIO.StockPriceResponseWrapper;
//import main.java.view.IView;
//import main.java.view.TextView;
//
//import static org.junit.Assert.assertEquals;
//
///**
// * This class is used to test functionality of model.
// */
//public class TestView {
//
//  // Test happy case on entering
//  ByteArrayOutputStream out;
//  IView iView;
//
//  String expected = "";
//
//  @Before
//  public void init() {
//    out = new ByteArrayOutputStream();
//    iView = new TextView(new PrintStream(out));
//  }
//
//  @After
//  public void after() {
//    assertEquals(expected, out.toString());
//  }
//
//  @Test
//  public void test() {
//    iView.showMainMenu();
//    expected = "==================================\n" + "What would you like to do today?\n"
//            + "1.Create a PortFolio\n" + "2.Get Portfolio composition\n" + "3.Get value of "
//            + "Portfolio on a given Date\n" + "4.Load Portfolio\n" + "5.Quit\n";
//    assertEquals(expected, out.toString());
//  }
//
//  @Test
//  public void testDisplayText() {
//    String message = "This is a sample text";
//    expected = message + "\n";
//    iView.displayText(message);
//    assertEquals(expected, out.toString());
//  }
//
//  @Test
//  public void testDisplayEmptyText() {
//    String message = "";
//    expected = message + "\n";
//    iView.displayText(message);
//    assertEquals(expected, out.toString());
//  }
//
//  @Test(expected = IllegalArgumentException.class)
//  public void testDisplayTextNull() {
//    String message = null;
//    iView.displayText(message);
//    assertEquals(expected, out.toString());
//  }
//
//  @Test
//  public void testGetPortfolioCompositionPrice() {
//    expected = "We were able to successfully get values of these stocks\n"
//            + "| id | Stock Code |      Name of Company | Number of Shares Brought |  Price |   "
//            + "Value |\n"
//            + "|  1 |       ABBV |           ABBVIE INC |                       35 | 147.61 |
//            5166"
//            + ".35 |\n"
//            + "|  2 |          W |          WAYFAIR INC |                       65 |  36.21 |
//            2353"
//            + ".65 |\n"
//            + "|  3 |        AGO | ASSURED GUARANTY LTD |                      123 |  58.72 |
//            7222"
//            + ".56 |\n"
//            + "|  4 |        AGO | ASSURED GUARANTY LTD |                      123 |  58.72 |
//            7222"
//            + ".56 |\n"
//            + "\n"
//            + "The total value of the portfolio on 2022-10-30: 21965.12\n"
//            + "\n"
//            + "Alas ! We fail to fetch the prices for these stocks\n"
//            + "| id | Stock Code |      Name of Company | Number of Shares Brought |\n"
//            + "|  1 |       ABBV |           ABBVIE INC |                       35 |\n"
//            + "|  2 |          W |          WAYFAIR INC |                       65 |\n"
//            + "|  3 |        AGO | ASSURED GUARANTY LTD |                      123 |\n"
//            + "|  4 |        AGO | ASSURED GUARANTY LTD |                      123 |\n"
//            + "\n";
//
//    List<String> successList = Arrays.asList("ABBVIE INC|ABBV|35|147.61|5166.35", "WAYFAIR "
//                    + "INC|W|65|36" + ".21|2353.65", "ASSURED GUARANTY LTD|AGO|123|58.72|7222.56",
//            "ASSURED GUARANTY " + "LTD|AGO|123|58.72|7222.56");
//    List<String> failureList = Arrays.asList("ABBVIE INC|ABBV|35", "WAYFAIR INC|W|65", "ASSURED "
//            + "GUARANTY" + " LTD|AGO|123", "ASSURED " + "GUARANTY" + " " + "LTD|AGO|123");
//    iView.showStockResponseWrapper(new StockPriceResponseWrapper(successList, failureList),
//            LocalDate.parse("2022-10-30"));
//    assertEquals(expected, out.toString());
//  }
//
//
//  @Test
//  public void testCreatePortfolio() {
//    expected = "| id |       Portfolio Name | Number of Stocks | Date of Creation |\n"
//            + "|  1 |                    e |                1 |       2022-10-31 |\n"
//            + "|  2 |     loaded portfolio |                4 |       2022-10-31 |\n"
//            + "|  3 |                 name |                4 |       2022-10-31 |\n"
//            + "|  4 |                temp2 |                1 |       2022-10-31 |\n"
//            + "|  5 |                    p |                1 |       2022-10-31 |\n"
//            + "|  6 | new loaded portfolio |                4 |       2022-10-31 |\n"
//            + "\n";
//    List<String> portfolios = Arrays.asList("e|2022-10-31|1", "loaded portfolio|2022-10-31|4",
//            "name|2022-10-31|4", "temp2|2022-10-31|1", "p|2022-10-31|1", "new loaded "
//                    + "portfolio|2022-10-31|4");
//    iView.createPortfolioTable(portfolios);
//    assertEquals(expected, out.toString());
//  }
//
//  @Test(expected = IllegalArgumentException.class)
//  public void testCreatePortfolioNull() {
//    iView.createPortfolioTable(null);
//  }
//
//  @Test
//  public void testCreatePortfolioEmpty() {
//    expected = "There is no list of portfolios.\n";
//    List<String> listOfPortfolio = new ArrayList<>();
//    iView.createPortfolioTable(listOfPortfolio);
//    assertEquals(expected, out.toString());
//  }
//
//
//  @Test(expected = IllegalArgumentException.class)
//  public void testShowStockResponseWrapperSuccessListNull() {
//    List<String> failureList = Arrays.asList("ABBVIE INC|ABBV|35", "WAYFAIR INC|W|65", "ASSURED "
//            + "GUARANTY" + " LTD|AGO|123", "ASSURED " + "GUARANTY" + " " + "LTD|AGO|123");
//
//
//    StockPriceResponseWrapper successResponse = new StockPriceResponseWrapper(null, failureList);
//    iView.showStockResponseWrapper(successResponse, LocalDate.parse("2022-09-18"));
//    assertEquals(expected, out.toString());
//  }
//
//  @Test(expected = IllegalArgumentException.class)
//  public void testShowStockResponseWrapperFailureListNull() {
//
//    List<String> successList = Arrays.asList("ABBVIE INC|ABBV|35|147.61|5166.35", "WAYFAIR "
//                    + "INC|W|65|36" + ".21|2353.65", "ASSURED GUARANTY LTD|AGO|123|58.72|7222.56",
//            "ASSURED GUARANTY " + "LTD|AGO|123|58.72|7222.56");
//
//
//    StockPriceResponseWrapper successResponse = new StockPriceResponseWrapper(successList, null);
//    iView.showStockResponseWrapper(successResponse, LocalDate.parse("2022-09-18"));
//    assertEquals(expected, out.toString());
//  }
//
//  @Test(expected = IllegalArgumentException.class)
//  public void testShowStockResponseWrapperDateNull() {
//
//    List<String> successList = Arrays.asList("ABBVIE INC|ABBV|35|147.61|5166.35", "WAYFAIR "
//            + "INC|W|65|36"
//            + ".21|2353.65", "ASSURED GUARANTY LTD|AGO|123|58.72|7222.56", "ASSURED GUARANTY "
//            + "LTD|AGO|123|58.72|7222.56");
//    List<String> failureList = Arrays.asList("ABBVIE INC|ABBV|35", "WAYFAIR INC|W|65",
//            "ASSURED GUARANTY"
//                    + " LTD|AGO|123",
//            "ASSURED "
//                    + "GUARANTY"
//                    + " "
//                    + "LTD|AGO|123");
//
//
//    StockPriceResponseWrapper successResponse = new StockPriceResponseWrapper(successList,
//            failureList);
//    iView.showStockResponseWrapper(successResponse, null);
//    assertEquals(expected, out.toString());
//  }
//
//  @Test
//  public void testShowStockResponseWrapperSuccessListIsEmpty() {
//    expected = "\n"
//            + "Alas ! We fail to fetch the prices for these stocks\n"
//            + "| id | Stock Code |      Name of Company | Number of Shares Brought |\n"
//            + "|  1 |       ABBV |           ABBVIE INC |                       35 |\n"
//            + "|  2 |          W |          WAYFAIR INC |                       65 |\n"
//            + "|  3 |        AGO | ASSURED GUARANTY LTD |                      123 |\n"
//            + "|  4 |        AGO | ASSURED GUARANTY LTD |                      123 |\n"
//            + "\n";
//    List<String> successList = new ArrayList<>();
//    List<String> failureList = Arrays.asList("ABBVIE INC|ABBV|35", "WAYFAIR INC|W|65",
//            "ASSURED GUARANTY"
//                    + " LTD|AGO|123",
//            "ASSURED "
//                    + "GUARANTY"
//                    + " "
//                    + "LTD|AGO|123");
//
//
//    StockPriceResponseWrapper successResponse = new StockPriceResponseWrapper(successList,
//            failureList);
//    iView.showStockResponseWrapper(successResponse, LocalDate.parse("2022-09-18"));
//    assertEquals(expected, out.toString());
//  }
//
//  @Test
//  public void testShowStockResponseWrapperFailureListIsEmpty() {
//    expected = "We were able to successfully get values of these stocks\n"
//            + "| id | Stock Code |      Name of Company | Number of Shares Brought |  Price |   "
//            + "Value |\n"
//            + "|  1 |       ABBV |           ABBVIE INC |                       35 | 147.61 |
//            5166"
//            + ".35 |\n"
//            + "|  2 |          W |          WAYFAIR INC |                       65 |  36.21 |
//            2353"
//            + ".65 |\n"
//            + "|  3 |        AGO | ASSURED GUARANTY LTD |                      123 |  58.72 |
//            7222"
//            + ".56 |\n"
//            + "|  4 |        AGO | ASSURED GUARANTY LTD |                      123 |  58.72 |
//            7222"
//            + ".56 |\n"
//            + "\n"
//            + "The total value of the portfolio on 2022-09-18: 21965.12\n";
//
//    List<String> successList = Arrays.asList("ABBVIE INC|ABBV|35|147.61|5166.35", "WAYFAIR "
//                    + "INC|W|65|36" + ".21|2353.65", "ASSURED GUARANTY LTD|AGO|123|58.72|7222.56",
//            "ASSURED GUARANTY " + "LTD|AGO|123|58.72|7222.56");
//    List<String> failureList = new ArrayList<>();
//
//
//    StockPriceResponseWrapper successResponse = new StockPriceResponseWrapper(successList,
//            failureList);
//    iView.showStockResponseWrapper(successResponse, LocalDate.parse("2022-09-18"));
//    assertEquals(expected, out.toString());
//  }
//
//}