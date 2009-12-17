package choco.cp.solver.constraints.global.automata.multicostregular.example;
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

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.automata.multicostregular.FastMultiCostRegular;
import choco.cp.solver.search.integer.branching.AssignVar;
import choco.cp.solver.search.integer.valiterator.DecreasingDomain;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.automaton.FA.Automaton;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.variables.integer.IntDomainVar;
import dk.brics.automaton.RegExp;
import gnu.trove.TIntHashSet;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Nov 19, 2009
 * Time: 9:46:44 AM
 */
public class RuleModel extends CPModel {

    dk.brics.automaton.Automaton full;

    TIntHashSet alpha;
    String work;
    String all;

    IntegerVariable[][] vs;
    IntegerVariable[][] cvs;


    public RuleModel()
    {



        int[] tmp = {0,1,2};
        alpha = new TIntHashSet();
        alpha.addAll(tmp);
        work = "(";
        for (int i = 0 ; i < tmp.length-1 ;i++)
            work+=tmp[i]+"|";
        work = work.substring(0,work.length()-1)+")";
        all = "("+tmp[2]+"|"+work.substring(1,work.length());
        System.out.println("WORK : "+work);


    }


    public void buildConsecutiveWERule()
    {
        StringBuilder frule = new StringBuilder("((");
        for (int j = 0 ; j < 3 ; j++)
        {
            for (int i = 0 ; i < 5 ; i++)
            {
                frule.append(all);
            }
            frule.append(work);
            frule.append(work);
        }
        for (int j = 0 ; j < 7 ; j++)
        {
            frule.append(all);
        }

        frule.append(")|(");

        for (int j = 0 ; j < 7 ; j++)
        {
            frule.append(all);
        }

        for (int j = 0 ; j < 3 ; j++)
        {
            for (int i = 0 ; i < 5 ; i++)
            {
                frule.append(all);
            }
            frule.append(work);
            frule.append(work);
        }

        frule.append("))");

        full = new RegExp(StringUtils.toCharExp(frule.toString())).toAutomaton();


    }

    public void buildNoNightBeforeFreeWE()
    {
        String ret = "((";
        for (int i = 0 ; i < 4 ; i++)
        {
            ret+=all;
        }
        ret+="122";

        for (int j = 0 ; j < 3 ; j++)
            for (int i = 0; i < 7 ; i++)
                ret+=all;
        ret+=")|(";

        for (int i = 0; i < 7 ; i++)
            ret+=all;


        for (int i = 0 ; i < 4 ; i++)
        {
            ret+=all;
        }
        ret+="122";

        for (int j = 0 ; j < 2 ; j++)
            for (int i = 0; i < 7 ; i++)
                ret+=all;
        ret+=")|(";


        for (int j = 0 ; j < 2 ; j++)
            for (int i = 0; i < 7 ; i++)
                ret+=all;

        for (int i = 0 ; i < 4 ; i++)
        {
            ret+=all;
        }
        ret+="122";

        for (int i = 0; i < 7 ; i++)
            ret+=all;

        ret+=")|(";

        for (int j = 0 ; j < 3 ; j++)
            for (int i = 0; i < 7 ; i++)
                ret+=all;

        for (int i = 0 ; i < 4 ; i++)
        {
            ret+=all;
        }
        ret+="122";
        ret+="))";

        full = full.union(new RegExp(StringUtils.toCharExp(ret)).toAutomaton());
        full.minimize();





    }

    public void buildNoMoreThanDayRule()
    {
        String ret = all+"*";
        for (int i = 0 ;i < 6 ; i++)
            ret+=work;
        ret+=all+"*";

        full = full.union(new RegExp(StringUtils.toCharExp(ret)).toAutomaton());
        full.minimize();


    }

    public void buildRestAfterNight()
    {
        String ret = all+"*";
        ret+="1+0";
        ret+=all+"*";

        full = full.union(new RegExp(StringUtils.toCharExp(ret)).toAutomaton());
        full.minimize();


    }

    public void buildCompleteWE()
    {
        StringBuffer b = new StringBuffer("(");
        String patter = "((2(0|1))|((0|1)2))";
        int nd = 2;
        int nw = 4;
        int sd = 6;

        for (int w = 0 ; w < nw ; w++)
        {
            b.append("(");
            for (int i = 1; i < sd+(7*w) ;i++)
                b.append(all);
            b.append(patter);
            for (int i = (sd+7*w)+nd ; i <=28 ; i++)
                b.append(all);
            b.append(")|");
        }
        b.deleteCharAt(b.length()-1).append(")");


        System.out.println(b);
        full = full.union(new RegExp(StringUtils.toCharExp(b.toString())).toAutomaton());
        full.minimize();

        Automaton tmp = new Automaton();
        tmp.fill(new RegExp(StringUtils.toCharExp(b.toString())).toAutomaton().complement(),alpha);
        System.out.println(tmp);



    }

    public void everything()
    {
        String p = ("(0|1|2)*");
        full = full.intersection(new RegExp(StringUtils.toCharExp(p)).toAutomaton().complement());
        full.minimize();
    }


