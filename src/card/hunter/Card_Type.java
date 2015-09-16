package card.hunter;

import javafx.scene.paint.Color;

public enum Card_Type{
	Armor(Color.GRAY),
	Assist(Color.LIGHTGRAY),
	Attack(Color.DARKRED),
	Block(Color.OLIVEDRAB),
	Boost(Color.CHOCOLATE),
	Handicap(new Color(.2,.2,.2,1)),
	Move(Color.STEELBLUE),
	Utility(Color.MEDIUMPURPLE);
	
	public final Color color;
	
	Card_Type(Color color){
		this.color=color;
	}
}
