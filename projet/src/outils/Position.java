package outils;

import Affichage.Grille;
import modele.batiments.Batiment;
import modele.Plateau;
import modele.unites.Unite;

import java.util.*;

public class Position {

    private double x;
    private double y;
    private double cout;

    public Position(int x, int y){
        this.x = x;
        this.y = y;
        this.cout = 0;
    }

    public Position(double x, double y){
        this.x = x;
        this.y = y;
        this.cout = 0;
    }

    public Position(Position p) {
        this.x = p.getX();
        this.y = p.getY();
        this.cout = p.getCout();
    }

    public static ArrayList<Position> a (Plateau plateau, Position init, Position but){
        ArrayList<Position> points = new ArrayList<>();

        ArrayList<Position> dejaVus = new ArrayList<>();
        ArrayList<Position> frontiere = new ArrayList<>();
        frontiere.add(init);

        while(!frontiere.isEmpty()){
            Position n = frontiere.get(0);
            if(!n.different(but)) return null; //construireSolution(n, Pere);
            frontiere.remove(n);
            dejaVus.add(n);
            for(Position p : n.successeurs(plateau)){
                //Position c = frontiere.element();
                boolean meilleurCout = true; //true si un point n est pas dans frontiere ou est dedans mais avec un cout superieur
                for(Position memePoints : frontiere){
                    //if(!p.different(memePoints) && p.getCout() >= memePoints.getCout()) meilleurCout = false;
                }
                if(!dejaVus.contains(p) && meilleurCout ){
                    //p.incrCout(n.getCout() + 1);
                    frontiere.add(p);
                }
                else{
                    //if()
                }
            }
        }

        return null;
    }

    /**
     * La fontion qui renvoies les 8 positions adjacentes d'une position
     * @return la liste des positions
     */
    public ArrayList<Position> successeurs(){
        ArrayList<Position> s = new ArrayList<>();
        int avantX = this.getXint() - 1;
        int apresX = this.getXint() + 1;
        int avantY = this.getYint() - 1;
        int apresY = this.getYint() + 1;

        if(apresX < Grille.LARG_VILLAGE) {
            Position s1 = new Position(apresX, this.getYint());
            s.add(s1);
        }
        if(apresY < Grille.HAUT_VILLAGE) {
            Position s2 = new Position(this.getXint(), apresY);
            s.add(s2);
        }
        if(avantX >= 0) {
            Position s3 = new Position(avantX, this.getYint());
            s.add(s3);
        }
        if(avantY >= 0) {
            Position s4 = new Position(this.getXint(), avantY);
            s.add(s4);
        }
        if(apresX < Grille.LARG_VILLAGE && apresY < Grille.HAUT_VILLAGE) {
            Position s5 = new Position(apresX, apresY);
            s.add(s5);
        }
        if(avantX >= 0 && apresY < Grille.HAUT_VILLAGE) {
            Position s6 = new Position(avantX, apresY);
            s.add(s6);
        }
        if(avantX >= 0 && avantY >= 0) {
            Position s7 = new Position(avantX, avantY);
            s.add(s7);
        }
        if(apresX < Grille.LARG_VILLAGE && avantY >= 0) {
            Position s8 = new Position(apresX, avantY);
            s.add(s8);
        }

        return s;
    }

    public ArrayList<Position> successeurs(Plateau plateau){
        ArrayList<Position> s = new ArrayList<>();
        int avantX = this.getXint() - 1;
        int apresX = this.getXint() + 1;
        int avantY = this.getYint() - 1;
        int apresY = this.getYint() + 1;

        if(apresX < Grille.LARG_VILLAGE) {
            Position s1 = new Position(apresX, this.getYint());

            s.add(s1);
        }
        if(apresY < Grille.HAUT_VILLAGE) {
            Position s2 = new Position(this.getXint(), apresY);
            s.add(s2);
        }
        if(avantX >= 0) {
            Position s3 = new Position(avantX, this.getYint());
            s.add(s3);
        }
        if(avantY >= 0) {
            Position s4 = new Position(this.getXint(), avantY);
            s.add(s4);

        }

        ArrayList<Position> res = verifiePasBat(plateau, s);
        return res;
    }

