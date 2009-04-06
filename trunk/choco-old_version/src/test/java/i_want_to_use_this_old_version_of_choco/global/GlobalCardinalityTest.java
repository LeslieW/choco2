package i_want_to_use_this_old_version_of_choco.global;

import i_want_to_use_this_old_version_of_choco.*;
import i_want_to_use_this_old_version_of_choco.global.matching.GlobalCardinality;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.search.MinDomain;
import i_want_to_use_this_old_version_of_choco.integer.search.MinVal;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntValSelector;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntVarSelector;
import i_want_to_use_this_old_version_of_choco.mem.trailing.EnvironmentTrailing;
import i_want_to_use_this_old_version_of_choco.search.NodeLimit;
import junit.framework.TestCase;

import java.util.Date;
import java.util.Random;

/**
 * Tests for the GlobalCardinality constraint.
 */
public class GlobalCardinalityTest extends TestCase {


	public void testGCC() {
		System.out.println("Dummy GlobalCardinality currentElement...");
		Problem pb = new Problem(new EnvironmentTrailing());

		IntDomainVar peter = pb.makeEnumIntVar("Peter", 0, 1);
		IntDomainVar paul = pb.makeEnumIntVar("Paul", 0, 1);
		IntDomainVar mary = pb.makeEnumIntVar("Mary", 0, 1);
		IntDomainVar john = pb.makeEnumIntVar("John", 0, 1);
		IntDomainVar bob = pb.makeEnumIntVar("Bob", 0, 2);
		IntDomainVar mike = pb.makeEnumIntVar("Mike", 1, 4);
		IntDomainVar julia = pb.makeEnumIntVar("Julia", 2, 4);
		IntDomainVar[] vars = new IntDomainVar[]{peter, paul, mary, john, bob, mike, julia};

		Constraint gcc = pb.globalCardinality(vars, 0, 4, new int[]{1, 1, 1, 0, 0}, new int[]{2, 2, 1, 2, 2});
		pb.post(gcc);

		try {
			pb.propagate();
			assertEquals(2, bob.getInf());
			assertEquals(2, bob.getSup());
			julia.remVal(3);
			pb.propagate();
		} catch (ContradictionException e) {
			assertTrue(false);
		}
	}

	public void testBoundGcc() {
		Problem pb = new Problem();
		IntDomainVar v1 = pb.makeBoundIntVar("v1", 2, 2);
		IntDomainVar v2 = pb.makeBoundIntVar("v2", 1, 2);
		IntDomainVar v3 = pb.makeBoundIntVar("v3", 2, 3);
		IntDomainVar v4 = pb.makeBoundIntVar("v4", 2, 3);
		IntDomainVar v5 = pb.makeBoundIntVar("v5", 1, 4);
		IntDomainVar v6 = pb.makeBoundIntVar("v6", 3, 4);

		pb.post(pb.boundGcc(new IntDomainVar[]{v1, v2, v3, v4, v5, v6}, 1, 4,
				new int[]{1, 1, 1, 2},
				new int[]{3, 3, 3, 3}));
		try {
			pb.propagate();
			assertTrue(v2.isInstantiatedTo(1));
			assertTrue(v5.isInstantiatedTo(4));
			assertTrue(v6.isInstantiatedTo(4));
		} catch (ContradictionException e) {
			assertTrue(false);
		}

		System.out.println(pb.varsToString());
	}

	public void testBoundGcc4() {
		Problem pb = new Problem();
		IntDomainVar v1 = pb.makeBoundIntVar("v1", 1, 2);
		IntDomainVar v2 = pb.makeBoundIntVar("v2", 1, 2);
		IntDomainVar v3 = pb.makeBoundIntVar("v3", 1, 3);
		IntDomainVar v4 = pb.makeBoundIntVar("v4", 2, 3);
		IntDomainVar v5 = pb.makeBoundIntVar("v5", 2, 4);
		IntDomainVar v6 = pb.makeBoundIntVar("v6", 3, 4);

		pb.post(pb.boundGcc(new IntDomainVar[]{v1, v2, v3, v4, v5, v6}, 1, 4,
				new int[]{3, 1, 1, 1},
				new int[]{3, 5, 5, 5}));
		try {
			pb.propagate();
			assertTrue(v1.isInstantiatedTo(1));
			assertTrue(v2.isInstantiatedTo(1));
			assertTrue(v3.isInstantiatedTo(1));
		} catch (ContradictionException e) {
			assertTrue(false);
		}

		System.out.println(pb.varsToString());
	}

