package model.agents;

import sim.engine.SimState;
import model.FinancialModel;
import model.market.books.LimitOrder;
import model.market.books.OrderBook.OrderType;
import support.Distributions;

/* Agent that places Market orders */
/* This agent is an amalgam of Cont and Farmer players 
 * It uses a signal ala Cont to decide when to trade and on which side.
 * And it uses a double auction to place orders.
 * 
 * The biggest concern for making this model work is to ensure that adequate 
 * liquidity is available to execute.
 */

public class FCImpatientPlayer extends GenericPlayer {

//	private Distributions randDist=null;

//	private double mu; // Poisson rate: ordered placed per step
	private int sigma; // order size

	private double s; // frequency of updating threshold
	private double threshold; // the threshold for buying or selling
	
	private String namyOfPlayersType = this.getClass().getSimpleName(); //mzbik added 31.03.2015 to print Limit Exception
	
	public FCImpatientPlayer() {

	}

	public void setup(int i, FinancialModel target) {
		// do initialization
		this.myWorld = target;
		this.id = i;
		
//		randDist = new Distributions(myWorld.random);
//		mu=myWorld.parameterMap.get("Farmer_mu");
		sigma=myWorld.parameterMap.get("Farmer_sigma").intValue();

		s = myWorld.parameterMap.get("Cont_s");
		
		// assign a random threshold; values range from 0 to 1
		threshold = myWorld.random.nextDouble();

		target.schedule.scheduleRepeating(1.0, 1, this, 1.0);
	}
	
	public void step(SimState state) {

		this.generateOrders();
		
		this.updateThresholds();

	}

	private void generateOrders() {

		int asset = 0; //myWorld.random.nextInt(myWorld.myMarket.orderBooks.size());
		double epsilon_t = myWorld.myMarket.getRandomComponentForAsset(asset);

		if (epsilon_t > this.threshold) {
			myWorld.myMarket.acceptMarketOrder(OrderType.PURCHASE, asset, sigma, namyOfPlayersType); //mzbik 31.03.2015 added namyOfPlayersType to print Liquidity exception);
		} else if (epsilon_t < -this.threshold) {
		    myWorld.myMarket.acceptMarketOrder(OrderType.SALE, asset, sigma, namyOfPlayersType); //mzbik 31.03.2015 added namyOfPlayersType to print Liquidity exception);
		}
	    // TODO Catch exceptions, manage wealth

	}
	
	// update thresholds for 's' fraction of traders
	public void updateThresholds() {
		// update threshold with probability 's'
		if (myWorld.random.nextBoolean(s)) {
			// set a new threshold
		  this.threshold = Math.abs(myWorld.myMarket.getReturnRateForAsset(0));
		}
	}


}
