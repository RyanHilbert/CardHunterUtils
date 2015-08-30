package card.hunter;

import java.util.ArrayList;
import java.util.Iterator;

public class Build{
    // <editor-fold defaultstate="collapsed" desc="Props and getters">

    public final String name;
    public final int minLevel;
    public final Race race;
    public final Class role;
    public final ArrayList<Item> items;

    public String getName(){
        return name;
    }

    public int getMinLevel(){
        return minLevel;
    }

    public Race getRace(){
        return race;
    }

    public Class getRole(){
        return role;
    }

    public Iterator<Item> getItems(){
        return items.iterator();
    }

    // </editor-fold>
    public Build(String name,Race race,Class role,int minLevel,Item... items){
        this.name=name;
        this.minLevel=minLevel;
        this.race=race;
        this.role=role;
        this.items=new ArrayList<Item>(12);
        for(Item i : items){
            this.items.add(i);
        }
    }

    public static boolean areIdentical(Build a,Build b){
        boolean same=false;

        if(a!=null&&b!=null){
            same
                =a.name.equals(b.name)
                &&a.minLevel==b.minLevel
                &&a.race==b.race
                &&a.role==b.role
                &&a.items.toString().equals(b.items.toString());
        }

        return same;
    }

    @Override
    public String toString(){
        return name+": "+items.toString();
    }
}
