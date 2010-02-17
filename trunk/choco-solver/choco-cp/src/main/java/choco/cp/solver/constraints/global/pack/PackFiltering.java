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
package choco.cp.solver.constraints.global.pack;

import static choco.cp.solver.SettingType.ADDITIONAL_RULES;
import static choco.cp.solver.SettingType.FILL_BIN;
import choco.cp.solver.constraints.BitFlags;
import choco.kernel.common.opres.AbstractNoSum;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateIntVector;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.set.SetVar;

import java.awt.*;
import java.util.BitSet;


/**
 * The Class {@link PackFiltering} is implements filtering rules for one-dimensional bin packing constraint.
 *In fact, all variables are not given. So, the constraint used an interface {@link IPackSConstraint} to get informations.
 *
 * </br> This class is a global constraint inspired from the 1BP constraint proposed by
 * [1].</br>
 * <tr valign="top">
 * <td align="right"> [<a name="shaw-04">1</a>] </td>
 *
 * <td> Paul Shaw. A constraint for bin packing. In Mark Wallace, editor,
 * <em>Principles and Practice of Constraint
 * Programming - CP 2004, 10th International Conference, CP 2004, Toronto,
 * Canada, September 27 - October 1, 2004, Proceedings</em>,
 * volume 3258 of <em>
 * Lecture Notes in Computer Science</em>, pages 648-662.
 * Springer, 2004. [&nbsp;<a
 * href="http://springerlink.metapress.com/openurl.asp?genre=article&amp;issn=0302-9743&amp;volume=3258&amp;spage=648">http</a>&nbsp;]
 * </td>
 * </tr>
 *
 * @author Arnaud Malapert
 * @since 2.0.0
 * @version 2.0.1
 */
public class PackFiltering {

	public final IPackSConstraint cstr;
	
	protected final BitFlags flags;


	/** The sizes of the items. */
	protected final IntDomainVar[] sizes;

	/** The loads of the bins. */
	protected final IntDomainVar[] loads;

	//general propagation info

	/** information about a given bin. */
	protected BinStatus status;

	/** the list of bin which can receive more items */
	protected IStateIntVector availableBins;
	
	/** The no fix point. */
	private boolean noFixPoint;

	protected final SumDataStruct loadSum;


	//FIXME protected SumDataStruct cardSum; implémenter les règles


	/**
	 * Instantiates a new 1BP constraint.
	 * @param environment
	 */
	public PackFiltering(IEnvironment environment, IPackSConstraint cstr, BitFlags flags) {
		this.cstr = cstr;
		this.sizes = cstr.getSizes();
		this.loads = cstr.getLoads();
		loadSum = new SumDataStruct(loads,computeTotalSize());
		this.flags = flags;
        availableBins = environment.makeBipartiteIntList(ArrayUtils.zeroToN( cstr.getNbBins()));
	}

	/**
	 * Compute the total size and check that sizes are constant.
	 *
	 */
	private final long computeTotalSize() {
		long l=0;
		int last=Integer.MAX_VALUE;
		for (int i = 0; i < sizes.length; i++) {
			if(sizes[i].isInstantiated()) {
				final int s=sizes[i].getVal();
				if(s>last) {throw new SolverException("size must be sorted according to non increasing order");}
				else {
					l+=s;
					last=s;
				}
			}
			else {throw new SolverException("sizes must be constant");}
		}
		return l;
	}




	/**
	 * Update the status of filled bins.
	 */
	private void updateAvailableBins() {
		DisposableIntIterator iter = availableBins.getIterator();
		while(iter.hasNext()) {
			final int b = iter.next();
			if( cstr.isFilled(b)) {
				iter.remove();
			}
		}
	}


	/**
	 * Update the minimal load of a given bin.
	 *
	 * @param bin the index of bin
	 * @param load the new load
	 *
	 * @throws ContradictionException the contradiction exception
	 */
	protected void updateInfLoad(final int bin,final int load) throws ContradictionException {
		noFixPoint |= cstr.updateInfLoad(bin, load);
	}


	/**
	 * Update the maximal load of a given bin.
	 *
	 * @param bin the index of bin
	 * @param load the new load
	 *
	 * @throws ContradictionException the contradiction exception
	 */
	protected void updateSupLoad(final int bin,final int load) throws ContradictionException {
		noFixPoint |= cstr.updateSupLoad(bin, load);
	}

	/**
	 * Pack an item into a bin
	 * @return true, if successful
	 *
	 * @throws ContradictionException the contradiction exception
	 */
	protected void pack(final int item,final int bin) throws ContradictionException {
		if(cstr.pack(item, bin)) {
			status.pack(item);
			noFixPoint |= true;
		}
	}



	/**
	 * Remove a possible assignment of an item into a bin.
	 *
	 * @return true, if successful
	 *
	 * @throws ContradictionException the contradiction exception
	 */
	protected void remove(final int item,final int bin) throws ContradictionException {
		if(cstr.remove(item, bin)) {
			status.remove(item);
			noFixPoint |= true;
		}
	}


	//	%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%//
	//	%%%%%%%%%%%%%%%%%%%%%%%%%% TYPICAL MODEL %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%//
	//	%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%//



