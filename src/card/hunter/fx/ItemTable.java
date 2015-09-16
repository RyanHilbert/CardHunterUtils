package card.hunter.fx;

import card.hunter.Equipment;
import card.hunter.Rarity;
import card.hunter.Set;
import card.hunter.Slot;
import card.hunter.Token;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class ItemTable extends TableView<Equipment>{
    public ItemTable(ObservableList<Equipment>list){
        final FilteredList<Equipment>filter=new FilteredList<>(list,item->true);
        final SortedList<Equipment>sorter=new SortedList<>(filter);
        sorter.comparatorProperty().bind(comparatorProperty());
        setItems(sorter);
        
        TableColumn<Equipment,ImageView>iconCol=new TableColumn<>("Icon");
        TableColumn<Equipment,String>nameCol=new TableColumn<>("Name");
        TableColumn<Equipment,Rarity>rarityCol=new TableColumn<>("Rarity");
        TableColumn<Equipment,Byte>levelCol=new TableColumn<>("Lv");
        TableColumn<Equipment,Token.Pair.View>tokenCol=new TableColumn<>("Tk");
        TableColumn<Equipment,HBox>cardCol=new TableColumn<>("Cards");
        TableColumn<Equipment,Slot>slotCol=new TableColumn<>("Slot");
        TableColumn<Equipment,Set>setCol=new TableColumn<>("Set");
        
        iconCol.setCellValueFactory(new PropertyValueFactory<>("view"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        rarityCol.setCellValueFactory(new PropertyValueFactory<>("rarity"));
        levelCol.setCellValueFactory(new PropertyValueFactory<>("level"));
        tokenCol.setCellValueFactory(new PropertyValueFactory<>("tokenView"));
        cardCol.setCellValueFactory(new PropertyValueFactory<>("cardView"));
        slotCol.setCellValueFactory(new PropertyValueFactory<>("slot"));
        setCol.setCellValueFactory(new PropertyValueFactory<>("set"));
        
        EnumFilter<Rarity>rarityFilter=new EnumFilter<>(Rarity.class);
        EnumFilter<Token.Pair>tokenFilter=new EnumFilter<>(Token.Pair.class);
        EnumFilter<Slot>slotFilter=new EnumFilter<>(Slot.class);
        EnumFilter<Set>setFilter=new EnumFilter<>(Set.class);
        TextField nameFilter=new TextField();
        InvalidationListener listener=observable->filter.setPredicate(item->
                rarityFilter.set.contains(item.rarity)
                &&tokenFilter.set.contains(Token.Pair.valueOf(item.token1,item.token2))
                &&slotFilter.set.contains(item.slot)
                &&setFilter.set.contains(item.set)
                &&item.name.toLowerCase().contains(nameFilter.getText().toLowerCase())
        );
        rarityFilter.set.addListener(listener);
        tokenFilter.set.addListener(listener);
        slotFilter.set.addListener(listener);
        setFilter.set.addListener(listener);
        nameFilter.textProperty().addListener(listener);
        
        nameCol.setContextMenu(new ContextMenu(new CustomMenuItem(nameFilter,false)));
        rarityCol.setContextMenu(new ContextMenu(new CustomMenuItem(rarityFilter,false)));
        tokenCol.setContextMenu(new ContextMenu(new CustomMenuItem(tokenFilter,false)));
        slotCol.setContextMenu(new ContextMenu(new CustomMenuItem(slotFilter,false)));
        setCol.setContextMenu(new ContextMenu(new CustomMenuItem(setFilter,false)));
        
        iconCol.setSortable(false);
        cardCol.setSortable(false);
        ObservableList<TableColumn<Equipment,?>>columns=getColumns();
        
        columns.add(iconCol);
        columns.add(nameCol);
        columns.add(rarityCol);
        columns.add(levelCol);
        columns.add(tokenCol);
        columns.add(cardCol);
        columns.add(slotCol);
        columns.add(setCol);
    }
}