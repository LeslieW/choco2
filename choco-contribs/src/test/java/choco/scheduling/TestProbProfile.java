package choco.scheduling;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.Test;

import choco.cp.solver.search.task.ProbabilisticProfile;
import choco.kernel.common.VizFactory;
import choco.kernel.solver.constraints.global.scheduling.ICumulativeResource;
import choco.kernel.solver.constraints.global.scheduling.IResource;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.AbstractTask;
import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.ITask;

///////////////////// REMOVE class (import problem with cp.test) ///////////////////


class SimpleTask extends AbstractTask {

	private static int nextID=0;

	private final Point domain;

	private final int duration;


    /**
     *
     * @param est
     * @param lst
     * @param duration
     */
	public SimpleTask(int est,int lst, int duration) {
		super(nextID++, "T"+nextID);
		this.domain = new Point(est, lst>=est ? lst :est);
		this.duration = duration>0 ? duration : 0;
	}


	/**
	 * @see ITask#getECT()
	 */
	@Override
	public int getECT() {
		return domain.x+duration;
	}

	/**
	 * @see ITask#getEST()
	 */
	@Override
	public int getEST() {
		return domain.x;
	}

	/**
	 * @see ITask#getLCT()
	 */
	@Override
	public int getLCT() {
		return domain.y+duration;
	}

	/**
	 * @see ITask#getLST()
	 */
	@Override
	public int getLST() {
		return domain.y;
	}

	/**
	 * @see ITask#getMinDuration()
	 */
	@Override
	public int getMinDuration() {
		return duration;
	}

	/**
	 * @see ITask#hasConstantDuration()
	 */
	@Override
	public boolean hasConstantDuration() {
		return true;
	}

	/**
	 * @see ITask#isScheduled()
	 */
	@Override
	public boolean isScheduled() {
		return domain.x==domain.y;
	}

	/**
	 * @see ITask#getMaxDuration()
	 */
	@Override
	public int getMaxDuration() {
		return getMinDuration();
	}


}



class SimpleResource implements ICumulativeResource<SimpleTask> {

	
	public final List<SimpleTask> tasksL;

	public int[] heights;
	
	public int capacity;
	
	
	public SimpleResource(List<SimpleTask> tasksL, int[] heights, int capacity) {
		super();
		this.tasksL = tasksL;
		this.heights = heights;
		this.capacity = capacity;
	}

	public SimpleResource(List<SimpleTask> tasksL) {
		super();
		this.tasksL=new ArrayList<SimpleTask>(tasksL);
		this.capacity = 1;
		this.heights = new int[tasksL.size()];
		Arrays.fill(heights, 1);
	}

	
	@Override
	public IRTask getRTask(int idx) {
		return null;
	}

	@Override
	public int getNbTasks() {
		return tasksL.size();
	}

	@Override
	public String getRscName() {
		return "internal resource (test)";
	}

	@Override
	public SimpleTask getTask(int idx) {
		return tasksL.get(idx);
	}

	@Override
	public Iterator<SimpleTask> getTaskIterator() {
		return tasksL.listIterator();
	}
	
	@Override
	public List<SimpleTask> asList() {
		return Collections.unmodifiableList(tasksL);
	}

	@Override
	public IntDomainVar getCapacity() {
		return null;
	}
	@Override
	
	public int getMaxCapacity() {
		return capacity;
	}

	@Override
	public int getMinCapacity() {
		return getCapacity().getInf();
	}

	public IntDomainVar getHeight(int idx) {
		return null;
	}
	
	
	@Override
	public IntDomainVar getConsumption() {
		return null;
	}

	@Override
	public int getMaxConsumption() {
		return 0;
	}

	@Override
	public int getMinConsumption() {
		return 0;
	}

	@Override
	public boolean isInstantiatedHeights() {
		return true;
	}

	@Override
	public boolean hasOnlyPosisiveHeights() {
		return true;
	}
	
	
}


///////////////////////////////////////////////////////////////////////////////////////




public class TestProbProfile {

	private final static double DELTA=0.01;

	private final static boolean DISPLAY=false;

	private IResource<SimpleTask> rsc;

	private ProbabilisticProfile profile;


	private void display() {
		if(DISPLAY) {
			VizFactory.displayGnuplot(new String(profile.draw()));
		}
	}

