/* ************************************************
 *           _       _                            *
 *          |   (..)  |                           *
 *          |_  J||L _|        CHOCO solver       *
 *                                                *
 *     Choco is a java library for constraint     *
 *     satisfaction problems (CSP), constraint    *
 *     programming (CP) and explanation-based     *
 *     constraint solving (e-CP). It is built     *
 *     on a event-based propagation mechanism     *
 *     with backtrackable structures.             *
 *                                                *
 *     Choco is an open-source software,          *
 *     distributed under a BSD licence            *
 *     and hosted by sourceforge.net              *
 *                                                *
 *     + website : http://choco.emn.fr            *
 *     + support : choco@emn.fr                   *
 *                                                *
 *     Copyright (C) F. Laburthe,                 *
 *                   N. Jussien    1999-2010      *
 **************************************************/

package choco.cp.solver.constraints.integer;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * A class implementing the constraint A[I] == X, with I and X being IntVars and A an array of IntVars
 */
public final class ElementVG extends AbstractLargeIntSConstraint {
  protected IStateInt[] lastInf,lastSup; 
  private final IEnvironment environment;
    private final int offset;

    public ElementVG(final IntDomainVar[] vars, final int offset, final IEnvironment environment) {
    super(vars);
      this.environment = environment;
    this.offset = offset;
    initElementV();
  }

  private void initElementV() {
    lastInf = new IStateInt[vars.length + 2];
    lastSup = new IStateInt[vars.length + 2];
    }

  public Object clone() throws CloneNotSupportedException {
    final Object res = super.clone();
    ((ElementVG) res).initElementV();
    return res;
  }

  public String toString() {
    return "eltV";
  }

  public String pretty() {
    return (this.getValueVar().toString() + " = nth(" + this.getIndexVar().toString() + ", " + StringUtils.pretty(this.vars, 0, vars.length - 3) + ')');
  }


  protected IntDomainVar getIndexVar() {
    return vars[vars.length - 2];
  }

  protected IntDomainVar getValueVar() {
    return vars[vars.length - 1];
  }

  public boolean isSatisfied() {
    return (vars[getIndexVar().getVal()].getVal() == getValueVar().getVal());
  }

  protected void updateValueFromIndex(final int idx) throws ContradictionException { // rem from Index
	  final IntDomainVar idxVar = getIndexVar();
	  final IntDomainVar valVar = getValueVar();
	  for (int v = vars[idx].getInf(); v < vars[idx].getSup(); v = vars[idx].getNextDomainValue(v)) {
		  if (valVar.canBeInstantiatedTo(v)) {
	          boolean possibleV = false;
	          final DisposableIntIterator it = idxVar.getDomain().getIterator();
	          while ((it.hasNext()) && !(possibleV) ) {
	        	  final int tentativeIdx = it.next();
	        	  if (vars[tentativeIdx + offset].canBeInstantiatedTo(v)) {
	        		  possibleV = true;
	        		  break;	        	  
	        	  }	          	
	          }
              it.dispose();
	          if (!possibleV) {
	            valVar.removeVal(v, this, false);
	          }
		  }
	  }
  }
  
  protected void updateValueFromIndex() throws ContradictionException { 
    final IntDomainVar idxVar = getIndexVar();
    final IntDomainVar valVar = getValueVar();
    int minval = Integer.MAX_VALUE;
    int maxval = Integer.MIN_VALUE;
      final DisposableIntIterator iter = idxVar.getDomain().getIterator();
      for (; iter.hasNext();) {
      final int feasibleIndex = iter.next();
      minval = Math.min(minval, vars[feasibleIndex + offset].getInf());
      maxval = Math.max(maxval, vars[feasibleIndex + offset].getSup());
    }
      iter.dispose();
    // further optimization:
    // I should consider for the min, the minimum value in domain(c.vars[feasibleIndex) that is >= to valVar.inf
    // (it can be greater than valVar.inf if there are holes in domain(c.vars[feasibleIndex]))
    valVar.updateInf(minval, this, false);
    valVar.updateSup(maxval, this, false);
    // v1.0: propagate on holes when valVar has an enumerated domain
    if (valVar.hasEnumeratedDomain()) {
      for (int v = valVar.getInf(); v < valVar.getSup(); v = valVar.getNextDomainValue(v)) {
        boolean possibleV = false;
        final DisposableIntIterator it = idxVar.getDomain().getIterator();
        while ((it.hasNext()) && !(possibleV) ) {
        final int tentativeIdx = it.next();
  //      for (int tentativeIdx = idxVar.getInf(); tentativeIdx <= idxVar.getSup(); tentativeIdx = idxVar.getNextDomainValue(tentativeIdx)) {
          if (vars[tentativeIdx + offset].canBeInstantiatedTo(v)) {
            possibleV = true;
            break;
          }
        }
          it.dispose();
        if (!possibleV) {
          valVar.removeVal(v, this, false);
        }
      }
    }
  }

