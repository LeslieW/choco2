/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  �(..)  |                           *
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
package choco.cp.solver.constraints.global.multicostregular.asap.hci.control;

import choco.cp.solver.constraints.global.multicostregular.asap.hci.presentation.ASAPResultPanel;
import choco.cp.solver.constraints.global.multicostregular.asap.hci.abstraction.ASAPDataHandler;

import java.util.Observer;
import java.util.Observable;
import java.util.HashSet;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import gnu.trove.TIntHashSet;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Mar 9, 2009
 * Time: 3:25:37 PM
 */
public class ASAPResultControl implements Observer {

    ASAPResultPanel pr;


    public ASAPResultControl(ASAPResultPanel pr)
    {
        this.pr = pr;
    }


    public void update(Observable observable, Object o) {
        //ASAPDataHandler d = (ASAPDataHandler) observable;
        Integer i = (Integer)o;

        if (i.equals(ASAPDataHandler.MODEL_FED))
        {
            this.pr.removeAll();
            this.pr.setText("PRESS THE SOLVE BUTTON TO START SOLVING THE FED MODEL");
        }
        else if (i.equals(ASAPDataHandler.NO_SOLUTION))
        {
            this.pr.setText("ARGH NO SOLUTION FOUND, CONSTRAINT PROGRAMMING SUCKS");
        }
        else if (i.equals(ASAPDataHandler.SOLVING))
        {
            this.pr.setText("SOLVING IN PROGRESS... PLEASE BE (VERY) PATIENT");
        }
        else if (i.equals(ASAPDataHandler.SOLUTION_FOUND))
        {
            this.pr.setSolved();

        }
        //this.pr.repaint();

    }


    public static void main(String[] args) {

        Automaton a;
        RegExp r;
        TIntHashSet symb = new TIntHashSet();
        symb.add(0);
        symb.add(1);
        symb.add(2);
        r = new RegExp("(0|1|2)*(0|1)(0|1)(0|1)(0|1|2)*");
        a = r.toAutomaton();
        a = a.complement();
        a.minimize();
        choco.kernel.model.constraints.automaton.FA.Automaton b;
        b = new choco.kernel.model.constraints.automaton.FA.Automaton();
        b.fill(a,symb);


                b.toDotty("/Users/julien/bui.dot");



    }
}