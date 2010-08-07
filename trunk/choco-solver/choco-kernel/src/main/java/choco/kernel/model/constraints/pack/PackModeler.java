/* * * * * * * * * * * * * * * * * * * * * * * * *
 *          _       _                            *
 *         |  Â°(..)  |                           *
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

package choco.kernel.model.constraints.pack;

import static choco.Choco.allDifferent;
import static choco.Choco.constantArray;
import static choco.Choco.eq;
import static choco.Choco.geq;
import static choco.Choco.implies;
import static choco.Choco.leq;
import static choco.Choco.makeIntVar;
import static choco.Choco.makeIntVarArray;
import static choco.Choco.makeSetVarArray;
import static choco.Choco.occurrence;
import static choco.Choco.plus;
import static choco.Options.V_BOUND;
import static choco.Options.V_ENUM;
import static choco.Options.V_NO_DECISION;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import choco.kernel.common.util.comparator.IPermutation;
import choco.kernel.common.util.tools.PermutationUtils;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.set.SetVariable;

/**
 * @author Arnaud Malapert</br>
 * @since 4 déc. 2008 <b>version</b> 2.0.1</br>
 * @version 2.0.1</br>
 */
public final class PackModeler {

	private static int nextIndex;

	public final int maxCapacity;

	public final int nbItems;

	public final int nbBins;

	public final IPermutation permutation;

	public final IntegerVariable[] bins;

	public final IntegerConstantVariable[] sizes;

	public final SetVariable[] itemSets;

	public final IntegerVariable[] loads;

	public final IntegerVariable nbNonEmpty;

	public IntegerVariable nbEmpty;

	/**
	 * We assume that the sizes are sorted accorded to a non-increasing order.
	 */
	public PackModeler(IntegerVariable[] bins, IntegerConstantVariable[] sizes,
			SetVariable[] itemSets, IntegerVariable[] loads,
			IntegerVariable nbNonEmpty,int capacity) {
		super();
		this.bins = bins;
		this.nbBins = bins.length;
		this.sizes = sizes;
		this.nbItems = sizes.length;
		this.itemSets = itemSets;
		this.loads = loads;
		this.nbNonEmpty = nbNonEmpty;
		this.nbEmpty = makeIntVar("nbEmpty"+nextID(),0, this.nbBins, V_BOUND,V_NO_DECISION);
		this.maxCapacity = capacity;
		permutation = PermutationUtils.getIdentity();
	}

	public PackModeler(int[] sizes, int nbBins, int capacity) {
		this(nextID(), sizes, nbBins, capacity);
	}

	public PackModeler(String name,int[] sizes, int nbBins, int capacity) {
		this(name, constantArray(sizes), nbBins, capacity);
	}

	public PackModeler(String name,IntegerConstantVariable[] sizes, int nbBins, int capacity) {
		this.maxCapacity=capacity;
		this.nbItems = sizes.length;
		this.nbBins = nbBins;
		this.bins = makeIntVarArray("B"+name, nbItems, 0, this.nbBins-1,V_ENUM);
		//this.bins = makeIntVarArray("bin"+name, nbItems, 0, this.nbBins-1, "cp:bound");
		this.itemSets = makeSetVarArray("S"+name, this.nbBins, 0, this.nbItems-1, V_BOUND,V_NO_DECISION);
		this.loads = makeIntVarArray("L"+name, this.nbBins, 0,this.maxCapacity,V_BOUND,V_NO_DECISION);
		this.nbNonEmpty = makeIntVar("NbNE"+name,0, this.nbBins,V_BOUND);
		//handle permutation
		this.nbEmpty = makeIntVar("NbE"+name,0, this.nbBins,V_BOUND,V_NO_DECISION);
		IPermutation tmp = PermutationUtils.getSortingPermuation(sizes,true);
		permutation = tmp.isIdentity() ? PermutationUtils.getIdentity() : tmp;
		this.sizes= PermutationUtils.applyPermutation(permutation,sizes);
	}

	private static String nextID() {
		return String.valueOf(nextIndex++);
	}

	protected void setNoDecision(Variable v) {
		v.addOption("cp:no_decision");
	}

	public final void setDefaultDecisionVariable() {
		for (int b = 0; b < nbBins; b++) {
			setNoDecision(loads[b]);
			setNoDecision(itemSets[b].getCard());
		}
		setNoDecision(nbNonEmpty);
		setNoDecision(nbEmpty);
	}

	private static Constraint[] toArray(List<Constraint> cstr) {
		return cstr.toArray(new Constraint[cstr.size()]);
	}

	/**
	 * order the items with equal sizes into the bins according to their index.
	 */
	public Constraint[] getEqualSizedItemsSBC(int begin) {
		List<Constraint> cstr = new ArrayList<Constraint>();
		int oldS = -1;
		int idx = begin;
		while(idx<nbItems) {
			final int newS = sizes[idx].getValue();
			if(newS==oldS) {cstr.add(geq(bins[idx],bins[idx-1]));}
			else {oldS=newS;}
			idx++;
		}
		return toArray(cstr);
	}

	/**
	 * add redundant constraint on the {@link NbNonEmptyBins}.
	 * It state an occurence constraint over the nil loads.
	 * It also provide an additional variables {@link NbEmptyBins}.
	 */
	public final Constraint[] getNbNonEmptyBinsRC() {
		return new Constraint[]{
				occurrence(nbEmpty, loads, 0),
				eq(plus(nbEmpty,nbNonEmpty),nbBins)
		};
	}

	private int reuseNbP;
	
	private int reuseNbT;

