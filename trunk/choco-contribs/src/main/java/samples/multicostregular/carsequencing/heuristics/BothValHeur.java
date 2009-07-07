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
 * Time: 7:18:16 PM
 */
public class BothValHeur extends AbstractSearchHeuristic implements ValSelector {


    Integer[] order;

    class Compare implements Comparator<Integer>
    {

        Integer[] nbc;
        Integer[] nbo;
        public Compare(Integer[] nbc, Integer[] nbo)
        {
            this.nbc = nbc;
            this.nbo = nbo;
        }

        public int compare(Integer i, Integer i1) {
            int ret = nbo[i1].compareTo(nbo[i]);
            if (ret == 0)
            {
                ret = nbc[i].compareTo(nbc[i1]);
            }
            return ret;
        }
    }

    public BothValHeur(CarSeqInstance instance)
    {
        order = new Integer[instance.nbClasses];
        Integer[] nbC = new Integer[instance.nbClasses];
        Integer[] nbO = new Integer[instance.nbClasses];

        for (int i = 0 ; i < instance.nbClasses ; i++)
        {
            order[i] = i;
            nbC[i] = instance.optionRequirement[i][1];
            nbO[i] = sum(instance.optionRequirement[i],instance.nbOptions);
        }
        Arrays.sort(order,new Compare(nbC,nbO));
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


    private int sum(int[] tab, int lgth)
    {
        int s = 0;
        for (int i  = 0 ; i < lgth ; i++) s+= tab[i+2];
        return s;

    }

}