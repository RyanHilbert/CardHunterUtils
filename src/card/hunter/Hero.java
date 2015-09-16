package card.hunter;

public enum Hero{
	DwarvenWarrior(Race.Dwarf,Character.Warrior),
	HumanWarrior(Race.Human,Character.Warrior),
	ElvenWarrior(Race.Elf,Character.Warrior),
	HumanPriest(Race.Human,Character.Priest),
	ElvenPriest(Race.Elf,Character.Priest),
	DwarvenPriest(Race.Dwarf,Character.Priest),
	HumanWizard(Race.Human,Character.Wizard),
	ElvenWizard(Race.Elf,Character.Wizard),
	DwarvenWizard(Race.Dwarf,Character.Wizard);
	
	public final Race race;
	public final Character character;
	
	Hero(Race race,Character character){
		this.race=race;
		this.character=character;
	}
	public static Hero valueOf(Race race,Character character){
		return valueOf(race.name().replace("f","ven")+character.name());
	}
}
