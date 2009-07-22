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
package assur.ihm;

import com.sun.tools.javac.comp.Flow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Calendar;

import assur.Main;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Jul 22, 2009
 * Time: 10:52:27 AM
 */
public class MainWindow extends JFrame {


    Container pane;
    File preference;
    File planning;
    File output;
    int month,year;
    Main main;


    Field plan;
    Field pref;
    Field out;

    JComboBox mois;
    JComboBox annee;

    JButton start;


    public MainWindow(Main main)
    {
        super("Permplanner");
        this.main = main;
        this.pane = this.getContentPane();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Toolkit tools = Toolkit.getDefaultToolkit();
        Dimension d = tools.getScreenSize();
        d.setSize(d.getWidth()*2/6, d.getHeight()*2/6);
        this.setSize(d);
        this.setPreferredSize(d);

        readPrefFile();

        makeMenuBar();
        makeFields(d);

        this.pack();
        this.setVisible(true);

    }

    public void writePrefFile()
    {
        File file = new File("prefs.ini");
        BufferedWriter bwr = null;

        try {
            bwr = new BufferedWriter(new FileWriter(file));

            bwr.write(planning.getPath(),0,planning.getPath().length());
            bwr.newLine();
            bwr.write(preference.getPath(),0,preference.getPath().length());
            bwr.newLine();
            bwr.write(output.getParentFile().getPath(),0,output.getParentFile().getPath().length());


            // dispose all the resources after using them.
            bwr.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void readPrefFile()
    {
        File file = new File("prefs.ini");
        if (!file.exists())
        {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("IMpossible de créer le fichier de préférence");
            }
            planning = new File("");
            plan = new Field("",planning);
            output = new File("permanences.xls");
            out = new Field("",output);
            preference = new File("");
            pref = new Field("",preference);
        }
        else
        {

            FileInputStream fis = null;
            BufferedInputStream bis = null;
            BufferedReader dis = null;

            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                dis = new BufferedReader(new InputStreamReader(bis));

                String tmp;
                int f = 0;
                while ((tmp = dis.readLine()) != null) {

                    if (f == 0)
                    {
                        planning = new File(tmp);
                        plan = new Field(planning.getPath(),planning);
                    }
                    else if (f== 1)
                    {
                        preference = new File(tmp);
                        pref = new Field(preference.getPath(),preference);
                    }
                    else if (f== 2)
                    {
                        output = new File(tmp);
                        if (output.isDirectory())
                            output = new File(tmp+File.separator+"permanences.xls");

                        out = new Field(output.getPath(),output);
                    }
                    f++;
                }
                if (f < 3 && f > 1)
                {

                    output = new File("permanences.xls");
                    out = new Field("",output);

                }
                else if (f!=3) {
                    preference = new File("");
                    pref = new Field("",preference);
                    planning = new File("");
                    plan = new Field("",planning);
                }
                output.createNewFile();

                // dispose all the resources after using them.
                fis.close();
                bis.close();
                dis.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    private void makeMenuBar() {

    }

    private void makeFields(Dimension d) {
        JPanel holder = new JPanel(new BorderLayout());
        JPanel north = new JPanel(new BorderLayout());
        JPanel center = new JPanel(new FlowLayout());
        holder.add(center,BorderLayout.CENTER);
        holder.add(north,BorderLayout.NORTH);
        JPanel south = new JPanel(new BorderLayout());
        holder.add(south,BorderLayout.SOUTH);


        this.pane.add(holder);

        JPanel inNorth = new JPanel(new GridLayout(3,1));

        inNorth.add(plan,BorderLayout.NORTH);
        inNorth.add(pref,BorderLayout.CENTER);
        inNorth.add(out,BorderLayout.SOUTH);
        north.add(inNorth,BorderLayout.CENTER);

        JLabel label1 = new JLabel("Planning : ");
        JLabel label2 = new JLabel("Preferences : ");
        JLabel label3 = new JLabel("Sortie: ");

        JPanel labPane = new JPanel(new GridLayout(3,1));
        labPane.add(label1);
        labPane.add(label2);
        labPane.add(label3);
        north.add(labPane,BorderLayout.WEST) ;



        mois = new JComboBox(new String[]{"Janvier","Fevrier","Mars","Avril","Mai","Juin","Juillet","Aout","Septembre","Octobre","Novembre","Decembre"});
        annee = new JComboBox(new String[]{"2009","2010","2011","2012","2013","2014","2015"});
        Date date = new Date();
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        mois.setSelectedIndex(cal.get(Calendar.MONTH)+1);
        annee.setSelectedIndex(cal.get(Calendar.YEAR)-2009);

        JPanel tmp0 = new JPanel(new GridLayout(2,1));
        tmp0.add(new JLabel("Mois : "));
        tmp0.add(mois);

        JPanel tmp1 = new JPanel(new GridLayout(2,1));
        tmp1.add(new JLabel("Annee : "));
        tmp1.add(annee);
        JPanel dummy1 = new JPanel();
        dummy1.add(tmp0);


        center.add(dummy1);
        center.add(tmp1);

        JPanel dummy = new JPanel();

        JButton compute = new JButton("Calculer Planning");
        compute.addActionListener(new StartHandler(compute));
        dummy.add(compute);
        south.add(dummy,BorderLayout.CENTER);


    }

    private static int monthToInt(String name)
    {
        String n = name.toLowerCase();
        if (n.equals("janvier")) return 1;
        if (n.equals("fevrier")) return 2;
        if (n.equals("mars")) return 3;
        if (n.equals("avril")) return 4;
        if (n.equals("mai")) return 5;
        if (n.equals("juin")) return 6;
        if (n.equals("juillet")) return 7;
        if (n.equals("aout")) return 8;
        if (n.equals("septembre")) return 9;
        if (n.equals("octobre")) return 10;
        if (n.equals("novembre")) return 11;
        if (n.equals("decembre")) return 12;
        return 1;
    }

    private class StartHandler implements ActionListener
    {

        private JButton button;
        public StartHandler(JButton but)
        {
            this.button = but;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            preference = pref.f;
            output = out.f;
            planning = plan.f;
            writePrefFile();
            month = monthToInt((String)mois.getSelectedItem());
            year = Integer.parseInt((String) annee.getSelectedItem());
            this.button.setText("En cours...");
            main.startSolving(month,year,planning,preference,output);
            this.button.setText("Resolu");

        }
    }

    private static class FieldHandler implements ActionListener
    {

        Field field;

        public FieldHandler(Field f)
        {
            this.field = f;
        }
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JFileChooser f = new JFileChooser(this.field.f);
            f.setMultiSelectionEnabled(false);
            f.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int returnVal = f.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                this.field.f = f.getSelectedFile();
                this.field.field.setText(this.field.f.getPath());
            }


        }
    }

    private static class Field extends JPanel
    {
        JTextField field;
        JButton click;
        File f;

        public Field(String text,File f)
        {
            this.f = f;
            this.field = new JTextField(text,30);
            this.click = new JButton("choisir...");
            this.click.addActionListener(new FieldHandler(this));
            this.setLayout(new BorderLayout());
            this.add(field,BorderLayout.CENTER);
            this.add(click,BorderLayout.EAST);
        }
    }

    public static void main(String[] args) {
        new MainWindow(new Main());
    }


}