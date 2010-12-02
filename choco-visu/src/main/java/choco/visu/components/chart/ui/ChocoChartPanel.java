package choco.visu.components.chart.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintType;
import choco.kernel.solver.Solver;

public class ChocoChartPanel extends JPanel implements TreeSelectionListener {

	private static final long serialVersionUID = -744486445775610448L;

	protected final static Logger LOGGER = ChocoLogging.getEngineLogger();

	public final static JLabel NO_DISPLAY= new JLabel("NO DISPLAY", JLabel.CENTER);

	//private JScrollPane htmlView;

	private final JTree tree;

	private final Solver solver;
	
	private JSplitPane splitPane;
	
	//Optionally play with line styles.  Possible values are
	//"Angled" (the default), "Horizontal", and "None".
	private static boolean playWithLineStyle = true;
	private static String lineStyle = "Horizontal";


	public ChocoChartPanel(Solver solver) {
		super(new GridLayout(1,0));
		this.solver = solver;
		//Create the nodes.
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Choco Resources");
		DefaultMutableTreeNode tmpN = new DefaultMutableTreeNode(new DefaultResourceView(solver));
		root.add(tmpN);
		final Model m = solver.getModel();
		createNodes(tmpN, m.getConstraintByType(ConstraintType.DISJUNCTIVE));
		tmpN = new DefaultMutableTreeNode("Cumulative");
		root.add(tmpN);
		createNodes(tmpN, m.getConstraintByType(ConstraintType.CUMULATIVE));
		tmpN = new DefaultMutableTreeNode("Packing");
		root.add(tmpN);
		createNodes(tmpN, m.getConstraintByType(ConstraintType.PACK));
	
		//Create a tree that allows one selection at a time.
		tree = new JTree(root);
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
		if (nodeInfo instanceof IResourceNode) {
			IResourceNode rscNode = (IResourceNode) nodeInfo;
			//			htmlView.removeAll();
			//			htmlView.add(rscNode.getResourceView());
			//			htmlView.repaint();

			splitPane.setBottomComponent(rscNode.getResourceView());
			repaint();
		}
		LOGGER.fine(nodeInfo.toString());
	}


	protected final void createNodes(DefaultMutableTreeNode father, Iterator<Constraint> iter) {
		while(iter.hasNext()) {
			father.add(new DefaultMutableTreeNode(new DefaultResourceView(solver.getCstr(iter.next()))));
		}
	}
	
	
}