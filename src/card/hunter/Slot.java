package card.hunter;

import card.hunter.collectible.Equipment;

public enum Slot{
	Treasure(0),
	Divine_Weapon(371),
	Weapon(364),
	Staff(365),
	Shield(366),
	Heavy_Armor(367),
	Helmet(368),
	Boots(369),
	Arcane_Item(370),
	Robes(372),
	Divine_Armor(373),
	Divine_Item(374),
	Arcane_Skill(100148),
	Divine_Skill(100149),
	Martial_Skill(100150),
	Elf_Skill(100151),
	Human_Skill(100152),
	Dwarf_Skill(100153);
	
	private final int id;
	public final int cardCount;
	
	Slot(int id){
		this.id=id;
		final int ordinal=ordinal();
		this.cardCount=ordinal==0?0:ordinal<4?6:3;
	}
	public int getCardCount(){
		return cardCount;
	}
	public Equipment getDefaultEquipment(){
		return Equipment.idMap().get(id);
	}
	public static Slot fromString(String string){
		return valueOf(string.replace(' ','_'));
	}
	@Override public String toString(){
		return super.toString().replace('_',' ');
	}
}