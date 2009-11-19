package choco.cp.solver.constraints.global.ordered;

import static choco.Choco.makeIntVar;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.ComponentConstraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: thierry.petit(a)emn.fr
 * Date: 9 nov. 2009
 * Time: 18:03:32
 * Test of OrderedGcc
 */
public class OrderedGccTest {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();
    public static boolean debug = false;

    @Test
    public void testIsSatisfiedTrue() {
        Model m = new CPModel();
		IntegerVariable[] vars = new IntegerVariable[4];
		vars[0] = makeIntVar("var0 ", 1, 5);
		vars[1] = makeIntVar("var1 ", 2, 5);
		vars[2] = makeIntVar("var2 ", 0, 3);
		vars[3] = makeIntVar("var3 ", 1, 5);
        int minValue = 0;
		int maxValue = 5;
		int[] Imax1 = new int[maxValue-minValue+1];
		Imax1[0] = vars.length;
		Imax1[1] = 3;
		Imax1[2] = 2;
		Imax1[3] = 2;
		Imax1[4] = 1;
		Imax1[5] = 0;
        int minBot = 1;
	    Object[] p = new Object[2];
	    //p[0] = vars;
	    p[0] = Imax1;
	    p[1] = minBot;
	    ComponentConstraint c = new ComponentConstraint(OrderedGccManager.class,(Object) p,vars);
        m.addConstraint(c);
        CPSolver s = new CPSolver();
        s.read(m);
        assertTrue(s.getCstr(c).isSatisfied());
    }

    @Test
    public void testIsSatisfiedFalse() {
        Model m = new CPModel();
		IntegerVariable[] vars = new IntegerVariable[4];
		vars[0] = makeIntVar("var0 ", 1, 5);
		vars[1] = makeIntVar("var1 ", 2, 5);
		vars[2] = makeIntVar("var2 ", 0, 3);
		vars[3] = makeIntVar("var3 ", 1, 5);
        int minValue = 0;
		int maxValue = 5;
		int[] Imax1 = new int[maxValue-minValue+1];
		Imax1[0] = vars.length;
		Imax1[1] = 3;
		Imax1[2] = 2;
		Imax1[3] = 2;
		Imax1[4] = 1;
		Imax1[5] = 0;
        int minBot = 2;
	    Object[] p = new Object[2];
	    p[0] = Imax1;
	    p[1] = minBot;
	    ComponentConstraint c = new ComponentConstraint(OrderedGccManager.class,(Object) p,vars);
        m.addConstraint(c);
        CPSolver s = new CPSolver();
        s.read(m);
        assertFalse(s.getCstr(c).isSatisfied());
  }

     @Test
    public void testSolution1() {
        Model m = new CPModel();
		IntegerVariable[] vars = new IntegerVariable[4];
		vars[0] = makeIntVar("var0 ", 1, 5);
		vars[1] = makeIntVar("var1 ", 2, 5);
		vars[2] = makeIntVar("var2 ", 0, 3);
		vars[3] = makeIntVar("var3 ", 1, 5);
        int minValue = 0;
		int maxValue = 5;
		int[] Imax1 = new int[maxValue-minValue+1];
		Imax1[0] = vars.length;
		Imax1[1] = 3;
		Imax1[2] = 1;
		Imax1[3] = 0;
		Imax1[4] = 0;
		Imax1[5] = 0;
        int minBot = 1;
	    Object[] p = new Object[2];
	    p[0] = Imax1;
	    p[1] = minBot;
	    ComponentConstraint c = new ComponentConstraint(OrderedGccManager.class,(Object) p,vars);
        m.addConstraint(c);
        CPSolver s = new CPSolver();
        s.read(m);
        s.solve();
        // minBot = 1 => at least one 0
        assertTrue(s.getVar(vars[2]).getVal()==0);
        // Imax[2] = 1 => var0 == 1 && var3 == 1
        assertTrue((s.getVar(vars[0]).getVal()==1) && (s.getVar(vars[3]).getVal()==1));
        // Imax[3] = 0 => var1 == 2
        assertTrue(s.getVar(vars[1]).getVal()==2);
    }

     @Test
    public void testNumberOfSolutions1() {
        Model m = new CPModel();
		IntegerVariable[] vars = new IntegerVariable[4];
		vars[0] = makeIntVar("var0 ", 1, 5);
		vars[1] = makeIntVar("var1 ", 2, 5);
		vars[2] = makeIntVar("var2 ", 0, 3);
		vars[3] = makeIntVar("var3 ", 1, 5);
        int minValue = 0;
		int maxValue = 5;
		int[] Imax1 = new int[maxValue-minValue+1];
		Imax1[0] = vars.length;
		Imax1[1] = 3;
		Imax1[2] = 1;
		Imax1[3] = 1;
		Imax1[4] = 0;
		Imax1[5] = 0;
        int minBot = 1;
        // nombre de solutions = 2
        // 1 2 0 1, 1 3 0 1
	    Object[] p = new Object[2];
	    p[0] = Imax1;
	    p[1] = minBot;
	    ComponentConstraint c = new ComponentConstraint(OrderedGccManager.class,(Object) p,vars);
        m.addConstraint(c);
        CPSolver s = new CPSolver();
        s.read(m);
        s.solveAll();
        assertTrue(s.getNbSolutions()==2);
    }

