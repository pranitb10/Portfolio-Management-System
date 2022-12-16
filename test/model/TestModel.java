package model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

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
import main.java.model.entities.StockPriceResponseWrapper;
import main.java.model.util.Util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * This is the test for model.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestModel {
  private DataIO mockDataIO;
  private PriceProvider mockPriceProvider;

  private IModel iModel;

  @Before
  public void setUp() throws Exception {
    NavigableSet<StockTransaction> inflexibleSet = new TreeSet<>();
    inflexibleSet.add(new StockTransaction(LocalDate.parse("2022-11-01"),
            10d, 5.0d, Util.TransactionType.BUY));
    inflexibleSet.add(new StockTransaction(LocalDate.parse("2022-11-10"),
            110d, 5.0d, Util.TransactionType.BUY));

    NavigableSet<StockTransaction> flexibleSet = new TreeSet<>();
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
                                            ((TreeSet<StockTransaction>) inflexibleSet).clone()))
            ),
            new FlexiblePortfolio("flexible", LocalDate.parse("2022-11-15"),
                    Arrays.asList(
                            new PurchasedStock("APPLE INC", "AAPL", flexibleSet),
                            new PurchasedStock("MICROSOFT CORP", "MSFT",
                                    (NavigableSet<StockTransaction>)
                                            ((TreeSet<StockTransaction>) flexibleSet).clone()),
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
  public void testDataIOExceptions() {
    Mockito.when(mockDataIO.readPersistedPortfolios()).thenReturn(null);
    try {
      IModel iModel1 = new Model(mockDataIO, mockPriceProvider);
      fail();
    } catch (IllegalArgumentException e) {
      // test passed
    }
    Mockito.when(mockDataIO.readPersistedPortfolios()).thenReturn(new ArrayList<>());
    Mockito.when(mockDataIO.readMasterStockList()).thenReturn(null);
    try {
      IModel iModel2 = new Model(mockDataIO, mockPriceProvider);
      fail();
    } catch (IllegalArgumentException e) {
      // pass
    }
  }

  @Test
  public void isValidPortfolioName() {

    //invalid inputs
    Assert.assertTrue(iModel.isValidPortfolioName().test("flexible"));
    Assert.assertTrue(iModel.isValidPortfolioName().test("inflexible"));
    Assert.assertTrue(iModel.isValidPortfolioName().test(" "));
    Assert.assertTrue(iModel.isValidPortfolioName().test(""));
    Assert.assertTrue(iModel.isValidPortfolioName().test("fsdbj@@"));
    Assert.assertTrue(iModel.isValidPortfolioName().test("fsdbj*@#$%^&*"));
    Assert.assertTrue(iModel.isValidPortfolioName().test(" dsf "));

    // valid inputs
    Assert.assertFalse(iModel.isValidPortfolioName().test("new portfolio"));
    Assert.assertFalse(iModel.isValidPortfolioName().test("new 1"));
    Assert.assertFalse(iModel.isValidPortfolioName().test("FANNG stocks"));
  }

  @Test
  public void validateStockNameSymbol() {
    //invalid inputs
    Assert.assertTrue(iModel.validateStockNameSymbol().test("Apple"));
    Assert.assertTrue(iModel.validateStockNameSymbol().test("AAAPL"));
    Assert.assertTrue(iModel.validateStockNameSymbol().test("Google"));
    Assert.assertTrue(iModel.validateStockNameSymbol().test("Alphabet"));
    Assert.assertTrue(iModel.validateStockNameSymbol().test("randomStock"));
    Assert.assertTrue(iModel.validateStockNameSymbol().test("12fguer13"));
    Assert.assertTrue(iModel.validateStockNameSymbol().test("microsoft"));
    Assert.assertTrue(iModel.validateStockNameSymbol().test("AGILENTE TECHNOLOGIES INC"));
    Assert.assertTrue(iModel.validateStockNameSymbol().test(null));

    // valid inputs
    Assert.assertFalse(iModel.validateStockNameSymbol().test("AAPL"));
    Assert.assertFalse(iModel.validateStockNameSymbol().test("APpLe InC"));
    Assert.assertFalse(iModel.validateStockNameSymbol().test("MSFT"));
    Assert.assertFalse(iModel.validateStockNameSymbol().test("MICROSOFT CORP"));
    Assert.assertFalse(iModel.validateStockNameSymbol().test("AGILENT TECHNOLOGIES INC"));


  }

  @Test
  public void getPortfolioComposition() {
    // illegal arg
    try {
      iModel.getPortfolioComposition(5, LocalDate.now());
      fail();
    } catch (IllegalArgumentException e) {
      // pass
    }
    try {
      iModel.getPortfolioComposition(-1, LocalDate.now());
      fail();
    } catch (IllegalArgumentException e) {
      // pass
    }

    // inflexible
    // irrespective of date inflexible should give us same composition
    Assert.assertEquals("[APPLE INC|AAPL|110.0, MICROSOFT CORP|MSFT|1000.0, ALPHABET " +
                    "INC|GOOGL|110.0]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2020-10-10")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|110.0, MICROSOFT CORP|MSFT|1000.0, ALPHABET " +
                    "INC|GOOGL|110.0]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-03")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|110.0, MICROSOFT CORP|MSFT|1000.0, ALPHABET " +
                    "INC|GOOGL|110.0]"
            , iModel.getPortfolioComposition(1, Util.getDateFromString("2022-11-15")).toString());

    // flexible
    // composition should be showed according to the latest transaction done on the stock
    Assert.assertEquals("[APPLE INC|AAPL|20.0, MICROSOFT CORP|MSFT|20.0, ALPHABET " +
                    "INC|GOOGL|NA]"
            , iModel.getPortfolioComposition(2, Util.getDateFromString("2021-11-04")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|50.0, MICROSOFT CORP|MSFT|50.0, ALPHABET " +
                    "INC|GOOGL|NA]"
            , iModel.getPortfolioComposition(2, Util.getDateFromString("2021-11-06")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|50.0, MICROSOFT CORP|MSFT|50.0, ALPHABET " +
                    "INC|GOOGL|NA]"
            , iModel.getPortfolioComposition(2, Util.getDateFromString("2021-11-12")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|0.0, MICROSOFT CORP|MSFT|0.0, ALPHABET " +
                    "INC|GOOGL|1000.0]"
            , iModel.getPortfolioComposition(2, Util.getDateFromString("2021-11-16")).toString());
    Assert.assertEquals("[APPLE INC|AAPL|0.0, MICROSOFT CORP|MSFT|0.0, ALPHABET " +
                    "INC|GOOGL|500.0]"
            , iModel.getPortfolioComposition(2, Util.getDateFromString("2021-11-20")).toString());


  }

  @Test
  public void transactStockExceptions() {
    // illegal args tests
    //=====invalid args for buy transactions======
    // invalid index
    try {
      iModel.transactStock(5, "", 0d, "b", LocalDate.now());
      fail();
    } catch (IllegalArgumentException e) {
      // pass
    }

    //invalid symbol
    try {
      iModel.transactStock(1, "", 0d, "b", LocalDate.now());
      fail();
    } catch (IllegalArgumentException e) {
      // pass
    }

    //invalid Qty
    try {
      iModel.transactStock(1, "AAPL", -10d, "b", LocalDate.now());
      fail();
    } catch (IllegalArgumentException e) {
      // pass
    }

    // invalid transaction type
    try {
      iModel.transactStock(1, "AAPL", 10d, "a", LocalDate.now());
      fail();
    } catch (IllegalArgumentException e) {
      // pass
    }

    // invalid future date
    try {
      iModel.transactStock(1, "AAPL", 10d, "b",
              Util.getDateFromString("9999-01-01"));
      fail();
    } catch (IllegalArgumentException e) {
      // pass
    }
    //=====invalid args with sell transactions ======
    // invalid index
    try {
      iModel.transactStock(5, "", 0d, "s", LocalDate.now());
      fail();
    } catch (IllegalArgumentException e) {
      // pass
    }

    //invalid symbol
    try {
      iModel.transactStock(1, "", 0d, "s", LocalDate.now());
      fail();
    } catch (IllegalArgumentException e) {
      // pass
    }

    //invalid Qty
    try {
      iModel.transactStock(1, "AAPL", -10d, "s", LocalDate.now());
      fail();
    } catch (IllegalArgumentException e) {
      // pass
    }

    // invalid date
    try {
      iModel.transactStock(1, "AAPL", 10d, "s",
              Util.getDateFromString("9999-01-01"));
      fail();
    } catch (IllegalArgumentException e) {
      // pass
    }

    // inflexible portfolio transact method not allowed
    try {
      iModel.transactStock(1, "AAPL", 10d, "b",
              Util.getDateFromString("2010-01-01"));
      fail();
    } catch (RuntimeException e) {
      // pass
    }
  }

  @Test
  public void testCommission() {
    // illegal arg
    try {
      iModel.setCommission(-12d);
      fail();
    } catch (IllegalArgumentException e) {
      // pass
    }

    Mockito.when(mockPriceProvider.getPriceOfStock(anyString(), any())).thenReturn(100d);
    iModel.createFlexiblePortfolio("new test portfolio");
    // with default commission value of 5 from the model
    iModel.transactStock(3, "aapl", 10d, "b",
            Util.getDateFromString("2020-10-10"));
    Assert.assertEquals(1005d, iModel.getCostBasis(3, Util.getDateFromString("2020-10-10")), 0.01);

    // setting commission to 100 and checking cost basis after a buy transaction
    iModel.setCommission(100d);
    iModel.transactStock(3, "aapl", 10d, "b",
            Util.getDateFromString("2020-10-15"));
    Assert.assertEquals(2105d, iModel.getCostBasis(3, Util.getDateFromString("2020-10-15")), 0.01);

  }

  @Test
  public void testTransactionBuy1() {
    // asserts before and after transaction
    Assert.assertEquals("[APPLE INC|AAPL|0.0, MICROSOFT CORP|MSFT|0.0, ALPHABET INC|GOOGL|1000" +
                    ".0]",
            iModel.getPortfolioComposition(2, Util.getDateFromString("2021-11-16"
            )).toString());
    iModel.transactStock(2, "aapl", 50.0, "b",
            Util.getDateFromString("2021-11-16"));
    Assert.assertEquals("[APPLE INC|AAPL|50.0, MICROSOFT CORP|MSFT|0.0, ALPHABET INC|GOOGL|1000" +
                    ".0]",
            iModel.getPortfolioComposition(2, Util.getDateFromString("2021-11-16"
            )).toString());
  }

  @Test
  public void testTransactionBuy2() {
    // asserts before and after transaction
    Assert.assertEquals("[APPLE INC|AAPL|NA, MICROSOFT CORP|MSFT|NA, ALPHABET INC|GOOGL|NA]",
            iModel.getPortfolioComposition(2, Util.getDateFromString("2021-10-30"
            )).toString());
    iModel.transactStock(2, "aapl", 100.0, "b",
            Util.getDateFromString("2021-10-30"));
    Assert.assertEquals("[APPLE INC|AAPL|100.0, MICROSOFT CORP|MSFT|NA, ALPHABET INC|GOOGL|NA]",
            iModel.getPortfolioComposition(2, Util.getDateFromString("2021-10-30"
            )).toString());
  }

  @Test
  public void testTransactionBuy3() {
    // asserts before and after transaction
    Assert.assertEquals("[APPLE INC|AAPL|50.0, MICROSOFT CORP|MSFT|50.0, ALPHABET INC|GOOGL|NA]",
            iModel.getPortfolioComposition(2, Util.getDateFromString("2021-11-06"
            )).toString());
    iModel.transactStock(2, "MSFT", 1000.0, "b",
            Util.getDateFromString("2021-11-06"));
    Assert.assertEquals("[APPLE INC|AAPL|50.0, MICROSOFT CORP|MSFT|1050.0, ALPHABET INC|GOOGL|NA]",
            iModel.getPortfolioComposition(2, Util.getDateFromString("2021-11-06"
            )).toString());
  }

  @Test
  public void testTransactionValidSell1() {
    // asserts before and after transaction
    Assert.assertEquals("[APPLE INC|AAPL|0.0, MICROSOFT CORP|MSFT|0.0, ALPHABET INC|GOOGL|500.0]",
            iModel.getPortfolioComposition(2, Util.getDateFromString("2021-11-20"
            )).toString());
    iModel.transactStock(2, "GOOGL", 200.0, "s",
            Util.getDateFromString("2021-11-20"));
    Assert.assertEquals("[APPLE INC|AAPL|0.0, MICROSOFT CORP|MSFT|0.0, ALPHABET INC|GOOGL|300.0]",
            iModel.getPortfolioComposition(2, Util.getDateFromString("2021-11-20"
            )).toString());
  }

  @Test
  public void testTransactionInValidSell1() {
    // sell without any previous buy
    Assert.assertEquals("[APPLE INC|AAPL|NA, MICROSOFT CORP|MSFT|NA, ALPHABET INC|GOOGL|NA]",
            iModel.getPortfolioComposition(2, Util.getDateFromString("2021-10-01"
            )).toString());

    try {
      iModel.transactStock(2, "GOOGL", 200.0, "s",
              Util.getDateFromString("2021-10-01"));
      fail();
    } catch (RuntimeException e) {
      // pass as transaction is invalid
    }
  }


  @Test
  public void testTransactionInValidSell2() {
    // sell qty more than available qty
    Assert.assertEquals("[APPLE INC|AAPL|50.0, MICROSOFT CORP|MSFT|50.0, ALPHABET INC|GOOGL|NA]",
            iModel.getPortfolioComposition(2, Util.getDateFromString("2021-11-05"
            )).toString());

    try {
      iModel.transactStock(2, "AAPL", 200.0, "s",
              Util.getDateFromString("2021-11-05"));
      fail();
    } catch (RuntimeException e) {
      // pass as transaction is invalid
    }
  }

  @Test
  public void testTransactionInValidSell3() {
    // invalid chronology of sell transaction
    Assert.assertEquals("[APPLE INC|AAPL|50.0, MICROSOFT CORP|MSFT|50.0, ALPHABET INC|GOOGL|NA]",
            iModel.getPortfolioComposition(2, Util.getDateFromString("2021-11-05"
            )).toString());

    // sell qty more than available qty
    try {
      iModel.transactStock(2, "AAPL", 50.0, "s",
              Util.getDateFromString("2021-11-06"));
      fail();
    } catch (RuntimeException e) {
      // pass as transaction is invalid
    }
  }


  @Test
  public void getCostBasis() {
    //Illegal arg test
    try {
      iModel.getCostBasis(5, LocalDate.now());
      fail();
    } catch (IllegalArgumentException e) {
      // pass
    }
    try {
      iModel.getCostBasis(-1, LocalDate.now());
      fail();
    } catch (IllegalArgumentException e) {
      // pass
    }
    try {
      iModel.getCostBasis(-1, Util.getDateFromString("9999-10-10"));
      fail();
    } catch (IllegalArgumentException e) {
      // pass
    }

    // inflexible does not support cost basis
    try {
      iModel.getCostBasis(1, LocalDate.now());
      fail();
    } catch (RuntimeException e) {
      // pass
    }
    Mockito.when(mockPriceProvider.getPriceOfStock(anyString(), any())).thenReturn(10d);

    // test all the cost basis for different dates based on the transactions given to the model
    // in the setup method
    Assert.assertEquals(0d, iModel.getCostBasis(2, Util.getDateFromString("2021-10-30")), 0.01);
    Assert.assertEquals(410d, iModel.getCostBasis(2, Util.getDateFromString("2021-11-01")), 0.01);
    Assert.assertEquals(1030d, iModel.getCostBasis(2, Util.getDateFromString("2021-11-05")), 0.01);
    Assert.assertEquals(1060d, iModel.getCostBasis(2, Util.getDateFromString("2021-11-15")), 0.01);
    Assert.assertEquals(11065d, iModel.getCostBasis(2, Util.getDateFromString("2021-11-18")), 0.01);
    Assert.assertEquals(11165d, iModel.getCostBasis(2, Util.getDateFromString("2021-11-20")), 0.01);

  }

  @Test
  public void createFlexiblePortfolio() {
    // illegal arg
    try {
      iModel.createFlexiblePortfolio("flexible");
      fail();
    } catch (IllegalArgumentException e) {
      //pass
    }

    // assert creation
    Assert.assertEquals(3, iModel.createFlexiblePortfolio("New flexible"));
  }

  @Test
  public void createInflexiblePortfolio() {
    // illegal args
    try {
      List<String> stocks = new ArrayList<>(Arrays.asList("alphabet|35|2022-04-12"));
      iModel.createInflexiblePortfolio("new portfolio", stocks);
      fail();
    } catch (Exception e) {
      // pass
    }

    try {
      List<String> stocks = new ArrayList<>(Arrays.asList("aapl|-35|2022-04-12"));
      iModel.createInflexiblePortfolio("new portfolio", stocks);
      fail();
    } catch (Exception e) {
      // pass
    }

    try {
      List<String> stocks = new ArrayList<>(Arrays.asList("aapl|35|9999-04-12"));
      iModel.createInflexiblePortfolio("new portfolio", stocks);
      fail();
    } catch (Exception e) {
      // pass
    }

    List<String> stocks = new ArrayList<>(Arrays.asList(
            "aapl|35|2020-10-10",
            "googl|35|2020-10-10",
            "aapl|35|2020-10-10",
            "aapl|35|2020-10-15",
            "googl|35|2020-10-15"
    ));
    iModel.createInflexiblePortfolio("new portfolio", stocks);
    Assert.assertEquals("[APPLE INC|AAPL|105.0, ALPHABET INC|GOOGL|70.0]"
            , iModel.getPortfolioComposition(3, Util.getDateFromString("2020-10-10")).toString());

  }

  @Test
  public void testGetPortfolioValueOnDateFlexible() {
    Mockito.when(mockPriceProvider.getPriceOfStock(anyString(), any())).thenReturn(10d);
    // flexible portfolio returns portfolio value considering the quanitity which was present on
    // that date
    StockPriceResponseWrapper stockPriceResponseWrapper1 = iModel.getPortfolioValueOnDate(
            2, Util.getDateFromString("2021-11-01"));
    StockPriceResponseWrapper stockPriceResponseWrapper2 = iModel.getPortfolioValueOnDate(
            2, Util.getDateFromString("2021-11-05"));
    StockPriceResponseWrapper stockPriceResponseWrapper3 = iModel.getPortfolioValueOnDate(
            2, Util.getDateFromString("2022-11-15"));

    //portfolio value asserts
    Assert.assertEquals(400d, stockPriceResponseWrapper1.getPortfolioValue(), 0.01);
    Assert.assertEquals(1000d, stockPriceResponseWrapper2.getPortfolioValue(), 0.01);
    Assert.assertEquals(5000d, stockPriceResponseWrapper3.getPortfolioValue(), 0.01);

    //individual stock asserts
    Assert.assertEquals("[APPLE INC|AAPL|20.0|10.0|200.0, MICROSOFT CORP|MSFT|20.0|10.0|200.0, " +
                    "ALPHABET INC|GOOGL|0|10.0|0]",
            stockPriceResponseWrapper1.getSuccessList().toString());
    Assert.assertEquals("[APPLE INC|AAPL|50.0|10.0|500.0, MICROSOFT CORP|MSFT|50.0|10.0|500.0, " +
                    "ALPHABET INC|GOOGL|0|10.0|0]",
            stockPriceResponseWrapper2.getSuccessList().toString());
    Assert.assertEquals("[APPLE INC|AAPL|0|10.0|0, MICROSOFT CORP|MSFT|0|10.0|0, ALPHABET " +
                    "INC|GOOGL|500.0|10.0|5000.0]",
            stockPriceResponseWrapper3.getSuccessList().toString());
  }

  @Test
  public void getPortfolioValueEdgeCases() {
    Mockito.when(mockPriceProvider.getPriceOfStock(anyString(), any())).thenReturn(10d)
            .thenThrow(new RuntimeException());
    // inflexible portfolio returns portfolio value considering the final quantity
    StockPriceResponseWrapper stockPriceResponseWrapper1 = iModel.getPortfolioValueOnDate(
            1, Util.getDateFromString("2022-11-05"));

    Assert.assertEquals(2, stockPriceResponseWrapper1.getFailureList().size());
    Assert.assertEquals(1, stockPriceResponseWrapper1.getSuccessList().size());

    Assert.assertEquals(1100d, stockPriceResponseWrapper1.getPortfolioValue(), 0.01);

    Assert.assertEquals("[APPLE INC|AAPL|110.0|10.0|1100.0]",
            stockPriceResponseWrapper1.getSuccessList().toString());
    Assert.assertEquals("[MICROSOFT CORP|MSFT|1000.0, ALPHABET INC|GOOGL|110.0]",
            stockPriceResponseWrapper1.getFailureList().toString());
  }

  @Test
  public void testLoadInFlexiblePortfolio() {
    // illegal arg
    try {
      iModel.loadInFlexiblePortfolioFromFile("inflexible");
      fail();
    } catch (IllegalArgumentException e) {
      //pass
    }

    // invalid entries from user file
    // invalid stockname
    try {
      Mockito.when(mockDataIO.readUserGivenFile(any())).thenReturn(Arrays.asList("alphabet|35" +
              "|2022"
              + "-04"
              + "-12"));
      iModel.loadInFlexiblePortfolioFromFile("new inflexible");
      fail();
    } catch (Exception e) {
      // pass
    }

    //negative stockQty
    try {
      Mockito.when(mockDataIO.readUserGivenFile(any())).thenReturn(Arrays.asList("aapl|-35|2022" +
              "-04-12"));
      iModel.loadInFlexiblePortfolioFromFile("new inflexible");
      fail();
    } catch (Exception e) {
      // pass
    }

    // future date
    try {
      Mockito.when(mockDataIO.readUserGivenFile(any())).thenReturn(Arrays.asList("aapl|35|9999-04" +
              "-12"));
      iModel.loadInFlexiblePortfolioFromFile("new inflexible");
      fail();
    } catch (Exception e) {
      // pass
    }

    // valid entries from userFile
    List<String> stocks = new ArrayList<>(Arrays.asList(
            "aapl|35|2020-10-10",
            "googl|35|2020-10-10",
            "aapl|35|2020-10-10",
            "aapl|35|2020-10-15",
            "googl|35|2020-10-15"
    ));
    Mockito.when(mockDataIO.readUserGivenFile(any())).thenReturn(stocks);
    iModel.loadInFlexiblePortfolioFromFile("new inflexible");
    Assert.assertEquals("[APPLE INC|AAPL|105.0, ALPHABET INC|GOOGL|70.0]",
            iModel.getPortfolioComposition(3, Util.getDateFromString("2020-10-10"))
                    .toString());
  }

  @Test
  public void testLoadFlexiblePortfolio() {
    // illegal arg
    try {
      iModel.loadFlexiblePortfolioFromFile("flexible","res/user_flexible.csv");
      fail();
    } catch (IllegalArgumentException e) {
      //pass
    }

    try {
      Mockito.when(mockDataIO.readUserGivenFile(any())).thenReturn(new ArrayList<>(Arrays.asList(
              "ABBV|23|2022-10-10|b"
      )));
      iModel.loadFlexiblePortfolioFromFile("flexible","res/user_flexible.csv");
      fail();
    } catch (IllegalArgumentException e) {
      //pass
    }


    List<String> stocks = new ArrayList<>(Arrays.asList(
            "GOOGL|35|2022-04-12|b",
            "AAPL|100|2022-05-10|b",
            "AAPL|2000|2022-05-12|b",
            "AAPL|2000|2022-05-13|s"
    ));
    Mockito.when(mockDataIO.readUserGivenFile(any())).thenReturn(stocks);
    iModel.loadFlexiblePortfolioFromFile("new flexible","res/user_flexible.csv");
    Assert.assertEquals("[ALPHABET INC|GOOGL|35.0, APPLE INC|AAPL|NA]",
            iModel.getPortfolioComposition(3, Util.getDateFromString("2022-04-12"))
                    .toString());
    Assert.assertEquals("[ALPHABET INC|GOOGL|35.0, APPLE INC|AAPL|100.0]",
            iModel.getPortfolioComposition(3, Util.getDateFromString("2022-05-10"))
                    .toString());
    Assert.assertEquals("[ALPHABET INC|GOOGL|35.0, APPLE INC|AAPL|2100.0]",
            iModel.getPortfolioComposition(3, Util.getDateFromString("2022-05-12"))
                    .toString());
    Assert.assertEquals("[ALPHABET INC|GOOGL|35.0, APPLE INC|AAPL|100.0]",
            iModel.getPortfolioComposition(3, Util.getDateFromString("2022-05-13"))
                    .toString());

  }

  @Test
  public void testGetPortfolioValueOnDateInFlexible() {
    // illegal args
    try {
      iModel.getPortfolioValueOnDate(5, LocalDate.now());
      fail();
    } catch (IllegalArgumentException e) {
      // pass
    }
    try {
      iModel.getPortfolioValueOnDate(-1, LocalDate.now());
      fail();
    } catch (IllegalArgumentException e) {
      // pass
    }

    Mockito.when(mockPriceProvider.getPriceOfStock(anyString(), any())).thenReturn(10d);
    // inflexible portfolio returns portfolio value considering the final quantity
    StockPriceResponseWrapper stockPriceResponseWrapper1 = iModel.getPortfolioValueOnDate(
            1, Util.getDateFromString("2022-11-05"));
    StockPriceResponseWrapper stockPriceResponseWrapper2 = iModel.getPortfolioValueOnDate(
            1, Util.getDateFromString("2022-10-01"));
    StockPriceResponseWrapper stockPriceResponseWrapper3 = iModel.getPortfolioValueOnDate(
            1, Util.getDateFromString("2022-11-20"));

    Assert.assertEquals(0, stockPriceResponseWrapper1.getFailureList().size());
    Assert.assertEquals(0, stockPriceResponseWrapper2.getFailureList().size());
    Assert.assertEquals(0, stockPriceResponseWrapper3.getFailureList().size());

    Assert.assertEquals(3, stockPriceResponseWrapper1.getSuccessList().size());
    Assert.assertEquals(3, stockPriceResponseWrapper2.getSuccessList().size());
    Assert.assertEquals(3, stockPriceResponseWrapper3.getSuccessList().size());

    //portfolio value asserts
    Assert.assertEquals(12200d, stockPriceResponseWrapper1.getPortfolioValue(), 0.01);
    Assert.assertEquals(12200d, stockPriceResponseWrapper2.getPortfolioValue(), 0.01);
    Assert.assertEquals(12200d, stockPriceResponseWrapper3.getPortfolioValue(), 0.01);


    //individual stock value asserts
    String expected_value = "[APPLE INC|AAPL|110.0|10.0|1100.0, MICROSOFT CORP|MSFT|1000.0|10" +
            ".0|10000" +
            ".0, ALPHABET INC|GOOGL|110.0|10.0|1100.0]";
    Assert.assertEquals(expected_value, stockPriceResponseWrapper1.getSuccessList().toString());
    Assert.assertEquals(expected_value, stockPriceResponseWrapper2.getSuccessList().toString());
    Assert.assertEquals(expected_value, stockPriceResponseWrapper3.getSuccessList().toString());
  }


  @Test
  public void getAllPortfolios() {
    Assert.assertEquals("[inflexible|2022-11-15|3, flexible|2022-11-15|3]",
            iModel.getAllPortfolios().toString());
    LocalDate date = Util.getDateFromString("2022-11-16");
    Mockito.when(mockDataIO.readPersistedPortfolios()).thenReturn(Arrays.asList(
            new FlexiblePortfolio("flex1", date, new ArrayList<>()),
            new InFlexiblePortfolio("inflex1", date, new ArrayList<>()),
            new InFlexiblePortfolio("inflex2", date, new ArrayList<>()),
            new FlexiblePortfolio("flex2", date, new ArrayList<>())
    ));
    iModel = new Model(mockDataIO, mockPriceProvider);
    Assert.assertEquals("[flex1|2022-11-16|0, inflex1|2022-11-16|0, inflex2|2022-11-16|0, " +
            "flex2|2022-11-16|0]", iModel.getAllPortfolios().toString());
  }

  @Test
  public void fetchGraphDataLessThanFiveDays() {
    IModel mockedModel = new Model(mockDataIO, mockPriceProvider);
    List<String> graphOutput = mockedModel.fetchGraphData(1,
            LocalDate.parse("2022-11-12"),
            LocalDate.parse("2022-11-14"));
    List<String> expected = Arrays.asList("2022-11-12|12200.0|50",
            "2022-11-13|12200.0|50",
            "2022-11-14|12200.0|50");

    assertArrayEquals(expected.toArray(), graphOutput.toArray());
  }

  @Test
  public void fetchGraphDataDays() {
    IModel mockedModel = new Model(mockDataIO, mockPriceProvider);
    List<String> graphOutput = mockedModel.fetchGraphData(1,
            LocalDate.parse("2022-10-30"),
            LocalDate.parse("2022-11-14"));
    List<String> expected = Arrays.asList("2022-10-30|12200.0|50", "2022-10-31|12200.0|50", "2022" +
                    "-11-01|12200.0|50", "2022-11-02|12200.0|50",
            "2022-11-03|12200.0|50", "2022" +
                    "-11-04|12200.0|50", "2022-11-05|12200.0|50",
            "2022-11-06|12200.0|50", "2022" +
                    "-11-07|12200.0|50", "2022-11-08|12200.0|50",
            "2022-11-09|12200.0|50", "2022" +
                    "-11-10|12200.0|50",
            "2022-11-11|12200.0|50", "2022-11-12|12200.0|50",
            "2022-11-13|12200.0|50",
            "2022-11-14|12200.0|50");

    assertArrayEquals(expected.toArray(), graphOutput.toArray());
  }

  @Test
  public void fetchGraphData31DaysSwitchToWeeks() {
    IModel mockedModel = new Model(mockDataIO, mockPriceProvider);
    List<String> graphOutput = mockedModel.fetchGraphData(1,
            LocalDate.parse("2022-01-01"),
            LocalDate.parse("2022-01-31"));
    List<String> expected = Arrays.asList("Week: 2021-12-26 to 2022-01-01 |12200.0|50",
            "Week: 2022-01-02 to 2022-01-08 |12200.0|50",
            "Week: 2022-01-09 to 2022-01-15 |12200" +
                    ".0|50",
            "Week: 2022-01-16 to 2022-01-22 |12200.0|50",
            "Week: 2022-01-23 to 2022-01-29 |12200" +
                    ".0|50",
            "Week: 2022-01-30 to 2022-02-05 |12200.0|50");

    assertArrayEquals(expected.toArray(), graphOutput.toArray());
  }

  @Test
  public void fetchGraphData31WeeksSwitchToMonths() {
    IModel mockedModel = new Model(mockDataIO, mockPriceProvider);
    List<String> graphOutput = mockedModel.fetchGraphData(1,
            LocalDate.parse("2022-01-01"),
            LocalDate.parse("2022-07-27"));
    List<String> expected = Arrays.asList("Jan-2022|12200.0|50", "Feb-2022|12200.0|50",
            "Mar-2022|12200.0|50", "Apr-2022|12200.0|50",
            "May-2022|12200.0|50",
            "Jun-2022|12200.0|50", "Jul-2022|12200.0|50");

    assertArrayEquals(expected.toArray(), graphOutput.toArray());
  }

  @Test
  public void fetchGraphData31MonthsSwitchToQuarters() {
    IModel mockedModel = new Model(mockDataIO, mockPriceProvider);
    List<String> graphOutput = mockedModel.fetchGraphData(1,
            LocalDate.parse("2022-01-01"),
            LocalDate.parse("2025-08-01"));
    List<String> expected = Arrays.asList("Q1-2022|12200.0|50", "Q2-2022|12200.0|50", "Q3-2022" +
                    "|12200" +
                    ".0|50",
            "Q4-2022|12200.0|50", "Q1-2023|12200.0|50",
            "Q2-2023|12200.0|50", "Q3-2023|12200.0|50",
            "Q4-2023|12200.0|50", "Q1-2024|12200.0|50",
            "Q2-2024|12200.0|50", "Q3-2024|12200.0|50",
            "Q4-2024|12200.0|50", "Q1-2025|12200.0|50",
            "Q2-2025|12200.0|50", "Q3-2025|12200.0|50");

    assertArrayEquals(expected.toArray(), graphOutput.toArray());
  }

  @Test
  public void fetchGraphData31QuarterSwitchToYears() {
    IModel mockedModel = new Model(mockDataIO, mockPriceProvider);
    List<String> graphOutput = mockedModel.fetchGraphData(1,
            LocalDate.parse("2022-01-01"),
            LocalDate.parse("2032-08-01"));
    List<String> expected = Arrays.asList("2022|12200.0|50",
            "2023|12200.0|50",
            "2024|12200.0|50",
            "2025|12200.0|50",
            "2026|12200.0|50",
            "2027|12200.0|50",
            "2028|12200.0|50",
            "2029|12200.0|50",
            "2030|12200.0|50",
            "2031|12200.0|50",
            "2032|12200.0|50");

    assertArrayEquals(expected.toArray(), graphOutput.toArray());
  }
}

