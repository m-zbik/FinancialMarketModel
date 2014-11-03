package gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import model.FinancialModel;

import org.jfree.data.xy.XYSeries;

import sim.engine.SimState;
import sim.engine.Steppable;

public class GUIReporter implements Steppable {

	private static final long serialVersionUID = 1L;

	public ArrayList<XYSeries> bidPriceSeries = new ArrayList<XYSeries>();
	
	public ArrayList<XYSeries> askPriceSeries = new ArrayList<XYSeries>();

	public ArrayList<XYSeries> returnSeries = new ArrayList<XYSeries>();

	public ArrayList<XYSeries> absReturnSeries = new ArrayList<XYSeries>();

	public ArrayList<XYSeries> trailingAbsReturnSeries = new ArrayList<XYSeries>();
	
	public ArrayList<XYSeries> acfAbsReturnsSeries = new ArrayList<XYSeries>();

	public ArrayList<XYSeries> acfReturnsSeries = new ArrayList<XYSeries>();
	
	public ArrayList<XYSeries> volumeSeries = new ArrayList<XYSeries>();

	public ArrayList<Vector<Double>> bidPriceMemory = new ArrayList<Vector<Double>>();
	
	public ArrayList<Vector<Double>> askPriceMemory = new ArrayList<Vector<Double>>();

	public ArrayList<Vector<Double>> returnMemory = new ArrayList<Vector<Double>>();
	
	public ArrayList<Vector<Double>> volumeMemory = new ArrayList<Vector<Double>>();
	
	// The number of data points saved and shown for the price, return, and volume series'
	// Also, the #/data points used in calculating autocorrelation
	public int NumViewable = 10000; 

	// number of steps "lag" to compute the autocorrelation for
	public int ACFViewable = 200;

	// number of steps to average over when doing trailing averages
	public int trailingSampleSize = 365;
	
	FinancialModel myModel;

	public int nextUpdate = 0;

	public int lastUpdate = 0;

	public GUIReporter(FinancialModelWithUI contModelWithUI) {
		myModel = (FinancialModel) contModelWithUI.state;

		for (int a = 0; a < myModel.parameterMap.get("numAssets"); a++) {

			bidPriceSeries.add(new XYSeries("Bid price for asset " + a));
			askPriceSeries.add(new XYSeries("Ask price for asset " + a));
			returnSeries.add(new XYSeries("Returns for asset " + a));
			trailingAbsReturnSeries.add(new XYSeries("Trailing volatility for asset " + a));
			absReturnSeries.add(new XYSeries("Absolute returns for asset " + a));
			volumeSeries.add(new XYSeries("Trade volumes for asset " + a));
			acfAbsReturnsSeries.add(new XYSeries("ACF of absolute returns for asset " + a));
			acfReturnsSeries.add(new XYSeries("ACF of returns for asset " + a));

			// maximum item count only needs to be set once
			bidPriceSeries.get(a).setMaximumItemCount(NumViewable);
			askPriceSeries.get(a).setMaximumItemCount(NumViewable);
			returnSeries.get(a).setMaximumItemCount(NumViewable);
			trailingAbsReturnSeries.get(a).setMaximumItemCount(NumViewable);
			volumeSeries.get(a).setMaximumItemCount(NumViewable);
			absReturnSeries.get(a).setMaximumItemCount(NumViewable);
			// initialize
			for (int i = 1; i < ACFViewable; i++) {
				acfAbsReturnsSeries.get(a).add(i, 0.0);
				acfReturnsSeries.get(a).add(i, 0.0);
			}
			for (int i=0; i<NumViewable; i++) {
				trailingAbsReturnSeries.get(a).add(i,0.0);
			}

			bidPriceMemory.add(new Vector<Double>());
			askPriceMemory.add(new Vector<Double>());
			returnMemory.add(new Vector<Double>());
			volumeMemory.add(new Vector<Double>());
		}

	}

	public void step(SimState state) {

		if (state.schedule.getTime() >= nextUpdate) {

			for (int a = 0; a < myModel.parameterMap.get("numAssets"); a++) {

				bidPriceMemory.get(a).add((myModel.myMarket.getBidPriceForAsset(a)));
				askPriceMemory.get(a).add((myModel.myMarket.getAskPriceForAsset(a)));
				returnMemory.get(a).add(myModel.myMarket.getReturnRateForAsset(a));
				volumeMemory.get(a).add(myModel.myMarket.getVolumeForAsset(a));

			}

			nextUpdate = nextUpdate + 1;
		}

	}

