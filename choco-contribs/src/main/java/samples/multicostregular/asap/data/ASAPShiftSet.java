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


import samples.multicostregular.asap.data.base.ASAPPatternElement;
import samples.multicostregular.asap.data.base.ASAPShift;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;

import gnu.trove.TIntHashSet;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Dec 17, 2008
 * Time: 3:28:18 PM
 */
public class ASAPShiftSet extends AbstractSet<ASAPPatternElement> implements ASAPPatternElement {

    public HashSet<ASAPPatternElement> elem;


    public ASAPShiftSet()
    {
        this.elem = new HashSet<ASAPPatternElement>();
    }

    public boolean add(ASAPPatternElement pe)
    {
        return elem.add(pe);
    }

    public Iterator<ASAPPatternElement> iterator() {
        return elem.iterator();
    }

    public int size() {
        return elem.size();
    }

    public boolean isInPattern(ASAPShift s) {
        return elem.contains(s);
    }

    public String toRegExp() {
        StringBuffer b = new StringBuffer("(");
        for (ASAPPatternElement e : this)
        {
            b.append(e.toRegExp()).append("|");
        }
        b.deleteCharAt(b.length()-1).append(")");
        return b.toString();
    }

    @Override
    public int[] getElementValues() {
        TIntHashSet set = new TIntHashSet();
        for (ASAPPatternElement pat : this)
        {
            set.addAll(pat.getElementValues());
        }
        return set.toArray();

    }
}