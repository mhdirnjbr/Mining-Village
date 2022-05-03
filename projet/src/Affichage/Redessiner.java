package Affichage;

import modele.Plateau;

public class Redessiner extends Thread {

    private Grille grille;
    private PanneauGestion panneauGestion;
    private Plateau plateau;

    public Redessiner(Grille g, PanneauGestion p, Plateau plateau){
        this.grille = g;
        this.panneauGestion = p;
        this.plateau = plateau;
    }

    @Override
    public void run() {
        while(!grille.defaite && !grille.victoire){
            try { Thread.sleep(5); }
            catch (Exception e) { e.printStackTrace(); }
            //grille.revalidate();
            grille.repaint();
            //panneauGestion.revalidate();
            panneauGestion.repaint();
            grille.victoire = plateau.victoire();
            grille.defaite = plateau.defaite();
        }
    }
}
