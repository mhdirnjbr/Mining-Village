package controleur;

import Affichage.Grille;
import Affichage.PanneauGestion;
import modele.batiments.Batiment;
import modele.batiments.Mine;
import modele.batiments.TypeBat;
import modele.Plateau;
import outils.Position;
import modele.unites.Chef;
import modele.unites.Ouvrier;
import modele.unites.TypeUnite;
import modele.unites.Unite;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;


public class ControleurGrille implements MouseListener, KeyListener {
    public Grille grille;
    public PanneauGestion panneauGestion;
    public Plateau plateau;

    public Point point = new Point();
    boolean haut, bas, gauche, droite;
    int cptEnClic = 0;
    //represente l etat de la selection : 0 pour la selection des ches, 1 des ouvriers, 2 des batiments
    int etat = 0;

    public ControleurGrille(Grille g, PanneauGestion pG, Plateau p) {
        this.grille = g;
        this.panneauGestion = pG;
        this.plateau = p;
    }

    /**
     *
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {
        //Point m = MouseInfo.getPointerInfo().getLocation();
        //Point f = grille.getLocationOnScreen();

        Position souris = new Position(e.getX(), e.getY());

        if(e.getButton()==1) {
            clicHotelDeVille(souris);
            clicMarche(souris);
            clicOuvrier(souris);
            clicBatiment(souris);
            clicMine(souris); //ordre important entre clicMine et clicOuvrier, qui vient avant
            clicChef(souris); //ordre important entre ouvirer en 1er et chef en 2e pour dernierEnClic qui utilise c.enClic ds ouvrier
        }else{

        }
        //plateau.aStarPoint = plateau.getBatiments().get(0).getPosition().
        //        quitteBatiment(plateau.getBatiments().get(0).getTaille(), souris, plateau);
        //System.out.println("bat? " + plateau.getEtatPlateau().get(plateau.positions[plateau.aStarPoint.getXint()][plateau.aStarPoint.getYint()-1]));

        grille.repaint();
        panneauGestion.repaint();
    }

    private void clicHotelDeVille(Position souris) {
        for (Batiment m : plateau.getBatiments()) {
            if (m.getType() == TypeBat.HDV) {
                if (m.enClic()) {

                    m.setEnClic(false);

                } else if (souris.dansCarre(m.getPosition(), m.getTaille() / 2.0) && !panneauGestion.etatConstruire()) {
                    this.etat = 2;
                    m.setEnClic(true);
                    boolean parDessusChef = false;
                    Chef chefEnClic = null;
                    for (Chef c : plateau.getChefs()) {
                        if(chefClic(c, souris))
                            parDessusChef = true;
                        if(c.enClic()){
                            chefEnClic = c;
                            c.changeDernierEnClic(true);
                        } else c.changeDernierEnClic(false);
                    }
                    if(!parDessusChef && chefEnClic != null){
                        chefEnClic.setDeplace(this.plateau, m.getPosition().getXint(), m.getPosition().getYint());
                        chefEnClic.setDansHdv(true);
                    }
                    sauvDernierChefEnClic();
                } else {
                    m.setEnClic(false); //inutile?
                }
            }


        }
    }

    private void clicMarche(Position souris) {
        for (Batiment m : plateau.getBatiments()) {
            if (m.getType() == TypeBat.MARCHE) {
                if (m.enClic()) {
                    m.setEnClic(false);
                }
                else if (souris.dansCarre(m.getPosition(), m.getTaille() / 2.0) && !panneauGestion.etatConstruire()) {
                    this.etat = 2;
                    m.setEnClic(true);
                    boolean parDessusChef = false;
                    Chef chefEnClic = null;
                    for (Chef c : plateau.getChefs()) {
                        if(chefClic(c, souris))
                            parDessusChef = true;
                        if(c.enClic()){
                            chefEnClic = c;
                        c.changeDernierEnClic(true);
                    } else c.changeDernierEnClic(false);
                    }
                    if(!parDessusChef && chefEnClic != null){
                        chefEnClic.setDeplace(this.plateau, m.getPosition().getXint(), m.getPosition().getYint());
                        chefEnClic.setDansMarche(true);
                    }
                } else {
                    m.setEnClic(false); //inutile?
                }
            }
        }
    }

    private void clicBatiment(Position souris) {
        for (Batiment b : plateau.getBatiments()){
            if(b.getType() != TypeBat.MARCHE && b.getType() != TypeBat.HDV) {
                if (b.enClic()) {
                    b.setEnClic(false);
                } else if (souris.dansCarre(b.getPosition(), b.getTaille() / 2.0) && !panneauGestion.etatConstruire()) {
                    this.etat = 2;
                    b.setEnClic(true);
                    sauvDernierChefEnClic();
                } else {
                    b.setEnClic(false); //inutile?
                }
            }
        }
    }

    private void clicMine(Position souris) {
        for (Mine m : plateau.getMines()){
            if(m.enClic()){
                m.setEnClic(false);
            }
            else if(souris.dansCarre(m.getPosition(), m.getTaille()/2.0)  && !panneauGestion.etatConstruire()){
                this.etat = 2;
                m.setEnClic(true);
                this.panneauGestion.setVisibleQuitterMine(m.getOuvriers().size());
                sauvDernierChefEnClic();
                for(Ouvrier o : this.plateau.getOuvriers()) if(o.enClic()) o.setEnClic(false);
            }
            else {
                m.setEnClic(false); //inutile?
            }
        }
    }

    private void clicOuvrier(Position souris) {
        for (Ouvrier o : plateau.getOuvriers()){
            if(o.enClic() && !o.enTravail()){
                boolean parDessus = false;
                for (Unite t : plateau.getUnites(TypeUnite.CHEF)) {
                    if(chefClic(t, souris)) parDessus = true;
                }
                for (Unite t : plateau.getUnites(TypeUnite.OUVRIER)) {
                    //if(souris.dansRond(t.getPosition(), t.getTaille()/2.0)) parDessus = true;
                    if(ouvrierClic(t, souris)) parDessus = true;
                }
                for (Batiment b : plateau.getBatiments()) {
                    if(souris.dansCarre(b.getPosition(), b.getTaille()/2.0)) parDessus = true;
                }
                for (Mine m : plateau.getMines()) {
                    if(souris.dansCarre(m.getPosition(), m.getTaille()/2.0)) parDessus = true;
                }
                if(!parDessus && o.enVie()) o.setDeplace(this.plateau, souris.getXint(), souris.getYint());
                else{
                    o.setEnClic(false);
                }
            }
            //else if(souris.dansRond(o.getPosition(), o.getTaille()/2.0)  && !panneauGestion.etatConstruire()){
            else if(ouvrierClic(o, souris)  && !panneauGestion.etatConstruire()){
                if(o.selectionnable()) {
                    this.etat = 1;
                    o.setEnClic(true);
                }
                sauvDernierChefEnClic();
                //Traite le cas ou on clique sur intersection de plusieurs unites
                for(Ouvrier t : plateau.getOuvriers()) {
                    if (t!=o) {
                        t.setEnClic(false);
                    }
                }
            }
            else o.setEnClic(false); //inutile?
        }
    }

    private void clicChef(Position souris) {
        for (Chef c : plateau.getChefs()){
            if(c.enClic()){
                boolean parDessus = false;
                boolean memeChef = false;
                for (Unite t : plateau.getUnites(TypeUnite.CHEF)) {
                    if(chefClic(t, souris)){
                        parDessus = true;
                        if(t == c) memeChef = true;
                    }
                }
                for (Unite t : plateau.getUnites(TypeUnite.OUVRIER)) {
                    //if(souris.dansRond(t.getPosition(), t.getTaille()/2.0)) parDessus = true;
                    if(ouvrierClic(t, souris)) parDessus = true;
                }
                for (Batiment b : plateau.getBatiments()) {
                    if(souris.dansCarre(b.getPosition(), b.getTaille()/2.0)) parDessus = true;
                }
                for (Mine m : plateau.getMines()) {
                    if(souris.dansCarre(m.getPosition(), m.getTaille()/2.0)) parDessus = true;
                }
                if(!parDessus && c.enVie()) c.setDeplace(this.plateau, souris.getXint(), souris.getYint());
                else{
                    //on ne peut pas deselectionner un chef quand on veut construire
                    if(!panneauGestion.etatConstruire()) c.setEnClic(false);
                    if(memeChef){
                        //Traite le cas ou on deselectionne un Chef qui est dans un batiment
                        //va seulement deselectionner le chef et ne va pas selectionner le batiment
                        for(Batiment batiment : plateau.getBatiments()){
                            batiment.setEnClic(false);
                        }
                    }
                }
            }
            //g.drawRect(c.getPosition().getXint() - 2 - larg/2, c.getPosition().getYint()- Chef.TAILLE/4, larg, Chef.TAILLE/2);
            // (c.getPosition().getXint()/2 - 1 + larg/4)
            // (c.getPosition().getYint()/2 + Chef.TAILLE/8)
            //else if(souris.dansRond(c.getPosition(), c.getTaille()/2.0) && !panneauGestion.etatConstruire()){
            else if(chefClic(c, souris)){
                this.etat = 0;
                c.setEnClic(true);
                //Traite le cas ou on selectionne un Chef qui est dans un batiment
                for(Batiment batiment : plateau.getBatiments()){
                    batiment.setEnClic(false);
                }
                //Traite le cas ou on clique sur intersection de plusieurs unites
                for(Chef t : plateau.getChefs()) {
                    if (t!=c) {
                        t.setEnClic(false);
                        t.changeDernierEnClic(false);
                    }
                }
            }
            else c.setEnClic(false); //inutile?
        }
    }

    public void sauvDernierChefEnClic(){
        for (Chef c : plateau.getChefs()) {
            if(c.enClic()) c.changeDernierEnClic(true);
            else c.changeDernierEnClic(false);
        }
    }

    public boolean chefClic(Unite c, Position souris){
        int larg = 16;
        return souris.dansRectangle(new Position
                (c.getPosition().getXint() - 2 - larg/2.0 + larg/2.0, c.getPosition().getYint()- Chef.TAILLE/4.0 + Chef.TAILLE/4.0), larg, Chef.TAILLE/2.0);

    }

    public boolean ouvrierClic(Unite c, Position souris){
        int larg = 20;
        return souris.dansRectangle(new Position
                (c.getPosition().getXint() + 2 - larg/2.0 + larg/2.0, c.getPosition().getYint() - Ouvrier.TAILLE/4.0 + Ouvrier.TAILLE/4.0 ), larg, Chef.TAILLE/2.0 - 4);

    }

    public void drawClic(Graphics g){
        int tailleX = 1;
        int tailleY = 1;
        int xx = point.x;
        int yy = point.y;
        int x;
        int y = 20*((point.y)/20);

        if((y/20)%2==1){
            x = 30*((point.x+15)/30)-15;
            System.out.println(y);
        }
        else{
            x = 30*((point.x )/30);
        }

        g.fillRect(xx-5, yy-5, 10 ,10);
        g.setColor(Color.RED);
        g.fillPolygon(
                new int[]{ x     , x + 15, x + 30, x + 30, x + 15, x      },
                new int[]{ y + 10, y     , y + 10, y + 20, y + 30, y + 20 },
                6);

        System.out.println("(" + xx + "," + yy + ")");

        System.out.println("(" + x + "," + y + ")");
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int vitesse = 5;
        Unite uniteEnClic = null;
        ArrayList<Ouvrier> ouvriersSelectionnables = new ArrayList<>();
        for(Ouvrier o : this.plateau.getOuvriers()) if(o.selectionnable()) ouvriersSelectionnables.add(o);
        if(etat == 0) {
            for (Chef c : this.plateau.getChefs()) if (c.enClic()) uniteEnClic = c;
        }
        else if(etat == 1) {
            for(Ouvrier o : this.plateau.getOuvriers()) if(o.enClic()) uniteEnClic = o;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_Z -> {
                haut = true;
            }
            case KeyEvent.VK_S -> {
                bas = true;
            }
            case KeyEvent.VK_Q -> {
                gauche = true;
            }
            case KeyEvent.VK_D -> {
                droite = true;
            }
            case KeyEvent.VK_DOWN, KeyEvent.VK_UP ->{
                for(Chef c : plateau.getChefs()) c.setEnClic(false);
                for(Ouvrier o : plateau.getOuvriers()) o.setEnClic(false);
                for(Batiment b : plateau.getBatiments()) b.setEnClic(false);
                for(Batiment b : plateau.getMines()) b.setEnClic(false);
                etat = ((((e.getKeyCode() == KeyEvent.VK_DOWN ? (etat-1) : (etat+1) )%3) +3) % 3) ;
                System.out.println("etat : " + etat + " cpt : " + cptEnClic);
                System.out.println(plateau.getMinesBats());
                cptEnClic = 0;
                if(etat == 0 && !plateau.getChefs().isEmpty()) this.plateau.getChefs().get(cptEnClic).setEnClic(true);
                else if(etat == 1 && !ouvriersSelectionnables.isEmpty()) {
                    System.out.println("ok");
                    ouvriersSelectionnables.get(cptEnClic).setEnClic(true);
                }
                else if(etat == 2 && !plateau.getMinesBats().isEmpty()) plateau.getMinesBats().get(cptEnClic).setEnClic(true);

            }
            case KeyEvent.VK_LEFT -> {
                if(etat == 0 && !plateau.getChefs().isEmpty()) plateau.getChefs().get(cptEnClic).setEnClic(false);
                else if(etat == 1 && !ouvriersSelectionnables.isEmpty()) ouvriersSelectionnables.get(cptEnClic).setEnClic(false);
                else if(etat == 2 && !plateau.getMinesBats().isEmpty()) plateau.getMinesBats().get(cptEnClic).setEnClic(false);

                cptEnClic--;
                if(cptEnClic < 0) {
                    if (etat == 0) {
                        cptEnClic = plateau.getChefs().size() - 1;
                    } else if (etat == 1) {
                        cptEnClic = ouvriersSelectionnables.size() - 1;
                    } else {
                        cptEnClic = plateau.getMinesBats().size() - 1;
                    }
                }
                if(etat == 0) plateau.getChefs().get(cptEnClic).setEnClic(true);
                else if(etat == 1 && !ouvriersSelectionnables.isEmpty()) ouvriersSelectionnables.get(cptEnClic).setEnClic(true);
                else plateau.getMinesBats().get(cptEnClic).setEnClic(true);
            }
            case KeyEvent.VK_RIGHT -> {
                if(etat == 0) plateau.getChefs().get(cptEnClic).setEnClic(false);
                else if(etat == 1) ouvriersSelectionnables.get(cptEnClic).setEnClic(false);
                else plateau.getMinesBats().get(cptEnClic).setEnClic(false);

                cptEnClic++;
                if ( (etat == 0 && cptEnClic >= plateau.getChefs().size())
                        || (etat == 1 && cptEnClic >= ouvriersSelectionnables.size())
                        || (etat == 2 && cptEnClic >= plateau.getMinesBats().size())) {
                    cptEnClic = 0;
                }
                if(etat == 0) plateau.getChefs().get(cptEnClic).setEnClic(true);
                else if(etat == 1 && !ouvriersSelectionnables.isEmpty()) ouvriersSelectionnables.get(cptEnClic).setEnClic(true);
                else plateau.getMinesBats().get(cptEnClic).setEnClic(true);
            }
            case KeyEvent.VK_C -> {
                panneauGestion.etatConstruire = !panneauGestion.etatConstruire;
                panneauGestion.afficheBoutonConstructionBats(panneauGestion.etatConstruire);
            }
            case KeyEvent.VK_P -> {
                plateau.enJeu = !plateau.enJeu;
            }

            //17 boutons
        }

        if(uniteEnClic != null) {
            deplaceUnite(uniteEnClic, vitesse);
        }

    }

    public void deplaceUnite(Unite uniteEnClic, int vitesse){
        if (haut && uniteEnClic.getPosition().getYint() - vitesse >= 0) {
            if (plateau.getEtatPlateau().get(plateau.positions
                    [uniteEnClic.getPosition().getXint()][uniteEnClic.getPosition().getYint() - vitesse]))
                uniteEnClic.setPosition(uniteEnClic.getPosition().getXint(), uniteEnClic.getPosition().getYint() - vitesse);
        }
        if (bas && uniteEnClic.getPosition().getYint() + vitesse < Grille.HAUT_VILLAGE) {
            if (plateau.getEtatPlateau().get(plateau.positions
                    [uniteEnClic.getPosition().getXint()][uniteEnClic.getPosition().getYint() + vitesse]))
                uniteEnClic.setPosition(uniteEnClic.getPosition().getXint(), uniteEnClic.getPosition().getYint() + vitesse);
        }
        if (gauche && uniteEnClic.getPosition().getXint() - vitesse >= 0 ) {
            if (plateau.getEtatPlateau().get(plateau.positions
                    [uniteEnClic.getPosition().getXint() - vitesse][uniteEnClic.getPosition().getYint()]))
                uniteEnClic.setPosition(uniteEnClic.getPosition().getXint() - vitesse, uniteEnClic.getPosition().getYint());
        }
        if (droite && uniteEnClic.getPosition().getXint() + vitesse < Grille.LARG_VILLAGE ) {
            if (plateau.getEtatPlateau().get(plateau.positions
                    [uniteEnClic.getPosition().getXint() + vitesse][uniteEnClic.getPosition().getYint()]))
                uniteEnClic.setPosition(uniteEnClic.getPosition().getXint() + vitesse, uniteEnClic.getPosition().getYint());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_Z -> {
                haut = false;
            }
            case KeyEvent.VK_S -> {
                bas = false;
            }
            case KeyEvent.VK_Q -> {
                gauche = false;
            }
            case KeyEvent.VK_D -> {
                droite = false;
            }
        }
    }
}