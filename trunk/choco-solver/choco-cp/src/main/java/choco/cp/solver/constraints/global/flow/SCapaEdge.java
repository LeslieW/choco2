package choco.cp.solver.constraints.global.flow;

import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.memory.structure.PartiallyStoredIntVector;
import choco.kernel.memory.structure.PartiallyStoredVector;
import choco.kernel.solver.branch.Extension;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.propagation.event.VarEvent;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Created by IntelliJ IDEA.
 * User: rochart
 * Date: Dec 2, 2003
 * Time: 3:12:07 PM
 * To change this template use Options | File Templates.
 */
public class SCapaEdge implements Var
{
    public final int dest;
    public final IntDomainVar capa;

    public SCapaEdge(IntDomainVar capa, int dest) {
        this.dest = dest;
        this.capa = capa;
    }

public String toString() {
        return ("capa "+ capa + "  -> node " + dest);
    }

@Override
public String getName()
{
        return capa.getName();
}

@Override
public int getNbConstraints()
{
        return capa.getNbConstraints();
}

@Override
public SConstraint getConstraint(int i)
{
        return capa.getConstraint(i);
}
@Override
public int getVarIndex(int constraintIndex)
{
        return capa.getVarIndex(constraintIndex);
}

@Override
public PartiallyStoredVector<? extends SConstraint> getConstraintVector()
{
        return capa.getConstraintVector();
}

@Override
public PartiallyStoredIntVector getIndexVector()
{
        return capa.getIndexVector();
}
@Override
public boolean isInstantiated()
{
        return capa.isInstantiated();
}

@Override
public int addConstraint(SConstraint c, int varIdx, boolean dynamicAddition)
{
        return capa.addConstraint(c,varIdx,dynamicAddition);
}

@Override
public VarEvent<? extends Var> getEvent()
{
        return capa.getEvent();
}

@Override
public DisposableIterator<SConstraint> getConstraintsIterator()
{
        return capa.getConstraintsIterator();
}

@Override
public Extension getExtension(int extensionNumber)
{
        return capa.getExtension(extensionNumber);
}

@Override
public String pretty()
{
        return this.toString();
}

@Override
public long getIndex()
{
        return capa.getIndex();
}
}
