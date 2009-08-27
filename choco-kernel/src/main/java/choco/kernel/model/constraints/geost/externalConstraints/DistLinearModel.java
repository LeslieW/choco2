package choco.kernel.model.constraints.geost.externalConstraints;

/**
 * Created by IntelliJ IDEA.
 * User: szampelli
 * Date: 3 févr. 2009
 * Time: 16:29:15
 * To change this template use File | Settings | File Templates.
 */
public class DistLinearModel extends IExternalConstraint{

    public int o1;
    public int[] a;
    public int b;

	public DistLinearModel(int ectrID, int[] dimensions, int[] objectIdentifiers, int[] a, int b)
	{
        super(ectrID, dimensions, null);
        int[] oids = new int[1];
        oids[0] = objectIdentifiers[0];
        setObjectIds(oids); //Prune only the first object!
        o1=objectIdentifiers[0];
        this.a=a; this.b=b;
	}

    public String toString() {
        String r="";
        r+="Linear(o:"+o1+" [";
        for (int i=0; i<a.length; i++) r+=a[i]+" ";
        r+="].X<="+b+")";        
        return r;

    }

}