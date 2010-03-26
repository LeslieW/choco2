package choco.cp.solver.constraints.global.geost.internalConstraints;

import java.io.Serializable;

/**
 * A class that all internal constraints should extend. It contains info and functionality common to all internal constraints.
 */
public class InternalConstraint implements Serializable {
	
	private int ictrID = 0;
	
	public InternalConstraint(int id)
	{
		this.ictrID = id;
	}
	
	public final int getIctrID()
	{
		return this.ictrID;
	}

	public final void setIctrID(int id)
	{
		this.ictrID=id;
	}
	
}
