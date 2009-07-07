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
package samples.multicostregular.asap.hci.control;


import samples.multicostregular.asap.hci.presentation.ASAPSummaryPanel;
import samples.multicostregular.asap.hci.abstraction.ASAPDataHandler;
import samples.multicostregular.asap.data.ASAPItemHandler;

import java.util.Observer;
import java.util.Observable;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Mar 9, 2009
 * Time: 2:40:14 PM
 */
public class ASAPSummaryControl implements Observer {

    ASAPSummaryPanel ps;

    public ASAPSummaryControl(ASAPSummaryPanel ps)
    {
        this.ps = ps;
    }

    public void update(Observable observable, Object o)
    {
        ASAPDataHandler d = (ASAPDataHandler) observable;
        ASAPItemHandler data = d.getCPModel().getHandler();
        Integer i = (Integer) o;

        if (i.equals(ASAPDataHandler.MODEL_FED))
        {
            this.ps.setSolving(d,false);
            this.ps.setSolved(false);
            ps.setNbDays(d.getCPModel().getNbDays());
            ps.setNbEmployee(d.getCPModel().getNbEmployees());
            ps.setStart(data.getStart());
            ps.setEnd(data.getEnd());
            ps.setFile(data.getProblemName());
            this.ps.repaint();
        }
         else if (i.equals(ASAPDataHandler.SOLVING))
        {
            this.ps.setSolving(d,true);
        }
        else if (i.equals(ASAPDataHandler.SOLUTION_FOUND) || i.equals(ASAPDataHandler.NO_SOLUTION))
        {
            this.ps.setSolving(d,false);
            this.ps.setSolved(true);
        }


    }
}