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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.model.constraints.set;

import choco.Choco;
import static choco.Choco.among;
import choco.Reformulation;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.cp.solver.search.set.MinEnv;
import choco.cp.solver.search.set.RandomSetValSelector;
import choco.cp.solver.search.set.RandomSetVarSelector;
import choco.cp.solver.search.set.StaticSetVarOrder;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.ContradictionException;
import gnu.trove.TIntHashSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;

/**
 * User : cprudhom
 * Mail : cprudhom(a)emn.fr
 * Date : 23 févr. 2010
 * Since : Choco 2.1.1
 */
public class AmongSetTest {

    private static final Logger LOGGER = ChocoLogging.getTestLogger();

    IntegerVariable[] _variables;
    SetVariable[] _svariable;

    private int[] buildValues(Random r, int low, int up) {
        int nb = 1 + r.nextInt(up - low + 1);
        TIntHashSet set = new TIntHashSet(nb);
        for (int i = 0; i < nb; i++) {
            set.add(low + r.nextInt(up - low + 1));
        }
        int[] values = set.toArray();
        Arrays.sort(values);
        return values;
    }

    private IntegerVariable[] buildVars(Random r, int low, int up) {
        int nb = 1 + r.nextInt(up - low + 1);
        IntegerVariable[] vars = new IntegerVariable[nb];
        for (int i = 0; i < nb; i++) {
            vars[i] = Choco.makeIntVar("vars_" + i, buildValues(r, low, up));
        }
        return vars;
    }

    private CPModel[] model(Random r, int size) {
        int low = r.nextInt(size);
        int up = low + r.nextInt(2 * size);
        int[] values = buildValues(r, low, up);
        IntegerVariable[] vars = buildVars(r, low, up);
        int max = 1+ r.nextInt(vars.length);
        int min = max - r.nextInt(max);
        SetVariable S = Choco.makeSetVar("S", values);
        IntegerVariable N = Choco.makeIntVar("N", min, max);

        _variables = ArrayUtils.append(vars, new IntegerVariable[]{N});
        _svariable = new SetVariable[]{S};

        CPModel[] ms = new CPModel[2];
        for (int i = 0; i < ms.length; i++) {
            CPModel m = new CPModel();
            switch (i) {
                case 0:
                    m.addConstraints(Reformulation.among(vars, S, N));
                    break;
                case 1:
                    m.addConstraint(among(vars, S, N));
                    break;
            }
            ms[i] = m;
        }
        return ms;
    }

    private CPSolver solve(int seed, CPModel m, boolean sta_tic) {
        CPSolver s = new CPSolver();
        s.read(m);
        if (sta_tic) {
            s.setVarIntSelector(new StaticVarOrder(s, s.getVar(_variables)));
            s.setValIntIterator(new IncreasingDomain());
            s.setVarSetSelector(new StaticSetVarOrder(s.getVar(_svariable)));
            s.setValSetSelector(new MinEnv(s));
        } else {
            s.setVarIntSelector(new RandomIntVarSelector(s, seed));
            s.setValIntSelector(new RandomIntValSelector(seed));
            s.setVarSetSelector(new RandomSetVarSelector(s, seed));
            s.setValSetSelector(new RandomSetValSelector(seed));
        }
        s.solveAll();
        return s;
    }

    /******************************************************************************************************************/
    /******************************************************************************************************************/
    /**
     * **************************************************************************************************************
     */

    @Test
    public void test0() {
        IntegerVariable[] xs = new IntegerVariable[3];
        xs[0] = Choco.makeIntVar("xs1", 2, 3, "cp:enum");
        xs[1] = Choco.makeIntVar("xs2", 2, 3, "cp:enum");
        xs[2] = Choco.makeIntVar("xs3", 1, 4, "cp:enum");
        SetVariable s = Choco.makeSetVar("S", 2, 3);
        IntegerVariable n = Choco.makeIntVar("N", 2, 2);

        CPModel m = new CPModel();
        m.addConstraint(among(xs, s, n));
        m.addConstraint(Choco.member(s, 2));
        m.addConstraint(Choco.member(s, 3));

        CPSolver so = new CPSolver();

        so.read(m);

        try {
            so.propagate();
        } catch (ContradictionException e) {
            Assert.fail();
        }
        Assert.assertEquals("X3 size", 2, so.getVar(xs[2]).getDomainSize());
        Assert.assertEquals("X3 value - LOW", 1, so.getVar(xs[2]).getInf());
        Assert.assertEquals("X3 value - UPP", 4, so.getVar(xs[2]).getSup());

    }

    @Test
    public void test01() {
        IntegerVariable[] xs = new IntegerVariable[3];
        xs[0] = Choco.makeIntVar("xs1", 2, 3, "cp:enum");
        xs[1] = Choco.makeIntVar("xs2", 2, 3, "cp:enum");
        xs[2] = Choco.makeIntVar("xs3", 2, 4, "cp:enum");
        SetVariable s = Choco.makeSetVar("S", 2, 3);
        IntegerVariable n = Choco.makeIntVar("N", 2, 3);

        CPModel m = new CPModel();
        m.addConstraints(Reformulation.among(xs, s, n));
//        m.addConstraint(among(xs, s, n));

        CPSolver so = new CPSolver();

        so.read(m);
        so.solveAll();
        Assert.assertEquals(22, so.getSolutionCount());
    }


