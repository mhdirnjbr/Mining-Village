package Affichage;

import modele.Plateau;
import modele.unites.Chef;
import modele.unites.Ennemi;
import modele.unites.Ouvrier;

public class Animation extends Thread{
    public Plateau plateau;

    public Animation(Plateau plateau){
        this.plateau = plateau;
    }

    @Override
    public void run() {
        while (true) {
            super.run();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (Chef c : plateau.getChefs()) {
                if(c.seDeplace())
                    c.imageState = (c.imageState + 1) % 6;
                else c.imageState = 4;
            }
            for(Ennemi e : plateau.getEnnemis()){
                if(e.seDeplace() || e.attaque) {
                    // Animation des images en cas d'attaque et deplacement de l'ennemi
                    if (e.attaque) {
                        e.imageStateAttaque = (e.imageStateAttaque + 1) % 9;
                    } else {
                        e.imageStateDeplacement = (e.imageStateDeplacement + 1) % 8;
                    }
                }
            }
            for (Ouvrier o : plateau.getOuvriers()) {
                if (o.seDeplace()) {
                    o.imageState = (o.imageState + 1) % 3;
                } else {
                    o.imageState = 1;
                }
            }
        }
    }
}
