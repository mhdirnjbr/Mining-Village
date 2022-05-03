package ressources;

public class Ressource {

    private Item item;
    private int quantite;

    public Ressource(Item item){
        this.item = item;
        this.quantite = 0;
    }

    public Ressource(Item item, int q) {
        this.item = item;
        this.quantite = q;
    }


    public int getPrix() {
        int prix = 0;
        switch(this.item) {
            case OR:
                prix = 50;
                break;
            case FER:
                prix = 20;
                break;
            case CHARBON:
                prix = 10;
                break;
            default:
        }
        return prix;
    }

    public int getPieces() {
        int prix = 0;
        switch(this.item) {
            case OR:
                prix = 50*this.quantite;
                break;
            case FER:
                prix = 20*this.quantite;
                break;
            case CHARBON:
                prix = 10*this.quantite;
                break;
            default:
        }
        return prix;
    }

    public Item getItem(){
        return this.item;
    }

    public int getQuantite() {
        return quantite;
    }

    public void ajouteQuantite(int x){
        this.quantite += x;
    }

    public void setQuantite(int x) {
        this.quantite = x;
    }

    public boolean estMinerais(){
        return this.item == Item.OR || this.item == Item.FER || this.item == Item.CHARBON;
    }

    @Override
    public String toString() {
        return item + " " + quantite;
    }
}