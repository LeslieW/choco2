// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.util;

import i_want_to_use_this_old_version_of_choco.Var;

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
