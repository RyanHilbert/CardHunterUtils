package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import static models.CalculatedProperties.invoke;
import models.Card.AttackType;
import models.Card.Type;

public class CalculatedProperties {
    private static String[] props = new String[] { "VampDamage", "StepMoves" };
    
    public static List<String> getProps() { 
        return Arrays.asList(props);
    }
    
    public static int calc(String name, Item item) {
        switch (name) {
            case "VampDamage":
                return invoke(VampDamage::calc, item, Op.Sum);
            case "StepMoves":
                return invoke(StepMoves::calc, item, Op.Sum);
        }
        
        return 0;
    }
    
    public static class VampDamage { // implements ICalculatedCardProperty {
        public static int calc(Card card) {
            if (card.attackType == AttackType.Melee)
                if (card.text.contains("<u>Heal "))
                    return card.damage;
                    
            return 0;
        }
    }
    
    public static class StepMoves { // implements ICalculatedCardProperty {
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
    
    public interface ICalculatedCardProperty {
        public int calc(Card card);
    }
    
    public enum Op { Sum,Avg,Min,Max };
        
    public static int invoke(Function<Card, Integer> fn, Item item, Op op) {
        ArrayList<Integer> vals = new ArrayList<>();
        
        if (item.card1 != null)
            vals.add(invoke(fn, item.card1));
        if (item.card2 != null)
            vals.add(invoke(fn, item.card2));
        if (item.card3 != null)
            vals.add(invoke(fn, item.card3));
        if (item.card4 != null)
            vals.add(invoke(fn, item.card4));
        if (item.card5 != null)
            vals.add(invoke(fn, item.card5));
        if (item.card6 != null)
            vals.add(invoke(fn, item.card6));
        
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
        
        return 0;
    }
    
    public static int invoke(Function<Card, Integer> fn, Card card) {
        return fn.apply(card);
    }
}
