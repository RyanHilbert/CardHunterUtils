package card.hunter.io;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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
	
	private static final Map<String,Image>jarAssets=new HashMap<>();
	public static final Map<String,Image>local=Collections.unmodifiableMap(jarAssets);
	
	private final ConcurrentMap<String,Image>cache=new ConcurrentHashMap<>();
	public final Map<String,Image>loaded=Collections.unmodifiableMap(cache);
	
	public final File directory;
	public final String extension;
	
	public static final Image NULL=retrieve(null);//1x1 transparent null.png
	
	public static final Image AOTA=retrieve("AotA");
	public static final Image CIT=retrieve("Cit");
	public static final Image AA=retrieve("AA");//needs actual image
	
	public static final Image COMMON=retrieve("Common");
	public static final Image UNCOMMON=retrieve("Uncommon");
	public static final Image RARE=retrieve("Rare");
	
	public static final Image MINOR=retrieve("Minor");
	public static final Image MAJOR=retrieve("Major");
	public static final Image GREAT=retrieve("Great");
	public static final Image ULTIMATE=retrieve("Ultimate");
	
	public static final Image PLAY=retrieve("Play");
	public static final Image TRIGGER=retrieve("Trigger");
	
	public static final Image ARROW=retrieve("Arrow");
	public static final Image BOOT=retrieve("Boot");
	public static final Image SWORD=retrieve("Sword");
	public static final Image BOLT=retrieve("Bolt");
	public static final Image PLATE=retrieve("Plate");
	public static final Image SHIELD=retrieve("Shield");
	public static final Image STAR=retrieve("Star");
	
	public static final Image ROLL2=retrieve("Roll2");
	public static final Image ROLL3=retrieve("Roll3");
	public static final Image ROLL4=retrieve("Roll4");
	public static final Image ROLL5=retrieve("Roll5");
	public static final Image ROLL6=retrieve("Roll6");
	
	public static final Image MOVE0=retrieve("Move0");
	public static final Image MOVE1=retrieve("Move1");
	public static final Image MOVE2=retrieve("Move2");
	public static final Image MOVE3=retrieve("Move3");
	public static final Image MOVE4=retrieve("Move4");
	public static final Image MOVE5=retrieve("Move5");
	public static final Image MOVE6=retrieve("Move6");
	public static final Image MOVE7=retrieve("Move7");
	public static final Image MOVE8=retrieve("Move8");
	
	public static final Image RANGE1=retrieve("Range1");
	public static final Image RANGE2=retrieve("Range2");
	public static final Image RANGE3=retrieve("Range3");
	public static final Image RANGE4=retrieve("Range4");
	public static final Image RANGE5=retrieve("Range5");
	public static final Image RANGE6=retrieve("Range6");
	public static final Image RANGE7=retrieve("Range7");
	public static final Image RANGE8=retrieve("Range8");
	public static final Image RANGE9=retrieve("Range9");
	public static final Image RANGE10=retrieve("Range10");
	
	public static final Image DAMAGE0=retrieve("Damage0");
	public static final Image DAMAGE1=retrieve("Damage1");
	public static final Image DAMAGE2=retrieve("Damage2");
	public static final Image DAMAGE3=retrieve("Damage3");
	public static final Image DAMAGE4=retrieve("Damage4");
	public static final Image DAMAGE5=retrieve("Damage5");
	public static final Image DAMAGE6=retrieve("Damage6");
	public static final Image DAMAGE7=retrieve("Damage7");
	public static final Image DAMAGE8=retrieve("Damage8");
	public static final Image DAMAGE9=retrieve("Damage9");
	public static final Image DAMAGE10=retrieve("Damage10");
	public static final Image DAMAGE11=retrieve("Damage11");
	public static final Image DAMAGE12=retrieve("Damage12");
	public static final Image DAMAGE13=retrieve("Damage13");
	public static final Image DAMAGE14=retrieve("Damage14");
	public static final Image DAMAGE15=retrieve("Damage15");
	public static final Image DAMAGE16=retrieve("Damage16");
	public static final Image DAMAGE17=retrieve("Damage17");
	public static final Image DAMAGE18=retrieve("Damage18");
	public static final Image DAMAGE19=retrieve("Damage19");
	public static final Image DAMAGE20=retrieve("Damage20");
	
	private static Image retrieve(String name){
		final Image result=new Image("card/icons/"+name+".png");
		jarAssets.put(name,result);
		return result;
	}
	public static Image get(String name){
		return jarAssets.getOrDefault(name,NULL);
	}
	Assets(final String ext){
		extension=ext;
		directory=new File("assets/"+toString());
    }
	public void unloadAll(){
		cache.clear();
	}
	public Image unload(String name){
		return cache.remove(name);
	}
	/*public Map<String,Image>loadAll(){//doesn't take into account assets not on disk
		try(DirectoryStream<Path>stream=Files.newDirectoryStream(Paths.get(location))){
			for(Path path:stream){
				final String string=path.getFileName().toString();
				load(string.substring(0,string.lastIndexOf('.')));
			}
			return loaded;
		}catch(IOException e){
			throw new UncheckedIOException(e);
		}
	}*/
	public Image load(String name){
		final Image cached=cache.get(name);
		if(cached!=null)return cached;
		final Image image;
		final File file=new File(directory,name+'.'+extension);
		synchronized(cache){//use double-checked-locking to make sure only 1 thread tries to do this
			final Image syncached=cache.get(name);
			if(syncached!=null)return syncached;
			if(file.isFile()){
				final Image result=new Image(file.toURI().toString());
				cache.put(name,result);
				return result;
			}
			try{
				image=new Image(new URI("http","live.cardhunter.com",'/'+file.toString().replace(File.separatorChar,'/'),null).toString(),true);
			}catch(URISyntaxException e){
				throw new IllegalArgumentException(name,e);
			}
			cache.put(name,image);
		}
		image.progressProperty().addListener(new ChangeListener<Number>(){
			@Override public void changed(ObservableValue<?extends Number>observable,Number oldValue,Number newValue){
				if(newValue.doubleValue()>=1.0 && (Image)image != null){//when image is finished downloading...
					try{//attempt to save it to disk
                                                directory.mkdirs();
						ImageIO.write(SwingFXUtils.fromFXImage(image,null),extension,file);
					}catch(IllegalArgumentException e){
						throw new IllegalArgumentException(name,e);
					}catch(IOException e){
						throw new UncheckedIOException(name,e);
					}finally{
						observable.removeListener(this);
					}
				}
			}
		});
		return image;
	}
}
