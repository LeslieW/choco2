package choco.kernel.solver.search.checker;

import java.util.logging.Level;

import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.constraints.integer.AbstractIntSConstraint;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;



public class SolutionCheckerEngine extends AbstractSolutionCheckerEngine {
	
	public boolean enableConsistency;

    private String reuseLabel;


	public SolutionCheckerEngine() {
		super();
		enableConsistency = true;
	}


    /**
     * Return if the constraints checking enables consistency.
     * @return true if consistency is checked.
     */
	public final boolean isConsistencyEnabled() {
		return enableConsistency;
	}


    /**
     * Enable consistency for constraints checking.
     * @param enableConsistency true if constraint consistency must be check
     */
	public final void setEnableConsistency(boolean enableConsistency) {
		this.enableConsistency = enableConsistency;
	}


    /**
     * Inspect the instantiation of {@code var}.
     * @param var variable to check
     * @return false if the variable is not instantiated.
     */
	public boolean inspectVariable(final Var var) {
		if ( ! var.isInstantiated() ) {
			if(LOGGER.isLoggable(Level.INFO)) {
				LOGGER.log(Level.INFO, "  FAILURE Not Instantiated: {0}",var.pretty());
			}
			return false;
		}
		return true;
	}

    /**
     * Check the instantiation of {@code var}.
     * @param var variable to check
     * @throws SolutionCheckerException if {@code var} is not instantiated.
     */
	public void checkVariable(final Var var) throws SolutionCheckerException {
		if ( ! var.isInstantiated() ) {
			throw new SolutionCheckerException("FAILURE Not Instantiated: "+var.pretty());
		}
	}

    /**
     * Check the satisfaction of {@code c}.
     * @param c constraint to check
     * @throws SolutionCheckerException if {@code c} is not satisfied
     */
	public void checkConstraint(SConstraint c) throws SolutionCheckerException {
		if( ! isSatisfied(c)) {
			throw new SolutionCheckerException("FAILURE "+reuseLabel+": "+c.pretty());
		}
		
	}

    /**
     * Inspect the satisfaction of {@code c}.
     * @param c contraint to check
     * @return false if the constraint is not satisfied.
     */
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

    /**
     * Check if {@code c} is satisfied.
     * @param c constraint to check.
     * @return false if the {@code c} is not satisfied.
     */
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

    /**
     * Check isSatisfied on {@code ic}.
     * {@code ic} is an integer constraint and allows consistency checking (with tuple).
     * @param ic integer constraint to check
     * @return false if the {@code c} is satisfied
     * @throws UnsupportedOperationException if {@code ic} doesn't allow consistency checking.
     */
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
			reuseLabel = "Fully Instantiated isSatisified(int[])"; 
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
