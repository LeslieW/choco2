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
package samples.comparaison;

import static choco.Choco.*;
import choco.cp.CPOptions;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.model.constraints.automaton.FA.FiniteAutomaton;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.variables.integer.IntDomainVar;
import dk.brics.automaton.RegExp;
import gnu.trove.TIntHashSet;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Nov 23, 2009
 * Time: 5:54:38 PM
 */
public class RegCRMCRComp
{

    public final static int REG = 0;
    public final static int COR = 1;
    public final static int MCR = 2;


    FiniteAutomaton automaton;
    IntegerVariable[] vars;
    IntegerVariable objectif;
    int[][] costs;
    Random rand;

    public RegCRMCRComp(int size, long seed)
    {

        if (seed >=0)
            this.rand = new Random(seed);
        else
            this.rand = new Random();
        this.automaton = makeAutomaton();
        this.costs = makeMatrix(size);

        this.vars = makeIntVarArray("x",size,0,2);



    }

    int[][] makeMatrix(int size)
    {
        int[][] ret = new int[size][3];
        for (int i  = 0 ; i < ret.length ; i++)
            for (int j  = 0 ; j < ret[i].length ; j++)
                ret[i][j] = rand.nextInt(1000);

        return ret;

    }


    FiniteAutomaton makeAutomaton()
    {
        dk.brics.automaton.Automaton auto;
        // On commence toujours par du travail :)
        auto = new RegExp(StringUtils.toCharExp("0(0|1|2)*")).toAutomaton().complement();

        // un repos après deux jours de boulot;
        auto = auto.intersection(new RegExp(StringUtils.toCharExp("(0|1|2)*(1|2)(1|2)(1|2)(0|1|2)*")).toAutomaton().complement());
        auto.minimize();

        // Deux repos d'affiler.
        auto = auto.intersection(new RegExp(StringUtils.toCharExp("((1|2)*00+(1|2)*)*")).toAutomaton());
        auto.minimize();

        //deux jour d'affiler au moins
        auto = auto.intersection(new RegExp(StringUtils.toCharExp("(0*(1|2)(1|2)+0*)*")).toAutomaton());
        auto.minimize();


        TIntHashSet alpha = new TIntHashSet(new int[]{0,1,2});

        FiniteAutomaton ret = new FiniteAutomaton();
        ret.fill(auto,alpha);
        System.out.println(ret);

        return ret;

    }

    public void launch(int type)
    {
        switch(type)
        {
            case REG : launchRegular(); break;
            case COR : launchCRegular(); break;
            case MCR : launchMCRegular(); break;
        }
    }

    private void launchRegular()
    {
        objectif = makeIntVar("obj",0,1000*vars.length, CPOptions.V_BOUND);
        CPModel m = new CPModel();
        m.addVariable(objectif);

        m.addConstraint(regular(automaton,vars));

        IntegerVariable[] cvar  = makeIntVarArray("c",vars.length,0,1000,CPOptions.V_BOUND);
        IntegerVariable[] gccvar = makeIntVarArray("gcc",3,0,vars.length);
        IntegerVariable nbTravail = makeIntVar("trav",6,8,CPOptions.V_BOUND);
        IntegerVariable nbRepos = makeIntVar("rep",costs.length-8,costs.length-6,CPOptions.V_BOUND);

        for (int i  = 0 ; i < vars.length ; i++)
        {
            m.addConstraint(nth(vars[i],costs[i],cvar[i]));
        }

        m.addConstraint(globalCardinality(vars,gccvar, 0));
        m.addConstraint(eq(nbTravail,(plus(gccvar[1],gccvar[2]))));
        m.addConstraint(eq(nbRepos,(gccvar[0])));

        m.addConstraint(eq(objectif,sum(cvar)));



        CPSolver s = new CPSolver();


        s.read(m);
        s.setVarIntSelector(new StaticVarOrder(s, s.getVar(vars)));

        s.minimize(s.getVar(objectif),false);
        //s.solve();
        printSolution(s);






    }

    private void launchCRegular()
    {
        objectif = makeIntVar("obj",0,1000*vars.length,CPOptions.V_BOUND);
        CPModel m = new CPModel();
        m.addVariable(objectif);
        m.addConstraint(costRegular(vars,objectif,automaton,costs));

        IntegerVariable[] gccvar = makeIntVarArray("gcc",3,0,vars.length);
        IntegerVariable nbTravail = makeIntVar("trav",6,8,CPOptions.V_BOUND);
        IntegerVariable nbRepos = makeIntVar("rep",costs.length-8,costs.length-6,CPOptions.V_BOUND);
        m.addConstraint(globalCardinality(vars,gccvar, 0));
        m.addConstraint(eq(nbTravail,(plus(gccvar[1],gccvar[2]))));
        m.addConstraint(eq(nbRepos,(gccvar[0])));


        CPSolver s = new CPSolver();


        s.read(m);
        s.setVarIntSelector(new StaticVarOrder(s, s.getVar(vars)));

        s.minimize(s.getVar(objectif),false);
        printSolution(s);


    }

    private void launchMCRegular()
    {
        objectif = makeIntVar("obj",0,1000*vars.length,CPOptions.V_BOUND);
        CPModel m = new CPModel();
        m.addVariable(objectif);

        IntegerVariable nbTravail = makeIntVar("trav",6,8,CPOptions.V_BOUND);
        IntegerVariable nbRepos = makeIntVar("rep",costs.length-8,costs.length-6,CPOptions.V_BOUND);


        int[][][] csts = new int[costs.length][costs[0].length][3];
        for (int i = 0 ; i < csts.length ; i++)
        {
            for (int j = 0 ; j< csts[i].length ; j++)
            {
                csts[i][j][0] = costs[i][j];
                csts[i][j][1] = (j==1||j==2)?1:0;
                csts[i][j][2] = (j==1||j==2)?0:1;

            }
        }
        IntegerVariable[] cvars = new IntegerVariable[]{objectif,nbTravail,nbRepos};
        m.addConstraint(multiCostRegular(vars,cvars,automaton,csts));


        CPSolver s = new CPSolver();


        s.read(m);
        s.setVarIntSelector(new StaticVarOrder(s, s.getVar(vars)));

        s.minimize(s.getVar(objectif),false);
        printSolution(s);



    }


    private void printSolution(CPSolver s)
    {
        System.out.println("REALISABLE ? "+s.isFeasible());
        for (IntDomainVar v : s.getVar(vars))
        {
            System.out.print(v.getVal()+ " ");
        }
        System.out.println("");
        System.out.println("COST : "+s.getVar(objectif).getVal());
        s.printRuntimeStatistics();
    }


    public static void main(String[] args) {

        RegCRMCRComp pb = new RegCRMCRComp(40,1);
        pb.launch(RegCRMCRComp.REG);
        pb.launch(RegCRMCRComp.COR);
        pb.launch(RegCRMCRComp.MCR);



    }



}