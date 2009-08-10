package choco.cp.model.managers.constraints.global;

import choco.Choco;
import choco.cp.model.managers.IntConstraintManager;
import choco.cp.solver.CPSolver;
import choco.cp.solver.constraints.global.Geost_Constraint;
import choco.cp.solver.constraints.global.geost.externalConstraints.DistLeq;
import choco.cp.solver.constraints.global.geost.externalConstraints.ExternalConstraint;
import choco.cp.solver.constraints.global.geost.externalConstraints.NonOverlapping;
import choco.cp.solver.constraints.global.geost.geometricPrim.Obj;
import choco.kernel.common.util.objects.Pair;
import choco.kernel.model.constraints.geost.externalConstraints.DistGeqModel;
import choco.kernel.model.constraints.geost.externalConstraints.DistLeqModel;
import choco.kernel.model.constraints.geost.externalConstraints.IExternalConstraint;
import choco.kernel.model.constraints.geost.externalConstraints.NonOverlappingModel;
import choco.kernel.model.variables.Variable;
import choco.kernel.model.variables.geost.GeostObject;
import choco.kernel.model.variables.geost.ShiftedBox;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.constraints.SConstraint;

import java.util.HashMap;
import java.util.HashSet;
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
    public SConstraint makeConstraint(Solver solver, Variable[] variables/*variables model*/, Object parameters, HashSet<String> options) {
        if (solver instanceof CPSolver) {
            if(parameters instanceof Object[]){
                Object[] params = (Object[])parameters;
                int dim = (Integer)params[0];
                Vector<ShiftedBox> shiftedBoxes = (Vector<ShiftedBox>)params[1];
                Vector<IExternalConstraint> ectr = (Vector<IExternalConstraint>)params[2];
                Vector<GeostObject> vgo = (Vector<GeostObject>)params[3];
                Vector<int[]> ctrlVs = (Vector<int[]>)params[4];
                
                boolean memo_active = ((Vector<Boolean>)params[5]).get(0);
                HashMap<Pair<Integer,Integer>, Boolean>  included = (HashMap<Pair<Integer,Integer>, Boolean>)params[6];
                Long a = (Long) params[7];
                Long b = (Long) params[8];
                boolean increment = ((Vector<Boolean>)params[9]).get(0);

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
                            ectrs.add(new DistLeq(dgm.getEctrID(), dgm.getDim(), dgm.getObjectIds(), dgm.D, dgm.q, solver.getVar(dgm.getDistanceVar() )));
                        else
                            ectrs.add(new DistLeq(dgm.getEctrID(), dgm.getDim(), dgm.getObjectIds(), dgm.D, dgm.q));
                    }

                    if (iectr instanceof NonOverlappingModel) {
                         NonOverlappingModel ctr = (NonOverlappingModel) iectr;
                         ectrs.add(new NonOverlapping(ctr.getEctrID(),ctr.getDim(),ctr.getObjectIds()));
                    }

                }


                if (ctrlVs == null) {
                    return new Geost_Constraint(solver.getVar((IntegerVariable[])variables)/*solver variables*/, dim, vo, shiftedBoxes, ectrs,false, included,a,b);
                } else {
                    return new Geost_Constraint(solver.getVar((IntegerVariable[])variables), dim, vo, shiftedBoxes, ectrs, ctrlVs,memo_active, included,a,b,increment);
                }
                        }
                    }
        if (Choco.DEBUG) {
            System.err.println("Could not found implementation for Geost !");
        }
        return null;
    }

}
