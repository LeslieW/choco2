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

import choco.cp.solver.SettingType;
import static choco.cp.solver.SettingType.DYNAMIC_LB;
import choco.cp.solver.constraints.BitFlags;
import choco.kernel.common.opres.pack.AbstractHeurisic1BP;
import choco.kernel.common.opres.pack.BestFit1BP;
import choco.kernel.common.opres.pack.LowerBoundFactory;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.SolverException;
import choco.kernel.solver.constraints.set.AbstractLargeSetIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.set.SetVar;
import gnu.trove.TIntArrayList;
import gnu.trove.TIntProcedure;

import java.util.Arrays;

/**
 * <b>{@link Pack} which maintains a primal-dual packing model.</b><br>
 * The primal model consists of {@link bins} variables. {@link bins}[item] = bin means that item is packed into bin.<br>
 * The dual model consists of {@link svars} variables. item is in {@link svars}[bin] also means that item is packed into bin.
 * @author Arnaud Malapert</br>
 * @since 5 déc. 2008 version 2.0.1</br>
 * @version 2.1.0</br>
 */
public class PackSConstraint extends AbstractLargeSetIntSConstraint implements IPackSConstraint {

	public final PackFiltering filtering;

	protected final BoundNumberOfBins bounds;

	protected final BinStatus status;

	private IStateInt maximumNumberOfNewBins; 

	/** The sizes of the items. */
	protected final IntDomainVar[] sizes;

	/** The loads of the bins. */
	protected final IntDomainVar[] loads;

	/** The bin of each item. */
	protected final IntDomainVar[] bins;

	public final BitFlags flags;

	public PackSConstraint(SetVar[] itemSets, IntDomainVar[] loads, IntDomainVar[] sizes,
			IntDomainVar[] bins,IntDomainVar nbNonEmpty, BitFlags  flags) {
		super(ArrayUtils.append(loads,sizes,bins,new IntDomainVar[]{nbNonEmpty}),itemSets);
		this.loads=loads;
		this.sizes=sizes;
		this.bins =bins;
		this.flags = flags;
		this.bounds = new BoundNumberOfBins();
		filtering = new PackFiltering(this,flags);
		status = new BinStatus(this.sizes);
	}

	public final boolean isEmpty(int bin) {
		return svars[bin].getKernelDomainSize()==0;
	}

	public final int getRequiredSpace(int bin) {
		DisposableIntIterator iter= svars[bin].getDomain().getKernelIterator();
		int load = 0;
		while(iter.hasNext()) {
			load+= sizes[iter.next()].getVal();
		}
		iter.dispose();
		return load;
	}


	public final int getRemainingSpace(int bin) {
		return loads[bin].getSup() - getRequiredSpace(bin);
	}

	protected final boolean isSetEvent(final int varIdx) {
		return varIdx < svars.length;
	}

	protected final int getItemIndex(final int varIdx) {
		int idx = varIdx-(2*loads.length+sizes.length);
		return idx<0 || idx == sizes.length ? -1 : idx;
	}

	protected final int getItemCindice(final int item) {
		return int_cIndices[loads.length+sizes.length+item];
	}

	@Override
	public void setSolver(Solver solver) {
		super.setSolver(solver);
		filtering.setSolver(solver);
		maximumNumberOfNewBins = solver.getEnvironment().makeInt( getNbBins());
	}


	public final void setMaximumNumberOfNewBins(int value) {
		if(value != maximumNumberOfNewBins.get()) {
			this.maximumNumberOfNewBins.set(value);
			constAwake(false);
		}
	}



	public final IntDomainVar[] getBins() {
		return bins;
	}


	//****************************************************************//
	//********* Filtering interface **********************************//
	//****************************************************************//


	@Override
	public final int getNbBins() {
		return loads.length;
	}

	@Override
	public final int getNbItems() {
		return sizes.length;
	}


	@Override
	public final IntDomainVar[] getLoads() {
		return loads;
	}


	@Override
	public final IntDomainVar[] getSizes() {
		return sizes;
	}

	@Override
	public final BinStatus getStatus(int bin) {
		status.set(bin, svars[bin]);
		return status;
	}

	@Override
	public final boolean isFilled(int bin) {
		return svars[bin].isInstantiated();
	}

