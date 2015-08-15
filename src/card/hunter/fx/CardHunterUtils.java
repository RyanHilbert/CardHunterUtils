package card.hunter.fx;

import card.hunter.Card;
import card.hunter.Item;
import card.hunter.assets.Data;
import java.io.File;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
		
		Data.Table cardData=Data.Cards.load();
		Data.Table itemData=Data.Equipment.load();
		ObservableList<Card>cardList=FXCollections.observableArrayList();
		ObservableList<Item>itemList=FXCollections.observableArrayList();
		for(Data.Table.Row row:cardData)cardList.add(new Card(row));
		for(Data.Table.Row row:itemData)itemList.add(new Item(row));
		
        ItemTable table=new ItemTable(itemList);//setup the user interface
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
        
        partyFile=new File(partiesDir,"currentParty.bbcode");
        if(partyFile.isFile())
            party.loadPartyFrom(partyFile);
        
        stage.setTitle("Card Hunter Utility Program");
        stage.setScene(new Scene(new HBox(party,table),1000,750));
        
        stage.show();
    }
    
    @Override
    public void stop(){
        System.out.println("Stage is closing");
        
        if (party != null && partyFile != null)
            party.SavePartyTo(partyFile);
    }
}
