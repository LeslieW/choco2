package choco.kernel.model.constraints.automaton.penalty;

import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Apr 27, 2010
 * Time: 11:30:01 AM
 */
public interface IPenaltyFunction
{

public int penalty(int value);

public double minGHat(double lambda, IntDomainVar var);
public double maxGHat(double lambda, IntDomainVar var);



}
