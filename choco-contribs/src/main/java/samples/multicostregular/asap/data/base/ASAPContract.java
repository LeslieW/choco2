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
package samples.multicostregular.asap.data.base;



import samples.multicostregular.asap.data.ASAPItemHandler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Dec 16, 2008
 * Time: 3:21:30 PM
 */
public class ASAPContract {
    String id;
    int maxShiftsPerDay;
    int maxNumAssignmentPenalty;
    int maxNumAssignment = -1;
    int minConsecutiveFreeDaysPenalty;
    int minConsecutiveFreeDays;
    int minConsecutiveWorkingDaysPenalty;
    int minConsecutiveWorkingDays;

    int maxConsecutiveWorkingWeekEndsPenalty;
    int maxConsecutiveWorkingWeekEnds;
    int maxShiftTypesPenalty;
    int minShiftTypesPenalty;
    int maxWorkingWeekEndsPenalty;
    int maxWorkingWeekEnds = -1;
    int minWorkingWeekEndsPenalty;
    int minWorkingWeekEnds;
    int minDaysOffPenalty;
    int minDaysOff;
    int maxDaysOffPenalty;
    int maxDaysOff = -1;

    int maxWeekEndDaysPenalty;
    int maxWeekEndDays = -1;
    int minWeekEndDaysPenalty;
    int minWeekEndDays;

    HashMap<ASAPShift,Integer> maxShiftType;
    HashMap<ASAPShift,Integer> minShiftType;


    int minShiftsPerWeekPenalty;
    int minShiftsPerWeek = 0;
    int maxShiftsPerWeekPenalty;
    int maxShiftsPerWeek = 7;

    ArrayList<ASAPPattern> patterns;
    ASAPItemHandler handler;




    public ASAPContract(ASAPItemHandler handler, String id)
    {
        this.id = id;
        this.handler = handler;
        this.maxShiftType = new HashMap<ASAPShift,Integer>();
        this.minShiftType = new HashMap<ASAPShift,Integer>();

        this.patterns = new ArrayList<ASAPPattern>();
        handler.putContract(id,this);
    }

    public void addMaxShiftType(String s, int val)
    {
        this.maxShiftType.put(handler.getShift(s),val);
    }
    public void addMinShiftType(String s, int val)
    {
        this.minShiftType.put(handler.getShift(s),val);
    }

    public int getMaxShiftType(ASAPShift s)
    {
        Integer i = this.maxShiftType.get(s);
        if (i == null)
            return Integer.MAX_VALUE;
        else
            return i;
    }

    public int getMinShiftType(ASAPShift s)
    {
        Integer i = this.minShiftType.get(s);
        if (i == null)
            return 0;
        else
            return i;
    }


    public ArrayList<ASAPPattern> getPatterns() {
        return patterns;
    }

    public void addPattern(ASAPPattern badPattern) {
        this.patterns.add(badPattern);
    }

    public String getId() {
        return id;
    }

    public int getMaxShiftsPerDay() {
        return maxShiftsPerDay;
    }

    public void setMaxShiftsPerDay(int maxShiftsPerDay) {
        this.maxShiftsPerDay = maxShiftsPerDay;
    }

    public int getMaxNumAssignmentPenalty() {
        return maxNumAssignmentPenalty;
    }

    public void setMaxNumAssignmentPenalty(int maxNumAssignmentPenalty) {
        this.maxNumAssignmentPenalty = maxNumAssignmentPenalty;
    }

    public int getMaxNumAssignment() {
        return maxNumAssignment;
    }

    public void setMaxNumAssignment(int maxNumAssignment) {
        this.maxNumAssignment = maxNumAssignment;
    }

    public int getMinConsecutiveFreeDaysPenalty() {
        return minConsecutiveFreeDaysPenalty;
    }

    public void setMinConsecutiveFreeDaysPenalty(int minConsecutiveFreeDaysPenalty) {
        this.minConsecutiveFreeDaysPenalty = minConsecutiveFreeDaysPenalty;
    }

    public int getMinConsecutiveFreeDays() {
        return minConsecutiveFreeDays;
    }

    public void setMinConsecutiveFreeDays(int minConsecutiveFreeDays) {
        this.minConsecutiveFreeDays = minConsecutiveFreeDays;
    }

    public int getMaxConsecutiveWorkingWeekEndsPenalty() {
        return maxConsecutiveWorkingWeekEndsPenalty;
    }

    public void setMaxConsecutiveWorkingWeekEndsPenalty(int maxConsecutiveWorkingWeekEndsPenalty) {
        this.maxConsecutiveWorkingWeekEndsPenalty = maxConsecutiveWorkingWeekEndsPenalty;
    }

    public int getMaxConsecutiveWorkingWeekEnds() {
        return maxConsecutiveWorkingWeekEnds;
    }

    public void setMaxConsecutiveWorkingWeekEnds(int maxConsecutiveWorkingWeekEnds) {
        this.maxConsecutiveWorkingWeekEnds = maxConsecutiveWorkingWeekEnds;
    }

    public int getMinShiftsPerWeekPenalty() {
        return minShiftsPerWeekPenalty;
    }

