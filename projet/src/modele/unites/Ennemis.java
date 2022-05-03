package modele.unites;

import Affichage.Grille;
import modele.Plateau;
import outils.Position;

import java.io.IOException;

public class Ennemis extends Thread {

    Plateau plateau;
    int cpt = 0;
    double porcenteageApparitonEnnemi = 0.001;

    public Ennemis (Plateau p) {
        this.plateau = p;
    }

    @Override
    public void run() {
        while(true) {
            //System.out.println(plateau.enJeu);
            if (plateau.enJeu) {
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (Ennemi e : plateau.getEnnemis()) {
                    e.verifEtat(plateau);
                    e.verifEffraye(plateau);
                    e.verifSens();
                    if (e.seDeplace()) {
                        e.deplaceAnime();
                    } else {
                        if (e.effraye) {
                            e.deplaceEffraye(plateau);
                            plateau.aStarPoint = e.posTest;
                        } else {
                            if (e.etat == 0 && e.cptDeplacement >= Ennemi.LIMITE_CPT*2) {
                                e.cptDeplacement = 0;
                                e.deplaceRandom(plateau);
                            } else if (e.etat == 1 && e.cptDeplacement >= Ennemi.LIMITE_CPT*4 / 2) {
                                e.cptDeplacement = 0;
                                e.deplaceSemiRandom(plateau);
                            } else if (e.etat == 2) {
                                e.cptDeplacement = 0;
                                //on le deplace que quand il n'attaque pas pour ne pas faire de calculs inutiles
                                if (e.positionProche != null && !e.attaque) {
                                    e.deplaceInteresse(plateau);
                                }
                                if (e.attaque && cpt % 100 == 0) { //pour attaquer seulement chaque seconde
                                    e.attaque(plateau);
                                }
                            }
                        }
                        e.cptDeplacement++;
                    }
                }
                if(plateau.spawnEnnemi && plateau.getEnnemis().size() <= Plateau.MAX_ENNEMIS) {
                    double nouvEnnemi = Math.random();
                    if (nouvEnnemi < porcenteageApparitonEnnemi) {
                        System.out.println("------------------------------------");
                        Position spawn2 = Position.randomCarre(plateau, new Position(250, 250), 500);
                        Position spawn = Position.randomRectangle(new Position
                                (Grille.LARG_VILLAGE / 2, Grille.HAUT_VILLAGE / 2), Grille.LARG_VILLAGE + 30, Grille.HAUT_VILLAGE + 30);
                        try {
                            plateau.ajouteUnite(new Ennemi(spawn));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            cpt+=1; //1 pour chaque seconde, 10 pour chaque 1/10e de seconde...
        }
    }

}
