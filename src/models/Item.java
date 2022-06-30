package models;

import app.App;
import utils.CSV;
import utils.AssetLoader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

//class used to represent every item in the game, from treasures to weapons
public class Item{
    //list contains every item in the game
    public final static ObservableList<Item>list=FXCollections.observableArrayList();
    public static enum Slot{
        Arcane_Item,Arcane_Skill,Boots,Divine_Armor,Divine_Item,Divine_Skill,Divine_Weapon,Dwarf_Skill,Elf_Skill,Heavy_Armor,Helmet,Human_Skill,Martial_Skill,Robes,Shield,Staff,Treasure,Weapon;
        public Item dfault;//hack used to give each slot a default item value during the global item list initialization
        @Override public String toString(){return super.toString().replace('_',' ');}
    }
    public static enum Token{
        None((byte)0),//arrays represent amount of corresponding tokens at each level after the token's introduction
        Minor((byte)7,new byte[]{1,2,3,4,5,6,7,8,7,6,5,4,5,6,5,4,3,2,2,2,2,2,2,2,3,2,2,2,2,3,2,1}),
        Major((byte)15,new byte[]{1,2,3,4,4,4,5,6,7,8,7,6,5,4,3,2,2,3,3,3,3,3,4,5,6,5,4,3,2,1}),
        Great((byte)25,new byte[]{1,2,3,4,5,6,6,6,5,4,3,3,3,3,3,4,5,5,6,7,8,8,8,8,8,8}),
        Ultimate((byte)33,new byte[]{1,2,3,3,3,3,3,3,3,4,4,4,4,4,4,4,4,4});
        public final byte firstLevel;
        private final byte[]levelAmounts;
        public final Image icon;
        private Token(byte first,byte...amounts){
            this.firstLevel=first;
            this.levelAmounts=amounts;
            this.icon=new Image('/'+toString()+".png");
        }
        private static final Token[]values=values();
        public static Token value(int i){return values[i];}
        public View getView(){return new View();}
        public int getAmountAtLevel(int level){
            if(level<firstLevel)return 0;
            if(level-firstLevel>=levelAmounts.length)return 0;
            return levelAmounts[level-firstLevel];
        }
        public class View extends ImageView implements Comparable<View>{
            public View(){super(icon);}
            public Token getToken(){return Token.this;}
            @Override public int compareTo(View view){
                return ordinal()-view.getToken().ordinal();
            }
        }
        
        //hack used to turn token pairs into an Enum for use with other classes
        public static enum Pair{
            None_None,Minor_None,Minor_Minor,Major_None,Major_Minor,Major_Major,Great_Major;//,Great_None,Great_Major,Great_Great,Ultimate_None,Ultimate_Great,Ultimate_Ultimate;
            public static Pair get(Token t1,Token t2){return Pair.valueOf(t1+"_"+t2);}
            public View getView(){return new View();}
            public HView getHView(){return new HView();}
            public String string(){return super.toString();}
            @Override public String toString(){return super.toString().replace("None","").replace('_',' ');}
            
            public class View extends VBox implements Comparable<View>{
                public View(){
                    String string=Pair.this.string();
                    int i=string.indexOf('_');
                    getChildren().addAll(Token.valueOf(string.substring(0,i)).getView(),Token.valueOf(string.substring(i+1)).getView());
                }
                public Pair getPair(){return Pair.this;}
                @Override public int compareTo(View view){
                    return ordinal()-view.getPair().ordinal();
                }
            }
            public class HView extends HBox implements Comparable<HView>{
                public HView(){
                    String string=Pair.this.string();
                    int i=string.indexOf('_');
                    getChildren().addAll(Token.valueOf(string.substring(0,i)).getView(),Token.valueOf(string.substring(i+1)).getView());
                }
                public Pair getPair(){return Pair.this;}
                @Override public int compareTo(HView view){
                    return ordinal()-view.getPair().ordinal();
                }
            }
        }
    }
    public final int id;
    public final String name;
    public final Rarity rarity;
    public final byte level;
    public final Token token1,token2;
    public final Card card1,card2,card3,card4,card5,card6;
    public final Slot slot;
    public final Set set;
    public final Image icon;
    public final int totalDamage;
    private final HashMap<CalculatedProperties.Names, Integer> cardProps = new HashMap<>();
    
    public int getId(){return id;}
    public String getName(){return name;}
    public Rarity getRarity(){return rarity;}
    public byte getLevel(){return level;}
    public Token getPrimaryToken(){return token1;}
    public Token getSecondaryToken(){return token2;}
    public Slot getSlot(){return slot;}
    public Set getSet(){return set;}
    public Image getIcon(){return icon;}
    public int getTotalDamage() { return totalDamage; }
    public final int getCardProp(CalculatedProperties.Names name) { 
        return cardProps.get(name); 
    }
    public ImageView getView(){ImageView view=new ImageView(icon);view.setOnMouseClicked(e->System.out.println("Test"));return view;}
    public Token.View getPrimaryTokenView(){return token1.getView();}
    public Token.View getSecondaryTokenView(){return token2.getView();}
    public Token.Pair.View getTokenView(){return Token.Pair.get(token1,token2).getView();}
    public Token.Pair.HView getTokenHView(){return Token.Pair.get(token1,token2).getHView();}
    public HBox getCardView(){
        HBox hbox=new HBox();
        ObservableList<Node>children=hbox.getChildren();
        if(card1!=null)children.add(card1.getThumbnail());
        if(card2!=null)children.add(card2.getThumbnail());
        if(card3!=null)children.add(card3.getThumbnail());
        if(card4!=null)children.add(card4.getThumbnail());
        if(card5!=null)children.add(card5.getThumbnail());
        if(card6!=null)children.add(card6.getThumbnail());
        return hbox;
    }

