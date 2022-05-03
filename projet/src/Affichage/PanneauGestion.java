package Affichage;

import controleur.ControleurGrille;
import controleur.ControleurPanneauGestion;
import modele.batiments.*;
import modele.Plateau;
import outils.Position;
import outils.SoundManager;
import ressources.*;
import modele.unites.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/* Pour enlever un bouton dans actionperformed ici, ajouter
Container parent = ajouter.getParent();
parent.remove(ajouter);
*/

public class PanneauGestion extends JPanel {

    //Largeur du village
    public static final int LARG_PANNEAU = 200;

    //Hauteur du village
    public static final int HAUT_PANNEAU = 550;

    //Ordonnee du village
    public static final int ORDONNEE_PANNEAU = 0;

    //Abscisse du village
    public static final int ABSCISSE_PANNEAU = 0;

    private Plateau plateau;
    private ControleurPanneauGestion cPG;
    private ControleurGrille c;

    /*private JButton valider;
    private JButton ajouter;*/
    private JButton miner;
    private JButton recuperer;
    private JButton information;

    private JButton construire;
    private JButton mine_or;
    private JButton mine_fer;
    private JButton mine_charbon;
    private JButton defense;
    private JButton hdv;
    private JButton marche;
    private JButton mur;
    private JButton plusMur, moinsMur, tournerMur;
    private JButton moulin;

    private JButton quitterMine1, quitterMine2, quitterMine3, quitterMine4, quitterMine5;

    private JButton recruterOuvrier;
    private JButton recruterChef;
    private JButton vendreRessources;

    // pour savoir quel batiment le joueur veut construire
    private TypeBat construction;
    public boolean enConstruction = false;

    //etats du bouton construire:
    // false : pas encore clique,
    // true : construction du batiment
    public boolean etatConstruire = false;

    private Chef chefEnClic;
    private Mine mineEnClic;

    private BufferedImage imageOr, imageFer, imageCharbon, imagePiece, imagePain, coeur;
    private BufferedImage cadre, testCadre, chefCadre, hdvCadre, marcheCadre, mineOrCadre, mineFerCadre, mineCharbonCadre, ouvrierCadre, defenseCadre, moulinCadre, murCadre;

    private BufferedImage fondPanneauGestion;

    private Position positionChef;

    //pour le rectangle transparant
    private boolean fantome = false;
    //pour le rectangle transparant lors de la construction
    public boolean construisible;

    // les 10 cases representent les 10 boutons possiblement desactivables a cause des limites l'ordre est le meme
    // que dans les if au debut de ActionListenner de construire
    // si la case numero x est true, alors le bouton correspondant est actuellement pas enable
    // cela permet de changer le setEnable et le setBackground des boutons seulement si ils sont en effet actuellement desactivés
    private boolean[] boutonsNonEnable = new boolean[10];

    private boolean enConstructionDeMurs = false; //vrai si on appuis sur le bouton Mur
    private int nombreMurs = 1; //nombre de fois qu'on appuis sur plusMur
    private boolean estTourneMur = false; //indique si on veut tourner le mur que l'on souhaite construire

    private boolean estEnInfo = false;
    private TypeBat dernierBatEnClic; //pour memoriser le dernier batiment ou unite en clic si on clic sur le bouton information
    private TypeUnite dernierUniteEnClic;       // alors qu on etait enclic dans un batiment, ca permet de reafficher les boutons du batiment

    JLabel jlabelFond;
    ImageIcon imageIconSoleil;
    Image imageSoleil;

    public PanneauGestion(Plateau p) {
        super();

        this.plateau = p;

        //Définition de la position et les dimensions du village
        this.setBounds(ORDONNEE_PANNEAU, ABSCISSE_PANNEAU, LARG_PANNEAU, HAUT_PANNEAU);

        //Définition de la coulour au fond
        this.setBackground(new Color(127, 151, 108));
        this.setBackground(new Color(135, 85, 54));


        //Définition de la coulour de bordure
        this.setBorder(new LineBorder(Color.BLACK));

        this.setLayout(null);

        //test fond
        /*String imgUrlFond="images/fondPG.png";
        BufferedImage imgFond = null;
        try {
            imgFond = ImageIO.read(new File(imgUrlFond));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Image dimgFond = imgFond.getScaledInstance(200,550, Image.SCALE_SMOOTH);
        imageIconSoleil = new ImageIcon(dimgFond);
        jlabelFond = new JLabel(imageIconSoleil);

        //jlabelFond.setBounds(20,20,1000, 400);
        jlabelFond.setOpaque(false);

        imageSoleil = imageIconSoleil.getImage();*/

        this.setBoutonInfo();

        this.setBoutons();

        this.setBoutonsConstruction();

        this.setQuitterMine();

        this.setImages();

    }

    public void setControleur(Grille g){
        this.cPG = new ControleurPanneauGestion(this, g, this.plateau);
        this.addMouseListener(cPG);
        this.c = g.getControleur();
    }

    public boolean estChefEnClic(){
        boolean resulat = false;
        for(Chef c : plateau.getChefs()){
            if(c.enClic()){
                this.dernierUniteEnClic = TypeUnite.CHEF;
                this.dernierBatEnClic = null;
                resulat = true;
            }
        }
        return resulat;
    }

    public boolean estOuvrierEnClic(){
        boolean resulat = false;
        for(Ouvrier o : plateau.getOuvriers()){
            if(o.enClic()){
                this.dernierUniteEnClic = TypeUnite.OUVRIER;
                this.dernierBatEnClic = null;
                resulat = true;
            }
        }
        return resulat;
    }

    /**
     * Pour voir si un mur, un moulin ou une defense est en clic pour afficher son cadre
     * @param batiment a voir si il est en clic
     * @return vrai si il y en a un en clic, faux sinon
     */
    public boolean estBatEnClic(TypeBat batiment){
        boolean resulat = false;
        for(Batiment o : plateau.getBatiments()){
            if(o.enClic() && o.getType() == batiment){
                this.dernierUniteEnClic = null;
                resulat = true;
            }
        }
        return resulat;
    }

    public boolean estMineEnClic(){
        boolean resulat = false;
        for(Mine m : plateau.getMines()){
            if(m.enClic()){
                this.dernierBatEnClic = TypeBat.MINE;
                this.dernierUniteEnClic = null;
                resulat = true;
            }
        }
        return resulat;
    }

    public boolean estMineEnClicItem(Item i){
        boolean resulat = false;
        for(Mine m : plateau.getMines()){
            if(m.enClic() && m.getItem() == i){
                resulat = true;
            }
        }
        return resulat;
    }


    public boolean estHdvEnClic(){
        boolean resulat = false;
        for(Batiment o : plateau.getBatiments()){
            if(o.enClic() && o.getType() == TypeBat.HDV){
                this.dernierBatEnClic = TypeBat.HDV;
                this.dernierUniteEnClic = null;
                resulat = true;
            }
        }
        return resulat;
    }

    public boolean estMarcheEnClic(){
        boolean resulat = false;
        for(Batiment o : plateau.getBatiments()){
            if(o.enClic() && o.getType() == TypeBat.MARCHE){
                this.dernierBatEnClic = TypeBat.MARCHE;
                this.dernierUniteEnClic = null;
                resulat = true;
            }
        }
        return resulat;
    }

