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
package choco.cp.solver.constraints.global.scheduling;

import java.util.BitSet;

import choco.kernel.common.opres.ssp.BellmanWithLists;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;


/**
 * specialized filtering for makespan minimization on unary resource.
 * The rules is efficient on Open-Shop problem.
 * It is better if the ratio load/makespan is high.
 * @author Arnaud Malapert</br> 
 * @since 23 janv. 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public class ForbiddenIntervals extends AbstractResourceSConstraint {

	private final ExtendedBitSet forbidden;

	private final int load;

	public ForbiddenIntervals(String name, final TaskVar[] taskvars,final IntDomainVar upperBound) {
		super(name, taskvars, upperBound);
		//initialize intervals by solving a Subset-Sum Problem
		int[] p=new int[getNbTasks()];
		IntDomainVar d;
		int l=0;
		for (int i = 0; i < p.length; i++) {
			d = getTask(i).duration();
			if(d.isInstantiated()) {
				p[i]=d.getVal();
			}else {throw new SolverException("forbidden intervals are only available for resources with constant duration");}
			l+=p[i];
		}
		load = l;
		final BellmanWithLists bell=new BellmanWithLists(p,load);
		bell.run();
		forbidden=new ExtendedBitSet(bell.getCoveredSet(),load);
	}

	/**
	 * Gets the upper bound on the objective
	 *
	 * @return the UB
	 */
	private int getUB() {
		return this.vars[indexUB].getSup();
	}

	private final boolean checkHead(final int head) {
		if(!forbidden.get(head)) {
			final int before=forbidden.prevSetBit(head);
			final int after=this.load-before;
			if(after+head>this.getUB()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Update the head of an operation.
	 *
	 */
	private final void updateHead(final int operation) throws ContradictionException {
		final TaskVar t= getTask(operation);
		int head=t.getEST();
		if(checkHead(head)) {
			rtasks[operation].updateEST(forbidden.nextSetBit(head));
		}
		head=t.getECT();
		if(checkHead(head)) {
			rtasks[operation].updateECT(forbidden.nextSetBit(head));
		}
	}

	private int checkTail(final int tail) {
		if(!forbidden.get(tail)) {
			final int before=forbidden.prevSetBit(tail);
			final int after=this.getUB()-(this.load-before);
			if(tail>after) {
				return after;
			}
		}
		return -1;
	}


	/**
	 * Update the tail of an operation.
	 *
	 */
	private final void updateTail(final int operation) throws ContradictionException {
		final TaskVar t= getTask(operation);
		int tail=t.getLST();
		int val=checkTail(tail);
		if(val>=0) {
			rtasks[operation].updateLST(val);
		}
		tail=t.getLCT();
		val=checkTail(tail);
		if(val>=0) {
			rtasks[operation].updateLCT(val);
		}
	}


	//****************************************************************//
	//********* CHOCO EVENTS *******************************************//
	//****************************************************************//


	@Override
	public void awakeOnInf(final int idx) throws ContradictionException {
		if(idx< getNbTasks()) {updateHead(idx);}
	}

	private void globalUpdate() throws ContradictionException {
		for (int o = 0; o < getNbTasks(); o++) {
			updateHead(o);
			updateTail(o);
		}
	}


	@Override
	public void awakeOnInst(final int idx) throws ContradictionException {
		if( idx < getNbTasks()) {
			updateHead(idx);
			updateTail(idx);
		}else if(idx== indexUB){
			globalUpdate();
		}
	}

	@Override
	public void awakeOnSup(final int idx) throws ContradictionException {
		if(idx== indexUB){
			globalUpdate();
		}else if(idx >= getNbTasks()) {
			updateTail(idx/2);
		}
	}


	/**
	 * Empty method.
	 *
	 */
	@Override
	public void propagate() throws ContradictionException {
		//nothing to do
	}



}


class ExtendedBitSet {

	private final int capacity;

	private final BitSet original;

	private final BitSet symmetric;

	/**
	 * @param original
	 * @param capacity
	 */
	public ExtendedBitSet(final BitSet original, final int capacity) {
		this.capacity = capacity;
		this.original=original;
		this.symmetric=new BitSet(this.capacity+1);
		for (int i = this.original.nextSetBit(0); i >= 0; i = this.original.nextSetBit(i + 1)) {
			this.set(capacity-i);
		}
	}

	public final boolean get(final int idx) {
		return original.get(idx);
	}

	public final void set(final int idx) {
		if(capacity-idx>=0) {
			original.set(idx);
			symmetric.set(capacity-idx);
		}
	}

	public int nextSetBit(final int idx) {
		return original.nextSetBit(idx);
	}

	public int prevSetBit(final int idx) {
		final int sidx=capacity-idx;
		if(sidx<0) {return original.length()-1;}
		else {
			final int n=symmetric.nextSetBit(sidx);
			return n<0 ? n : capacity-n;
		}
	}

	@Override
	public String toString() {
		return original.toString();
	}


}

