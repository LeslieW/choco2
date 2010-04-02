package choco.cp.solver.constraints.global.flow;

import choco.kernel.solver.variables.integer.IntDomainVar;


/**
    Cette classe contient des methodes utilitaires pour manipuler des objet
    "graphe" au sens de la contrainte globale de flot (List d'adjascence 
    particuliere)
    
    our l'instant elle ne contient qu'une methode starique d'affichage du graphe
    passer en parametre.
    En attendant le développement du classe de graphe spécialisée pour choco,
    on pourra y ajouter des méthodes facilitant la construction de ce
    graphe
 */
public class FlowGraph {
    public static String prettyGraph(SCapaEdge[][] g) {
        int nbNodes = g.length;
        int nbArcs = 0;
        for (int n = 0; n < g.length; n++) {
            nbArcs += g[n].length;
        }
        StringBuffer buf = new StringBuffer("");
        buf.append("Graphe : nbNodes=")
           .append(nbNodes)
           .append(" nbArcs=")
           .append(nbArcs)
           .append("\n");

        for (int n = 0; n < nbNodes; n++) {
            buf.append(n)
               .append(" -> ");
            int arcIdx = 0;
            while (arcIdx < g[n].length) {
                SCapaEdge arc = (SCapaEdge)g[n][arcIdx];
                int dest = arc.dest;
                Object flow = arc.capa;
                String flowStr;
                if (flow instanceof Integer) {
                    // Comportement de FlowConstraint pas tres claire
                    // flowStr = "=[" + ((Integer)flow).toString() + "]";
                    flowStr = "~[0, " + ((Integer)flow).toString() + "]";
                } else {
                    // je suppose que c'est une IntDomainVar
                    IntDomainVar v = (IntDomainVar)flow;
                    flowStr = v.toString();
                    if (v.isInstantiated()) {
                        // flowStr += "=" + v.getVal();
                    } else {
                        flowStr += " [" + v.getInf() + "," +
                                          v.getSup() + "]";
                    }
                    // flowStr = ((IntDomainVar)flow).pretty();
                }
                if (arcIdx > 0) {
                    // indentation des arcs suivants du mÍme noeud source
                    buf.append("     ");
                }

                buf.append( arc.dest )
                   .append(" ")
                   .append( flowStr )
                   .append("\n");
                arcIdx++;
            }
        }
        // return buf.toString()+"\n";
        return buf.toString();
    }
}
// ./
