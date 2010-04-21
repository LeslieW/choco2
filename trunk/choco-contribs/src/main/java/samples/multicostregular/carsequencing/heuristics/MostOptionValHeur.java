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

import choco.kernel.solver.search.ValSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;
import samples.multicostregular.carsequencing.parser.CarSeqInstance;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Jan 28, 2009
 * Time: 7:06:58 PM
 */
public class MostOptionValHeur implements ValSelector<IntDomainVar> {


    Integer[] order;

    class Compare implements Comparator<Integer>
    {

        Integer[] val;
        public Compare(Integer[] val)
        {
            this.val = val;
        }

        public int compare(Integer i, Integer i1) {
            return val[i1].compareTo(val[i]);
        }
    }

    public MostOptionValHeur(CarSeqInstance instance)
    {
        order = new Integer[instance.nbClasses];
        Integer[] nbC = new Integer[instance.nbClasses];

        for (int i = 0 ; i < instance.nbClasses ; i++)
        {
            order[i] = i;

            nbC[i] = sum(instance.optionRequirement[i],instance.nbOptions);
        }
        Arrays.sort(order,new Compare(nbC));
    }

    private int sum(int[] tab, int lgth)
    {
        int s = 0;
        for (int i  = 0 ; i < lgth ; i++) s+= tab[i+2];
        return s;

    }

    public int getBestVal(IntDomainVar x) {
        for (Integer anOrder : order) {
            if (x.canBeInstantiatedTo(anOrder)) {
                return anOrder;
            }
        }
        assert false;
        return x.getInf();
    }
}