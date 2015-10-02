package card.hunter.collectible;

import card.hunter.Rarity;
import card.hunter.Set;
import card.hunter.Slot;
import card.hunter.Token;
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

/**
 * Represents an item in Card Hunter. Inherits guarantees about immutability and non-nulls from {@link Collectible}.
 */
public final class Equipment extends Collectible{

	/** An unmodifiable {@link List} of the cards on this Equipment; empty in the case of {@link Slot#Treasure}. */
	public final List<Card> cards;

	/** The greater {@link Token} of this Equipment. */
	public final Token token1;

	/** The lesser {@link Token} of this Equipment. */
	public final Token token2;

	/** The {@link Slot} this Equipment belongs to. */
	public final Slot slot;

	private static final List<Equipment> cache=new ArrayList<>();
	private static final Map<Integer,Equipment> idCache=new HashMap<>();
	private static final Map<String,Equipment> nameCache=new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

	private static final List<Equipment> list=Collections.unmodifiableList(cache);
	private static final Map<Integer,Equipment> idMap=Collections.unmodifiableMap(idCache);
	private static final Map<String,Equipment> nameMap=Collections.unmodifiableMap(nameCache);

	/** @return an unmodifiable cached {@link List} of every item in the game */
	public static List<Equipment> list(){
		if(!cache.isEmpty())return list;
		synchronized(cache){
			if(!cache.isEmpty())return list;
			final Map<String,Card> map=Card.nameMap();
			for(Data.Table.Row row:Data.Equipment.load())cache.add(new Equipment(row,map));
			return list;
		}
	}
	/** @return an unmodifiable cached {@link Map}ping of every item in the game to its {@link #id} */
	public static Map<Integer,Equipment> idMap(){
		if(!idCache.isEmpty())return idMap;
		synchronized(idCache){
			if(!idCache.isEmpty())return idMap;
			for(Equipment equipment:list())idCache.put(equipment.id,equipment);
			return idMap;
		}
	}
	/** @return an unmodifiable cached {@link Map}ping of every item in the game to its {@link #name} */
	public static Map<String,Equipment> nameMap(){
		if(!nameCache.isEmpty())return nameMap;
		synchronized(nameCache){
			if(!nameCache.isEmpty())return nameMap;
			for(Equipment equipment:list())nameCache.put(equipment.name,equipment);
			return nameMap;
		}
	}
	public Equipment(int id,String name,String shortName,Rarity rarity,Set set,byte level,Image icon,Slot slot,Token token1,Token token2,Card... cards){
		super(id,name,shortName,rarity,set,level,icon);
		this.slot=slot;
		this.token1=token1;
		this.token2=token2;
		this.cards=Collections.unmodifiableList(Arrays.asList(cards));
	}
	Equipment(Data.Table.Row row,Map<String,Card> map){
		super(row);
		slot=row.getEnum(Slot.Treasure);
		token1=row.getEnum(Token.None,"Talent 1");
		token2=row.getEnum(Token.None,"Talent 2");

		String string=row.getString("Image");
		illustration=string.contains("Default Item ")?Assets.NULL:Assets.item_illustrations.load(string);

		ArrayList<Card> list=new ArrayList<>(6);
		for(int i=1;!(string=row.getString("Card "+i)).isEmpty();++i)list.add(map.get(string));
		cards=Collections.unmodifiableList(list);
	}
	public List<Card> getCards(){
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
		for(Card card:cards)result.getChildren().add(card.getView());
		return result;
	}
	public Node getTokenView(){
		return token1.pair(token2).getView();
	}
	private void readObject(java.io.ObjectInputStream in) throws java.io.IOException,ClassNotFoundException{
		in.defaultReadObject();
		illustration=Assets.item_illustrations.load(name);
	}
}
