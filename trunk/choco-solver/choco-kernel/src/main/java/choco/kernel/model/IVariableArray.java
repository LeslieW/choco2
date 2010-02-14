package choco.kernel.model;

import java.util.Iterator;

import choco.IPretty;
import choco.kernel.model.variables.Variable;

public interface IVariableArray extends IPretty {

	Iterator<Variable> getVariableIterator();

	Variable getVariable(int i);

	Variable[] getVariables();

	int getNbVars();
	
	/**
	 * Extract a non-redundant variables.
	 */
	public Variable[] extractVariables();

}



