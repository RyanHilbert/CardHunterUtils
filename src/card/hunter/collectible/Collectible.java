package card.hunter.collectible;

import card.hunter.Rarity;
import card.hunter.Set;
import card.hunter.Viewable;
import card.hunter.io.*;
import java.io.Serializable;
import javafx.scene.Node;
import javafx.scene.image.*;

/**
 * Abstract base class encapsulating common functionality of the {@link Card} and {@link Equipment} classes. All fields
 * are guaranteed to be immutable and non-null.
 */
public abstract class Collectible implements Comparable<Collectible>,Cloneable,Serializable,Viewable{

	/** An integer ID used to identify this Collectible within its class. All standard Cards and Equipment are
	 * guaranteed to have a positive ID value, unique within their class. Custom Collectible IDs have negative values,
	 * but are not guaranteed to be unique. A special nameless Collectible may exist for each subclass with an empty
	 * name and an ID value of zero. */
	public final int id;

	/** The name of this Collectible, unique within its class. */
	public final String name;

	/** A shorter name for this Collectible, useful for display purposes; may be the same as {@link #name}. */
	public final String shortName;

	/** The rarity of this Collectible; defaults to {@link Rarity#Epic}. */
	public final Rarity rarity;

	/** The set of this Collectible; defaults to {@link Set#Base}. */
	public final Set set;

	/** The level of this Collectible; for Cards this is an obscure value used for determining loot tables. */
	public final byte level;

	/** An illustration of this Collectible. Since the Image class is not Serializable, this field must be handled
	 * specially by subclasses during deserialization. */
	protected transient Image illustration=Assets.NULL;

	/** Special constructor used for creating 'blank' Collectibles. Only one of these should exist for each subclass. */
	Collectible(){
		this.shortName=this.name="";
		this.rarity=Rarity.Epic;
		this.set=Set.Base;
		this.id=this.level=0;
	}
	Collectible(String name){
		this.id=Integer.MIN_VALUE|name.hashCode();//custom IDs must be negative
		this.name=shortName=name;
		this.rarity=Rarity.Epic;
		this.set=Set.Base;
		this.level=0;
	}
	Collectible(int id,String name,String shortName,Rarity rarity,Set set,byte level,Image illustration){
		this.id=Integer.MIN_VALUE|id;//custom IDs must be negative
		this.name=name;
		this.shortName=shortName;
		this.rarity=rarity;
		this.set=set;
		this.level=level;
		this.illustration=illustration;
	}
	/** Constructs a standard Collectible from a row of data in one of the game's .csv files. */
	Collectible(Data.Table.Row row){
		name=row.get(getClass().getSimpleName()+" Name");
		final String string=row.getString("Short Name");
		shortName=string.isEmpty()?name:string;

		id=row.getInt("Id");
		level=row.getByte("Level");
		rarity=row.getEnum(Rarity.Legendary);
		set=row.getEnum(Set.Base);
	}
	/** @return this Collectible, since all Collectibles are immutable */
	@Override public Collectible clone(){
		return this;
	}
	/** @return true if the passed Object is a Collectible of the same class with the same name; false otherwise */
	@Override public boolean equals(Object o){
		return (o==null||o.getClass()!=getClass())?false:((Collectible)o).name.equals(name);
	}
	/** @return the {@link #id} of this Collectible */
	@Override public int hashCode(){
		return id;
	}
	/** @return the {@link #name} of this Collectible */
	@Override public String toString(){
		return name;
	}
	/** @return the comparison of the associated {@link #id}s, as specified by {@link Integer#compare} */
	@Override public int compareTo(Collectible c){
		return Integer.compare(id,c.id);
	}
	/** @return a small ImageView of the {@link #illustration}; fit for use by the {@link Equipment} class */
	@Override public Node getView(){
		ImageView view=new ImageView(illustration);
		view.setFitWidth(60);
		view.setFitHeight(60);
		return view;
	}
	/** @return true if this is a user-created Collectible; false otherwise */
	public boolean isCustom(){
		return id<0;
	}
	public int getId(){
		return id;
	}
	public byte getLevel(){
		return level;
	}
	public String getName(){
		return name;
	}
	public String getShortName(){
		return shortName;
	}
	public Rarity getRarity(){
		return rarity;
	}
	public Set getSet(){
		return set;
	}
	public Image getIllustration(){
		return illustration;
	}
}
