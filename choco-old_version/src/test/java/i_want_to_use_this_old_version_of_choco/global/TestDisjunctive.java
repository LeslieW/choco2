/**
 *
 */
package i_want_to_use_this_old_version_of_choco.global;

import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.Solver;
import i_want_to_use_this_old_version_of_choco.global.scheduling.Disjunctive;
import i_want_to_use_this_old_version_of_choco.global.scheduling.DisjunctiveSettings;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntValSelector;
import i_want_to_use_this_old_version_of_choco.integer.search.RandomIntVarSelector;
import static junit.framework.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;


/**
 * @author nono
 *
 */
public class TestDisjunctive {

	public static int nbSolve=2;

	public static int defaultSolSize=50;

	public static int defaultProbSize=4;

	public static boolean random=true;

	private DisjunctiveSettings settings=new DisjunctiveSettings();

	private Problem pb;

	@BeforeClass
	public static void init() {
		Solver.setVerbosity(Solver.SILENT);
	}

	protected IntDomainVar[] createVars(final int[] min,final int[] max) {
		IntDomainVar[] vars=new IntDomainVar[max.length];
		for (int i = 0; i < max.length; i++) {
			vars[i]=pb.makeBoundIntVar("X_"+i, min[i], max[i]);
		}
		return vars;
	}

	protected void solveAll(final int[] min,final int[] max,final int[] pt, final int nbSol) {
		pb=new Problem();
		pb.post(new Disjunctive(createVars(min, max),pt,settings));
		if(random) {
			pb.getSolver().setVarIntSelector(new RandomIntVarSelector(pb));
			pb.getSolver().setValIntSelector(new RandomIntValSelector());
		}
		pb.solveAll();
		assertEquals("nombre solutions",nbSol,pb.getSolver().getNbSolutions());
		this.pb=new Problem();

	}


	public void launchAllRules(final int[] min,final int[] max,final int[] pt,final int nbSol) {
		for (int i = 0; i < nbSolve; i++) {
			settings=new DisjunctiveSettings();
			settings.setOnlyOverloadChecking();
			solveAll(min, max, pt, nbSol);
			//			Solver.setVerbosity(Solver.SEARCH);
			for (int rule = 0; rule < DisjunctiveSettings.LAST_RULE; rule++) {
				settings.setSingleRule(rule);
				solveAll(min, max, pt, nbSol);
			}
			settings.setOnlyDetectablePrecedence();
			settings.noNotFirstNotLast();
			settings.setOnlyOverloadChecking();
			solveAll(min, max, pt, nbSol);
			settings.setEdgeFinding();
			solveAll(min, max, pt, nbSol);
		}
	}

	@Test
	public void testToyProblem() {
		final int[] pt={4,6,2};
		final int[] min={1,3,6};
		final int[] max={4,7,11};
		launchAllRules(min, max, pt, 1);
	}

	@Test
	public void testToyProblem2() {
		final int[] pt={11,10,5};
		final int[] min={0,1,14};
		final int[] max={14,17,30};
		launchAllRules(min, max, pt, 238);
	}

	@Test
	public void testToyProblem3() {
		final int[] pt={5,16,9};
		final int[] min={0,0,0};
		final int[] max={25,14,21};
		launchAllRules(min, max, pt, 6);
	}
	@Test
	public void testToyProblem4() {
		final int[] pt={4,5,6};
		final int[] min={0,0,0};
		final int[] max={11,10,9};
		launchAllRules(min, max, pt, 6);
	}


	private int[] genProcessingTimes(final int size) {
		final Random rnd=new Random();
		final int[] p=new int[size];
		final int max=20;
		for (int i = 0; i < p.length; i++) {
			p[i]=rnd.nextInt(max)+1;
		}
		return p;
	}

	private int[][] permutSolution() {
		final int[][] res=new int[2][defaultSolSize];
		res[1]=this.genProcessingTimes(defaultSolSize);
		for (int i = 1; i < res[0].length; i++) {
			res[0][i]=res[0][i-1]+res[1][i-1];
		}
		return res;
	}

	private int[][] permutProblem() {
		int[][] res=new int[3][defaultProbSize];
		res[2]=this.genProcessingTimes(defaultProbSize);
		int total=0;
		for (int i = 0; i < res[2].length; i++) {total+=res[2][i];}
		for (int i = 0; i < res[1].length; i++) {res[1][i]=total-res[2][i];}
		return res;
	}

	@Test
	public void gotToBeTrue() {
		final int mem=nbSolve;
		nbSolve=1;
		for (int i = 0; i < nbSolve; i++) {
			final int[][] tab=permutSolution();
			launchAllRules(tab[0], tab[0], tab[1], 1);
		}
		nbSolve=mem;
	}

	public final static int factorielle(final int n) {
		if(n>1) {return n*factorielle(n-1);}
		else {return 1;}
	}

	@Test
	public void gotToBeFactoriel() {
		for (int i = 0; i < Math.min(nbSolve,2); i++) {
			final int[][] tab=permutProblem();
			launchAllRules(tab[0], tab[1], tab[2], factorielle(defaultProbSize));
		}
	}


	//@Test
	public void testToyProblem5() {
		final int memo=nbSolve;
		nbSolve=5;
		final int[] pt={11,10,5,8,9};
		final int[] min={0,0,0,0,0};
		final int[] max={32,33,38,35,34};
		solveAll(min, max,pt, 120);
		nbSolve=memo;
	}

	//@Test
	public void testToyProblem6() {
		final int memo=nbSolve;
		nbSolve=5;
		final int[] pt={11,13,5,24,10,4};
		final int[] min={0,0,0,0,0,0};
		final int[] max={56,54,62,43,57,63};
		solveAll(min, max,pt, 120);
		nbSolve=memo;
	}


}
