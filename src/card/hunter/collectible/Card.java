package card.hunter.collectible;

import card.hunter.Action;
import card.hunter.Attack_Type;
import card.hunter.Type;
import card.hunter.Damage_Type;
import card.hunter.Quality;
import card.hunter.Rarity;
import card.hunter.Set;
import card.hunter.Trigger;
import card.hunter.fx.MouseHelper;
import card.hunter.io.*;
import java.util.*;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.*;

/**
 * Represents a card in Card Hunter. Inherits guarantees about immutability and non-nulls from {@link Collectible}.
 */
public final class Card extends Collectible{

	/** The primary (left) {@link Type} of this Card. */
	public final Type type1;

	/** The secondary (right) {@link Type} of this Card; may be the same as {@link #type1}. */
	public final Type type2;

	/** The {@link Attack_Type} of this Card; may be {@link Attack_Type#None}. */
	public final Attack_Type attackType;

	/** The {@link Damage_Type} of this Card; may be {@link DamageType#None}. */
	public final Damage_Type damageType;

	/** The {@link Quality} of this Card. */
	public final Quality quality;
        
        /** The {@link Action} for this Card (if it has one). */
        public final Action action;
        
        /** The first {@link Trigger} for this Card (if it has one). */
        public final Trigger trigger1;
        
        /** The second {@link Trigger} for this Card (if it has one). */
        public final Trigger trigger2;

	/** The amount of damage this Attack Card deals; -1 if this is not an Attack Card. */
	public final byte damage;

	/** The range of this Card; -1 if this Card has no range. */
	public final byte range;

	/** The move points of this Move Card; -1 if this is not a Move Card. */
	public final byte movement;
        
        /** The duration of this Card's attachment effect. */
	public final byte duration;

	/** The roll needed for this Card's first trigger to succeed; -1 if there is no such roll. */
	public final byte roll1;

	/** The roll needed for this Card's second trigger to succeed; -1 if there is no such roll. */
	public final byte roll2;

	/** The effect of playing this Card; empty for trigger-only cards and 'plain' Attack or Move Cards. */
	public final String text;

	/** The flavor text of this Card; empty if this Card has no flavor text. */
	public final String flavorText;

	/** The text for the first trigger of this Card; empty if this Card has no triggers. */
	public final String triggerText1;

	/** The text for the second trigger of this Card; empty if this Card has no second trigger. */
	public final String triggerText2;

        /** Indicates whether Blue Manchu has implemented this Card for play. */
        public final boolean implemented;
        
        /** Indicates whether Blue Manchu is withholding this Card's art (Card has no illustration) */
        public final boolean artHeld;
        
	/** Low resolution image; may be the same as {@link #illustration} for custom Cards. */
	protected transient Image thumbnail;

	private static final List<Card> cache=new ArrayList<>();
	private static final Map<Integer,Card> idCache=new HashMap<>();
	private static final Map<String,Card> nameCache=new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

	private static final List<Card> list=Collections.unmodifiableList(cache);
	private static final Map<Integer,Card> idMap=Collections.unmodifiableMap(idCache);
	private static final Map<String,Card> nameMap=Collections.unmodifiableMap(nameCache);

