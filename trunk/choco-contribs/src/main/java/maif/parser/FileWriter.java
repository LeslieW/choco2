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
package maif.parser;

import choco.cp.solver.CPSolver;
import choco.kernel.model.variables.integer.IntegerVariable;
import maif.cp.MaifModel;
import maif.entities.Person;

import java.io.File;
import java.io.IOException;
import java.util.*;

import jxl.write.*;
import jxl.write.Number;
import jxl.WorkbookSettings;
import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Alignment;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Jul 23, 2009
 * Time: 5:35:45 PM
 */
public class FileWriter {

    public  void writeSolution(CPSolver s, MaifModel m, File f, int month, int year) throws WriteException {

        WorkbookSettings ws = new WorkbookSettings();
        ws.setLocale(new Locale("fr", "FR"));
        WritableWorkbook workbook = null;
        try {
            workbook = Workbook.createWorkbook(f, ws);
        } catch (IOException e) {
            System.err.println("Impossible d'ecrire le fichier solution");
        }
        WritableSheet sheet = workbook.createSheet("Feuil1", 0);
        WritableFont wf = new WritableFont(WritableFont.createFont("Comic Sans MS"),10,WritableFont.BOLD);
        WritableCellFormat cf = new WritableCellFormat(wf);

        cf.setAlignment(Alignment.CENTRE);
        for (int i = 1 ; i< 6 ; i++)
        {
            sheet.addCell(new Blank(i,0,cf));
        }

        Label title =  new Label(0,0,getMonthFromIndex(month).toUpperCase()+" "+year,cf);

        sheet.addCell(title);

        sheet.mergeCells(0,0,5,0);

        wf = new WritableFont(WritableFont.createFont("Bookman Old Style"),10,WritableFont.BOLD);
        cf = new WritableCellFormat(wf);
        cf.setAlignment(Alignment.CENTRE);
        cf.setBorder(Border.ALL,BorderLineStyle.THIN);
        sheet.addCell(new Label(2,2,"8h30-9h30",cf));
        sheet.addCell(new Label(3,2,"11h30-12h45",cf));
        sheet.addCell(new Label(4,2,"12h45-14h00",cf));
        sheet.addCell(new Label(5,2,"16h00-18h00",cf));
        cf =  new WritableCellFormat(wf);
        cf.setBorder(Border.TOP, BorderLineStyle.THIN);
        cf.setBorder(Border.BOTTOM, BorderLineStyle.THIN);
        sheet.addCell(new Label(0,2,"",cf));
        sheet.addCell(new Label(1,2,"",cf));



        int i;
        ArrayList<Person> p = m.getPeople();
        IntegerVariable[] vars = m.getDayVars();
        IntegerVariable[] cardVars = m.getCardVars();

        int[] maxSize = new int[4];
        for (i = 0 ; i < m.getDays().size() ; i++)
        {
            wf = new WritableFont(WritableFont.ARIAL,10, WritableFont.NO_BOLD);
            cf = new WritableCellFormat(wf);
            cf.setWrap(true);
            cf.setShrinkToFit(true);

            cf.setBorder(Border.RIGHT,BorderLineStyle.THIN);
            cf.setAlignment(Alignment.CENTRE);

            Date d = m.getDays().get(i);
            Calendar cal = new GregorianCalendar();
            cal.setTime(d);
            String jour = getDayFromCalendar(cal);

            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
                cf.setBorder(Border.BOTTOM,BorderLineStyle.THICK);
            else if (i == m.getDays().size()-1)
                cf.setBorder(Border.BOTTOM,BorderLineStyle.THIN);

            Label l0 = new Label(0,i+3,jour,cf);

            cf = new WritableCellFormat(wf,new DateFormat("d MMM yyy"));
            cf.setAlignment(Alignment.CENTRE);
            cf.setWrap(true);
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
                cf.setBorder(Border.BOTTOM,BorderLineStyle.THICK);
            else if (i == m.getDays().size()-1)
                cf.setBorder(Border.BOTTOM,BorderLineStyle.THIN);

            sheet.addCell(l0);
            DateTime l1 = new DateTime(1,i+3,d,cf);
            sheet.addCell(l1);

            int k = 0;
            for (int j = i*4 ; j < i*4+4 ; j++)
            {
                wf = new WritableFont(WritableFont.ARIAL,8, WritableFont.NO_BOLD);
                cf = new WritableCellFormat(wf);

                cf.setBorder(Border.ALL, BorderLineStyle.THIN);
                cf.setAlignment(jxl.format.Alignment.CENTRE);
                if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
                    cf.setBorder(Border.BOTTOM,BorderLineStyle.THICK);
                //cf.setWrap(true);
                //cf.setShrinkToFit(true);
                String name = p.get(s.getVar(vars[j]).getVal()).getName();
                int newL = name.length();
                if (maxSize[k] < newL)
                {
                    maxSize[k] = newL;
                    System.out.println(maxSize[k]);
                    sheet.setColumnView(k+2,newL+4);
                }
                Label tmp = new Label(2+k,i+3,name,cf);
                sheet.addCell(tmp);
                k++;
            }

        }
        i+=5;
        wf = new WritableFont(WritableFont.ARIAL,10, WritableFont.BOLD);
        cf = new WritableCellFormat(wf);
        for (int j = 0 ; j < p.size(); j++)
        {
            Label tmp = new Label(2,i+j,p.get(j).getName(),cf);
            sheet.addCell(tmp);
            WritableCellFormat cf2 = new WritableCellFormat(NumberFormats.INTEGER);
            Number n = new Number(3,i+j,s.getVar(cardVars[j]).getVal(),cf2);
            sheet.addCell(n);
        }


        try {
            workbook.write();
            workbook.close();
        } catch (IOException e) {
            System.err.println("Unable to write solution");
        }

    }

    private static String getMonthFromIndex(int m)
    {
        switch(m)
        {
            case 1 : return "Janvier";
            case 2 : return "Fevrier";
            case 3 : return "Mars";
            case 4 : return "Avril";
            case 5 : return "Mai";
            case 6 : return "Juin";
            case 7 : return "Juillet";
            case 8 : return "Aout";
            case 9 : return "Septembre";
            case 10: return "Octobre";
            case 11: return "Novembre";
            case 12: return "Decembre";

        }
        return null;
    }

    private static String getDayFromCalendar(Calendar cal)
    {
        int d = cal.get(Calendar.DAY_OF_WEEK);
        switch(d)
        {
            case Calendar.MONDAY : return "Lundi";
            case Calendar.TUESDAY : return "Mardi";
            case Calendar.WEDNESDAY : return "Mercredi";
            case Calendar.THURSDAY : return "Jeudi";
            case Calendar.FRIDAY : return "Vendredi";
            case Calendar.SATURDAY : return "Samedi";
            case Calendar.SUNDAY : return "Dimanche";
            default : return null;

        }
    }

}