    public void setMinShiftsPerWeekPenalty(int minShiftsPerWeekPenalty) {
        this.minShiftsPerWeekPenalty = minShiftsPerWeekPenalty;
    }

    public int getMinShiftsPerWeek() {
        return minShiftsPerWeek;
    }

    public void setMinShiftsPerWeek(int minShiftsPerWeek) {
        this.minShiftsPerWeek = minShiftsPerWeek;
    }

    public int getMaxShiftsPerWeekPenalty() {
        return maxShiftsPerWeekPenalty;
    }

    public void setMaxShiftsPerWeekPenalty(int maxShiftsPerWeekPenalty) {
        this.maxShiftsPerWeekPenalty = maxShiftsPerWeekPenalty;
    }

    public int getMaxShiftsPerWeek() {
        return maxShiftsPerWeek;
    }

    public void setMaxShiftsPerWeek(int maxShiftsPerWeek) {
        this.maxShiftsPerWeek = maxShiftsPerWeek;
    }

    public int getMaxShiftTypesPenalty() {
        return maxShiftTypesPenalty;
    }

    public void setMaxShiftTypesPenalty(int maxShiftTypesPenalty) {
        this.maxShiftTypesPenalty = maxShiftTypesPenalty;
    }
      public void setMinShiftTypesPenalty(int maxShiftTypesPenalty) {
        this.minShiftTypesPenalty = maxShiftTypesPenalty;
    }

    public int getMaxWorkingWeekEndsPenalty() {
        return maxWorkingWeekEndsPenalty;
    }

    public void setMaxWorkingWeekEndsPenalty(int maxWorkingWeekEndsPenalty) {
        this.maxWorkingWeekEndsPenalty = maxWorkingWeekEndsPenalty;
    }

    public int getMaxWorkingWeekEnds() {
        return maxWorkingWeekEnds;
    }

    public void setMaxWorkingWeekEnds(int maxWorkingWeekEnds) {
        this.maxWorkingWeekEnds = maxWorkingWeekEnds;
    }

    public int getMinWorkingWeekEndsPenalty() {
        return minWorkingWeekEndsPenalty;
    }

    public void setMinWorkingWeekEndsPenalty(int minWorkingWeekEndsPenalty) {
        this.minWorkingWeekEndsPenalty = minWorkingWeekEndsPenalty;
    }

    public int getMinWorkingWeekEnds() {
        return minWorkingWeekEnds;
    }

    public void setMinWorkingWeekEnds(int minWorkingWeekEnds) {
        this.minWorkingWeekEnds = minWorkingWeekEnds;
    }

    public int getMinConsecutiveWorkingDaysPenalty() {
        return minConsecutiveWorkingDaysPenalty;
    }

    public void setMinConsecutiveWorkingDaysPenalty(int minConsecutiveWorkingDaysPenalty) {
        this.minConsecutiveWorkingDaysPenalty = minConsecutiveWorkingDaysPenalty;
    }

    public int getMinConsecutiveWorkingDays() {
        return minConsecutiveWorkingDays;
    }

    public void setMinConsecutiveWorkingDays(int minConsecutiveWorkingDays) {
        this.minConsecutiveWorkingDays = minConsecutiveWorkingDays;
    }

    public int getMinDaysOffPenalty() {
        return minDaysOffPenalty;
    }

    public void setMinDaysOffPenalty(int minDaysOffPenalty) {
        this.minDaysOffPenalty = minDaysOffPenalty;
    }

    public int getMinDaysOff() {
        return minDaysOff;
    }

    public void setMinDaysOff(int minDaysOff) {
        this.minDaysOff = minDaysOff;
    }

    public int getMaxDaysOffPenalty() {
        return maxDaysOffPenalty;
    }

    public void setMaxDaysOffPenalty(int maxDaysOffPenalty) {
        this.maxDaysOffPenalty = maxDaysOffPenalty;
    }

    public int getMaxDaysOff() {
        return maxDaysOff;
    }

    public void setMaxDaysOff(int maxDaysOff) {
        this.maxDaysOff = maxDaysOff;
    }

    public int getMaxWeekEndDaysPenalty() {
        return maxWeekEndDaysPenalty;
    }

    public void setMaxWeekEndDaysPenalty(int maxWeekEndDaysPenalty) {
        this.maxWeekEndDaysPenalty = maxWeekEndDaysPenalty;
    }

    public int getMaxWeekEndDays() {
        return maxWeekEndDays;
    }

    public void setMaxWeekEndDays(int maxWeekEndDays) {
        this.maxWeekEndDays = maxWeekEndDays;
    }

    public int getMinWeekEndDaysPenalty() {
        return minWeekEndDaysPenalty;
    }

    public void setMinWeekEndDaysPenalty(int minWeekEndDaysPenalty) {
        this.minWeekEndDaysPenalty = minWeekEndDaysPenalty;
    }

    public int getMinWeekEndDays() {
        return minWeekEndDays;
    }

    public void setMinWeekEndDays(int minWeekEndDays) {
        this.minWeekEndDays = minWeekEndDays;
    }
}