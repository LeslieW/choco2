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

import choco.kernel.common.util.IntIterator;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.variables.AbstractVar;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.set.SetDomain;
import choco.kernel.solver.variables.set.SetVar;

/** History:
 * 2007-12-07 : FR_1873619 CPRU: DomOverDeg+DomOverWDeg
 * */
public class SetVarImpl extends AbstractVar implements SetVar {

	protected SetDomainImpl domain;

	protected IntDomainVar card;

	public SetVarImpl(Solver solver, String name, int a, int b, IntDomainVar card) {
		super(solver, name);
		this.domain = new SetDomainImpl(this, a, b);
		this.event = new SetVarEvent(this);
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
		super(solver, name);
		this.domain = new SetDomainImpl(this, a, b);
		this.event = new SetVarEvent(this);
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
		super(solver, name);
		this.domain = new SetDomainImpl(this, sortedValues);
		this.event = new SetVarEvent(this);
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
        super(solver, name);
		this.event = new SetVarEvent(this);
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
        this.domain = new SetDomainImpl(this, sortedValues, constant);
	}


    public IntDomainVar getCard() {
		return card;
	}

	/**
	 * CPRU 07/12/2007: DomOverFailureDeg implementation
	 * Add:
	 * - call of super.fail()
	 * - call of raiseContradiction(this)
	 * - comment fail() initial
	 *
	 * @throws ContradictionException
	 */
	@Override
	public void fail() throws ContradictionException {
		super.fail();
		solver.getPropagationEngine().raiseContradiction(this, ContradictionException.VARIABLE);
		//this.fail();
	}

	public boolean isInstantiated() {
		return domain.isInstantiated();  //To change body of implemented methods use File | Settings | File Templates.
	}

	public void setValIn(int x) throws ContradictionException {
		addToKernel(x, SetVarEvent.NOCAUSE);
	}

	public void setValOut(int x) throws ContradictionException {
		remFromEnveloppe(x, SetVarEvent.NOCAUSE);
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
		IntIterator it = domain.getKernelIterator();
		int i = 0;
		while (it.hasNext()) {
			val[i] = it.next();
			i++;
		}
		return val;
	}

	public void setVal(int[] val) throws ContradictionException {
		instantiate(val, SetVarEvent.NOCAUSE);
	}

	public boolean addToKernel(int x, int idx) throws ContradictionException {
		return domain.addToKernel(x, idx);
	}

	public boolean remFromEnveloppe(int x, int idx) throws ContradictionException {
		return domain.remFromEnveloppe(x, idx);
	}

	public boolean instantiate(int[] x, int idx) throws ContradictionException {
		return domain.instantiate(x, idx);
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

	public Solver getSolver() {
		return this.solver;
	}

	public void setSolver(Solver solver) {
		this.solver = solver;
	}
}
