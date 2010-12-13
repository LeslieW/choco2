/**
 * Copyright (c) 1999-2010, Ecole des Mines de Nantes
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Ecole des Mines de Nantes nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package trace.visualizers;

import choco.cp.solver.variables.set.SetVarImpl;
import choco.kernel.solver.search.IntBranchingDecision;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.set.SetVar;
import org.slf4j.Logger;
import trace.Display;
import trace.Visualizer;

/**
 * A class to represent a vector of variables
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 9 déc. 2010
 */
public class Vector extends Visualizer {
    public static final String vector = "vector";

    public static final String PREFIX = "\t\t\t";

    final Var[] variables;

    /**
     * Build a vector of variables visualizer
     * @param vars domain variables
     * @param display how to diplay the visualizer
     * @param width width of the visualizer
     * @param height height of the visualizer
     */
    public Vector(Var[] vars, Display display, int width, int height) {
        super(vector, display, width, height);
        this.variables = vars;
    }

    /**
     * Build a visualizer of variables visualizer
     * @param vars domain variables
     * @param display how to diplay the visualizer
     * @param x coordinate of the visualizer in the x-axis (horizontal)
     * @param y coordinate of the visualizer in the y-axis (vertical)
     * @param width width of the visualizer
     * @param height height of the visualizer
     * @param group group name (to group multiple constraints)
     * @param min expected minimal value of any of the domains
     * @param max expected maximal value of any of the domains
     */
    public Vector(Var[] vars, Display display, int x, int y, int width, int height, String group, int min, int max) {
        super(vector, display, x, y, width, height, group, min, max);
        this.variables = vars;
    }

    @Override
    protected int print(Logger logger, IntBranchingDecision decision) {
        int f = 0;
        for (int i = 0; i < variables.length; i++) {
            Var var = variables[i];
            if (var instanceof IntDomainVar) {
                IntDomainVar ivar = (IntDomainVar) var;
                XMLHelper.dvar(ivar, i + 1, logger, PREFIX);
            } else if (var instanceof SetVarImpl) {
                SetVar svar = (SetVar) var;
                XMLHelper.svar(svar, i + 1, logger, PREFIX);
            }
            if (decision != null && decision.getBranchingObject() == var) {
                f = i + 1;
            }
        }
        return f;
    }
}
