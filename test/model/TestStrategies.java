package model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NavigableSet;
import java.util.Random;
import java.util.TreeSet;

import main.java.model.AlphaVantageImpl;
import main.java.model.DataIO;
import main.java.model.DataIOImpl;
import main.java.model.FlexiblePortfolio;
import main.java.model.IModel;
import main.java.model.InFlexiblePortfolio;
import main.java.model.Model;
import main.java.model.Portfolio;
import main.java.model.PriceProvider;
import main.java.model.entities.PurchasedStock;
import main.java.model.entities.Stock;
import main.java.model.entities.StockTransaction;
import main.java.model.entities.StockWeight;
import main.java.model.entities.Strategy;
import main.java.model.util.Util;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;

/**
 * This is a class used to test all the functionality related to strategy execution.
 */
public class TestStrategies {

  private DataIO mockDataIO;
  private PriceProvider mockPriceProvider;

  private IModel iModel;

  @Before
  public void setUp() throws Exception {
    TreeSet<StockTransaction> inflexibleSet = new TreeSet<>();
    inflexibleSet.add(new StockTransaction(LocalDate.parse("2022-11-01"),
            10d, 5.0d, Util.TransactionType.BUY));
    inflexibleSet.add(new StockTransaction(LocalDate.parse("2022-11-10"),
            110d, 5.0d, Util.TransactionType.BUY));

    TreeSet<StockTransaction> flexibleSet = new TreeSet<>();
    flexibleSet.add(new StockTransaction(LocalDate.parse("2021-11-01"),
            20d, 5.0d, Util.TransactionType.BUY));
    flexibleSet.add(new StockTransaction(LocalDate.parse("2021-11-05"),
            50d, 10.0d, Util.TransactionType.BUY));
    flexibleSet.add(new StockTransaction(LocalDate.parse("2021-11-15"),
            0d, 15.0d, Util.TransactionType.SELL));

    mockDataIO = Mockito.mock(DataIOImpl.class);
    Mockito.when(mockDataIO.readMasterStockList()).thenReturn(Arrays.asList(
            new Stock("APPLE INC", "AAPL"),
            new Stock("MICROSOFT CORP", "MSFT"),
            new Stock("ALPHABET INC", "GOOGL"),
            new Stock("AGILENT TECHNOLOGIES INC", "A")));
    List<Portfolio> portfolioList = new ArrayList<>(Arrays.asList(
            new InFlexiblePortfolio("inflexible", LocalDate.parse("2022-11-15"),
                    Arrays.asList(
                            new PurchasedStock("APPLE INC", "AAPL", inflexibleSet),
                            new PurchasedStock("MICROSOFT CORP", "MSFT",
                                    new TreeSet<>(
                                            List.of(new StockTransaction(
                                                    Util.getDateFromString(
                                                            "2022-10-10"),
                                                    1000d, 5d,
                                                    Util.TransactionType.BUY)))),
                            new PurchasedStock("ALPHABET INC", "GOOGL",
                                    (NavigableSet<StockTransaction>)
                                            inflexibleSet.clone()))
            ),
            new FlexiblePortfolio("flexible", LocalDate.parse("2022-11-15"),
                    Arrays.asList(
                            new PurchasedStock("APPLE INC", "AAPL", flexibleSet),
                            new PurchasedStock("MICROSOFT CORP", "MSFT",
                                    (NavigableSet<StockTransaction>)
                                            flexibleSet.clone()),
                            new PurchasedStock("ALPHABET INC", "GOOGL",
                                    new TreeSet<>(
                                            List.of(new StockTransaction(
                                                            Util.
                                                                    getDateFromString(
                                                                            "2021-11-16"),
                                                            1000d, 5d,
                                                            Util.TransactionType.BUY),
                                                    new StockTransaction(
                                                            Util.
                                                                    getDateFromString(
                                                                            "2021-11-20"),
                                                            500d, 100d,
                                                            Util.TransactionType.SELL)))))
            )));
    Mockito.when(mockDataIO.readPersistedPortfolios()).thenReturn(portfolioList);

    mockPriceProvider = Mockito.mock(AlphaVantageImpl.class);
    iModel = new Model(mockDataIO, mockPriceProvider);
    Random r = new Random();
    double random = 0 + r.nextDouble() * (-100);
    Mockito.when(mockPriceProvider.getPriceOfStock(anyString(), any(LocalDate.class)))
            .thenReturn(10.0d);
  }

