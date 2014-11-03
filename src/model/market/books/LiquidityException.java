package model.market.books;

public class LiquidityException extends Exception {
	LiquidityException(int qExecd, double totalPrice) {
		quantityExecuted=qExecd;
		this.totalPrice=totalPrice;
	}
	public int quantityExecuted; // number actually executed (!=requested)
	public double totalPrice;  // total price of those executed	
}
