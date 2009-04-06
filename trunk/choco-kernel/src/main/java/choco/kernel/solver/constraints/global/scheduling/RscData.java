package choco.kernel.solver.constraints.global.scheduling;

import choco.kernel.model.ModelException;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;

public final class RscData implements IResourceData {

	private static int resourceIndex = 0;
	
	private final String name;
	
	private final int nbReq;
	
	private final int nbOpt;
	
	private final int nbTot;
	
	private final IntegerVariable uppBound;
	
	public RscData(String name, TaskVariable[] tasks,IntegerVariable[] usages, IntegerVariable uppBound) {
		super();
		if(tasks == null || tasks.length == 0) {
			throw new ModelException("Empty resource ?");
		}
		nbTot = tasks.length;
		if(usages == null) {
			nbOpt= 0;
		}else {
			nbOpt = usages.length;
			if(nbOpt>nbTot) {
				throw new ModelException("Invalid resource dimension.");
			}
			for (int i = 0; i < usages.length; i++) {
				if( ! usages[i].isBoolean()) {
					throw new ModelException(usages[i].pretty()+" should be a boolean variable.");
				}
			}
		}
		nbReq = nbTot - nbOpt;
		this.name = name == null ? "rsc"+(resourceIndex++) : name;
		this.uppBound = uppBound;
	}
	
	
	@Override
	public int getNbOptionalTasks() {
		return nbOpt;
	}

	@Override
	public int getNbRequiredTasks() {
		return nbReq;
	}

	@Override
	public int getNbTasks() {
		return nbTot;
	}

	@Override
	public String getRscName() {
		return name;
	}


	public final IntegerVariable getUppBound() {
		return uppBound;
	}


	
	
	

}
