/* ************************************************
*           _       _                            *
*          |  °(..)  |                           *
*          |_  J||L _|        CHOCO solver       *
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
*                   N. Jussien    1999-2009      *
**************************************************/
package choco.model.constraints.reified;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 3 nov. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*/
public class ImplicationTest {

    @Test
    public void test1(){
        for (int i = 0; i< 100; i++){
            IntegerVariable b = Choco.makeBooleanVar("b");
            IntegerVariable[] bs = Choco.makeBooleanVarArray("bs", 2);

            Constraint c = Choco.reifiedRightImp(b, bs[0], bs[1]);

            List<int[]> feas = new ArrayList<int[]>();
            feas.add(new int[]{1,1,1});
            feas.add(new int[]{0,1,0});
            feas.add(new int[]{1,0,1});
            feas.add(new int[]{1,0,0});
            Constraint verif = Choco.feasTupleAC(feas, ArrayUtils.append(new IntegerVariable[]{b}, bs));

            Model m = new CPModel();
            m.addConstraint(c);

            Model m_v = new CPModel();
            m_v.addConstraint(verif);

            Solver s = new CPSolver();
            s.read(m);
            s.setVarIntSelector(new RandomIntVarSelector(s, i));
            s.setValIntSelector(new RandomIntValSelector(i));
            s.solveAll();

            Solver s_v = new CPSolver();
            s_v.read(m);
            s_v.setVarIntSelector(new RandomIntVarSelector(s_v, i));
            s_v.setValIntSelector(new RandomIntValSelector(i));
            s_v.solveAll();

            Assert.assertEquals("nb sol", s_v.getNbSolutions(), s.getSolutionCount());
        }
    }

}
