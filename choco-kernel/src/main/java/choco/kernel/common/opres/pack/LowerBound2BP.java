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

package choco.kernel.common.opres.pack;

import java.util.BitSet;



/**
 * This class implements lower bound for the 2D bin packing problem.
 * The LowerBound are proposed by F. Clautiaux in his phd thesis.
 * You can compute the L_{2CM}=max(L^{DFF},L_{2CM}_2) lower bound.
 *
 * @author Arnaud Malapert (arnaud.malapert@emn.fr)
 */
public class LowerBound2BP {


	/** The Constant A_GR. */
	private final static int A_GR=0;

	/** The Constant A_HT. */
	private final static int A_HT=1;

	/** The Constant A_LG. */
	private final static int A_LG=2;

	/** The Constant A_PT. */
	private final static int A_PT=3;


	/** The size of the problem. */
	private final int n;

	/** The widths. */
	private final int[] widths;

	/** The bin's width. */
	private final int cwidth;

	/** The heights. */
	private final int[] heights;

	/** The bin's height. */
	private final int cheight;

	/** The x axis first. */
	private boolean xAxisFirst=true;

	/**
	 * Instantiates a new lower bound2 BP.
	 *
	 * @param widths the widths
	 * @param cwidth the cwidth
	 * @param heights the heights
	 * @param cheight the cheight
	 */
	public LowerBound2BP(final int[] widths,final int cwidth,final int[] heights,final int cheight) {
		this(widths,cwidth,heights,cheight,false);
	}

	/**
	 * The Constructor.
	 *
	 * @param cwidth the cwidth
	 * @param cheight the cheight
	 * @param widths the width
	 * @param heights the height
	 * @param multiplyBy2 the multiply by2
	 */
	public LowerBound2BP(final int[] widths,final int cwidth,final int[] heights,final int cheight,final boolean multiplyBy2) {
		super();
		this.widths = widths;
		this.heights = heights;
		this.n=this.widths.length;
		if(multiplyBy2) {
			//multiply all by two : improve the bounds
			this.cwidth = 2*cwidth;
			this.cheight = 2*cheight;
			for (int i = 0; i < widths.length; i++) {
				this.widths[i]*=2;
				this.heights[i]*=2;
			}
		}else {
			this.cwidth = cwidth;
			this.cheight = cheight;
		}
	}



	/**
	 * Checks if is x axis first.
	 *
	 * @return true, if is x axis first
	 */
	public final boolean isXAxisFirst() {
		return xAxisFirst;
	}

	/**
	 * Sets the x axis first.
	 *
	 * @param axisFirst the new x axis first
	 */
	public final void setXAxisFirst(final boolean axisFirst) {
		xAxisFirst = axisFirst;
	}

	public long getArea() {
		return cwidth*cheight;
	}

	public long getEnergy() {
		long s=0;
		for (int i = 0; i < n; i++) {
			s+=widths[i]*heights[i];
		}
		return s;
	}
	/**
	 * Compute the continous lower bound.
	 *
	 * @return the lower bound
	 */
	public int computeL0() {
		final long s=getEnergy();
		return AbstractDDFF.round(s, getEnergy());
	}


	/**
	 * an estimation function used to reduce the computation of {@link LowerBound2BP#computeL_DFF()}.
	 *
	 * @param ddff1 the first DDFF
	 * @param ddff2 the second DDFF
	 *
	 */
	private static final int estimate(final AbstractDDFF ddff1,final AbstractDDFF ddff2) {
		double r=0;
		for (int i = ddff1.selected.nextSetBit(0); i >= 0; i = ddff1.selected.nextSetBit(i + 1)) {
		//for (int i = 0; i < ddff1.sizes.length; i++) {
			final int nb=ddff2.storedCapacity/ddff2.storedSizes[i];
			final double tmp= ( (double) ddff1.sizes[i])/ddff1.capacity;
			r+=(tmp/nb);
		}
		return (int) Math.ceil(r);
	}

	/**
	 * Internal loop {@link LowerBound2BP#computeL_DFF()}.
	 *
	 * @param ddff1 the first DDFF
	 * @param ddff2 the second DDFF
	 *
	 * @return the lower bound
	 */
	private int internalLoopDFF(final AbstractDDFF ddff1,final AbstractDDFF ddff2) {
		int lb=0;
		ddff2.setStoredSelected(ddff1.getSelected());
		final BitSet params=ddff2.selectParameters();
		//for (int k = 1;k <=cheight/2; k++) {
		for (int k = params.nextSetBit(0); k >= 0; k = params.nextSetBit(k + 1)) {
			ddff2.applyFunction(k);
			final int v=LowerBoundFactory.computeL0(ddff1, ddff2);
			if(v>lb) {lb=v;}
			ddff2.reset();
		}
		return lb;
	}



