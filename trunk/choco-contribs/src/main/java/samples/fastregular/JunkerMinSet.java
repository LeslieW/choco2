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
package samples.fastregular;


import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.automaton.FA.Automaton;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import dk.brics.automaton.RegExp;
import gnu.trove.TIntArrayList;
import gnu.trove.TIntHashSet;

import java.util.ArrayList;
import java.util.HashMap;

import static choco.Choco.*;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Nov 23, 2009
 * Time: 11:32:49 AM
 */
public class JunkerMinSet {

    ArrayList<dk.brics.automaton.Automaton> allAutomata;
    TIntArrayList idx;
    Constraint in;
    CPModel m;
    CPSolver s;
    IntegerVariable[] vars;

    TIntHashSet alpha;

    public JunkerMinSet(IntegerVariable[] vars,ArrayList<dk.brics.automaton.Automaton> allAutomata, TIntHashSet alpha)
    {
        this.allAutomata = allAutomata;
        this.m = new CPModel();
        this.s = new CPSolver();
        this.alpha = alpha;
        this.vars = vars;
    }


    public int[] computeMinConflictSet(double[] inactive)
    {
        HashMap<dk.brics.automaton.Automaton,Integer> map = new HashMap<dk.brics.automaton.Automaton,Integer>();
        ArrayList<dk.brics.automaton.Automaton> in = new ArrayList<dk.brics.automaton.Automaton>();
        for (int i = 0 ; i < inactive.length ;i++)
        {
            if (inactive[i] == 0)
            {
                in.add(allAutomata.get(i));
                map.put(allAutomata.get(i),i);
            }

        }
        ArrayList<dk.brics.automaton.Automaton> tmp = getMinSet(in);

        TIntHashSet ret = new TIntHashSet();
        for (dk.brics.automaton.Automaton a : tmp)
        {
            ret.add(map.get(a));
        }

        return ret.toArray();

    }

    ArrayList<dk.brics.automaton.Automaton> cset = new ArrayList<dk.brics.automaton.Automaton>();

    private ArrayList<dk.brics.automaton.Automaton> getMinSet(ArrayList<dk.brics.automaton.Automaton> autos)
    {
        ArrayList<dk.brics.automaton.Automaton> in = new ArrayList<dk.brics.automaton.Automaton>();

        boolean b = true;
        while(b)
        {
            dk.brics.automaton.Automaton tmp = autos.remove(autos.size()-1);
            in.add(tmp);
            Automaton a = makeAutomaton(in);
            System.out.println(a);
            b = solve(a);

        }
        cset.add(in.remove(in.size()-1));
        b = true;

        Automaton a = makeAutomaton(cset);
        if(solve(a))
        {
            autos.clear();

            autos.addAll(in);
            autos.addAll(cset);
            return getMinSet(autos);
        }


        return cset;



    }

    private boolean solve(Automaton a)
    {
        m.removeConstraint(in);
        in = regular(a,vars);
        m.addConstraint(in);
        s.clear();
        s.read(m);

        try{
            s.propagate();
        } catch (ContradictionException e) {
            return false;
        }
        return true;

    }

    private Automaton makeAutomaton(ArrayList<dk.brics.automaton.Automaton> in)
    {
        dk.brics.automaton.Automaton temp = in.get(0);

        for (int i = 1 ; i < in.size() ; i++)
        {
            temp = temp.intersection(in.get(i));
            temp.minimize();
        }
        Automaton auto = new Automaton();
        auto.fill(temp,alpha);
        return auto;
    }

    public static dk.brics.automaton.Automaton fromRule(String s,boolean allowed)
    {
        dk.brics.automaton.Automaton ret = new RegExp(StringUtils.toCharExp(s)).toAutomaton();
        return allowed?ret:ret.complement();
    }

    public static void main(String[] args) {

        ArrayList<dk.brics.automaton.Automaton> bui = new ArrayList<dk.brics.automaton.Automaton>();
        //rules 1 : START WITH A WORKING DAY
        bui.add(fromRule("0(0|1|2)*",false));
        System.out.println(bui.get(0));

        //rules 2 : NO MORE THAN TWO CONSECUTIVE RESTS
        //  bui.add(fromRule("(0|1|2)*000(0|1|2)*",false));


        //rules 8 : NO SHIFT :)
        //bui.add(fromRule("(0|1|2)*",false));

        //rules 3 : REST AFTER A NIGHT SHIFT SET
        //  bui.add(fromRule("(0|1|2)*2+(1)(0|1|2)*",false));
        //rules 4 : AT LEAST THREE CONSECUTIVE WORKING DAYS
        bui.add(fromRule("((0|1|2)*(1|2)(1|2)0(0|1|2)*)|((0|1|2)*(1|2)0(0|1|2)*)",false));

        //rules 5 : AFTER TWO WORKING DAYS => A REST
        bui.add(fromRule("(0|1|2)*(1|2)(1|2)(1|2)(0|1|2)*",false));
        //rules 6 : AT LEAST TWO REST IN A ROW
        //   bui.add(fromRule("(0|1|2)*0(1|2)(0|1|2)*"));

        //rules 7 : AT LEAST TWO RESTS DURING THE SCHEDULE
        //   bui.add(fromRule("(1|2)*|(1|2)*0(1|2)*"));


        IntegerVariable[] vars = makeIntVarArray("x",10,0,2);

        TIntHashSet hs = new TIntHashSet(new int[]{0,1,2});
        JunkerMinSet ms = new JunkerMinSet(vars,bui,hs);

        int[] cset = ms.computeMinConflictSet(new double[bui.size()]);

        for (int d : cset)
            System.out.print(d+" ");
        System.out.println("");



    }



}