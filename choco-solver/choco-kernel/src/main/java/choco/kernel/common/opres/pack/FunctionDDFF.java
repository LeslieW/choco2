package choco.kernel.common.opres.pack;

public interface FunctionDDFF {
	
	int getCapacity();
	
	int getCurrentCapacity();
	
	void setParameter(int k);
	
	int getParameter();
	
	int findParameter(int size);
	
	int apply(int size);
	
}