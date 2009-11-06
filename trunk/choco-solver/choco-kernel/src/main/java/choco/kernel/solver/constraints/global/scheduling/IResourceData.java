package choco.kernel.solver.constraints.global.scheduling;

public interface IResourceData {

	String getRscName();
	
	int getNbTasks();
	
	int getNbOptionalTasks();
	
	int getNbRequiredTasks();
	
}
