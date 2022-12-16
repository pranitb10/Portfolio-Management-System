# SETUP README

## JAR File setup

* You would find assignment_6.jar which would be the main jar to test and run this assignment
* When run the JAR for the assignment you would be able to access the main menu and get started 
  right away, it would look something like as follows:
* The jar is inside res folder please copy it outside the res folder, it should be at same
  level as the res folder.
### Run Configurations of JAR:
> For the text based user interface <br>
> _java -jar assignment_6.jar text_ <br>
> For the graphical user interface <br>
> _java -jar assignment_6.jar gui_ <br>
> Please note that if any arguments other than these are provided then the application won't run.
> If you want to run this project in IntelliJ please import it as a maven project, if asked to 
> download dependencies from pom.xml please click OK. once dependencies aer downloaded the 
> project would be ready to run.


> ================================== <br>
> What would you like to do today? <br>
> 1.Create an InFlexible PortFolio <br>
> 2.Create a Flexible PortFolio <br>
> 3.Get Portfolio composition <br>
> 4.Get value of Portfolio on a given Date <br>
> 5.Load Flexible Portfolio <br>
> 6.Load InFlexible Portfolio <br>
> 7.Buy/Sell a stock in an portfolio <br>
> 8.Get cost basis of a Portfolio <br>
> 9.Update Commission of the system <br>
> 10.Show Graphical Representation of Portfolio <br>
> 11.Quit <br>

### Please enter the option number mentioned in the main menu in order to test the features.

#### For e.g: Enter 1 to create an Inflexible portfolio.

# Create an Inflexible Portfolio:

* Inflexible portfolio is the one which cannot be edited. It also does not support feature like 
  getting costBasis. 
* To create an inflexible Portfolio you would be asked these data in order

> _1_ <br>
> Enter the name of the Portfolio: <br>
> _ooo_ <br>
> How many stocks do you want to add to the portfolio? <br>
> _1_ <br>
> Enter the name or symbol for stock 1: <br>
> _AAPL_ <br>
> Enter the quantity of shares purchased for stock 1: <br>
> _10_ <br>
> Please Enter the date at which you bought the stock 1(yyyy-mm-dd)  <br>
> _2022-11-15_ <br>

Once the input data needed from the user is taken then program automatically persists
the portfolio and redirects you to the main menu.

# Create a Flexible Portfolio:

* A flexible portfolio is the one which can be edited, and we can also get cost basis and display 
  graph of the portfolio values on a date interval.
* To create a flexible Portfolio you would be asked these data in order

> 2 <br>
> Enter the name of the Portfolio: <br>
> yyy <br>
> How many stocks do you want to add to the portfolio? <br>
> 1 <br>
> Enter the name or symbol of the stock : <br>
> AAPL <br>
> Do want to buy or sell the stock? Enter b for buy or s for sell. <br>
> b <br>
> Enter the quantity of stocks you wish to transact? <br>
> 10 <br>
> Enter the date of the transaction(yyyy-mm-dd): <br>
> 2022-11-15 <br>

Once the input data needed from the user is taken then program automatically persists
the portfolio and redirects you to the main menu.

# Portfolio Composition:

* To get a Portfolio composition, first you would be displayed all the portfolios at the disposal.

> ================================== <br>
> What would you like to do today? <br>
> 1.Create a InFlexible PortFolio <br>
> 2.Create a Flexible PortFolio <br>
> 3.Get Portfolio composition <br>
> 4.Get value of Portfolio on a given Date <br>
> 5.Load Flexible Portfolio <br>
> 6.Load InFlexible Portfolio <br>
> 7.Buy/Sell a stock in an portfolio <br>
> 8.Get cost basis of a Portfolio <br>
> 9.Update Commission of the system <br>
> 10.Show Graphical Representation of Portfolio <br>
> 11.Quit <br>
> _3_ <br>

| id | Portfolio Name          | Number of Stocks        | Date of Creation |
| ---|-------------------------|-------------------------|------------------|
|  1 | e                       | 1                       | 2022-10-31       |
|  2 | loaded portfolio        | 4                       | 2022-10-31       | 
|  3 | name                    | 4                       | 2022-10-31       | 
|  4 | temp2                   | 1                       | 2022-10-31       | 
|  5 | new kartik              | 1                       | 2022-10-31       | 
|  6 | kartik loaded portfolio | 4                       | 2022-10-31       | 
|  7 | temp                    | 1                       | 2022-11-02       | 

