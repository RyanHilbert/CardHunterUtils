package card.hunter;

import card.hunter.assets.Assets;
import card.hunter.assets.Data;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class Item implements Viewable{
	
	public final List<Card>cards;
    public final String name,shortName;
    public final Rarity rarity;
    public final int level,id;
    public final Token token1,token2;
    public final Slot slot;
    public final Set set;
    public final Image icon;
    
    public String getName(){
		return name;
	}
    public Rarity getRarity(){
		return rarity;
	}
    public int getLevel(){
		return level;
	}
    public Token getPrimaryToken(){
		return token1;
	}
    public Token getSecondaryToken(){
		return token2;
	}
    public Slot getSlot(){
		return slot;
	}
    public Set getSet(){
		return set;
	}
    public Token.Pair.View getTokenView(){
		return token1.pair(token2).view();
	}
    public HBox getCardView(){
        HBox hbox=new HBox();
        ObservableList<Node>children=hbox.getChildren();
        for(Card card:cards)children.add(card.view());
        return hbox;
    }
    private Item(String name,Rarity rarity,int level,Token token1,Token token2,Slot slot,String image,Set set,Card...cards){
		this.id=0;
        this.name=this.shortName=name;
        this.rarity=rarity;
        this.level=level;
        this.token1=token1;
        this.token2=token2;
        this.slot=slot;
        this.set=set;
        this.icon=Assets.item_illustrations.load(image);
		this.cards=Collections.unmodifiableList(Arrays.asList(cards));
    }
    public Item(Data.Table.Row row,Map<String,Card>map){
		name=row.get("Equipment Name");
		String string=row.get("Short Name");
		shortName=string.isEmpty()?name:string;
		
		id=row.getInt("ID");
		level=row.getInt("Level");
		icon=Assets.item_illustrations.load(row.get("Image"));
		
		rarity=row.getRarity();
		slot=row.getSlot();
		set=row.getSet();
		token1=row.getToken1();
		token2=row.getToken2();
		
		ArrayList<Card>list=new ArrayList<>(6);
		for(int i=1;!(string=row.get("Card "+i)).isEmpty();++i)list.add(map.get(string));
		cards=Collections.unmodifiableList(list);
	}
	public Item(Data.Table.Row row,Collection<Card>collection){
		this(row,collectionToMap(collection));
	}
	public Item(Data.Table.Row row,Card...array){
		this(row,Arrays.asList(array));
	}
    @Override public String toString(){
		return name;
	}
	@Override public ImageView view(){
		return new ImageView(icon);
	}
}