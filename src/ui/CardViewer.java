
import app.App;
import java.io.File;
import javafx.scene.control.ScrollPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import models.Item;
import ui.WebBrowser;
import utils.FileUtils;

public class CardViewer {
    private Item item;
    private boolean showAll;
    
    public CardViewer() { 
        this.showAll = true;
    }
    
    public CardViewer(Item item) {
        this.item = item;
    }
    
    public void show(Stage stage) {
        boolean all = (showAll || item == null);
        String title = (all) ? "ALL CARDS" : "Cards for: " + item.name;
        int height = (all) ? 1200 : 400;
        String json = (all) ?
                String.format("cardViewData = { title: \"" + title + "\", cards: cardNames, comparisons: %s };", App.state().compareCardArt) :
                item.toCardViewJson();
                
        FileUtils.writeFile(new File("src/scripts/cardViewData.js"), json);
        
        String path = "file:///" + System.getProperty("user.dir").replace("\\\\", "/") + "/src/cardview.html"; 
        
        if (App.state().openExternally)
            WebBrowser.launch(path);
        else {
            WebView browser = new WebView();
            browser.setPrefSize(1600, height);
            WebEngine webEngine = browser.getEngine();
             
            webEngine.load(path);   

            ScrollPane content = new ScrollPane(browser);
            ModalDialog.show(stage, title, content);
        }
    }
}
