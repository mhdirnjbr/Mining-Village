package modele.batiments;

import outils.Position;

import java.awt.*;

public class Marche extends Batiment{

    public static final int PRIX = 500;

    public Marche(Position p){
        super(p);
        this.estTraversable = true;
        this.typeBat = TypeBat.MARCHE;
        this.taille = getTailleTypeBat(typeBat);
        this.couleur = new Color(100, 100, 50);
        setImages();
    }
}
