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
package choco.cp.common.util.detector;

import choco.cp.CPOptions;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.model.variables.scheduling.TaskVariable;
import gnu.trove.THashSet;

import java.util.Set;

/**
 * User : cprudhom<br/>
 * Mail : cprudhom(a)emn.fr<br/>
 * Date : 1 avr. 2010br/>
 * Since : Choco 2.1.1<br/>
 */
public class TaskMerger {

    public IntegerVariable start;
        public IntegerVariable duration;
        public IntegerVariable end;

        private Set<String> optionsSet;

        public TaskMerger() {
            optionsSet = new THashSet<String>();
        }

        public TaskMerger(final TaskVariable v) {
            this();
            start = v.start();
            duration = v.duration();
            end = v.end();
            optionsSet.addAll(v.getOptions());
        }

        public void merge(final TaskVariable d){
            if(start  == null){
                start = d.start();
            }
            if(duration == null){
                duration = d.duration();
            }
            if(end == null){
                end = d.end();
            }
            final THashSet<String> toptionsSet = new THashSet<String>();
            if(d.getOptions().contains(CPOptions.V_DECISION)
                    || optionsSet.contains(CPOptions.V_DECISION)){
                toptionsSet.add(CPOptions.V_DECISION);
            }
            if(d.getOptions().contains(CPOptions.V_NO_DECISION)
                    || optionsSet.contains(CPOptions.V_NO_DECISION)){
                toptionsSet.add(CPOptions.V_NO_DECISION);
            }
            if(d.getOptions().contains(CPOptions.V_OBJECTIVE)
                    || optionsSet.contains(CPOptions.V_OBJECTIVE)){
                toptionsSet.add(CPOptions.V_OBJECTIVE);
            }
            this.optionsSet = toptionsSet;
        }
}