  @Test
  public void testIllegalArgsOnExecuteStrategy() {
    // invalid portfolioIndexes -ve
    try {
      iModel.executeStrategy(-1, new ArrayList<>(), 0d, null, null, 0d, 0);
      fail();
    } catch (IllegalArgumentException e) {
      // test passed
    }
    // invalid portfolioIndexes large
    try {
      iModel.executeStrategy(5, new ArrayList<>(), 0d, null, null, 0d, 0);
      fail();
    } catch (IllegalArgumentException e) {
      // test passed
    }

    // invalid stock symbols
    try {
      List<String> stocks = List.of("APPLE|10", "GOOGL|20");
      iModel.executeStrategy(2, stocks, 0d, null, null, 0d, 0);

      fail();
    } catch (IllegalArgumentException e) {
      // test passed
    }

    // invalid investmentAmount -ve
    try {
      List<String> stocks = List.of("AAPL|50", "GOOGL|50");
      iModel.executeStrategy(2, stocks, -10d, null, null, 0d, 0);

      fail();
    } catch (IllegalArgumentException e) {
      // test passed
    }

    // invalid investmentAmount 0
    try {
      List<String> stocks = List.of("AAPL|50", "GOOGL|50");
      iModel.executeStrategy(2, stocks, 0d, null, null, 0d, 0);
      fail();
    } catch (IllegalArgumentException e) {
      // test passed
    }

    // investment amount smaller than commission paid
    try {
      List<String> stocks = List.of("AAPL|50", "GOOGL|50");
      iModel.executeStrategy(2, stocks, 15d, null, null, 10d, 0);

      fail();
    } catch (IllegalArgumentException e) {
      // test passed
    }

    // invalid weights -ve
    try {
      List<String> stocks = List.of("AAPL|-10", "GOOGL|20");
      iModel.executeStrategy(2, stocks, 15d, null, null, 10d, 0);

      fail();
    } catch (IllegalArgumentException e) {
      // test passed
    }

    // invalid weights - not adding to 100
    try {
      List<String> stocks = List.of("AAPL|10", "GOOGL|20");
      iModel.executeStrategy(2, stocks, 15d, null, null, 10d, 0);

      fail();
    } catch (IllegalArgumentException e) {
      // test passed
    }

    // invalid weights - not a number
    try {
      List<String> stocks = List.of("AAPL|1dr0", "GOOGL|2d0");
      iModel.executeStrategy(2, stocks, 15d, null, null, 10d, 0);

      fail();
    } catch (IllegalArgumentException e) {
      // test passed
    }

    //start greater than end
    try {
      List<String> stocks = List.of("AAPL|50", "GOOGL|50");
      iModel.executeStrategy(2, stocks, 150d, Util.getDateFromString("2022-11-12"),
              Util.getDateFromString("2022-11-10"), 10d, 0);

      fail();
    } catch (IllegalArgumentException e) {
      // test passed
    }

    //start date null
    try {
      List<String> stocks = List.of("AAPL|50", "GOOGL|50");
      iModel.executeStrategy(2, stocks, 150d, null,
              Util.getDateFromString("2022-11-10"), 10d, 0);

      fail();
    } catch (IllegalArgumentException e) {
      // test passed
    }

    //end date null
    try {
      List<String> stocks = List.of("AAPL|50", "GOOGL|50");
      iModel.executeStrategy(2, stocks, 150d, Util.getDateFromString("2022-11-10"),
              null, 10d, 0);

      fail();
    } catch (IllegalArgumentException e) {
      // test passed
    }


    //negative commission
    try {
      List<String> stocks = List.of("AAPL|50", "GOOGL|50");
      iModel.executeStrategy(2, stocks, 150d, Util.getDateFromString("2022-11-10"),
              Util.getDateFromString("2022-11-10"), -10d, 0);

      fail();
    } catch (IllegalArgumentException e) {
      // test passed
    }

    //negative frequency
    try {
      List<String> stocks = List.of("AAPL|50", "GOOGL|50");
      iModel.executeStrategy(2, stocks, 150d, Util.getDateFromString("2022-11-10"),
              Util.getDateFromString("2022-11-10"), 10d, -1);

      fail();
    } catch (IllegalArgumentException e) {
      // test passed
    }
  }


