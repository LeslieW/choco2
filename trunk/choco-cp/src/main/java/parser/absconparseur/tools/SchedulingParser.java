package parser.absconparseur.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.w3c.dom.Element;

import parser.absconparseur.InstanceTokens;
import parser.absconparseur.components.PConstraint;
import parser.absconparseur.components.PDisjunctive;
import parser.absconparseur.components.PTask;
import parser.absconparseur.components.PVariable;

public class SchedulingParser extends InstanceParser {

	@Override
	protected PConstraint parseDisjunctiveConstraint(String name,
			PVariable[] scope, Element parameters) {
		StringTokenizer st = new StringTokenizer(buildStringRepresentationOf(parameters), InstanceTokens.WHITE_SPACE + "{}");
		st.nextToken(); // token '[' skipped
		List<PTask> tasks = new ArrayList<PTask>();
		String token = st.nextToken();
		while (!token.equals("]")) {
			Object origin = getMapOfVariables().get(token);
			if (origin == null)
				origin = token.equals(InstanceTokens.NIL) ? null : Integer.parseInt(token);
			token = st.nextToken();
			Object duration = getMapOfVariables().get(token);
			if (duration == null)
				duration = token.equals(InstanceTokens.NIL) ? null : Integer.parseInt(token);
			tasks.add(new PTask(origin, duration, null, 1));
			token = st.nextToken();
		}
		return new PDisjunctive(name, scope, tasks.toArray(new PTask[tasks.size()]));
	}

	
}
