package choco.cp.solver.constraints.global.geost.frames;


import choco.cp.solver.constraints.global.geost.geometricPrim.Region;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Hashtable;
import java.util.Vector;

/**
 * A class that all Frames should extend. It contains info and functionality common to all frames.
 */
public class Frame implements Externalizable {
	
	/**
	 * Integer for the object id and the vector is the relative Forbidden Regions of every shifted box of the shapes of the object
	 */
	private Hashtable<Integer, Vector<Region>> RelForbidRegions;
	
	public Frame()
	{
		RelForbidRegions = new Hashtable<Integer,  Vector<Region>>();
	}

	/**
	 * Gets the Relative forbidden regions of this frame. It return a hash table where the key is an Integer object representing the shape id and the value a vector of Region object.
	 */
	public Hashtable<Integer,  Vector<Region>> getRelForbidRegions()
	{
		return RelForbidRegions;
	}
	
	/**
	 * Adds a given shape id and a Vector of regions to the Frame.
	 */
	public void addForbidRegions(int oid, Vector<Region> regions)
	{
		this.RelForbidRegions.put(oid, regions);
	}
	
	/**
	 * Gets the Relative forbidden regions of a certain shape id. It returns Vector of Region object.
	 */
	public Vector<Region> getRelForbidRegions(int oid)
	{
		return this.RelForbidRegions.get(oid);
	}
	
	/**
	 * Returns the size of the frame.
	 */
	public int size()
	{
		return RelForbidRegions.size();
	}


    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(RelForbidRegions);

    }

    @SuppressWarnings({"unchecked"})
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        //To change body of implemented methods use File | Settings | File Templates.
        RelForbidRegions=(Hashtable<Integer, Vector<Region>>) in.readObject();        
    }
}
