package card.hunter;

// Class for representing a party of Card Hunter characters
import card.hunter.collectible.Equipment;
import java.util.ArrayList;

public class Party extends ArrayList<Character>{
    // <editor-fold defaultstate="collapsed" desc="Singleton implementation">
    private static Party instance=null;

    private static Party get(){
        if(instance==null){
            instance=new Party();
        }

        return instance;
    }
    // </editor-fold>

    private CountBag<Equipment> equipment=new CountBag<>();

    public static void add(Equipment item){
        get().equipment.add(item);
    }

    public static void remove(Equipment item){
        get().equipment.remove(item);
    }

    public static int count(Equipment item){
        return get().equipment.countOf(item);
    }
}
