/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |   (..)  |                           *
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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.kernel.detector;

import static choco.Choco.*;
import choco.cp.CPOptions;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.detector.DetectorFactory;
import choco.kernel.model.variables.integer.IntegerVariable;
import org.junit.Assert;
import org.junit.Test;

import java.util.logging.Logger;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 1 avr. 2010br/>
 * Since : Choco 2.1.1<br/>
 */
public class DetectorsTest {

    private static final Logger LOGGER  = ChocoLogging.getTestLogger();

//    @Test
//    public void detectEqualities(){
//        ChocoLogging.setVerbosity(Verbosity.VERBOSE);
//
//        String[] options  = new String[]{CPOptions.V_BTREE, CPOptions.V_ENUM, CPOptions.V_BLIST, CPOptions.V_LINK, CPOptions.V_BOUND};
//        Class[] classes = new Class[]{IntervalBTreeDomain.class, BitSetIntDomain.class,
//                BipartiteIntDomain.class, LinkedIntDomain.class, IntervalIntDomain.class};
//
//        CPModel m;
//        CPSolver s;
//        Random r;
//        for(int seed = 0; seed < 50; seed++){
//            r = new Random(seed);
//            m = new CPModel();
//            int dx = r.nextInt(options.length);
//            int dy = r.nextInt(options.length);
//            int low = r.nextInt(20);
//            int upp = low + r.nextInt(20);
//            IntegerVariable x = makeIntVar("x", low, upp, options[dx]);
//            low = r.nextInt(20);
//            upp = low + r.nextInt(20);
//            IntegerVariable y = makeIntVar("y", low, upp, options[dy]);
//
//            m.addConstraint(eq(x, y));
//
//            DetectorFactory.run(m, DetectorFactory.intVarEqDet(m));
//
//            s = new CPSolver();
//            s.read(m);
//            Class c = classes[Math.min(dx,dy)];
//
//
//            Assert.assertEquals("bad object for X ("+seed+")", s.getVar(x).getDomain().getClass(),  c);
//            Assert.assertEquals("bad object for Y ("+seed+")", s.getVar(y).getDomain().getClass(),  c);
//            Assert.assertEquals("wrong lower bound for X ("+seed+")", 0, s.getVar(x).getInf());
//            Assert.assertEquals("wrong upper bound for X ("+seed+")", 2, s.getVar(x).getSup());
//            Assert.assertEquals("wrong lower bound for X ("+seed+")", 0, s.getVar(y).getInf());
//            Assert.assertEquals("wrong upper bound for Y ("+seed+")", 2, s.getVar(y).getSup());
//        }
//    }
//
//    @Test
//    public void detectEqualities2(){
//        String[] options  = new String[]{CPOptions.V_BTREE, CPOptions.V_ENUM, CPOptions.V_BLIST, CPOptions.V_LINK, CPOptions.V_BOUND};
//
//        CPModel m;
//        CPSolver s;
//        Random r;
//        for(int seed = 0; seed < 50; seed++){
//            r = new Random(seed);
//            m = new CPModel();
//            int dx = r.nextInt(options.length);
//            int dy = r.nextInt(options.length);
//            IntegerVariable x = makeIntVar("x", 0, 1, options[dx]);
//            IntegerVariable y = makeIntVar("y", -10, 10, options[dy]);
//            m.addConstraint(eq(x, y));
//
//            DetectorFactory.run(m, DetectorFactory.intVarEqDet(m));
//
//            s = new CPSolver();
//            s.read(m);
//            Assert.assertEquals("bad object for X ("+seed+")", s.getVar(x).getDomain().getClass(),  BooleanDomain.class);
//            Assert.assertEquals("bad object for Y ("+seed+")", s.getVar(y).getDomain().getClass(),  BooleanDomain.class);
//            Assert.assertEquals("wrong lower bound for X ("+seed+")", 0, s.getVar(x).getInf());
//            Assert.assertEquals("wrong upper bound for X ("+seed+")", 1, s.getVar(x).getSup());
//            Assert.assertEquals("wrong lower bound for X ("+seed+")", 0, s.getVar(y).getInf());
//            Assert.assertEquals("wrong upper bound for Y ("+seed+")", 1, s.getVar(y).getSup());
//        }
//    }


    @Test
    public void detectEqualities3(){
        CPModel m;
        CPSolver s;
        for(int size = 1000; size <= 2000; size +=100){
            m = new CPModel();
            IntegerVariable[] vars = makeIntVarArray("v", size, 0, 10, CPOptions.V_BOUND);
            for(int i = 0; i < size-1; i++){
                m.addConstraint(eq(vars[i], vars[i+1]));
            }
            long t = -System.currentTimeMillis();
            DetectorFactory.run(m, DetectorFactory.intVarEqDet(m));
            t += System.currentTimeMillis();
            LOGGER.info(String.format("t: %d ms", t));
            s = new CPSolver();
            s.read(m);
            Assert.assertEquals(1, s.getNbIntVars());
            Assert.assertEquals(0, s.getNbIntConstraints());
        }
    }

    @Test
    public void detectEqualities4(){
        CPModel m;
        CPSolver s;
        for(int size = 1000; size <= 2000; size +=100){
            m = new CPModel();
            IntegerVariable[] vars = makeIntVarArray("v", size, 0, 10, CPOptions.V_BOUND);
            for(int i = 0; i < size-1; i++){
                m.addConstraint(eq(vars[i], vars[i+1]));
                m.addConstraint(leq(vars[i], vars[i+1]));
            }
            long t = -System.currentTimeMillis();
            DetectorFactory.run(m, DetectorFactory.intVarEqDet(m));
            t += System.currentTimeMillis();
            LOGGER.info(String.format("t: %d ms", t));
            s = new CPSolver();
            s.read(m);
            Assert.assertEquals(1, s.getNbIntVars());
            Assert.assertEquals(size, s.getNbIntConstraints());
        }
    }


}
