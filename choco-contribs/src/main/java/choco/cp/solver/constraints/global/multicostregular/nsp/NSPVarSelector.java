package choco.cp.solver.constraints.global.multicostregular.nsp;

import choco.kernel.solver.search.integer.AbstractIntVarSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Dec 9, 2008
 * Time: 12:41:53 AM
 */
public class NSPVarSelector extends AbstractIntVarSelector {
    NSPStruct struct;
    ArrayList<IntDomainVar> queue;
    HashMap<IntDomainVar,int[]> map ;
    VarCompar vc;

    public NSPVarSelector(NSPStruct struct)
    {
        this.struct = struct;
        this.vc = new VarCompar();
        this.map = new HashMap<IntDomainVar,int[]>();
        this.queue = new ArrayList<IntDomainVar>();
        for (int i = 0 ; i < struct.vars.length ; i++)
        {
            map.put(struct.vars[i],new int[]{i/struct.instance.nbDays,i%struct.instance.nbDays});
            queue.add(struct.vars[i]);
        }




    }

    


    public  class  VarCompar implements Comparator<IntDomainVar>
    {


        public int compare(IntDomainVar v1, IntDomainVar v2)
        {
            if (v1.isInstantiated())
            {
                if (v2.isInstantiated())
                    return 0;
                else
                    return 1;
            }
            if (v2.isInstantiated())
            {
                return -1;
            }

            int[] pos1 = map.get(v1);
            int[] pos2 = map.get(v2);
            int max = -1000000;
            int max2 = -1000000;
            int nj = -1;
            int nj2 = -1;
            for (int j = 0 ; j < struct.instance.nbShifts ; j++)
            {
                int tmp = struct.need[pos1[1]][j].get();
                int tmp2 = struct.need[pos2[1]][j].get();
                if (tmp > max)// && v1.canBeInstantiatedTo(j))
                {
                    max  = tmp;
                    nj = j;
                }
                if (tmp2 > max2)// && v2.canBeInstantiatedTo(j))
                {
                    max2 = tmp2;
                    nj2 = j;
                }
            }
            if (Math.max(max2,max) < 0) return 0;
            if (max2 > max)
            {
                return 1;
            }
            else if (max > max2)
            {
                return -1;
            }
            else {
                int a = struct.instance.prefs[pos1[0]][pos1[1]*struct.instance.nbShifts+nj];
                int b = struct.instance.prefs[pos2[0]][pos2[1]*struct.instance.nbShifts+nj2];
                return new Integer(a).compareTo(b);
            }



        }

    }

    public IntDomainVar selectIntVar()  {

        Collections.sort(queue,vc);
        for (IntDomainVar v : queue)
        {
            if (!v.isInstantiated()) return v;
        }
        return null;
    }
}
