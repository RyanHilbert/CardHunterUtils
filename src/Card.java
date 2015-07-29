import java.util.LinkedHashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

//class used to represent every card in the game
public class Card{
    //map links every card's name to its object identity
    public static final ObservableMap<String,Card>map=FXCollections.observableMap(new LinkedHashMap());
    
    public static final int ILLUSTRATION_WIDTH=217,ILLUSTRATION_HEIGHT=129,THUMBNAIL_WIDTH=62,THUMBNAIL_HEIGHT=37;
    
    public enum Type{
        Armor(Color.GRAY),Assist(Color.LIGHTGRAY),Attack(Color.DARKRED),Block(Color.OLIVEDRAB),Boost(Color.CHOCOLATE),Handicap(new Color(.2,.2,.2,1)),Move(Color.STEELBLUE),Utility(Color.MEDIUMPURPLE);
        public final Color color;
        private Type(Color color){this.color=color;}
    }
    public enum AttackType{Magic,Melee,Projectile}
    public enum DamageType{Acid,Arcane,Cold,Crushing,Electrical,Fire,Holy,Laser,Piercing,Poison,Psychic,Radiation,Slashing,Sonic,Unholy}
    public enum Quality{
        Black_,Black,Black$,
        Paper_,Paper,Paper$,
        Bronze_,Bronze,Bronze$,
        Silver_,Silver,Silver$,
        Gold_,Gold,Gold$,
        Emerald_,Emerald,Emerald$,
        Amethyst_,Amethyst,Amethyst$;
        @Override public String toString(){return super.toString().replace('_','-').replace('$','+');}
        private static Quality[]values=values();
        public static Quality value(int i){return values[i];}
        public static Quality fromStrings(String mod,String quality){
            byte val=1+Byte.MIN_VALUE;
            switch(quality){
                case"E":val=-3;break;
                case"D":val=0;break;
                case"C":val=3;break;
                case"B":val=6;break;
                case"A":val=9;break;
                case"AA":val=12;break;
                case"AAA":val=15;break;
            }
            switch(mod){
                case"+":++val;break;
                case"-":--val;break;
            }
            return values[val+4];
        }
        public Color getColor(){
            switch(ordinal()/3){
                case 0:return Color.BLACK;
                case 1:return Color.TAN;
                case 2:return Color.SIENNA;
                case 3:return Color.SILVER;
                case 4:return Color.GOLD;
                case 5:return Color.CHARTREUSE;
                case 6:return Color.VIOLET;
                default:return Color.TRANSPARENT;
            }
        }
    }
    
