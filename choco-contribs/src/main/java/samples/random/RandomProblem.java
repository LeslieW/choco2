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
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.strong.DomOverDDegRPC;
import choco.cp.solver.constraints.strong.StrongConsistencyManager;
import choco.cp.solver.constraints.strong.maxrpcrm.MaxRPCrm;
import choco.cp.solver.search.integer.branching.AssignOrForbidIntVarVal;
import choco.cp.solver.search.integer.branching.DomOverWDegBranching;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.cp.solver.search.integer.valselector.MinVal;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.ComponentConstraintWithSubConstraints;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import samples.random.RandomListGenerator.Structure;

import java.security.InvalidParameterException;
import java.util.*;

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
		// System.out.println("Generating problem with " + nbVar +
		// " variables, "
		// + nbVal + " values, " + nbConstraints + " constraints, "
		// + tightness + " Tightness");

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

	public IntegerVariable[] getVariables() {
		return variables;
	}

	public Collection<Constraint> getConstraints() {
		return constraints;
	}

	private static double criticTightness(int nbvar, int nbval, int nbcons) {
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

	private static int nbArcs(double n, double density) {
		return (int) (density * n * (n - 1) / 2d);
	}

	private static int NBINSTANCES = -1;

	private static int TIMEOUT = -1;

	private enum Heuristic {
		DDEG, WDEG, PROPAGATE
	}

	public static void main(String args[]) {
		final int nbVar;
		final int nbVal;
		final double density;
		final int e;
		final long seed;
		final boolean force;

		final double tightness;

		final double maxRPCProp;

		final Heuristic heuristic;

		final boolean light;
		try {
			int i = 0;
			nbVar = Integer.valueOf(args[i++]);
			nbVal = Integer.valueOf(args[i++]);

			density = Double.valueOf(args[i++]);

			tightness = Double.valueOf(args[i++]);

			force = Boolean.valueOf(args[i++]);

			seed = Long.valueOf(args[i++]);

			heuristic = Heuristic.valueOf(args[i++]);

			maxRPCProp = Double.valueOf(args[i++]);

			light = Boolean.valueOf(args[i++]);

			TIMEOUT = 1000 * Integer.valueOf(args[i++]);

			NBINSTANCES = Integer.valueOf(args[i++]);

		} catch (Exception exception) {
			System.out
					.println("Usage : RandomProblem nbVar nbVal density tightness force seed heuristic maxrpcprop light timeout nbinstances");
			return;
		}

		e = nbArcs(nbVar, density);

		System.out.println("-------------------");
		System.out.println(nbVar + " var, " + nbVal + " val, " + density
				+ " density (" + e + " cstr), " + tightness + " tightness"
				+ (force ? ", forced" : "") + " (pcr = "
				+ criticTightness(nbVar, nbVal, e) + "), " + maxRPCProp
				+ " maxrpc, " + (light ? "light" : "full") + ", " + heuristic);

		test(light, nbVar, nbVal, e, tightness, seed, force, heuristic,
				maxRPCProp);

	}

	private static Collection<Clique> cliques(List<Constraint> constraints) {
		final Collection<Clique> cliques = new ArrayList<Clique>();

		for (int ci1 = constraints.size(); --ci1 >= 0;) {
			final Constraint c1 = constraints.get(ci1);
			final Set<Variable> c1Scope = new HashSet<Variable>(Arrays
					.asList(c1.getVariables()));
			for (int ci2 = ci1; --ci2 >= 0;) {
				final Constraint c2 = constraints.get(ci2);

				final Variable v3;

				if (c1Scope.contains(c2.getVariables()[0])) {
					v3 = c2.getVariables()[1];
				} else if (c1Scope.contains(c2.getVariables()[1])) {
					v3 = c2.getVariables()[0];
				} else {
					continue;
				}

				for (int ci3 = ci2; --ci3 >= 0;) {
					final Constraint c3 = constraints.get(ci3);
					if ((v3 == c3.getVariables()[0] && c1Scope.contains(c3
							.getVariables()[1]))
							|| (v3 == c3.getVariables()[1] && c1Scope
									.contains(c3.getVariables()[0]))) {
						cliques.add(new Clique(c1, c2, c3));
						break;
					}
				}
			}
		}
		return cliques;
	}

	private static void test(boolean light, int nbVar, int nbVal, int nbCons,
			double tightness, long seed, boolean force, Heuristic heuristic,
			double maxRPCProp) {
		final List<Integer> nodes = new ArrayList<Integer>(NBINSTANCES);
		final List<Double> cpu = new ArrayList<Double>(NBINSTANCES);
		final List<Integer> nbCliques = new ArrayList<Integer>(NBINSTANCES);
		final List<Integer> nbMaxRPCCons = new ArrayList<Integer>(NBINSTANCES);
		final List<Integer> nbAwakes = new ArrayList<Integer>(NBINSTANCES);
		final List<Long> mem = new ArrayList<Long>(NBINSTANCES);
		// final double[] nps = new double[NBINSTANCES - 1];

		for (int i = NBINSTANCES; --i >= 0;) {
			System.out.print("g");
			final RandomProblem problem = new RandomProblem(nbVar, nbVal,
					nbCons, tightness, seed + i, force);

			final List<Constraint> constraints = new ArrayList<Constraint>(
					problem.getConstraints());

			// Build a model
			final Model m = new CPModel();

			m.addVariables(problem.getVariables());

			final Set<Constraint> maxRPCConstraints = new HashSet<Constraint>();
			final Set<Constraint> acConstraints = new HashSet<Constraint>(
					constraints);

			if (maxRPCProp > 1) {
				nbCliques.add(-1);
				maxRPCConstraints.addAll(acConstraints);
				acConstraints.clear();
			} else {
				final Collection<Clique> cliques = cliques(constraints);
				nbCliques.add(cliques.size());
				int nbMaxRPC = (int) (cliques.size() * maxRPCProp);
				for (Clique c : cliques) {
					if (nbMaxRPC-- <= 0) {
						break;
					}
					maxRPCConstraints.addAll(Arrays.asList(c.constraints));
				}
			}

			if (!maxRPCConstraints.isEmpty()) {
				final Set<Variable> maxRPCVariables = new HashSet<Variable>();
				for (Constraint c : maxRPCConstraints) {
					maxRPCVariables.addAll(Arrays.asList(c.getVariables()));
				}

				final Constraint cc = new ComponentConstraintWithSubConstraints(
						StrongConsistencyManager.class, maxRPCVariables
								.toArray(new Variable[maxRPCVariables.size()]),
						MaxRPCrm.class, maxRPCConstraints
								.toArray(new Constraint[maxRPCConstraints
										.size()]));
				if (light) {
					cc.addOption("light");
				}
				m.addConstraint(cc);
				acConstraints.removeAll(maxRPCConstraints);
			}

			nbMaxRPCCons.add(maxRPCConstraints.size());

			for (Constraint c : acConstraints) {
				m.addConstraint(c);
			}

			// System.out.println("Building solver...");

			// Build a solver
			final Solver s = new CPSolver();
			// CPSolver.setVerbosity(CPSolver.SEARCH);
			// Read the model
			s.read(m);

			s.setTimeLimit(TIMEOUT);
			// System.out.println("Solving...");
			System.out.print("s");
			Boolean result;
			System.gc();

			long time;

			if (heuristic.equals(Heuristic.PROPAGATE)) {
				time = -System.nanoTime();
				try {
					s.propagate();
					result = Boolean.TRUE;
				} catch (ContradictionException e) {
					result = Boolean.FALSE;
				}
				time += System.nanoTime();
			} else {
				switch (heuristic) {
				case WDEG:
					// s.attachGoal(new DomOverWDegBranching(s, new MinVal()));
//					s.attachGoal(new AssignOrForbidIntVarVal(
//							new DomOverWDeg(s), new MinVal()));
					
					// s.setVarIntSelector(new DomOverWDegRPC(s));
					// s
					// .attachGoal(new
					// choco.cp.solver.search.integer
					// .branching.DomOverWDegBranching(
					// s, new MinVal()));
					// s
					// .attachGoal(new AssignVar(new DomOverWDeg(s),
					// new MinVal()));
					 s.attachGoal(new DomOverWDegBranching(s, new IncreasingDomain()));
				default:
					s.attachGoal(new AssignOrForbidIntVarVal(
							new DomOverDDegRPC(s), new MinVal()));
				}
				result = s.solve();
				time = (long) s.getTimeCount() * 1000000l;
			}

			// final Boolean result = s.solve();
			mem.add(Runtime.getRuntime().totalMemory()
					- Runtime.getRuntime().freeMemory());
			// CPSolver.flushLogs();

			if (result == null) {
				System.out.print("*");
				// System.out.print(">=" + nbSol);
				// nodes[i] = Integer.MAX_VALUE;
				cpu.add(Double.POSITIVE_INFINITY);
				nbAwakes.add(Integer.MAX_VALUE);
				nodes.add(Integer.MAX_VALUE);
			} else {
				System.out.print(result ? 1 : 0);
				try {
					nodes.add(s.getNodeCount());
				} catch (Exception e) {
					nodes.add(0);
				}
				cpu.add(time / 1e9d);
				nbAwakes.add(MaxRPCrm.nbPropag);
				MaxRPCrm.nbPropag = 0;
			}
			// nps[i] = 1000 * s.getNodeCount() / s.getTimeCount();

		}

		System.out.println();

		System.out.println(median(cpu) + " seconds med");
		System.out.println(median(nodes) + " nodes med");
		System.out.println(median(nbCliques) + " cliques med");
		System.out.println(median(nbMaxRPCCons) + " maxrpccons med");
		System.out.println(median(nbAwakes) + " awakes med");
		System.out.println(median(mem) + " mem med");
	}

	private static class Clique {
		final Constraint[] constraints;

		public Clique(Constraint c1, Constraint c2, Constraint c3) {
			constraints = new Constraint[] { c1, c2, c3 };
		}
	}

	private static <T extends Comparable<T>> T median(List<T> array) {
		final List<T> sorted = new ArrayList<T>(array);
		Collections.sort(sorted);
		return sorted.get((sorted.size() - 1) / 2);
	}
}