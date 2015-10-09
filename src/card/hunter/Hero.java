package card.hunter;

public enum Hero{
	DwarvenWarrior(Race.Dwarf,Role.Warrior),
	HumanWarrior(Race.Human,Role.Warrior),
	ElvenWarrior(Race.Elf,Role.Warrior),
	HumanPriest(Race.Human,Role.Priest),
	ElvenPriest(Race.Elf,Role.Priest),
	DwarvenPriest(Race.Dwarf,Role.Priest),
	HumanWizard(Race.Human,Role.Wizard),
	ElvenWizard(Race.Elf,Role.Wizard),
	DwarvenWizard(Race.Dwarf,Role.Wizard);
	
	public final Race race;
	public final Role role;
	
	Hero(Race race,Role role){
		this.race=race;
		this.role=role;
	}
	public static Hero valueOf(Race race,Role role){
		return valueOf(race.name().replace("f","ven")+role.name());
	}
}
