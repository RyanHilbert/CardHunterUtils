package card.hunter.collectible;

import card.hunter.Attack_Type;
import card.hunter.Type;
import card.hunter.Damage_Type;
import card.hunter.Quality;
import card.hunter.Rarity;
import card.hunter.Set;
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

	/** The amount of damage this Attack Card deals; -1 if this is not an Attack Card. */
	public final byte damage;

	/** The range of this Card; -1 if this Card has no range. */
	public final byte range;

	/** The move points of this Move Card; -1 if this is not a Move Card. */
	public final byte movement;

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
			Type type1,Type type2,Attack_Type attackType,Damage_Type damageType,byte damage,byte roll1,
			byte roll2,byte range,byte move,String text,String flavor,String trigger1,String trigger2,Quality quality){
		super(id,name,shortName,rarity,set,level,illustration);
		this.type1=type1;
		this.type2=type2;
		this.attackType=attackType;
		this.damageType=damageType;
		this.damage=damage;
		this.range=range;
		this.movement=move;
		this.roll1=roll1;
		this.roll2=roll2;
		this.text=text;
		this.flavorText=flavor;
		this.triggerText1=trigger1;
		this.triggerText2=trigger2;
		this.quality=quality;
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
		roll1=row.getByte("Trigger");
		roll2=row.getByte("Trigger 2");

		final String[] types=row.getString("Types").split(",");
		type1=Type.valueOf(types[0]);
		type2=types.length>1?Type.valueOf(types[1]):type1;

		attackType=row.getEnum(Attack_Type.None);
		damageType=row.getEnum(Damage_Type.None);
		quality=Quality.valueOf(row.getString("Quality"),row.getString("Plus Minus"));

		if("HOLD".equalsIgnoreCase(row.getString("Art")))illustration=thumbnail=Assets.NULL;
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
	public Quality getQuality(){
		return quality;
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
			setOnMouseClicked(event->menu.show(this,event.getScreenX(),event.getScreenY()));

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
}
