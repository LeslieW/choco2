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
        this(ectrID,dimensions,objectIdentifiers,D_,q_,null);
	}

    public DistGeqModel(int ectrID, int[] dimensions, int[] objectIdentifiers, int D_, int q_, IntegerVariable var)
    {
        super(ectrID, dimensions, null);
        int[] oids = new int[2];
        oids[0] = objectIdentifiers[0];
        oids[1] = objectIdentifiers[1];

        setObjectIds(oids); //Prune only the first object!
        D=D_;
        o1=objectIdentifiers[0];
        o2=objectIdentifiers[1];
        q=q_;
        setObjectIds(oids);
        modelDVar=var;
    }


    public String toString() {
        StringBuilder r= new StringBuilder();
        if (modelDVar!=null){
            r.append("Geq(D=[").append(modelDVar.getLowB()).append(",").append(modelDVar.getUppB())
                    .append("],q=").append(q).append(",o1=").append(o1).append(",o2=").append(o2).append(")");
        }
        else{
            r.append("Geq(D=").append(D).append(",q=").append(q).append(",o1=").append(o1)
                    .append(",o2=").append(o2).append(")");
        }
        return r.toString();
    }

    public boolean hasDistanceVar() { return (modelDVar!=null); }

    public IntegerVariable getDistanceVar() { return modelDVar; }


}
