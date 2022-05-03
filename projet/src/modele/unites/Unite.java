package modele.unites;
import Affichage.Grille;
import modele.batiments.Batiment;
import modele.batiments.TypeBat;
import modele.Plateau;
import outils.Position;
import outils.aStar.AStarAlg;
import outils.aStar.Node;
import ressources.Item;
import ressources.Ressource;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

public abstract class Unite {

    //vrai si l'unite est en vie, faux sinon
    protected boolean enVie;
    //Compteur qui s'enclenche a la mort d'une unite
    protected int cptMort = 4;
    //la position de l'unite
    protected Position position;
    //nouvelles positions de l'unite
    protected LinkedList<Position> nouvellePosition;
    //position finale
    protected Position positionFinale;
    //Ressources d'une unite
    protected ArrayList<Ressource> ressources;
    //Type de l'unite
    protected TypeUnite typeUnite;
    //Vrai si l'unite est en deplacement faux sinon
    protected boolean seDeplace;
    //Vrai si l'unite a été clique dessus faux sinon
    protected boolean enClic;
    //Vitesse de l'unite
    protected double vitesse;
    //Taille de l'unite
    protected int taille;
    //Couleur de l'unite
    protected Color couleur;

    // Represente la direction de deplacement
    public enum Direction{
        GAUCHE_VERS_DROITE, DROITE_VERS_GAUCHE
    }
    // Attribut de direction
    public Direction sensDeDeplacement;



    public Unite (Position p){
        this.position = p;
        this.enVie = true;
        this.seDeplace = false;
        this.nouvellePosition = new LinkedList<>();
        this.enClic = false;
        this.ressources = new ArrayList<>();
        this.ressources.add(new Ressource(Item.OR));
        this.ressources.add(new Ressource(Item.FER));
        this.ressources.add(new Ressource(Item.CHARBON));
    }


    public Unite (){
        this.position = new Position((int) (Math.random() * Grille.LARG_VILLAGE), (int) (Math.random() * Grille.HAUT_VILLAGE));
        this.enVie = true;
        this.nouvellePosition = new LinkedList<>();
        this.ressources = new ArrayList<>();
        this.ressources.add(new Ressource(Item.OR));
        this.ressources.add(new Ressource(Item.FER));
        this.ressources.add(new Ressource(Item.CHARBON));
    }

    public void deplace(int a, int b){
        position.deplaceX(a);
        position.deplaceY(b);
    }

    public void setDeplace(Plateau plateau, int a, int b){
        Batiment batimentInit = plateau.uniteDansBatiment(this);
        if(batimentInit != null){
            System.out.println("Quitte bat");
            boolean memeBat = false;
            Position quitteBat = this.position.quitteBatiment(batimentInit.getTaille(),new Position(a,b), plateau);
            System.out.println("qB : " + quitteBat + " " + new Position(a,b));
            Batiment batimentBut = plateau.dansBatiment(new Position(a,b));
            if(batimentBut != null){
                System.out.println("Rentre bat");
                if(batimentBut.getPosition().egales(batimentInit.getPosition())){
                    System.out.println("Meme bat");
                    memeBat = true;
                }
                //Position rentreBat = batimentBut.getPosition().quitteBatiment //il faut mettre le resultat de meilleur d abord en position mais on a besoin de ce resultat pour l algo
                //        (batimentBut.getTaille(), batimentBut.getPosition(), plateau);
                Position rentreBat = batimentBut.getPosition().quitteBatiment //il faut mettre le resultat de meilleur d abord en position mais on a besoin de ce resultat pour l algo
                        (batimentBut.getTaille(), this.position, plateau);
                a = rentreBat.getXint();
                b = rentreBat.getYint();
                a = batimentBut.getPosition().getXint();
                b = batimentBut.getPosition().getYint()+batimentBut.getTaille()/2;
            }
            if(!memeBat) {
                Position but = new Position(a, b);
                AStarAlg alg = new AStarAlg(plateau, null);
                LinkedList<Position> solution = alg.solutionMeilleurDabord(quitteBat, but);
                System.out.println("init : " + quitteBat + " but : " + but + " " + solution);
                Node n = new Node(quitteBat, quitteBat.distance(but), 0, null);
                System.out.println(alg.successeurs(n, but, true).size());
                solution.addFirst(quitteBat);
                if (batimentBut != null && typeUnite != TypeUnite.ENNEMI) {
                    Position last = batimentBut.getPosition();
                    if(batimentBut.getType() == TypeBat.HDV) last = new Position(last.getX(), last.getY() + 30);
                    else if(batimentBut.getType() == TypeBat.MARCHE) last = new Position(last.getX() -20, last.getY() + 20);
                    solution.addLast(last);
                }
                this.nouvellePosition = solution;
                this.positionFinale = this.nouvellePosition.getLast();
                changeDeplace(true);
            }
        }
        else {
            System.out.println("Normal");
            Batiment batimentBut = plateau.dansBatiment(new Position(a,b));
            if(batimentBut != null){
                System.out.println("Rentre bat");
                //Position rentreBat = batimentBut.getPosition().quitteBatiment //il faut mettre le resultat de meilleur d abord en position mais on a besoin de ce resultat pour l algo
                //        (batimentBut.getTaille(), batimentBut.getPosition(), plateau);
                Position rentreBat = batimentBut.getPosition().quitteBatiment //il faut mettre le resultat de meilleur d abord en position mais on a besoin de ce resultat pour l algo
                        (batimentBut.getTaille(), this.position, plateau);
                a = rentreBat.getXint();
                b = rentreBat.getYint();
                a = batimentBut.getPosition().getXint();
                b = batimentBut.getPosition().getYint()+batimentBut.getTaille()/2;
                System.out.println(a + " " + b);
            }
            Position but = new Position(a,b);
            AStarAlg alg = new AStarAlg(plateau, null);
            LinkedList <Position> solution = alg.solutionMeilleurDabord(this.position, but);
            System.out.println("init : " + this.position + " but : " + but + " " + solution);
            if(batimentBut != null && typeUnite != TypeUnite.ENNEMI) {
                Position last = batimentBut.getPosition();
                if(batimentBut.getType() == TypeBat.HDV) last = new Position(last.getX(), last.getY() + 30);
                else if(batimentBut.getType() == TypeBat.MARCHE) last = new Position(last.getX() -20, last.getY() + 20);
                solution.addLast(last);
            }
            this.nouvellePosition = solution;
            this.positionFinale = this.nouvellePosition.getLast();
            changeDeplace(true);
        }

    }

