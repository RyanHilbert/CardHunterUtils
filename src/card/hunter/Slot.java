package card.hunter;

public enum Slot{
	Treasure(0),
	Weapon(364),
	Staff(365),
	Shield(366),
	Heavy_Armor(367),
	Helmet(368),
	Boots(369),
	Arcane_Item(370),
	Divine_Weapon(371),
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
	
	Slot(int id){
		this.id=id;
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