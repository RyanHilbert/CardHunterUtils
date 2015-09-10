import utils.EnumFilter;
import app.App;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import ui.EnumFilter;
import models.Rarity;
import models.Set;
import models.Item;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import ui.IPersistViewState;
import utils.Formula;

public class ItemTable extends TableView<Item> implements IPersistViewState{
    private final static String COL_ORDER = "columnOrder";
    private final static String SORTED_COLUMNS = "sortedColumns";
    private final static String SORT_DIRECTIONS = "sortDirections";
    
    private final List<TableColumn<Item, ?>> orderedColumns = new ArrayList<>();
    private final List<TableColumn<Item, ?>> calculatedColumns = new ArrayList<>();
    private final List<EnumFilter> enumFilters = new ArrayList<>();
    private final List<TextField> textFilters = new ArrayList<>();
    
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
        TableColumn<Item,Integer> qtyCol=new TableColumn<>("Qty");
        TableColumn<Item,Integer> usedCol=new TableColumn<>("In Use");
        TableColumn<Item,Integer> damageCol=new TableColumn<>("Damage");
        for (String name : CalculatedProperties.getProps()) {
            TableColumn<Item, Integer> col = new TableColumn<>(name);
            col.setCellValueFactory(cell -> new ReadOnlyObjectWrapper(((Item) cell.getValue()).getCardProp(name)));
            TextField textFilter = new TextField();
            textFilter.setId(name + "Filter");
            //textFilters.add(textFilter);
            calculatedColumns.add(col);
        }
        