* User can then select the portfolio id for which composition can be viewed.

> Enter id of the portfolio you want see the composition for: <br>
> _7_ <br>
> Enter the date for which you want to see the composition(yyyy-mm-dd): <br>
> _2022-11-15_ <br>

| id | Stock Code | Name of Company | Number of Shares Brought |
|----|------------|-----------------|--------------------------|
|  1 |       AAPL |       APPLE INC |                       10 |

# Portfolio Evaluation on a Date:

* It follows the same steps as selecting a portfolio from the list of portfolios.

> ================================== <br>
> What would you like to do today? <br>
> 1.Create a InFlexible PortFolio <br>
> 2.Create a Flexible PortFolio <br>
> 3.Get Portfolio composition <br>
> 4.Get value of Portfolio on a given Date <br>
> 5.Load Flexible Portfolio <br>
> 6.Load InFlexible Portfolio <br>
> 7.Buy/Sell a stock in an portfolio <br>
> 8.Get cost basis of a Portfolio <br>
> 9.Update Commission of the system <br>
> 10.Show Graphical Representation of Portfolio <br>
> 11.Quit <br>
> _4_ <br>

| id | Portfolio Name          | Number of Stocks        | Date of Creation |
| ---|-------------------------|-------------------------|------------------|
|  1 | e                       | 1                       | 2022-10-31       |
|  2 | loaded portfolio        | 4                       | 2022-10-31       | 
|  3 | name                    | 4                       | 2022-10-31       | 
|  4 | temp2                   | 1                       | 2022-10-31       | 
|  5 | new kartik              | 1                       | 2022-10-31       | 
|  6 | kartik loaded portfolio | 4                       | 2022-10-31       | 
|  7 | temp                    | 1                       | 2022-11-02       | 

* User can then select the portfolio id for which the value can be viewed.

> Enter id of the portfolio you want see the value for: <br>
> _2_ <br>
> Enter the date when the stock price needs to displayed <br>
> _2022-10-27_ <br>
>
> We were able to successfully get prices of these stocks

| id | Stock Code |      Name of Company | Number of Shares Brought | Price  |   Value |
|----|------------|----------------------|--------------------------|--------|---------|
|  1 |       ABBV |           ABBVIE INC |                       35 | 153.5  |  5372.5 |
|  2 |          W |          WAYFAIR INC |                       65 | 35.24  |  2290.6 |
|  3 |        AGO | ASSURED GUARANTY LTD |                      123 | 57.04  | 7015.92 |
|  4 |        AGO | ASSURED GUARANTY LTD |                      123 | 57.04  | 7015.92 |

> The total value of the portfolio on 2022-10-27: 21694.94

# Load Inflexible Portfolio from a file:

* In order to retrieve data from the user, we currently restrict them to csv file in a specific
  format.

> ================================== <br>
> What would you like to do today? <br>
> 1.Create a InFlexible PortFolio <br>
> 2.Create a Flexible PortFolio <br>
> 3.Get Portfolio composition <br>
> 4.Get value of Portfolio on a given Date <br>
> 5.Load Flexible Portfolio <br>
> 6.Load InFlexible Portfolio <br>
> 7.Buy/Sell a stock in an portfolio <br>
> 8.Get cost basis of a Portfolio <br>
> 9.Update Commission of the system <br>
> 10.Show Graphical Representation of Portfolio <br>
> 11.Quit <br>
> _5_ <br>
> Please place your file at the path res/user_inflexible.csv <br>
> Please make sure the data in the file is in format:

> |Stock Details|Quantity|Date| <br>
> |AAPL|34|2022-05-12| <br>
> |Carvana Co|87|2022-05-12| <br>

* The format should have Stock Details, Quantity, Date as column header
* Each row should have 3 columns
* Tilde (**|**) is the main separator, each data point needs to be separated by tilde.

