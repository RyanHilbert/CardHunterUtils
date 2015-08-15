package card.hunter;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public enum Token implements Viewable{
	None,Minor,Major,Great,Ultimate;
	
	private static final Token[]values=values();
	
	public final Image image=new Image("card/"+toString()+".png");
	
	public static Token valueOf(int i){
		return i==-1?None:values[i];
	}
	@Override public View view(){
		return new View();
	}
	public Pair pair(Token token){
		return new Pair(token);
	}
	public class View extends ImageView implements Comparable<View>{
		
		public final Token token=Token.this;
		
		public View(){
			super(Token.this.image);
		}
		@Override public int compareTo(View view){
			return token.compareTo(view.token);
		}
	}
	public class Pair implements Viewable,Comparable<Pair>{
		public final Token greater,lesser;
		public Pair(Token token){
			if(Token.this.compareTo(token)<0){
				greater=token;
				lesser=Token.this;
			}else{
				greater=Token.this;
				lesser=token;
			}
		}
		@Override public int compareTo(Pair pair) {
			final int comparison=greater.compareTo(pair.greater);
			return comparison!=0?comparison:lesser.compareTo(pair.lesser);
		}
		@Override public View view(){
			return new View();
		}
		public class View extends VBox implements Comparable<View>{
			public final Pair pair=Pair.this;
			public View(){
				super(Pair.this.greater.view(),Pair.this.lesser.view());
			}
			@Override public int compareTo(View view) {
				return pair.compareTo(view.pair);
			}
		}
	}
}