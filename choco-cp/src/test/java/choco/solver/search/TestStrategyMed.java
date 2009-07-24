/**
 *
 */
package choco.solver.search;

import static org.junit.Assert.assertEquals;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import samples.Examples.MinimumEdgeDeletion;
import choco.Choco;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.memory.recomputation.EnvironmentRecomputation;


public class TestStrategyMed {
	
	protected final static Logger LOGGER = ChocoLogging.getTestLogger();

	@Before
	public void init() {
//		ChocoLogging.getTestLogger().setLevel(Level.INFO);
//		ChocoLogging.setVerbosity(Verbosity.VERBOSE);
		Choco.DEBUG=true;
	}

	private final MinimumEdgeDeletion med = new MedShaker();

	private boolean recomputation = false;
	
	private boolean restartAfterSolution = false;
	
	private boolean restartPolicy = false;
	
	private boolean nogoodFromRestart = false;
	
	private boolean randomSelectors = false;
	
	/**
	 * shake a little bit the optimization options.
	 */
	public void testMED(Object parametersMED) {
		med.setUp(parametersMED);
		med.buildModel();
		//DFS
		Number objective = null; 
		do {
			do {
				do {
					//do {
						//do {
			
			med.buildSolver();
			med.solve();
			med.prettyOut();
			assertEquals("Minimum Edge Deletion is Feasible", Boolean.TRUE, med._s.isFeasible());
			if(objective == null) {objective = med._s.getOptimumValue();}
			else {assertEquals("objective", objective, med._s.getOptimumValue());}
			//also check that the nogood from restart reduce the number of nodes
			
			restartAfterSolution = !restartAfterSolution;
		}while( restartAfterSolution);
			restartPolicy = !restartPolicy;
		}while( restartPolicy);
			//fail in combination with random selectors
//			nogoodFromRestart = ! nogoodFromRestart;
//		}while( nogoodFromRestart);
			randomSelectors = ! randomSelectors;
		}while( randomSelectors);
		//	recomputation = ! recomputation;
		//}while( recomputation);
	}

	@Test
	public void debugTest() {
		recomputation = false;
		restartAfterSolution = true;
		restartPolicy = true;
		nogoodFromRestart = false;
		randomSelectors = false;
		med.setUp(new Object[]{10,0.7,1});
		med.buildModel();
		med.buildSolver();
		med.solve();
		med.prettyOut();
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
		for (double p = 0.6; p < 0.9; p+=0.1) {
			//testMED(new Object[]{9,p});
			testMED(new Object[]{10,p});
		}
	}

	@Test
	public void testLargeMED() {
		testMED(new Object[]{15,0.4});
	}
	
	class MedShaker extends MinimumEdgeDeletion {

		@Override
		public void buildSolver() {
			LOGGER.log(Level.INFO, "Use Recomputation: {0}", recomputation);
			_s =  recomputation ? new CPSolver(new EnvironmentRecomputation()) : new CPSolver();
			_s.monitorBackTrackLimit(true);
			_s.monitorFailLimit(true);
			_s.setLoggingMaxDepth(100);
			_s.read(_m);
			_s.setFirstSolution(false);
			_s.setDoMaximize(false);
			CPSolver s = (CPSolver) _s;
			s.setRestart(restartAfterSolution);
			if(restartPolicy) {s.setLubyRestart(1, 2);} //many restarts, bad performance but good testing !
			s.setRecordNogoodFromRestart(nogoodFromRestart);
			if(randomSelectors) {s.setRandomSelectors(0);}
			if(LOGGER.isLoggable(Level.INFO)) {
				StringBuilder b =new StringBuilder();
				b.append("solver configuration: DFS ;");
				if(restartAfterSolution) {b.append(" restartAfterSolution ;");}
				if(restartPolicy) {b.append(" restartPolicy (LUBY) ;");}
				if(nogoodFromRestart) {b.append(" nogoodFromRestart ;");}
				if(randomSelectors) {b.append(" randomSelectors ;");}
				LOGGER.info(new String(b));
			}
			_s.generateSearchStrategy();
		}
		

		@Override
		public void solve() {
			super.solve();
			prettyOut();
		}
	}
}




