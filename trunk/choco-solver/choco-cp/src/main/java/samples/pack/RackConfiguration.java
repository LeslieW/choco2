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

package samples.pack;

import static choco.Choco.*;
import choco.cp.CPOptions;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.variables.integer.IntegerConstantVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.Solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;


/**
 * <b>CSPLib n°31 : Rack Configuration Problem</b>
 * @author Arnaud Malapert</br>
 * @since 3 déc. 2008 <b>version</b> 2.0.1</br>
 * @version 2.0.1</br>
 */
public class RackConfiguration {
    protected final static Logger LOGGER = ChocoLogging.getMainLogger();

	public final RackConfigurationData data;



	//MODEL DATA
	public CPModel model;

	ArrayList<IntegerVariable[]> cardTypeMap;

	protected SetVariable[] plugged;

	protected IntegerVariable[] loads;

	protected IntegerVariable[] cardRacks;

	protected IntegerConstantVariable[] cardPower;

	protected IntegerVariable[] rackTypes;

	protected IntegerVariable[] rackMaxLoad;

	protected IntegerVariable[] rackMaxConnector;

	protected IntegerVariable[] rackCosts;

	protected Solver solver;
	//trier les racks par prix decroissant et les cards par power decroissant
	public RackConfiguration(RackConfigurationData data) {
		super();
		this.data = data;
	}



	public void createVariables() {
		cardRacks = new IntegerVariable[data.nbCards];
		cardPower= new IntegerConstantVariable[data.nbCards];
		cardTypeMap = new ArrayList<IntegerVariable[]>();
		int offset =0;
		for (int i = 0; i < data.nbCardTypes; i++) {
			IntegerVariable[] tmp = makeIntVarArray("cardRack_"+i, data.cardDemand[i], 0,data.nbRacks-1, CPOptions.V_ENUM);
			IntegerVariable[] tmps = new IntegerVariable[data.cardDemand[i]];
			Arrays.fill(tmps, constant(data.cardPower[i]));
			System.arraycopy(tmp, 0, cardRacks, offset, data.cardDemand[i]);
			System.arraycopy(tmps, 0, cardPower, offset, data.cardDemand[i]);
			offset += data.cardDemand[i];
			cardTypeMap.add(tmp);
		}
		int maxPower =0;
		for (int i = 0; i < data.nbRackModels; i++) {
			maxPower = Math.max(maxPower, data.rackPower[i]);
		}
		plugged = makeSetVarArray("plugged", data.nbRacks, 0, data.nbCards-1);
		loads = makeIntVarArray("rackType", data.nbRacks, 0,maxPower,CPOptions.V_ENUM);
		rackTypes = makeIntVarArray("rackType", data.nbRacks, 0,data.nbRackModels-1,CPOptions.V_ENUM);
		rackCosts = makeIntVarArray("rackCost", data.nbRacks,data.rackPrice);
		rackMaxLoad = makeIntVarArray("rackMaxLoad", data.nbRacks, data.rackPower);
		rackMaxConnector = makeIntVarArray("rackMaxConnector", data.nbRacks, data.rackConnector);
	}

	public void createConstraints() {
		model.addConstraints(pack(plugged, loads,cardRacks,cardPower));
		//add connector,power and price constraints
		for (int i = 0; i < data.nbRacks; i++) {
			model.addConstraints(
					leqCard(plugged[i], rackMaxConnector[i]),
					leq(loads[i],rackMaxLoad[i]),
					nth(rackTypes[i], data.rackPower, rackMaxLoad[i]),
					nth(rackTypes[i], data.rackPrice, rackCosts[i]),
					nth(rackTypes[i], data.rackConnector, rackMaxConnector[i])
			);
		}
		int idx=0;
		for (int i = 0; i < data.nbCardTypes; i++) {
			idx++;
			for (int j = 1; j < data.cardDemand[i]; j++) {
				model.addConstraint(leq(cardRacks[idx-1],cardRacks[idx]));
				idx++;
			}
		}
	}

	public void createModel() {
		model =new CPModel();
		createVariables();
		createConstraints();
	}

	public void solve() {
		solver = new CPSolver();
		solver.read(model);
		solver.solve();
	}

	public void print() {
		for (int i = 0; i < data.nbRacks; i++) {
			LOGGER.info(solver.getVar(rackTypes[i]).pretty()+" --> "+solver.getVar(plugged[i]).pretty());
		}
	}

	public static void main(String[] args) {
		RackConfigurationData data= new RackConfigurationData(3,new int[]{200,150}, new int[]{16,8},new int[]{200,150},
				new int[]{75,50,40,20},new int[]{1,2,4,10});
		RackConfiguration pb = new RackConfiguration(data);
		pb.createModel();
		LOGGER.info(pb.model.pretty());
		pb.solve();
		pb.print();
	}


}


class RackConfigurationData {

	public final int nbRacks;

	public final int nbCards;

	public final int nbRackModels;

	public final int nbCardTypes;

	public final int[] rackPower;

	public final int[] rackPrice;

	public final int[] rackConnector;

	public final int[] cardPower;

	public final int[] cardDemand;

	public RackConfigurationData(int nbRacks, int[] rackPower,
			int[] rackConnector, int[] rackPrice, int[] cardPower,
			int[] cardDemand) {
		super();
		this.nbRacks = nbRacks;
		this.rackPower = rackPower;
		this.rackConnector = rackConnector;
		this.rackPrice = rackPrice;
		this.cardPower = cardPower;
		this.cardDemand = cardDemand;
		this.nbCardTypes=cardDemand.length;
		this.nbRackModels=rackPrice.length;
		int cpt = 0;
		for (int i = 0; i < cardDemand.length; i++) {
			cpt+=cardDemand[i];
		}
		this.nbCards=cpt;
	}


}