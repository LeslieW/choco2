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

import static choco.Choco.*;
import choco.kernel.common.util.comparator.IPermutation;
import choco.kernel.common.util.tools.PermutationUtils;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.set.SetVariable;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Arnaud Malapert</br>
 * @since 4 déc. 2008 <b>version</b> 2.0.1</br>
 * @version 2.0.1</br>
 */
public class PackModeler {

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
		this.nbEmpty = makeIntVar("nbEmpty"+nextID(),0, this.nbBins, "cp:bound");
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
		this.bins = makeIntVarArray("B"+name, nbItems, 0, this.nbBins-1, "cp:enum");
		//this.bins = makeIntVarArray("bin"+name, nbItems, 0, this.nbBins-1, "cp:bound");
		this.itemSets = makeSetVarArray("S"+name, this.nbBins, 0, this.nbItems-1,"cp:bound");
		this.loads = makeIntVarArray("L"+name, this.nbBins, 0,this.maxCapacity, "cp:bound");
		this.nbNonEmpty = makeIntVar("nbNonEmpty-"+name,0, this.nbBins, "cp:bound");
		//handle permutation
		this.nbEmpty = makeIntVar("nbEmpty-"+name,0, this.nbBins, "cp:bound");
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

	public void setDefaultDecisionVariable() {
		for (int b = 0; b < nbBins; b++) {
			setNoDecision(loads[b]);
			setNoDecision(itemSets[b].getCard());
		}
		setNoDecision(nbNonEmpty);
		setNoDecision(nbEmpty);
	}

	protected Constraint[] toArray(List<Constraint> cstr) {
		return cstr.toArray(new Constraint[cstr.size()]);
	}

	/**
	 * sort the items with equal size into the bins according to their index.
	 */
	public Constraint[] symBreakEqualSizedItems() {
		List<Constraint> cstr = new LinkedList<Constraint>();
		int v = -1;
		int idx = 0;
		while(idx<nbItems) {
			final int s = sizes[idx].getValue();
			if(s==v) {cstr.add(geq(bins[idx],bins[idx-1]));}
			else {v=s;}
			idx++;
		}
		return toArray(cstr);
	}

	/**
	 * add redundant constraint on the {@link NbNonEmptyBins}.
	 * It state an occurence constraint over the nil loads.
	 * It also provide an additional variables {@link NbEmptyBins}.
	 */
	public final Constraint[] redundantCstrNbNonEmptyBins() {
		return new Constraint[]{
				occurrence(0, nbEmpty, loads),
				eq(plus(nbEmpty,nbNonEmpty),nbBins)
		};
	}

	private int nbPack;
	/**
	 * pack the large items in consecutive bins (beginning at 0), then add other leq constraints (bins[i]<=i) if any.
	 *
	 * @param leqCstr if <code>true</code> then add leq constraints otherwise do not add them.
	 * @return
	 */
	public final Constraint[] symBreakPackLargeItems(boolean leqCstr) {
		List<Constraint> cstr = new LinkedList<Constraint>();
		nbPack=0;
		while(nbPack< nbItems && sizes[nbPack].getValue()> maxCapacity/2) {
			cstr.add( eq(bins[nbPack],nbPack));
			nbPack++;
		}
		//TODO add one more item with s= c/2 if c is even.
		if(leqCstr) {
			int m = Math.min(nbBins, nbItems);
			while(nbPack<m) {
				cstr.add( leq(bins[nbPack],nbPack));
				nbPack++;
			}
		}
		return toArray(cstr);
	}



	public final Constraint[] symBreakEndsWithEmptyBins(int begin,int end) {
		final int size= end-begin;
		Constraint[] cstr = new Constraint[2*size-1];
		IntegerVariable[] b = makeIntVarArray("bool-empty",nbBins, 0, 1);
		int idx=0;
		for (int i = begin; i < end; i++) {
			int j = i-begin;
			cstr[idx++]= boolChanneling(b[j], loads[i], 0);
			if(i>begin) {
				cstr[idx++]= geq(b[j], b[j-1]);
			}
		}
		return cstr;
	}

	public final Constraint redundantCstrAllDiffLargeItems() {
		return redundantCstrAllDiffLargeItems(null);
	}

	/**
	 * get the allDifferent constraint for the large items.
	 * @param option allDifferent option
	 */
	public final Constraint redundantCstrAllDiffLargeItems(String option) {
		final List<IntegerVariable> vars= new LinkedList<IntegerVariable>();
		int idx=0;
		while(sizes[idx].getValue()> maxCapacity/2) {
			vars.add(bins[idx]);
			idx++;
		}
		final IntegerVariable[] large= vars.toArray(new IntegerVariable[vars.size()]);
		return option==null ? allDifferent(large) : allDifferent(option,large);
	}

	protected IntegerVariable[] getCardinality() {
		IntegerVariable[] cards =new IntegerVariable[itemSets.length];
		for (int i = 0; i < itemSets.length; i++) {
			cards[i]=itemSets[i].getCard();
		}
		return cards;
	}

	public final Constraint[] symBreakLoadOrdering(boolean breakTie) {
		return symBreakLoadOrdering(0,nbBins,breakTie);
	}

	/**
	 * sort the bins according to the load (non increasing) in a given interval of index.
	 * @param inf minimum of the interval
	 * @param sup maximum of the interval
	 * @param breakTie if <code>true</code> break tie with the cardinality of the itemSets, otherwise no.
	 * @return
	 */
	public final Constraint[] symBreakLoadOrdering(int inf,int sup, boolean breakTie) {
		return symBreakOrdering(inf, sup, loads, breakTie ? getCardinality() : null);
	}



	public final Constraint[] symBreakOrdering(int inf,int sup, IntegerVariable[] vars, IntegerVariable[] breakTieVars) {
		final int nb = sup-inf-1;
		boolean breakTie = breakTieVars!=null;
		if(nb>0) {
			final int f =  breakTie ?  2 :1;
			final Constraint[] cstr= new Constraint[f*nb];
			for (int i = inf; i < sup-1; i++) {
				final int idx = i-inf;
				cstr[f*idx] = geq(vars[i], vars[i+1]);
				if(breakTie) {
					cstr[f*idx+1] = ifThenElse(
							eq(vars[i], vars[i+1]),
							geq(breakTieVars[i], breakTieVars[i+1]),
							TRUE
					);
				}
			}
			return cstr;
		}else {return null;}

	}

	public void packAll(Model model) {
		packAll(model, false, false);
	}

	/**
	 *
	 * We pack the maximum number of items, then we sort the remaining empty bins according to their loads.
	 * All bins should be equivalent.
	 * @param model
	 */
	public void packAll(Model model,boolean leqCstr, boolean breakTie) {
		model.addConstraints(symBreakPackLargeItems(leqCstr));
		Constraint[] ordering =symBreakLoadOrdering(nbPack, nbBins, breakTie);
		if(ordering!=null) {
			model.addConstraints(ordering);
		}
	}

	/**
	 * we order the bins according to their loads.
	 * <i>If it bins are equivalents, you should prefer the {@link packAll} symmetry breaking </i>
	 * @param model
	 */
	public void sortAll(Model model) {
		sortAll(model,false,null);
	}

	/**
	 * We sort bins accroding to their load, then we post a allDifferent for large items.
	 * All bins should be equivalent.
	 */
	public void sortAll(Model model,boolean breakTie,String option) {
		model.addConstraints(symBreakLoadOrdering(breakTie));
		model.addConstraints(redundantCstrAllDiffLargeItems(option));
	}

}
