package samples.tutorials.lns.lns;

import choco.kernel.solver.branch.AbstractIntBranchingStrategy;

/** @author Sophie Demassey */
public class Neighborhood implements Comparable {

//private Solution solution;
private NeighborhoodOperator operator;
private AbstractIntBranchingStrategy strategy;
private int impact;

public Neighborhood(NeighborhoodOperator operator, AbstractIntBranchingStrategy strategy, int impact)
{
//	this.solution = solution;
	this.operator = operator;
	this.strategy = strategy;
	this.impact = impact;
}

public Neighborhood(NeighborhoodOperator operator)
{
	this(operator, null, 0);
}

/*public Solution getSolution()
{
	return solution;
}*/

public NeighborhoodOperator getOperator()
{
	return operator;
}

public AbstractIntBranchingStrategy getStrategy()
{
	return strategy;
}

/*
public void setSolution(Solution solution)
{
	this.solution = solution;
}
*/

@Override
public int compareTo(Object o)
{
	return (this.impact - ((Neighborhood) o).impact);
}

/*
public Neighborhood clone() 
{
	return new Neighborhood(solution, operator, strategy, impact);
}
*/

public int decreaseImpact()
{
	return --impact;
}

public int increaseImpact()
{
	return ++impact;
}

}
