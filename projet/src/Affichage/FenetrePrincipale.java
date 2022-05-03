package Affichage;

import modele.Plateau;
import controleur.ControleurGrille;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Ça représente la fenêtre principale du jeu.
 */
public class FenetrePrincipale extends JFrame {

    //Largeur de la fenetre
    public static final int LARG_FENETRE = 900;

    //Hauteur de la fenetre
    public static final int HAUT_FENETRE = 600;

    public Grille grille;

    public PanneauGestion panneauGestion;

    public ControleurGrille c;

    private JButton rejouer;

    public Plateau plateau;

    /**
     * Ça crée une fenêtre principale avec un titre spécifique.
     */
    public FenetrePrincipale() throws IOException {
        super("Mining Village");

        //Définition des dimensions de la fenetre
        this.setSize(LARG_FENETRE, HAUT_FENETRE);

        //Mettre la fenetre au centre de l'ecran
        this.setLocationRelativeTo(null);

        //Fermer le jeu en cliquant sur le bouton fermer de la fenetre
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Rend impossible le changement de taille de la fenetre
        //this.setResizable(false);

        this.plateau = new Plateau();
        this.panneauGestion = new PanneauGestion(plateau);
        this.grille = new Grille(plateau, panneauGestion);
        this.c = new ControleurGrille(this.grille, this.panneauGestion, plateau);
        this.grille.setControleur(this.panneauGestion);
        this.panneauGestion.setControleur(this.grille);

        JPanel fenetre = new JPanel();
        fenetre.add(grille);
        fenetre.add(panneauGestion);
        grille.setFocusable(true);
        grille.requestFocusInWindow();
        grille.repaint();
        this.add(fenetre);

        fenetre.setBackground(new Color(51, 161, 27));
        //fenetre.setBackground(new Color(127, 151, 108));


        //this.rejouer = new JButton("Rejouer");
        //fenetre.add(rejouer);

        //Rendre visible la fenetre
        this.setVisible(true);
    }

}
