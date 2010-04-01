package choco.kernel.model;

import choco.IPretty;
import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.model.variables.Variable;

public interface IVariableArray extends IPretty {

	DisposableIterator<Variable> getVariableIterator();

	Variable getVariable(int i);

	Variable[] getVariables();

	int getNbVars();
	
	/**
	 * Extract a non-redundant variables.
	 */
	public Variable[] extractVariables();

    /**
     * Substitute {@code outVar} by {@code inVar} in every constraint involving {@code outVar}.
     * @param outVar variable to replace
     * @param inVar substitute variable
     */
    public void replaceBy(final Variable outVar, final Variable inVar);

}



