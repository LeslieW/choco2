package choco.kernel.common.util.tools;

import static choco.Choco.makeBooleanVar;

import java.util.List;
import java.util.ListIterator;

import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.VariableType;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import choco.kernel.model.variables.set.SetVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.Var;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.variables.scheduling.TaskVar;
import choco.kernel.solver.variables.set.SetVar;

public final class VariableUtils {



	private VariableUtils() {
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
	
	
    //****************************************************************//
    //********* TYPE *******************************************//
    //****************************************************************//



    public static boolean checkInteger(VariableType v){
        return v == VariableType.INTEGER || v == VariableType.CONSTANT_INTEGER;
    }

    public static boolean checkSet(VariableType v){
        return v == VariableType.SET || v == VariableType.CONSTANT_SET;
    }

    public static boolean checkReal(VariableType v){
        return v == VariableType.REAL || v == VariableType.CONSTANT_DOUBLE || v == VariableType.REAL_EXPRESSION;
    }

    /**
     * Check the type of each variable and compute a int value
     * @param v1 type of the first variable
     * @param v2 type of he second variable
     * @return a value corresponding to the whole type
     *
     * if the type is integer return 1 * position
     * if the type is set return 2 * position
     * if the type is real return 3 * position
     *
     * where position is 10 for v1 and 1 for v2
     */
    public static int checkType(VariableType v1, VariableType v2){
        int t1 = 0;
        int t2 = 0;
        if(checkInteger(v1)){
            t1 = 1;
        }else if(checkSet(v1)){
            t1 = 2;
        }else if(checkReal(v1)){
            t1 = 3;
        }
        if(checkInteger(v2)){
            t2 = 1;
        }else if(checkSet(v2)){
            t2 = 2;
        }else if(checkReal(v2)){
            t2 = 3;
        }
        return 10*t1+t2;

    }

    /**
	 * A quickSort algorithm for sorting a table of variable according
     * to a table of integers.
     * @param a : the integer table to be sorted
     * @param vs : the intvar table to be sorted according a
* @param left
* @param right
     */
    public static void quicksort(int[] a, IntDomainVar[] vs, int left, int right) {
        if (right <= left) {
            return;
        }
        int i = partition(a, vs, left, right);
        quicksort(a, vs, left, i - 1);
        quicksort(a, vs, i + 1, right);
    }

    public static int partition(int[] a, IntDomainVar[] vs, int left, int right) {
        int i = left - 1;
        int j = right;
        while (true) {
            while (a[++i] < a[right]) {
// a[right] acts as sentinel
            }
            while (a[right] < a[--j]) {
                if (j == left) {
                    break;           // don't go out-of-bounds
                }
            }
            if (i >= j) {
                break;                  // check if pointers cross
            }
            exch(a, vs, i, j);                    // swap two elements into place
        }
        exch(a, vs, i, right);                      // swap with partition element
        return i;
    }

    public static void exch(int[] a, IntDomainVar[] vs, int i, int j) {
        int swap = a[i];
        IntDomainVar vswap = vs[i];
        a[i] = a[j];
        vs[i] = vs[j];
        a[j] = swap;
        vs[j] = vswap;
    }

    /**
	 * Reverse a table of integer and variables (use for api on linear combination)
* @param tab array of integer to reverse
* @param vs array of variables to reverse
*/
    public static void reverse(int[] tab, IntDomainVar[] vs) {
        int[] revtab = new int[tab.length];
        IntDomainVar[] revvs = new IntDomainVar[vs.length];
        for (int i = 0; i < revtab.length; i++) {
            revtab[i] = tab[revtab.length - 1 - i];
            revvs[i] = vs[revtab.length - 1 - i];
        }
        for (int i = 0; i < revtab.length; i++) {
            tab[i] = revtab[i];
            vs[i] = revvs[i];
        }
    }


  	//*****************************************************************//
	//*******************  TaskVariable  ********************************//
	//***************************************************************//
    
    
    public static IntegerVariable createDirectionVar(TaskVariable t1, TaskVariable t2, String... boolOptions) {
    	return makeBooleanVar("dir-"+t1.getName()+"-"+t2.getName(), boolOptions);
    }
    
    public static IntDomainVar createDirectionVar(TaskVar t1, TaskVar t2) {
    	String name = "dir-"+t1.getName()+"-"+t2.getName();
    	return t1.getSolver() == null ? 
    			( t2.getSolver() == null ? null : t2.getSolver().createBooleanVar(name) )
    			: t1.getSolver().createBooleanVar(name);
    }
    
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
   
    public static TaskVar[] getTaskVars(Solver solver) {
    	final int n = solver.getNbTaskVars();
    	final TaskVar[] vars = new TaskVar[n];
    	for (int i = 0; i < n; i++) {
			vars[i] = solver.getTaskVar(i);
		}
        return vars;
    }
    
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
