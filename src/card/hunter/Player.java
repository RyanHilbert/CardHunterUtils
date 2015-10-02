package card.hunter;

import card.hunter.collectible.Equipment;
import card.hunter.io.Assets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public class Player{

    // <editor-fold defaultstate="collapsed" desc="Consts">
    public final static String BLOCK_START="[SIZE=";
    public final static String BLOCK_END="/INDENT]";
    public final static int MAX_EXPORT_ITEMS = 10;
    // </editor-fold>

    public final static ObservableList<Player> roster=FXCollections.observableArrayList();

    // <editor-fold defaultstate="collapsed" desc="Props and getters">
    public final String name;
    public final int level;
    public final Race race;
    public final Character role;
    public final Build equipment;
    public final Image figure;

    public String getName(){
        return name;
    }

    public int getLevel(){
        return level;
    }

    public Race getRace(){
        return race;
    }

    public Character getRole(){
        return role;
    }

    public Build getEquipment(){
        return equipment;
    }

    public Image getFigure(){
        return figure;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    public Player(String name,int level,Race race,Character role,String image,Equipment... items){
        this.name=name;
        this.level=level;
        this.race=race;
        this.role=role;
        this.figure=Assets.large_portraits.load(image);
        this.equipment=new Build(name+"'s Gear",race,role,level,items);
        roster.add(this);
    }

    public static ArrayList<Player> allFromBBCode(String bbCode){
        ArrayList<Player> chars=new ArrayList<>();

        String remains=String.valueOf(bbCode);
        int start=remains.indexOf(BLOCK_START);
        int end=remains.indexOf(BLOCK_END)+BLOCK_END.length();

        while(start>=0){
            String block=remains.substring(start,end);
            chars.add(firstFromBBCode(block));

            remains=remains.substring(end); 
            start=remains.indexOf(BLOCK_START);
            end=remains.indexOf(BLOCK_END)+BLOCK_END.length();

            // check for long-form bbCode - two block ends before next start
            if(end<start){
                remains=remains.substring(end);
                start=remains.indexOf(BLOCK_START);
                end=remains.indexOf(BLOCK_END)+BLOCK_END.length();
            }
        }

        return chars;
    }

    public static Player firstFromBBCode(String bbCode){
        int nextChar=bbCode.indexOf("/INDENT]")+8;
        if(nextChar>=8)
            bbCode=bbCode.substring(0,nextChar);

        Matcher m=Pattern.compile(".*"+Pattern.quote("[B]")+"(.*)"+Pattern.quote("[/B]")+".*").matcher(bbCode);

        String name=(m.find()) ? m.group(1) : "New Adventurer";
        int level = 1;
        Race race = Race.Human;
        Character role = Character.Warrior;
        
        String oWiki=Pattern.quote("[wiki]");
        String cWiki=Pattern.quote("[/wiki]");
        
        m=Pattern.compile(".*Level ([0-9]+) "+oWiki+"(.*)"+cWiki+" "+oWiki+"(.*)"+cWiki+".*").matcher(bbCode);
        if (m.find()) {
            level = Integer.parseInt(m.group(1));
            race = Race.valueOf(m.group(2));
            role = Character.valueOf(m.group(3));
        }

        ArrayList<Equipment> items = new ArrayList<>();
        String oItem=Pattern.quote("[item]");
        String cItem=Pattern.quote("[/item]");
        
        m=Pattern.compile(".*"+oItem+"(.*)"+cItem+".*").matcher(bbCode);
        while (m.find())
            items.add(Equipment.nameMap().get(m.group(1)));
        
        if (items.isEmpty())
            items.add(Equipment.nameMap().get("Adventuring Boots"));
        
        return new Player(name,level,race,role,"HumanWarriorM01A",items.toArray(new Equipment[items.size()])); // Item.EmptyItem
    }

    public static Player byName(String name){
        for(Player c : roster){
            if(c.name.equalsIgnoreCase(name)){
                return c;
            }
        }

        return null;
    }

    public static ArrayList<Player> byName(String... names){
        ArrayList<Player> retVal=new ArrayList<>(names.length);

        for(String n : names){
            retVal.add(Player.byName(n));
        }

        return retVal;
    }

    public static boolean areIdentical(Player a,Player b){
        boolean same=false;

        if(a!=null&&b!=null){
            same
                =a.name.equals(b.name)
                &&a.level==b.level
                &&a.race==b.race
                &&a.role==b.role
                &&Build.areIdentical(a.equipment,b.equipment);
        }

        return same;
    }

    @Override
    public String toString(){
        return name;
    }
    
    public String toBBCode(String charTemplate, String itemTemplate) {
        ArrayList<String> items = new ArrayList<String>();
        
        for (Equipment i : equipment.items)
        {
            if (items.size() < MAX_EXPORT_ITEMS)
                items.add(String.format(itemTemplate, i.name));
        }
        
        String bbCode = String.format(charTemplate, name, level, race, role, String.join("\n", items));
        
        return bbCode;
    }
}
