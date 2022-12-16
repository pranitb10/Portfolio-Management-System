package controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.NavigableSet;
import java.util.Random;
import java.util.TreeSet;
import java.util.function.Predicate;

import main.java.controller.IController;
import main.java.controller.TextController;
import main.java.model.AlphaVantageImpl;
import main.java.model.DataIO;
import main.java.model.DataIOImpl;
import main.java.model.FlexiblePortfolio;
import main.java.model.InFlexiblePortfolio;
import main.java.model.Model;
import main.java.model.PriceProvider;
import main.java.model.entities.PurchasedStock;
import main.java.model.entities.Stock;
import main.java.model.entities.StockTransaction;
import main.java.model.util.Util;
import main.java.view.TextView;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * This is tests for flexible portfolios.
 */
public class TestControllerCreateFlexiblePortfolio {

  StringBuilder expected;
  private StringBuilder mainMenuString;

  private DataIO mockDataIO;
  private PriceProvider mockPriceProvider;

  private IController controller;


  private Model mockModel;

  private final Predicate trueTest = (o -> true);

  private final Predicate falseTest = (o -> false);

  @Before
  public void init() {
    expected = new StringBuilder();
    mainMenuString = new StringBuilder("==================================\n" +
                                               "What would you like to do today?\n" +
                                               "1.Create a InFlexible PortFolio\n" +
                                               "2.Create a Flexible PortFolio\n" +
                                               "3.Get Portfolio composition\n" +
                                               "4.Get value of Portfolio on a given Date\n" +
                                               "5.Load Flexible Portfolio\n" +
                                               "6.Load InFlexible Portfolio\n" +
                                               "7.Buy/Sell a stock in an portfolio\n" +
                                               "8.Get cost basis of a Portfolio\n" +
                                               "9.Update Commission of the system\n" +
                                               "10.Show Graphical Representation of Portfolio\n" +
                                               "11.Quit\n");
    expected.append(mainMenuString);

    NavigableSet<StockTransaction> inflexibleSet = new TreeSet<>();
    inflexibleSet.add(new StockTransaction(LocalDate.parse("2022-11-01"),
                                           10d, 5d, Util.TransactionType.BUY));

    NavigableSet<StockTransaction> flexibleSet = new TreeSet<>();
    flexibleSet.add(new StockTransaction(LocalDate.parse("2022-11-01"),
                                         20d, 5d, Util.TransactionType.BUY));
    flexibleSet.add(new StockTransaction(LocalDate.parse("2022-11-02"),
                                         50d, 5d, Util.TransactionType.BUY));
    flexibleSet.add(new StockTransaction(LocalDate.parse("2022-11-15"),
                                         0d, 5d, Util.TransactionType.SELL));

    mockDataIO = Mockito.mock(DataIOImpl.class);
    Mockito.when(mockDataIO.readMasterStockList()).thenReturn(Arrays.asList(
            new Stock("APPLE INC", "AAPL"),
            new Stock("MICROSOFT CORP", "MSFT"),
            new Stock("ALPHABET INC", "GOOGL"),
            new Stock("AGILENT TECHNOLOGIES INC", "A")));
    Mockito.when(mockDataIO.readPersistedPortfolios()).thenReturn(Arrays.asList(
            new InFlexiblePortfolio("inflexible", LocalDate.parse("2022-11-15"),
                                    Arrays.asList(
                                            new PurchasedStock("APPLE INC", "AAPL", inflexibleSet),
                                            new PurchasedStock("MICROSOFT CORP", "MSFT",
                                                               inflexibleSet),
                                            new PurchasedStock("ALPHABET INC", "GOOGL",
                                                               inflexibleSet))
            ),
            new FlexiblePortfolio("flexible", LocalDate.parse("2022-11-15"),
                                  Arrays.asList(
                                          new PurchasedStock("APPLE INC", "AAPL", flexibleSet),
                                          new PurchasedStock("MICROSOFT CORP", "MSFT", flexibleSet),
                                          new PurchasedStock("ALPHABET INC", "GOOGL", flexibleSet))
            )));

    mockPriceProvider = Mockito.mock(AlphaVantageImpl.class);
    Random r = new Random();
    double random = 0 + r.nextDouble() * (0 - 100);
    Mockito.when(mockPriceProvider.getPriceOfStock(anyString(), any(LocalDate.class)))
           .thenReturn(10.0d);
    mockModel = Mockito.mock(Model.class);


  }

  @Test
  public void testMainMenu() {
    String input = "11";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    OutputStream out = new ByteArrayOutputStream();
    StringBuilder logs = new StringBuilder();

    Model mockModel = new Model(mockDataIO, mockPriceProvider);

    controller = new TextController(mockModel,
                                    new TextView(new PrintStream(out)),
                                    in);
    controller.start();
    input = "";
    assertEquals("",input);
    assertEquals(expected.toString(), out.toString());
  }

  @Test
  public void testMainMenuInvalidOption() {
    String input = "12\n11";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    OutputStream out = new ByteArrayOutputStream();
    StringBuilder logs = new StringBuilder();
    expected.append("Invalid option, please enter a valid option\n");
    expected.append(mainMenuString);


    controller = new TextController(mockModel,
                                    new TextView(new PrintStream(out)),
                                    in);
    controller.start();
    input = "";
    assertEquals("",input);
    assertEquals(expected.toString(), out.toString());

  }

