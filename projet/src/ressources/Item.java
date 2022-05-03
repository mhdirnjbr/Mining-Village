package ressources;

public enum Item {
    OR, FER, CHARBON, PIECE, PAIN;

    public static boolean estMinerais(Item item){
        return item == OR || item == FER || item == CHARBON;
    }
}
