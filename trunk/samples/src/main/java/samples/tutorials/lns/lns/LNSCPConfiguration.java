package samples.tutorials.lns.lns;

import choco.kernel.solver.Configuration;
import parser.instances.BasicSettings;

/** @author Sophie Demassey */
public class LNSCPConfiguration extends Configuration {

@Default(value = "BACKTRACK")
public static final String LNS_INIT_SEARCH_LIMIT = "lns.initial.cp.search.limit.type";

@Default(value = "1000")
public static final String LNS_INIT_SEARCH_LIMIT_BOUND = "lns.initial.cp.search.limit.value";

@Default(value = "BACKTRACK")
public static final String LNS_NEIGHBORHOOD_SEARCH_LIMIT = "lns.neighborhood.cp.search.limit.type";

@Default(value = "1000")
public static final String LNS_NEIGHBORHOOD_SEARCH_LIMIT_BOUND = "lns.neighborhood.cp.search.limit.value";

@Default(value = "3")
public static final String LNS_RUN_LIMIT_NUMBER = "lns.run.limit.number";

public LNSCPConfiguration()
{
	super(new BasicSettings());
}

}
