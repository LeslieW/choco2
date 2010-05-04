package choco.cp.solver.configure;

import static choco.kernel.solver.Configuration.*;
import static choco.kernel.solver.Configuration.MINIMIZE;
import choco.kernel.solver.Configuration;
import choco.kernel.solver.Solver;
import choco.kernel.solver.search.limit.Limit;

public final class MessageFactory {

	private MessageFactory() {
		super();
	}
	public String getGeneralMsg(Solver solver) {
		return getGeneralMsg(solver.getConfiguration(), "");
	}

	public static String getGeneralMsg(Configuration conf, String name) {
		final StringBuilder b = new StringBuilder();
		if( conf.readBoolean(MAXIMIZE) ) b.append("MAXIMIZE    ");	
		else if( conf.readBoolean(MINIMIZE) ) b.append("MINIMIZE    ");
		else b.append("CSP    ");
		if( conf.readBoolean(STOP_AT_FIRST_SOLUTION) ) b.append("FIRST_SOLUTION    ");
		b.append(name).append("    ");
		b.append(conf.readString(RANDOM_SEED)).append(" SEED");
		return b.toString();
	}

	private static String getLimitMsg(Configuration conf, String name, String key, String boundKey) {
		final Limit lim = conf.readEnum(key, Limit.class);
		if( ! lim.equals(Limit.UNDEF) ) {
			return name+ " "+ conf.readString(boundKey)+" "+lim.getUnit()+"    ";
		} else return "";
	}
	public static String getLimitMsg(Solver solver) {
		final Configuration conf = solver.getConfiguration();
		return getLimitMsg(conf, "SEARCH_LIMIT", SEARCH_LIMIT, SEARCH_LIMIT_BOUND) + 
		getLimitMsg(conf, "RESTART_LIMIT", SEARCH_LIMIT, SEARCH_LIMIT_BOUND);
	}

	private static String getPolicyMsg(Configuration conf, String name, String growKey) {
		return name+"( "+conf.readString(RESTART_BASE)+", "+conf.readString(growKey)+" )    ";
	}

	public static String getRestartMsg(Solver solver) {
		final StringBuilder b = new StringBuilder();
		final Configuration conf = solver.getConfiguration();
		if( conf.readBoolean(RESTART_LUBY)) {
			b.append(getPolicyMsg(conf, "LUBY", RESTART_LUBY_GROW));
		} else if(conf.readBoolean(RESTART_GEOMETRICAL) ) {
			b.append(getPolicyMsg(conf, "GEOM", RESTART_GEOM_GROW));
		}
		if(conf.readBoolean( RESTART_AFTER_SOLUTION)) b.append("FROM_SOLUTION    ");
		if( conf.readBoolean(NOGOOD_RECORDING_FROM_RESTART) ) b.append("NOGOOD_RECORDING");
		return null;
	}

	public static String getShavingMsg(Solver solver) {
		final StringBuilder b = new StringBuilder();
		final Configuration conf = solver.getConfiguration();
		if(conf.readBoolean(INIT_SHAVING)) {b.append("SHAVING    ");}
		if(conf.readBoolean(INIT_SHAVING)) {
			b.append("DESTRUCTIVE_LOWER_BOUND");
			if(conf.readBoolean(INIT_DLB_SHAVING)) {b.append("_WITH_SHAVING");}
		}
		if(b.length() > 0) b.insert(0,"INITIAL_PROPAGATION    ");
		return "";
	}


}
