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
package choco.cp.solver.variables.set;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.memory.structure.Couple;
import choco.kernel.memory.structure.PartiallyStoredSetCstrList;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.AbstractSConstraint;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.propagation.event.VarEvent;
import choco.kernel.solver.propagation.listener.SetPropagator;
import choco.kernel.solver.variables.AbstractVar;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.set.SetDomain;
import choco.kernel.solver.variables.set.SetVar;

/** History:
 * 2007-12-07 : FR_1873619 CPRU: DomOverDeg+DomOverWDeg
 * */
public final class SetVarImpl <C extends AbstractSConstraint & SetPropagator> extends AbstractVar implements SetVar {

	protected SetDomain domain;

	protected IntDomainVar card;

	public SetVarImpl(Solver solver, String name, int a, int b, IntDomainVar card) {
		super(solver, name, new PartiallyStoredSetCstrList<C>(solver.getEnvironment()));
		this.domain = new SetDomainImpl(this, a, b, solver.getEnvironment(), propagationEngine);
		this.event = new SetVarEvent<C>(this);
		this.card = card;

	}

    /**
     * Constructor of SetVar
     * @param solver
     * @param name
     * @param a
     * @param b
     * @param card IntDomainvar representing the cardinality, CAN BE NULL
     * @param type
     */
    public SetVarImpl(Solver solver, String name, int a, int b,IntDomainVar card, int type) {
		super(solver, name, new PartiallyStoredSetCstrList<C>(solver.getEnvironment()));
		this.domain = new SetDomainImpl(this, a, b, solver.getEnvironment(), propagationEngine);
		this.event = new SetVarEvent<C>(this);
        if(card==null){
            if (type == SetVar.BOUNDSET_ENUMCARD) {
                this.card = solver.createEnumIntVar(name, 0, b - a + 1);
            } else if (type == SetVar.BOUNDSET_BOUNDCARD) {
                this.card = solver.createBoundIntVar("|"+name+"|", 0, b - a + 1);
            } else throw new SolverException("unknown type of set var " + type);
        }else{
            this.card = card;
        }
    }

    public SetVarImpl(Solver solver, String name, int[] sortedValues, IntDomainVar card) {
		super(solver, name, new PartiallyStoredSetCstrList<C>(solver.getEnvironment()));
		this.domain = new SetDomainImpl(this, sortedValues, solver.getEnvironment(), propagationEngine);
		this.event = new SetVarEvent<C>(this);
		this.card = card;
	}

    /**
     * Constructor of a SetVar
     * @param solver
     * @param name
     * @param sortedValues
     * @param card IntDomainvar representing the cardinality, CAN BE NULL
     * @param type
     */
    public SetVarImpl(Solver solver, String name, int[] sortedValues, IntDomainVar card, int type) {
        super(solver, name, new PartiallyStoredSetCstrList<C>(solver.getEnvironment()));
		this.event = new SetVarEvent<C>(this);
        int size = sortedValues.length;
        if(card ==null){
            if (type == SetVar.BOUNDSET_ENUMCARD) {
                this.card = solver.createEnumIntVar("|"+name+"|", 0, size);
            } else if (type == SetVar.BOUNDSET_BOUNDCARD) {
                this.card = solver.createBoundIntVar("|"+name+"|", 0, size);
            } else if(type == SetVar.BOUNDSET_CONSTANT){
                this.card = solver.createBoundIntVar("|"+name+"|", size, size);
            }else
                throw new SolverException("unknown type of set var " + type);
        }else{
            this.card = card;
        }
        boolean constant = (type == BOUNDSET_CONSTANT);
        this.domain = new SetDomainImpl(this, sortedValues, constant, solver.getEnvironment(), propagationEngine);
	}

    @SuppressWarnings({"unchecked"})
    public final DisposableIterator<Couple<C>> getActiveConstraints(C cstrCause){
        return ((PartiallyStoredSetCstrList)constraints).getActiveConstraint(cstrCause);
    }


    public IntDomainVar getCard() {
		return card;
	}

	public boolean isInstantiated() {
		return domain.isInstantiated();  //To change body of implemented methods use File | Settings | File Templates.
	}

	public final void setValIn(int x) throws ContradictionException {
		addToKernel(x, null, true);
	}

	public final void setValOut(int x) throws ContradictionException {
		remFromEnveloppe(x, null, true);
	}

	public boolean isInDomainKernel(int x) {
		return domain.getKernelDomain().contains(x);
	}

	public boolean isInDomainEnveloppe(int x) {
		return domain.getEnveloppeDomain().contains(x);
	}

	public SetDomain getDomain() {
		return domain;
	}

	/**
	 * Check if the both domain intersects
	 *
	 * @param x SetVar to be checked with
	 * @return a boolean
	 */
	public boolean canBeEqualTo(SetVar x) {
		return false;
	}

	public int getKernelDomainSize() {
		return domain.getKernelDomain().getSize();
	}

	public int getEnveloppeDomainSize() {
		return domain.getEnveloppeDomain().getSize();
	}

	public int getEnveloppeInf() {
		return domain.getEnveloppeDomain().getFirstVal();
	}

	public int getEnveloppeSup() {
		return domain.getEnveloppeDomain().getLastVal();
	}

	public int getKernelInf() {
		return domain.getKernelDomain().getFirstVal();
	}

	public int getKernelSup() {
		return domain.getKernelDomain().getLastVal();
	}

	public int[] getValue() {
		int[] val = new int[getKernelDomainSize()];
		DisposableIntIterator it = domain.getKernelIterator();
		int i = 0;
		while (it.hasNext()) {
			val[i] = it.next();
			i++;
		}
        it.dispose();
		return val;
	}

	public final void setVal(int[] val) throws ContradictionException {
		instantiate(val, null, true);
	}

    @Deprecated
    private SConstraint getCause(int idx){
        if(idx < -1){
//            this.getConstraintVector().get(VarEvent.domOverWDegInitialIdx(idx)));
            return this.getConstraint(VarEvent.domOverWDegInitialIdx(idx));
        }else if(idx > -1){
            return this.getConstraint(idx);
        }
        return null;
    }

	public boolean addToKernel(int x, final SConstraint cause, final boolean forceAwake) throws ContradictionException {
		return domain.addToKernel(x, cause, forceAwake);
	}

    public boolean remFromEnveloppe(int x, final SConstraint cause, final boolean forceAwake) throws ContradictionException {
		return domain.remFromEnveloppe(x, cause, forceAwake);
	}

    public boolean instantiate(int[] x, final SConstraint cause, final boolean forceAwake) throws ContradictionException {
		return domain.instantiate(x, cause, forceAwake);
	}

    @Override
    @Deprecated
    public boolean addToKernel(final int x, final int idx) throws ContradictionException {
        return domain.addToKernel(x, getCause(idx), (idx>=0));
    }

    @Override
    @Deprecated
    public boolean remFromEnveloppe(final int x, final int idx) throws ContradictionException {
        return domain.remFromEnveloppe(x, getCause(idx), (idx>=0));
    }

    @Override
    @Deprecated
    public boolean instantiate(final int[] x, final int idx) throws ContradictionException {
        return instantiate(x, getCause(idx), (idx>=0));
    }

    /**
	 * pretty printing
	 *
	 * @return a String representation of the variable
	 */
	public String pretty() {
		return this.toString();
	}

	@Override
	public String toString(){
		return this.name + " "+this.domain.toString();
	}

	
}
