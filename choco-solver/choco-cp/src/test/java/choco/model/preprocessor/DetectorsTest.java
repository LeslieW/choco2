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
package choco.model.preprocessor;

import choco.Choco;
import static choco.Choco.*;
import choco.cp.CPOptions;
import choco.cp.model.CPModel;
import choco.cp.model.preprocessor.ModelDetectorFactory;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.BoundAllDiff;
import choco.cp.solver.constraints.global.matching.AllDifferent;
import choco.cp.solver.preprocessor.PreProcessCPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.variables.scheduling.TaskVar;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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


    @Test
    public void detectEqualities1(){
        CPModel m;
        CPSolver s;
        for(int size = 1000; size <= 2000; size +=100){
            m = new CPModel();
            IntegerVariable[] vars = makeIntVarArray("v", size, 0, 10, CPOptions.V_BOUND);
            for(int i = 0; i < size-1; i++){
                m.addConstraint(eq(vars[i], vars[i+1]));
            }
            long t = -System.currentTimeMillis();
            ModelDetectorFactory.run(m, ModelDetectorFactory.intVarEqDet(m));
            t += System.currentTimeMillis();
            LOGGER.info(String.format("t: %d ms", t));
            s = new CPSolver();
            s.read(m);
            Assert.assertEquals(1, s.getNbIntVars());
            Assert.assertEquals(0, s.getNbIntConstraints());
        }
    }

    @Test
    public void detectEqualities2(){
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
            ModelDetectorFactory.run(m, ModelDetectorFactory.intVarEqDet(m));
            t += System.currentTimeMillis();
            LOGGER.info(String.format("t: %d ms", t));
            s = new CPSolver();
            s.read(m);
            Assert.assertEquals(1, s.getNbIntVars());
            Assert.assertEquals(size-1, s.getNbIntConstraints());
        }
    }

     @Test
    public void testDetectionCliques() {
        CPModel m = new CPModel();
        m.setDefaultExpressionDecomposition(false);
        IntegerVariable[] v = makeIntVarArray("v", 13, 0, 11);

        for (int i = 0; i < v.length; i++) {
            for (int j = i + 1; j < v.length; j++) {
               m.addConstraint(neq(v[i],v[j]));
            }
        }
         ModelDetectorFactory.run(m, ModelDetectorFactory.cliqueDetector(m, true));

        CPSolver s = new CPSolver();

        s.read(m);

        DisposableIterator<SConstraint> it = s.getConstraintIterator();
         while (it.hasNext()) {
            SConstraint p = it.next();
            LOGGER.info(p.pretty());
         }
         it.dispose();
        s.solveAll();
        assertEquals(0, s.getNbSolutions());
    }


    @Test
    public void testIncludedDiff() {
        CPModel m = new CPModel();
        m.setDefaultExpressionDecomposition(true);
        IntegerVariable[] v = makeIntVarArray("v", 5, 0, 5);
        IntegerVariable[] v2 = makeIntVarArray("v", 5, 0, 5);

        for (int i = 0; i < 5; i++) {
            for (int j = i + 1; j < 5; j++) {
                m.addConstraint(gt(0,mult(minus(v[i],v[j]),minus(v[j],v[i]))));
                m.addConstraint(neq(v2[i],v2[j]));
            }
        }

        ModelDetectorFactory.run(m, ModelDetectorFactory.cliqueDetector(m, true));

        CPSolver s = new CPSolver();

        s.read(m);

        DisposableIterator<SConstraint> it = s.getConstraintIterator();
        boolean alldiffd = false;
        while (it.hasNext()) {
            SConstraint p = it.next();
            LOGGER.info(p.pretty());
            LOGGER.info(String.format("%s", p));
            alldiffd |= (p instanceof AllDifferent || p instanceof BoundAllDiff);
        }
        it.dispose();
        assertTrue(alldiffd);
    }

    @Test
    public void testEqualitiesDetection() {
        for (int k = 0; k < 10; k++) {
            CPModel m = new CPModel();
            Solver s = new PreProcessCPSolver();
            Solver s2 = new CPSolver();
            int n = 2;
            IntegerVariable[] vars = Choco.makeIntVarArray("v", n, 0, n - 1);

            for (int i = 0; i < n - 1; i++) {
                m.addConstraint(Choco.eq(vars[i], vars[i + 1]));
            }
            m.addConstraint(Choco.eq(vars[n - 1], n-1));
            long t1 = System.currentTimeMillis();
            ModelDetectorFactory.run(m, ModelDetectorFactory.intVarEqDet(m));
            s2.read(m);
            long t2 = System.currentTimeMillis();
            s.read(m);
            long t3 = System.currentTimeMillis();

            Assert.assertEquals("nb var BB", s.getNbIntVars(), 1);
            Assert.assertEquals("nb const var BB", s.getNbConstants(),1);
            Assert.assertEquals("nb var S", s2.getNbIntVars(), 1); // n + 1 = n + cste
            Assert.assertEquals("nb var S", s2.getNbConstants(),1);
            Assert.assertTrue("One solution BB",s.solve());
            Assert.assertTrue("One solution S",s2.solve());
            Assert.assertEquals("Nb node BB",s.getSearchStrategy().getNodeCount(), 1);
            Assert.assertEquals("Nb node S",s2.getSearchStrategy().getNodeCount(), 1);
            LOGGER.info(String.format("BlackBox:%d / Solver:%d", (t2 - t1), (t3 - t2)));
        }
    }


    @Test
    public void testMixedEqualitiesDetection() {
        for (int k = 0; k < 10; k++) {
            CPModel m = new CPModel();
            Solver ps = new PreProcessCPSolver();
            Solver s = new CPSolver();
            int n = 1000;
            IntegerVariable[] vars = Choco.makeIntVarArray("v", n, 0, n - 1);

            for (int i = 0; i < n/2; i++) {
                m.addConstraint(Choco.eq(vars[i], vars[i + 1]));
            }
            m.addConstraint(Choco.leq(vars[n/2], vars[(n/2)+1]));
            for (int i = (n/2)+1; i < n-1; i++) {
                m.addConstraint(Choco.eq(vars[i], vars[i + 1]));
            }
            long t1 = System.currentTimeMillis();
            ModelDetectorFactory.run(m, ModelDetectorFactory.intVarEqDet(m));
            s.read(m);
            long t2 = System.currentTimeMillis();

            ps.read(m);
            long t3 = System.currentTimeMillis();

            Assert.assertEquals("nb var S", s.getNbIntVars(), 2);
            Assert.assertTrue("One solution S",s.solve());
//            Assert.assertEquals("Nb node S",s.getSearchStrategy().getNodeCount(), 3);
            Assert.assertEquals("nb var PS", ps.getNbIntVars(), 2);
            Assert.assertTrue("One solution PS",ps.solve());
//            Assert.assertEquals("Nb node PS",ps.getSearchStrategy().getNodeCount(), 3);
            LOGGER.info(new StringBuilder().append("S:").append(t2 - t1).append(" / PS:").append(t3 - t2).toString());
        }
    }

    @Test
    public void testEqualitiesWithConstante() {
        for (int k = 0; k < 4; k++) {
            CPModel m = new CPModel();
            Solver s = new CPSolver();
            IntegerVariable v1 = makeIntVar("v1", 0, 2);
            IntegerVariable v2 = null;
            Boolean doable = null;
            switch (k){
                case 0:
                    v2 = makeIntVar("v2", 0, 2);
                    doable = true;
                break;
                case 1:
                    v2 = makeIntVar("v2", -1, 3);
                    doable = true;
                    break;
                case 2:
                    v2 = makeIntVar("v2", 1, 1);
                    doable = true;
                    break;
                case 3:
                    v2 = makeIntVar("v2", 4, 6);
                    doable = false;
                    break;
            }
            m.addConstraint(Choco.eq(v1, v2));
            ModelDetectorFactory.run(m, ModelDetectorFactory.intVarEqDet(m));
            s.read(m);
            s.solve();
            Assert.assertEquals("Not expected results", doable, s.isFeasible());

        }
    }

    @Test
    public void detectTasks2(){
        CPModel m;
        CPSolver s;

        m = new CPModel();
        IntegerVariable A = Choco.makeIntVar("A", 0, 10, CPOptions.V_BOUND);
        IntegerVariable B = Choco.makeIntVar("B", 0, 10, CPOptions.V_BOUND);
        IntegerVariable C = Choco.makeIntVar("C", 0, 10, CPOptions.V_BOUND);

        TaskVariable t1 = Choco.makeTaskVar("t2", A, C);
        TaskVariable t2 = Choco.makeTaskVar("t1", A, B, C);

        m.addVariables(t1, t2);
        ModelDetectorFactory.run(m, ModelDetectorFactory.taskVarEqDet(m));

        s = new CPSolver();
        s.read(m);

        Assert.assertEquals(1, s.getNbTaskVars());
        TaskVar tv = s.getTaskVar(0);

        Assert.assertEquals(A.getLowB(), tv.start().getInf());
        Assert.assertEquals(B.getLowB(), tv.duration().getInf());
        Assert.assertEquals(C.getLowB(), tv.end().getInf());

        Assert.assertEquals(A.getUppB(), tv.start().getSup());
        Assert.assertEquals(B.getUppB(), tv.duration().getSup());
        Assert.assertEquals(C.getUppB(), tv.end().getSup());
    }

    @Test
    public void detectTasks3(){
        CPModel m;
        CPSolver s;

        m = new CPModel();
        IntegerVariable A = Choco.makeIntVar("A", 0, 10, CPOptions.V_BOUND);
        IntegerVariable B = Choco.makeIntVar("B", 0, 10, CPOptions.V_BOUND);
        IntegerVariable C = Choco.makeIntVar("C", 0, 10, CPOptions.V_BOUND);

        for(int i = 0; i < 100; i++){
            TaskVariable t = Choco.makeTaskVar("t", A, B, C);
            m.addVariables(t);
        }

        ModelDetectorFactory.run(m, ModelDetectorFactory.taskVarEqDet(m));
        s = new CPSolver();
        s.read(m);

        Assert.assertEquals(1, s.getNbTaskVars());

    }

}
