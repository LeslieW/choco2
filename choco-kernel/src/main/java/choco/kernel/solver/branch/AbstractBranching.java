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

import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;

public abstract class AbstractBranching {

    static final String LOG_MSG_FORMAT = "{0} {1} {2} {3}";

    protected static final String LOG_MSG_FORMAT_WITH_BRANCH = "{0} {1} {2} branch {3}";

    protected static final String LOG_DECISION_MSG = "==";
    /**
   * the main control object (responsible for the whole exploration, while Eqthe branching object
   * is responsible only at the choice point level
   */
  private AbstractGlobalSearchStrategy manager;
  /**
   * a link towards the next branching object (once this one is exhausted)
   */
  private AbstractBranching nextBranching;


  public void setSolver(AbstractGlobalSearchStrategy s) {
    manager = s;
  }

    public AbstractGlobalSearchStrategy getManager(){
        return manager;
    }

  /**
   * Gets the next branching.
   * @return the next branching
   */
  public final AbstractBranching getNextBranching() {
    return nextBranching;
  }

  /**
   * Sets the next branching.
   * @param nextBranching the next branching
   */
  public final void setNextBranching(AbstractBranching nextBranching) {
    this.nextBranching = nextBranching;
  }

    /**
     * used for logging messages related to the search tree
     *
     * @param branchObject is the object of the branching
     * @param branchIndex  is the index of the branching
     * @return an string that will be printed between the branching object and the branch index
     *         Suggested implementations return LOG_DECISION_MSG[0] or LOG_DECISION_MSG[branchIndex]
     */
    public String getDecisionLogMsg(Object branchObject, int branchIndex) {
        StringBuffer st = new StringBuffer();
        st.append(branchObject);
        st.append(LOG_DECISION_MSG);
//        st.append(branchIndex);
        return st.toString();
    }

  /**
   * This method is called before launching the search. it may be used to intialiaze data structures or counters for
   * instance.
   */
  public void initBranching() {
    // Nothing to do by default
  }

    /**
     * this method is used to build the data structure in the branching for
     * the given constraint. This is used when the constraint was not present
     * at the initialization of the branching, for example a cut
     * @param c constraint
     */
  public void initConstraintForBranching(SConstraint c) {
    //nothing to do by default
  }

    /**
	 * a log message using java.util logging {} arguments </br>
	 * {0}: world index (formatter arguments)</br>
	 * {1}: Up or Down message </br>
	 * {2}: log message </br>
     * 
	 * {5}: Branch index </br>
	 *
	 * @return log message
	 */
    protected String getLogMessage() {
		return LOG_MSG_FORMAT;
	}
}
