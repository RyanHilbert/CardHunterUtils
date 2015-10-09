package utils;

import card.hunter.collectible.Card;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javax.imageio.ImageIO;

public final class AssetLoader{
    
    public static final File assetDirectory=new File("assets");
    public static final File audioDirectory=new File(assetDirectory,"audio");
    public static final File cardDir=new File("data/gameplay/Cards");
    public static final File itemDir=new File("data/gameplay/Equipment");
        
    static {
        //create all necessary directories...        
        audioDirectory.mkdirs();
        cardDir.mkdirs();
        itemDir.mkdirs();
    }

    public static void setupJSON() {
        File jsonFile=new File(cardDir,"cards.js");
        if (jsonFile.exists())
            jsonFile.delete();
        
        try {
            jsonFile.createNewFile();
            PrintWriter writer = new PrintWriter(jsonFile.getPath(), "UTF-8");

            writer.print(Card.namesToJSONArray());
            writer.println();
            writer.println();
            writer.print(Card.allToJSON());
            
            writer.close();
        } catch (IOException ex) {
            System.out.println("Problem trying to create card json:" + ex.getMessage());
        }
    }
    
    public static enum ImageType{
        Board_Decal_Doodads("png"),
        Board_Tiles("png"),
        Card_Illustrations("png"),
        Card_Thumbnails("png"),
        Figure_Illustrations("png"),
        Item_Illustrations("png"),
        Large_Portraits("png"),
        League_Images("jpg"),
        Lower_Doodads("png"),
        Module_Cover("jpg"),
        News_Images("png"),
        Small_Portraits("png"),
        Starter_Packs("png"),
        Upper_Doodads("png");
        
        public final File directory;
        public final String extension;
        
        ImageType(final String ext){
            extension=ext;
            directory=new File(assetDirectory,toString().toLowerCase());
            directory.mkdir();
        }
    }
    public static Image loadImage(final ImageType type,final String name){
        final File file=new File(type.directory,name+'.'+type.extension);
        if(file.isFile())return new Image(file.toURI().toString());
        final String path='/'+assetDirectory.getName()+'/'+type.directory.getName()+'/'+file.getName();
        Image image;
        try{
            image=new Image(new URI("http","live.cardhunter.com",path,null).toString(),true);
        }catch(URISyntaxException e){//probably shouldn't happen, but provide a fallback anyway
            image=new Image("http://live.cardhunter.com"+path,true);
        }
        final Image img=image;
        image.progressProperty().addListener(new ChangeListener<Number>(){
            @Override public void changed(ObservableValue<?extends Number>observable,Number oldValue,Number newValue){
                if(newValue.doubleValue()>=1.0){//when image is finished downloading...
                    try{//attempt to save it to disk
                        ImageIO.write(SwingFXUtils.fromFXImage(img,null),type.extension,file);
                    }catch(Exception e){}//file just won't get written in this case, which is fine
                    observable.removeListener(this);
                }
            }
        });
        return img;
    }
    public static AudioClip loadAudio(final String name){
        File file=new File(audioDirectory,name+".mp3");
        if(file.isFile())return new AudioClip(file.toURI().toString());
        final String path='/'+assetDirectory.getName()+'/'+audioDirectory.getName()+'/'+file.getName();
        try(InputStream in=new URL("http://live.cardhunter.com"+path).openStream()){
            Files.copy(in,file.toPath());
            return new AudioClip(file.toURI().toString());
        }catch(Exception e){return null;}
    }
}
