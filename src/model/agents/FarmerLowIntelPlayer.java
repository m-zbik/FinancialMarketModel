package model.agents;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import com.heatonresearch.book.introneuralnet.common.ReadCSV;
import com.heatonresearch.book.introneuralnet.neural.activation.ActivationFunction;
import com.heatonresearch.book.introneuralnet.neural.activation.ActivationTANH;
import com.heatonresearch.book.introneuralnet.neural.feedforward.FeedforwardLayer;
import com.heatonresearch.book.introneuralnet.neural.feedforward.FeedforwardNetwork;
import com.heatonresearch.book.introneuralnet.neural.feedforward.train.Train;
import com.heatonresearch.book.introneuralnet.neural.feedforward.train.anneal.NeuralSimulatedAnnealing;
import com.heatonresearch.book.introneuralnet.neural.feedforward.train.backpropagation.Backpropagation;
import com.heatonresearch.book.introneuralnet.neural.util.ErrorCalculation;
import com.heatonresearch.book.introneuralnet.neural.util.SerializeObject;

import experiments.RunsWrapper;
import sim.engine.SimState;
import model.FinancialModel;
import model.agents.farmerliextraclass.ActualTimeSeries;
import model.agents.farmerliextraclass.FinancialSample;
import model.market.books.LimitOrder;
import model.market.books.LiquidityException;
import model.market.books.OrderBook.OrderType;
import support.Distributions;
import support.Reporter;


/* Agent that places Market orders according to Farmer's model */

//NOTE: All prices in this model are LOG prices, so negative 
//prices are perfectly acceptable.
public class FarmerLowIntelPlayer extends GenericPlayer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Distributions randDist = null;
	private double mu; // Poisson rate: ordered placed per step
	private int sigma; // order size
	
	private String nameOfPlayersType = this.getClass().getSimpleName(); //mzbik added 31.03.2015 to print Limit Exception
	