  protected void updateIndexFromValue(final int v) throws ContradictionException {
	  final IntDomainVar idxVar = getIndexVar();
	  final IntDomainVar valVar = getValueVar();
	  // if the domain of idxVar has been reduced to one element, then it behaves like an equality
	  if (idxVar.isInstantiated()) {
		  equalityBehaviour();	    
	  } else {	        
		  for (int idx = idxVar.getInf(); idx < idxVar.getSup(); idx = idxVar.getNextDomainValue(idx)) {    
			  if (!valVar.canBeEqualTo(vars[idx + offset])) {
		            idxVar.removeVal(idx, this, false);
		          }
		        }	    	
	    }	  
  }
    
  protected void updateIndexFromValue() throws ContradictionException {
    final IntDomainVar idxVar = getIndexVar();
    final IntDomainVar valVar = getValueVar();
    int minFeasibleIndex = Math.max(0 - offset, idxVar.getInf());
    int maxFeasibleIndex = Math.min(idxVar.getSup(), vars.length - 3 - offset);
      boolean forceAwake = false;
    if (valVar.hasEnumeratedDomain()) {
        forceAwake = true;
    }
    while (idxVar.canBeInstantiatedTo(minFeasibleIndex) &&
        !(valVar.canBeEqualTo(vars[minFeasibleIndex + offset]))) {
      minFeasibleIndex++;
    }
    idxVar.updateInf(minFeasibleIndex, this, forceAwake);


    while (idxVar.canBeInstantiatedTo(maxFeasibleIndex) &&
        !(valVar.canBeEqualTo(vars[maxFeasibleIndex + offset]))) {
      maxFeasibleIndex--;
    }
    idxVar.updateSup(maxFeasibleIndex, this, forceAwake);

    if (idxVar.hasEnumeratedDomain()) { //those remVal would be ignored for variables using an interval approximation for domain
      for (int i = minFeasibleIndex + 1; i < maxFeasibleIndex - 1; i++) {
        if (idxVar.canBeInstantiatedTo(i) && !valVar.canBeEqualTo(vars[i + offset])) {
          idxVar.removeVal(i, this, forceAwake);
        }
      }
    }
    // if the domain of idxVar has been reduced to one element, then it behaves like an equality
    if (idxVar.isInstantiated()) {
      equalityBehaviour();
    }
  }

  // cas 1-1-0 de mise � jour
  protected void updateVariable(final int index, final int value) throws ContradictionException {
      boolean existsSupport = false;
      final IntDomainVar idxVar = getIndexVar();
      final IntDomainVar valVar = getValueVar();
      final DisposableIntIterator it = idxVar.getDomain().getIterator();
      while (it.hasNext() && (!existsSupport)) {
        final int feasibleIndex = it.next() + this.offset;
        if (vars[feasibleIndex].canBeInstantiatedTo(value)) {
          existsSupport = true;
        }
      }
      it.dispose();
      if (!existsSupport) {
        valVar.removeVal(value, this, true);
      }
  }

// Once the index is known, the constraints behaves like an equality : valVar == c.vars[idxVar.value]
// This method must only be called when the value of idxVar is known.
  protected void equalityBehaviour() throws ContradictionException {
    assert(getIndexVar().isInstantiated());
    final int indexVal = getIndexVar().getVal();
    final IntDomainVar valVar = getValueVar();
    final IntDomainVar targetVar = vars[indexVal + offset];
    // code similar to awake@Equalxyc
    valVar.updateInf(targetVar.getInf(), this, false);
    valVar.updateSup(targetVar.getSup(), this, false);
    targetVar.updateInf(valVar.getInf(), this, false);
    targetVar.updateSup(valVar.getSup(), this, false);
    if (targetVar.hasEnumeratedDomain()) {
      for (int val = valVar.getInf(); val < valVar.getSup(); val = valVar.getNextDomainValue(val)) {
        if (!targetVar.canBeInstantiatedTo(val)) {
          valVar.removeVal(val, this, false);
        }
      }
    }
    if (valVar.hasEnumeratedDomain()) {
      for (int val = targetVar.getInf(); val < targetVar.getSup(); val = targetVar.getNextDomainValue(val)) {
        if (!valVar.canBeInstantiatedTo(val)) {
          targetVar.removeVal(val, this, false);
        }
      }
    }
  }

