# Design Document

This application follows a traditional Model-View-Controller architecture. The responsibilities of
each module are as follows:

* Model - The model is responsible for providing all the features of the application.
* View - The view is responsible for displaying the output to the user.
* Controller - The controller is responsible for managing the complete flow of the application. It
  receives input from the user, and based on the input it asks the model for any functionality and
  redirects to the view to show some output to the User.

For now the scope of the project is a text-based User Interface.
Hence we have an interface each for model, view and controller, and corresponding text-based
implementations of view and controller.

### Some important design decisions

* The model, view and controller are implemented in complete isolation of each other. The
  inter-communication between MVC is implemented in such a way, that any current implementation of
  model, view or controller can easily be plugged out and a new implementation can be easily be
  plugged in.
* The inter-communication between MVC only happens through Generic available data types like Strings
  and other primitive types. This will ease the development of any new implementation of either of
  the three.
* The model is the main component of the application which does majority of the processing. It
  holds all the information like the list of supported stocks, persisted portfolios etc. The
  Data input and output from the Model is decoupled into another interfaces (DataIO and
  PriceProvider) which is responsible for providing and fetching data from the model like
  persistedPortfolios, stockMasterList, prices of stocks etc.
* This follows Adaptor design pattern,The DataIO and PriceProvider objects needs to be passed to
  the model during its
  creation, to get the desired behaviour.
* For now we have one implementation for the DataIO interface which reads and writes all the
  data from Json's and passes it to the model. In Future this can be replaced with any other
  implementation which can
  deal with any data sources like CSV, excel, SQL etc. and functionalities of the model would
  still work fine as it is, adhering by the SOLID principle "open for extension and closed for
  modification".

### Description of important entity classes

* Stock - A class that represents a stock that is supported by the application(it has attributes
  like
  name and symbol, any more static attributes like description,fundamentals etc. can be added in
  future).
* StockTransaction - It represents a transaction entity with details like transactionType
  (buy/sell),date,commission,Quantity etc..
* PurchasedStock - It extends the Stock class retaining all the information from the parent...
  apart from this it has List of transactions(StockTransaction class) associated with that stock.

## Major Design Changes

* Portfolio as a Union type
    * In our previous design portfolio was an entity class which had data like the list of purchased
      stock and date of creation.
    * We have replaced it with a Union type, wherein Portfolio is now an interface which has all the
      methods which are required to operate on a portfolio.
    * An Abstract class, AbstractPortfolio implements this interface and maintains all the data
      attributes of the
      portfolio like list of stocks,portfolioName and dateAdded and implements its corresponding
      getters and
      setters.
    * Two classes InflexiblePortfolio and FlexiblePortfolio extends the above abstract portfolio
      class and implements the other unimplemented methods, according to there definitions and
      requirements.


* PriceProvider interface
    * Earlier the price fetching logic was with : DataIO interface - which was injected as an
      adapter to the Model.
    * We have further decoupled it into a PriceProvider interface so that we can support
      multiple implementations in the future(API providers).
    * An Abstract class AbstractPriceProvider implements the above interface, it has an abstract
      method getPriceFromAPI which has to be compulsorily implemented by the child classes
      extending it.
    * This AbstractClass handles the caching logic, it holds the historical prices of the
      stocks in a cache, it first checks the price in the cache if not than only calls the
      getPriceFromAPI method from the child class. Once prices are retrieved it saves it to the
      cache.
    * AlphaVantageImpl is the only class for now which extends this abstract class.
    * This also follows an adaptor design pattern, PriceProvider object can be passed to the
      model while its creation, to get the prices from desired provider.

* Changes in the Model
    * New methods have been added to the IModel interface to support additional features like
      getCostBasis, setCommission, show Graph etc.
    * The methods that were getting the information related portfolios remain unchanged, the
      model will internally use dynamic dispatch on the portfolio union type to get the necessary
      information.
    * The method to create a portfolio and load a portfolio from user changes, now they are
      divided into two separate methods to create flexible and inflexible portfolio and to
      load flexible and inflexible portfolio.
* Changes in the View
    * All previous methods remain unchanged, only a new method to display graph is added to the
      IView interface.
* Changes in the Controller
    * Only major changes in the controller are to support the additional features like costBasis,
      SetCommission, showGraph, buy/sell stock etc.
    * Since creation of portfolio methods(flexible/inflexible) changed in IModel additional
      cases are added in the controller to handle the same. 

## Changes post assignment 6
* Changes in the Model
    * New method is added to the IModel interface to support additional feature of 
      dollar-cost averaging and to support execution of weighted investment strategy on a 
      particular date.
    * Moreover, Entity classes like(Strategy,StockPrice,StockWeight) are created which help in 
      persisting the strategy, and help doing transactions on ongoing strategies for future dates, 
      Everything else in the model remains unchanged.
* Changes in the Controller
    * For the new GUI interface we have added a new Controller with an Interface and its 
      implementation.
    * The old Text controller couldn't be reused as the user-flow of GUI is completely 
      different from that of a text-based UI.
* Changes in the View
    * We have added new View with its Interface and implementation for supporting the GUI.
    * The new View interacts with the new FrameController which is specifically designed to 
      worked with GUI.
    * The Old View(TextView) is still functional with the old controller(textController).