    @Test
    public void test02() {
        IntegerVariable[] xs = new IntegerVariable[2];
        xs[0] = Choco.makeIntVar("xs1", 2, 2, "cp:enum");
        xs[1] = Choco.makeIntVar("xs2", 2, 3, "cp:enum");
        SetVariable s = Choco.makeSetVar("S", 3, 3);
        IntegerVariable n = Choco.makeIntVar("N", 1, 2);

        CPModel m = new CPModel();
//        m.addConstraints(among_ref(xs, s, n));
        m.addConstraint(among(xs, s, n));

        CPSolver so = new CPSolver();

        so.read(m);
        so.solveAll();
        Assert.assertEquals(1, so.getSolutionCount());
    }

    @Test
    public void test03() {
        IntegerVariable[] xs = new IntegerVariable[3];
        xs[0] = Choco.makeIntVar("xs1", new int[]{0, 1, 3}, "cp:enum");
        xs[1] = Choco.makeIntVar("xs2", 2, 2, "cp:enum");
        xs[2] = Choco.makeIntVar("xs2", 5, 5, "cp:enum");
        SetVariable s = Choco.makeSetVar("S", new int[]{1, 3, 5});
        IntegerVariable n = Choco.makeIntVar("N", 2, 2);

        CPModel m = new CPModel();
//        m.addConstraints(among_ref(xs, s, n));
        m.addConstraint(among(xs, s, n));
//        m.addConstraint(Choco.member(s, 5));

        CPSolver so = new CPSolver();

        so.read(m);
        so.solveAll();
        Assert.assertEquals(4, so.getSolutionCount());
    }

    @Test
    public void test1() {
        IntegerVariable[] xs = new IntegerVariable[8];
        xs[0] = Choco.makeIntVar("xs1", 1, 2, "cp:enum");
        xs[1] = Choco.makeIntVar("xs2", 1, 2, "cp:enum");
        xs[2] = Choco.makeIntVar("xs3", 3, 3, "cp:enum");
        xs[3] = Choco.makeIntVar("xs3", 3, 3, "cp:enum");
        xs[4] = Choco.makeIntVar("xs3", 4, 4, "cp:enum");
        xs[5] = Choco.makeIntVar("xs3", 4, 4, "cp:enum");
        xs[6] = Choco.makeIntVar("xs3", 5, 5, "cp:enum");
        xs[7] = Choco.makeIntVar("xs3", 5, 5, "cp:enum");

        SetVariable s = Choco.makeSetVar("S", 1, 5);
        IntegerVariable n = Choco.makeIntVar("N", 5, 8);

        CPModel m = new CPModel();
        m.addConstraint(among(xs, s, n));
        m.addConstraint(Choco.member(s, 1));
        m.addConstraint(Choco.member(s, 2));

        CPSolver so = new CPSolver();

        so.read(m);

        try {
            so.propagate();
        } catch (ContradictionException e) {
            Assert.fail();
        }
        Assert.assertEquals("X1 size", 2, so.getVar(xs[0]).getDomainSize());
        Assert.assertEquals("X2 size", 2, so.getVar(xs[1]).getDomainSize());
        Assert.assertEquals("S size", 5, so.getVar(s).getEnveloppeDomainSize());
        Assert.assertEquals("N size", 4, so.getVar(n).getDomainSize());
    }

    @Test
    public void test2() {
        IntegerVariable[] xs = new IntegerVariable[6];
        xs[0] = Choco.makeIntVar("xs1", 1, 2, "cp:enum");
        xs[1] = Choco.makeIntVar("xs2", 1, 2, "cp:enum");
        xs[2] = Choco.makeIntVar("xs3", 3, 3, "cp:enum");
        xs[3] = Choco.makeIntVar("xs3", 3, 3, "cp:enum");
        xs[4] = Choco.makeIntVar("xs3", 4, 4, "cp:enum");
        xs[5] = Choco.makeIntVar("xs3", 4, 4, "cp:enum");
        SetVariable s = Choco.makeSetVar("S", 1, 4);
        IntegerVariable n = Choco.makeIntVar("N", 2, 3);

        CPModel m = new CPModel();
        m.addConstraint(among(xs, s, n));
        m.addConstraint(Choco.member(s, 1));
        m.addConstraint(Choco.member(s, 2));

        CPSolver so = new CPSolver();

        so.read(m);

        try {
            so.propagate();
        } catch (ContradictionException e) {
            Assert.fail();
        }
        Assert.assertEquals("S size", 2, so.getVar(s).getEnveloppeDomainSize());
        Assert.assertEquals("S value - LOW", 1, so.getVar(s).getEnveloppeInf());
        Assert.assertEquals("S value - UPP", 2, so.getVar(s).getEnveloppeSup());
    }


    @Test
    public void testRandom1() {
        Random r;
        for (int i = 0; i < 500; i++) {
            r = new Random(i);
            CPModel[] ms = model(r, 4);
            CPSolver[] ss = new CPSolver[ms.length];
            for (int j = 0; j < ms.length; j++) {
                ss[j] = solve(i, ms[j], false);
            }
            for (int j = 1; j < ms.length; j++) {
                Assert.assertEquals("nb solutions, seed:" + i, ss[0].getSolutionCount(), ss[j].getSolutionCount());
//                  See article for more explanation, but some examples can be found where the algo is better than BC
//                  And others where the algo is worse than BC.
//                  So no nodes comparisons
//                  if(st > 0){
//                        Assert.assertEquals("nb nodes, seed:"+i, ss[0].getNodeCount(), ss[j].getNodeCount());
//                   }
            }
        }
    }
}