  public void awake() throws ContradictionException {
    final int n = vars.length;
    final IntDomainVar idxVar = getIndexVar();
    final IntDomainVar valVar = getValueVar();
    final int[] value;
    final int[] occur;
    final int[] firstPos;
	final int[] redirect;
	final int heigth; // length of redirect
	final int offset; // starting value in redirect
	
	/* On pr�calcule la taille minimale du tableau redirect et on initialise lastInf et lastSup */	  
    int minval = Integer.MAX_VALUE;
    int maxval = Integer.MIN_VALUE;  
    for (int i=0;i<n-2;i++) {		  
    	minval = Math.min(minval, vars[i].getInf());
    	maxval = Math.max(maxval, vars[i].getSup());
    	lastInf[i] = environment.makeInt(vars[i].getInf());
    	lastSup[i] = environment.makeInt(vars[i].getSup());
    }
    offset = minval;
    heigth = maxval - offset + 1;
    
    value = new int[n-2];	  
    occur = new int[n-2];	  
    firstPos = new int[n-2];	  
    redirect = new int[heigth];
    /*Initialisation de redirect */	  
    for (int i=0; i<heigth;i++) {		  	
    	redirect[i] = -1 ;
    }
    
	/* comptage de chaque occurrence de chaque valeur dans Tableau */
	int nbVal = 0 ;
      DisposableIntIterator iter = idxVar.getDomain().getIterator();
      for (; iter.hasNext();) {
        final int feasibleIndex = iter.next();
          final DisposableIntIterator iter2 = vars[feasibleIndex].getDomain().getIterator();
          for (; iter2.hasNext();) {
            final int feasibleValue = iter2.next();
            if (redirect[feasibleValue-offset] == -1) { /* nouvelle valeur */ 
		  		value[nbVal] = feasibleValue ;
		  		redirect[feasibleValue-offset] = nbVal;
		  		occur[nbVal] = 1 ;
		  		firstPos[nbVal] = feasibleIndex;
		  		nbVal = nbVal + 1 ;
		  } else {	/* valeur existante */
		  		occur[redirect[feasibleValue - offset]] = occur[redirect[feasibleValue - offset]] + 1 ;			  
		  }	  
        }
          iter2.dispose();
    }
      iter.dispose();

	  /* Update Index via Var = cas 0-1-1 : 
		v in Tableau, i in Index but v not in Var => remove i from Index if Tableau is fixed or v from Tableau is Index is fixed */
      for (int i = 0; i < nbVal; i++) {
    	  for (int j = 0; j<n-2;j++) {
    		  if (vars[j].canBeInstantiatedTo(value[i])) {
    			  if (vars[j].getDomainSize() == 1) {
    				  idxVar.removeVal(j, this, false);
    			  }
    			  if (idxVar.getDomainSize() == 1) {
    				  vars[j].removeVal(value[i], this, false);
    			  }
    		  }
    	  }
      }


	  /* update Var via Index = cas 1-0-1 :
		v in Tableau, i not in Index => remove v from Var si occur = 1 sinon update FirstPos */	  
	  for (int i=0;i<nbVal;i++) {
		  if (valVar.canBeInstantiatedTo(value[i])) {
			  while ((occur[i] > 1) && (!idxVar.canBeInstantiatedTo(firstPos[i]))) {
				  occur[i] = occur[i] - 1;
				  int j = firstPos[i] + 1;
				  while (!vars[j].canBeInstantiatedTo(value[i])) {
					  j = j + 1;
				  }
				  firstPos[i] = j;
			  }
			  if ((occur[i] == 1) && (!idxVar.canBeInstantiatedTo(firstPos[i]))) {
				  valVar.removeVal(value[i], this, false);
			  } 	  /* sinon, firstPos est mis a une v�ritable premi�re position de v , cas 1-1-1 */
		  }
	  }

	  /* cas 1-1-0 :
	     aucun support entre la variable et une variable du tableau => remove index i  */  
	  iter = idxVar.getDomain().getIterator();
      try{
          for (; iter.hasNext();) {
              final int feasibleIndex = iter.next();
              if (!valVar.canBeEqualTo(vars[feasibleIndex])) {
                  idxVar.removeVal(feasibleIndex, this, false);
              }
          }
      }finally {
          iter.dispose();
      }
	  
	  /* Elegage des bornes d'Index et enregistrement des premieres valeurs */
	  lastInf[n-2] = environment.makeInt(idxVar.getInf());
	  if (n < idxVar.getSup()) {
		  idxVar.updateSup(n, this, false);
		  lastSup[n-2] = environment.makeInt(n);
	  } else {
		  lastSup[n-2] = environment.makeInt(idxVar.getSup());
	  }
	  
	  /* Elegage des bornes de Var et enregistrement des premieres valeurs */
	  if (offset > valVar.getInf()) {
		  valVar.updateInf(offset, this, false);
		  lastInf[n-1] = environment.makeInt(offset);
	  } else {
		  lastInf[n-1] = environment.makeInt(valVar.getInf());
	  }
	  if (valVar.getSup() > (heigth + offset - 1)) {
		  valVar.updateSup(heigth + offset - 1, this, false);
		  lastSup[n-1] = environment.makeInt(heigth + offset - 1);
	  } else {
		  lastSup[n-1] = environment.makeInt(valVar.getSup());
	  }
	  	  
	  /* update Var via Tableau = cas 1-0-0 :
	  		v in Var but not in Tableau => remove v from Var */
	  for (int i=offset;i<heigth+offset-1;i++) { /* on balaie les valeurs restantes de Var */
		  if (valVar.canBeInstantiatedTo(i) && (redirect[i - offset] == -1)) {
			  valVar.removeVal(i, this, false);
		  }  
	  }
  }

