package utils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

// Class for evaluating string expressions as mathematical (boolean) comparisons
// Shamelessly ripped from: http://stackoverflow.com/a/3423360/3782
// Maybe we should try this instead? http://javaluator.sourceforge.net/en/home/
public class Formula{

    private final static ScriptEngineManager mgr=new ScriptEngineManager();
    private final static ScriptEngine engine=mgr.getEngineByName("JavaScript");

    public static boolean eval(String formula,int expectedValue){
        boolean result=true;
        if(formula!=null&&!formula.isEmpty()){
            try{
                int val=Integer.parseInt(formula,10);
                return val==expectedValue;
            }
            catch(Exception ex){
                // not an int, keep going...
            }

            String safe=formula.replaceAll("[^0-9.<>=]","");
            Object value=null;

            try{
                value=engine.eval(expectedValue+safe);
                result=Boolean.parseBoolean(value.toString());
            }
            catch(Exception ex){
                System.out.format("Failed to evaluate formula %s (converted to %s for safety), got value %s.",formula,safe,value);
            }
        }

        return result;
    }
}
