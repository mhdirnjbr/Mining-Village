package modele.unites;

import outils.Position;
import ressources.Ressource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Chef extends Unite{

    //Prix d'un chef
    public final static int PRIX = 1500;
    //Consommation d'un chef en nourriture
    public final static int CONSOMMATION = 10;
    //Taille
    private final int T = 20;
    //Vitesse
    private final double V = 2;
    // Taille du chef
    public static final int TAILLE = 80;

    private boolean dansMarche = false;
    private boolean dansHdv = false;
    private boolean dernierEnClic = false;


    public int imageState;

    public final ArrayList<BufferedImage> imgDeplacementRight = new ArrayList<BufferedImage>();
    public final ArrayList<BufferedImage> imgDeplacementLeft = new ArrayList<BufferedImage>();

    public Chef (Position p) throws IOException {
        super(p);
        this.typeUnite = TypeUnite.CHEF;
        this.taille = T;
        this.vitesse = V;
        this.couleur = Color.red;

        chargeImage();
    }

    public Chef () throws IOException {
        super();
        this.typeUnite = TypeUnite.CHEF;
        this.taille = T;
        this.vitesse = V;
        this.couleur = Color.red;

        chargeImage();
    }

    public void chargeImage() throws IOException {
        imageState = 0;
        for(int i=1;i<=6;i++){
            imgDeplacementRight.add(ImageIO.read(
                    new File(
                            "chef/frame-" + i + ".png")));
        }
        for(int i=1;i<=6;i++){
            imgDeplacementLeft.add(ImageIO.read(
                    new File(
                            "chef/framel-" + i + ".png")));
        }
    }

    /**
     * La fonction qui ajoute la quantite d'une ressource dans les ressources du chef
     * @param ressource la ressource a ajouter
     */
    private void ajoutRessource(Ressource ressource){
        if(ressource.estMinerais()) {
            incrItem(ressource.getItem(), ressource.getQuantite());
        }
        else {
            System.out.println("ajoutRessource() : pas la bonne ressource");
        }
    }

    /**
     * La fonction qui ajoute les ressources d'un ouvrier a celle du chef
     * @param ouvrier l'ouvrier
     */
    public void recuperer(Ouvrier ouvrier){
        for (Ressource r : ouvrier.ressources){
            ajoutRessource(r);
            ouvrier.donneRessource(r);
        }

    }

    /**
     * actualise le sens de deplacement du chef
     */
    public void verifSens(){
        if(positionFinale != null){
            if(position.getX() != positionFinale.getX()) {
                sensDeDeplacement = position.getX() < positionFinale.getX() ?
                        Direction.GAUCHE_VERS_DROITE : Direction.DROITE_VERS_GAUCHE;
                // sensDroite = position.getX() < positionFinale.getX();
            }
        }
    }

    public boolean dansMarche() {
        return this.dansMarche;
    }

    public void setDansMarche(boolean bool) {
        this.dansMarche = bool;
    }

    public boolean dansHdv() {
        return this.dansHdv;
    }

    public void setDansHdv(boolean bool) {
        this.dansHdv = bool;
    }

    public boolean dernierEnClic() {
        return this.dernierEnClic;
    }

    public void changeDernierEnClic(boolean b){
        this.dernierEnClic = b;
    }
}
