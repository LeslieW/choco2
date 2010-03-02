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
package samples.lattice;

import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.model.constraints.automaton.FA.FiniteAutomaton;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Dec 3, 2009
 * Time: 12:41:43 PM
 */
public class Test {

    int n;

    public Test(int seqSize)
    {
        this.n = seqSize;
    }


    public dk.brics.automaton.Automaton makeLengthAccept(int[] alphabet, int size)
    {
        dk.brics.automaton.Automaton auto = new dk.brics.automaton.Automaton();
        State start = new State();
        State tmp = start;
        State last =start;

        for (int i = 0 ; i < size ; i++)
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


    public dk.brics.automaton.Automaton makeStartWithWork()
    {
        return new RegExp(StringUtils.toCharExp("(1|2)(0|1|2)*")).toAutomaton();
    }

    public dk.brics.automaton.Automaton makeNoMoreTwoConsRest()
    {
        return new RegExp(StringUtils.toCharExp("(0|1|2)*000(0|1|2)*")).toAutomaton().complement();
    }

    public dk.brics.automaton.Automaton testInter()
    {
        dk.brics.automaton.Automaton auto = makeLengthAccept(new int[]{0,1,2},n);
        auto = auto.intersection(makeStartWithWork());
        auto = auto.intersection(makeNoMoreTwoConsRest());
        auto.minimize();
        return auto;
    }

    public dk.brics.automaton.Automaton unionWithRule1()
    {
        dk.brics.automaton.Automaton auto = testInter();
        auto = auto.union((makeLengthAccept(new int[]{0,1,2},n).intersection(makeStartWithWork())));
   //     auto.minimize();
        auto.determinize();
        return auto;
    }


    public void printAuto(dk.brics.automaton.Automaton auto, String name)
    {
        name+=".dot";
        try {
            FileWriter fw = new FileWriter(name);
            fw.write(auto.toDot());
            fw.close();
        } catch (IOException e) {
            System.err.println("Unable to write automaton");
        }
    }


    public static void main(String[] args) {
        Test t = new Test(8);
        dk.brics.automaton.Automaton auto = t.testInter();
        t.printAuto(auto,"autoTest");
        t.printAuto(t.unionWithRule1(),"unionTest");

    }


}