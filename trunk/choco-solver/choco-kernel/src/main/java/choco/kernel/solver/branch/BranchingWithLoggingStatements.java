/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.kernel.solver.branch;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.search.IntBranchingDecision;

import java.util.logging.Level;

public class BranchingWithLoggingStatements extends AbstractIntBranchingStrategy {

	/**
	 * prefixes for log statements (visualize search depth)
	 */
	public final static String[] LOG_PREFIX = { "", ".", "..", "...", "....",
		".....", "......", ".......", "........", ".........", ".........." };

	public final AbstractIntBranchingStrategy internalBranching;

	private int nextInformationNode = ChocoLogging.getEveryXNodes();

	public BranchingWithLoggingStatements(AbstractIntBranchingStrategy internalBranching) {
		super();
		this.internalBranching = internalBranching;
		this.setSolver(internalBranching.manager);
	}

	@Override
	public boolean finishedBranching(IntBranchingDecision decision) {
		return internalBranching.finishedBranching(decision);
	}

	@Override
	public String getDecisionLogMessage(IntBranchingDecision decision) {
		throw new SolverException("What are you doing ? It is the logging wrapper !");
	}


	public final static StringBuilder makeLoggingMsgPrefix(int worldStamp) {
		StringBuilder b  =new StringBuilder();
		//		b.append(LOG_PREFIX[worldStamp % (LOG_PREFIX.length)]);
		b.append(StringUtils.pad("", worldStamp, "."));
		b.append('[').append(worldStamp).append(']');
		return b;
	}

	protected String makeLoggingMessage(IntBranchingDecision decision, String dirMsg,int worldStamp) {
		StringBuilder b  = makeLoggingMsgPrefix(worldStamp);
		b.append(' ').append(dirMsg);
		b.append(internalBranching.getDecisionLogMessage(decision));
		b.append(" branch ").append(decision.getBranchIndex());
		return new String(b);
	}

	@Override
	public void goDownBranch(IntBranchingDecision decision)
	throws ContradictionException {
		if(LOGGER.isLoggable(Level.INFO)) {
			if(manager.getNodeCount() >= nextInformationNode) {
				LOGGER.log(Level.INFO, "- Partial Search - {0}.", manager.partialRuntimeStatistics(false));
				nextInformationNode = manager.getNodeCount() + ChocoLogging.getEveryXNodes();
				ChocoLogging.flushLogs();
			}  
			if (LOGGER.isLoggable(Level.CONFIG) &&
					manager.solver.getWorldIndex()  < ChocoLogging.getLoggingMaxDepth()) {
				LOGGER.log(Level.CONFIG, makeLoggingMessage(decision, LOG_DOWN_MSG, manager.solver.getWorldIndex()));
				ChocoLogging.flushLogs();
			}
		}
		internalBranching.goDownBranch(decision);
	}

	@Override
	public void goUpBranch(IntBranchingDecision decision)
	throws ContradictionException {
		if ( LOGGER.isLoggable(Level.CONFIG) 
				&& manager.solver.getWorldIndex() + 1 < ChocoLogging.getLoggingMaxDepth()) {
			LOGGER.log(Level.CONFIG, makeLoggingMessage(decision, LOG_UP_MSG, manager.solver.getWorldIndex() + 1));
		}
		internalBranching.goUpBranch(decision);

	}

	@Override
	public void setFirstBranch(IntBranchingDecision decision) {
		internalBranching.setFirstBranch(decision);

	}

	@Override
	public void setNextBranch(IntBranchingDecision decision) {
		internalBranching.setNextBranch(decision);

	}

	@Override
	public Object selectBranchingObject() throws ContradictionException {
		return internalBranching.selectBranchingObject();
	}


	@Override
	public void initBranching() {
		super.initBranching();
		internalBranching.initBranching();
	}

	@Override
	public void initConstraintForBranching(SConstraint c) {
		super.initConstraintForBranching(c);
		internalBranching.initConstraintForBranching(c);
	}

	public final static AbstractIntBranchingStrategy setLoggingStatement(AbstractIntBranchingStrategy goal) {
		AbstractIntBranchingStrategy res = new BranchingWithLoggingStatements(goal);
		if (goal.getNextBranching() != null) {
			if(goal.getNextBranching() instanceof AbstractIntBranchingStrategy) {
				res.setNextBranching(setLoggingStatement((AbstractIntBranchingStrategy) goal.getNextBranching()));
			}else {
				throw new SolverException("cant set logging statements");
			}
		}
		return res;
	}
}
