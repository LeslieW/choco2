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
package assur;

import assur.parser.FileReader;
import assur.entities.Person;
import assur.entities.Preference;
import assur.cp.MaifModel;
import assur.cp.heuristics.PrefValSelector;
import assur.ihm.MainWindow;

import java.util.*;
import java.io.File;
import java.io.IOException;
import java.lang.Boolean;

import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.varselector.StaticVarOrder;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.search.integer.ValSelector;
import choco.kernel.solver.variables.integer.IntDomainVar;
import jxl.WorkbookSettings;
import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Jul 20, 2009
 * Time: 4:32:42 PM
 */
public class Main {


    public static void main(String[] args) {
        MainWindow win = new MainWindow(new Main());


    }

    private static HashMap<IntDomainVar, ArrayList<Person>> toSolverMap(CPSolver s, HashMap<IntegerVariable, Preference> varMap,HashMap<Preference,ArrayList<Person>> prefMap) {

        HashMap<IntDomainVar,ArrayList<Person>> ret = new HashMap<IntDomainVar,ArrayList<Person>>();
        for (IntegerVariable v : varMap.keySet())
        {

            ret.put(s.getVar(v),prefMap.get(varMap.get(v)));
        }
        return ret;
    }

    private static void affiche(CPSolver s, MaifModel m)
    {
        ArrayList<Person> p = m.getPeople();
        IntegerVariable[] dayVars = m.getDayVars();
        IntegerVariable[] cardVars = m.getCardVars();
        if (s.isFeasible())
        {
            int i = 0;
            for (IntegerVariable v : dayVars)
            {
                System.out.print(p.get(s.getVar(v).getVal()).getName());
                System.out.print("\t");
                i++;
                if (i %4 == 0)
                    System.out.println("");
            }

            i = 0;
            for (IntegerVariable v : cardVars)
            {
                System.out.println(p.get(i++).getName()+" : "+s.getVar(v).getVal());
            }
            System.out.println("");
            System.out.println("Ecart Maximal : "+s.getVar(m.getDeviation()).getVal());

        }
    }

    public void startSolving(int month, int year, File planning, File preference, File output) {

        FileReader read = new FileReader();
        ArrayList<Person> people = read.makePeople(month,year,planning,preference);

        MaifModel m  = new MaifModel(people,read.getDays());

        CPSolver s = new CPSolver();
        s.read(m);

        ValSelector val = new PrefValSelector(toSolverMap(s,m.getVarMap(),read.getPrefMap())) ;
        s.setValIntSelector(val);
        s.setVarIntSelector(new StaticVarOrder(s.getVar(m.getDayVars())));

        s.setCpuTimeLimit(6000);

        IntDomainVar obj = s.getVar(m.getDeviation());

        if (s.solve())
        {
            do {
                affiche(s,m);
                s.postCut(s.lt(obj,obj.getVal()));
            } while(s.nextSolution() == Boolean.TRUE);
        }
        s.worldPopUntil(0);
        s.restoreSolution(s.getSearchStrategy().getSolutionPool().getBestSolution());
        try {
            writeSolution(s,m,output);
        } catch (WriteException e) {
            System.err.println("Impossible d'ecrire la solution");
        }


    }

    private void writeSolution(CPSolver s, MaifModel m, File f) throws WriteException {

        WorkbookSettings ws = new WorkbookSettings();
        ws.setLocale(new Locale("fr", "FR"));
        WritableWorkbook workbook = null;
        try {
            workbook = Workbook.createWorkbook(f, ws);
        } catch (IOException e) {
            System.err.println("Impossible d'ecrire le fichier solution");
        }
        WritableSheet sheet = workbook.createSheet("Feuil1", 0);


        int i;
        ArrayList<Person> p = m.getPeople();
        IntegerVariable[] vars = m.getDayVars();
        IntegerVariable[] cardVars = m.getCardVars();

        for (i = 0 ; i < m.getDays().size() ; i++)
        {
            WritableFont wf = new WritableFont(WritableFont.ARIAL,10, WritableFont.BOLD);
            WritableCellFormat cf = new WritableCellFormat(wf);
            cf.setWrap(true);

            Date d = m.getDays().get(i);
            Calendar cal = new GregorianCalendar();
            cal.setTime(d);
            String jour = getDayFromCalendar(cal);
            Label l0 = new Label(0,i+3,jour,cf);
            sheet.addCell(l0);
            DateTime l1 = new DateTime(1,i+3,d);
            sheet.addCell(l1);

            int k = 0;
            for (int j = i*4 ; j < i*4+4 ; j++)
            {
                wf = new WritableFont(WritableFont.ARIAL,10, WritableFont.NO_BOLD);
                cf = new WritableCellFormat(wf);
                String name = p.get(s.getVar(vars[j]).getVal()).getName();
                Label tmp = new Label(2+k,i+3,name,cf);
                sheet.addCell(tmp);
                k++;
            }

        }
        i+=5;
        WritableFont wf = new WritableFont(WritableFont.ARIAL,10, WritableFont.BOLD);
        WritableCellFormat cf = new WritableCellFormat(wf);
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
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

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