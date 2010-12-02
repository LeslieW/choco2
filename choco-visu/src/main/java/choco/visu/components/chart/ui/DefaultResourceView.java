package choco.visu.components.chart.ui;

import static choco.visu.components.chart.ChocoChartFactory.*;
import javax.swing.JComponent;

import org.jfree.chart.ChartPanel;

import choco.cp.solver.constraints.global.pack.IPackSConstraint;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.global.scheduling.ICumulativeResource;
import choco.kernel.solver.constraints.global.scheduling.IResource;
import choco.kernel.solver.variables.scheduling.TaskVar;

public class DefaultResourceView implements IResourceNode {

		private final Object obj;

		private JComponent viewPanel;

		public DefaultResourceView(Object obj) {
			super();
			this.obj = obj;
		}

		@Override
		public final JComponent getResourceView() {
			if(viewPanel == null) { 
				viewPanel = createViewPanel();
			}
			return viewPanel;
		}


		protected JComponent createViewPanel() {
			if (obj instanceof Solver) {
				return new ChartPanel( createUnaryHChart(null, (Solver) obj));
			} else if (obj instanceof IPackSConstraint) {
				return new ChartPanel(createPackChart(null, (IPackSConstraint) obj));
			} else if (obj instanceof ICumulativeResource<?>) {
				return new ChartPanel(createCumulativeChart(null, (ICumulativeResource<TaskVar>) obj, true));
			} else if (obj instanceof IResource<?>) {
				return new ChartPanel(createUnaryHChart(null, (IResource<TaskVar>) obj));
			}		
			return ChocoChartPanel.NO_DISPLAY;
		}

		@Override
		public String toString() {
			return obj instanceof Solver ? "Disjunctive" : obj.toString();
		}
	}
