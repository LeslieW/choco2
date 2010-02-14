package choco.kernel.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import choco.kernel.common.HashCoding;
import choco.kernel.common.IIndex;
import choco.kernel.common.IndexFactory;
import choco.kernel.common.util.tools.CollectionUtils;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.model.variables.Variable;

public class ModelObject extends VariableArray implements IOptions, IIndex {

	private final long indice;
	private final Set<String> options;
	
	public ModelObject(Variable[] variables, boolean enableOptions) {
		super(variables);
		options = makeOptions(enableOptions);
		indice = IndexFactory.getId();
	}
	
	public ModelObject(boolean enableOptions) {
		super();
		options = makeOptions(enableOptions);
		indice = IndexFactory.getId();
	}

	private static Set<String> makeOptions(boolean enableOptions) {
		return enableOptions ? new HashSet<String>() : CollectionUtils.<String>emptySet();
	}
	
	@Override
	public final int hashCode() {
		return HashCoding.hashCodeMe(this);
	}
	
	@Override
	public void addOption(String option) {
		this.options.add(option);
	}
		
	@Override
	public final void addOptions(String options) {
		Iterator<String> iter = StringUtils.getOptionIterator(options);
		while(iter.hasNext()) addOption(iter.next());
		
	}

	@Override
	public final void addOptions(String[] options) {
		for (String option : options) {
			addOption(option);
		}
	}

	@Override
	public final void addOptions(Set<String> options) {
		for (String option : options) {
			addOption(option);
		}
	}

	@Override
	public final Set<String> getOptions() {
		return options;
	}

	@Override
	public final long getIndex() {
		return indice;
	}

}
