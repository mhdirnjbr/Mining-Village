package Affichage;

import modele.batiments.Batiment;
import modele.batiments.Mine;
import modele.batiments.Moulin;
import modele.batiments.TypeBat;
import modele.Plateau;
import outils.Position;
import controleur.ControleurGrille;
import controleur.ControleurPanneauGestion;
import modele.unites.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Math.cos;

public class Grille extends JPanel {


    //Largeur du village
    public static final int LARG_VILLAGE = 540;

    //Hauteur du village
    public static final int HAUT_VILLAGE = 550;

    //Ordonnee du village
    public static final int ORDONNEE_VILLAGE = 0;

    //Abscisse du village
    public static final int ABSCISSE_VILLAGE = 0;

    private Plateau plateau;

    private ControleurGrille c;
    private ControleurPanneauGestion cPG;
    private PanneauGestion panneauGestion;

    ArrayList<Position> poss = new ArrayList<>();

    private BufferedImage rip;

    public boolean victoire = false;
    public boolean defaite = false;

    int cpt = 0;
    int cptPain = 0;
    int cptAffichageInvasion = 0;

    /**
     * Ça crée le village du jeu.
     */
    public Grille(Plateau p, PanneauGestion pG) {
        super();

        //Définition de la position et les dimensions du village
        this.setBounds(ORDONNEE_VILLAGE, ABSCISSE_VILLAGE, LARG_VILLAGE, HAUT_VILLAGE);

        //Définition de la coulour au fond
        this.setBackground(new Color(113, 112, 112));
        this.setBackground(new Color(127, 151, 108));


        //Définition de la coulour de bordure
        this.setBorder(new LineBorder(Color.BLACK));

        this.plateau = p;
        this.panneauGestion = pG;
        setImage();
    }

    public void setImage(){
        String imgUrlFond="images/rip.png";
        try {
            rip = ImageIO.read(new File(imgUrlFond));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setControleur(PanneauGestion pG) {
        this.c = new ControleurGrille(this, pG, plateau);
        this.addMouseListener(c);
        this.addKeyListener(c);
    }

    /**
     * Ça permet de dessiner le village.
     *
     * @param g Une instance de la classe Graphics
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        cpt++;
        if(victoire){
            drawVictoire(g);
        }
        else if(defaite){
            drawDefaite(g);
        }
        else {
            //Dessin de la grille
            drawGrille(g);

            //Dessin des éléments du plateau
            drawPlateau(g);

            //if (plateau.aStarPoint != null)
                //g.fillOval(plateau.aStarPoint.getXint() - 4, plateau.aStarPoint.getYint() - 4, 8, 8);

            g.setColor(Color.black);
            for (int i = 0; i < LARG_VILLAGE; i += 100) {
                g.drawString(String.valueOf(i), i, 10);
                g.drawString(String.valueOf(i), 0, i);
            }

            //test dessin losange
        /*
        int midX = 220;
        int midY = 200;
        int taille = 100;
        int longe = (int)Math.sqrt(taille*taille + taille*taille)/2;
        int largeur = 100;
        int hauteur = 30;
        int incr = largeur/hauteur;
        int cpt = 0;
        for(int y = midY - hauteur/2; y < midY; y++){
            for (int x = midX - cpt; x < midX + cpt; x++){
                //System.out.println(new Position(x,y));
                g.fillRect(x, y, 1, 1);
            }
            cpt+=incr;
        }
        for(int y = midY; y < midY + hauteur/2; y++){
            for (int x = midX - cpt; x < midX + cpt; x++){
               // System.out.println(new Position(x,y));
                g.fillRect(x, y, 1, 1);
            }
            cpt-=incr;
        }*/
            //test de random rect
        /*g.setColor(Color.black);
        Position p = Position.randomRectangle(new Position
                (Grille.LARG_VILLAGE/2, Grille.HAUT_VILLAGE/2), 80,  30);
        g.drawRect(Grille.LARG_VILLAGE/2 -40, Grille.HAUT_VILLAGE/2 - 15, 80,  30);
        poss.add(p);
        for(Position ps : poss) {
            g.fillOval(ps.getXint()-2, ps.getYint() -2, 4, 4);
        }
         */
            //test de semi random
        /*g.setColor(Color.black);
        g.drawOval(125 - 2,425 - 2, 4, 4);
        g.drawOval(100, 400, 100, 100);
        g.setColor(Color.red);
        Position p = Position.randomCerclePourcentage(new Position(150, 450), 50, new Position(300, 425));
        poss.add(p);
        for(Position ps : poss) {
            g.fillOval(ps.getXint()-2, ps.getYint() -2, 4, 4);
        }*/
            //tests de etatPlaeau
        /*for(int i = 0; i <LARG_VILLAGE; i+=10) {
            for (int j = 0; j < HAUT_VILLAGE; j += 10) {
                Boolean b = plateau.getEtatPlateau().get(plateau.positions[i][j]);
                String s = b ? "t" : "f";
                g.setColor(Color.black);
                g.drawString(s, i, j);
            }
        }*/
        /*for(int i = 0; i <LARG_VILLAGE; i+=1) {
            for (int j = 0; j < HAUT_VILLAGE; j += 1) {
                Boolean b = plateau.getEtatPlateau().get(plateau.positions[i][j]);
                String s = b ? "t" : "f";
                System.out.print(s + " ");
            }
            System.out.println();
        }
        System.out.println();System.out.println();System.out.println();*/

        }
    }

