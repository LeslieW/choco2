package choco.cp.solver.constraints.global.geost.layers.continuousSolver;

import choco.kernel.common.logging.ChocoLogging;

import java.util.logging.Logger;

final class TestQuimper {

    private static final Logger LOGGER = ChocoLogging.getTestLogger();

    private TestQuimper() {
    }

    public static void main(String[] args) {

	Quimper q = new Quimper("/Users/szampelli/Research/EssaisGeost/src/Ibex/ring.qpr");
	
	LOGGER.info(q.get_lb("x[1]")+" "+q.get_ub("x[2]"));

	q.contract("sat");

	LOGGER.info(q.get_lb("x[1]")+" "+q.get_ub("x[2]"));
    }


}