    /**
     * renvoie les points qui ne sont pas dans un batiment
     * @param plateau
     * @param positions
     * @return
     */
    public static ArrayList<Position> verifiePasBat(Plateau plateau, ArrayList<Position> positions){
        for(Position p : positions){
            boolean dansBat = false;
            for(Batiment b : plateau.getBatiments()){
                if(p.dansCarre(b.getPosition(), b.getTaille()/2.0)) dansBat = true;
            }
            if(dansBat) positions.remove(p);
        }
        return positions;
    }

    public ArrayList<Position> positionsDansCarre(double portee){
        ArrayList<Position> res = new ArrayList<>();
        int debutX = this.getXint()-(int)portee/2;
        int debutY = this.getYint()-(int)portee/2;
        int finX = this.getXint()+(int)portee/2;
        int finY = this.getYint()+(int)portee/2;

        for(int i = debutX; i < finX; i++) {
            for (int j = debutY; j < finY; j++) {
                res.add(new Position(i,j));
            }
        }
        return res;
    }


    public ArrayList<Position> positionsDansLosange(int largeur, int hauteur){
        ArrayList<Position> res = new ArrayList<>();
        int midX = this.getXint();
        int midY = this.getYint();
        int incr = largeur/hauteur;
        int cpt = 0;
        for(int y = midY - hauteur/2; y < midY; y++){
            for (int x = midX - cpt; x < midX + cpt; x++){
                res.add(new Position(x,y));
            }
            cpt+=incr;
        }
        for(int y = midY; y < midY + hauteur/2; y++){
            for (int x = midX - cpt; x < midX + cpt; x++){
                res.add(new Position(x,y));
            }
            cpt-=incr;
        }
        return res;
    }
    /**
     * Quitte une unite d un batiment en la rapporchant le plus possible du but
     * @param portee la taille du batiment
     * @param but la position ou l unite veut aller
     * @param plateau le plateau
     * @return la position en dehors du batiment
     */
    public Position quitteBatiment(double portee, Position but, Plateau plateau){
        Position distanceMinPos = null;
        double distanceMin = Grille.LARG_VILLAGE*Grille.HAUT_VILLAGE;
        int debutX = this.getXint()-(int)portee/2 -1;
        int debutY = this.getYint()-(int)portee/2 -1;
        int finX = this.getXint()+(int)portee/2;
        int finY = this.getYint()+(int)portee/2;

        if(debutY >= 0) {
            for (int i = debutX; i <= finX; i++) {
                Position pos = new Position(i, debutY);
                double dist = pos.distance(but);
                if (i >= 0 && i < Grille.LARG_VILLAGE) {
                    if (dist < distanceMin && plateau.getEtatPlateau().get(plateau.positions[i][debutY])) {
                        distanceMin = dist;
                        distanceMinPos = pos;
                    }
                }
            }
        }
        if(finY < Grille.HAUT_VILLAGE) {
            for (int i = debutX; i <= finX; i++) {
                Position pos = new Position(i, finY);
                double dist = pos.distance(but);
                if (i >= 0 && i < Grille.LARG_VILLAGE) {
                    if (dist < distanceMin && plateau.getEtatPlateau().get(plateau.positions[i][finY])) {
                        distanceMin = dist;
                        distanceMinPos = pos;
                    }
                }
            }
        }
        if(debutX >= 0) {
            for (int i = debutY; i <= finY; i++) {
                Position pos = new Position(debutX, i);
                double dist = pos.distance(but);
                if (i >= 0 && i < Grille.HAUT_VILLAGE) {
                    if (dist < distanceMin && plateau.getEtatPlateau().get(plateau.positions[debutX][i])) {
                        distanceMin = dist;
                        distanceMinPos = pos;
                    }
                }
            }
        }
        if(finX < Grille.LARG_VILLAGE) {
            for (int i = debutY; i <= finY; i++) {
                Position pos = new Position(finX, i);
                double dist = pos.distance(but);
                if (i >= 0 && i < Grille.HAUT_VILLAGE) {
                    if (dist < distanceMin && plateau.getEtatPlateau().get(plateau.positions[finX][i])) {
                        distanceMin = dist;
                        distanceMinPos = pos;
                    }
                }
            }
        }
        return distanceMinPos;
    }