    @Override
    public void paint(Graphics g){
        super.paint(g);

        g.drawString(plateau.getTimer()+"s",  22, 15);

        //g.drawImage(imageSoleil, 0,0,null);

        //actualise les derniers en clic pour le bouton information
        this.estMineEnClic();
        this.estMarcheEnClic();
        this.estHdvEnClic();

        //Affichage des boutons de chef et ouvrier seulement si ils sont en clic
        if(this.estChefEnClic() && !this.estEnInfo){
            this.construire.setVisible(true);
            this.recuperer.setVisible(true);
        }else{
            this.construire.setVisible(false);
            this.recuperer.setVisible(false);
        }

        if(this.estOuvrierEnClic() && !this.estEnInfo){
            this.miner.setVisible(true);
        }else{
            this.miner.setVisible(false);
        }

        this.drawRessources(g);

        //Bouton information
        if(this.estEnInfo){
            g.setFont(new Font("", Font.PLAIN, 14));
            g.drawString("Bienvenue dans Mining Village!", 2,70);
            g.drawString("récupérez les minerais enfouis" , 2,85);
            g.drawString("dans les profondeur pour bâtir", 2, 100);
            g.drawString("un empire et atteindre les", 2, 115);
            g.drawString(Plateau.PIECES_VICTOIRE +" pièces le plus ", 2, 130);
            g.drawString("rapidement possible.", 2, 145);
            g.drawString("Mais attention ! ", 2,160);
            g.drawString("Des barbares rôdent, vont-ils ", 2, 175);
            g.drawString("remarquer votre présence ?", 2, 190);

        }else{
            //remet visible les boutons qui etaient caché a cause du bouton information
            if(dernierBatEnClic == null && dernierUniteEnClic != null) this.setVisibleGroupeBoutons(null, dernierUniteEnClic, true);
            else if(dernierBatEnClic != null && dernierUniteEnClic == null) this.setVisibleGroupeBoutons(dernierBatEnClic, null, true);


            //cadres
            g.drawImage(cadre, -10, 15, null);
            Font tmp = g.getFont();
            g.setFont(new Font("", Font.CENTER_BASELINE, 14));
            if(estChefEnClic()){
                g.drawString("CHEF", 80, 218);
                g.drawImage(chefCadre, 40, 65, null);
            }
            else if(estOuvrierEnClic()){
                g.drawString("OUVRIER", 73, 218);
                g.drawImage(chefCadre, 40, 65, null);

                //affichage de la forme
                for(Ouvrier o : this.plateau.getOuvriers()){
                    if(o.enClic()){
                        int forme = o.getForme();
                        g.drawString(""+ forme, 180, 218);
                    }
                }
                g.drawImage(coeur, 165, 208, 10, 10, null);
                g.drawImage(ouvrierCadre, 40, 65, null);
            }
            else if(estHdvEnClic()) {
                g.drawString("HOTEL DE VILLE", 45, 225);
                g.drawImage(hdvCadre, 40, 65, null);
            }
            else if(estMarcheEnClic()) {
                g.drawString("MARCHE", 68, 225);
                g.drawImage(marcheCadre, 40, 65, null);
            }
            else if(estMineEnClicItem(Item.OR)) {

                g.drawString("Mine d'or", 75, 225);
                g.drawImage(mineOrCadre, 40, 65, null);
            }
            else if(estMineEnClicItem(Item.FER)) {

                g.drawString("Mine de fer", 60, 225);
                g.drawImage(mineFerCadre, 40, 65, null);
            }
            else if(estMineEnClicItem(Item.CHARBON)) {

                g.drawString("Mine de charbon", 48, 225);
                g.drawImage(mineCharbonCadre, 40, 65, null);
            }
            else if(estBatEnClic(TypeBat.MOULIN)) {

                g.drawString("Moulin", 78, 225);
                g.drawImage(moulinCadre, 40, 65, null);
            }
            else if(estBatEnClic(TypeBat.DEFENSE)) {

                g.drawString("Defense", 68, 225);
                g.drawImage(defenseCadre, 40, 65, null);
            }
            else if(estBatEnClic(TypeBat.MUR)) {

                g.drawString("Mur", 88, 225);
                g.drawImage(murCadre, 40, 65, null);
            }
            else{
                g.drawImage(testCadre, 40, 65, 125, 125, null);
            }
            g.setFont(tmp);

            //pour cacher les boutons de murs si on selectionne un autre batiment a construire
            if(this.construction != TypeBat.MUR){
                enConstructionDeMurs = false;
                this.plusMur.setVisible(false);
                this.moinsMur.setVisible(false);
                this.tournerMur.setVisible(false);
            }


            this.drawUniteSelectionne(g);
            this.drawEtatOuvriersDansMine(g);
            this.drawHdvSelectionne();
            this.drawMarcheSelectionne();

        }
        //drawEtatOuvriers(g);
        this.changeQuitterMine();
        this.actualisePosChef();
        this.actualiseQuitterMine();


    }

    public void setVisibleGroupeBoutons(TypeBat batiment, TypeUnite unite ,boolean b){
        if(!estMarcheEnClic() && !estOuvrierEnClic() && !estHdvEnClic() && !estChefEnClic() && !estMineEnClic()){
            this.recruterOuvrier.setVisible(false);
            this.recruterChef.setVisible(false);
            this.vendreRessources.setVisible(false);
            this.miner.setVisible(false);
            this.recuperer.setVisible(false);
            this.construire.setVisible(false);
        }else {
            if (batiment == TypeBat.HDV && unite == null) {
                this.recruterOuvrier.setVisible(b);
                this.recruterChef.setVisible(b);
            } else if (batiment == TypeBat.MARCHE && unite == null) {
                this.vendreRessources.setVisible(b);
            } else if (batiment == TypeBat.MINE && unite == null) {
                this.quitterMine1.setVisible(b);
                this.quitterMine2.setVisible(b);
                this.quitterMine3.setVisible(b);
                this.quitterMine4.setVisible(b);
                this.quitterMine5.setVisible(b);
            }
            //ouvrier
            else if (unite == TypeUnite.OUVRIER && batiment == null) {
                this.miner.setVisible(b);
            } else if (unite == TypeUnite.CHEF && batiment == null) {
                //chef
                this.recuperer.setVisible(b);
                this.construire.setVisible(b);
                //enConstruction
                if (enConstruction) {
                    this.mine_or.setVisible(b);
                    this.mine_fer.setVisible(b);
                    this.mine_charbon.setVisible(b);
                    this.defense.setVisible(b);
                    this.hdv.setVisible(b);
                    this.marche.setVisible(b);
                    this.mur.setVisible(b);
                    this.moulin.setVisible(b);

                    if (enConstructionDeMurs) {
                        this.plusMur.setVisible(b);
                        this.moinsMur.setVisible(b);
                        this.tournerMur.setVisible(b);
                    }
                }


            }
        }

    }


    /*public void paintComponent(Graphics g){
    //Chargement de l"image de fond
        try {
            Image img = ImageIO.read(new File("images/fondPG.png"));
            g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur image de fond: " +e.getMessage());
        }
    }*/

    /**
     * affiche les ressources possedees par les chefs
     * @param g
     */
    private void drawRessources(Graphics g) {
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.setColor(Color.WHITE);

        g.drawImage(imagePiece, 70, -1, null);
        g.drawString("" + this.plateau.getNbrePieces(), 105, 17);

        g.drawImage(imagePain, 140, 20, null);
        g.drawString("" + plateau.getPain(), 170, 40);

        g.drawImage(imageOr, -10, 20, null);
        g.drawImage(imageFer, 40, 20, null);
        g.drawImage(imageCharbon, 100, 23, null);

        ArrayList<Ressource> rs = this.plateau.getRessources();

        g.drawString("" + rs.get(0).getQuantite(), 38, 40);
        g.drawString("" + rs.get(1).getQuantite(), 86, 40);
        g.drawString("" + rs.get(2).getQuantite(), 131, 40);


    }

    /**
     * affiche les ressources du chef selectionne
     * @param g
     */
    private void drawUniteSelectionne(Graphics g) {
        int i = 20;
        int j = 0;
        if(estChefEnClic() || estOuvrierEnClic()){ g.drawRect(2, 205, 195, 38);}

        for (Unite u : this.plateau.getUnites()) {
            j++;
            if(u.enClic()){
                String uniteType = u.getType() == TypeUnite.CHEF ? "Chef " : "Ouvrier ";
                //g.drawString(uniteType + j + " : " + u.toString(), 5, 200+i);
                g.drawString( "Or :  " + u.getItem(Item.OR), 5, 215+i);
                g.drawString( "Fer :  " + u.getItem(Item.FER), 55, 215+i);
                g.drawString( "Charbon :  " + u.getItem(Item.CHARBON), 105, 215+i);
                //g.drawString( "P " + u.getItem(Item.PIECE), 155, 215+i);
                i+=10;
            }
        }
    }

    /**
     * fonction qui affiche l etat de la forme de l ouvrier et si il travail ou non
     * @param g
     */
    private void drawEtatOuvriers(Graphics g){
        int i = HAUT_PANNEAU-10;
        int j = 0;
        for(Ouvrier o : this.plateau.getOuvriers()){
            j++;
            if(o.selectionnable()) {
                if (o.enTravail()) {
                    g.drawString("Ouvrier " + j + " travaille, forme : ", 5, i);
                    for (int n = 0; n < o.getForme(); n++) {
                        g.fillRect(LARG_PANNEAU - 4 - (n * 5), i - 5, 4, 4);
                    }
                } else {
                    g.drawString("Ouvrier " + j + " se repose, forme : ", 5, i);
                    for (int n = 0; n < o.getForme(); n++) {
                        g.fillRect(LARG_PANNEAU - 4 - (n * 5), i - 5, 4, 4);
                    }
                }
                i-=10;
            }

        }
    }

