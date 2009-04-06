/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package parser.absconparseur.components;


import parser.absconparseur.InstanceTokens;

import java.util.Arrays;
import java.util.BitSet;

public class PDomain {
	private String name;

	private int[] values;

    private int index;

    public String getName() {
		return name;
	}

	public int[] getValues() {
		return values;
	}

	public int getNbValues() {
		return values.length;
	}

	public BitSet getBitSetDomain() {
		if (values[0] < 0) return null;
		BitSet b = new BitSet();
		for (int i = 0; i < values.length; i++) {
			b.set(values[i]);
		}
		return b;
	}

	public int getIntersectionSize(PDomain dom2) {
		if (getMaxValue() < dom2.getMinValue() ||
			dom2.getMinValue() > getMaxValue()) {
			return 0;
		} else {
			BitSet b1 = getBitSetDomain();
			BitSet b2 = dom2.getBitSetDomain();
			if (b1 != null && b2 != null) {
				b1.and(b2);
				return b1.cardinality();
			} else return -1;

		}
	}

	//assume the values are sorted from the min to the max
	public int getMinValue() {
		return values[0];
	}

	public int getMaxValue() {
		return values[values.length - 1];
	}

	public int getMaxAbsoluteValue() {
		return Math.max(Math.abs(values[0]), Math.abs(values[values.length - 1]));
	}

	public PDomain(String
			name, int[] values) {
		this.name = name;
		this.values = values;
        this.index = Integer.parseInt(name.substring(1));
    }

	public boolean contains(int value) {
		return Arrays.binarySearch(values, value) >= 0;
	}

	public String toString() {
		int displayLimit = 5;
		String s = "  domain " + name + " with " + values.length + " values : ";
		for (int i = 0; i < Math.min(values.length, displayLimit); i++)
			s += values[i] + " ";
		return s + (values.length > displayLimit ? "..." : "");
	}

	public String getStringListOfValues() {
		int previousValue = values[0];
		boolean startedInterval = false;
		StringBuffer sb = new StringBuffer();
		for (int i = 1; i < values.length; i++) {
			int currentValue = values[i];
			if (currentValue != previousValue + 1) {
				if (startedInterval) {
					sb.append(previousValue + InstanceTokens.DISCRETE_INTERVAL_END);
					startedInterval = false;
				} else
					sb.append(previousValue);
				sb.append(InstanceTokens.VALUE_SEPARATOR);
			} else {
				if (!startedInterval) {
					sb.append(InstanceTokens.DISCRETE_INTERVAL_START + previousValue + InstanceTokens.DISCRETE_INTERVAL_SEPARATOR);
					startedInterval = true;
				}
			}
			previousValue = currentValue;
		}
		if (startedInterval)
			sb.append(previousValue + InstanceTokens.DISCRETE_INTERVAL_END);
		else
			sb.append(previousValue);
		return sb.toString();
	}

    public int hashCode() {
        return index;
    }
}
