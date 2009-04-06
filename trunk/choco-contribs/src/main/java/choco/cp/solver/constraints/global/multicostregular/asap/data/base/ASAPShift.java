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
import choco.cp.solver.constraints.global.multicostregular.asap.data.base.ASAPPattern;
import choco.cp.solver.constraints.global.multicostregular.asap.data.ASAPItemHandler;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Dec 16, 2008
 * Time: 3:14:30 PM
 */
public class ASAPShift implements ASAPPatternElement {

    String id;
    String label;
    String colour;
    String description;
    int start;
    int end;
    int duration;
    boolean not;

    ASAPItemHandler handler;



    public ASAPShift(ASAPItemHandler handler, String ID, String label, String colour, String description, int start, int end, int duration)
    {
        this.id             =   ID;
        this.label          =   label;
        this.colour         =   colour;
        this.description    =   description;
        this.start          =   start;
        this.end            =   end;
        this.duration       =   duration;
        this.handler        =   handler;

        handler.putShift(this.id,this);
        this.not = true;
        addToMap(handler);
    }

    public ASAPShift(ASAPItemHandler handler, String id)
    {
        this.not = true;
        this.id = id;
        this.handler = handler;

        handler.putShift(this.id,this);
        addToMap(handler);
    }

    private void addToMap(ASAPItemHandler handler){
        int a = handler.map.size();
        handler.map.put(this.getID(),a);
        handler.inverseMap.put(a,this.getID());

    }

    public String getID() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getColour() {
        return colour;
    }

    public String getDescription() {
        return description;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getDuration() {
        return duration;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStart(int start) {
        this.start = start;
    }
    public void setStart(String time)
    {
        this.start = Integer.parseInt(time.split(":")[0]);
    }

    public void setEnd(int end) {
        this.end = end;
    }
    public void setEnd(String end)
    {
       this.end = Integer.parseInt(end.split(":")[0]);
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setDuration(String dur)
    {
        this.duration = (int) Float.parseFloat(dur);
    }

    public boolean equals(Object o)
    {
        if (o instanceof ASAPShift)
        {
            ASAPShift os = (ASAPShift) o;
            return os.id.equals(id) && os.label.equals(label) && os.duration == duration && os.start == start;
        }
        return false;       
    }

    public boolean isInPattern(ASAPShift s) {
        return s.equals(this);
    }

    

    public String toRegExp() {
        return ""+ handler.map.get(this.id);
    }
}