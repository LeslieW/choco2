package choco.cp.solver.constraints.global.scheduling.cumulative;


import choco.kernel.solver.ContradictionException;

public interface ICumulRules {

	/**
	 * fast task intervals in n*log(n)
	 */
	void taskIntervals() throws ContradictionException;

	/**
	 * a basic n^2 tasks interval
	 */
	void slowTaskIntervals() throws ContradictionException;

	/**
	 * reset all the flags for dynamic computation of R
	 */
	void reinitConsumption();

	/**
	 * Initialize some data structure for the edge finding.
	 * If the height are constant, this is done only once
	 * at the beginning, otherwise it has to be recomputed at each call.
	 * Shall we maintain it incrementally ?
	 */
	void initializeEdgeFindingData();
	
	void initializeEdgeFindingStart();
	
	void initializeEdgeFindingEnd();

	/**
	 * Edge finding algorithm for starting dates in O(n^2 \times k) where
	 * k is the number of distinct heights.
	 */
	boolean calcEF_start() throws ContradictionException;

	/**
	 * Edge finding algorithm for starting dates in O(n^2 \times k) where
	 * k is the number of distinct heights. Vilim version based on the theta-
	 * lambda tree.
	 */
	boolean vilimStartEF() throws ContradictionException;

	/**
	 * Edge finding algorithm for ending dates in O(n^2 \times k) where
	 * k is the number of distinct heights.
	 */
	boolean calcEF_end() throws ContradictionException;

	/**
	 * Edge finding algorithm for ending dates in O(n^2 \times k) where
	 * k is the number of distinct heights. Vilim version based on the theta-
	 * lambda tree.
	 */
	boolean vilimEndEF() throws ContradictionException;

}