	public void testBoundGcc5() {
		Problem pb = new Problem();
		IntDomainVar v1 = pb.makeBoundIntVar("v1", 1, 4);
		IntDomainVar v2 = pb.makeBoundIntVar("v2", 1, 4);
		IntDomainVar v3 = pb.makeBoundIntVar("v3", 1, 4);
		IntDomainVar v4 = pb.makeBoundIntVar("v4", 1, 3);
		IntDomainVar v5 = pb.makeBoundIntVar("v5", 1, 3);
		IntDomainVar v6 = pb.makeBoundIntVar("v6", 1, 3);

		pb.post(pb.boundGcc(new IntDomainVar[]{v1, v2, v3, v4, v5, v6}, 1, 4,
				new int[]{1, 1, 1, 3},
				new int[]{5, 5, 5, 5}));
		try {
			pb.propagate();
			assertTrue(v1.isInstantiatedTo(4));
			assertTrue(v2.isInstantiatedTo(4));
			assertTrue(v3.isInstantiatedTo(4));
		} catch (ContradictionException e) {
			assertTrue(false);
		}
		System.out.println(pb.varsToString());
	}

	public void testBoundGcc6() {
		Problem pb = new Problem();
		IntDomainVar v1 = pb.makeBoundIntVar("v1", 1, 4);
		IntDomainVar v2 = pb.makeBoundIntVar("v2", 1, 4);
		IntDomainVar v3 = pb.makeBoundIntVar("v3", 1, 4);
		IntDomainVar v4 = pb.makeEnumIntVar("v4", 1, 3);
		IntDomainVar v5 = pb.makeEnumIntVar("v5", 1, 3);
		IntDomainVar v6 = pb.makeEnumIntVar("v6", 1, 3);

		pb.post(pb.boundGcc(new IntDomainVar[]{v1, v2, v3, v4, v5, v6}, 1, 4,
				new int[]{1, 3, 1, 1},
				new int[]{5, 3, 5, 5}));
		try {
			v4.removeVal(2,-1);
			v5.removeVal(2,-1);
			v6.removeVal(2,-1);
			pb.propagate();
			assertTrue(v1.isInstantiatedTo(2));
			assertTrue(v2.isInstantiatedTo(2));
			assertTrue(v3.isInstantiatedTo(2));
		} catch (ContradictionException e) {
			assertTrue(false);
		}
		System.out.println(pb.varsToString());
	}


	public void testBoundGcc3() {
		int n = 5;
		Problem pb = new Problem();
		IntDomainVar[] vars = new IntDomainVar[n];
		for (int i = 0; i < n; i++) {
			vars[i] = pb.makeEnumIntVar("var " + i, 1, n);
		}
		int[] LB2 = {0, 1, 1, 0, 3};
		int[] UB2 = {0, 1, 1, 0, 3};
		pb.post(pb.boundGcc(vars, 1, n, LB2, UB2));
		//pb.post(pb.globalCardinality(vars,1,n,LB2,UB2));

		int cpt = 1;
		pb.solve();
		for (int i = 0; i < n; i++) {
			System.out.print("" + vars[i].getVal());
		}
		System.out.println("");
		while (pb.nextSolution() == Boolean.TRUE) {
			cpt++;
			for (int i = 0; i < n; i++) {
				System.out.print("" + vars[i].getVal());
			}
			System.out.println("");
		}
		System.out.println("nb Sol " + cpt + " time " + pb.getSolver().getSearchSolver().getTimeCount());
		assertEquals(20, cpt);
	}

	public void testBoundGcc2() {
		int n = 3;
		Problem pb = new Problem();
		IntDomainVar[] vars = new IntDomainVar[n];
		for (int i = 0; i < n; i++) {
			vars[i] = pb.makeEnumIntVar("var " + i, 1, n);
		}
		int[] LB2 = {0, 0, 2};
		int[] UB2 = {2, 2, 3};
		pb.post(pb.boundGcc(vars, 1, 3, LB2, UB2));
		//pb.post(pb.globalCardinality(vars,1,3,LB2,UB2));

		int cpt = 1;
		pb.solve();
		for (int i = 0; i < n; i++) {
			System.out.print("" + vars[i].getVal());
		}
		System.out.println("");
		while (pb.nextSolution() == Boolean.TRUE) {
			cpt++;
			for (int i = 0; i < n; i++) {
				System.out.print("" + vars[i].getVal());
			}
			System.out.println("");
		}
		System.out.println("nb Sol " + cpt + " time " + pb.getSolver().getSearchSolver().getTimeCount());
		assertEquals(7, cpt);
	}

