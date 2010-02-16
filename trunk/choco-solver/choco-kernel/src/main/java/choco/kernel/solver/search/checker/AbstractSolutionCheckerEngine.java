package choco.kernel.solver.search.checker;

import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.real.RealVar;
import choco.kernel.solver.variables.set.SetVar;

import java.util.Iterator;
import java.util.logging.Level;

public abstract class AbstractSolutionCheckerEngine implements ISolutionCheckerEngine {

    /**
     * Check satisfaction of every constraints involved within the {@code solver}.
     * @param solver containing solver
     * @throws SolutionCheckerException if one or more constraint is not satisfied.
     */
	@Override
	public final void checkConstraints(Solver solver) throws SolutionCheckerException {
		final Iterator<SConstraint> ctit = solver.getConstraintIterator();
		while (ctit.hasNext()) {
			checkConstraint(ctit.next());
		}		
	}

     /**
     * Check the current solution of the {@code solver}.
     * It runs over variables (check instantiation) and constraints (call isSatisfied).
     * By defautlt, it checks the consistency and ignore the nogood recording.
     *
     * @param solver involving solver
     * @throws SolutionCheckerException if the current solution is not correct.
     */
	@Override
	public void checkSolution(Solver solver) throws SolutionCheckerException {
		checkVariables(solver);
		checkConstraints(solver);		
	}

    /**
     * Check instantiation of every variables involved within the {@code solver}.
     * @param solver containing solver
     * @throws SolutionCheckerException if one or more variable is not instantiated.
     */
	@Override
	public final void checkVariables(Solver solver) throws SolutionCheckerException {
		final Iterator<IntDomainVar> ivIter = solver.getIntVarIterator();
		while(ivIter.hasNext()) {
			checkVariable(ivIter.next());
		}
		final Iterator<SetVar> svIter = solver.getSetVarIterator();
		while(svIter.hasNext()) {
			checkVariable(svIter.next());
		}
		final Iterator<RealVar> rvIter = solver.getRealVarIterator();
		while(rvIter.hasNext()) {
			checkVariable(rvIter.next());
		}		
	}

    /**
     * Inspect satisfaction of every constraints declared in {@code solver}.
     * @param solver containing solver
     * @return false if one or more constraint is not satisfied.
     */
	@Override
	public final boolean inspectConstraints(Solver solver) {
		boolean isOk = true;
		Iterator<SConstraint> ctit =  solver.getConstraintIterator();
		while (ctit.hasNext()) {
			isOk &= inspectConstraint(ctit.next());
		}
		return isOk;
	}

    /**
     * Inspect the current solution of {@code solver}.
     * It runs over variables (check instantiation) and constraints (call isSatisfied).
     * By defautlt, it checks the consistency and ignore the nogood recording.
     * @param solver involving solver
     * @return false if the current solution is not correct
     */
	@Override
	public boolean inspectSolution(Solver solver) {
		LOGGER.log(Level.INFO, "- Check solution: {0}", this.getClass().getSimpleName());
		boolean isOk = true;
		if ( inspectVariables(solver) ) LOGGER.info("- Check solution: Every variables are instantiated.");
		else {
			isOk = false;
			LOGGER.severe("- Check solution: Some variables are not instantiated.");
		}
		if(inspectConstraints(solver)) LOGGER.info("- Check solution: Every constraints are satisfied.");
		else {
			isOk= false;
			LOGGER.severe("- Check solution: Some constraints are not satisfied.");
		}
		return isOk;
	}

    /**
     * Inspect instantiation of every variables involved in {@code solver}.
     * @param solver containing solver.
     * @return false if one or more variable is not instantiated.
     */
	@Override
	public final boolean inspectVariables(Solver solver) {
		boolean isOk = true;
		final Iterator<IntDomainVar> ivIter = solver.getIntVarIterator();
		while(ivIter.hasNext()) {
			isOk &= inspectVariable(ivIter.next());
		}
		final Iterator<SetVar> svIter = solver.getSetVarIterator();
		while(svIter.hasNext()) {
			isOk &= inspectVariable(svIter.next());
		}
		final Iterator<RealVar> rvIter = solver.getRealVarIterator();
		while(rvIter.hasNext()) {
			isOk &= inspectVariable(rvIter.next());
		}
		return isOk;
	}


}
