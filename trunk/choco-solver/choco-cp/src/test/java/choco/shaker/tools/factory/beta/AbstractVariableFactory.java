package choco.shaker.tools.factory.beta;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import choco.kernel.model.variables.Variable;


abstract class AbstractVariablePool <E extends Variable> implements IVariableFactory<E> {

	private List<E> varPool;
	
	private final String[] defaultScope;

	protected final List<String> userScope;

	public AbstractVariablePool(String[] defaultScope) {
		super();
		this.defaultScope = defaultScope;
		userScope = new ArrayList<String>();
		varPool = Collections.<E>emptyList();
	}

	public final void definePool(E... variables){
		this.varPool = new ArrayList<E>(Arrays.asList(variables));
	}

	public final void clearPool() {
		varPool = new ArrayList<E>();
	}

	@Override
	public void cancelScope() {
		userScope.clear();
	}

	@Override
	public void addScope(String... options) {
		userScope.addAll(Arrays.asList(options));
	}

	@Override
	public void setScope(String... options) {
		userScope.clear();
		userScope.addAll(Arrays.asList(options));
	}
	
	@Override
	public void remScope(String... options) {
		userScope.removeAll(Arrays.asList(options));
	}

	@Override
	public E make(String option, Random r) {
		return varPool.isEmpty() ? null : varPool.get(r.nextInt(varPool.size()));
	}
	
	protected abstract Class<E> getComponentClass();

	
	@SuppressWarnings("unchecked")
	public final E[] make(int nb, Random r) {
		E[] variables = (E[]) Array.newInstance(getComponentClass(), nb);
		for (int i = 0; i < variables.length; i++) {
			variables[i] = make(r);
		}
		return variables;
	}

	protected final String any(Random r) {
		return  userScope.isEmpty() ? 
				defaultScope[r.nextInt(defaultScope.length)]:
					userScope.get(r.nextInt(userScope.size()));
	}
	
	@Override
	public final E make(Random r) {
		return make(any(r), r);
	}
	
	@SuppressWarnings("unchecked")
	public final E[] make(String option, int nb, Random r) {
		E[] variables = (E[]) Array.newInstance(getComponentClass().getComponentType(), nb);
		for (int i = 0; i < variables.length; i++) {
			variables[i] = make(option, r);
		}
		return variables;
	}

}

public abstract class AbstractVariableFactory<E extends Variable> extends AbstractVariablePool<E> {

	protected int maxCreated = 20;

	protected int maxDomainSize = 20;

	protected Integer valueOffset;

	protected List<E> varCreated;

	public AbstractVariableFactory(String... defaultScope) {
		super(defaultScope);
		varCreated = new ArrayList<E>(maxCreated);
	}
	
	@Override
	public final void setMaxDomSize(int maxDomSize) {
		if( maxDomSize > 0) this.maxDomainSize = maxDomSize;
	}
	
	@Override
	public final void cancelValueOffset() {
		valueOffset = null;		
	}

	@Override
	public final void setValueOffset(int valOffset) {
		valueOffset = Integer.valueOf(valOffset);
	}

	public final void setMaxCreated(int nbVars) {
		if(nbVars > 0) maxCreated = nbVars;
	}

	public abstract E create(String option, Random r);

	@Override
	public final E make(String option, Random r) {
		E var = super.make(option, r);
		if (varCreated.size() >= maxCreated) {
			var = varCreated.get(r.nextInt(varCreated.size()));
		}else {
			var = create(option, r);
			varCreated.add(var);
		}
		return var;
	}

}