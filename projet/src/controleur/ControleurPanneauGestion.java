package controleur;

import Affichage.Grille;
import Affichage.PanneauGestion;
import modele.Plateau;
import modele.batiments.*;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ControleurPanneauGestion implements MouseListener {

    public PanneauGestion panneauGestion;
    public Grille grille;
    public Plateau plateau;

    private Mine mineEnClic;

    public ControleurPanneauGestion(PanneauGestion pG, Grille g, Plateau p) {
        this.panneauGestion = pG;
        this.grille = g;
        this.plateau = p;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        for(Mine m : this.plateau.getMines()){
            if(m.enClic()){
                this.mineEnClic = m;

            }
        }
    }

    public Mine getMineEnClic(){
        return this.mineEnClic;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
