package card.hunter;

import card.hunter.assets.Assets;
import card.hunter.assets.Data;
import java.io.Serializable;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

//class used to represent every card in the game
public class Card implements Viewable,Serializable{
	
    public static final int ILLUSTRATION_WIDTH=217,ILLUSTRATION_HEIGHT=129,THUMBNAIL_WIDTH=62,THUMBNAIL_HEIGHT=37;
    
    public final String name,shortName,text,flavorText,triggerText1,triggerText2;
    public final Type type1,type2;
    public final Attack attackType;
    public final Damage damageType;
    public final int damage,range,movement;
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
	public Thumbnail getThumbnail(){
		return new Thumbnail();
	}
	public class View extends Group{
        public static final int WIDTH=234,HEIGHT=333,BORDER=(WIDTH-ILLUSTRATION_WIDTH-1)/2+1;
        public View(){
            ObservableList<Node>children=this.getChildren();
            children.add(new Rectangle(WIDTH,HEIGHT,Card.this.type1.color));
            if(Card.this.type2!=null){
                Rectangle rect=new Rectangle(WIDTH/2,HEIGHT,Card.this.type2.color);
                rect.setX(WIDTH/2);
                children.add(rect);
            }
            int y=BORDER+25;
            
            Rectangle rect=new Rectangle(WIDTH-2*BORDER,ILLUSTRATION_HEIGHT,Card.this.type1.color.brighter());
            rect.setX(BORDER);
            rect.setY(y);
            children.add(rect);
            
            Label titleBar=new Label(Card.this.name);
            titleBar.setFont(Font.font(null,FontWeight.BOLD,16));
            titleBar.setAlignment(Pos.CENTER);
            titleBar.setMinWidth(WIDTH);
            titleBar.setBackground(new Background(new BackgroundFill(Card.this.quality.color(),null,null)));
            children.add(titleBar);
            
            ImageView image=new ImageView(Card.this.illustration);
            image.setX(BORDER);
            image.setY(y);
            children.add(image);
            
            y+=BORDER+ILLUSTRATION_HEIGHT;
            int W=WIDTH-2*BORDER,H=ILLUSTRATION_HEIGHT;
            if(Card.this.attackType!=null||Card.this.damageType!=null){
                Label typeBox=new Label(Card.this.attackType+" "+Card.this.damageType);
                typeBox.setBackground(new Background(new BackgroundFill(Color.BEIGE,null,null)));
                typeBox.setTranslateX(BORDER);
                typeBox.setTranslateY(y);
                typeBox.setMinWidth(W);
                typeBox.setMaxWidth(W);
                children.add(typeBox);
                y+=3*BORDER;
            }
            if(!Card.this.text.isEmpty()||!Card.this.flavorText.isEmpty()){
                Label flavorBox=new Label(Card.this.text+"\n\n"+Card.this.flavorText);
                flavorBox.setBackground(new Background(new BackgroundFill(Color.BEIGE,null,null)));
                flavorBox.setAlignment(Pos.TOP_LEFT);
                flavorBox.setWrapText(true);
                flavorBox.setTranslateX(BORDER);
                flavorBox.setTranslateY(y);
                if(!Card.this.triggerText1.isEmpty())y+=BORDER+(H/=2);
                flavorBox.setMinSize(W,H);
                flavorBox.setMaxSize(W,H);
                children.add(flavorBox);
            }
            if(!Card.this.triggerText1.isEmpty()){
                Label textBox=new Label(Card.this.triggerText1);
                textBox.setBackground(new Background(new BackgroundFill(Color.BEIGE,null,null)));
                textBox.setAlignment(Pos.TOP_LEFT);
                textBox.setWrapText(true);
                textBox.setTranslateX(BORDER);
                textBox.setTranslateY(y);
                textBox.setMinSize(W,H);
                textBox.setMaxSize(W,H);
                children.add(textBox);
            }
        }
    }
    
    //class responsible for displaying the thumbnail version of a Card
    public class Thumbnail extends Group{
        public Thumbnail(){
            setOnMouseClicked(mouse->{
                //if(mouse.getButton()==MouseButton.SECONDARY)Card.this.context.show(this,mouse.getScreenX(),mouse.getScreenY());
            });
            final double height=60,width=height/1.4,border=2;
            ObservableList<Node>children=getChildren();
            Rectangle rect=new Rectangle(width,height,Card.this.type1.color);
            rect.setStroke(Color.BLACK);
            children.add(rect);
            if(Card.this.type2!=null){
                rect=new Rectangle(width/2,height,Card.this.type2.color);
                rect.setX(width/2);
                children.add(rect);
            }
            ImageView view=new ImageView(Card.this.thumbnail);
            view.setPreserveRatio(true);
            view.setFitWidth(width-2*border);
            view.setX(border);
            Bounds bounds=view.getBoundsInLocal();
            
            view.setY(bounds.getHeight()/2);
            rect=new Rectangle(bounds.getWidth(),bounds.getHeight(),Card.this.type1.color);
            rect.setX(view.getX());
            rect.setY(view.getY());
            rect.setStroke(Color.BLACK);
            children.add(rect);
            children.add(view);
            
            Label label=new Label(Card.this.shortName);
            label.setWrapText(true);
            label.setMinWidth(width);
            label.setMaxWidth(width);
            label.setMinHeight(view.getY()-border);
            label.setMaxHeight(view.getY()-border);
            label.setFont(new Font(6));
            label.setBackground(new Background(new BackgroundFill(Card.this.quality.color(),null,null)));
            children.add(label);
        }
    }
}