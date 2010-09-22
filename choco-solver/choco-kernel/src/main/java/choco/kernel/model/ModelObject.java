package choco.kernel.model;

import choco.Options;
import choco.kernel.common.HashCoding;
import choco.kernel.common.IIndex;
import choco.kernel.common.IndexFactory;
import choco.kernel.common.util.iterators.DisposableIterator;
import choco.kernel.common.util.tools.CollectionUtils;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.model.variables.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ModelObject extends VariableArray implements IOptions, IIndex {

	private static final long serialVersionUID = 1700344707758777465L;
	private final long indice;
	private final List<String> options;
	
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

	private static List<String> makeOptions(boolean enableOptions) {
		return enableOptions ? new ArrayList<String>(8) : CollectionUtils.<String>emptyList();
	}
	
	@Override
	public final int hashCode() {
		return HashCoding.hashCodeMe(indice);
	}
	
	@Override
	public void addOption(String option) {
        int h = Options.getCategorie(option);
        int  i = this.options.size();
        while(i - h <= 0){
            this.options.add(i++, Options.NO_OPTION);
        }
		this.options.set(h, option);
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
	public final void addOptions(List<String> options) {
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
	public final List<String> getOptions() {
		return options;
	}
    	
	@Override
	public final boolean containsOption(String option) {
        return options.lastIndexOf(option) >= 0;
	}

	@Override
	public final long getIndex() {
		return indice;
	}

}
