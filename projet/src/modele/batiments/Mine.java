package modele.batiments;

import modele.Plateau;
import outils.Position;
import ressources.Item;
import modele.unites.Ouvrier;

import java.awt.*;
import java.util.ArrayList;

public class Mine extends Batiment{

    //TODO : mine qui explose

    //nombre maximal d'ouvriers dans une mine
    public final static int CAPACITE = 5;
    //ouvriers dans la mine
    private ArrayList<Ouvrier> ouvriers = new ArrayList<>();
    //production de la mine : or plus rare et fer le moins rare
    private int production;
    public final static int PRIX_MINE_OR = 2000;
    public final static int PROD_MINE_OR = 1;
    public final static int PRIX_MINE_FER = 1200;
    public final static int PROD_MINE_FER= 2;
    public final static int PRIX_MINE_CHARBON = 600;
    public final static int PROD_MINE_CHARBON= 3;
    public Mine(Position p, Item item){
        super(p);
        this.typeBat = TypeBat.MINE;
        this.taille = getTailleTypeBat(typeBat);

        this.estTraversable =  true;
        if(Item.estMinerais(item)) {
            this.item = item;
        }
        else {
            System.out.println("Mine() : mauvaise ressource");
        }
        this.setProduction();
        this.setCouleur();
        setImages();
    }


    /**
     * initialise la valeur de la production
     */
    public void setProduction(){
        if(this.item == Item.OR) this.production = PROD_MINE_OR;
        else if(this.item == Item.FER) this.production = PROD_MINE_FER;
        else {
            this.production = PROD_MINE_CHARBON;
        }
    }

    /**
     * initialise la couleur de la mine
     */
    public void setCouleur(){
        if(this.item == Item.OR) this.couleur = Color.yellow;
        else if(this.item == Item.FER) this.couleur = Color.gray;
        else {
            this.couleur = Color.black;
        }
    }

    /**
     * ajoute un ouvrier a la liste d'ouvriers dans la mine
     * @param ouvrier
     */
    public void ajouteOuvrier(Ouvrier ouvrier){
        if(this.ouvriers.size() < CAPACITE){
            ouvrier.changeTravail(true);
            this.ouvriers.add(ouvrier);
        }
    }

    /**
     * La fonction qui donne de la ressource aux ouvriers
     * et les fatigue
     */
    public void miner(){
        for(Ouvrier o : this.ouvriers){
            if(!o.getPosition().different(this.position)){
                o.ajouteRessource(this.item, this.production);
                o.decrForme();
            }
        }
    }

    /**
     * Detruit la mine et tue tous les ouvriers a l interieur si la vie de la mine est egale (ou inferieure) a zero
     * et renvoie vrai si la mine est detruite
     */
    public boolean verifDestruction(){
        if(this.vie <= 0){
            for(Ouvrier o : this.ouvriers){
                o.tue();
            }
            return true;
        }
        return false;
    }

    /**
     * Renvoie le prix de la mine en fonction de l'item
     * @param item l'item
     * @return le prix
     */
    public static int prix(Item item){
        if(item == Item.OR) return PRIX_MINE_OR;
        else if(item == Item.FER) return PRIX_MINE_FER;
        else if(item == Item.CHARBON) return PRIX_MINE_CHARBON;
        else {
            System.out.println("prix() : mauvaise ressource");
            return 0;
        }
    }

    public void enleveOuvrier(Plateau plateau, Ouvrier ouvrier){
        boolean b = this.ouvriers.remove(ouvrier);
        //On verifie si l'on a bien retire l'ouvrier
        if(b) {
            Position p = Position.randomCarre(plateau, this.position, this.taille + ouvrier.getTaille());
            ouvrier.setDeplace(plateau, p.getXint(), p.getYint());
        }
    }

    public ArrayList<Ouvrier> getOuvriers() {
        return this.ouvriers;
    }

    public Item getItem(){
        return this.item;
    }

    /**
     * enleve les ouvriers pas en vie et renvoie le nombre d ouvriers supprimes de la mine en clic
     */
    public void verifieEnVie(){
        this.ouvriers.removeIf(o -> !o.enVie());
    }

    @Override
    public String toString() {
        return "Mine{" +
                "CAPACITE=" + CAPACITE +
                ", ouvriers=" + ouvriers +
                ", production=" + production +
                ", position=" + position +
                '}';
    }
}
