package card.hunter;

public enum Set{
    Base,AotA,Cit,AA;
    private final static Set[]values=Set.values();
    public static Set valueOf(int i){return values[i];}
}