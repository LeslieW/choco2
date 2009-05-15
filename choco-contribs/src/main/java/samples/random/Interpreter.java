package samples.random;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interpreter {

    private final static String fl = "\\d*\\.?\\d+E?-?\\d*";

    private Interpreter() {

    }

    public static void main(String[] arg) throws IOException {
        final BufferedReader fr = new BufferedReader(new FileReader(arg[0]));

        final Matcher tghtM = Pattern.compile("(" + fl + ")\\ tightness")
                .matcher("");
        final Matcher secsM = Pattern.compile("(" + fl + ")\\ sec").matcher("");
        final Matcher nodesM = Pattern.compile("(" + fl + ")\\ nodes").matcher(
                "");
        final Matcher memM = Pattern.compile("(" + fl + ")\\ mem").matcher("");

        final DecimalFormat f = (DecimalFormat) DecimalFormat.getInstance();
        f.setGroupingSize(0);

        do {
            if (fr.readLine() == null) {
                break;
            }
            tghtM.reset(fr.readLine());
            tghtM.find();
            final float tght = Float.parseFloat(tghtM.group(1));
            fr.readLine();
            fr.readLine();
            fr.readLine();
            fr.readLine();

            // secsM.reset(fr.readLine());
            // secsM.find();
            // final float lightSecs = Float.parseFloat(secsM.group(1));
            // nodesM.reset(fr.readLine());
            // nodesM.find();
            // final float lightNodes = Float.parseFloat(nodesM.group(1));
            // fr.readLine();
            // memM.reset(fr.readLine());
            // memM.find();
            // final float lightMem = Float.parseFloat(memM.group(1));
            // fr.readLine();
            // fr.readLine();
            //
            // secsM.reset(fr.readLine());
            // secsM.find();
            // final float maxSecs = Float.parseFloat(secsM.group(1));
            // nodesM.reset(fr.readLine());
            // nodesM.find();
            // final float maxNodes = Float.parseFloat(nodesM.group(1));
            // fr.readLine();
            // memM.reset(fr.readLine());
            // memM.find();
            // final float maxMem = Float.parseFloat(memM.group(1));
            // fr.readLine();
            // fr.readLine();

            secsM.reset(fr.readLine());
            secsM.find();
            final float acSecs = Float.parseFloat(secsM.group(1));
            nodesM.reset(fr.readLine());
            nodesM.find();
            final float acNodes = Float.parseFloat(nodesM.group(1));
            fr.readLine();
            memM.reset(fr.readLine());
            memM.find();
            final float acMem = Float.parseFloat(memM.group(1));

            // System.out.println(f.format(tght) + "\t" + f.format(lightSecs)
            // + "\t" + f.format(lightNodes) + "\t" + f.format(lightMem)
            // + "\t" + f.format(maxSecs) + "\t" + f.format(maxNodes)
            // + "\t" + f.format(maxMem) + "\t" + f.format(acSecs) + "\t");
            System.out.println(f.format(acSecs) + "\t" + f.format(acMem));
        }

        while (true);

    }
}
