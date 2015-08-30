package card.hunter.assets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import javafx.scene.media.AudioClip;

public final class Audio{
	
	private Audio(){};
	
	public static final File directory=new File("assets","audio");
	
	public static AudioClip load(String name){
		File file=new File(directory,name+".mp3");
        if(file.isFile())return new AudioClip(file.toURI().toString());
		directory.mkdirs();
        final String path="/assets/"+directory.getName()+'/'+file.getName();
        try(InputStream in=new URL("http://live.cardhunter.com/"+path).openStream()){
            Files.copy(in,file.toPath());
            return new AudioClip(file.toURI().toString());
        }catch(IOException e){
			throw new UncheckedIOException("Failed to load audio: "+file.getName(),e);
		}
	}
}
