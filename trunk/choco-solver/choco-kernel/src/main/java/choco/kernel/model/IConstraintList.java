package choco.kernel.model;

import java.util.Iterator;

import choco.kernel.model.constraints.Constraint;

public interface IConstraintList {
	
	public void _addConstraint(Constraint c);

	public void _removeConstraint(Constraint c);
	
	public Constraint getConstraint(int i);
	
	public Iterator<Constraint> getConstraintIterator(Model m);

	public int getNbConstraint(Model m);

	public Constraint[] getConstraints();
	
	void removeConstraints();
}