    /**
     * renvoie vrai si un point se situe dans une une zone carree de centre p et de largeur portee
     * @param p le centre
     * @param portee la portee
     * @return vrai si le point est dans la zone, faux sinon
     */
    public boolean dansCarre(Position p, double portee){
        return x > p.getX() - portee && x < p.getX() + portee
            && y > p.getY() - portee && y < p.getY() + portee;
    }

    /**
     * renvoie vrai si un point se situe dans une une zone rectangle de centre p
     * @param p le centre
     * @param largeur la largeur du rectangle
     * @param hauteur la hauteur du rectangle
     * @return vrai si le point est dans la zone, faux sinon
     */
    public boolean dansRectangle(Position p, double largeur, double hauteur){
        return x > p.getX() - largeur/2 && x < p.getX() + largeur/2
                && y > p.getY() - hauteur/2 && y < p.getY() + hauteur/2;
    }

    /**
     * renvoie vrai si un point se situe dans un cercle de centre p et de rayon portee
     * @param p le centre
     * @param portee la portee
     * @return vrai si le point est dans la zone, faux sinon
     */
    public boolean dansRond(Position p, double portee){
        return this.distance(p) <= portee;
    }

    /**
     * a renommer
     * regarde si un point est sur un autres parmi les unites
     * @param unites
     * @param portee
     * @return
     */
    public boolean estDansUnite(Unite unite, ArrayList<Unite> unites, double portee){
        boolean b = false;
        for(Unite u : unites ){
            if(u!= unite) {
                if (u.getPositionFinale() != null) if (this.dansRond(u.getPositionFinale(), portee)) {
                    System.out.println(this + " n " + u.getNouvellePosition());
                    b = true;
                }
                else {
                    if (this.dansRond(u.getPosition(), portee)) {
                        System.out.println(this + " p" + u.getPositionFinale());
                        b = true;
                    }
                }
            }        System.out.println("b " + b);

        }
        return b;
    }

    /**
     * la fonction qui verifie si un point et un autre n ont pas la meme position
     * @param p l autre point
     * @return vrai s ils n ont pas la meme position, faux sinon
     */
    public boolean different(Position p){
        return this.getXint() != p.getXint() || this.getYint() != p.getYint();
    }

    /**
     * la fonction qui verifie si un point et un autre n ont la meme position
     * @param p l autre point
     * @return vrai s ils n ont pas la meme position, faux sinon
     */
    public boolean egales(Position p){
        return this.getXint() == p.getXint() && this.getYint() == p.getYint();
    }

    /**
     * Deplace une unite de maniere aleatoire dans les cotes d un rectangle
     * @param p position du milieu de carre
     * @param longueur la longueur du rectangle
     * @param hauteur la hauteur du rectangle
     * @return une position dans les cotes au hasard
     */
    public static Position randomRectangle(Position p, double longueur, double hauteur) {
        Random r = new Random();
        int q = r.nextInt(4);
        double x;
        double y;
        if (q == 0) {
            x = p.getX() - longueur / 2.0D;
            y = r.nextDouble() * hauteur + p.getY() - hauteur / 2.0D;
        } else if (q == 1) {
            x = r.nextDouble() * longueur + p.getX() - longueur / 2.0D;
            y = p.getY() - hauteur / 2.0D;
        } else if (q == 2) {
            x = p.getX() + longueur / 2.0D;
            y = r.nextDouble() * hauteur + p.getY() - hauteur / 2.0D;
        } else {
            x = r.nextDouble() * longueur + p.getX() - longueur / 2.0D;
            y = p.getY() + hauteur / 2.0D;
        }
        return new Position(x, y);
    }

