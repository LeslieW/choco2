package samples.tutorials.lns.lns;
/*
 * Created by IntelliJ IDEA.
 * User: sofdem - sophie.demassey{at}mines-nantes.fr
 * Date: 13/01/11 - 14:19
 */

import choco.kernel.solver.Solution;

/** @author Sophie Demassey */
public interface NeighborhoodOperator {
boolean restrictNeighborhood(Solution solution);
}
