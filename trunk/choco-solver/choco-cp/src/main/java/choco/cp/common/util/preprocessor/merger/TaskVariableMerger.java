/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |   (..)  |                           *
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
 *                  N. Jussien    1999-2010      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.common.util.preprocessor.merger;

import choco.Options;
import choco.kernel.common.util.tools.StringUtils;
import choco.kernel.model.variables.scheduling.TaskVariable;
import gnu.trove.THashSet;

import java.util.Set;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 1 avr. 2010br/>
 * Since : Choco 2.1.1<br/>
 */
public class TaskVariableMerger {

    public IntegerVariableMerger start;
    public IntegerVariableMerger duration;
    public IntegerVariableMerger end;

    private Set<String> optionsSet;

    public TaskVariableMerger() {
        optionsSet = new THashSet<String>();
    }

    public TaskVariableMerger(final TaskVariable v) {
        this();
        start = new IntegerVariableMerger(v.start());
        duration = new IntegerVariableMerger(v.duration());
        end = new IntegerVariableMerger(v.end());
        optionsSet.addAll(v.getOptions());
    }

    public void copy(final TaskVariableMerger toCopy) {
        this.start = toCopy.start;
        this.duration = toCopy.duration;
        this.end = toCopy.end;
        this.optionsSet = toCopy.optionsSet;
    }

    public TaskVariable create(){
        return new TaskVariable(StringUtils.randomName(), start.create(), duration.create(), end.create());
    }

    public boolean intersection(final TaskVariable d) {
        if (start == null) {
            start = new IntegerVariableMerger(d.start());
        } else if (!start.intersection(d.start())) {
            return false;
        }
        if (duration == null) {
            duration = new IntegerVariableMerger(d.duration());
        } else if (!duration.intersection(d.duration())) {
            return false;
        }
        if (end == null) {
            end = new IntegerVariableMerger(d.end());
        } else if (!end.intersection(d.end())) {
            return false;
        }
        final THashSet<String> toptionsSet = new THashSet<String>();
        if (d.getOptions().contains(Options.V_DECISION)
                || optionsSet.contains(Options.V_DECISION)) {
            toptionsSet.add(Options.V_DECISION);
        }
        if (d.getOptions().contains(Options.V_NO_DECISION)
                || optionsSet.contains(Options.V_NO_DECISION)) {
            toptionsSet.add(Options.V_NO_DECISION);
        }
        if (d.getOptions().contains(Options.V_OBJECTIVE)
                || optionsSet.contains(Options.V_OBJECTIVE)) {
            toptionsSet.add(Options.V_OBJECTIVE);
        }
        this.optionsSet = toptionsSet;
        return true;
    }
}
