/**
 *  Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package samples.tutorials.trunk;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.variables.integer.IntegerVariable;

import java.util.logging.Logger;

import static choco.Choco.*;

/**
 * The Magic Serie problem
 */
public class MagicSerie {

    protected final static Logger LOGGER = ChocoLogging.getMainLogger();


    public static void main(String[] args) {
        int n = 5;

        LOGGER.info("Magic Serie Model with n = " + n);

        CPModel pb = new CPModel();
        IntegerVariable[] vs = new IntegerVariable[n];
        for (int i = 0; i < n; i++) {
            vs[i] = makeIntVar("" + i, 0, n - 1);
        }
        for (int i = 0; i < n; i++) {
            pb.addConstraint(occurrence(vs[i], vs, i));
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

