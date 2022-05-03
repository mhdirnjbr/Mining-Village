package outils.aStar;

import modele.Plateau;
import outils.Position;

public class Node implements Comparable<Node> {
    Position position;
    Double h;
    Double g;
    Double f;
    Position but;
    Node pere;
    Plateau plateau;

    public Node(Position p, double h, double g, Node pere){
        this.position = p;
        this.h = h;
        this.g = g;
        this.f = (double)this.g + this.h;
        this.pere = pere;
    }

    public Node(){
        this.position = null;
        this.h = Double.parseDouble(null);
        this.g = Double.parseDouble(null);
        this.pere = null;
    }

    public Position getPosition() {
        return position;
    }

    public void setH(double heuristique){
        this.h = heuristique;
    }

    @Override
    public int compareTo(Node s2) {
        if(this.f != null && s2.f != null){
            return this.f.compareTo(s2.f);
        }
        return 0;
    }/*
    @Override
    public int compareTo(Node s2) {
        if(this.h != null && s2.h != null){
            return this.h.compareTo(s2.h);
        }
        return 0;
    }*/

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Node other = (Node) obj;
        if (this.pere == null) {
            if (other.pere != null)
                return false;
        } else if (!this.pere.equals(other.pere))
            return false;
        return this.position.egales(other.position);
    }


    @Override
    public String toString() {
        return position + ", h = " + h.toString() + ", g = " + g +  ", f = " + f.toString();
    }
}
