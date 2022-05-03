package outils;

import modele.Plateau;
import modele.unites.*;

public class Deplacer extends Thread {

    Plateau plateau;

    public Deplacer (Plateau p) {
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
                if (plateau != null)
                    for (Chef u : plateau.getChefs()) {
                        if (u.seDeplace()) {
                            u.deplaceAnime();
                            u.verifSens();
                        }
                    }
                for (Ouvrier u : plateau.getOuvriers()) {
                    if (u.seDeplace()) {
                        u.deplaceAnime();
                        u.verifSens();
                    }
                }
                plateau.verifieSelectionnable();
            }
        }
    }

}
