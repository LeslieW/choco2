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
import choco.kernel.common.opres.nosum.INoSumCell;
import choco.kernel.common.opres.nosum.NoSumList;
import choco.kernel.memory.IStateIntVector;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.awt.*;
import java.util.ListIterator;


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
public final class PackFiltering {

	public final IPackSConstraint cstr;

	protected final BitFlags flags;

	/** The sizes of the items. */
	protected final IntDomainVar[] sizes;

	/** The loads of the bins. */
	protected final IntDomainVar[] loads;

	//general propagation info

	/** information about a given bin. */
	private NoSumList reuseStatus;

	/** The no fix point. */
	private boolean noFixPoint;

	protected final SumDataStruct loadSum;

	//TODO protected SumDataStruct cardSum; implémenter les règles


	/**
	 * Instantiates a new 1BP constraint.
	 * @param environment
	 */
	public PackFiltering(IPackSConstraint cstr, BitFlags flags) {
		this.cstr = cstr;
		this.sizes = cstr.getSizes();
		this.loads = cstr.getLoads();
		loadSum = new SumDataStruct(loads,computeTotalSize());
		this.flags = flags;
	}

	/**
	 * Compute the total size and check that sizes are constant.
	 *
	 */
	private long computeTotalSize() {
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
	 * Update the minimal load of a given bin.
	 *
	 * @param bin the index of bin
	 * @param load the new load
	 *
	 * @throws ContradictionException the contradiction exception
	 */
	protected final void updateInfLoad(final int bin,final int load) throws ContradictionException {
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
	protected final void updateSupLoad(final int bin,final int load) throws ContradictionException {
		noFixPoint |= cstr.updateSupLoad(bin, load);
	}

	/**
	 * Do not update status
	 */
	protected final void pack(final int item,final int bin) throws ContradictionException {
		noFixPoint |= cstr.pack(item, bin);
	}


	/**
	 * Do not update status
	 */
	protected final void remove(final int item,final int bin) throws ContradictionException {
		noFixPoint |=  cstr.remove(item, bin);
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
	protected final void loadMaintenance(final int bin) throws ContradictionException {
		updateInfLoad(bin,reuseStatus.getRequiredLoad());
		updateSupLoad(bin,reuseStatus.getMaximumLoad());
	}

	/**
	 * The minimum and maximum load of each bin {@link PackFiltering#loads } is maintained according to the domains of the bin assignment variables.
	 *
	 * @param bin the bin
	 *
	 * @throws ContradictionException the contradiction exception
	 */
	protected final void loadSizeAndCoherence(final int bin) throws ContradictionException {
		final Point p = loadSum.getBounds(bin);
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
	protected final void singleItemEliminationAndCommitment(final int bin) throws ContradictionException {
		final ListIterator<INoSumCell> iter = reuseStatus.listIterator();
		while(iter.hasNext()) {
			final int item = iter.next().getID();
			if(sizes[item].getInf() + reuseStatus.getRequiredLoad()>loads[bin].getSup()) {
				reuseStatus.remove(iter, item);
				remove(item, bin);
			}else if(reuseStatus.getMaximumLoad()-sizes[item].getSup()<loads[bin].getInf()) {
				reuseStatus.pack(iter, item);
				pack(item, bin);
			}
		}
	}

	/**
	 *
	 * @param bin the bin
	 *
	 * @throws ContradictionException the contradiction exception
	 */
	protected final void singleItemEliminationAndCommitmentAndFill(final int bin) throws ContradictionException {
		//Warning: bins must be equivalent ...
		final ListIterator<INoSumCell> iter = reuseStatus.listIterator();
		while(iter.hasNext()) {
			final int item = iter.next().getID();
			if(sizes[item].getInf() + reuseStatus.getRequiredLoad()>loads[bin].getSup()) {
				reuseStatus.remove(iter, item);
				remove(item, bin);
			}else if( reuseStatus.getMaximumLoad()-sizes[item].getSup()<loads[bin].getInf() ||
					reuseStatus.getRequiredLoad()+sizes[item].getInf()==loads[bin].getSup() ) {
				reuseStatus.pack(iter, item);
				pack(item, bin);
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
	protected final void noSumPruningRule(final NoSumList nosum,final int bin) throws ContradictionException {
		if(nosum.noSum(loads[bin].getInf()-reuseStatus.getRequiredLoad(),loads[bin].getSup()-reuseStatus.getRequiredLoad())) {
			cstr.fail();
		}
	}

	/**
	 * Update the load of a given bin with no sum algorithm
	 *
	 *
	 * @throws ContradictionException the contradiction exception
	 */
	protected final void noSumBinLoads(final NoSumList nosum,final int bin) throws ContradictionException {
		int value = loads[bin].getInf()-reuseStatus.getRequiredLoad();
		if(nosum.noSum(value, value) ) {
			updateInfLoad(bin, reuseStatus.getRequiredLoad()+value);
		}
		value = loads[bin].getSup()-reuseStatus.getRequiredLoad();
		if(nosum.noSum(value, value)) {
			updateSupLoad(bin,reuseStatus.getRequiredLoad()+ value);
		}
	}

	/**
	 * use no sum algorithm to pack into or remove from.
	 *
	 *
	 * @throws ContradictionException the contradiction exception
	 */
	protected final void noSumItemEliminationAndCommitment(final NoSumList nosum,final int bin) throws ContradictionException {
		final ListIterator<INoSumCell> iter = reuseStatus.listIterator();
		while(reuseStatus.getNbCandidates() > 1 && iter.hasNext()) {
			final int item = iter.next().getID();
			reuseStatus.remove(iter, item);
			if(nosum.noSum(loads[bin].getInf()-reuseStatus.getRequiredLoad()-sizes[item].getVal(), loads[bin].getSup()-reuseStatus.getRequiredLoad()-sizes[item].getInf())) {
				remove(item, bin);
			}else if (nosum.noSum(loads[bin].getInf()-reuseStatus.getRequiredLoad(),loads[bin].getSup()-reuseStatus.getRequiredLoad())) {
				reuseStatus.packRemoved(item);
				pack(item, bin);
			}else {
				reuseStatus.undoRemove(iter, item);
			}
		}
	}




	//	****************************************************************//
	//	********* PROPAGATION LOOP *************************************//
	//	****************************************************************//



	public void propagate() throws ContradictionException {
		//CPSolver.flushLogs();
		final IStateIntVector abins = cstr.getAvailableBins();
		final int n = abins.size();
		noFixPoint=true;
		while(noFixPoint) {
			noFixPoint=false;
			loadSum.update();
			for (int i = 0; i < n ; i++) {
				propagate( abins.quickGet(i));
			}
		}
		cstr.fireAvailableBins(); 

	}

	/**
	 *
	 * @throws ContradictionException the contradiction exception
	 */
	private void propagate(final int bin) throws ContradictionException {
		loadSizeAndCoherence(bin);
		reuseStatus = cstr.getStatus(bin);
		loadMaintenance(bin);
		if(flags.contains(FILL_BIN)) {singleItemEliminationAndCommitmentAndFill(bin);}
		else {singleItemEliminationAndCommitment(bin);}
		if( flags.contains(ADDITIONAL_RULES) && reuseStatus.getNbCandidates() > 1) {
			noSumPruningRule(reuseStatus,bin);
			noSumBinLoads(reuseStatus,bin);
			noSumItemEliminationAndCommitment(reuseStatus, bin);
		}
	}



	static final class SumDataStruct {

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
}
