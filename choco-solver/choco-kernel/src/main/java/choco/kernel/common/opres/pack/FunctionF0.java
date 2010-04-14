package choco.kernel.common.opres.pack;

public class FunctionF0 extends AbstractFunctionDDFF {

	private int ceil;

	public FunctionF0(int capacity) {
		super(capacity);
	}

	@Override
	public void setParameter(int k) {
		super.setParameter(k);
		ceil = capacity - k;
	}

	@Override
	public int apply(int size) {
		if(size> ceil) return capacity;
		else if(size>= k) return size;
		else return 0;
	}

	@Override
	public int findParameter(int size) {
		if( size > midCapacity) {
			return  size == midCapacity + 1 ? midCapacity : capacity - size +1;
		} else return size;
	}

	@Override
	public int getCurrentCapacity() {
		return capacity;
	}

}