  public void awakeOnInf(final int idx) throws ContradictionException {
    final IntDomainVar idxVar = getIndexVar();
    final IntDomainVar valVar = getValueVar();
    if (idx == vars.length - 2) {        // the event concerns idxVar
      if (idxVar.isInstantiated()) {
        equalityBehaviour();
      } else {
      	final int minIndex = idxVar.getInf();
    	for (int index=lastInf[vars.length-2].get();index<minIndex;index++) {
    		updateValueFromIndex(index);
    	}
    	lastInf[vars.length-2].set(minIndex);
      }
    } else if (idx == vars.length - 1) { // the event concerns valVar
      if (idxVar.isInstantiated()) {
        final int idxVal = idxVar.getVal();
        vars[idxVal + offset].updateInf(valVar.getInf(), this, false);
      } else {
      	final int minVar = valVar.getInf();
    	for (int index=lastInf[vars.length-1].get();index<minVar;index++) {
    	      updateIndexFromValue(index);
    	}
    	lastInf[vars.length-1].set(minVar);
      }
    } else {                            // the event concerns a variable from the array
      if (idxVar.isInstantiated()) {
        final int idxVal = idxVar.getVal();
        if (idx == idxVal + offset) {
          valVar.updateInf(vars[idx].getInf(), this, false);
        }
      } else if (idxVar.canBeInstantiatedTo(idx - offset)) {  //otherwise the variable is not in scope
        if (!valVar.canBeEqualTo(vars[idx])) {
          idxVar.removeVal(idx - offset, this, true);
          // NOCAUSE because if it changes the domain of IndexVar (what is not sure if idxVar
          // uses an interval approximated domain) then it must cause updateValueFromIndex(c)
        } else {
        	final int minVar = vars[idx].getInf();
        	for (int index=lastInf[idx].get();index<minVar;index++) {
        	      updateVariable(idx,index);
        	}
        	lastInf[idx].set(minVar);
        	
        }
      }
    }
  }

