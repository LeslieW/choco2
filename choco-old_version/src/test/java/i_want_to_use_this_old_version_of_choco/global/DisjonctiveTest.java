package i_want_to_use_this_old_version_of_choco.global;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.Problem;
import i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet;
import i_want_to_use_this_old_version_of_choco.global.scheduling.trees.CumTreeT;
import i_want_to_use_this_old_version_of_choco.global.scheduling.trees.DisjTreeT;
import i_want_to_use_this_old_version_of_choco.global.scheduling.trees.DisjTreeTL;
import i_want_to_use_this_old_version_of_choco.global.scheduling.trees.abstrees.AbstractTree;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import junit.framework.TestCase;

import java.util.ArrayList;


class Tasks implements ITasksSet {

	private IntDomainVar[] vars;

	private int[] p;




	/**
	 * @param vars
	 * @param p
	 */
	public Tasks(IntDomainVar[] vars, int[] p) {
		super();
		this.vars = vars;
		this.p = p;
	}



	/**
	 * @see i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet#getECT(int)
	 */
	//@Override
	public int getECT(int i) {
		return vars[i].getInf()+p[i];
	}

	/**
	 * @see i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet#getEST(int)
	 */
	//@Override
	public int getEST(int i) {
		return vars[i].getInf();
	}

	/**
	 * @see i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet#getLCT(int)
	 */
	//@Override
	public int getLCT(int i) {
		return vars[i].getSup()+p[i];
	}

	/**
	 * @see i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet#getLST(int)
	 */
	//@Override
	public int getLST(int i) {
		return vars[i].getSup();
	}

	/**
	 * @see i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet#getNbTasks()
	 */
	//@Override
	public int getNbTasks() {
		return vars.length;
	}

	/**
	 * @see i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet#getProcessingTime(int)
	 */
	//@Override
	public int getProcessingTime(int i) {
		return p[i];
	}


	/**
	 * @see i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet#getConsumption(int)
	 */
	//@Override
	public long getConsumption(int i) {
		return getProcessingTime(i);
	}



	/**
	 * @see i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet#getTotalLoad()
	 */
	//@Override
	public int getTotalLoad() {
		int s=0;
		for (int i = 0; i < p.length; i++) {
			s+=p[i];
		}
		return s;
	}



	/**
	 * @see i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet#getHeight(int)
	 */
	//@Override
	public int getHeight(int i) {
		return 0;
	}



	/**
	 * @see i_want_to_use_this_old_version_of_choco.global.scheduling.ITasksSet#getTotalConsumption()
	 */
	//@Override
	public int getTotalConsumption() {
		// TODO Auto-generated method stub
		return 0;
	}

}

/**
 * JUnit tests.
 *
 * @author Arnaud Malapert (arnaud.malapert@emn.fr)
 */

public class DisjonctiveTest extends TestCase {

	public static final Problem pb = new Problem();


	public static final int[][] DATA= {
		{5, 6, 4, 10,7},
		{0,4,2,3,6},
		{23,26,16,14,16}
	};

	public static final long[] PROC_TIMES = {5,3,2};
	public static final long[] HEIGHT = {2,2,3};
	public static final long[] EST = {0,0,0};
	public static final long[] LST = {2,3,0};

	private static void thetaExamples(DisjTreeT tree,String pathname) {
		for (int i = 0; i < tree.getNumberOfLeaves()-1; i++) {
			tree.insert(i);
		}
		tree.toDotty(pathname);
	}

	private static void thetaLambdaExamples(DisjTreeTL tree,String pathname) {
		for (int i = 0; i < tree.getNumberOfLeaves()/2+1; i++) {
			tree.rmThetaAndInsertLambda(i);
		}
		tree.toDotty(pathname);
	}

	public static void treesExample() {
		IntDomainVar[] v=createVariables();
		DisjTreeT ttree=new DisjTreeT(new Tasks(v,DATA[0]),AbstractTree.ECT_TREE);
		thetaExamples(ttree, "/tmp/ttree1.dot");
		ttree.setEctOrLst(AbstractTree.LST_TREE);
		ttree.update();
		thetaExamples(ttree, "/tmp/ttree2.dot");

		v=createVariables();
		DisjTreeTL tltree=new DisjTreeTL(new Tasks(v,DATA[0]),AbstractTree.ECT_TREE);
		thetaLambdaExamples(tltree, "/tmp/tltree1.dot");
		tltree.setEctOrLst(AbstractTree.LST_TREE);
		tltree.update();
		thetaLambdaExamples(tltree, "/tmp/tltree2.dot");
	}

	public static IntDomainVar[] createVariables() {
		IntDomainVar[] vars = new IntDomainVar[DATA[0].length];
		for (int i = 0; i < vars.length; i++) {
			vars[i] = pb.makeBoundIntVar("X_" + i, DATA[1][i],DATA[2][i]);
		}
		return vars;
	}


	public void launch(DisjTreeT tree,int[] res,String label) {
		for (int i = 0; i < tree.getNumberOfLeaves(); i++) {
			assertEquals("insert",tree.insert(i),true);
			assertEquals("insert" ,tree.insert(i),false);
			assertEquals("Ttime "+label,	res[i],tree.getTime());

		}
		for (int i = tree.getNumberOfLeaves()-1;i>=0; i--) {
			assertEquals("Ttime "+label,res[i],tree.getTime());
			assertEquals("remove" ,tree.remove(i),true);
			assertEquals("remove" ,tree.remove(i),false);
		}
	}

