package choco.kernel.model.constraints.geost.externalConstraints;

import choco.kernel.model.variables.integer.IntegerVariable;

/**
 * Created by IntelliJ IDEA.
 * User: szampelli
 * Date: 7 aožt 2009
 * Time: 18:56:08
 * To change this template use File | Settings | File Templates.
 */
public class DistGeqModel extends IExternalConstraint {

    public int D;
    public int o1;
    public int o2;
    public int q;
    public IntegerVariable modelDVar = null;

	public DistGeqModel(int ectrID, int[] dimensions, int[] objectIdentifiers, int D_, int q_)
	{
        super(ectrID, dimensions, null);
        int[] oids = new int[1];
        oids[0] = objectIdentifiers[0];

        setObjectIds(oids); //Prune only the first object!
        D=D_;
        o1=objectIdentifiers[0];
        o2=objectIdentifiers[1];
        q=q_;
	}

    public DistGeqModel(int ectrID, int[] dimensions, int[] objectIdentifiers, int D_, int q_, IntegerVariable var)
    {
        super(ectrID, dimensions, null);
        int[] oids = new int[1];
        oids[0] = objectIdentifiers[0];
        setObjectIds(oids); //Prune only the first object!
        D=D_;
        o1=objectIdentifiers[0];
        o2=objectIdentifiers[1];
        q=q_;
        modelDVar=var;
    }


    public String toString() {
        String r="";
        if (modelDVar!=null) r+="Geq(D=["+modelDVar.getLowB()+","+modelDVar.getUppB()+"],q="+q+",o1="+o1+",o2="+o2+")";
        else  r+="Geq(D="+D+",q="+q+",o1="+o1+",o2="+o2+")";
        return r;
    }

    public boolean hasDistanceVar() { return (modelDVar!=null); }

    public IntegerVariable getDistanceVar() { return modelDVar; }


}
