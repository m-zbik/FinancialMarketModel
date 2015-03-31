/**
 *  DoubleAuctionOrderBook implementation
 */
package model.market.books;

import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import model.FinancialModel;

/**
 * @author jbriggs Implements a double auction order book. The market moves when
 *         market orders fulfill limit orders. - limit orders don't ever execute
 *         when they're placed
 * 
 */
public class DoubleAuctionOrderBook implements OrderBook {

	protected FinancialModel myWorld;

	protected SortedSet<LimitOrder> buyOrders;

	protected SortedSet<LimitOrder> sellOrders;

	protected int myID;

	public double returnRate_t = 0;

	public double price_t = 1;

	// set to true if price as stored in the order book are the log of actual
	// price;
	// this implies negative prices are perfectly acceptable
	public boolean logPricing = false;

	public double volumeTraded = 0;
	public double valueTraded = 0;
	public double oldVolumeTraded = 0;
	public double oldValueTraded = 0;
	
    public double epsilon_t = 0.01;

	public DoubleAuctionOrderBook() {
		super();

		this.buyOrders = new TreeSet<LimitOrder>();
		this.sellOrders = new TreeSet<LimitOrder>();
	}

	public synchronized boolean placeLimitOrder(LimitOrder order) {
		if (order.quantity <= 0 || order.assetID != myID) {
			return false;
		}

		// give it a new, unique transaction ID within the orderbook
		// order.transactionID.set(nextTransactionID++);

		if (order.type == OrderType.PURCHASE) {
			buyOrders.add(order);
		} else {
			sellOrders.add(order);
		}

		return true;
	}

	// Attempt to cancel a given limit order.
	// Returns true on successful cancellation with no units transacted.
	// Returns false otherwise; final status can be found in order.
	public synchronized boolean cancelLimitOrder(LimitOrder order) {

		order.cancelled.set(true);
		if (order.type == OrderType.PURCHASE) {
			buyOrders.remove(order);
		} else {
			sellOrders.remove(order);
		}

		return true;
	}

	// Returns total price of purchasing 'quantity' units if successful.
	// Otherwise, throws LiquidityException, which contains the number
	// successfully executed.
	public synchronized double executeMarketOrder(OrderType type, int quantity) throws LiquidityException {
		double currentTime = myWorld.schedule.getTime();
		int origQuant = quantity;
		double totalPrice = 0.0;

		// The operation is the same regardless of whether we're buying or
		// selling. We just need to choose the right orders to work on.
		SortedSet<LimitOrder> orders;
		if (type == OrderType.PURCHASE) {
			orders = sellOrders;
		} else {
			orders = buyOrders;
		}

		while ((quantity > 0) && (!orders.isEmpty())) {

			LimitOrder lo = orders.first();

			// ensure this order hasn't expired
			if (lo.expirationTime < currentTime) {
				continue;
			}

			int curQuantity = 0;
			if (lo.quantityPending() >= quantity) {
				curQuantity = quantity;
			} else {
				curQuantity = quantity - lo.quantityPending();
			}
			quantity -= curQuantity;

			// Execute:
			// Update the LimitOrder by adding to the quantityExecuted
			lo.quantityExecuted.addAndGet(curQuantity);
			// Update the market order
			totalPrice += lo.pricePerUnit * curQuantity;
            // Update aggregate value/volume traded
			this.valueTraded += lo.pricePerUnit * curQuantity;
			this.volumeTraded += curQuantity;

			if (lo.quantityPending() == 0) {
				// the limitOrder is fully executed; remove it from the pending
				// orders
				orders.remove(lo);
			}
		}

		if (quantity > 0) { //mzbik added 31.03.2015 order.size() and quantity to print exception info
			throw new LiquidityException((int) (origQuant - quantity), 
					totalPrice, orders.size(), quantity);
		}

		return totalPrice;
	}

	public synchronized double getBidPrice() {

		if (buyOrders.isEmpty()) {
			// TODO: this should probably change to price_t
			//return myWorld.parameterMap.get("initialPrice");
			return price_t;
		} else {
			return buyOrders.first().pricePerUnit;
		}

	}

	public synchronized double getAskPrice() {

		if (sellOrders.isEmpty()) {
			// TODO: this should probably change to price_t
			//return myWorld.parameterMap.get("initialPrice");
			return price_t;
		} else {
			return sellOrders.first().pricePerUnit;
		}

	}

