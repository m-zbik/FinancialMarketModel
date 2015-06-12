package model.market.books;

import model.agents.GenericPlayer;
import model.market.books.OrderBook.OrderType;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

public class LimitOrder implements Comparable {

    // These variables are set when the order is created
	final public OrderType type;
	final public int assetID;
	final public int quantity;
	final public double pricePerUnit;
	final public long entryTime;        // schedule step
	final public double expirationTime; // in schedule time (!=steps)
	final public GenericPlayer investor;
    
	// These variables are modified by the orderbook as the order is executed.
	public AtomicInteger quantityExecuted;
	public AtomicBoolean cancelled;

	static public enum LimitStatus {
		PENDING, // Status while not expired or fully executed
		EXPIRED, // Expired or Cancelled. No units executed.
		PARTIALLY_EXECUTED, // Expired or cancelled, some but not all units executed
		FULLY_EXECUTED	// All units executed
	};

	public LimitOrder(GenericPlayer investor, OrderType type, int asset, double price, int quantity, double expirationTime) 
	{
		this.type = type;
		this.assetID = asset;
		this.quantity = quantity;
		this.pricePerUnit = price;
		this.expirationTime = expirationTime;
		this.entryTime = investor.myWorld.schedule.getSteps();
		this.investor = investor;
		this.quantityExecuted = new AtomicInteger();
		this.quantityExecuted.set(0);
		this.cancelled = new AtomicBoolean();
		this.cancelled.set(false);
	}

	// Get the status of this LimitOrder
	public LimitStatus getStatus() {
		double timeNow = investor.myWorld.schedule.getTime();
		int currentQuantityExecuted = quantityExecuted.get();
		if (quantity == currentQuantityExecuted) {
			return LimitStatus.FULLY_EXECUTED;
		} else if (timeNow > expirationTime || cancelled.get()) {
			if (currentQuantityExecuted != 0) {
				return LimitStatus.PARTIALLY_EXECUTED;
			} else {
				return LimitStatus.EXPIRED;
			}
		} else {
			return LimitStatus.PENDING;
		}
	}

	public int quantityPending() {
		return quantity - quantityExecuted.get();
	}

	public int compareTo(Object arg0) {

		LimitOrder that = (LimitOrder) arg0;
		int retval=0;

		if (this.pricePerUnit==that.pricePerUnit) { 
			// price equal; check time order was placed
			if (this.entryTime > that.entryTime) {
				retval=1;
			} else if (this.entryTime < that.entryTime) {
				retval=-1;
			} // returns default of 0 if equal.
		}
		else 
		{
				retval=1;
				if (this.pricePerUnit > that.pricePerUnit) {
			} else { // (this.pricePerUnit < that.pricePerUnit)
				retval=-1;
			}
			
			// For purchase limitorders, the ordering is reversed
			// since higher priced purchases execute first
			if (this.type == OrderType.PURCHASE) {
			  retval=-retval;
			}

		}
		return retval;
	}
}
