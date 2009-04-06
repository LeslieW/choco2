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

import choco.cp.solver.constraints.global.multicostregular.asap.data.ASAPItemHandler;


/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Dec 16, 2008
 * Time: 5:46:37 PM
 */
public class ASAPShiftOn {

    int weight;
    ASAPShift shift;
    ASAPEmployee employee;
    ASAPDate date;
    ASAPItemHandler handler;

    public ASAPShiftOn(ASAPItemHandler handler)
    {
        this(handler,0);
    }

    public ASAPShiftOn(ASAPItemHandler handler, int weight, ASAPShift shift, ASAPEmployee employee, ASAPDate date) {
        this(handler,0);
        this.weight = weight;
        this.shift = shift;
        this.employee = employee;
        this.date = date;
    }
    public ASAPShiftOn(ASAPItemHandler handler, int weight)
    {
        this.handler = handler;
        this.weight = weight;
        handler.requestOn.add(this);
    }


    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public ASAPShift getShift() {
        return shift;
    }

    public void setShift(String sid) {
        this.shift = handler.getShift(sid);
    }

    public ASAPEmployee getEmployee() {
        return employee;
    }

    public void setEmployee(String eid) {
        this.employee = handler.getEmployee(eid);
    }

    public ASAPDate getDate() {
        return date;
    }

    public void setDate(ASAPDate date) {
        this.date = date;
    }
}