	public void testDisjTreeT_ECT() {
		IntDomainVar[] v=createVariables();
		DisjTreeT tree=new DisjTreeT(new Tasks(v,DATA[0]),AbstractTree.ECT_TREE);
		int[] res={5,11,15,25,32};
		launch(tree, res,"ECT");
	}

	public void testDisjTreeT_LST() {
		IntDomainVar[] v=createVariables();
		DisjTreeT tree=new DisjTreeT(new Tasks(v,DATA[0]),AbstractTree.LST_TREE);
		int[] res={23,21,16,7,0};
		launch(tree, res,"LST");
	}


	public void launch(DisjTreeTL tree,int[] res1,int[] res2,int[] res3,String label) {
		for (int i = 0; i < tree.getNumberOfLeaves()-1; i++) {
			assertEquals("rm theta ",tree.rmThetaAndInsertLambda(i),true);
			assertEquals("rm theta ",tree.rmThetaAndInsertLambda(i),false);
			assertEquals("time "+label,	res1[i],tree.getTime());
			assertEquals("gray time "+label,	res2[i],tree.getGrayTime());
			assertEquals("resp. task"+label,	res3[i],tree.getGrayResponsibleTask());
		}
		tree.update();
		int shift=tree.getNumberOfLeaves()/2+1;
		for (int i = 0; i < shift; i++) {
			assertEquals("rm theta ",tree.rmThetaAndInsertLambda(i),true);
			assertEquals("gray time "+label,	res2[i],tree.getGrayTime());
			assertEquals("resp. task "+label,	res3[i],tree.getGrayResponsibleTask());
		}

		for (int i = 0; i < shift; i++) {
			assertEquals("rm lambda",tree.rmLambda(i),true);
			assertEquals("rm lambda",tree.rmLambda(i),false);
			tree.toDotty("/tmp/test.dot");
//			assertEquals("gray time "+label,res2[i+tree.getNumberOfLeaves()-1],tree.getGrayTime());
//			assertEquals("resp. task "+label,res3[i+tree.getNumberOfLeaves()-1],tree.getGrayResponsibleTask());
		}

	}

	public void testDisjTreeTL_ECT() {
		IntDomainVar[] v=createVariables();
		DisjTreeTL tree=new DisjTreeTL(new Tasks(v,DATA[0]),AbstractTree.ECT_TREE);
		int[] res1={29,23,20,13};;
		int[] res2={32,29,26,20,26,23,20};
		int[] res3={0,1,1,3,1,2,-1};
		launch(tree, res1,res2,res3,"ECT");
	}

	public void testDisjTreeTL_LST() {
		IntDomainVar[] v=createVariables();
		DisjTreeTL tree=new DisjTreeTL(new Tasks(v,DATA[0]),AbstractTree.LST_TREE);
		int[] res1={3,3,7,16};
		int[] res2={0,2,3,7,3,3,7};
		int[] res3={0,0,2,3,2,2,-1};
		launch(tree, res1,res2,res3,"LST");
	}


	public void testFolderTree() {
	IntDomainVar[] v={pb.makeBoundIntVar("X_0",0, 10)};
	int[] p={6};
	DisjTreeT tree=new DisjTreeT(new Tasks(v,p),AbstractTree.ECT_TREE);
	tree.insert(0);
	assertEquals("folder tree", 6,tree.getTime());
	tree.update();
	tree.insert(0);
	assertEquals("folder tree", 6,tree.getTime());
	tree.remove(0);
	assertTrue("folder tree",tree.getTime()<0);
	}


	public void testCumulTree() {
		IntDomainVar[] vars = new IntDomainVar[EST.length];
		for (int i = 0; i < vars.length; i++) {
			vars[i] = pb.makeBoundIntVar("X3_" + i, (int) EST[i], (int) LST[i]);
		}
		int[] LE = new int[]{7,7,2};
		//changer en energy
		long[] energy=new long[PROC_TIMES.length];
		int[] energy2=new int[PROC_TIMES.length];
		for (int i = 0; i < energy.length; i++) {
			energy[i]=PROC_TIMES[i]*HEIGHT[i];
			energy2[i]= (int) energy[i];
		}
		CumTreeT tree = new CumTreeT(new Tasks(vars,energy2),AbstractTree.ECT_TREE, 4);
		ArrayList<Integer> Ytasks = new ArrayList<Integer>();
		Ytasks.add(2);
		Ytasks.add(1);
		Ytasks.add(0);
		for (int i : Ytasks) {
			tree.insert(i);
			System.out.println("add " + i + " with " + vars[i].pretty() + " " + tree.getEnergy() + " " + 4 * LE[i]);
			if (tree.getEnergy() > 4 * LE[i]) {
				assertTrue(false);
			}
		}
		try {
			vars[0].setInf(2);
			vars[1].setInf(2);
		} catch (ContradictionException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}

		tree.reset(); //reset plante ici
	}




}

