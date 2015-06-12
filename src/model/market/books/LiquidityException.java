package model.market.books;

public class LiquidityException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	LiquidityException(int qExecd, double totalPrice, int orders, int quantity) {
		quantityExecuted = qExecd;
		this.totalPrice = totalPrice;
		ordersPlayerOperatesOn = orders; //mzbik added 31.03.2015 to print exception info
		quantityPlayerExecuteMarketOrder = quantity; //mzbik added 31.03.2015 to print exception info
	}
	public int quantityExecuted; // number actually executed (!=requested)
	public double totalPrice;  // total price of those executed
	public int ordersPlayerOperatesOn; //mzbik added 31.03.2015 to print exception info
	public int quantityPlayerExecuteMarketOrder; //mzbik added 31.03.2015 to print exception info
	public static int liquidityExeptionCounter = 1; //mzbik added 31.03.2015 to count the no. of exceptions that occurs
}