package choco.kernel.model.variables.geost;

import choco.kernel.common.util.tools.ArrayUtils;
import choco.kernel.model.variables.MultipleVariables;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.integer.IntegerVariable;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 4 juil. 2008
 * Time: 16:31:57
 * Class to define in an easy way objects for the Geost Constraint.
 * This is a model object that is readable by the solver to create a Geost Constraint.
 * 
 */
public class GeostObject extends MultipleVariables{

    private final int dim;
    private final int objectId;
    private final IntegerVariable shapeId;
    private final IntegerVariable[] coordinates;
    private final IntegerVariable startTime;
    private final IntegerVariable durationTime;
    private final IntegerVariable endTime;
    private final int radius;

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
    public GeostObject(int dim, int objectId, IntegerVariable shapeId, IntegerVariable[] coordinates, IntegerVariable startTime, IntegerVariable durationTime, IntegerVariable endTime) {
       this(dim, objectId, shapeId, coordinates, startTime, durationTime, endTime, -1);
    }

    public GeostObject(int dim, int objectId, IntegerVariable shapeId, IntegerVariable[] coordinates, IntegerVariable startTime, IntegerVariable durationTime, IntegerVariable endTime, int radius) {
    	 this.dim = dim;
         this.objectId = objectId;
         this.shapeId = shapeId;
         this.coordinates = coordinates;
         this.startTime = startTime;
         this.durationTime = durationTime;
         this.endTime = endTime;
         this.radius=radius;
         Variable[] vars = new Variable[coordinates.length + 3];
         vars[0] = shapeId;
         System.arraycopy(coordinates, 0, vars, 1, coordinates.length);
         vars[vars.length-3]=startTime;
         vars[vars.length-2]=durationTime;
         vars[vars.length-1]=endTime;
         setVariables(vars);
    }


    public int getDim() {
        return dim;
    }

    public int getObjectId() {
        return objectId;
    }

    public IntegerVariable getShapeId() {
        return shapeId;
    }

    public IntegerVariable[] getCoordinates() {
        return coordinates;
    }

    public IntegerVariable getStartTime() {
        return startTime;
    }

    public IntegerVariable getDurationTime() {
        return durationTime;
    }

    public IntegerVariable getEndTime() {
        return endTime;
    }

    public int getRadius() {
        return radius;
    }

    public boolean isSphere() {
        return (radius!=-1);
    }
}
