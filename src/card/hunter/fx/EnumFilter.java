package card.hunter.fx;

import java.util.EnumSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

//class used by ItemTable as context menus to filter items based on Enums
public class EnumFilter<E extends Enum<E>>extends VBox{
    public final ObservableSet<E>set;
    public EnumFilter(Class<E>type){
        final ObservableSet<E>writableSet=FXCollections.observableSet(EnumSet.allOf(type));
        set=FXCollections.unmodifiableObservableSet(writableSet);
        ObservableList<Node>children=getChildren();
        for(final E e:type.getEnumConstants()){
            CheckBox box=new CheckBox(e.toString());
            box.setSelected(true);
            box.setOnAction(event->{
                if(box.isSelected())writableSet.add(e);
                else writableSet.remove(e);
            });
            children.add(box);
        }
    }
}