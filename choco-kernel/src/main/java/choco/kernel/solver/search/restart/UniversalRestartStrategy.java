package choco.kernel.solver.search.restart;


public interface UniversalRestartStrategy {

	String getName();
	
	int getScaleFactor();

	void setScaleFactor(int scaleFactor);

	double getGeometricalFactor();
	
	void setGeometricalFactor(double geometricalFactor);

	int getNextCutoff(int nbRestarts);
}
