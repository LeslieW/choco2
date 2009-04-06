// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package i_want_to_use_this_old_version_of_choco.util;

/**
 * Implements utilities for handling bit sets
 */
public class BitSet {

  /**
   * tests whether the i-th bit of an search is on or off
   */
  public static boolean getBit(int n, int i) {
    return (((n >> i) & 1) == 1);
  }

  /**
   * sets the i-th bit of an search (does nothing if it is already on)
   */
  public static int setBit(int n, int i) {
    if (!getBit(n, i))
      n += (1 << i);
    return n;
  }

  /**
   * sets the i-th bit of an search (does nothing if it is already on)
   */
  public static int unsetBit(int n, int i) {
    if (getBit(n, i))
      n -= (1 << i);
    return n;
  }

  /**
   * returns the heaviest bit on (-1 if none)
   */
  public static int getHeavierBit(int n) {
    for (int i = 31; i >= 0; i--) {
      if (getBit(n, i))
        return i;
    }
    return -1;
  }

}                   
