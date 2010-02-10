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
import choco.cp.solver.constraints.integer.channeling.ReifiedLargeOr;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.integer.extension.TuplesTest;
import choco.kernel.solver.variables.integer.IntDomainVar;
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
public class OrTest {

    @Test
    public void test1(){
        for(int i = 0; i < 50; i++){
            Model m1 = new CPModel();
            Model m2 = new CPModel();
            Solver s1 = new CPSolver();
            Solver s2 = new CPSolver();

            IntegerVariable[] bool = makeBooleanVarArray("b", 2);

            m1.addConstraint(or(bool));

            m2.addConstraint(or(eq(bool[0],1), eq(bool[1], 1)));

            s1.read(m1);
            s2.read(m2);

            s1.setVarIntSelector(new RandomIntVarSelector(s1, s1.getVar(bool), i));
            s2.setVarIntSelector(new RandomIntVarSelector(s2, s2.getVar(bool), i));
            s1.setValIntSelector(new RandomIntValSelector(i));
            s2.setValIntSelector(new RandomIntValSelector(i));

            s1.solveAll();
            s2.solveAll();

            Assert.assertEquals("solutions", s2.getNbSolutions(), s1.getNbSolutions());
            Assert.assertEquals("nodes", s2.getNodeCount(), s1.getNodeCount());
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

            IntegerVariable[] bool = makeBooleanVarArray("b", 5+r.nextInt(5));

            m1.addConstraint(or(bool));

            Constraint[] cs = new Constraint[bool.length];
            for(int j = 0; j < bool.length; j++){
                cs[j] = eq(bool[j],1);
            }

            m2.addConstraint(or(cs));

            s1.read(m1);
            s2.read(m2);

            s1.setVarIntSelector(new RandomIntVarSelector(s1, s1.getVar(bool), i));
            s2.setVarIntSelector(new RandomIntVarSelector(s2, s2.getVar(bool), i));
            s1.setValIntSelector(new RandomIntValSelector(i));
            s2.setValIntSelector(new RandomIntValSelector(i));

            s1.solveAll();
            s2.solveAll();

            Assert.assertEquals("solutions", s2.getNbSolutions(), s1.getNbSolutions());
//            Assert.assertEquals("nodes", s2.getNodeCount(), s1.getNodeCount());
        }
    }

    @Test
    public void test3(){
        Random r;
        for(int i = 0; i < 100; i++){
            r = new Random(i);
            Model m1 = new CPModel();
            Model m2 = new CPModel();
            Solver s1 = new CPSolver();
            Solver s2 = new CPSolver();

            IntegerVariable bin = makeBooleanVar("bin");
            IntegerVariable[] bool = makeBooleanVarArray("b", 1+r.nextInt(20));

            Constraint c = reifiedOr(bin, bool);

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


            s1.solveAll();

            s2.solveAll();
            int nbSol = (int)Math.pow(2,bool.length)-1;

            Assert.assertEquals("solutions", 1 , s1.getNbSolutions());
            Assert.assertEquals("solutions", nbSol, s2.getNbSolutions());
        }
    }

    @Test
    public void test4(){
        Random r;
        for(int i = 0; i< 100; i++){
            r = new Random(i);
            Model m1 = new CPModel();
            IntegerVariable[] bool = makeBooleanVarArray("b", 3);
            Constraint c = reifiedOr(bool[0], bool[1],bool[2]);
            m1.addConstraint(c);
            int nbSol = 4;
            int ad = r.nextInt(7);
            switch (ad){
                case 0 :
                    m1.addConstraint(eq(bool[0], 0));
                    nbSol = 1;
                    break;
                case 1:
                    m1.addConstraint(eq(bool[0], 1));
                    nbSol = 3;
                    break;
                case 2 :
                    m1.addConstraint(eq(bool[1], 0));
                    nbSol = 2;
                    break;
                case 3:
                    m1.addConstraint(eq(bool[1], 1));
                    nbSol = 2;
                    break;
                case 4 :
                    m1.addConstraint(eq(bool[2], 0));
                    nbSol = 2;
                    break;
                case 5:
                    m1.addConstraint(eq(bool[2], 1));
                    nbSol = 2;
                    break;
            }
            Solver s1 = new CPSolver();
            s1.read(m1);

            s1.setVarIntSelector(new RandomIntVarSelector(s1, i));
            s1.setValIntSelector(new RandomIntValSelector(i));
            s1.solveAll();
            Assert.assertEquals("solutions", nbSol , s1.getNbSolutions());

        }
    }