	@Override
	public final boolean pack(int item, int bin) throws ContradictionException {
		boolean res = svars[bin].addToKernel(item, set_cIndices[bin]);
		if(bins[item].canBeInstantiatedTo(bin)) {
			final DisposableIntIterator iter = bins[item].getDomain().getIterator();
			//remove from other env
			try{
				while(iter.hasNext()) {
					final int b= iter.next();
					if(bin!=b) {
						res |= svars[b].remFromEnveloppe(item, set_cIndices[b]);
					}
				}
			}finally {
				iter.dispose();
			}
			res |= bins[item].instantiate(bin, getItemCindice(item));
		}else {
			LOGGER.warning("should not raise a contradiction here.");
			this.fail();
		}
		return res;
	}

	@Override
	public final boolean remove(int item, int bin) throws ContradictionException {
		boolean res = svars[bin].remFromEnveloppe(item, set_cIndices[bin]);
		res |= bins[item].removeVal(bin, getItemCindice(item));
		if(bins[item].isInstantiated()) {
			final int b = bins[item].getVal();
			svars[b].addToKernel(item, set_cIndices[b]);
		}
		return res;
	}


	@Override
	public final boolean updateInfLoad(int bin, int load) throws ContradictionException {
		return loads[bin].updateInf(load, int_cIndices[bin]);

	}

	@Override
	public final boolean updateNbNonEmpty(int min, int max) throws ContradictionException {
		boolean res = false;
		final int idx = ivars.length-1;
		ivars[idx].updateInf( min, int_cIndices[idx]);
		if( ivars[idx].updateSup(max, int_cIndices[idx])
				&& flags.contains(SettingType.LAST_BINS_EMPTY)) {
			for (int b = max; b < getNbBins(); b++) {
				final DisposableIntIterator iter = svars[b].getDomain().getEnveloppeIterator();
				try{
					while(iter.hasNext()) {
						res |= remove(iter.next(), b);
					}
				}finally {
					iter.dispose();
				}
			}
		}
		return res;
	}

	@Override
	public final boolean updateSupLoad(int bin, int load) throws ContradictionException {
		return loads[bin].updateSup(load, int_cIndices[bin]);
	}

	//****************************************************************//
	//********* Events *******************************************//
	//****************************************************************//



	@Override
	public boolean isConsistent() {
		// really no idea. wait and propagate
		return false;
	}

	protected final void checkBounds(int item) throws ContradictionException {
		bins[item].updateInf(0, getItemCindice(item));
		bins[item].updateSup(svars.length-1, getItemCindice(item));
	}

	protected void checkEnveloppes() throws ContradictionException {
		for (int bin = 0; bin < svars.length; bin++) {
			int inf;
			while( (inf = svars[bin].getEnveloppeInf())<0) {
				svars[bin].remFromEnveloppe(inf,set_cIndices[bin]);
			}
			int sup;
			while( (sup = svars[bin].getEnveloppeSup()) > bins.length-1) {
				svars[bin].remFromEnveloppe(sup,set_cIndices[bin]);
			}
		}
	}
	@Override
	public void awake() throws ContradictionException {
		//initial channeling
		checkEnveloppes();
		for (int item = 0; item < bins.length; item++) {
			checkBounds(item);
			if(bins[item].isInstantiated()) {
				//the item is packed
				final int b0 = bins[item].getVal();
				svars[b0].addToKernel(item,set_cIndices[b0]);
				for (int b = 0; b < b0; b++) {
					svars[b].remFromEnveloppe(item,set_cIndices[b]);
				}
				for (int b = b0+1; b < svars.length; b++) {
					svars[b].remFromEnveloppe(item,set_cIndices[b]);
				}
			}else {
				for (int bin = 0; bin < svars.length; bin++) {
					if(svars[bin].isInDomainEnveloppe(item)) {
						//item could be packed here
						if(svars[bin].isInDomainKernel(item)) {
							//item is packed
							bins[item].instantiate(bin, getItemCindice(item));
						}else if(! bins[item].getDomain().contains(bin)) {
							//in fact, channeling fails
							svars[bin].remFromEnveloppe(item,set_cIndices[bin]);
						}
						//channeling ok enveloppe-domain
					}else {
						//otherwise remove from domain
						bins[item].removeVal(bin, getItemCindice(item));
					}
				}
			}
		}
		super.awake();
	}

	@Override
	public void awakeOnEnv(int varIdx, int x) throws ContradictionException {
		bins[x].removeVal(varIdx, getItemCindice(x));
		//if the item is packed, update variables
		if(bins[x].isInstantiated()) {
			final int b = bins[x].getVal();
			svars[b].addToKernel(x, set_cIndices[b]);
		}
		this.constAwake(false);
	}


