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
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package samples.Examples;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.set.AssignSetVar;
import choco.cp.solver.search.set.MinDomSet;
import choco.cp.solver.search.set.MinEnv;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.Solution;

public class SteinerSystem extends PatternExample{

    /**
   * A ternary Steiner system of order n is a set of triplets of distinct elements
   * taking their values between 1 and n, such that all the pairs included in two different triplets are different.
   * a solution for n = 7 :
   * [{1, 2, 3}, {2, 4, 5}, {3, 4, 6}, {1, 4, 7}, {1, 5, 6}, {2,6, 7}, {3, 5, 7}]
   * we must have n % 6 = 1 or n % 6 = 3 to get a valid n for the problem
   */

    static int p = 7;
    static int n = p * (p - 1) / 6;

    SetVariable[] vars;

    @Override
    public void setUp(Object paramaters) {
        if(paramaters instanceof Integer){
            p = (Integer)paramaters;
            n = p * (p - 1) / 6;
        }
    }

    @Override
    public void buildModel() {
        _m = new CPModel();

        vars = new SetVariable[n];
        SetVariable[] intersect = new SetVariable[n * n];

        // Create Variables
        for (int i = 0; i < n; i++)
          vars[i] = makeSetVar("set " + i, 1, n);
        for (int i = 0; i < n; i++)
          for (int j = i + 1; j < n; j++)
            intersect[i * n + j] = makeSetVar("interSet " + i + " " + j, 1, n);

        // Post constraints
        for (int i = 0; i < n; i++){
            _m.addConstraint(eqCard(vars[i], 3));
        }
        for (int i = 0; i < n; i++) {
          for (int j = i + 1; j < n; j++) {
            _m.addConstraint(setInter(vars[i], vars[j], intersect[i * n + j]));
            _m.addConstraint(leqCard(intersect[i * n + j], 1));
          }
        }
    }

    @Override
    public void buildSolver() {
        _s = new CPSolver();
        _s.read(_m);
    }

    @Override
    public void solve() {
        _s.setFirstSolution(true);
        _s.generateSearchStrategy();
        _s.addGoal(new AssignSetVar(new MinDomSet(_s, _s.getVar(vars)), new MinEnv(_s)));
        _s.launch();
    }

    @Override
    public void prettyOut() {
        StringBuffer s = new StringBuffer();
        Solution sol = _s.getSearchStrategy().solutions.get(0);
        _s.restoreSolution(sol);
        s.append("A ternary Steiner system of order n is a set of triplets of n*(n - 1) / 6 " +
            "distinct elements taking their values between 1 and n," +
            " such that all the pairs included in two different triplets are different. " +
            " see http://mathworld.wolfram.com/SteinerTripleSystem.html \n \n");

        s.append("A solution for n = " + p + "\n" + "\n");
        for (int i = 0; i < n; i++) {
          s.append("set[" + i + "]:" + vars[i].pretty() + "\n");
        }
        System.out.println(s.toString());

    }

  public static void main(String[] args) {
    new SteinerSystem().execute(7);
  }


}