  public void awakeOnSup(final int idx) throws ContradictionException {
    final IntDomainVar idxVar = getIndexVar();
    final IntDomainVar valVar = getValueVar();
    if (idx == vars.length - 2) {        // the event concerns idxVar
      if (idxVar.isInstantiated()) {
        equalityBehaviour();
      } else {
      	final int maxIndex = lastSup[vars.length-2].get();
    	for (int index=idxVar.getSup()+1;index<=maxIndex;index++) {
    		this.updateValueFromIndex(index);
    	}
    	lastSup[vars.length-2].set(idxVar.getSup());
      }
    } else if (idx == vars.length - 1) {  // the event concerns valVar
      if (idxVar.isInstantiated()) {
        final int idxVal = idxVar.getVal();
        vars[idxVal + offset].updateSup(valVar.getSup(), this, false);
      } else {
      	final int maxVar = lastSup[vars.length-1].get();
    	for (int index=valVar.getSup()+1;index<=maxVar;index++) {
    	      this.updateIndexFromValue(index);
    	}
    	lastSup[vars.length-1].set(valVar.getSup());      
      }
    } else {                            // the event concerns a variable from the array
      if (idxVar.isInstantiated()) {
        final int idxVal = idxVar.getVal();
        if (idx == idxVal + offset) {
          valVar.updateSup(vars[idx].getSup(), this, false);
        }
      } else if (idxVar.canBeInstantiatedTo(idx - offset)) {  //otherwise the variable is not in scope
        if (!valVar.canBeEqualTo(vars[idx])) {
          idxVar.removeVal(idx - offset, this, true);
          // NOCAUSE because if it changes the domain of IndexVar (what is not sure if idxVar
          // uses an interval approximated domain) then it must cause updateValueFromIndex(c)
        } else {
        	final int maxVar = lastSup[idx].get();
        	for (int index=vars[idx].getSup()+1;index<maxVar;index++) {
        	      updateVariable(idx,index);
        	}
        	lastSup[idx].set(vars[idx].getSup());
        }
      }
    }
  }

  public void awakeOnInst(final int idx) throws ContradictionException {
    final IntDomainVar idxVar = getIndexVar();
    final IntDomainVar valVar = getValueVar();
    if (idx == vars.length - 2) {        // the event concerns idxVar
      equalityBehaviour();
    } else if (idx == vars.length - 1) {  // the event concerns valVar
      if (idxVar.isInstantiated()) {
        final int idxVal = idxVar.getVal();
        vars[idxVal + offset].instantiate(valVar.getVal(), this, false);
      } else {
        	final int minVar = valVar.getInf();
        	for (int index=lastInf[vars.length-1].get();index<minVar;index++) {
        	      updateIndexFromValue(index);
        	}
        	lastInf[vars.length-1].set(minVar);
          	final int maxVar = lastSup[vars.length-1].get();
        	for (int index=valVar.getSup()+1;index<=maxVar;index++) {
        	      this.updateIndexFromValue(index);
        	}
        	lastSup[vars.length-1].set(valVar.getSup());      
      }
    } else {                            // the event concerns a variable from the array
      if (idxVar.isInstantiated()) {
        final int idxVal = idxVar.getVal();
        if (idx == idxVal + offset) {
          valVar.instantiate(vars[idx].getVal(), this, false);
        }
      } else if (idxVar.canBeInstantiatedTo(idx - offset)) {  //otherwise the variable is not in scope
        if (!valVar.canBeEqualTo(vars[idx])) {
          idxVar.removeVal(idx - offset, this, true);
          // NOCAUSE because if it changes the domain of IndexVar (what is not sure if idxVar
          // uses an interval approximated domain) then it must cause updateValueFromIndex(c)
        } else {
        	final int minVar = vars[idx].getInf();
        	for (int index=lastInf[idx].get();index<minVar;index++) {
        	      updateVariable(idx,index);
        	}
        	lastInf[idx].set(minVar);
        	final int maxVar = lastSup[idx].get();
        	for (int index=vars[idx].getSup()+1;index<maxVar;index++) {
        	      updateVariable(idx,index);
        	}
        	lastSup[idx].set(vars[idx].getSup());
        }
      }
    }
  }

