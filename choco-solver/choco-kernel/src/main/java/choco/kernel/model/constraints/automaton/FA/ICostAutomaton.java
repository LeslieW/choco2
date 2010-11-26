package choco.kernel.model.constraints.automaton.FA;

import choco.kernel.model.constraints.automaton.FA.utils.ICounter;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Nov 19, 2010
 * Time: 3:25:16 PM
 */
public interface ICostAutomaton extends IAutomaton
{

double getCost(int i, int j);

double getCostByState(int layer, int counter, int state);

double getCostByResource(int layer, int value, int counter);

int getNbResources();

double getCostByResourceAndState(int layer, int value, int counter, int state);


List<ICounter> getCounters();

}
