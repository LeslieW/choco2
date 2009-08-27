package choco.cp.solver.constraints.global.geost.externalConstraints;


import choco.cp.solver.constraints.global.geost.frames.Frame;


/**
 * A class that all external constraints should extend. It contains info and functionality common to all external constraints.
 */
public class ExternalConstraint {
	
	protected int ectrID;
	protected int[] dim;
	protected int[] objectIds;
	protected Frame frame;
    public static int maxId=0;



    public ExternalConstraint()
    {
    }

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
