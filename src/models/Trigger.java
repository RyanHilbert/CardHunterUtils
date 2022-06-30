package models;

public class Trigger {
    public final Integer trigger;
    public final Integer keep;
    public final Effect effect;
    public final String text,iconLeft,iconRight;
    public final Integer amountLeft,amountRight;
    
    public Trigger(Integer trigger, Integer keep, Effect effect, String text) {
        this.trigger=trigger;
        this.keep=keep;
        this.effect=effect;
        this.text=text.trim();
        
        this.iconLeft = (trigger != 0) ? "Dice" : "";
        this.amountLeft = trigger;
        
        this.iconRight = effect.name();
        this.amountRight = 0;
    }    
    
    public enum Effect{Armor,Attack,Block,Boost,Handicap,Move,Special}
}
