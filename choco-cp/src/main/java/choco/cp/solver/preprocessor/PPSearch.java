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
package choco.cp.solver.preprocessor;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.BoundAllDiff;
import choco.cp.solver.constraints.global.matching.AllDifferent;
import choco.cp.solver.constraints.global.scheduling.Cumulative;
import choco.cp.solver.constraints.integer.DistanceXYC;
import choco.cp.solver.constraints.integer.DistanceXYZ;
import choco.cp.solver.constraints.integer.IntLinComb;
import choco.cp.solver.constraints.integer.bool.BoolIntLinComb;
import choco.cp.solver.constraints.integer.channeling.ReifiedIntSConstraint;
import choco.cp.solver.search.integer.branching.AssignVar;
import choco.cp.solver.search.integer.branching.DomOverWDegBranching;
import choco.cp.solver.search.integer.branching.ImpactBasedBranching;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.cp.solver.search.integer.varselector.DomOverDynDeg;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.kernel.model.constraints.ConstraintType;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.AbstractVar;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: Aug 15, 2008
 * Time: 4:15:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class PPSearch {

    protected CPModel mod;

    public void setModel(CPModel m) {
        this.mod = m;
    }

    public boolean isNaryExtensional() {
        return mod.getNbConstraintByType(ConstraintType.TABLE) > 0;
    }

    public boolean isSat() {
        return mod.getNbConstraintByType(ConstraintType.CLAUSES) > 0;
    }

    public boolean isScheduling() {
        return mod.getNbConstraintByType(ConstraintType.DISJUNCTIVE) != 0;
    }

    public boolean isReified() {
        return mod.getConstraintByType(ConstraintType.REIFIEDINTCONSTRAINT).hasNext();
    }

    public boolean isMixedScheduling() {
        return mod.getNbConstraintByType(ConstraintType.DISJUNCTIVE) +
                mod.getNbConstraintByType(ConstraintType.PRECEDING) +
                mod.getNbConstraintByType(ConstraintType.LEQ) +
                mod.getNbConstraintByType(ConstraintType.LT) +
                mod.getNbConstraintByType(ConstraintType.GT) +
                mod.getNbConstraintByType(ConstraintType.GEQ) !=
                mod.getNbConstraints();
    }

    /**
     * set the DomOverDeg heuristic
     *
     * @param s
     * @return true if the problem was not detected infeasible in the process
     */
    public boolean setVersatile(CPSolver s, int inittime) {
        int h = determineHeuristic(s);
        if (h == 2) return setImpact(s, inittime);
        else return setDomOverWeg(s, inittime);
    }

    /**
     * set the DomOverDeg heuristic
     *
     * @param s
     * @return true if the problem was not detected infeasible in the process
     */
    public boolean setDomOverDeg(CPSolver s) {
        s.setVarIntSelector(new DomOverDynDeg(s));
        s.setValIntIterator(new IncreasingDomain());
        return true;
    }

    /**
     * set the DomOverWDeg heuristic
     *
     * @param s
     * @return true if the problem was not detected infeasible in the process
     */
    public boolean setDomOverWeg(CPSolver s, int inittime) {
        if (isSat() && s.getNbIntConstraints() == 1) {
            //when there is a single constraint domOverWdeg makes no sense
            return setImpact(s, inittime);
        } else {
            if (isScheduling()) {
                if (!isMixedScheduling()) { //pure scheduling
                    DomOverWDegBranching dwd = new DomOverWDegBranching(s, new IncreasingDomain());
                    dwd.setBranchingVars(getBooleanVars(s));
                    s.attachGoal(dwd);
                    AssignVar dwd2 = new AssignVar(new MinDomain(s, getOtherVars(s)), new IncreasingDomain());
                    s.addGoal(dwd2);
                } else {                    //side constraints added
                    DomOverWDegBranching dwd = new DomOverWDegBranching(s, new IncreasingDomain());
                    dwd.setBranchingVars(concat(getBooleanVars(s), getOtherVars(s)));
                    s.attachGoal(dwd);
                }
//            } else if (isReified()) { //some constraints are reified (decide them before the rest)
//                DomOverWDegBranching dwd = new DomOverWDegBranching(s, new IncreasingDomain());
//                dwd.setBranchingVars(getBooleanVars(s));
//                s.attachGoal(dwd);
//                AssignVar dwd2 = new AssignVar(new MinDomain(s, getOtherVars(s)), new IncreasingDomain());
//                s.addGoal(dwd2);
            } else {                        //general case
                DomOverWDegBranching dwd = new DomOverWDegBranching(s, new IncreasingDomain());
                s.attachGoal(dwd);
            }
            return true;
        }
    }


    /**
     * set the Impact heuristic
     *
     * @param s
     * @return true if the problem was not detected infeasible in the process
     */
    public boolean setImpact(CPSolver s, int initialisationtime) {
        ImpactBasedBranching ibb;
        IntDomainVar[] bvs = getBooleanVars(s);
        IntDomainVar[] ovs = getOtherVars(s);
        if (isScheduling()) {
            if (!isMixedScheduling()) { //pure scheduling
                ibb = new ImpactBasedBranching(s, bvs);
                if (!ibb.getImpactStrategy().initImpacts(initialisationtime))
                    return false;
                s.attachGoal(ibb);
                AssignVar dwd2 = new AssignVar(new MinDomain(s, ovs), new IncreasingDomain());
                s.addGoal(dwd2);
            } else {                    //side constraints added
                ibb = new ImpactBasedBranching(s, concat(getBooleanVars(s), getOtherVars(s)));
                if (!ibb.getImpactStrategy().initImpacts(initialisationtime))
                    return false;
                s.attachGoal(ibb);
            }
//        } else if (isReified()) { //some constraints are reified (decide them before the rest)
//            ibb = new ImpactBasedBranching(s, bvs);
//            if (!ibb.getImpactStrategy().initImpacts(initialisationtime))
//                return false;
//            s.attachGoal(ibb);
//            AssignVar dwd2 = new AssignVar(new MinDomain(s, ovs), new IncreasingDomain());
//            s.addGoal(dwd2);
        } else {                        //general case
            ibb = new ImpactBasedBranching(s);
            if (!ibb.getImpactStrategy().initImpacts(initialisationtime))
                return false;
            s.attachGoal(ibb);
        }
        return true;
    }

//******************************************************************//
//***************** Define the Decision Variables ******************//
//******************************************************************//

    /**
     * Sort the precedences by decreasing sum of the two
     * durations of the two tasks
     */
    public static class BoolSchedComparator implements Comparator {
        public int compare(Object o, Object o1) {
            int sd1 = (Integer) ((AbstractVar) o).getExtension(2);
            int sd2 = (Integer) ((AbstractVar) o1).getExtension(2);
            if (sd1 > sd2) {
                return -1;  //To change body of implemented methods use File | Settings | File Templates.
            } else if (sd1 == sd2) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    public IntDomainVar[] getBooleanVars(CPSolver s) {
        List<IntDomainVar> ldvs = new ArrayList<IntDomainVar>();
        for (int i = 0; i < s.getNbIntVars(); i++) {
            IntDomainVar v = (IntDomainVar) s.getIntVar(i);
            if (v.hasBooleanDomain()) {
                ldvs.add(v);
            }
        }
        IntDomainVar[] vs = new IntDomainVar[ldvs.size()];
        for (int i = 0; i < vs.length; i++) {
            vs[i] = ldvs.get(i);
        }
        return vs;
    }

    public IntDomainVar[] getOtherVars(CPSolver s) {
        List<IntDomainVar> ldvs = new ArrayList<IntDomainVar>();
        for (int i = 0; i < s.getNbIntVars(); i++) {
            IntDomainVar v = (IntDomainVar) s.getIntVar(i);
            if (v.getDomainSize() > 2) {
                ldvs.add(v);
            }
        }
        IntDomainVar[] vs = new IntDomainVar[ldvs.size()];
        for (int i = 0; i < vs.length; i++) {
            vs[i] = ldvs.get(i);
        }
        return vs;
    }

    public IntDomainVar[] concat(IntDomainVar[] t1, IntDomainVar[] t2) {
        IntDomainVar[] vs = new IntDomainVar[t1.length + t2.length];
        System.arraycopy(t1, 0, vs, 0, t1.length);
        System.arraycopy(t2, 0, vs, t1.length, t2.length);
        return vs;
    }

    //******************************************************************//
    //***************** Heuristic Identifier ***************************//
    //******************************************************************//

    /**
     * return 1 (domWdeg) or 2 (Impact) depending on the nature of the problem
     */
    public int determineHeuristic(CPSolver s) {
        Iterator it = s.getIntConstraintIterator();
        int heuristic = 1;
        if (isSat()) return 2; //degree is unrelevant using the clause propagator
        if (isNaryExtensional()) {
            return 1;
        }
        for (; it.hasNext();) {
            SConstraint constraint = (SConstraint) it.next();
            if (constraint instanceof Cumulative) return 2;
            if (constraint instanceof AllDifferent) return 2;
            if (constraint instanceof BoundAllDiff) {
                if (constraint.getNbVars() > 10) {
                    heuristic = 2;
                }
            }
            if (constraint instanceof ReifiedIntSConstraint) return 2;
            if (constraint instanceof IntLinComb ||
                    constraint instanceof BoolIntLinComb) {
                int arity = constraint.getNbVars();
                if (arity >= 6) {
                    return 2;
                }
            }
            if (constraint instanceof DistanceXYZ) return 1;
            if (constraint instanceof DistanceXYC) return 1;

        }
        if (getSumOfDomains(s) > 500000) {
            return 1;
        }
        return heuristic;
    }

    public int getSumOfDomains(CPSolver s) {
        int sum = 0;
        for (int i = 0; i < s.getNbIntVars(); i++) {
            sum += ((IntDomainVar) s.getIntVar(i)).getDomainSize();

        }
        return sum;
    }
}