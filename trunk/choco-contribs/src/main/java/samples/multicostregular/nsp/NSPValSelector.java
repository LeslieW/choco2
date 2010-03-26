package samples.multicostregular.nsp;

import choco.kernel.solver.search.AbstractSearchHeuristic;
import choco.kernel.solver.search.integer.ValSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Dec 9, 2008
 * Time: 12:57:27 AM
 */
public class NSPValSelector implements ValSelector {

    NSPStruct struct;
    NSPVarSelector varselec;
    public NSPValSelector(NSPVarSelector varsel)
    {
        this.struct = varsel.struct;
        this.varselec = varsel;

    }

    public int getBestVal(IntDomainVar x) {
        int[] pos = varselec.map.get(x);



        int idx = pos[0]*struct.instance.nbDays+pos[1];

        int val = -1;
        int dayIndex = idx%struct.instance.nbDays;
        int max = Integer.MIN_VALUE/100;
        for (int i = 0 ; i < struct.instance.nbShifts ; i++)
        {
            int tmp =struct.need[dayIndex][i].get();
            {
                if (max < tmp)
                {
                    max = tmp;
                    val = i;

                }
            }

        }
        if (max > 0&& x.canBeInstantiatedTo(val))
            return val;
        else return x.getSup();

    }
}
