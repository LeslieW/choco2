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
package samples.multicostregular.carsequencing;

import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Jan 26, 2009
 * Time: 5:10:53 PM
 */
public class Main {


    public static void solve(String filename, boolean all)
    {

        CarSeqModel m = new CarSeqModel(filename,false);
        Solver s = new CPSolver();

        s.read(m);
        IntegerVariable[] seqMVars = m.seqVars;
        IntDomainVar[] seqVars = s.getVar(seqMVars);

       // s.setValIntSelector(new LeastCarValHeur(m.getInstance()));
       // s.setValIntSelector(new MostOptionValHeur(m.getInstance()));
        //s.setValIntSelector(new BothValHeur(m.getInstance()));

         s.setVarIntSelector(new StaticVarOrder(s, seqVars));
        //s.setVarIntSelector(new ManInTheMiddleVarHeur(seqVars));
        s.monitorFailLimit(true);
        	



        System.out.println("Trying "+m.getInstance().name+"...");

        if (s.solve())
        {
            do {

                for (int i = 0 ; i < seqVars.length ; i++)
                {
                    System.out.print(seqVars[i].getVal()+"\t");
                    for (int j = 0 ; j < m.getInstance().nbOptions ; j++)
                        System.out.print(m.getInstance().optionRequirement[seqVars[i].getVal()][j+2]+" ");
                    System.out.println("");


                }
                System.out.println("");
                System.out.println("");
            } while(all && s.nextSolution());


        }
        s.printRuntimeStatistics();
        System.out.println(s.getNbSolutions()+" SOLUTIONS" );

    }


    public static void main(String[] args) {
        String prefix= "carseq/pb";

        for (int i = 1 ;i < 80 ; i++)
        {
            solve(prefix+i+".txt",false);
        }

    }
   

}