    public class View extends Group{
        public static final int WIDTH=234,HEIGHT=333,BORDER=(WIDTH-ILLUSTRATION_WIDTH-1)/2+1;
        public View(){
            ObservableList<Node>children=this.getChildren();
            children.add(new Rectangle(WIDTH,HEIGHT,Card.this.type1.color));
            if(Card.this.type2!=null){
                Rectangle rect=new Rectangle(WIDTH/2,HEIGHT,Card.this.type2.color);
                rect.setX(WIDTH/2);
                children.add(rect);
            }
            int y=BORDER+25;
            
            Rectangle rect=new Rectangle(WIDTH-2*BORDER,ILLUSTRATION_HEIGHT,Card.this.type1.color.brighter());
            rect.setX(BORDER);
            rect.setY(y);
            children.add(rect);
            
            Label titleBar=new Label(Card.this.name);
            titleBar.setFont(Font.font(null,FontWeight.BOLD,16));
            titleBar.setAlignment(Pos.CENTER);
            titleBar.setMinWidth(WIDTH);
            titleBar.setBackground(new Background(new BackgroundFill(Card.this.quality.getColor(),null,null)));
            children.add(titleBar);
            
            ImageView image=new ImageView(Card.this.art);
            image.setX(BORDER);
            image.setY(y);
            children.add(image);
            
            y+=BORDER+ILLUSTRATION_HEIGHT;
            int W=WIDTH-2*BORDER,H=ILLUSTRATION_HEIGHT;
            if(Card.this.attackType!=null||Card.this.damageType!=null){
                Label typeBox=new Label(Card.this.attackType+" "+Card.this.damageType);
                typeBox.setBackground(new Background(new BackgroundFill(Color.BEIGE,null,null)));
                typeBox.setTranslateX(BORDER);
                typeBox.setTranslateY(y);
                typeBox.setMinWidth(W);
                typeBox.setMaxWidth(W);
                children.add(typeBox);
                y+=3*BORDER;
            }
            if(!Card.this.text.isEmpty()||!Card.this.flavorText.isEmpty()){
                Label flavorBox=new Label(Card.this.text+"\n\n"+Card.this.flavorText);
                flavorBox.setBackground(new Background(new BackgroundFill(Color.BEIGE,null,null)));
                flavorBox.setAlignment(Pos.TOP_LEFT);
                flavorBox.setWrapText(true);
                flavorBox.setTranslateX(BORDER);
                flavorBox.setTranslateY(y);
                if(!Card.this.triggerText.isEmpty())y+=BORDER+(H/=2);
                flavorBox.setMinSize(W,H);
                flavorBox.setMaxSize(W,H);
                children.add(flavorBox);
            }
            if(!Card.this.triggerText.isEmpty()){
                Label textBox=new Label(Card.this.triggerText);
                textBox.setBackground(new Background(new BackgroundFill(Color.BEIGE,null,null)));
                textBox.setAlignment(Pos.TOP_LEFT);
                textBox.setWrapText(true);
                textBox.setTranslateX(BORDER);
                textBox.setTranslateY(y);
                textBox.setMinSize(W,H);
                textBox.setMaxSize(W,H);
                children.add(textBox);
            }
        }
    }
    
    //class responsible for displaying the thumbnail version of a Card
    public class Thumbnail extends Group{
        public Thumbnail(){
            setOnMouseClicked(mouse->{
                if(MouseHelper.isRightClick(mouse))Card.this.context.show(this,mouse.getScreenX(),mouse.getScreenY());
            });
            final double height=60,width=height/1.4,border=2;
            ObservableList<Node>children=getChildren();
            Rectangle rect=new Rectangle(width,height,Card.this.type1.color);
            rect.setStroke(Color.BLACK);
            children.add(rect);
            if(Card.this.type2!=null){
                rect=new Rectangle(width/2,height,Card.this.type2.color);
                rect.setX(width/2);
                children.add(rect);
            }
            ImageView view=new ImageView(Card.this.thumbnail);
            view.setPreserveRatio(true);
            view.setFitWidth(width-2*border);
            view.setX(border);
            Bounds bounds=view.getBoundsInLocal();
            
            view.setY(bounds.getHeight()/2);
            rect=new Rectangle(bounds.getWidth(),bounds.getHeight(),Card.this.type1.color);
            rect.setX(view.getX());
            rect.setY(view.getY());
            rect.setStroke(Color.BLACK);
            children.add(rect);
            children.add(view);
            
            Label label=new Label(Card.this.shortName);
            label.setWrapText(true);
            label.setMinWidth(width);
            label.setMaxWidth(width);
            label.setMinHeight(view.getY()-border);
            label.setMaxHeight(view.getY()-border);
            label.setFont(new Font(6));
            label.setBackground(new Background(new BackgroundFill(Card.this.quality.getColor(),null,null)));
            children.add(label);
        }
    }
    private final ContextMenu context;
    public final int id;
    public final String name,shortName,text,flavorText,triggerText;
    public final Type type1,type2;
    public final AttackType attackType;
    public final DamageType damageType;
    public final byte damage,range,movement,duration;
    public final Quality quality;
    public final Rarity rarity;
    public final Action action;
    public final Trigger trigger1;
    public final Trigger trigger2;
    public final Set set;
    public final boolean implemented;
    public final Image art;
    public final boolean artHeld;
    public final Image thumbnail;
    public Thumbnail getThumbnail(){return new Thumbnail();}
    public View view(){return new View();}
    
