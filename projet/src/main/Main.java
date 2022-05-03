package main;

import modele.batiments.Miner;
import modele.Plateau;
import outils.Deplacer;
import outils.SoundManager;
import ressources.Pain;
import modele.unites.*;
import Affichage.*;

import java.io.IOException;

public class Main {

    private Deplacer deplacer;
    private Plateau plateau;
    private Redessiner redessiner;
    private Miner miner;
    private Pain pain;
    private Ennemis ennemis;

    public Main() throws InterruptedException, IOException {
        SoundManager.play("son.wav");

        FenetrePrincipale fenetrePrincipale = new FenetrePrincipale();
        plateau = fenetrePrincipale.plateau;

        miner = new Miner(plateau);
        deplacer = new Deplacer(plateau);
        redessiner = new Redessiner(fenetrePrincipale.grille, fenetrePrincipale.panneauGestion, plateau);
        pain = new Pain(plateau);
        ennemis = new Ennemis(plateau);
        Animation animation = new Animation(plateau);

        miner.start();
        deplacer.start();
        redessiner.start();
        pain.start();
        ennemis.start();
        animation.start();


        /*
        AStarAlg aStar = new AStarAlg(plateau, fenetrePrincipale);

        Position p1 = new Position(13,10);
        Position p2 = new Position( 400,400);

        LinkedList<Position> res = aStar.solutionProfHMonotone(p1,p2);
        System.out.println(res);
        LinkedList<Position> res2 = Position.simplifie(res);
        System.out.println(res2);
        plateau.chemin = res2;
        */






    }

    public static void main(String[] args) throws InterruptedException, IOException {

        new Main();

    }
}
