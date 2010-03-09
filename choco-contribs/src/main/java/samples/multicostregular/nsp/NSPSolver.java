package samples.multicostregular.nsp;

import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.automata.fast_multicostregular.FastMultiCostRegular;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Dec 5, 2008
 * Time: 12:09:50 AM
 */
public class NSPSolver {

    public static void main(String[] args) {
        for (int i = 25 ; i <= 25 ; i+=25)
            for (int j = 501 ; j <= 501 ; j+=50)
            {
                NSPModel mod = new NSPModel("/Users/julien/These/NSP/NSPLib/N"+i+"/"+j+".nsp");
                CPSolver s = new CPSolver();
                s.read(mod);
              //  CPSolver.setVerbosity(CPSolver.SOLUTION);
               // s.setVarIntSelector(new StaticVarOrder(UtilAlgo.append(s.getVar(mod.globalCost),s.getVar(mod.flattenShifts()))));
                // s.setVarIntSelector(new StaticVarOrder(s.getVar(mod.flattenShifts(false))));
                NSPVarSelector varselec = new NSPVarSelector((NSPStruct) s.getCstr(mod.forHeuristic));
                NSPValSelector valselec = new NSPValSelector(varselec);

                s.attachGoal(new NSPBranching(varselec,valselec));


                FastMultiCostRegular[] cons = new FastMultiCostRegular[mod.constraints.length];
                for (int k  = 0 ; k < mod.constraints.length ; k++)
                    cons[k] = (FastMultiCostRegular) s.getCstr(mod.constraints[k]);

               // s.setValIntSelector(new RCCRValSelector(cons,false));

             //   s.setValIntIterator(new DecreasingDomain());
                
                System.out.println("N"+i+" : "+j);
                //if (s.minimize(s.getVar(mod.globalCost),false))
                if (s.solve())
                do {
                    System.out.println(mod.solution(s));
                    s.postCut(s.lt(s.getVar(mod.globalCost),s.getVar(mod.globalCost).getVal()));
                 } while(false && s.nextSolution());
                s.printRuntimeStatistics();
                System.out.println("");
                System.out.println("");
            }
    }
}
