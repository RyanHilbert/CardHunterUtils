package card.hunter.assets;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.WeakHashMap;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;

public enum Assets{

	board_decal_doodads("png"),
	board_tiles("png"),
	card_illustrations("png"),
	card_thumbnails("png"),
	figure_illustrations("png"),
	item_illustrations("png"),
	large_portraits("png"),
	league_images("jpg"),
	lower_doodads("png"),
	module_cover("jpg"),
	news_images("png"),
	small_portraits("png"),
	starter_packs("png"),
	upper_doodads("png");
	
	private final WeakHashMap<String,Image>cache=new WeakHashMap<>();
	
    public final File directory;
    public final String extension;
	
    Assets(final String ext){
        extension=ext;
        directory=new File("assets",toString());
    }
	public Image load(String name){
		final Image cached=cache.get(name);
		if(cached!=null)return cached;
		final File file=new File(directory,name+'.'+extension);
        if(file.isFile()){
			final Image result=new Image(file.toURI().toString());
			cache.put(name,result);
			return result;
		}
		directory.mkdirs();
        final String path="/assets/"+directory.getName()+'/'+file.getName();
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
                        ImageIO.write(SwingFXUtils.fromFXImage(img,null),extension,file);
                    }catch(IOException e){
						throw new UncheckedIOException("Failed to save image: "+directory.getName()+file.getName(),e);
					}
                    observable.removeListener(this);
                }
            }
        });
		cache.put(name,img);
        return img;
	}
}