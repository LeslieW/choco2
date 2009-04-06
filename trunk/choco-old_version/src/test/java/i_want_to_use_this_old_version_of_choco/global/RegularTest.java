package i_want_to_use_this_old_version_of_choco.global;

import i_want_to_use_this_old_version_of_choco.AbstractProblem;
import i_want_to_use_this_old_version_of_choco.Constraint;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.global.regular.DFA;
import i_want_to_use_this_old_version_of_choco.global.regular.Transition;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntValSelector;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntVarSelector;
import i_want_to_use_this_old_version_of_choco.search.NodeLimit;
import junit.framework.TestCase;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Hadrien
 * Date: 26 d�c. 2006
 * Time: 18:47:04
 * To change this template use File | Settings | File Templates.
 */
public class RegularTest extends TestCase {

	public static void testAutoExampleNegative() {
		Problem pb = new Problem();
		IntDomainVar v1 = pb.makeEnumIntVar("v1", -5, 5);
		IntDomainVar v2 = pb.makeEnumIntVar("v2", -5, 5);
		IntDomainVar v3 = pb.makeEnumIntVar("v3", -5, 5);
		IntDomainVar v5 = pb.makeEnumIntVar("v5", -5, 5);
		IntDomainVar v6 = pb.makeEnumIntVar("v6", -5, 5);

		//remove some forbidden tuples (here, the tuples define a not_all_equal constraint)
		List<int[]> tuples = new LinkedList<int[]>();
		tuples.add(new int[]{-1, 3, 0});
		tuples.add(new int[]{2, -3, 3});
		tuples.add(new int[]{3, -3, 3});

		// post the constraint
		pb.post(pb.regular(new IntDomainVar[]{v1, v2, v3}, tuples));
		pb.post(pb.regular(new IntDomainVar[]{v2, v5, v6}, tuples));

		//pb.post(pb.regular(dfa,new IntDomainVar[]{v1, v2, v3}));
		//pb.post(pb.regular(dfa,new IntDomainVar[]{v2, v5, v6}));

		pb.solve();
		if (pb.isFeasible()) {
			do {
				System.out.println(v1 + " " + v2 + " " + v3);
				System.out.println(v2 + " " + v5 + " " + v6);

			} while (pb.nextSolution() == Boolean.TRUE);
		}
		System.out.println("ExpectedSolutions 1 - nbSol " + pb.getSolver().getNbSolutions());
		assertEquals(1, pb.getSolver().getNbSolutions());
	}

	public static void testAutoExample0() {
		Problem pb = new Problem();
		IntDomainVar v1 = pb.makeEnumIntVar("v1", 1, 2);
		IntDomainVar v2 = pb.makeEnumIntVar("v2", new int[]{0, 3});
		IntDomainVar v3 = pb.makeEnumIntVar("v3", new int[]{0, 3});

		//remove some forbidden tuples (here, the tuples define a not_all_equal constraint)
		List<int[]> tuples = new LinkedList<int[]>();
		tuples.add(new int[]{1, 3, 0});
		tuples.add(new int[]{2, 3, 3});

		// post the constraint
		pb.post(pb.regular(new IntDomainVar[]{v1, v2, v3}, tuples));

		pb.solveAll();

		System.out.println("ExpectedSolutions 2 - nbSol " + pb.getSolver().getNbSolutions());
		assertEquals(2, pb.getSolver().getNbSolutions());
	}

	public static void testAutoExample1() {
		Problem pb = new Problem();
		IntDomainVar v1 = pb.makeEnumIntVar("v1", 1, 4);
		IntDomainVar v2 = pb.makeEnumIntVar("v2", 1, 4);
		IntDomainVar v3 = pb.makeEnumIntVar("v3", 1, 4);

		//remove some forbidden tuples (here, the tuples define a not_all_equal constraint)
		List<int[]> tuples = new LinkedList<int[]>();
		tuples.add(new int[]{1, 1, 1});
		tuples.add(new int[]{2, 2, 2});
		tuples.add(new int[]{3, 3, 3});
		tuples.add(new int[]{4, 4, 4});

		// post the constraint
		pb.post(pb.regular(new IntDomainVar[]{v1, v2, v3}, tuples, new int[]{1, 1, 1}, new int[]{4, 4, 4}));

		pb.solveAll();

		System.out.println("ExpectedSolutions 60 - nbSol " + pb.getSolver().getNbSolutions());
		assertEquals(60, pb.getSolver().getNbSolutions());
	}