	/**
	 * The minimum and maximum load of each bin {@link PackFiltering#loads } is maintained according to the domains of the bin assignment variables.
	 *
	 * @param bin the index of the bin
	 *
	 * @throws ContradictionException the contradiction exception
	 */
	protected void loadMaintenance(final int bin) throws ContradictionException {
		updateInfLoad(bin,status.getRequiredLoad());
		updateSupLoad(bin,status.getMaxLoad());
	}

	/**
	 * The minimum and maximum load of each bin {@link PackFiltering#loads } is maintained according to the domains of the bin assignment variables.
	 *
	 * @param bin the bin
	 *
	 * @throws ContradictionException the contradiction exception
	 */
	protected void loadSizeAndCoherence(final int bin) throws ContradictionException {
		Point p = loadSum.getBounds(bin);
		updateInfLoad(bin, p.x);
		updateSupLoad(bin, p.y);
	}


	/**
	 * Single item elimination and commitment.
	 *
	 * @param bin the bin
	 *
	 * @throws ContradictionException the contradiction exception
	 */
	protected void singleItemEliminationAndCommitment(final int bin) throws ContradictionException {
		final BitSet candidates=status.getCandidates();
		for (int item = candidates.nextSetBit(0); item >= 0; item = candidates.nextSetBit(item + 1)) {
			if(sizes[item].getInf()+status.getRequiredLoad()>loads[bin].getSup()) {
				remove(item,bin);
			}else if(status.getMaxLoad()-sizes[item].getSup()<loads[bin].getInf()) {
				pack(item,bin);
			}
		}
	}

	/**
	 *
	 * @param bin the bin
	 *
	 * @throws ContradictionException the contradiction exception
	 */
	protected void singleItemEliminationAndCommitmentAndFill(final int bin) throws ContradictionException {
		//FIXME no conflicts, equivalent bins ...
		final BitSet candidates=status.getCandidates();
		for (int item = candidates.nextSetBit(0); item >= 0; item = candidates.nextSetBit(item + 1)) {
			if(sizes[item].getInf()+status.getRequiredLoad()>loads[bin].getSup()) {
				remove(item,bin);
			}else if(status.getMaxLoad()-sizes[item].getSup()<loads[bin].getInf()) {
				pack(item,bin);
			}else if(status.getRequiredLoad()+sizes[item].getInf()==loads[bin].getSup()) {
				pack(item,bin);
			}
		}
	}




	//	%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%//
	//	%%%%%%%%%%%%%%%%%%%%%%%%%% ADDITIONAL RULES %%%%%%%%%%%%%%%%%%%%%%%%%%%%//
	//	%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%//



	/**
	 * Feasibility test on the load of a given bin using no sum algorothm.
	 *
	 *
	 * @throws ContradictionException the contradiction exception
	 */
	protected final void noSumPruningRule(final AbstractNoSum nosum,final int bin) throws ContradictionException {
		if(nosum.noSum(loads[bin].getInf()-status.getRequiredLoad(),loads[bin].getSup()-status.getRequiredLoad())) {
			cstr.fail();
		}
	}

	/**
	 * Update the load of a given bin with no sum algorithm
	 *
	 *
	 * @throws ContradictionException the contradiction exception
	 */
	protected final void noSumBinLoads(final AbstractNoSum nosum,final int bin) throws ContradictionException {
		if(nosum.noSum(loads[bin].getInf()-status.getRequiredLoad(), loads[bin].getInf()-status.getRequiredLoad())) {
			updateInfLoad(bin, status.getRequiredLoad()+nosum.getAlphaBeta().y);
		}
		if(nosum.noSum(loads[bin].getSup()-status.getRequiredLoad(),loads[bin].getSup()-status.getRequiredLoad())) {
			updateSupLoad(bin,status.getRequiredLoad()+nosum.getAlphaBeta().x);
		}
	}

	/**
	 * use no sum algorithm to pack into or remove from.
	 *
	 *
	 * @throws ContradictionException the contradiction exception
	 */
	protected final void noSumItemEliminationAndCommitment(final AbstractNoSum nosum,final int bin) throws ContradictionException {
		final BitSet candidates=status.getCandidates();
		for (int item = candidates.nextSetBit(0); item >= 0; item = candidates.nextSetBit(item + 1)) {
			status.remove(item);
			if(candidates.isEmpty()) {break;}
			else {
				if(nosum.noSum(loads[bin].getInf()-status.getRequiredLoad()-sizes[item].getVal(), loads[bin].getSup()-status.getRequiredLoad()-sizes[item].getInf())) {
					status.insertCandidate(item); //reset
					remove(item, bin);
				}else if (nosum.noSum(loads[bin].getInf()-status.getRequiredLoad(),loads[bin].getSup()-status.getRequiredLoad())) {
					status.insertCandidate(item); //reset
					pack(item, bin);
				}else {
					status.insertCandidate(item); //reset
				}
			}
		}
	}



	//	****************************************************************//
	//	********* PROPAGATION LOOP *************************************//
	//	****************************************************************//



