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

package samples.tutorials.packing;

import static choco.Choco.constantArray;
import static choco.Choco.eq;
import static choco.Choco.geq;
import static choco.Choco.leq;
import static choco.Choco.leqCard;
import static choco.Choco.makeIntVar;
import static choco.Choco.makeIntVarArray;
import static choco.Choco.makeSetVarArray;
import static choco.Choco.nth;
import static choco.Choco.pack;
import static choco.Choco.sum;
import static choco.Options.C_PACK_AR;
import static choco.Options.V_BOUND;
import static choco.Options.V_ENUM;
import static choco.Options.V_NO_DECISION;
import static choco.Options.V_OBJECTIVE;
import static choco.visu.components.chart.ChocoChartFactory.createAndShowGUI;
import static choco.visu.components.chart.ChocoChartFactory.createPackChart;

import java.util.logging.Level;

import samples.tutorials.PatternExample;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.util.tools.MathUtils;
import choco.kernel.model.constraints.pack.PackModel;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.set.SetVariable;


/**
 * <b>CSPLib n°31 : Rack Configuration Problem</b>
 * @author Arnaud Malapert</br>
 * @since 3 déc. 2008 <b>version</b> 2.0.1</br>
 * @version 2.0.1</br>
 */
public class RackConfiguration extends PatternExample {
	//DATA
	private int nbRacks, nbCards, nbRackModels, nbCardTypes;

	private int[] rackMPower, rackMConnector, rackMPrice, cardMDemand;

	//MODEL
	private SetVariable[] racks;

	private IntegerVariable[] cardRacks;

	private IntegerConstantVariable[] cardMPower, cardPower;

	private IntegerVariable[] rackLoads, rackTypes, rackMaxLoads, rackMaxConnectors, rackCosts;

	private IntegerVariable objective;
	
	private PackModel packMod;
	
	public RackConfiguration() {
		super();
	}

	@Override
	public void setUp(Object parameters) {
		super.setUp(parameters);
		if (parameters instanceof int[][]) {
			int[][] params = (int[][]) parameters;
			nbRacks = params[0][0];
			rackMPower = params[1];
			rackMConnector = params[2];
			rackMPrice = params[3];
			cardMPower = constantArray(params[4]); 
			cardMDemand = params[5];
			nbCardTypes=cardMDemand.length;
			nbRackModels=rackMPrice.length;
			nbCards = MathUtils.sum(cardMDemand);
		}
	}

	@Override
	public void buildModel() {
		model =new CPModel();
		//VARIABLES
		cardRacks = new IntegerVariable[nbCards];
		cardPower= new IntegerConstantVariable[nbCards];
		int offset =0;
		for (int i = 0; i < nbCardTypes; i++) {
			for (int j = offset; j < offset + cardMDemand[i]; j++) {
				cardRacks[j] = makeIntVar("cardRack_"+i+"_"+(j-offset), 0,nbRacks-1, V_ENUM);
				cardPower[j] = cardMPower[i];
			}
			offset += cardMDemand[i];
		}
		racks = makeSetVarArray("rack", nbRacks, 0, nbCards-1, V_NO_DECISION);
		rackLoads = makeIntVarArray("rackLoad", nbRacks, 0, MathUtils.max(rackMPower), V_BOUND, V_NO_DECISION);
		rackTypes = makeIntVarArray("rackType", nbRacks, 0,nbRackModels-1, V_ENUM);
		rackCosts = makeIntVarArray("rackCost", nbRacks,rackMPrice, V_ENUM);
		rackMaxLoads = makeIntVarArray("rackMaxLoad", nbRacks, rackMPower,V_ENUM, V_NO_DECISION);
		rackMaxConnectors = makeIntVarArray("rackMaxConnector", nbRacks, rackMConnector,V_NO_DECISION);
		objective = makeIntVar("obj", 0, nbRacks * MathUtils.max(rackMPrice), V_OBJECTIVE, V_NO_DECISION);
		
		//CONSTRAINTS
		//post packing constraints 
		packMod = new PackModel(cardRacks,cardPower, rackLoads, racks);
		model.addConstraints(pack(packMod, C_PACK_AR));
		//post connector,power and price constraints
		for (int i = 0; i < nbRacks; i++) {
			model.addConstraints(
					leqCard(racks[i], rackMaxConnectors[i]),
					leq(rackLoads[i],rackMaxLoads[i]),
					nth(rackTypes[i], rackMPower, rackMaxLoads[i]),
					nth(rackTypes[i], rackMPrice, rackCosts[i]),
					nth(rackTypes[i], rackMConnector, rackMaxConnectors[i])
			);
		}
		//post objective
		model.addConstraint(eq(objective, sum(rackCosts)));
		//post symmetry breaking constraints
		model.addConstraints(packMod.orderEqualSizedItems(0));
		for (int i = 1; i < nbRacks; i++) {
			model.addConstraint( geq(rackTypes[i-1], rackTypes[i]));
		}

	}


	@Override
	public void buildSolver() {
		solver = new CPSolver();
		solver.read(model);
	}


	@Override
	public void solve() {
		solver.minimize(false);
	}


	@Override
	public void prettyOut() {
		if(LOGGER.isLoggable(Level.INFO) && solver.existsSolution()) {
			final StringBuilder b = new StringBuilder();
			for (int i = 0; i < nbRacks; i++) {
				b.append(solver.getVar(rackTypes[i]));
				b.append(" --> ").append(solver.getVar(racks[i]));
				b.append('\n');
			}
			b.append("total cost: ").append(solver.getObjectiveValue());
			LOGGER.info(b.toString());
			createAndShowGUI("Rack Configuration", createPackChart(null, solver,packMod));
		}
	}


	@Override
	public void execute() {
		execute(
				new int[][]{ {10}, //nbRacks
						{0, 50, 150, 200}, {0, 2, 5, 9}, {0, 35, 140, 200}, //Racks 
						{75, 50, 40, 20},{3, 4, 6, 8} //Cards
				} 
		);
	}



	public static void main(String[] args) {
		(new RackConfiguration()).execute();
	}


}
