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
package choco.cp.solver.constraints.global.geost.externalConstraints;

import choco.cp.solver.constraints.global.geost.frames.Frame;
import choco.kernel.model.variables.geost.IExternalConstraint;


/**
 * A class that all external constraints should extend. It contains info and functionality common to all external constraints.
 */
public class ExternalConstraint implements IExternalConstraint{
	
	private int ectrID;
	private int[] dim;
	private int[] objectIds;
	private Frame frame;
	
	public ExternalConstraint(int ectrID, int[] dimensions, int[] objectIdentifiers) 
	{
		this.ectrID = ectrID;
		this.dim = dimensions;
		this.objectIds = objectIdentifiers;
		this.frame = new Frame();
		//UpdateObjectsRelatedConstraintInfo();
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
	
	/**
	 * Gets the frame related to an external constraint
	 */
	public Frame getFrame() {
		return frame;
	}

	/**
	 * Sets the frame related to an external constraint
	 */
	public void setFrame(Frame frame) {
		this.frame = frame;
	}

	
	

}
