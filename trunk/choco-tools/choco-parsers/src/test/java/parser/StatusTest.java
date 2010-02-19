package parser;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import parser.instances.ResolutionStatus;
import parser.instances.checker.IStatusChecker;
import parser.instances.checker.SCheckFactory;
import choco.kernel.solver.search.checker.SolutionCheckerException;

public class StatusTest {

	

	@BeforeClass
	public static void initalize() {
		SCheckFactory.PROPERTIES.setProperty("csp1", "true");
		SCheckFactory.PROPERTIES.setProperty("csp2", "False");
		SCheckFactory.PROPERTIES.setProperty("optim1", "10");
		SCheckFactory.PROPERTIES.setProperty("optim2", "10:20");
	}
	
	@AfterClass
	public static void teardDown() {
		SCheckFactory.PROPERTIES.clear();
	}
	
	@Test
	public void testGoodStatus() throws SolutionCheckerException {
		IStatusChecker scheck = SCheckFactory.makeStatusChecker("csp1");
		scheck.checkStatus(null, ResolutionStatus.SAT,null);
		scheck.checkStatus(null, ResolutionStatus.UNKNOWN,null);
		scheck.checkStatus(null, ResolutionStatus.TIMEOUT,null);
		scheck = SCheckFactory.makeStatusChecker("csp2");
		scheck.checkStatus(null, ResolutionStatus.UNSAT,null);
		scheck = SCheckFactory.makeStatusChecker("optim1");
		scheck.checkStatus(Boolean.TRUE, ResolutionStatus.SAT, Integer.valueOf(8));
		scheck.checkStatus(Boolean.TRUE, ResolutionStatus.OPTIMUM, Integer.valueOf(10));
		scheck = SCheckFactory.makeStatusChecker("optim2");
		scheck.checkStatus(Boolean.FALSE, ResolutionStatus.SAT, Integer.valueOf(12));
		scheck.checkStatus(Boolean.FALSE, ResolutionStatus.OPTIMUM, Integer.valueOf(10));

	}
	

	@Test(expected = SolutionCheckerException.class)
	public void testBadStatus1() throws SolutionCheckerException {
		SCheckFactory.makeStatusChecker("csp1").checkStatus(null, ResolutionStatus.UNSAT,null);
	}
	
	@Test(expected = SolutionCheckerException.class)
	public void testBadStatus2() throws SolutionCheckerException {
		SCheckFactory.makeStatusChecker("csp2").checkStatus(null, ResolutionStatus.SAT,null);
	}
	
	@Test(expected = SolutionCheckerException.class)
	public void testBadStatus3() throws SolutionCheckerException {
		SCheckFactory.makeStatusChecker("optim1").checkStatus(Boolean.TRUE, ResolutionStatus.UNSAT,null);
	}
	
	@Test(expected = SolutionCheckerException.class)
	public void testBadStatus4() throws SolutionCheckerException {
		SCheckFactory.makeStatusChecker("optim1").checkStatus(Boolean.TRUE, ResolutionStatus.SAT,Integer.valueOf(11));
	}
	
	@Test(expected = SolutionCheckerException.class)
	public void testBadStatus5() throws SolutionCheckerException {
		SCheckFactory.makeStatusChecker("optim2").checkStatus(Boolean.FALSE, ResolutionStatus.SAT,Integer.valueOf(9));
	}
}
