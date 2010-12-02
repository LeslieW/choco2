package choco.kernel.model.constraints;

import choco.kernel.model.variables.scheduling.ITaskVariable;

public interface ITemporalRelation<T extends ITaskVariable<V>, V> { //extends IDotty { 

	T getOrigin();

	T getDestination();

	V getDirection();
	
	boolean IsFixed();

	boolean canBeBackward();

	int backwardSetup();
	
	boolean canBeForward();
	
	int forwardSetup();

	
}