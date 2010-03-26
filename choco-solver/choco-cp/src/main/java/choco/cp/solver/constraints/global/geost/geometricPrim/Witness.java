package choco.cp.solver.constraints.global.geost.geometricPrim;

import choco.kernel.memory.trailing.StoredInt;

import static java.lang.System.arraycopy;


public final class Witness {
	
	private StoredInt[] coords;
	private int dim;
	
	public Witness(int dim)
	{	//creates a point at the origin of the coordinate base.
		this.dim = dim;
		coords =  new StoredInt[this.dim];
		for (int i = 0; i < this.dim; i++){
			this.coords[i].set(0);
        }
	}
	
	public Witness(Witness w)
	{
		//creates a point from another point.
		coords =  new StoredInt[this.dim];
		for(int i = 0; i < w.getCoords().length; i++){
			this.coords[i].set(w.getCoord(i));
        }
	}
	
	public StoredInt[] getCoords()
	{
		return this.coords;
	}
	
	public void setCoords(StoredInt coordinates[])
	{
        arraycopy(coordinates, 0, this.coords, 0, coordinates.length);
	}
	
	public int getCoord(int index)
	{
		return this.coords[index].get();
	}
	
	public void setCoord(int index, int value)
	{
		this.coords[index].set(value);
	}
	
}
