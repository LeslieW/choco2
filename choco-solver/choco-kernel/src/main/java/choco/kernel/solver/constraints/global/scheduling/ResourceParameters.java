package choco.kernel.solver.constraints.global.scheduling;

import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;

public final class ResourceParameters implements IResourceParameters {

	private static final long serialVersionUID = 3665641934423408763L;

	private final String name;
	
	private final int nbReg;
	
	private final int nbOpt;
	
	private final int nbTot;
	
	private final boolean isHorizonDefined;
		
	public ResourceParameters(String name, TaskVariable[] tasks,IntegerVariable[] usages, IntegerVariable uppBound) {
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
				throw new ModelException("Invalid resource dimensions.");
			}
			for (int i = 0; i < usages.length; i++) {
				if( ! usages[i].isBoolean()) {
					throw new ModelException("Resource usage variable: "+usages[i].pretty()+" is not boolean.");
				}
			}
		}
		nbReg = nbTot - nbOpt;
		this.name = name == null ? StringUtils.randomName()+"-RSC" : name;
		this.isHorizonDefined = (uppBound != null);
	}

	public boolean isRegular() {
		return nbOpt == 0;
	}

	public boolean isAlternative() {
		return nbOpt > 0;
	}
	@Override
	public int getNbOptionalTasks() {
		return nbOpt;
	}

	@Override
	public int getNbRegularTasks() {
		return nbReg;
	}

	@Override
	public int getNbTasks() {
		return nbTot;
	}

	@Override
	public String getRscName() {
		return name;
	}

	public boolean isHorizonDefined() {
		return isHorizonDefined;
	}

	@Override
	public String toString() {
		return name +"("+nbReg+", "+nbOpt+")";
	}

	public final int getUsagesOffset() {
		return nbTot;
	}
	
	public final int getHeightsOffset() {
		return nbTot + nbOpt;
	}
	
	public final int getConsOffset() {
		return nbTot + nbOpt + nbTot;
	}
	
	public final int getCapaOffset() {
		return nbTot + nbOpt + nbTot + 1;
	}
		
}
