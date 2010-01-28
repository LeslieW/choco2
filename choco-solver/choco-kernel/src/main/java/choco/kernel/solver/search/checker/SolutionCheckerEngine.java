package choco.kernel.solver.search.checker;

import java.util.logging.Level;

import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.integer.AbstractIntSConstraint;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;



public class SolutionCheckerEngine extends AbstractSolutionCheckerEngine {

	//TODO 
	public boolean ignoreNogoodStore;
	
	public boolean enableConsistency;

	public SolutionCheckerEngine() {
		super();
		enableConsistency = true;
		ignoreNogoodStore = false;
	}

	

	public final boolean isConsistencyEnabled() {
		return enableConsistency;
	}



	public final void setEnableConsistency(boolean enableConsistency) {
		this.enableConsistency = enableConsistency;
	}



	public boolean inspectVariable(final Var var) {
		if ( ! var.isInstantiated() ) {
			if(LOGGER.isLoggable(Level.INFO)) {
				LOGGER.log(Level.INFO, "  FAILURE Not Instantiated: {0}",var.pretty());
			}
			return false;
		}
		return true;
	}

	public void checkVariable(final Var var) throws SolutionCheckerException {
		if ( ! var.isInstantiated() ) {
			throw new SolutionCheckerException("FAILURE Not Instantiated: "+var.pretty());
		}
	}


	@Override
	public void checkConstraint(SConstraint c) throws SolutionCheckerException {
		if( ! isSatisfied(c)) {
			throw new SolutionCheckerException("FAILURE "+reuseLabel+": "+c.pretty());
		}
		
	}



	private String reuseLabel;

	public boolean inspectConstraint(SConstraint c) {
		if( isSatisfied(c)) {
			if(LOGGER.isLoggable(Level.CONFIG)) {
				LOGGER.log(Level.CONFIG, "  {0}: {1}", new Object[]{reuseLabel, c.pretty()});
			}
			return true;
		}else if(LOGGER.isLoggable(Level.INFO)) {
			LOGGER.log(Level.INFO, "  FAILURE {0}: {1}", new Object[]{reuseLabel, c.pretty()});
		}
		return false;
	}

	protected boolean isSatisfied(SConstraint c) {
		boolean isOk;
		if(c instanceof AbstractIntSConstraint){
			try{
				isOk = isSatisfied( (AbstractIntSConstraint) c);
			}catch (UnsupportedOperationException e){
				LOGGER.log(Level.WARNING,"- Check solution: isSatisified(int[]) is not implemented: {0}", c);
				reuseLabel = "is isSatisified()";
				isOk = c.isSatisfied();
			}
		}else{
			reuseLabel = "is isSatisified()";
			isOk = c.isSatisfied();
		}
		return isOk;
	}

	protected boolean isSatisfied(AbstractIntSConstraint ic)throws UnsupportedOperationException  {
		int[] tuple = new int[ic.getNbVars()];
		int[] tupleL = new int[ic.getNbVars()];
		int[] tupleU = new int[ic.getNbVars()];
		boolean fullInstantiated = true;
		for(int i = 0; i < ic.getNbVars(); i++){
			IntDomainVar v = (IntDomainVar)ic.getVar(i);
			if(v.isInstantiated()){
				tuple[i] = v.getVal();
				tupleL[i] = tuple[i];
				tupleU[i] = tuple[i];
			}else{
				fullInstantiated = false;
				tupleL[i] = v.getInf();
				tupleU[i] = v.getSup();
			}
		}
		boolean isOk = true;
		if(fullInstantiated){
			reuseLabel = "Fully Instatiated isSatisified(int[])"; 
			isOk = ic.isSatisfied(tuple);
		}else if(enableConsistency){
			reuseLabel = "Consistency isSatisfied(int[])";
			isOk = ic.isSatisfied(tupleL) && ic.isSatisfied(tupleU);
		} else {
			reuseLabel= "No Consistency Test";
		}
		return isOk;
	}

}
