/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien    1999-2008      *
 **************************************************/
package choco.kernel.model.constraints.geost.externalConstraints;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 9 févr. 2009
* Since : Choco 2.0.1
* Update : Choco 2.0.1
*/
public abstract class IExternalConstraint {
    protected int ectrID;
    protected int[] dim;
    protected int[] objectIds;

    public IExternalConstraint()
    {
    }

	public IExternalConstraint(int ectrID, int[] dimensions, int[] objectIdentifiers)
	{
		this.ectrID = ectrID;
		this.dim = dimensions;
		this.objectIds = objectIdentifiers;
	}

	/**
	 * Gets the list of dimensions that an external constraint is active for.
	 */
	public int[] getDim() {
		return dim;
	}

	/**
	 * Gets the external constraint ID
	 */
	public int getEctrID() {
		return ectrID;
	}

	/**
	 * Gets the list of object IDs that this external constraint affects.
	 */
	public int[] getObjectIds() {
		return objectIds;
	}

	/**
	 * Sets the list of dimensions that an external constraint is active for.
	 */
	public void setDim(int[] dim) {
		this.dim = dim;
	}

//	public void setEctrID(int ectrID) {
//		this.ectrID = ectrID;
//	}

	/**
	 * Sets the list of object IDs that this external constraint affects.
	 */
	public void setObjectIds(int[] objectIds) {
		this.objectIds = objectIds;
	}




}