    void fillModel()
    {

        vs = makeIntVarArray("x",8,28,0,2);

        int[][][] csts = new int[vs[0].length][3][7];


        for (int i = 0 ; i < csts.length ; i++)
        {
            for (int j = 0 ; j < csts[i].length ; j++)
            {
                if (j == 0 || j == 1)
                    csts[i][j][4] = 1;
                if (j==1)
                    csts[i][j][5] = 1;
                if (j==2)
                    csts[i][j][6] = 1;

                if (j == 0 || j == 1)
                {
                    csts[i][j][0+i/7] = 1;
                }


            }
        }

        cvs = new IntegerVariable[8][7];
        for (int i  = 0 ; i < 4 ;i++)
        {
            IntegerVariable[] tmp = cvs[i];
            tmp[4] = makeIntVar("z_{"+i+",0}",0,18,"cp:bound");
            tmp[5] = makeIntVar("z_{"+i+",1}",0,4,"cp:bound");
            tmp[6] = makeIntVar("z_{"+i+",2}",10,28,"cp:bound");
            for (int j = 0 ; j < 4 ; j++)
                tmp[j] = makeIntVar("z_{"+i+","+j+"}",4,5,"cp:bound");
            this.addVariables(tmp);
          //  this.addConstraint(eq(minus(tmp[4],28),minus(0,tmp[6])));

            this.addVariables(vs[i]);

        }

        for (int i  = 4 ; i < 8 ;i++)
        {
            IntegerVariable[] tmp = cvs[i];
            tmp[4] = makeIntVar("z_{"+i+",0}",0,10,"cp:bound");
            tmp[5] = makeIntVar("z_{"+i+",1}",0,4,"cp:bound");
            tmp[6] = makeIntVar("z_{"+i+",1}",18,28,"cp:bound");

            for (int j = 0 ; j < 4 ; j++)
                tmp[j] = makeIntVar("z_{"+i+","+j+"}",2,3,"cp:bound");
            this.addVariables(tmp);
        //    this.addConstraint(eq(plus(tmp[4],tmp[6]),28));
            this.addVariables(vs[i]);

        }

        Automaton auto = new Automaton();
        auto.fill(full.complement(),alpha);

        int[] word = {2,2,2,2,1,2,0,2,2,2,1,2,2,0,0,0,2,1,2,2,2,2,2,0,0,2,2,1};
        for (int i : word)
            System.out.print(i+" ");
        System.out.println("");

        System.out.println(word.length);
        System.out.println("BIZARRE ? "+auto.run(word));

        for (int i  = 0 ;i < 8 ; i++)
        {
            Constraint mr = multiCostRegular(vs[i],cvs[i],auto,csts);
          //  this.addConstraint(regular(auto,vs[i]));

            this.addConstraint(mr);
            cstr.add(mr);

        }


        int[] low = {3,1,4};
        int[] up = {3,1,4};

        IntegerVariable[][] trans = ArrayUtils.transpose(vs);
        for (int i  = 0 ; i < 28 ; i++)
        {
            this.addConstraint("cp:bc",globalCardinality(trans[i],low,up));
        }



    }

    public void addLexConstraint()
    {
        IntegerVariable[][] a = new IntegerVariable[4][28];
        IntegerVariable[][] b = new IntegerVariable[4][28];



       /* System.arraycopy(vs, 0, a, 0, a.length);
        System.arraycopy(vs, 4, b, 0, b.length);
        System.arraycopy(vs, 0, aa, 0, aa.length);
        System.arraycopy(vs, 4, bb, 0, bb.length);*/

        for (int i = 0 ; i < a.length ; i++)
        {
            System.arraycopy(vs[i],0,a[i],0,a[i].length);
            System.arraycopy(vs[i+4],0,b[i],0,b[i].length);




        }

        this.addConstraints(lexChain(a),lexChain(b));



    }


    ArrayList<Constraint> cstr = new ArrayList<Constraint>();


    public static void main(String[] args) {
        RuleModel m = new RuleModel();

        m.buildConsecutiveWERule();
        m.buildNoNightBeforeFreeWE();
       m.buildNoMoreThanDayRule();
        m.buildRestAfterNight();
        m.buildCompleteWE();

       // m.everything();

        m.fillModel();

        m.addLexConstraint();
        
        CPSolver s = new CPSolver();
       

        s.read(m);

        FastMultiCostRegular[] mcr = new FastMultiCostRegular[m.cstr.size()];
        for (int i = 0 ; i < mcr.length ; i++)
                mcr[i] =(FastMultiCostRegular) s.getCstr(m.cstr.get(i));

        ArrayList<IntDomainVar> mars = new ArrayList<IntDomainVar>();
        for (int i  = 0 ; i < 8 ; i++)
            mars.add(s.getVar(m.cvs[i][4]));

        s.attachGoal(new AssignVar(new StaticVarOrder(mars.toArray(new IntDomainVar[8])),new DecreasingDomain()));
        s.addGoal(new AssignVar(new StaticVarOrder(s.getVar(ArrayUtils.flatten(ArrayUtils.transpose(m.vs)))),new IncreasingDomain()));

     
      //  s.setValIntIterator(new DecreasingDomain());
       // s.setValIntSelector(new MCRValSelector(mcr,false));

        if (s.solve())
        {

                System.out.println(mcr[4].check());
            int i = 0 ;
            for (IntegerVariable[] va : m.vs)
            {

                for (IntDomainVar v : s.getVar(va))
                {
                    System.out.print(toChar(v.getVal())+" ");
                }
                System.out.print("     |   ");
                for (IntDomainVar v : s.getVar(m.cvs[i++]))
                {
                    System.out.print(v.getVal()+" ");
                }
                System.out.println("");
            }

        }
        s.printRuntimeStatistics();
    }


    static char toChar(int i)
    {
        switch(i)
        {
            case 0 : return 'D';
            case 1 : return 'N';
            case 2 : return 'R';
            default : return 'E';
        }
    }


}