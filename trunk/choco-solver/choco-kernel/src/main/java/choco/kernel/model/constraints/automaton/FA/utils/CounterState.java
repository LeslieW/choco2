package choco.kernel.model.constraints.automaton.FA.utils;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Nov 24, 2010
 * Time: 10:37:53 AM
 */
public class CounterState implements ICounter
{
int[][][] costs;
Bounds bounds;

public CounterState(int[][][] layer_value_state, int min, int max)
{
        this.costs  = layer_value_state;
        this.bounds = Bounds.makeBounds(min,min,null,max,max,null);
}



@Override
public Bounds bounds()
{
        return bounds;
}

@Override
public double cost(int layer, int value)
{
        return cost(layer,value,0);
}

@Override
public double cost(int layer, int value, int state)
{
        return this.costs[layer][value][state];
}

}