//	set parameters for NN like:
	public final static int TRAINING_SIZE = /*16*/ 500 /*1800*/;
	public final static int INPUT_SIZE = /*4 */10;
	public final static int OUTPUT_SIZE = 1;
	public final static int NEURONS_HIDDEN_1 = /*8*/ 20;
	public final static int NEURONS_HIDDEN_2 =/* 0 */20;
	public final static double MAX_ERROR = 0.02;
	public final static double /*Date*/ PREDICT_FROM = /*17*/ 501/*1080*/;
	public final static double /*Date*/ LEARN_FROM = 0;
	private double[] predict = new double[OUTPUT_SIZE];
	public static int[] stageNNdevolpment = {0, 1, 2, 3};
	public static int stage = 0;
	private static double predictedValue; // = predict[0] and it is created only to store last predicted value for generateOrders()
	

	public FarmerLowIntelPlayer() {

	}
	
	public void setup(int i, FinancialModel target) {
		// do initialization
		this.myWorld = target;
		this.id = i;
		
		randDist = new Distributions(myWorld.random);
		mu = myWorld.parameterMap.get("Farmer_mu");
		sigma = myWorld.parameterMap.get("Farmer_sigma").intValue();

		target.schedule.scheduleRepeating(1.0, 1, this, 1.0);
	}
	
	public void step(SimState state) {
		this.predictReturn();
		this.generateOrders();
	}

	private void predictReturn(){
		final FarmerLowIntelPlayer predict = new FarmerLowIntelPlayer();
		
		if (stage == 0 & RunsWrapper.movesCounter < (INPUT_SIZE * 2) ){
			System.out.println("Not enougth data for NN. Low-Int agent currently not involved " + RunsWrapper.movesCounter);
		} else if (RunsWrapper.movesCounter < TRAINING_SIZE){
			predict.run(stageNNdevolpment[1]);
		} else if (RunsWrapper.movesCounter == TRAINING_SIZE){
			predict.run(stageNNdevolpment[2]); // there could be a condition New NN true or preset NN false
		} else {
			predict.run(stageNNdevolpment[3]);
		}
	}

	private void generateOrders() {
		
		int amountLocalSigma = 0;
		int assetLocalAsset = 0;
		int ordersPlaced = randDist.nextPoisson(mu);
		System.out.println("ordersPlaced = " + ordersPlaced);
		
		// place 'ordersPlaced' orders this iteration, each of size sigma
		for (int i = 0; i < ordersPlaced; i++) {
			OrderType orderType;
			int asset = myWorld.random.nextInt(myWorld.myMarket.orderBooks.size());
			if (myWorld.random.nextBoolean(0.5)) {
				orderType = OrderType.PURCHASE;
			} else {
				orderType = OrderType.SALE;
			}
			
			if (predictedValue > 0 && orderType == OrderType.PURCHASE || 
					predictedValue < 0 && orderType == OrderType.SALE){
				System.out.println("I take part in the market mechanism");
				myWorld.myMarket.acceptMarketOrder(orderType, asset, sigma, nameOfPlayersType); //mzbik 31.03.2015 added namyOfPlayersType to print Liquidity exception 
			} else {
				// acceptMarket Order with Zero Asset and Zero Amount = do nothing;
				System.out.println("I DO NOT take part in the market mechanism");
				myWorld.myMarket.acceptMarketOrder(orderType, assetLocalAsset, amountLocalSigma, nameOfPlayersType);
			}
		}
	}
	
	private double input[][];

	private double ideal[][];
	private FeedforwardNetwork network;

	private ActualTimeSeries actual;

	public void createNetwork() {
		final ActivationFunction thresholdFunction = new ActivationTANH();
		this.network = new FeedforwardNetwork();
		this.network.addLayer(new FeedforwardLayer(thresholdFunction,
				FarmerLowIntelPlayer.INPUT_SIZE * 2));
		this.network.addLayer(new FeedforwardLayer(thresholdFunction,
				FarmerLowIntelPlayer.NEURONS_HIDDEN_1));
		if (FarmerLowIntelPlayer.NEURONS_HIDDEN_2 > 0) {
			this.network.addLayer(new FeedforwardLayer(thresholdFunction,
					FarmerLowIntelPlayer.NEURONS_HIDDEN_2));
		}
		this.network.addLayer(new FeedforwardLayer(thresholdFunction,
				FarmerLowIntelPlayer.OUTPUT_SIZE));
		this.network.reset();
	}

	public void display() {
		final NumberFormat percentFormat = NumberFormat.getPercentInstance();
		percentFormat.setMinimumFractionDigits(2);

		final double[] present = new double[INPUT_SIZE * 2];
		//double[] predict = new double[OUTPUT_SIZE];
		final double[] actualOutput = new double[OUTPUT_SIZE];
		
		int index = 0;
		for (final FinancialSample sample : this.actual.getSamples()) {
			if (stage == 1 & sample.getTimeTimeSeries()/*.after*/ > (FarmerLowIntelPlayer.PREDICT_FROM)) {
				final StringBuilder str = new StringBuilder();
				str.append(/*ReadCSV.displayDate(*/sample.getTimeTimeSeries());
				str.append(":Start=");
				str.append(sample.getReturnTimeSeries());

				this.actual.getInputData(index - INPUT_SIZE, present);
				this.actual.getOutputData(index - INPUT_SIZE, actualOutput);

				predict = this.network.computeOutputs(present);
				str.append(",Actual % Change=");
				str.append(percentFormat.format(actualOutput[0]));
				str.append(",Predicted % Change= ");
				str.append(percentFormat.format(predict[0]));

				str.append(":Difference=");

				final ErrorCalculation error = new ErrorCalculation();
				str.append(percentFormat.format(error.calculateRMS()));
				error.updateError(predict, actualOutput);

				predictedValue = predict[0];
				System.out.println(str.toString());
				
			} else if (stage == 1 & sample.getTimeTimeSeries()/*.after*/ > /*0*/ INPUT_SIZE - 1 /*+ OUTPUT_SIZE*/){
				final StringBuilder str = new StringBuilder();
				str.append(/*ReadCSV.displayDate(*/sample.getTimeTimeSeries());
				str.append(":Start=");
				str.append(sample.getReturnTimeSeries());

				this.actual.getInputData(index - INPUT_SIZE, present);
				this.actual.getOutputData(index - INPUT_SIZE, actualOutput);

				predict = this.network.computeOutputs(present);
//				str.append(",Actual % Change=");
//				str.append(percentFormat.format(actualOutput[0]));
//				str.append(",Predicted % Change= ");
//				str.append(percentFormat.format(predict[0]));
//
//				str.append(":Difference=");
//
//				final ErrorCalculation error = new ErrorCalculation();
//				str.append(percentFormat.format(error.calculateRMS()));
//				error.updateError(predict, actualOutput);
				
				predictedValue = predict[0];
//				System.out.println(str.toString());
			}

			index++;
		}
		stage = 0;
	}
	
	private void generateTrainingSets() {
		this.input = new double[TRAINING_SIZE][INPUT_SIZE * 2];
		this.ideal = new double[TRAINING_SIZE][OUTPUT_SIZE];

		// find where we are starting from
		int startIndex = 0;
		for (final FinancialSample sample : this.actual.getSamples()) {
			if (sample.getTimeTimeSeries() > LEARN_FROM) /*sample.getDate().after(LEARN_FROM)*/ {
				break;
			}
			startIndex++;
		}

/*		
		int startIndex;
		if (Reporter.returnRateForAssetFroNN.size() > LEARN_FROM){
			startIndex = 0;
		} else {
			startIndex = Reporter.returnRateForAssetFroNN.size();
		}
*/
		
		// create a sample factor across the training area
		final int eligibleSamples = TRAINING_SIZE - startIndex;
		if (eligibleSamples == 0) {
			System.out
					.println("Need an earlier date for LEARN_FROM or a smaller number for TRAINING_SIZE.");
			System.exit(0);
		}
		final int factor = eligibleSamples / TRAINING_SIZE;

		// grab the actual training data from that point
		for (int i = 0; i < TRAINING_SIZE; i++) {
			this.actual.getInputData(startIndex + (i * factor), this.input[i]);
			this.actual.getOutputData(startIndex + (i * factor), this.ideal[i]);
		}
	}

	public void loadNeuralNetwork() throws IOException, ClassNotFoundException {
		this.network = (FeedforwardNetwork) SerializeObject.load("timeSeries/FarmerLowIntelPlayerNN.net");
	}

	public void run(int stage) {
		try {
			this.actual = new ActualTimeSeries(INPUT_SIZE, OUTPUT_SIZE);
//			this.actual.loadTimeSeries("timeSeries/timeSeries.txt");
			actual.setSamples(); 
			
			System.out.println("Samples read: " + this.actual.size());
			
			FarmerLowIntelPlayer.stage = stage;
			
			if (stage == 1){
				createNetwork();
			} else if (stage == 2) {
				createNetwork();
				generateTrainingSets();
				trainNetworkBackprop();
				saveNeuralNetwork();
			} else if (stage == 3) {
				loadNeuralNetwork();
			} else {
				System.out.println("The neural network is in a stage 0 of development");
			}
			display();

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveNeuralNetwork() throws IOException {
		SerializeObject.save("timeSeries/FarmerLowIntelPlayerNN.net", this.network);
	}

	private void trainNetworkBackprop() {
		final Train train = new Backpropagation(this.network, this.input,
				this.ideal, 0.00001, 0.1);
		double lastError = Double.MAX_VALUE;
		int epoch = 1;
		int lastAnneal = 0;

		do {
			train.iteration();
			double error = train.getError();
			
			System.out.println("Iteration(Backprop) #" + epoch + " Error:"
					+ error);
			
			if( error>0.05 ){
				if( (lastAnneal>100) && (error>lastError || Math.abs(error-lastError)<0.0001) ){
					trainNetworkAnneal();
					lastAnneal = 0;
				}
			}
			lastError = train.getError();
			epoch++;
			lastAnneal++;
		} while (train.getError() > MAX_ERROR);
	}
	
	private void trainNetworkAnneal() {
		System.out.println("Training with simulated annealing for 5 iterations");
		// train the neural network
		final NeuralSimulatedAnnealing train = new NeuralSimulatedAnnealing(
				this.network, this.input, this.ideal, 10, 2, 100);

		int epoch = 1;

		for(int i=1;i<=5;i++) {
			train.iteration();
			System.out.println("Iteration(Anneal) #" + epoch + " Error:"
					+ train.getError());
			epoch++;
		} 
	}
}

