/**
 *  Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package choco.model.constraints.integer;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import gnu.trove.TIntHashSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

/**
 * User : cprudhom
 * Mail : cprudhom(a)emn.fr
 * Date : 22 f�vr. 2010
 * Since : Choco 2.1.1
 */
public class AmongTest {


    private static int[] buildValues(Random r, int low, int up){
        int nb = 1+r.nextInt(up-low+1);
        TIntHashSet set = new TIntHashSet(nb);
        for(int i = 0 ; i < nb; i++){
            set.add(low + r.nextInt(up - low + 1));
        }
        int[] values = set.toArray();
        Arrays.sort(values);
        return values;
    }

    @Test
    public void test1(){
        Random r;
        for(int i = 0; i < 200; i++){
            r = new Random(i);
            int low = r.nextInt(20);
            int up = low + r.nextInt(10) + 1;
            int[] values = buildValues(r, low, up);

            IntegerVariable v = makeIntVar("v", low, up);

            Model m1 = new CPModel();
            Constraint among = member(v, values);

            m1.addConstraint(among);

            Solver s = new CPSolver();
            s.read(m1);

            s.setVarIntSelector(new RandomIntVarSelector(s, i));
            s.setValIntSelector(new RandomIntValSelector(i));

            s.solveAll();
            Assert.assertEquals("seed:"+i, values.length, s.getSolutionCount());
            if(values.length == 0){
                Assert.assertEquals("seed:"+i, 0, s.getNodeCount());    
            }else if(values.length == 1){
                Assert.assertEquals("seed:"+i, 1, s.getNodeCount());
            }else{
                Assert.assertEquals("seed:"+i, values.length+1, s.getNodeCount());
            }
        }
    }

    @Test
    public void test2(){
        Random r;
        for(int i = 0; i < 200; i++){
            r = new Random(i);
            int low = r.nextInt(20);
            int up = low + r.nextInt(10) + 1;
            int[] values = buildValues(r, low, up);

            IntegerVariable v = makeIntVar("v", low, up);

            Model m1 = new CPModel();
            Constraint among = member(v, values);

            m1.addConstraint(among);

            Solver s = new CPSolver();
            s.read(m1);

            try {
                s.propagate();
                Assert.assertEquals("seed:"+i, values.length, s.getVar(v).getDomainSize());
            } catch (ContradictionException e) {
                Assert.assertEquals("seed:"+i, 0, values.length);
            }
        }
    }

    @Test
    public void test3(){
        Random r;
        for(int i = 0; i < 200; i++){
            r = new Random(i);
            int low = r.nextInt(20);
            int up = low + r.nextInt(10) + 1;
            int[] values = buildValues(r, low, up);

            IntegerVariable v = makeIntVar("v", low, up);

            Model m1 = new CPModel();
            Constraint among = member(v, values);
            m1.addConstraint(among);

            Model m2 = new CPModel();
            IntegerVariable[] bools = new IntegerVariable[values.length];
            for(int j = 0; j< values.length; j++){
                bools[j] = makeBooleanVar("b"+j);
                m2.addConstraint(reifiedConstraint(bools[j], eq(v, values[j])));
            }
            m2.addConstraint(eq(sum(bools), 1));

            Solver s1 = new CPSolver();
            s1.read(m1);
            s1.setVarIntSelector(new RandomIntVarSelector(s1, i));
            s1.setValIntSelector(new RandomIntValSelector(i));

            Solver s2 = new CPSolver();
            s2.read(m2);
            s2.setVarIntSelector(new RandomIntVarSelector(s2, i));
            s2.setValIntSelector(new RandomIntValSelector(i));

            s1.solveAll();
            s2.solveAll();

            Assert.assertEquals("seed:"+i, s2.getSolutionCount(), s1.getSolutionCount());
        }
    }

}