    public int getQuantity(){
        return Hoard.countOf(this);
    }

    public int getNumInUse(){
        return Party.count(this);
    }

    private Item(int id,String name,Rarity rarity,byte level,Token token1,Token token2,Card card1,Card card2,Card card3,Card card4,Card card5,Card card6,Slot slot,String image,Set set){
        this.id=id;
        this.name=name;
        this.rarity=rarity;
        this.level=level;
        this.token1=token1;
        this.token2=token2;
        this.card1=card1;
        this.card2=card2;
        this.card3=card3;
        this.card4=card4;
        this.card5=card5;
        this.card6=card6;
        this.calculateCardProps();
        this.totalDamage = this.getCardProp(CalculatedProperties.Names.Damage);
        this.slot=slot;
        this.set=set;
        this.icon=AssetLoader.loadImage(AssetLoader.ImageType.Item_Illustrations,image);
        if(image.contains("Default Item"))slot.dfault=this;
        list.add(this);
    }
    
    private void calculateCardProps() {
        for (CalculatedProperties.Names prop : CalculatedProperties.Names.values())
            if (prop.isCardDriven())
                cardProps.put(prop, CalculatedProperties.calc(prop, this));
    }
    
    // TODO: Get this to be observable instead of public static :/
    // Basically, fix the initialization timing so that this happens on Party/Hoard load
    public static void CalculateStateDependentItemProps() {
        for (Item item : list)
            for (CalculatedProperties.Names prop : CalculatedProperties.Names.values())
                if (prop.isStateDriven())
                    item.cardProps.put(prop, CalculatedProperties.calc(prop, item));
    }
    
    //passes CSV data into the Item constructor
    public static Item fromCSV(String string){
        String[]s=CSV.tokenizeLine(string);
        byte token2=0;//must handle empty string and -1
        try{token2=Byte.parseByte(0+s[8]);}
        catch(NumberFormatException e){}
        return new Item(Integer.parseInt(s[0],10),s[1],
                Rarity.valueOf(s[3]),
                Byte.parseByte(s[4]),
                Token.value(Byte.parseByte(0+s[7])),
                Token.value(token2),
                Card.map.get(s[9]),
                Card.map.get(s[10]),
                Card.map.get(s[11]),
                Card.map.get(s[12]),
                Card.map.get(s[13]),
                Card.map.get(s[14]),
                Slot.valueOf(s[19].replace(' ','_')),
                s[21],
                Set.value(Byte.parseByte(0+s[23]))
        );
    }

    public static Item byName(String name){
        for(Item i : list){
            if(i.name.equalsIgnoreCase(name)){
                return i;
            }
        }

        return null;
    }
    
    public static ArrayList<Item> byName(String... names){
        ArrayList<Item> retVal = new ArrayList<>(names.length);
        
        for(String n : names)
            retVal.add(Item.byName(n));

        return retVal;
    }
    
    public static Item byId(int id){
        for(Item i : list){
            if(i.id == id){
                return i;
            }
        }

        return null;
    }
    
    public static ArrayList<Item> byName(int... ids){
        ArrayList<Item> retVal = new ArrayList<>(ids.length);
        
        for(int n : ids)
            retVal.add(Item.byId(n));

        return retVal;
    }

    //public static Item EmptyItem=new Item("_",Rarity.Common,(byte) 0,Token.None,Token.None,null,null,null,null,null,null,Slot.Boots,"",Set.Base);
	
    public static Comparator<Item> nameComparer=(a,b) -> {
        return a.name.compareTo(b.name);
    };

    
    public String toCardViewJson() {
        return this.toCardViewJson(App.state().compareCardArt);
    }

    public String toCardViewJson(boolean comparisons) {
        String format = 
            "cardViewData = {\n" +
            "title: \"%s\", \n" +
            "cards: [\"%s\"], \n" +
            "comparisons: %s };";
        
        String json = String.format(format, 
            this.name,
            this.getCardsString(),
            comparisons
        );
        
        return json;
    }
    
    public String getCardsString() {
        ArrayList<String> cards = new ArrayList<String>();
        
        if (card1 != null)
            cards.add(card1.name);
        if (card2 != null)
            cards.add(card2.name);
        if (card3 != null)
            cards.add(card3.name);
        if (card4 != null)
            cards.add(card4.name);
        if (card5 != null)
            cards.add(card5.name);
        if (card6 != null)
            cards.add(card6.name);
        
        return String.join("\", \"", cards.toArray(new String[cards.size()]));
    }
	
    @Override public String toString(){return name;}
}