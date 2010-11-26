package choco.kernel.model.constraints.automaton.FA.utils;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Nov 23, 2010
 * Time: 11:09:07 AM
 */
public interface ICounter
{

Bounds bounds();

double cost(int layer, int value);

double cost(int layer, int value, int state);



}
