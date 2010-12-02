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

package samples.tutorials.seminar;

import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.branching.AssignVar;
import choco.cp.solver.search.integer.valiterator.DecreasingDomain;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;

import java.util.logging.Logger;

import static choco.Choco.*;

/*
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 28 mai 2008
 * Since : Choco 2.0.0
 *
 */
public class ExKnapSack {

    protected final static Logger LOGGER = ChocoLogging.getMainLogger();

    static IntegerVariable obj1;
    static IntegerVariable obj2;
    static IntegerVariable obj3;
    static IntegerVariable c;


    public static Model postKnapsacPB() {
        Model m = new CPModel();

        obj1 = makeIntVar("obj1", 0, 17);
        obj2 = makeIntVar("obj2", 0, 17);
        obj3 = makeIntVar("obj3", 0, 17);
        c = makeIntVar("cost", 0, 500);
        m.addVariable(Options.V_BOUND, c);

        int capacity = 34;

        int[] volumes = new int[]{7, 5, 2};
        int[] energy = new int[]{6, 4, 3};

        m.addConstraint(leq(scalar(volumes, new IntegerVariable[]{obj1, obj2, obj3}), capacity));
        m.addConstraint(eq(scalar(energy, new IntegerVariable[]{obj1, obj2, obj3}), c));

        return m;
    }

    public static void knapsacSAT() {
        Model m = postKnapsacPB();

        Solver s =  new CPSolver();
        s.read(m);
        s.attachGoal(new AssignVar(new StaticVarOrder(s, s.getVar(new IntegerVariable[]{obj1, obj2, obj3})), new IncreasingDomain()));
//        ChocoLogging.toSearch();
        s.maximize(s.getVar(c), false);

        LOGGER.info("obj1: " + s.getVar(obj1).getVal());
        LOGGER.info("obj2: " + s.getVar(obj2).getVal());
        LOGGER.info("obj3: " + s.getVar(obj3).getVal());
        LOGGER.info("cost: " + s.getVar(c).getVal());
    }

    public static void knapsacOPT() {
        Model m = postKnapsacPB();

        Solver s = new CPSolver();
        s.read(m);

        s.setValIntIterator(new DecreasingDomain());

        s.maximize(s.getVar(c), true);

        LOGGER.info("obj1: " + s.getVar(obj1).getVal());
        LOGGER.info("obj2: " + s.getVar(obj2).getVal());
        LOGGER.info("obj3: " + s.getVar(obj3).getVal());
        LOGGER.info("cost: " + s.getVar(c).getVal());
    }


    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        knapsacSAT();
        long t2 = System.currentTimeMillis();
        LOGGER.info("time : "+ (t2-t1));
        LOGGER.info("");
        LOGGER.info("=============================================");
        LOGGER.info("");
        long t3 = System.currentTimeMillis();
//        knapsacOPT();
        long t4 = System.currentTimeMillis();
        LOGGER.info("time : "+ (t4-t3));
        ChocoLogging.flushLogs();
    }

}