    @Test
       public void testNumberOfSolutions2() {
           Model m = new CPModel();
           IntegerVariable[] vars = new IntegerVariable[4];
           vars[0] = makeIntVar("var0 ", -1, 3);
           vars[1] = makeIntVar("var1 ", 0, 3);
           vars[2] = makeIntVar("var2 ", -2, 1);
           vars[3] = makeIntVar("var3 ", -1, 3);
           int minValue = -2;
           int maxValue = 3;
           int[] Imax1 = new int[maxValue-minValue+1];
           Imax1[0] = vars.length;
           Imax1[1] = 3;
           Imax1[2] = 1;
           Imax1[3] = 1;
           Imax1[4] = 0;
           Imax1[5] = 0;
           int minBot = 1;
           // nombre de solutions = 2
           // -1 0 -2 -1, -1 1 -2 -1
           Object[] p = new Object[2];
           p[0] = Imax1;
           p[1] = minBot;
           ComponentConstraint c = new ComponentConstraint(OrderedGccManager.class,(Object) p,vars);
           m.addConstraint(c);
           CPSolver s = new CPSolver();
           s.read(m);
           s.solveAll();
           assertTrue(s.getNbSolutions()==2);
       }

    @Test
        public void testNumberOfSolutions3() {
            Model m = new CPModel();
            IntegerVariable[] vars = new IntegerVariable[4];
            vars[0] = makeIntVar("var0 ", 1, 2);
            vars[1] = makeIntVar("var1 ", 1, 5);
            vars[2] = makeIntVar("var2 ", 0, 3);
            vars[3] = makeIntVar("var3 ", 1, 5);
            int minValue = 0;
            int maxValue = 5;
            int[] Imax1 = new int[maxValue-minValue+1];
            Imax1[0] = vars.length;
            Imax1[1] = 3;
            Imax1[2] = 1;
            Imax1[3] = 1;
            Imax1[4] = 0;
            Imax1[5] = 0;
            int minBot = 1;
            // nombre de solutions = 6
            // 1 1 0 1, 2 1 0 1, 1 2 0 1, 1 1 0 2,
            // 1 3 0 1, 1 1 0 3,
            Object[] p = new Object[2];
            p[0] = Imax1;
            p[1] = minBot;
            ComponentConstraint c = new ComponentConstraint(OrderedGccManager.class,(Object) p,vars);
            m.addConstraint(c);
            CPSolver s = new CPSolver();
            s.read(m);
            s.solve();
            if(debug) {
                System.out.println(s.pretty());
            }
            while(s.nextSolution()){
                  if(debug) {
                      System.out.println(s.pretty());
                  }
            }
            assertTrue(s.getNbSolutions()==6);
        }

     @Test
        public void testNumberOfSolutions4() {
            Model m = new CPModel();
            IntegerVariable[] vars = new IntegerVariable[4];
            vars[0] = makeIntVar("var0 ", 0, 1);
            vars[1] = makeIntVar("var1 ", 0, 4);
            vars[2] = makeIntVar("var2 ", -1, 2);
            vars[3] = makeIntVar("var3 ", 0, 4);
            int minValue = -1;
            int maxValue = 4;
            int[] Imax1 = new int[maxValue-minValue+1];
            Imax1[0] = vars.length;
            Imax1[1] = 3;
            Imax1[2] = 1;
            Imax1[3] = 1;
            Imax1[4] = 0;
            Imax1[5] = 0;
            int minBot = 1;
            // nombre de solutions = 6
            // 0 0 -1 0, 1 0 -1 0, 0 1 -1 0, 0 0 -1 1,
            // 0 2 -1 0, 0 0 -1 2,
            Object[] p = new Object[2];
            p[0] = Imax1;
            p[1] = minBot;
            ComponentConstraint c = new ComponentConstraint(OrderedGccManager.class,(Object) p,vars);
            m.addConstraint(c);
            CPSolver s = new CPSolver();
            s.read(m);
            s.solve();
            if(debug) {
                System.out.println(s.pretty());
            }
            while(s.nextSolution()){
                 if(debug) {
                      System.out.println(s.pretty());
                 }
            }
            assertTrue(s.getNbSolutions()==6);
        }



}
