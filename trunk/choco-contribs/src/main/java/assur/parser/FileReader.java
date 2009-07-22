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
package assur.parser;

import jxl.*;
import jxl.read.biff.BiffException;
import assur.entities.Person;
import assur.entities.Preference;

import java.util.*;
import java.io.File;
import java.io.IOException;

import choco.kernel.model.variables.integer.IntegerVariable;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Jul 20, 2009
 * Time: 5:56:09 PM
 */

public class FileReader {

    ArrayList<Date> days;
    HashMap<Preference,ArrayList<Person>> prefMap;
    HashMap<IntegerVariable,Preference> varMap;

    static String[] months = {"Janvier","Fevier","Mars","Avril","Mai","Juin","Juillet","Aout","Septembre","Octobre","Novembre","Decembre"};


    public FileReader()
    {
        this.days = new ArrayList<Date>();
        this.prefMap = new HashMap<Preference,ArrayList<Person>>();
        this.varMap = new HashMap<IntegerVariable,Preference>();


    }

    public ArrayList<Person> makePeople(int month,int year, File planning, File preference) {

        days.clear();
        ArrayList<Person> people = new ArrayList<Person>();
        HashMap<String,Person> names = new HashMap<String,Person>();

        HashMap<Integer, Date> columnDay = new HashMap<Integer,Date>();

        try {
            Workbook wb = Workbook.getWorkbook(planning);
            Sheet sheet = wb.getSheet(0);
            for (Sheet s : wb.getSheets())
            {
                if (s.getName().equals(Integer.toString(year)))
                    sheet = s;
            }

            Cell[] col = sheet.getColumn(0);

            boolean found = false;
            int idx = 0;
            int nbdays = 0;
            Cell cons;
            while (!found)
            {
                cons = col[idx];
                if (cons.getContents().equalsIgnoreCase(months[month-1]))
                    found = true;
                else
                    idx++;
            }

            int i = 1;
            cons = sheet.getCell(i,idx+1);
            while (!(cons.getContents()).equals("") && i < sheet.getColumns() )
            {
                nbdays++;
                DateCell dcell = (DateCell)cons;
                Date d = dcell.getDate();
                this.days.add(d);
                columnDay.put(i,d);
                i++;
                if (i < sheet.getColumns())
                    cons = sheet.getCell(i,idx+1);

            }

            int j = 2;

            while(!col[idx+j].getContents().equals(""))
            {
                Person p = new Person(col[idx+j].getContents());
                names.put(p.getName(),p);
                for (i = 1 ; i <= nbdays ;i++)
                {
                    int val = sheet.getCell(i,idx+j).getCellFormat().getBackgroundColour().getValue();
                    if (val == 42)
                        p.addHollyday(columnDay.get(i));

                }
                people.add(p);
                p.setIndex(people.size()-1);

                j++;


            }

            // Loading Preferences
            wb = Workbook.getWorkbook(preference);
            sheet = wb.getSheet(0);

            Cell[] days = sheet.getColumn(0);
            ArrayList<Date> dates = new ArrayList<Date>();

            for (j = 2 ; j < days.length ; j++)
            {
                String d = sheet.getCell(0,j).getContents().toLowerCase();
                fillDates(dates,d);
                for (i = 2 ; i < sheet.getColumns() ; i++)
                {
                    String[] name = sheet.getCell(i,j).getContents().split(",");
                    for (String n : name)
                    {
                        try {
                            Person p = names.get(n);
                            for (Date date : dates)
                            {
                               // System.out.println(++bui);
                                Preference pref = p.addPreference(date,i-2);
                                ArrayList<Person> tmp;
                                tmp = prefMap.get(pref);
                                if (tmp == null)
                                {
                                    tmp = new ArrayList<Person>();
                                    prefMap.put(pref,tmp);
                                }
                                tmp.add(p);


                            }
                        }
                        catch (NullPointerException e)
                        {
                            System.err.println("Attention : "+n+" n'est pas de le planning");
                        }
                    }
                }

            }











        } catch (IOException e) {
            System.err.println("Invalid file name");
        } catch (BiffException e) {
            System.err.println("Invalid excel file");
        }

        return people;


    }

    private void fillDates(ArrayList<Date> dates, String d)
    {
        dates.clear();
        for (Date date : this.days)
        {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(date);
            if (d.equals("lundi") && cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
            {
                dates.add(date);
            }
            if (d.equals("mardi") && cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY)
            {
                dates.add(date);
            }
            if (d.equals("mercredi") && cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY)
            {
                dates.add(date);
            }
            if (d.equals("jeudi") && cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY)
            {
                dates.add(date);
            }
            if (d.equals("vendredi") && cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
            {
                dates.add(date);
            }
            if (d.equals("samedi") && cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
            {
                dates.add(date);
            }
            if (d.equals("dimanche") && cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
            {
                dates.add(date);
            }

        }

    }

    public ArrayList<Date> getDays()
    {
        return this.days;
    }

    public HashMap<Preference,ArrayList<Person>> getPrefMap()
    {
        return this.prefMap;
    }

    /* private static Day parseDay(String tmp, int month, int year)
   {
       int day  = Integer.parseInt(tmp.split("-")[0]);
       return new Day(day,month,year);


   } */

}