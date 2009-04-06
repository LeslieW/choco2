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
package choco.cp.solver.constraints.global.multicostregular;

import choco.kernel.model.constraints.automaton.FA.Automaton;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.Model;
import choco.kernel.solver.Solver;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.propagation.EventQueue;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.common.util.IntIterator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Mar 30, 2009
 * Time: 11:55:06 AM
 */
public class FilteringTest {

    private static Automaton generateRandomAutomaton(int nbVar, int[] val, Random r)
    {
        StringBuffer regexp = new StringBuffer();
        for (int i = 0 ; i  < nbVar ; i++)
        {
            regexp.append(getRandomSubset(val,r));
        }
        System.out.println(regexp);
        return new Automaton(regexp.toString());

    }

    private static int[][][] generateRandomCosts(int nbVar, int maxVal, int nbR, int maxCost,Random r)
    {
        int[][][] ret = new int[nbVar][maxVal+1][nbR];
        for (int i = 0 ; i < ret.length ;i++)
            for (int j = 0 ; j < ret[i].length ;j++)
                for (int k = 0 ; k < ret[i][j].length ; k++)
                {   if (r.nextInt(3) > 1)
                        ret[i][j][k] = r.nextInt(maxCost/2)+1+maxCost;
                    else
                        ret[i][j][k] = r.nextInt(maxCost/2)+1;
                }

        return ret;
    }



    private static String getRandomSubset(int[] val, Random r)
    {

        ArrayList<Integer> arr = new ArrayList<Integer>();
        for (int i : val) arr.add(i);

        Collections.shuffle(arr,r);

        int k;
        k = r.nextInt(arr.size());

        StringBuffer b = new StringBuffer("(");
        for (int i : arr.subList(0,k))
        {
            if (i > 9)
                b.append('<').append(i).append('>');
            else
                b.append(i);
            b.append('|');
        }
        b.deleteCharAt(b.length()-1).append(')');

        return b.toString();

    }

    public static void main(String[] args) {


        int n = 2;
        int d = 655;
        int r = 10;
        int max = n*100;
        int iter = 10;

        double all = 0.0;

        for (int seed = 0 ; seed < iter; seed++)
        {
            Random rand = new Random(seed);
            Model m = new CPModel();
            Solver s = new CPSolver();

            IntegerVariable[] vars = makeIntVarArray("v",n,0,d,"cp:enum");
            m.addVariables(vars);
            int[] val = new int[d+1];
            for (int i  = 0 ; i <= d ; i++) val[i] =i;
            Automaton a = generateRandomAutomaton(n,val,rand);
            int[][][] csts = generateRandomCosts(n,d,r,max,rand);


            IntegerVariable[] z = new IntegerVariable[r];
            for (int i  = 0 ; i < r ;i++)
                z[i] = makeIntVar("z_"+i,0,rand.nextInt(max*n),"cp:bound");
            m.addVariables(z);

            Constraint c = multiCostRegular(vars,z,a,csts);

            m.addConstraint(c);


            // Solver PART

            s.read(m);

            int bui = 0;
            for (IntDomainVar v: s.getVar(vars)) bui+= v.getDomainSize();
            System.out.println("AU DEPART : "+bui+" val");

            try {
                s.propagate();

                double nbVal = 0;
                double nbFalse = 0;
                double nbTrue = 0;
                for (IntDomainVar v : s.getVar(vars))
                {
                    IntIterator it ;
                    for (it = v.getDomain().getIterator(); it.hasNext() ;)
                    {
                        int j = it.next();
                        nbVal++;
                        int env = s.getEnvironment().getWorldIndex();
                        s.worldPush();
                        try {
                            v.setVal(j);
                        } catch (ContradictionException e) {
                            System.err.println("Should not be here !");
                        }
                       
                        if (s.solve()) nbTrue++;
                        else nbFalse++;
                      //  System.err.println("LEAVING SOLVE");
                        s.worldPopUntil(env);



                    }

                }

                System.out.println("NB VAL   : "+nbVal);
                System.out.println("NB TRUE  : "+nbTrue);
                System.out.println("NB FALSE : "+nbFalse);
                double perc = nbTrue/nbVal*100;
                all+=perc;

                System.out.println("");
                System.out.println("POURCENTAGE DE OK : "+perc+"%");

            }
            catch (ContradictionException e) {
                System.out.println("PAS DE SOLUTION ET TOUT RETIRE");
                all+=100.0;
            }

        }

        System.out.println("");
        System.out.println("ALL IN ALL : "+(all/iter)+'%');

    }


}