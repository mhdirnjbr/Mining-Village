package modele;

import Affichage.Grille;
import modele.batiments.*;
import outils.*;
import modele.unites.*;
import ressources.*;

import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Plateau {

    private static final int PIECESDEPART = 1000;
    private static final int PAINDEPART = 300;

    //Nb pieces
    private int pieces = PIECESDEPART;
    private int pain = PAINDEPART;
    public static int PIECES_VICTOIRE = 5000;


    long debutTemps;
    //CopyOnWriteArrayList : meilleure version d'arraylist pour les threads mais plus couteux
    //liste de batiments
    private CopyOnWriteArrayList<Mine> mines = new CopyOnWriteArrayList<>();
    private ArrayList<Batiment> batiments = new ArrayList<>();
    //listes de chefs, ouvriers et ennemis
    private CopyOnWriteArrayList<Chef>chefs = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Ouvrier>ouvriers = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Ennemi>ennemis = new CopyOnWriteArrayList<>();

    public Position[][] positions = new Position[Grille.LARG_VILLAGE][Grille.HAUT_VILLAGE];

    //faux si le jeu est en pause
    public boolean enJeu = true;

    //Nombre de moulin
    private int nombreMoulins = 0;

    //Limites de construction
    public static int MAX_HDV = 1;
    public static int MAX_MARCHE = 1;
    public static int MAX_DEFENSE = 4;
    public static int MAX_MINE_OR = 2;
    public static int MAX_MINE_FER = 4;
    public static int MAX_MINE_CHARBON = 6;
    public static int MAX_MOULIN = 4;
    public static int MAX_MUR = 50;
    //Limites du nombre d unites
    public static int MAX_CHEF = 2;
    public static int MAX_OUVRIER = 10;
    public static int MAX_ENNEMIS = 5;

    //distance entre un chef et un dernier pour que ce dernier soit selectionnable
    public static int PORTEE_SELECTIONNABLE = 80;
    private Grille grille;
    public GraphPostion gP;

    public String messageDefaite;

    HashMap<Position, Boolean> etatPlateau;

    public Position aStarPoint;
    public LinkedList<Position> chemin = new LinkedList<>();

    //tests timer
    public TimerDemo minuteur;
    //temps apres lequel les ennemis apparaissent en secondes
    private static final int TEMPS_PROTECTION = 90;
    public boolean spawnEnnemi = false;


    public Plateau() throws IOException {
        debutTemps = System.currentTimeMillis();

        this.setTimer();

        //this.gP = this.creerGraph();
        this.etatPlateau = this.setEtatPlateau();

        Chef chef1 = new Chef(new Position(100,100));
        Chef chef2 = new Chef(new Position(10,100));

        Ouvrier ouvrier1 = new Ouvrier(new Position(150,200));
        Ouvrier ouvrier2 = new Ouvrier(new Position(150,150));

        this.chefs.add(chef1);
        //this.chefs.add(chef2);
        this.ouvriers.add(ouvrier1);
        this.ouvriers.add(ouvrier2);


        Ennemi ennemi1 = new Ennemi(new Position(400, 400));
        //this.ennemis.add(ennemi1);

        Position p = Position.randomRectangle(new Position
                (Grille.LARG_VILLAGE/2, Grille.HAUT_VILLAGE/2), Grille.LARG_VILLAGE + 30, Grille.HAUT_VILLAGE + 30);
        Ennemi ennemi2 = new Ennemi(p);
        //this.ennemis.add(ennemi2);

        Mine mine1 = new Mine(new Position(220,231), Item.OR);
        this.ajouteMine(mine1);

        Mine mine2 = new Mine(new Position(220,169), Item.OR);
        //this.ajouteMine(mine2);

        Mine mine3 = new Mine(new Position(220,200), Item.OR);
        //this.ajouteMine(mine3);

        Mine mine4 = new Mine(new Position(251,200), Item.OR);
        //this.ajouteMine(mine4);

        HotelDeVille hdv1 = new HotelDeVille(new Position(100,400));
        //this.ajouteBat(hdv1);


    }

    private void setTimer() {
        ///compte a rebours
        this.minuteur = new TimerDemo(TEMPS_PROTECTION);
        this.minuteur.timer.scheduleAtFixedRate(new TimerTask() {
            int i = TEMPS_PROTECTION;
            public void run() {
                i--;
                if (i == 0) {
                    spawnEnnemi = true;
                    SoundManager.play("alarmeEnnemi.wav");
                    SoundManager.play("musiqueEnnemi.wav");
                    System.out.println("Time Over");
                }
            }
        }, 0, 1000);
    }

    private HashMap<Position, Boolean> setEtatPlateau() {
        HashMap<Position, Boolean> res = new HashMap<>();
        for(int i = 0; i < Grille.LARG_VILLAGE; i++){
            for(int j = 0; j < Grille.HAUT_VILLAGE; j++){
                Position p = new Position(i,j);
                positions[i][j]=p;
                res.put(p, true);
            }
        }
        return res;
    }

    private GraphPostion creerGraph() {
        GraphPostion gP = new GraphPostion();

        long debut = System.currentTimeMillis();

        for(int i = 0; i < Grille.LARG_VILLAGE; i++){
            for(int j = 0; j < Grille.HAUT_VILLAGE; j++){
                Position p = new Position(i,j);
                positions[i][j]=p;
                gP.addVertex(p);
            }
        }

        for(Position p : gP.getAdjVertices().keySet()) {
            ArrayList<Position> adj = p.successeurs(this);
            for (Position ps : adj) {
                gP.addEdge(p, ps);
            }
        }
        System.out.println("temps création graphe : " + (System.currentTimeMillis()-debut) + "ms");

        for(Position p : gP.getAdjVertices().keySet()) {
            if(!p.different(new Position(Grille.LARG_VILLAGE-1,0))) System.out.print(gP.getAdjVertices(p));
        }

        return gP;
    }

    public void actualiseGraph(Batiment b){
        ArrayList<Position>positions = b.getPosition().positionsDansCarre(b.getTaille());
        for(Position p : positions) gP.removeVertex(p);
    }

    /**
     * ajoute une unite dans la liste correspondant a son type
     * @param u
     */
    public void ajouteUnite(Unite u){
        if(u.getType() == TypeUnite.OUVRIER) this.ouvriers.add((Ouvrier) u);
        else if(u.getType() == TypeUnite.CHEF) this.chefs.add((Chef) u);
        else this.ennemis.add((Ennemi) u);

    }

    public void ajouteUnite(TypeUnite type) throws IOException {
        if(type == TypeUnite.CHEF) this.chefs.add(new Chef());
        else if(type == TypeUnite.OUVRIER) this.ouvriers.add(new Ouvrier());
        else this.ennemis.add(new Ennemi());
    }

    /**
     * verifie si un ouvrier est dans un batiment
     * @param unite l unite
     * renvoie le batiment dans lequel est l unite et null sinon
     */
    public Batiment uniteDansBatiment(Unite unite){
        for(Batiment b : this.batiments)
            if(unite.getPosition().dansCarre(b.getPosition(), b.getTaille()/2.0)) return b;

        for(Mine m : this.mines)
            if(unite.getPosition().dansCarre(m.getPosition(), m.getTaille()/2.0)) return m;
        return null;
    }

    /**
     * verifie si une position est dans un batiment
     * @param position l unite
     * renvoie le batiment dans lequel est l unite et null sinon
     */
    public Batiment dansBatiment(Position position){
        for(Batiment b : this.batiments)
            if(position.dansCarre(b.getPosition(), b.getTaille()/2.0)) return b;

        for(Mine m : this.mines)
            if(position.dansCarre(m.getPosition(), m.getTaille()/2.0)) return m;
        return null;
    }

    /**
     * renvoie le batiment qui est dans la position en parametre
     */
    public Batiment getBatiment(Position p){
        for(Batiment b : this.batiments)
            if(p.dansCarre(b.getPosition(), b.getTaille()/2.0)) return b;
        for(Batiment b : this.mines)
            if(p.dansCarre(b.getPosition(), b.getTaille()/2.0)) return b;
        return null;
    }

    /**
     * ajoute une unite dans la liste correspondant a son type
     * @param type
     */
    public void ajouteUniteBatimentEnclic(TypeUnite type) throws IOException {
        Batiment batimentEnClic = null;
        for(Batiment b : batiments){
            if(b.enClic()){
                batimentEnClic = b;
            }
        }
        for(Mine b : mines){
            if(b.enClic()){
                batimentEnClic = b;
            }
        }
        if (batimentEnClic != null ){
            System.out.println("1 :" + batimentEnClic.getPosition());
            Position position = Position.randomCarre(this, batimentEnClic.getPosition(), batimentEnClic.getTaille());
            System.out.println("2 :" + batimentEnClic.getPosition());
            if(type == TypeUnite.CHEF && this.pieces > Chef.PRIX){
                this.pieces -= Chef.PRIX;
                Position depart = new Position(batimentEnClic.getPosition());
                Chef chef = new Chef(depart);
                this.chefs.add(chef);
                chef.setDeplaceSansAlgoRecherche(position.getXint(), position.getYint());
                System.out.println("3 :" + batimentEnClic.getPosition());
            }
            else if(type == TypeUnite.OUVRIER && this.pieces > Ouvrier.PRIX){
                this.pieces -= Ouvrier.PRIX;
                int xDepart = batimentEnClic.getPosition().getXint();
                int yDepart = batimentEnClic.getPosition().getYint();
                Position depart = new Position(xDepart, yDepart);
                Ouvrier ouvrier = new Ouvrier(depart);
                this.ouvriers.add(ouvrier);
                ouvrier.setDeplaceSansAlgoRecherche(position.getXint(), position.getYint());
            }
            else this.ennemis.add(new Ennemi(position));
        }

    }

    /**
     * retire une unite de la liste si elle n'est plus en vie
     */
    public void verifieEnVie(){
        for(Mine mine : this.mines) mine.verifieEnVie();
        for(Chef c : this.chefs){
            if(!c.enVie()) {
                if(c.getCptMort() <= 0) this.chefs.remove(c);
                else c.decrCptMort();
            }
        }
        for(Ouvrier o : this.ouvriers){
            if(!o.enVie()) {
                if(o.getCptMort() <= 0) this.ouvriers.remove(o);
                else o.decrCptMort();
            }
        }
        for(Ennemi e : this.ennemis){
            if(e.enDehorsTresLoin()) this.ennemis.remove(e);
            if(!e.enVie()) {
                if(e.getCptMort() <= 0) this.ennemis.remove(e);
                else e.decrCptMort();
            }
        }
    }

    /**
     * Verifie si un ouvrier est selectionnable
     */
    public void verifieSelectionnable(){
        for(Ouvrier o : this.ouvriers){
            boolean proche = false;
            boolean selection = false;
            for(Chef c : this.chefs){
                if(o.getPosition().dansRond(c.getPosition(), PORTEE_SELECTIONNABLE)) {
                    selection = true;
                    if(c.enClic() && !o.enTravail()) proche = true;
                }
            }
            o.changeSelectionnable(proche);
            if(!selection) {
                o.setEnClic(false);
            }
        }
    }

    /**
     * amene les ouvriers dans la mine la plus proche
     */
    public void ouvriersMine(){
        for(Ouvrier o : ouvriers) {
            if (o.enClic()) {
                if (!o.enTravail()) {
                    Mine mMin = null;
                    double minDist = Math.sqrt(Grille.LARG_VILLAGE * Grille.LARG_VILLAGE + Grille.HAUT_VILLAGE * Grille.HAUT_VILLAGE);
                    double dist;
                    for (Mine m : mines) {
                        dist = o.getPosition().distance(m.getPosition());
                        if (minDist > dist && m.getOuvriers().size() < Mine.CAPACITE) {
                            minDist = dist;
                            mMin = m;
                        }
                    }
                    if (mMin != null) {
                        o.setDeplace(this, mMin.getPosition().getXint(), mMin.getPosition().getYint());
                        mMin.ajouteOuvrier(o);
                    }
                } else {
                    quitteMine(o);
                }
            }
        }
    }

    /**
     * Fais quitter l'ouvrier de la mine
     * @param ouvrier l'ouvrier
     */
    public void quitteMine(Ouvrier ouvrier){
        ouvrier.changeTravail(false);
        for (Mine m : this.mines){
            m.enleveOuvrier(this, ouvrier);
        }
    }

    /**
     * renvoies la mine dans laquelle travaille un ouvrier
     * @param ouvrier
     * @return
     */
    public Mine getMine(Ouvrier ouvrier){
        for(Mine m : mines){
            for(Ouvrier o : m.getOuvriers()) if(ouvrier == o) return m;
        }
        return null;
    }

    /**
     * Collecte les ressources de l ouvrier en clic seulement, ou des ouvriers selectionnables si aucun n est en clic
     * Le chef en clic recupere toutes les ressources
     */
    public void collecteRessourcesOuvriers() {
        boolean ouvrierEnClic = false;
        Chef chefEnClic = null;
        for (Chef c : this.chefs) {
            if (c.enClic() || c.dernierEnClic()) chefEnClic = c;
        }
        if (chefEnClic != null) {
            for (Ouvrier o : this.ouvriers) {
                    if (o.enClic()) {
                        chefEnClic.recuperer(o);
                        ouvrierEnClic = true;
                    }

                }
            if (!ouvrierEnClic) {
                for (Ouvrier o : this.ouvriers) {
                    if (o.selectionnable()) chefEnClic.recuperer(o);
                }
            }
        }
    }

    public void enleveBat(Mine b){
        updateEtatPlateau(b, true);
        this.mines.remove(b);
    }

    /**
     * Change les positions en x et y qu'occupe le batiment a false
     * @param b
     */
    public void updateEtatPlateau(Batiment b, boolean bool){
        ArrayList<Position> points = b.getPosition().positionsDansCarre(b.getTaille());
        //ArrayList<Position> points = b.getPosition().positionsDansLosange(b.getTaille(), b.getTaille());

        for(Position p : points){
            this.etatPlateau.put(positions[p.getXint()][p.getYint()], bool);
        }
    }

    /**
     * ajoute un batiment dans la liste de batiments
     * @param b
     */
    public void ajouteBat(Batiment b){
        if(b.getType() == TypeBat.MOULIN) this.nombreMoulins++;
        updateEtatPlateau(b, false);
        this.batiments.add(b);
    }

    /**
     * ajoute une mine dans la liste de mines
     * @param m
     */
    public void ajouteMine(Mine m) {
        updateEtatPlateau(m, false);
        this.mines.add(m);
    }

    /**
     * la fonction qui renvoie la liste d'unite correspondant au type en parametre
     * @param u le type d'unite demande
     * @return la liste d'unites
     */
    public List<? extends Unite> getUnites(TypeUnite u){
        if(u == TypeUnite.OUVRIER) return this.ouvriers;
        else if(u == TypeUnite.CHEF) return this.chefs;
        else return this.ennemis;
    }

    /**
     * la fonction qui renvoie les unites du plateau
     * @return la liste d'unites
     */
    public ArrayList<Unite> getUnites(){
        ArrayList<Unite> unites = new ArrayList<>();
        unites.addAll(this.chefs);
        unites.addAll(this.ouvriers);
        unites.addAll(this.ennemis);

        return unites;
    }

    /**
     * La fonction qui ajoute toute la quantite de ressource à l'item correspondant puis les ajoute dans une liste
     * @return la liste de toutes les ressources
     */
    public ArrayList<Ressource> getRessources(){
        ArrayList<Ressource> ressources = new ArrayList<>();
        Ressource or = new Ressource(Item.OR);
        Ressource fer = new Ressource(Item.FER);
        Ressource charbon = new Ressource(Item.CHARBON);

        for(Chef c : this.chefs){
            for(Ressource r : c.getRessources()){
                if(r.getItem() == Item.OR) or.ajouteQuantite(r.getQuantite());
                else if(r.getItem() == Item.FER) fer.ajouteQuantite(r.getQuantite());
                else if(r.getItem() == Item.CHARBON) charbon.ajouteQuantite(r.getQuantite());
            }
        }

        ressources.add(or);
        ressources.add(fer);
        ressources.add(charbon);
        return ressources;
    }

    /**
     * Verifie si la limite de construction est atteinte pour le batiment en parametre
     *      * Cette fonction est aussi utilisee dans le cadre des conditions pour savoir si un bouton doit redevenir enable
     *      * @param batiment type de batiment pour lequel on veut tester si la limite est atteinte
     *      * @return true si oui, false sinon
     */
    public boolean verifieLimitesBatiments(TypeBat batiment){
        boolean resultat = false;
        int compteur=0;
        switch(batiment){
            case DEFENSE :
                for(Batiment b : batiments){
                    if(b.getType() == batiment){
                        compteur++;
                    }
                }
                resultat = (compteur >= (MAX_DEFENSE));
                break;
            case HDV :
                for(Batiment b : batiments){
                    if(b.getType() == batiment){
                        compteur++;
                    }
                }
                resultat = (compteur >= (MAX_HDV));
                break;
            case MARCHE :
                for(Batiment b : batiments){
                    if(b.getType() == batiment){
                        compteur++;
                    }
                }
                resultat = (compteur >= (MAX_MARCHE));
                break;
            case MINEOR :
                for(Mine b : mines){
                    if(b.getItem() == Item.OR){
                        compteur++;
                    }
                }
                resultat = (compteur >= (MAX_MINE_OR));
                break;
            case MINEFER :
                for(Mine b : mines){
                    if(b.getItem() == Item.FER){
                        compteur++;
                    }
                }
                resultat = (compteur >= (MAX_MINE_FER));
                break;
            case MINECHARBON :
                for(Mine b : mines){
                    if(b.getItem() == Item.CHARBON){
                        compteur++;
                    }
                }
                resultat = (compteur >= (MAX_MINE_CHARBON));
                break;
            case MOULIN :
                for(Batiment b : batiments){
                    if(b.getType() == batiment){
                        compteur++;
                    }
                }
                resultat = (compteur >= (MAX_MOULIN));
                break;
            case MUR :
                for(Batiment b : batiments){
                    if(b.getType() == batiment){
                        compteur++;
                    }
                }
                resultat = (compteur >= (MAX_MUR));
                break;
        }
        return resultat;
    }

    /**
     * Verifie si la limite de quantite d une unite est atteinte pour l unite en parametre
     * Cette fonction est aussi utilisee dans le cadre des conditions pour savoir si un bouton de recrutement chef / ouvrier
     * doit redevenir enable
     * @param unite type de l unite pour laquelle on veut tester si la limite est atteinte
     * @return true si oui, false sinon
     */
    public boolean verifieLimitesUnites(TypeUnite unite){
        boolean resultat = false;
        int compteur=0;
        switch(unite){
            case CHEF:
                for(Chef chef : chefs){
                    compteur++;
                }
                resultat = (compteur >= (MAX_CHEF));
                break;
            case OUVRIER:
                for(Ouvrier ouvrier : ouvriers){
                    compteur++;
                }
                resultat = (compteur >= (MAX_OUVRIER));
                break;
            case ENNEMI:
                break;

        }
        return resultat;
    }


    public void vendRessource(Ressource ressource){
        if(!(ressource.getItem() == Item.PIECE)) {
            SoundManager.play("vendrePieces.wav");
            pieces += ressource.getQuantite() * ressource.getPrix();
            System.out.println("Vous avez vendu " + ressource.getQuantite() + " " + ressource.getItem() + " au prix de : " + (ressource.getPrix()));
        }
    }

    public void achatBatiment(int prixBatiment){
        SoundManager.play("sonConstruction.wav");
        pieces-=prixBatiment;
    }

    public void attaque(Ennemi e){
        if(e.ouvrierProche != null){
            e.ouvrierProche.decrForme();
        }
        else if (e.batimentProche != null){
            if(e.batimentProche.getType()==TypeBat.MINE){
                e.batimentProche.decrVie();
            }
            else if(e.batimentProche.getType()==TypeBat.MOULIN){
                if(this.pain - Ennemi.QTITEPAIN >= 0) this.pain -= Ennemi.QTITEPAIN;
                else this.pain = Ennemi.QTITEPAIN;
            }
        }
    }

    /**
     * Verifie si un moulin est construit
     * @return true si oui, false sinon
     */
    public boolean estMoulinConstruit(){
        boolean resultat = false;
        for(Batiment b : this.batiments){
            if (b.getType() == TypeBat.MOULIN) {
                resultat = true;
                break;
            }
        }
        return resultat;
    }

    /**
     * Augmente le nombre de pain quand un moulin est construit
     */
    public void produitPain(){
        Random random = new Random();
        int valeur = random.nextInt(Moulin.PRODUCTION) + Moulin.PRODUCTION/2;
        this.pain += nombreMoulins*valeur;
    }

    /**
     * Reduit le nombre de pain
     */
    public void consomePain(int cons){
        Random random = new Random();
        int valeur = random.nextInt(cons)+cons/2;
        if(this.pain - valeur >= 0) this.pain -= valeur;
        else this.pain = 0;
    }

    /**
     * Quand il n'y a plus de nourriture, des unites meurent au hasard
     */
    public void plusDeNourriture(){
        if(pain == 0){
            List<Unite> unites =  Stream.concat(chefs.stream(), ouvriers.stream()).collect(Collectors.toList());
            Random random = new Random();
            int indice = random.nextInt(unites.size());
            if(unites.get(indice).getType() == TypeUnite.OUVRIER){
                this.ouvriers.get(indice - chefs.size()).tue();
            }else{
                this.chefs.get(indice).tue();
            }
        }
    }

    public boolean victoire(){
        return this.pieces >= PIECES_VICTOIRE;
    }

    public boolean defaite() {
        boolean chefsMorts = true;
        for (Chef chef : chefs) if (chef.enVie() || chef.getCptMort() >= 4) chefsMorts = false;
        boolean hdv = false;
        boolean marche = false;
        boolean mine = false;
        for (Batiment b : batiments){
            if (b.getType() == TypeBat.HDV) {
                hdv = true;
            }
            if (b.getType() == TypeBat.MARCHE) {
                marche = true;
            }
            if (b.getType() == TypeBat.MINE) {
                mine = true;
            }
        }
        int piecesTot = this.pieces;
        for(Ressource r : this.getRessources()){
            piecesTot+=r.getPieces();
        }
        boolean perduMine = !mine || ouvriers.isEmpty();
        boolean perduHdv = (this.ouvriers.size() == 0 && piecesTot < HotelDeVille.PRIX && !hdv)
                || (this.ouvriers.size() == 0 && hdv && piecesTot < Ouvrier.PRIX);
        boolean perduMarche = !marche && piecesTot < Marche.PRIX;
        if(chefsMorts) messageDefaite = "Vous n'avez plus de chefs...";
        if(perduHdv) messageDefaite = "Vous n'aves plus d'ouvriers...";
        if(perduMarche) messageDefaite = "Vous n'avez plus d'argent...";
        return chefsMorts || this.chefs.size() == 0 || perduHdv || perduMarche;
    }

    public CopyOnWriteArrayList<Chef> getChefs(){
        return this.chefs;
    }

    public CopyOnWriteArrayList<Ouvrier> getOuvriers(){
        return this.ouvriers;
    }

    public CopyOnWriteArrayList<Ennemi> getEnnemis(){
        return this.ennemis;
    }

    public ArrayList<Batiment> getBatiments() {
        return batiments;
    }

    public CopyOnWriteArrayList<Mine> getMines() {
        return mines;
    }

    public ArrayList<Batiment> getMinesBats(){
        ArrayList<Batiment> tmp = new ArrayList<>(batiments);
        tmp.addAll(mines);
        return tmp;
    }

    /**
     * Guetter qui renvoie le nombre de pieces
     * @return les pieces restantes
     */
    public int getNbrePieces() {
        return pieces;
    }

    public Mine getMineEnClic(){
        Mine mineEnClic = null;
        for(Mine m : this.mines) if(m.enClic()) mineEnClic = m;
        return mineEnClic;
    }

    public int getPain(){
        return this.pain;
    }

    public int getTimer(){
        return (int)(System.currentTimeMillis() - debutTemps)/1000;
    }

    /**
     * Permet de remove un ouvrier pour faire des tests
     * @param ouvrier
     */
    public void enleveUnite(TypeUnite ouvrier) {
        batiments.remove(batiments.size()-1);

    }

    public HashMap<Position, Boolean> getEtatPlateau() {
        return etatPlateau;
    }

    @Override
    public String toString() {
        return "bats " + (batiments.size() + mines.size()) + "modele/unites " + getUnites().size() ;
    }
}
