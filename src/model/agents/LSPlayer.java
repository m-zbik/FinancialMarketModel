package model.agents;

import model.FinancialModel;
import model.market.books.LSBook;
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

public class LSPlayer extends GenericPlayer {

	private static final long serialVersionUID = 1L;

	// trader's threshold
	public double L_i;

	public double U_i;

	public double P_i;

	public double C_i;

	public boolean shortPosition = true;

	public double s_local;

	// constructor
	public LSPlayer() {

	}

	// main logic function; not really used in this case
	public void step(SimState state) {

		if (state.schedule.getTime() == 0) {

			this.s_local = myWorld.parameterMap.get("Cont_s");

			// assign a random threshold; values range from 0 to 1
			this.P_i = myWorld.myMarket.getAskPriceForAsset(0);
			this.L_i = 0.1 + 0.2 * myWorld.random.nextDouble();
			this.U_i = 1 + 0.1 + 0.2 * myWorld.random.nextDouble();
			this.C_i = 100* myWorld.random.nextDouble();

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

		// if epsilon_t is below the negative value of threshold
		// issue an order to sell
		double currentAskPrice = this.myWorld.myMarket.getAskPriceForAsset(0);
		if (this.shortPosition) {
			tempOrder = new LimitOrder(this, OrderType.SALE, 0, currentAskPrice, 1, 1);

		} else {
			tempOrder = new LimitOrder(this, OrderType.PURCHASE, 0, currentAskPrice, 1, 1);

		}
		this.myWorld.myMarket.acceptOrder(tempOrder);

		// pass the order value to the market agent

	}

	// update thresholds for 's' fraction of traders
	public void updateThresholds() {

		// update threshold if a random value u_i_t is below

		if ((myWorld.myMarket.getAskPriceForAsset(0) < this.L_i * this.P_i) || (myWorld.myMarket.getAskPriceForAsset(0) > this.U_i * this.P_i)) {

			this.P_i = myWorld.myMarket.getAskPriceForAsset(0);
			this.L_i = 0.1 + 0.2 * myWorld.random.nextDouble();
			this.U_i = 1 + 0.1 + 0.2 * myWorld.random.nextDouble();
			if (this.shortPosition) {
				this.shortPosition = false;
			} else {
				this.shortPosition = true;
			}

			if (myWorld.optionsMap.get("LS_herding").equalsIgnoreCase("true")) {

				LSBook myBook = (LSBook) myWorld.myMarket.orderBooks.get(0);

				if (((myBook.pastExcessDemand < 0) && this.shortPosition) || ((myBook.pastExcessDemand > 0) && !this.shortPosition)) {
					this.L_i = this.L_i + myWorld.parameterMap.get("LS_h") * this.C_i * Math.abs(myBook.pastExcessDemand);
					this.U_i = this.U_i - myWorld.parameterMap.get("LS_h") * this.C_i * Math.abs(myBook.pastExcessDemand);
				}

			}

		}
	}

}
