import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
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
import javax.imageio.ImageIO;

//class used to represent every card in the game
public class Card{
    //map links every card's name to its object identity
    public final static ObservableMap<String,Card>map=FXCollections.observableMap(new LinkedHashMap());
    
    public enum Type{
        Armor(Color.GRAY),Assist(Color.LIGHTGRAY),Attack(Color.DARKRED),Block(Color.OLIVEDRAB),Boost(Color.CHOCOLATE),Handicap(new Color(.2,.2,.2,1)),Move(Color.STEELBLUE),Utility(Color.MEDIUMPURPLE);
        public final Color color;
        private Type(Color color){this.color=color;}
    }
    public enum AttackType{Magic,Melee,Projectile}
    public enum DamageType{Acid,Arcane,Cold,Crushing,Electrical,Fire,Holy,Laser,Piercing,Poison,Psychic,Radiation,Slashing,Sonic,Unholy}
    public enum Quality{
        Black_,Black,Black$,
        Paper_,Paper,Paper$,
        Bronze_,Bronze,Bronze$,
        Silver_,Silver,Silver$,
        Gold_,Gold,Gold$,
        Emerald_,Emerald,Emerald$,
        Amethyst_,Amethyst,Amethyst$;
        @Override public String toString(){return super.toString().replace('_','-').replace('$','+');}
        private static Quality[]values=values();
        public static Quality value(int i){return values[i];}
        public static Quality fromStrings(String mod,String quality){
            byte val=1+Byte.MIN_VALUE;
            switch(quality){
                case"E":val=-3;break;
                case"D":val=0;break;
                case"C":val=3;break;
                case"B":val=6;break;
                case"A":val=9;break;
                case"AA":val=12;break;
                case"AAA":val=15;break;
            }
            switch(mod){
                case"+":++val;break;
                case"-":--val;break;
            }
            return values[val+4];
        }
        public Color getColor(){
            switch(ordinal()/3){
                case 0:return Color.BLACK;
                case 1:return Color.TAN;
                case 2:return Color.SIENNA;
                case 3:return Color.SILVER;
                case 4:return Color.GOLD;
                case 5:return Color.CHARTREUSE;
                case 6:return Color.VIOLET;
                default:return Color.TRANSPARENT;
            }
        }
    }
    
    //class responsible for displaying the thumbnail version of a Card
    public class Thumbnail extends Group{
        public Thumbnail(){
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
            label.setBackground(new Background(new BackgroundFill(Card.this.quality.getColor(),null,null)));
            children.add(label);
        }
    }
    public final String name,shortName,text,flavorText;
    public final Type type1,type2;
    public final AttackType attackType;
    public final DamageType damageType;
    public final byte damage,range,movement,duration;
    public final Quality quality;
    public final Rarity rarity;
    public final Set set;
    public final Image art;
    public final Image thumbnail;
    public Thumbnail getThumbnail(){return new Thumbnail();}
    
    public Card(String name,String shortName,Type type1,Type type2,AttackType attackType,DamageType damageType,byte damage,byte range,byte movement,byte duration,String text,String flavorText,Quality quality,Rarity rarity,Set set){
        this.name=name;
        this.shortName=shortName.isEmpty()?name:shortName;
        this.type1=type1;
        this.type2=type2;
        this.attackType=attackType;
        this.damageType=damageType;
        this.damage=damage;
        this.range=range;
        this.movement=movement;
        this.duration=duration;
        this.text=text;
        this.flavorText=flavorText;
        this.quality=quality;
        this.rarity=rarity;
        this.set=set;
        Image art;
        final File artFile=new File("assets/card_illustrations",name+".png");
        if(artFile.isFile())art=new Image(artFile.toURI().toString());
        else{
            try{
                art=new Image(new URI("http","live.cardhunter.com","/assets/card_illustrations/"+artFile.getName(),null).toString(),true);
            }catch(URISyntaxException e){//probably shouldn't happen, but provide a fallback anyway
                art=new Image("http://live.cardhunter.com/assets/card_illustrations/"+artFile.getName(),true);
            }
            final Image temp=art;//needed as 'effectively final' variable in anonymous class
            art.progressProperty().addListener(new ChangeListener<Number>(){
                @Override public void changed(ObservableValue<?extends Number>observable,Number oldValue,Number newValue){
                    if(newValue.doubleValue()>=1.0){//when art is finished downloading...
                        try{//attempt to save it to disk
                            ImageIO.write(SwingFXUtils.fromFXImage(temp,null),"png",artFile);
                        }catch(Exception e){}//file just won't get written in this case, which is fine
                        observable.removeListener(this);
                    }
                }
            });
        }
        this.art=art;
        Image thumbnail;
        final File thumbFile=new File("assets/card_thumbnails",name+".png");
        if(thumbFile.isFile())thumbnail=new Image(thumbFile.toURI().toString());
        else{
            try{
                thumbnail=new Image(new URI("http","live.cardhunter.com","/assets/card_thumbnails/"+thumbFile.getName(),null).toString(),true);
            }catch(URISyntaxException e){//probably shouldn't happen, but provide a fallback anyway
                thumbnail=new Image("http://live.cardhunter.com/assets/card_thumbnails/"+thumbFile.getName(),true);
            }
            final Image temp=thumbnail;//needed as 'effectively final' variable in anonymous class
            thumbnail.progressProperty().addListener(new ChangeListener<Number>(){
                @Override public void changed(ObservableValue<?extends Number>observable,Number oldValue,Number newValue){
                    if(newValue.doubleValue()>=1.0){//when thumbnail is finished downloading...
                        try{//attempt to save it to disk
                            ImageIO.write(SwingFXUtils.fromFXImage(temp,null),"png",thumbFile);
                        }catch(Exception e){}//file just won't get written in this case, which is fine
                        observable.removeListener(this);
                    }
                }
            });
        }
        this.thumbnail=thumbnail;
        map.put(name,this);
    }
    //passes CSV data into the Card constructor
    public static Card fromCSV(String string){
        String[]s=CSV.tokenizeLine(string);
        String[]types=s[3].split(",");
        return new Card(s[1],s[2],
                Type.valueOf(types[0]),
                types.length<2?null:Type.valueOf(types[1]),
                s[4].isEmpty()?null:AttackType.valueOf(s[4]),
                s[5].isEmpty()?null:DamageType.valueOf(s[5]),
                Byte.parseByte(0+s[6]),
                Byte.parseByte(0+s[8]),
                Byte.parseByte(0+s[9]),
                Byte.parseByte(0+s[10]),
                s[17],s[18],
                Quality.fromStrings(s[39],s[40]),
                s[47].isEmpty()?null:Rarity.valueOf(s[47]),
                Set.value(Integer.parseInt(0+s[53])));
    }
    @Override public String toString(){return name;}
}