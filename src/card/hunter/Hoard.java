package card.hunter;

import card.hunter.collectible.Equipment;
import java.util.Comparator;
import java.util.TreeMap;

// Class for holding a hoard of Card Hunter items
public class Hoard extends TreeMap<Equipment,Integer>{
    // <editor-fold defaultstate="collapsed" desc="Singleton implementation">
    private static Hoard instance = null;

    private static Hoard get() {
        if(instance==null)
            instance=new Hoard(Equipment.nameComparer);

        return instance;
    }

    private Hoard(Comparator<Equipment> c) { super(c); };
    // </editor-fold>

    public static int countOf(Equipment item) {
        Hoard h = get();
        return (item != null && h.containsKey(item)) ? 
            h.get(item) : 0;
    }
    
    public static void load(String text){
        if(text!=null&&!text.isEmpty()){
            System.out.println("Starting hoard import...");
            Hoard h = get();
            System.out.format("\tClearing existing %d items... ", h.size());
            h.clear();
            System.out.println("cleared.");

            String[] lines=text.split("\n");
            int sum=(lines[0].contains(HOARD_HEADER))
                ? loadHoardFile(h,lines)
                : loadConsoleOutput(h,lines);

            System.out.format("Imported %d items (of %d kinds) to hoard.\n",sum,h.size());
        }
    }

    private static int loadConsoleOutput(Hoard h,String[] lines){
        boolean inItems=false;
        int sum=0;

        final int LINE_INDEX=9;
        final int LINES_AFTER_START=3;
        final String START_LINE="Received extension response: collection";
        final String END_LINE="sfs";

        for(int ix=0;ix<lines.length;ix++){
            String line = lines[ix];

            if (inItems)
                if(line.length()<=LINE_INDEX||line.contains(END_LINE))
                    break;
                else {
                    int id = Integer.parseInt(line.substring(LINE_INDEX), 10);
                    Equipment item = Equipment.byId(id);
                    ix++;
                    if (item == null)
                        System.out.format("\tUh-oh, couldn't find an item with id %d.  Maybe we don't have the latest item data update?\n", id);
                    else {
                        line = lines[ix];
                        int count = Integer.parseInt(line.substring(LINE_INDEX), 10);
                        System.out.format("\tAdding %d %s to the hoard...\n", count, item.name);

                        h.put(item, count);
                        sum += count;
                    }
                }
            else if(line.contains(START_LINE)){
                System.out.println("\tFound item sequence...");
                inItems = true;
                ix+=LINES_AFTER_START; // advance to first item
            }
        }

        return sum;
    }

    // <editor-fold defaultstate="collapsed" desc="File format">
    private static final String HOARD_HEADER="Id\tQty\tName\tRarity\tSaleValue\tHoardfile";
    private static final int ID=0;
    private static final int QTY=1;
    private static final int NAME=2;
    private static final int RARITY=3;
    private static final int SALE_VALUE=4;
    // </editor-fold>

    private static int loadHoardFile(Hoard h,String[] lines){
        int sum=0;

        for(int ix=1;ix<lines.length;ix++){
            String line=lines[ix];

            if(line.isEmpty())
                break;
            else{
                String fields[]=line.split("\t");

                int id=Integer.parseInt(fields[ID],10);
                Equipment item=Equipment.byId(id);

                if(item==null){
                    System.out.format("\tUh-oh, couldn't find an item with id %d.  Maybe we don't have the latest item data update?\n",id);
                }
                else{
                    int count=Integer.parseInt(fields[QTY],10);
                    System.out.format("\tAdding %d %s to the hoard...\n",count,item.name);

                    h.put(item,count);
                    sum+=count;
                }
            }
        }

        return sum;
    }

    public static String toText(){
        String hoardText=HOARD_HEADER+"\n";
        Hoard h=get();

        int sum=0;
        int cash=0;

        if(h.size()>0){
            for(Equipment item : h.keySet()){
                int qty=h.get(item);
                int saleVal=(Rarity.saleValue(item.rarity)*qty);
                sum+=qty;
                cash+=saleVal;
                hoardText
                    +=item.id+"\t"
                    +qty+"\t"
                    +item.name+"\t"
                    +item.rarity+"\t"
                    +saleVal+"\n";
            }
        }
        hoardText+="\nAll\t"+sum+"\tAll\t\t"+cash;

        return hoardText;
    }

    public static String toConsoleishText(){
        final int LINE_INDEX=9;
        final int LINES_AFTER_START=3;
        final String START_LINE="Received extension response: collection";
        final String END_LINE="sfs";

        String hoardText=START_LINE;
        Hoard h=get();

        if(h.size()>0){
            String lineLead="";
            for(int i=0;i<=LINES_AFTER_START;i++){
                hoardText+="\n";
            }
            for(int i=0;i<LINE_INDEX;i++){
                lineLead+=" ";
            }

            for(Equipment item : h.keySet()){
                hoardText+=lineLead+item.id+"\n";
                hoardText+=lineLead+h.get(item)+"\n";
            }
        }

        hoardText+=END_LINE+"\n";

        return hoardText;
    }
}
