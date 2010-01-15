package parser.instances;

import choco.cp.solver.CPSolver;
import choco.kernel.solver.SolverException;

import java.util.Properties;


public class RestartSettings extends BasicSettings {

	public static enum RestartPolicy {OFF, LUBY, GEOM, FIXED}
	
	/**
	 * the restart policy.
	 */
	public RestartPolicy restartPolicy = RestartPolicy.OFF;
	/**
	 * the scale factor of the restart policy. It multiplies the current cutoff.
	 */
	private int scaleFactor = 1;
	/**
	 * the geometrical factor of the luby restart policy. determines the growth of the cutoff.
	 */
	private int lubyGeometricalFactor = 2;
	/**
	 * the geometrical factor of the restart policy. determines the growth of the cutoff.
	 */
	private double walshGeometricalFactor = 1.2;
	/**
	 * indicates if nogood recording FROM RESTART is active
	 */
	public boolean nogoodRecording = false;

	public RestartSettings() {
		super();
	}

	public RestartSettings(RestartSettings set) {
		super(set);
		this.restartPolicy = set.restartPolicy;
        this.scaleFactor = set.scaleFactor;
		this.walshGeometricalFactor= set.walshGeometricalFactor;
        this.lubyGeometricalFactor= set.lubyGeometricalFactor;
        this.nogoodRecording = set.nogoodRecording;
	}

	public final RestartPolicy getRestartPolicy() {
		return restartPolicy;
	}

	public final void setRestartPolicy(RestartPolicy restartPolicy) {
		this.restartPolicy = restartPolicy;
	}

	public final double getScaleFactor() {
		return scaleFactor;
	}

	public final void setScaleFactor(int scaleFactor) {
		if(scaleFactor > 0) this.scaleFactor = scaleFactor;
	}

	public final int getLubyGeometricalFactor() {
		return lubyGeometricalFactor;
	}

	public final double getWalshGeometricalFactor() {
		return walshGeometricalFactor;
	}

	public final void setWalshGeometricalFactor(double geometricalFactor) {
		if( walshGeometricalFactor >= 1) this.walshGeometricalFactor = geometricalFactor;
	}

	public final void setLubyGeometricalFactor(int geometricalFactor) {
		if( lubyGeometricalFactor > 0) this.lubyGeometricalFactor = geometricalFactor;
	}

	public final boolean isNogoodRecording() {
		return nogoodRecording;
	}

	public final void setNogoodRecording(boolean nogoodRecording) {
		this.nogoodRecording = nogoodRecording;
	}
	
	public final void applyRestartPolicy(CPSolver s) {
		switch (restartPolicy) {
		case OFF: s.restartConfig.cancelRestarts();break;
		case FIXED: s.setGeometricRestart(scaleFactor,1);break;
		case GEOM: s.setGeometricRestart(scaleFactor, walshGeometricalFactor);break;
		case LUBY: s.setLubyRestart(scaleFactor, lubyGeometricalFactor);break;
		default:
			throw new SolverException("cant configure restart policy");
		}
		s.setRecordNogoodFromRestart(nogoodRecording);
		//by default, do not restart after each solution
		s.restartConfig.setInitializeSearchAfterRestart(false); 
	}

	@Override
	public void configure(Properties properties) {
		super.configure(properties);
		//final String s = getString(properties, PP+"restart.policy", null);
		//if ( s != null) restartPolicy = Enum.valueOf(RestartPolicy.class, s);
		restartPolicy = readEnum(properties, PP+"restart.policy", restartPolicy);
		scaleFactor = readInteger(properties, PP+"restart.scale", scaleFactor);
		lubyGeometricalFactor = readInteger(properties, PP+"restart.luby.geom", lubyGeometricalFactor);
		walshGeometricalFactor = readDouble(properties, PP+"restart.walsh.geom", walshGeometricalFactor);
		nogoodRecording = readBoolean(properties, PP+"restart.nogood", nogoodRecording);
	}
	
	

}