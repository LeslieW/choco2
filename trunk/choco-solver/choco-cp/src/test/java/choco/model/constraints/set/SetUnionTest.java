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
package choco.model.constraints.set;

import choco.Choco;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.set.RandomSetValSelector;
import choco.cp.solver.search.set.RandomSetVarSelector;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;

import static choco.Choco.*;
import static junit.framework.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 29 janv. 2008
 * Time: 17:38:34
 */
public class SetUnionTest {

    protected final static Logger LOGGER = ChocoLogging.getTestLogger();
    Model m;
    Solver s;

    @After
    public void tearDown() throws Exception {
        LOGGER.info(s.pretty());
        s = null;
        m = null;
    }

    @Before
    public void setUp() throws Exception {
        m = new CPModel();
        s = new CPSolver();
    }

    @Test
    public void test() {
        for (int seed = 0; seed < 20; seed++) {
            m = new CPModel();
            s = new CPSolver();
            SetVariable v1 = makeSetVar("v1", 4, 6);
            SetVariable v2 = makeSetVar("v2", 3, 5);
            SetVariable v3 = makeSetVar("v3", 1, 6);
            SetVariable v4 = makeSetVar("v4", 4, 4);

            m.addConstraint(setDisjoint(v1, v4));
            m.addConstraint(eqCard(v4, 1));
            Constraint c1 = setUnion(v1, v2, v3);
            m.addConstraint(c1);
            s.read(m);
            s.setVarSetSelector(new RandomSetVarSelector(s, seed));
            s.setValSetSelector(new RandomSetValSelector(seed + 1));
            s.solveAll();
            assertEquals(32, s.getNbSolutions());
        }

    }

    private int[][] buildEdges(Random r, int nbVertices) {

        int maxEdges = ((nbVertices - 1) * (nbVertices))/2;

        int nbEdges = maxEdges / 2 + r.nextInt(maxEdges/2);

        int[][] edges = new int[nbEdges][2];
        int[][] sets = new int[nbVertices][nbVertices];
        int i = 0;
        while (i < nbEdges) {
            int c1;
            int c2;
            do {
                c1 = r.nextInt(nbVertices);
                c2 = r.nextInt(nbVertices);
            } while (c1 == c2 || sets[c1][c2] == 1);
            sets[c1][c2] = 1;
            sets[c2][c1] = 1;
            if(c1<c2){
                edges[i][0] = c1;
                edges[i][1] = c2;
            }else{
                edges[i][0] = c2;
                edges[i][1] = c1;
            }
            i++;
        }
        return edges;
    }

    @Test
    public void pprossertest() {
        Random r = new Random();
        ChocoLogging.toQuiet();
        for (int i = 3; i < 20; i++) {
            for (int b = 0; b < 20; b++) {
                System.out.printf("%d - %d\n", i, b);
                int[][] pairs = buildEdges(r, i);

                CPModel m1 = new CPModel();
                IntegerVariable[] v = makeIntVarArray("v", i, 0, i-1); // v[i] = j <-> ith vertex takes colour j
                for (int p = 0; p < pairs.length; p++) {
                    m1.addConstraint(neq(v[pairs[p][0]], v[pairs[p][1]]));
                }
                IntegerVariable[] colors = makeIntVarArray("colors", i, 0, i-1);
                IntegerVariable nbColors1 = Choco.makeIntVar("nb colors", 0, i -1, Options.V_BOUND, Options.V_OBJECTIVE);
                IntegerVariable nbZero1 = Choco.makeIntVar("zero", 0, i, Options.V_BOUND);

                m1.addConstraint(globalCardinality(v, colors, 0));
                m1.addConstraint(occurrence(0, nbZero1, colors));
                m1.addConstraint(eq(nbColors1, minus(i, nbZero1)));


                CPModel m2 = new CPModel();
                SetVariable[] c = makeSetVarArray("c", i, 0, i-1); // i in c[j] <-> ith vertex takes colour j
                IntegerVariable[] ccards = new IntegerVariable[i];
                for(int j = 0; j < i; j++){
                    ccards[j] = c[j].getCard();
                }
                SetVariable S = makeSetVar("S", 0, i-1); // the set of all variables, to be coloured

                for (int p = 0; p < pairs.length; p++) {
                    SetVariable edgeP = constant(pairs[p]);
                    for(int cs = 0; cs < i; cs++){
                        m2.addConstraint(isNotIncluded(edgeP, c[cs]));
                    }
                }

                m2.addConstraint(setDisjoint(c));

                m2.addConstraint(setUnion(c, S)); // all variables must be coloured

                m2.addConstraint(eqCard(S, i));   // all variables must be coloured

                IntegerVariable nbZero2 = Choco.makeIntVar("nb 0", 0, i, Options.V_BOUND);
                IntegerVariable nbColors2 = Choco.makeIntVar("nb colors 2", 1, i, Options.V_BOUND, Options.V_OBJECTIVE);
                m2.addConstraint(occurrence(0, nbZero2, ccards));
                m2.addConstraint(eq(nbColors2, minus(i, nbZero2)));

                CPSolver s1 = new CPSolver();
                s1.read(m1);
                s1.minimize(true);

                CPSolver s2 = new CPSolver();
                s2.read(m2);
//                s2.setVarSetSelector(new MinDomSet(s2, s2.getVar(c)));
                s2.minimize(true);

                Assert.assertEquals(Arrays.toString(ArrayUtils.flatten(pairs))+'\n'+s1.pretty()+'\n'+s2.pretty(), s1.isFeasible(), s2.isFeasible());
                Assert.assertEquals(Arrays.toString(ArrayUtils.flatten(pairs))+'\n'+s1.pretty()+'\n'+s2.pretty(),s1.getVar(nbColors1).getVal(), s2.getVar(nbColors2).getVal());
            }
        }

    }

    @Test
    public void bugNoContradiction1() {

        Model m = new CPModel();
        SetVariable s1 = makeSetVar("s1", 1, 3);
        SetVariable x = makeSetVar("x", 1, 3);
        SetVariable y = makeSetVar("y", 1, 3);

        m.addConstraint(setUnion(x, y, s1));
        m.addConstraint(setDisjoint(x, y));
        m.addConstraint(geqCard(x, 1));

        Solver s = new CPSolver();
        s.read(m);
        try {
            s.propagate();
        } catch (ContradictionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        s.solveAll();
        Assert.assertEquals("nb of solutions", 19, s.getNbSolutions());
    }

    @Test
    public void bugNoContradiction2() {
        Model m = new CPModel();
        SetVariable s1 = makeSetVar("s1", 1, 10);
        m.addConstraint(geqCard(s1, 1));
        m.addConstraint(eqCard(s1, 0));
        Solver s = new CPSolver();
        s.read(m);

        try {
            s.propagate();
            Assert.fail();
        } catch (ContradictionException e) {
            //OK    
        }
    }


}
