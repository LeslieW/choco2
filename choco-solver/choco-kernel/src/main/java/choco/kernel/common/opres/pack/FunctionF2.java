package choco.kernel.common.opres.pack;

public class FunctionF2 extends AbstractFunctionDDFF {

	private int currentCapacity;

	private int currentMidCapacity;

	public FunctionF2(int capacity) {
		super(capacity);
	}

	@Override
	public void setParameter(int k) {
		super.setParameter(k);
		currentMidCapacity = (capacity / k);
		currentCapacity = 2 * currentMidCapacity;
	}

	@Override
	public int apply(int size) {
		if( size > midCapacity) {
			return currentCapacity - ( ( (capacity - size) / k)  << 1);
		}else if( (size << 1) == capacity) return currentMidCapacity;
		else return (size/k) << 1;
	}

	@Override
	public int findParameter(int size) {
		if( size > midCapacity) return  size == midCapacity + 1 ? midCapacity : capacity - size +1;
		else return size;
	}

	@Override
	public int getCurrentCapacity() {
		return currentCapacity;
	}

}
