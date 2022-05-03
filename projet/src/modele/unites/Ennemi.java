package modele.unites;

import Affichage.Grille;
import modele.batiments.Batiment;
import modele.batiments.TypeBat;
import modele.Plateau;
import outils.Position;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Ennemi extends Chef{

    public static final int BORNE_PAS_MOYEN = 200;
    public static final int BORNE_MOYEN_TRES = 100;
    public static final int LIMITE_CPT = 100;
    public static final int PORTEE_EFFRAYE = 90;
    public static final int DIST_EFFRAYE = 80; //combien de distance l ennemi va parcourir en etant effraye
    public static final int QTITEPAIN = 30; //combien de pain l'ennemi va manger
    public static final int TAILLE_ENNEMY = 60;

    public boolean attaque;
    public boolean effraye;
    private Position posEffraye; //position du chef ou du batiment qui effraie l'ennemi
    public int etat = 0; //niveau d interessement de l ennemi : 0 pas interesse, 1 : moyen, 2 : tres interesse
    public int cptDeplacement = 0;
    public double distProche;
    public Position positionProche;
    public Ouvrier ouvrierProche;
    public Batiment batimentProche;
    public Position posTest;

    // Etat d'image de l'ennemi
    public int imageStateDeplacement;
    public int imageStateAttaque;

    public final ArrayList<BufferedImage> imgEnnemiDeplacementRight = new ArrayList<BufferedImage>();
    public final ArrayList<BufferedImage> imgEnnemiDeplacementLeft = new ArrayList<BufferedImage>();
    public final ArrayList<BufferedImage> imgEnnemiAttaqueRight = new ArrayList<BufferedImage>();
    public final ArrayList<BufferedImage> imgEnnemiAttaqueLeft = new ArrayList<BufferedImage>();


    public Ennemi(Position p) throws IOException {
        super(p);
        this.typeUnite = TypeUnite.ENNEMI;
        this.taille = 20;
        this.vitesse = 2;
        this.effraye = false;
        this.attaque = false;
        this.couleur = Color.green;

        chargeImages();
    }


    public Ennemi() throws IOException {
        super();
        this.typeUnite = TypeUnite.ENNEMI;
        this.taille = 20;
        this.vitesse = 2;
        this.effraye = false;
        this.couleur = Color.green;
        this.attaque = false;

        chargeImages();
    }

    public void chargeImages() throws IOException {
        sensDeDeplacement = Direction.DROITE_VERS_GAUCHE;
        imageStateDeplacement = 0;
        imageStateAttaque = 0;

        for (int i = 1; i <= 8; i++) {
            imgEnnemiDeplacementRight.add(ImageIO.read(
                    new File(
                            "ennemi/deplacement/frame-0" + i + ".png")));
        }
        for (int i = 1; i <= 8; i++) {
            imgEnnemiDeplacementLeft.add(ImageIO.read(
                    new File(
                            "ennemi/deplacement/framel-0" + i + ".png")));
        }

        for (int i = 1; i <= 9; i++) {
            imgEnnemiAttaqueRight.add(ImageIO.read(
                    new File(
                            "ennemi/attaque/frame-0" + i + ".png")));
        }
        for (int i = 1; i <= 9; i++) {
            imgEnnemiAttaqueLeft.add(ImageIO.read(
                    new File(
                            "ennemi/attaque/framel-0" + i + ".png")));
        }
    }

    public void deplaceRandom(Plateau plateau){
        int portee = 50 + (int)(Math.random()*100);
        Position dest = Position.randomCercle(plateau, this.position, portee);
        this.deplaceVerifDehors(plateau, dest.getXint(), dest.getYint());
    }

    public void deplaceSemiRandom(Plateau plateau){
        int portee = 50 + (int)(Math.random()*100);
        Position dest = Position.randomCerclePourcentage(plateau, this.position, portee, positionProche);
        this.deplaceVerifDehors(plateau, dest.getXint(), dest.getYint());
    }

    public void deplaceInteresse(Plateau plateau){
        if(batimentProche != null) {
            deplaceVerifDehors(plateau, positionProche.getXint(), positionProche.getYint());
        }
        else if(ouvrierProche != null) {
            Position p = ouvrierProche.getPosition().quitteBatiment(this.taille, position, plateau);
            if(p!=null) deplaceVerifDehors(plateau, p.getXint(), p.getYint());
        }
    }

    public void deplaceEffraye(Plateau plateau){
        cptDeplacement = 0;
        double x1 = posEffraye.getXint() - this.position.getXint();
        double y1 = posEffraye.getYint() - this.position.getYint();
        double s = Math.sqrt(x1 * x1 + y1 * y1);
        x1 = - x1 / s;
        y1 = - y1 / s;
        int a = (int)(x1*DIST_EFFRAYE) + this.position.getXint();
        int b = (int)(y1*DIST_EFFRAYE) + this.position.getYint();
        this.deplaceVerifDehors(plateau, a, b);
        posTest = new Position(a,b);
    }

   /* public void deplaceEffraye2(Plateau plateau){
        cptDeplacement = 0;
        double x1 = posEffraye.getXint() - this.position.getXint();
        double y1 = posEffraye.getYint() - this.position.getYint();
        double s = Math.sqrt(x1 * x1 + y1 * y1);
        x1 = - x1 / s;
        y1 = - y1 / s;
        int a = (int)(x1*DIST_EFFRAYE) + this.position.getXint();
        int b = (int)(y1*DIST_EFFRAYE) + this.position.getYint();
        if(a < 0) a = 0;
        else if(a >= Grille.LARG_VILLAGE) a = Grille.LARG_VILLAGE-1;
        if(b < 0) b = 0;
        else if(b >= Grille.HAUT_VILLAGE) a = Grille.HAUT_VILLAGE-1;
        System.out.println(x1 + " " + y1 + " ---- " + a + " " + b);
        this.setDeplace(plateau, a, b);
        posTest = new Position(a,b);
    }*/

    public void verifSens(){
        if(effraye){
            if(position.getX() != posEffraye.getX()){
                //sensDroite = position.getX() > posEffraye.getX();
                sensDeDeplacement = position.getX() > posEffraye.getX() ? Direction.GAUCHE_VERS_DROITE : Direction.DROITE_VERS_GAUCHE;
            }

        }
        else if(positionFinale != null){
            if(position.getX() != positionFinale.getX()){
                //sensDroite = position.getX() < positionFinale.getX();
                sensDeDeplacement = position.getX() < positionFinale.getX() ? Direction.GAUCHE_VERS_DROITE : Direction.DROITE_VERS_GAUCHE;
            }
        }
    }

    public void verifEffraye(Plateau plateau){
        boolean resetEffraye = true;
        Position posEffraye = null;
        for(Chef c : plateau.getChefs()) {
            if(this.position.distance(c.getPosition()) < PORTEE_EFFRAYE) {
                effraye = true;
                resetEffraye = false;
                posEffraye = c.getPosition();
                couleur = Color.cyan;
            }
        }
        for(Batiment b : plateau.getBatiments()) {
            if(b.getType() == TypeBat.DEFENSE && this.position.distance(b.getPosition()) < PORTEE_EFFRAYE){
                effraye = true;
                resetEffraye = false;
                        posEffraye = b.getPosition();
                couleur = Color.cyan;
            }
        }
        if(resetEffraye) effraye = false;
        else {
            this.posEffraye = posEffraye;
        }
    }

    public void verifEtat(Plateau plateau){
        distanceMin(plateau);
        if(distProche < BORNE_MOYEN_TRES){
            etat = 2;
            this.couleur = Color.pink;
        }
        else if(distProche < BORNE_PAS_MOYEN){
            etat = 1;
            this.couleur = Color.yellow;
        }
        else {
            etat = 0;
            this.couleur = Color.green;
        }
        attaque = verifAttaque(plateau);
    }

    public boolean verifAttaque(Plateau plateau){
        if(etat == 2){
            if(this.ouvrierProche != null){
                Position p = ouvrierProche.getPosition().quitteBatiment(this.taille, position, plateau);
                ouvrierProche.setEnAttaque(true);
                if(p!= null) return p.egales(this.position);
            }
            else if(this.batimentProche != null){
                Position p;
                p = batimentProche.getPosition();
                p = p.quitteBatiment(batimentProche.getTaille(), position, plateau);
                if(p!=null) return p.egales(position);
            }
        }
        return false;
    }

    public void attaque(Plateau plateau){
        plateau.attaque(this);
    }

    public void distanceMin(Plateau plateau){
        Batiment batMin = null;
        Ouvrier ouvrierMin = null;
        double distance;
        double distanceMax = (new Position(0,0)).distance(new Position(Grille.LARG_VILLAGE, Grille.HAUT_VILLAGE));
        double distanceMin = distanceMax;
        for(Batiment b : plateau.getMinesBats()){
            if(b.getType() == TypeBat.MOULIN || b.getType() == TypeBat.MINE) {
                distance = this.position.distance(b.getPosition());
                if (distance < distanceMin) {
                    distanceMin = distance;
                    batMin = b;
                }
            }
        }
        for(Ouvrier o : plateau.getOuvriers()){
            distance = this.position.distance(o.getPosition());
            if(distance < distanceMin){
                distanceMin = distance;
                ouvrierMin = o;
            }
        }
        if(distanceMin != distanceMax){
            distProche = distanceMin;
            if(ouvrierMin != null){
                batimentProche = null;
                ouvrierProche = ouvrierMin;
                positionProche = ouvrierProche.getPosition();
            }
            else if (batMin != null){
                batimentProche = batMin;
                ouvrierProche = null;
                positionProche = batimentProche.getPosition();
            }
            else {
                batimentProche = null;
                ouvrierProche = null;
                positionProche = null;
            }
        }
        else {
            batimentProche = null;
            ouvrierProche = null;
            positionProche = null;
            distProche = distanceMax;
        }
    }

    public void deplaceVerifDehors(Plateau plateau, int a, int b){
        if(this.enDehors() || Position.enDehors(a,b)) this.setDeplaceSansAlgoRecherche(a,b);
        else this.setDeplace(plateau, a, b);
    }

    public boolean enDehors(){
        return position.getX() < 0 || position.getX() >= Grille.LARG_VILLAGE
                || position.getY() < 0 || position.getY() >= Grille.HAUT_VILLAGE;
    }

    public boolean enDehorsTresLoin(){
        return position.getX() <  - 50 || position.getX() >= Grille.LARG_VILLAGE + 50
                || position.getY() < - 50 || position.getY() >= Grille.HAUT_VILLAGE + 50;
    }
}
