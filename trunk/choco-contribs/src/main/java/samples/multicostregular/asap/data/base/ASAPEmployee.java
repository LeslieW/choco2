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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Dec 16, 2008
 * Time: 4:14:21 PM
 */
public class ASAPEmployee {

    String id;
    String name;
    ASAPContract contract;
    ASAPItemHandler handler;
    HashSet<ASAPSkill> skills;




    public ASAPEmployee(ASAPItemHandler handler, String id)
    {
        this.id = id;
        this.skills = new HashSet<ASAPSkill>();
        this.handler = handler;
        handler.putEmployee(this.id,this);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ASAPContract getContract() {
        return contract;
    }

    public void setContract(ASAPContract contract) {
        this.contract = contract;
    }

    public void setContract(String cId)
    {
        this.contract = handler.getContract(cId);
    }

    public String toString()
    {
        return this.id+" ("+contract.getId()+")";
    }


    public HashSet<ASAPSkill> getSkills() {
        return skills;
    }

    public void addSkill(String skillId) {
        this.skills.add(handler.getSkill(skillId));
    }
    public void addSkill(ASAPSkill skill)
    {
        this.skills.add(skill);
    }

   

}