	public void propagate() throws ContradictionException {
		//CPSolver.flushLogs();
		noFixPoint=true;
		while(noFixPoint) {
			noFixPoint=false;
			loadSum.update();
			for (int i = 0; i < availableBins.size() ; i++) {
				propagate( availableBins.get(i));
			}
		}
		updateAvailableBins();
	}

	/**
	 *
	 * @throws ContradictionException the contradiction exception
	 */
	private void propagate(final int bin) throws ContradictionException {
		loadSizeAndCoherence(bin);
		status = cstr.getStatus(bin);
		loadMaintenance(bin);
		if(flags.contains(FILL_BIN)) {singleItemEliminationAndCommitmentAndFill(bin);}
		else {singleItemEliminationAndCommitment(bin);}
		if( flags.contains(ADDITIONAL_RULES) && status.getCandidates().cardinality()>1) {
			final AbstractNoSum nosum=new NoSum();
			noSumPruningRule(nosum,bin);
			noSumBinLoads(nosum,bin);
			noSumItemEliminationAndCommitment(nosum, bin);
		}
	}


	final class NoSum extends AbstractNoSum {

		public NoSum() {
			super(PackFiltering.this.sizes);
		}
		/**
		 * @see choco.kernel.common.opres.AbstractNoSum#getCandidatesLoad()
		 */
		@Override
		protected int getCandidatesLoad() {
			return status.getCandidatesLoad();
		}

		/**
		 * @see choco.kernel.common.opres.AbstractNoSum#getLargestItemIndex()
		 */
		@Override
		protected int getLargestItemIndex() {
			return status.getCandidates().nextSetBit(0);
		}

		/**
		 * @see choco.kernel.common.opres.AbstractNoSum#getSmallestItemIndex()
		 */
		@Override
		protected int getSmallestItemIndex() {
			return status.getCandidates().length()-1;
		}

		/**
		 * @see choco.kernel.common.opres.AbstractNoSum#next(int)
		 */
		@Override
		protected int next(final int k) {
			return status.getCandidates().nextSetBit(k+1);
		}

		/**
		 * @see choco.kernel.common.opres.AbstractNoSum#previous(int)
		 */
		@Override
		protected int previous(final int k) {
			for (int i = k-1; i>=0; i--) {
				if(status.getCandidates().get(i)) {return i;}
			}
			//FIXME implem plus efficace ?
			return -1;
		}




	}


}

/**
 * information about load of a bin.
 * used during propagation.
 *
 */
final class BinStatus {

	private final IntDomainVar[] sizes;

	/** The bin. */
	private int bin;

	/** The maximal load. */
	private int mLoad;

	/** The required load. */
	private int rLoad;

	/** The candidate items. */
	private final BitSet candidates;


	public BinStatus(IntDomainVar[] sizes) {
		candidates=new BitSet(sizes.length);
		this.sizes=sizes;
	}

	/**
	 * Change the bin for which it computes the status.
	 *
	 * @param bin the new bin
	 */
	public void set(final int bin, SetVar set) {
		this.bin=bin;
		mLoad=0;
		rLoad=0;
		candidates.clear();
		final DisposableIntIterator iter=set.getDomain().getEnveloppeIterator();
		while(iter.hasNext()) {
			final int item=iter.next();
			if(set.isInDomainKernel(item)) {
				rLoad+=sizes[item].getVal();
			}else {
				candidates.set(item);
				mLoad+=sizes[item].getVal();
			}
		}
        iter.dispose();
		mLoad+=rLoad;
	}


	public void pack(final int item) {
		candidates.clear(item);
		rLoad+=sizes[item].getVal();
	}

	public void remove(final int item) {
		candidates.clear(item);
		mLoad-=sizes[item].getVal();
	}

	public void insertCandidate(final int item) {
		candidates.set(item);
		mLoad+=sizes[item].getVal();
	}

	public final int getBin() {
		return bin;
	}

	public final int getMaxLoad() {
		return mLoad;
	}

	/**
	 * Gets the required load.
	 *
	 */
	public final int getRequiredLoad() {
		return rLoad;
	}

	/**
	 * Gets the candidates load.
	 *
	 * @return the candidates load
	 */
	public final int getCandidatesLoad() {
		return mLoad-rLoad;
	}

	public final BitSet getCandidates() {
		return candidates;
	}

}


final class SumDataStruct {

	/** variables to sum */
	protected final IntDomainVar[] vars;

	/** the constant sum. */
	public final long sum;

	protected long sumMinusInfs;

	protected long sumMinusSups;

	public SumDataStruct(IntDomainVar[] vars, long sum) {
		super();
		this.vars = vars;
		this.sum = sum;
	}

	public void update() {
		sumMinusInfs = sum;
		sumMinusSups = sum;
		for (int i = 0; i < vars.length; i++) {
			sumMinusInfs -= vars[i].getInf();
			sumMinusSups -= vars[i].getSup();
		}
	}

	public Point getBounds(int idx) {
		return new Point( (int) (sumMinusSups + vars[idx].getSup()),
				(int) (sumMinusInfs + vars[idx].getInf()));
	}
}
