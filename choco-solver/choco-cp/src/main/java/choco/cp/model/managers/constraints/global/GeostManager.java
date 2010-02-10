package choco.cp.model.managers.constraints.global;

import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.Geost_Constraint;
import choco.cp.solver.constraints.global.geost.externalConstraints.*;
import choco.cp.solver.constraints.global.geost.geometricPrim.Obj;
import choco.kernel.model.ModelException;
import choco.kernel.model.constraints.geost.GeostOptions;
import choco.kernel.model.constraints.geost.externalConstraints.*;
import choco.kernel.model.variables.geost.GeostObject;
import choco.kernel.model.variables.geost.ShiftedBox;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;

import java.util.Set;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: charles
 * Date: 8 août 2008
 * Time: 19:38:51
 */
public class GeostManager extends IntConstraintManager {

    /**
     * Build a constraint for the given solver and "model variables"
     *
     * @param solver
     * @param variables
     * @param parameters : a "hook" to attach any kind of parameters to constraints
     * @param options
     * @return
     */
    public SConstraint makeConstraint(Solver solver, IntegerVariable[] variables/*variables model*/, Object parameters, Set<String> options) {
        if (solver instanceof CPSolver) {
            if(parameters instanceof Object[]){
                Object[] params = (Object[])parameters;
                int dim = (Integer)params[0];
                Vector<ShiftedBox> shiftedBoxes = (Vector<ShiftedBox>)params[1];
                Vector<IExternalConstraint> ectr = (Vector<IExternalConstraint>)params[2];
                Vector<GeostObject> vgo = (Vector<GeostObject>)params[3];
                Vector<int[]> ctrlVs = (Vector<int[]>)params[4];
                GeostOptions opt = (GeostOptions) params[5];
                if (opt==null) { opt=new GeostOptions(); }

                //Transformation of Geost Objects (model) to interval geost object (constraint)
                Vector<Obj> vo = new Vector<Obj>(vgo.size());
                for (int i = 0; i < vgo.size(); i++) {
                    GeostObject g = vgo.elementAt(i);
                    vo.add(i, new Obj(g.getDim(),
                            g.getObjectId(),
                            solver.getVar(g.getShapeId()),
                            solver.getVar(g.getCoordinates()),
                            solver.getVar(g.getStartTime()),
                            solver.getVar(g.getDurationTime()),
                            solver.getVar(g.getEndTime()),
                            g.getRadius())
                            );
                }

                Vector<ExternalConstraint> ectrs = new Vector<ExternalConstraint>();

                for (IExternalConstraint iectr : ectr) {
                    
                    if (iectr instanceof DistLeqModel) {
                        DistLeqModel dlm = (DistLeqModel) iectr;
                        if (dlm.hasDistanceVar()) 
                            ectrs.add(new DistLeq(dlm.getEctrID(), dlm.getDim(), dlm.getObjectIds(), dlm.D, dlm.q, solver.getVar(dlm.getDistanceVar() )));
                        else
                            ectrs.add(new DistLeq(dlm.getEctrID(), dlm.getDim(), dlm.getObjectIds(), dlm.D, dlm.q));
                    }

                    if (iectr instanceof DistGeqModel) {
                        DistGeqModel dgm = (DistGeqModel) iectr;
                        if (dgm.hasDistanceVar())
                            ectrs.add(new DistGeq(dgm.getEctrID(), dgm.getDim(), dgm.getObjectIds(), dgm.D, dgm.q, solver.getVar(dgm.getDistanceVar() )));
                        else
                            ectrs.add(new DistGeq(dgm.getEctrID(), dgm.getDim(), dgm.getObjectIds(), dgm.D, dgm.q));
                    }

                    if (iectr instanceof NonOverlappingModel) {
                         NonOverlappingModel ctr = (NonOverlappingModel) iectr;
                         ectrs.add(new NonOverlapping(ctr.getEctrID(),ctr.getDim(),ctr.getObjectIds()));
                    }

                    if (iectr instanceof DistLinearModel) {
                         DistLinearModel ctr = (DistLinearModel) iectr;
                         ectrs.add(new DistLinear(ctr.getEctrID(),ctr.getDim(),ctr.getObjectIds(), ctr.a, ctr.b));
                    }

                }


                if (ctrlVs == null) {
                    return new Geost_Constraint(solver.getVar((IntegerVariable[])variables)/*solver variables*/, dim, vo, shiftedBoxes, ectrs,false, opt.included);
                } else {
                    return new Geost_Constraint(solver.getVar((IntegerVariable[])variables), dim, vo, shiftedBoxes, ectrs, ctrlVs,opt.memoisation, opt.included,opt.increment);
                }
                        }
                    }
       throw new ModelException("Could not found a constraint manager in " + this.getClass() + " !");
    }

}
