package outils.aStar;

import Affichage.FenetrePrincipale;
import modele.Plateau;
import outils.GraphPostion;
import outils.Position;

import java.awt.*;
import java.security.PublicKey;
import java.util.*;

public class AStarAlg {

    Plateau plateau;
    FenetrePrincipale fP;

    public AStarAlg(Plateau p, FenetrePrincipale fP){
        this.plateau = p;
        this.fP = fP;
    }

    public LinkedList<Position> solution(Position init, Position but){

        //System.out.println(plateau.getEtatPlateau());

        ArrayList<Node> dejaVus = new ArrayList<>();
        PriorityQueue<Node> frontiere = new PriorityQueue<>();
        Node n1 = new Node(init, init.distanceManhattan(but), 0, null);
        frontiere.add(n1);
        dejaVus.add(n1);

        System.out.println("1");

        int i = 0;

        while(!frontiere.isEmpty()){
            //System.out.println("2");
            Node nodePere = frontiere.poll();
            if(nodePere.position.egales(but)){
                return construitChemin(nodePere);
            }

            plateau.aStarPoint = nodePere.getPosition();
            Iterator<Node> itSuccessors = successeurs(nodePere, but, true).iterator();
            //System.out.println(nodePere);
            //System.out.println(successeurs(nodePere, but));
            while (itSuccessors.hasNext()){
                //System.out.println("3");
                Node fils = itSuccessors.next();
                if(!dejaVus.contains(fils)) {
                    frontiere.add(fils);
                    dejaVus.add(fils);
                }
                else {
                    if(compareG(dejaVus, fils)) {
                        dejaVus.add(fils);
                        frontiere.add(fils);
                    }
                }
            }
            i++;
        }
        return null;
    }

    public LinkedList<Position> solutionProf(Position init, Position but){
        ArrayList<Node> dejaDev = new ArrayList<>();
        PriorityQueue<Node> frontiere = new PriorityQueue<>();
        Node n1 = new Node(init, init.distance(but), 0, null);
        frontiere.add(n1);
        int i = 0;
        while(!frontiere.isEmpty()){
            i++;
            Node nodePere = frontiere.peek();
            if(i == 100){
                for(int t = 0; t < frontiere.size(); t++){
                    //System.out.println(frontiere.poll() + " ");
                }
            }
            if(nodePere.position.egales(but)){
                return construitChemin(nodePere);
            }
            frontiere.remove();
            dejaDev.add(nodePere);
            //System.out.println(dejaDev);
            plateau.aStarPoint = nodePere.getPosition();
            Iterator<Node> itSuccessors = successeurs(nodePere, but, true).iterator();
            while (itSuccessors.hasNext()){
                Node fils = itSuccessors.next();
                //System.out.println(frontiere);
                //System.out.println(fils + " - " + frontiere.contains(fils) + " - " + contient(frontiere, fils));
                if( !contient(dejaDev, fils) && !contient(frontiere, fils)) {
                    frontiere.add(fils);
                }
                else {
                    if(compareG(dejaDev, fils)) {
                        System.out.println("ok");
                        frontiere.add(fils);
                    }
                }
            }
        }
        return null;
    }

    public LinkedList<Position> solutionProfHMonotone(Position init, Position but) {
        long debut = System.currentTimeMillis();
        ArrayList<Node> dejaDev = new ArrayList<>();
        PriorityQueue<Node> frontiere = new PriorityQueue<>();
        Node n1 = new Node(init, init.distance(but), 0, null);
        frontiere.add(n1);
        while(!frontiere.isEmpty()){
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Node nodePere = frontiere.peek();
            //System.out.println(nodePere.position + " " + but);
            if(nodePere.position.egales(but)){
                LinkedList<Position> sol = construitChemin(nodePere);
                plateau.chemin = sol;
                LinkedList<Position> solSimplifiee = Position.simplifie(sol);
                System.out.println("temps création chemin solutionProfHMonotone : " + (System.currentTimeMillis()-debut) + "ms");
                return solSimplifiee;
            }
            frontiere.remove();
            dejaDev.add(nodePere);
            plateau.aStarPoint = nodePere.getPosition();
            Iterator<Node> itSuccessors = successeurs(nodePere, but, true).iterator();
            while (itSuccessors.hasNext()){
                Node fils = itSuccessors.next();
                if( !contient(dejaDev, fils) && !contient(frontiere, fils)) {
                    frontiere.add(fils);
                }
                else {
                    int a = gAvecMemeNoeud(dejaDev, fils);
                    if(a != -1) {
                        System.out.println("ok");
                        frontiere.add(fils);
                        dejaDev.remove(a);
                    }
                }
            }
        }
        return null;
    }

