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
import java.util.List;
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
import static org.mockito.ArgumentMatchers.anyString;


/**
 * This is tests for inflexible portfolios.
 */
public class TestControllerCreatePortfolioInflexible {

  private final Predicate trueTest = (o -> true);
  private final Predicate falseTest = (o -> false);
  StringBuilder expected;
  private StringBuilder mainMenuString;
  private DataIO mockDataIO;
  private PriceProvider mockPriceProvider;
  private IController controller;
  private Model mockModel;

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
    assertEquals("", input);
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
    assertEquals("", input);
    assertEquals(expected.toString(), out.toString());

  }

  @Test
  public void testCreateInflexiblePortfolio() {
    String input = "1\ntemp\n1\nA\n10\n2022-11-11\n11";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    OutputStream out = new ByteArrayOutputStream();
    controller = new TextController(mockModel,
            new TextView(new PrintStream(out)),
            in);

    Predicate<String> mockPredicateStockName = Mockito.mock(Predicate.class);
    Predicate<String> mockPredicateStockNameSymbol = Mockito.mock(Predicate.class);

    Mockito.when(mockPredicateStockName.test(anyString())).thenReturn(false);
    Mockito.when(mockPredicateStockNameSymbol.test(anyString())).thenReturn(false);

    Mockito.when(mockModel.isValidPortfolioName()).thenReturn(falseTest);
    Mockito.when(mockModel.validateStockNameSymbol()).thenReturn(falseTest);
    Mockito.doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        assertEquals("temp", args[0]);
        List<String> stockList = (List<String>) args[1];
        List<String> expected = Arrays.asList("A|10.0|2022-11-11");
        assertEquals(expected, stockList);
        return null;
      }
    }).when(mockModel).createInflexiblePortfolio(anyString(), any(List.class));

    controller.start();
    input = "";
    assertEquals("", input);
  }

  @Test
  public void testCreateInflexiblePortfolioNullName() {
    String input = "1\n\ntemp\n1\nA\n10\n2022-11-11\n11";
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

    Mockito.doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        assertEquals("temp", args[0]);
        List<String> stockList = (List<String>) args[1];
        List<String> expected = Arrays.asList("A|10.0|2022-11-11");
        assertEquals(expected, stockList);
        return null;
      }
    }).when(mockModel).createInflexiblePortfolio(anyString(), any(List.class));

    controller.start();
    input = "";
    assertEquals("", input);
  }

  @Test
  public void testCreateInflexiblePortfolioAlreadyExistName() {
    String input = "1\nyt\ntemp\n1\nA\n10\n2022-11-11\n11";
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

    Mockito.doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        assertEquals("temp", args[0]);
        List<String> stockList = (List<String>) args[1];
        List<String> expected = Arrays.asList("A|10.0|2022-11-11");
        assertEquals(expected, stockList);
        return null;
      }
    }).when(mockModel).createInflexiblePortfolio(anyString(), any(List.class));

    controller.start();
    input = "";
    assertEquals("", input);
  }

  @Test
  public void testCreateInflexiblePortfolioNumericName() {
    String input = "1\n2\ntemp\n1\nA\n10\n2022-11-11\n11";
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

    Mockito.doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        assertEquals("temp", args[0]);
        List<String> stockList = (List<String>) args[1];
        List<String> expected = Arrays.asList("A|10.0|2022-11-11");
        assertEquals(expected, stockList);
        return null;
      }
    }).when(mockModel).createInflexiblePortfolio(anyString(), any(List.class));

    controller.start();
    input = "";
    assertEquals("", input);
  }

  @Test
  public void testCreateInflexiblePortfolioAlreadyExistingName() {
    String input = "1\nyt\ntemp\n1\nA\n10\n2022-11-11\n11";
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

    Mockito.doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        assertEquals("temp", args[0]);
        List<String> stockList = (List<String>) args[1];
        List<String> expected = Arrays.asList("A|10.0|2022-11-11");
        assertEquals(expected, stockList);
        return null;
      }
    }).when(mockModel).createInflexiblePortfolio(anyString(), any(List.class));

    controller.start();
    input = "";
    assertEquals("", input);
  }

  @Test
  public void testCreateInflexiblePortfolioNegativeNumStocks() {
    String input = "1\ntemp\n-1\n1\nA\n10\n2022-11-11\n11";
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

    Mockito.doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        assertEquals("temp", args[0]);
        List<String> stockList = (List<String>) args[1];
        List<String> expected = Arrays.asList("A|10.0|2022-11-11");
        assertEquals(expected, stockList);
        return null;
      }
    }).when(mockModel).createInflexiblePortfolio(anyString(), any(List.class));

    controller.start();
    input = "";
    assertEquals("", input);
  }

  @Test
  public void testCreateInflexiblePortfolioNumStocksCharacter() {
    String input = "1\ntemp\n \n1\nA\n10\n2022-11-11\n11";
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

    Mockito.doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        assertEquals("temp", args[0]);
        List<String> stockList = (List<String>) args[1];
        List<String> expected = Arrays.asList("A|10.0|2022-11-11");
        assertEquals(expected, stockList);
        return null;
      }
    }).when(mockModel).createInflexiblePortfolio(anyString(), any(List.class));

    controller.start();
    input = "";
    assertEquals("", input);
  }

  @Test
  public void testCreateInflexiblePortfolioNumStocksZero() {
    String input = "1\ntemp\n0\n1\nA\n10\n2022-11-11\n11";
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

    Mockito.doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        assertEquals("temp", args[0]);
        List<String> stockList = (List<String>) args[1];
        List<String> expected = Arrays.asList("A|10.0|2022-11-11");
        assertEquals(expected, stockList);
        return null;
      }
    }).when(mockModel).createInflexiblePortfolio(anyString(), any(List.class));

    controller.start();
    input = "";
    assertEquals("", input);
  }

  @Test
  public void testCreateInflexiblePortfolioInvalidStockName() {
    String input = "1\ntemp\n1\n \nA\n10\n2022-11-11\n11";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    OutputStream out = new ByteArrayOutputStream();
    controller = new TextController(mockModel,
            new TextView(new PrintStream(out)),
            in);
    Predicate<String> mockPredicateStockName = Mockito.mock(Predicate.class);
    Predicate<String> mockPredicateStockNameSymbol = Mockito.mock(Predicate.class);

    Mockito.when(mockPredicateStockName.test(anyString())).thenReturn(false);
    Mockito.when(mockPredicateStockNameSymbol.test(anyString())).thenReturn(true, false);
    Mockito.when(mockModel.isValidPortfolioName()).thenReturn(mockPredicateStockName);
    Mockito.when(mockModel.validateStockNameSymbol()).thenReturn(mockPredicateStockNameSymbol);

    Mockito.doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        assertEquals("temp", args[0]);
        List<String> stockList = (List<String>) args[1];
        List<String> expected = Arrays.asList("A|10.0|2022-11-11");
        assertEquals(expected, stockList);
        return null;
      }
    }).when(mockModel).createInflexiblePortfolio(anyString(), any(List.class));

    controller.start();
    input = "";
    assertEquals("", input);
  }

  @Test
  public void testCreateInflexiblePortfolioNumericStockName() {
    String input = "1\ntemp\n1\n5\nA\n10\n2022-11-11\n11";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    OutputStream out = new ByteArrayOutputStream();
    controller = new TextController(mockModel,
            new TextView(new PrintStream(out)),
            in);
    Predicate<String> mockPredicateStockName = Mockito.mock(Predicate.class);
    Predicate<String> mockPredicateStockNameSymbol = Mockito.mock(Predicate.class);

    Mockito.when(mockPredicateStockName.test(anyString())).thenReturn(false);
    Mockito.when(mockPredicateStockNameSymbol.test(anyString())).thenReturn(true, false);
    Mockito.when(mockModel.isValidPortfolioName()).thenReturn(mockPredicateStockName);
    Mockito.when(mockModel.validateStockNameSymbol()).thenReturn(mockPredicateStockNameSymbol);

    Mockito.doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        assertEquals("temp", args[0]);
        List<String> stockList = (List<String>) args[1];
        List<String> expected = Arrays.asList("A|10.0|2022-11-11");
        assertEquals(expected, stockList);
        return null;
      }
    }).when(mockModel).createInflexiblePortfolio(anyString(), any(List.class));

    controller.start();
    input = "";
    assertEquals("", input);
  }

  @Test
  public void testCreateInflexiblePortfolioStockCompanyName() {
    String input = "1\ntemp\n1\nAPPLE\n10\n2022-11-11\n11";
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

    Mockito.doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        assertEquals("temp", args[0]);
        List<String> stockList = (List<String>) args[1];
        List<String> expected = Arrays.asList("APPLE|10.0|2022-11-11");
        assertEquals(expected, stockList);
        return null;
      }
    }).when(mockModel).createInflexiblePortfolio(anyString(), any(List.class));

    controller.start();
    input = "";
    assertEquals("", input);
  }

  @Test
  public void testCreateInflexiblePortfolioStockQuantEmpty() {
    String input = "1\ntemp\n1\nA\n\n10\n2022-11-11\n11";
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

    Mockito.doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        assertEquals("temp", args[0]);
        List<String> stockList = (List<String>) args[1];
        List<String> expected = Arrays.asList("A|10.0|2022-11-11");
        assertEquals(expected, stockList);
        return null;
      }
    }).when(mockModel).createInflexiblePortfolio(anyString(), any(List.class));

    controller.start();
    input = "";
    assertEquals("", input);
  }

  @Test
  public void testCreateInflexiblePortfolioStockQuantNegative() {
    String input = "1\ntemp\n1\nA\n-1\n10\n2022-11-11\n11";
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

    Mockito.doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        assertEquals("temp", args[0]);
        List<String> stockList = (List<String>) args[1];
        List<String> expected = Arrays.asList("A|10.0|2022-11-11");
        assertEquals(expected, stockList);
        return null;
      }
    }).when(mockModel).createInflexiblePortfolio(anyString(), any(List.class));

    controller.start();
    input = "";
    assertEquals("", input);
  }

  @Test
  public void testCreateInflexiblePortfolioStockQuantZero() {
    String input = "1\ntemp\n1\nA\n0\n10\n2022-11-11\n11";
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

    Mockito.doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        assertEquals("temp", args[0]);
        List<String> stockList = (List<String>) args[1];
        List<String> expected = Arrays.asList("A|10.0|2022-11-11");
        assertEquals(expected, stockList);
        return null;
      }
    }).when(mockModel).createInflexiblePortfolio(anyString(), any(List.class));

    controller.start();
    input = "";
    assertEquals("", input);
  }

  @Test
  public void testCreateInflexiblePortfolioStockQuantCharacter() {
    String input = "1\ntemp\n1\nA\nc\n10\n2022-11-11\n11";
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

    Mockito.doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        assertEquals("temp", args[0]);
        List<String> stockList = (List<String>) args[1];
        List<String> expected = Arrays.asList("A|10.0|2022-11-11");
        assertEquals(expected, stockList);
        return null;
      }
    }).when(mockModel).createInflexiblePortfolio(anyString(), any(List.class));

    controller.start();
    input = "";
    assertEquals("", input);
  }


  @Test
  public void testCreateInflexiblePortfolioMultiplePortfolios() {
    String input = "1\ntemp\n2\nA\n10\n2022-11-11\nAAPL\n10\n2022-11-12\n11";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    OutputStream out = new ByteArrayOutputStream();
    controller = new TextController(mockModel,
            new TextView(new PrintStream(out)),
            in);
    Predicate<String> mockPredicateStockName = Mockito.mock(Predicate.class);
    Predicate<String> mockPredicateStockNameSymbol = Mockito.mock(Predicate.class);

    Mockito.when(mockPredicateStockName.test(anyString())).thenReturn(false, false);
    Mockito.when(mockPredicateStockNameSymbol.test(anyString())).thenReturn(false, false);
    Mockito.when(mockModel.isValidPortfolioName()).thenReturn(mockPredicateStockName);
    Mockito.when(mockModel.validateStockNameSymbol()).thenReturn(mockPredicateStockNameSymbol);

    Mockito.doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        assertEquals("temp", args[0]);
        List<String> stockList = (List<String>) args[1];
        List<String> expected = Arrays.asList("A|10.0|2022-11-11", "AAPL|10.0|2022-11-12");
        assertEquals(expected, stockList);
        return null;
      }
    }).when(mockModel).createInflexiblePortfolio(anyString(), any(List.class));

    controller.start();
    input = "";
    assertEquals("", input);
  }


  @Test
  public void testCreateInflexiblePortfolioMultiplePortfoliosWithOneInvalid() {
    String input = "1\ntemp\n2\nA\n10\n2022-11-11\n \nAAPL\n10\n2022-11-12\n11";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    OutputStream out = new ByteArrayOutputStream();
    controller = new TextController(mockModel,
            new TextView(new PrintStream(out)),
            in);
    Predicate<String> mockPredicateStockName = Mockito.mock(Predicate.class);
    Predicate<String> mockPredicateStockNameSymbol = Mockito.mock(Predicate.class);

    Mockito.when(mockPredicateStockName.test(anyString())).thenReturn(false, false);
    Mockito.when(mockPredicateStockNameSymbol.test(anyString())).thenReturn(false, true, false);
    Mockito.when(mockModel.isValidPortfolioName()).thenReturn(mockPredicateStockName);
    Mockito.when(mockModel.validateStockNameSymbol()).thenReturn(mockPredicateStockNameSymbol);

    Mockito.doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        Object[] args = invocation.getArguments();
        assertEquals("temp", args[0]);
        List<String> stockList = (List<String>) args[1];
        List<String> expected = Arrays.asList("A|10.0|2022-11-11", "AAPL|10.0|2022-11-12");
        assertEquals(expected, stockList);
        return null;
      }
    }).when(mockModel).createInflexiblePortfolio(anyString(), any(List.class));

    controller.start();
    input = "";
    assertEquals("", input);
  }
}
