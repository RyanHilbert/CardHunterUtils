import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class CardHunterUtils extends Application{
    public static void main(String...args){launch(args);}
    
    private PartyView party;
    private File partyFile;
    
    @Override
    public void start(Stage stage) throws Exception{
        File partiesDir=new File(Paths.get(System.getProperty("user.dir"),"saved","parties").toString());
        partiesDir.mkdirs();

        File cardDir=new File("data/gameplay/Cards"),itemDir=new File("data/gameplay/Equipment");
        cardDir.mkdirs();//create all necessary directories...
        itemDir.mkdirs();
        new File("assets/card_thumbnails").mkdirs();
        new File("assets/card_illustrations").mkdirs();
        new File("assets/item_illustrations").mkdirs();
        String address="http://live.cardhunter.com/";//location to download files from if not found
        File cardFile=new File(cardDir,"Cards.csv"),itemFile=new File(itemDir,"Equipment.csv");
        if(!cardFile.isFile())try(InputStream in=new URL(address+cardFile.toString().replace('\\','/')).openStream()){
            Files.copy(in,cardFile.toPath());//download card file if not found
        }
        if(!itemFile.isFile())try(InputStream in=new URL(address+itemFile.toString().replace('\\','/')).openStream()){
            Files.copy(in,itemFile.toPath());//download item file if not found
        }
        try(Scanner scanner=new Scanner(cardFile,StandardCharsets.ISO_8859_1.toString())){
            scanner.nextLine();//skip first two lines
            scanner.nextLine();//parse remaining CSV file into cards
            while(scanner.hasNextLine())Card.fromCSV(scanner.nextLine());
        }
        try(Scanner scanner=new Scanner(itemFile,StandardCharsets.ISO_8859_1.toString())){
            scanner.nextLine();//skip first two lines
            scanner.nextLine();//parse remaining CSV file into items
            while(scanner.hasNextLine())Item.fromCSV(scanner.nextLine());
        }
        
        AssetLoader.setupJSON();        
        
        ItemTable table=new ItemTable();//setup the user interface
        table.setPrefWidth(0);
        HBox.setHgrow(table,Priority.ALWAYS);
        
        party=new PartyView();
        party.onSlotClick=slot -> {
            Item item=table.getFocusModel().getFocusedItem();
            if(slot.isHolding(item)){
                slot.empty();
            }
            else{
                slot.setItem(item);
            }
        };
        party.onSlotRightClick=slot -> {
            new CardViewer(slot.itemProperty.getValue()).show(stage);
        };
        
        partyFile=new File(partiesDir,"currentParty.bbcode");
        if(partyFile.isFile())
            party.loadPartyFrom(partyFile);
        
        stage.setTitle("Card Hunter Utility Program");
        stage.setScene(new Scene(new HBox(party,table),1000,750));
        
        stage.show();
        
        table.onShow();
    }
    
    @Override
    public void stop(){
        System.out.println("Stage is closing");
        
        if (party != null && partyFile != null)
            party.savePartyTo(partyFile);
    }
}
