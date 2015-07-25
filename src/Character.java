import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public class Character{

    public final static ObservableList<Character> roster=FXCollections.observableArrayList();

    // <editor-fold defaultstate="collapsed" desc="Race and Role Enums">
    public static enum Race{
        Dwarf, Elf, Human;
        private Race(){}
        private static final Race[] values=values();
        public static Race value(int i){
            return values[i];
        }
    }

    public static enum Role{
        Priest, Warrior, Wizard;
        private Role(){}
        private static final Role[] values=values();
        public static Role value(int i){
            return values[i];
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Props and getters">
    public final String name;
    public final int level;
    public final Race race;
    public final Role role;
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

    public Role getRole(){
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
    private Character(String name,int level,Race race,Role role,String image,Item... items){
        this.name=name;
        this.level=level;
        this.race=race;
        this.role=role;
        this.figure=AssetLoader.loadImage(AssetLoader.ImageType.Large_Portraits,image);
        this.equipment=new Build(name+"'s Gear",race,role,level,items);
        roster.add(this);
    }

    public static ArrayList<Character> allFromBBCode(String bbCode){
        ArrayList<Character> chars=new ArrayList<>();

        String remains=String.valueOf(bbCode);
        int start=remains.indexOf("[SIZE=");
        int end=remains.indexOf("/INDENT]")+8; // len of /INDENT]
        while(start>=0){
            String block=remains.substring(start,end);
            chars.add(firstFromBBCode(block));

            remains=remains.substring(end); 
            start=remains.indexOf("[SIZE=");
            end=remains.indexOf("/INDENT]")+8; // len of /INDENT]
        }

        return chars;
    }

    public static Character firstFromBBCode(String bbCode){
        int nextChar=bbCode.indexOf("/INDENT]")+8;
        if(nextChar>=8)
            bbCode=bbCode.substring(0,nextChar);

        Matcher m=Pattern.compile(".*"+Pattern.quote("[B]")+"(.*)"+Pattern.quote("[/B]")+".*").matcher(bbCode);

        String name=(m.find()) ? m.group(1) : "New Adventurer";
        int level = 1;
        Race race = Race.Human;
        Role role = Role.Warrior;
        
        String oWiki=Pattern.quote("[wiki]");
        String cWiki=Pattern.quote("[/wiki]");
        
        m=Pattern.compile(".*Level ([0-9]+) "+oWiki+"(.*)"+cWiki+" "+oWiki+"(.*)"+cWiki+".*").matcher(bbCode);
        if (m.find()) {
            level = Integer.parseInt(m.group(1));
            race = Race.valueOf(m.group(2));
            role = Role.valueOf(m.group(3));
        }

        ArrayList<Item> items = new ArrayList<>();
        String oItem=Pattern.quote("[item]");
        String cItem=Pattern.quote("[/item]");
        
        m=Pattern.compile(".*"+oItem+"(.*)"+cItem+".*").matcher(bbCode);
        while (m.find())
            items.add(Item.byName(m.group(1)));
        
        if (items.isEmpty())
            items.add(Item.byName("Adventuring Boots"));
        
        return new Character(name,level,race,role,"HumanWarriorM01A",items.toArray(new Item[items.size()])); // Item.EmptyItem
    }

    public static Character byName(String name){
        for(Character c : roster){
            if(c.name.equalsIgnoreCase(name)){
                return c;
            }
        }

        return null;
    }

    public static ArrayList<Character> byName(String... names){
        ArrayList<Character> retVal=new ArrayList<>(names.length);

        for(String n : names){
            retVal.add(Character.byName(n));
        }

        return retVal;
    }

    public static boolean areIdentical(Character a,Character b){
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
}
