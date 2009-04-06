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

package choco.kernel.common.util;

import choco.kernel.solver.variables.Var;

/**
 * Implements utilities for arithmetic (TODO: should disappear once I know Java better)
 */
public class Arithm {  

  public static int divFloor(int a, int b) {
    if (b < 0) return divFloor(-a, -b);
    if (b == 0) return Integer.MAX_VALUE;
    if (a >= 0)
      return (a / b);
    else // if (a < 0)
      return (a - b + 1) / b;
  }

  public static int divCeil(int a, int b) {
    if (b < 0) return divCeil(-a, -b);
    if (b == 0) return Integer.MIN_VALUE;
    if (a >= 0)
      return ((a + b - 1) / b);
    else // if (a < 0)
      return a / b;
  }


  public static int min(int a, int b) {
    if (a <= b) {
      return a;
    } else {
      return b;
    }
  }

  public static int max(int a, int b) {
    if (a >= b) {
      return a;
    } else {
      return b;
    }
  }

  public static String pretty(int[] lval) {
    StringBuffer sb = new StringBuffer();
    sb.append("{");
    for (int i = 0; i < lval.length - 1; i++) {
      sb.append(lval[i]);
      sb.append(",");
    }
    sb.append(lval[lval.length - 1]);
    sb.append("}");
    return sb.toString();
  }

  public static String pretty(int[][] lvals) {
    StringBuffer sb = new StringBuffer();
    sb.append("{");
    for (int i = 0; i < lvals.length; i++) {
      if (i>0) sb.append(", ");
      int[] lval = lvals[i];
      sb.append("{");
      for (int j = 0; j < lval.length; j++) {
        if (j > 0) sb.append(",");
        int val = lval[j];
        sb.append(val);
      }
      sb.append("}");
    }
    sb.append("}");
    return sb.toString();
  }

  public static String pretty(Var[] lvar, int firstIndex, int lastIndex) {
    StringBuffer sb = new StringBuffer();
    sb.append("{");
    for (int i = firstIndex; i <= lastIndex - 1; i++) {
      sb.append(lvar[i].toString());
      sb.append(",");
    }
    sb.append(lvar[lastIndex].toString());
    sb.append("}");
    return sb.toString();
  }

  public static String pretty(int c) {
    StringBuffer sb = new StringBuffer();
    if (c > 0) {
      sb.append(" + ");
      sb.append(c);
    } else if (c < 0) {
      sb.append(" - ");
      sb.append(-(c));
    }
    return sb.toString();
  }

}
