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
package choco.cp.solver.constraints.global.multicostregular.carsequencing;

import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Jan 26, 2009
 * Time: 5:10:53 PM
 */
public class Main {

    protected final static Logger LOGGER = ChocoLogging.getSolverLogger();

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

         s.setVarIntSelector(new StaticVarOrder(seqVars));
        //s.setVarIntSelector(new ManInTheMiddleVarHeur(seqVars));
        s.monitorFailLimit(true);
        s.monitorCpuTimeLimit(true);




        LOGGER.info("Trying "+m.getInstance().name+"...");

        if (s.solve())
        {
            do {

                for (int i = 0 ; i < seqVars.length ; i++)
                {
                    StringBuffer st = new StringBuffer();
                    st.append(seqVars[i].getVal()).append("\t");
                    for (int j = 0 ; j < m.getInstance().nbOptions ; j++)
                        st.append(m.getInstance().optionRequirement[seqVars[i].getVal()][j + 2]).append(" ");
                    LOGGER.info(st.toString());


                }
            } while(all && s.nextSolution());


        }
        s.printRuntimeSatistics();
        LOGGER.info(s.getNbSolutions()+" SOLUTIONS" );

    }


    public static void main(String[] args) {
        String prefix= "carseq/pb";

        for (int i = 1 ;i < 80 ; i++)
        {
            solve(prefix+i+".txt",false);
        }

    }
   

}