	public static void testAutoExample1Bis() {
		Problem pb = new Problem();
		IntDomainVar v1 = pb.makeEnumIntVar("v1", 1, 4);
		IntDomainVar v2 = pb.makeEnumIntVar("v2", 1, 4);
		IntDomainVar v3 = pb.makeEnumIntVar("v3", 1, 4);

		//remove some forbidden tuples (here, the tuples define a not_all_equal constraint)
		List<int[]> tuples = new LinkedList<int[]>();
		tuples.add(new int[]{2, 2, 2});
		tuples.add(new int[]{3, 3, 3});
		tuples.add(new int[]{4, 4, 4});

		// post the constraint
		pb.post(pb.regular(new IntDomainVar[]{v1, v2, v3}, tuples, new int[]{1, 1, 1}, new int[]{4, 4, 4}));

		pb.solveAll();

		System.out.println("ExpectedSolutions 61 - nbSol " + pb.getSolver().getNbSolutions());
		assertEquals(61, pb.getSolver().getNbSolutions());
	}

	public static void testAutoExample2() {
		Problem pb = new Problem();
		IntDomainVar v1 = pb.makeEnumIntVar("v1", 1, 4);
		IntDomainVar v2 = pb.makeEnumIntVar("v2", 1, 4);
		IntDomainVar v3 = pb.makeEnumIntVar("v3", 1, 4);

		//add some allowed tuples (here, the tuples define a all_equal constraint)

		List<int[]> tuples = new LinkedList<int[]>();
		tuples.add(new int[]{1, 1, 1});
		tuples.add(new int[]{2, 2, 2});
		tuples.add(new int[]{3, 3, 3});
		tuples.add(new int[]{4, 4, 4});

		// post the constraint
		pb.post(pb.regular(new IntDomainVar[]{v1, v2, v3}, tuples));

		pb.solveAll();
		System.out.println("ExpectedSolutions 4 - nbSol " + pb.getSolver().getNbSolutions());
		assertEquals(4, pb.getSolver().getNbSolutions());
	}

	public static void testAutoExample2Bis() {
		Problem pb = new Problem();
		IntDomainVar v1 = pb.makeEnumIntVar("v1", 1, 4);
		IntDomainVar v2 = pb.makeEnumIntVar("v2", 1, 4);
		IntDomainVar v3 = pb.makeEnumIntVar("v3", 1, 4);

		//add some allowed tuples (here, the tuples define a all_equal constraint)
		List<int[]> tuples = new LinkedList<int[]>();
		tuples.add(new int[]{2, 2, 2});
		tuples.add(new int[]{3, 3, 3});
		tuples.add(new int[]{4, 4, 4});

		// post the constraint
		pb.post(pb.regular(new IntDomainVar[]{v1, v2, v3}, tuples));

		pb.solveAll();
		System.out.println("ExpectedSolutions 3 - nbSol " + pb.getSolver().getNbSolutions());
		assertEquals(3, pb.getSolver().getNbSolutions());
	}


	public static void testAutoExample3() {
		Problem pb = new Problem();
		int n = 8;
		IntDomainVar[] vars = new IntDomainVar[n];
		for (int i = 0; i < vars.length; i++) {
			vars[i] = pb.makeEnumIntVar("v" + i, 0, 1);
		}
		//
		List<Transition> t = new LinkedList<Transition>();
		t.add(new Transition(0, 0, 1));
		t.add(new Transition(1, 1, 0));
		t.add(new Transition(0, 1, 2));
		t.add(new Transition(2, 0, 0));

		List<Integer> fs = new LinkedList<Integer>();
		fs.add(0);
		DFA auto = new DFA(t, fs, n);
		// post the constraint
		pb.post(pb.regular(auto, vars));

		pb.solveAll();
		System.out.println("ExpectedSolutions 16 - nbSol " + pb.getSolver().getNbSolutions());
		assertEquals(16, pb.getSolver().getNbSolutions());
	}

	public static void testAutoExample3Bis() {
		Problem pb = new Problem();
		int n = 8;
		IntDomainVar[] vars = new IntDomainVar[n];
		for (int i = 0; i < vars.length; i++) {
			vars[i] = pb.makeEnumIntVar("v" + i, 0, 2);
		}
		//
		List<Transition> t = new LinkedList<Transition>();
		t.add(new Transition(0, 0, 1));
		t.add(new Transition(1, 1, 0));
		t.add(new Transition(0, 1, 2));
		t.add(new Transition(2, 0, 2));
		t.add(new Transition(0, 2, 3));
		t.add(new Transition(1, 2, 3));
		t.add(new Transition(2, 2, 3));

		List<Integer> fs = new LinkedList<Integer>();
		fs.add(0);
		fs.add(3);
		DFA auto = new DFA(t, fs, n);
		// post the constraint
		pb.post(pb.regular(auto, vars));

		pb.solveAll();
		System.out.println("ExpectedSolutions 16 - nbSol " + pb.getSolver().getNbSolutions());
		assertEquals(6, pb.getSolver().getNbSolutions());
	}

