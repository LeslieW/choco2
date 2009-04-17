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
package samples.Examples;

import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.variables.integer.IntegerVariable;

/**
 * <i>benchmark proposed by Michel Lemaitre.</i>
 * <h4>The U2 problem</h4>
 *
 *"U2" has a Christmas concert that starts in 17 minutes and they must
 * all cross a bridge to get there.  All four men begin on the same side
 * of the bridge.  You must help them across to the other side. It is
 * night. There is one flashlight.  A maximum of two people can cross at
 * one time.  Any party who crosses, either 1 or 2 people, must have the
 * flashlight with them.  The flashlight must be walked back and forth,
 * it cannot be thrown, etc.  Each band member walks at a different
 * speed.  A pair must walk together at the rate of the slower man's
 * pace:
 *
 *           <ul>
 *           <li> Bono:- 1 minute to cross
 *           <li> Edge:- 2 minutes to cross
 *           <li> Adam:- 5 minutes to cross
 *           <li> Larry:-10 minutes to cross
 *           </ul>
 *
 * For example: if Bono and Larry walk across first, 10 minutes have
 * elapsed when they get to the other side of the bridge.  If Larry then
 * returns with the flashlight, a total of 20 minutes have passed and you
 * have failed the mission.  Notes: There is no trick behind this.  It is
 * the simple movement of resources in the appropriate order.  There are
 * two known answers to this problem.  This is based on a question
 * Microsoft gives to all prospective employees.  <br>
 * <i>Note</i>: Microsoft expects
 * you to answer this question in under 5 minutes!  Good Luck!  REMEMBER
 * - all the parameters you need to solve the problem are given to you!
 * That means all 4 guys ARE AT THE OTHER SIDE in 17 minutes. 2 guys at a
 * time MAX on the bridge at any time AND they HAVE to have the ONE
 * flashlight with them.

 */


public class U2planning extends PatternExample{



	public static class Person {
		String name;
		int rate;

		public Person(String n, int r) {
			name = n;
			rate = r;
		}
	}

	private final static int nbPersons = 5;
	private final static int flashlightIndex = nbPersons - 1;
	private final static int maxDurationU2 = 17;
    private static int nbSteps = 2;

	protected Person bono, edge, adam, larry, flashlight;
	protected Person[] persons;
	private static int[] timeTravel;


    @Override
    public void setUp(Object paramaters) {
        if(paramaters instanceof Integer){
            nbSteps = (Integer)paramaters;
        }
        bono = new Person("Bono", 1);
        edge = new Person("edge", 2);
        adam = new Person("adam", 5);
        larry = new Person("larry", 10);
        flashlight = new Person("flashlight", 0);
        persons = new Person[]{larry, adam, edge, bono, flashlight};
        timeTravel = new int[]{larry.rate, adam.rate, edge.rate, bono.rate, flashlight.rate};
    }

    @Override
    public void buildModel() {
        _m = new CPModel();
		int nbStates = (nbSteps + 1);
		IntegerVariable[][] move = makeIntVarArray("move", nbSteps, nbPersons, 0, 1,"cp:no_decision");
		IntegerVariable[][] position = makeIntVarArray("position", nbStates, nbPersons, 0, 1,"cp:no_decision");
		IntegerVariable[][] outward = makeIntVarArray("outward", nbSteps, nbPersons, 0, 1);
		IntegerVariable[][] backward = makeIntVarArray("backward", nbSteps, nbPersons, 0, 1);
		IntegerVariable[] duration = makeIntVarArray("duration", nbSteps, 1, maxDurationU2,"cp:no_decision");

		for (int u = 0; u < nbPersons; u++) {
			//  Starting and ending positions. 0 is before the bridge, 1 is after crossing.
			_m.addConstraint(eq(position[0][u], 0));          // starting positions
			_m.addConstraint(eq(position[nbStates - 1][u], 1));   // ending positions
			for (int s = 0; s < nbSteps - 1; s++) {
				// Define movements from positions.
				_m.addConstraint(ifOnlyIf(eq(outward[s][u], 1), leq(position[s][u], minus(position[s + 1][u], 1))));
				_m.addConstraint(ifOnlyIf(eq(backward[s][u], 1), leq(position[s + 1][u], minus(position[s][u], 1))));
				_m.addConstraint(eq(move[s][u], plus(outward[s][u], backward[s][u])));
				// a redundant constraint that could be useful if we were to instantiate move variables instead of position
				_m.addConstraint(ifOnlyIf(eq(move[s][u], 0), eq(position[s + 1][u], position[s][u])));

				// redundant constraint (because the cDefDuration one only propagates once all but one are instantiated)
				_m.addConstraint(geq(duration[s], mult(persons[u].rate, move[s][u])));

				//  If somebody crosses, the flashlight also crosses in the same direction.
				_m.addConstraint(leq(outward[s][u], outward[s][flashlightIndex]));
				_m.addConstraint(leq(backward[s][u], backward[s][flashlightIndex]));
			}
		}
    }

    @Override
    public void buildSolver() {
        _s = new CPSolver();
		_s.read(_m);
    }

    @Override
    public void solve() {
    	_s.solve();
    }

    @Override
    public void prettyOut() {
        LOGGER.info(_s.pretty());
    }


	public static void main(String[] args) {
		new U2planning().execute(2);
	}
}
