// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in dev.i_want_to_use_this_old_version_of_choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************
package i_want_to_use_this_old_version_of_choco.set.search;

import i_want_to_use_this_old_version_of_choco.set.SetVar;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

import java.util.Random;
import java.util.Vector;

public class RandomSetValSelector implements SetValSelector {
  protected Random random;

  /**
   * Default constructor for a random value selector for banching.
   */
  public RandomSetValSelector() {
    random = new Random();
  }

  /**
   * Constructs a random value selector for branching with a specified seed.
   * @param seed to replay a random test
   */
  public RandomSetValSelector(long seed) {
    random = new Random(seed);
  }

  public int getBestVal(SetVar v) {
      int value = Integer.MIN_VALUE;
      Vector<Integer> vector;
      vector = new Vector();
      IntIterator it = v.getDomain().getEnveloppeIterator();
      while (it.hasNext()){
          int val = it.next();
          if(!v.isInDomainKernel(val)) {
              vector.add(val);
          }
      }
      if(vector.size()>0)value = Integer.valueOf(vector.get(random.nextInt(vector.size())));
      return value;
  }
}