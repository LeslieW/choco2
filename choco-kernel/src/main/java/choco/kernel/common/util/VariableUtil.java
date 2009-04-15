package choco.kernel.common.util;

import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;
import choco.kernel.solver.variables.set.SetVar;

public final class VariableUtil {



	private VariableUtil() {
		super();
	}

	public static Var[] getVar(Solver solver, Variable[] variables, int begin, int end) {
		if(end > begin && begin >= 0 && end <= variables.length ) {
			Var[] vars = new Var[end-begin];
			for (int i = begin; i < end; i++) {
				vars[i - begin] = solver.getVar(variables[i]);
			}
			return vars;
		}
		return null;
	}

		
	public static IntDomainVar[] getVar(Solver solver, IntegerVariable[] variables, int begin, int end) {
		if(end > begin && begin >= 0 && end <= variables.length ) {
			IntDomainVar[] vars = new IntDomainVar[end-begin];
			for (int i = begin; i < end; i++) {
				vars[i - begin] = solver.getVar(variables[i]);
			}
			return vars;
		}
		return null;
	}
	
	public static SetVar[] getVar(Solver solver, SetVariable[] variables, int begin, int end) {
		if(end > begin && begin >= 0 && end <= variables.length ) {
			SetVar[] vars = new SetVar[end-begin];
			for (int i = begin; i < end; i++) {
				vars[i - begin] = solver.getVar(variables[i]);
			}
			return vars;
		}
		return null;
	}
	
	public static TaskVar[] getVar(Solver solver, TaskVariable[] variables, int begin, int end) {
		if(end > begin && begin >= 0 && end <= variables.length ) {
			TaskVar[] vars = new TaskVar[end-begin];
			for (int i = begin; i < end; i++) {
				vars[i - begin] = solver.getVar(variables[i]);
			}
			return vars;
		}
		return null;
	}
	
	public static IntDomainVar[] getIntVar(Solver solver, Variable[] variables, int begin, int end) {
		if(end > begin && begin >= 0 && end <= variables.length ) {
			IntDomainVar[] vars = new IntDomainVar[end-begin];
			for (int i = begin; i < end; i++) {
				vars[i - begin] = solver.getVar( (IntegerVariable) variables[i]);
			}
			return vars;
		}
		return null;
	}
	
	public static SetVar[] getSetVar(Solver solver, Variable[] variables, int begin, int end) {
		if(end > begin && begin >= 0 && end <= variables.length ) {
			SetVar[] vars = new SetVar[end-begin];
			for (int i = begin; i < end; i++) {
				vars[i - begin] = solver.getVar( (SetVariable) variables[i]);
			}
			return vars;
		}
		return null;
	}
	
	public static TaskVar[] getTaskVar(Solver solver, Variable[] variables, int begin, int end) {
		if(end > begin && begin >= 0 && end <= variables.length ) {
			TaskVar[] vars = new TaskVar[end-begin];
			for (int i = begin; i < end; i++) {
				vars[i - begin] = solver.getVar( (TaskVariable) variables[i]);
			}
			return vars;
		}
		return null;
	}


}