    @Test
    public void test5(){
        Random r;
        for(int i = 0; i< 200; i++){
            r = new Random(i);
            Model m1 = new CPModel();
            IntegerVariable[] bin = makeBooleanVarArray("bin", 1);
            IntegerVariable[] bool = makeBooleanVarArray("b", 1+r.nextInt(15));
            IntegerVariable[] bools = ArrayUtils.append(bin, bool);
            Constraint c = reifiedOr(bin[0], bool);
            m1.addConstraint(c);
            int idx = r.nextInt(bools.length);
            int val = r.nextInt(2);
            m1.addConstraint(eq(bools[idx], val));
            int nbSol = (int)Math.pow(2,bool.length)/2;
            if(idx == 0){
                if(val ==0){
                    nbSol = 1;
                }else{
                    nbSol = (int)Math.pow(2,bool.length)-1;
                }
            }

            Solver s1 = new CPSolver();
            s1.read(m1);

            s1.setVarIntSelector(new RandomIntVarSelector(s1, i));
            s1.setValIntSelector(new RandomIntValSelector(i));
            s1.solveAll();
            Assert.assertEquals("solutions", nbSol , s1.getNbSolutions());

        }
    }

    private static class OrChecker extends TuplesTest {
        public boolean checkTuple(int[] tuple) {
            int bin = tuple[0];
            int sum = 0;
            int i  = 1 ;
            while (i<  tuple.length) {
                sum += tuple[i];
                i++;
                if(sum>0)break;
            }
            return (bin == 1 && sum == 1)||(bin == 0 && sum == 0);
        }
    }

    @Test
    public void test6(){
        Random r;
        for(int i = 34; i< 200; i++){
            r = new Random(i);
            Model m1 = new CPModel();
            Model m2 = new CPModel();

            IntegerVariable[] bin = makeBooleanVarArray("bin", 1);
            IntegerVariable[] bool = makeBooleanVarArray("b", 1+r.nextInt(10));
            IntegerVariable[] bools = ArrayUtils.append(bin, bool);
            Constraint c1 = reifiedOr(bin[0], bool);
            Constraint c2 = relationTupleAC(bools, new OrChecker());

            m1.addConstraint(c1);
            m2.addConstraint(c2);

            int idx = r.nextInt(bools.length);
            int val = r.nextInt(2);

            m1.addConstraint(eq(bools[idx], val));

            m2.addConstraint(eq(bools[idx], val));

            int nbSol = (int)Math.pow(2,bool.length)/2;
            if(idx == 0){
                if(val ==0){
                    nbSol = 1;
                }else{
                    nbSol = (int)Math.pow(2,bool.length)-1;
                }
            }
            Solver s1 = new CPSolver();
            s1.read(m1);
            Solver s2 = new CPSolver();
            s2.read(m2);

            s1.setVarIntSelector(new RandomIntVarSelector(s1, i));
            s1.setValIntSelector(new RandomIntValSelector(i));
            s1.solveAll();

            s2.setVarIntSelector(new RandomIntVarSelector(s2, i));
            s2.setValIntSelector(new RandomIntValSelector(i));
            s2.solveAll();
            Assert.assertEquals("solutions  -- seed:"+i, nbSol , s1.getNbSolutions());
            Assert.assertEquals("solutions -- seed:"+i, s2.getNbSolutions() , s1.getNbSolutions());

        }
    }

    @Test
    public void test7(){
        Solver s = new CPSolver();
        IntDomainVar a  = s.createEnumIntVar("a", 1, 1);
        IntDomainVar b  = s.createEnumIntVar("b", 1, 1);
        IntDomainVar c  = s.createEnumIntVar("c", 1, 1);

        s.post(new ReifiedLargeOr(new IntDomainVar[]{a,b,c}));

        s.solveAll();

        Assert.assertEquals(1, s.getNbSolutions());

    }
}
