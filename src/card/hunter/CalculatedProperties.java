package card.hunter;

import java.util.ArrayList;
import java.util.function.Function;
import static card.hunter.CalculatedProperties.invoke;
import card.hunter.collectible.Card;
import card.hunter.collectible.Equipment;

public class CalculatedProperties {
    public enum Names { 
        
        Damage,Qty,InUse,VampDamage,StepMoves;
    
        public boolean isStateDriven() {
            return isStateDriven(this);
        }
        
        public static boolean isStateDriven(Names name) {
            switch (name) {
                case Qty:
                case InUse:
                    return true;
                default:
                    return false;
            }
        }
        
        public boolean isCardDriven() {
            return isCardDriven(this);
        }
        
        public static boolean isCardDriven(Names name) {
            return !isStateDriven(name);
        }
    }
    
    public static int calc(Names name, Equipment item) {
        switch (name) {
            case Damage:
                return invoke(Damage::calc, item, Op.Sum);
            case Qty:
                return item.getQuantity();
            case InUse:
                return item.getNumInUse();
            case VampDamage:
                return invoke(VampDamage::calc, item, Op.Sum);
            case StepMoves:
                return invoke(StepMoves::calc, item, Op.Sum);
        }
        
        return 0;
    }
    
    public static class Damage {
        public static int calc(Card card) {
            return (card.type1 == Type.Attack || card.type2 == Type.Attack) ? card.damage : 0;
        }
    }
    
    public static class VampDamage {
        public static int calc(Card card) {
            if (card.attackType == Attack_Type.Melee)
                if (card.text.contains("<u>Heal "))
                    return card.damage;
                    
            return 0;
        }
    }
    
    public static class StepMoves {
        public static int calc(Card card) {
            String STEP_TEXT = "<u>Step ";
            if (card.type1 == Type.Attack && card.type2 == Type.Move)
                if (card.text.contains(STEP_TEXT)) {
                    int s = card.text.indexOf(STEP_TEXT);
                    int e = card.text.indexOf("</u>", s);
                    String val = card.text.substring(s + STEP_TEXT.length(), e);
                    return Integer.parseInt(val, 10);
                }   
                    
            return 0;
        }
    }
    
    public enum Op { Sum,Avg,Min,Max };
        
    public static int invoke(Function<Card, Integer> fn, Equipment item, Op op) {
        ArrayList<Integer> vals = new ArrayList<>();
        
        if (item.cards != null) {
            for (Card c : item.cards)
                if (c != null)
                    vals.add(invoke(fn, c));

            switch (op) {
                case Sum:
                    return vals.stream().mapToInt(i -> i).sum();
                case Avg:
                    return (int)Math.floor(vals.stream().mapToInt(i -> i).average().orElse(0));
                case Min: 
                    return vals.stream().mapToInt(i -> i).min().orElse(0);
                case Max:
                    return vals.stream().mapToInt(i -> i).max().orElse(0);
            }
        }
        
        return 0;
    }
    
    public static int invoke(Function<Card, Integer> fn, Card card) {
        return fn.apply(card);
    }
}
