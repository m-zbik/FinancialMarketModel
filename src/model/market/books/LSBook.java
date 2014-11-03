package model.market.books;

import model.FinancialModel;

public class LSBook implements OrderBook {

	private static final long serialVersionUID = 1L;

	public FinancialModel myWorld;

	public LSBook() {

	}

	public double price_t = 1;

	// set initial excess demand to zero
	public double excessDemand = 0;

	// set initial return rate to zero
	public double returnRate_t = 0;

	// external signal to update threshold
	public double epsilon_t = 0.5;

	public double pastExcessDemand = 0;

	public void cleanup() {
		// calculate return rate; the excess demand is already known
		// as it is called by agents during order generation phase
		returnRate_t = this.priceImpact(excessDemand / myWorld.agentList.size());
		// calculate new price
		price_t = price_t * Math.exp(returnRate_t);
		// clear out excess demand parameter
		pastExcessDemand = excessDemand/ myWorld.agentList.size();

		excessDemand = 0.0;
		// set shock for next period
		epsilon_t = myWorld.parameterMap.get("D") * myWorld.random.nextGaussian();
	}

	public boolean placeLimitOrder(LimitOrder order) {

		// calculate excess demand; the function is called by agents
		// during the order generation phase

		if (order.type.equals(OrderType.PURCHASE)) {
			excessDemand += order.quantity;
		} else {
			excessDemand -= order.quantity;
		}

		return true;
	}

	public double priceImpact(double d) {
		double h = myWorld.parameterMap.get("LS_h");
		double kappa = myWorld.parameterMap.get("LS_kappa");
		double f = 1;
		double alpha = 2;

		if (myWorld.optionsMap.get("LS_volatility").equalsIgnoreCase("true")) {
			f = 1 + alpha * Math.abs(d);
		}

		return (Math.sqrt(h) * epsilon_t - h / 2) * f + kappa * (d - this.pastExcessDemand);
	}

	public void setMyWorld(FinancialModel myWorld) {
		this.myWorld = myWorld;
		price_t = myWorld.parameterMap.get("initialPrice");
	}

	public double getReturnRate() {
		return returnRate_t;
	}

	public double getAskPrice() {
		return price_t;
	}

	public double getBidPrice() {
		return price_t;
	}

	public double[] getBuyOrders() {
		// TODO Auto-generated method stub
		return null;
	}

	public double[] getSellOrders() {
		// TODO Auto-generated method stub
		return null;
	}

	public double getSpread() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean cancelLimitOrder(LimitOrder order) {
		// TODO Auto-generated method stub
		return false;
	}

	public double executeMarketOrder(OrderType type, int quantity) throws LiquidityException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setMyID(int a) {
		// TODO Auto-generated method stub

	}

	public double getRandomComponent() {
		// TODO Auto-generated method stub
		return epsilon_t;
	}

	public double getVolume() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getAverageTradePrice() {
		// TODO Auto-generated method stub
		return 0;
	}

}