    /**
     * Deplace une unite de maniere aleatoire dans les cotes d un carre
     * (sans aller dans un autre batiment)
     * @param plateau les unites a ne pas superposer
     * @param p position du milieu de carre
     * @param portee la taille du cote d un carre
     * @return
     */
    public static Position randomCarre(Plateau plateau, Position p, double portee){
        double x;
        double y;
        Position position;
        Random r = new Random();
        int q; //point au hasard en fonction de q: si = 0 : dans le cote gauche; 1 haut; 2 droit; 3 bas

        do {
            do {
                q = r.nextInt(4);
                if (q == 0) {
                    x = p.getXint() - portee / 2;
                    y = r.nextDouble() * portee + p.getYint() - portee / 2;
                } else if (q == 1) {
                    x = r.nextDouble() * portee + p.getXint() - portee / 2;
                    y = p.getYint() - portee / 2;
                } else if (q == 2) {
                    x = p.getXint() + portee / 2;
                    y = r.nextDouble() * portee + p.getYint() - portee / 2;
                } else {
                    x = r.nextDouble() * portee + p.getXint() - portee / 2;
                    y = p.getYint() + portee / 2;
                }
                position = new Position((int) x, (int) y);
            } while ( !(position.getX() >= 0 && position.getX() < Grille.LARG_VILLAGE
                    && position.getY() >= 0 && position.getY() < Grille.HAUT_VILLAGE));
        }while( !plateau.getEtatPlateau().get(plateau.positions[position.getXint()][position.getYint()]));

        return position;
    }

    /**
     * Deplace une unite de maniere aleatoire dans les cotes d un carre
     * @param p position du milieu de carre
     * @param portee la taille du cote d un carre
     * @return
     */
    public static Position randomCarre(Position p, double portee) {
        Random r = new Random();
        int q = r.nextInt(4);
        double x;
        double y;
        if (q == 0) {
            x = p.getX() - portee / 2.0D;
            y = r.nextDouble() * portee + p.getY() - portee / 2.0D;
        } else if (q == 1) {
            x = r.nextDouble() * portee + p.getX() - portee / 2.0D;
            y = p.getY() - portee / 2.0D;
        } else if (q == 2) {
            x = p.getX() + portee / 2.0D;
            y = r.nextDouble() * portee + p.getY() - portee / 2.0D;
        } else {
            x = r.nextDouble() * portee + p.getX() - portee / 2.0D;
            y = p.getY() + portee / 2.0D;
        }
        return new Position(x, y);
    }

    /**
     * Deplace une unite de maniere aleatoire dans les cotes d un cercle
     * de maniere a ne pas aller sur un batiment
     * @param plateau le plateau
     * @param p position du milieu de cercle
     * @param rayon la taille du rayon du cercle
     * @return un point au hasard dans la circonference du cercle
     */
    public static Position randomCercle(Plateau plateau, Position p, int rayon){
        Position position;
        do{
            do{
                position = randomCercle(p, rayon);
                System.out.println(p);
            } while ( !(position.getX() >= 0 && position.getX() < Grille.LARG_VILLAGE
                    && position.getY() >= 0 && position.getY() < Grille.HAUT_VILLAGE));
        }while( !plateau.getEtatPlateau().get(plateau.positions[position.getXint()][position.getYint()]));
        return position;
    }

    /**
     * Deplace une unite de maniere aleatoire dans les cotes d un cercle
     * @param p position du milieu de cercle
     * @param rayon la taille du rayon du cercle
     * @return un point au hasard dans la circonference du cercle
     */
    public static Position randomCercle(Position p, int rayon){
        double theta = Math.random() * 2 * Math.PI;
        double x = p.getXint() + rayon * Math.cos(theta);
        double y = p.getYint() + rayon * Math.sin(theta);
        return new Position(x,y);
    }

    /**
     * Deplace une unite de maniere semi aleatoire dans les cotes d un cercle
     * de maniere a ne pas aller sur un batiment
     * @param plateau le plateau
     * @param p position du milieu de cercle
     * @param rayon la taille du rayon du cercle
     * @return un point au hasard dans la circonference du cercle avec 50% de chance d aller vers le but
     */
    public static Position randomCerclePourcentage(Plateau plateau, Position p, int rayon, Position but){
        Position position;
        do{
            do{
                position = randomCerclePourcentage(p, rayon, but);
            } while ( !(position.getX() >= 0 && position.getX() < Grille.LARG_VILLAGE
                    && position.getY() >= 0 && position.getY() < Grille.HAUT_VILLAGE));
        }while( !plateau.getEtatPlateau().get(plateau.positions[position.getXint()][position.getYint()]));
        return position;
    }

