package card.hunter;

public enum Rarity implements ViewableEnum<Rarity>{
    Common,Uncommon,Rare,Epic,Legendary;
    private final static Rarity[]values=Rarity.values();
    public static Rarity value(int i){
        return values[i];
    }

    public static int saleValue(Rarity r){
        switch(r){
            case Common:
                return 1;
            case Uncommon:
                return 2;
            case Rare:
                return 5;
            case Epic:
                return 20;
            case Legendary:
                return 100;
        }

        return 0;
    }
}