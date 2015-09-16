package card.hunter;

public enum Race{
	Dwarf(Slot.Dwarf_Skill,366),
	Human(Slot.Human_Skill,356),
	Elf(Slot.Elf_Skill,349);
	
	public final Slot slot;
	private final int id;
	
	Race(Slot slot,int id){
		this.slot=slot;
		this.id=id;
	}
	public Slot getSlot(){
		return slot;
	}
	public Card getDefaultMoveCard(){
		return Card.idMap().get(id);
	}
}