    public void setDeplace2(Plateau plateau, int a, int b){
        Batiment bat = plateau.dansBatiment(new Position(a, b));
        if(bat != null) {
            System.out.println("a " + a + " b " + b + " rentre bat");
            Position batPos = new Position(bat.getPosition().getXint(), bat.getPosition().getYint());
            Position avantBat = batPos.quitteBatiment(bat.getTaille(), bat.getPosition(), plateau);
            plateau.aStarPoint = avantBat;
            a = avantBat.getXint();
            b = avantBat.getYint();
        }
        System.out.println(bat + " " + plateau.uniteDansBatiment(this));
        Position but = new Position(a,b);
        if(plateau.uniteDansBatiment(this) != null) {
            System.out.println("a " + a + " b " + b + " quitte bat");

            Batiment batiment = plateau.uniteDansBatiment(this);
            Position quitteBat = this.position.quitteBatiment(batiment.getTaille(), but, plateau);
            AStarAlg alg= new AStarAlg(plateau, null);
            LinkedList<Position> solution = alg.solutionMeilleurDabord(quitteBat, but);
            solution.addFirst(quitteBat);
            this.nouvellePosition = solution;
            this.positionFinale = solution.getLast();
            System.out.println(nouvellePosition);
            changeDeplace(true);
        }
        else {
            System.out.println("a " + a + " b " + b + " normal");

            AStarAlg alg = new AStarAlg(plateau, null);
            LinkedList<Position> solution = alg.solutionMeilleurDabord(this.position, new Position(a, b));
            this.nouvellePosition = solution;
            this.positionFinale = solution.getLast();
            System.out.println(nouvellePosition);
            changeDeplace(true);
        }
        if(bat != null) {
            this.nouvellePosition.addLast(bat.getPosition());
            this.positionFinale = bat.getPosition();
        }
    }

    public void setDeplaceSansAlgoRecherche(int a, int b){
        changeDeplace(true);
        this.nouvellePosition.addFirst(new Position(a,b));
        this.positionFinale = new Position(a,b);
    }

    public void deplaceAnime(){
        if(nouvellePosition.isEmpty()){
            seDeplace = false;
        }
        else {
            position.deplaceAnime(nouvellePosition, vitesse);
        }
    }

    public void setPosition(int a, int b){
        position.setX(a);
        position.setY(b);
    }

    /**
     * La fonction qui renvoie la quantite de la ressource correspondante a l item en parametre
     * @param item
     * @return la quantite de la ressource
     */
    public int getItem(Item item){
        for (Ressource r : this.ressources){
            if (r.getItem() == item) return r.getQuantite();
        }
        return 0;
    }

    /**
     * La fonction qui change la quantite de la ressource correspondante a l item en parametre a la quantite q
     * @param item l item
     * @param q la quantite
     */
    public void setItemQuantite(Item item, int q){
        for (Ressource r : this.ressources){
            if (r.getItem() == item) r.setQuantite(q);
        }
    }

    /**
     * La fonction qui augmente la quantite d'une ressource en fonction de l'item
     * @param item
     * @param q la quantite a incrementer
     */
    public void incrItem(Item item, int q){
        for (Ressource r : this.ressources){
            if (r.getItem() == item) r.ajouteQuantite(q);
        }
    }

    /**
     * La fonction qui renvoie le type de l'unite
     * @return le type de l'unite
     */
    public TypeUnite getType() {
        return typeUnite;
    }

    /**
     * La fonction qui renvoie le compteur apres la mort de l unite
     * @return le compteur
     */
    public int getCptMort(){
        return cptMort;
    }

    /**
     * decrémente le compteur apres la mort de l unite
     */
    public void decrCptMort(){
        this.cptMort--;
    }

    /**
     * La fonction qui renvoie la valeur de enVie
     * @return true si l unite est en vie, false sinon
     */
    public boolean enVie(){
        return enVie;
    }

    public boolean seDeplace(){
        return seDeplace;
    }

    public void changeDeplace(boolean b){
        this.seDeplace = b;
    }

    public void tue(){
        this.enVie = false;
    }

    public void setEnClic(boolean b){
        this.enClic = b;
    }

    public boolean enClic(){
        return this.enClic;
    }

    public Position getPosition(){
        return this.position;
    }

    public LinkedList<Position> getNouvellePosition() {
        return nouvellePosition;
    }

    public Position getPositionFinale() {
        return positionFinale;
    }

    public int getTaille(){
        return this.taille;
    }

    public Color getCouleur() {
        return couleur;
    }

    public void setCouleur(Color c){
        this.couleur = c;
    }

    public ArrayList<Ressource> getRessources() {
        return this.ressources;
    }

    @Override
    public String toString() {
        String s = "";
        for (Ressource r : ressources){
            s = s + "\n" +  r.toString();
        }
        s= "\n" + position + "\n";
        return s;
    }
}
