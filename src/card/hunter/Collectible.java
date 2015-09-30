package card.hunter;

import card.hunter.io.Data;
import java.io.Serializable;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class Collectible<C extends Collectible<C>>implements Comparable<C>,Cloneable,Serializable,Viewable{
	
	public final int id;
	public final String name,shortName;
	public final Rarity rarity;
	public final Set set;
	public final byte level;
	protected transient Image illustration;//not serializable, subclasses are responsible for initialization
	
	public Collectible(){
		id=level=0;
		name=shortName="";
		rarity=Rarity.Base;
		set=Set.Base;
	}
	public Collectible(String name){
		this.name=shortName=name;
		id=name.hashCode();
		rarity=Rarity.Base;
		set=Set.Base;
		level=0;
	}
	public Collectible(int id,String name,String shortName,Rarity rarity,Set set,byte level,Image illustration){
		this.id=id;
		this.name=name;
		this.shortName=shortName;
		this.rarity=rarity;
		this.set=set;
		this.level=level;
		this.illustration=illustration;
	}
	public Collectible(Data.Table.Row row){
		name=row.get(getClass().getSimpleName()+" Name");
		final String string=row.getString("Short Name");
		shortName=string.isEmpty()?name:string;
		
		id=row.getInt("Id");
		level=row.getByte("Level");
		rarity=row.getEnum(Rarity.class);
		set=row.getEnum(Set.class);
	}
	@Override public C clone(){
		return(C)this;
	}
	@Override public boolean equals(Object o){
		return(o==null||o.getClass()!=getClass())?false:((Collectible)o).name.equals(name);
	}
	@Override public int hashCode(){
		return id;
	}
	@Override public String toString(){
		return name;
	}
	@Override public int compareTo(Collectible c){
		return id-c.id;
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
