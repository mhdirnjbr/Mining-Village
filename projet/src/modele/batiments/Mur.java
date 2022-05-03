package modele.batiments;

import outils.Position;

import java.awt.*;

public class Mur extends Batiment {

    public static final int PRIX = 50;
    public static final int MAX_TAILLE_MUR = 9;

    public Mur(Position p){
        super(p);
        this.typeBat = TypeBat.MUR;
        this.estTraversable =  false;
        this.taille = getTailleTypeBat(typeBat);
        this.couleur = new Color(12, 165, 212);
    }

}
