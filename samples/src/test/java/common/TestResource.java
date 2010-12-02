package common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import samples.tutorials.scheduling.CumulativeScheduling;

public class TestResource {


	@Test
	public void testCumulativeScheduling() {
		final CumulativeScheduling cwe = new CumulativeScheduling();
		cwe.execute(Boolean.FALSE);
		final Number obj = cwe.solver.getOptimumValue();
		cwe.execute(Boolean.TRUE);
		assertEquals("Cumulative Website Example",obj, cwe.solver.getOptimumValue());
	}


}
