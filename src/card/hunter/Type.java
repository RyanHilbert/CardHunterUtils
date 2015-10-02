package card.hunter;

import javafx.scene.paint.Color;

public enum Type{
	Armor(Color.GRAY),
	Assist(Color.LIGHTGRAY),
	Attack(Color.DARKRED),
	Block(Color.OLIVEDRAB),
	Boost(Color.CHOCOLATE),
	Handicap(new Color(.2,.2,.2,1)),
	Move(Color.STEELBLUE),
	Utility(Color.MEDIUMPURPLE);
	
	public final Color color;
	
	Type(Color color){
		this.color=color;
	}
}
