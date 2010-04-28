package trace;

import org.aspectj.lang.JoinPoint;

import choco.kernel.memory.IEnvironment;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntVar;
import choco.kernel.solver.variables.integer.IntDomainVar;
import choco.kernel.solver.constraints.SConstraint;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.propagation.AbstractPropagationEngine;
import choco.kernel.common.util.iterators.DisposableIntIterator;
import choco.kernel.solver.search.IntBranchingTrace;

public aspect CPLogAspect extends ALogAspect{

	// Previous domain
	protected int[]  previousDomain;
	
	/*************************************************/
	/** 											**/
	/** 				POINTCUTS 					**/
	/** 											**/
	/*************************************************/
	
	// Choice point on world push
	//pointcut choicePoint() : execution(public void IEnvironment.worldPush());
	
	// Choice point on world push
	pointcut setIntVal(IntBranchingTrace ibt) : 
		execution(public void IntBranchingTrace.setIntVal()) && 
		target(ibt);
	
	// Choice point on world push
	pointcut remIntVal(IntBranchingTrace ibt) : 
		execution(public void IntBranchingTrace.remIntVal()) && 
		target(ibt);
	
	// Back track on world pop
	pointcut backTo() : execution(public void IEnvironment.worldPop());
	
	// Contradiction
	pointcut failure() : 
		execution(* AbstractPropagationEngine+.raiseContradiction(..)) 
		&& !cflow(anyUpdate(IntVar));
	
	// Any modification
	pointcut anyUpdate(IntDomainVar var) : target(var) && (execution(* IntVar+.update*(..)) || 
		execution(* IntVar+.remove*(..)) || execution(* IntVar+.instantiate(..)));
	
	// Instantiation
	pointcut instantiate(SConstraint cst, IntDomainVar var):  
		execution(* AbstractPropagationEngine+.postInstInt(..)) && args(var, *) &&
		cflow(anyAwake() && target(cst));
	pointcut instantiateC(IntDomainVar var):
		execution(* AbstractPropagationEngine+.postInstInt(..)) && args(var, *) &&
		!cflow(anyAwake());
	
	// Bound updates
	pointcut updateInf(SConstraint cst, IntDomainVar var):
		execution(* AbstractPropagationEngine+.postUpdateInf(..)) && args(var, *) &&
		cflow(anyAwake() && target(cst));
	
	pointcut updateSup(SConstraint cst, IntDomainVar var):
		execution(* AbstractPropagationEngine+.postUpdateSup(..)) && args(var, *) &&
		cflow(anyAwake() && target(cst));
	
	pointcut updateInfC(IntDomainVar var):
		execution(* AbstractPropagationEngine+.postUpdateInf(..)) && args(var, *) &&
		!cflow(anyAwake());
	
	pointcut updateSupC(IntDomainVar var):
		execution(* AbstractPropagationEngine+.postUpdateSup(..)) && args(var, *) &&
		!cflow(anyAwake());
	
	// Remove Val
	pointcut removeVal(SConstraint cst, IntDomainVar var, int val):
		execution(* AbstractPropagationEngine+.postRemoveVal(..)) && args(var, val, *) &&
		cflow(anyAwake() && target(cst));
	
	pointcut removeValC(IntDomainVar var, int val):
		execution(* AbstractPropagationEngine+.postRemoveVal(..)) && args(var, val, *) &&
		!cflow(anyAwake());
	
	/*************************************************/
	/** 											**/
	/** 				ADVICES 					**/
	/** 											**/
	/*************************************************/
	
	// Choice point and back track
	//before() : choicePoint() {
	//	singleElementLn("<choice-point " + eventAttributes(thisJoinPointStaticPart) + "/>");
	//}
	
	// Choice point and back track
	//before() : choicePoint() {
	before(IntBranchingTrace ibt) : setIntVal(ibt) {//
		IntDomainVar var = ibt.getBranchingIntVar();
		int val = ibt.getBranchingValue();
		elementOpening("<choice-point " + eventAttributes(thisJoinPointStaticPart) + ">");	
		singleElementLn("<choice-constraint vident=\"v"+var.varNumber+"\" constraints=\":=\" value=\""+val+"\" />");
		elementClosing("</choice-point>");
		stream.flush();
	}
	
	before(IntBranchingTrace ibt) : remIntVal(ibt) {
		//singleElementLn("<choice-point " + eventAttributes(thisJoinPointStaticPart) + "/>");
		IntDomainVar var = ibt.getBranchingIntVar();
		int val = ibt.getBranchingValue();
		elementOpening("<choice-point " + eventAttributes(thisJoinPointStaticPart) + ">");	
		singleElementLn("<choice-constraint vident="+var.varNumber+" constraints=\"!=\" value="+val+" />");
		elementClosing("</choice-point>");
		System.err.println("AAAAAAAAAAAAAAAAAAAAA");
		stream.flush();
	}
	
	after() : backTo() {
		singleElementLn("<back-to " + eventAttributes(thisJoinPointStaticPart) + "/>");
	}
	
	// Failures (except empty domains)
	before() : failure() {
		singleElementLn("<failure " + eventAttributes(thisJoinPointStaticPart) + "/>");
	}
	
	// Stores domain before update
	before(IntDomainVar var) : anyUpdate(var) {
		if (var.hasEnumeratedDomain()) {
			previousDomain = new int[var.getDomainSize()];
			int i = 0;
			DisposableIntIterator iterator = var.getDomain().getIterator();
			for(; iterator.hasNext();) {
				previousDomain[i++] = iterator.next();
			}
			iterator.dispose();
		} else {
			previousDomain = new int[]{var.getInf(), var.getSup()};
		}
	}
	
	// Empty domains
	after(IntDomainVar var) throwing(ContradictionException e): anyUpdate(var) {
		elementOpening("<reduce " + eventAttributes(thisJoinPointStaticPart) + 
				" vident=\"v" + var.varNumber + "\">");
		elementOpening("<delta>");
		if (var.hasEnumeratedDomain()) {
			singleElement("<values>");
			for(int i = 0; i < previousDomain.length; i++) {
				stream.print(previousDomain[i] + " ");
			}
			stream.println("</values>");
		} else {
			singleElementLn("<range from=\"" + previousDomain[0] + "\" to=\"" +
					previousDomain[1] + "\"/>");
		}
		elementClosing("</delta>");
		singleElementLn("<update vident=\"v" + var.varNumber + "\" types=\"empty\"/>");
		elementClosing("</reduce>");
		singleElementLn("<failure " + eventAttributes(thisJoinPointStaticPart) + "/>");
	}
	
	// Instantiation
	before(SConstraint cst, IntDomainVar var): instantiate(cst, var) {
		instantiateHandle(cst, var, thisJoinPointStaticPart);
	}
	before(IntDomainVar var): instantiateC(var) {
		instantiateHandle(null, var, thisJoinPointStaticPart);
	}
	
	// Bound updates
	before(SConstraint cst, IntDomainVar var): updateInf(cst, var) {
		boundHandle(cst, var, 0, thisJoinPointStaticPart);
	}
	before(SConstraint cst, IntDomainVar var): updateSup(cst, var) {
		boundHandle(cst, var, 1, thisJoinPointStaticPart);
	}
	before(IntDomainVar var): updateInfC(var) {  // Really useful ?????
		boundHandle(null, var, 0, thisJoinPointStaticPart);
	}
	before(IntDomainVar var): updateSupC(var) {  // Really useful ?????
		boundHandle(null, var, 1, thisJoinPointStaticPart);
	}
	
	// Value removals
	before(SConstraint cst, IntDomainVar var, int val): removeVal(cst, var, val) {
		removalHandle(cst, var, val, thisJoinPointStaticPart);
	}
	before(IntDomainVar var, int val): removeValC(var, val) {  // Really useful ?????
		removalHandle(null, var, val, thisJoinPointStaticPart);
	}
	
	/*************************************************/
	/** 											**/
	/** 				TOOLS	 					**/
	/** 											**/
	/*************************************************/
	
	protected String solverName() {
		return "Choco";
	}
	
	protected int getCurrentDepth() {
		return currentSolver.getWorldIndex();
	}
	
	protected void instantiateHandle(SConstraint cst, IntDomainVar var, JoinPoint.StaticPart jp) {
		elementOpening("<reduce " + eventAttributes(jp) +
				(cst != null?" cident=\"c" + cst.constNumber + "\"":"") + 
				 " vident=\"v" + var.varNumber + "\">");
		elementOpening("<delta>");
		if (var.hasEnumeratedDomain()) {
			singleElement("<values>");
			for(int i = 0; i < previousDomain.length; i++) {
				if (previousDomain[i] != var.getValue())
					stream.print(previousDomain[i] + " ");
			}
			stream.println("</values>");
		} else {
			if (previousDomain[0] < var.getInf())
				singleElementLn("<range from=\"" + previousDomain[0] + "\" to=\"" +
					(var.getInf() - 1) + "\"/>");
			if (previousDomain[1] > var.getSup())
				singleElementLn("<range from=\"" + (var.getSup() + 1) + "\" to=\"" +
				    previousDomain[1] + "\"/>");
		}
		elementClosing("</delta>");
		singleElementLn("<update vident=\"v" + var.varNumber + "\" types=\"ground\"/>");
		elementClosing("</reduce>");
	}
	
	protected void boundHandle(SConstraint cst, IntDomainVar var, int bound, JoinPoint.StaticPart jp) {
		elementOpening("<reduce " + eventAttributes(jp) +
				(cst != null?" cident=\"c" + cst.constNumber + "\"":"") + 
				 " vident=\"v" + var.varNumber + "\">");
		elementOpening("<delta>");
		if (var.hasEnumeratedDomain()) {
			singleElement("<values>");
			if (bound == 0) {
				int i = 0;
				while (i < previousDomain.length && previousDomain[i] < var.getInf()) {
					stream.print(previousDomain[i] + " ");
					i++;
				}
			} else {
				int i = 0;
				while (i < previousDomain.length && previousDomain[i] <= var.getSup()) {
					i++;
				}
				while (i < previousDomain.length) {
					stream.print(previousDomain[i] + " ");
					i++;
				}
			}
			stream.println("</values>");
		} else {
			if (bound == 0) {
				singleElementLn("<range from=\"" + previousDomain[0] + "\" to=\"" +
						(var.getInf() - 1) + "\"/>");
			} else {
				singleElementLn("<range from=\"" + (var.getSup() + 1) + "\" to=\"" +
						previousDomain[1] + "\"/>");
			}
		}
		elementClosing("</delta>");
		singleElementLn("<update vident=\"v" + var.varNumber + 
				"\" types=\"" + (bound == 0?"min":"max") + "\"/>");
		elementClosing("</reduce>");
	}
	
	protected void removalHandle(SConstraint cst, IntDomainVar var, int val, JoinPoint.StaticPart jp) {
		elementOpening("<reduce " + eventAttributes(jp) + 
			(cst != null?" cident=\"c" + cst.constNumber + "\"":"") + ">");
		singleElementLn("<delta><values>"+val+"</values></delta>");
		singleElementLn("<update  vident=\"v" + var.varNumber + "\" types=\"val\"/>");
		elementClosing("</reduce>");
		stream.flush();
	}
	
}
