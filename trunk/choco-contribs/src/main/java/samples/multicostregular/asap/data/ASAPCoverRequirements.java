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
package samples.multicostregular.asap.data;


import samples.multicostregular.asap.data.base.ASAPCover;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Dec 16, 2008
 * Time: 4:43:45 PM
 */
public class ASAPCoverRequirements extends AbstractList<ASAPCover> {

    ArrayList<ASAPCover> dayOfWeekCovers;


    public ASAPCoverRequirements()
    {
        this.dayOfWeekCovers = new ArrayList<ASAPCover>();
    }

    public ASAPCover get(int i) {
        return this.dayOfWeekCovers.get(i);
    }


    public void addDayOfWeekCover(ASAPCover cover)
    {
        this.dayOfWeekCovers.add(cover);
    }

    public Iterator<ASAPCover> iterator()
    {
        return this.dayOfWeekCovers.iterator();
    }

    public int size() {
        return this.dayOfWeekCovers.size();
    }

}