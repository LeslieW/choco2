package i_want_to_use_this_old_version_of_choco.integer.constraints.extension;

import i_want_to_use_this_old_version_of_choco.ContradictionException;
import i_want_to_use_this_old_version_of_choco.integer.IntDomainVar;
import i_want_to_use_this_old_version_of_choco.mem.IStateInt;
import i_want_to_use_this_old_version_of_choco.util.IntIterator;

/**
 * Created by IntelliJ IDEA.
 * User: hcambaza
 * Date: 30 janv. 2007
 * Time: 11:41:13
 * To change this template use File | Settings | File Templates.
 */
public class GAC2001PositiveLargeConstraint extends CspLargeConstraint {

       // Last valid supports Last(x_i, val) = supports( (blocks(i) + val) * size )
       protected IStateInt[] supports;

       // blocks[i] = index of var[i]'s supports
       protected int[] blocks;

       // Cardinality
       protected int size;

       // offsets(i) = Min(x_i)
       protected int[] offsets;


       public GAC2001PositiveLargeConstraint(IntDomainVar[] vs, IterLargeRelation relation) {
           super(vs, relation);

           this.size = vs.length;
           this.blocks = new int[size];
           this.offsets = new int[size];

           int nbElt = 0;

           for (int i = 0; i < size; i++) {
               offsets[i] = vs[i].getInf();
               blocks[i] = nbElt;
               nbElt += vars[i].getSup() - vars[i].getInf() + 1;
           }

           this.supports = new IStateInt[nbElt * size];


           for (int i = 0; i < size; i++) {
               int domSize = vs[i].getDomainSize();
               for (int j = 0; j < domSize; j++) {
                   for (int k = 0; k < size; k++) {
                       if (k == i) {
                           supports[(blocks[i] + j) * size + k] = this.getProblem().getEnvironment().makeInt(offsets[k] + j);
                       } else {
                           supports[(blocks[i] + j) * size + k] = this.getProblem().getEnvironment().makeInt(offsets[k]);
                       }
                   }
               }
           }


       }


       // updates the support for all values in the domain of variable
       // and remove unsupported values for variable
       public void reviseVar(int indexVar, int indexModifiedVar) throws ContradictionException {
           int[] currentSupport;
           IntIterator itvar = vars[indexVar].getDomain().getIterator();
           int val;

           while (itvar.hasNext()) {
               val = itvar.next();
               if (!isValid(getSupport(indexVar, val))) {
                   currentSupport = seekSupport(getSupport(indexVar, val), indexVar, val);

                   if (currentSupport != NO_TUPLE) {
                       setSupport(indexVar, val, currentSupport);
                   } else {
                       vars[indexVar].removeVal(val, cIndices[indexVar]);
                   }
               }
           }
       }

       // updates the support for all values in the domain of variable
       // and remove unsupported values for variable
       public void reviseVar(int indexVar) throws ContradictionException {
           int[] currentSupport;
           IntIterator itvar = vars[indexVar].getDomain().getIterator();
           int val;

           while (itvar.hasNext()) {
               val = itvar.next();
               if (!isValid(getSupport(indexVar, val))) {
                   currentSupport = seekSupport(getSupport(indexVar, val), indexVar, val);

                   if (currentSupport != NO_TUPLE) {
                       setSupport(indexVar, val, currentSupport);
                   } else {
                       vars[indexVar].removeVal(val, cIndices[indexVar]);
                   }
               }
           }
       }

       // Store Last(x_i, val) = support
       public void setSupport(int indexVar, int value, int[] support) {
           for (int i = 0; i < vars.length; i++) {
               supports[(blocks[indexVar] + value - offsets[indexVar]) * size + i].set(support[i]);
           }
       }

       // Get Last(x_i, val)
       public int[] getSupport(int indexVar, int value) {
           int[] resultat = new int[size];
           for (int i = 0; i < size; i++) {
               resultat[i] = supports[(blocks[indexVar] + value - offsets[indexVar]) * size + i].get();
           }
           return resultat;
       }


       // Is tuple valide ?
       public boolean isValid(int[] tuple) {
           for (int i = 0; i < size; i++)
               if (!vars[i].canBeInstantiatedTo(tuple[i])) return false;
           return true;
       }

       // Is tuple valide ?
       public boolean isValid(int[] tuple, int i) {
           return vars[i].canBeInstantiatedTo(tuple[i]);
       }

       public boolean isAllowed(int[] tuple) {
           return relation.isConsistent(tuple);
       }

