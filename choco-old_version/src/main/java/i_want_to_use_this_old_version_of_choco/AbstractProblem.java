// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco;

import i_want_to_use_this_old_version_of_choco.integer.IntConstraint;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.IntVar;
import i_want_to_use_this_old_version_of_choco.mem.IEnvironment;
import i_want_to_use_this_old_version_of_choco.mem.recomputation.EnvironmentRecomputation;
import i_want_to_use_this_old_version_of_choco.mem.trailing.EnvironmentTrailing;
import i_want_to_use_this_old_version_of_choco.prop.AbstractPropagationEngine;
import i_want_to_use_this_old_version_of_choco.prop.ChocEngine;
import i_want_to_use_this_old_version_of_choco.prop.EventQueue;
import i_want_to_use_this_old_version_of_choco.prop.PropagationEngine;
import i_want_to_use_this_old_version_of_choco.real.RealVar;
import i_want_to_use_this_old_version_of_choco.set.SetVar;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

import java.util.Iterator;

public abstract class AbstractProblem extends AbstractModel {
    /**
     * Precision of the search for a real problem.
     */
    protected double precision = 1.0e-6;
    /**
     * Minimal width reduction between two propagations.
     */
    protected double reduction = 0.99;
    /**
     * Allows to know if the problem is feasible (null if it was not solved).
     */
    public Boolean feasible = null;
    /**
     * True if the problem was solved.
     */
    protected boolean solved = false;
    /**
     * The environment managing the backtrackable data.
     */
    protected IEnvironment environment;
    /**
     * The propagation engine to propagate during solving.
     */
    protected AbstractPropagationEngine propagationEngine;
    /**
     * The object controlling the global search exploration
     */
    protected Solver solver;

    public AbstractProblem(IEnvironment env) {
        super();
        this.solver = new Solver(this);
        this.propagationEngine = new ChocEngine(this);
        this.environment = env;
        this.constraints = env.makePartiallyStoredVector();

        if (env instanceof EnvironmentRecomputation)
            useRecomputation = true;

    }

    public AbstractProblem() {
        this(new EnvironmentTrailing());
    }

    /**
     * Returns the memory environment used by the problem.
     */

    public final IEnvironment getEnvironment() {
        return environment;
    }

    public int getIntVarIndex(IntDomainVar c) {
        return intVars.indexOf(c);
    }

    /**
     * retrieving the total number of constraints over integers
     *
     * @return the total number of constraints over integers in the problem
     */
    public final int getNbIntConstraints() {
        return this.constraints.size();
    }

    /**
     * <i>Network management:</i>
     * Retrieve a constraint by its index.
     *
     * @param i index of the constraint in the problem
     * @deprecated
     */

    public final IntConstraint getIntConstraint(int i) {
        return (IntConstraint) constraints.get(i);
    }