  @Test
  public void testRecurringStrategyEndBeforeToday() {
    List<String> stocks = List.of("AAPL|25", "MSFT|25", "GOOGL|50");
    String res = iModel.executeStrategy(2, stocks, 130d,
            Util.getDateFromString("2022-11-22"), Util.getDateFromString("2022-11-25"), 10d, 1);
    //comp
    Assert.assertEquals("Strategy successfully executed till 2022-11-25", res);
    Assert.assertEquals("[APPLE INC|AAPL|0.0, MICROSOFT CORP|MSFT|0.0, ALPHABET INC|GOOGL|500.0]"
            , iModel.getPortfolioComposition(2, Util.getDateFromString("2022-11-21")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|2.5, MICROSOFT CORP|MSFT|2.5, ALPHABET INC|GOOGL|505.0]"
            , iModel.getPortfolioComposition(2, Util.getDateFromString("2022-11-22")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|5.0, MICROSOFT CORP|MSFT|5.0, ALPHABET INC|GOOGL|510.0]"
            , iModel.getPortfolioComposition(2, Util.getDateFromString("2022-11-23")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|7.5, MICROSOFT CORP|MSFT|7.5, ALPHABET INC|GOOGL|515.0]"
            , iModel.getPortfolioComposition(2, Util.getDateFromString("2022-11-24")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|10.0, MICROSOFT CORP|MSFT|10.0, ALPHABET INC|GOOGL|520.0]"
            , iModel.getPortfolioComposition(2, Util.getDateFromString("2022-11-25")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|10.0, MICROSOFT CORP|MSFT|10.0, ALPHABET INC|GOOGL|520.0]"
            , iModel.getPortfolioComposition(2, Util.getDateFromString("2022-11-26")).toString());

    //cost basis
    Assert.assertEquals(11165.0, iModel.getCostBasis(2, Util.getDateFromString("2022-11-21")),
            0.01);
    Assert.assertEquals(11295.0, iModel.getCostBasis(2, Util.getDateFromString("2022-11-22")),
            0.01);
    Assert.assertEquals(11425.0, iModel.getCostBasis(2, Util.getDateFromString("2022-11-23")),
            0.01);
    Assert.assertEquals(11555.0, iModel.getCostBasis(2, Util.getDateFromString("2022-11-24")),
            0.01);
    Assert.assertEquals(11685.0, iModel.getCostBasis(2, Util.getDateFromString("2022-11-25")),
            0.01);
    Assert.assertEquals(11685.0, iModel.getCostBasis(2, Util.getDateFromString("2022-11-26")),
            0.01);

    // portfolio Value
    Assert.assertEquals(5000.0, iModel.getPortfolioValueOnDate(2, Util.getDateFromString("2022"
            + "-11-21")).getPortfolioValue(), 0.01);
    Assert.assertEquals(5100.0, iModel.getPortfolioValueOnDate(2, Util.getDateFromString("2022"
            + "-11-22")).getPortfolioValue(), 0.01);
    Assert.assertEquals(5200.0, iModel.getPortfolioValueOnDate(2, Util.getDateFromString("2022"
            + "-11-23")).getPortfolioValue(), 0.01);
    Assert.assertEquals(5300.0, iModel.getPortfolioValueOnDate(2, Util.getDateFromString("2022"
            + "-11-24")).getPortfolioValue(), 0.01);
    Assert.assertEquals(5400.0, iModel.getPortfolioValueOnDate(2, Util.getDateFromString("2022"
            + "-11-25")).getPortfolioValue(), 0.01);
    Assert.assertEquals(5400.0, iModel.getPortfolioValueOnDate(2, Util.getDateFromString("2022"
            + "-11-26")).getPortfolioValue(), 0.01);

  }