    public void drawVictoire(Graphics g){
        g.setColor(Color.black);
        g.drawString("Vous avez gagné !, temps : " + plateau.getTimer(), LARG_VILLAGE/2 - 75, HAUT_VILLAGE/2);
    }

    public void drawDefaite(Graphics g){
        g.setColor(Color.black);
        g.drawString("Vous avez perdu ..., temps : " + plateau.getTimer(), LARG_VILLAGE/2 - 75, HAUT_VILLAGE/2);
        g.drawString(plateau.messageDefaite, LARG_VILLAGE/2 - 70, HAUT_VILLAGE/2 + 20);
    }

    public void drawP(Graphics g, Position p, int i){
        g.setColor(new Color(i,i,i));
        g.drawOval(p.getXint(), p.getYint(), 1,1);
    }

    /**
     * Ça permet de dessiner la grille du village.
     *
     * @param g Une instance de la classe Graphics
     */
    public void drawGrille(Graphics g) {
        g.setColor(Color.BLACK);

        int tailleX = 30;
        int tailleY = 40;
        for (int x = 0; x < 30; x += 1) {
            for (int y = 0; y < 20; y += 1) {
                g.drawPolygon(
                        new int[]{ (x+x+1)*tailleX/2, (x+1)*tailleX, (x+x+3)*tailleX/2, (x+x+3)*tailleX/2, (x+1)*tailleX, (x+x+1)*tailleX/2 },
                        new int[]{ (4*y-1)*tailleY/4, (2*y-1)*tailleY/2, (4*y-1)*20/2, y*tailleY, (4*y+1)*tailleY/4, y*tailleY},
                        6);

                g.drawPolygon(
                        new int[]{ x*tailleX, (x+x+1)*tailleX/2, (x+1)*tailleX, (x+1)*tailleX, (x+x+1)*tailleX/2, x*tailleX },
                        new int[]{ (4*y+1)*tailleY/4, y*tailleY, (4*y+1)*tailleY/4, (2*y+1)*tailleY/2, tailleY*y + 30, (2*y+1)*tailleY/2 },
                        6);
            }
        }

    }

