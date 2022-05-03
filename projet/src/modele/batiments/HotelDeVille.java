package modele.batiments;

import outils.Position;

import java.awt.*;

public class HotelDeVille extends Batiment{

    public static final int PRIX = 200;

    public HotelDeVille(Position p){
        super(p);
        this.typeBat = TypeBat.HDV;
        this.taille = getTailleTypeBat(typeBat);;
        this.couleur = new Color(247, 151, 43);
        setImages();
    }


}
