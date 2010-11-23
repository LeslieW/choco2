package choco.kernel.solver.constraints.global.scheduling;

import java.io.Serializable;

public interface IResourceParameters extends Serializable{

	String getRscName();
	
	int getNbTasks();
	
	int getNbRegularTasks();
	
	int getNbOptionalTasks();
	
}