    public void drawPlateau(Graphics g){
        if (!this.panneauGestion.estNullConstruction() && this.panneauGestion.getFantome()) {
            if (this.panneauGestion.getConstruisible()) {
                g.setColor(new Color(0.0F, 1.0F, 0.0F, 0.5F));
            } else {
                g.setColor(new Color(1.0F, 0.0F, 0.0F, 0.5F));
            }

            if(this.panneauGestion.getConstruction() == TypeBat.MUR) {
                int nombreMurs = panneauGestion.getNombreMurs();
                //System.out.println("NombreMurs ="+ nombreMurs);
                if(nombreMurs == 1) {
                    g.setColor(Color.GREEN);
                    g.fillRect(this.panneauGestion.getPositionChef().getXint() - Batiment.getTailleTypeBat(TypeBat.MUR) / 2, this.panneauGestion.getPositionChef().getYint() - Batiment.getTailleTypeBat(TypeBat.MUR) / 2, Batiment.getTailleTypeBat(TypeBat.MUR), Batiment.getTailleTypeBat(TypeBat.MUR));
                }else {
                    for (int i = 3; i <= nombreMurs; i += 2) {
                        if(panneauGestion.estTourneMur()){
                            g.fillRect(this.panneauGestion.getPositionChef().getXint() - Batiment.getTailleTypeBat(TypeBat.MUR) /2,
                                    this.panneauGestion.getPositionChef().getYint() - Batiment.getTailleTypeBat(TypeBat.MUR) * (i / 2) - Batiment.getTailleTypeBat(TypeBat.MUR)/2,
                                    Batiment.getTailleTypeBat(TypeBat.MUR), Batiment.getTailleTypeBat(TypeBat.MUR) * i);
                        }else{
                            g.fillRect(this.panneauGestion.getPositionChef().getXint() - Batiment.getTailleTypeBat(TypeBat.MUR) * (i / 2) - Batiment.getTailleTypeBat(TypeBat.MUR)/2,
                                    this.panneauGestion.getPositionChef().getYint() - Batiment.getTailleTypeBat(TypeBat.MUR) / 2,
                                    Batiment.getTailleTypeBat(TypeBat.MUR) * i, Batiment.getTailleTypeBat(TypeBat.MUR));
                        }
                    }

                    //System.out.println("largeur mur = " + Batiment.getTailleTypeBat(TypeBat.MUR));
                    //System.out.println("posx gauche = " + (this.panneauGestion.getPositionChef().getXint() -30));
                    //System.out.println("posx droite = " + (this.panneauGestion.getPositionChef().getXint() + Batiment.getTailleTypeBat(TypeBat.MUR)));
                }
            }else
                g.fillRect(this.panneauGestion.getPositionChef().getXint() - Batiment.getTailleTypeBat(this.panneauGestion.getConstruction()) / 2, this.panneauGestion.getPositionChef().getYint() - Batiment.getTailleTypeBat(this.panneauGestion.getConstruction()) / 2, Batiment.getTailleTypeBat(this.panneauGestion.getConstruction()), Batiment.getTailleTypeBat(this.panneauGestion.getConstruction()));
        }

        for (Mine b : plateau.getMines()) {
            g.setColor(b.getCouleur());
            //g.fillRect(b.getPosition().getXint() - b.getTaille()/2, b.getPosition().getYint() - b.getTaille()/2, b.getTaille(), b.getTaille());
            if(b.enClic()) {
                g.setColor(Color.black);
                g.drawRect(b.getPosition().getXint() - b.getTaille() / 2, b.getPosition().getYint() - b.getTaille() / 2, b.getTaille(), b.getTaille());
            }
            //g.drawString(""+b.getVie(), b.getPosition().getXint(), b.getPosition().getYint());
            g.drawImage(b.getImage(),b.getPosition().getXint() - b.getTaille()/2, b.getPosition().getYint() - b.getTaille()/2
                    , b.getTaille(), b.getTaille(),null);
        }

        for (Batiment b : plateau.getBatiments()) {
            g.setColor(b.getCouleur());
            if(b.getType() == TypeBat.MOULIN){
                g.setColor(((Moulin) b).getCouleurMoulin());
                g.fillOval(b.getPosition().getXint() - b.getTaille()/4, b.getPosition().getYint() - b.getTaille()/4, b.getTaille()/2, b.getTaille()/2);
            }
            if(b.getType() == TypeBat.MUR) g.fillRect(b.getPosition().getXint() - b.getTaille()/2, b.getPosition().getYint() - b.getTaille()/2, b.getTaille(), b.getTaille());
            else
                g.drawImage(b.getImage(),b.getPosition().getXint() - b.getTaille()/2, b.getPosition().getYint() - b.getTaille()/2
                    , b.getTaille(), b.getTaille(),null);
            if(b.enClic()) {
                g.setColor(Color.black);
                g.drawRect(b.getPosition().getXint() - b.getTaille() / 2, b.getPosition().getYint() - b.getTaille() / 2 , b.getTaille(), b.getTaille());
            }
        }

        for (Ouvrier o : plateau.getOuvriers()) {
            Position pTravail = new Position(-1, -1);
            if(o.enTravail() && o.enVie()) {
                pTravail = plateau.getMine(o).getPosition();
            }
            if(o.selectionnable() && !o.enClic() && o.enVie() && !o.getPosition().dansRectangle(pTravail, 10, 10)) {
                BufferedImage test = o.sensDeDeplacement == Unite.Direction.DROITE_VERS_GAUCHE ?
                        copyImage(o.imgDeplacementRight.get(o.imageState)) : copyImage(o.imgDeplacementLeft.get(o.imageState));
                for(int i = 0; i < test.getWidth(); i++){
                    for(int j = 0; j < test.getHeight(); j++){
                        //test.setRGB(i,j,test.getRGB(i,j) + cpt*2);
                        //if(test.getRGB(i,j)!=0) test.setRGB(i,j,new Color(cpt%255, 0, 255).getRGB());
                        if(test.getRGB(i,j)!=0) test.setRGB(i,j,Color.getHSBColor((cpt%100)/100.0f , 0.1f, 1.0f).getRGB());
                        //System.out.println(test.getRGB(i,j));
                    }
                }
                g.drawImage(test,
                        o.getPosition().getXint() - Ouvrier.TAILLE / 2 - 2,
                        o.getPosition().getYint() - Ouvrier.TAILLE / 2 - 2,
                        Ouvrier.TAILLE + 3,
                        Ouvrier.TAILLE + 3,
                        null
                );
                //g.drawOval(o.getPosition().getXint() - o.getTaille()/2, o.getPosition().getYint() - o.getTaille()/2, o.getTaille(), o.getTaille());

            }
            if (o.enClic()) {
                g.setColor(Color.BLACK);
                BufferedImage test = o.sensDeDeplacement == Unite.Direction.DROITE_VERS_GAUCHE ?
                        copyImage(o.imgDeplacementRight.get(o.imageState)) : copyImage(o.imgDeplacementLeft.get(o.imageState));
                for(int i = 0; i < test.getWidth(); i++){
                    for(int j = 0; j < test.getHeight(); j++){
                        //test.setRGB(i,j,test.getRGB(i,j) + cpt*2);
                        //if(test.getRGB(i,j)!=0) test.setRGB(i,j,new Color(cpt%255, 0, 255).getRGB());
                        if(test.getRGB(i,j)!=0) test.setRGB(i,j,Color.getHSBColor((cpt%100)/100.0f , 0.3f, 1.0f).getRGB());
                        //System.out.println(test.getRGB(i,j));
                    }
                }
                g.drawImage(test,
                        o.getPosition().getXint() - Ouvrier.TAILLE / 2 - 2,
                        o.getPosition().getYint() - Ouvrier.TAILLE / 2 - 2,
                        Ouvrier.TAILLE + 3,
                        Ouvrier.TAILLE + 3,
                        null
                );
                //g.drawOval(c.getPosition().getXint() - c.getTaille() / 2, c.getPosition().getYint() - c.getTaille() / 2, c.getTaille(), c.getTaille());
            }
            if(o.enVie() && !o.getPosition().dansRectangle(pTravail, 10, 10)) {
                if (o.sensDeDeplacement == Unite.Direction.DROITE_VERS_GAUCHE) {
                    g.drawImage(o.imgDeplacementRight.get(o.imageState),
                            o.getPosition().getXint() - Ouvrier.TAILLE / 2,
                            o.getPosition().getYint() - Ouvrier.TAILLE / 2,
                            Ouvrier.TAILLE,
                            Ouvrier.TAILLE,
                            null

                    );
                } else{
                    g.drawImage(o.imgDeplacementLeft.get(o.imageState),
                            o.getPosition().getXint() - Ouvrier.TAILLE / 2,
                            o.getPosition().getYint() - Ouvrier.TAILLE / 2,
                            Ouvrier.TAILLE,
                            Ouvrier.TAILLE,
                            null
                    );
                }
                int larg = 20 ;
                g.setColor(o.getCouleur());
                //g.fillOval(o.getPosition().getXint() - o.getTaille()/2, o.getPosition().getYint() - o.getTaille()/2, o.getTaille(), o.getTaille());
                //g.drawRect(o.getPosition().getXint() + 2 - larg / 2, o.getPosition().getYint() - Chef.TAILLE / 4 + 2, larg, Chef.TAILLE / 2 - 4);

                if(o.enClic()) {
                    g.setColor(Color.white);
                    //g.drawOval(o.getPosition().getXint() - o.getTaille() / 2 + 1, o.getPosition().getYint() - o.getTaille() / 2 + 1, o.getTaille() - 2, o.getTaille() - 2);
                }
            }
            else if(!o.enVie()) g.drawImage(rip,o.getPosition().getXint() - 10, o.getPosition().getYint() -10, 20, 20, null);
        }

        for (Chef c : plateau.getChefs()) {
            if (c.enClic() && c.enVie()) {
                g.setColor(Color.BLACK);
                BufferedImage test = c.sensDeDeplacement == Unite.Direction.GAUCHE_VERS_DROITE ?
                        copyImage(c.imgDeplacementRight.get(c.imageState)) : copyImage(c.imgDeplacementLeft.get(c.imageState));
                for(int i = 0; i < test.getWidth(); i++){
                    for(int j = 0; j < test.getHeight(); j++){
                        //test.setRGB(i,j,test.getRGB(i,j) + cpt*2);
                        //if(test.getRGB(i,j)!=0) test.setRGB(i,j,new Color(cpt%255, 0, 255).getRGB());
                        if(test.getRGB(i,j)!=0) test.setRGB(i,j,Color.getHSBColor((cpt%100)/100.0f , 0.3f, 1.0f).getRGB());
                        //System.out.println(test.getRGB(i,j));
                    }
                }
                g.drawImage(test,
                        c.getPosition().getXint() - Chef.TAILLE / 2 - 3,
                        c.getPosition().getYint() - Chef.TAILLE / 2 - 2 - 3,
                        Chef.TAILLE + 6,
                        Chef.TAILLE + 6,
                        null
                );
                //g.drawOval(c.getPosition().getXint() - c.getTaille() / 2, c.getPosition().getYint() - c.getTaille() / 2, c.getTaille(), c.getTaille());
            }
            if(c.enVie() ){
                if (c.sensDeDeplacement == Unite.Direction.GAUCHE_VERS_DROITE) {
                    g.drawImage(c.imgDeplacementRight.get(c.imageState),
                            c.getPosition().getXint() - Chef.TAILLE / 2,
                            c.getPosition().getYint() - Chef.TAILLE / 2 - 2,
                            Chef.TAILLE,
                            Chef.TAILLE,
                            null
                    );

                } else {
                    g.drawImage(c.imgDeplacementLeft.get(c.imageState),
                            c.getPosition().getXint() - Chef.TAILLE / 2,
                            c.getPosition().getYint() - Chef.TAILLE / 2 - 2,
                            Chef.TAILLE,
                            Chef.TAILLE,
                            null
                    );
                }
                int larg = 16;
                g.setColor(c.getCouleur());
                //g.drawRect(c.getPosition().getXint() - 2 - larg / 2, c.getPosition().getYint() - Chef.TAILLE / 4, larg, Chef.TAILLE / 2);
                //g.fillOval(c.getPosition().getXint() - c.getTaille() / 2, c.getPosition().getYint() - c.getTaille() / 2, c.getTaille(), c.getTaille());
                if (c.seDeplace() || !c.getNouvellePosition().isEmpty())
                    g.fillOval(c.getPositionFinale().getXint() - 4, c.getPositionFinale().getYint() - 4, 8, 8);


            }
            else g.drawImage(rip,c.getPosition().getXint() -10, c.getPosition().getYint() -10, 20, 20,  null);

        }

        for (Ennemi e : plateau.getEnnemis()) {
            if(e.enVie()) {
                g.setColor(e.getCouleur());
                //g.fillOval(e.getPosition().getXint() - e.getTaille() / 2, e.getPosition().getYint() - e.getTaille() / 2, e.getTaille(), e.getTaille());

                if (e.attaque) {
                    if (e.sensDeDeplacement == Unite.Direction.GAUCHE_VERS_DROITE) {
                        g.drawImage(e.imgEnnemiAttaqueRight.get(e.imageStateAttaque),
                                e.getPosition().getXint() - Ennemi.TAILLE_ENNEMY / 2,
                                e.getPosition().getYint() - Ennemi.TAILLE_ENNEMY / 2,
                                Ennemi.TAILLE_ENNEMY,
                                Ennemi.TAILLE_ENNEMY,
                                null);
                    } else {
                        g.drawImage(e.imgEnnemiAttaqueLeft.get(e.imageStateAttaque),
                                e.getPosition().getXint() - Ennemi.TAILLE_ENNEMY / 2,
                                e.getPosition().getYint() - Ennemi.TAILLE_ENNEMY / 2,
                                Ennemi.TAILLE_ENNEMY,
                                Ennemi.TAILLE_ENNEMY,
                                null);
                    }
                } else {
                    if(e.effraye){
                        /*g.drawImage(test,
                                e.getPosition().getXint() - Ennemi.TAILLE_ENNEMY / 2 - 2,
                                e.getPosition().getYint() - Ennemi.TAILLE_ENNEMY / 2 - 2,
                                Ennemi.TAILLE_ENNEMY,
                                Ennemi.TAILLE_ENNEMY,
                                null
                        );*/
                    }
                    if (e.sensDeDeplacement == Unite.Direction.GAUCHE_VERS_DROITE) {
                        g.drawImage(e.imgEnnemiDeplacementRight.get(e.imageStateDeplacement),
                                e.getPosition().getXint() - Ennemi.TAILLE_ENNEMY / 2 + 5,
                                e.getPosition().getYint() - Ennemi.TAILLE_ENNEMY / 2 - 5,
                                Ennemi.TAILLE_ENNEMY,
                                Ennemi.TAILLE_ENNEMY,
                                null);

                    } else {
                        g.drawImage(e.imgEnnemiDeplacementLeft.get(e.imageStateDeplacement),
                                e.getPosition().getXint() - Ennemi.TAILLE_ENNEMY / 2 - 5,
                                e.getPosition().getYint() - Ennemi.TAILLE_ENNEMY / 2 - 5,
                                Ennemi.TAILLE_ENNEMY,
                                Ennemi.TAILLE_ENNEMY,
                                null);
                    }
                }
            }
            else g.drawImage(rip,e.getPosition().getXint() -10, e.getPosition().getYint() -10, 20, 20,  null);

        }


        if(plateau.getPain() <= 10){
            g.setColor(Color.red);
            cptPain++;
            Font f = g.getFont();
            g.setFont(new Font("monospaced", Font.BOLD,(int)(20 + 4*cos(cptPain/30.0))) );
            //g.drawString("Il n'y a plus de nourriture !", (int)(50 - 10*cos(cpt/10.0)), 100);
            drawCenteredString(g, "Il n'y a plus de nourriture !", new Rectangle(Grille.LARG_VILLAGE/2 - 20,10, 100,10), g.getFont());
            g.setFont(f);
        }

        if(plateau.spawnEnnemi && cptAffichageInvasion < 1000){
            g.setColor(Color.red);
            Font f = g.getFont();
            g.setFont(new Font("monospaced", Font.BOLD,(int)(20 + 4*cos(cptAffichageInvasion/30.0))) );
            drawCenteredString(g, "Attention à l'invasion !", new Rectangle(Grille.LARG_VILLAGE/2 - 20,10, 100,10), g.getFont());
            g.setFont(f);
            cptAffichageInvasion++;
        }

    }

    /**
     * Draw a String centered in the middle of a Rectangle.
     *
     * @param g The Graphics instance.
     * @param text The String to draw.
     * @param rect The Rectangle to center the text in.
     */
    public void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        // Set the font
        g.setFont(font);
        // Draw the String
        g.drawString(text, x, y);
    }

    public static BufferedImage copyImage(BufferedImage source){
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics2D g = b.createGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(LARG_VILLAGE, HAUT_VILLAGE);
    }

    public ControleurGrille getControleur() {
        return this.c;
    }
}
