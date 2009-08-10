package choco.cp.solver.constraints.global.geost.externalConstraints;

/**
 * Created by IntelliJ IDEA.
 * User: szampelli
 * Date: 3 févr. 2009
 * Time: 16:29:15
 * To change this template use File | Settings | File Templates.
 */
public class DistLinear extends ExternalConstraint{

    public int o1;
    public int[] a;
    public int b;

	public DistLinear(int ectrID, int[] dimensions, int[] objectIdentifiers, int[] a, int b)
	{
        super(ectrID, dimensions, null);
        int[] oids = new int[1];
        oids[0] = objectIdentifiers[0];
        setObjectIds(oids); //Prune only the first object!
        o1=objectIdentifiers[0];
        this.a=a; this.b=b;       
	}

}