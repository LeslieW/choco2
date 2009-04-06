// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.integer.search;

import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.util.DisposableIntIterator;

import java.util.Random;

public class RandomIntValSelector implements ValSelector {
  protected Random random;

  /**
   * Default constructor for a random value selector for banching.
   */
  public RandomIntValSelector() {
    random = new Random();
  }

  /**
   * Constructs a random value selector for branching with a specified seed.
   */
  public RandomIntValSelector(long seed) {
    random = new Random(seed);
  }

  public int getBestVal(IntDomainVar x) {
    if (x.hasEnumeratedDomain()) {
        int val = (random.nextInt(x.getDomainSize()));
        DisposableIntIterator iterator = x.getDomain().getIterator();
        for (int i = 1; i < val; i++) {
            iterator.next();
        }
        int res = iterator.next();
        iterator.dispose();
        return res;
    } else {
        int val = (random.nextInt(2));
        if (val == 0) return x.getInf();
        else return x.getSup();
    }
  }
}
