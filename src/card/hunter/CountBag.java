package card.hunter;

import java.util.HashMap;
import java.util.Map;

public class CountBag<T>{

    private final Map<T,Integer> counts=new HashMap<>();

    public boolean add(T item){
        int currCt=countOf(item);
        counts.put(item,currCt+1);

        return true;
    }

    public int countOf(T item){
        return counts.containsKey(item) ? counts.get(item) : 0;
    }

    public void remove(T item){
        int ct = countOf(item);
        if (ct > 1)
            counts.put(item, ct - 1);
        else if (ct == 1)
            counts.remove(item);
    }
}
