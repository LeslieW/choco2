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
package choco.cp.solver.constraints.global.scheduling.trees;

import choco.kernel.common.IDotty;
import choco.kernel.common.opres.graph.ITree;
import choco.kernel.solver.variables.scheduling.ITask;


/**
 * @author Arnaud Malapert</br> 
 * @since 10 févr. 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public interface IVilimTree extends IDotty, ITree {
	
	
	public enum TreeMode {
		ECT(true,"ECT"), LST(false,"LST");

		private final boolean value;
		
		private final String label;

		TreeMode(boolean value, String label) {
			this.value = value;
			this.label = label;
		}

		
		public final String label() {
			return label;
		}

		public final boolean value() {
			return value;
		}
	}

	boolean contains(ITask task);

	void insert(ITask task);

	void remove(ITask task);

	TreeMode getMode();

	void setMode(TreeMode mode);

	void sort();

	void reset();
	
	int getTime();
}
