package modele.batiments;

import modele.Plateau;
import modele.unites.Ennemi;
import modele.unites.Ouvrier;

import java.util.Iterator;

public class Miner extends Thread{

    Plateau plateau;

    public Miner (Plateau p) {
        this.plateau = p;
    }

    @Override
    public void run() {
        while(true){
            if(true || plateau.enJeu) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (Ouvrier o : this.plateau.getOuvriers()) {
                    boolean attaque = false;
                    for(Ennemi e : this.plateau.getEnnemis()){
                        if (e.ouvrierProche == o && e.attaque) {
                            attaque = true;
                            break;
                        }
                    }
                    o.setEnAttaque(attaque);
                    if (!o.enTravail() && !o.getAttaque()) o.incrForme();
                }
                //iterateur car ça limite les effets d'erreurs qd on parcourt une liste qui est modifiée en parallele
                Iterator<Mine> iterator = this.plateau.getMines().iterator();
                while(iterator.hasNext()){
                    Mine m = iterator.next();
                    m.miner();
                    boolean detruit = m.verifDestruction();
                    if(detruit) plateau.enleveBat(m);
                }
                this.plateau.verifieEnVie();
            }
        }
    }
}
