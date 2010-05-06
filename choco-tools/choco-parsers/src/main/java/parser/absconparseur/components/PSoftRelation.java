package parser.absconparseur.components;

import parser.absconparseur.InstanceTokens;
import parser.absconparseur.Toolkit;

import java.util.Arrays;

public class PSoftRelation extends PRelation {

	private final int[] weights;

	/**
	 * defaultCost = Integer.MAX_VALUE if defaultCost is infinity
	 */
	private final int defaultCost;

	/**
	 * The max of all weights values and defaultCost.
	 */
	private final int maximalCost;

	public int[] getWeights() {
		return weights;
	}

	public int getDefaultCost() {
		return defaultCost;
	}

	public int getMaximalCost() {
		return maximalCost;
	}

	public PSoftRelation(String name, int arity, int nbTuples, String semantics, int[][] tuples, int[] weights, int defaultCost) {
		super(name, arity, nbTuples, semantics, tuples);
		this.weights = weights;
		this.defaultCost = defaultCost;
		int _maximalCost = defaultCost;
		for (int w : weights){
			if (w > _maximalCost){
				_maximalCost = w;
            }
        }
        maximalCost = _maximalCost;
	}

	public int computeCostOf(int[] tuple) {
		int position = Arrays.binarySearch(tuples, tuple, Toolkit.lexicographicComparator);
		return position >= 0 ? weights[position] : defaultCost;
	}

	public String toString() {
		int displayLimit = 5;
		StringBuilder s = new StringBuilder(256);
        s.append("  relation ").append(name).append(" with arity=").append(arity).append(", semantics=")
                .append(semantics).append(", nbTuples=").append(nbTuples).append(", defaultCost=")
                .append(defaultCost).append(" : ");
		for (int i = 0; i < Math.min(nbTuples, displayLimit); i++) {
			s.append('(');
			for (int j = 0; j < arity; j++)
				s.append(tuples[i][j]).append(j < arity - 1 ? "," : "");
			s.append(") ");
			if (weights != null)
                s.append(" with cost=").append(weights[i]).append(", ");
		}
		return s + (nbTuples > displayLimit ? "..." : "");
	}

	public boolean isSimilarTo(int arity, int nbTuples, String semantics, int[][] tuples, int[] weights, int defaultCost) {
		if (!super.isSimilarTo(arity, nbTuples, semantics, tuples))
			return false;
		if (this.defaultCost != defaultCost)
			return false;
		for (int i = 0; i < weights.length; i++)
			if (this.weights[i] != weights[i])
				return false;
		return true;
	}

	public String getStringListOfTuples() {
        StringBuilder sb = new StringBuilder(128);
		int currentWeigth = -1;
		for (int i = 0; i < tuples.length; i++) {
			if (i != 0)
				sb.append('|');
			if (weights[i] != currentWeigth) {
				currentWeigth = weights[i];
                sb.append(currentWeigth).append(InstanceTokens.COST_SEPARATOR);
			}
			for (int j = 0; j < tuples[i].length; j++) {
				sb.append(tuples[i][j]);
				if (j != tuples[i].length - 1)
					sb.append(' ');
			}
		}
		return sb.toString();
	}

}
