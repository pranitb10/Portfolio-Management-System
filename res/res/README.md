# ASSIGNMENT 5 #

## Team Members: Nair Karthik, Sankhadasariya Udit ##
## Code Receivers: Pranit Brahmbhatt, Sachi Patel ##

## How to run the code ##
#### Through a JAR file: ####
* In order to run the code through a JAR file, go to the res folder and open a terminal/cmd there,
and type in the following command to execute the jar file: 
"java -jar PortfolioManagementSystem.jar text" (To run the CLI version) or 
"java -jar PortfolioManagementSystem.jar gui" (To run the GUI version).
#### Through the main PMS class in IDE: ####
* In order to run the code directly from the main method i.e. the PMS Class, go to the 
"Edit Configurations" of PMS in IDE and either write "text" or "GUI" in the "Build and run"
textfield and press "OK". After that, just run the PMS file.

### Restrictions: ###

Stocks: We support all stocks listed on NYSE website (8128 to be precise) which can found
at [here](NYSE_ALL_STOCKS.csv).

Date: We are currently dependent on the Alphavantage API, for getting price of a given stock name/
company.
If queried for a stock we hit the API and also cache the complete historical price data of the
stock for future.
Next query for the same stock for any past date will return the result from the cache.

## Parts we were able to complete ##
### Feature 1: Creation of Inflexible Portfolio ###
We allow creation of one portfolio at a time, and we ask for following inputs from user:

* Name for Portfolio: User can name portfolio with alphabets (A-Z, a-z, space: standalone
  spaces are not allowed), the portfolio name is unique.
* Quantity of stocks: Number of stocks that you want to include into portfolio. The number
  should be non-negative.
* Stock Name or Stock Symbol: Enter the name of the company or stock symbol from the supported
  stocks.
* Quantity of shares: For each stock the number of shares to be brought, it should be
  non-negative integer.
* Date of purchase: For inflexible we do ask for date as input from the user which should be 
  valid format i.e yyyy-mm-dd, but the 
  **operations pertaining to this type of portfolio is independent of date.**

### Feature 2: Creation of Flexible Portfolio ###
We allow creation of one portfolio at a time, and we ask for following inputs from user:
* Name for Portfolio: User can name portfolio with alphabets (A-Z, a-z, space: standalone
  spaces are not allowed), the portfolio name is unique.
* Quantity of stocks: Number of stocks that you want to include into portfolio. The number
  should be non-negative.
* Stock Name or Stock Symbol: Enter the name of the company or stock symbol from the supported
  stocks.
* Quantity of shares: For each stock the number of shares to be brought, it should be
  non-negative integer.
* Type of transaction: For this type of transaction we ask for whether user wants to BUY or 
  SELL for each stock input taken.
* Date of purchase: For Flexible portfolios we do ask for date as input from the user which 
  should be valid format i.e yyyy-mm-dd, but the operations pertaining to this type portfolio is 
  dependent on date.
* In this type of portfolio we can do multiple transactions to an stock.

### Feature 3: Get Portfolio Composition ###
We allow user to view the composition of a chosen portfolio:

* The ID of Portfolio to view: User can select the specific number from list of portfolio
  displayed to view the composition of the selected Portfolio.
* This would actually display the Details of stocks i.e:
  * Inflexible: the stock symbol and company name,
      number of shares brought by the user.
  * Flexible: the stock symbol and company name,
    number of shares brought by the user until that date. If the date provided is such that 
    user didn't had any stocks by that date than NA is shown in the stock Quantity column.

### Feature 4: Value of Portfolio at a given date ###
We allow user to evaluate the value of Portfolio at a given date:

* We display the portfolios at disposal and ask user to select the specific portfolio.
* Date: The date at which portfolio value needs to be determined.
* The following are the effects for different types of portfolios:
  * Inflexible: We would get the composition of the stocks involved in the portfolio for the 
    concerned dates and show there corresponding prices.
  * Flexible: In this case as we would get the all the transactions until that day and evaluate 
    the quantity of stocks left with the user by that date and than value is generated until the 
    date which user specifies. 

### Feature 5: Load Flexible Portfolio from User given file ###
We allow user to upload a csv file:
* Input of file path: res/user_inflexible.csv.
* The user file should be placed in the above mentioned path.
* The csv file should be in specific format in order it to read, the format goes as follow:

> StockName|Quantity|TransactionDate|TransactionType <br>
> ABBV|35|2022-04-12|b <br>
> AAPL|100|2022-05-10|b <br>
> MSFT|100|2022-11-11|b <br>
> MSFT|40|2022-11-12|s <br>

The successful records will be stored in the portfolio name given by the user while inputting
during this user journey

### Feature 6: Load Inflexible Portfolio from User given file ###
We allow user to upload a csv file:
* Input of file path: res/user_inflexible.csv.
* The user file should be placed in the above mentioned path.
* The csv path should be in specific format in order it to read, the format goes as follows:

> Stock Details | Quantity | Date <br>
> ABBV | 35 | 2022-04-12 <br>
> Wayfair Inc | 65 | 2022-04-12 <br>
> Wayfair Inc. | 12 | 2022-04-12 <br>
> Assured Guaranty Ltd | 123 | 2022-04-12 <br>
> AGO | 123 | 2022-05-12 <br>
> TEMP | 11 | 2012-09-08 <br>
> AGO| -9 | 2022-05-12 <br>

