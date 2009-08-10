package choco.cp.solver.constraints.global.geost.externalConstraints;

public class NonOverlappingCircle extends ExternalConstraint{

    public int D;
    public int q;

	public NonOverlappingCircle(int ectrID, int[] dimensions, int[] objectIdentifiers, int D, int q)
	{
		super(ectrID, dimensions, objectIdentifiers);
        this.q=q;
        this.D=D;
	}

}