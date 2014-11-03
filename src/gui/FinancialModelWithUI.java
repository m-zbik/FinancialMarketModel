/*
 Copyright 2006 by Sean Luke and George Mason University
 Licensed under the Academic Free License version 3.0
 See the file "LICENSE" for more information
 */

/*
 * All changes made by Michal Zbikowski are marked with: mzbik
 */

package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Paint;

import javax.swing.JFrame;

import model.FinancialModel;

import sim.display.Console;
import sim.display.Controller;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.media.chart.HistogramGenerator;
import sim.util.media.chart.HistogramGeneratorProtecedUpdateMethodDoingPublic; //mzbik 30.10.2014
import sim.util.media.chart.TimeSeriesChartGenerator;

/**
 * @author Maciek
 */
public class FinancialModelWithUI extends GUIState {

	public TimeSeriesChartGenerator priceChart;

	public JFrame priceFrame;

	public TimeSeriesChartGenerator returnChart;

	public JFrame returnFrame;

	public TimeSeriesChartGenerator trailingAbsReturnChart;

	public JFrame trailingReturnFrame;
	
	public TimeSeriesChartGenerator acfChart;

	public JFrame acfFrame;

	public TimeSeriesChartGenerator volumeChart;

	public JFrame volumeFrame;

	public HistogramGenerator returnHist;
	
	public HistogramGeneratorProtecedUpdateMethodDoingPublic returnHistUpdate; //mzbik 30.10.2014 comment on this in line 287 where: returnHist = returnHistUpdate;

	public JFrame returnHistFrame;

	public GUIReporter myReporter;

	public HistogramGenerator orderHist;
	
	public HistogramGeneratorProtecedUpdateMethodDoingPublic orderHistUpdate; //mzbik 30.10.2014 comment on this in line 308 where orderHist = orderHistUpdate;

	public JFrame orderHistFrame;

	public double nextUpdate = 0;

	public static void main(String[] args) {
		FinancialModelWithUI householdWorld = new FinancialModelWithUI();
		Console c = new Console(householdWorld);
		c.setBounds(600, 0, 425, 470);
		c.setVisible(true);
	}

	public FinancialModelWithUI() {
		super(new FinancialModel(System.currentTimeMillis()));
	}

	public FinancialModelWithUI(SimState state) {
		super(state);
	}

	public static String getName() {
		return "Financial Market Simulation";
	}

	public Object getSimulationInspectedObject() {
		return state;
	}

	public void start() {
		super.start();
		setupPortrayals();
	}

	public void load(SimState state) {
		super.load(state);
		setupPortrayals();
	}

	public void setupPortrayals() {

		this.scheduleImmediateRepeat(true, myReporter);

		final Steppable histUpdater = new Steppable() {

			private static final long serialVersionUID = 1L;

			public void step(SimState state) {

				if (state.schedule.getTime() >= nextUpdate) {

					if (returnHistFrame.isVisible()) {
						for (int a = 0; a < ((FinancialModel) state).parameterMap.get("numAssets"); a++) {

							Double[] tempArray = new Double[myReporter.returnMemory.get(a).size()];
							tempArray = myReporter.returnMemory.get(a).toArray(tempArray);
							double[] temp2Array = new double[myReporter.returnMemory.get(a).size()];
							for (int i = 0; i < temp2Array.length; i++) {
								temp2Array[i] = tempArray[i].doubleValue();
							}
							if (temp2Array.length > 0) {
								returnHist.updateSeries(a, temp2Array/*, false*/); // mzbik 28.10.2014 I have commented 'false' because of the error '...not applicable for the arguments' in the HistogrammGenerator, line 158: public void updateSeries(int index, double[] vals)
							}
						}
					}

					if (orderHistFrame.isVisible()) {
						for (int a = 0; a < ((FinancialModel) state).parameterMap.get("numAssets"); a++) {

							// ((FinancialModel)
							// state).myMarket.orderBooks.get(a).cleanup();

							double[] temp2Array = ((FinancialModel) state).myMarket.orderBooks.get(a).getBuyOrders();
							if (temp2Array != null) {
								if (temp2Array.length > 0) {
									orderHist.updateSeries(2 * a, temp2Array/*, false*/);// mzbik 28.10.2014 I have commented 'false' because of the error '...not applicable for the arguments' in the HistogrammGenerator, line 158: public void updateSeries(int index, double[] vals)
								}
							}

							temp2Array = ((FinancialModel) state).myMarket.orderBooks.get(a).getSellOrders();
							if (temp2Array != null) {
								if (temp2Array.length > 0) {
									orderHist.updateSeries(2 * a + 1, temp2Array/*, false*/); // mzbik 28.10.2014 I have commented 'false' because of the error '...not applicable for the arguments' in the HistogrammGenerator, line 158: public void updateSeries(int index, double[] vals)
								}
							}
						}
					}

					if (acfFrame.isVisible()) {
						myReporter.acfAbsReturns();
					}
					
					if (trailingReturnFrame.isVisible()) {
						myReporter.updateTrailing();
					}

					if (priceFrame.isVisible() || 
						returnFrame.isVisible() ||
						volumeFrame.isVisible()) 
					{
						priceChart.disable();
						returnChart.disable();
						volumeChart.disable();

						myReporter.setSeries();
					
						priceChart.enable();
						returnChart.enable();
						volumeChart.enable();
					}

					nextUpdate = nextUpdate + 1;
				}
			}
		};

		this.scheduleImmediateRepeat(false, histUpdater);

	}

