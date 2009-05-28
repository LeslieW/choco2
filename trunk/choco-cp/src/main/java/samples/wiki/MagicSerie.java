package samples.wiki;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.common.logging.ChocoLogging;

import static choco.Choco.*;

import java.util.logging.Logger;

/**
 * The Magic Serie problem
 */
public class MagicSerie {

    protected final static Logger LOGGER = ChocoLogging.getSamplesLogger();


    public static void main(String[] args) {
        int n = 5;

        LOGGER.info("Magic Serie Model with n = " + n);

        CPModel pb = new CPModel();
        IntegerVariable[] vs = new IntegerVariable[n];
        for (int i = 0; i < n; i++) {
            vs[i] = makeIntVar("" + i, 0, n - 1);
        }
        for (int i = 0; i < n; i++) {
            pb.addConstraint(occurrence(i, vs[i], vs));
        }
        pb.addConstraint(eq(sum(vs), n));     // contrainte redondante 1
        int[] coeff2 = new int[n - 1];
        IntegerVariable[] vs2 = new IntegerVariable[n - 1];
        for (int i = 1; i < n; i++) {
            coeff2[i - 1] = i;
            vs2[i - 1] = vs[i];
        }
        pb.addConstraint(eq(scalar(coeff2, vs2), n)); // contrainte redondante 2
        CPSolver s = new CPSolver();
        s.read(pb);
        s.monitorBackTrackLimit(true);
        s.setVarIntSelector(new MinDomain(s,s.getVar(vs)));
        s.solve();
        for (int i = 0; i < vs.length; i++) {    // affichage de la solution
            LOGGER.info(("" + i + ": " + s.getVar(vs[i]).getVal()));
        }
        LOGGER.info("NB_NODE: " + s.getSearchStrategy().getNodeCount());
        LOGGER.info("BACKT: " + s.getSearchStrategy().getBackTrackCount());
        LOGGER.info("TIME: " + s.getSearchStrategy().getTimeCount());
        ChocoLogging.flushLogs();
    }

}

