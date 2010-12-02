/**
 *  Copyright (c) 1999-2010, Ecole des Mines de Nantes
 *  All rights reserved.
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the Ecole des Mines de Nantes nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package maif.ihm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Calendar;

import maif.MaifPlanner;

/**
 * Class part of the graphical interface, this frame allow the user to interact
 * with the solver
 *
 * Created by IntelliJ IDEA.
 * User: julien
 * Mail: julien.menana{at}emn.fr
 * Date: Jul 22, 2009
 * Time: 10:52:27 AM
 */
public class MainWindow extends JFrame {


    /**
     * The Container of the frame
     */
    Container pane;

    /**
     * The file descriptor of the preference needed
     * by the solver
     */
    File preference;

    /**
     * The file descriptor of the planning needed
     * by the solver
     */
    File planning;

    /**
     * The file descriptor of the written output of the
     * solution
     */
    File output;

    /**
     * Integer to know which month of the planning is to be built
     */
    int month,year;

    /**
     * Reference to the main class which handles the solver
     */
    MaifPlanner main;

    /**
     * Planning field to be completed by the user
     */
    Field plan;

    /**
     * Preference field to be completed by the user
     */
    Field pref;

    /**
     * File output field
     */
    Field out;

    /**
     * ComboBox that allows the user to select
     * which month he wants to consider
     */
    JComboBox mois;

    /**
     * ComboBox that allows the user to select
     * which yeat he wants to consider
     */
    JComboBox annee;

    /**
     * Button to start the solving
     */
    JButton start;


    /**
     * Constructs a new Window
     * @param main The main class of the program that handles the cp solver
     */
    public MainWindow(MaifPlanner main)
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
        makeFields();


        this.pack();
        this.setResizable(false);
        this.setVisible(true);

    }

    /**
     * This method write the pref.ini file w.r.t. the current state of the fields
     */
    public void writePrefFile()
    {
        File file = new File("prefs.ini");
        BufferedWriter bwr;

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

    /**
     * This method reads the preference file files.ini in order to pre-complete the different field
     */
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

            FileInputStream fis;
            BufferedInputStream bis;
            BufferedReader dis;

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


    /**
     * Not used but complete if needed
     */
    private void makeMenuBar() {

    }


    /**
     * Build all the field within the window.
     */
    private void makeFields() {

        JPanel holder = new JPanel(new BorderLayout());
        JPanel north = new JPanel(new BorderLayout());
        JPanel center = new JPanel(new FlowLayout());
        holder.add(center,BorderLayout.CENTER);
        holder.add(north,BorderLayout.NORTH);
        JPanel south = new JPanel(new BorderLayout());
        holder.add(south,BorderLayout.SOUTH);

        holder.setBorder(BorderFactory.createBevelBorder(0));
        
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
        JButton reset = new JButton("Reset");
        reset.setEnabled(false);

        compute.addActionListener(new StartHandler(compute,reset));
        dummy.add(compute);
        south.add(dummy,BorderLayout.CENTER);

        JPanel dummy2 = new JPanel();
        reset.addActionListener(new StartHandler(compute,reset));
        dummy2.add(reset);

        south.add(dummy2,BorderLayout.EAST);


    }

    /**
     * French month to int translation
     * @param name the name of the month without accent
     * @return the index of the month in the yeat
     */
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


    /**
     * Inner Class to handle events on the start and reset buttons
     */
    private class StartHandler implements ActionListener
    {

        private JButton start;
        private JButton reset;
        public StartHandler(JButton start, JButton reset)
        {
            this.start = start;
            this.reset = reset;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getSource() == start)
            {
                out.f = new File(out.field.getText());
                if (out.f.isDirectory())
                    out.field.setText(out.field.getText()+"/permanence.xls");

                preference = pref.f = new File(pref.field.getText());
                output = out.f = new File(out.field.getText());
                planning = plan.f = new File(plan.field.getText());
                writePrefFile();
                month = monthToInt((String)mois.getSelectedItem());
                year = Integer.parseInt((String) annee.getSelectedItem());
                this.start.setText("En cours...");
                main.startSolving(month,year,planning,preference,output);
                this.start.setText("Resolu");
                this.start.setEnabled(false);
                this.reset.setEnabled(true);
            }
            else if (actionEvent.getSource() == reset)
            {
                this.reset.setEnabled(false);
                this.start.setEnabled(true);
                this.start.setText("Calculer Planning");
            }
        }
    }

    /**
     * Inner class to Handle events on the Choose button.
     */
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
        new MainWindow(new MaifPlanner());
    }


}