	private void setTask(int idx,List<SimpleTask> l) {
		switch (idx) {
		case 0 : l.add(new SimpleTask(10,30,40));break;
		case 1 : l.add(new SimpleTask(15,70,20));break;
		case 2 : l.add(new SimpleTask(0,12,10));break;
		case 3 : l.add(new SimpleTask(0,0,15));break;
		default:
			System.err.println("error while creating profile");
		break;
		}
	}

	public void initialize(boolean[] tasks) {
		List<SimpleTask> l=new LinkedList<SimpleTask>();
		for (int i = 0; i < tasks.length; i++) {
			if(tasks[i]) {setTask(i, l);}
		}
		rsc=new SimpleResource(l);
		profile=new ProbabilisticProfile(l);
		profile.initializeEvents();
		profile.generateEventsList(rsc);
	}


	@Test
	public void testProfileTask1() {
		boolean[] t={true,false,false};
		initialize(t);
		display();
		assertTrue("compute : ",profile.compute(10)>0);
		assertTrue("compute : ",profile.compute(30)==1);
		assertTrue("compute : ",profile.compute(40)==1);
		assertTrue("compute : ",profile.compute(51)<1);
		assertEquals("compute : ",0,profile.compute(70),DELTA);
	}

	@Test
	public void testProfileTask2() {
		boolean[] t={false,true,false};
		initialize(t);
		display();
		assertEquals("compute : ",((double) 1)/56,profile.compute(15),DELTA);
		assertEquals("compute : ",0.357,profile.compute(40),DELTA);
		assertEquals("compute : ",0.357,profile.compute(70),DELTA);
		assertEquals("compute : ",0,profile.compute(90),DELTA);
	}
	@Test
	public void testProfileTask3() {
		boolean[] t={false,false,true};
		initialize(t);
		display();
		//assertEquals("compute : ",0.769,profile.compute(11),DELTA);
		assertEquals("compute : ",( (double) 1)/13,profile.compute(0),DELTA);
		//assertEquals("compute : ",0,profile.compute(22),DELTA);
	}

	@Test
	public void testProfileTask4() {
		boolean[] t={false,false,false,true};
		initialize(t);
		display();
		assertEquals("compute : ",1,profile.compute(0),DELTA);
		assertEquals("compute : ",1,profile.compute(10),DELTA);
		assertEquals("compute : ",0,profile.compute(15),DELTA);
	}


	protected void testProfile(int c,double v) {
		assertEquals("prof. coord. max : ",c,profile.getMaxProfileCoord());
		assertEquals("prof. value max : ",v,profile.getMaxProfileValue(),DELTA);

	}

	protected void testProfile(boolean[] involved) {
		for (int i = 0; i < involved.length; i++) {
			assertEquals("prof. max involved "+i+" : ", involved[i],profile.getMaxProfInvolved().contains(rsc.getTask(i)));
		}
	}

	@Test
	public void testProfile() {
		boolean[] t={true,true,true,false};
		initialize(t);
		display();
		SimpleTask task = rsc.getTask(0);
		assertEquals("ind. contrib. : ",0,profile.getIndividualContribution(task, 9),DELTA);
		assertTrue("ind. contrib. : ",profile.getIndividualContribution(task, 10)>0);
		assertEquals("ind. contrib. : ",1,profile.getIndividualContribution(task, 30),DELTA);
		assertEquals("ind. contrib. : ",1,profile.getIndividualContribution(task, 40),DELTA);
		assertEquals("ind. contrib. : ",0,profile.getIndividualContribution(task, 70),DELTA);
		profile.computeMaximum(rsc);
		testProfile(35,1.35);
		testProfile(new boolean[]{true,true,false});
		profile.minimalSize=2;
		profile.computeMaximum(rsc);
		testProfile(35,1.35);
		testProfile(new boolean[]{true,true,false});
	}

	@Test
	public void testProfile2() {
		List<SimpleTask> l=new LinkedList<SimpleTask>();
		l.add(new SimpleTask(0,0,10));
		l.add(new SimpleTask(10,20,12));
		l.add(new SimpleTask(28,38,15));
		rsc=new SimpleResource(l);
		profile=new ProbabilisticProfile(l);
		profile.initializeEvents();
		profile.computeMaximum(rsc);
		testProfile(0,1);
		testProfile(new boolean[]{true,false,false});
		profile.minimalSize=2;
		profile.computeMaximum(rsc);
		testProfile(28,0.54);
		testProfile(new boolean[]{false,true,true});
	}

}




