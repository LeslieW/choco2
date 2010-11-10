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
package choco.model.constraints.reified;

import static choco.Choco.and;
import static choco.Choco.makeIntVar;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.ComponentConstraint;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintType;
import static choco.kernel.model.constraints.ConstraintType.*;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;
/* 
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 29 oct. 2008
 */

public class ReifiedDisequalitiesTest {

    private ConstraintType[] types;

    @Before
    public void before(){
        types = new ConstraintType[]{EQ, NEQ, GEQ, LEQ, GT, LT};
    }


    @Test
    public void testDisequalities(){
        Model m1;
        Model m2;
        Solver s1;
        Solver s2;
        IntegerVariable x = makeIntVar("x", 1, 3);
        IntegerVariable y = makeIntVar("y", 1, 3);
        IntegerVariable z = makeIntVar("z", 1, 3);
        Random r = new Random();

        for(int seed = 0; seed < 60; seed++){
            int op = r.nextInt(6);
            Constraint c1 = new ComponentConstraint(types[op], types[op], new Variable[]{x, y});
            op = r.nextInt(6);
            Constraint c2 = new ComponentConstraint(types[op], types[op], new Variable[]{y, z});
    
            m1 = new CPModel();
            m2 = new CPModel();

            m1.addConstraints(c1, c2);
            m2.addConstraint(and(c1, c2));

            s1 = new CPSolver();
            s2 = new CPSolver();

            s1.read(m1);
            s2.read(m2);

            s1.solveAll();
            s2.solveAll();

            Assert.assertEquals("Same number of solution", s1.getNbSolutions(), s2.getNbSolutions());
        }
    }


}
