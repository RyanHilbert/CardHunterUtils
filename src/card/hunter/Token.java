package card.hunter;

import javafx.scene.layout.VBox;

public enum Token implements ViewableEnum<Token>{
	None,Minor,Major,Great,Ultimate;
	
	public Pair pair(Token token){
		return Pair.valueOf(this,token);
	}
	public enum Pair implements Viewable{

		None_None,Minor_None,Minor_Minor,Major_None,Major_Minor,Major_Major;

		public final Token greater,lesser;

		Pair(){
			final String[]strings = super.toString().split("_");
			this.greater = Token.valueOf(strings[0]);
			this.lesser = Token.valueOf(strings[1]);
		}
		public static Pair valueOf(Token greater,Token lesser){
			if (greater.compareTo(lesser)<0){
				final Token temp=greater;
				greater=lesser;
				lesser=temp;
			}
			return valueOf(greater+"_"+lesser);
		}
		@Override public View getView(){
			return new View();
		}
		public class View extends VBox implements Comparable<View> {

			public final Pair pair=Pair.this;

			public View(){
				super(greater.getView(),lesser.getView());
			}
			@Override public int compareTo(View view){
				return pair.compareTo(view.pair);
			}
		}
	}
}