/* ************************************************
 *           _       _                            *
 *          |  °(..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien    1999-2009      *
 **************************************************/
package samples.documentation;

import choco.Choco;
import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.geost.Constants;
import choco.cp.solver.search.integer.valselector.RandomIntValSelector;
import choco.cp.solver.search.integer.varselector.RandomIntVarSelector;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.geost.externalConstraints.IExternalConstraint;
import choco.kernel.model.constraints.geost.externalConstraints.NonOverlappingModel;
import choco.kernel.model.variables.geost.GeostObject;
import choco.kernel.model.variables.geost.ShiftedBox;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

/*
* User : charles
* Mail : cprudhom(a)emn.fr
* Date : 16 oct. 2009
* Since : Choco 2.1.1
* Update : Choco 2.1.1
*
* See Code4Doc1.java for more informations.
*/
public class Code4Doc2 {

    public static void main(String[] args) {
        new Code4Doc2().catmostnvalue();
    }

    public void cabs() {
        //totex cabs
        Model m = new CPModel();
        IntegerVariable x = makeIntVar("x", 1, 5, "cp:enum");
        IntegerVariable y = makeIntVar("y", -5, 5, "cp:enum");
        m.addConstraint(abs(x, y));
        Solver s = new CPSolver();
        s.read(m);
        s.solve();
        //totex
    }

    public void calldifferent() {
        //totex calldifferent
        int n = 8;
        CPModel m = new CPModel();
        IntegerVariable[] queens = new IntegerVariable[n];
        IntegerVariable[] diag1 = new IntegerVariable[n];
        IntegerVariable[] diag2 = new IntegerVariable[n];
        for (int i = 0; i < n; i++) {
            queens[i] = makeIntVar("Q" + i, 1, n);
            diag1[i] = makeIntVar("D1" + i, 1, 2 * n);
            diag2[i] = makeIntVar("D2" + i, -n + 1, n);
        }
        m.addConstraint(allDifferent(queens));
        for (int i = 0; i < n; i++) {
            m.addConstraint(eq(diag1[i], plus(queens[i], i)));
            m.addConstraint(eq(diag2[i], minus(queens[i], i)));
        }
        m.addConstraint("cp:clique", allDifferent(diag1));
        m.addConstraint("cp:clique", allDifferent(diag2));
        // diagonal constraints
        CPSolver s = new CPSolver();
        s.read(m);
        long tps = System.currentTimeMillis();
        s.solveAll();
        System.out.println("tps nreines1 " + (System.currentTimeMillis() - tps) + " nbNode " + s.
                getNodeCount());
        //totex
    }

    public void cand() {
        //totex cand
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable v1 = makeIntVar("v1", 0, 1);
        IntegerVariable v2 = makeIntVar("v2", 0, 1);
        m.addConstraint(and(eq(v1, 1), eq(v2, 1)));
        s.read(m);
        s.solve();
        //totex
    }

    public void catmostnvalue() {
        //totex catmostnvalue
        Model m = new CPModel();
        CPSolver s = new CPSolver();
        IntegerVariable v1 = makeIntVar("v1", 1, 1);
        IntegerVariable v2 = makeIntVar("v2", 2, 2);
        IntegerVariable v3 = makeIntVar("v3", 3, 3);
        IntegerVariable v4 = makeIntVar("v4", 3, 4);
        IntegerVariable n = makeIntVar("n", 3, 3);
        Constraint c = atMostNValue(new IntegerVariable[]{v1, v2, v3, v4}, n);
        m.addConstraint(c);
        s.read(m);
        s.solve();
        //totex
    }

    public void cboolchanneling() {
        //totex cboolchanneling
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable bool = makeIntVar("bool", 0, 1);
        IntegerVariable x = makeIntVar("x", 0, 5);
        m.addConstraint(boolChanneling(bool, x, 4));
        s.read(m);
        s.solveAll();
        //totex
    }

