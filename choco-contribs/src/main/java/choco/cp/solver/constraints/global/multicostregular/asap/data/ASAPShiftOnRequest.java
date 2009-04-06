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
package choco.cp.solver.constraints.global.multicostregular.asap.data;

import choco.cp.solver.constraints.global.multicostregular.asap.data.base.ASAPShiftOn;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Dec 16, 2008
 * Time: 5:45:53 PM
 */
public class ASAPShiftOnRequest extends AbstractList<ASAPShiftOn> {

    ArrayList<ASAPShiftOn> requests;

    public ASAPShiftOnRequest() {
        this.requests = new ArrayList<ASAPShiftOn>();
    }

    public ASAPShiftOn get(int i)
    {
        return this.requests.get(i);

    }

    public boolean add(ASAPShiftOn req)
    {
        return this.requests.add(req);
    }

    public int size() {
        return this.requests.size();
    }

    public Iterator<ASAPShiftOn> iterator()
    {
        return this.requests.iterator();
    }
}