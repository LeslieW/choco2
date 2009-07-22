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
package assur.cp.heuristics;

import choco.kernel.solver.search.integer.ValSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.Solver;
import choco.kernel.model.variables.integer.IntegerVariable;
import assur.entities.Person;
import assur.entities.Preference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Jul 21, 2009
 * Time: 1:33:41 PM
 */
public class PrefValSelector implements ValSelector {

    HashMap<IntDomainVar,ArrayList<Person>> varPeopleMap;


    public PrefValSelector(HashMap<IntDomainVar,ArrayList<Person>> varPeopleMap)
    {
        this.varPeopleMap = varPeopleMap;


    }


    @Override
    public int getBestVal(IntDomainVar x) {
        ArrayList<Person> people = varPeopleMap.get(x);
        if (people != null)
        {
            for (Person p : people)
            {
                int out = p.getIndex();
                if (x.canBeInstantiatedTo(out)) return out;
            }
        }
        return x.getInf();

    }
}