The successful records will be stored in the portfolio name given by the user while inputting 
during this user journey

### Feature 7: Transactions in the Flexible Portfolio ###
We do not allow any transactions on an inflexible portfolio.<br>
However,it is allowed on a flexible portfolio, for this we ask the following inputs:
* The ID of Portfolio to view: User can select the specific number from list of portfolio
  displayed to view the composition of the selected Portfolio.
* Stock Name or Stock Symbol: Enter the name of the company or stock symbol from the supported
    stocks.
* Type of transaction: For this type of transaction we ask for whether user wants to BUY or
  SELL for each stock input taken.
* Quantity of stocks: Number of stocks that you want to include into portfolio. The number
  should be non-negative.
* Date of transaction: For Flexible portfolios we do ask for date as input from the user which
  should be valid format i.e yyyy-mm-dd
* If the user enters an invalid transaction, It would indicate that transaction cannot coexist


### Feature 8: Cost Basis in the Flexible Portfolio ###
We do not allow getting cost basis on an inflexible portfolio.<br>
We allow getting cost basis on a  flexible portfolios for this we ask the following inputs:
* The ID of Portfolio to view: User can select the specific number from list of portfolio
  displayed to view the composition of the selected Portfolio.
* Date of for which cost basis needs to be determined: For Flexible portfolios we do ask for 
  date as input from the user which should be valid format i.e yyyy-mm-dd.
* If the user enters an invalid transaction, It would indicate that transaction cannot coexist


### Feature 9: Update Commission ###
We allow to update the commission for future transactions:
* Commission fee that needs to be changed for that portfolio: By default the commission is 
  allotted as 5.0 but the user can change the commission in the system.

### Feature 10: Display of the Graph output of portfolio ###
This feature get the user the distribution for a particular portfolio for a given start date or 
end date:
* The ID of Portfolio to view: User can select the specific number from list of portfolio
  displayed to view the composition of the selected Portfolio.
* Start Date: Start date as input from the user which should be valid format i.e yyyy-mm-dd.
* End Date: End date as input from the user which should be valid format i.e: yyyy-mm-dd.
* The scales on which Date is allowed is DAYS, WEEKS, MONTHS, QUARTERS, YEARS, LEAP YEARS, 
  DECADES, SEMI CENTENNIALS AND CENTURY
* So for minimum we can have 1 day displayed on the graph and maximum we have 3000 years.
* The way we keep the graph inside the breadth (50) is take the max price and normalize it to 
  50 and then relatively scale every value in the list, this ensure that we stay within range.

### Feature 11: Invest a fixed amount using specified weight for each stock in the portfolio on a particular date ###
This feature allows the user to select a portfolio and give the following inputs:
* Investment Amount: the amount that user wants to invest into the portfolio.
* The Stock Name: This would be picked up from the portfolio which user has selected.
* the Weightage of investment: By default the weightage will be divided equally for all the 
  stocks. The user can enter his custom weight for any stock if needed.
* Date of Execution: The date on which strategy needs to be executed.

### Feature 12: Create a start to end dollar cost averaging strategy as single operation ###
This feature allows the user do dollar cost averaging on a portfolio. The strategy can be 
executed on a new portfolio or on an existing portfolio.
* Investment Amount: the amount that user wants to invest into the portfolio.
* The number of stocks: This would be automatically generated if the user wants to apply for 
  existing portfolio but for create portfolio user has to give custom value.
* The Stock Name: This would be picked up from the portfolio if user has selected to execute 
  strategy from existing portfolio else user enters the stock name.
* the Weightage of investment: By default the weightage will be divided equally for all the
  stocks. The user can enter his custom weight for any stock if needed.
* Date of Execution: The date on which strategy needs to be executed.
* End Date of Execution: The date on which the strategy stops the execution, user will be given 
  an option to keep the strategy without end date.
* Frequency: The frequency on how often the investment should be made according to the stocks and 
  there weights provided.

### Feature 13: Rebalance a portfolio on the basis of the percent weights provided by the user ###
This feature allows the user to rebalance the entire portfolio by specifying particular percent
weight for each and every stock in a portfolio.
* Portfolio name: The name of the portfolio that is to be rebalanced.
* Date: The date on which the portfolio needs to be rebalanced.
* Percent weight: The percent weight defined by the user for each and every stock in a portfolio on
  which basis the stock will be bought and sold from a portfolio in order to rebalance it.

## Changes that we made to implement new methods in Assignment 7: ##
* As the "Model" and "FlexiblePortfolio" classes are tightly coupled with each other and the 
  controller uses the Model class to call for the methods, we defined our new method 
  "RebalancingPortfolio" in the "IModel" interface and the "Portfolio" interface and implemented 
  these methods in their respective implemented classes. After that, we made a new method to 
  implement in "TextController" and "FrameController" to call for these methods through controller 
  from these classes. And to finally display it in the view, we added a new method 
  "RebalancePortfolio" in the "TextView" for cli view and created two new classes "RebalanceView" 
  and "StockPercent" for the GUI view.
