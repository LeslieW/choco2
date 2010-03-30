package choco.kernel.solver.constraints.global.scheduling;

import java.io.Serializable;

public interface IResourceData extends Serializable{

	String getRscName();
	
	int getNbTasks();
	
	int getNbOptionalTasks();
	
	int getNbRegularTasks();
	
}