	/** @return an unmodifiable cached {@link List} of every Card in the game */
	public static List<Card> list(){
		if(!cache.isEmpty())return list;
		synchronized(cache){
			if(!cache.isEmpty())return list;
			for(Data.Table.Row row:Data.Cards.load())cache.add(new Card(row));
			return list;
		}
	}
	/** @return an unmodifiable cached {@link Map}ping of every Card in the game to its {@link #id}. */
	public static Map<Integer,Card> idMap(){
		if(!idCache.isEmpty())return idMap;
		synchronized(idCache){
			if(!idCache.isEmpty())return idMap;
			for(Card card:list())idCache.put(card.id,card);
			return idMap;
		}
	}
	/** @return an unmodifiable cached {@link Map}ping of every Card in the game to its {@link #name}. */
	public static Map<String,Card> nameMap(){
		if(!nameCache.isEmpty())return nameMap;
		synchronized(nameCache){
			if(!nameCache.isEmpty())return nameMap;
			for(Card card:list())nameCache.put(card.name,card);
			return nameMap;
		}
	}
	public Card(int id,String name,String shortName,Rarity rarity,Set set,byte level,Image illustration,
			Action action,Trigger trigger1,Trigger trigger2,boolean implemented,boolean artHeld,byte duration,
			Type type1,Type type2,Attack_Type attackType,Damage_Type damageType,byte damage,byte roll1,
			byte roll2,byte range,byte move,String text,String flavor,String triggerText1,String triggerText2,Quality quality){
		super(id,name,shortName,rarity,set,level,illustration);
		this.type1=type1;
		this.type2=type2;
		this.attackType=attackType;
		this.damageType=damageType;
		this.damage=damage;
		this.range=range;
		this.movement=move;
		this.duration=duration;
		this.roll1=roll1;
		this.roll2=roll2;
		this.text=text;
		this.flavorText=flavor;
		this.triggerText1=triggerText1;
		this.triggerText2=triggerText2;
		this.action=action;
		this.trigger1 = trigger1;
		this.trigger2 = trigger2;
		this.quality=quality;
		this.implemented=implemented;
		this.artHeld=artHeld;
//		if(!this.artHeld)
//			this.art=AssetLoader.loadImage(AssetLoader.ImageType.Card_Illustrations,name);
//		else
//			this.art=null;

		this.thumbnail=illustration;
                
	}
	Card(Data.Table.Row row){
		super(row);
		text=row.getString("Text");
		flavorText=row.getString("Flavor Text");
		triggerText1=row.getString("Trigger Text");
		triggerText2=row.getString("Trigger Text 2");

		damage=row.getByte("Damage");
		range=row.getByte("Range");
		movement=row.getByte("Move Points");
                duration=row.getByte("Duration");
		roll1=row.getByte("Trigger");
		roll2=row.getByte("Trigger 2");

		final String[] types=row.getString("Types").split(",");
		type1=Type.valueOf(types[0]);
		type2=types.length>1?Type.valueOf(types[1]):type1;

		attackType=row.getEnum(Attack_Type.None);
		damageType=row.getEnum(Damage_Type.None);
                
                action=new Action(type1, text, flavorText, damage, range, movement);
                String triggerEffect1 = row.getString("Trigger Effect");
                trigger1=(triggerEffect1.trim().length() > 0) ? 
                        new Trigger(row.getIntOr("Trigger", 0), row.getIntOr("Keep", 0), Trigger.Effect.valueOf(triggerEffect1), triggerText1) : null;
                String triggerEffect2 = row.getString("Trigger Effect 2");
                trigger2=(triggerEffect2.trim().length()>0) ? 
                        new Trigger(row.getIntOr("Trigger 2", 0),row.getIntOr("Keep 2", 0),Trigger.Effect.valueOf(triggerEffect2),triggerText2) : null;
		quality=Quality.valueOf(row.getString("Quality"),row.getString("Plus Minus"));

                implemented="Implemented".equalsIgnoreCase(row.getString("Status"));
                artHeld="HOLD".equalsIgnoreCase(row.getString("Art")) ||
                        // HACK HACK HACK
                        "BRONZE PUNCH".equalsIgnoreCase(name);
		if(artHeld)illustration=thumbnail=Assets.NULL;
		else{
			illustration=Assets.card_illustrations.load(name);
			thumbnail=Assets.card_thumbnails.load(name);
		}
	}
	public boolean isPlayable(){
		return !text.isEmpty()||type1==Type.Assist||type1==Type.Attack||type1==Type.Move||type1==Type.Utility
				||type2==Type.Assist||type2==Type.Attack||type2==Type.Move||type2==Type.Utility;
	}
	public boolean isTriggerable(){
		return !triggerText1.isEmpty();
	}
	public Type getType1(){
		return type1;
	}
	public Type getType2(){
		return type2;
	}
	public Attack_Type getAttackType(){
		return attackType;
	}
	public Damage_Type getDamageType(){
		return damageType;
	}
	public byte getDamage(){
		return damage;
	}
	public byte getRange(){
		return range;
	}
	public byte getMovement(){
		return movement;
	}
        public byte getDuration() {
            return duration;
        }
	public byte getRoll(){
		return roll1>0?roll1:roll2;
	}
	public String getText(){
		return text;
	}
	public String getFlavorText(){
		return flavorText;
	}
	public String getTriggerText1(){
		return triggerText1;
	}
	public String getTriggerText2(){
		return triggerText2;
	}
        public Action getAction() {
            return action;
        }
        public Trigger getTrigger1() {
            return trigger1;
        }
        public Trigger getTrigger2() {
            return trigger2;
        }
	public Quality getQuality(){
		return quality;
	}
        public boolean getImplemented() {
            return implemented;
        }
        public boolean getArtHeld() {
            return artHeld;
        }
	public Image getThumbnail(){
		return thumbnail;
	}
	@Override public Node getView(){
		return new View();
	}
	public class FullView extends ImageView{//placeholder for actual fullsize view class

		public FullView(){
			super(illustration);
			setFitWidth(217);
			setFitHeight(129);
		}
	}
	public class View extends Group{