	public void setSeries() {

		int stop = (int) myModel.schedule.getTime();
		int start = Math.max(lastUpdate, stop - NumViewable);

		for (int a = 0; a < myModel.parameterMap.get("numAssets"); a++) {
			for (int i = start; i < stop; i++) {
				askPriceSeries.get(a).add(i, askPriceMemory.get(a).get(i));
				bidPriceSeries.get(a).add(i, bidPriceMemory.get(a).get(i));
				returnSeries.get(a).add(i, returnMemory.get(a).get(i));
				absReturnSeries.get(a).add(i, Math.abs(returnMemory.get(a).get(i)));
				volumeSeries.get(a).add(i, volumeMemory.get(a).get(i));
			}
		}

		if (stop > start)
			lastUpdate = stop;

	}

	public void acfAbsReturns() {

		// make an array of the absolute value of the (last NumViewable) returns

		for (int a = 0; a < myModel.parameterMap.get("numAssets"); a++) {

			int arrayLen = Math.min(NumViewable, returnMemory.get(a).size());
			int offset = returnMemory.get(a).size() - arrayLen;
			double[] absRetArray = new double[arrayLen];
			double[] retArray = new double[arrayLen];
			for (int i = 0; i < absRetArray.length; i++) {
				absRetArray[i] = Math.abs(returnMemory.get(a).get(i + offset));
				retArray[i] = returnMemory.get(a).get(i + offset);
			}

			int numLags = Math.min(ACFViewable, returnMemory.get(a).size());
			for (int lag = 1; lag < numLags; lag++) {
				double abs_autocorr_lag = autocorrelation(absRetArray, lag);
				double autocorr_lag = autocorrelation(retArray, lag);

				// instead of clearing and adding, we just replace the earlier
				// values
				acfAbsReturnsSeries.get(a).updateByIndex(lag - 1, abs_autocorr_lag);
				acfReturnsSeries.get(a).updateByIndex(lag - 1, autocorr_lag);

			}
		}

	}

	public static double correlation(double[] rankArray1, double[] rankArray2) {
		if (rankArray1.length != rankArray2.length || rankArray1.length < 1) {
			return 0.0;
		}
		int nObs = rankArray1.length;
		double mua = 0;
		double mub = 0;
		double prod = 0;
		double a2 = 0;
		double b2 = 0;

		for (int i = 0; i < nObs; i++) {
			prod += rankArray1[i] * rankArray2[i];
			a2 += rankArray1[i] * rankArray1[i];
			b2 += rankArray2[i] * rankArray2[i];
			mua += rankArray1[i];
			mub += rankArray2[i];
		}
		return ((nObs * prod) - (mua * mub)) / (Math.sqrt(nObs * a2 - mua * mua) * Math.sqrt(nObs * b2 - mub * mub));
	}

	public static double autocorrelation(double[] array, int lag) {
		if (lag >= array.length || array.length < 1) {
			return 0.0;
		}

		int nObs = array.length - lag;
		double mua = 0;
		double mub = 0;
		double prod = 0;
		double a2 = 0;
		double b2 = 0;

		for (int i = 0; i < nObs; i++) {
			prod += array[i] * array[i + lag];
			a2 += array[i] * array[i];
			b2 += array[i + lag] * array[i + lag];
			mua += array[i];
			mub += array[i + lag];
		}
		return ((nObs * prod) - (mua * mub)) / (Math.sqrt(nObs * a2 - mua * mua) * Math.sqrt(nObs * b2 - mub * mub));
	}
	
	public void updateTrailing() {

		for (int a = 0; a < myModel.parameterMap.get("numAssets"); a++) {

			int arrayLen = Math.min(NumViewable, returnMemory.get(a).size());
			int offset = returnMemory.get(a).size() - arrayLen;
			if (arrayLen<=trailingSampleSize) return;
			double[] volatilityArray = new double[arrayLen];
			for (int i = 0; i < arrayLen; i++) {
				volatilityArray[i] = Math.abs(returnMemory.get(a).get(i + offset));
			}

			double[] avgsArray = trailingAverage(volatilityArray, trailingSampleSize);
			trailingAbsReturnSeries.get(a).clear();
			for (int i=0; i<avgsArray.length; i++) {
				// instead of clearing and adding, we just replace the earlier
				// values
				//trailingAbsReturnSeries.get(a).updateByIndex(i, avgsArray[i]);
				trailingAbsReturnSeries.get(a).add(i,avgsArray[i]);
			}
		}

	}

	
	// Takes an array of n numbers; returns the trailing average over samplesize.
	// The array of averages returned is of length n-samplesize
	public static double[] trailingAverage(double[] data, int samplesize)
	{
	    if (data==null || data.length<=samplesize) {
		    return null;
	    }
	    double[] avgArray = new double[data.length-samplesize];
	    double sum = 0.0;
	    
	    for (int i=0; i<samplesize; i++) {
	    	sum+=data[i];
	    }
	    
	    for (int i=0; i<data.length-samplesize; i++) {
	    	avgArray[i] = sum/samplesize;
	    	sum+=data[i+samplesize];
	    	sum-=data[i];
	    }
	    
	    return avgArray;
	}

}
