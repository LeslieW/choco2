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
package choco.cp.solver.constraints.set;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.SConstraintType;
import choco.kernel.solver.constraints.set.AbstractMixedSetIntSConstraint;
import choco.kernel.solver.propagation.PropagationEngine;
import choco.kernel.solver.propagation.event.VarEvent;
import choco.kernel.solver.variables.AbstractVar;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.HashSet;

/**
 * A constraint that allows to reify another constraint into a boolean value.
 * b = 1 <=> cons is satisfied
 * b = 0 <=> oppositeCons is satisfied
 * <p/>
 * cons and oppositeCons do not need to be really the constraint and its
 * opposite, it can be two different constraints as well
 */
public class ReifiedIntSetSConstraint extends AbstractMixedSetIntSConstraint{

    protected AbstractSConstraint cons;
    protected AbstractSConstraint oppositeCons;

    //scopeCons[i] = j means that the i-th variable of cons is the j-th in reifiedIntConstraint
    protected int[] scopeCons;
    //scopeOCons[i] = j means that the i-th variable of oppositeCons is the j-th in reifiedIntConstraint
    protected int[] scopeOCons;

    private final IntDomainVar bool;

    public static Var[] makeTableVar(IntDomainVar bool, AbstractSConstraint cons, AbstractSConstraint oppcons) {
        HashSet<Var> consV = new HashSet<Var>();
        for (int i = 0; i < cons.getNbVars(); i++)
            consV.add(cons.getVar(i));
        for (int i = 0; i < oppcons.getNbVars(); i++)
            consV.add(oppcons.getVar(i));
        consV.add(bool);
        Var[] vars = new Var[consV.size()];
        consV.remove(bool);
        vars[0] = bool;
        int i = 1;
        for (Var var: consV) {
            vars[i] = var;
            i++;
        }
        return vars;
    }

    /**
     * A constraint that allows to reify another constraint into a boolean value.
     * b = 1 <=> cons is satisfied
     * b = 0 <=> oppositeCons is satisfied
     * if the opposite methode of the constraint is not defined, use the other constructor
     * by giving yourself the opposite constraint !
     * @param bool reified variable
     * @param cons the reified constraint
     * @param solver solver
     */
    public ReifiedIntSetSConstraint(IntDomainVar bool, AbstractSConstraint cons, Solver solver) {
        super(makeTableVar(bool, cons, cons.opposite(solver)));
        this.cons = cons;
        this.oppositeCons = cons.opposite(solver);
        this.bool = bool;
        init();
    }

    /**
     * A constraint that allows to reify another constraint into a boolean value.
     * b = 1 <=> cons is satisfied
     * b = 0 <=> oppositeCons is satisfied
     * <p/>
     * cons and oppositeCons do not need to be really the constraint and its
     * opposite, it can be two different constraints as well
     * @param bool reified variable
     * @param cons the reified constraint
     * @param oppositeCons the opposite reified constraint
     */
    public ReifiedIntSetSConstraint(IntDomainVar bool, AbstractSConstraint cons, AbstractSConstraint oppositeCons) {
        super(makeTableVar(bool, cons, oppositeCons));
        this.cons = cons;
        this.oppositeCons = oppositeCons;
        this.bool = bool;
        init();
    }

    public void init() {
        scopeCons = new int[cons.getNbVars()];
        scopeOCons = new int[oppositeCons.getNbVars()];
        for (int i = 0; i < cons.getNbVars(); i++) {
            Var v = cons.getVar(i);
            for (int j = 0; j < vars.length; j++) {
                if (v.equals(vars[j])) {
                    scopeCons[i] = j;
                    break;
                }
            }
        }
        for (int i = 0; i < oppositeCons.getNbVars(); i++) {
            Var v = oppositeCons.getVar(i);
            for (int j = 0; j < vars.length; j++) {
                if (v.equals(vars[j])) {
                    scopeOCons[i] = j;
                    break;
                }
            }
        }
    }

