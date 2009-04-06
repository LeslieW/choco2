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
package choco.memory;


import choco.cp.solver.constraints.global.scheduling.Precedence;
import choco.kernel.memory.*;
import choco.kernel.memory.trailing.EnvironmentTrailing;
import choco.kernel.solver.variables.scheduling.TaskVar;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author Arnaud Malapert</br> 
 * @since 11 févr. 2009 version 2.0.3</br>
 * @version 2.0.0</br>
 */
public class StoredCollectionTest {

	private final static int NB_TESTS = 1000;

	@Test
	public void testStoredCollection() {

	}
	@Test
    @Ignore
	public void testTrailMemoryLeak() {
		EnvironmentTrailing env = new EnvironmentTrailing();
		//Trail Objects
		IStateBool sbool = env.makeBool(true);
		IStateInt sint = env.makeInt(0);
		IStateLong slong = env.makeLong(0);
		IStateDouble sdouble = env.makeFloat(0);
		IStateVector<Object> svector = env.makeVector();

		svector.add(0);

		IStateIntVector sintvector = env.makeIntVector(new int[]{0});
		IStateBinaryTree sbtree = env.makeBinaryTree(0, NB_TESTS);
		env.worldPush();
		//loop with operation then reverse operation for each object
		for (int i = 0; i < NB_TESTS; i++) {
			sbool.set(false);
			sbool.set(true);
			sint.set(i);
			sint.set(0);
			slong.set(i);
			slong.set(0);
			sdouble.add(i);
			sdouble.add(-i);
			sdouble.set(i);
			sdouble.set(0);
			sintvector.add(i);
			sintvector.removeLast();
			sintvector.set(0, i);
			sintvector.set(0, 0);
			svector.add(i);
			svector.removeLast();
			svector.set(0, i);
			svector.set(0, 0);

			sbtree.remove(i);
			sbtree.remove(i+1);
			sbtree.remove(i+2);
			sbtree.add(i, i+2);
			env.worldPush();
		}
		System.out.println("Total Trail Size: "+env.getTrailSize());
		//check final value
		assertEquals(true, sbool.get());
		assertEquals(0, sint.get());
		assertEquals(0, slong.get());
		assertEquals(0, sdouble.get(),0.01);
		assertEquals(1, sintvector.size());
		assertEquals(1, svector.size());
		assertEquals(NB_TESTS+2, sbtree.getSize());
		//check Trail size
		assertEquals("bool trail", 0, env.getBoolTrailSize());
		assertEquals("int trail", 0, env.getIntTrailSize());
		assertEquals("long trail", 0, env.getLongTrailSize());
		assertEquals("float trail", 0, env.getFloatTrailSize());
		assertEquals("int vector trail", 0, env.getIntVectorTrailSize());
		assertEquals("vector trail", 0, env.getVectorTrailSize());
		assertEquals("btree trail", 0, env.getBinaryTreeTrailSize());

	}

	@Test
	public void testTrailMemoryLeakPatch() {
		EnvironmentTrailing env = new EnvironmentTrailing();
		StoredCollection2Trail collTrail = new StoredCollection2Trail(env,1000,1000);
		//Trail Objects
		IStateCollection2<Integer> scoll = collTrail.makeStoredCollection();
		scoll.staticAdd(0);
		env.worldPush();
		//loop with operation then reverse operation for each object
		for (int i = 0; i < NB_TESTS; i++) {
			scoll.storedAdd(1);
			scoll.storedRemove(1);
			collTrail.worldPush();
			env.worldPush();
		}
		System.out.println("Total Trail Size: "+env.getTrailSize());
		//check final value
		assertEquals(1, scoll.size());
		//check Trail size
		assertEquals("coll trail", 0, collTrail.getSize());
	}

