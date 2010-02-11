/*
 * Created on 19 aout 08 by coletta 
 *
 */
package choco.cp.solver.constraints.set;

import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.memory.IEnvironment;
import choco.kernel.memory.IStateInt;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.constraints.set.AbstractLargeSetSConstraint;
import choco.kernel.solver.variables.set.SetVar;

import java.util.Arrays;
import java.util.HashMap;

public class SetNaryUnion extends AbstractLargeSetSConstraint {

    protected SetVar[] setVars;
    protected SetVar unionSet;
    /*
     * store the number of occurences of each value
     * in envellop of setvars set variables
     */
    protected IStateInt[] occurCpt;
    protected final static int UNION_SET_INDEX = 0;
    
    public SetNaryUnion(SetVar[] vars, IEnvironment environment) {
        super(vars);
        unionSet = vars[UNION_SET_INDEX];
        setVars = Arrays.copyOfRange(vars, 1, vars.length);
        HashMap<Integer,Integer> allValues = new HashMap<Integer,Integer>();
        for(SetVar v : vars) { 
            DisposableIntIterator it = v.getDomain().getEnveloppeIterator(); 
            while (it.hasNext()) {
                int val = it.next();
                if (allValues.containsKey(val)) allValues.put(val, allValues.get(val)+1);
                else allValues.put(val,1);
            }
        }
        int max = 0;
        for (int v : allValues.keySet())  max = (v > max ? v : max);
        occurCpt = new IStateInt[max+1];
        for (int v : allValues.keySet()) {
            occurCpt[v] = environment.makeInt();
            occurCpt[v].set(allValues.get(v));
        }
    }

    /**
     * Default propagation on kernel modification: propagation on adding a value to the kernel.
     */
    public void awakeOnKer(int varIdx, int x) throws ContradictionException {
        if (varIdx > UNION_SET_INDEX) 
            unionSet.addToKernel(x, cIndices[UNION_SET_INDEX]);
        else //x has been add to the unionSet kernel
            instanciateIfLastOccurence(x);
    }
    
    /**
     * Default propagation on enveloppe modification: propagation on removing a value from the enveloppe.
     */
    public void awakeOnEnv(int varIdx, int x) throws ContradictionException {
        if (varIdx == UNION_SET_INDEX) 
            for (int idx = 0; idx < setVars.length; idx++) 
                setVars[idx].remFromEnveloppe(x, cIndices[idx]);
        else 
            decOccurence(x);
    }
    /**
     * Default propagation on instantiation.
     */
    public void awakeOnInst(int varIdx) throws ContradictionException { //FIXME
        if (varIdx == UNION_SET_INDEX) {
            DisposableIntIterator it = unionSet.getDomain().getKernelIterator();
            while (it.hasNext()) {
                int val = it.next();
                instanciateIfLastOccurence(val);
            }
        } else {
            DisposableIntIterator it1 = vars[varIdx].getDomain().getKernelIterator();
            while (it1.hasNext()) {
                int val = it1.next();
                unionSet.addToKernel(val, cIndices[UNION_SET_INDEX]);
            }
            DisposableIntIterator it4 = vars[varIdx].getDomain().getEnveloppeIterator();
            while (it4.hasNext()) {
                int val = it4.next();
                if (getNbOccurence(val) == 0) unionSet.remFromEnveloppe(val, cIndices[UNION_SET_INDEX]);
            }
        }
    }

    public void propagate() throws ContradictionException {
        for (int idx = 0; idx < setVars.length; idx++) {
            DisposableIntIterator it = setVars[idx].getDomain().getKernelIterator();
            while (it.hasNext()) {
                int val = it.next();
                unionSet.addToKernel(val, cIndices[UNION_SET_INDEX]);
            }
        }
        
        DisposableIntIterator it2 = unionSet.getDomain().getKernelIterator();
        while (it2.hasNext()) {
            int val = it2.next();
            instanciateIfLastOccurence(val);
        }

        DisposableIntIterator it4 = unionSet.getDomain().getEnveloppeIterator();
        while (it4.hasNext()) {
            int val = it4.next();
            if (getNbOccurence(val) == 0) unionSet.remFromEnveloppe(val, cIndices[UNION_SET_INDEX]);
        }
    }
    
    public boolean isSatisfied() {
        for (int idx = 0; idx < setVars.length; idx++) {
            DisposableIntIterator it = setVars[idx].getDomain().getKernelIterator();
            while (it.hasNext()) {
                int val = it.next();
                if(!unionSet.isInDomainKernel(val)) return false;
            }
        }
        DisposableIntIterator it1 = unionSet.getDomain().getKernelIterator();
        while (it1.hasNext()) {
            int val = it1.next();
            boolean isInASet = false;
            for (int idx = 0; idx < setVars.length; idx++)
                if (setVars[idx].isInDomainKernel(val)) {isInASet = true; break;}
            if (!isInASet) return false;
        }
        return true;
    }

    public boolean isConsistent() {
        return isSatisfied();
    }
    
    private int getNbOccurence(int x) {
        return occurCpt[x].get();
    }
    private void decOccurence(int x) throws ContradictionException {
        occurCpt[x].add(-1);
        instanciateIfLastOccurence(x);
    }
    private void instanciateIfLastOccurence(int x) throws ContradictionException {
        if (occurCpt[x].get()<=1 && unionSet.isInDomainKernel(x)) {
            if (occurCpt[x].get()<=0) {System.out.println();fail();}
            int removed=0;
            for (int idx = 0; idx < setVars.length; idx++) {
                if(setVars[idx].isInDomainEnveloppe(x)) {
                    removed ++; 
                    setVars[idx].addToKernel(x, cIndices[idx]);
                    }
            }
            assert (removed == 1);
        }
    }
    

    
    public String pretty() {
        StringBuilder sb = new StringBuilder();
        sb.append("Union({");
        for (int i = 0; i < setVars.length; i++) {
            if (i > 0) sb.append(", ");
            SetVar var = setVars[i];
            sb.append(var.pretty());
        }
        sb.append("}) = "+unionSet.pretty());
        return sb.toString();
    }


    public String toString() {
        String autstring = "Union : ";
        for (int i = 0; i < vars.length; i++) {
            autstring += vars[i] + " ";
        }
        autstring += unionSet;
        return autstring;
    }
    
}
