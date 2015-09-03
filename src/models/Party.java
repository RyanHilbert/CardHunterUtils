package models;

// Class for representing a party of Card Hunter characters
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

    private CountBag<Item> equipment=new CountBag<>();

    public static void add(Item item){
        get().equipment.add(item);
    }

    public static void remove(Item item){
        get().equipment.remove(item);
    }

    public static int count(Item item){
        return get().equipment.countOf(item);
    }
}
