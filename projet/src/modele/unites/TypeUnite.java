package modele.unites;

public enum TypeUnite {
    CHEF, ENNEMI, OUVRIER;

    public static boolean estAllie(Unite unite){
        return unite.typeUnite == CHEF || unite.typeUnite == OUVRIER;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
