package model.agents;

import sim.engine.SimState;
import model.FinancialModel;
import model.market.books.LimitOrder;
import model.market.books.OrderBook.OrderType;
import support.Distributions;
import java.util.ArrayList;

/* Agent that places limit orders according to Farmer's Model */

// NOTE: All prices in this model are LOG prices
public class ConstrainedFarmerPlayer extends GenericPlayer {

	private Distributions randDist = null;

	private double alpha; // Poisson rate: orders placed per step

	private double delta; // Poisson rate: orders cancelled per step

	private int sigma; // order size (constant)

	ArrayList<LimitOrder> myOrders;

	public ConstrainedFarmerPlayer() {
		myOrders = new ArrayList<LimitOrder>();
	}

	public void setup(int i, FinancialModel target) {
		// do initialization
		this.myWorld = target;
		this.id = i;

		randDist = new Distributions(myWorld.random);
		alpha = myWorld.parameterMap.get("Farmer_alpha");
		delta = myWorld.parameterMap.get("Farmer_delta");
		sigma = myWorld.parameterMap.get("Farmer_sigma").intValue();

		target.schedule.scheduleRepeating(0.0, 1, this, 1.0);
	}

	public void step(SimState state) {

		this.cancelOldOrders();
		this.generateOrders();
		this.manageOrderExecution();

	}

	private void generateOrders() {

		int numOrdersPlaced = randDist.nextPoisson(alpha);

		for (int i = 0; i < numOrdersPlaced; i++) {

			OrderType newType;
			double price = 0.0;
			int asset = myWorld.random.nextInt(myWorld.myMarket.orderBooks.size());

			if (myWorld.random.nextBoolean(0.5)) {
				// Make a purchase limit order
				newType = OrderType.PURCHASE;

				double ask = myWorld.myMarket.orderBooks.get(asset).getAskPrice();
				// Exponential distribution: Log prices at "uniform intensity"
				double offset = randDist.nextExponential(0.9);

				price = ask - Math.log(offset + 1.0);

			} else {
				// Make a sale limit order
				newType = OrderType.SALE;

				double bid = myWorld.myMarket.orderBooks.get(asset).getBidPrice();
				// Exponential distribution: Log prices at "uniform intensity"
				double offset = randDist.nextExponential(0.9);

				price = bid + Math.log(offset + 1.0);
			}

			double expirationTime = myWorld.schedule.getTime() + 10000.0;
			LimitOrder newOrder = new LimitOrder(this, newType, asset, price, sigma/* quantity */, expirationTime);

			if (myWorld.myMarket.acceptOrder(newOrder)) {
				myOrders.add(newOrder);
			} // TODO else?
		}

	}

	private void cancelOldOrders() {

		if (myOrders.isEmpty())
			return;
		// find how many orders to cancel according to a poisson process
		int numOrdersCancelled = 0;
		for (int i = 0; i < myOrders.size(); i++) {
			numOrdersCancelled += randDist.nextPoisson(delta);
		}

		for (int i = 0; i < numOrdersCancelled && !myOrders.isEmpty(); i++) {
			int indexToCancel = myWorld.random.nextInt(myOrders.size());
			LimitOrder lo = myOrders.get(indexToCancel);

			if (myWorld.myMarket.cancelOrder(lo)) {
				myOrders.remove(indexToCancel);
				// TODO: Track earnings/losses
			} // TODO else?
		}

	}

	private void manageOrderExecution() {
		ArrayList<LimitOrder> ordersToRemove = new ArrayList<LimitOrder>();
		for (LimitOrder lo : myOrders) {
			LimitOrder.LimitStatus status = lo.getStatus();
			if (status != LimitOrder.LimitStatus.PENDING) {
				ordersToRemove.add(lo);
				// TODO: Track earnings/losses
			}
		}
		myOrders.removeAll(ordersToRemove);
	}

}