	public synchronized double getSpread() {
		return this.getAskPrice() - this.getBidPrice();
	}

	public synchronized void cleanup() {

		/* Clean expired orders */

		HashSet<LimitOrder> ordersToRemove = new HashSet<LimitOrder>();
		double currentTime = myWorld.schedule.getTime();

		for (LimitOrder l : this.sellOrders) {
			if (l.expirationTime <= currentTime) {
				ordersToRemove.add(l);
			}
		}
		sellOrders.removeAll(ordersToRemove);
		ordersToRemove.clear();

		for (LimitOrder l : this.buyOrders) {
			if (l.expirationTime <= currentTime) {
				ordersToRemove.add(l);
			}
		}
		buyOrders.removeAll(ordersToRemove);

		/* Clean up negative spreads by trading overlapping LimitOrders */
		while ((this.getSpread() <= 0.0) && (buyOrders.size() > 0) && (sellOrders.size() > 0)) {

			LimitOrder firstBuy = buyOrders.first();
			LimitOrder firstSell = sellOrders.first();

			int curQuantity = Math.min(firstBuy.quantityPending(), firstSell.quantityPending());
			firstBuy.quantityExecuted.addAndGet(curQuantity);
			firstSell.quantityExecuted.addAndGet(curQuantity);

			this.volumeTraded += curQuantity;
			this.valueTraded += curQuantity * (firstBuy.pricePerUnit + firstSell.pricePerUnit) * 0.5;

			// NB: Any difference in prices is profit for the exchange :)

			if (firstBuy.quantityPending() == 0) {
				buyOrders.remove(firstBuy);
			}

			if (firstSell.quantityPending() == 0) {
				sellOrders.remove(firstSell);
			}

		}

		/* Calculate return rate */
		double newPrice_t = (getAskPrice() + getBidPrice()) / 2;
		if (logPricing) {
			returnRate_t = newPrice_t - price_t;
		} else {
			// note we must be careful here to use this only
			// when price_t cannot be 0, and the ratio can only be positive.
			returnRate_t = Math.log(newPrice_t / price_t);
		}

		price_t = newPrice_t;
		this.oldValueTraded= this.valueTraded;
		this.valueTraded = 0;
		this.oldVolumeTraded = this.volumeTraded;
		this.volumeTraded = 0;

		// set shock for next period
		epsilon_t = myWorld.parameterMap.get("D") * myWorld.random.nextGaussian();
	}

	// returns an array with an entry of the price for each unit of limit order
	public double[] getBuyOrders() {
		Vector<Double> freqVec = new Vector<Double>();

		// Iterate through limit order queues from askPrice on up
		for (LimitOrder l : buyOrders) {

			// theoretically this should be quantityPending
			for (int i = 0; i < l.quantity; i++) {
				freqVec.add(l.pricePerUnit);
			}

		}

		double[] retArray = new double[freqVec.size()];
		for (int i = 0; i < freqVec.size(); i++) {
			retArray[i] = freqVec.get(i);
		}

		return retArray;
	}

	// returns an array with an entry of the price for each unit of each limit
	// order
	public double[] getSellOrders() {
		Vector<Double> freqVec = new Vector<Double>();

		// Iterate through limit order queues from askPrice on up
		for (LimitOrder l : sellOrders) {

			// theoretically this should be quantityPending
			for (int i = 0; i < l.quantity; i++) {
				freqVec.add(l.pricePerUnit);
			}

		}

		double[] retArray = new double[freqVec.size()];
		for (int i = 0; i < freqVec.size(); i++) {
			retArray[i] = freqVec.get(i);
		}
		return retArray;
	}

	public synchronized void setMyWorld(FinancialModel myWorld) {
		this.myWorld = myWorld;
		price_t = myWorld.parameterMap.get("initialPrice");
		if (this.myWorld.optionsMap.get("orderBookOptions").equalsIgnoreCase("logPricing")) {
			logPricing = true;
		}
	}

	public double getReturnRate() {
		return returnRate_t;
	}

	public double getTickPrice() {
		return price_t;
	}

	public void setMyID(int a) {
		this.myID = a;
	}

	public double getRandomComponent() {
		return epsilon_t;
	}

	public double getVolume() {
		return this.oldVolumeTraded;
	}

	public double getAverageTradePrice() {
		return this.oldValueTraded / (this.oldVolumeTraded + 1);
	}
}
