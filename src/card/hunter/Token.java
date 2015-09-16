package card.hunter;

import javafx.scene.layout.VBox;

public enum Token implements ViewableEnum<Token>{
	Base,Minor,Major,Great,Ultimate;
	
	private static final Token[]values=values();
	
	public static Token valueOf(int i){
		return i==-1?Base:values[i];
	}
	public Pair pair(Token token){
		return Pair.valueOf(this,token);
	}
	public enum Pair implements Viewable{

		Base_Base,Minor_Base,Minor_Minor,Major_Base,Major_Minor,Major_Major;

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