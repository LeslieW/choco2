package choco.kernel.common.opres.pack;

public abstract class AbstractFunctionDDFF implements FunctionDDFF {

	public final int capacity;
	
	public final int midCapacity;
	
	public int k;

	public AbstractFunctionDDFF(int capacity) {
		super();
		this.capacity = capacity;
		this.midCapacity = this.capacity/2;
	}

	public final int getCapacity() {
		return capacity;
	}

	public final int getParameter() {
		return k;
	}
	
	@Override
	public void setParameter(int k) {
		assert(k> 0 && k <= midCapacity);
		this.k = k;
	}
	
	
	
	
	
}