	public void init(Controller c) {
		super.init(c);

		myReporter = new GUIReporter(this);
		FinancialModel myModel = (FinancialModel) this.state;

		priceChart = new TimeSeriesChartGenerator();
		priceChart.setTitle("Prices plot");
		priceChart.setDomainAxisLabel("Step");
		priceChart.setRangeAxisLabel("Price");
		priceChart.getChartPanel().getChart().setBackgroundPaint(Color.white);
		for (int a = 0; a < myModel.parameterMap.get("numAssets"); a++) {
			priceChart.addSeries(myReporter.bidPriceSeries.get(a), null);
			priceChart.addSeries(myReporter.askPriceSeries.get(a), null);
		}
		priceFrame = priceChart.createFrame(this);
		priceFrame.getContentPane().setLayout(new BorderLayout());
		priceFrame.getContentPane().add(priceChart, BorderLayout.CENTER);
		priceFrame.pack();
		c.registerFrame(priceFrame);

		returnChart = new TimeSeriesChartGenerator();
		returnChart.setTitle("Returns plot");
		returnChart.setDomainAxisLabel("Step");
		returnChart.setRangeAxisLabel("Return");
		returnChart.getChartPanel().getChart().setBackgroundPaint(Color.white);
		for (int a = 0; a < myModel.parameterMap.get("numAssets"); a++) {
			returnChart.addSeries(myReporter.returnSeries.get(a), null);
			returnChart.addSeries(myReporter.absReturnSeries.get(a), null);
		}
		returnFrame = returnChart.createFrame(this);
		returnFrame.getContentPane().setLayout(new BorderLayout());
		returnFrame.getContentPane().add(returnChart, BorderLayout.CENTER);
		returnFrame.pack();
		c.registerFrame(returnFrame);

		trailingAbsReturnChart = new TimeSeriesChartGenerator();
		trailingAbsReturnChart.setTitle("Annual average volatility plot");
		trailingAbsReturnChart.setDomainAxisLabel("Step");
		trailingAbsReturnChart.setRangeAxisLabel("Trailing average annual absolute returns");
		trailingAbsReturnChart.getChartPanel().getChart().setBackgroundPaint(Color.white);
		for (int a = 0; a < myModel.parameterMap.get("numAssets"); a++) {
			trailingAbsReturnChart.addSeries(myReporter.trailingAbsReturnSeries.get(a), null);
		}
		trailingReturnFrame = trailingAbsReturnChart.createFrame(this);
		trailingReturnFrame.getContentPane().setLayout(new BorderLayout());
		trailingReturnFrame.getContentPane().add(trailingAbsReturnChart, BorderLayout.CENTER);
		trailingReturnFrame.pack();
		c.registerFrame(trailingReturnFrame);
		
		
		volumeChart = new TimeSeriesChartGenerator();
		volumeChart.setTitle("Volumes plot");
		volumeChart.setDomainAxisLabel("Step");
		volumeChart.setRangeAxisLabel("Volume");
		volumeChart.getChartPanel().getChart().setBackgroundPaint(Color.white);
		for (int a = 0; a < myModel.parameterMap.get("numAssets"); a++) {
			volumeChart.addSeries(myReporter.volumeSeries.get(a), null);
		}
		volumeFrame = volumeChart.createFrame(this);
		volumeFrame.getContentPane().setLayout(new BorderLayout());
		volumeFrame.getContentPane().add(volumeChart, BorderLayout.CENTER);
		volumeFrame.pack();
		c.registerFrame(volumeFrame);

		acfChart = new TimeSeriesChartGenerator();
		acfChart.setTitle("Autocorrelation of returns");
		acfChart.setDomainAxisLabel("Lag");
		acfChart.setRangeAxisLabel("Correlation");
		acfChart.getChartPanel().getChart().setBackgroundPaint(Color.white);
		for (int a = 0; a < myModel.parameterMap.get("numAssets"); a++) {
			acfChart.addSeries(myReporter.acfAbsReturnsSeries.get(a), null);
			acfChart.addSeries(myReporter.acfReturnsSeries.get(a), null);
		}
		acfFrame = acfChart.createFrame(this);
		acfFrame.getContentPane().setLayout(new BorderLayout());
		acfFrame.getContentPane().add(acfChart, BorderLayout.CENTER);
		acfFrame.pack();
		c.registerFrame(acfFrame);

		double[] fakeArray = { 1, 2, 3 };

		returnHist = new HistogramGenerator();
		returnHistUpdate = new HistogramGeneratorProtecedUpdateMethodDoingPublic(); //mzbik 30.10.2014 comment on this in line 287 where: returnHist = returnHistUpdate;
		returnHist.setTitle("Returns histogram");
		returnHist.setDomainAxisLabel("Value");
		returnHist.setRangeAxisLabel("Number of observations");
		returnHist.getChartPanel().getChart().setBackgroundPaint(Color.white);
		for (int a = 0; a < myModel.parameterMap.get("numAssets"); a++) {
			returnHist.addSeries(fakeArray, 60, "Return histogram for asset " + a, null);
		}
		returnHistUpdate.callProtectedUpdateFromHistGen(); //mzbik 30.10.2014 comment on this in line 287 where: returnHist = returnHistUpdate;
		returnHist = returnHistUpdate; // mzbik 30.10.2014 I have changed update() (which was protected) to callProtectedUpdateFromHistGen() (which is public) -> the whole comment is in the callProtectedUpdateFromHistGen() in the package package sim.util.media.chart
		returnHistFrame = returnHist.createFrame(this);
		returnHistFrame.getContentPane().setLayout(new BorderLayout());
		returnHistFrame.getContentPane().add(returnHist, BorderLayout.CENTER);
		returnHistFrame.pack();
		returnHistFrame.setVisible(false);
		c.registerFrame(returnHistFrame);

		orderHist = new HistogramGenerator();
		orderHistUpdate = new HistogramGeneratorProtecedUpdateMethodDoingPublic(); //mzbik 30.10.2014 comment on this in line 308 where orderHist = orderHistUpdate;
		orderHist.setTitle("Order books");
		orderHist.setDomainAxisLabel("Value");
		orderHist.setRangeAxisLabel("Number of observations");
		orderHist.getChartPanel().getChart().setBackgroundPaint(Color.white);
		for (int a = 0; a < myModel.parameterMap.get("numAssets"); a++) {
			orderHist.addSeries(fakeArray, 150, "buy orders for asset " + a, null);
			orderHist.addSeries(fakeArray, 150, "sell orders for asset " + a, null);
		}
		orderHistUpdate.callProtectedUpdateFromHistGen(); //mzbik 30.10.2014 comment on this in line 308 where orderHist = orderHistUpdate;
		orderHist = orderHistUpdate; // mzbik 30.10.2014 I have changed update() (which was protected) to callProtectedUpdateFromHistGen() (which is public) -> the whole comment is in the callProtectedUpdateFromHistGen() in the package package sim.util.media.chart
		orderHistFrame = orderHist.createFrame(this);
		orderHistFrame.getContentPane().setLayout(new BorderLayout());
		orderHistFrame.getContentPane().add(orderHist, BorderLayout.CENTER);
		orderHistFrame.pack();
		orderHistFrame.setVisible(false);
		c.registerFrame(orderHistFrame);

	}

	public void quit() {
		super.quit();
	}

}
