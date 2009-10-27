package choco.visu.components.chart;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.jfree.chart.ChartPanel;
import org.jfree.data.general.Dataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import samples.pack.BinPackingExample;
import samples.pack.CPpack;
import choco.cp.solver.constraints.global.pack.PackSConstraint;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintType;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.global.scheduling.ICumulativeResource;
import choco.kernel.solver.variables.scheduling.TaskVar;
import choco.visu.components.chart.renderer.MyXYBarRenderer.ResourceRenderer;

public class ChocoChartPanel extends JPanel implements TreeSelectionListener {
    
    protected final static Logger LOGGER = ChocoLogging.getSolverLogger();

	private final static JLabel NO_DISPLAY= new JLabel("NO DISPLAY", JLabel.CENTER);
	

	JScrollPane htmlView;

	private final JTree tree;

	private final Solver solver;
	private final Model model;

	private JSplitPane splitPane;
	private static boolean DEBUG = false;

	//Optionally play with line styles.  Possible values are
	//"Angled" (the default), "Horizontal", and "None".
	private static boolean playWithLineStyle = true;
	private static String lineStyle = "Horizontal";

	//Optionally set the look and feel.
	private static boolean useSystemLookAndFeel = false;

	public ChocoChartPanel(Solver solver) {
		super(new GridLayout(1,0));
		this.solver = solver;
		model = this.solver.getModel();
		//Create the nodes.
		DefaultMutableTreeNode top =
			new DefaultMutableTreeNode("Choco Resources");
		createPackNodes(top);
		createDisjNodes(top);
		createCumulNodes(top);
		

		//Create a tree that allows one selection at a time.
		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode
		(TreeSelectionModel.SINGLE_TREE_SELECTION);

		//Listen for when the selection changes.
		tree.addTreeSelectionListener(this);

		if (playWithLineStyle) {
			LOGGER.log(Level.INFO,"line style = {0}", lineStyle);
			tree.putClientProperty("JTree.lineStyle", lineStyle);
		}

		//Create the scroll pane and add the tree to it. 
		JScrollPane treeView = new JScrollPane(tree);

	

		//Add the scroll panes to a split pane.
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setTopComponent(treeView);
		splitPane.setBottomComponent(NO_DISPLAY);

		Dimension minimumSize = new Dimension(100, 50);
		treeView.setMinimumSize(minimumSize);
		splitPane.setDividerLocation(150); 
		splitPane.setPreferredSize(new Dimension(600, 300));

		//Add the split pane to this panel.
		add(splitPane);
	}

	/** Required by TreeSelectionListener interface. */
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
		tree.getLastSelectedPathComponent();

		if (node == null) return;
		Object nodeInfo = node.getUserObject();
		if (nodeInfo instanceof ResourceNode) {
			ResourceNode rscNode = (ResourceNode) nodeInfo;
			//			htmlView.removeAll();
			//			htmlView.add(rscNode.getResourceView());
			//			htmlView.repaint();

			splitPane.setBottomComponent(rscNode.getResourceView());
			repaint();
		}
		if (DEBUG) {
			LOGGER.info(nodeInfo.toString());
		}
	}

	private static interface ResourceNode {

		JComponent getResourceView();

	}



	private static abstract class AbstractResourceView implements ResourceNode {

		private Dataset dataset;

		private JComponent viewPanel;

		private final String name;


		public AbstractResourceView(String name) {
			super();
			this.name = name;
		}




		@Override
		public JComponent getResourceView() {
			if(viewPanel == null) { 
				viewPanel = createViewPanel();
			}
			return viewPanel;
		}


		protected abstract JComponent createViewPanel();


		@Override
		public String toString() {
			return name;
		}
	}


	private static class PackView extends AbstractResourceView {

		private final PackSConstraint pack;

		public PackView(String name, PackSConstraint pack) {
			super(name);
			this.pack = pack;
		}


		@Override
		protected JComponent createViewPanel() {
			return new ChartPanel(ChocoChartFactory.createPackChart(this.toString(),pack));
		}

	}
	
	private static class CumulView extends AbstractResourceView {
		
		private final ICumulativeResource<TaskVar> cumul;

		public CumulView(ICumulativeResource<TaskVar> cumul) {
			super(cumul.getRscName());
			this.cumul = cumul;
		}
		
		
		@Override
		protected JComponent createViewPanel() {
			return new ChartPanel(ChocoChartFactory.createCumulativeChart(this.toString(), cumul, true));
		}

		
	}
	

	private  class DisjView extends AbstractResourceView {


		public DisjView() {
			super("Disjunctive");
		}


		@Override
		protected JComponent createViewPanel() {
			return new ChartPanel(ChocoChartFactory.createUnaryChart(this.toString(), solver, ResourceRenderer.COORD));
		}

	}



	private final void createDisjNodes(DefaultMutableTreeNode top) {
		top.add(new DefaultMutableTreeNode(new DisjView()));
	}
	
	private final void createPackNodes(DefaultMutableTreeNode top) {
		DefaultMutableTreeNode pack = new DefaultMutableTreeNode("Packing");
		top.add(pack);
		final int n = model.getNbConstraintByType(ConstraintType.PACK);
		Iterator<Constraint> cstr = model.getConstraintByType(ConstraintType.PACK);
		for (int i = 0; i < n; i++) {
			pack.add(new DefaultMutableTreeNode(new PackView("pack "+i, (PackSConstraint) solver.getCstr(cstr.next()))));
		}
	}
	
	@SuppressWarnings("unchecked")
	private final void createCumulNodes(DefaultMutableTreeNode top) {
		final DefaultMutableTreeNode cumul = new DefaultMutableTreeNode("Cumulative");
		top.add(cumul);
		final Iterator<Constraint> cstr = model.getConstraintByType(ConstraintType.CUMULATIVE);
		while(cstr.hasNext()) {
			cumul.add(new DefaultMutableTreeNode(new CumulView( (ICumulativeResource<TaskVar>) solver.getCstr(cstr.next()))));
		}
	}
	

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event dispatch thread.
	 */
	private static void createAndShowGUI() {
		if (useSystemLookAndFeel) {
			try {
				UIManager.setLookAndFeel(
						UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				LOGGER.severe("Couldn't use system look and feel.");
			}
		}

		CPpack cppack = new CPpack(BinPackingExample.N1C1W1_N,BinPackingExample.CAPACITY_N,BinPackingExample.OPT_N);
		cppack.setTimelimit(5);
		cppack.cpPack();
		//Create and set up the window.
		final ApplicationFrame frame = new ApplicationFrame("Choco Visu Demo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Add content to the window.
		frame.add(new ChocoChartPanel(cppack.getSolver()));

		//Display the window.
		frame.pack();
		RefineryUtilities.centerFrameOnScreen(frame);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		//Schedule a job for the event dispatch thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}