	public static void testAutoExample4Rostering() {
		Problem pb = new Problem();
		int n = 6;
		IntDomainVar[] vars = new IntDomainVar[n];
		for (int i = 0; i < vars.length; i++) {
			vars[i] = pb.makeEnumIntVar("v" + i, 0, 5);
		}
		//Impose exactement 3 "un" cons�cutifs (ou aucun) dans une s�quence de
		//6 variables qui prennent la valeur 3 sinon.
		List<Transition> t = new LinkedList<Transition>();
		t.add(new Transition(0, 1, 1));
		t.add(new Transition(1, 1, 2));
		t.add(new Transition(2, 1, 3));

		t.add(new Transition(3, 3, 0));
		t.add(new Transition(0, 3, 0));

		// 2 �tats finaux : 0, 3
		List<Integer> fs = new LinkedList<Integer>();
		fs.add(0);
		fs.add(3);

		DFA auto = new DFA(t, fs, n);
		// post the constraint
		pb.post(pb.regular(auto, vars));

		pb.solveAll();
		System.out.println("ExpectedSolutions 5 - nbSol " + pb.getSolver().getNbSolutions());
		assertEquals(5, pb.getSolver().getNbSolutions());
	}

	public static void testStrechExemple() {
		ArrayList<int[]> lgt = new ArrayList<int[]>();
		lgt.add(new int[]{2, 2, 2});
		lgt.add(new int[]{0, 2, 2});
		lgt.add(new int[]{1, 2, 3});

		Problem pb = new Problem();
		int n = 7;
		IntDomainVar[] vars = new IntDomainVar[n];
		for (int i = 0; i < vars.length; i++) {
			vars[i] = pb.makeEnumIntVar("v" + i, 0, 2);
		}
		pb.post(pb.stretchPath(vars, lgt));
		pb.solve();
		if (pb.isFeasible()) {
			do {
				for (int i = 0; i < vars.length; i++) {
					System.out.print(vars[i].getVal());
				}
				System.out.println("");
			} while (pb.nextSolution() == Boolean.TRUE);
		}
		assertEquals(12, pb.getSolver().getNbSolutions());
	}

	public static void testAutoExampleRegExp() {
		Problem pb = new Problem();
		int n = 6;
		IntDomainVar[] vars = new IntDomainVar[n];
		for (int i = 0; i < vars.length; i++) {
			vars[i] = pb.makeEnumIntVar("v" + i, 0, 5);
		}
		String regexp = "(1|2)(3*)(4|5)";
		// post the constraint
		pb.post(pb.regular(regexp, vars));

		pb.solve();
		if (pb.isFeasible()) {
			do {
				System.out.print("Solution: ");
				for (int i = 0; i < vars.length; i++) {
					System.out.print(vars[i].getVal());
				}
				System.out.println("");
			} while (pb.nextSolution() == Boolean.TRUE);
		}
		assertEquals(4, pb.getSolver().getNbSolutions());
	}


	public void testNQueen5() {
		nQueen(5);
	}

	public void testNQueen6() {
		nQueen(6);
	}

	public void testNQueen7() {
		nQueen(7);
	}

	private static final int star = Integer.MAX_VALUE;


	private static final int[] NBSols = new int[]{0, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200, 73712};

	/**
	 * Ajoute � l'automate tous les tuples de taille n contenant au moins
	 * 2 valeurs identiques. => l'automate encode un alldifferent
	 */

	public void genereAlldiffAutom(List<int[]> tuples, int n) {
		// tuples.add(new int[]{1, 1, 1, 1, 1});
		int[] tuple = new int[n];
		for (int i = 0; i < n; i++)
			tuple[i] = star;
		for (int value = 0; value < tuple.length; value++) {
			for (int i = 0; i < n; i++) {
				for (int j = i + 1; j < n; j++) {
					tuple[i] = value;
					tuple[j] = value;
					int[] t = new int[n];
					System.arraycopy(tuple, 0, t, 0, tuple.length);
					tuples.add(t);
					//afficheNogood(t);
					tuple[i] = star;
					tuple[j] = star;
				}
			}
		}
	}