    public Iterator getIntConstraintIterator() {
        return new Iterator() {
            IntIterator it = constraints.getIndexIterator();

            public boolean hasNext() {
                return it.hasNext();
            }

            public Object next() {
                return constraints.get(it.next());
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * As all Entity, must be able to return a Problem, returns itself.
     */

    public final AbstractProblem getProblem() {
        return this;
    }

    /**
     * currentElement if the problem has been found to be feasible (there exist solutions) or not.
     * precondition : has to be called after a search
     * @return Boolean.TRUE if a solution was found,
     *         Boolean.FALSE if the problem was proven infeasible,
     *         null otherwise
     */
    public final Boolean isFeasible() {
        return feasible;
    }

    public final boolean isConsistent() {
        Iterator ctit = this.getIntConstraintIterator();
        while(ctit.hasNext()) {
            if (!((Propagator) ctit.next()).isConsistent())
                return false;
        }
        return true;
    }

    /**
     * Checks if all the variables are instantiated.
     */
    public boolean isCompletelyInstantiated() {
        int n = getNbIntVars();
        for (int i = 0; i < n; i++) {
            if (!(getIntVar(i).isInstantiated()))
                return false;
        }
        return true;
    }

    public String pretty() {
        StringBuffer buf = new StringBuffer("Pb[" + (getNbIntVars() + getNbRealVars() + getNbSetVars()) + " vars, " + getNbIntConstraints() + " cons]\n");
        buf.append(this.varsToString());
        buf.append(this.constraintsToString());
        return new String(buf);
    }

    public String varsToString() {
        StringBuffer buf = new StringBuffer("Pb[" + (getNbIntVars() + getNbRealVars() + getNbSetVars()) + " vars, " + getNbIntConstraints() + " cons]\n");
        buf.append("==== VARIABLES ====\n");
        for (int i = 0; i < getNbIntVars(); i++) {
            buf.append(getIntVar(i).pretty());
            buf.append("\n");
        }
        for (int i1 = 0; i1 < floatVars.size(); i1++) {
            Object floatVar = floatVars.get(i1);
            RealVar realVar = (RealVar) floatVar;
            buf.append(realVar.pretty());
        }
        for (int i = 0; i < setVars.size(); i++) {
            buf.append(getSetVar(i).pretty());
            buf.append("\n");
        }
        return new String(buf);
    }

    public String constraintsToString() {
        StringBuffer buf = new StringBuffer("Pb[" + (getNbIntVars() + getNbRealVars() + getNbSetVars()) + " vars, " + getNbIntConstraints() + " cons]\n");
        buf.append("==== CONSTRAINTS ====\n");
        IntIterator it = constraints.getIndexIterator();
        while (it.hasNext()) {
            int i = it.next();
            AbstractConstraint c = (AbstractConstraint) constraints.get(i);
            buf.append(c.pretty());
            buf.append("\n");
        }
        return new String(buf);
    }

    public String solutionToString() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < getNbIntVars(); i++) {
            IntVar v = getIntVar(i);
            if (v.isInstantiated()) {
                buf.append(v.toString());
                buf.append(", ");
            }
        }
        for (int j  = 0; j < getNbRealVars(); j++){
            RealVar v = getRealVar(j);
            if(v.isInstantiated()){
                buf.append(v.toString());
                buf.append(", ");
            }
        }

        for (int k  = 0; k < getNbSetVars(); k++){
            SetVar v = getSetVar(k);
            if(v.isInstantiated()){
                buf.append(v.toString());
                buf.append(", ");
            }
        }
        return new String(buf);
    }

    /**
     * removes (permanently) a constraint from the constraint network
     * Beware, this is a permanent removal, it may not be backtracked
     * Warnin : For a composition of constraint by boolean connectors, only the root constraint may be removed
     */
    public void eraseConstraint(Constraint c) {
        constraints.remove(c);
        Constraint rootConstraint = ((AbstractConstraint) c).getRootConstraint();
        if (rootConstraint == c) {
            ((AbstractConstraint) c).setPassive();
            for (int i = 0; i < c.getNbVars(); i++) {
                AbstractVar v = (AbstractVar) c.getVar(i);
                v.eraseConstraint(c);
            }
        }
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public double getReduction() {
        return reduction;
    }

    public void setReduction(double reduction) {
        this.reduction = reduction;
    }

    /**
     * Returns the propagation engine associated to the problem
     */

    public PropagationEngine getPropagationEngine() {
        return propagationEngine;
    }

    /**
     * accessor returning the handler of the object responsible for the resolution of the problem.
     * It may be either a local search solver or a global search solver
     *
     * @return the object responsible for solving the problem
     */
    public final Solver getSolver() {
        return solver;
    }

    /**
     * <i>Propagation:</i>
     * Computes consistency on the problem (the problem may no longer
     * be consistent since the last propagation because of listeners
     * that have been posted and variables that have been reduced
     *
     * @throws ContradictionException
     */

    public void propagate() throws ContradictionException {
        PropagationEngine pe = getPropagationEngine();
        boolean someEvents = true;
        while (someEvents) {
            EventQueue q = pe.getNextActiveEventQueue();
            if (q != null) {
                q.propagateSomeEvents();
            } else
                someEvents = false;
        }
        assert(pe.checkCleanState());
    }

    /**
     * pushing one world on the stack
     */
    public void worldPush() {
        environment.worldPush();
    }

    /**
     * pushing one world on the stack
     */
    public void worldPop() {
        environment.worldPop();
        propagationEngine.flushEvents();
    }

    /**
     * Backtracks to a given level in the search tree.
     */
    public final void worldPopUntil(int n) {
        while (environment.getWorldIndex() > n) {
            worldPop();
        }
    }

    /**
     * returning the index of the current worl
     */
    public final int getWorldIndex() {
        return environment.getWorldIndex();
    }

    public Boolean solve() {
        solver.firstSolution = true;
        solver.generateSearchSolver(this);
//    solver.getSearchSolver().incrementalRun();
        solver.launch();
        return isFeasible();
    }

    public Boolean nextSolution() {
        return solver.getSearchSolver().nextSolution();
    }

    /**
     * Displays all the runtime statistics.
     */
    public void printRuntimeSatistics() {
        (this.getSolver().getSearchSolver()).printRuntimeStatistics();
    }

    public abstract Boolean solve(boolean all);

    public Boolean solveAll() {
        solver.firstSolution = false;
        solver.generateSearchSolver(this);
//    solver.getSearchSolver().incrementalRun();
        solver.launch();
        return Boolean.TRUE;
    }

    /**
     * <i>Resolution:</i>
     * Searches for the solution minimizing the objective criterion.
     *
     * @param obj     The variable modelling the optimization criterion
     * @param restart If true, then a new search is restarted from scratch
     *                after each solution is found;
     *                otherwise a single branch-and-bound search is performed
     */

    public Boolean minimize(Var obj, boolean restart) {
        return optimize(false, obj, restart);
    }

    /**
     * <i>resolution:</i>
     * Searches for the solution maximizing the objective criterion.
     *
     * @param obj     The variable modelling the optimization criterion
     * @param restart If true, then a new search is restarted from scratch
     *                after each solution is found;
     *                otherwise a single branch-and-bound search is performed
     */
    public Boolean maximize(Var obj, boolean restart) {
        return optimize(true, obj, restart);
    }

    protected Boolean optimize(boolean maximize, Var obj, boolean restart) {
        solver.setDoMaximize(maximize);
        solver.setObjective(obj);
        solver.setRestart(restart);
        solver.setFirstSolution(false);
        solver.generateSearchSolver(this);
        solver.launch();
        return this.isFeasible();
    }


    public boolean useRecomputation() {
        return useRecomputation;
    }

    public void setRecomputation(boolean on) {
        useRecomputation = on;
    }
}