    /**
     * fonction qui affiche l etat de la forme de l ouvrier dans la mine selectionnee et si il travail ou non
     * @param g
     */
    private void drawEtatOuvriersDansMine(Graphics g){
        int i = HAUT_PANNEAU-10;
        int j = 0;
        for(Mine m : this.plateau.getMines()) {
            if(m.enClic()) {
                for (Ouvrier o : m.getOuvriers()) {
                    j++;
                    if (o.enTravail()) {
                        g.drawString("Ouvrier " + j + " travaille, forme : " + o.getForme(), 5, i);
                        /*for (int n = 0; n < o.getForme(); n++) {
                            g.fillRect(LARG_PANNEAU - 4 - (n * 5), i - 5, 4, 4);
                        }*/
                    } else {
                        g.drawString("Ouvrier " + j + " se repose, forme : " + o.getForme(), 5, i);
                        /*for (int n = 0; n < o.getForme(); n++) {
                            g.fillRect(LARG_PANNEAU - 4 - (n * 5), i - 5, 4, 4);
                        }*/
                    }
                    i -= 25;

                }
            }
        }

    }

    /**
     * fonction qui affiche les boutons de recrutement apres avoir selectionne un Hotel de Ville
     */
    private void drawHdvSelectionne() {
        //tests pour voir si il faut remettre enable les boutons de recrutement
        //on remet enable si on est plus a la limite ET si le bouton est actuellement pas enable
        if (!plateau.verifieLimitesUnites(TypeUnite.CHEF) && boutonsNonEnable[8]) {
            recruterChef.setEnabled(true);
            //recruterChef.setBackground(Color.white);
            boutonsNonEnable[8] = false;
        }
        if (!plateau.verifieLimitesUnites(TypeUnite.OUVRIER) && boutonsNonEnable[9]) {
            recruterOuvrier.setEnabled(true);
            //recruterOuvrier.setBackground(Color.white);
            boutonsNonEnable[9] = false;
        }
        //test pour voir si les limites sont atteintes et donc desactiver les boutons
        if (plateau.verifieLimitesUnites(TypeUnite.CHEF)) {
            recruterChef.setEnabled(false);
            boutonsNonEnable[8] = true;
            //recruterChef.setBackground(Color.red);
        }
        if (plateau.verifieLimitesUnites(TypeUnite.OUVRIER)) {
            recruterOuvrier.setEnabled(false);
            boutonsNonEnable[9] = true;
            //recruterOuvrier.setBackground(Color.red);
        }
        for (Batiment HDV : plateau.getBatiments()) {
            if (HDV.getType() == TypeBat.HDV) {
                if (HDV.enClic() && existeChefDansHdv()) {
                    //System.out.println("SETVISIBLE HDV= " );
                    this.recruterChef.setVisible(true);
                    this.recruterOuvrier.setVisible(true);
                    break;
                } else {
                    this.recruterChef.setVisible(false);
                    this.recruterOuvrier.setVisible(false);
                }
            }
        }
    }

    /**
     * Regarde si il y a un chef qui est dans un Marche
     * @return true si oui, false sinon
     */
    public boolean existeChefDansMarche(){
        boolean resultat =false;
        for(Chef chef : plateau.getChefs()){
            if(chef.dansMarche()) resultat = true;
        }
        return resultat;
    }

    /**
     * Regarde si il y a un chef qui est dans un Hdv
     * @return true si oui, false sinon
     */
    public boolean existeChefDansHdv(){
        boolean resultat =false;
        for(Chef chef : plateau.getChefs()){
            if(chef.dansHdv()) resultat = true;
        }
        return resultat;
    }

    /**
     * fonction qui affiche le bouton de vente apres avoir selectionne un Marche
     */
    private void drawMarcheSelectionne(){
        for(Batiment marche : plateau.getBatiments()){
            if(marche.getType() == TypeBat.MARCHE){
                if(marche.enClic() && existeChefDansMarche()){
                    //System.out.println("SETVISIBLE MARCHE = " );
                    this.vendreRessources.setVisible(true);
                    break;
                }else{
                    this.vendreRessources.setVisible(false);
                }
            }
        }

    }

    private void setImages() {
        /** Partie concernant les icons des ressources **/
        String imgUrl1="images/ressources/or.png";
        try {
            this.imageOr = ImageIO.read(new File(imgUrl1));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imgUrl2="images/ressources/charbon.png";
        try {
            this.imageCharbon = ImageIO.read(new File(imgUrl2));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imgUrl3="images/ressources/fer.png";
        try {
            this.imageFer = ImageIO.read(new File(imgUrl3));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imgUrl4="images/ressources/piece.png";
        try {
            this.imagePiece = ImageIO.read(new File(imgUrl4));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imgUrl6="images/ressources/pain.png";
        try {
            this.imagePain = ImageIO.read(new File(imgUrl6));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imgUrl7="images/cadres/cadre.png";
        try {
            this.cadre = ImageIO.read(new File(imgUrl7));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imgUrl8="images/cadres/photoCadre.png";
        try {
            this.testCadre = ImageIO.read(new File(imgUrl8));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imgUrl9="images/cadres/chefCadre.jpg";
        try {
            this.chefCadre = ImageIO.read(new File(imgUrl9));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imgUrl10="images/cadres/cadreHDV.jpg";
        try {
            this.hdvCadre = ImageIO.read(new File(imgUrl10));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imgUrl11="images/cadres/cadreMarche.jpg";
        try {
            this.marcheCadre = ImageIO.read(new File(imgUrl11));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imgUrl12="images/cadres/mineCadre.jpg";
        try {
            this.mineOrCadre = ImageIO.read(new File(imgUrl12));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imgUrl13="images/cadres/cadreOuvrier.jpg";
        try {
            this.ouvrierCadre = ImageIO.read(new File(imgUrl13));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imgUrl14="images/cadres/cadreDefense.jpg";
        try {
            this.defenseCadre = ImageIO.read(new File(imgUrl14));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imgUrl15="images/cadres/cadreMoulin.jpg";
        try {
            this.moulinCadre = ImageIO.read(new File(imgUrl15));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imgUrl16="images/cadres/cadreMur.jpg";
        try {
            this.murCadre = ImageIO.read(new File(imgUrl16));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imgUrl17="images/cadres/cadreMineFer.jpg";
        try {
            this.mineFerCadre = ImageIO.read(new File(imgUrl17));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imgUrl18="images/cadres/cadreMineCharbon.jpg";
        try {
            this.mineCharbonCadre = ImageIO.read(new File(imgUrl18));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imgUrl19="images/coeur.png";
        try {
            this.coeur = ImageIO.read(new File(imgUrl19));
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*String imgUrl5 = "images/fondPanneauGestion.png";
        try {
            this.fondPanneauGestion = ImageIO.read(new File(imgUrl5));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private void setQuitterMine(){
        this.quitterMine1 = new JButton(new ImageIcon("images/quitterBouton.png"));
        this.add(quitterMine1);

        this.quitterMine2 = new JButton(new ImageIcon("images/quitterBouton.png"));
        this.add(quitterMine2);

        this.quitterMine3 = new JButton(new ImageIcon("images/quitterBouton.png"));
        this.add(quitterMine3);

        this.quitterMine4 = new JButton(new ImageIcon("images/quitterBouton.png"));
        this.add(quitterMine4);

        this.quitterMine5 = new JButton(new ImageIcon("images/quitterBouton.png"));
        this.add(quitterMine5);

        quitterMine1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!enConstruction) {
                    quitterMine1.setVisible(false);
                }
            }
        });

        quitterMine2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!enConstruction) {
                    quitterMine2.setVisible(false);
                }
            }
        });

        quitterMine3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!enConstruction) {
                    quitterMine3.setVisible(false);
                }
            }
        });

        quitterMine4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!enConstruction) {
                    quitterMine4.setVisible(false);
                }
            }
        });

        quitterMine5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!enConstruction) {
                    quitterMine5.setVisible(false);
                }
            }
        });

        setVisibleQuitterMine(0);

        quitterMine1.setBounds(LARG_PANNEAU - 25,HAUT_PANNEAU - 25,20,20);
        quitterMine2.setBounds(LARG_PANNEAU - 25,HAUT_PANNEAU - 50,20,20);
        quitterMine3.setBounds(LARG_PANNEAU - 25,HAUT_PANNEAU - 75,20,20);
        quitterMine4.setBounds(LARG_PANNEAU - 25,HAUT_PANNEAU - 100,20,20);
        quitterMine5.setBounds(LARG_PANNEAU - 25,HAUT_PANNEAU - 125,20,20);

    }