    /**
     * Deplace une unite de maniere semi aleatoire dans les cotes d un cercle
     * @param p position du milieu de cercle
     * @param rayon la taille du rayon du cercle
     * @return un point au hasard dans la circonference du cercle avec 50% de chance d aller vers le but
     */
    public static Position randomCerclePourcentage(Position p, int rayon, Position but){
        double theta = Math.random() * 2 * Math.PI;
        double x = p.getXint() + rayon * Math.cos(theta);
        double y = p.getYint() + rayon * Math.sin(theta);
        Position tmp = new Position(x, y);
        double d = p.distance(but);
        double tmpDist = tmp.distance(but);
        if(tmpDist > Math.sqrt(d*d + rayon*rayon)) {
            theta = Math.random() * 2 * Math.PI;
            x = p.getXint() + rayon * Math.cos(theta);
            y = p.getYint() + rayon * Math.sin(theta);
        }
        return new Position(x,y);
    }

    public void deplace(double a, double b) {
        this.x = this.x + a;
        this.y = this.y + b;
    }

    /**
     * Permet le déplacement animé de nos unités
     * @param positions
     * @param vitesse
     */
    public void deplaceAnime(LinkedList<Position> positions, double vitesse){
        //TODO mettre x1 y1 en attribut de unite? pas besoin de les recalculer , juste une fois quand on clique
        Position b = positions.peek();
        if(b != null) { //if inutile, on verifie dans l appel que positions n est pas vide
            if (this.egales(b) && positions.size() > 1) {
                positions.remove();
                b = positions.peek();
            }
            double x1 = b.getX() - this.x;
            double y1 = b.getY() - this.y;
            double s = Math.sqrt(x1 * x1 + y1 * y1);
            x1 = x1 / s;
            y1 = y1 / s;
            if (s < 1 || this.distance(b) <= 2) {
                this.x = b.getX();
                this.y = b.getY();
                positions.remove();
            }
            if (this.different(b)) {
                this.deplace(x1 * vitesse, y1 * vitesse);
            }
        }
    }

    public double distance(Position b){
        return Math.sqrt((b.getX() - this.x)*(b.getX() - this.x) + (b.getY() - this.y)*(b.getY() - this.y));
    }

    public int distanceManhattan(Position b){
        return Math.abs(this.getXint() - b.getXint()) + Math.abs(this.getYint() - b.getYint());
    }

    public static LinkedList<Position>  simplifie(LinkedList<Position> positions){
        LinkedList<Position> res = new LinkedList<>();
        double x1 = 0;
        double x2 = 0;
        double y1 = 0;
        double y2 = 0;
        double s = 0;
        for(int i = 0; i < positions.size()-1; i++){
            x2 = x1;
            y2 = y1;
            Position p1 = positions.get(i);
            Position p2 = positions.get(i+1);
            x1 = p2.getX() - p1.getX();
            y1 = p2.getY() - p1.getYint();
            s = Math.sqrt(x1*x1 + y1*y1);
            x1 = x1/s;
            y1 = y1/s;

            //System.out.println(x1 + " " + y1);
            //System.out.println(x2 + " " + y2);

            if(x1 != x2 && y1 != y2) {
                res.add(p1);
            }
        }
        res.poll(); //on retire le point de départ
        res.add(positions.get(positions.size()-1)); //on ajoute le dernier point
        return res;
    }

    public static boolean enDehors(int a, int b){
        return a < 0 || a >= Grille.LARG_VILLAGE
                || b < 0 || b >= Grille.HAUT_VILLAGE;
    }

    public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }

    public int getXint(){
        return (int)this.x;
    }

    public int getYint(){
        return (int)this.y;
    }

    public double getCout() {
        return this.cout;
    }

    public void deplaceX(double a){
        this.x = this.x + a;
    }

    public void deplaceY(double a){
        this.y = this.y + a;
    }

    public void set(double a, double b){
        this.x = a;
        this.y = b;
    }

    public void setX(int a) {
        this.x = a;
    }

    public void setY(int a) {
        this.y = a;
    }

    public void setCout(double c) {
        this.cout = c;
    }

    public void incrCout(double c) {
        this.cout += c;
    }

    @Override
    public String toString() {
        return "[" +
                "" + (int)x +
                ", " + (int)y +
                "]";
    }

}

