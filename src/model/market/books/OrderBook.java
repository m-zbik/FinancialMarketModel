/**
 * 
 */
package model.market.books;

import model.FinancialModel;

import org.jfree.data.xy.XYSeries;

/**
 * @author jbriggs
 * 
 */
public interface OrderBook {

	static public enum OrderType {
		PURCHASE, SALE
	}

	// Place an order for immediate execution
	// Returns total transaction value for 'quantity' units, if successful.
	// Otherwise, throws LiquidityException, which contains the number
	// successfully executed.
	public double executeMarketOrder(OrderType type, int quantity) throws LiquidityException;

	// Place a limit order for a particular price/quantity/expirationTime
	// returns true if order was successfully placed (-not- executed) and false
	// otherwise
	public boolean placeLimitOrder(LimitOrder order);

	// Attempt to cancel a given limit order.
	// Returns true on successful cancellation with no units transacted.
	// Returns false otherwise; final status can be found in order.
	public boolean cancelLimitOrder(LimitOrder order);

	// Registers two series to be updated with the distribution of open limit
	// orders
	/*
	 * public boolean registerGUISeries(XYSeries BuyOrdersSeries, XYSeries
	 * SellOrdersSeries);
	 */
	// Intended to be called once each time step to do final work with orders
	// (including purging expired ones)
	public void cleanup();

	// Used to support drawing histograms. Returns a list of prices for each
	// unit of pending limit orders.
	// Should be called after a Cleanup().
	public double[] getBuyOrders();

	public double[] getSellOrders();

	// The current best (lowest) price offered by sellers
	public double getAskPrice();

	// The current best (highest) price offered by buyers
	public double getBidPrice();

	// The gap between the best price ask and bid
	public double getSpread();
	
	// The return on the asset over the last 'tick'.
	// Different orderbooks calculate it differently
	public double getReturnRate();

	public void setMyWorld(FinancialModel myWorld);

	public void setMyID(int a);

	public double getRandomComponent();
	
	public double getVolume();
	
	public double getAverageTradePrice();
	
}