    public void actionMiner(){
        if(!enConstruction)
            plateau.ouvriersMine();
    }

    public boolean estBatimentEnClic(TypeBat bat){
        boolean resultat = false;
        if(bat == TypeBat.HDV) {
            for (Batiment b : plateau.getBatiments()) {
                if (b.getType() == TypeBat.HDV && b.enClic()) {
                    resultat = true;
                }
            }
        }else if(bat == TypeBat.MARCHE){
            for (Batiment b : plateau.getBatiments()) {
                if (b.getType() == TypeBat.MARCHE && b.enClic()) {
                    resultat = true;
                }
            }
        }
        else if(bat == TypeBat.MINE){
            for (Batiment b : plateau.getMines()) {
                if ((b.getType() == TypeBat.MINE || b.getType() == TypeBat.MINEFER || b.getType() == TypeBat.MINEOR || b.getType() == TypeBat.MINECHARBON) && b.enClic()) {
                    resultat = true;
                }
            }
        }
        return resultat;
    }

    /**
     * Action lors du clic sur le bouton information
     * estEnInfo devient true/false
     * et on cache les boutons qui etaient visible lors du clic sur information
     */
    public void actionInformation(){
        if(estEnInfo) this.estEnInfo = false;
        else this.estEnInfo = true;

        if(estBatimentEnClic(TypeBat.HDV)){
            this.setVisibleGroupeBoutons(TypeBat.HDV, null, false);
            this.dernierBatEnClic = TypeBat.HDV;
        }else if(estBatimentEnClic(TypeBat.MARCHE)){
            this.setVisibleGroupeBoutons(TypeBat.MARCHE, null, false);
            this.dernierBatEnClic = TypeBat.MARCHE;
        }else if(estBatimentEnClic(TypeBat.MINE) || estBatimentEnClic(TypeBat.MINECHARBON) || estBatimentEnClic(TypeBat.MINEFER) || estBatimentEnClic(TypeBat.MINEOR) ){
            this.setVisibleGroupeBoutons(TypeBat.MINE, null, false);
            this.dernierBatEnClic = TypeBat.MINE;
        }else if(estOuvrierEnClic()){
            this.setVisibleGroupeBoutons(null, TypeUnite.OUVRIER, false);
            this.dernierUniteEnClic = TypeUnite.OUVRIER;
        }else if(estChefEnClic()){
            this.setVisibleGroupeBoutons(null, TypeUnite.CHEF, false);
            this.dernierUniteEnClic = TypeUnite.CHEF;
        }

    }

    public void actionRecuperer(){
        if(!enConstruction)
            plateau.collecteRessourcesOuvriers();
    }

    public void actionRecruterChef() throws IOException {
        /*if (plateau.verifieLimitesUnites(TypeUnite.CHEF)) {
                        recruterChef.setEnabled(false);
                        boutonsNonEnable[8] = true;
                        recruterChef.setBackground(Color.red);
                    }*/
        if(!enConstruction)
            plateau.ajouteUniteBatimentEnclic(TypeUnite.CHEF);
    }

    public void actionRecruterOuvrier() throws IOException {
        /*if (plateau.verifieLimitesUnites(TypeUnite.OUVRIER)) {
                        recruterOuvrier.setEnabled(false);
                        boutonsNonEnable[9] = true;
                        recruterOuvrier.setBackground(Color.red);
                    }*/
        if(!enConstruction)
            plateau.ajouteUniteBatimentEnclic(TypeUnite.OUVRIER);
    }

    public void actionVendreRessources(){
        if(!enConstruction) {
            ArrayList<Ressource> ressources = new ArrayList<>();
            for (Chef chef : plateau.getChefs()) {
                if (chef.dansMarche()) {
                    ressources = chef.getRessources();
                    //on ne compte pas les pieces cad ressources.get(4)
                    if (ressources.get(0).getQuantite() > 0 || ressources.get(1).getQuantite() > 0 || ressources.get(2).getQuantite() > 0) {
                        //System.out.println("ressources.size() = " + ressources.size());
                        for (int i = 0; i < 3; i++) {
                            plateau.vendRessource(ressources.get(i));
                            SoundManager.play("vendrePieces.wav");
                            ressources.get(i).setQuantite(0);
                        }
                    } else {
                        System.out.println("PAS DE RESSOURCES A VENDRE !!");
                    }
                } else {
                    //System.out.println("PAS DE CHEF DANS UN MARCHE !!");
                }
            }
        }
    }