    public void ccumulative() {
        //totex ccumulative
        CPModel m = new CPModel();
        // data
        int n = 11 + 3; //number of tasks (include the three fake tasks)
        int[] heights_data = new int[]{2, 1, 4, 2, 3, 1, 5, 6, 2, 1, 3, 1, 1, 2};
        int[] durations_data = new int[]{1, 1, 1, 2, 1, 3, 1, 1, 3, 4, 2, 3, 1, 1};
        // variables
        IntegerVariable capa = constant(7);
        IntegerVariable[] starts = makeIntVarArray("start", n, 0, 5, "cp:bound");
        IntegerVariable[] ends = makeIntVarArray("end", n, 0, 6, "cp:bound");
        IntegerVariable[] duration = new IntegerVariable[n];
        IntegerVariable[] height = new IntegerVariable[n];
        for (int i = 0; i < height.length; i++) {
            duration[i] = constant(durations_data[i]);
            height[i] = makeIntVar("height " + i, new int[]{0, heights_data[i]});
        }
        IntegerVariable[] bool = makeIntVarArray("taskIn?", n, 0, 1);
        IntegerVariable obj = makeIntVar("obj", 0, n, "cp:bound", "cp:objective");
        //post the cumulative
        m.addConstraint(cumulative(starts, ends, duration, height, capa, ""));
        //post the channeling to know if the task is scheduled or not
        for (int i = 0; i < n; i++) {
            m.addConstraint(boolChanneling(bool[i], height[i], heights_data[i]));
        }
        //state the objective function
        m.addConstraint(eq(sum(bool), obj));
        CPSolver s = new CPSolver();
        s.read(m);
        //set the fake tasks to establish the profile capacity of the ressource
        try {
            s.getVar(starts[0]).setVal(1);
            s.getVar(ends[0]).setVal(2);
            s.getVar(height[0]).setVal(2);
            s.getVar(starts[1]).setVal(2);
            s.getVar(ends[1]).setVal(3);
            s.getVar(height[1]).setVal(1);
            s.getVar(starts[2]).setVal(3);
            s.getVar(ends[2]).setVal(4);
            s.getVar(height[2]).setVal(4);
        } catch (ContradictionException e) {
            System.out.println("error, no contradiction expected at this stage");
        }
        // maximize the number of tasks placed in this profile
        s.maximize(s.getVar(obj), false);
        System.out.println("Objective : " + (s.getVar(obj).getVal() - 3));
        for (int i = 3; i < starts.length; i++) {
            if (s.getVar(height[i]).getVal() != 0)
                System.out.println("[" + s.getVar(starts[i]).getVal() + " - "
                        + (s.getVar(ends[i]).getVal() - 1) + "]:"
                        + s.getVar(height[i]).getVal());
        }
        //totex
    }

    public void cdistanceeq() {
        //totex cdistanceeq
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable v0 = makeIntVar("v0", 0, 5);
        IntegerVariable v1 = makeIntVar("v1", 0, 5);
        IntegerVariable v2 = makeIntVar("v2", 0, 5);
        m.addConstraint(distanceEQ(v0, v1, v2, 0));
        s.read(m);
        s.solveAll();
        //totex
    }

    public void cdistancegt() {
        //totex cdistancegt
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable v0 = makeIntVar("v0", 0, 5);
        IntegerVariable v1 = makeIntVar("v1", 0, 5);
        IntegerVariable v2 = makeIntVar("v2", 0, 5);
        m.addConstraint(distanceGT(v0, v1, v2, 0));
        s.read(m);
        s.solveAll();
        //totex
    }

    public void cdistancelt() {
        //totex cdistancelt
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable v0 = makeIntVar("v0", 0, 5);
        IntegerVariable v1 = makeIntVar("v1", 0, 5);
        IntegerVariable v2 = makeIntVar("v2", 0, 5);
        m.addConstraint(distanceLT(v0, v1, v2, 0));
        s.read(m);
        s.solveAll();
        //totex
    }

    public void cdistanceneq() {
        //totex cdistanceneq
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable v0 = makeIntVar("v0", 0, 5);
        IntegerVariable v1 = makeIntVar("v1", 0, 5);
        m.addConstraint(distanceNEQ(v0, v1, 0));
        s.read(m);
        s.solveAll();
        //totex
    }

    public void ceq1() {
        //totex ceq1
        Model m = new CPModel();
        Solver s = new CPSolver();
        int c = 1;
        IntegerVariable v = makeIntVar("v", 0, 2);
        m.addConstraint(eq(v, c));
        s.read(m);
        s.solve();
        //totex
    }

    public void ceq2() {
        //totex ceq2
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable v1 = makeIntVar("v1", 0, 2);
        IntegerVariable v2 = makeIntVar("v2", 0, 2);
        IntegerExpressionVariable w1 = plus(v1, 1);
        IntegerExpressionVariable w2 = minus(v2, 1);
        m.addConstraint(eq(w1, w2));
        s.read(m);
        s.solve();
        //totex
    }

    public void ceqcard() {
        //totex ceqcard
        Model m = new CPModel();
        Solver s = new CPSolver();
        SetVariable set = makeSetVar("s", 1, 5);
        IntegerVariable card = makeIntVar("card", 2, 3);
        m.addConstraint(member(set, 3));
        m.addConstraint(eqCard(set, card));
        s.read(m);
        s.solve();
        //totex
    }

