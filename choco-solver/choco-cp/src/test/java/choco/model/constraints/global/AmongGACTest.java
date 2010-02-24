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
package choco.model.constraints.global;

import choco.Choco;
import choco.Reformulation;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import gnu.trove.TIntHashSet;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;

/**
 * User : cprudhom
 * Mail : cprudhom(a)emn.fr
 * Date : 22 févr. 2010
 * Since : Choco 2.1.1
 */
public class AmongGACTest {

    private static final Logger LOGGER = ChocoLogging.getTestLogger();

    IntegerVariable[] _variables;

    private int[] buildValues(Random r, int low, int up){
        int nb = 1 + r.nextInt(up-low + 1);
        TIntHashSet set = new TIntHashSet(nb);
        for(int i = 0 ; i < nb; i++){
            set.add(low + r.nextInt(up - low + 1));
        }
        int[] values = set.toArray();
        Arrays.sort(values);
        return values;
    }

    private IntegerVariable[] buildVars(Random r, int low, int up){
        int nb = 1 + r.nextInt(up - low + 1);
        IntegerVariable[] vars = new IntegerVariable[nb];
        for(int i = 0 ; i < nb; i++){
            vars[i] = Choco.makeIntVar("vars_"+i, buildValues(r, low, up));
        }
        return vars;
    }

    private CPModel[] model(Random r, int size){
        int low = r.nextInt(size);
        int up = low + r.nextInt(2*size);
        int[] values = buildValues(r, low, up);
        IntegerVariable[] vars = buildVars(r, low,up);
        int max = r.nextInt(vars.length);
        int min = Math.max(0, max-1 - r.nextInt(vars.length));
        IntegerVariable N = Choco.makeIntVar("N", min,max);

        _variables = ArrayUtils.append(vars, new IntegerVariable[]{N});

        CPModel[] ms = new CPModel[2];
        for(int i = 0; i< ms.length; i++){
            CPModel m = new CPModel();
            switch (i){
                case 0 :
                    m.addConstraints(Reformulation.among(N, vars, values));
                    break;
                case 1:
                    m.addConstraint(Choco.among(N, vars, values));
                break;
            }
            ms[i] = m;
        }
        return ms;
    }


    private CPSolver solve(int seed, CPModel m, boolean sta_tic){
        CPSolver s = new CPSolver();
        s.read(m);
        if(sta_tic){
            s.setVarIntSelector(new StaticVarOrder(s, s.getVar(_variables)));
            s.setValIntIterator(new IncreasingDomain());
        }else{
            s.setVarIntSelector(new RandomIntVarSelector(s, seed));
            s.setValIntSelector(new RandomIntValSelector(seed));
        }
        s.solveAll();
        return s;
    }

    @Test
    public void test0(){
        Model m = new CPModel();
        IntegerVariable[] vars = Choco.makeIntVarArray("vars", 3, 0,3);
        
        int[] values = new int[]{0, 1};
        IntegerVariable N = Choco.makeIntVar("N", 0, 2);
        Constraint among =Choco.among(N, vars, values);

        m.addConstraint(among);
        Solver s = new CPSolver();
        s.read(m);
        s.solveAll();

        Model mr = new CPModel();
        IntegerVariable[] bools = Choco.makeBooleanVarArray("bools", vars.length);
        for(int j = 0; j < bools.length; j++){
            mr.addConstraint(Choco.reifiedConstraint(bools[j], Choco.among(vars[j], values)));
        }
        mr.addConstraint(Choco.eq(Choco.sum(bools), N));
        Solver sr = new CPSolver();
        sr.read(mr);
        sr.solveAll();

        Assert.assertEquals(sr.getSolutionCount(), s.getSolutionCount());
    }


    @Test
    public void test1() throws IOException {
        Random r;
        for(int st = 0; st < 2; st++){
            for(int i = 0; i < 500/*4000*/; i++){
                r = new Random(i);
                CPModel[] ms = model(r, 5);
                CPSolver[] ss = new CPSolver[ms.length];
                for(int j = 0; j < ms.length; j++){
                    ss[j] = solve(i, ms[j], (st == 1));
                }
                for(int j = 1; j < ms.length; j++){
                    Assert.assertEquals("nb solutions, seed:"+i, ss[0].getSolutionCount(), ss[j].getSolutionCount());
                    if(st > 0){
                        Assert.assertEquals("nb nodes, seed:"+i, ss[0].getNodeCount(), ss[j].getNodeCount());
                    }
                }
            }
        }
    }


}
