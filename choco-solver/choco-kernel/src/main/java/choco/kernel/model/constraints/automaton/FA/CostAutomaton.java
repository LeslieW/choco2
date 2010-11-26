package choco.kernel.model.constraints.automaton.FA;

import choco.kernel.model.constraints.automaton.FA.utils.Counter;
import choco.kernel.model.constraints.automaton.FA.utils.CounterState;
import choco.kernel.model.constraints.automaton.FA.utils.ICounter;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Nov 23, 2010
 * Time: 11:07:36 AM
 */
public class CostAutomaton extends FiniteAutomaton implements ICostAutomaton
{

protected ArrayList<ICounter> counters;

public CostAutomaton()
{
        super();
        this.counters = new ArrayList<ICounter>();
}


public CostAutomaton(IAutomaton auto)
{
        super((FiniteAutomaton) auto);
        this.counters = new ArrayList<ICounter>();
}

public CostAutomaton(IAutomaton auto, List<ICounter> counters)
{
        this(auto);
        this.counters.addAll(counters);
}

public CostAutomaton(IAutomaton auto, ICounter counter)
{
        this(auto);
        this.counters.add(counter);
}


public double getCost(int layer,int value)
{
        ICounter zero = counters.get(0);
        return zero.cost(layer,value);
}

public double getCostByState(int layer, int value, int state)
{
        ICounter zero = counters.get(0);
        return zero.cost(layer,value,state);
}

public double getCostByResource(int layer, int value, int counter)
{
        ICounter aCounter = counters.get(counter);
        return aCounter.cost(layer,value);
}

public int getNbResources()
{
        return counters.size();
}

public double getCostByResourceAndState(int layer, int value, int counter, int state)
{
        ICounter aCounter = counters.get(counter);
        return aCounter.cost(layer,value,state);
}

@Override
public List<ICounter> getCounters()
{
        return counters;
}

public static ICostAutomaton makeSingleResource(IAutomaton pi, int[][][] costs, int inf, int sup)
{
        ICounter c = new CounterState(costs,inf,sup);
        ArrayList<ICounter> tmp = new ArrayList<ICounter>();
        tmp.add(c);
        return new CostAutomaton(pi,tmp);
}

public static ICostAutomaton makeSingleResource(IAutomaton pi, int[][] costs, int inf, int sup)
{
        ICounter c = new Counter(costs,inf,sup);
        ArrayList<ICounter> tmp = new ArrayList<ICounter>();
        tmp.add(c);
        return new CostAutomaton(pi,tmp);
}

public static ICostAutomaton makeMultiResources(IAutomaton pi, int[][][] layer_value_resource, int[] infs, int[] sups)
{
        int[][][] ordered = new int[infs.length][layer_value_resource.length][];
        ArrayList<ICounter> tmp = new ArrayList<ICounter>();
        for (int k = 0 ; k < infs.length ; k++)
        {
                for (int i = 0 ; i < layer_value_resource.length ; i++)
                {
                        ordered[k][i] = new int[layer_value_resource[i].length];
                        for (int j = 0 ; j < layer_value_resource[i].length ; j++)
                        {
                                ordered[k][i][j] = layer_value_resource[i][j][k];
                        }
                }
                tmp.add(new Counter(ordered[k],infs[k],sups[k]));
        }
        return new CostAutomaton(pi,tmp);
}

public static ICostAutomaton makeMultiResources(IAutomaton pi, int[][][][] layer_value_resource_state, int[] infs, int[] sups)
{
        int[][][][] ordered = new int[infs.length][layer_value_resource_state.length][][];
        ArrayList<ICounter> tmp = new ArrayList<ICounter>();
        for (int k = 0 ; k < infs.length ; k++)
        {
                boolean stateDependant = true;
                for (int i = 0 ; i < layer_value_resource_state.length ; i++)
                {
                        ordered[k][i] = new int[layer_value_resource_state[i].length][];
                        for (int j = 0 ; j < layer_value_resource_state[i].length ; j++)
                        {
                                ordered[k][i][j] = new int[layer_value_resource_state[i][j][k].length];
                                for (int q = 0 ; q < layer_value_resource_state[i][j][k].length ; q++)
                                {
                                        ordered[k][i][j][q] = layer_value_resource_state[i][j][k][q];
                                }
                                if (ordered[k][i][j].length == 1)   stateDependant = false;
                        }
                }

                if (stateDependant)
                        tmp.add(new CounterState(ordered[k],infs[k],sups[k]));
                else
                        tmp.add(new Counter(ordered[k],infs[k],sups[k]));                
        }
        return new CostAutomaton(pi,tmp);
}

public static ICostAutomaton makeMultiResources(IAutomaton auto, int[][][][] c, IntegerVariable[] z)
{
        int[][] bounds = getBounds(z);
        return makeMultiResources(auto,c,bounds[0],bounds[1]);
}

public static ICostAutomaton makeMultiResources(IAutomaton auto, int[][][][] c, IntDomainVar[] z)
{
        int[][] bounds = getBounds(z);
        return makeMultiResources(auto,c,bounds[0],bounds[1]);
}



public static ICostAutomaton makeMultiResources(IAutomaton auto, int[][][] c, IntegerVariable[] z)
{
        int[][] bounds = getBounds(z);
        return makeMultiResources(auto,c,bounds[0],bounds[1]);
}

public static ICostAutomaton makeMultiResources(IAutomaton auto, int[][][] c, IntDomainVar[] z)
{
        int[][] bounds = getBounds(z);
        return makeMultiResources(auto,c,bounds[0],bounds[1]);
}


private static int[][] getBounds(IntDomainVar[] cr)
{
        int[][] bounds = new  int[2][cr.length];
        for (int i = 0 ; i < cr.length ; i++)
        {
                bounds[0][i] = cr[i].getInf();
                bounds[1][i] = cr[i].getSup();
        }
        return bounds;
}

private static int[][] getBounds(IntegerVariable[] cr)
{
        int[][] bounds = new  int[2][cr.length];
        for (int i = 0 ; i < cr.length ; i++)
        {
                bounds[0][i] = cr[i].getLowB();
                bounds[1][i] = cr[i].getUppB();
        }
        return bounds;
}

public void addCounter(ICounter c)
{
        this.counters.add(c);
}


}
