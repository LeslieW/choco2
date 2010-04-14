package choco.kernel.common.opres.pack;

import gnu.trove.TIntArrayList;

import java.util.Arrays;

public class FunctionF1 extends AbstractFunctionDDFF {

	/**
	 * by ascending sizes.
	 */
	public TIntArrayList items;
	
	private final int[] knapsacks;
	
	public FunctionF1(int capacity) {
		super(capacity);
		knapsacks = new int[capacity + 1];
	}

	@Override
	public int apply(int size) {
		if(size > midCapacity) return knapsacks[capacity] - knapsacks[capacity - size];
		else if( size >= k) return 1;
		else return 0;
	}
	
	public final TIntArrayList getItems() {
		return items;
	}

	public final void setItems(TIntArrayList items) {
		this.items = items;
	}

	@Override
	public void setParameter(int k) {
		super.setParameter(k);
		final int n = items.size();
		int idx = 0;
		while( idx < n && items.getQuick(idx) < k) {idx++;}
		int nbI = 0;
		int offset = 0;
		int size = 0;
		while(idx < n) {
			//final int sizeI = items.getQuick(idx);
			size += items.getQuick(idx);
			if(size > capacity) break;
			else {
				Arrays.fill(knapsacks, offset, size, nbI);
				offset = size;
				nbI++;
			}
			idx++;
		}
		Arrays.fill(knapsacks, offset, capacity + 1, nbI);
	}

	@Override
	public int findParameter(int size) {
		if( size > midCapacity)	return size == capacity ? midCapacity : size - midCapacity;
		else return size;
	}

	@Override
	public int getCurrentCapacity() {
		return knapsacks[capacity];
	}

	public static void main(String[] args) {
		FunctionF1 f1 = new FunctionF1(10);
		f1.items = new TIntArrayList(new int[]{1,2,3,4,5});
		f1.setParameter(5);
		System.out.println(Arrays.toString(f1.knapsacks));
	}
		
}
