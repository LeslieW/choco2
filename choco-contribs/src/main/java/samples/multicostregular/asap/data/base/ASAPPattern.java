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
package samples.multicostregular.asap.data.base;

import java.util.ArrayList;
import java.util.AbstractList;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Dec 16, 2008
 * Time: 3:59:43 PM
 */
public class ASAPPattern extends AbstractList<ASAPPatternElement> {

    
    int weight;
    boolean weekPattern;
    boolean bad;
    String startDay;
    public ArrayList<ASAPPatternElement> pattern;
    private boolean complete;

    public ASAPPattern(int weight,boolean bad)
    {
        this.weight = weight;
        this.pattern = new ArrayList<ASAPPatternElement>();
        this.weekPattern = true;
        this.bad = bad;
        this.complete =false;
    }

    public boolean isBad() { return bad;}
    public boolean isWeekPattern() { return weekPattern;}
    public void setWeekPattern(boolean bool) { this.weekPattern = bool;}

    public boolean add(ASAPPatternElement s)
    {
        return pattern.add(s);
    }

    @Override
    public ASAPPatternElement get(int index) {
        return pattern.get(index);
    }

    public int size()
    {
        return pattern.size();
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }
    public String toRegExp()
    {
        StringBuffer b = new StringBuffer();

        for (ASAPPatternElement pe : pattern)
        {
            b.append(pe.toRegExp());
        }



        return b.toString();

    }

    public void setComplete(boolean b) {
        this.complete = b;
    }
    public boolean isComplete()
    {
        return this.complete;
    }
}