	private boolean isLargeItem(int idx) {
		return idx < nbItems && sizes[idx].getValue()> maxCapacity/2;	
	}

	private boolean isAdditionalLargeItem(int idx) {
		//the capacity is even and the size of the item is C/2.
		//So, for any previous items s_i + s_j >= C/2 (C/2+1) >= C+1
		return idx < nbItems && 
		sizes[idx].getValue() == maxCapacity/2 && 
		maxCapacity % 2 == 0;
	}
	
	/**
	 * pack the large items in consecutive bins (beginning at 0), then add other leq constraints (bins[i]<=i) if activated.
	 *
	 * @param leqCstr if <code>true</code> then add leq constraints otherwise do not add them.
	 * @return
	 */
	public final Constraint[] getPackLargeItemsSBC(boolean leqCstr) {
		List<Constraint> cstr = new LinkedList<Constraint>();
		reuseNbP=0;
		while( isLargeItem(reuseNbP)) {
			cstr.add( eq(bins[reuseNbP],reuseNbP));
			reuseNbP++;
		}
		if( isAdditionalLargeItem(reuseNbP)) {
			cstr.add( eq(bins[reuseNbP],reuseNbP));
			reuseNbP++;
		}
		reuseNbT=reuseNbP;
		if(leqCstr) {
			final int m = Math.min(nbBins, nbItems);
			while(reuseNbT<m) {
				cstr.add( leq(bins[reuseNbT],reuseNbT));
				reuseNbT++;
			}
		}
		return toArray(cstr);
	}


	//Pack Constraint Option 
	//	public final Constraint[] symBreakEndsWithEmptyBins(int begin,int end) {
	//		final int size= end-begin;
	//		Constraint[] cstr = new Constraint[2*size-1];
	//		IntegerVariable[] b = makeIntVarArray("bool-empty",nbBins, 0, 1);
	//		int idx=0;
	//		for (int i = begin; i < end; i++) {
	//			int j = i-begin;
	//			cstr[idx++]= boolChanneling(b[j], loads[i], 0);
	//			if(i>begin) {
	//				cstr[idx++]= geq(b[j], b[j-1]);
	//			}
	//		}
	//		return cstr;
	//	}


	/**
	 * get the allDifferent constraint for the large items.
	 * @param option allDifferent option
	 */
	public final Constraint getAllDiffLargeItemsRC(String option) {
		final List<IntegerVariable> vars= new LinkedList<IntegerVariable>();
		reuseNbP=0;
		while( isLargeItem(reuseNbP)) {
			vars.add( bins[reuseNbP++]);
		}
		if( isAdditionalLargeItem(reuseNbP)) {
			vars.add( bins[reuseNbP++]);
		}
		reuseNbT=reuseNbP;
		return allDifferent(option,vars.toArray(new IntegerVariable[reuseNbP]));
	}

	private IntegerVariable[] getCardinality() {
		IntegerVariable[] cards =new IntegerVariable[itemSets.length];
		for (int i = 0; i < itemSets.length; i++) {
			cards[i]=itemSets[i].getCard();
		}
		return cards;
	}


	public final Constraint[] getLoadOrderingSBC(boolean breakTie) {
		return getLoadOrderingSBC(0,nbBins,breakTie);
	}

	/**
	 * sort the bins according to the load (non increasing) in a given interval of index.
	 * @param inf minimum of the interval
	 * @param sup maximum of the interval
	 * @param breakTie if <code>true</code> break tie with the cardinality of the itemSets, otherwise no.
	 * @return
	 */
	protected final Constraint[] getLoadOrderingSBC(int inf,int sup, boolean breakTie) {
		return symBreakOrdering(inf, sup, loads, breakTie ? getCardinality() : null);
	}



	private final static Constraint[] symBreakOrdering(int inf,int sup, IntegerVariable[] vars, IntegerVariable[] breakTieVars) {
		final int nb = sup-inf-1;
		boolean breakTie = breakTieVars!=null;
		if(nb>0) {
			final int f =  breakTie ?  2 :1;
			final Constraint[] cstr= new Constraint[f*nb];
			for (int i = inf; i < sup-1; i++) {
				final int idx = i-inf;
				cstr[f*idx] = geq(vars[i], vars[i+1]);
				if(breakTie) {
					cstr[f*idx+1] = implies(
							eq(vars[i], vars[i+1]),
							geq(breakTieVars[i], breakTieVars[i+1])
					);
				}
			}
			return cstr;
		}else {return null;}

	}

	
	public final void statePackLargeItems(Model model,boolean leqCstr, boolean orderEqualSized) {
		model.addConstraints(getPackLargeItemsSBC(leqCstr));
		if(orderEqualSized) model.addConstraints( getEqualSizedItemsSBC(reuseNbP));
	}
	
	public final void statePackLargeItemsThenSortLoad(Model model,boolean breakTie, boolean orderEqualSized) {
		model.addConstraints(getPackLargeItemsSBC(false));
		if(orderEqualSized) model.addConstraints( getEqualSizedItemsSBC(reuseNbP));
		if( reuseNbT < nbBins) model.addConstraints(getLoadOrderingSBC(reuseNbT, nbBins, breakTie));
	}
	/**
	 * We sort bins accroding to non-increasing loads, and 
	 * state a redundant allDifferent for large items.
	 * All bins should be equivalent.
	 */
	public void stateSortLoadAndAllDiff(Model model,boolean breakTie,String option) {
		model.addConstraints(getLoadOrderingSBC(breakTie));
		model.addConstraints(getAllDiffLargeItemsRC(option));
	}

}
