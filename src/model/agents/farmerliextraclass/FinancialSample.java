/**
 * Introduction to Neural Networks with Java, 2nd Edition
 * Copyright 2008 by Heaton Research, Inc. 
 * http://www.heatonresearch.com/books/java-neural-2/
 * 
 * ISBN13: 978-1-60439-008-7  	 
 * ISBN:   1-60439-008-5
 *   
 * This class is released under the:
 * GNU Lesser General Public License (LGPL)
 * http://www.gnu.org/copyleft/lesser.html
 */
package model.agents.farmerliextraclass;

import java.text.NumberFormat;
//import java.util.Date;

import com.heatonresearch.book.introneuralnet.common.ReadCSV;

/**
 * Chapter 10: Application to the Financial Markets
 * 
 * FinancialSample: Holds a sample of financial data at the specified
 * date.  This includes the close of the SP500 and the prime interest
 * rate.
 * 
 * @author Jeff Heaton
 * @version 2.1
 */
public class FinancialSample implements Comparable<FinancialSample> {
	private double returnTimeSeries;
	private double rate;
	private double timeTimeSeries;
	private double volumeTimeSeries;
	private double percent;

	public int compareTo(final FinancialSample other) {
		return new Double(getTimeTimeSeries()).compareTo(other.getTimeTimeSeries());
	}

	/**
	 * @return the return from TimeSeries (changed from getAmount)
	 */
	public double getReturnTimeSeries() {
		return this.returnTimeSeries;
	}

	/**
	 * @return the time from TimeSeries (changed from getData)
	 */
	public double getTimeTimeSeries() {
		return this.timeTimeSeries;
	}

	/**
	 * @return the percent
	 */
	public double getPercent() {
		return this.percent;
	}

	/**
	 * @return the rate
	 */
	public double getRate() {
		return this.rate;
	}
	
	/**
	 * @return the volume
	 */
	public double getVolumeTimeSeries() {
		return this.volumeTimeSeries;
	}

	/**
	 * @param set return 
	 * 				from TimeSeries (changed from setAmount)
	 */
	public void setReturnTimeSeries(final double returnTimeSeries) {
		this.returnTimeSeries = returnTimeSeries;
	}

	/**
	 * @param set time from TimeSeries (changed from setDate)
	 */
	public void setTimeTimeSeries(final double timeTimeSeries) {
		this.timeTimeSeries = timeTimeSeries;
	}

	/**
	 * @param percent
	 *            the percent to set
	 */
	public void setPercent(final double percent) {
		this.percent = percent;
	}

	/**
	 * @param rate
	 *            the rate to set
	 */
	public void setRate(final double rate) {
		this.rate = rate;
	}
	
	/**
	 * @param volume
	 *            the volume to set
	 */
	public void setVolumeTimeSeries(final double volumeTimeSeries) {
		this.volumeTimeSeries = volumeTimeSeries;
	}

	@Override
	public String toString() {
		final NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		final StringBuilder result = new StringBuilder();
		result.append("time: ");
		result.append(this.timeTimeSeries);
		result.append(", return: ");
		result.append(this.returnTimeSeries);
		result.append(", volume: ");
		result.append(this.volumeTimeSeries);
		result.append(", Prime Rate: ");
		result.append(this.rate);
		result.append(", Percent from Previous: ");
		result.append(nf.format(this.percent));

		
		return result.toString();
	}

}
