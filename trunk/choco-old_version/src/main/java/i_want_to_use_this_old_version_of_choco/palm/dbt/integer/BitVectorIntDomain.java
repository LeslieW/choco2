//     ___  ___         PaLM library for Choco system
//    /__ \/ __\        (c) 2001 - 2004 -- Narendra Jussien
//   ____\ |/_____
//  /_____\/_____ \     PalmExplanation based constraint programming tool
// |/   (_)(_)   \|
//       \ /            Version 0.1
//       \ /            January 2004
//       \ /
// ______\_/_______     Contibutors: Fran�ois Laburthe, Hadrien Cambazard, Guillaume Rochart...

package i_want_to_use_this_old_version_of_choco.palm.dbt.integer;

import i_want_to_use_this_old_version_of_choco.Var;

import java.util.BitSet;

public class BitVectorIntDomain {
  private int offset;
  private int bucketSize;
  private BitSet booleanVector;

  private static final int FIRST_SUCC_PRES = 0;
  private static final int FIRST_PREC_PRES = 1;
  private static final int FIRST_SUCC_ABS = 2;
  private static final int FIRST_PREC_ABS = 3;
  private int[] firsts = new int[4];
  private int[] precVector, succVector;

  // ****************
  // API
  // ****************

  public BitVectorIntDomain(Var var, int dinf, int dsup) {
    //assert dinf <= dsup;
    //assert dsup <= dinf + MAXSIZE;

    // Taille du domaine
    int n = dsup - dinf + 1;

    this.offset = dinf;

    this.bucketSize = n;

    this.firsts[FIRST_SUCC_PRES] = 0;
    this.firsts[FIRST_PREC_PRES] = n - 1;
    this.firsts[FIRST_SUCC_ABS] = -1;
    this.firsts[FIRST_PREC_ABS] = -1;

    this.succVector = new int[n];
    for (int i = 0; i < n - 1; i++) this.succVector[i] = i + 1;
    this.succVector[n - 1] = -1;
    this.precVector = new int[n];
    for (int i = 0; i < n; i++) this.precVector[i] = i - 1;

    // Initialisation ? vrai entre 0 (inclus) et n (exclus)
    this.booleanVector = new BitSet(n);
    this.booleanVector.set(0, n);

    // tableaux de null par d?faut
// dans PalmIntVar !!
    //this.explanationOnVal = new RemovalExplanation[n];
    //this.instantiationConstraints = new I_Constraint[n];
    //this.negInstantiationConstraints = new I_Constraint[n];
  }

  public int[] domainList() {
    int card = this.booleanVector.cardinality();
    int[] seq = new int[card];
    int idx = 0;
    int z = this.firsts[FIRST_SUCC_PRES]; //this.firstSuccPres;
    while (z != -1) {
      seq[idx] = z + this.offset;
      z = this.succVector[z];
      idx++;
    }
    //assert idx == card;
    return seq;
  }

  public int[] domainSet() {
    int card = this.booleanVector.cardinality();
    int[] seq = new int[card];
    int idx = 0;
    for (int i = this.booleanVector.nextSetBit(0); i >= 0; i = this.booleanVector.nextSetBit(i + 1)) {
      seq[idx] = i + this.offset;
      idx++;
    }
    //assert idx == card;
    return seq;
  }

  public int[] removedList() {
    int card = this.bucketSize - this.booleanVector.cardinality();
    int[] seq = new int[card];
    int idx = 0;
    int z = this.firsts[FIRST_SUCC_ABS]; //this.firstSuccAbs;
    while (z != -1) {
      seq[idx] = z + this.offset;
      z = this.succVector[z];
      idx++;
    }
    //assert idx == card;
    return seq;
  }

  public String toString() {
    StringBuffer str = new StringBuffer();
    int[] dom = this.domainSet();
    int leng = dom.length;

    // void set
    if (leng == 0)
      str.append("{}");

    // less than 4
    else if (leng <= 4) {
      str.append("{");
      for (int i = 0; i < leng - 1; i++) str.append(dom[i] + ",");
      str.append(dom[leng - 1] + "}");
    }

    // more than 4
    else
      str.append("{" + dom[0] + "," + dom[1] + "..."
          + dom[leng - 2] + "," + dom[leng - 1] + "}");
    return str.toString();
  }

  public int getBucketSize() {
    // domain size
    return this.bucketSize;
  }

  public int getSize() {
    // number of present elements
    return this.booleanVector.cardinality();
  }

  public boolean containsValInDomain(int x) {
    // checking the status of a value
    if ((x - this.offset >= 0) && (x - this.offset < this.bucketSize))
      return this.booleanVector.get(x - this.offset);
    else
      return false;
  }

  public int firstElement() {
    if (this.firsts[FIRST_SUCC_PRES] == -1)
      return Integer.MAX_VALUE;
    else
      return (this.firsts[FIRST_SUCC_PRES] + this.offset);
  }

  public int getInf() {
    return this.booleanVector.nextSetBit(0) + this.offset;
  }

  public int getSup() {
    int i = this.bucketSize - 1;
    while (!this.booleanVector.get(i)) {
      i--;
    }
    return i + this.offset;
  }

  private void updateChain(int firstSuccOld, int firstPrecOld, int firstSuccNew, int firstPrecNew, int value) {
    // updating chains
    int succX = this.succVector[value];
    int precX = this.precVector[value];
    // updating present chain
    if (precX == -1) { // z n'a pas de pr?c?dent
      this.firsts[firstSuccOld] = succX;	// le premier devient le suivant de z
      if (succX == -1) this.firsts[firstPrecOld] = -1; else this.precVector[succX] = -1;
    } else { // z a un pr?c?dent
      this.succVector[precX] = succX;
      if (succX == -1) this.firsts[firstPrecOld] = -1; else this.precVector[succX] = precX;
    }

    //	updating absent chain
    if (this.firsts[firstSuccNew] == -1)
      this.firsts[firstSuccNew] = value;
    else {
      this.succVector[this.firsts[firstPrecNew]] = value;
    }
    this.succVector[value] = -1;
    this.precVector[value] = this.firsts[firstPrecNew];
    this.firsts[firstPrecNew] = value;
  }

  public void addDomainVal(int z) {
    if ((z - this.offset >= 0) && (z - this.offset < this.bucketSize) && !this.booleanVector.get(z - this.offset)) {
      // adding the value
      this.booleanVector.set(z - this.offset);

      this.updateChain(FIRST_SUCC_ABS, FIRST_PREC_ABS,
          FIRST_SUCC_PRES, FIRST_PREC_PRES, z - this.offset);
    }
  }

  public Boolean removeDomainVal(int z) {
    if ((z - this.offset >= 0) && (z - this.offset < this.bucketSize)) {
      // removing the value
      this.booleanVector.clear(z - this.offset);

      this.updateChain(FIRST_SUCC_PRES, FIRST_PREC_PRES,
          FIRST_SUCC_ABS, FIRST_PREC_ABS, z - this.offset);

      return Boolean.TRUE;
    } else
      return Boolean.FALSE;
  }

  /**
   * @return the offset (value of the first element in the domain)
   */
  public int getOffset() {
    return offset;
  }
}