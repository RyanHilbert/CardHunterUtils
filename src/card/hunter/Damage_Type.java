package card.hunter;

import card.hunter.io.Assets;

public enum Damage_Type implements ViewableEnum<Damage_Type>{
	None,Acid,Arcane,Cold,Crushing,Electrical,Fire,Holy,Laser,Piercing,Poison,Psychic,Radiation,Slashing,Sonic,Unholy;
	
	@Override public EnumView<Damage_Type>getView(){
		EnumView result=new EnumView(this);
		result.setImage(ordinal()==0?null:Assets.card_thumbnails.load("Adapted to "+name()));
		return result;
	}
}