	protected void checkDeltaDomain(int item) throws ContradictionException {
		final DisposableIntIterator iter=bins[item].getDomain().getDeltaIterator();
		if(iter.hasNext()) {
			try{
				while(iter.hasNext()) {
					final int b=iter.next();
					svars[b].remFromEnveloppe(item, set_cIndices[b]);
				}
			}finally {
				iter.dispose();
			}
		}else {
			throw new SolverException("empty delta domain: "+bins[item].pretty());
		}
	}

	@Override
	public void awakeOnBounds(int varIndex) throws ContradictionException {
		final int item = getItemIndex(varIndex);
		if(item>=0) {
			//the item is not packed
			//so, we can safely remove from other enveloppes
			checkDeltaDomain(item);
		}
		this.constAwake(false);
	}


	@Override
	public void awakeOnInf(int varIdx) throws ContradictionException {
		awakeOnBounds(varIdx);
	}

	@Override
	public void awakeOnInst(int varIdx) throws ContradictionException {
		if(isSetEvent(varIdx)) {
			DisposableIntIterator iter= svars[varIdx].getDomain().getKernelIterator();
			try{
				while(iter.hasNext()) {
					final int item=iter.next();
					if(! bins[item].isInstantiated()) {
						pack(item,varIdx);
					}
				}
			}finally {
				iter.dispose();
			}
			iter= svars[varIdx].getDomain().getEnveloppeDomain().getDeltaIterator();
			while(iter.hasNext()) {
				final int item=iter.next();
				if(bins[item].getDomain().contains(varIdx)) {
					remove(item, varIdx);
				}
			}

		}else {
			final int item=getItemIndex(varIdx);
			if(item>=0) {
				final int b = bins[item].getVal();
				svars[b].addToKernel(item, set_cIndices[b]);
				checkDeltaDomain(item);
			}
		}
		constAwake(false);
	}


	@Override
	public void awakeOnKer(int varIdx, int x) throws ContradictionException {
		pack(x,varIdx);
		this.constAwake(false);
	}

	@Override
	public void awakeOnRem(int varIdx, int val) throws ContradictionException {
		final int item = getItemIndex(varIdx);
		//remove from associated enveloppe
		if(item>=0) {
			svars[val].remFromEnveloppe(item, set_cIndices[val]);
		}
		this.constAwake(false);
	}

	@Override
	public void awakeOnSup(int varIdx) throws ContradictionException {
		awakeOnBounds(varIdx);
	}

	@Override
	public void propagate() throws ContradictionException {
		do {
			filtering.propagate();
			//feasibility test (DDFF)
			if ( ! bounds.computeBounds(flags.contains(DYNAMIC_LB)) ) fail();
		}while( updateNbNonEmpty(bounds.getMinimumNumberOfBins(), bounds.getMaximumNumberOfBins()));


	}

	@Override
	public boolean isSatisfied() {

		int[] l = new int[loads.length];
		int[] c = new int[loads.length];
		for (int i = 0; i < bins.length; i++) {
			final int b =  bins[i].getVal();
			if( ! svars[b].isInDomainKernel(i)) return false; //check channeling
			l[b] += sizes[i].getVal();
			c[b] ++;
		}
		int nbb = 0;
		for (int i = 0; i < loads.length; i++) {
			if( svars[i].getCard().getVal() != c[i]) return false; //check cardinality
			if( loads[i].getVal() != l[i]) return false; //check load
			if( c[i] != 0) {nbb++;}
		}
		return ivars[ivars.length-1].getVal() == nbb; //check number of bins
	}



	protected final class BoundNumberOfBins {

		private final int[] remainingSpace;
		
		private final int[] itemsMLB;

		private int sizeMLB;
		
		protected int capacityMLB;
		
		private final TIntArrayList binsMLB;

		private int totalSizeCLB;

		private final TIntArrayList binsCLB;

		protected int nbEmpty;

		protected int nbSome;

		protected int nbFull;

		protected int nbNewCLB;

		public BoundNumberOfBins() {
			super();
			itemsMLB=new int[getNbBins() + getNbItems()];
			binsMLB = new TIntArrayList(getNbBins());
			binsCLB = new TIntArrayList(getNbBins());
			remainingSpace = new int[getNbBins()];
		}


