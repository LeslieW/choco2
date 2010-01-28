package choco.kernel.solver.search.checker;

import java.util.Iterator;
import java.util.logging.Level;

import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.real.RealVar;
import choco.kernel.solver.variables.set.SetVar;

public abstract class AbstractSolutionCheckerEngine implements ISolutionCheckerEngine {

	@Override
	public final void checkConstraints(Solver solver) throws SolutionCheckerException {
		final Iterator<SConstraint> ctit = solver.getIntConstraintIterator();
		while (ctit.hasNext()) {
			checkConstraint(ctit.next());
		}		
	}

	@Override
	public void checkSolution(Solver solver) throws SolutionCheckerException {
		checkVariables(solver);
		checkConstraints(solver);		
	}

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

	@Override
	public final boolean inspectConstraints(Solver solver) {
		boolean isOk = true;
		Iterator<SConstraint> ctit =  solver.getIntConstraintIterator();
		while (ctit.hasNext()) {
			isOk &= inspectConstraint(ctit.next());
		}
		return isOk;
	}

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
