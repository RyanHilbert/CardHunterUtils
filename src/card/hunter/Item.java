package card.hunter;

import card.hunter.assets.Assets;
import java.io.Serializable;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

//class used to represent every item in the game, from treasures to weapons
public class Item implements Viewable,Serializable{
	
    public final String name;
    public final Rarity rarity;
    public final byte level;
    public final Token token1,token2;
    public final Card card1,card2,card3,card4,card5,card6;
    public final Slot slot;
    public final Set set;
    public final Image icon;
    
    public String getName(){return name;}
    public Rarity getRarity(){return rarity;}
    public byte getLevel(){return level;}
    public Token getPrimaryToken(){return token1;}
    public Token getSecondaryToken(){return token2;}
    public Slot getSlot(){return slot;}
    public Set getSet(){return set;}
    public Image getIcon(){return icon;}
    public ImageView getView(){ImageView view=new ImageView(icon);view.setOnMouseClicked(e->System.out.println("Test"));return view;}
    public Token.View getPrimaryTokenView(){return token1.view();}
    public Token.View getSecondaryTokenView(){return token2.view();}
    public Token.Pair.View getTokenView(){return token1.pair(token2).view();}
    public HBox getCardView(){
        HBox hbox=new HBox();
        ObservableList<Node>children=hbox.getChildren();
        if(card1!=null)children.add(card1.getThumbnail());
        if(card2!=null)children.add(card2.getThumbnail());
        if(card3!=null)children.add(card3.getThumbnail());
        if(card4!=null)children.add(card4.getThumbnail());
        if(card5!=null)children.add(card5.getThumbnail());
        if(card6!=null)children.add(card6.getThumbnail());
        return hbox;
    }
    private Item(String name,Rarity rarity,byte level,Token token1,Token token2,Card card1,Card card2,Card card3,Card card4,Card card5,Card card6,Slot slot,String image,Set set){
        this.name=name;
        this.rarity=rarity;
        this.level=level;
        this.token1=token1;
        this.token2=token2;
        this.card1=card1;
        this.card2=card2;
        this.card3=card3;
        this.card4=card4;
        this.card5=card5;
        this.card6=card6;
        this.slot=slot;
        this.set=set;
        this.icon=Assets.item_illustrations.load(image);
        if(image.contains("Default Item"))slot.dfault=this;
        list.add(this);
    }
    
    public static ArrayList<Item> byName(String... names){
        ArrayList<Item> retVal = new ArrayList<>(names.length);
        
        for(String n : names)
            retVal.add(Item.byName(n));

        return retVal;
    }

    //public static Item EmptyItem=new Item("_",Rarity.Common,(byte) 0,Token.None,Token.None,null,null,null,null,null,null,Slot.Boots,"",Set.Base);

    @Override public String toString(){return name;}
}