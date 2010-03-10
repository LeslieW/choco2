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
package choco.cp.solver.constraints.integer.channeling;

import choco.cp.solver.variables.integer.IntVarEvent;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.branch.Extension;
import choco.kernel.solver.constraints.integer.AbstractIntSConstraint;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.propagation.PropagationEngine;
import choco.kernel.solver.variables.AbstractVar;
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
public class ReifiedIntSConstraint extends AbstractLargeIntSConstraint {

    protected AbstractIntSConstraint cons;
    protected AbstractIntSConstraint oppositeCons;

    //scopeCons[i] = j means that the i-th variable of cons is the j-th in reifiedIntConstraint
    protected int[] scopeCons;
    //scopeOCons[i] = j means that the i-th variable of oppositeCons is the j-th in reifiedIntConstraint
    protected int[] scopeOCons;

    public static IntDomainVar[] makeTableVar(IntDomainVar bool, AbstractIntSConstraint cons, AbstractIntSConstraint oppcons) {
        HashSet<IntDomainVar> consV = new HashSet<IntDomainVar>();
        for (int i = 0; i < cons.getNbVars(); i++)
            consV.add(cons.getVar(i));
        for (int i = 0; i < oppcons.getNbVars(); i++)
            consV.add(oppcons.getVar(i));
        consV.add(bool);
        IntDomainVar[] vars = new IntDomainVar[consV.size()];
        consV.remove(bool);
        vars[0] = bool;
        int i = 1;
        for (Object aConsV : consV) {
            IntDomainVar intDomainVar = (IntDomainVar) aConsV;
            vars[i] = intDomainVar;
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
     * @param solver
     */
    public ReifiedIntSConstraint(IntDomainVar bool, AbstractIntSConstraint cons, Solver solver) {
        super(makeTableVar(bool, cons, (AbstractIntSConstraint) cons.opposite(solver)));
        this.cons = cons;
        this.oppositeCons = (AbstractIntSConstraint) cons.opposite(solver);
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
    public ReifiedIntSConstraint(IntDomainVar bool, AbstractIntSConstraint cons, AbstractIntSConstraint oppositeCons) {
        super(makeTableVar(bool, cons, oppositeCons));
        this.cons = cons;
        this.oppositeCons = oppositeCons;
        init();
    }

    /**
     * Adds a new extension.
     *
     * @param extensionNumber should use the number returned by getAbstractSConstraintExtensionNumber
     */
    @Override
    public void addExtension(final int extensionNumber) {
        super.addExtension(extensionNumber);
        Extension ext = extensions[extensionNumber];
        cons.setExtension(ext, extensionNumber);
        oppositeCons.setExtension(ext, extensionNumber);
    }

    public void init() {
        tupleCons = new int[cons.getNbVars()];
        tupleOCons = new int[oppositeCons.getNbVars()];
        scopeCons = new int[cons.getNbVars()];
        scopeOCons = new int[oppositeCons.getNbVars()];
        for (int i = 0; i < cons.getNbVars(); i++) {
            IntDomainVar v = cons.getVar(i);
            for (int j = 0; j < vars.length; j++) {
                if (v.equals(vars[j])) {
                    scopeCons[i] = j;
                    break;
                }
            }
        }
        for (int i = 0; i < oppositeCons.getNbVars(); i++) {
            IntDomainVar v = oppositeCons.getVar(i);
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
        if (vars[0].isInstantiatedTo(1)) {
            cons.awake();//propagate();
        } else {
            oppositeCons.awake();//propagate();
        }
    }

    public void filterReifiedConstraintFromCons() throws ContradictionException {
        Boolean isEntailed = cons.isEntailed();
        if (isEntailed != null) {
            if (isEntailed) {
                vars[0].instantiate(1, this, false);
            } else {
                vars[0].instantiate(0, this, true);//cIndices[0]);
            }
        }
    }


    @Override
    public int getFilteredEventMask(int idx) {
        if (vars[idx].hasEnumeratedDomain()) {
            return IntVarEvent.REMVALbitvector;
        } else {
            return IntVarEvent.BOUNDSbitvector;
        }
    }

    public void filter() throws ContradictionException {
        if (vars[0].isInstantiated()) {
            filterReifiedConstraintFromBool();
        } else {
            filterReifiedConstraintFromCons();
        }
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

    public void propagate() throws ContradictionException {
        filter();
    }

    public void awake() throws ContradictionException {
        filter();
    }

    public void addListener(AbstractIntSConstraint thecons) {
        if (thecons instanceof ReifiedIntSConstraint) {
            ReifiedIntSConstraint rcons = (ReifiedIntSConstraint) thecons;
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

    //temporary data to store tuples
    int[] tupleCons;
    int[] tupleOCons;

    /**
     * TEMPORARY: if not overriden by the constraint, throws an error
     * to avoid bug using reified constraints in constraints
     * that have not been changed to fulfill this api yet !
     *
     * @param tuple value for each variable
     * @return true if the tuple satisfies the constraint
     */
    public boolean isSatisfied(int[] tuple) {
        int val = tuple[0];
        for (int i = 0; i < tupleCons.length; i++) {
            tupleCons[i] = tuple[scopeCons[i]];
        }
        if (val == 1) {
            return cons.isSatisfied(tupleCons);
        } else {
            for (int i = 0; i < tupleOCons.length; i++) {
                tupleOCons[i] = tuple[scopeOCons[i]];
            }
            return !cons.isSatisfied(tupleCons)
            && oppositeCons.isSatisfied(tupleOCons);
        }
    }
}