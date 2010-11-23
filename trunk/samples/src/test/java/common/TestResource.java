package common;

import static org.junit.Assert.assertEquals;
import junit.framework.Assert;

import org.junit.Test;

import samples.tutorials.scheduling.CumulativeWebEx;
import samples.tutorials.scheduling.DisjunctiveWebEx;

public class TestResource {


	@Test
	public void testExampleDisjunctiveWebSite() {
		//ChocoLogging.setVerbosity(Verbosity.SEARCH);
		final DisjunctiveWebEx cwe = new DisjunctiveWebEx();
		cwe.execute(Boolean.FALSE);
		final Number obj = cwe.solver.getObjectiveValue();
		cwe.execute(Boolean.TRUE);
		Assert.assertEquals("Disjunctive Website Example",obj, cwe.solver.getObjectiveValue());
	}


	@Test
	public void testExampleCumulativeWebSite() {
		final CumulativeWebEx cwe = new CumulativeWebEx();
		cwe.execute(Boolean.FALSE);
		final Number obj = cwe.solver.getObjectiveValue();
		cwe.execute(Boolean.TRUE);
		assertEquals("Cumulative Website Example",obj, cwe.solver.getObjectiveValue());
	}

}
