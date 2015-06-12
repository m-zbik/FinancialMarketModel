package experiments;

import model.FinancialModel;

public class RunsWrapper {

	public static int movesCounter = 0; //mzbik added movesCounter 04.02.2015 for debugging purposes
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		
		FinancialModel hhw = new FinancialModel(System.currentTimeMillis());
		hhw.wrapperActive = true; //mzbik comment this lets us omit the myReporter.finishAll() in
									//FinancialModel and execute it here below line34
		
		for (int t = 0; t < 1; t++) {//mzbik mod t < 500 04.02.2015

			System.out.println("Run " + t);
			hhw.parameterMap.put("Cont_D", new Double(0.1*hhw.random.nextDouble()));
			System.out.println(hhw.parameterMap.get("Cont_D"));

			hhw.start();
			
//			write and paste here code that:
//			- 	moves files (if exist) timeSeries and FarmerLowIntelPlayerNN.net
//				to new folder called results each time new world is created
//			-	reset movesCounter

			movesCounter = 0; // mzbik reset movesCounter before creating new world
			
			while ((hhw.schedule.step(hhw))) {
				movesCounter++;// mzbik added 04.02.2015
			}
			
			System.out.println("Number of moves/events on the market " + movesCounter
					+ "\nwith the same amount of agents and the same group of agents.");//mzbik mod 04.02.2015 Where one move = every agent makes one move
			System.out.println("Now we change the group of agents."
					+ "\n--------------------------------------");//mzbik mod 04.02.2015 The amount of agents remain the same.
			
			//mzbik add 05.02.2015 different approach for establishing the "movesCounter" is to (as described in Scheadule.class) :
			/** "... You can get the number of times that step(...) has been called on the
			schedule by calling the getSteps() method." */
			hhw.finish();
			
		}
		hhw.myReporter.finishAll();
	}

}