> What would you like your portfolio to be called? <br>
> temp <br>
> Portfolio name is invalid or is already used <br>
> custom temp <br>
> Successfully created the portfolio <br>

* The successful output will be stored as portfolio with the given portfolio name given by the
  user

# Load Flexible Portfolio from a file:

* In order to retrieve data from the user, we currently restrict them to csv file in a specific
  format.

> ================================== <br>
> What would you like to do today? <br>
> 1.Create a InFlexible PortFolio <br>
> 2.Create a Flexible PortFolio <br>
> 3.Get Portfolio composition <br>
> 4.Get value of Portfolio on a given Date <br>
> 5.Load Flexible Portfolio <br>
> 6.Load InFlexible Portfolio <br>
> 7.Buy/Sell a stock in an portfolio <br>
> 8.Get cost basis of a Portfolio <br>
> 9.Update Commission of the system <br>
> 10.Show Graphical Representation of Portfolio <br>
> 11.Quit <br>
> _5_ <br>
> Please place your file at the path res/user_flexible.csv <br>
> Please make sure the data in the file is in format:

> StockName|Quantity|TransactionDate|TransactionType <br>
> ABBV|35|2022-04-12|b <br>
> AAPL|100|2022-05-10|b <br>

* The format should have Stock Name, Quantity, Date, Type as column header
* Each row should have 4 columns
* Tilde (**|**) is the main separator, each data point needs to be separated by tilde.

> What would you like your portfolio to be called? <br>
> temp <br>
> Portfolio name is invalid or is already used <br>
> custom temp <br>
> Successfully created the portfolio <br>

* The successful output will be stored as portfolio with the given portfolio name given by the
  user

# Transaction of stocks in Flexible Portfolio:

* This feature walks through the transaction of stocks in the flexible portfolio

> ================================== <br>
> What would you like to do today? <br>
> 1.Create a InFlexible PortFolio <br>
> 2.Create a Flexible PortFolio <br>
> 3.Get Portfolio composition <br>
> 4.Get value of Portfolio on a given Date <br>
> 5.Load Flexible Portfolio <br>
> 6.Load InFlexible Portfolio <br>
> 7.Buy/Sell a stock in an portfolio <br>
> 8.Get cost basis of a Portfolio <br>
> 9.Update Commission of the system <br>
> 10.Show Graphical Representation of Portfolio <br>
> 11.Quit <br>
> _7_ <br>

| id | Portfolio Name          | Number of Stocks        | Date of Creation |
| ---|-------------------------|-------------------------|------------------|
|  1 | e                       | 1                       | 2022-10-31       |
|  2 | loaded portfolio        | 4                       | 2022-10-31       | 
|  3 | name                    | 4                       | 2022-10-31       | 
|  4 | temp2                   | 1                       | 2022-10-31       | 
|  5 | new kartik              | 1                       | 2022-10-31       | 
|  6 | kartik loaded portfolio | 4                       | 2022-10-31       | 
|  7 | temp                    | 1                       | 2022-11-02       | 

* User can then select the portfolio id for which the transaction needs to happen.

> Enter id of the portfolio you edit: <br>
> _2_ <br>
> Enter the name or symbol of the stock : <br>
> _AAPL_ <br>
> Do want to buy or sell the stock? Enter b for buy or s for sell. <br>
> b <br>
> Enter the quantity of stocks you wish to transact? <br>
> 10 <br>
> Enter the date of the transaction(yyyy-mm-dd): <br>
> 2022-11-15 <br>

# Cost Basis of stocks in Flexible Portfolio:

* This feature walks through how to compute cost basis of stocks in the flexible portfolio

> ================================== <br>
> What would you like to do today? <br>
> 1.Create a InFlexible PortFolio <br>
> 2.Create a Flexible PortFolio <br>
> 3.Get Portfolio composition <br>
> 4.Get value of Portfolio on a given Date <br>
> 5.Load Flexible Portfolio <br>
> 6.Load InFlexible Portfolio <br>
> 7.Buy/Sell a stock in an portfolio <br>
> 8.Get cost basis of a Portfolio <br>
> 9.Update Commission of the system <br>
> 10.Show Graphical Representation of Portfolio <br>
> 11.Quit <br>
> _8_ <br>