       // seek a new support for (variable, value), the smallest tuple greater than currentSupport
       public int[] seekValid(int[] tuple, int indexVar, int value) {
           if (tuple == NO_TUPLE)
               return NO_TUPLE;
           int k = seekInvalidPosition(tuple, 0);
           if (k != NO_INVALID_VALUE) {
               for (int i = k + 1; i < tuple.length; i++) {
                   if (i != indexVar)
                       tuple[i] = vars[i].getDomain().getInf();
               }

               for (int i = k; i >=0; i--) {
                   if (i != indexVar) {
                       if (vars[i].getDomain().hasNextValue(tuple[i])) {
                          tuple[i] =  vars[i].getDomain().getNextValue(tuple[i]);
                          return tuple;
                       } else {
                          tuple[i] = vars[i].getDomain().getInf();
                       }
                   }
               }

               return NO_TUPLE;

           }

           return tuple;
           /*
           while (k != NO_INVALID_VALUE) {
               if (vars[k].getDomain().hasNextValue(tuple[k])) {
                   tuple[k] = vars[k].getDomain().getNextValue(tuple[k]);
               } else {
                   return NO_TUPLE;
               }

               k = seekInvalidPosition(tuple, k);
           }
           return tuple;  */
       }


       public int[] seekSupport(int[] tuple, int indexVar, int value) {
           if (tuple == NO_TUPLE)
               return NO_TUPLE;
           tuple = seekValid(tuple, indexVar, value);
           while (tuple != NO_TUPLE) {

               tuple = seekAllowed(tuple, indexVar, value);
               if (tuple == NO_TUPLE)
                   return NO_TUPLE;
               int indexInvalid = seekInvalidPosition(tuple, 0);
               if (indexInvalid == NO_INVALID_VALUE)
                   return tuple;
               tuple = seekValid(tuple, indexVar, value);
           }
           return NO_TUPLE;

       }


       static final int[] NO_TUPLE = null;
       static final int NO_INVALID_VALUE = Integer.MAX_VALUE;

       public int seekInvalidPosition(int[] tuple, int indexStart) {
           for (int i = indexStart; i < size; i++)
               if (!vars[i].canBeInstantiatedTo(tuple[i])) return i;

           return NO_INVALID_VALUE;
       }

       public int[] seekAllowed(int[] currentSupport, int indexVar, int value) {
           return ((IterLargeRelation) relation).seekAllowedSupport(currentSupport, indexVar, value);
       }


       public void awake() throws ContradictionException {
           int[] currentSupport;

           for (int indexVar = 0; indexVar < size; indexVar++) {
               IntIterator itv = vars[indexVar].getDomain().getIterator();

               while (itv.hasNext()) {
                   int val = itv.next();
                   currentSupport = seekSupport(getSupport(indexVar, val), indexVar, val);
                   if (currentSupport != NO_TUPLE) {
                       setSupport(indexVar, val, currentSupport);
                   } else {
                       vars[indexVar].removeVal(val, cIndices[indexVar]);
                   }
               }
           }
       }


       public void propagate() throws ContradictionException {
           for (int i = 0; i < size; i++)
               reviseVar(i);
       }

//  ------------ AWAKE ----------------------

       // wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww

       public void awakeOnRemovals(int idx, IntIterator deltaDomain) throws ContradictionException {
           for (int i = 0; i < size; i++)
               if (idx != i) reviseVar(i, idx);
       }

       public void awakeOnInf(int idx) throws ContradictionException {
           for (int i = 0; i < size; i++)
               if (idx != i) reviseVar(i, idx);
       }

       public void awakeOnSup(int idx) throws ContradictionException {
           for (int i = 0; i < size; i++)
               if (idx != i) reviseVar(i, idx);
       }

       public void awakeOnRem(int idx, int x) throws ContradictionException {
           for (int i = 0; i < size; i++)
               if (idx != i) reviseVar(i, idx);
       }

       public void awakeOnBounds(int idx) throws ContradictionException {
           for (int i = 0; i < size; i++)
               if (idx != i) reviseVar(i, idx);
       }

       public void awakeOnInst(int idx) throws ContradictionException {
           for (int i = 0; i < size; i++)
               if (idx != i) reviseVar(i, idx);
       }

  public String pretty() {
    StringBuilder sb = new StringBuilder();
    sb.append("GAC2001PositiveLarge({");
    for (int i = 0; i < vars.length; i++) {
      if (i > 0) sb.append(", ");
      IntDomainVar var = vars[i];
      sb.append(var.pretty());
    }
    sb.append("})");
    return sb.toString();
  }

}