    public LinkedList<Position> solutionMeilleurDabord(Position init, Position but) {
        long debut = System.currentTimeMillis();

        ArrayList<Node> dejaVus = new ArrayList<>();
        PriorityQueue<Node> frontiere = new PriorityQueue<>();
        Node n1 = new Node(init, init.distance(but), 0, null);
        frontiere.add(n1);

        //System.out.println("initAlg : " + init + " butAlg : "  + but);

        while(!frontiere.isEmpty()){
            try {
                Thread.sleep(00);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Node nodePere = frontiere.poll();

            //System.out.println(frontiere);
            //System.out.println(dejaVus);
            //System.out.println();

            if((System.currentTimeMillis()-debut) > 1000) {
                System.out.println("solutionMeilleurDabord() : erreur");
                return Position.simplifie(construitChemin(nodePere)) ;
            }
            //System.out.println(nodePere.position + " " + but);
            if(nodePere.position.egales(but)){
                LinkedList<Position> sol = construitChemin(nodePere);
                plateau.chemin = sol;
                LinkedList<Position> solSimplifiee = Position.simplifie(sol);
                //System.out.println("arrivé à " + nodePere );
                //System.out.println( "chemin : " + sol);
                //System.out.println( "simple : " + solSimplifiee);

                System.out.println("temps création chemin solutionMeilleurDabord : " + (System.currentTimeMillis()-debut) + "ms");


                return solSimplifiee;
            }
            dejaVus.add(nodePere);

            //plateau.aStarPoint = nodePere.getPosition();
            Iterator<Node> itSuccessors = successeurs(nodePere, but, false).iterator();
            while (itSuccessors.hasNext()){
                Node fils = itSuccessors.next();
                if(!contient(dejaVus, fils) && !contient(frontiere, fils)) {
                    frontiere.add(fils);
                }
            }
        }
        return null;
    }

    public static int gAvecMemeNoeud(ArrayList<Node> list, Node node){
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).position.egales(node.position) && list.get(i).g > node.g) {
                return i;
            }
        }
        return -1;
    }

    public static boolean contient(Collection<Node> frontiere, Node node){
        for(Node n : frontiere){
            if(n.position.egales(node.position)) return true;
        }
        return false;
    }

    public boolean compareG(ArrayList<Node> nodes, Node node){
        for(Node ns : nodes){
            if(ns.position.egales(node.position) && ns.g > node.g) {
                nodes.remove(ns);
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param nodePere
     * @param but
     * @param compteG vrai si l'on veut compter le cout (pour aStar) et faux sinon (pour meilleur d'abord)
     * @return
     */
    public ArrayList<Node> successeurs(Node nodePere, Position but, boolean compteG){
        ArrayList<Position> s = nodePere.getPosition().successeurs();
        //System.out.println(s);
        ArrayList<Node> successeurs = new ArrayList<>();
        for(Position p : s){
            //System.out.println(this.plateau.positions[p.getXint()][p.getYint()]);
            //System.out.println(this.plateau.getEtatPlateau().get(this.plateau.positions[p.getXint()][p.getYint()]));
            if(this.plateau.getEtatPlateau().get(this.plateau.positions[p.getXint()][p.getYint()])) {
                double h = p.distance(but);
                double g = compteG ? nodePere.g + nodePere.getPosition().distance(p) : 0;
                successeurs.add(new Node(p, h, g, nodePere));
            }
        }
        return successeurs;
    }


    public LinkedList<Position> construitChemin(Node noeudFinal){
        LinkedList<Position> solution = new LinkedList<>();
        construitChemin(noeudFinal, solution);
        return solution;
    }
    public static void construitChemin(Node n, LinkedList<Position> solution){
        if(n.pere != null){
            construitChemin(n.pere, solution);
        }
        solution.add(n.getPosition());
    }

}
