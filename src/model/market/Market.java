package model.market;

import java.util.ArrayList;

import model.FinancialModel;
import model.agents.FarmerImpatientPlayer;
import model.market.books.LimitOrder;
import model.market.books.LiquidityException;
import model.market.books.OrderBook;
import model.market.books.OrderBook.OrderType;
import sim.engine.SimState;
import sim.engine.Steppable;

/* Class: Market 
 * Spring 2008
 * FinancialMarketModel Team
 * 
 * Function: generates the market price based on 
 * the orders placed by traders 
 */

public class Market implements Steppable {

	public FinancialModel myWorld;

	// ArrayList of orderBooks (one for each of the assets)
	public ArrayList<OrderBook> orderBooks = new ArrayList<OrderBook>();

	// constructor
	public Market(FinancialModel myWorld) {
		this.myWorld = myWorld;
/*
		for (int o = 0; o < myWorld.parameterMap.get("numAssets"); o++) {

		}
*/

	}

	// function contains the main business logic of the class
	// generates return rate and new price
	public void step(SimState state) {
		for (OrderBook b : this.orderBooks) {
			b.cleanup();
		}
	}

	public double getReturnRateForAsset(int i) {
		return this.orderBooks.get(i).getReturnRate();
	}

	public OrderBook getOrderBookForAsset(int i) {
		return orderBooks.get(i);
	}

	
	public double getAskPriceForAsset(int i) {
		return orderBooks.get(i).getAskPrice();
	}

	public boolean acceptOrder(LimitOrder tempOrder) {
		return orderBooks.get(tempOrder.assetID).placeLimitOrder(tempOrder);		
	}

	public boolean cancelOrder(LimitOrder tempOrder) {
		return orderBooks.get(tempOrder.assetID).cancelLimitOrder(tempOrder);		
	}
	
	
	public double getBidPriceForAsset(int i) {
		return orderBooks.get(i).getBidPrice();
	}
	
	public void acceptMarketOrder(OrderType newType, int asset, int amount, String nameOfPlayersType) { //mzbik added 31.03.2015 String nameOfPlayersType to print exception
		try {
			this.orderBooks.get(asset).executeMarketOrder(newType, amount);
		} catch (LiquidityException e) {
			// TODO: this should be left for the caller to catch
			// e.printStackTrace(); //mzbik changed 31.03.2015

			System.out.println("+---There is a Liquidity problem no. " + LiquidityException.liquidityExeptionCounter ); //mzbik added 31.03.2015
			System.out.println("quantity = " + e.quantityPlayerExecuteMarketOrder + 
					"; orders = " + e.ordersPlayerOperatesOn); //mzbik added 31.03.2015
			System.out.println("Type of the player: " + nameOfPlayersType); //mzbik added 31.03.2015
			System.out.println("+---"); //mzbik added 31.03.2015
			LiquidityException.liquidityExeptionCounter ++; //mzbik added 31.03.2015
		}
	}

	public double getRandomComponentForAsset(int i) {
		return orderBooks.get(i).getRandomComponent();
	}

	public double getAverageTradePriceForAsset(int i) {
		return orderBooks.get(i).getAverageTradePrice();
	}

	public double getVolumeForAsset(int i) {
		return orderBooks.get(i).getVolume();
	}

}
