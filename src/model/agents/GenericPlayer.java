package model.agents;

import model.FinancialModel;
import sim.engine.Steppable;

public abstract class GenericPlayer implements Steppable {
	
	// trader's id
	public int id;

	// instantiate ContModel class in order to access its variables
	public FinancialModel myWorld;

	public void setup(int i, FinancialModel target) {
		
		this.myWorld = target;
		this.id = i;
		target.schedule.scheduleRepeating(this, 1, 1.0);
		
	}

}
