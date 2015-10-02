package card.hunter.fx;

import card.hunter.collectible.Equipment;
import java.io.File;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class CardHunterUtils extends Application{
    public static void main(String...args){launch(args);}
    
    //private PartyView party;
    private File partyFile;
    
    @Override public void start(Stage stage)throws Exception{
		stage.getIcons().add(new Image("file:Icon-16x16.png"));
        File partiesDir=new File(Paths.get(System.getProperty("user.dir"),"saved","parties").toString());
        partiesDir.mkdirs();
		
        ItemTable table=new ItemTable(FXCollections.observableList(Equipment.list()));
        table.setPrefWidth(0);
        HBox.setHgrow(table,Priority.ALWAYS);
        /*
        party=new PartyView();
        party.onSlotClick=slot -> {
            Equipment item=table.getFocusModel().getFocusedItem();
            if(slot.isHolding(item)){
                slot.empty();
            }
            else{
                slot.setItem(item);
            }
        };
        */
        partyFile=new File(partiesDir,"currentParty.bbcode");
        if(partyFile.isFile())
            //party.loadPartyFrom(partyFile);
        
        stage.setTitle("Card Hunter Utility Program");
        stage.setScene(new Scene(table,1000,750));
        
        stage.show();
    }
    /*
    @Override
    public void stop(){
        System.out.println("Stage is closing");
        
        if (party != null && partyFile != null)
            party.SavePartyTo(partyFile);
    }*/
}
