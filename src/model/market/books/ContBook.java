package model.market.books;

import model.FinancialModel;

public class ContBook implements OrderBook {

	private static final long serialVersionUID = 1L;

	public FinancialModel myWorld;

	public ContBook() {

	}

	public double price_t = 1;

	// set initial excess demand to zero
	public double excessDemand = 0;

	// set initial return rate to zero
	public double returnRate_t = 0;
	
	// external signal to update threshold
	public double epsilon_t = 0.1;
	
	public double currentDemand = 0;
	
	public double currentSupply = 0;
	
	public double oldVolume = 0;

	public double averageTradePrice = 0;

	public void cleanup() {
		

		oldVolume = Math.max(currentDemand, currentSupply);
		
		averageTradePrice = oldVolume*price_t;
		
		// calculate return rate; the excess demand is already known
		// as it is called by agents during order generation phase
		returnRate_t = this.priceImpact(excessDemand / myWorld.agentList.size());
		// calculate new price
		price_t = price_t * Math.exp(returnRate_t);
		// clear out excess demand parameter
		excessDemand = 0.0;
		// set shock for next period
		epsilon_t = myWorld.parameterMap.get("D") * myWorld.random.nextGaussian();
		
		currentDemand = 0;
		
		currentSupply = 0;
	}



	public boolean placeLimitOrder(LimitOrder order) {
	
		// calculate excess demand; the function is called by agents
		// during the order generation phase

		
		if (order.type.equals(OrderType.PURCHASE)) {
			excessDemand += order.quantity;
			currentDemand++;
		} else {
			excessDemand -= order.quantity;
			currentSupply++;
		}
		
		return true;
	}

	

	public double priceImpact(double d) {
		return Math.atan(d / myWorld.parameterMap.get("Cont_lambda"));
		// return Math.atan2(d, myWorld.lambda);
		//return d / myWorld.parameterMap.get("Cont_lambda");
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
		return this.oldVolume;
	}



	public double getAverageTradePrice() {
		return  this.averageTradePrice;
	}
	
	

}
