/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.kernel.solver.constraints.global.automata.common;

import choco.kernel.model.constraints.automaton.FA.FiniteAutomaton;
import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Dec 3, 2009
 * Time: 4:26:07 PM
 */
public class AutomatonOperation {


    public static Automaton makeAcceptAllOfLength(int length, int[] alphabet)
    {
        Automaton auto = new Automaton();
        State start = new State();
        State tmp = start;
        State last =start;

        for (int i = 0 ; i < length ; i++)
        {
            last = new State();
            for (int k : alphabet)
            {
                tmp.addTransition(new Transition((char) FiniteAutomaton.getCharFromInt(k),last));
            }
            tmp = last;

        }
        last.setAccept(true);
        auto.setInitialState(start);
        return auto;
    }



}