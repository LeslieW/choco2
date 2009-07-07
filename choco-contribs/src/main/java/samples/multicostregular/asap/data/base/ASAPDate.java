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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Dec 16, 2008
 * Time: 5:49:20 PM
 */
public class ASAPDate {

    public static String[] names = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
    public static HashMap<String,Integer> indexes = new HashMap<String,Integer>();
    static
    {
        for (int i = 0 ;i < names.length ;i++)
        {
            indexes.put(names[i],i);
        }
    }

    int year;
    int month;
    int day;
    private Calendar calendar;


    public ASAPDate(int year,int month, int day)
    {
        this.year = year;
        this.month = month-1;
        this.day = day;
        this.setCalendar();
    }

    private void setCalendar()
    {
        calendar = new GregorianCalendar(year,month,day);

    }
    public int getDayOfWeek()
    {
        return calendar.get(Calendar.DAY_OF_WEEK);
    }
    public String getDayOfWeekName()
    {
        return names[getDayOfWeek()-1];
    }


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month+1;
    }

    public void setMonth(int month) {
        this.month = month-1;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public static ASAPDate parseDate(String s)
    {
        String[] tmp = s.split("-");
        return new ASAPDate(Integer.parseInt(tmp[0]),Integer.parseInt(tmp[1]),Integer.parseInt(tmp[2]));
    }

    public String toString()
    {
        return this.year+"-"+(this.month+1)+"-"+this.day;
    }

    public static int getDaysBetween (ASAPDate da1, ASAPDate da2) {
        Calendar d1 = da1.calendar;
        Calendar d2 = da2.calendar;
        if (d1.after(d2)) {  // swap dates so that d1 is start and d2 is end
            java.util.Calendar swap = d1;
            d1 = d2;
            d2 = swap;
        }
        int days = d2.get(java.util.Calendar.DAY_OF_YEAR) -
                d1.get(java.util.Calendar.DAY_OF_YEAR);
        int y2 = d2.get(java.util.Calendar.YEAR);
        if (d1.get(java.util.Calendar.YEAR) != y2) {
            d1 = (java.util.Calendar) d1.clone();
            do {
                days += d1.getActualMaximum(java.util.Calendar.DAY_OF_YEAR);
                d1.add(java.util.Calendar.YEAR, 1);
            } while (d1.get(java.util.Calendar.YEAR) != y2);
        }
        return days;
    } // getDaysBetween()


}