package choco.kernel.model;

import choco.kernel.common.HashCoding;
import choco.kernel.common.IIndex;
import choco.kernel.common.IndexFactory;
import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.common.util.tools.CollectionUtils;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.model.variables.Variable;

import java.util.HashSet;
import java.util.Set;

public class ModelObject extends VariableArray implements IOptions, IIndex {

	private static final long serialVersionUID = 1700344707758777464L;
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
		return HashCoding.hashCodeMe(indice);
	}
	
	@Override
	public void addOption(String option) {
		this.options.add(option);
	}
		
	@Override
	public final void addOptions(String options) {
		DisposableIterator<String> iter = StringUtils.getOptionIterator(options);
		while(iter.hasNext()){
            addOption(iter.next());
        }
        iter.dispose();
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
	public final boolean containsOption(String option) {
		return options.contains(option);
	}

	@Override
	public final long getIndex() {
		return indice;
	}

}
