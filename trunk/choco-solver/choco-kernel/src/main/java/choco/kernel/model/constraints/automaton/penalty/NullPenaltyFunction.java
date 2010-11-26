package choco.kernel.model.constraints.automaton.penalty;

import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: May 3, 2010
 * Time: 6:50:02 PM
 */
public class NullPenaltyFunction extends AbstractPenaltyFunction
{
@Override
public int penalty(int value)
{
        return 0;
}

@Override
public double minGHat(double lambda, IntDomainVar var)
{
        double ghat = -lambda *  ((lambda > 0) ? var.getSup() : var.getInf());
        return ghat;
}

@Override
public double maxGHat(double lambda, IntDomainVar var)
{
        double ghat = -lambda *  ((lambda < 0) ? var.getSup() : var.getInf());
        return ghat;
}

}
