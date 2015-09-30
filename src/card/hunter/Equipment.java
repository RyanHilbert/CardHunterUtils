package card.hunter;

import card.hunter.io.Assets;
import card.hunter.io.Data;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;

public final class Equipment extends Collectible<Equipment>{
	
	private final Card[]array;
	public final List<Card>cards;
    public final Token token1,token2;
    public final Slot slot;
	
	private static final List<Equipment>cache=new ArrayList<>();
	private static final Map<Integer,Equipment>idCache=new HashMap<>();
	private static final Map<String,Equipment>nameCache=new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	
	public static final List<Equipment>list=Collections.unmodifiableList(cache);
	public static final Map<Integer,Equipment>idMap=Collections.unmodifiableMap(idCache);
	public static final Map<String,Equipment>nameMap=Collections.unmodifiableMap(nameCache);
	
	public static List<Equipment>list(){
		if(!cache.isEmpty())return list;
		synchronized(cache){
			if(!cache.isEmpty())return list;
			final Map<String,Card>map=Card.nameMap();
			for(Data.Table.Row row:Data.Equipment.load())cache.add(new Equipment(row,map));
			return list;
		}
	}
	public static Map<Integer,Equipment>idMap(){
		if(!idCache.isEmpty())return idMap;
		synchronized(idCache){
			if(!idCache.isEmpty())return idMap;
			for(Equipment equipment:list())idCache.put(equipment.id,equipment);
			return idMap;
		}
	}
	public static Map<String,Equipment>nameMap(){
		if(!nameCache.isEmpty())return nameMap;
		synchronized(nameCache){
			if(!nameCache.isEmpty())return nameMap;
			for(Equipment equipment:list())nameCache.put(equipment.name,equipment);
			return nameMap;
		}
	}
    public Equipment(int id,String name,String shortName,Rarity rarity,Set set,byte level,Image icon,Slot slot,Token token1,Token token2,Card...cards){
		super(id,name,shortName,rarity,set,level,icon);
		this.slot=slot;
		this.token1=token1;
		this.token2=token2;
		this.cards=Collections.unmodifiableList(Arrays.asList(this.array=cards));
    }
    public Equipment(Data.Table.Row row,Map<String,Card>map){
		super(row);
		slot=row.getEnum(Slot.class);
		token1=row.getEnum(Token.class,"Talent 1");
		token2=row.getEnum(Token.class,"Talent 2");
		
		String string=row.getString("Image");
		illustration=string.contains("Default Item ")?null:Assets.item_illustrations.load(string);
		
		ArrayList<Card>list=new ArrayList<>(6);
		for(int i=1;!(string=row.getString("Card "+i)).isEmpty();++i)list.add(map.get(string));
		cards=Collections.unmodifiableList(Arrays.asList(this.array=list.toArray(new Card[0])));
	}
	public List<Card>getCards(){
		return cards;
	}
	public Token getToken1(){
		return token1;
	}
	public Token getToken2(){
		return token2;
	}
	public Slot getSlot(){
		return slot;
	}
	public Node getCardView(){
		HBox result=new HBox();
		result.setSpacing(2);
		for(Card card:array)result.getChildren().add(card.getView());
		return result;
	}
	public Node getTokenView(){
		return token1.pair(token2).getView();
	}
	private void readObject(java.io.ObjectInputStream in)throws java.io.IOException,ClassNotFoundException{
		in.defaultReadObject();
		illustration=Assets.item_illustrations.load(name);
	}
}