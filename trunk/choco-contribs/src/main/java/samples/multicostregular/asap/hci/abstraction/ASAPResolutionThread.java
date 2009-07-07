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
package samples.multicostregular.asap.hci.abstraction;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Mar 9, 2009
 * Time: 1:29:25 PM
 */
public class ASAPResolutionThread extends Thread{

    ASAPDataHandler d;

    public ASAPResolutionThread(ASAPDataHandler d)
    {
        this.d=d;
    }

    public void run()
    {
        if (!d.isSolved())
        {
            if (this.d.solver.solve())
            {
                System.out.println("SOLVE FINI +");
                this.d.setSolved(true);
            }
            else
            {
                System.out.println("SOLVE FINI -");
                this.d.setSolved(false);
            }
        }
        else
        {
            if (this.d.solver.nextSolution())
            {
                this.d.setSolved(true);
            }
        }
    }
}