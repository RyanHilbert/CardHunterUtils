package card.hunter.fx;

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

public class ItemTable extends TableView<Item>{
    public ItemTable(){
        final FilteredList<Item>filter=new FilteredList<>(Item.list,item->true);
        final SortedList<Item>sorter=new SortedList<>(filter);
        sorter.comparatorProperty().bind(comparatorProperty());
        setItems(sorter);
        
        TableColumn<Item,ImageView>iconCol=new TableColumn<>("Icon");
        TableColumn<Item,String>nameCol=new TableColumn<>("Name");
        TableColumn<Item,Rarity>rarityCol=new TableColumn<>("Rarity");
        TableColumn<Item,Byte>levelCol=new TableColumn<>("Lv");
        TableColumn<Item,Item.Token.Pair.HView>tokenCol=new TableColumn<>("Tokens");
        TableColumn<Item,HBox>cardCol=new TableColumn<>("Cards");
        TableColumn<Item,Item.Slot>slotCol=new TableColumn<>("Slot");
        TableColumn<Item,Set>setCol=new TableColumn<>("Set");
        
        iconCol.setCellValueFactory(new PropertyValueFactory<>("view"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        rarityCol.setCellValueFactory(new PropertyValueFactory<>("rarity"));
        levelCol.setCellValueFactory(new PropertyValueFactory<>("level"));
        tokenCol.setCellValueFactory(new PropertyValueFactory<>("tokenHView"));
        cardCol.setCellValueFactory(new PropertyValueFactory<>("cardView"));
        slotCol.setCellValueFactory(new PropertyValueFactory<>("slot"));
        setCol.setCellValueFactory(new PropertyValueFactory<>("set"));
        
        EnumFilter<Rarity>rarityFilter=new EnumFilter<>(Rarity.class);
        EnumFilter<Item.Token.Pair>tokenFilter=new EnumFilter<>(Item.Token.Pair.class);
        EnumFilter<Item.Slot>slotFilter=new EnumFilter<>(Item.Slot.class);
        EnumFilter<Set>setFilter=new EnumFilter<>(Set.class);
        TextField nameFilter=new TextField();
        InvalidationListener listener=observable->filter.setPredicate(item->
                rarityFilter.set.contains(item.rarity)
                &&tokenFilter.set.contains(Item.Token.Pair.get(item.token1,item.token2))
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
        ObservableList<TableColumn<Item,?>>columns=getColumns();
        
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