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

/* ************************************************
*           _      _                             *
*          |  (..)  |                            *
*          |_ J||L _|         CHOCO solver       *
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
*                   N. Jussien    1999-2010      *
**************************************************/
package samples.tutorials.seminar;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.branching.AssignVar;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 7 oct. 2010
 */
public class CarSequence {

    static int nbCars;
    static int nbOpt;
    static int nbClasses;

    static int[] demands;
    static int[][] options;
    static int[][] idleConfs;
    static int[][] optfreq;

    static int[][] matrix;

    /**
     * reads out given problem from the given file
     *
     * @param path
     * @param problem
     */
    public static void readFile(String path, String problem) {
        File file = new File(path);
        try {
            BufferedReader input = new BufferedReader(new FileReader(file));
            // look for the problem
            String line;
            while ((line = input.readLine()) != null) {
                if (line.contains(problem)) {
                    input.readLine();
                    StringTokenizer strT = new StringTokenizer(input.readLine());
                    nbCars = Integer.parseInt(strT.nextToken());
                    nbOpt = Integer.parseInt(strT.nextToken());
                    nbClasses = Integer.parseInt(strT.nextToken());
                    demands = new int[nbClasses];
                    options = new int[nbOpt][];
                    idleConfs = new int[nbOpt][];

                    matrix = new int[nbClasses][nbOpt];

                    StringTokenizer strT1 = new StringTokenizer(input
                            .readLine());
                    StringTokenizer strT2 = new StringTokenizer(input
                            .readLine());
                    optfreq = new int[strT1.countTokens()][2];
                    for (int i = 0; i < optfreq.length; i++) {
                        optfreq[i][0] = Integer.parseInt(strT1.nextToken());
                        optfreq[i][1] = Integer.parseInt(strT2.nextToken());
                    }

                    int counter1 = 0;
                    while (counter1 < nbClasses) {
                        strT = new StringTokenizer(input.readLine());
                        strT.nextToken();
                        demands[counter1] = Integer.parseInt(strT.nextToken());
                        int counter2 = 0;
                        while (counter2 < nbOpt) {
                            matrix[counter1][counter2] = Integer.parseInt(strT
                                    .nextToken());
                            counter2++;
                        }
                        counter1++;
                    }
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("File error" + e);
        }

        for (int i = 0; i < matrix[0].length; i++) {
            int nbNulls = 0;
            int nbOnes = 0;
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[j][i] == 1)
                    nbOnes++;
                else
                    nbNulls++;
            }
            options[i] = new int[nbOnes];
            idleConfs[i] = new int[nbNulls];
            int countOnes = 0;
            int countNulls = 0;
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[j][i] == 1) {
                    options[i][countOnes] = j;
                    countOnes++;
                } else {
                    idleConfs[i][countNulls] = j;
                    countNulls++;
                }
            }
        }

    }

    static class Extractor {
        IntegerVariable[] _vars = null;

        int _size;

        Extractor(IntegerVariable[] array) {
            _vars = array;
            _size = array.length;
        }

        IntegerVariable[] extract(int initialNumber, int amount) {
            if ((initialNumber + amount) > _size)
                amount = _size - initialNumber;
            IntegerVariable[] tmp = new IntegerVariable[amount];
            for (int i = initialNumber; i < initialNumber + amount; i++) {
                tmp[i - initialNumber] = _vars[i];
            }
            return tmp;
        }
    }


    public static void main(String[] args) {
        readFile("/Users/cprudhom/Documents/Projects/Sources/JSR331/org.jcp.jsr331.tck/extra/CarSequenceData.txt", "Problem myPb");

//		========= Problem Representation ==================
        Model model = new CPModel();


        IntegerVariable[] cars = Choco.makeIntVarArray("cars", nbCars, 0, nbClasses - 1);

        IntegerVariable[] expArray = new IntegerVariable[nbClasses];

        int max = 0;
        for (int i = 0; i < cars.length; i++) {
            if (cars[i].getUppB() > max)
                max = cars[i].getUppB();
        }
        for (int optNum = 0; optNum < options.length; optNum++) {
            int nbConf = options[optNum].length;
            for (int seqStart = 0; seqStart < (cars.length - optfreq[optNum][1]); seqStart++) {
                IntegerVariable[] carSequence = new Extractor(cars).extract(
                        seqStart, optfreq[optNum][1]);
                IntegerVariable[] atMost = Choco.makeIntVarArray("atmost", options[optNum].length, options[optNum]);
                //m.addConstraint(Choco.globalCardinality(carSequence, atMost, 0));
                // configurations that include given option may be chosen
                // optfreq[optNum][0] times AT MOST
                for (int i = 0; i < nbConf; i++) {
                    //problem.post(atMost[i],"<=",optfreq[optNum][0]);
                    //m.addConstraint(Choco.leq(atMost[i], optfreq[optNum][0]));
                }

                IntegerVariable[] atLeast = Choco.makeIntVarArray("atleast", idleConfs[optNum].length, idleConfs[optNum]);
                //m.addConstraint(Choco.globalCardinality(carSequence, atLeast, 0));
                // all others configurations may be chosen
                // optfreq[optNum][1] - optfreq[optNum][0] times AT LEAST
//                m.addConstraint(Choco.geq(Choco.sum(atLeast), optfreq[optNum][1] - optfreq[optNum][0]));
            }
        }

        int[] values = new int[expArray.length];
        for (int i = 0; i < expArray.length; i++) {
            expArray[i] = Choco.makeIntVar("var", 0, demands[i]);
            values[i] = i;
        }
        model.addConstraint(Choco.globalCardinality(cars, expArray, 0));
//			GlobalCardinality gcc3 = new GlobalCardinality(cars, expArray);
//			gcc3.post();

//			========= Problem Resolution ==================
        Solver solver = new CPSolver();
        solver.read(model);
        solver.addGoal(new AssignVar(new MinDomain(solver), new IncreasingDomain()));
        ChocoLogging.toVerbose();
        solver.solveAll();

    }

}