  public void awakeOnRem(final int idx, final int x) throws ContradictionException {
    final IntDomainVar idxVar = getIndexVar();
    final IntDomainVar valVar = getValueVar();
    if (idx == vars.length - 2) {        // the event concerns idxVar
      updateValueFromIndex(x);
    } else if (idx == vars.length - 1) {  // the event concerns valVar
      if (idxVar.isInstantiated()) {
        final int idxVal = idxVar.getVal();
        vars[idxVal + offset].removeVal(x, this, false);
      } else {
        updateIndexFromValue(x);
      }
    } else {                            // the event concerns a variable from the array
      if (idxVar.isInstantiated()) {
        final int idxVal = idxVar.getVal();
        if (idx == idxVal + offset) {
          valVar.removeVal(x, this, false);
        }
      } else if ((idxVar.canBeInstantiatedTo(idx - offset)) && (valVar.hasEnumeratedDomain())) {
    	  updateVariable(idx,x);
      }
    }
  }

  public void awakeOnVar(final int idx) throws ContradictionException {
    final IntDomainVar idxVar = getIndexVar();
    final IntDomainVar valVar = getValueVar();
    if (idx == vars.length - 2) {
      if (idxVar.isInstantiated()) {
        equalityBehaviour();
      } else {
        updateValueFromIndex();
      }
    } else if (idx == vars.length - 1) {
      if (idxVar.isInstantiated()) {
        final int idxVal = idxVar.getVal();
        vars[idxVal + offset].updateSup(valVar.getSup(), this, false);
        vars[idxVal + offset].updateInf(valVar.getInf(), this, false);
          final DisposableIntIterator it = vars[idxVal + offset].getDomain().getIterator();
          for (; it.hasNext();) {
          final int v = it.next();
          if (!(valVar.canBeInstantiatedTo(v))) {
            vars[idxVal + offset].removeVal(v, this, false);
          }
        }
          it.dispose();
      } else {
        updateIndexFromValue();
      }
    } else {
      if (idxVar.isInstantiated()) {
        final int idxVal = idxVar.getVal();
        if (idx == idxVal + offset) {
          valVar.updateSup(vars[idx].getSup(), this, false);
          valVar.updateInf(vars[idx].getInf(), this, false);
            final DisposableIntIterator it = valVar.getDomain().getIterator();
            for (; it.hasNext();) {
            final int v = it.next();
            if (!(vars[idx].canBeInstantiatedTo(v))) {
              valVar.removeVal(v, this, false);
            }
          }
            it.dispose();
        }
      } else if (idxVar.canBeInstantiatedTo(idx - offset)) {  // otherwise the variable is not in scope
        // TODO : there is probably something missing here ....
      }
    }
  }

  public Boolean isEntailed() {
    Boolean isEntailed = null;
    final IntDomainVar idxVar = getIndexVar();
    final IntDomainVar valVar = getValueVar();
    if ((valVar.isInstantiated()) &&
        (idxVar.getInf() + this.offset >= 0) &&
        (idxVar.getSup() + this.offset < vars.length - 2)) {
      boolean allEqualToValVar = true;
        final DisposableIntIterator it = idxVar.getDomain().getIterator();
        for (; it.hasNext();) {
        final int feasibleIndex = it.next() + this.offset;
        if (!vars[feasibleIndex].isInstantiatedTo(valVar.getVal())) {
          allEqualToValVar = false;
        }
      }
        it.dispose();
      if (allEqualToValVar) {
        isEntailed = Boolean.TRUE;
      }
    }
    if (isEntailed != Boolean.TRUE) {
      boolean existsSupport = false;
        final DisposableIntIterator it = idxVar.getDomain().getIterator();
        for (; it.hasNext();) {
        final int feasibleIndex = it.next() + this.offset;
        if ((feasibleIndex >= 0) && (feasibleIndex < vars.length - 2) && (valVar.canBeEqualTo(vars[feasibleIndex]))) {
          existsSupport = true;
        }
      }
        it.dispose();
      if (!existsSupport) isEntailed = Boolean.FALSE;
    }
    return isEntailed;
  }

    @Override
    public void propagate() throws ContradictionException {
	// TODO Auto-generated method stub
	
}


}