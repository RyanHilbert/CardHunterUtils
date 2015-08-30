package card.hunter;

import card.hunter.assets.Assets;
import card.hunter.assets.Data;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

public class Card implements Viewable{
    
    public final String name,shortName,text,flavorText,triggerText1,triggerText2;
    public final Type type1,type2;
    public final Attack attackType;
    public final Damage damageType;
    public final int id,damage,range,movement;
    public final Quality quality;
    public final Rarity rarity;
    public final Set set;
    public final Image illustration;
    public final Image thumbnail;
	
	public Card(final Data.Table.Row row){
		name=row.get("Card Name");
		final String string=row.get("Short Name");
		shortName=string.isEmpty()?name:string;
		
		text=row.get("Text");
		flavorText=row.get("Flavor Text");
		triggerText1=row.get("Trigger Text");
		triggerText2=row.get("Trigger Text 2");
		
		id=row.getInt("ID");
		damage=row.getInt("Damage");
		range=row.getInt("Range");
		movement=row.getInt("Move Points");
		
		final String[]types=row.get("Types").split(",");
		type1=Type.valueOf(types[0]);
		type2=types.length>1?Type.valueOf(types[1]):type1;
		
		attackType=row.getAttackType();
		damageType=row.getDamageType();
		
		set=row.getSet();
		rarity=row.getRarity();
		quality=row.getQuality();
		
		illustration=Assets.card_illustrations.load(name);
		thumbnail=Assets.card_thumbnails.load(name);
	}
    @Override public String toString(){
		return name;
	}
    @Override public View view(){
		return new View();
	}
	public class View extends Rectangle{//placeholder for card thumbnail class
		private final ContextMenu menu=new ContextMenu(new CustomMenuItem(new LargeView()));
		public View(){
			setWidth(74);
			setHeight(101);
			setFill(Card.this.type1.color);
			setOnMouseClicked(event->menu.show(this,event.getScreenX(),event.getScreenY()));
		}
	}
	public class LargeView extends Rectangle{//placeholder for card view class
		public LargeView(){
			setWidth(248);
			setHeight(347);
			setFill(Card.this.type1.color);
		}
	}
}