  @Test
  public void testRecurringStrategyEndAfterToday() {
    //considering 27th as current date transaction should happen only until 27th
    Portfolio mockPortfolio = Mockito.spy(new FlexiblePortfolio("", null, new ArrayList<>()));

    doCallRealMethod().doCallRealMethod().doThrow(new RuntimeException()).when(mockPortfolio)
            .executeStrategy(any()
                    , any(), any(), any(), any());
    List<Portfolio> s = new ArrayList<>(Arrays.asList(mockPortfolio));
    Mockito.when(mockDataIO.readPersistedPortfolios()).thenReturn(s);
    iModel = new Model(mockDataIO, mockPriceProvider);
    List<String> stocks = List.of("AAPL|25", "MSFT|25", "GOOGL|50");
    String res = iModel.executeStrategy(1, stocks, 130d,
            Util.getDateFromString("2022-11-26"), Util.getDateFromString("2022-11-30"), 10d, 1);

    Assert.assertEquals("Strategy successfully executed till 2022-11-27", res);
    Assert.assertEquals("[APPLE INC|AAPL|NA, MICROSOFT CORP|MSFT|NA, ALPHABET INC|GOOGL|NA]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-25")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|2.5, MICROSOFT CORP|MSFT|2.5, ALPHABET INC|GOOGL|5.0]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-26")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|5.0, MICROSOFT CORP|MSFT|5.0, ALPHABET INC|GOOGL|10.0]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-27")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|5.0, MICROSOFT CORP|MSFT|5.0, ALPHABET INC|GOOGL|10.0]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-28")).toString());

    //cost basis
    Assert.assertEquals(0.0, iModel.getCostBasis(1, Util.getDateFromString("2022-11-25")), 0.01);
    Assert.assertEquals(130.0, iModel.getCostBasis(1, Util.getDateFromString("2022-11-26")), 0.01);
    Assert.assertEquals(260.0, iModel.getCostBasis(1, Util.getDateFromString("2022-11-27")), 0.01);
    Assert.assertEquals(260.0, iModel.getCostBasis(1, Util.getDateFromString("2022-11-28")), 0.01);

    //portfolio value
    Assert.assertEquals(0.0, iModel.getPortfolioValueOnDate(1,
            Util.getDateFromString("2022-11-25")).getPortfolioValue(), 0.01);
    Assert.assertEquals(100.0, iModel.getPortfolioValueOnDate(1, Util.getDateFromString("2022-11"
            + "-26")).getPortfolioValue(), 0.01);
    Assert.assertEquals(200.0, iModel.getPortfolioValueOnDate(1, Util.getDateFromString("2022-11"
            + "-27")).getPortfolioValue(), 0.01);
    Assert.assertEquals(200.0, iModel.getPortfolioValueOnDate(1, Util.getDateFromString("2022-11"
            + "-28")).getPortfolioValue(), 0.01);

  }

  @Test
  public void testRecurringStrategyStartAndEndAfterToday() {
    List<String> stocks = List.of("AAPL|25", "MSFT|25", "GOOGL|50");
    String res = iModel.executeStrategy(2, stocks, 130d,
            Util.getDateFromString("2030-11-26"), Util.getDateFromString("2030-11-30"), 10d, 1);

    Assert.assertEquals("No transactions were executed,your strategy is saved and will "
            + "be executed when the appropriate day arrives.", res);
    Assert.assertEquals(
            "[APPLE INC|AAPL|0.0, MICROSOFT CORP|MSFT|0.0, ALPHABET INC|GOOGL|500.0]"
            , iModel.getPortfolioComposition(2, Util.getDateFromString("2022-11-25")).toString());

    Assert.assertEquals(11165.0, iModel.getCostBasis(2, Util.getDateFromString("2022-11-25")),
            0.01);
  }

  @Test
  public void testRecurringStrategyFailureBecauseOfPublicHoliday() {
    // no price found because of holiday
    Mockito.when(mockPriceProvider.getPriceOfStock(any(), any())).thenThrow(new RuntimeException());

    List<String> stocks = List.of("AAPL|25", "MSFT|25", "GOOGL|50");
    String res = iModel.executeStrategy(2, stocks, 130d,
            Util.getDateFromString("2022-11-26"), Util.getDateFromString("2022-11-30"), 10d, 1);

    Assert.assertEquals("No transactions were executed,your strategy is saved and will "
            + "be executed when the appropriate day arrives.", res);
    Assert.assertEquals(
            "[APPLE INC|AAPL|110.0, MICROSOFT CORP|MSFT|1000.0, ALPHABET INC|GOOGL|110.0]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-25")).toString());
  }

  @Test
  public void testSingleDayStrategy() {
    List<String> stocks = List.of("AAPL|25", "MSFT|25", "GOOGL|50");
    String res = iModel.executeStrategy(2, stocks, 130d,
            Util.getDateFromString("2022-11-26"), Util.getDateFromString("2022-11-26"), 10d, 1);

    Assert.assertEquals("Strategy successfully executed for date 2022-11-26", res);
    Assert.assertEquals("[APPLE INC|AAPL|0.0, MICROSOFT CORP|MSFT|0.0, ALPHABET INC|GOOGL|500.0]"
            , iModel.getPortfolioComposition(2, Util.getDateFromString("2022-11-25")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|2.5, MICROSOFT CORP|MSFT|2.5, ALPHABET INC|GOOGL|505.0]"
            , iModel.getPortfolioComposition(2, Util.getDateFromString("2022-11-26")).toString());

    //cost basis
    Assert.assertEquals(11165.0, iModel.getCostBasis(2, Util.getDateFromString("2022-11-25")),
            0.01);
    Assert.assertEquals(11295.0, iModel.getCostBasis(2, Util.getDateFromString("2022-11-26")),
            0.01);

    //portfolio Value
    Assert.assertEquals(5000.0, iModel.getPortfolioValueOnDate(2, Util.getDateFromString("2022"
            + "-11-25")).getPortfolioValue(), 0.01);
    Assert.assertEquals(5100.0, iModel.getPortfolioValueOnDate(2, Util.getDateFromString("2022"
            + "-11-26")).getPortfolioValue(), 0.01);
  }

  @Test
  public void testSingleDayStrategyPublicHoliday() {
    // no price found because of holiday
    Mockito.when(mockPriceProvider.getPriceOfStock(any(), any())).thenThrow(new RuntimeException());

    List<String> stocks = List.of("AAPL|25", "MSFT|25", "GOOGL|50");
    String res = iModel.executeStrategy(2, stocks, 130d,
            Util.getDateFromString("2022-11-26"), Util.getDateFromString("2022-11-26"), 10d, 1);

    Assert.assertEquals("Failed to execute the strategy", res);
    Assert.assertEquals(
            "[APPLE INC|AAPL|110.0, MICROSOFT CORP|MSFT|1000.0, ALPHABET INC|GOOGL|110.0]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-25")).toString());
  }


  @Test
  public void testSingleDayStrategyFutureDate() {
    List<String> stocks = List.of("AAPL|25", "MSFT|25", "GOOGL|50");
    String res = iModel.executeStrategy(2, stocks, 130d,
            Util.getDateFromString("2030-11-26"), Util.getDateFromString("2030-11-26"), 10d, 1);

    Assert.assertEquals("Failed to execute the strategy", res);
    Assert.assertEquals(
            "[APPLE INC|AAPL|110.0, MICROSOFT CORP|MSFT|1000.0, ALPHABET INC|GOOGL|110.0]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-25")).toString());
  }

  @Test
  public void testExecutePendingTransactions() {
    mockDataIO = Mockito.mock(DataIOImpl.class);
    Portfolio newPortfolio = new FlexiblePortfolio("", null, new ArrayList<>());
    List<StockWeight> stockWeights = new ArrayList<>(Arrays.asList(
            new StockWeight("AAPL", "APPLE INC", 50d),
            new StockWeight("GOOGL", "Alphabet inc", 50d)
    ));
    Strategy strategy = new Strategy(Util.getDateFromString("2022-11-10"), Util.getDateFromString(
            "2022-11-15"), null, 130d, 10d, 2, stockWeights);
    newPortfolio.getStrategies().add(strategy);
    List<Portfolio> s = new ArrayList<>(Arrays.asList(newPortfolio));
    Mockito.when(mockDataIO.readPersistedPortfolios()).thenReturn(s);


    iModel = new Model(mockDataIO, mockPriceProvider);
    Assert.assertEquals("[APPLE INC|AAPL|5.5, Alphabet inc|GOOGL|5.5]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-10")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|5.5, Alphabet inc|GOOGL|5.5]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-11")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|11.0, Alphabet inc|GOOGL|11.0]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-12")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|11.0, Alphabet inc|GOOGL|11.0]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-13")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|16.5, Alphabet inc|GOOGL|16.5]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-14")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|16.5, Alphabet inc|GOOGL|16.5]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-15")).toString());
  }


  @Test
  public void testExecutePendingTransactions2() {
    // considering current date as 13th transactions would be executed only till 13th
    mockDataIO = Mockito.mock(DataIOImpl.class);
    Portfolio mockPortfolio = Mockito.spy(new FlexiblePortfolio("", null, new ArrayList<>()));

    doCallRealMethod().doCallRealMethod().doThrow(new RuntimeException()).when(mockPortfolio)
            .executeStrategy(any()
                    , any(), any(), any(), any());
    List<StockWeight> stockWeights = new ArrayList<>(Arrays.asList(
            new StockWeight("AAPL", "APPLE INC", 50d),
            new StockWeight("GOOGL", "Alphabet inc", 50d)
    ));
    Strategy strategy = new Strategy(Util.getDateFromString("2022-11-10"), Util.getDateFromString(
            "2022-11-15"), null, 130d, 10d, 2, stockWeights);
    mockPortfolio.getStrategies().add(strategy);
    List<Portfolio> s = new ArrayList<>(Arrays.asList(mockPortfolio));
    Mockito.when(mockDataIO.readPersistedPortfolios()).thenReturn(s);


    iModel = new Model(mockDataIO, mockPriceProvider);
    Assert.assertEquals("[APPLE INC|AAPL|5.5, Alphabet inc|GOOGL|5.5]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-10")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|5.5, Alphabet inc|GOOGL|5.5]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-11")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|11.0, Alphabet inc|GOOGL|11.0]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-12")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|11.0, Alphabet inc|GOOGL|11.0]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-13")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|11.0, Alphabet inc|GOOGL|11.0]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-14")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|11.0, Alphabet inc|GOOGL|11.0]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-15")).toString());
  }


  @Test
  public void testExecutePendingTransactions3() {
    mockDataIO = Mockito.mock(DataIOImpl.class);
    Portfolio newPortfolio = new FlexiblePortfolio("", null, new ArrayList<>());
    List<StockWeight> stockWeights = new ArrayList<>(Arrays.asList(
            new StockWeight("AAPL", "APPLE INC", 50d),
            new StockWeight("GOOGL", "Alphabet inc", 50d)
    ));
    Strategy strategy = new Strategy(Util.getDateFromString("2022-11-10"), Util.getDateFromString(
            "2022-11-15"), Util.getDateFromString("2022-11-10"), 130d, 10d, 2, stockWeights);
    newPortfolio.getStrategies().add(strategy);
    List<Portfolio> s = new ArrayList<>(Arrays.asList(newPortfolio));
    Mockito.when(mockDataIO.readPersistedPortfolios()).thenReturn(s);


    iModel = new Model(mockDataIO, mockPriceProvider);
    Assert.assertEquals("[APPLE INC|AAPL|NA, Alphabet inc|GOOGL|NA]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-10")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|NA, Alphabet inc|GOOGL|NA]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-11")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|5.5, Alphabet inc|GOOGL|5.5]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-12")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|5.5, Alphabet inc|GOOGL|5.5]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-13")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|11.0, Alphabet inc|GOOGL|11.0]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-14")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|11.0, Alphabet inc|GOOGL|11.0]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-15")).toString());
  }


  @Test
  public void testExecutePendingTransactions4() {
    mockDataIO = Mockito.mock(DataIOImpl.class);
    Portfolio newPortfolio = new FlexiblePortfolio("", null, new ArrayList<>());
    List<StockWeight> stockWeights = new ArrayList<>(Arrays.asList(
            new StockWeight("AAPL", "APPLE INC", 50d),
            new StockWeight("GOOGL", "Alphabet inc", 50d)
    ));
    Strategy strategy = new Strategy(Util.getDateFromString("2022-11-10"), Util.getDateFromString(
            "2022-11-15"), Util.getDateFromString(
            "2022-11-15"), 130d, 10d, 2, stockWeights);
    newPortfolio.getStrategies().add(strategy);
    List<Portfolio> s = new ArrayList<>(Arrays.asList(newPortfolio));
    Mockito.when(mockDataIO.readPersistedPortfolios()).thenReturn(s);


    iModel = new Model(mockDataIO, mockPriceProvider);
    Assert.assertEquals("[]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-10")).toString());
    Assert.assertEquals("[]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-11")).toString());
    Assert.assertEquals("[]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-12")).toString());
    Assert.assertEquals("[]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-13")).toString());
    Assert.assertEquals("[]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-14")).toString());
    Assert.assertEquals("[]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-15")).toString());
  }


}
