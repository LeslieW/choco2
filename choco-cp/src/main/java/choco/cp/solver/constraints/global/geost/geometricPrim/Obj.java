/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
 *         |_  J||L _|        CHOCO solver       *
 *                                               *
 *    Choco is a java library for constraint     *
 *    satisfaction problems (CSP), constraint    *
 *    programming (CP) and explanation-based     *
 *    constraint solving (e-CP). It is built     *
 *    on a event-based propagation mechanism     *
 *    with backtrackable structures.             *
 *                                               *
 *    Choco is an open-source software,          *
 *    distributed under a BSD licence            *
 *    and hosted by sourceforge.net              *
 *                                               *
 *    + website : http://choco.emn.fr            *
 *    + support : choco@emn.fr                   *
 *                                               *
 *    Copyright (C) F. Laburthe,                 *
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.constraints.global.geost.geometricPrim;

import choco.cp.solver.constraints.global.geost.externalConstraints.ExternalConstraint;
import choco.cp.solver.constraints.global.geost.internalConstraints.InternalConstraint;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.Vector;

/**
 * This class represent an Object of our placement problem.
 */
public class Obj {

	private int oid;          //Object id
	private IntDomainVar sid; // the shape id that corresponds to this object
	private IntDomainVar[] coords;
	private IntDomainVar start;
	private IntDomainVar duration;
	private IntDomainVar end;
	private Vector<ExternalConstraint> relatedExternalConstraints;
	private Vector<InternalConstraint> relatedInternalConstraints;
	private int dim;

	/**
	 * Creates an object with the given parameters
	 * @param dim An integer representing the dimension of the placement problem
	 * @param objectId An integer representing the object id
	 * @param shapeId  An Integer Domain Variable representing the possible shape ids the Object can have
	 * @param coordinates An array of size k of Integer Domain Variables (where k is the dimension of the space we are working in) representing the Domain of our object origin
	 * @param startTime An Integer Domain Variable representing the time that the object start in
	 * @param durationTime An Integer Domain Variable representing the duration
	 * @param endTime An Integer Domain Variable representing the time that the object ends in
	 */
	public Obj(int dim, int objectId, IntDomainVar shapeId, IntDomainVar[] coordinates, IntDomainVar startTime , IntDomainVar durationTime, IntDomainVar endTime)
	{
		this.dim = dim;
		this.oid = objectId;
		this.sid = shapeId;
		this.coords = coordinates;
		this.start = startTime;
		this.duration = durationTime;
		this.end = endTime;
		this.relatedExternalConstraints = new Vector<ExternalConstraint>();
		this.relatedInternalConstraints = new Vector<InternalConstraint>();
	}

	/**
	 * Creates an object in a certain given dimension
	 */
	public Obj(int dim)
	{
		this.dim = dim;
		this.coords = new IntDomainVar[this.dim];
		this.relatedExternalConstraints = new Vector<ExternalConstraint>();
		this.relatedInternalConstraints = new Vector<InternalConstraint>();
	}

	/**
	 * Gets the Object id
	 */
	public int getObjectId()
	{
		return this.oid;
	}

	/**
	 * Sets the Object id
	 */
	public void setObjectId(int objectId)
	{
		this.oid = objectId;
	}

	/**
	 * Gets the Shape id domain variable
	 */
	public IntDomainVar getShapeId()
	{
		return this.sid;
	}

	/**
	 * Sets the Shape id domain variable
	 */
	public void setShapeId(IntDomainVar shapeId)
	{
		this.sid = shapeId;
	}

	/**
	 * Gets all the coordinate domain variables of the object origin
	 */
	public IntDomainVar[] getCoordinates()
	{
		return this.coords;
	}

	/**
	 * Sets all the coordinate domain variables of the object origin to the ones given as parameter
	 */
	public void setCoordinates(IntDomainVar[] coordinates)
	{
		this.coords = coordinates;
	}

	/**
	 * Sets a coordinate domain variables of the object origin at the given dimension given by the parameter index, to another domain variable given by the parameter value.
	 */
	public void setCoord(int index, IntDomainVar value)
	{
		this.coords[index] = value;
	}

	/**
	 * Gets the index coordinate domain variable  of the object origin
	 */
	public IntDomainVar getCoord(int index)
	{
		return this.coords[index];
	}

	public IntDomainVar getDuration() {
		return duration;
	}

	public void setDuration(IntDomainVar duration) {
		this.duration = duration;
	}

	public IntDomainVar getEnd() {
		return end;
	}

	public void setEnd(IntDomainVar end) {
		this.end = end;
	}

	public IntDomainVar getStart() {
		return start;
	}

	public void setStart(IntDomainVar start) {
		this.start = start;
	}

	/**
	 * Gets all Related External Constraints to this object.
	 */
	public Vector<ExternalConstraint> getRelatedExternalConstraints() {
		return relatedExternalConstraints;
	}

	/**
	 * Gets all Related Internal Constraints to this object.
	 */
	public Vector<InternalConstraint> getRelatedInternalConstraints() {
		return relatedInternalConstraints;
	}


	/**
	 * Sets all Related External Constraints to this object.
	 */
	public void setRelatedExternalConstraints(Vector<ExternalConstraint> relatedExtConstraints) {
		this.relatedExternalConstraints = relatedExtConstraints;
	}

	/**
	 * Sets all Related Internal Constraints to this object.
	 */
	public void setRelatedInternalConstraints(Vector<InternalConstraint> relatedIntConstraints) {
		this.relatedInternalConstraints = relatedIntConstraints;
	}

	/**
	 * Adds a Related External Constraint to this object.
	 */
	public void addRelatedExternalConstraint(ExternalConstraint ectr)
	{
		this.relatedExternalConstraints.add(ectr);
	}

	/**
	 * Adds a Related Internal Constraint to this object.
	 */
	public void addRelatedInternalConstraint(InternalConstraint ictr)
	{
		this.relatedInternalConstraints.add(ictr);
	}




	/**
	 * Calculate the domain size (to check if we pruned the object at a certain iteration)
	 */
	public int calculateDomainSize()
	{
		int result = 0;
		for (int i = 0; i < this.coords.length; i++)
		{
			result = result + (this.getCoord(i).getSup() - this.getCoord(i).getInf()) + 1; // the coordinates are BoundIntVar
		}

		result = result + this.sid.getDomainSize();  // the shape is EnumIntVar

//      result = result + (this.start.getSup() - this.start.getInf()) + 1;
//      result = result + (this.duration.getSup() - this.duration.getSup()) + 1;
//      result = result + (this.end.getSup() - this.end.getInf()) + 1;

		return result;
	}



//	public void clearInternalConstraints()
//	{
//		this.relatedInternalConstraints.clear();
//	}

}
