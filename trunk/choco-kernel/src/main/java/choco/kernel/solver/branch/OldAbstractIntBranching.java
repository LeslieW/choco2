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

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;
import choco.kernel.solver.search.IntBranchingDecision;

import java.util.logging.Logger;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 7 août 2009
* Since : Choco 2.1.0
* Update : Choco 2.1.0
*
*
* A specific  
*/
@Deprecated
public abstract class OldAbstractIntBranching {

    /**
       * the main control object (responsible for the whole exploration, while the branching object
       * is responsible only at the choice point level
       */
      protected AbstractGlobalSearchStrategy manager;
      /**
       * a link towards the next branching object (once this one is exhausted)
       */
      protected AbstractBranching nextBranching;
      /**
       * an object for logging trace statements
       */
      protected static Logger logger = Logger.getLogger("choco.kernel.solver.search.branching");

      public static String LOG_DOWN_MSG = "down branch ";
      public static String LOG_UP_MSG = "up branch ";
      public String[] LOG_DECISION_MSG = {""};


    public void setSolver(AbstractGlobalSearchStrategy s){
        manager = s;
    }

      /**
       * Gets the next branching.
       * @return the next branching
       */
      public AbstractBranching getNextBranching() {
        return nextBranching;
      }

      /**
       * Sets the next branching.
       * @param nextBranching the next branching
       */
      public void setNextBranching(AbstractBranching nextBranching) {
        this.nextBranching = nextBranching;
      }

      /**
       * used for logging messages related to the search tree
       * @param branchIndex is the index of the branching
       * @return an string that will be printed between the branching object and the branch index
       * Suggested implementations return LOG_DECISION_MSG[0] or LOG_DECISION_MSG[branchIndex]
       */
      public abstract String getDecisionLogMsg(int branchIndex);

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
         * @param c
         */
      public void initConstraintForBranching(SConstraint c) {
        //nothing to do by default
      }


    /**
      * selecting the object under scrutiny (that object on which an alternative will be set)
      *
      * @return the object on which an alternative will be set (often  a variable)
      */
    
    public abstract Object selectBranchingObject() throws ContradictionException;

/**
   * Performs the action,
   * so that we go down a branch from the current choice point.
   * @param x the object on which the alternative is set
   * @param i the label of the branch that we want to go down
   * @throws choco.kernel.solver.ContradictionException if a domain empties or a contradiction is
   * infered
   */
    public abstract void goDownBranch(Object x, int i) throws ContradictionException;

/**
   * Performs the action,
   * so that we go up the current branch to the father choice point.
   * @param x the object on which the alternative has been set
   * at the father choice point
   * @param i the label of the branch that has been travelled down
   * from the father choice point
   * @throws choco.kernel.solver.ContradictionException if a domain empties or a contradiction is
   * infered
   */
    
    public abstract void goUpBranch(Object x, int i) throws ContradictionException;


/**
   * Computes the search index of the first branch of the choice point.
   * @param x the object on which the alternative is set
   * @return the index of the first branch
   */    
    public abstract int getFirstBranch(Object x);

  /**
   * Computes the search index of the next branch of the choice point.
   * @param x the object on which the alternative is set
   * @param i the index of the current branch
   * @return the index of the next branch
   */
  public abstract int getNextBranch(Object x, int i);

  /**
   * Checks whether all branches have already been explored at the
   * current choice point.
   * @param x the object on which the alternative is set
   * @param i the index of the last branch
   * @return true if no more branches can be generated
   */
  public abstract boolean finishedBranching(Object x, int i);


    public class OldHeurWrapper extends AbstractIntBranching{
        OldAbstractIntBranching o;


        public OldHeurWrapper(OldAbstractIntBranching o) {
            this.o = o;
        }

        /**
         * selecting the object under scrutiny (that object on which an alternative will be set)
         *
         * @return the object on which an alternative will be set (often  a variable)
         */
        @Override
        public Object selectBranchingObject() throws ContradictionException {
            return o.selectBranchingObject();
        }

        /**
         * Performs the action,
         * so that we go down a branch from the current choice point.
         *
         * @param decision the decision to apply.
         * @throws choco.kernel.solver.ContradictionException
         *          if a domain empties or a contradiction is
         *          infered
         */
        @Override
        public void goDownBranch(IntBranchingDecision decision) throws ContradictionException {
            o.goDownBranch(decision.getBranchingObject(), decision.getBranchingValue());
        }

        /**
         * Performs the action,
         * so that we go up the current branch to the father choice point.
         *
         * @param decision the decision that has been set at the father choice point
         * @throws choco.kernel.solver.ContradictionException
         *          if a domain empties or a contradiction is
         *          infered
         */
        @Override
        public void goUpBranch(IntBranchingDecision decision) throws ContradictionException {
            o.goUpBranch(decision.getBranchingObject(), decision.getBranchingValue());
        }

        /**
         * compute the first decision by setting a branching value or modifying the branching object
         *
         * @param decision the current decision
         */
        @Override
        public void setFirstBranch(IntBranchingDecision decision) {
            decision.setBranchingValue(o.getFirstBranch(decision.getBranchingObject()));
        }

        /**
         * compute the next decision by setting a branching value or modifying the branching object
         *
         * @param decision the current decision
         */
        @Override
        public void setNextBranch(IntBranchingDecision decision) {
            decision.setBranchingValue(o.getNextBranch(decision.getBranchingObject(), decision.getBranchIndex()));
        }

        /**
         * Checks whether all branches have already been explored at the
         * current choice point.
         *
         * @param decision the last decision applied
         * @return true if no more branches can be generated
         */
        @Override
        public boolean finishedBranching(IntBranchingDecision decision) {
            return o.finishedBranching(decision.getBranchingObject(), decision.getBranchIndex());
        }

        /**
         * The logging message associated with the current decision.
         *
         * @param decision current decision
         * @return logging message.
         */
        @Override
        public String getDecisionLogMessage(IntBranchingDecision decision) {
            return decision.getBranchingObject() + ":" +o.getDecisionLogMsg(decision.getBranchIndex());
        }
    }
}