| id | Portfolio Name          | Number of Stocks        | Date of Creation |
| ---|-------------------------|-------------------------|------------------|
|  1 | e                       | 1                       | 2022-10-31       |
|  2 | loaded portfolio        | 4                       | 2022-10-31       | 
|  3 | name                    | 4                       | 2022-10-31       | 
|  4 | temp2                   | 1                       | 2022-10-31       | 
|  5 | new kartik              | 1                       | 2022-10-31       | 
|  6 | kartik loaded portfolio | 4                       | 2022-10-31       | 
|  7 | temp                    | 1                       | 2022-11-02       | 

* User can then select the portfolio id for which cost basis needs to be computed.

> Enter id of the portfolio you want to see the cost basis for: <br>
> 1 <br>
> Enter the date for which you want to see the cost basis for(yyyy-mm-dd): <br>
> 2022-11-11 <br>
> Cost basis for the portfolio on 2022-11-11 is $5372.5 <br>

# Update Commission :

* This feature walks through on how to update commission as by default the commission is 5.0

> ================================== <br>
> What would you like to do today? <br>
> 1.Create a InFlexible PortFolio <br>
> 2.Create a Flexible PortFolio <br>
> 3.Get Portfolio composition <br>
> 4.Get value of Portfolio on a given Date <br>
> 5.Load Flexible Portfolio <br>
> 6.Load InFlexible Portfolio <br>
> 7.Buy/Sell a stock in an portfolio <br>
> 8.Get cost basis of a Portfolio <br>
> 9.Update Commission of the system <br>
> 10.Show Graphical Representation of Portfolio <br>
> 11.Quit <br>
> _9_ <br>
> Enter the new commission that you want to configure in the system <br>
> -1 <br>
> Commission fee cannot be negative, please enter a valid positive number <br>
> 0 <br>

# Graphical output :

* This feature walks through on how to update commission as by default the commission is 5.0

> ================================== <br>
> What would you like to do today? <br>
> 1.Create a InFlexible PortFolio <br>
> 2.Create a Flexible PortFolio <br>
> 3.Get Portfolio composition <br>
> 4.Get value of Portfolio on a given Date <br>
> 5.Load Flexible Portfolio <br>
> 6.Load InFlexible Portfolio <br>
> 7.Buy/Sell a stock in an portfolio <br>
> 8.Get cost basis of a Portfolio <br>
> 9.Update Commission of the system <br>
> 10.Show Graphical Representation of Portfolio <br>
> 11.Quit <br>
> _10_ <br>

| id | Portfolio Name          | Number of Stocks        | Date of Creation |
| ---|-------------------------|-------------------------|------------------|
|  1 | e                       | 1                       | 2022-10-31       |
|  2 | loaded portfolio        | 4                       | 2022-10-31       | 
|  3 | name                    | 4                       | 2022-10-31       | 
|  4 | temp2                   | 1                       | 2022-10-31       | 
|  5 | new kartik              | 1                       | 2022-10-31       | 
|  6 | kartik loaded portfolio | 4                       | 2022-10-31       | 
|  7 | temp                    | 1                       | 2022-11-02       | 

* User can then select the portfolio id for which the graph mustbe displayed.

> Enter id of the portfolio you want to see the graph for: <br>
> ppp <br>
> Invalid number, please enter a valid portfolio number. <br>
> 3 <br>
> Enter the start date to display the graph <br>
> 2020-01-01 <br>
> Enter the end date to display the graph <br>
> 2022-11-11 <br>
> Q1-2020: [$ 0.0] <br>
> Q2-2020: [$ 0.0] <br>
> Q3-2020: [$ 0.0] <br>
> Q4-2020: [$ 0.0] <br>
> Q1-2021: [$ 0.0] <br>
> Q2-2021: [$ 0.0] <br>
> Q3-2021: [$ 0.0] <br>
> Q4-2021: [$ 0.0] <br>
> Q1-2022: [$ 0.0] <br>
> Q2-2022:*********************************************** [$ 19032.6] <br>
> Q3-2022:********************************************* [$ 18517.35] <br>
> Q4-2022:************************************************** [$ 20225.6] <br>