	/**
	 * Compute L^{DFF}.
	 *
	 * @return L^{DFF}
	 */
	public int computeL_DFF() {
		return this.computeL_DFF(xAxisFirst);
	}

	/**
	 * Compute L^{DFF}.
	 *
	 * @param xAxisFirst the x axis first
	 *
	 * @return L^{DFF}
	 */
	public int computeL_DFF(final boolean xAxisFirst) {
		AbstractDDFF[] ddff1=LowerBoundFactory.createDDFF(widths, cwidth,null);
		AbstractDDFF[] ddff2=LowerBoundFactory.createDDFF(heights,cheight,null);
		if(!xAxisFirst) {
			final AbstractDDFF[] temp=ddff1;
			ddff1=ddff2;
			ddff2=temp;
		}
		int lb=0;
		for (int u = 0; u < ddff1.length; u++) {
			final BitSet params1=ddff1[u].selectParameters();
			for (int k1 = params1.nextSetBit(0);k1 >=0; k1=params1.nextSetBit(k1+1)) {
				//for (int k1 = 1;k1 <=cwidth/2; k1++) {
				if(ddff1[u].applyFunction(k1)>=lb) {
					//f_1
					int v=internalLoopDFF(ddff1[u], ddff2[1]);
					if(v>lb) {lb=v;}
					if(estimate(ddff1[u],ddff2[1])>=lb) {
						//f_0 et f_2
						v=Math.max(internalLoopDFF(ddff1[u], ddff2[0]),internalLoopDFF(ddff1[u], ddff2[2]));
						if(v>lb) {lb=v;}
					}
				}
				ddff1[u].reset();
			}

		}
		return lb;
	}



	/**
	 * Extract subsets for BM lower bounds.
	 *
	 * @param k the x  parameter
	 * @param l the y parameter
	 *
	 * @return the sets of items
	 */
	private BitSet[] extractSubsets(final int k,final int l) {
		BitSet[] subsets=new BitSet[4];
		for (int i = 0; i < subsets.length; i++) {
			subsets[i]=new BitSet(n);
		}
		//A_{gr}
		for (int i = 0; i < n; i++) {
			if(heights[i]>cheight-l && widths[i]>cwidth-k) {subsets[A_GR].set(i);}
		}
		//A_{ht} && A_{lg}
		int cpt=-1;
		while( (cpt=subsets[A_GR].nextClearBit(cpt+1)) <n) {
			if(heights[cpt]>cheight-l && widths[cpt]>=k) {subsets[A_HT].set(cpt);}
			else if(heights[cpt]>=l && widths[cpt]>cwidth-k) {subsets[A_LG].set(cpt);}
		}
		//A_{pt}
		subsets[A_PT].set(0, n);
		for (int i = 0; i < subsets.length-1; i++) {
			subsets[A_PT].andNot(subsets[i]);
		}

		cpt=-1;
		while ( (cpt=subsets[A_PT].nextSetBit(cpt+1))!=-1) {
			if(heights[cpt]<l || widths[cpt]<k) {subsets[A_PT].clear(cpt);}
		}
		return subsets;
	}



	/**
	 * Compute the second 1BP lower bound of Boschetti and Mingozzi.
	 *
	 * @param subsets the subsets of items
	 *
	 * @return L_^{DFF}_{1BP} of the constructed instance
	 *
	 * @deprecated This lower bound do not improve the {@link LowerBound2BP#computeL_2CM()}
	 */
	@Deprecated
	private int computeBM_1BP_1(final BitSet[] subsets) {
		int s=0;
		for (int i = 1; i < subsets.length; i++) {
			s+=subsets[i].cardinality();
		}
		int[] sizes=new int[s];
		int i=0;
		int cpt=-1;
		while( (cpt=subsets[A_HT].nextSetBit(cpt+1))!=-1) {
			sizes[i]=widths[cpt]*cheight;
			i++;
		}
		while( (cpt=subsets[A_LG].nextSetBit(cpt+1))!=-1) {
			sizes[i]=cwidth*heights[cpt];
			i++;
		}
		while( (cpt=subsets[A_PT].nextSetBit(cpt+1))!=-1) {
			sizes[i]=widths[cpt]*heights[cpt];
			i++;
		}
		return LowerBoundFactory.computeL_DFF_1BP(sizes, cwidth*cheight)+subsets[A_GR].cardinality();
	}

