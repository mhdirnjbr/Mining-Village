package modele.unites;

import outils.Position;
import ressources.Item;
import ressources.Ressource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Ouvrier extends Unite{

    // Taille de l'ouvrier
    public static final int TAILLE = 30;
    //Prix d'un ouvrier
    public final static int PRIX = 50;
    //Consommation d'un ouvrier en nourriture
    public final static int CONSOMMATION = 4;
    //Vrai si l'ouvrier est dans une mine, faux sinon
    private boolean enTravail;
    //vrai si l'ouvrier se fait attaquer, faux sinon
    private boolean enAttaque;
    //Valeur representant la forme de l'ouvrier de 0 a 10 : 10 en forme et il meurt a 0
    private int forme;
    //Vrai si l ouvrier est proche d un chef selectionne
    private boolean selectionnable;
    //Taille
    private final int T = 20;
    //Vitesse
    private final int V = 2;
    //points de vie de l'ouvrier
    private final static int FORME = 20;
    // Etat de l'image
    public int imageState;
    // Liste des image de gauche vers droite
    public final ArrayList<BufferedImage> imgDeplacementRight = new ArrayList<BufferedImage>();
    // Liste des image de droite vers gauche
    public final ArrayList<BufferedImage> imgDeplacementLeft = new ArrayList<BufferedImage>();

    public Ouvrier(Position p) throws IOException {
        super(p);
        this.typeUnite = TypeUnite.OUVRIER;
        this.taille = T;
        this.vitesse = V;
        this.enTravail = false;
        this.enAttaque = false;
        this.forme = FORME;
        this.couleur = Color.black;
        this.chargeImages();
    }

    public Ouvrier() throws IOException {
        super();
        this.typeUnite = TypeUnite.OUVRIER;
        this.taille = T;
        this.vitesse = V;
        this.enTravail = false;
        this.enAttaque = false;
        this.forme = FORME;
        this.couleur = Color.black;
        this.chargeImages();
    }

    /**
     * Ća permet de telecharger les images dans les listes des images de l'ouvers.
     * @throws IOException
     */
    public void chargeImages() throws IOException {
        sensDeDeplacement = Direction.DROITE_VERS_GAUCHE;
        imageState = 0;

        for (int i = 1; i <= 3; i++) {
            imgDeplacementRight.add(ImageIO.read(
                    new File(
                            "miner/miner" + i + "R.png")));
        }
        for (int i = 1; i <= 3; i++) {
            imgDeplacementLeft.add(ImageIO.read(
                    new File(
                            "miner/miner" + i + "L.png")));
        }
    }

    /**
     * La fonction qui remet a 0 la quantite d'une ressource de l'ouvrier
     * @param ressource la ressource
     */
    public void donneRessource(Ressource ressource){
        if(ressource.estMinerais()) setItemQuantite(ressource.getItem(), 0);
        else {
            System.out.println("donneRessource() : pas la bonne ressource");
        }
    }

    /**
     * La fonction qui ajoute la quantite d'une ressource a l'ouvrier
     * @param item l'item
     * @param x la quantite de l'item
     */
    public void ajouteRessource(Item item, int x){
        for(Ressource r : ressources){
            if(r.getItem() == item){
                r.ajouteQuantite(x);
            }
        }
    }

    /**
     * La fonction qui ajoute la quantite d'une ressource a l'ouvrier
     * @param ressource la ressource
     */
    public void ajouteRessource(Ressource ressource){
        for(Ressource r : ressources){
            if(r.getItem() == ressource.getItem()){
                r.ajouteQuantite(ressource.getQuantite());
            }
        }
    }

    /**
     * inverse la valeur de enTravail
     */
    public void changeTravail(boolean b){
        this.enTravail = b;
    }

    public boolean enTravail() {
        return this.enTravail;
    }

    /**
     * actualise le sens de deplacement de l'ouvrier
     */
    public void verifSens() {
        if(positionFinale != null){
            if(position.getX() != positionFinale.getX()) {
                sensDeDeplacement = position.getX() > positionFinale.getX() ?
                        Direction.GAUCHE_VERS_DROITE : Direction.DROITE_VERS_GAUCHE;
                // sensDroite = position.getX() < positionFinale.getX();
            }
        }
    }

    /**
     * Verifie la fatigue de l'ouvrier
     */
    public void verifForme(){
        if(this.forme == 0) this.tue();
    }

    public void incrForme(){
        if(this.forme < FORME) this.forme++;
        this.couleur = new Color(12*(FORME-this.forme),12*(FORME-this.forme),12*(FORME-this.forme));
    }

    public boolean getAttaque(){
        return enAttaque;
    }

    public void setEnAttaque(boolean b){
        this.enAttaque = b;
    }

    public int getForme() {
        return this.forme;
    }

    public void decrForme(){
        if(this.forme > 0) {
            this.forme--;
            verifForme();
        }
        this.couleur = new Color(12*(FORME-this.forme),12*(FORME-this.forme),12*(FORME-this.forme));
    }

    public boolean selectionnable() {
        return this.selectionnable;
    }

    public void changeSelectionnable(boolean b){
        this.selectionnable = b;
    }

}
