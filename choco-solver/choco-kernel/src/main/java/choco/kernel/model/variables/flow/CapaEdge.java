package choco.kernel.model.variables.flow;

import choco.kernel.model.constraints.ManagerFactory;
import choco.kernel.model.variables.MultipleVariables;
import choco.kernel.model.variables.VariableManager;
import choco.kernel.model.variables.integer.IntegerVariable;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Mar 31, 2010
 * Time: 1:09:52 PM
 */
public class CapaEdge  extends MultipleVariables
{
public final int dest;
public final IntegerVariable capa;

protected String variableManager;

public CapaEdge(IntegerVariable capa, int dest) {
        super(false,true);
        this.dest = dest;
        this.capa = capa;
        this.setVariables(capa);

}

public String toString() {
        return ("capa "+ capa + "  -> node " + dest);
}

@Override
public void findManager(Properties properties) {
        if (variableManager == null) {
                variableManager = "choco.cp.model.managers.variables.CapaEdgeManager";
        }
       
        super.findManager(properties);
}


	@Override
	public VariableManager<?> getVariableManager() {
		return ManagerFactory.loadVariableManager(variableManager);
	}
}

