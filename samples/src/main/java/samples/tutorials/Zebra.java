/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
//*  CHOCO: an open-source Constraint Programming  *
//*     System for Research and Education          *
//*                                                *
//*    contributors listed in choco.Entity.java    *
//*           Copyright (C) F. Laburthe, 1999-2006 *
//**************************************************
package samples.tutorials;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;

import java.util.logging.Logger;

public class Zebra {

	protected final static Logger LOGGER = ChocoLogging.getEngineLogger();
    private static CPSolver solver;

    private static void propagateDecision(IntegerVariable v,int value) throws ContradictionException {
		LOGGER.info(v.pretty()+" = "+value);
		solver.getVar(v).setVal(value);
		solver.propagate();
		LOGGER.info(solver.solutionToString());
	}

	public static void main(String args[]) {
		LOGGER.fine("Zebra Testing...");
        Model model = new CPModel();
        IntegerVariable green = makeIntVar("green", 1, 5);
        IntegerVariable blue = makeIntVar("blue", 1, 5);
        IntegerVariable yellow = makeIntVar("yellow", 1, 5);
        IntegerVariable ivory = makeIntVar("ivory", 1, 5);
        IntegerVariable red = makeIntVar("red", 1, 5);
        IntegerVariable diplomat = makeIntVar("diplomat", 1, 5);
        IntegerVariable painter = makeIntVar("painter", 1, 5);
        IntegerVariable sculptor = makeIntVar("sculptor", 1, 5);
        IntegerVariable doctor = makeIntVar("doctor", 1, 5);
        IntegerVariable violinist = makeIntVar("violinist", 1, 5);
        IntegerVariable norwegian = makeIntVar("norwegian", 1, 5);
        IntegerVariable english = makeIntVar("english", 1, 5);
        IntegerVariable japanese = makeIntVar("japanese", 1, 5);
        IntegerVariable spaniard = makeIntVar("spaniard", 1, 5);
        IntegerVariable italian = makeIntVar("italian", 1, 5);
        IntegerVariable wine = makeIntVar("wine", 1, 5);
        IntegerVariable milk = makeIntVar("milk", 1, 5);
        IntegerVariable coffee = makeIntVar("coffee", 1, 5);
        IntegerVariable water = makeIntVar("water", 1, 5);
        IntegerVariable tea = makeIntVar("tea", 1, 5);
        IntegerVariable fox = makeIntVar("fox", 1, 5);
        IntegerVariable snail = makeIntVar("snail", 1, 5);
        IntegerVariable horse = makeIntVar("horse", 1, 5);
        IntegerVariable dog = makeIntVar("dog", 1, 5);
        IntegerVariable zebra = makeIntVar("zebra", 1, 5);
        IntegerVariable[] colors = new IntegerVariable[]{green, blue, yellow, ivory, red};
        IntegerVariable[] trades = new IntegerVariable[]{diplomat, painter, sculptor, doctor, violinist};
        IntegerVariable[] nationalities = new IntegerVariable[]{norwegian, english, japanese, spaniard, italian};
        IntegerVariable[] drinks = new IntegerVariable[]{wine, milk, coffee, water, tea};
        IntegerVariable[] pets = new IntegerVariable[]{fox, snail, horse, dog, zebra};
        IntegerVariable[][] arrays = new IntegerVariable[][]{colors, trades, nationalities, drinks, pets};

		for (int a = 0; a < 5; a++) {
			for (int i = 0; i < 4; i++) {
				for (int j = i + 1; j < 5; j++) {
					model.addConstraint(neq(arrays[a][i], arrays[a][j]));
				}
			}
		}
		// help for incomplete alldiff on colors
		model.addConstraint(eq(yellow, 1));
//		//
//		// help for incomplete alldiff on colors
		model.addConstraint(eq(water, 1));

		model.addConstraint(eq(english, red));
		model.addConstraint(eq(spaniard, dog));
		model.addConstraint(eq(coffee, green));
		model.addConstraint(eq(italian, tea));
		model.addConstraint(eq(sculptor, snail));
		model.addConstraint(eq(diplomat, yellow));
		model.addConstraint(eq(green, plus(ivory, 1)));
		model.addConstraint(eq(milk, 3));
		model.addConstraint(eq(norwegian, 1));
		model.addConstraint(eq(minus(doctor, fox), 1));
		model.addConstraint(eq(violinist, wine));
		model.addConstraint(eq(japanese, painter));
		model.addConstraint(eq(minus(diplomat, horse), -1));
		model.addConstraint(eq(minus(norwegian, blue), -1));

		solver=new CPSolver();
		solver.read(model);
		try {
			propagateDecision(fox, 1);
			propagateDecision(italian, 2);
			propagateDecision(english, 3);
			propagateDecision(tea, 2);
		} catch (ContradictionException e) {
			LOGGER.info("find a contradiction !");
		}
		LOGGER.info(solver.pretty());
	}
}