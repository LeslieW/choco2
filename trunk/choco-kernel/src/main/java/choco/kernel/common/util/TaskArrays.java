package choco.kernel.common.util;

import java.util.List;
import java.util.ListIterator;

import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;

public final class TaskArrays {

	private TaskArrays() {}
	
	//*****************************************************************//
	//*******************  TaskVariable  ********************************//
	//***************************************************************//
	
	public static IntegerVariable[] getStartVars(TaskVariable... tasks) {
		final IntegerVariable[] vars = new IntegerVariable[tasks.length];
		for (int i = 0; i < tasks.length; i++) {
			vars[i] = tasks[i].start();
		}
		return vars;
	}
	
	public static IntegerVariable[] getDurationVars(TaskVariable... tasks) {
		final IntegerVariable[] vars = new IntegerVariable[tasks.length];
		for (int i = 0; i < tasks.length; i++) {
			vars[i] = tasks[i].duration();
		}
		return vars;
	}

	public static IntegerVariable[] getEndVars(TaskVariable... tasks) {
		final IntegerVariable[] vars = new IntegerVariable[tasks.length];
		for (int i = 0; i < tasks.length; i++) {
			vars[i] = tasks[i].end();
		}
		return vars;
	}
	
	public static IntegerVariable[] getStartVars(List<TaskVariable> tasks) {
		final IntegerVariable[] vars = new IntegerVariable[tasks.size()];
		ListIterator<TaskVariable> iter = tasks.listIterator();
		while(iter.hasNext()) {
			vars[iter.nextIndex()] = iter.next().start(); 
		}
		return vars;
	}
	

	public static IntegerVariable[] getDurationVars(List<TaskVariable> tasks) {
		final IntegerVariable[] vars = new IntegerVariable[tasks.size()];
		ListIterator<TaskVariable> iter = tasks.listIterator();
		while(iter.hasNext()) {
			vars[iter.nextIndex()] = iter.next().duration(); 
		}
		return vars;
	}
	
	public static IntegerVariable[] getEndVars(List<TaskVariable> tasks) {
		final IntegerVariable[] vars = new IntegerVariable[tasks.size()];
		ListIterator<TaskVariable> iter = tasks.listIterator();
		while(iter.hasNext()) {
			vars[iter.nextIndex()] = iter.next().end(); 
		}
		return vars;
	}
	

	
	
	//*****************************************************************//
	//*******************  TaskVar  ********************************//
	//***************************************************************//
	
	public static IntDomainVar[] getStartVars(TaskVar... tasks) {
		final IntDomainVar[] vars = new IntDomainVar[tasks.length];
		for (int i = 0; i < tasks.length; i++) {
			vars[i] = tasks[i].start();
		}
		return vars;
	}
	
	public static IntDomainVar[] getDurationVars(TaskVar... tasks) {
		final IntDomainVar[] vars = new IntDomainVar[tasks.length];
		for (int i = 0; i < tasks.length; i++) {
			vars[i] = tasks[i].duration();
		}
		return vars;
	}

	public static IntDomainVar[] getEndVars(TaskVar... tasks) {
		final IntDomainVar[] vars = new IntDomainVar[tasks.length];
		for (int i = 0; i < tasks.length; i++) {
			vars[i] = tasks[i].end();
		}
		return vars;
	}
	
	public static IntDomainVar[] getStartVars(List<TaskVar> tasks) {
		final IntDomainVar[] vars = new IntDomainVar[tasks.size()];
		ListIterator<TaskVar> iter = tasks.listIterator();
		while(iter.hasNext()) {
			vars[iter.nextIndex()] = iter.next().start(); 
		}
		return vars;
	}
	

	public static IntDomainVar[] getDurationVars(List<TaskVar> tasks) {
		final IntDomainVar[] vars = new IntDomainVar[tasks.size()];
		ListIterator<TaskVar> iter = tasks.listIterator();
		while(iter.hasNext()) {
			vars[iter.nextIndex()] = iter.next().duration(); 
		}
		return vars;
	}
	
	public static IntDomainVar[] getEndVars(List<TaskVar> tasks) {
		final IntDomainVar[] vars = new IntDomainVar[tasks.size()];
		ListIterator<TaskVar> iter = tasks.listIterator();
		while(iter.hasNext()) {
			vars[iter.nextIndex()] = iter.next().end(); 
		}
		return vars;
	}
	
	
	
}
