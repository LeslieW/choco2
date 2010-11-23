package common;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import samples.tutorials.packing.BinPackingCmd;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.common.logging.Verbosity;
import choco.kernel.common.util.tools.ArrayUtils;

public class TestBinPacking {

	private final BinPackingCmd cmd = new BinPackingCmd();

	private final static String PATH = "./src/main/resources/bin-packing-tut/instances/";

	private final static String[] CMD_PREFIX = {"--seed","0"};


	@BeforeClass
	public final static void setUp() {
		ChocoLogging.setVerbosity(Verbosity.QUIET);
	}

	@AfterClass
	public final static void tearDown() {
		ChocoLogging.setVerbosity(Verbosity.SILENT);
	}


	private void testInstances(String... args) {
		cmd.doMain(ArrayUtils.append(new String[] {
				"--seed", String.valueOf(0),
				"-time", String.valueOf(15),
				"-f",PATH}, 
				args
		));
	}

	@Test
	public void testBinPacking1() {
		testInstances();
	}
	
	@Test
	public void testBinPacking2() {
		testInstances("--light");
	}
}
