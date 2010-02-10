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

import gnu.trove.TIntHashSet;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Mar 5, 2009
 * Time: 3:29:44 PM
 */
public class ASAPSubPattern implements ASAPPatternElement {

    ArrayList<ASAPPatternElement> pattern;


    public ASAPSubPattern()
    {
        this.pattern = new ArrayList<ASAPPatternElement>();
    }


    public boolean isInPattern(ASAPShift s) {
        return pattern.contains(s);
    }

    public void addPatternElement(ASAPPatternElement pe)
    {
        pattern.add(pe);
    }


    public String toRegExp() {
        StringBuffer b = new StringBuffer();

        for (ASAPPatternElement pe : pattern)
        {
            b.append(pe.toRegExp());
        }
        return b.toString();
    }

    @Override
    public int[] getElementValues() {
        TIntHashSet set = new TIntHashSet();
        for (ASAPPatternElement pat : this.pattern)
        {
            set.addAll(pat.getElementValues());
        }
        return set.toArray();
    }
}