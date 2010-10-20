/* ************************************************
*           _      _                             *
*          |  (..)  |                            *
*          |_ J||L _|         CHOCO solver       *
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
*                   N. Jussien    1999-2010      *
**************************************************/
package common;

import choco.Choco;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.branching.AssignOrForbidIntVarVal;
import choco.cp.solver.search.integer.valselector.MinVal;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.cp.solver.variables.integer.AbstractIntDomain;
import choco.cp.solver.variables.integer.IntDomainVarImpl;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.tools.StatisticUtils;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.trailing.EnvironmentTrailing;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import gnu.trove.TLongArrayList;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 27 juil. 2010
 */
public class KnapsackTest {

    static TLongArrayList times = new TLongArrayList();

    int[] capacites = {0, 34};
    int[] energies = {6, 4, 3};
    int[] volumes = {7, 5, 2};
    int[] nbOmax = {4, 6, 17};
    int n = 3;


    private void generate(int n) {
        Random r = new Random();

        capacites = new int[2];
        capacites[1] = 60 + r.nextInt(15);

        energies = new int[n];
        volumes = new int[n];
        nbOmax = new int[n];
        for (int i = 0; i < n; i++) {
            energies[i] = 1 + r.nextInt(10);
            volumes[i] = 1 + r.nextInt(10);
            nbOmax[i] = capacites[1] / volumes[i];
        }
//        System.out.println(n);
//        System.out.println(Arrays.toString(capacites));
//        System.out.println(Arrays.toString(energies));
//        System.out.println(Arrays.toString(volumes));
//        System.out.println(Arrays.toString(nbOmax));
    }


    private void parse(String fileName, int n) throws IOException {
        URL url = this.getClass().getResource(fileName);
        ParseurKS.parseFile(url.getFile(), n);

//        Arrays.sort(ParseurKS.bounds);
        capacites = ParseurKS.bounds;
        energies = ParseurKS.instances[0];
        volumes = ParseurKS.instances[1];
        this.n = volumes.length;
        nbOmax = new int[this.n];
        for (int i = 0; i < this.n; i++) {
            nbOmax[i] = capacites[1] / volumes[i];
        }

//        System.out.println(this.n);
//        System.out.println(Arrays.toString(capacites));
//        System.out.println(Arrays.toString(energies));
//        System.out.println(Arrays.toString(volumes));
//        System.out.println(Arrays.toString(nbOmax));

    }


    public void modelIt(boolean opt) throws IOException {
        int nos = energies.length;

        IEnvironment env = new EnvironmentTrailing();

        IntegerVariable[] objects = new IntegerVariable[nos];
        int i = 0;
        for (; i < nos; i++) {
            objects[i] = Choco.makeIntVar("v" + i, 0, nbOmax[i], Options.V_ENUM);
        }

        IntegerVariable power = Choco.makeIntVar("v" + (i++), 0, 999999, Options.V_BOUND);

        IntegerVariable scalar = Choco.makeIntVar("v" + (i++), capacites[0] - 1, capacites[1] + 1, Options.V_ENUM);

        List<Constraint> lcstrs = new ArrayList<Constraint>(3);

        lcstrs.add(Choco.eq(Choco.scalar(objects, volumes), scalar));
//        lcstrs.add(Choco.geq(scalar, capacites[0]));
//        lcstrs.add(Choco.leq(scalar, capacites[1]));
        lcstrs.add(Choco.eq(Choco.scalar(objects, energies), power));

        Model model = new CPModel();
        model.addConstraints(lcstrs.toArray(new Constraint[lcstrs.size()]));

        IntegerVariable[] vars = new IntegerVariable[nos + 2];
        System.arraycopy(objects, 0, vars, 0, nos);
        vars[nos] = power;
        vars[nos + 1] = scalar;
        Solver s = new CPSolver();
        s.read(model);
//        s.attachGoal(BranchingFactory.incDomWDeg(s, s.getVar(objects), new IncreasingDomain(), 0));
        s.attachGoal(new AssignOrForbidIntVarVal(new StaticVarOrder(s, s.getVar(objects)), new MinVal()));
//        s.setTimeLimit(20000);

//        System.out.printf("%s\n", s.pretty());
        ChocoLogging.setEveryXNodes(Integer.MAX_VALUE);
        ChocoLogging.toVerbose();
        if(opt){
            s.maximize(s.getVar(power), false);
        }else{
            s.solveAll();
        }
        times.add(s.getTimeCount());
//        System.out.println("down:" + AbstractSearchLoop.d);
//        System.out.printf("a:%d, b:%d, c:%d\n", IntLinComb.a, IntLinComb.b, IntLinComb.c);
    }

    public static void main(String[] args) throws IOException {
        KnapsackTest ks = new KnapsackTest();
        ChocoLogging.toSilent();
//        ks.generate(3);
//        for (int i = 0; i < 5; i++) {
//            ks.parse("../files/knapsack.20-1.txt");
        for (int j = 6; j < 15; j++) {
            for (int i = 1; i < 11; i++) {
                ks.parse("../files/knapsack.20-1.txt", j);
                ks.modelIt(false);
//                ChocoLogging.flushLogs();
            }
            long[] values = StatisticUtils.prepare(times.toNativeArray());
            times.clear();
            System.out.printf("j = %d\n", j);
            System.out.printf("Moyenne: %f\n", StatisticUtils.mean(values));
            System.out.printf("Ecart-type: %f\n", StatisticUtils.standarddeviation(values));
//        System.out.printf("inst: %f (%d)\n", StatisticUtils.mean(IntLinComb.a.toNativeArray()), IntLinComb.a.size());
//        System.out.printf("low: %f (%d)\n", StatisticUtils.mean(IntLinComb.b.toNativeArray()), IntLinComb.b.size());
//        System.out.printf("upp: %f (%d)\n", StatisticUtils.mean(IntLinComb.c.toNativeArray()), IntLinComb.c.size());
        }
//        }


    }

    @Test
    public void testALL10() throws IOException {
        KnapsackTest ks = new KnapsackTest();
        for(int i = 0; i < 10; i++){
            ks.parse("../files/knapsack.13-1.txt", 13);
            ks.modelIt(true);
            ChocoLogging.flushLogs();
        }

        long[] t = StatisticUtils.prepare(times.toNativeArray());
        
        System.out.printf("std : %f", StatisticUtils.mean(t));
        System.out.printf("std : %f", StatisticUtils.standarddeviation(t));

    }

    @Test
    public void testOPT13() throws IOException {
        KnapsackTest ks = new KnapsackTest();
        ks.parse("../files/knapsack.13-1.txt", 13);
        ks.modelIt(true);
        System.out.printf("i:%d, r:%d, l:%d, u:%d\n", IntDomainVarImpl.inst, IntDomainVarImpl.rem,
                IntDomainVarImpl.low, IntDomainVarImpl.upp);
        System.out.printf("i:%d, r:%d, l:%d, u:%d\n", AbstractIntDomain.instE, AbstractIntDomain.remE,
                AbstractIntDomain.lowE, AbstractIntDomain.uppE);
//        Assert.assertTrue(s.getSearchLoop().getTimeCount() < 15000, "time spent > 15000 ms" );
    }

}
