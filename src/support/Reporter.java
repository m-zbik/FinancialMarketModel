package support;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.heatonresearch.book.introneuralnet.neural.util.ErrorCalculation;
import com.sun.xml.internal.fastinfoset.util.ValueArrayResourceException;

import model.FinancialModel;
import model.agents.farmerliextraclass.FinancialSample;
import sim.engine.SimState;
import sim.engine.Steppable;

public class Reporter implements Steppable {

	private static final long serialVersionUID = 1L;

	BufferedWriter outPrices;
	BufferedWriter outParameters;
	FinancialModel myModel;

	public static List <Double> timeForAssetNN = new ArrayList<Double>();
	public static List <Double> returnRateForAssetNN = new ArrayList<Double>();
	public static List <Double> volumeForAssetNN = new ArrayList<Double>();
	public static Set<FinancialSample> samplesForNN = new TreeSet<FinancialSample>();
	
	public Reporter(FinancialModel myModel) {

		this.myModel = myModel;

		try {
			//mzbik 03.11.2014 changed the timeSeries file path from "timeSeries.txt" to "timeSeries/timeSeries.txt"
			outPrices = new BufferedWriter(new FileWriter("timeSeries/timeSeries.txt", true));
			
			
			String temp = "";
			temp = temp + "runID" + "	";
			temp = temp + "time" + "	";
			temp = temp + "asset" + "	";
			temp = temp + "closeAskPrice" + "	";
			temp = temp + "closeBidPrice" + "	";
			temp = temp + "averageTradePrice" + "	";
			temp = temp + "volume" + "	";
			temp = temp + "return" + "	";
//			temp = temp + "predictedReturn" + "	";
			temp = temp + "predictionError";
			

			outPrices.write(temp);
			outPrices.newLine();
			
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	
	public void step(SimState state) {

		if (myModel.schedule.getTime() == 0) {

		}
		
		try {

			for (int asset =0; asset < myModel.parameterMap.get("numAssets"); asset++) {
			
				String temp = "";
				temp = temp + myModel.runID + "	";
				temp = temp + myModel.schedule.getTime() + "	";
				temp = temp + myModel.parameterMap.get("Cont_D") + "	";
				temp = temp + myModel.myMarket.getAskPriceForAsset(asset)+ "	";
				temp = temp + myModel.myMarket.getBidPriceForAsset(asset)+ "	";
				temp = temp + myModel.myMarket.getAverageTradePriceForAsset(asset) + "	";
				temp = temp + myModel.myMarket.getVolumeForAsset(asset) + "	";
				temp = temp + myModel.myMarket.getReturnRateForAsset(asset) + "	";
//				temp = temp + myModel.myMarket.getAveragePredictionRate(asset);
				temp = temp + ErrorCalculation.errorNN;
				
				//only for numAssets = 1
				if (myModel.parameterMap.get("numAssets") > 1){
					System.out.println("!!! code written only for numAsset = 1; Check Reporter.java");
				} else{
					timeForAssetNN.add(myModel.schedule.getTime());
					returnRateForAssetNN.add(myModel.myMarket.getReturnRateForAsset(asset));
					volumeForAssetNN.add(myModel.myMarket.getVolumeForAsset(asset));
					
					final FinancialSample sampleNN = new FinancialSample();
					sampleNN.setTimeTimeSeries(myModel.schedule.getTime());
					sampleNN.setReturnTimeSeries(returnRateForAssetNN.get((int)myModel.schedule.getTime()));
					sampleNN.setVolumeTimeSeries(volumeForAssetNN.get((int)myModel.schedule.getTime()));
					samplesForNN.add(sampleNN);
				}
				
				outPrices.write(temp);
				outPrices.newLine();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	

	public void finishAll() {

		try {
			outPrices.flush();
			outPrices.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
