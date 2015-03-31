package model;

import java.util.ArrayList;
import java.util.HashMap;

import model.agents.ContPlayer;
import model.agents.GenericPlayer;
import model.market.Market;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Interval;
import support.ModelFactory;
import support.Reporter;

//mzbik comment 25.03.2015 changing ContModel to FarmerModel
/* Class: ContModel 
 * Spring 2008
 * FinancialMarketModel Team
 * 
 * Function: the main class of the applications. 
 * Initializes N traders and runs trades for a maxT number of times
 */

public class FinancialModel extends SimState {

	private static final long serialVersionUID = 1L;

	public Reporter myReporter;
	
	public ModelFactory myCreator;
	
	public HashMap<String, Double> parameterMap = new HashMap<String, Double>();

	public HashMap<String, String> optionsMap = new HashMap<String, String>();
	
	// array list of traders
	public ArrayList<GenericPlayer> agentList = null;
	
	// Market class: calculates excess demand and return rate
	public Market myMarket;
	

	
	public int runID = 0;
	
	public boolean wrapperActive = false;

	// constructor
	public FinancialModel(long seed) {
		super(seed);
		this.myReporter = new Reporter(this);
		this.myCreator = new ModelFactory(this);
	}

	public void start() {
		
		super.start();
		
		this.schedule.reset();
		
		myCreator.buildAgents();
		
		runID++;

	
		// embedded class; stops the the model run (used in scheduler)
		final Steppable finalAgent = new Steppable() {

			private static final long serialVersionUID = 6184761986120478954L;

			public void step(SimState state) {
				
				if (!wrapperActive) {
					myReporter.finishAll();
				}
				
				state.finish();
			}

		};
		
		// run the model maxT times and then finish the execution
		schedule.scheduleOnce(this.parameterMap.get("maxT"), (int) schedule.MAXIMUM_INTEGER, finalAgent);
	
		schedule.scheduleRepeating(myReporter, 10, 1);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// run the model 
		doLoop(FinancialModel.class, args);
		System.exit(0);

	}

}
