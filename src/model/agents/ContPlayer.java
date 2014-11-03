package model.agents;

import model.FinancialModel;
import model.market.books.LimitOrder;
import model.market.books.OrderBook.OrderType;
import sim.engine.SimState;
import sim.engine.Steppable;

/* Class: Player 
 * Spring 2008
 * FinancialMarketModel Team
 * 
 * Function: trader agent; generates orders and updates thresholds 
 */

public class ContPlayer extends GenericPlayer {

	private static final long serialVersionUID = 1L;

	// trader's threshold
	public double threshold;

	public double s_local;

	// constructor
	public ContPlayer() {

	}

	// main logic function; not really used in this case
	public void step(SimState state) {

		if (state.schedule.getTime() == 0) {

			this.s_local = myWorld.parameterMap.get("Cont_s");
			// assign a random threshold; values range from 0 to 1
			this.threshold = myWorld.parameterMap.get("D") * myWorld.random.nextDouble();
		}

		if (state.schedule.getTime() > 0) {

			this.updateThresholds();

		}

		this.generateOrders();

	}

	// generate an order if trader's threshold is above or below
	// a market wide parameter epsilon_t
	public void generateOrders() {

		LimitOrder tempOrder;

		double epsilon_t = myWorld.myMarket.getRandomComponentForAsset(0);

		// if epsilon_t is below the negative value of threshold
		// issue an order to sell
		if (epsilon_t < -1 * this.threshold) {
			double currentAskPrice = this.myWorld.myMarket.getAskPriceForAsset(0);
			tempOrder = new LimitOrder(this, OrderType.SALE, 0, currentAskPrice, 1, 1);
			this.myWorld.myMarket.acceptOrder(tempOrder);

			// if epsilon_t is above the positive value of threshold
			// issue an order to buy
		} else if (epsilon_t > this.threshold) {

			double currentBidPrice = this.myWorld.myMarket.getBidPriceForAsset(0);
			tempOrder = new LimitOrder(this, OrderType.PURCHASE, 0, currentBidPrice, 1, 1);
			this.myWorld.myMarket.acceptOrder(tempOrder);

		} else {
			// otherwise do nothing
		}

		// pass the order value to the market agent

	}

	// update thresholds for 's' fraction of traders
	public void updateThresholds() {
		// generate a random value between 0 and 1 from uniform distribution
		double u_i_t = myWorld.random.nextDouble();
		// get the current return rate from the market agent
		double r_t = myWorld.myMarket.getReturnRateForAsset(0);

		// update threshold if a random value u_i_t is below
		// the given update frequency 's'
		if (u_i_t < s_local) {
			// set a new threshold
			this.threshold = Math.abs(r_t);
		}
	}

}