		ContextMenu menu=new ContextMenu(new CustomMenuItem(new FullView()));
		public View(){
			ObservableList<Node> children=getChildren();
			Rectangle primary=new Rectangle(74,101,type1.color);
			primary.setStroke(Color.BLACK);
			primary.setStrokeType(StrokeType.INSIDE);
			primary.setStrokeWidth(2);
			children.add(primary);
			if(type2!=type1){
				Rectangle secondary=new Rectangle(36,18,36,81);
				secondary.setFill(type2.color);
				children.add(secondary);
			}
			Label title=new Label(shortName);
			title.setAlignment(Pos.CENTER);
			title.setFont(Font.font("Times New Roman",FontWeight.BOLD,11));
			title.setMinSize(74,18);
			title.setMaxSize(74,18);
			title.setBackground(new Background(new BackgroundFill(quality.color(),CornerRadii.EMPTY,Insets.EMPTY)));
			children.add(title);

			Rectangle background=new Rectangle(5,21,64,39);
			background.setFill(type1.color);
			background.setStroke(Color.BLACK);
			background.setStrokeWidth(2);
			children.add(background);

			ImageView view=new ImageView(thumbnail);
			view.setFitWidth(62);
			view.setFitHeight(37);
			view.setX(6);
			view.setY(22);
			children.add(view);
			setOnMouseClicked(event-> {
                                double x = event.getScreenX();
                                double y = event.getScreenY();
                                
//				if(MouseHelper.isRightClick(event))
//                                    Card.this.context.show(this, x, y);
//				else 
					menu.show(this, x, y);
			});

			boolean row2=false;
			final byte x1=4, x2=38, y1=63, y2=81;
			if(isPlayable()){
				view=new ImageView(damage<0?movement<0?Assets.PLAY:Assets.get("Move"+movement):Assets.get("Damage"+damage));
				view.setLayoutX(x1);
				view.setLayoutY(y1);
				children.add(view);
				view=new ImageView(range<0?Assets.PLAY:Assets.get("Range"+range));
				view.setLayoutX(x2);
				view.setLayoutY(y1);
				children.add(view);
				row2=true;
			}
			if(!triggerText1.isEmpty()){
				view=new ImageView(roll1<=0?Assets.TRIGGER:Assets.get("Roll"+roll1));
				view.setLayoutX(x1);
				view.setLayoutY(row2?y2:y1);
				children.add(view);
				final Image image;
				switch(type1){
					case Armor: image=Assets.PLATE;
						break;
					case Block: image=Assets.SHIELD;
						break;
					case Boost: image=Assets.STAR;
						break;
					case Handicap: image=Assets.BOLT;
						break;
					default: switch(type2){
						case Armor: image=Assets.PLATE;
							break;
						case Block: image=Assets.SHIELD;
							break;
						case Handicap: image=Assets.BOLT;
							break;
						default: image=Assets.STAR;
					}
				}
				view=new ImageView(image);
				view.setLayoutX(x2);
				view.setLayoutY(row2?y2:y1);
				children.add(view);
				row2=true;
				if(!triggerText2.isEmpty()){
					view=new ImageView(roll2<=0?Assets.TRIGGER:Assets.get("Roll"+roll2));
					view.setLayoutX(x1);
					view.setLayoutY(y2);
					children.add(view);
					view=new ImageView(Assets.STAR);
					view.setLayoutX(x2);
					view.setLayoutY(y2);
					children.add(view);
				}
			}
			if(!row2){
				view=new ImageView(Assets.PLAY);
				view.setLayoutX(x1);
				view.setLayoutY(y1);
				children.add(view);
				view=new ImageView(Assets.PLAY);
				view.setLayoutX(x2);
				view.setLayoutY(y1);
				children.add(view);
			}
		}
	}
	private void readObject(java.io.ObjectInputStream in) throws java.io.IOException,ClassNotFoundException{
		in.defaultReadObject();
		illustration=Assets.card_illustrations.load(name);
		thumbnail=Assets.card_thumbnails.load(name);
	}

    //passes CSV data into the Card constructor
//    public static Card fromCSV(String string){
//        String[]s=CSV.tokenizeLine(string);
//        String[]types=s[3].split(",");
//        return new Card(toInt(s[0]),s[1],s[2],
//            Type.valueOf(types[0]),
//            types.length<2?null:Type.valueOf(types[1]),
//            s[4].isEmpty()?null:AttackType.valueOf(s[4]),
//            s[5].isEmpty()?null:DamageType.valueOf(s[5]),
//            toByte(s[6]),
//            toByte(s[8]),
//            toByte(s[9]),
//            toByte(s[10]),
//            s[17],s[18],s[20],
//            Quality.fromStrings(s[39],s[40]),
//            s[47].isEmpty()?null:Rarity.valueOf(s[47]),
//            Set.value(toInt(s[53])),
//            ((s[17]+s[18]).trim().length() > 0) ? new Action(Type.valueOf(types[0]), s[17], s[18], toByte(s[6]), toByte(s[8]), toByte(s[9])) : null,
//            (s[13].trim().length() > 0) ? new Trigger(toInt(s[11]), toInt(s[12]), Trigger.Effect.valueOf(s[13]), s[20]) : null,
//            (s[16].trim().length()>0) ? new Trigger(toInt(s[14]),toInt(s[15]),Trigger.Effect.valueOf(s[16]),s[24]) : null,
//            !s[50].isEmpty(),
//            (s[56].toLowerCase().equals("hold"))
//        );
//    }
    
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
            //"triggerText: \"%s\",\n" +
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
            //safe(this.triggerText).replace("\"", "\\\""),
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

        names += String.join("\",\n\"", nameCache.keySet());
        
        names += "\" ];";
                
        return names;
    }
    
    public static final String allToJSON() {
        String cards = "cards = { ";
        
        for (Card c : nameCache.values()) {
            cards += "\"" + c.name + "\": ";
            cards += c.toJSON();
            cards += ",\n";
        }
        
        cards = cards.substring(0, cards.length() - 2) + " };";
                
        return cards;
    }
    
    @Override public String toString(){return name;}
}
