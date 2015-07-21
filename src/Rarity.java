public enum Rarity{
    Common,Uncommon,Rare,Epic,Legendary;
    private final static Rarity[]values=Rarity.values();
    public static Rarity value(int i){return values[i];}
}