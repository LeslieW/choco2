/* * * * * * * * * * * * * * * * * * * * * * * * * 
 *          _       _                            *
 *         |  °(..)  |                           *
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
 *                  N. Jussien    1999-2008      *
 * * * * * * * * * * * * * * * * * * * * * * * * */
package choco.cp.solver.constraints.global.multicostregular.asap.data.base;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Dec 16, 2008
 * Time: 4:54:22 PM
 */
public class ASAPMasterWeights {
    int prefOverStaffing;
    int prefUnderStaffing;
    int maxOverStaffing;
    int minUnderStaffing;
    int maxShiftsPerDay;


    public int getPrefOverStaffing() {
        return prefOverStaffing;
    }

    public void setPrefOverStaffing(int prefOverStaffing) {
        this.prefOverStaffing = prefOverStaffing;
    }

    public int getPrefUnderStaffing() {
        return prefUnderStaffing;
    }

    public void setPrefUnderStaffing(int prefUnderStaffing) {
        this.prefUnderStaffing = prefUnderStaffing;
    }

    public int getMaxOverStaffing() {
        return maxOverStaffing;
    }

    public void setMaxOverStaffing(int maxOverStaffing) {
        this.maxOverStaffing = maxOverStaffing;
    }

    public int getMinUnderStaffing() {
        return minUnderStaffing;
    }

    public void setMinUnderStaffing(int minUnderStaffing) {
        this.minUnderStaffing = minUnderStaffing;
    }

    public int getMaxShiftsPerDay() {
        return maxShiftsPerDay;
    }

    public void setMaxShiftsPerDay(int maxShiftsPerDay) {
        this.maxShiftsPerDay = maxShiftsPerDay;
    }
}