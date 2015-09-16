package card.hunter.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import javafx.scene.media.AudioClip;

public final class Audio{
	
	private Audio(){};
	
	public static final File directory=new File("assets","audio");
	
	public static AudioClip load(String name){//current version may have threading issues
		try{
			File file=new File(directory,name.toString()+".mp3");//toString() may cause NullPointerException to be thrown
			if(file.isFile())return new AudioClip(file.toURL().toString());
			directory.mkdirs();
			try(InputStream in=new URL("http","live.cardhunter.com",'/'+file.getPath().replace(File.separatorChar,'/')).openStream()){
				Files.copy(in,file.toPath());
				return new AudioClip(file.toURL().toString());
			}
		}catch(MalformedURLException e){
			throw new IllegalArgumentException(name,e);
		}catch(IOException e){
			throw new UncheckedIOException(name,e);
		}
	}
}