	/**
	 * Ajoute � l'automate tous les tuples associ�s
	 * aux diagonales d'un �chiquer de taille n
	 */
	public void genereLeftTuples(List<int[]> tuples, int n) {
		int[] tuple = new int[n];
		int nbNogood = 0;
		for (int i = 0; i < n; i++)
			tuple[i] = star;
		for (int l = 0; l < n; l++) {
			for (int i = 0; i < n; i++) {
				for (int j = l + 1; j < n; j++) {
					if ((i + j - l) < n) {
						tuple[l] = i;
						tuple[j] = i + j - l;
						int[] t = new int[n];
						//afficheNogood(tuple);
						System.arraycopy(tuple, 0, t, 0, tuple.length);
						tuples.add(t);
						nbNogood++;
						tuple[l] = star;
						tuple[j] = star;
					}
					if ((i + l - j) >= 0) {
						tuple[l] = i;
						tuple[j] = i + l - j;
						int[] t = new int[n];
						//afficheNogood(tuple);
						System.arraycopy(tuple, 0, t, 0, tuple.length);
						tuples.add(t);
						nbNogood++;
						tuple[l] = star;
						tuple[j] = star;
					}
				}
			}
		}
		//System.out.println("Nombre :  " + nbNogood);
	}

	public static void afficheNogood(int[] noGood) {
		for (int i = 0; i < noGood.length; i++) {
			System.out.print(" " + noGood[i]);
		}
		System.out.println();
	}

