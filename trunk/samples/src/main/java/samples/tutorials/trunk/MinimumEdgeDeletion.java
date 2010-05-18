package samples.tutorials.trunk;

import choco.Choco;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.variables.integer.IntegerVariable;
import samples.tutorials.PatternExample;

import java.util.Arrays;
import java.util.Random;

import static choco.Choco.*;

/**
 * Let consider a set of N boolean variables and a binary constraint network (eq or neq).
 * The goal is to find an assignment minimizing the number of required edge, or constraint, deletion.
 * The problem is inspired from the Minimum Equivalence Deletion Problem
 * @author Arnaud Malapert</br> 
 * @since 22 mars 2009 version 2.0.3</br>
 * @version 2.0.3</br>
 */
public class MinimumEdgeDeletion extends PatternExample {

	protected IntegerVariable[] pairVars;

	protected IntegerVariable[] boolVars;

	protected IntegerVariable deletion;

	protected int nbBools;

	protected double pairProba;

	protected int nbPairs;

	protected int[] instantiated;


	protected Boolean[][] pairs;

	/**
	 * the function takes the following arguments: 
	 * the number of variable;
	 * the probability of an edge constraint;
	 * a seed (optional).
	 */
	@Override
	public void setUp(Object paramaters) {
		final Object[] params = (Object[]) paramaters;
		nbBools = (Integer) params[0];
		pairProba = (Double) params[1];
		nbPairs = 0;
		pairs = new Boolean[nbBools][nbBools];
		instantiated = new int[nbBools];
		final Random rnd = params.length == 3 ? new Random( (Integer) params[2]) : new Random();
		for (int i = 0; i < nbBools; i++) {
			for (int j = 0; j < i; j++) {
				//equivalence existence
				if( rnd.nextDouble() < pairProba) {
					pairs[i][j]= rnd.nextBoolean();
					nbPairs++;
				}
			}
		}	
	}






	@Override
	public void buildModel() {
		model = new CPModel();
		boolVars = makeBooleanVarArray("b", nbBools);
		model.addVariables(boolVars);
		pairVars = new IntegerVariable[nbPairs];
		deletion = makeIntVar("deletion", 0, nbPairs, Options.V_OBJECTIVE);
		int cpt = 0;
		for (int i = 0; i < nbBools; i++) {
			for (int j = 0; j < nbBools; j++) {
				if(pairs[i][j] == Boolean.TRUE) {
					pairVars[cpt] = makeBooleanVar("eq_"+i+"_"+j);
					model.addConstraint( Choco.reifiedConstraint(pairVars[cpt], eq(boolVars[i], boolVars[j])));
					cpt++;
				}else if(pairs[i][j] == Boolean.FALSE) {
					pairVars[cpt] = makeBooleanVar("neq_"+i+"_"+j);
					model.addConstraint( Choco.reifiedConstraint(pairVars[cpt], neq(boolVars[i], boolVars[j])));
					cpt++;
				}
			}
		}
		model.addConstraint( eq( minus(cpt, sum(pairVars)), deletion));
		//LOGGER.info(model.pretty());
	}

	@Override
	public void buildSolver() {
		solver = new CPSolver();
		solver.read(model);
		solver.setFirstSolution(false);
		solver.setDoMaximize(false);
	}

	@Override
	public void prettyOut() {
		//LOGGER.info("pairs: "+Arrays.toString(solver.getVar(pairVars)));
		LOGGER.info("nbDeletions= "+ solver.getOptimumValue());
		LOGGER.info("bool vars: "+Arrays.toString(solver.getVar(boolVars)));
	}

	@Override
	public void solve() {
		//solver.setSolutionPoolCapacity(5);
		solver.generateSearchStrategy();
		solver.launch();
	}

	@Override
	public void execute() {
		execute(new Object[]{7,0.5,0});
	}

	public static void main(String[] args) {
		new MinimumEdgeDeletion().execute();
	}
}
