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
package choco.cp.solver.constraints.global.multicostregular.asap.data.base;

import choco.cp.solver.constraints.global.multicostregular.asap.data.base.ASAPPatternElement;
import choco.cp.solver.constraints.global.multicostregular.asap.data.base.ASAPShift;
import choco.cp.solver.constraints.global.multicostregular.asap.data.ASAPItemHandler;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Dec 16, 2008
 * Time: 3:23:02 PM
 */
public class ASAPShiftGroup extends AbstractList<ASAPShift> implements ASAPPatternElement {

    String id;
    ArrayList<ASAPShift> shifts;



    public void init(String id, ASAPItemHandler handler)
    {
        this.id = id;
        this.shifts = new ArrayList<ASAPShift>();
        handler.putShiftGroup(id,this);
    }

    public ASAPShiftGroup(ASAPItemHandler handler, String id, ASAPShift... shift)
    {
        this.init(id,handler);
        this.shifts.addAll(Arrays.asList(shift));
    }
    public ASAPShiftGroup(ASAPItemHandler handler, String id, String... sids)
    {
        this.init(id, handler);
        for (String s : sids)
        {
            this.shifts.add(handler.getShift(s));
        }
    }

    public ASAPShiftGroup(ASAPItemHandler handler, String id)
    {
        this.init(id, handler);
    }

    public boolean add(ASAPShift s)
    {
        return this.shifts.add(s);
    }

    public String getId() {
        return id;
    }

    public ASAPShift get(int i) {
        return this.shifts.get(i);
    }

    public Iterator<ASAPShift> iterator() {
        return this.shifts.iterator();
    }

    public int size() {
        return this.shifts.size();
    }

    public boolean isInPattern(ASAPShift s) {
        return this.shifts.contains(s);
    }

    public String toRegExp() {
        StringBuffer b = new StringBuffer("(");
        for (ASAPPatternElement e : shifts)
        {
            b.append(e.toRegExp()).append("|");
        }
        b.deleteCharAt(b.length()-1).append(")");
        //System.out.println(b.toString());
        return b.toString();

    }
}