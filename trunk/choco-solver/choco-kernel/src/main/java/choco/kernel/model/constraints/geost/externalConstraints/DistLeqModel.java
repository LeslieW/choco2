package choco.kernel.model.constraints.geost.externalConstraints;

import choco.kernel.model.variables.integer.IntegerVariable;

/**
 * Created by IntelliJ IDEA.
 * User: szampelli
 * Date: 7 ao�t 2009
 * Time: 18:41:21
 * To change this template use File | Settings | File Templates.
 */
public class DistLeqModel extends IExternalConstraint {

    public int D;
    public int o1;
    public int o2;
    public int q;
    public IntegerVariable modelDVar = null;

    public DistLeqModel(int ectrID, int[] dimensions, int[] objectIdentifiers, int D_, int q_)
    {
        this(ectrID,dimensions,objectIdentifiers,D_,q_,null);
    }

    public DistLeqModel(int ectrID, int[] dimensions, int[] objectIdentifiers, int D_, int q_,IntegerVariable var)
    {
        super(ectrID, dimensions, null);
        int[] oids = new int[2];
        D=D_;
        o1=objectIdentifiers[0];
        o2=objectIdentifiers[1];
        q=q_;
        oids[0]=o1;
        oids[1]=o2;
        setObjectIds(oids); //only the first object is pruned.
        modelDVar=var;
    }

    public IntegerVariable getDistanceVar() { return modelDVar; } 
    public boolean hasDistanceVar() { return modelDVar!=null; }
    public String toString() {
        StringBuilder r= new StringBuilder();
        if (modelDVar!=null){
            r.append("Leq(D=[").append(modelDVar.getLowB()).append(",").append(modelDVar.getUppB())
                    .append("],q=").append(q).append(",o1=").append(o1).append(",o2=").append(o2).append(")");
        }
        else{
            r.append("Leq(D=").append(D).append(",q=").append(q).append(",o1=").append(o1).append(",o2=").append(o2).append(")");
        }
        return r.toString();
    }


}