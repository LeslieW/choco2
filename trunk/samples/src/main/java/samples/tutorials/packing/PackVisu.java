package samples.tutorials.packing;

import java.util.logging.Level;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.constraints.pack.PackModel;
import choco.visu.components.chart.ChocoChartFactory;
import samples.tutorials.PatternExample;
import samples.tutorials.scheduling.pack.binpacking.BinPackingExample;

public class PackVisu extends PatternExample {

	private PackModel pm1, pm2;

	@Override
	public void buildModel() {
		model =new CPModel();
		pm1 = new PackModel(BinPackingExample.N1C1W1_N,BinPackingExample.OPT_N+2,BinPackingExample.CAPACITY_N);
		pm2 = new PackModel(BinPackingExample.N2C2W1_H,BinPackingExample.OPT_H+4,BinPackingExample.CAPACITY);
		model.addConstraints( Choco.pack(pm1), Choco.pack(pm2));
	}

	@Override
	public void buildSolver() {
		solver =new CPSolver();
		solver.read(model);
		solver.setTimeLimit(2000);
	}

	@Override
	public void prettyOut() {
		if(LOGGER.isLoggable(Level.INFO)) {
			final String title = "Bin Packing Constraint Visualization";
			LOGGER.info(title);
			if(solver.existsSolution()) ChocoChartFactory.createAndShowGUI(title,ChocoChartFactory.createPackChart(title, solver,pm1, pm2));
		}
	}

	@Override
	public void solve() {
		solver.solve();
	}

	public static void main(String[] args) {
		new PackVisu().execute();
	}

}
