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
package samples.wiki;

/*
 * Sample used by the wiki page 'N-queens'
 *
 * PLEASE, DO NOT MODIFY THIS FILE WITHOUT MODIFYING THE INVOLVED WIKI
 */
public class NQueens {

//    public static void queen(int n) {
//        Model m = new CPModel();
//        IntegerVariable[] queens = makeEnumIntVarArray("Q", n, 1, n);
//        for (int i = 0; i < n; i++) {
//            for (int j = i + 1; j < n; j++) {
//                int k = j - i;
//                m.addConstraint(neq(queens[i], queens[j]));
//                m.addConstraint(neq(queens[i], plus(queens[j], k)));  // diagonal constraints
//                m.addConstraint(neq(queens[i], minus(queens[j], k))); // diagonal constraints
//            }
//        }
//        //FIXME modifier le wiki, je crois pas que ce soit dedans
//        Solver s = new CPSolver();
//        s.read(m);
//        s.setVarIntSelector(new MinSizeMinLB(m, s.getVar(queens)));
//        s.solve();
//        int nbNode = s.getSearchStrategy().getNodeCount();
//        int tps = s.getSearchStrategy().getTimeCount();
//        System.out.println(n + " tps: " + tps + " Node:" + nbNode);
//    }
//
//    public static void firstSteps() {
//      int n = 8;
//
//        Model mQueens = new CPModel();
//
//        IntegerVariable[] queens = makeEnumIntVarArray("Queen", n,  1, n);
//
//        for (int i = 0; i < n; i++) {
//            for (int j = i + 1; j < n; j++) {
//                mQueens.addConstraint(neq(queens[i], queens[j])); // different columns
//                mQueens.addConstraint(neq(queens[i],
//                        plus(queens[j], j - i))); // diagonal
//                mQueens.addConstraint(neq(queens[i],
//                        minus(queens[j], j - i))); // diagonal
//            }
//        }
//        Solver sQueens = new CPSolver();
//        sQueens.read(mQueens);
//        try {
//            sQueens.propagate();
//        }
//        catch (ContradictionException e) {
//            System.err.println("This model is obviously over-constrained");
//            System.exit(-1);
//        }
//
//        sQueens.worldPush();
//
//        if (sQueens.solve() == Boolean.TRUE) {
//            System.out.println("the model has at least one solution");
//        } else {
//            System.out.println("the model has no solution");
//        }
//
//        sQueens.worldPopUntil(0);
//        sQueens.worldPush();
//
//        sQueens.solveAll();
//        System.out.println("the model has " + sQueens.getNbSolutions()
//                + " solutions");
//
//        sQueens.worldPopUntil(0);
//        sQueens.worldPush();
//
//        sQueens.solve();
//        System.out.println("One solution is:");
//        for (int i = 0; i < queens.length; i++) {
//            System.out.println(sQueens.getVar(queens[i]) + " = " + sQueens.getVar(queens[i]).getVal());
//        }
//
//        for (int i = 0; i < mQueens.getNbIntVars(); i++) {
//            System.out.println(mQueens.getIntVar(i) + " = "
//                    + ((IntDomainVar) mQueens.getIntVar(i)).getVal());
//        }
//
//        sQueens.worldPopUntil(0);
//        sQueens.worldPush();
//
//        int nbSol = 0;
//        if (sQueens.solve() == Boolean.TRUE) {
//            do {
//                System.out.println("Solution " + ++nbSol);
//                for (int i = 0; i < mQueens.getNbIntVars(); i++) {
//                    System.out.println(mQueens.getIntVar(i) + " = "
//                            + ((IntDomainVar) mQueens.getIntVar(i)).getVal());
//                }
//            } while (sQueens.nextSolution() == Boolean.TRUE);
//        }
//
//        sQueens.worldPopUntil(0);
//        sQueens.worldPush();
//
//        sQueens.solveAll();
//
//        System.out.println("the current solution is: ");
//        for (int i = 0; i < mQueens.getNbIntVars(); i++) {
//            System.out.println(mQueens.getIntVar(i) + " = "
//                    + ((IntDomainVar) sQueens.getIntVar(i)).getVal());
//        }
//
//
//        Solution previousSolution =
//                (Solution) sQueens.getSearchStrategy().solutions.get(1);
//
//
//        System.out.println("the previous solution was: ");
//        for (int i = 0; i < mQueens.getNbIntVars(); i++) {
//            System.out.println(mQueens.getIntVar(i) + " = "
//                    + previousSolution.getValue(i));
//        }
//    }
//
//
//
//    public static void main(String[] args) {
//        for (int i = 500; i < 501; i++) {
//            queen(i);
//        }
//    }
}
