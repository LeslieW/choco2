/* ************************************************
 *           _      _                             *
 *          |  (..)  |                            *
 *          |_ J||L _|         CHOCO solver       *
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
 *                   N. Jussien    1999-2010      *
 **************************************************/
package samples.tutorials.packing;

import org.jfree.chart.JFreeChart;

import parser.instance.AbstractMinimizeModel;
import parser.instance.IHeuristicAlgorithm;
import parser.instances.BasicSettings;
import choco.Choco;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.constraints.global.pack.PackSConstraint;
import choco.cp.solver.search.BranchingFactory;
import choco.cp.solver.search.integer.branching.PackDynRemovals;
import choco.cp.solver.search.integer.valselector.BestFit;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.kernel.common.opres.pack.LowerBoundFactory;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.pack.PackModel;
import choco.kernel.solver.Configuration;
import choco.kernel.solver.Solver;
import choco.visu.components.chart.ChocoChartFactory;


/**
 *
 * @author Arnaud Malapert
 */
public class BinPackingModel extends AbstractMinimizeModel {

	private int nbBins;
	
	/** variables of the model*/
	private PackModel modeler;

	private Constraint pack;

	public BinPackingModel(Configuration configuration) {
		super(new BinPackingFileParser(), configuration);
	}

	@Override
	public void initialize() {
		super.initialize();
		heuristics = IHeuristicAlgorithm.SINGLOTON;
		nbBins = 0;
		modeler = null;
		pack = null;
	}

	@Override
	public Boolean preprocess() {
		final BinPackingFileParser pr = (BinPackingFileParser) parser;
		heuristics = new CompositeHeuristics1BP(pr);
		Boolean b = super.preprocess();
		if(b != null && Boolean.valueOf(b)) {
			computedLowerBound = LowerBoundFactory.computeL_DFF_1BP(pr.sizes, pr.capacity, heuristics.getObjectiveValue().intValue());
			nbBins = heuristics.getObjectiveValue().intValue() - 1;
		}else {
			computedLowerBound = 0;
			nbBins = pr.sizes.length;
		}
		return b;
	}

	@Override
	public Model buildModel() {
		CPModel m = new CPModel();
		final BinPackingFileParser pr = (BinPackingFileParser) parser;
		modeler = new PackModel(pr.sizes, nbBins, pr.capacity);
		pack = Choco.pack(modeler, Options.C_PACK_AR, Options.C_PACK_DLB);
		m.addConstraint(pack);
		if( ! defaultConf.readBoolean(BasicSettings.LIGHT_MODEL) ) {
			pack.addOption(Options.C_PACK_FB);
			m.addConstraints(modeler.packLargeItems()); // best symmetry breaking ? 
		}
		modeler.nbNonEmpty.addOption(Options.V_OBJECTIVE);
		return m;
	}

	@Override
	public Solver buildSolver() {
		Solver s = super.buildSolver(); // create the solver
		s.read(model);  //read the model
		if(defaultConf.readBoolean(BasicSettings.LIGHT_MODEL) ) {
			s.clearGoals();
			s.addGoal(BranchingFactory.lexicographic(s, s.getVar(modeler.getBins())));
		}else {
			s.clearGoals();
			final PackSConstraint ct = (PackSConstraint) s.getCstr(pack);
			//value selection : First-Fit ~ MinVal
			s.addGoal(new PackDynRemovals(new StaticVarOrder(s, s.getVar(modeler.getBins())), new BestFit(ct), ct));
		}
		s.generateSearchStrategy();
		return s;
	}

	@Override
	public String getValuesMessage() {
		if(solver != null && solver.existsSolution()) {
			return ( (PackSConstraint) solver.getCstr(pack) ).getSolutionMsg(); 
		} else return "";
	}	
	
	@Override
	public JFreeChart makeSolutionChart() {
		return solver != null && solver.existsSolution() ?
				ChocoChartFactory.createPackChart(getInstanceName()+" : "+getStatus(), (PackSConstraint) solver.getCstr(pack)) : null;
	}
	
}
