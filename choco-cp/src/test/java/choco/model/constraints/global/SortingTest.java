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
package choco.model.constraints.global;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.SortingSConstraint;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.UtilAlgo;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import junit.framework.TestCase;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 4 juin 2007
 * Time: 16:45:46
 */
public class SortingTest extends TestCase {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

    public static void testSorting() {
        CPModel m = new CPModel();
        IntegerVariable[] x = {
                makeIntVar("x0", 1, 16),
                makeIntVar("x1", 5, 10),
                makeIntVar("x2", 7, 9),
                makeIntVar("x3", 12, 15),
                makeIntVar("x4", 1, 13)
        };
        IntegerVariable[] y = {
                makeIntVar("y0", 2, 3),
                makeIntVar("y1", 6, 7),
                makeIntVar("y2", 8, 11),
                makeIntVar("y3", 13, 16),
                makeIntVar("y4", 14, 18)
        };
        Constraint c = sorting(x, y);
        m.addConstraint(c);
        CPSolver s = new CPSolver();
        s.read(m);
        try {
            ((SortingSConstraint)s.getCstr(c)).boundConsistency();
        }
        catch (ContradictionException e) {
            assertTrue(false);
            e.printStackTrace();
        }
    }

    public static void testSorting2() {
        for (int seed = 0; seed < 1; seed++) {
            CPModel m = new CPModel();
            int n = 3;
            IntegerVariable[] x = makeIntVarArray("x", n, 0, n);
            IntegerVariable[] y = makeIntVarArray("y", n, 0, n);
            Constraint c = sorting(x, y);
            m.addConstraint(c);
            m.addConstraint(allDifferent(x));
            CPSolver s = new CPSolver();
            s.read(m);
//            s.setValIntSelector(new RandomIntValSelector(seed));
//            s.setVarIntSelector(new RandomIntVarSelector(s, seed + 2));
            s.solve();
            HashSet<String> sols = new HashSet<String>();
            if(s.isFeasible()){
                do{
                    StringBuffer st = new StringBuffer();
                    st.append(s.getVar(x[0]).getVal());
                    for(int i = 1; i < n; i++){
                        st.append(",").append(s.getVar(x[i]).getVal());
                    }
//                    st.append(" - ").append(s.getVar(y[0]).getVal());
//                    for(int i = 1; i < n; i++){
//                        st.append(",").append(s.getVar(y[i]).getVal());
//                    }
                    sols.add(st.toString());
                    System.out.println(st.toString());
                }while(s.nextSolution());
            }

            System.out.println("---------------");
            CPSolver s1 = new CPSolver();
            s1.read(m);
//            s.setValIntSelector(new RandomIntValSelector(seed));
//            s.setVarIntSelector(new RandomIntVarSelector(s, seed + 2));
            s1.setVarIntSelector(new StaticVarOrder(s1.getVar((IntegerVariable[])UtilAlgo.append(x,y))));
            s1.setValIntIterator(new IncreasingDomain());
            s1.solve();
            if(s1.isFeasible()){
                do{
                    StringBuffer st = new StringBuffer();
                    st.append(s1.getVar(x[0]).getVal());
                    for(int i = 1; i < n; i++){
                        st.append(",").append(s1.getVar(x[i]).getVal());
                    }
//                    st.append(" - ").append(s1.getVar(y[0]).getVal());
//                    for(int i = 1; i < n; i++){
//                        st.append(",").append(s1.getVar(y[i]).getVal());
//                    }
                    sols.remove(st.toString());
                    System.out.println(st.toString());
                }while(s1.nextSolution());
            }
            System.out.println("########");
            for(int i = 0 ; i < sols.size(); i++){
                System.out.println(sols.toArray()[i]);
            }

            System.out.println(MessageFormat.format("{0} - {1}:{2}", n, s.getNbSolutions(), s1.getNbSolutions()));
            assertEquals(s.getNbSolutions(), s1.getNbSolutions());
//            assertEquals(840, s1.getNbSolutions());
        }

    }

    public void testName() {
        CPModel m = new CPModel();
        int n = 3;
        IntegerVariable[] x = makeIntVarArray("x", n, 0, n);
        IntegerVariable[] y = makeIntVarArray("y", n, 0, n);
        m.addConstraint(sorting(x, y));
        m.addConstraint(allDifferent(x));
        CPSolver s = new CPSolver();
        s.read(m);
        s.setVarIntSelector(new StaticVarOrder(s.getVar(x)));
        s.setValIntIterator(new IncreasingDomain());
        s.solve();
        if(s.isFeasible()){
                do{
                    StringBuffer st = new StringBuffer();
                    st.append(s.getVar(x[0]).getVal());
                    for(int i = 1; i < n; i++){
                        st.append(",").append(s.getVar(x[i]).getVal());
                    }
                    System.out.println(st.toString());
                }while(s.nextSolution());
            }
        System.out.println(MessageFormat.format("{0}", s.getNbSolutions()));
    }
}
