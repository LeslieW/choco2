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
package choco.common;

import choco.cp.solver.CPSolver;
import choco.kernel.common.opres.AbstractNoSum;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.util.BitSet;

/**
 * @author Arnaud Malapert
 *
 */
@SuppressWarnings({"PMD.LocalVariableCouldBeFinal","PMD.MethodArgumentCouldBeFinal"})
public class TestNoSum {

	private final static String MSG="noSum";

	private BitSet candidates;

	private final int[] sizes={20,17,15,13,11,9,7,5,3,2,1};

	private final int[] sizes2={100,85,70,60,47,35,27,17,6,4};


	private static IntDomainVar[] vsizes;

	private void initialize(int[] sizes) {
		Solver s=new CPSolver();
		vsizes=new IntDomainVar[sizes.length];
		for (int i = 0; i < sizes.length; i++) {
			vsizes[i]=s.createIntegerConstant("s_"+i,sizes[i]);
		}
		candidates=new BitSet(sizes.length);
		candidates.set(0, sizes.length);
	}

	private int getMaxLoad() {
		int load=0;
		for (int i = candidates.nextSetBit(0); i >= 0; i = candidates.nextSetBit(i + 1)) {
			load+=vsizes[i].getVal();
		}
		return load;
	}

	@Test
	public void testHasSum() {
		initialize(sizes);
		JavaNoSum nosum2=new JavaNoSum(candidates,getMaxLoad(),vsizes);
		assert1(nosum2);
	}

	private void assert1(AbstractNoSum nosum) {
		assertFalse(MSG,nosum.noSum(10, 20));
		assertFalse(MSG,nosum.noSum(34, 40));
		assertFalse(MSG,nosum.noSum(65,80));
		assertFalse(MSG,nosum.noSum(65,80));
		assertFalse(MSG,nosum.noSum(85,85));
		assertFalse(MSG,nosum.noSum(103,103));
		assertFalse(MSG,nosum.noSum(50,55));
		assertFalse(MSG,nosum.noSum(55,65));
	}
	@Test
	public void testHasSum2() {
		initialize(sizes2);
		JavaNoSum nosum2=new JavaNoSum(candidates,getMaxLoad(),vsizes);
		assert2(nosum2);
	}

	private void assert2(AbstractNoSum nosum) {
		assertFalse(MSG,nosum.noSum(35,39));
		assertFalse(MSG,nosum.noSum(50,51));
		assertFalse(MSG,nosum.noSum(215,225));
		assertFalse(MSG,nosum.noSum(313,314));
		assertFalse(MSG,nosum.noSum(375,378));
		assertFalse(MSG,nosum.noSum(421,427));
		assertFalse(MSG,nosum.noSum(431,436));
	}

	@Test
	public void testHasSumBug() {
		int[] sizes={8,7,6,1};
		initialize(sizes);
		JavaNoSum nosum2=new JavaNoSum(candidates,getMaxLoad(),vsizes);
		assertFalse(MSG,nosum2.noSum(9,9));
	}

	private int remove(int k) {
		candidates.clear(k);
			return this.getMaxLoad();
	}
	@Test
	public void testNoSum() {
		initialize(sizes);
		JavaNoSum nosum2=new JavaNoSum(candidates,getMaxLoad(),vsizes);
		assert3(nosum2);
	}

	private void assert3(JavaNoSum nosum) {
		this.remove(8);
		nosum.setMaxLoad(this.remove(9));
		assertTrue(MSG,nosum.noSum(94,96));
		assertTrue(MSG,nosum.noSum(2,4));
		nosum.setMaxLoad(this.remove(6));
		assertTrue(MSG,nosum.noSum(7,8));
		assertFalse(MSG,nosum.noSum(82,84));
		assertTrue(MSG,nosum.noSum(87,89));
		this.remove(7);
		assertTrue(MSG,nosum.noSum(2,8));
		assertTrue(MSG,nosum.noSum(78,84));
	}

	@Test
	public void testNoSum2() {
		initialize(sizes2);
		JavaNoSum nosum2=new JavaNoSum(candidates,getMaxLoad(),vsizes);
		assert4(nosum2);
	}

	private void assert4(AbstractNoSum nosum) {
		assertTrue(MSG,nosum.noSum(11,16));
		assertFalse(MSG,nosum.noSum(42,43)); //false positive
		assertFalse(MSG,nosum.noSum(408,409)); //false positive
		assertFalse(MSG,nosum.noSum(421,423)); //false positive
		assertTrue(MSG,nosum.noSum(435,440));
		assertTrue(MSG,nosum.noSum(448,450));
	}
}


/**
 * @author Arnaud Malapert
 *
 */
class JavaNoSum extends AbstractNoSum {

	/** The max load. */
	protected int maxLoad;

	private final BitSet candidates;

	/**
	 * @param candidates
	 * @param maxLoad
	 * @param sizes
	 */
	public JavaNoSum(BitSet candidates, int maxLoad, IntDomainVar[] sizes) {
		super(sizes);
		this.candidates = candidates;
		this.maxLoad=maxLoad;
	}

	/**
	 * @see AbstractNoSum#getLargestItemIndex()
	 */
	@Override
	protected int getLargestItemIndex() {
		return candidates.nextSetBit(0);
	}

	/**
	 * @see AbstractNoSum#getSmallestItemIndex()
	 */
	@Override
	protected int getSmallestItemIndex() {
		return candidates.length()-1;
	}

	/**
	 * @see AbstractNoSum#next(int)
	 */
	@Override
	protected int next(final int k) {
		return candidates.nextSetBit(k+1);
	}

	/**
	 * @see AbstractNoSum#previous(int)
	 */
	@Override
	protected int previous(final int k) {
		for (int i = k-1; i>=0; i--) {
				if(candidates.get(i)) {return i;}
			}
		//FIXME implem plus efficace ?
		return -1;
	}

	public final void removeCandidate(final int item) {
		candidates.clear(item);
		maxLoad-=sizes[item].getVal();

	}

	public final void insertCandidate(final int item) {
		candidates.set(item);
		maxLoad+=sizes[item].getVal();

	}


	/**
	 * @see AbstractNoSum#getCandidatesLoad()
	 */
	@Override
	protected int getCandidatesLoad() {
		return maxLoad;
	}

	/**
	 * Sets the max load for candidate subset. It avoid a loop to compute it ({@link AbstractNoSum}
	 *
	 * @param maxLoad the maxLoad to set
	 */
	public final void setMaxLoad(final int maxLoad) {
		this.maxLoad = maxLoad;
	}
}
