
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author mcw
 */
public class Character_test{

    public final static String[] data=new String[]{
        "[SIZE=5][B]Dwarfy McFakeData[/B][/SIZE]\n[I]Level 10 [wiki]Dwarf[/wiki] [wiki]Warrior[/wiki][/I]\n[INDENT=1][item]Powerpike[/item]\n[item]Militiaman's Pike[/item]\n[item]Weighted Shortsword[/item]\n[item]Dependable Mail[/item]\n[item]Twisting Shield[/item]\n[item]Shifty Skin Hat[/item]\n[item]Boots of Safety[/item]\n[item]Charging Boulder[/item]\n[item]Novice Impaling[/item]\n[/INDENT]\n",
        "[SIZE=5][B]Steev[/B][/SIZE]\n[I]Level 12 [wiki]Human[/wiki] [wiki]Warrior[/wiki][/I]\n[INDENT=1][item]Bejeweled Shortsword[/item]\n[item]Bejeweled Shortsword[/item]\n[item]Infused Greatclub[/item]\n[item]Dependable Mail[/item]\n[item]Twisting Shield[/item]\n[item]Commander's Cap[/item]\n[item]Marmot Boots[/item]\n[item]Unsubtle Thinker[/item]\n[item]Novice Bruising[/item]\n[/INDENT]\n",
        "[SIZE=5][B]La-dee-dah[/B][/SIZE]\\n[I]Level 10 [wiki]Elf[/wiki] [wiki]Warrior[/wiki][/I]\\n[INDENT=1][item]Bejeweled Shortsword[/item]\\n[item]Bejeweled Shortsword[/item]\\n[item]Unbalanced Doloire[/item]\\n[item]Perilous Ringmail[/item]\\n[item]Parrying Buckler[/item]\\n[item]Necalli Cap[/item]\\n[item]Shielding Warp Boots[/item]\\n[item]Cautious Mobility[/item]\\n[item]Powered Slicing[/item]\\n[/INDENT]"
    };

    private static CardHunterUtils app;

    public Character_test(){
    }

    @BeforeClass
    public static void setUpClass(){
        // yeesh... just because test classes touch Image which touches JavaFX
        // http://stackoverflow.com/a/17455384/3782
        app=new CardHunterUtils();
        app.main(); 
    }

    @AfterClass
    public static void tearDownClass(){
    }

    @Before
    public void setUp(){
    }

    @After
    public void tearDown(){
    }
    
    public static boolean areSame(ArrayList<Character> a,ArrayList<Character> b){
        boolean same = false;
        
        if (a != null && b != null && a.size() == b.size()) {
            for(int ix=0;ix<a.size();ix++){
                if(!Character.areIdentical(a.get(ix),b.get(ix))){
                    System.out.println("Could not find a match for:\n"+a.get(ix).name);
                    return false;
                }
            }
            
            same = true;
        }
        else
            System.out.println("One side was null, or lengths don't match");
        
        return same;
    }

    @Test
    public void ItParsesTheNameField(){
        String expected="Dwarfy McFakeData";
        String actual=Character.firstFromBBCode(data[0]).name;

        Assert.assertEquals("Wanted: "+expected+", but got: "+actual,expected,actual);
    }
    
    @Test
    public void ItParsesTheLevelField(){
        int expected=10;
        int actual=Character.firstFromBBCode(data[0]).level;

        Assert.assertEquals("Wanted: "+expected+", but got: "+actual,expected,actual);
    }
    
    @Test
    public void ItParsesTheRaceField(){
        Character.Race expected=Character.Race.Dwarf;
        Character.Race actual=Character.firstFromBBCode(data[0]).race;

        Assert.assertEquals("Wanted: "+expected+", but got: "+actual,expected,actual);
    }
    
    @Test
    public void ItParsesTheRoleField(){
        Character.Role expected=Character.Role.Warrior;
        Character.Role actual=Character.firstFromBBCode(data[0]).role;

        Assert.assertEquals("Wanted: "+expected+", but got: "+actual,expected,actual);
    }
    
    @Test
    public void ItParsesInventory(){
        ArrayList<Item> expected=Item.byName(
            "Powerpike",
            "Militiaman's Pike",
            "Weighted Shortsword",
            "Dependable Mail",
            "Twisting Shield",
            "Shifty Skin Hat",
            "Boots of Safety",
            "Charging Boulder",
            "Novice Impaling");
        
        ArrayList<Item> actual=Character.firstFromBBCode(data[0]).equipment.items;

        Assert.assertEquals("Wanted: "+expected+", but got: "+actual,expected,actual);
    }


    @Test
    public void ItHandlesMultipleCharacters(){
        // build actual first, because it loads the roster.
        ArrayList<Character> actual=Character.allFromBBCode(String.join("\n",data));

        ArrayList<Character> expected=Character.byName(
            "Dwarfy McFakeData",
            "Steev",
            "La-dee-dah");

        Assert.assertTrue(areSame(expected,actual));
    }

    @Test
    public void ItRecognizesIdenticalCharacters(){
        Character c=Character.firstFromBBCode(data[1]);
        Character c2=Character.allFromBBCode(String.join("\n",data)).get(1);

        Assert.assertTrue(Character.areIdentical(c,c2));
    }

    @Test
    public void ItOnlyGrabsTheFirstCharacterWhenAsked(){
        Character actual=Character.firstFromBBCode(String.join("\n",data[1],data[2]));

        Character expected=Character.byName("Steev");

        Assert.assertTrue(Character.areIdentical(expected,actual));
    }

    @Test
    public void ItReadsTheFullFormatWithGearAndCards(){
        try{
            String fileData=FileUtils.textFromFile("test/data/party1.bbcode");
            ArrayList<Character> actual=Character.allFromBBCode(String.join("\n",fileData));

            ArrayList<Character> expected=Character.byName(
                "Elfelfa",
                "Jorbs",
                "Wizzy Bizzy Fizzy");

            Assert.assertTrue(areSame(expected,actual));
        }
        catch(FileNotFoundException ex){
            Assert.fail(ex.getMessage());
        }
    }
}
