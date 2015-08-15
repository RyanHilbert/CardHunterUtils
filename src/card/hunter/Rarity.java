package card.hunter;

public enum Rarity{
    Common,Uncommon,Rare,Epic,Legendary;
    private final static Rarity[]values=Rarity.values();
    public static Rarity valueOf(int i){return values[i];}
}