	/**
	 * Compute l_ DFF.
	 *
	 * @param ddff the DDFFs
	 * @param selected the selected items
	 *
	 * @return the lower bound
	 */
	private final int computeL_DFF(final AbstractDDFF[] ddff,final BitSet selected) {
		int r=0;
		if(selected.cardinality()>0) {
			for (int i = 0; i < ddff.length; i++) {
				ddff[i].setStoredSelected(selected);
			}
			r=LowerBoundFactory.computeL_DFF_1BP(ddff);

		}
		return r;

	}

	/**
	 * Compute the second 1BP lower bound of Boschetti and Mingozzi.
	 *
	 * @param subsets the subsets of items
	 * @param ddffw the x DDFF
	 * @param ddffh the y DDFF
	 *
	 * @return the L_^{DFF}_{1BP} of the constructed instance
	 */
	private int computeBM2(final BitSet[] subsets,final AbstractDDFF[] ddffw,final AbstractDDFF[] ddffh) {
		return this.computeL_DFF(ddffw,subsets[A_HT])
		+ this.computeL_DFF(ddffh,subsets[A_LG])
		+subsets[A_GR].cardinality();
	}



	/**
	 * Compute L_^{2CM}_2.
	 *
	 * @return the  L_^{2CM}_2
	 * @deprecated old version used {@link LowerBound2BP#computeBM_1BP_1(BitSet[])}
	 */
	@Deprecated
	public int computeL_2CM_2_old() {
		int v=0;
		for (int k = 1; k <= cwidth/2; k++) {
			for (int l = 1; l <= cheight/2; l++) {
				final BitSet[] subsets=extractSubsets(k, l);
				final int l21=computeBM_1BP_1(subsets);
				final int l22=this.computeBM_1BP_1(subsets);
				final int l2=l21<l22 ? l22 : l21;
				if(v<l2) {v=l2;}
			}
		}
		return v;
	}


	/**
	 * Sets the parameter.
	 *
	 * @param params the params
	 * @param size the size
	 * @param capacity the capacity
	 */
	private void setParameter(final BitSet params,final int size,final int capacity) {
		final int k=Math.min(size+1, capacity-size+1);
		if(AbstractDDFF.isValidParameter(k, capacity)) {
			params.set(k);
		}
	}

	/**
	 * Select parameters for BM lower bounds.
	 *
	 * @return the set of parameters for each dimension
	 */
	private BitSet[] selectParametersBM() {
		final BitSet[] select=new BitSet[2];
		select[0]=new BitSet(cwidth/2+1);
		select[1]=new BitSet(cheight/2+1);
		select[0].set(1);
		select[1].set(1);
		for (int i = 0; i < widths.length; i++) {
			if(widths[i]>cwidth/2 || widths[i]>cwidth/2) {
				setParameter(select[0],widths[i], cwidth);
				setParameter(select[1],heights[i], cheight);
			}
		}
		return select;
	}

	/**
	 * Compute L_^{2CM}_2.
	 *
	 * @return the  L_^{2CM}_2
	 */
	public int computeL_2CM_2() {
		int v=0;
		final AbstractDDFF[] ddffw=LowerBoundFactory.createDDFF(widths, cwidth, null);
		final AbstractDDFF[] ddffh=LowerBoundFactory.createDDFF(heights, cheight, null);
		final BitSet[] select=selectParametersBM();
		for (int k = select[0].nextSetBit(0); k >= 0; k = select[0].nextSetBit(k + 1)) {
			for (int l = select[1].nextSetBit(0); l >= 0; l = select[1].nextSetBit(l + 1)) {
				final BitSet[] subsets=extractSubsets(k, l);
				final int l2=computeBM2(subsets,ddffw,ddffh);
				if(v<l2) {v=l2;}
			}
		}
		return v;
	}

	/**
	 * Compute L_^{2CM}.
	 *
	 * @return max(L^{DFF},L_^{2CM}_2
	 */
	public int computeL_2CM() {
		final int lb1=this.computeL_DFF();
		final int lb2=this.computeL_2CM_2();
		return lb1 <lb2 ? lb2 : lb1;
	}


}
