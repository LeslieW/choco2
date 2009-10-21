/**
 *
 */
package choco.solver.search;

import choco.Choco;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import junit.framework.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import samples.Examples.MinimumEdgeDeletion;

import java.util.logging.Level;
import java.util.logging.Logger;


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
					do {
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
				nogoodFromRestart = ! nogoodFromRestart;
			}while( nogoodFromRestart);
			randomSelectors = ! randomSelectors;
		}while( randomSelectors);
		//			recomputation = ! recomputation;
		//		}while( recomputation);
	}

	@Test
	public void recomputationMedTest() {
//		ChocoLogging.setVerbosity(Verbosity.VERBOSE);
		recomputation = true;
		restartAfterSolution = true;
		restartPolicy = false;
		nogoodFromRestart = false;
		randomSelectors = false;
		med.setUp(new Object[]{6,0.7,2});
		med.buildModel();
		med.buildSolver();
		med.solve();
		med.prettyOut();
		ChocoLogging.setVerbosity(Verbosity.SILENT);
		Assert.assertEquals("nb deletion", 1, med._s.getObjectiveValue());
	}

	@Test
	public void testMinimumEquivalenceDetection1() {
		testMED(new Object[]{6,0.7,2});	
	}

	@Test
	public void testMinimumEquivalenceDetection2() {
		testMED(new Object[]{8,0.6});
	}

	@Test
	public void testMinimumEquivalenceDetection3() {
		testMED(new Object[]{9,0.6,6});

	}

	@Test
	//@Ignore
	public void testMinimumEquivalenceDetection4() {
		testMED(new Object[]{10,0.9,1});
	}

	@Test
	@Ignore
	public void testLargeMinimumEquivalenceDetection() {
		testMED(new Object[]{15,0.4});
	}

	class MedShaker extends MinimumEdgeDeletion {

		@Override
		public void buildSolver() {
			_s =  new CPSolver();
			_s.monitorBackTrackLimit(true);
			_s.monitorFailLimit(true);
			_s.setLoggingMaxDepth(100);
			_s.read(_m);
			_s.setFirstSolution(false);
			_s.setDoMaximize(false);
			CPSolver s = (CPSolver) _s;
			//_s.attachGoal(new AssignVar(new MinDomain(_s), new IncreasingDomain()));
			s.setRecomputation(recomputation);
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
				if(recomputation) {b.append(" recomputation ;");}
				LOGGER.info(new String(b));
			}
		}
	}
}