		public void reset() {
			Arrays.fill(remainingSpace, 0);
			sizeMLB = 0;
			capacityMLB=0;
			binsMLB.clear();
			totalSizeCLB = 0;
			binsCLB.clear();
			nbEmpty=0;
			nbSome = 0;
			nbFull=0;
			nbNewCLB = 0;
		}

		/**
		 * add unpacked items (MLB) compute their total size (CLB).
		 */
		private void handleItems() {
			final int n = getNbItems();
			for (int i = 0; i < n; i++) {
				final int s = sizes[i].getVal();
				if(bins[i].isInstantiated()) {
					remainingSpace[bins[i].getVal()] -= s;
				}else {
					totalSizeCLB += s;
					itemsMLB[sizeMLB++] = s;
				}
			}
		}


		/**
		 * compute the remaining space in each bin and the cardinality of sets (empty, partially filled, full)
		 */
		private void handleBins() {
			final int n = getNbBins();
			//compute the number of empty, partially filled and closed bins
			//also compute the remaining space in each open bins
			for (int b = 0; b < n; b++) {
				if(svars[b].isInstantiated()) {
					//we ignore closed bins
					if(loads[b].isInstantiatedTo(0)) nbEmpty++;
					else nbFull++;
				}else {
					//the bins is used by the modified lower bound
					binsMLB.add(b);
					remainingSpace[b] += loads[b].getSup();
					capacityMLB = Math.max(capacityMLB, remainingSpace[b]);
					if(svars[b].getKernelDomainSize()>0) {
						//partially filled
						nbSome++;
						totalSizeCLB -= remainingSpace[b]; //fill partially filled bin before empty ones
					} else {
						//still empty
						binsCLB.add(remainingSpace[b]); //record empty bins to fill them later
					}
				}
			}
		}

		/**
		 * compute fake top-items which fills the bin until the current capacity.
		 */
		private void createFakeItems() {
			binsMLB.forEach( new TIntProcedure() {
				@Override
				public boolean execute(int arg0) {
					final int s = capacityMLB - remainingSpace[arg0];
					if( s > 0) itemsMLB[sizeMLB++]  = s;
					return true;
				}
			});
		}

		private void computeMinimumNumberOfNewBins() {
			binsCLB.sort();
			binsCLB.forEachDescending( new TIntProcedure() {
				@Override
				public boolean execute(int arg0) {
					nbNewCLB++;
					if( totalSizeCLB <= arg0) {
						return false;
					}
					totalSizeCLB -= arg0;
					return true;
				}
			});
		}

		/**
		 * 
		 * @param useDDFF do we use advanced and costly bounding procedure for a feaasibility test.
		 * @return <code>false</code>  if the current state is infeasible.
		 */
		public boolean computeBounds(boolean useDDFF) {
			reset();
			//the order of the following calls is important
			handleItems();
			handleBins();
			if( sizeMLB > 0) {
				if( sizeMLB < maximumNumberOfNewBins.get() ) maximumNumberOfNewBins.set(sizeMLB); 
				//there is unpacked items
				//handleBins();
				if( totalSizeCLB > 0) {
					//compute an estimation of the minimal number of additional bins.
					if( binsCLB.isEmpty()) return false;  //no more available bins for remaining unpacked items
					computeMinimumNumberOfNewBins();
				}
				if( getMinimumNumberOfBins() > ivars[ivars.length - 1].getSup()) return false; //the continous bound prove infeasibility
				if( useDDFF) {	
					createFakeItems(); 
					int[] items = getItems();
					final int ub=new BestFit1BP(items,capacityMLB,AbstractHeurisic1BP.SORT).computeUB();
					if( ub > binsMLB.size()) {
						//the heuristics solution is infeasible
						//so, the lower bound could also be infeasible
						final int lb = LowerBoundFactory.computeL_DFF_1BP(items, capacityMLB,ub);
						if( lb > binsMLB.size()) return false;
					}//otherwise, the modified instance is feasible with best fit heuristics
				}
			}
			return true;
		}


		public int[] getItems() {
			return Arrays.copyOf(itemsMLB, sizeMLB);
		}

		public int getMaximumNumberOfBins() {
			return Math.min(getNbBins() -nbEmpty, nbFull + nbSome + maximumNumberOfNewBins.get());
		}

		public int getMinimumNumberOfBins() {
			return nbFull + nbSome + nbNewCLB;
		}
	}

}