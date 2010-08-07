package choco.model.constraints.global;

import static choco.Choco.*;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.model.constraints.automaton.FA.FiniteAutomaton;
import choco.kernel.model.variables.integer.IntegerVariable;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Feb 5, 2010
 * Time: 6:00:16 PM
 */
public class CostRegularTest {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();

    CPModel m;
    CPSolver s;

    @Before
    public void before() {
        m = new CPModel();
        s = new CPSolver();
    }

    @After
    public void after() {
        m = null;
        s = null;
    }

    @Test
    public void testSimpleAuto() {
        IntegerVariable[] vars = makeIntVarArray("x",10,0,2, Options.V_ENUM);
        IntegerVariable z = makeIntVar("z",3,4, Options.V_BOUND);
        int n = vars.length;

        FiniteAutomaton auto = new FiniteAutomaton();
        int start = auto.addState();
        int end = auto.addState();
        auto.setInitialState(start);
        auto.setFinal(start);
        auto.setFinal(end);

        auto.addTransition(start,start, 0,1);
        auto.addTransition(start,end,2);

        auto.addTransition(end,start,2);
        auto.addTransition(end,start, 0,1);

        int[][][] costs = new int[n][3][2];
        for (int i = 0 ; i < costs.length ; i++)
        {
            costs[i][0][1] = 1;
            costs[i][1][1] = 1;
        }

        m.addConstraint(costRegular(z, vars, auto,costs));

        s.read(m);

        s.solveAll();
        assertEquals(9280,s.getNbSolutions());
    }

    @Test
    public void ccostregular2(){
        IntegerVariable[] vars = makeIntVarArray("x", 28, 0, 2, Options.V_ENUM);
        IntegerVariable z = makeIntVar("z", 0, 4, Options.V_BOUND);

        // different rules are formulated as patterns that must NOT be matched by x
        List<String> forbiddenRegExps = new ArrayList<String>();
        // do not end with '00' if start with '11'
        forbiddenRegExps.add("11(0|1|2)*00");
        // at most three consecutive 0
        forbiddenRegExps.add("(0|1|2)*0000(0|1|2)*");
        // no pattern '112' at position 5
        forbiddenRegExps.add("(0|1|2){4}112(0|1|2)*");
        // pattern '12' after a 0 or a sequence of 0
        forbiddenRegExps.add("(0|1|2)*02(0|1|2)*");
        forbiddenRegExps.add("(0|1|2)*01(0|1)(0|1|2)*");
        // at most three 2 on consecutive even positions
        forbiddenRegExps.add("(0|1|2)((0|1|2)(0|1|2))*2(0|1|2)2(0|1|2)2(0|1|2)*");

        // a unique automaton is built as the complement language composed of all the forbidden patterns
        FiniteAutomaton auto = new FiniteAutomaton();
        for (String reg : forbiddenRegExps) {
            FiniteAutomaton a = new FiniteAutomaton(reg);
            auto = auto.union(a);
            auto.minimize();
        }
        auto = auto.complement();
        auto.minimize();
        assertEquals(54, auto.getNbStates());
        // costs
        int[][] costs = new int[vars.length][3];
        for (int i = 1 ; i < costs.length ; i+=2) {
            costs[i][0] = 1; costs[i][1] = 1;
        }

        m.addConstraint(costRegular(z, vars, auto, costs));
        s.read(m);
        s.solveAll();
        assertEquals(229376, s.getSolutionCount());
    }


    @Test
    public void isCorrect()  {

        IntegerVariable[] vars = makeIntVarArray("x",12,0,2, Options.V_ENUM);
        IntegerVariable z = makeIntVar("z",10,10, Options.V_BOUND);
        int n = vars.length;

        FiniteAutomaton auto = new FiniteAutomaton();
        int start = auto.addState();
        int end = auto.addState();
        auto.setInitialState(start);
        auto.setFinal(start);
        auto.setFinal(end);

        auto.addTransition(start,start, 0,1);
        auto.addTransition(start,end,2);

        auto.addTransition(end,start,2);
        auto.addTransition(end,start, 0,1);

        int[][][] costs = new int[n][3][2];
        for (int i = 0 ; i < costs.length ; i++)
        {
            for (int k = 0 ; k < 2 ; k++)
            {
                costs[i][0][k] = 1;
                costs[i][1][k] = 1;
            }
        }

        m.addConstraint(costRegular(z, vars, auto,costs));

        s.read(m);

        s.setVarIntSelector(new StaticVarOrder(s, s.getVar(ArrayUtils.append(new IntegerVariable[]{z},vars))));

        s.solveAll();
        assertEquals(67584,s.getNbSolutions());
        assertEquals(124927,s.getNodeCount());

    }

