package card.hunter;

import card.hunter.io.Assets;
import javafx.scene.image.*;

public class EnumView<E extends Enum<E>&ViewableEnum<E>>extends ImageView implements Comparable<EnumView<E>>{
	public final E viewed;
	public EnumView(E e){
		super(Assets.get(e.name()));
		viewed=e;
	}
	public EnumView(E e,Image image){
		super(image);
		viewed=e;
	}
	@Override public boolean equals(Object object){
		return object==viewed;
	}
	@Override public int hashCode(){
		return viewed.hashCode();
	}
	@Override public String toString(){
		return viewed.toString();
	}
	@Override public int compareTo(EnumView<E>view){
		return viewed.compareTo(view.viewed);
	}
}
