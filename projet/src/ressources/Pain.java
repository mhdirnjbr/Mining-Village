package ressources;

import modele.Plateau;
import modele.unites.Chef;
import modele.unites.Ouvrier;

public class Pain extends Thread{

    private Plateau plateau;
    int cpt = 0;

    public Pain (Plateau p) {
        this.plateau = p;
    }

    @Override
    public void run() {
        while(true){
            if(plateau.enJeu) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if( (plateau.getChefs().size()>0 || plateau.getOuvriers().size() >0) && cpt%5==0)
                    plateau.plusDeNourriture();

                if (plateau.estMoulinConstruit()) {
                    plateau.produitPain();
                }

                for(int i = 0; i < plateau.getChefs().size(); i++) {
                    plateau.consomePain(Chef.CONSOMMATION);
                }
                for(int i = 0; i < plateau.getOuvriers().size(); i++) {
                    plateau.consomePain(Ouvrier.CONSOMMATION);
                }
            }
            cpt++;
        }
    }


}