package choco.cp.solver.constraints.global.geost.externalConstraints;

import choco.cp.solver.constraints.global.geost.frames.Frame;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Created by IntelliJ IDEA.
 * User: szampelli
 * Date: 3 févr. 2009
 * Time: 16:29:15
 * To change this template use File | Settings | File Templates.
 */
public final class DistLeq extends ExternalConstraint implements Externalizable {

    public int id;
    public int D;
    public int o1;
    public int o2;
    public int q;
    public IntDomainVar DVar = null;

	public DistLeq(){}

	public DistLeq(int ectrID, int[] dimensions, int[] objectIdentifiers, int D_, int q_)
	{
        this(ectrID,dimensions,objectIdentifiers,D_,q_,null);
	}

    public DistLeq(int ectrID, int[] dimensions, int[] objectIdentifiers, int D_, int q_, IntDomainVar var)
    {
        super(ectrID, dimensions, null);
        int[] oids = new int[1];
        id=super.maxId; super.maxId++;


        D=D_;
        o1=objectIdentifiers[0];
        o2=objectIdentifiers[1];
        q=q_;
        oids[0]=o1;
        setObjectIds(oids); //only the first object is pruned.
        DVar=var;
    }


    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(D);
        out.writeObject(o1);
        out.writeObject(o2);
        out.writeObject(q);
        out.writeObject(super.ectrID);

        out.writeObject(super.dim.length);
        for (int i=0; i<super.dim.length; i++){
            out.writeObject(super.dim[i]);
        }
        out.writeObject(super.objectIds.length);
        for (int i=0; i<super.objectIds.length; i++){
            out.writeObject(super.objectIds[i]);
        }
        out.writeObject(super.frame);

    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {     
        D=(Integer) in.readObject();
        o1=(Integer) in.readObject();
        o2=(Integer) in.readObject();
        q=(Integer) in.readObject();

        super.ectrID=(Integer) in.readObject();
        int n=(Integer) in.readObject();
        super.dim=new int[n];
        for (int i=0; i<n; i++){
            super.dim[i]=(Integer) in.readObject();
        }
        n=(Integer) in.readObject();
        super.objectIds=new int[n];
        for (int i=0; i<n; i++){
            super.objectIds[i]=(Integer) in.readObject();
        }
        super.frame=(Frame) in.readObject();
    }

    public String toString() {
           StringBuilder r=new StringBuilder();

            if (DVar!=null){
                r.append("Leq(D=[").append(DVar.getInf()).append(",").append(DVar.getSup())
                        .append("],q=").append(q).append(",o1=").append(o1).append(",o2=").append(o2).append(")");
            }
            else {
                r.append("Leq(D=").append(D).append(",q=").append(q).append(",o1=")
                        .append(o1).append(",o2=").append(o2).append(")");
            }

            return r.toString();
    }

    public boolean hasDistanceVar() { return (DVar!=null); }

    public IntDomainVar getDistanceVar() { return DVar; }

    public int getCstrId() {
        return id;
    }

    
}
