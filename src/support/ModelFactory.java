package support;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import model.FinancialModel;
import model.agents.GenericPlayer;
import model.market.Market;
import model.market.books.OrderBook;

public class ModelFactory {

	public boolean returnSim = false;

	public FinancialModel target;

	String fileName;

	public ModelFactory(FinancialModel target) {

		/* Read in logging settings. */

		if (target == null) {
			returnSim = true;
			target = new FinancialModel(System.currentTimeMillis());
		} else {
			this.target = target;
		}

		/* Read in simulation settings and set them to the target. */

		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("setups//main.properties"));
		} catch (IOException e) {
		}

		target.parameterMap.put("D", new Double(properties.getProperty("D", "2")));
		target.parameterMap.put("maxT", new Double(properties.getProperty("maxT", "100000")));
		target.parameterMap.put("numAssets", new Double(properties.getProperty("numAssets", "1")));
		target.parameterMap.put("initialPrice", new Double(properties.getProperty("initialPrice", "10")));
		target.parameterMap.put("Cont_s", new Double(properties.getProperty("Cont_s", "0.1")));
		target.parameterMap.put("Cont_lambda", new Double(properties.getProperty("Cont_lambda", "10")));
		target.parameterMap.put("Farmer_alpha", new Double(properties.getProperty("Farmer_alpha", "0.65")));
		target.parameterMap.put("Farmer_delta", new Double(properties.getProperty("Farmer_delta", ".01")));
		target.parameterMap.put("Farmer_mu", new Double(properties.getProperty("Farmer_mu", "0.5")));
		target.parameterMap.put("Farmer_sigma", new Double(properties.getProperty("Farmer_sigma", "1")));
		target.parameterMap.put("Farmer_granularity", new Double(properties.getProperty("Farmer_granularity", "0.0")));

		target.parameterMap.put("LS_h", new Double(properties.getProperty("LS_h", "1")));
		target.parameterMap.put("LS_kappa", new Double(properties.getProperty("LS_kappa", "1")));
		target.optionsMap.put("LS_herding", properties.getProperty("LS_herding", "true"));
		target.optionsMap.put("LS_volatility", properties.getProperty("LS_volatility", "true"));
		target.optionsMap.put("Farmer_logPricing", properties.getProperty("Farmer_logPricing", "true"));
		
		target.optionsMap.put("agentConfiguration", properties.getProperty("agentConfiguration", "cont.txt"));
		target.optionsMap.put("orderBookClass", properties.getProperty("orderBookClass", "ContBook"));
		target.optionsMap.put("orderBookOptions", properties.getProperty("orderBookOptions", "none"));
	
	}

	public void buildAgents() {

		// initialize an array list of traders
		target.agentList = new ArrayList<GenericPlayer>();
		
		BufferedReader in;
		String tempLine;
		StringTokenizer st;
		try {
			in = new BufferedReader(new FileReader("setups//" + target.optionsMap.get("agentConfiguration")));
			while (!((tempLine = in.readLine()) == null)) {
				st = new StringTokenizer(tempLine, ",");
				String className = st.nextToken();
				int numOfInstances = new Integer(st.nextToken()).intValue();			
				this.createLotsOfAgents(className, numOfInstances);
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// initialize market agent
		target.myMarket = new Market(target);

		try {
			for (int a = 0; a < target.parameterMap.get("numAssets"); a++) {
				target.myMarket.orderBooks.add((OrderBook) Class.forName("model.market.books." + target.optionsMap.get("orderBookClass")).newInstance());
			}
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int a = 0; a < target.parameterMap.get("numAssets"); a++) {
			target.myMarket.orderBooks.get(a).setMyWorld(target);
			target.myMarket.orderBooks.get(a).setMyID(a);
		}

		target.schedule.scheduleRepeating(target.myMarket, 2, 1.0);

	}

	private void createLotsOfAgents(String className, int numOfInstances) {
		
		try {

			// initialize traders and add them to the list
			for (int i = 0; i < numOfInstances; i++) {
				// create new trader
				GenericPlayer tempAgent = null;
				tempAgent = (GenericPlayer) Class.forName("model.agents." + className).newInstance();
				// setup agent
				tempAgent.setup(i, target);
				// add trader to the list
				target.agentList.add(tempAgent);
			}

		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
