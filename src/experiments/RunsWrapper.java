package experiments;

import model.FinancialModel;

public class RunsWrapper {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		FinancialModel hhw = new FinancialModel(System.currentTimeMillis());
		hhw.wrapperActive = true;

		for (int t = 0; t < 500; t++) {

			System.out.println("Run " + t);
			hhw.parameterMap.put("Cont_D", new Double(0.1*hhw.random.nextDouble()));
			System.out.println(hhw.parameterMap.get("Cont_D"));

			hhw.start();
			while ((hhw.schedule.step(hhw))) {
			}
			hhw.finish();

		}
		hhw.myReporter.finishAll();
	}

}
