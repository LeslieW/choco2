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
package choco.model.constraints.integer;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.ModelException;
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
public class ExactlyTest {

    private static final Logger LOGGER = ChocoLogging.getTestLogger();

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
        int nb = r.nextInt(up - low + 1);
        IntegerVariable[] vars = new IntegerVariable[nb];
        for(int i = 0 ; i < nb; i++){
            vars[i] = Choco.makeIntVar("vars_"+i, buildValues(r, low, up));
        }
        return vars;
    }

    private CPModel[] model(Random r){
        int low = r.nextInt(5);
        int up = low + r.nextInt(10);
        int value = low + r.nextInt(up+1);
        IntegerVariable[] vars = buildVars(r, low,up);
        int N = r.nextInt(vars.length+1);

//        variables = ArrayUtils.append(vars, new IntegerVariable[]{N});

        CPModel[] ms = new CPModel[2];
        for(int i = 0; i< ms.length; i++){
            CPModel m = new CPModel();
            switch (i){
                case 0 :
                    IntegerVariable[] bools = Choco.makeBooleanVarArray("bools", vars.length);
                    for(int j = 0; j < bools.length; j++){
                        m.addConstraint(Choco.reifiedConstraint(bools[j], Choco.eq(vars[j], value)));
                    }
                    m.addConstraint(Choco.eq(Choco.sum(bools), N));
                    break;
                case 1:
                m.addConstraint(Choco.exactly(N, vars, value));
                break;
            }
            ms[i] = m;
        }
        return ms;
    }


    private CPSolver solve(int seed, CPModel m){
        CPSolver s = new CPSolver();
        s.read(m);
        s.setVarIntSelector(new RandomIntVarSelector(s, seed));
        s.setValIntSelector(new RandomIntValSelector(seed));
        s.solveAll();
        return s;
    }

    @Test
    public void test0(){
        Model m = new CPModel();
        IntegerVariable[] vars = Choco.makeIntVarArray("vars", 3, 0,3);

        int value = 0;
        int N = 2;
        Constraint among =Choco.exactly(N, vars, value);

        m.addConstraint(among);
        Solver s = new CPSolver();
        s.read(m);
        s.solveAll();

        Model mr = new CPModel();
        IntegerVariable[] bools = Choco.makeBooleanVarArray("bools", vars.length);
        for(int j = 0; j < bools.length; j++){
            mr.addConstraint(Choco.reifiedConstraint(bools[j], Choco.eq(vars[j], value)));
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
        for(int i = 0; i < 1000; i++){
            r = new Random(i);
            try{
                CPModel[] ms = model(r);
                CPSolver[] ss = new CPSolver[ms.length];
                for(int j = 0; j < ms.length; j++){
                    ss[j] = solve(i, ms[j]);
                }
                for(int j = 1; j < ms.length; j++){
                    Assert.assertEquals("nb solutions, seed:"+i, ss[0].getSolutionCount(), ss[j].getSolutionCount());
                }
            }catch (ModelException ignored){}
        }
    }


}