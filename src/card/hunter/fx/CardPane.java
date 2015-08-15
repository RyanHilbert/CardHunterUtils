package card.hunter.fx;

import card.hunter.Card;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;

public class CardPane extends ScrollPane{
    public CardPane(){
        FlowPane flow=new FlowPane();
        ObservableList<Node>children=flow.getChildren();
        for(Card card:Card.map.values()){children.add(card.getThumbnail());}
        setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        setFitToWidth(true);
        setContent(flow);
    }
}