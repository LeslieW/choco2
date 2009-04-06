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

import choco.cp.solver.constraints.global.multicostregular.asap.hci.abstraction.ASAPDataHandler;

import javax.swing.*;
import java.util.Observer;
import java.util.Observable;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Mar 9, 2009
 * Time: 2:53:32 PM
 */
public class ASAPButtonControl implements Observer, ActionListener {


    JButton solve;
    ASAPDataHandler d;
    boolean next;

    public ASAPButtonControl(ASAPDataHandler d,JButton button)
    {
        this.solve =  button;
        this.solve.setEnabled(false);
        this.solve.addActionListener(this);
        this.d=d;
        this.next = false;
    }


    public void update(Observable observable, Object o) {
        Integer i = (Integer) o;

        if (i.equals(ASAPDataHandler.MODEL_FED))
        {
            this.solve.setEnabled(true);
            this.next = false;
        }
        else if (i.equals(ASAPDataHandler.SOLUTION_FOUND))
        {
            this.solve.setEnabled(true);
            this.next = true;
        }
        else if (i.equals(ASAPDataHandler.NO_SOLUTION))
        {
            this.solve.setEnabled(true);
        }
        else if (i.equals(ASAPDataHandler.SOLVING))
        {
            this.solve.setEnabled(false);
        }
        
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (!this.next)
            this.d.solve();
        else
        {
            this.d.next();
        }
        
    }
}