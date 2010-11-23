package choco.cp.common.util.preprocessor.detector.scheduling;

import java.util.Iterator;

import choco.Choco;
import choco.Options;
import choco.cp.common.util.preprocessor.AbstractDetector;
import choco.cp.model.CPModel;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.constraints.ConstraintType;
import choco.kernel.model.constraints.TemporalConstraint;


abstract class AbstractSchedulingDetector extends AbstractDetector {

	public final DisjunctiveModel disjMod;

	public AbstractSchedulingDetector(CPModel model, DisjunctiveModel disjMod) {
		super(model);
		this.disjMod = disjMod;
	}

	public final DisjunctiveModel getDisjunctiveModel() {
		return disjMod;
	}

	protected abstract ConstraintType getType();


	protected abstract void setUp();

	protected boolean isInPreprocess(Constraint c) {
		return ! c.getOptions().contains(Options.C_NO_DETECTION);
	}

	protected abstract void apply(Constraint ct);

	protected abstract void tearDown();

	@Override
	public final void apply() {
		setUp();
		final Iterator<Constraint> iter = model.getConstraintByType(getType());
		while(iter.hasNext()) {
			final Constraint c = iter.next();
			if( isInPreprocess(c)) apply(c);
		}
		tearDown();
	}

}

public abstract class AbstractTemporalDetector extends AbstractSchedulingDetector {


	public AbstractTemporalDetector(CPModel model, DisjunctiveModel disjMod) {
		super(model, disjMod);
	}

	@Override
	protected boolean isInPreprocess(Constraint c) {
		if( super.isInPreprocess(c) ) {
			if (c instanceof TemporalConstraint) {
				TemporalConstraint ct = (TemporalConstraint) c;
				return ct.isInPreprocess();
			}
		}
		return false;	
	}



	@Override
	protected final void setUp() {}



	@Override
	protected final void tearDown() {}

	
	@Override
	protected final void apply(Constraint c) {
		apply( (TemporalConstraint) c);
	}

	protected abstract void apply(TemporalConstraint ct);

	protected final void reformulateImpliedReified(TemporalConstraint ct) {
		assert ct.getConstraintType() == ConstraintType.PRECEDENCE_IMPLIED || ct.getConstraintType() == ConstraintType.PRECEDENCE_REIFIED;
		if(disjMod.containsArc(ct.getOrigin(), ct.getDestination())) {
			delete(ct);
			replaceBy(ct.getDirection(), Choco.ONE);
			if(ct.forwardSetup() > disjMod.setupTime(ct.getOrigin(), ct.getDestination())) {
				add(Choco.precedence(ct.getOrigin(), ct.getDestination(), ct.forwardSetup()));
			}
		} else if(disjMod.containsArc(ct.getDestination(), ct.getOrigin())) {
			delete(ct);
			replaceBy(ct.getDirection(), Choco.ZERO);
		}
	}

}
