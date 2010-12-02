package choco.kernel.solver.constraints.global.scheduling;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import choco.kernel.common.util.tools.IteratorUtils;
import choco.kernel.solver.variables.scheduling.IRTask;
import choco.kernel.solver.variables.scheduling.ITask;

public class FakeResource<E extends ITask> implements IResource<E> {
	private static final long serialVersionUID = -6700792497144348896L;

	private final E[] tasks;
	
	public FakeResource(E[] tasks) {
		super();
		this.tasks = tasks;
	}
	
	@Override
	public List<E> asTaskList() {
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
	public List<IRTask> asRTaskList() {
		return Collections.<IRTask>emptyList();
	}

	@Override
	public E getTask(int idx) {
		return tasks[idx];
	}

	@Override
	public Iterator<E> getTaskIterator() {
		return IteratorUtils.iterator(tasks);
	}
	
	@Override
	public Iterator<IRTask> getRTaskIterator() {
		return asRTaskList().iterator();
	}
	
	@Override
	public int getNbOptionalTasks() {
		return 0;
	}

	@Override
	public int getNbRegularTasks() {
		return getNbTasks();
	}

	
	
}
