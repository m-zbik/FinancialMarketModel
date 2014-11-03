svnMASmm
========

SVN assembla  Financial Market Model on MASON  from http://svn2.assembla.com/svn/MarketModel/ created by Michal Latek

The project was imported from: http://svn2.assembla.com/svn/MarketModel

Below is the description from the Maciej Latek Assembla:

/*********************************************************
Home
-----
Version 25, last updated by MaciejMLatek at 2008-08-19
A Family of Financial Market Models

George Mason University CSS 739 under direction of Dr. Robert Axtell
Overall status:

We have a framework with several financial market models implementations (see paper with overview here). The framework allows for multiple assets and mixing of agents using different behavioral logics. You can download the jar file here and associated property files here. See RunGuide for further info.

The current version includes following graphs:

   1. Bid and ask price history (up to last 10000 observations);
   2. Return and absolute return history (up to last 10000 observations);
   3. Trading volume history (up to last 10000 observations);
   4. Graph of the autocorrelation of both returns and absolute returns (1 to 200 lags);
   5. Trailing average volatility (absolute returns) (up to last 10000 observations, over 365 steps)
   6. Frequency of returns;
   7. Distribution of order book limit orders (if applicable);

Graphs are computationally expensive and are updated only when visible. Best practice is to run the model a while, then pause, 'Show' the graph you're interested in (from the Displays panel), and step the model once. Use 'Hide All' to dismiss them again -- closing the windows using the 'X' will render the graphs irretrievable.

Documentation

   - How to run model: RunGuide;
   - Details of OrderBook specification and implementation;
   - Specification for GenericPlayer and LimitOrder;
   - Document with Cont equivalent STATA graphs for SP500; Excel file with S&P 500 data: 1950-2008
   - Data Summary Document with Cont equivalent STATA graphs for SP500 Daily Quotes, IBM Per Transaction Quotes, and Simulation (Farmer and Cont models) Quotes
   - Data Analysis Slides: comparison between SP500, IBM and "Mason"
   - All datasets have been uploaded FTP server to: wean.css.gmu.edu 




OrderBook
-----
Version 4, last updated by jtbriggs at 2008-05-13

Order books of various kinds may be created to facilitate the trade of a single asset between player/agents.

Specification for the OrderBook interface which each order book must implement:

The interface includes functions to create and cancel LimitOrder and execute market orders, as well as do initialization and bookkeeping

   - double executeMarketOrder(OrderType type, int quantity) throws LiquidityException
   - boolean placeLimitOrder(LimitOrder order)
   - boolean cancelLimitOrder(LimitOrder order)
   - void cleanup() -- this function does periodic bookkeeping and cleanup for the order book
   - void setMyWorld(FinancialModel myWorld);
   - void setMyID(int a);

It also includes functions to get 'snapshot' meta-data about the OrderBook

   - double[] getBuyOrders();  double[] getSellOrders();
   - double getAskPrice(); double getBidPrice(); double getSpread();
   - double getReturnRate();
   - double getReturnRate();
   - double getVolume(); double getAverageTradePrice();
   - double getRandomComponent() -- returns a commonly shared random value computed each 'step'

Remarks:

See definitions of LimitOrder and GenericPlayer.

The players are responsible for doing accounting about the proceeds and costs of buying and selling the assets. Order books have no knowledge of the actual quantity of asset that are outstanding, or whether the Players actually have enough money or assets on hand to realistically complete the transaction. To that extent, the order books act on faith.

At present several order books have been implemented:

   - ContBook, the most basic, handles limit orders, computes the total excess demand and a trade price to clear the market of all trades each step
   - DoubleAuctionOrderBook behaves like a typical stock market. Some (patient) traders place limit orders which specify a quantity and price. Other (impatient) traders place market orders which execute immediately at the market price, provided that there is adequate liquidity provided by outstanding limit orders. As implemented, placing or cancelling a limit order takes log time, placing a market order takes constant time, and cleanup takes linear time, all w.r.t. the number of limit orders pending.
   - LSBook
   



GenericPlayer
-----
Version 2, last updated by jtbriggs at 2008-05-03


All 'Player' agents in this framework derive from GenericPlayer, which is itself a steppable MASON object.

It is a simple construct, but it provides a reference to the FinancialModel it is a part of, and a setup function which is used for construction in the ModelFactory.

Each player, in randomized order, has its step() function called each iteration of the model.

Current Players deriving from GenericPlayer are:

   - ContPlayer
   - FarmerImpatienPlayer
   - FarmerPatientPlayer
   - FCImpatientPlayer (for Farmer-Cont)
   - LSPlayer




LimitOrder
-----
Version 1, last updated by jtbriggs at 2008-05-03

LimitOrders are, for our purposes, Buy or Sell orders that do not (necessarily) execute immediately. MarketOrders are their counterpart which do execute immediately.

They are created and managed by Players. But after they are submitted to an OrderBook for execution, only the orderbook may modify them. The Player retains responsibility for checking on their execution status.

Within the context of the DoubleAuctionOrderBook, they are LimitOrders in the normal sense as found on stock markets, where a price, quantity, and possibly an expiration are specified, and it will execute if market conditions make it feasible before expiration.


Specification for LimitOrder class:

public class LimitOrder

These values are set at creation by the player:

   - Enumeration type (Purchase, Sale)
   - int assetID -- correlated 1-1 with the orderbook
   - GenericPlayer investor -- owner of the LimitOrder
   - int quantity -- requested quantity to buy or sell
   - double pricePerUnit -- price at which the investor wishes to transact
   - double expirationTime -- schedule time the order will expire (implicitly)
   - Enumeration getStatus() (Expired, Finalized, Outstanding);

 These values are modified by the OrderBook upon execution, expiration, or cancellation

   - AtomicInteger quantityExecuted -- the quantity that have been executed to date
   - AtomicInteger cancelled -- whether the order has been explicitly cancelled by the investor via the OrderBook 

At any time, the player/investor may check the status of his orders (to do accounting for example)

   - LimitStatus getStatus() where LimitStatus is one of (Pending, Expired, Partially_Executed, Fully_Executed).




RunGuide
-----
Version 3, last updated by jtbriggs at 2008-05-03
Guide to installing and running FinancialModel

 There are two ways to get the project. The simpler way is:

   1. Download FinancialMarket.jar and setups.zip from the Files tab of this site.
   2. Unpack setups.zip and place the setups folder next to the jar file.

Alternatively, if you want to be able to revise or extend the models you can:

   1. Check them out using Subversion from http://svn2.assembla.com/svn/MarketModel
   2. Build them in Eclipse for Java.

Subversion and Eclipse are fully documented on the web.

 

Next, you need to configure some files to set the parameters to the models.

   - In setups folder, you should see following files:
       1. main.properties: a configuration file describing what simulation you will run. In particular, by changing values of orderbookClass and agentConfiguration parameters, you can choose the type of orderbook and parameters for the models. Example main.properties files (ie, main_cont.properties) are included for reference, but it is 'main.properties' that counts. Not all order books work with all agents.
       2. *.txt: these files contain data on composition of population of players and are included via the 'agentConfiguration' option in main.properties. Entries should be formatted as playerClass,numOfInstances with single line per playerClass. You can have as many lines (or different types of agents) in you simulation as required.

 

Finally, you're ready to run the model.

   - If you downloaded the jar file, doubleclick the jar file (or use java -Xmx1024M -jar FinancialModel.jar command) to start the simulation.
   - If you're using eclipse, go to src/gui and run FinancialModelWithUI.java as a Java Application.

All Mason simulations have common interface. You can refer to following slides for explanation of control window that should appear.


**********************************************************/



