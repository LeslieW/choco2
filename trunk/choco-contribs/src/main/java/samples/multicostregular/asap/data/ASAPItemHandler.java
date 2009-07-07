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
package samples.multicostregular.asap.data;


import samples.multicostregular.asap.data.base.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Mar 18, 2009
 * Time: 2:17:50 PM
 */
public class ASAPItemHandler
{

    public ASAPDate start;
    public ASAPDate end;

    public HashMap<String, ASAPContract> contracts;
    public HashMap<String, ASAPEmployee> employees;
    public ArrayList<ASAPEmployee> orderedEmployees;
    public HashMap<String,Integer> map;
    public HashMap<Integer,String> inverseMap;
    public HashMap<String, ASAPShift> shifts;
    public HashMap<String, ASAPShiftGroup> shiftgroups;
    public HashMap<String, ASAPSkill> skills;

    public ASAPCoverRequirements cover;
    public ASAPMasterWeights masterWeights;
    public ASAPShiftOnRequest requestOn;


    public String pbName;



    public ASAPItemHandler()
    {
        contracts = new HashMap<String, ASAPContract>();
        employees = new HashMap<String, ASAPEmployee>();
        orderedEmployees = new ArrayList<ASAPEmployee>();
        map = new HashMap<String,Integer>();
        inverseMap = new HashMap<Integer,String>();
        shifts = new HashMap<String, ASAPShift>();
        shiftgroups = new HashMap<String, ASAPShiftGroup>();
        skills = new HashMap<String, ASAPSkill>();
        cover = new ASAPCoverRequirements();
        masterWeights = new ASAPMasterWeights();
        requestOn = new ASAPShiftOnRequest();
    }


    public ASAPDate getStart() {
        return start;
    }

    public void setStart(ASAPDate start) {
        this.start = start;
    }

    public ASAPDate getEnd() {
        return end;
    }

    public void setEnd(ASAPDate end) {
        this.end = end;
    }

    public ASAPEmployee getEmployee(String id)
    {
        return employees.get(id);
    }

    public ASAPContract getContract(String id)
    {
        return contracts.get(id);
    }

    public ASAPShift getShift(String id)
    {
        return shifts.get(id);
    }
    public Collection<ASAPShift> getShifts()
    {
        return shifts.values();
    }

    public ASAPShiftGroup getShiftGroup(String id)
    {
        return shiftgroups.get(id);
    }

    public ASAPSkill getSkill(String id)
    {
        return skills.get(id);
    }
    public Collection<ASAPSkill> getSkills()
    {
        return skills.values();
    }


    public Collection<ASAPEmployee> getEmployeeBySkill(ASAPSkill s)
    {
        if (s == null)
            return orderedEmployees;
        else
        {
            HashSet<ASAPEmployee> hs = new HashSet<ASAPEmployee>();
            for (ASAPEmployee e : orderedEmployees)
            {
                if (e.getSkills().contains(s))
                    hs.add(e);
            }
            return hs;
        }
    }



    public void putContract(String id, ASAPContract c)
    {
        contracts.put(id,c);
    }


    public void putEmployee(String id, ASAPEmployee e)
    {
        employees.put(id,e);
        orderedEmployees.add(e);
    }
    public void putShift(String id, ASAPShift s)
    {
        shifts.put(id,s);
    }

    public void putShiftGroup(String id, ASAPShiftGroup a) {
        shiftgroups.put(id,a);
    }

    public void putSkill(String id, ASAPSkill s) {
        skills.put(id,s);
    }


    public ASAPSkill makeSkill(String id)
    {
        return new ASAPSkill(this,id);
    }

    public ASAPShift makeShift(String ID, String label, String colour, String description, int start, int end, int duration)
    {
        return new ASAPShift(this, ID, label, colour, description, start, end, duration);
    }

    public ASAPShift makeShift(String id)
    {
        return new ASAPShift(this, id);
    }


    public ASAPShiftGroup makeShiftGroup(String id, ASAPShift... shift)
    {
        return new ASAPShiftGroup(this,id, shift);
    }
    public ASAPShiftGroup makeShiftGroup(String id, String... sids)
    {
        return new ASAPShiftGroup(this,id, sids);
    }

    public ASAPShiftGroup makeShiftGroup(String id)
    {
        return new ASAPShiftGroup(this,id);
    }

    public ASAPRestShift makeRestShift()
    {
        return new ASAPRestShift(this);
    }

    public ASAPEmployee makeEmployee(String id)
    {
        return new ASAPEmployee(this,id);

    }

    public ASAPContract makeContract(String id)
    {
        return new ASAPContract(this,id);
    }

    public ASAPCover makeCover()
    {
        return new ASAPCover(this);
    }

    public ASAPDate makeDate(int year, int month, int day)
    {
        return new ASAPDate(year,month,day);
    }

    public ASAPMasterWeights makeMasterWeights()
    {
        return new ASAPMasterWeights();
    }

    public ASAPPattern makePattern(int weight, boolean bad)
    {
        return new ASAPPattern(weight,bad);
    }

    public ASAPShiftOn makeShiftOn()
    {
        return new ASAPShiftOn(this);
    }

    public ASAPShiftOn makeShiftOn(int weight, ASAPShift shift, ASAPEmployee employee, ASAPDate date)
    {
        return new ASAPShiftOn(this,weight,shift,employee, date);
    }
    public ASAPShiftOn makeShiftOn(int weight)
    {
        return new ASAPShiftOn(this,weight);
    }

    public ASAPSubPattern makeSubPattern()
    {
        return new ASAPSubPattern();
    }


    public ASAPShiftSet makeShiftSet() {
        return new ASAPShiftSet();
    }

    public ASAPCoverRequirements makeCoverRequirements() {
        return new ASAPCoverRequirements();
    }

    public ASAPShiftOnRequest makeShiftOnRequest() {
        return new ASAPShiftOnRequest();
    }

    public ASAPCoverRequirements getCover() {
        return cover;
    }

    public ASAPShiftOnRequest getRequestOn() {
        return requestOn;
    }

    public void setProblemName(String s) {
        this.pbName = s;
    }
    public String getProblemName()
    {
        return this.pbName;
    }
}