    public void cequation() {
        //totex cequation
        CPModel m = new CPModel();
        CPSolver s = new CPSolver();
        int n = 10;
        IntegerVariable[] bvars = makeIntVarArray("b", n, 0, 10, "cp:enum");
        int[] coefs = new int[n];

        int charge = 10;
        Random rand = new Random();
        for (int i = 0; i < coefs.length; i++) {
            coefs[i] = rand.nextInt(10);
        }
        Constraint knapsack = equation(bvars, coefs, charge);
        m.addConstraint(knapsack);
        s.read(m);
        s.solveAll();
        //totex
    }

    public void cfeaspairac() {
        //totex cfeaspairac
        Model m = new CPModel();
        Solver s = new CPSolver();
        ArrayList couples2 = new ArrayList();
        couples2.add(new int[]{1, 2});
        couples2.add(new int[]{1, 3});
        couples2.add(new int[]{2, 1});
        couples2.add(new int[]{3, 1});
        couples2.add(new int[]{4, 1});
        IntegerVariable v1 = makeIntVar("v1", 1, 4);
        IntegerVariable v2 = makeIntVar("v2", 1, 4);
        m.addConstraint(feasPairAC("cp:ac32", v1, v2, couples2));
        s.read(m);
        s.solveAll();
        //totex
    }

    public void cfeastupleac() {
        //totex cfeastupleac
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable v1 = makeIntVar("v1", 0, 2);
        IntegerVariable v2 = makeIntVar("v2", 0, 4);
        ArrayList feasTuple = new ArrayList();
        feasTuple.add(new int[]{1, 1}); // x*y = 1
        feasTuple.add(new int[]{2, 4}); // x*y = 1
        m.addConstraint(feasTupleAC("cp:ac2001", feasTuple, new IntegerVariable[]{v1, v2}));
        s.read(m);
        s.solve();
        //totex
    }

    public void cfeastuplefc() {
        //totex cfeastuplefc
        Model m = new CPModel();
        Solver s = new CPSolver();
        IntegerVariable v1 = makeIntVar("v1", 0, 2);
        IntegerVariable v2 = makeIntVar("v2", 0, 4);
        ArrayList feasTuple = new ArrayList();
        feasTuple.add(new int[]{1, 1}); // x*y = 1
        feasTuple.add(new int[]{2, 4}); // x*y = 1
        m.addConstraint(feasTupleFC(feasTuple, new IntegerVariable[]{v1, v2}));
        s.read(m);
        s.solve();
        //totex
    }

    public void cgeost() {
        //totex cgeost
        Model m = new CPModel();
        int dim = 3;
        int lengths[] = {5, 3, 2};
        int widths[] = {2, 2, 1};
        int heights[] = {1, 1, 1};
        int nbOfObj = 3;
        long seed = 0;
        //Create the Objects
        Vector<GeostObject> obj = new Vector<GeostObject>();
        for (int i = 0; i < nbOfObj; i++) {
            IntegerVariable shapeId = Choco.makeIntVar("sid", i, i);
            IntegerVariable coords[] = new IntegerVariable[dim];
            for (int j = 0; j < coords.length; j++) {
                coords[j] = Choco.makeIntVar("x" + j, 0, 2);
            }
            IntegerVariable start = Choco.makeIntVar("start", 1, 1);
            IntegerVariable duration = Choco.makeIntVar("duration", 1, 1);
            IntegerVariable end = Choco.makeIntVar("end", 1, 1);
            obj.add(new GeostObject(dim, i, shapeId, coords, start, duration, end));
        }
        //Create the ShiftedBoxes and add them to corresponding shapes
        Vector<ShiftedBox> sb = new Vector<ShiftedBox>();
        int[] t = {0, 0, 0};
        for (int d = 0; d < nbOfObj; d++) {
            int[] l = {lengths[d], heights[d], widths[d]};
            sb.add(new ShiftedBox(d, t, l));
        }
        //Create the external constraints vector
        Vector<IExternalConstraint> ectr = new Vector<IExternalConstraint>();
        //create the list of dimensions for the external constraint
        int[] ectrDim = new int[dim];
        for (int d = 0; d < dim; d++)
            ectrDim[d] = d;
        //create the list of object ids for the external constraint
        int[] objOfEctr = new int[nbOfObj];
        for (int d = 0; d < nbOfObj; d++) {
            objOfEctr[d] = obj.elementAt(d).getObjectId();
        }
        //create and add one external constraint of type non overlapping
        NonOverlappingModel n = new NonOverlappingModel(Constants.NON_OVERLAPPING, ectrDim, objOfEctr);
        ectr.add(n);
        //create and post the geost constraint
        Constraint geost = Choco.geost(dim, obj, sb, ectr);
        m.addConstraint(geost);
        Solver s = new CPSolver();
        s.read(m);
        s.setValIntSelector(new RandomIntValSelector(seed));
        s.setVarIntSelector(new RandomIntVarSelector(s, seed));
        s.solveAll();
        //totex
    }
}
