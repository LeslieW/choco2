package choco.kernel.model;

import choco.kernel.model.constraints.Constraint;

import java.io.Serializable;
import java.util.Iterator;

public interface IConstraintList extends Serializable{

	public void _addConstraint(Constraint c);

	public void _removeConstraint(Constraint c);

    public boolean _contains(Constraint c);

	public Constraint getConstraint(int i);
	
	public Iterator<Constraint> getConstraintIterator(Model m);

	public int getNbConstraint(Model m);

	public Constraint[] getConstraints();
	
	void removeConstraints();
}