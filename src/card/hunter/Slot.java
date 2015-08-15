package card.hunter;

public enum Slot{
	Arcane_Item,Arcane_Skill,Boots,Divine_Armor,Divine_Item,Divine_Skill,Divine_Weapon,Dwarf_Skill,Elf_Skill,Heavy_Armor,Helmet,Human_Skill,Martial_Skill,Robes,Shield,Staff,Treasure,Weapon;
	public Item dfault;//hack used to give each slot a default item value during the global item list initialization
	@Override public String toString(){return super.toString().replace('_',' ');}
}