    //assume that the boolean is known
    public void filterReifiedConstraintFromBool() throws ContradictionException {
        if (bool.isInstantiatedTo(1)) {
            cons.awake();
        } else {
            oppositeCons.awake();
        }
    }

    public void filterReifiedConstraintFromCons() throws ContradictionException {
        Boolean isEntailed = cons.isEntailed();
        if (isEntailed != null) {
            if (isEntailed) {
                bool.instantiate(1, cIndices[0]);
            } else {
                bool.instantiate(0, VarEvent.domOverWDegIdx(cIndices[0]));
            }
        }
    }

    public void filter() throws ContradictionException {
        if (vars[0].isInstantiated()) {
            filterReifiedConstraintFromBool();
        } else {
            filterReifiedConstraintFromCons();
        }
    }


    public void awake() throws ContradictionException {
        filter();
    }

    public void propagate() throws ContradictionException {
        filter();
    }

    public void awakeOnInf(int idx) throws ContradictionException {
        filter();
    }

    public void awakeOnSup(int idx) throws ContradictionException {
        filter();
    }

    public void awakeOnInst(int idx) throws ContradictionException {
        filter();
    }

    public void awakeOnRem(int idx, int x) throws ContradictionException {
        filter();
    }

    public void awakeOnRemovals(int idx, DisposableIntIterator deltaDomain) throws ContradictionException {
        filter();
    }

    public void awakeOnBounds(int varIndex) throws ContradictionException {
        filter();
    }

    @Override
    public void awakeOnKer(int varIdx, int x) throws ContradictionException {
        filter();
    }

    @Override
    public void awakeOnEnv(int varIdx, int x) throws ContradictionException {
        filter();
    }

    public void addListener(AbstractSConstraint thecons) {
        if (thecons instanceof ReifiedIntSetSConstraint) {
            ReifiedIntSetSConstraint rcons = (ReifiedIntSetSConstraint) thecons;
            addListener(rcons.cons);
            addListener(rcons.oppositeCons);
        }
        int n = thecons.getNbVars();
        for (int i = 0; i < n; i++) {
            thecons.setConstraintIndex(i, getIndex((AbstractVar) thecons.getVar(i)));
        }
    }

    public int getIndex(AbstractVar v) {
        for (int i = 0; i < vars.length; i++) {
            if (vars[i] == v) return cIndices[i];
        }
        return -1; //should never go there !
    }

    public void addListener(boolean dynamicAddition) {
        super.addListener(dynamicAddition);
        addListener(cons);
        addListener(oppositeCons);
    }

    /**
     * Define the propagation engine within the constraint.
     * Mandatory to throw {@link choco.kernel.solver.ContradictionException}.
     *
     * @param propEng the current propagation engine
     */
    @Override
    public void setPropagationEngine(PropagationEngine propEng) {
        super.setPropagationEngine(propEng);
        cons.setPropagationEngine(propEng);
        oppositeCons.setPropagationEngine(propEng);
    }

    public String pretty() {
        StringBuffer sb = new StringBuffer("(");
        sb.append(" 1");
        sb.append("<=>").append(cons.pretty());
        if (oppositeCons != null) {
            sb.append(" -- 0");
            sb.append("<=>").append(oppositeCons.pretty());
        }
        sb.append(")");
        sb.append("~").append(vars[0].pretty());
        return sb.toString();
    }

    /**
     * <i>Semantic:</i>
     * Testing if the constraint is satisfied.
     * Note that all variables involved in the constraint must be
     * instantiated when this method is called.
     *
     * @return true if the constraint is satisfied
     */
    @Override
    public boolean isSatisfied() {
        if(isCompletelyInstantiated()){
            if(bool.isInstantiatedTo(1)){
                return cons.isSatisfied();
            }else{
                return oppositeCons.isSatisfied();
            }
        }
        return false;
    }

    @Override
    public SConstraintType getConstraintType() {
        return SConstraintType.INT_SET;
    }
}