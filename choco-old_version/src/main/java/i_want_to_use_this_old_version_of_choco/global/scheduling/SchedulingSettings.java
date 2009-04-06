package i_want_to_use_this_old_version_of_choco.global.scheduling;

/**
 * User: hcambaza
 * A class to deal with all the scheduling settings for
 * cumulative and disjTree global constraint
 */
public class SchedulingSettings {

	/**
	 * Should the cumulative apply task intervals ?
	 * also called Efeasability by Van Hentenrick - Mercier
	 * or overload checking by Vilim
	 */
	public static boolean cumulative_taskInterval = true;

	/**
	 * Should the cumulative apply edge finding ?
	 */
	public static boolean cumulative_edgeFinding = false;

	/**
	 * Three variants of the edge finding for the cumulative.
	 * take a value among {0,1}.
	 * The best one should be 0 (Vilim theta lambda tree +
	 * lazy computation of the inner maximization of the edge finding rule of
	 * Van hentenrick and Mercier).
	 */
	public static int cumulative_edgefindingAlgo = 1;

	/**
	 * Vilim theta lambda tree +
	 * lazy computation of the inner maximization of the edge finding rule of
	 * Van hentenrick and Mercier
	 */
	public static final int VilimCEFAlgo = 0;

	/**
	 * Simple n^2 \times k algorithm (lazy for R) (CalcEF in the paper of Van Hentenrick) 
	 */
	public static final int VHMCEFAlgo_n2k = 1;


	public static void setCumulativeEdgeFindingAlgo(int algonum) {
		if (algonum >= 0 && algonum <= 1) {
			cumulative_edgefindingAlgo = algonum;
		} else throw new Error("Edge finding algo" + algonum + "is unknown ");
	}

	/**
	 * Ask the cumulative to enforce tasks intervals
	 */
	public static void setCumulativeTaskInterval() {
		cumulative_taskInterval = true;
	}

	/**
	 * Edge finding implies that you also need task intervals
	 */
	public static void setCumulativeEdgeFinding() {
		cumulative_taskInterval = true;
		cumulative_edgeFinding = true;
	}

	/**
	 * Ask the cumulative not to enforce tasks intervals
	 */
	public static void noCumulativeTaskInterval() {
		cumulative_taskInterval = false;
	}

	/**
	 * Ask the cumulative not to enforce edge finding
	 */
	public static void noCumulativeEdgeFinding() {
		cumulative_edgeFinding = false;
	}
}