	/**
	 * Construit une contrainte contenant tous les tuples
	 * interdits des n-reines et v�rifie le nombre de solutions
	 */
	public void nQueen(int n) {
		Problem pb = new Problem();
		IntDomainVar[] reines = new IntDomainVar[n];
		int[] min = new int[n];
		int[] max = new int[n];
		for (int i = 0; i < n; i++) {
			reines[i] = pb.makeEnumIntVar("reine-" + i, 0, n - 1);
			min[i] = 0;
			max[i] = n - 1;
		}
		long tps = System.currentTimeMillis();

		List<int[]> tuplesAllDiff = new LinkedList<int[]>();
		genereAlldiffAutom(tuplesAllDiff, n);
		genereLeftTuples(tuplesAllDiff, n);
		pb.post(pb.regular(reines, tuplesAllDiff, min, max));


		pb.getSolver().setValSelector(new RandomIntValSelector(120));
		pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, 112));
		pb.solveAll();

		int nbsolution = pb.getSolver().getNbSolutions();
		int nbNode = ((NodeLimit) pb.getSolver().getSearchSolver().limits.get(1)).getNbTot();
		System.out.println("TestsAutomate test7(" + n + " reines) : " + nbsolution + " nodes " + nbNode + " tps " + (System.currentTimeMillis() - tps));
		assertEquals(NBSols[n - 1], nbsolution);

	}

	public Constraint makeKnapsack(AbstractProblem pb, int[] coefs, IntDomainVar[] vars, IntDomainVar charge) {
		Constraint knap = null;
		int n = vars.length + 1;
		//nodes[i] : la liste des noeuds du graphe a la couche i
		ArrayList<Node>[] nodes = new ArrayList[n + 1];
		//transitions[i] : la liste des transitions de la couche i a i+1
		ArrayList<Transition>[] transitions = new ArrayList[n];

		nodes[0] = new ArrayList<Node>();
		nodes[0].add(new Node(0, 0, 0));
		int nodeIdx = 1;
		for (int i = 1; i < n; i++) {
			nodes[i] = new ArrayList<Node>();
			transitions[i - 1] = new ArrayList<Transition>();
			for (Iterator it = nodes[i - 1].iterator(); it.hasNext();) {
				Node pnode = (Node) it.next();
				int cidx = pnode.idx;
				int cb = pnode.b;
				if (vars[i - 1].canBeInstantiatedTo(0)) {
					Node existingNode = isNodeAlreadyAvailable(nodes[i], cb);
					if (existingNode == null) {
						existingNode = new Node(nodeIdx, cb, i);
						nodeIdx++;
						nodes[i].add(existingNode);
					}
					//tester si il existe un noeud de capacite cb a la couche i

					transitions[i - 1].add(new Transition(cidx, 0, existingNode.idx));
				}
				if (vars[i - 1].canBeInstantiatedTo(1)) {
					int newCoef = cb + coefs[i - 1];
					Node existingNode = isNodeAlreadyAvailable(nodes[i], newCoef);
					if (existingNode == null) {
						existingNode = new Node(nodeIdx, newCoef, i);
						nodeIdx++;
						nodes[i].add(existingNode);
					}
					transitions[i - 1].add(new Transition(cidx, 1, existingNode.idx));
				}
			}
		}
		transitions[n - 1] = new ArrayList<Transition>();
		for (Iterator it = nodes[n - 1].iterator(); it.hasNext();) {
			Node lnode = (Node) it.next();
			transitions[n - 1].add(new Transition(lnode.idx, lnode.b, nodeIdx));
		}
		List<Transition> t = new LinkedList<Transition>();
		for (int i = 0; i < transitions.length; i++) {
			ArrayList<Transition> transition = transitions[i];
			for (Iterator<Transition> it = transition.iterator(); it.hasNext();) {
				t.add(it.next());
			}
		}

		List<Integer> fs = new LinkedList<Integer>();
		fs.add(nodeIdx);

		DFA auto = new DFA(t, fs, n);
		// post the constraint
		IntDomainVar[] vs = new IntDomainVar[n];
		for (int i = 0; i < n - 1; i++) {
			vs[i] = vars[i];
		}
		vs[n - 1] = charge;
		knap = pb.regular(auto, vs);
		return knap;
	}

	public Node isNodeAlreadyAvailable(ArrayList<Node> lnode, int c) {
		for (Iterator<Node> it = lnode.iterator(); it.hasNext();) {
			Node node = it.next();
			if (node.b == c) return node;
		}
		return null;
	}

	public class Node {
		int idx;
		int b;
		int layer;

		public Node(int idx, int b, int layer) {
			this.idx = idx;
			this.b = b;
			this.layer = layer;
		}
	}

	public void testKnapsack() {
		Problem pb = new Problem();
		int n = 10;
		IntDomainVar[] bvars = pb.makeEnumIntVarArray("b", n, 0, 1);
		IntDomainVar charge = pb.makeBoundIntVar("charge", 0, 4000);
		int[] coefs = new int[n];
		Random rand = new Random(100);
		int[] coef = new int[]{2000, 4000};
		for (int i = 0; i < coefs.length; i++) {
			coefs[i] = coef[rand.nextInt(2)];
		}
		//pb.post(pb.eq(pb.scalar(,)));
		long tps = System.currentTimeMillis();
		//Constraint knapsack = pb.eq(pb.scalar(coefs,bvars),charge);
		Constraint knapsack = makeKnapsack(pb, coefs, bvars, charge);

		System.out.println("tps construction " + (System.currentTimeMillis() - tps));
		pb.post(knapsack);
		tps = System.currentTimeMillis();
		pb.solveAll();
		System.out.println("tps resolution " + (System.currentTimeMillis() - tps));
		/*if (pb.isFeasible()) {
					do {
						for (int i = 0; i < n; i++) {
							System.out.print(coefs[i] + "*" + bvars[i].getVal() + " ");
						}
						System.out.println(" = " + charge.getVal());
					} while (pb.nextSolution() == Boolean.TRUE);
				} else System.out.println("no solution");*/
		System.out.println("" + pb.getSolver().getNbSolutions());
	}

	public void testAnotherRegexp() {
		for (int k = 0; k < 10; k++) {
			System.out.println("*************************" + k);
			Problem pb = new Problem();
			int longueur = 10;

			int n = 4;
			IntDomainVar[] vars = new IntDomainVar[n];
			for (int i = 0; i < vars.length; i++) {
				vars[i] = pb.makeEnumIntVar("v" + i, 0, 9);
			}
			String regexp = "(1|2|3|4|5|6|7|8|9)(0|1|2|3|4|5|6|7|8|9)(0)(" + k + ")";
			// post the constraint
			pb.post(pb.regular(regexp,vars));

			pb.solve();
			int tour = 0;
			if (pb.isFeasible() == Boolean.TRUE) {

				do {
					int port = 0;
					System.out.println("------------Solution---------");
					for (int i = 0; i < pb.getNbIntVars(); i++) {
						int valPort = ((IntDomainVar) pb.getIntVar(i))
								.getVal();
						double mult = Math.pow(10, pb.getNbIntVars()
								- 1 - i);
						port += valPort * mult;
						System.err.println("au tour " + tour + " port = " + port + ", valPort = " + valPort + ", mult =" + mult);
						System.out.println("" + pb.getIntVar(i) + " = " + ((IntDomainVar) (pb.getIntVar(i))).getVal());
					}
					tour++;
				} while (pb.nextSolution() == Boolean.TRUE && tour < longueur);
			}
		}
	}


}

