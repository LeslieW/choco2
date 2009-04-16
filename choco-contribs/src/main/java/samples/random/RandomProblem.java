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

package samples.random;

import static choco.Choco.feasPairAC;
import static choco.Choco.makeIntVar;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import samples.random.RandomListGenerator.Structure;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class RandomProblem {

    private IntegerVariable[] variables;
    private Collection<Constraint> constraints;

    public RandomProblem(int nbVar, int nbVal, double density, long seed,
            boolean force) {
        this(nbVar, nbVal, nbArcs(nbVar, density), criticTightness(nbVar,
                nbVal, nbArcs(nbVar, density)), seed, force);
    }

    public RandomProblem(int nbVar, int nbVal, int nbConstraints,
            double tightness, long seed, boolean force) {
        // Build enumerated domain variables
        variables = new IntegerVariable[nbVar];

        for (int i = nbVar; --i >= 0;) {
            variables[i] = makeIntVar("var" + i, 0, nbVal - 1);
        }

        final ProportionRandomListGenerator r = new CoarseProportionRandomListGenerator(
                nbVar, 2, seed);

        final int[][] activeConstraints = r.selectTuples(nbConstraints,
                Structure.UNSTRUCTURED, false, false);

        final int[] solution;

        final Random random = new Random(seed);

        if (force) {
            solution = new int[nbVar];
            for (int i = nbVar; --i >= 0;) {
                solution[i] = random.nextInt(nbVal);
            }
        } else {
            solution = null;
        }

        constraints = new ArrayList<Constraint>();

        final int[] sizes = { nbVal, nbVal };

        for (int[] arc : activeConstraints) {

            final boolean[][] matrix = new boolean[nbVal][nbVal];

            ProportionRandomListGenerator randomList = new CoarseProportionRandomListGenerator(
                    sizes, random.nextLong());

            final int[][] tupleList;

            if (force) {
                tupleList = randomList.selectTuples(
                        (int) (nbVal * nbVal * (1 - tightness)),
                        Structure.UNSTRUCTURED, false, true, new int[] {
                                solution[arc[0]], solution[arc[1]] }, true);
            } else {
                tupleList = randomList.selectTuples(
                        (int) (nbVal * nbVal * (1 - tightness)),
                        Structure.UNSTRUCTURED, false, true);
            }

            for (int[] tuple : tupleList) {
                matrix[tuple[0]][tuple[1]] = true;
            }

            constraints.add(feasPairAC(variables[arc[0]], variables[arc[1]],
                    matrix));
        }
    }

    public static int nbArcs(double n, double density) {
        return (int) (density * n * (n - 1) / 2d);
    }

    public static double criticTightness(int nbvar, int nbval, int nbcons) {
        // d = n^alpha
        double alpha = Math.log(nbval) / Math.log(nbvar);

        if (alpha <= .5) {
            throw new InvalidParameterException();
        }

        // e = rn ln(n)
        double r = nbcons / (nbvar * Math.log(nbvar));

        // pcr = 1 − exp(−alpha/r)
        double pcr = 1 - Math.exp(-alpha / r);

        // if (pcr > .5) {
        // System.err.println("Warning : tightness should not be > 0.5");
        // }

        return pcr;
    }

    public IntegerVariable[] getVariables() {
        return variables;
    }

    public Collection<Constraint> getConstraints() {
        return constraints;
    }

}