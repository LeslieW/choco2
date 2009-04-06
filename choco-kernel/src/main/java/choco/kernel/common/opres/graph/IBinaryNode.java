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
package choco.kernel.common.opres.graph;

import choco.kernel.common.IDotty;

/**
 * @author Arnaud Malapert : arnaud(dot)malapert(at)emn(dot)fr
 *
 */
/**
 * @author Arnaud Malapert</br> 
 * @since version 2.0.0</br>
 * @version 2.0.3</br>
 */
public interface IBinaryNode extends IDotty {

	int getID();

	boolean hasFather();

	IBinaryNode getFather();

	void setFather(IBinaryNode father);

	boolean isLeaf();

	boolean hasLeftChild();

	boolean hasRightChild();

	IBinaryNode getLeftChild();

	IBinaryNode getRightChild();

	void setLeftChild(IBinaryNode leftChild);

	void setRightChild(IBinaryNode rightChild);

	INodeLabel getNodeStatus();

	void fireStatusChanged();

	public void clear();

}