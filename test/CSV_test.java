/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Arrays;
import java.util.HashSet;
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
public class CSV_test {
    
    public final static String[] data= new String[] { 
        "1,Amorphous Body,Amo. Body,Armor,,,,,,,,0,0,Armor,,,,,\"\"\"The foul beast's form wobbled  when I struck, but my hammer left no mark!\"\"\",,This Armor grants <u>Immunity</u> to Crushing. <u>Keep</u>.,<tta> is *resistant to damage -*,,,,,,,TriggeredArmorComponent,damageTypes=Crushing;invulnerableDamageType=Crushing,,,,,,,,,unplayable,,B,,,,,,,Common,,,Implemented,ARMOR,,0,,,Done,,,,,,,,\n",
        "2,Bronze Plates,,Armor,,,,,,,,5,0,Armor,,,,,,,<u>Armor 4</u>. <u>Keep</u>.,<tta> is wearing *armor* -,,,,,,,TriggeredArmorComponent,damageReduction=4,,,,,,,,,unplayable,,D,,,,,,,Common,,,Implemented,ARMOR,,0,,,Done,,,,,,,,\n",
        "3,Cloth Armor,,Armor,,,,,,,,3,8,Armor,,,,,\"Ease of movement can be more important than the protection of metal or hide, as any Wizard will tell you. The cheapskates.\",,<u>Armor 2</u>.,<tta> is wearing *armor* -,,,,,,,TriggeredArmorComponent,damageReduction=2,,,,,,,,,unplayable,,D,D,D,D,,,,Common,,,Implemented,ARMOR,,0,1,\"Divine Armor,Boots,Helmet,Heavy Armor,Robes,Dwarf Skill,Martial Skill\",Done,,,,,,,,\n",
        "47,Bless,,Assist,Magic,Holy,,,,,2,,,,,,,\"All Squares occupied by allies become Blessed Terrain (at the start of each round, occupant Heals 2 and draws a card). Duration 2.\",\"\"\"Be well, be still.\"\" -Monks of Joleph chant\",<aia> bestows *blessings*,,,,,,,,,EveryActorsSquareComponent,filter=allies,AttachToSquareComponent,entryCost=1,AttachedOccupantDrawComponent,drawNumber=1,AttachedHealOccupantComponent,healAmount=2;triggerID=2,,,terrainOnly,,A,A,A,A,,,,Rare,HelpfulTerrain,HolyGround,Implemented,HOLY_TERRAIN,,0,23,\"Divine Armor,Divine Item,Divine Weapon,Divine Skill\",Done,,,,,,,,\n"
    };
    
    public final static String[] tokens0 = new String[] {
        "1","Amorphous Body","Amo. Body","Armor","","","","","","","",
        "0","0","Armor","","","","",
        "\"The foul beast's form wobbled  when I struck, but my hammer left no mark!\"",
        "","This Armor grants <u>Immunity</u> to Crushing. <u>Keep</u>.","<tta> is *resistant to damage -*",
        "","","","","","","TriggeredArmorComponent",
        "damageTypes=Crushing;invulnerableDamageType=Crushing","","","","",
        "","","","","unplayable","","B","","","","","","","Common",
        "","","Implemented","ARMOR","","0","","","Done","","","","",
        "","","","\n"
    };
    
    public final static String[] tokens1 = new String[] {
        "2","Bronze Plates","","Armor","","","","","","","",
        "5","0","Armor","","","","","","","<u>Armor 4</u>. <u>Keep</u>.",
        "<tta> is wearing *armor* -","","","","","","","TriggeredArmorComponent",
        "damageReduction=4","","","","","","","","","unplayable","","D","",
        "","","","","","Common","","","Implemented","ARMOR","","0","","",
        "Done","","","","","","","","\n"
    };

    public final static String[] tokens2=new String[]{
        "3","Cloth Armor","","Armor","","","","","","","","3",
        "8","Armor","","","","",
        "Ease of movement can be more important than the protection of metal or hide, as any Wizard will tell you. The cheapskates.",
        "","<u>Armor 2</u>.","<tta> is wearing *armor* -","","","","","",
        "","TriggeredArmorComponent","damageReduction=2","","","","","",
        "","","","unplayable","","D","D","D","D","","","","Common",
        "","","Implemented","ARMOR","","0","1",
        "Divine Armor,Boots,Helmet,Heavy Armor,Robes,Dwarf Skill,Martial Skill",
        "Done","","","","","","","","\n"
    };
    
    public final static String[] tokens3=new String[]{
        "47","Bless","","Assist","Magic","Holy","","","","","2","","","","","",
        "","All Squares occupied by allies become Blessed Terrain (at the start of each round, occupant Heals 2 and draws a card). Duration 2.",
        "\"Be well, be still.\" -Monks of Joleph chant",
        "<aia> bestows *blessings*","","","","","","","","",
        "EveryActorsSquareComponent","filter=allies","AttachToSquareComponent",
        "entryCost=1","AttachedOccupantDrawComponent","drawNumber=1",
        "AttachedHealOccupantComponent","healAmount=2;triggerID=2","","",
        "terrainOnly","","A","A","A","A","","","","Rare","HelpfulTerrain",
        "HolyGround","Implemented","HOLY_TERRAIN","","0","23",
        "Divine Armor,Divine Item,Divine Weapon,Divine Skill",
        "Done","","","","","","","","\n"
    };

    public CSV_test() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    public static boolean areSame(String[] a, String[] b) {
        boolean same = false;
        
        if (a != null && b != null && a.length == b.length) {
            HashSet<String> left = new HashSet<>();
            left.addAll(Arrays.asList(a));
            
            HashSet<String> right = new HashSet<>();
            right.addAll(Arrays.asList(b));
            
            for(String s : left) {
                if(!right.contains(s)) {
		    System.out.println("Could not find a match for:\n"+s);
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
    public void ItHandlesALineWithNoFlavorTextQuotesOrCommas(){
        String[] expected = tokens1;
        String[] actual = CSV.tokenizeLine(data[1]);

        Assert.assertTrue(areSame(expected, actual));
    }

    @Test
    public void ItHandlesALineWithFlavorTextCommas(){
        String[] expected=tokens2;
        String[] actual=CSV.tokenizeLine(data[2]);

        Assert.assertTrue(areSame(expected,actual));
    }
    
    @Test
    public void ItHandlesALineWithFlavorTextQuotesAndCommas(){
        String[] expected = tokens0;
        String[] actual = CSV.tokenizeLine(data[0]);
        
        Assert.assertTrue(areSame(expected,actual));
    }
    
    @Test
    public void ItHandlesALineWithMultipleSequentialFlavorTextQuotesAndCommas(){
        String[] expected = tokens3;
        String[] actual = CSV.tokenizeLine(data[3]);
        
        Assert.assertTrue(areSame(expected,actual));
    }

}