        iconCol.setCellValueFactory(new PropertyValueFactory<>("view"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        rarityCol.setCellValueFactory(new PropertyValueFactory<>("rarity"));
        levelCol.setCellValueFactory(new PropertyValueFactory<>("level"));
        tokenCol.setCellValueFactory(new PropertyValueFactory<>("tokenHView"));
        cardCol.setCellValueFactory(new PropertyValueFactory<>("cardView"));
        slotCol.setCellValueFactory(new PropertyValueFactory<>("slot"));
        setCol.setCellValueFactory(new PropertyValueFactory<>("set"));
        qtyCol.setCellValueFactory(cell -> new ReadOnlyObjectWrapper(((Item) cell.getValue()).getQuantity()));
        usedCol.setCellValueFactory(cell -> new ReadOnlyObjectWrapper(((Item) cell.getValue()).getNumInUse()));
        damageCol.setCellValueFactory(new PropertyValueFactory<>("totalDamage"));
        
        EnumFilter<Rarity>rarityFilter=new EnumFilter<>(Rarity.class);
        EnumFilter<Item.Token.Pair>tokenFilter=new EnumFilter<>(Item.Token.Pair.class);
        EnumFilter<Item.Slot>slotFilter=new EnumFilter<>(Item.Slot.class);
        EnumFilter<Set>setFilter=new EnumFilter<>(Set.class);
        TextField nameFilter=new TextField();
        nameFilter.setId("nameFilter");
        TextField qtyFilter=new TextField();
        qtyFilter.setId("qtyFilter");
        TextField usedFilter=new TextField();
        usedFilter.setId("usedFilter");
        TextField damageFilter = new TextField();
        damageFilter.setId("damageFilter");
        InvalidationListener listener=observable->filter.setPredicate(item->
            rarityFilter.set.contains(item.rarity)
            &&tokenFilter.set.contains(Item.Token.Pair.get(item.token1,item.token2))
            &&slotFilter.set.contains(item.slot)
            &&setFilter.set.contains(item.set)
            &&item.name.toLowerCase().contains(nameFilter.getText() != null ? nameFilter.getText().toLowerCase() : "")
            &&Formula.eval(qtyFilter.getText(),item.getQuantity())
            &&Formula.eval(usedFilter.getText(),item.getNumInUse())
            &&Formula.eval(damageFilter.getText(),item.totalDamage));
        rarityFilter.set.addListener(listener);
        tokenFilter.set.addListener(listener);
        slotFilter.set.addListener(listener);
        setFilter.set.addListener(listener);
        nameFilter.textProperty().addListener(listener);
        qtyFilter.textProperty().addListener(listener);
        usedFilter.textProperty().addListener(listener);
        damageFilter.textProperty().addListener(listener);
        
        nameCol.setContextMenu(new ContextMenu(new CustomMenuItem(nameFilter,false)));
        rarityCol.setContextMenu(new ContextMenu(new CustomMenuItem(rarityFilter,false)));
        tokenCol.setContextMenu(new ContextMenu(new CustomMenuItem(tokenFilter,false)));
        slotCol.setContextMenu(new ContextMenu(new CustomMenuItem(slotFilter,false)));
        setCol.setContextMenu(new ContextMenu(new CustomMenuItem(setFilter,false)));
        qtyCol.setContextMenu(new ContextMenu(new CustomMenuItem(qtyFilter,false)));
        usedCol.setContextMenu(new ContextMenu(new CustomMenuItem(usedFilter,false)));
        damageCol.setContextMenu(new ContextMenu(new CustomMenuItem(damageFilter, false)));
        
        enumFilters.add(rarityFilter);
        enumFilters.add(tokenFilter);
        enumFilters.add(slotFilter);
        enumFilters.add(setFilter);
        
        textFilters.add(nameFilter);
        textFilters.add(qtyFilter);
        textFilters.add(usedFilter);
        textFilters.add(damageFilter);
        
        iconCol.setSortable(false);
        cardCol.setSortable(false);
        ObservableList<TableColumn<Item,?>>columns=getColumns();

        orderedColumns.add(qtyCol);
        orderedColumns.add(iconCol);
        orderedColumns.add(nameCol);
        orderedColumns.add(usedCol);
        orderedColumns.add(rarityCol);
        orderedColumns.add(levelCol);
        orderedColumns.add(tokenCol);
        orderedColumns.add(cardCol);
        orderedColumns.add(slotCol);
        orderedColumns.add(setCol);
            if(MouseHelper.isRightClick(event) && !inHeader)
                new CardViewer(this.getSelectionModel().getSelectedItem()).show((Stage) this.getScene().getWindow());
        });
    }
    
            columns.add(c);
        final Node header = this.lookup("TableHeaderRow");
        if (header != null) {
            header.setOnMouseEntered(event-> { this.inHeader = true; });
            header.setOnMouseExited(event-> { this.inHeader = false; });
        }
        orderedColumns.add(damageCol);
        
        for (TableColumn<Item,?> c : calculatedColumns)
            orderedColumns.add(c);
        
        for (TableColumn<Item,?> c : orderedColumns)
            columns.add(c);
    }

    @Override
    public void refresh(){
        // lol, java... http://stackoverflow.com/questions/11065140/javafx-2-1-tableview-refresh-items
        TableColumn<Item,?> c=getColumns().get(0);
        boolean vis=c.isVisible();
        c.setVisible(!vis);
        c.setVisible(vis);
    }

    @Override
    public void updateViewState() {
        // <editor-fold defaultstate="collapsed" desc="column order and sort">
        ObservableList<TableColumn<Item, ?>> columns = getColumns();
        String[] colOrder = new String[columns.size()];
        ArrayList<String> sortCols = new ArrayList<>();
        ArrayList<String> sortDirs = new ArrayList<>();
        
        for (int i = 0; i < columns.size(); ++i) {
          colOrder[i] = orderedColumns.indexOf(columns.get(i)) + "";
        }
        
        for (TableColumn<Item, ?> c : getSortOrder()) {
            sortCols.add(c.getText());
            sortDirs.add(c.getSortType().toString());
        }
        App.state().viewState.saveControlSetting(CONTROL_NAME, COL_ORDER, String.join(",", colOrder));
        App.state().viewState.saveControlSetting(CONTROL_NAME, SORTED_COLUMNS, String.join(",", sortCols));
        App.state().viewState.saveControlSetting(CONTROL_NAME, SORT_DIRECTIONS, String.join(",", sortDirs));
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="filters">
        for (EnumFilter f : enumFilters)
            f.updateViewState();
        
        for (TextField f : textFilters)
            App.state().viewState.saveControlSetting(CONTROL_NAME, f.getId(), f.getText());
        // </editor-fold>
    }
    
    @Override
    public void setViewFromState() {
        // <editor-fold defaultstate="collapsed" desc="column order">
        HashMap<String, String> settings = App.state().viewState.getControlSettings(CONTROL_NAME);
        ObservableList<TableColumn<Item, ?>> columns = getColumns();
        
        String orderText = settings.get(COL_ORDER);
        if (orderText != null && !orderText.isEmpty()) {
            String[] orders = orderText.split(",");

            if (orders.length == columns.size()) {
                columns.clear();

                for (int ix = 0; ix < orders.length; ix++) {
                    columns.add(orderedColumns.get(Integer.parseInt(orders[ix], 10)));
                }
            }
        }
        
        String sortText = settings.get(SORTED_COLUMNS);
        if (sortText != null && !sortText.isEmpty()) {
            String[] sortCols = sortText.split(",");
            String[] sortDirs = settings.get(SORT_DIRECTIONS).split(",");

            List<TableColumn<Item, ?>> sortOrder = getSortOrder();
            sortOrder.clear();

            for (int ix = 0; ix < sortCols.length; ix++) {
                for (TableColumn<Item, ?> c : orderedColumns) {
                    if (c.getText().equals(sortCols[ix])) {
                        sortOrder.add(c);
                        c.setSortType(TableColumn.SortType.valueOf(sortDirs[ix]));
                    }
                }
            }
        }
            
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="filters">
        for (EnumFilter f : enumFilters)
            f.setViewFromState();
        
        for (TextField f : textFilters)
            f.setText(App.state().viewState.getControlSetting(CONTROL_NAME, f.getId()));
        // </editor-fold>
    }
}