  @Test
  public void testCreateFlexiblePortfolio() {
    String input = "2\ntemp\n1\nA\nb\n10\n2022-11-11\n11";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    OutputStream out = new ByteArrayOutputStream();
    controller = new TextController(mockModel,
                                    new TextView(new PrintStream(out)),
                                    in);

    Predicate<String> mockPredicateStockName = Mockito.mock(Predicate.class);
    Predicate<String> mockPredicateStockNameSymbol = Mockito.mock(Predicate.class);

    Mockito.when(mockPredicateStockName.test(anyString())).thenReturn(false);
    Mockito.when(mockPredicateStockNameSymbol.test(anyString())).thenReturn(false);

    Mockito.when(mockModel.isValidPortfolioName()).thenReturn(mockPredicateStockName);
    Mockito.when(mockModel.validateStockNameSymbol()).thenReturn(mockPredicateStockNameSymbol);

    Mockito.doReturn(1).when(mockModel).createFlexiblePortfolio(anyString());


    Mockito.doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        assertEquals(1, args[0]);
        return null;
      }
    }).when(mockModel).transactStock(anyInt(), anyString(), anyDouble(), anyString(),
                                     any(LocalDate.class));

    controller.start();
    input = "";
    assertEquals("",input);
  }

  @Test
  public void testCreateFlexiblePortfolioNameNull() {
    String input = "2\n\ntemp\n1\nA\nb\n10\n2022-11-11\n11";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    OutputStream out = new ByteArrayOutputStream();
    controller = new TextController(mockModel,
                                    new TextView(new PrintStream(out)),
                                    in);

    Predicate<String> mockPredicateStockName = Mockito.mock(Predicate.class);
    Predicate<String> mockPredicateStockNameSymbol = Mockito.mock(Predicate.class);

    Mockito.when(mockPredicateStockName.test(anyString())).thenReturn(true, false);
    Mockito.when(mockPredicateStockNameSymbol.test(anyString())).thenReturn(false);

    Mockito.when(mockModel.isValidPortfolioName()).thenReturn(mockPredicateStockName);
    Mockito.when(mockModel.validateStockNameSymbol()).thenReturn(mockPredicateStockNameSymbol);

    Mockito.doReturn(1).when(mockModel).createFlexiblePortfolio(anyString());


    Mockito.doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        assertEquals(1, args[0]);
        return null;
      }
    }).when(mockModel).transactStock(anyInt(), anyString(), anyDouble(), anyString(),
                                     any(LocalDate.class));

    controller.start();
    input = "";
    assertEquals("",input);
  }

  @Test
  public void testCreateFlexiblePortfolioNameNumeric() {
    String input = "2\n2\ntemp\n1\nA\nb\n10\n2022-11-11\n11";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    OutputStream out = new ByteArrayOutputStream();
    controller = new TextController(mockModel,
                                    new TextView(new PrintStream(out)),
                                    in);

    Predicate<String> mockPredicateStockName = Mockito.mock(Predicate.class);
    Predicate<String> mockPredicateStockNameSymbol = Mockito.mock(Predicate.class);

    Mockito.when(mockPredicateStockName.test(anyString())).thenReturn(true, false);
    Mockito.when(mockPredicateStockNameSymbol.test(anyString())).thenReturn(false);

    Mockito.when(mockModel.isValidPortfolioName()).thenReturn(mockPredicateStockName);
    Mockito.when(mockModel.validateStockNameSymbol()).thenReturn(mockPredicateStockNameSymbol);

    Mockito.doReturn(1).when(mockModel).createFlexiblePortfolio(anyString());


    Mockito.doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        assertEquals(1, args[0]);
        return null;
      }
    }).when(mockModel).transactStock(anyInt(), anyString(), anyDouble(), anyString(),
                                     any(LocalDate.class));

    controller.start();
    input = "";
    assertEquals("",input);
  }

  @Test
  public void testCreateFlexiblePortfolioNameSpace() {
    String input = "2\n \ntemp\n1\nA\nb\n10\n2022-11-11\n11";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    OutputStream out = new ByteArrayOutputStream();
    controller = new TextController(mockModel,
                                    new TextView(new PrintStream(out)),
                                    in);

    Predicate<String> mockPredicateStockName = Mockito.mock(Predicate.class);
    Predicate<String> mockPredicateStockNameSymbol = Mockito.mock(Predicate.class);

    Mockito.when(mockPredicateStockName.test(anyString())).thenReturn(true, false);
    Mockito.when(mockPredicateStockNameSymbol.test(anyString())).thenReturn(false);

    Mockito.when(mockModel.isValidPortfolioName()).thenReturn(mockPredicateStockName);
    Mockito.when(mockModel.validateStockNameSymbol()).thenReturn(mockPredicateStockNameSymbol);

    Mockito.doReturn(1).when(mockModel).createFlexiblePortfolio(anyString());


    Mockito.doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        assertEquals(1, args[0]);
        return null;
      }
    }).when(mockModel).transactStock(anyInt(), anyString(), anyDouble(), anyString(),
                                     any(LocalDate.class));

    controller.start();
    input = "";
    assertEquals("",input);
  }

}
