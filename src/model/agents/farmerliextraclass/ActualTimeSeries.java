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

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import support.Reporter;

import com.heatonresearch.book.introneuralnet.common.ReadCSV;

/**
 * Chapter 10: Application to the Financial Markets
 * 
 * SP500Actual: Holds actual SP500 data and prime interest rates.
 *  
 * @author Jeff Heaton
 * @version 2.1
 */
public class ActualTimeSeries {

/*	private final Set<InterestRate> rates = new TreeSet<InterestRate>();*/
	private /*final*/ Set<FinancialSample> samples = new TreeSet<FinancialSample>();
	private final int inputSize;
	private final int outputSize;

	public ActualTimeSeries(final int inputSize, final int outputSize) {
		this.inputSize = inputSize;
		this.outputSize = outputSize;
	}

   	public void calculatePercents() {
		double prev = -1;
		for (final FinancialSample sample : this.samples) {
			if (prev != -1) {
				final double movement = sample.getReturnTimeSeries() - prev;
				final double percent = movement / prev;
				sample.setPercent(percent);
			}
			prev = sample.getReturnTimeSeries();
		}
	}

	public void getInputData(final int offset, final double[] input) {
		final Object[] samplesArray = this.samples.toArray();
		// get SP500 & prime data
		for (int i = 0; i < this.inputSize; i++) {
			final FinancialSample sample = (FinancialSample) samplesArray[offset
					+ i];
				input[i] = sample.getReturnTimeSeries(); /*getPercent()*/
				input[i + this.outputSize] = sample.getVolumeTimeSeries()/*getRate()*/;
		}
	}

	public void getOutputData(final int offset, final double[] output) {
		final Object[] samplesArray = this.samples.toArray();
		for (int i = 0; i < this.outputSize; i++) {
			final FinancialSample sample = (FinancialSample) samplesArray[offset
					+ this.inputSize + i];
			output[i] = sample.getReturnTimeSeries()/*getPercent()*/;
		}
	}
	
/*
	public double getPrimeRate(final Date date) {
		double currentRate = 0;

		for (final InterestRate rate : this.rates) {
			if (rate.getEffectiveDate().after(date)) {
				return currentRate;
			} else {
				currentRate = rate.getRate();
			}
		}
		return currentRate;
	}
*/
	/**
	 * @return the samples
	 */
	public Set<FinancialSample> getSamples() {
		return this.samples;
	}

/*	public void load(final String fileDirectory, final String primeFilename)
			throws IOException, ParseException {
		loadTimeSeries(fileDirectory);
		loadPrime(primeFilename);
		stitchInterestRates();
		calculatePercents();
	}
*/
/*	public void loadPrime(final String primeFilename) throws IOException,
			ParseException {
		try {
			properties.load(new FileInputStream("setups//main.properties"));
		} catch (IOException e) {
		}
		final ReadCSV csv = new ReadCSV(primeFilename);

		while (csv.next()) {
			final Date date = csv.getDate("date");
			final double rate = csv.getDouble("prime");
			final InterestRate ir = new InterestRate(date, rate);
			this.rates.add(ir);
		}

		csv.close();
	}
*/
	public void loadTimeSeries(final String timeSeriesDirectory) throws IOException,
			ParseException {
		final ReadCSV csv = new ReadCSV(timeSeriesDirectory);
		while (csv.next()) {
			final double timeTimeSeries = csv.getDouble("time");
			final double returnTimeSeries = csv.getDouble("return");
			final double volumeTimeSeries = csv.getDouble("volume");
			final FinancialSample sample = new FinancialSample();
			sample.setTimeTimeSeries(timeTimeSeries);
			sample.setReturnTimeSeries(returnTimeSeries);
			sample.setVolumeTimeSeries(volumeTimeSeries);
			this.samples.add(sample);
		}
		csv.close();
	}
	
	public void setSamples(){
		this.samples = Reporter.samplesForNN;
		
	}

	public int size() {
		return this.samples.size();
	}
/*
	public void stitchInterestRates() {
		for (final FinancialSample sample : this.samples) {
			final double rate = getPrimeRate(sample.getDate());
			sample.setRate(rate);
		}
	}
*/
}