	public void tooLongTestBugTPetit1(boolean bound) {
		int n = 10;
		Problem pb = new Problem();
		IntDomainVar[] vars = new IntDomainVar[n];
		for (int i = 0; i < n; i++) {
			vars[i] = pb.makeEnumIntVar("var " + i, 1, n);
		}
		int[] LB = {0, 1, 2, 0, 0, 0, 3, 0, 0, 0};
		int[] UB = {5, 2, 2, 9, 10, 9, 5, 1, 5, 5};
		System.out.println("premiere gcc :");
		if (bound) {
			pb.post(pb.boundGcc(vars, 1, 10, LB, UB));
		} else {
			pb.post(pb.globalCardinality(vars, 1, 10, LB, UB));
		}

		int[] LB2 = {0, 0, 0, 0, 0, 4, 0, 0, 0, 0};
		int[] UB2 = {10, 10, 10, 10, 10, 10, 10, 10, 10, 10};
		System.out.println("deuxieme gcc :");
		if (bound) {
			pb.post(pb.boundGcc(vars, 1, 10, LB2, UB2));
		} else {
			pb.post(pb.globalCardinality(vars, 1, 10, LB2, UB2));
		}

		int cpt = 1;
		pb.solve();
		for (int i = 0; i < n; i++) {
			System.out.print("" + vars[i].getVal());
		}
		System.out.println("");
		while (pb.nextSolution() == Boolean.TRUE) {
			cpt++;
			/*for (int i = 0; i < n; i++) {
							System.out.print("" + vars[i].getVal());
						}
						System.out.println(""); */
		}
		System.out.println("nb Sol " + cpt + " time " + pb.getSolver().getSearchSolver().getTimeCount() + " nbNode " + pb.getSolver().getSearchSolver().getNodeCount());
		assertEquals(12600, cpt);
	}

	public void toolongtestBoundBugTPetit1() {
		tooLongTestBugTPetit1(true);
	}

	public void toolongtestBugTPetit1() {
		tooLongTestBugTPetit1(false);
	}


	public void testBugTPetit2() {
		Problem pb = new Problem();
		IntDomainVar[] vars = new IntDomainVar[2];
		vars[0] = pb.makeEnumIntVar("x " + 0, 1, 3);
		vars[1] = pb.makeEnumIntVar("x " + 1, 2, 7);
		IntDomainVar[] absVars = new IntDomainVar[6];
		for (int i = 0; i < absVars.length; i++) {
			absVars[i] = pb.makeEnumIntVar("V" + i, 0, 6);
		}

		pb.post(pb.or(pb.eq(pb.minus(vars[0], vars[1]), absVars[0]),
				pb.eq(pb.minus(vars[1], vars[0]), absVars[0])));

		int[] LB = {0, 0, 0, 0, 2, 0, 0};
		int[] UB = {6, 6, 6, 6, 6, 6, 6};
		Constraint gcc = pb.globalCardinality(absVars,
				0,
				6,
				LB,
				UB);
		pb.post(gcc);
		pb.solve();
	}

	public void testlatinSquareGCC() {
		latinSquareGCC(false);
	}

	public void testlatinSquareBoundGCC() {
		latinSquareGCC(true);
	}

	public void latinSquareGCC(boolean bound) {
		System.out.println("Latin Square Test...");
		// Toutes les solutions de n=5 en 90 sec  (161280 solutions)
		final int n = 4;
		final int[] soluces = new int[]{1, 2, 12, 576, 161280};

		// Problem
		Problem myPb = new Problem();

		// Variables
		IntDomainVar[] vars = new IntDomainVar[n * n];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++) {
				vars[i * n + j] = myPb.makeEnumIntVar("C" + i + "_" + j, 1, n);
			}

