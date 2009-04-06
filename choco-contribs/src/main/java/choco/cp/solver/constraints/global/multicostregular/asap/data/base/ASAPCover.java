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

import choco.cp.solver.constraints.global.multicostregular.asap.data.base.ASAPShift;
import choco.cp.solver.constraints.global.multicostregular.asap.data.base.ASAPSkill;
import choco.cp.solver.constraints.global.multicostregular.asap.data.ASAPItemHandler;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Dec 16, 2008
 * Time: 4:44:46 PM
 */
public class ASAPCover {

    String day;


    ArrayList<ASAPSkill> skills;
    ArrayList<ASAPShift> shifts;
    ArrayList<Integer> prefs;
    ArrayList<Integer> mins;
    ArrayList<Integer> maxs;


    public ASAPCover(ASAPItemHandler handler) {
        this.skills = new ArrayList<ASAPSkill>();
        this.shifts = new ArrayList<ASAPShift>();
        this.prefs = new ArrayList<Integer>();
        this.mins = new ArrayList<Integer>();
        this.maxs = new ArrayList<Integer>();
        handler.cover.addDayOfWeekCover(this);
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void push(ASAPSkill skill, ASAPShift shift, Integer pref, Integer min, Integer max)
    {
        this.skills.add(skill);
        this.shifts.add(shift);
        this.prefs.add(pref);
        this.mins.add(min);
        this.maxs.add(max);
    }

    public ArrayList<ASAPSkill> getSkills() {
        return skills;
    }

    public ArrayList<ASAPShift> getShifts() {
        return shifts;
    }

    public ArrayList<Integer> getPrefs() {
        return prefs;
    }

    public ArrayList<Integer> getMins() {
        return mins;
    }

    public ArrayList<Integer> getMaxs() {
        return maxs;
    }
}