package model.market.books;

public class LiquidityException extends Exception {
	LiquidityException(int qExecd, double totalPrice, int orders, int quantity) {
		quantityExecuted=qExecd;
		this.totalPrice=totalPrice;
		ordersImpatientPlayerOperatesOn = orders; //mzbik added 31.03.2015 to print exception info
		quantityImpatientPlayerExecuteMarketOrder = quantity; //mzbik added 31.03.2015 to print exception info
	}
	public int quantityExecuted; // number actually executed (!=requested)
	public double totalPrice;  // total price of those executed
	public int ordersImpatientPlayerOperatesOn; //mzbik added 31.03.2015 to print exception info
	public int quantityImpatientPlayerExecuteMarketOrder; //mzbik added 31.03.2015 to print exception info
	public static int liquidityExeptionCounter = 1; //mzbik added 31.03.2015 to count the no. of exceptions that occurs
}