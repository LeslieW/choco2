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
package maif;

import choco.Choco;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.search.integer.ValSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;
import jxl.write.WriteException;
import maif.cp.MaifModel;
import maif.cp.heuristics.PrefValSelector;
import maif.entities.Person;
import maif.entities.Preference;
import maif.ihm.MainWindow;
import maif.parser.FileReader;
import maif.parser.FileWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Jul 20, 2009
 * Time: 4:32:42 PM
 */
public class MaifPlanner {

    CPSolver s;
    MaifModel m;
    ValSelector valselector;


    public static void main(String[] args) {
        new MainWindow(new MaifPlanner());
    }

    private static HashMap<IntDomainVar, ArrayList<Person>> toSolverMap(CPSolver s, HashMap<IntegerVariable, Preference> varMap,HashMap<Preference,ArrayList<Person>> prefMap) {

        HashMap<IntDomainVar,ArrayList<Person>> ret = new HashMap<IntDomainVar,ArrayList<Person>>();
        for (IntegerVariable v : varMap.keySet())
        {

            ret.put(s.getVar(v),prefMap.get(varMap.get(v)));
        }
        return ret;
    }

    private static void affiche(CPSolver s, MaifModel m)
    {
        ArrayList<Person> p = m.getPeople();
        IntegerVariable[] dayVars = m.getDayVars();
        IntegerVariable[] cardVars = m.getCardVars();
        if (s.isFeasible())
        {
            int i = 0;
            for (IntegerVariable v : dayVars)
            {
                System.out.print(p.get(s.getVar(v).getVal()).getName());
                System.out.print("\t");
                i++;
                if (i %4 == 0)
                    System.out.println("");
            }

            i = 0;
            for (IntegerVariable v : cardVars)
            {
                System.out.println(p.get(i++).getName()+" : "+s.getVar(v).getVal());
            }
            System.out.println("");
            System.out.println("Ecart Maximal : "+s.getVar(m.getDeviation()).getVal());

        }
    }

    public void startSolving(int month, int year, File planning, File preference, File output) {

        FileReader read = new FileReader();
        FileWriter write = new FileWriter();
        ArrayList<Person> people = read.makePeople(month,year,planning,preference);

        m  = new MaifModel(people,read.getDays(),true);

        s = new CPSolver();
        s.read(m);

        valselector = new PrefValSelector(toSolverMap(s,m.getVarMap(),read.getPrefMap()),read.getBouchon()) ;
        s.setValIntSelector(valselector);
        s.setVarIntSelector(new StaticVarOrder(s, s.getVar(m.getDayVars())));


        s.setTimeLimit(1600);

        IntDomainVar obj = s.getVar(m.getDeviation());

        if (s.minimize(obj,true) == Boolean.TRUE)
        //if (s.solve() == Boolean.TRUE)
        {
            do {
                affiche(s,m);
                s.postCut(s.lt(obj,obj.getVal()));
            } while(s.nextSolution() == Boolean.TRUE);
            s.worldPopUntil(0);
            s.restoreSolution(s.getSearchStrategy().getSolutionPool().getBestSolution());
            try {
                write.writeSolution(s,m,output,month,year);
            } catch (WriteException e) {
                System.err.println("Impossible d'ecrire la solution");
            }
        }
        else
        {
            System.out.println("NO SOLUTION FOUND WITH GEQ CONSTRAINTS, DROPPING THEM...");
            m  = new MaifModel(people,read.getDays(),false);

            s = new CPSolver();
            s.read(m);

            valselector = new PrefValSelector(toSolverMap(s,m.getVarMap(),read.getPrefMap()),read.getBouchon()) ;
            s.setValIntSelector(valselector);
            s.setVarIntSelector(new StaticVarOrder(s, s.getVar(m.getDayVars())));

            s.setTimeLimit(2000);

            obj = s.getVar(m.getDeviation());
            if (s.minimize(obj,true) == Boolean.TRUE)
            //if (s.solve() == Boolean.TRUE)
            {
                do {
                    affiche(s,m);
                    s.postCut(s.lt(obj,obj.getVal()));
                } while(s.nextSolution() == Boolean.TRUE);
                s.worldPopUntil(0);
                s.restoreSolution(s.getSearchStrategy().getSolutionPool().getBestSolution());
                try {
                    write.writeSolution(s,m,output,month,year);
                } catch (WriteException e) {
                    System.err.println("Impossible d'ecrire la solution");
                }
            }
            else
            {
                System.err.println("NO SOLUTION FOUND");
            }



        }
    }

    public void nextSolution()
    {
        m.addConstraint(Choco.eq(m.getDeviation(),s.getVar(m.getDeviation()).getVal()));
        s = new CPSolver();
        s.read(m);
        s.setValIntSelector(valselector);
        s.setVarIntSelector(new StaticVarOrder(s, s.getVar(m.getDayVars())));

        if (s.solve() == Boolean.TRUE)
        {

        }

    }



}