  //      @Test
  /*  public void isCorrectWithOldCostReg()
    {

        long time = System.currentTimeMillis();
        IntegerVariable[] vars = makeIntVarArray("x",12,0,2,CPOptions.V_ENUM);
        IntegerVariable z = makeIntVar("z",10,10,CPOptions.V_BOUND);
        int n = vars.length;

        Automaton auto = new Automaton();
        int start = auto.addState();
        int end = auto.addState();
        auto.setInitialState(start);
        auto.setFinal(start);
        auto.setFinal(end);

        auto.addTransition(start,start,new int[]{0,1});
        auto.addTransition(start,end,2);

        auto.addTransition(end,start,2);
        auto.addTransition(end,start,new int[]{0,1});

        int[][] costs = new int[n][3];
        for (int i = 0 ; i < costs.length ; i++)
        {
                costs[i][0] = 1;
                costs[i][1] = 1;
        }

        m.addConstraint(costRegular(vars,z,auto,costs));

        s.read(m);

        s.setVarIntSelector(new StaticVarOrder(s.getVar(ArrayUtils.append(new IntegerVariable[]{z},vars))));

        s.solveAll();
        assertEquals(67584,s.getNbSolutions());
        assertEquals(124927,s.getNodeCount());

    } */

     @Test
    public void isCorrect2()
    {

        IntegerVariable[] vars = makeIntVarArray("x",13,0,2, Options.V_ENUM);
        IntegerVariable z = makeIntVar("z",4,6, Options.V_BOUND);
        int n = vars.length;

        FiniteAutomaton auto = new FiniteAutomaton();
        int start = auto.addState();
        int end = auto.addState();
        auto.setInitialState(start);
        auto.setFinal(start);
        auto.setFinal(end);

        auto.addTransition(start,start, 0,1);
        auto.addTransition(start,end,2);

        auto.addTransition(end,start,2);
        auto.addTransition(end,start, 0,1);

        int[][][] costs = new int[n][3][2];
        for (int i = 0 ; i < costs.length ; i++)
        {
            costs[i][0][1] = 1;

            costs[i][1][1] = 1;


        }

        m.addConstraint(costRegular(z, vars, auto,costs));

        s.read(m);

        s.setVarIntSelector(new StaticVarOrder(s, s.getVar(ArrayUtils.append(new IntegerVariable[]{z},vars))));

        s.solveAll();


    }


    @Test
    public void compareVersionSpeedNew()
    {
        int n = 14;
        FiniteAutomaton auto = new FiniteAutomaton("(0|1|2)*(0|1)(0|1)(0|1)(0|1|2)*");

        int[][] c1 = new int[n][3];
        int[][][] c2 = new int[n][3][auto.getNbStates()];
        for (int i = 0 ; i < n ; i++)
        {
            for (int k = 0 ; k < auto.getNbStates() ; k++)
            {
                c1[i][0] = 1;
                c1[i][1] = 2;

                c2[i][0][k] = 1;
                c2[i][1][k] = 2;
            }
        }

        IntegerVariable[] v2 = makeIntVarArray("x",n,0,2, Options.V_ENUM);
        IntegerVariable z2 = makeIntVar("z",n/2,n/2+1, Options.V_BOUND);

        m.addConstraint(costRegular(z2, v2, auto,c2));



        s.read(m);



        s.solveAll();



    }

  /*      @Test
    public void compareVersionSpeedOld()
    {
        int n = 14;
        Automaton auto = new Automaton("(0|1|2)*(0|1)(0|1)(0|1)(0|1|2)*");

        int[][] c1 = new int[n][3];
        double[][][] c2 = new double[n][3][auto.getNbStates()];
        for (int i = 0 ; i < n ; i++)
        {
            for (int k = 0 ; k < auto.getNbStates() ; k++)
            {
                c1[i][0] = 1;
                c1[i][1] = 2;

                c2[i][0][k] = 1.0;
                c2[i][1][k] = 2.0;
            }
        }

        IntegerVariable[] v1 = makeIntVarArray("x",n,0,2,CPOptions.V_ENUM);
        IntegerVariable z1 = makeIntVar("z",n/2,n/2+1,CPOptions.V_BOUND);

        m.addConstraint(costRegular(v1,z1,auto,c1));


        s.read(m);

        s.solveAll();



    }  */





}