    public void actionBoutonConstruire(){
        verifLimitesPourBoutons();
        verifLimitesBatiments();
        //enleve les 3 boutons des murs
        this.plusMur.setVisible(false);
        this.moinsMur.setVisible(false);
        this.tournerMur.setVisible(false);

        if(etatConstruire) {
            etatConstruire = false;
            enConstruction = false;
            if (construction != null) {
                actionConstruire();
                construction = null;
            }
            afficheBoutonConstructionBats(false);
        }
        else{
            for(Chef c : plateau.getChefs()){
                if(c.enClic()) chefEnClic = c;
            }
            //cas de depart ou on'a encore pas selectionne de chef
            if(chefEnClic != null) {
                //cas ou on a selectionne un chef puis deselectionne plus tard
                if(chefEnClic.enClic()) {
                    etatConstruire = true;
                    afficheBoutonConstructionBats(true);
                    enConstruction = true;
                }
            }
        }
    }
    private void setBoutonInfo(){
        this.information = new JButton(new ImageIcon("images/infoBouton.png"));
        //information.setVisible(false);
        information.setFocusable(false);
        this.add(information);
        information.setBounds(0,0,20,20);
        information.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionInformation();
            }
        });
    }


    private void setBoutons(){
        this.miner = new JButton(new ImageIcon("images/minerBouton50x50.png"));
        miner.setFocusable(false);
        this.miner.setVisible(false);
        this.add(miner);
        miner.setContentAreaFilled(false);

        this.recuperer = new JButton(new ImageIcon("images/recupererBouton.png"));
        recuperer.setFocusable(false);
        recuperer.setVisible(false);
        this.add(recuperer);

        this.recruterChef = new JButton(new ImageIcon("images/chefBouton.png"));
        recruterChef.setFocusable(false);
        this.add(this.recruterChef);
        this.recruterChef.setVisible(false);

        this.recruterOuvrier = new JButton(new ImageIcon("images/ouvrierBouton.png"));
        recruterOuvrier.setFocusable(false);
        this.add(this.recruterOuvrier);
        this.recruterOuvrier.setVisible(false);

        this.vendreRessources = new JButton(new ImageIcon("images/echangerBouton.png"));
        vendreRessources.setFocusable(false);
        this.add(this.vendreRessources);
        this.vendreRessources.setVisible(false);


        miner.setBounds(75,250,50,50);

        recuperer.setBounds(110,250,50,50);
        recruterChef.setBounds(110,250,50,50);
        recruterOuvrier.setBounds(40,250,50,50);
        vendreRessources.setBounds(75,250,50,50);

        miner.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionMiner();
            }
        });

        recuperer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionRecuperer();
            }
        });

        recruterChef.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    actionRecruterChef();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        recruterOuvrier.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    actionRecruterOuvrier();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        vendreRessources.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionVendreRessources();
            }
        });

    }

    /**
     * tests pour voir si il faut remettre enable les boutons de construction
     * on remet enable si on est plus a la limite ET si le bouton est actuellement pas enable
     */
    public void verifLimitesPourBoutons(){
        if(!plateau.verifieLimitesBatiments(TypeBat.MINEOR) && boutonsNonEnable[0]){
            mine_or.setEnabled(true);
            //mine_or.setBackground(Color.white);
            boutonsNonEnable[0] = false;
        }
        if(!plateau.verifieLimitesBatiments(TypeBat.MINEFER) && boutonsNonEnable[1]){
            mine_fer.setEnabled(true);
            //mine_fer.setBackground(Color.white);
            boutonsNonEnable[1] = false;
        }
        if(!plateau.verifieLimitesBatiments(TypeBat.MINECHARBON) && boutonsNonEnable[2]){
            mine_charbon.setEnabled(true);
            //mine_charbon.setBackground(Color.white);
            boutonsNonEnable[2] = false;
        }
        if(!plateau.verifieLimitesBatiments(TypeBat.MARCHE) && boutonsNonEnable[3]){
            marche.setEnabled(true);
            //marche.setBackground(Color.white);
            boutonsNonEnable[3] = false;
        }
        if(!plateau.verifieLimitesBatiments(TypeBat.MOULIN) && boutonsNonEnable[4]){
            moulin.setEnabled(true);
            //moulin.setBackground(Color.white);
            boutonsNonEnable[4] = false;
        }
        if(!plateau.verifieLimitesBatiments(TypeBat.HDV) && boutonsNonEnable[5]){
            hdv.setEnabled(true);
            //hdv.setBackground(Color.white);
            boutonsNonEnable[5] = false;
        }
        if(!plateau.verifieLimitesBatiments(TypeBat.DEFENSE) && boutonsNonEnable[6]){
            defense.setEnabled(true);
            //defense.setBackground(Color.white);
            boutonsNonEnable[6] = false;
        }
        if(!plateau.verifieLimitesBatiments(TypeBat.MUR) && boutonsNonEnable[7]){
            mur.setEnabled(true);
            //mur.setBackground(Color.white);
            boutonsNonEnable[7] = false;
        }
    }

    /**
     * Verifie toutes les limites de construction des batiments
     */
    public void verifLimitesBatiments(){
        if(plateau.verifieLimitesBatiments(TypeBat.MINEOR)){
            //mine_or.setBackground(new Color(255,0,0, 203));
            mine_or.setEnabled(false);
            boutonsNonEnable[0] = true;
        }
        if(plateau.verifieLimitesBatiments(TypeBat.MINEFER)){
            //mine_fer.setBackground(Color.red);
            mine_fer.setEnabled(false);
            boutonsNonEnable[1] = true;
        }
        if(plateau.verifieLimitesBatiments(TypeBat.MINECHARBON)){
            //mine_charbon.setBackground(Color.red);
            mine_charbon.setEnabled(false);
            boutonsNonEnable[2] = true;
        }
        if(plateau.verifieLimitesBatiments(TypeBat.MARCHE)){
            //marche.setBackground(Color.red);
            marche.setEnabled(false);
            boutonsNonEnable[3] = true;
        }
        if(plateau.verifieLimitesBatiments(TypeBat.MOULIN)){
            //moulin.setBackground(Color.red);
            moulin.setEnabled(false);
            boutonsNonEnable[4] = true;
        }
        if(plateau.verifieLimitesBatiments(TypeBat.HDV)){
            //hdv.setBackground(Color.red);
            hdv.setEnabled(false);
            boutonsNonEnable[5] = true;
        }
        if(plateau.verifieLimitesBatiments(TypeBat.DEFENSE)){
            //defense.setBackground(Color.red);
            defense.setEnabled(false);
            boutonsNonEnable[6] = true;
        }
        if(plateau.verifieLimitesBatiments(TypeBat.MUR)) {
            //mur.setBackground(Color.red);
            mur.setEnabled(false);
            boutonsNonEnable[7] = true;
        }
    }

    public void actionConstruire(){
        //System.out.println("PAS DEJA CONSTRUIT : " + pasDejaConstruit(construction)+"\n");

        actualisePosChef();
        boolean limiteConstruction = plateau.verifieLimitesBatiments(construction);

        switch (construction) {
            case MINEOR:
                if (plateau.getNbrePieces() >= Mine.prix(Item.OR) && pasDejaConstruit(construction)) {
                    Mine mo = new Mine(positionChef, Item.OR);
                    plateau.ajouteMine(mo);
                    plateau.achatBatiment(Mine.prix(Item.OR));
                    fantome = false;
                } else {
                    System.out.println("VOUS N AVEZ PAS ASSEZ DE PIECES POUR CONSTRUIRE : MINE OR\n");
                    fantome = false;
                }
                break;
            case MINEFER:
                if (plateau.getNbrePieces() >= Mine.prix(Item.FER) && pasDejaConstruit(construction)) {
                    Mine mo = new Mine(positionChef, Item.FER);
                    plateau.ajouteMine(mo);
                    plateau.achatBatiment(Mine.prix(Item.FER));
                    fantome = false;
                } else {
                    System.out.println("VOUS N AVEZ PAS ASSEZ DE PIECES POUR CONSTRUIRE : MINE FER\n");
                    fantome = false;
                }
                break;
            case MINECHARBON:
                if (plateau.getNbrePieces() >= Mine.prix(Item.CHARBON) && pasDejaConstruit(construction)) {
                    Mine mo = new Mine(positionChef, Item.CHARBON);
                    plateau.ajouteMine(mo);
                    plateau.achatBatiment(Mine.prix(Item.CHARBON));
                    fantome = false;
                } else {
                    System.out.println("VOUS N AVEZ PAS ASSEZ DE PIECES POUR CONSTRUIRE : MINE CHARBON\n");
                    fantome = false;
                }
                break;
            case MARCHE:
                if (plateau.getNbrePieces() >= Marche.PRIX && pasDejaConstruit(construction)) {
                    Marche mo = new Marche(positionChef);
                    plateau.ajouteBat(mo);
                    plateau.achatBatiment(Marche.PRIX);
                    fantome = false;
                } else {
                    System.out.println("VOUS N AVEZ PAS ASSEZ DE PIECES POUR CONSTRUIRE : MARCHE\n");
                    fantome = false;
                }
                break;
            case MOULIN:
                if (plateau.getNbrePieces() >= Moulin.PRIX && pasDejaConstruit(construction)) {
                    Moulin mo = new Moulin(positionChef);
                    plateau.ajouteBat(mo);
                    plateau.achatBatiment(Moulin.PRIX);
                    fantome = false;
                } else {
                    System.out.println("VOUS N AVEZ PAS ASSEZ DE PIECES POUR CONSTRUIRE : MOULIN\n");
                    fantome = false;
                }
                break;
            case HDV:
                if (plateau.getNbrePieces() >= HotelDeVille.PRIX && pasDejaConstruit(construction)) {
                    HotelDeVille mo = new HotelDeVille(positionChef);
                    plateau.ajouteBat(mo);
                    plateau.achatBatiment(HotelDeVille.PRIX);
                    fantome = false;
                } else {
                    System.out.println("VOUS N AVEZ PAS ASSEZ DE PIECES POUR CONSTRUIRE : HOTEL DE VILLE\n");
                    fantome = false;
                }
                break;
            case DEFENSE:
                if (plateau.getNbrePieces() >= Defense.PRIX && pasDejaConstruit(construction)) {
                    Defense mo = new Defense(positionChef);
                    plateau.ajouteBat(mo);
                    plateau.achatBatiment(Defense.PRIX);
                    fantome = false;
                } else {
                    System.out.println("VOUS N AVEZ PAS ASSEZ DE PIECES POUR CONSTRUIRE : DEFENSE\n");
                    fantome = false;
                }
                break;
            case MUR:
                if (plateau.getNbrePieces() >= Mur.PRIX*nombreMurs && pasDejaConstruit(construction)) {
                    if (nombreMurs == 1) {
                        Mur mo = new Mur(positionChef);
                        plateau.ajouteBat(mo);
                        plateau.achatBatiment(Mur.PRIX);
                        fantome = false;
                    } else {
                        //premier mur (celui du millieu)
                        Mur inv1 = new Mur(positionChef);
                        plateau.ajouteBat(inv1);
                        plateau.achatBatiment(Mur.PRIX);
                        for (int i = 3; i <= nombreMurs; i += 2) {
                            if (estTourneMur) {
                                //les murs du haut
                                Position nouvPos = new Position(positionChef.getXint(), positionChef.getYint()- Batiment.getTailleTypeBat(TypeBat.MUR) * (i / 2));
                                Mur inv2 = new Mur(nouvPos);
                                plateau.ajouteBat(inv2);
                                plateau.achatBatiment(Mur.PRIX);
                                //les murs du bas
                                Position nouvPos2 = new Position(positionChef.getXint(), positionChef.getYint()+ Batiment.getTailleTypeBat(TypeBat.MUR) * (i / 2));
                                Mur inv3 = new Mur(nouvPos2);
                                plateau.ajouteBat(inv3);
                                plateau.achatBatiment(Mur.PRIX);
                            } else {
                                //les murs de droite
                                Position nouvPos = new Position(positionChef.getXint()- Batiment.getTailleTypeBat(TypeBat.MUR) * (i / 2), positionChef.getYint());
                                Mur inv2 = new Mur(nouvPos);
                                plateau.ajouteBat(inv2);
                                plateau.achatBatiment(Mur.PRIX);
                                //les murs de gauche
                                Position nouvPos2 = new Position(positionChef.getXint()+ Batiment.getTailleTypeBat(TypeBat.MUR) * (i / 2), positionChef.getYint());
                                Mur inv3 = new Mur(nouvPos2);
                                plateau.ajouteBat(inv3);
                                plateau.achatBatiment(Mur.PRIX);
                            }
                        }
                        fantome = false;
                    }
                } else{
                    System.out.println("VOUS N AVEZ PAS ASSEZ DE PIECES POUR CONSTRUIRE : MUR\n");
                    fantome = false;
                }
                break;

            default:
                break;
        }
    }

    public void afficheBoutonConstructionBats(boolean etat){
        mine_or.setVisible(etat);
        mine_fer.setVisible(etat);
        mine_charbon.setVisible(etat);
        defense.setVisible(etat);
        hdv.setVisible(etat);
        marche.setVisible(etat);
        mur.setVisible(etat);
        moulin.setVisible(etat);
    }

    private void setBoutonsConstruction() {
        this.construire = new JButton(new ImageIcon("images/construireBouton.png"));
        this.construire.setVisible(false);
        construire.setFocusable(false);
        this.add(construire);
        this.mine_or = new JButton(new ImageIcon("images/mineOrBouton.png"));
        this.mine_or.setVisible(false);
        mine_or.setFocusable(false);
        this.add(mine_or);
        this.mine_charbon = new JButton(new ImageIcon("images/mineCharbonBouton.png"));
        this.mine_charbon.setVisible(false);
        mine_charbon.setFocusable(false);
        this.add(mine_charbon);
        this.mine_fer = new JButton(new ImageIcon("images/mineFerBouton.png"));
        this.mine_fer.setVisible(false);
        mine_fer.setFocusable(false);
        this.add(mine_fer);
        this.hdv = new JButton(new ImageIcon("images/hdvBouton.png"));
        this.hdv.setVisible(false);
        hdv.setFocusable(false);
        this.add(hdv);
        this.moulin = new JButton(new ImageIcon("images/moulinBouton.png"));
        this.moulin.setVisible(false);
        moulin.setFocusable(false);
        this.add(moulin);
        this.mur = new JButton(new ImageIcon("images/murBouton.png"));
        this.mur.setVisible(false);
        mur.setFocusable(false);
        this.add(mur);
        this.plusMur = new JButton(new ImageIcon("images/murPlus.png"));
        this.plusMur.setVisible(false);
        plusMur.setFocusable(false);
        this.add(this.plusMur);
        this.moinsMur = new JButton(new ImageIcon("images/murMoins.png"));
        this.moinsMur.setVisible(false);
        moinsMur.setFocusable(false);
        this.add(this.moinsMur);
        this.tournerMur = new JButton(new ImageIcon("images/murTourner.png"));
        this.tournerMur.setVisible(false);
        tournerMur.setFocusable(false);
        this.add(this.tournerMur);
        this.defense = new JButton(new ImageIcon("images/defenseBouton.png"));
        this.defense.setVisible(false);
        defense.setFocusable(false);
        this.add(defense);
        this.marche = new JButton(new ImageIcon("images/marcheBouton.png"));
        this.marche.setVisible(false);
        marche.setFocusable(false);
        this.add(marche);

        construire.setBounds(40,250,50,50);

        int width = 50;
        int height = 50;
        int x = LARG_PANNEAU/2 -width;
        int y = 300 + 10;
        mine_or.setBounds(x,y,width,height);
        mine_fer.setBounds(x+width,y,width,height);
        mine_charbon.setBounds(x,y+height,width,height);
        marche.setBounds(x+width,y+height,width,height);
        moulin.setBounds(x,y+height*2,width,height);
        hdv.setBounds(x+width,y+height*2,width,height);
        defense.setBounds(x,y+height*3,width,height);
        mur.setBounds(x+width,y+height*3,width,height);

        moinsMur.setBounds(100, y+height*4, 20, 20);
        tournerMur.setBounds(120, y+height*4, 20, 20);
        plusMur.setBounds(140, y+height*4, 20, 20);

        construire.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                actionBoutonConstruire();

            }
        });

        mine_or.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                construction = TypeBat.MINEOR;
                positionChef = new Position(chefEnClic.getPosition().getXint(), chefEnClic.getPosition().getYint());
                fantome = true;
                construisible = plateau.getNbrePieces() >= Mine.prix(Item.OR) && pasDejaConstruit(construction);
            }
        });

        mine_fer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                construction = TypeBat.MINEFER;
                positionChef = new Position(chefEnClic.getPosition().getXint(), chefEnClic.getPosition().getYint());
                fantome = true;
                construisible = plateau.getNbrePieces() >= Mine.prix(Item.FER) && pasDejaConstruit(construction);
            }
        });

        mine_charbon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                construction = TypeBat.MINECHARBON;
                positionChef = new Position(chefEnClic.getPosition().getXint(), chefEnClic.getPosition().getYint());
                fantome = true;
                construisible = plateau.getNbrePieces() >= Mine.prix(Item.CHARBON) && pasDejaConstruit(construction);
            }
        });

        marche.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                construction = TypeBat.MARCHE;
                positionChef = new Position(chefEnClic.getPosition().getXint(), chefEnClic.getPosition().getYint());
                fantome = true;
                construisible = plateau.getNbrePieces() >= Marche.PRIX && pasDejaConstruit(construction);
            }
        });

        moulin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                construction = TypeBat.MOULIN;
                positionChef = new Position(chefEnClic.getPosition().getXint(), chefEnClic.getPosition().getYint());
                fantome = true;
                construisible = plateau.getNbrePieces() >= Moulin.PRIX && pasDejaConstruit(construction);
            }
        });

        hdv.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                construction = TypeBat.HDV;
                positionChef = new Position(chefEnClic.getPosition().getXint(), chefEnClic.getPosition().getYint());
                fantome = true;
                construisible = plateau.getNbrePieces() >= HotelDeVille.PRIX && pasDejaConstruit(construction);
            }
        });

        defense.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                construction = TypeBat.DEFENSE;
                positionChef = new Position(chefEnClic.getPosition().getXint(), chefEnClic.getPosition().getYint());
                fantome = true;
                construisible = plateau.getNbrePieces() >= Defense.PRIX && pasDejaConstruit(construction);
            }
        });

        mur.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //si on a deja appuye sur mur et qu on re appuis alors on ne veut pas construire un mur
                if (construction == TypeBat.MUR){
                    construction = null;
                    enConstructionDeMurs = false;
                    plusMur.setVisible(false);
                    moinsMur.setVisible(false);
                    tournerMur.setVisible(false);
                }
                else {
                    construction = TypeBat.MUR;

                    if (enConstructionDeMurs) enConstructionDeMurs = false;
                    else enConstructionDeMurs = true;
                    System.out.println("enConstructionDeMurs =" + enConstructionDeMurs);
                    positionChef = new Position(chefEnClic.getPosition().getXint(), chefEnClic.getPosition().getYint());
                    fantome = true;
                    construisible = plateau.getNbrePieces() >= Mur.PRIX*nombreMurs && pasDejaConstruit(construction);
                    System.out.println(construisible);
                    if (plusMur.isVisible()) {
                        plusMur.setVisible(false);
                        moinsMur.setVisible(false);
                        tournerMur.setVisible(false);
                    } else {
                        plusMur.setVisible(true);
                        moinsMur.setVisible(true);
                        tournerMur.setVisible(true);
                    }
                }
            }
        });

        this.moinsMur.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (nombreMurs > 1) {
                    nombreMurs -= 2;
                }

            }
        });

        this.tournerMur.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                estTourneMur = !estTourneMur;
            }
        });

        this.plusMur.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (nombreMurs < Mur.MAX_TAILLE_MUR) {
                    nombreMurs += 2;
                }
            }
        });

        mine_or.setVisible(false);
        mine_fer.setVisible(false);
        mine_charbon.setVisible(false);
        defense.setVisible(false);
        hdv.setVisible(false);
        marche.setVisible(false);
        mur.setVisible(false);
        plusMur.setVisible(false);
        moinsMur.setVisible(false);
        tournerMur.setVisible(false);
        moulin.setVisible(false);
    }

    /**
     * Regarde si la position du chef n'est pas sur un batiment
     * @return true si oui, false si non
     */
    public boolean pasDejaConstruit(TypeBat batiment) {
        Position positionBat, positionMine;
        int tailleBat, tailleMine;
        boolean resultat = true;
        //System.out.println("Batiment = " + batiment);
        //System.out.println("plateau.getBatiments().size() = " + plateau.getBatiments().size());
        //System.out.println("plateau.getMines().size() = " + plateau.getMines().size());

        if((plateau.getBatiments().size() == 0 && plateau.getMines().size() == 0) || batiment == null){

            /*System.out.println(positionChef.getYint() + "-" + Batiment.getTailleTypeBat(batiment)/2.0 + "< 0  \n"
                    + positionChef.getYint() +"+"+ Batiment.getTailleTypeBat(batiment)/2.0 +">"+ Grille.HAUT_VILLAGE +"\n"
                    + positionChef.getXint() +"+"+ Batiment.getTailleTypeBat(batiment)/2.0 +">"+ Grille.LARG_VILLAGE +"\n"
                    + positionChef.getXint() +"-"+ Batiment.getTailleTypeBat(batiment)/2.0 +"<0 \n"
            );*/
            if(batiment == TypeBat.MUR){
                if (!estTourneMur) {
                    if (positionChef.getYint() - (Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0) < 0
                            || positionChef.getYint() + (Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0) > Grille.HAUT_VILLAGE
                            || positionChef.getXint() + (Batiment.getTailleTypeBat(TypeBat.MUR) * (nombreMurs / 2))
                            + Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0 > Grille.LARG_VILLAGE
                            || positionChef.getXint() - (Batiment.getTailleTypeBat(TypeBat.MUR) * (nombreMurs / 2))
                            - Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0 < 0) {
                        return false;
                    } else return true;
                } else {
                    if (positionChef.getYint() - (Batiment.getTailleTypeBat(TypeBat.MUR) * (nombreMurs / 2))
                            - Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0 < 0
                            || positionChef.getYint() + (Batiment.getTailleTypeBat(TypeBat.MUR) * (nombreMurs / 2))
                            + Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0 > Grille.HAUT_VILLAGE
                            || positionChef.getXint() + (Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0) > Grille.LARG_VILLAGE
                            || positionChef.getXint() - (Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0) < 0) {
                        return false;
                    } else return true;
                }
            }else if (positionChef.getYint() - Batiment.getTailleTypeBat(batiment) / 2.0 < 0
                    || positionChef.getYint() + Batiment.getTailleTypeBat(batiment) / 2.0 > Grille.HAUT_VILLAGE
                    || positionChef.getXint() + Batiment.getTailleTypeBat(batiment) / 2.0 > Grille.LARG_VILLAGE
                    || positionChef.getXint() - Batiment.getTailleTypeBat(batiment) / 2.0 < 0) {

                return false;
            }else{
                return true;
            }
        }


        if(batiment == TypeBat.MUR){
            for (Batiment bat : plateau.getBatiments()) {
                if (bat.getType() != TypeBat.MUR) {
                    positionBat = bat.getPosition();
                    tailleBat = bat.getTaille();
                    for (int i = 0; i <= nombreMurs / 2; i++) {
                        if (!estTourneMur) {
                            if ((Math.abs((positionChef.getX() - Batiment.getTailleTypeBat(TypeBat.MUR) * i) - positionBat.getX()) < (Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0 + tailleBat / 2.0)
                                    && Math.abs(positionChef.getY() - positionBat.getY()) < (Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0 + tailleBat / 2.0)) ||
                                    (Math.abs((positionChef.getX() + Batiment.getTailleTypeBat(TypeBat.MUR) * i) - positionBat.getX()) < (Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0 + tailleBat / 2.0) && Math.abs(positionChef.getY() - positionBat.getY()) < (Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0 + tailleBat / 2.0))) {
                                resultat = false;
                            }
                        } else {
                            if ((Math.abs(positionChef.getX() - positionBat.getX()) < (Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0 + tailleBat / 2.0)
                                    && Math.abs((positionChef.getY() - Batiment.getTailleTypeBat(TypeBat.MUR) * i) - positionBat.getY()) < (Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0 + tailleBat / 2.0))
                                    || (Math.abs((positionChef.getY() + Batiment.getTailleTypeBat(TypeBat.MUR) * i) - positionBat.getY()) < (Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0 + tailleBat / 2.0) && Math.abs(positionChef.getX() - positionBat.getX()) < (Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0 + tailleBat / 2.0))) {
                                resultat = false;
                            }
                        }
                    }
                }
            }
            //Maintenant on verifie que le mur ne chevauche pas une mine
            for (Mine mine : plateau.getMines()) {
                positionMine = mine.getPosition();
                tailleMine = mine.getTaille();
                for (int i = 0; i <= nombreMurs / 2; i++) {
                    if (!estTourneMur) {
                        if ((Math.abs((positionChef.getX() - Batiment.getTailleTypeBat(TypeBat.MUR) * i) - positionMine.getX()) < (Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0 + tailleMine / 2.0)
                                && Math.abs(positionChef.getY() - positionMine.getY()) < (Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0 + tailleMine / 2.0)) ||
                                (Math.abs((positionChef.getX() + Batiment.getTailleTypeBat(TypeBat.MUR) * i) - positionMine.getX()) < (Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0 + tailleMine / 2.0) && Math.abs(positionChef.getY() - positionMine.getY()) < (Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0 + tailleMine / 2.0))) {
                            resultat = false;
                        }
                    } else {
                        if ((Math.abs(positionChef.getX() - positionMine.getX()) < (Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0 + tailleMine / 2.0)
                                && Math.abs((positionChef.getY() - Batiment.getTailleTypeBat(TypeBat.MUR) * i) - positionMine.getY()) < (Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0 + tailleMine / 2.0))
                                || (Math.abs((positionChef.getY() + Batiment.getTailleTypeBat(TypeBat.MUR) * i) - positionMine.getY()) < (Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0 + tailleMine / 2.0) && Math.abs(positionChef.getX() - positionMine.getX()) < (Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0 + tailleMine / 2.0))) {
                            resultat = false;
                        }
                    }
                }
            }
            //verifications pour voir si les murs sortent du plateau
            if (!estTourneMur) {
                if (positionChef.getYint() - (Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0) < 0
                        || positionChef.getYint() + (Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0) > Grille.HAUT_VILLAGE
                        || positionChef.getXint() + (Batiment.getTailleTypeBat(TypeBat.MUR) * (nombreMurs / 2))
                        + Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0 > Grille.LARG_VILLAGE
                        || positionChef.getXint() - (Batiment.getTailleTypeBat(TypeBat.MUR) * (nombreMurs / 2))
                        - Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0 < 0) return false;
            } else {
                if (positionChef.getYint() - (Batiment.getTailleTypeBat(TypeBat.MUR) * (nombreMurs / 2))
                        - Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0 < 0
                        || positionChef.getYint() + (Batiment.getTailleTypeBat(TypeBat.MUR) * (nombreMurs / 2))
                        + Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0 > Grille.HAUT_VILLAGE
                        || positionChef.getXint() + (Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0) > Grille.LARG_VILLAGE
                        || positionChef.getXint() - (Batiment.getTailleTypeBat(TypeBat.MUR) / 2.0) < 0) return false;

            }

        }else { //le batiment n'est pas un mur :


            for (Batiment bat : plateau.getBatiments()) {
                positionBat = bat.getPosition();
                tailleBat = bat.getTaille();
                if (Math.abs(positionChef.getX() - positionBat.getX()) < (Batiment.getTailleTypeBat(batiment) / 2.0 + tailleBat / 2.0)
                        && Math.abs(positionChef.getY() - positionBat.getY()) < (Batiment.getTailleTypeBat(batiment) / 2.0 + tailleBat / 2.0)) {
                    resultat = false;
                }
                if (positionChef.getYint() - Batiment.getTailleTypeBat(batiment) / 2.0 < 0
                        || positionChef.getYint() + Batiment.getTailleTypeBat(batiment) / 2.0 > Grille.HAUT_VILLAGE
                        || positionChef.getXint() + Batiment.getTailleTypeBat(batiment) / 2.0 > Grille.LARG_VILLAGE
                        || positionChef.getXint() - Batiment.getTailleTypeBat(batiment) / 2.0 < 0) resultat = false;
            }

            for (Mine mine : plateau.getMines()) {
                positionMine = mine.getPosition();
                tailleMine = mine.getTaille();
                if (Math.abs(positionChef.getX() - positionMine.getX()) < (Batiment.getTailleTypeBat(batiment) / 2.0 + tailleMine / 2.0)
                        && Math.abs(positionChef.getY() - positionMine.getY()) < (Batiment.getTailleTypeBat(batiment) / 2.0 + tailleMine / 2.0)) {
                    resultat = false;
                }
                if (positionChef.getYint() - Batiment.getTailleTypeBat(batiment) / 2.0 < 0
                        || positionChef.getYint() + Batiment.getTailleTypeBat(batiment) / 2.0 > Grille.HAUT_VILLAGE
                        || positionChef.getXint() + Batiment.getTailleTypeBat(batiment) / 2.0 > Grille.LARG_VILLAGE
                        || positionChef.getXint() - Batiment.getTailleTypeBat(batiment) / 2.0 < 0) resultat = false;
            }
        /*System.out.println(positionChef.getYint() + "-" + Batiment.getTailleTypeBat(batiment)/2.0 + "< 0  \n"
                + positionChef.getYint() +"+"+ Batiment.getTailleTypeBat(batiment)/2.0 +">"+ Grille.HAUT_VILLAGE +"\n"
                + positionChef.getXint() +"+"+ Batiment.getTailleTypeBat(batiment)/2.0 +">"+ Grille.LARG_VILLAGE +"\n"
                + positionChef.getXint() +"-"+ Batiment.getTailleTypeBat(batiment)/2.0 +"<0 \n"
        );*/

        }
        return resultat;
    }


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(LARG_PANNEAU, HAUT_PANNEAU);
    }

    public boolean etatConstruire() {
        return etatConstruire;
    }

    public boolean estNullConstruction(){
        if(this.construction == null) return true;
        else return false;
    }

    public boolean getFantome(){
        return this.fantome;
    }

    public Position getPositionChef(){
        return positionChef;
    }

    public TypeBat getConstruction() {
        return construction;
    }

    public boolean getConstruisible(){
        return construisible;
    }

    public void actualisePosChef(){
        if(chefEnClic != null && enConstruction && construction != null) {
            int xC = chefEnClic.getPosition().getXint();
            int yC = chefEnClic.getPosition().getYint();
            positionChef = new Position(xC, yC);
            switch(construction){
                case MINEOR:
                    construisible = pasDejaConstruit(construction) && plateau.getNbrePieces() >= Mine.prix(Item.OR);
                    break;
                case MINEFER:
                    construisible = pasDejaConstruit(construction) && plateau.getNbrePieces() >= Mine.prix(Item.FER);
                    break;
                case MINECHARBON:
                    construisible = pasDejaConstruit(construction) && plateau.getNbrePieces() >= Mine.prix(Item.CHARBON);
                    break;
                case HDV:
                    construisible = pasDejaConstruit(construction) && plateau.getNbrePieces() >= HotelDeVille.PRIX;
                    break;
                case MARCHE:
                    construisible = pasDejaConstruit(construction) && plateau.getNbrePieces() >= Marche.PRIX;
                    break;
                case MOULIN:
                    construisible = pasDejaConstruit(construction) && plateau.getNbrePieces() >= Moulin.PRIX;
                    break;
                case DEFENSE:
                    construisible = pasDejaConstruit(construction) && plateau.getNbrePieces() >= Defense.PRIX;
                    break;
                case MUR:
                    construisible = pasDejaConstruit(construction) && plateau.getNbrePieces() >= Mur.PRIX;
                    break;
            }

        }
    }

    private void actualiseQuitterMine() {
        Mine m = this.plateau.getMineEnClic();
        if(m != null) {
            this.setVisibleQuitterMine(m.getOuvriers().size());
        }
        else this.setVisibleQuitterMine(0);
    }

    /**
     * reinstialise l actionListener des boutons
     * fait quitter la mine a un ouvrier
     */
    public void changeQuitterMine(){
        Mine m = this.plateau.getMineEnClic();
        if(m != null){
            if(m.getOuvriers().size()>0){
                for(ActionListener aL : this.quitterMine1.getActionListeners()) this.quitterMine1.removeActionListener(aL);
                this.quitterMine1.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if(!enConstruction) {
                            Ouvrier o = m.getOuvriers().get(0);
                            plateau.quitteMine(o);
                        }
                    }
                });
            }
            if(m.getOuvriers().size()>1){
                for(ActionListener aL : this.quitterMine2.getActionListeners()) this.quitterMine2.removeActionListener(aL);
                this.quitterMine2.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if(!enConstruction) plateau.quitteMine(m.getOuvriers().get(1));
                    }
                });
            }
            if(m.getOuvriers().size()>2){
                for(ActionListener aL : this.quitterMine3.getActionListeners()) this.quitterMine3.removeActionListener(aL);
                this.quitterMine3.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if(!enConstruction) plateau.quitteMine(m.getOuvriers().get(2));
                    }
                });
            }
            if(m.getOuvriers().size()>3){
                for(ActionListener aL : this.quitterMine4.getActionListeners()) this.quitterMine4.removeActionListener(aL);
                this.quitterMine4.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if(!enConstruction) plateau.quitteMine(m.getOuvriers().get(3));
                    }
                });
            }
            if(m.getOuvriers().size()>4){
                for(ActionListener aL : this.quitterMine5.getActionListeners()) this.quitterMine5.removeActionListener(aL);
                this.quitterMine5.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if(!enConstruction) plateau.quitteMine(m.getOuvriers().get(4));
                    }
                });
            }
        }
    }

    /**
     * change la visibilite des boutons quitterMine
     * @param i le nombre de mines affichees
     */
    public void setVisibleQuitterMine(int i){
        if(i == 0 && !estEnInfo) {
            quitterMine1.setVisible(false);
            quitterMine2.setVisible(false);
            quitterMine3.setVisible(false);
            quitterMine4.setVisible(false);
            quitterMine5.setVisible(false);
        }
        else if(i == 1 && !estEnInfo) {
            quitterMine1.setVisible(true);
            quitterMine2.setVisible(false);
            quitterMine3.setVisible(false);
            quitterMine4.setVisible(false);
            quitterMine5.setVisible(false);
        }
        else if(i == 2 && !estEnInfo) {
            quitterMine1.setVisible(true);
            quitterMine2.setVisible(true);
            quitterMine3.setVisible(false);
            quitterMine4.setVisible(false);
            quitterMine5.setVisible(false);
        }
        else if(i == 3 && !estEnInfo) {
            quitterMine1.setVisible(true);
            quitterMine2.setVisible(true);
            quitterMine3.setVisible(true);
            quitterMine4.setVisible(false);
            quitterMine5.setVisible(false);
        }
        else if(i == 4 && !estEnInfo) {
            quitterMine1.setVisible(true);
            quitterMine2.setVisible(true);
            quitterMine3.setVisible(true);
            quitterMine4.setVisible(true);
            quitterMine5.setVisible(false);
        }
        else if(i == 5 && !estEnInfo) {
            quitterMine1.setVisible(true);
            quitterMine2.setVisible(true);
            quitterMine3.setVisible(true);
            quitterMine4.setVisible(true);
            quitterMine5.setVisible(true);
        }
        //else System.out.println("erreur setVisibleQuitterMine() : i > 5");
    }

    public int getNombreMurs() {
        return this.nombreMurs;
    }

    public boolean estTourneMur() {
        return this.estTourneMur;
    }

    public void setEstEnInfo(boolean b){
        this.estEnInfo = b;
    }

}
