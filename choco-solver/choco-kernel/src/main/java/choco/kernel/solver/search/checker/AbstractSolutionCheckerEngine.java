package choco.kernel.solver.search.checker;

import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.real.RealVar;
import choco.kernel.solver.variables.set.SetVar;

import java.util.logging.Level;

public abstract class AbstractSolutionCheckerEngine implements ISolutionCheckerEngine {

    /**
     * Check satisfaction of every constraints involved within the {@code solver}.
     * @param solver containing solver
     * @throws SolutionCheckerException if one or more constraint is not satisfied.
     */
	@Override
	public final void checkConstraints(Solver solver) throws SolutionCheckerException {
		final DisposableIterator<SConstraint> ctit = solver.getConstraintIterator();
		try{
            while (ctit.hasNext()) {
                checkConstraint(ctit.next());
            }
        }finally {
            ctit.dispose();
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
		final DisposableIterator<IntDomainVar> ivIter = solver.getIntVarIterator();
		while(ivIter.hasNext()) {
			checkVariable(ivIter.next());
		}
        ivIter.dispose();
		final DisposableIterator<SetVar> svIter = solver.getSetVarIterator();
		while(svIter.hasNext()) {
			checkVariable(svIter.next());
		}
        svIter.dispose();
		final DisposableIterator<RealVar> rvIter = solver.getRealVarIterator();
		while(rvIter.hasNext()) {
			checkVariable(rvIter.next());
		}
        rvIter.dispose();
	}

    /**
     * Inspect satisfaction of every constraints declared in {@code solver}.
     * @param solver containing solver
     * @return false if one or more constraint is not satisfied.
     */
	@Override
	public final boolean inspectConstraints(Solver solver) {
		boolean isOk = true;
		DisposableIterator<SConstraint> ctit =  solver.getConstraintIterator();
		while (ctit.hasNext()) {
			isOk &= inspectConstraint(ctit.next());
		}
        ctit.dispose();
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
		final DisposableIterator<IntDomainVar> ivIter = solver.getIntVarIterator();
		while(ivIter.hasNext()) {
			isOk &= inspectVariable(ivIter.next());
		}
        ivIter.dispose();
		final DisposableIterator<SetVar> svIter = solver.getSetVarIterator();
		while(svIter.hasNext()) {
			isOk &= inspectVariable(svIter.next());
		}
        svIter.dispose();
		final DisposableIterator<RealVar> rvIter = solver.getRealVarIterator();
		while(rvIter.hasNext()) {
			isOk &= inspectVariable(rvIter.next());
		}
        rvIter.dispose();
		return isOk;
	}


}