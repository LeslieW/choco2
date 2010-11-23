package choco.shaker.tools.factory.beta;

import java.util.Random;

import choco.Options;
import choco.kernel.model.variables.Variable;

public interface IVariableFactory<E extends Variable> {

	void setScope(String... options);

	void addScope(String... options);

	void remScope(String... options);

	void cancelScope();

	void setValueOffset(int valOffset);

	void cancelValueOffset();

	void setMaxDomSize(int maxDomSize); 

	void setMaxCreated(int nbVars);

	void definePool(E... variables);

	void clearPool();

	E[] make(int nb, Random r);

	E make(Random r);

	E[] make(String option, int nb, Random r);

	E make(String option, Random r);

}