package modele.batiments;

import outils.Position;
import ressources.Item;
import ressources.Ressource;

import java.awt.*;

public class Moulin extends Batiment {

    //prix d'un moulin
    public static final int PRIX = 500;
    //production d'un moulin
    public static final int PRODUCTION = 17;
    //couleur du moulin
    private Color couleurMoulin;

    public Moulin (Position p){
        super(p);
        this.typeBat = TypeBat.MOULIN;
        this.estTraversable = true;
        this.taille = getTailleTypeBat(typeBat);
        this.couleur = new Color(102,170,102); //vert
        this.couleurMoulin = new Color(170,102,51); //marron
        setImages();
    }

    //incremente la quantite de ressource avec la production
    public void produit(Ressource ressource){
        if(ressource.getItem() == Item.PAIN){
            ressource.ajouteQuantite(PRODUCTION);
        }
    }

    public Color getCouleurMoulin() {
        return this.couleurMoulin;
    }
}
