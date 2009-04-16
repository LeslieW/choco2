/**
 *
 */
package choco.solver.search;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import samples.Examples.MinimumEdgeDeletion;
import samples.Examples.PatternExample;
import choco.Choco;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.SearchLoopWithNogoodFromRestart;
import choco.kernel.solver.Solver;
import choco.kernel.solver.search.AbstractGlobalSearchStrategy;



public class TestNogoodFromRestart {


	@Before
	public void init() {
		Choco.DEBUG=true;
	}

	private final static MinimumEdgeDeletion MED = new MinimumEdgeDeletion();

	private final static CPSolver [] SOLVERS = new CPSolver[3];

	
	public void testMED(Object parametersMED) {
		MED.setUp(parametersMED);
		MED.buildModel();
		int cpt = 0;
		//DFS
		MED.buildSolver();
		SOLVERS[cpt] = (CPSolver) PatternExample._s;
	
		
		//Restart + nogood + Dom over WDEG
		cpt++;
		MED.buildSolver();
		SOLVERS[cpt] = (CPSolver) PatternExample._s;
		SOLVERS[cpt].setGeometricRestart(1, 1.2);
		SOLVERS[cpt].generateSearchStrategy();
		AbstractGlobalSearchStrategy strat = SOLVERS[cpt].getSearchStrategy();
		strat.setSearchLoop(new SearchLoopWithNogoodFromRestart(strat, SOLVERS[cpt].getRestartStrategy()));
		
		//Restart + nogood + Random search
		cpt++;
		MED.buildSolver();
		SOLVERS[cpt] = (CPSolver) PatternExample._s;
		SOLVERS[cpt].setGeometricRestart(1, 1.2);
		SOLVERS[cpt].setRandomSelectors(7);
		SOLVERS[cpt].generateSearchStrategy();
		strat = SOLVERS[cpt].getSearchStrategy();
		strat.setSearchLoop(new SearchLoopWithNogoodFromRestart(strat, SOLVERS[cpt].getRestartStrategy()));
		compare("Min. Edge. Del.", SOLVERS);
	}

	
	public static void compare(String label ,Solver... solvers) {
		Choco.DEBUG = true;
		for (int i = 0; i < solvers.length; i++) {
			final Solver s=solvers[i];
			//CPSolver.setVerbosity(CPSolver.SEARCH);
			s.setLoggingMaxDepth(100);
			s.launch();
			System.out.println(s.solutionToString());
			final String str = label +" index "+i;
			repmessage(str,"opt="+s.getOptimumValue(),s.isFeasible(),s);
			if(i>0){
				assertEquals("opt value"+str,solvers[i-1].getOptimumValue(),s.getOptimumValue());
			}
		}
	}

	public static void repmessage(String header,String label,Boolean r,Solver solver) {
		StringBuilder buffer=new StringBuilder();
		if( ! header.isEmpty() ) {
			buffer.append(header).append("\t");
		}
		buffer.append(label).append(' ').append(r).append(" ; nb Sol. ").append(solver.getNbSolutions());
		buffer.append(" ; ").append(solver.getTimeCount()).append("ms ; ").append(solver.getNodeCount()).append(" node(s)");
		System.out.println(buffer);
	}


	@Test
	public void testMinimumEquivalenceDetection1() {
		testMED(new Object[]{6,0.7,2});	
	}

	@Test
	public void testMinimumEquivalenceDetection2() {
		testMED(new Object[]{8,0.6,0});
	}

	@Test
	public void testMED() {
		testMED(new Object[]{9,0.6,6});
		
	}
	
	@Test
	public void testManyMED() {
		for (double p = 0.4; p < 1; p+=0.1) {
			testMED(new Object[]{9,p});
			testMED(new Object[]{10,p});
		}
	}
	
	@Test
	public void testLargeMED() {
		testMED(new Object[]{15,0.4});
	}
}




