package model.agents;

import sim.engine.SimState;
import model.FinancialModel;
import model.market.books.OrderBook.OrderType;
import support.Distributions;

/* Agent that places Market orders according to Farmer's model */

//NOTE: All prices in this model are LOG prices, so negative 
//prices are perfectly acceptable.
public class FarmerImpatientPlayer extends GenericPlayer {

	private Distributions randDist = null;

	private double mu; // Poisson rate: ordered placed per step

	private int sigma; // order size

	public FarmerImpatientPlayer() {

	}

	public void setup(int i, FinancialModel target) {
		// do initialization
		this.myWorld = target;
		this.id = i;

		randDist = new Distributions(myWorld.random);
		mu = myWorld.parameterMap.get("Farmer_mu");
		sigma = myWorld.parameterMap.get("Farmer_sigma").intValue();

		target.schedule.scheduleRepeating(1.0, 1, this, 1.0);
	}

	public void step(SimState state) {

		this.generateOrders();

	}

	private void generateOrders() {

		int ordersPlaced = randDist.nextPoisson(mu);

		// place 'ordersPlaced' orders this iteration, each of size sigma
		for (int i = 0; i < ordersPlaced; i++) {
			OrderType orderType;
			int asset = myWorld.random.nextInt(myWorld.myMarket.orderBooks.size());
			if (myWorld.random.nextBoolean(0.5)) {
				orderType = OrderType.PURCHASE;
			} else {
				orderType = OrderType.SALE;
			}

			myWorld.myMarket.acceptMarketOrder(orderType, asset, sigma);
			// TODO Catch exceptions, manage wealth
		}

	}

}