		// Constraints
		for (int i = 0; i < n; i++) {
			int[] low = new int[n];
			int[] up = new int[n];
			IntDomainVar[] row = new IntDomainVar[n];
			IntDomainVar[] col = new IntDomainVar[n];
			for (int x = 0; x < n; x++) {
				row[x] = vars[i * n + x];
				col[x] = vars[x * n + i];
				low[x] = 0;
				up[x] = 1;
			}
			if (bound) {
				myPb.post(myPb.boundGcc(row, 1, n, low, up));
				myPb.post(myPb.boundGcc(col, 1, n, low, up));
			} else {
				myPb.post(myPb.globalCardinality(row, 1, n, low, up));
				myPb.post(myPb.globalCardinality(col, 1, n, low, up));
			}
		}

		myPb.solve(true);

		assertEquals(soluces[n - 1], myPb.getSolver().getNbSolutions());
		System.out.println("LatinSquare Solutions : " + myPb.getSolver().getNbSolutions() + " " + myPb.getSolver().getSearchSolver().getTimeCount());
	}

	public void testGccEmi() {
		int n = 8;
		System.out.println("Le probleme des " + n + " reines");
		Problem nreine = new Problem();
		// mod�lisation par Ligne
		IntDomainVar[] ligne = new IntDomainVar[n];
		int i, j;
		for (i = 0; i < n; i++)
			ligne[i] = nreine.makeEnumIntVar("Ligne " + i, 0, n - 1);
		// allez hop les constrainnnnnts
		// constraint sur la colonne (ligne d�ja faite)
		for (i = 0; i < n; i++)
			for (j = i + 1; j < n; j++) {
				// contrainte ligne pas � g�rer grace � la mod�lisation
				// contrainte colonne
				nreine.post(nreine.neq(ligne[i], ligne[j]));
				nreine.post(nreine.neq(ligne[i], nreine.plus(ligne[j], Math.abs(i - j))));
				nreine.post(nreine.neq(ligne[i], nreine.minus(ligne[j], Math.abs(i - j))));
			}
		long tps = System.currentTimeMillis();
		//nreine.getSolver().setVarSelector(new MinDomain(nreine, ligne));
		int[] lb = new int[n];
		int[] ub = new int[n];
		for (int ii = 0; ii < 8; ii++) {
			lb[ii] = 0;
			ub[ii] = n - 1;
		}
		lb[0] = 2; // force 2 reine sur la premiere colonne, interdit donc une solution


		GlobalCardinality Gcc = new GlobalCardinality(ligne, 0, 7, lb, ub);

		nreine.post(Gcc);
		nreine.solve(); // en solveAll, ca sort une erreur avec le choco 1_02_3 au passage
		assertTrue(nreine.getSolver().getNbSolutions() == 0);
		if (nreine.getSolver().getNbSolutions() > 0) {
			System.out.print("Solution : ");
			for (IntDomainVar l : ligne) //foreach
			{
				System.out.print(l.getVal() + "/"); // les solutions
			}
			System.out.println();
		}


		tps = System.currentTimeMillis() - tps;
		int nbNode = ((NodeLimit) nreine.getSolver().getSearchSolver().limits.get(1)).getNbTot();
		System.out.println("temps (en ms) : " + tps + " Noeud : " + nbNode + " Nombre de solutions : " + nreine.getSolver().getNbSolutions());

	}


	public void testRandomBoundGcc() {
		randomGCCTest(true);
	}

	public void testRandomGcc() {
		randomGCCTest(false);
	}

	public void randomGCCTest(boolean bound) {
		System.out.println("Random GlobalCardinality currentElement...");
		for (int seed = 0; seed < 20; seed++) {
			int n = 6;
			int[] min = new int[]{1, 1, 0, 0, 0, 1};
			int[] max = new int[]{1, 2, 0, 2, 3, 1};
			Problem pb = new Problem();
			IntDomainVar[] vars = new IntDomainVar[n];
			Random rand = new Random(seed + 102);
			for (int i = 0; i < n; i++) {
				vars[i] = pb.makeEnumIntVar("var " + i, 0, n - 1);
			}
			for (int i = 0; i < vars.length; i++) {
				int val1 = rand.nextInt(n);
				int val2 = rand.nextInt(n);
				int val3 = rand.nextInt(n);
				try {
					vars[i].remVal(val1);
					vars[i].remVal(val2);
					vars[i].remVal(val3);
				} catch (ContradictionException e) {
					e.printStackTrace();
				}
			}
			int nbsol = getNBSolByBruteForce(vars, n, seed, min, max);
			Constraint gcc;
			if (!bound) {
				gcc = pb.globalCardinality(vars, 0, n - 1, min, max);
			} else {
				gcc = pb.boundGcc(vars, 0, n - 1, min, max);
			}

			pb.post(gcc);
			pb.getSolver().setValSelector(new RandomIntValSelector(seed));
			pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, seed + 10));
			pb.solveAll();
			System.out.println("" + pb.getSolver().getNbSolutions() + "?=" + nbsol);
			assertEquals(pb.getSolver().getNbSolutions(), nbsol);
		}
	}

	public int getNBSolByBruteForce(IntDomainVar[] initdom, int n, int seed, int[] min, int[] max) {
		int nbsol = 0;
		int[] stuple = nextLexicoTuple(null, n - 1, n);
		while (stuple != null) {
			int[] tup = new int[stuple.length];
			System.arraycopy(stuple, 0, tup, 0, n);
			int[] occ = new int[n];
			boolean isCorrect = true;
			for (int i = 0; i < n; i++) {
				if (!initdom[i].canBeInstantiatedTo(tup[i])) {
					isCorrect = false;
					break;
				}
			}
			if (isCorrect) {
				for (int j = 0; j < tup.length; j++) {
					occ[tup[j]]++;
				}
				boolean isValid = true;
				for (int i = 0; i < occ.length; i++) {
					if (!(occ[i] >= min[i] && occ[i] <= max[i])) {
						isValid = false;
						break;
					}
				}
				if (isValid) nbsol++;
			}
			stuple = nextLexicoTuple(stuple, n - 1, n);
		}
		return nbsol;
	}

	public int[] nextLexicoTuple(int[] t, int maxval, int s) {
		if (t == null) return new int[s];
		for (int i = (s - 1); i >= 0; i--) {
			if (t[i] < maxval) {
				t[i]++;
				for (int j = i + 1; j < s; j++) {
					t[j] = 0;
				}
				return t;
			}
		}
		return null;
	}

	public void testGCCEmilien() {
		int n = 8;
		System.out.println("Le probleme des " + n + " reines");

		Problem nreine = new Problem();

		// mod�lisation par Ligne
		IntDomainVar[] ligne = new IntDomainVar[n];

		int i, j;

		for (i = 0; i < n; i++)
			ligne[i] = nreine.makeEnumIntVar("Ligne " + i, 0, n - 1);

		// allez hop les constrainnnnnts

		// constraint sur la colonne (ligne d�ja faite)
		for (i = 0; i < n; i++)
			for (j = i + 1; j < n; j++) {
				// contrainte ligne pas � g�rer grace � la mod�lisation
				// contrainte colonne
				nreine.post(nreine.neq(ligne[i], ligne[j]));
				nreine.post(nreine.neq(ligne[i], nreine.plus(ligne[j], Math.abs(i - j))));
				nreine.post(nreine.neq(ligne[i], nreine.minus(ligne[j], Math.abs(i - j))));
			}
		long tps = System.currentTimeMillis();
		//nreine.getSolver().setVarSelector(new MinDomain(nreine, ligne));

		int[] lb = new int[n];
		int[] ub = new int[n];
		for (int ii = 0; ii < 8; ii++) {
			lb[ii] = 0;
			ub[ii] = n - 1;
		}

		/*
			lb[0] = 2;
			ub[0] = 2;
			   // pas de solutions => n'entraine pas de bugs
			*/

		lb[7] = 2;
		ub[7] = 2;
		//  1 solution => bug

		GlobalCardinality Gcc = new GlobalCardinality(ligne, 0, 7, lb, ub);

		nreine.post(Gcc);
		nreine.solve(); // en solveAll, ca sort une erreur bizarre au passage
		// a tester le solve All !!!

		if (nreine.getSolver().getNbSolutions() > 0) {
			assertTrue(false);
			System.out.print("Solution : ");
			for (IntDomainVar l : ligne) //foreach
			{
				System.out.print(l.getVal() + "/"); // la solution
			}
			System.out.println();
		}


		tps = System.currentTimeMillis() - tps;
		int nbNode = ((NodeLimit) nreine.getSolver().getSearchSolver().limits.get(1)).getNbTot();
		System.out.println("temps (en ms) : " + tps + " Noeud : " + nbNode + " Nombre de solutions : " + nreine.getSolver().getNbSolutions());

	}


	public static void robustBoundGccTest() {
		int n = 5;
		Problem pb = new Problem();
		IntDomainVar[] card = pb.makeEnumIntVarArray("c", n, 0, n);
		pb.post(pb.eq(pb.sum(card), n));
		//Solver.setVerbosity(Solver.SEARCH);
		//pb.getSolver().setLoggingMaxDepth(100);
		int totnbsol = 0;
		pb.solve();
		do {
			Problem pb2 = new Problem();
			IntDomainVar[] vs = pb2.makeBoundIntVarArray("c", n, 1, n);
			int[] min = new int[n];
			int[] max = new int[n];
			for (int i = 0; i < max.length; i++) {
				min[i] = card[i].getVal();
				max[i] = card[i].getVal();
			}
			pb2.post(pb2.boundGcc(vs, 1, n, min, max));
			pb2.solveAll();
			totnbsol += pb2.getSolver().getNbSolutions();
			int aseertnbsol = assertNbSol(card, n);
			if (pb2.getSolver().getNbSolutions() != aseertnbsol) {
				System.out.println(pb2.getSolver().getNbSolutions() + " " + aseertnbsol);
				for (int i = 0; i < n; i++) {
					System.out.print(" " + card[i].getVal());
				}
				System.out.println("");
			}
			assertEquals(pb2.getSolver().getNbSolutions(), aseertnbsol);
		} while (pb.nextSolution() == Boolean.TRUE);

		assertEquals(totnbsol, 3125);
		//if (pb.getSolver().getNbSolutions() != 3125)
		// throw new Error("stop " + seed);
	}

	public static int assertNbSol(IntDomainVar[] card, int n) {
		Problem pb2 = new Problem();
		IntDomainVar[] vs = pb2.makeEnumIntVarArray("c", n, 1, n);
		int[] min = new int[n];
		int[] max = new int[n];
		for (int i = 0; i < max.length; i++) {
			min[i] = card[i].getVal();
			max[i] = card[i].getVal();
		}
		pb2.post(pb2.globalCardinality(vs, 1, n, min, max));
		pb2.solveAll();
		return pb2.getSolver().getNbSolutions();
	}

	public static void testBoundGccVar() {
		for (int seed = 0; seed < 20; seed++) {
			int n = 5;
			Problem pb = new Problem();
			IntDomainVar[] vs = pb.makeBoundIntVarArray("v", n, 1, n);
			IntDomainVar[] card = pb.makeBoundIntVarArray("c", n, 0, n);
			//pb.post(pb.eq(pb.sum(card), n));
			pb.post(pb.boundGccVar(vs, 1, n, card));
			pb.getSolver().setValSelector(new RandomIntValSelector(seed));
			pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, seed + 10));

			pb.solveAll();
			System.out.println("" + pb.getSolver().getNbSolutions() + " nbnode " + pb.getSolver().getSearchSolver().getNodeCount() + " time " + pb.getSolver().getSearchSolver().getTimeCount());
			assertEquals(pb.getSolver().getNbSolutions(), 3125);
		}
	}

	public static void testBoundGccVarWithEnum() {
		for (int seed = 0; seed < 20; seed++) {
			int n = 5;
			Problem pb = new Problem();
			IntDomainVar[] vs = pb.makeEnumIntVarArray("v", n, 1, n);
			IntDomainVar[] card = pb.makeBoundIntVarArray("c", n, 0, n);
			//pb.post(pb.eq(pb.sum(card), n));
			pb.post(pb.boundGccVar(vs, 1, n, card));
			pb.getSolver().setValSelector(new RandomIntValSelector(seed));
			pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, seed + 10));

			pb.solveAll();
			System.out.println("" + pb.getSolver().getNbSolutions() + " nbnode " + pb.getSolver().getSearchSolver().getNodeCount() + " time " + pb.getSolver().getSearchSolver().getTimeCount());
			assertEquals(pb.getSolver().getNbSolutions(), 3125);
		}
	}

	public static void testBoundGccVarWithEnum2() {
		for (int seed = 0; seed < 10; seed++) {
			int n = 7;
			Problem pb = new Problem();
			IntDomainVar[] vs = pb.makeEnumIntVarArray("v", n, 4, n);
			IntDomainVar[] card = pb.makeBoundIntVarArray("c", n - 4 + 1, 1, 2);
			//pb.post(pb.eq(pb.sum(card), n));
			pb.post(pb.boundGccVar(vs, 4, n, card));
			//pb.post(new GlobalCardinalityVar(vs, 4, n, card));

			pb.getSolver().setValSelector(new RandomIntValSelector(seed));
			pb.getSolver().setVarSelector(new RandomIntVarSelector(pb, seed + 10));

			pb.solveAll();
			System.out.println("" + pb.getSolver().getNbSolutions() + " nbnode " + pb.getSolver().getSearchSolver().getNodeCount() + " time " + pb.getSolver().getSearchSolver().getTimeCount());
			assertEquals(pb.getSolver().getNbSolutions(), 2520);
		}
	}


	public static int f(int i, int j, int n) {
		if (j >= i)
			return (i * n - i * (i + 1) / 2 + j - i);
		else
			return f(j, i - 1, n);
	}

	public static void testIlogGColoring() {
		Problem pb = new Problem();

		long executionStart = System.currentTimeMillis();

		String arg = "31";
		int clique_size = Integer.parseInt(arg);
		System.out.println("Graph Coloring for " + clique_size
				+ " cliques. " + new Date());
		int n = (clique_size % 2 > 0) ? clique_size + 1 : clique_size;
		boolean redundant_constraint = true;

		int size = n * (n - 1) / 2;

		int i, j;
		int nbColors = n - 1;
		IntDomainVar[] vars = pb.makeEnumIntVarArray("vars", size, 0, nbColors - 1);

		IntDomainVar[][] cliques = new IntDomainVar[n][n - 1];
		for (i = 0; i < n; i++) {
			for (j = 0; j < n - 1; j++) {
				int node = f(i, j, n);
				IntDomainVar v = vars[node];
				cliques[i][j] = v;
			}
			Constraint constraintAllDiff = new BoundAllDiff(cliques[i], true);
			pb.post(constraintAllDiff);
		}

		// Redundant Constraint: every color is used at least n/2 times
		int[] colAr = new int[nbColors];
		for (int k = 0; k < nbColors; k++)
			colAr[k] = k;

		if (redundant_constraint) {
			//create two int arrays to pass to chocoDistribute()
			int[] low = new int[colAr.length];
			int[] up = new int[colAr.length];
			for (int l = 0; l < colAr.length; l++) {
				low[l] = (n / 2); // low[l] = 16
				//up[l] = nbColors - 1;  
				up[l] = 16;
			}
			pb.post(pb.boundGcc(vars, 0, nbColors - 1, low, up));
		}

		pb.getSolver().setVarSelector(new MinDomain(pb, vars));
		pb.getSolver().setValSelector(new MinVal());

		Solver s = pb.getSolver();
		s.setFirstSolution(true);
		s.generateSearchSolver(pb);
		s.launch();
		System.out.println("nb choice points " + pb.getSolver
				().getSearchSolver().getNodeCount());
		assertTrue(pb.getSolver().getSearchSolver().getNodeCount() <= 500);
		if (false)
			System.out.println("no solution found");
		else {
			//stop time
			long executionTime = System.currentTimeMillis() - executionStart;
			System.out.println("Execution time: " + executionTime + " msec");

			// print Solution
			System.out.println("Solution:");

			for (i = 0; i < n; i++) {
				System.out.println("\nClique " + i + ":");
				String str = new String();
				for (j = 0; j < n - 1; j++) {
					int node = f(i, j, n);
					int color = vars[node].getVal();
					str = str + " " + node + "=" + color;
				}
				System.out.println(str);
			}
		}
	}


  public void testSatisfied() {
        AbstractProblem pb = new Problem();
    IntDomainVar v1 = pb.makeEnumIntVar("v1", 1, 1);
    IntDomainVar v2 = pb.makeEnumIntVar("v2", 1, 1);
    IntDomainVar v3 = pb.makeEnumIntVar("v3", 2, 2);
    IntDomainVar v4 = pb.makeEnumIntVar("v4", 2, 2);
    Constraint c1 = pb.globalCardinality(new IntDomainVar[]{v1, v2, v3, v4}, 1, 2, new int[]{1, 1}, new int[]{2, 2});
    Constraint c2 = pb.globalCardinality(new IntDomainVar[]{v1, v2, v3, v4}, 1, 2, new int[]{1, 1}, new int[]{1, 3});
    System.out.println(c1.pretty());
    System.out.println(c2.pretty());
    assertTrue(c1.isSatisfied());
    assertFalse(c2.isSatisfied());
  }

}
