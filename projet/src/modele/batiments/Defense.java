package modele.batiments;

import outils.Position;

import java.awt.*;

public class Defense extends Batiment{

    public static final int PRIX = 100;
    public static final int POWER = 10;

    // Rayon de poussement
    public static final int PUSH_RADIUS = 10;
    // Puissance de poussement de l'ennemi
    public static final int POWER_DEFENSE = 7;
    // Barre de vie maximum
    public static final int HEALTH_MAX = 100;
    // Barre de vie
    private int health;

    public Defense (Position p){
        super(p);
        this.estTraversable = false;
        this.typeBat = TypeBat.DEFENSE;
        this.taille = getTailleTypeBat(typeBat);
        this.couleur = new Color(100, 200, 10);

        //Initialisation de la barre de vie
        this.health = HEALTH_MAX;
        setImages();

    }

    public void setHealth(int health){
        this.health = health;
    }

    public int getHealth(){
        return this.health;
    }


}