    public Card(int id,String name,String shortName,Type type1,Type type2,AttackType attackType,DamageType damageType,byte damage,byte range,byte movement,byte duration,String text,String flavorText,String triggerText,Quality quality,Rarity rarity,Set set,Action action,Trigger trigger1,Trigger trigger2,boolean implemented,boolean artHeld){
        this.id=id;
        this.name=name;
        this.shortName=shortName.isEmpty()?name:shortName;
        this.type1=type1;
        this.type2=type2;
        this.attackType=attackType;
        this.damageType=damageType;
        this.damage=damage;
        this.range=range;
        this.movement=movement;
        this.duration=duration;
        this.text=text;
        this.flavorText=flavorText;
        this.triggerText=triggerText;
        this.action = action;
        this.trigger1 = trigger1;
        this.trigger2 = trigger2;
        this.quality=quality;
        this.rarity=rarity;
        this.set=set;
        this.implemented=implemented;
        this.artHeld=artHeld;
        if(!this.artHeld)
            this.art=AssetLoader.loadImage(AssetLoader.ImageType.Card_Illustrations,name);
        else
            this.art=null;

        this.thumbnail=AssetLoader.loadImage(AssetLoader.ImageType.Card_Thumbnails,name);
        this.context=new ContextMenu(new CustomMenuItem(new View()));
        map.put(name,this);
    }
    //passes CSV data into the Card constructor
    public static Card fromCSV(String string){
        String[]s=CSV.tokenizeLine(string);
        String[]types=s[3].split(",");
        return new Card(toInt(s[0]),s[1],s[2],
            Type.valueOf(types[0]),
            types.length<2?null:Type.valueOf(types[1]),
            s[4].isEmpty()?null:AttackType.valueOf(s[4]),
            s[5].isEmpty()?null:DamageType.valueOf(s[5]),
            toByte(s[6]),
            toByte(s[8]),
            toByte(s[9]),
            toByte(s[10]),
            s[17],s[18],s[20],
            Quality.fromStrings(s[39],s[40]),
            s[47].isEmpty()?null:Rarity.valueOf(s[47]),
            Set.value(toInt(s[53])),
            ((s[17]+s[18]).trim().length() > 0) ? new Action(Type.valueOf(types[0]), s[17], s[18], toByte(s[6]), toByte(s[8]), toByte(s[9])) : null,
            (s[13].trim().length() > 0) ? new Trigger(toInt(s[11]), toInt(s[12]), Trigger.Effect.valueOf(s[13]), s[20]) : null,
            (s[16].trim().length()>0) ? new Trigger(toInt(s[14]),toInt(s[15]),Trigger.Effect.valueOf(s[16]),s[24]) : null,
            !s[50].isEmpty(),
            (s[56].toLowerCase().equals("hold"))
        );
    }
    
    private static byte toByte(String field) {
        return (field == null || field.isEmpty()) ? (byte)0 : Byte.parseByte(field);
    }
    
    private static int toInt(String field) {
        return (field == null || field.isEmpty()) ? 0 : Integer.parseInt(field, 10);
    }
    
