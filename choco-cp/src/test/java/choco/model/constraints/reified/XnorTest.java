/* ************************************************
*           _       _                            *
*          |  °(..)  |                           *
*          |_  J||L _|        CHOCO solver       *
*                                                *
*     Choco is a java library for constraint     *
*     satisfaction problems (CSP), constraint    *
*     programming (CP) and explanation-based     *
*     constraint solving (e-CP). It is built     *
*     on a event-based propagation mechanism     *
*     with backtrackable structures.             *
*                                                *
*     Choco is an open-source software,          *
*     distributed under a BSD licence            *
*     and hosted by sourceforge.net              *
*                                                *
*     + website : http://choco.emn.fr            *
*     + support : choco@emn.fr                   *
*                                                *
*     Copyright (C) F. Laburthe,                 *
*                   N. Jussien    1999-2009      *
**************************************************/
package choco.model.constraints.reified;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 26 oct. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*/
public class XnorTest {


    @Test
    public void test1(){
        for(int i = 0; i < 50; i++){
            Model m1 = new CPModel();
            Solver s1 = new CPSolver();
            Model m2 = new CPModel();
            Solver s2 = new CPSolver();


            IntegerVariable[] bool = makeBooleanVarArray("b", 2);

            Constraint c = xnor(bool[0], bool[1]);

            m1.addConstraint(c);
            s1.read(m1);

            m2.addConstraint(or(and(eq(bool[0], 1), eq(bool[1], 1)), and(eq(bool[1], 0), eq(bool[0], 0))));
            s2.read(m2);

            s1.setVarIntSelector(new RandomIntVarSelector(s1, i));
            s1.setValIntSelector(new RandomIntValSelector(i));


            s2.setVarIntSelector(new RandomIntVarSelector(s2, i));
            s2.setValIntSelector(new RandomIntValSelector(i));

            s1.solve();
            do{
                Assert.assertEquals(s1.getVar(bool[0]).getVal(), s1.getVar(bool[1]).getVal());
            }while(s1.nextSolution());
            s2.solveAll();

            Assert.assertEquals("solutions", s2.getNbSolutions() , s1.getNbSolutions());
        }
    }

    @Test
    public void test2(){
        Random r;
        for(int i = 0; i < 50; i++){
            r = new Random(i);
            Model m1 = new CPModel();
            Model m2 = new CPModel();
            Solver s1 = new CPSolver();
            Solver s2 = new CPSolver();

            IntegerVariable bin = makeBooleanVar("bin");
            IntegerVariable[] bool = makeBooleanVarArray("b", 2);

            Constraint c = reifiedXnor(bin, bool[0], bool[1]);

            m1.addConstraint(c);
            m1.addConstraint(eq(bin,0));

            m2.addConstraint(c);
            m2.addConstraint(eq(bin,1));


            s1.read(m1);
            s2.read(m2);

            s1.setVarIntSelector(new RandomIntVarSelector(s1, i));
            s1.setValIntSelector(new RandomIntValSelector(i));

            s2.setVarIntSelector(new RandomIntVarSelector(s2, i));
            s2.setValIntSelector(new RandomIntValSelector(i));

            s1.solve();
            do{
                Assert.assertEquals(s1.getVar(bool[0]).getVal(), Math.abs(s1.getVar(bool[1]).getVal()-1));
            }while(s1.nextSolution());

            s2.solve();
            do{
                Assert.assertEquals(s2.getVar(bool[0]).getVal(), s2.getVar(bool[1]).getVal());
            }while(s2.nextSolution());

            Assert.assertEquals("solutions", 2 , s1.getNbSolutions());
            Assert.assertEquals("solutions", 2, s2.getNbSolutions());
        }
    }
}