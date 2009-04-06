package choco.kernel.solver.constraints.global.scheduling;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import choco.kernel.common.util.ChocoUtil;
import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.ITask;

public class FakeResource<E extends ITask> implements IResource<E> {

	protected final E[] tasks;
	
	
	
	public FakeResource(E[] tasks) {
		super();
		this.tasks = tasks;
	}
	
	@Override
	public List<E> asList() {
		return Arrays.asList(tasks);
	}

	@Override
	public int getNbTasks() {
		return tasks.length;
	}

	@Override
	public String getRscName() {
		return "FakeResource";
	}

	@Override
	public IRTask getRTask(int idx) {
		return null;
	}

	@Override
	public E getTask(int idx) {
		return tasks[idx];
	}

	@Override
	public Iterator<E> getTaskIterator() {
		return ChocoUtil.iterator(tasks);
	}

	
	
}
