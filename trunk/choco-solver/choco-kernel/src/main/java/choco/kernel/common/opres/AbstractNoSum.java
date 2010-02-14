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
package choco.kernel.common.opres;

import choco.kernel.solver.variables.integer.IntDomainVar;

import java.awt.*;

/**
 * A class mainly used in Pack Constraint. The algorithm checks if there is no sum of the sizes which is between given bounds.
 *
 * @author Arnaud Malapert
 */
public abstract class AbstractNoSum {

	/** The sizes of the items. */
	protected final IntDomainVar[] sizes;

	/** The alpha beta. value used to update the load of the bin */
	private Point alphaBeta;

	/**
	 * The Constructor.We assume that the items are sorted in non increasing order.
	 *
	 * @param sizes
	 */
	public AbstractNoSum(final IntDomainVar[] sizes) {
		super();
		this.sizes = sizes;
	}



	protected abstract int getCandidatesLoad();


	/**
	 * Gets the largest item index.
	 *
	 * @return the largest item index
	 */
	protected abstract int getLargestItemIndex();

	/**
	 * Gets the smallest item index.
	 *
	 * @return the smallest item index
	 */
	protected abstract int getSmallestItemIndex();

	/**
	 * give the index of the next candidate item
	 *
	 * @param k the current index
	 *
	 * @return the index of the next item (smaller)
	 */
	protected abstract int next(int k);

	/**
	 * give the index of the previous candidate item
	 *
	 * @param k the current index
	 *
	 * @return the index of the next item (larger)
	 */
	protected abstract int previous(int k);

	/**
	 * No sum indicates the existence of a subset of candidate items which has a load between the parameters.
	 * <br><b>This function can return false negative.</b>
	 *
	 * @param alpha the minimum load expected
	 * @param beta the maximum load expected
	 *
	 * @return <code>true</code> if it do not exist a subset of candidate items which has a load between alpha and beta.
	 */
	public boolean noSum(final int alpha,final int beta) {
		if(alpha<=0 || beta>= getCandidatesLoad()) {return false;}
		else if(this.getCandidatesLoad()<alpha) {return true;}
		int sa=0,sb=0,sc=0;
		int k1=getLargestItemIndex();
		int k2=getSmallestItemIndex();
		int cpt=0;
		while(sc+sizes[k2].getVal()<alpha) {
			sc+=sizes[k2].getVal();
			k2=previous(k2);
		}
		sb=sizes[k2].getVal();
		while(sa<alpha && sb<=beta) {
			sa+=sizes[k1].getVal();
			//sa est croissante => s_0>=alpha pour ne pas lancer l'algorithme : ca evite la première boucle
			k1=next(k1);
			cpt++;
			if(sa<alpha) {
				k2=next(k2);
				sb+=sizes[k2].getVal();
				sc-=sizes[k2].getVal();
				while(sa+sc>=alpha) {
					k2=next(k2);
					sc-=sizes[k2].getVal();
					sb+=sizes[k2].getVal();
					int k3=k2;
					for (int i = 0; i <= cpt; i++) {
						//FIXME a mon avis y a un bug en perspective ici
						k3=previous(k3);
					}
					sb-=sizes[k3].getVal();
				}
			}
		}
		this.alphaBeta=new Point(sa+sc,sb);
		return sa<alpha;

	}

	/**
	 * Gets the value used to update the load.
	 *
	 * @return the alphaBeta
	 */
	public final Point getAlphaBeta() {
		return alphaBeta;
	}

}