    // serializes a card into its corresponding json object
    public final String toJSON() {
        String format="{ id: %s, " +
            "name: \"%s\", " +
            "shortName: \"%s\", " +
            "type1: \"%s\", " +
            "type2: \"%s\",\n" +
            "attackType: \"%s\", " +
            "damageType: \"%s\", " +
            "damage: %s,\n" +
            "range: %s, " +
            "movement: %s, " +
            "duration: %s,\n" +
            "text: \"%s\",\n" +
            "flavorText: \"%s\",\n" +
            "triggerText: \"%s\",\n" +
            "quality: \"%s\", " +
            "rarity: \"%s\", " +
            "set: \"%s\", " +
            "image: { name: \"%s\", ext: \".png\" },\n" +
            "hasAction: %s, " +
            "hasEffect1: %s, " +
            "hasEffect2: %s,\n" +
            "implemented: %s,\n" +
            "artHeld: %s,\n" +
            "action: { \n" +
                "\ttext: \"%s\", icons: [ \n" +
                    "\t\t{ side: \"left\", type: \"%s\", amount: %s }, \n" +
                    "\t\t{ side: \"right\", type: \"%s\", amount: %s } \n" +
            " ] },\n" +
            "triggers: [ { \n" +
                "\ttext: \"%s\", icons: [ \n" +
                    "\t\t{ side: \"left\", type: \"%s\", amount: %s }, \n" +
                    "\t\t{ side: \"right\", type: \"%s\", amount: %s } \n" +
                " ] }, { \n" + 
                "\ttext: \"%s\", icons: [ \n" +
                    "\t\t{ side: \"left\", type: \"%s\", amount: %s }, \n" +
                    "\t\t{ side: \"right\", type: \"%s\", amount: %s } \n" +
            "] } ] }\n\n";
        
        boolean hasAction = this.action != null && !(this.action.text.isEmpty() && this.action.iconLeft.isEmpty() && this.action.iconRight.isEmpty());
        boolean hasTrigger1 = this.trigger1 != null && !this.trigger1.text.isEmpty() && !(this.trigger1.iconLeft.isEmpty() && this.trigger1.iconRight.isEmpty());
        boolean hasTrigger2 = this.trigger2 != null && !this.trigger2.text.isEmpty() && !(this.trigger2.iconLeft.isEmpty() && this.trigger2.iconRight.isEmpty());
        
        String json=String.format(format,
            this.id,
            safe(this.name),
            safe(this.shortName),
            safe(this.type1),
            safe(this.type2),
            safe(this.attackType),
            safe(this.damageType),
            this.damage,
            this.range,
            this.movement,
            this.duration,
            safe(this.text).replace("\"", "\\\""),
            safe(this.flavorText).replace("\"", "\\\""),
            safe(this.triggerText).replace("\"", "\\\""),
            safe(this.quality).replace("-", "").replace("+", ""),
            safe(this.rarity),
            safe(this.set),
            safe(this.name),
            hasAction,
            hasTrigger1,
            hasTrigger2,
            safe(this.implemented),
            safe(this.artHeld),
            hasAction ? this.action.fullText.replace("\"", "\\\"") : "",
            hasAction ? this.action.iconLeft : "",
            hasAction ? this.action.amountLeft : 0,
            hasAction ? this.action.iconRight : "",
            hasAction ? this.action.amountRight : 0,
            (hasTrigger1 ? this.trigger1.text.replace("\"", "\\\"") : "") + (hasAction && this.flavorText.length() > 0 ? "" : "<br /><br /><i>" + safe(this.flavorText.trim()).replace("\"", "\\\"") + "</i>"),
            hasTrigger1 ? this.trigger1.iconLeft : "",
            hasTrigger1 ? this.trigger1.amountLeft : 0,
            hasTrigger1 ? this.trigger1.iconRight : "",
            hasTrigger1 ? this.trigger1.amountRight : 0,
            hasTrigger2 ? this.trigger2.text.replace("\"", "\\\"") : "",
            hasTrigger2 ? this.trigger2.iconLeft : "",
            hasTrigger2 ? this.trigger2.amountLeft : 0,
            hasTrigger2 ? this.trigger2.iconRight : "",
            hasTrigger2 ? this.trigger2.amountRight : 0
        );
        
        return json;
    }
    
    public static final String safe(Object o){
        return (o != null) ? o.toString() : "";
    }
    
    public static final String namesToJSONArray() {
        String names = "cardNames = [ \"";

        names += String.join("\",\n\"", map.keySet());
        
        names += "\" ];";
                
        return names;
    }
    
    public static final String allToJSON() {
        String cards = "cards = { ";
        
        for (Card c : map.values()) {
            cards += "\"" + c.name + "\": ";
            cards += c.toJSON();
            cards += ",\n";
        }
        
        cards = cards.substring(0, cards.length() - 2) + " };";
                
        return cards;
    }
    
    @Override public String toString(){return name;}
}