package support;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import model.FinancialModel;

import sim.engine.SimState;
import sim.engine.Steppable;

public class Reporter implements Steppable {

	private static final long serialVersionUID = 1L;

	BufferedWriter outPrices;
	
	BufferedWriter outParameters;

	FinancialModel myModel;

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
			temp = temp + "return" ;
			

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
			temp = temp + myModel.myMarket.getReturnRateForAsset(asset);
			
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
