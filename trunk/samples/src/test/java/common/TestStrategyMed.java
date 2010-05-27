/**
 *
 */
package common;

import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.Configuration;
import choco.kernel.solver.ResolutionPolicy;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import samples.tutorials.trunk.MinimumEdgeDeletion;

import java.util.Arrays;
import java.util.logging.Logger;

import static choco.kernel.solver.Configuration.*;
import static org.junit.Assert.assertEquals;

public class TestStrategyMed {

	protected final static Logger LOGGER = ChocoLogging.getTestLogger();

	private final MinimumEdgeDeletion med = new MedShaker();

	private Number objective = null; 

	private final static Configuration CONFIG = new Configuration();

	@BeforeClass
	public static void setUp() {
		ChocoLogging.toVerbose();
		CONFIG.putFalse(STOP_AT_FIRST_SOLUTION);
		CONFIG.putEnum(Configuration.RESOLUTION_POLICY, ResolutionPolicy.MINIMIZE);
		CONFIG.putInt(RESTART_BASE, 1);//many restarts, bad performance but good testing !
	}
	private void solve(boolean randomSelectors) {
		med.buildSolver();
		if(randomSelectors) ((CPSolver) med.solver).setRandomSelectors(0);
		med.solve();
		med.prettyOut();
		assertEquals("Minimum Edge Deletion is Feasible", Boolean.TRUE, med.solver.isFeasible());
		if(objective == null) {objective = med.solver.getOptimumValue();}
		else {assertEquals("objective", objective, med.solver.getOptimumValue());}
	}
	private void recursiveTestMED(Object parametersMED, String...confBoolValues) {
		if(confBoolValues != null && confBoolValues.length > 0) {
			final int n = confBoolValues.length-1;
			final String[] newConfboolValues = Arrays.copyOf(confBoolValues, n);
			CONFIG.putFalse(confBoolValues[n]);
			recursiveTestMED(parametersMED, newConfboolValues);
			CONFIG.putTrue(confBoolValues[n]);
			recursiveTestMED(parametersMED, newConfboolValues);
		} else {
			//configuration is set: solve instance
			LOGGER.info(CONFIG.toString());
			solve(false);
			solve(true);
		}

	}
	/**
	 * shake a little bit the optimization options.
	 * @param parametersMED parameters of the  minimum edge deletion
	 */
	public void testMED(Object parametersMED) {
		//CONFIG.clear();
		med.setUp(parametersMED);
		med.buildModel();
		objective = null;
		recursiveTestMED(parametersMED, 
				RESTART_LUBY, RESTART_AFTER_SOLUTION, NOGOOD_RECORDING_FROM_RESTART
				,BOTTOM_UP
				, INIT_SHAVING, INIT_DESTRUCTIVE_LOWER_BOUND
		);
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
			solver =  new CPSolver(CONFIG);
			solver.monitorFailLimit(true);
			solver.read(model);
		}
	}
}




