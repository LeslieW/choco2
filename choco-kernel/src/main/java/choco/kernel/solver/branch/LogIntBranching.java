/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien    1999-2009      *
 **************************************************/
package choco.kernel.solver.branch;

import choco.kernel.common.logging.WorldFormatter;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;

import java.util.logging.Level;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 28 juil. 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*
* A class to move logging out of the branching.
* It contains a wrapper (AbstractIntBranching object) and
* deal with log messages.
*/
public final class LogIntBranching
        extends AbstractIntBranching{


  public final static String LOG_DOWN_MSG = "down branch ";
  public final static String LOG_UP_MSG = "up branch ";
  
    private final AbstractIntBranching wrapper;

    public LogIntBranching(AbstractIntBranching wrapper) {
        this.wrapper = wrapper;
    }

    public final void setSolver(AbstractGlobalSearchStrategy s) {
        super.setSolver(s);
        wrapper.setSolver(s);
    }

    @Override
    public final AbstractGlobalSearchStrategy getManager() {
        return wrapper.getManager();
    }

    private void logDownBranch(final Object x, final int i) {
		if (LOGGER.isLoggable(Level.INFO)) {
			final WorldFormatter wl = new WorldFormatter(getManager());
			if ( wl.isLoggable(getManager())) {
				LOGGER.log(Level.INFO, wrapper.getLogMessage(), new Object[]{wl,
                        LOG_DOWN_MSG, wrapper.getDecisionLogMsg(x, i), i});
			}
		}
	}

    private void logUpBranch(final Object x, final int i) {
		if (LOGGER.isLoggable(Level.INFO)) {
			final WorldFormatter wl = new WorldFormatter(getManager(), 1);
			if ( wl.isLoggable(getManager())) {
				LOGGER.log(Level.INFO, wrapper.getLogMessage(), new Object[]{wl,
                        LOG_UP_MSG, wrapper.getDecisionLogMsg(x, i), i});
			}
		}
	}

    /**
     * used for logging messages related to the search tree
     *
     * @param branchObject is the object of the branching
     * @param branchIndex  is the index of the branching
     * @return an string that will be printed between the branching object and the branch index
     *         Suggested implementations return LOG_DECISION_MSG[0] or LOG_DECISION_MSG[branchIndex]
     */
    @Override
    public String getDecisionLogMsg(Object branchObject, int branchIndex) {
        return wrapper.getDecisionLogMsg(branchObject, branchIndex);
    }

    /**
     * Performs the action,
     * so that we go down a branch from the current choice point.
     *
     * @param x the object on which the alternative is set
     * @param i the label of the branch that we want to go down
     * @throws choco.kernel.solver.ContradictionException
     *          if a domain empties or a contradiction is
     *          infered
     */
    @Override
    public void goDownBranch(Object x, int i) throws ContradictionException {
        logDownBranch(x,i);
        wrapper.goDownBranch(x,i);
    }

    /**
     * Performs the action,
     * so that we go up the current branch to the father choice point.
     *
     * @param x the object on which the alternative has been set
     *          at the father choice point
     * @param i the label of the branch that has been travelled down
     *          from the father choice point
     * @throws choco.kernel.solver.ContradictionException
     *          if a domain empties or a contradiction is
     *          infered
     */
    @Override
    public void goUpBranch(Object x, int i) throws ContradictionException {
        logUpBranch(x,i);
        wrapper.goUpBranch(x,i);
    }

    /**
     * Computes the search index of the first branch of the choice point.
     *
     * @param x the object on which the alternative is set
     * @return the index of the first branch
     */
    @Override
    public int getFirstBranch(Object x) {
        return wrapper.getFirstBranch(x);
    }

    /**
     * Computes the search index of the next branch of the choice point.
     *
     * @param x the object on which the alternative is set
     * @param i the index of the current branch
     * @return the index of the next branch
     */
    @Override
    public int getNextBranch(Object x, int i) {
        return wrapper.getNextBranch(x,i);
    }

    /**
     * This method is called before launching the search. it may be used to intialiaze data structures or counters for
     * instance.
     */
    @Override
    public void initBranching() {
        wrapper.initBranching();
    }

    /**
     * this method is used to build the data structure in the branching for
     * the given constraint. This is used when the constraint was not present
     * at the initialization of the branching, for example a cut
     *
     * @param c constraint
     */
    @Override
    public void initConstraintForBranching(SConstraint c) {
        wrapper.initConstraintForBranching(c);
    }

    /**
     * selecting the object under scrutiny (that object on which an alternative will be set)
     *
     * @return the object on which an alternative will be set (often  a variable)
     */
    @Override
    public Object selectBranchingObject() throws ContradictionException {
        return wrapper.selectBranchingObject();
    }

    /**
     * Checks whether all branches have already been explored at the
     * current choice point.
     *
     * @param x the object on which the alternative is set
     * @param i the index of the last branch
     * @return true if no more branches can be generated
     */
    @Override
    public boolean finishedBranching(Object x, int i) {
        return wrapper.finishedBranching(x,i);
    }

    /**
     * Get the wrapped branching
     * @return initial branching
     */
    public AbstractIntBranching getWrapper() {
        return wrapper;
    }

    /**
     * Check wether it is a logged branching
     *
     * @return false if it is not the case
     */
    @Override
    public final boolean isLogged() {
        return true;
    }

    /**
     * a log message using java.util logging {} arguments </br>
     * {0}: world index (formatter arguments)</br>
     * {1}: Up or Down message </br>
     * {2}: log message </br>
     * {5}: Branch index </br>
     *
     * @return log message
     */
    @Override
    protected String getLogMessage() {
        return wrapper.getLogMessage();
    }
}
