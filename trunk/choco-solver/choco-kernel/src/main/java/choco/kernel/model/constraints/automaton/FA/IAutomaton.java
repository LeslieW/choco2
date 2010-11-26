package choco.kernel.model.constraints.automaton.FA;

import dk.brics.automaton.Automaton;
import gnu.trove.TIntArrayList;
import gnu.trove.TIntHashSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Nov 19, 2010
 * Time: 2:06:37 PM
 */
public interface IAutomaton
{




IAutomaton clone() throws CloneNotSupportedException;

int getInitialState();

int delta(int k, int j) throws NonDeterministicOperationException;

void delta(int k, int j, TIntHashSet nexts);

boolean isFinal(int k);

int getNbStates();

boolean run(int[] str);


public static class StateNotInAutomatonException extends Exception
{
        public StateNotInAutomatonException(int state)
        {
                super("State "+state+ " is not in the automaton, please add it using addState");
        }
}

public static class NonDeterministicOperationException extends Exception
{
        public NonDeterministicOperationException()
        {
                super("This operation can oly be called on a determinitic automaton, please use determinize()");
        }
}

public static class Triple
{
        int a;
        int b;
        int c;

        public Triple(int a, int b, int c)
        {
                this.a=a;
                this.b=b;
                this.c=c;
        }
}
}
