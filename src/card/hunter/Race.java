package card.hunter;

public enum Race{
	Dwarf(366),
	Human(356),
	Elf(349);
	
	public final Slot slot;
	private final int id;
	
	Race(int id){
		this.slot=Slot.valueOf(name()+"_Skill");
		this.id=id;
	}
	public Slot getSlot(){
		return slot;
	}
	public Card getDefaultMoveCard(){
		return Card.idMap().get(id);
	}
}