package modele.batiments;

import outils.Position;
import ressources.Item;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public abstract class Batiment {

    protected Position position;
    //indique si le batiment peut etre traversable par les unites
    protected boolean estTraversable;
    //le type du batiment
    protected TypeBat typeBat;
    //taille du batiment
    protected int taille;
    //coleur du batiment
    protected Color couleur;
    //Vrai si le batiment a été cliqué dessus faux sinon
    protected boolean enClic;
    //image qui represente le batiment
    protected BufferedImage image;
    //type de mine : d'or, de fer ou de charbon
    protected Item item;

    //points de vie du batiment
    protected int vie = 10;

    public Batiment(Position p){
        this.position = p;
        this.couleur = Color.pink;
        this.enClic = false;
    }

    public Batiment(int x, int y){
        this.position = new Position(x, y);
    }

    public void setImages(){
        String imgUrl;
        if(this.typeBat != TypeBat.MINE) imgUrl = "images/batiments/"+this.typeBat+".png";
        else {
            String itemString;
            if(this.item == Item.OR) itemString = "mineOr";
            else if(this.item == Item.FER) itemString = "mineFer";
            else itemString = "mineCharbon";
            imgUrl = "images/batiments/"+itemString+".png";
        }
        System.out.println(imgUrl);
        try {
            this.image = ImageIO.read(new File(imgUrl));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TypeBat getType(){
        return this.typeBat;
    }

    public Position getPosition(){
        return this.position;
    }

    public Color getCouleur() {
        return couleur;
    }

    public int getTaille(){
        return this.taille;
    }

    public Item getItem(){
        if (typeBat == TypeBat.MINE) return item;
        else {
            System.out.println("getItem() : appel sur un batiment autre qu'une mine");
            return null;
        }
    }

    public int getVie() {
        return vie;
    }

    public void incrVie(){
        this.vie++;
    }

    public void decrVie(){
        this.vie--;
    }

    public void setEnClic(boolean b){
        this.enClic = b;
    }

    public boolean enClic(){
        return this.enClic;
    }

    public BufferedImage getImage() {
        return image;
    }

    /**
     * Renvoie la taille d'un batiment en fonction du TypeBat en param
     * @param batiment  le TypeBat
     * @return la taille du batiment correspondant au TypeBat Batiment
     */
    public static int getTailleTypeBat(TypeBat batiment){
        return switch (batiment) {
            case DEFENSE -> 50;
            case HDV -> 120;
            case MARCHE -> 120;
            case MINECHARBON, MINEFER, MINEOR, MINE -> 70;
            case MOULIN -> 90;
            case MUR -> 20;
        };
    }


    @Override
    public String toString() {
        return "Batiment{" +
                "position=" + position +
                ", typeBat=" + typeBat +
                ", couleur=" + couleur +
                '}';
    }
}
