package samples.multicostregular.nsp;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: julien
 * Date: Dec 4, 2008
 * Time: 3:15:58 PM
 */
public class NSPParser {




    public static NSPInstance parseNSPFile(String filename)
    {

        NSPInstance out = new NSPInstance();
        FileReader f = null;
        try {
            f = new FileReader(filename);
        } catch (FileNotFoundException e) {
            System.err.println("Unable to open file");
            System.exit(-1);
        }
        BufferedReader r = new BufferedReader(f);

        String line;
        Pattern p = Pattern.compile("(\\d+\\t?)+");

        try {
            int count = 0;
            String[] l;
            while ((line = r.readLine()) != null) {

                Matcher m = p.matcher(line);
                if (m.matches())
                {
                    l = line.split("\t");
                    if (count == 0)
                    {

                        out.nbNurses = Integer.parseInt(l[0]);
                        out.nbDays = Integer.parseInt(l[1]);
                        out.nbShifts = Integer.parseInt(l[2]);
                        out.coverages = new int[out.nbDays][out.nbShifts];
                        out.prefs = new int[out.nbNurses][out.nbDays*out.nbShifts];
                    }
                    else if (count > 0 && count < out.nbDays+1)
                    {
                        for (int i = 0 ; i < l.length ; i++)
                        {
                            out.coverages[count-1][i] = Integer.parseInt(l[i]);
                        }
                    }
                    else if (count >= out.nbDays+1)
                    {
                        for (int i = 0 ; i < l.length ; i++)
                        {
                            out.prefs[count-out.nbDays-1][i] = Integer.parseInt(l[i]);
                        }

                    }
                    count++;
                }

            }
        } catch (IOException e) {
            System.err.println("Error reading file");
            System.exit(-1);
        }

        return out;
    }

    public static void main(String[] args) {
        NSPInstance nsp = NSPParser.parseNSPFile("/Users/julien/These/NSP/NSPLib/N25/1.nsp");
        System.out.println(nsp);


    }

}
