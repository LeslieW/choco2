// **************************************************
// *  CHOCO: an open-source Constraint Programming  *
// *     System for Research and Education          *
// *                                                *
// *    contributors listed in choco.Entity.java    *
// *           Copyright (C) F. Laburthe, 1999-2006 *
// **************************************************

package choco.cp.solver.constraints.integer;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.integer.AbstractLargeIntSConstraint;
import choco.kernel.solver.propagation.event.VarEvent;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * A class implementing the constraint A[I] == X, with I and X being IntVars and A an array of IntVars
 */
public class ElementVG extends AbstractLargeIntSConstraint {
  protected IStateInt[] lastInf,lastSup; 
  private final IEnvironment environment;
  public ElementVG(IntDomainVar[] vars, int offset, IEnvironment environment) {
    super(vars);
      this.environment = environment;
    this.cste = offset;
    initElementV();
  }

  private void initElementV() {
    lastInf = new IStateInt[vars.length + 2];
    lastSup = new IStateInt[vars.length + 2];
    }

  public Object clone() throws CloneNotSupportedException {
    Object res = super.clone();
    ((ElementVG) res).initElementV();
    return res;
  }

  public String toString() {
    return "eltV";
  }

  public String pretty() {
    return (this.getValueVar().toString() + " = nth(" + this.getIndexVar().toString() + ", " + StringUtils.pretty(this.vars, 0, vars.length - 3) + ")");
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

  protected void updateValueFromIndex(int idx) throws ContradictionException { // rem from Index	    
	  IntDomainVar idxVar = getIndexVar();  
	  IntDomainVar valVar = getValueVar();
	  for (int v = vars[idx].getInf(); v < vars[idx].getSup(); v = vars[idx].getNextDomainValue(v)) {
		  if (valVar.canBeInstantiatedTo(v)) {
	          boolean possibleV = false;
	          DisposableIntIterator it = idxVar.getDomain().getIterator();
	          while ((it.hasNext()) && !(possibleV) ) {
	        	  int tentativeIdx = it.next();  
	        	  if (vars[tentativeIdx + cste].canBeInstantiatedTo(v)) {	              
	        		  possibleV = true;
	        		  break;	        	  
	        	  }	          	
	          }
	          if (!possibleV) {
	            valVar.removeVal(v, cIndices[vars.length - 1]);
	          }
		  }
	  }
  }
  
  protected void updateValueFromIndex() throws ContradictionException { 
    IntDomainVar idxVar = getIndexVar();
    IntDomainVar valVar = getValueVar();
    int minval = Integer.MAX_VALUE;
    int maxval = Integer.MIN_VALUE;
    for (DisposableIntIterator iter = idxVar.getDomain().getIterator(); iter.hasNext();) {
      int feasibleIndex = iter.next();
      minval = Math.min(minval, vars[feasibleIndex + cste].getInf());
      maxval = Math.max(maxval, vars[feasibleIndex + cste].getSup());
    }
    // further optimization:
    // I should consider for the min, the minimum value in domain(c.vars[feasibleIndex) that is >= to valVar.inf
    // (it can be greater than valVar.inf if there are holes in domain(c.vars[feasibleIndex]))
    valVar.updateInf(minval, cIndices[vars.length - 1]);
    valVar.updateSup(maxval, cIndices[vars.length - 1]);
    // v1.0: propagate on holes when valVar has an enumerated domain
    if (valVar.hasEnumeratedDomain()) {
      for (int v = valVar.getInf(); v < valVar.getSup(); v = valVar.getNextDomainValue(v)) {
        boolean possibleV = false;
        DisposableIntIterator it = idxVar.getDomain().getIterator();
        while ((it.hasNext()) && !(possibleV) ) {
        int tentativeIdx = it.next();
  //      for (int tentativeIdx = idxVar.getInf(); tentativeIdx <= idxVar.getSup(); tentativeIdx = idxVar.getNextDomainValue(tentativeIdx)) {
          if (vars[tentativeIdx + cste].canBeInstantiatedTo(v)) {
            possibleV = true;
            break;
          }
        }
        if (!possibleV) {
          valVar.removeVal(v, cIndices[vars.length - 1]);
        }
      }
    }
  }

  protected void updateIndexFromValue(int v) throws ContradictionException {
	  IntDomainVar idxVar = getIndexVar();  
	  IntDomainVar valVar = getValueVar();
	  // if the domain of idxVar has been reduced to one element, then it behaves like an equality
	  if (idxVar.isInstantiated()) {
		  equalityBehaviour();	    
	  } else {	        
		  for (int idx = idxVar.getInf(); idx < idxVar.getSup(); idx = idxVar.getNextDomainValue(idx)) {    
			  if (!valVar.canBeEqualTo(vars[idx + cste])) {
		            idxVar.removeVal(idx, cIndices[vars.length - 2]);
		          }
		        }	    	
	    }	  
  }
    
  protected void updateIndexFromValue() throws ContradictionException {
    IntDomainVar idxVar = getIndexVar();
    IntDomainVar valVar = getValueVar();
    int minFeasibleIndex = Math.max(0 - cste, idxVar.getInf());
    int maxFeasibleIndex = Math.min(idxVar.getSup(), vars.length - 3 - cste);
    int cause = cIndices[vars.length - 2];
    if (valVar.hasEnumeratedDomain()) {
      cause = VarEvent.NOCAUSE;
    }
    while (idxVar.canBeInstantiatedTo(minFeasibleIndex) &&
        !(valVar.canBeEqualTo(vars[minFeasibleIndex + cste]))) {
      minFeasibleIndex++;
    }
    idxVar.updateInf(minFeasibleIndex, cause);


    while (idxVar.canBeInstantiatedTo(maxFeasibleIndex) &&
        !(valVar.canBeEqualTo(vars[maxFeasibleIndex + cste]))) {
      maxFeasibleIndex--;
    }
    idxVar.updateSup(maxFeasibleIndex, cause);

    if (idxVar.hasEnumeratedDomain()) { //those remVal would be ignored for variables using an interval approximation for domain
      for (int i = minFeasibleIndex + 1; i < maxFeasibleIndex - 1; i++) {
        if (idxVar.canBeInstantiatedTo(i) && !valVar.canBeEqualTo(vars[i + cste])) {
          idxVar.removeVal(i, cause);
        }
      }
    }
    // if the domain of idxVar has been reduced to one element, then it behaves like an equality
    if (idxVar.isInstantiated()) {
      equalityBehaviour();
    }
  }

  // cas 1-1-0 de mise à jour
  protected void updateVariable(int index, int value) throws ContradictionException {
      boolean existsSupport = false;
      IntDomainVar idxVar = getIndexVar();
      IntDomainVar valVar = getValueVar();
      DisposableIntIterator it = idxVar.getDomain().getIterator();
      while (it.hasNext() && (existsSupport == false)) {
        int feasibleIndex = it.next() + this.cste;
        if (vars[feasibleIndex].canBeInstantiatedTo(value)) {
          existsSupport = true;
        }
      }
      if (!existsSupport) {
        valVar.removeVal(value, VarEvent.NOCAUSE);
      }
  }

// Once the index is known, the constraints behaves like an equality : valVar == c.vars[idxVar.value]
// This method must only be called when the value of idxVar is known.
  protected void equalityBehaviour() throws ContradictionException {
    assert(getIndexVar().isInstantiated());
    int indexVal = getIndexVar().getVal();
    IntDomainVar valVar = getValueVar();
    IntDomainVar targetVar = vars[indexVal + cste];
    // code similar to awake@Equalxyc
    valVar.updateInf(targetVar.getInf(), cIndices[vars.length - 1]);
    valVar.updateSup(targetVar.getSup(), cIndices[vars.length - 1]);
    targetVar.updateInf(valVar.getInf(), cIndices[indexVal + cste]);
    targetVar.updateSup(valVar.getSup(), cIndices[indexVal + cste]);
    if (targetVar.hasEnumeratedDomain()) {
      for (int val = valVar.getInf(); val < valVar.getSup(); val = valVar.getNextDomainValue(val)) {
        if (!targetVar.canBeInstantiatedTo(val)) {
          valVar.removeVal(val, cIndices[vars.length - 1]);
        }
      }
    }
    if (valVar.hasEnumeratedDomain()) {
      for (int val = targetVar.getInf(); val < targetVar.getSup(); val = targetVar.getNextDomainValue(val)) {
        if (!valVar.canBeInstantiatedTo(val)) {
          targetVar.removeVal(val, cIndices[indexVal]);
        }
      }
    }
  }

  public void awake() throws ContradictionException {
    int n = vars.length;
    IntDomainVar idxVar = getIndexVar();
    IntDomainVar valVar = getValueVar();
    int[] value;  
    int[] occur;
    int[] firstPos; 
	int[] redirect;
	int heigth; // length of redirect
	int offset; // starting value in redirect
	
	/* On précalcule la taille minimale du tableau redirect et on initialise lastInf et lastSup */	  
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
    for (DisposableIntIterator iter = idxVar.getDomain().getIterator(); iter.hasNext();) {
        int feasibleIndex = iter.next();
        for (DisposableIntIterator iter2 = vars[feasibleIndex].getDomain().getIterator(); iter2.hasNext();) {
            int feasibleValue = iter2.next();
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
    }

	  /* Update Index via Var = cas 0-1-1 : 
		v in Tableau, i in Index but v not in Var => remove i from Index if Tableau is fixed or v from Tableau is Index is fixed */
      for (int i = 0; i < nbVal; i++) {
    	  for (int j = 0; j<n-2;j++) {
    		  if (vars[j].canBeInstantiatedTo(value[i])) {
    			  if (vars[j].getDomainSize() == 1) {
    				  idxVar.removeVal(j,cIndices[n - 2]);
    			  }
    			  if (idxVar.getDomainSize() == 1) {
    				  vars[j].removeVal(value[i], cIndices[j]);
    			  }
    		  }
    	  }
      }


	  /* update Var via Index = cas 1-0-1 :
		v in Tableau, i not in Index => remove v from Var si occur = 1 sinon update FirstPos */	  
	  for (int i=0;i<nbVal;i++) {
		  if (valVar.canBeInstantiatedTo(value[i]) == true) {
			  while ((occur[i] > 1) && (idxVar.canBeInstantiatedTo(firstPos[i]) == false)) {
				  occur[i] = occur[i] - 1;
				  int j = firstPos[i] + 1;
				  while (vars[j].canBeInstantiatedTo(value[i]) == false) {
					  j = j + 1;
				  }
				  firstPos[i] = j;
			  }
			  if ((occur[i] == 1) && (idxVar.canBeInstantiatedTo(firstPos[i]) == false)) {
				  valVar.removeVal(value[i], cIndices[n - 2]);
			  } 	  /* sinon, firstPos est mis a une véritable première position de v , cas 1-1-1 */
		  }
	  }

	  /* cas 1-1-0 :
	     aucun support entre la variable et une variable du tableau => remove index i  */  
	  for (DisposableIntIterator iter = idxVar.getDomain().getIterator(); iter.hasNext();) {
		  int feasibleIndex = iter.next();
		  if (!valVar.canBeEqualTo(vars[feasibleIndex])) {
			  idxVar.removeVal(feasibleIndex, cIndices[n - 1]);
		  }
	  }
	  
	  /* Elegage des bornes d'Index et enregistrement des premieres valeurs */
	  lastInf[n-2] = environment.makeInt(idxVar.getInf());
	  if (n < idxVar.getSup()) {
		  idxVar.updateSup(n, cIndices[n - 2]);
		  lastSup[n-2] = environment.makeInt(n);
	  } else {
		  lastSup[n-2] = environment.makeInt(idxVar.getSup());
	  }
	  
	  /* Elegage des bornes de Var et enregistrement des premieres valeurs */
	  if (offset > valVar.getInf()) {
		  valVar.updateInf(offset, cIndices[n - 1]);
		  lastInf[n-1] = environment.makeInt(offset);
	  } else {
		  lastInf[n-1] = environment.makeInt(valVar.getInf());
	  }
	  if (valVar.getSup() > (heigth + offset - 1)) {
		  valVar.updateSup(heigth + offset - 1, cIndices[n - 1]);
		  lastSup[n-1] = environment.makeInt(heigth + offset - 1);
	  } else {
		  lastSup[n-1] = environment.makeInt(valVar.getSup());
	  }
	  	  
	  /* update Var via Tableau = cas 1-0-0 :
	  		v in Var but not in Tableau => remove v from Var */
	  for (int i=offset;i<heigth+offset-1;i++) { /* on balaie les valeurs restantes de Var */
		  if (valVar.canBeInstantiatedTo(i) && (redirect[i - offset] == -1)) {
			  valVar.removeVal(i, cIndices[n - 1]);			  
		  }  
	  }
  }

  public void awakeOnInf(int idx) throws ContradictionException {
    IntDomainVar idxVar = getIndexVar();
    IntDomainVar valVar = getValueVar();
    if (idx == vars.length - 2) {        // the event concerns idxVar
      if (idxVar.isInstantiated()) {
        equalityBehaviour();
      } else {
      	int minIndex = idxVar.getInf();
    	for (int index=lastInf[vars.length-2].get();index<minIndex;index++) {
    		updateValueFromIndex(index);
    	}
    	lastInf[vars.length-2].set(minIndex);
      }
    } else if (idx == vars.length - 1) { // the event concerns valVar
      if (idxVar.isInstantiated()) {
        int idxVal = idxVar.getVal();
        vars[idxVal + cste].updateInf(valVar.getInf(), cIndices[idxVal + cste]);
      } else {
      	int minVar = valVar.getInf();
    	for (int index=lastInf[vars.length-1].get();index<minVar;index++) {
    	      updateIndexFromValue(index);
    	}
    	lastInf[vars.length-1].set(minVar);
      }
    } else {                            // the event concerns a variable from the array
      if (idxVar.isInstantiated()) {
        int idxVal = idxVar.getVal();
        if (idx == idxVal + cste) {
          valVar.updateInf(vars[idx].getInf(), cIndices[vars.length - 1]);
        }
      } else if (idxVar.canBeInstantiatedTo(idx - cste)) {  //otherwise the variable is not in scope
        if (!valVar.canBeEqualTo(vars[idx])) {
          idxVar.removeVal(idx - cste, VarEvent.NOCAUSE);
          // NOCAUSE because if it changes the domain of IndexVar (what is not sure if idxVar
          // uses an interval approximated domain) then it must cause updateValueFromIndex(c)
        } else {
        	int minVar = vars[idx].getInf();
        	for (int index=lastInf[idx].get();index<minVar;index++) {
        	      updateVariable(idx,index);
        	}
        	lastInf[idx].set(minVar);
        	
        }
      }
    }
  }

  public void awakeOnSup(int idx) throws ContradictionException {
    IntDomainVar idxVar = getIndexVar();
    IntDomainVar valVar = getValueVar();
    if (idx == vars.length - 2) {        // the event concerns idxVar
      if (idxVar.isInstantiated()) {
        equalityBehaviour();
      } else {
      	int maxIndex = lastSup[vars.length-2].get();
    	for (int index=idxVar.getSup()+1;index<=maxIndex;index++) {
    		this.updateValueFromIndex(index);
    	}
    	lastSup[vars.length-2].set(idxVar.getSup());
      }
    } else if (idx == vars.length - 1) {  // the event concerns valVar
      if (idxVar.isInstantiated()) {
        int idxVal = idxVar.getVal();
        vars[idxVal + cste].updateSup(valVar.getSup(), cIndices[idxVal + cste]);
      } else {
      	int maxVar = lastSup[vars.length-1].get();
    	for (int index=valVar.getSup()+1;index<=maxVar;index++) {
    	      this.updateIndexFromValue(index);
    	}
    	lastSup[vars.length-1].set(valVar.getSup());      
      }
    } else {                            // the event concerns a variable from the array
      if (idxVar.isInstantiated()) {
        int idxVal = idxVar.getVal();
        if (idx == idxVal + cste) {
          valVar.updateSup(vars[idx].getSup(), cIndices[vars.length - 1]);
        }
      } else if (idxVar.canBeInstantiatedTo(idx - cste)) {  //otherwise the variable is not in scope
        if (!valVar.canBeEqualTo(vars[idx])) {
          idxVar.removeVal(idx - cste, VarEvent.NOCAUSE);
          // NOCAUSE because if it changes the domain of IndexVar (what is not sure if idxVar
          // uses an interval approximated domain) then it must cause updateValueFromIndex(c)
        } else {
        	int maxVar = lastSup[idx].get();
        	for (int index=vars[idx].getSup()+1;index<maxVar;index++) {
        	      updateVariable(idx,index);
        	}
        	lastSup[idx].set(vars[idx].getSup());
        }
      }
    }
  }

  public void awakeOnInst(int idx) throws ContradictionException {
    IntDomainVar idxVar = getIndexVar();
    IntDomainVar valVar = getValueVar();
    if (idx == vars.length - 2) {        // the event concerns idxVar
      equalityBehaviour();
    } else if (idx == vars.length - 1) {  // the event concerns valVar
      if (idxVar.isInstantiated()) {
        int idxVal = idxVar.getVal();
        vars[idxVal + cste].instantiate(valVar.getVal(), cIndices[idxVal + cste]);
      } else {
        	int minVar = valVar.getInf();
        	for (int index=lastInf[vars.length-1].get();index<minVar;index++) {
        	      updateIndexFromValue(index);
        	}
        	lastInf[vars.length-1].set(minVar);
          	int maxVar = lastSup[vars.length-1].get();
        	for (int index=valVar.getSup()+1;index<=maxVar;index++) {
        	      this.updateIndexFromValue(index);
        	}
        	lastSup[vars.length-1].set(valVar.getSup());      
      }
    } else {                            // the event concerns a variable from the array
      if (idxVar.isInstantiated()) {
        int idxVal = idxVar.getVal();
        if (idx == idxVal + cste) {
          valVar.instantiate(vars[idx].getVal(), cIndices[vars.length - 1]);
        }
      } else if (idxVar.canBeInstantiatedTo(idx - cste)) {  //otherwise the variable is not in scope
        if (!valVar.canBeEqualTo(vars[idx])) {
          idxVar.removeVal(idx - cste, VarEvent.NOCAUSE);
          // NOCAUSE because if it changes the domain of IndexVar (what is not sure if idxVar
          // uses an interval approximated domain) then it must cause updateValueFromIndex(c)
        } else {
        	int minVar = vars[idx].getInf();
        	for (int index=lastInf[idx].get();index<minVar;index++) {
        	      updateVariable(idx,index);
        	}
        	lastInf[idx].set(minVar);
        	int maxVar = lastSup[idx].get();
        	for (int index=vars[idx].getSup()+1;index<maxVar;index++) {
        	      updateVariable(idx,index);
        	}
        	lastSup[idx].set(vars[idx].getSup());
        }
      }
    }
  }

  public void awakeOnRem(int idx, int x) throws ContradictionException {
    IntDomainVar idxVar = getIndexVar();
    IntDomainVar valVar = getValueVar();
    if (idx == vars.length - 2) {        // the event concerns idxVar
      updateValueFromIndex(x);
    } else if (idx == vars.length - 1) {  // the event concerns valVar
      if (idxVar.isInstantiated()) {
        int idxVal = idxVar.getVal();
        vars[idxVal + cste].removeVal(x, cIndices[idxVal + cste]);
      } else {
        updateIndexFromValue(x);
      }
    } else {                            // the event concerns a variable from the array
      if (idxVar.isInstantiated()) {
        int idxVal = idxVar.getVal();
        if (idx == idxVal + cste) {
          valVar.removeVal(x, cIndices[vars.length - 1]);
        }
      } else if ((idxVar.canBeInstantiatedTo(idx - cste)) && (valVar.hasEnumeratedDomain())) {
    	  updateVariable(idx,x);
      }
    }
  }

  public void awakeOnVar(int idx) throws ContradictionException {
    IntDomainVar idxVar = getIndexVar();
    IntDomainVar valVar = getValueVar();
    if (idx == vars.length - 2) {
      if (idxVar.isInstantiated()) {
        equalityBehaviour();
      } else {
        updateValueFromIndex();
      }
    } else if (idx == vars.length - 1) {
      if (idxVar.isInstantiated()) {
        int idxVal = idxVar.getVal();
        vars[idxVal + cste].updateSup(valVar.getSup(), cIndices[idxVal + cste]);
        vars[idxVal + cste].updateInf(valVar.getInf(), cIndices[idxVal + cste]);
        for (DisposableIntIterator it = vars[idxVal + cste].getDomain().getIterator(); it.hasNext();) {
          int v = it.next();
          if (!(valVar.canBeInstantiatedTo(v))) {
            vars[idxVal + cste].removeVal(v, cIndices[idxVal + cste]);
          }
        }
      } else {
        updateIndexFromValue();
      }
    } else {
      if (idxVar.isInstantiated()) {
        int idxVal = idxVar.getVal();
        if (idx == idxVal + cste) {
          valVar.updateSup(vars[idx].getSup(), cIndices[vars.length - 1]);
          valVar.updateInf(vars[idx].getInf(), cIndices[vars.length - 1]);
          for (DisposableIntIterator it = valVar.getDomain().getIterator(); it.hasNext();) {
            int v = it.next();
            if (!(vars[idx].canBeInstantiatedTo(v))) {
              valVar.removeVal(v, cIndices[vars.length - 1]);
            }
          }
        }
      } else if (idxVar.canBeInstantiatedTo(idx - cste)) {  // otherwise the variable is not in scope
        // TODO : there is probably something missing here ....
      }
    }
  }

  public Boolean isEntailed() {
    Boolean isEntailed = null;
    IntDomainVar idxVar = getIndexVar();
    IntDomainVar valVar = getValueVar();
    if ((valVar.isInstantiated()) &&
        (idxVar.getInf() + this.cste >= 0) &&
        (idxVar.getSup() + this.cste < vars.length - 2)) {
      boolean allEqualToValVar = true;
      for (DisposableIntIterator it = idxVar.getDomain().getIterator(); it.hasNext();) {
        int feasibleIndex = it.next() + this.cste;
        if (!vars[feasibleIndex].isInstantiatedTo(valVar.getVal())) {
          allEqualToValVar = false;
        }
      }
      if (allEqualToValVar) {
        isEntailed = Boolean.TRUE;
      }
    }
    if (isEntailed != Boolean.TRUE) {
      boolean existsSupport = false;
      for (DisposableIntIterator it = idxVar.getDomain().getIterator(); it.hasNext();) {
        int feasibleIndex = it.next() + this.cste;
        if ((feasibleIndex >= 0) && (feasibleIndex < vars.length - 2) && (valVar.canBeEqualTo(vars[feasibleIndex]))) {
          existsSupport = true;
        }
      }
      if (!existsSupport) isEntailed = Boolean.FALSE;
    }
    return isEntailed;
  }

@Override
public void propagate() throws ContradictionException {
	// TODO Auto-generated method stub
	
}


}
