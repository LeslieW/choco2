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
package samples.multicostregular.carsequencing.heuristics;

import choco.kernel.solver.search.AbstractSearchHeuristic;
import choco.kernel.solver.search.integer.ValSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.Arrays;
import java.util.Comparator;

import samples.multicostregular.carsequencing.parser.CarSeqInstance;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Jan 28, 2009
 * Time: 6:48:21 PM
 */
public class LeastCarValHeur extends AbstractSearchHeuristic implements ValSelector {


    Integer[] order;

    class Compare implements Comparator<Integer>
    {

        Integer[] val;
        public Compare(Integer[] val)
        {
            this.val = val;
        }

        public int compare(Integer i, Integer i1) {
            return val[i].compareTo(val[i1]);
        }
    }

    public LeastCarValHeur(CarSeqInstance instance)
    {
        order = new Integer[instance.nbClasses];
        Integer[] nbC = new Integer[instance.nbClasses];

        for (int i = 0 ; i < instance.nbClasses ; i++)
        {
            order[i] = i;
            nbC[i] = instance.optionRequirement[i][1];
        }
        Arrays.sort(order,new Compare(nbC));
    }

    public int getBestVal(IntDomainVar x) {
        for (Integer anOrder : order) {
            if (x.canBeInstantiatedTo(anOrder)) {
                return anOrder;
            }
        }

        System.err.println("pas normal d'etre la most car val heur");
        return x.getInf();
    }
}