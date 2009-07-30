package choco.kernel.solver.search.restart;

import choco.IPretty;

/**
 *  The interface defines a universal restart policy. 
 *  @see Luby; Sinclair & Zuckerman <br>
 * Optimal Speedup of Las Vegas Algorithms <br>
 * IPL: Information Processing Letters, 1993, 47, 173-180
 */
public interface UniversalRestartStrategy extends IPretty {

	String getName();
	
	int getScaleFactor();

	void setScaleFactor(int scaleFactor);

	double getGeometricalFactor();
	
	void setGeometricalFactor(double geometricalFactor);

	int getNextCutoff(int nbRestarts);
}
