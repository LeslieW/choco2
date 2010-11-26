package choco.kernel.model.constraints.automaton.penalty;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Apr 30, 2010
 * Time: 1:57:07 PM
 */
public abstract class AbstractPenaltyFunction implements IPenaltyFunction
{
@Override
public abstract int penalty(int value);

@Override
public double minGHat(double lambda, IntDomainVar var)
{
        DisposableIntIterator valIter = var.getDomain().getIterator();
        double ghat = Double.POSITIVE_INFINITY;
        while (valIter.hasNext())
        {
                int val = valIter.next();
                ghat = Math.min(ghat, penalty(val)-lambda*val);
        }
        valIter.dispose();
        return ghat;
}

@Override
public double maxGHat(double lambda, IntDomainVar var)
{
        DisposableIntIterator valIter = var.getDomain().getIterator();
        double ghat = Double.NEGATIVE_INFINITY;
        while (valIter.hasNext())
        {
                int val = valIter.next();
                ghat = Math.max(ghat, penalty(val)-lambda*val);
        }
        valIter.dispose();
        return ghat;
}
}
