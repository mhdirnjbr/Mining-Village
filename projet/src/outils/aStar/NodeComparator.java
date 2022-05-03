package outils.aStar;

import java.util.Comparator;

public class NodeComparator implements Comparator<Node> {
    @Override
    public int compare(Node s1, Node s2) {
        if (s1.f < s2.f)
            return -1;
        else if (s1.f > s2.f)
            return 1;
        return 0;
    }
}
