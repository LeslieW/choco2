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

package samples.tutorials.trunk;

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
//        LOGGER.info(n + " tps: " + tps + " Node:" + nbNode);
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
//            LOGGER.info("the model has at least one solution");
//        } else {
//            LOGGER.info("the model has no solution");
//        }
//
//        sQueens.worldPopUntil(0);
//        sQueens.worldPush();
//
//        sQueens.solveAll();
//        LOGGER.info("the model has " + sQueens.getNbSolutions()
//                + " solutions");
//
//        sQueens.worldPopUntil(0);
//        sQueens.worldPush();
//
//        sQueens.solve();
//        LOGGER.info("One solution is:");
//        for (int i = 0; i < queens.length; i++) {
//            LOGGER.info(sQueens.getVar(queens[i]) + " = " + sQueens.getVar(queens[i]).getVal());
//        }
//
//        for (int i = 0; i < mQueens.getNbIntVars(); i++) {
//            LOGGER.info(mQueens.getIntVar(i) + " = "
//                    + ((IntDomainVar) mQueens.getIntVar(i)).getVal());
//        }
//
//        sQueens.worldPopUntil(0);
//        sQueens.worldPush();
//
//        int nbSol = 0;
//        if (sQueens.solve() == Boolean.TRUE) {
//            do {
//                LOGGER.info("Solution " + ++nbSol);
//                for (int i = 0; i < mQueens.getNbIntVars(); i++) {
//                    LOGGER.info(mQueens.getIntVar(i) + " = "
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
//        LOGGER.info("the current solution is: ");
//        for (int i = 0; i < mQueens.getNbIntVars(); i++) {
//            LOGGER.info(mQueens.getIntVar(i) + " = "
//                    + ((IntDomainVar) sQueens.getIntVar(i)).getVal());
//        }
//
//
//        Solution previousSolution =
//                (Solution) sQueens.getSearchStrategy().solutions.get(1);
//
//
//        LOGGER.info("the previous solution was: ");
//        for (int i = 0; i < mQueens.getNbIntVars(); i++) {
//            LOGGER.info(mQueens.getIntVar(i) + " = "
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
