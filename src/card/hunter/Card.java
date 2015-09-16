package card.hunter;

import card.hunter.io.Assets;
import card.hunter.io.Data;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public final class Card extends Collectible<Card>{
    
    public final Card_Type type1,type2;
	public final Attack_Type attackType;
    public final Damage_Type damageType;
	public final byte damage,range,movement,roll;
	public final String text,flavorText,triggerText1,triggerText2;
    public final Quality quality;
    private Image thumbnail;
	
	private static final List<Card>cache=new ArrayList<>();
	private static final Map<Integer,Card>idCache=new HashMap<>();
	private static final Map<String,Card>nameCache=new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	
	public static final List<Card>list=Collections.unmodifiableList(cache);
	public static final Map<Integer,Card>idMap=Collections.unmodifiableMap(idCache);
	public static final Map<String,Card>nameMap=Collections.unmodifiableMap(nameCache);
	
	public static List<Card>list(){
		if(!cache.isEmpty())return list;
		synchronized(cache){
			if(!cache.isEmpty())return list;
			for(Data.Table.Row row:Data.Cards.load())cache.add(new Card(row));
			return list;
		}
	}
	public static Map<Integer,Card>idMap(){
		if(!idCache.isEmpty())return idMap;
		synchronized(idCache){
			if(!idCache.isEmpty())return idMap;
			for(Card card:list())idCache.put(card.id,card);
			return idMap;
		}
	}
	public static Map<String,Card>nameMap(){
		if(!nameCache.isEmpty())return nameMap;
		synchronized(nameCache){
			if(!nameCache.isEmpty())return nameMap;
			for(Card card:list())nameCache.put(card.name,card);
			return nameMap;
		}
	}
	public Card(int id,String name,String shortName,Rarity rarity,Set set,byte level,Image illustration,
				Card_Type type1,Card_Type type2,Attack_Type attackType,Damage_Type damageType,byte damage,byte roll,
				byte range,byte move,String text,String flavor,String trigger1,String trigger2,Quality quality){
		super(id,name,shortName,rarity,set,level,illustration);
		this.type1=type1;
		this.type2=type2;
		this.attackType=attackType;
		this.damageType=damageType;
		this.damage=damage;
		this.range=range;
		this.movement=move;
		this.roll=roll;
		this.text=text;
		this.flavorText=flavor;
		this.triggerText1=trigger1;
		this.triggerText2=trigger2;
		this.quality=quality;
		this.thumbnail=illustration;
	}
	public Card(final Data.Table.Row row){
		super(row);
		text=row.getString("Text");
		flavorText=row.getString("Flavor Text");
		triggerText1=row.getString("Trigger Text");
		triggerText2=row.getString("Trigger Text 2");
		
		damage=row.getByte("Damage");
		range=row.getByte("Range");
		movement=row.getByte("Move Points");
		roll=row.getByte("Trigger");
		
		final String[]types=row.getString("Types").split(",");
		type1=Card_Type.valueOf(types[0]);
		type2=types.length>1?Card_Type.valueOf(types[1]):type1;
		
		attackType=row.getEnum(Attack_Type.class);
		damageType=row.getEnum(Damage_Type.class);
		quality=Quality.valueOf(row.getString("Quality"),row.getString("Plus Minus"));
		
		if("HOLD".equalsIgnoreCase(row.getString("Art")))illustration=thumbnail=null;
		else{
			illustration=Assets.card_illustrations.load(name);
			thumbnail=Assets.card_thumbnails.load(name);
		}
	}
	public Card_Type getType1(){
		return type1;
	}
	public Card_Type getType2(){
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
		return roll;
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
	@Override public View getView() {
		return new View();
	}
	public class View extends ImageView{//placeholder for actual thumbnail view class
		private final ContextMenu menu=new ContextMenu(new CustomMenuItem(new FullView()));
		public View(){
			super(thumbnail);
			setFitWidth(62);
			setFitHeight(37);
			setOnMouseClicked(event->menu.show(this,event.getScreenX(),event.getScreenY()));
		}
	}
	public class FullView extends ImageView{//placeholder for actual fullsize view class
		public FullView(){
			super(illustration);
			setFitWidth(217);
			setFitHeight(129);
		}
	}
	private void readObject(java.io.ObjectInputStream in)throws java.io.IOException,ClassNotFoundException{
		in.defaultReadObject();
		illustration=Assets.card_illustrations.load(name);
		thumbnail=Assets.card_thumbnails.load(name);
	}
}