	/**
	 * testing the empty constructor with a few backtracks, additions, and updates
	 */
	@Test
	public void test1() {
		EnvironmentTrailing env = new EnvironmentTrailing();	
		StoredCollection2Trail collTrail = new StoredCollection2Trail(env,1000,1000);
		IStateCollection2<Integer> vector = collTrail.makeStoredCollection();
		assertEquals(0, env.getWorldIndex());
		assertTrue(vector.isEmpty());
		collTrail.worldPush();
		env.worldPush();
		assertEquals(1, env.getWorldIndex());
		vector.add(new Integer(0));
		vector.add(new Integer(1));
		collTrail.worldPush();
		env.worldPush();
		assertEquals(2, env.getWorldIndex());
		vector.add(new Integer(2));
		vector.add(new Integer(3));
		vector.staticAdd(new Integer(4));
		assertEquals(5, vector.size());
		collTrail.worldPop();
		env.worldPop();
		assertEquals(3, vector.size());
		assertEquals(1, env.getWorldIndex());
		collTrail.worldPop();
		env.worldPop();
		assertEquals(1, vector.size());
		assertEquals(0, env.getWorldIndex());
	}
	//	private EnvironmentTrailing env;
	//
	//	private StoredUnorderedList<Integer> list;
	//
	//
	//	@SuppressWarnings("unchecked")
	//	@Before
	//	public void setUp() {
	//		env = new EnvironmentTrailing();
	//		list = (StoredUnorderedList<Integer>) env.makeList();
	//	}
	//
	//	@After
	//	public void tearDown() {
	//		list = null;
	//		env = null;
	//	}
	//
	//	public void add(int v) {
	//		list.add(Integer.valueOf(v));
	//	}
	//
	//	public void remove(int v) {
	//		list.remove(Integer.valueOf(v));
	//	}
	//
	//	private void testTrail(int world,int tsize,int lsize) {
	//		assertEquals("world", world, env.getWorldIndex());
	//		assertEquals("trail size", tsize, env.getTrailSize());
	//		assertEquals("element", lsize, list.size());
	//	}
	//
	//	@Test
	//	public void testList1() {
	//		env.worldPush();
	//		add(0);
	//		env.worldPush();
	//		assertEquals("element", 0, list.getList().get(0).intValue());
	//		testTrail(2, 1, 1);
	//		env.worldPush();
	//		testTrail(3, 1, 1);
	//		env.worldPop();
	//		testTrail(2, 1, 1);
	//		env.worldPop();
	//		env.worldPop();
	//		testTrail(0, 1, 0);
	//	}
	//
	//
	//	@Test
	//	public void testList2() {
	//		env.worldPush();
	//		add(0);
	//		env.worldPush();
	//		add(1);
	//		env.worldPush();
	//		testTrail(3, 1, 2);
	//		remove(1);
	//		testTrail(3, 1, 1);
	//		env.worldPush();
	//		testTrail(4, 1, 1);
	//		env.worldPop();
	//		testTrail(3, 1, 1);
	//		env.worldPop();
	//		testTrail(2, 1, 2);
	//		System.out.println(list.getList());
	//		env.worldPop();
	//		testTrail(1, 1, 1);
	//		env.worldPop();
	//		testTrail(0, 1, 0);
	//	}
	//
	//	@SuppressWarnings({"unchecked" })
	//	@Test
	//	public void testList3() {
	//		env.worldPush();
	//		testTrail(1, 1, 0);
	//		add(0);
	//		env.worldPush();
	//		StoredUnorderedList<Integer> list2=(StoredUnorderedList<Integer>) env.makeList(new ArrayList<Integer>());
	//		list2.add(Integer.valueOf(10));
	//		testTrail(2, 2, 1);
	//		env.worldPush();
	//		testTrail(3, 2, 1);
	//		env.worldPop();
	//		env.worldPop();
	//		testTrail(1, 1, 1);
	//
	//		try {
	//			list2.add(Integer.valueOf(11));
	//		} catch (MemoryException e) {
	//			return;
	//		}
	//		fail("expected Memory exception");
	//	}
	//



//	private void empty(StoredPrecDag[] graphs,boolean empty) {
//		for (StoredPrecDag g : graphs) {
//			assertEquals("empty : ",empty,g.getGraph().isEmpty());
//		}
//	}
//
//
//	private void isConnected(StoredPrecDag[] graphs,TaskVar t1,TaskVar t2,boolean isConnected) {
//		for (StoredPrecDag g : graphs) {
//			assertEquals("isConnected "+t1.getName()+" -> "+t2.getName()+" : ",isConnected,g.isConnected(t1, t2));
//		}
//	}
//	private void postPrec(StoredPrecDag[] graphs,Precedence prec) {
//		for (StoredPrecDag g : graphs) {
//			g.add(prec);
//		}
//	}
//
//	private void checkFlags(StoredPrecDag[] graphs,boolean cycle,boolean existing) {
//		for (StoredPrecDag g : graphs) {
//			assertEquals("cycle flag :",cycle,g.cycleDetected());
//			assertEquals("exist flag :",existing,g.doublonDetected());
//		}
//	}

	@Ignore
	@Test
	public void testPrecDag() {
		fail("TODO");
		//		SchedulingModel m =new SchedulingModel(12);
		//		TaskVariable t1= Choco.makeTaskVar("t1",12, 1);
		//		TaskVariable t2=Choco.makeTaskVar("t2", 12, 2);
		//		TaskVariable t3=Choco.makeTaskVar("t3", 12, 3);
		////
		//		CPSolver s = new CPSolver();
		//		EnvironmentTrailing env= (EnvironmentTrailing) s.getEnvironment();
		////
		//		StoredPrecDag[] graphs={ (StoredPrecDag) env.makePrecDag(5,false), (StoredPrecDag) env.makePrecDag(7,true), 
		//				(StoredPrecDag) env.makePrecDag(10,false) };
		//		int cpt=0;
		//		pb.close();
		//		pb.worldPush();
		//		empty(graphs, true);
		//		postPrec(graphs,new Precedence(t1,t2,0,pb.makeConstant(1)));
		//		isConnected(graphs, t1, t2,true);
		//		pb.worldPush();
		//		postPrec(graphs,new Precedence(t3,t2,1,pb.makeConstant(2)));
		//		isConnected(graphs, t3, t2, true);
		//		pb.worldPush();
		//		postPrec(graphs,new Precedence(t3,t1,1,pb.makeConstant(2)));
		//		postPrec(graphs,new Precedence(t1,t3,1,pb.makeConstant(2)));
		//		isConnected(graphs, t3, t1, true);
		//		isConnected(graphs, t1, t3, false);
		//		empty(graphs, false);
		//		checkFlags(graphs, true, false);
		//		pb.worldPush();
		//		postPrec(graphs,new Precedence(t3,t1,1,pb.makeConstant(2)));
		//		checkFlags(graphs, true, true);
		//		pb.worldPop();
		//		checkFlags(graphs, true, false);
		//		//le flag ne disparait qu'au prochain pop
		//		pb.worldPop();
		//		isConnected(graphs, t3, t1, false);
		//		checkFlags(graphs, false, false);
		//
		//		pb.worldPop();
		//		isConnected(graphs, t3, t2, false);
		//		pb.worldPop();
		//		empty(graphs, true);

	}


}
