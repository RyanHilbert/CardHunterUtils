package card.hunter.fx;

import app.App;
import card.hunter.collectible.Equipment;
import card.hunter.CalculatedProperties;
import card.hunter.Rarity;
import card.hunter.Set;
import card.hunter.Slot;
import card.hunter.Token;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.InvalidationListener;
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
import javafx.stage.Stage;
import utils.Formula;

public class ItemTable extends TableView<Equipment> implements IPersistViewState{
    private boolean inHeader = false;
	
    private final static String CONTROL_NAME = "ITEM_TABLE";
    private final static String COL_ORDER = "columnOrder";
    private final static String SORTED_COLUMNS = "sortedColumns";
    private final static String SORT_DIRECTIONS = "sortDirections";
    
    private final List<TableColumn<Equipment, ?>> orderedColumns = new ArrayList<>();
    private final List<TableColumn<Equipment, ?>> calculatedColumns = new ArrayList<>();
    private final HashMap<EnumFilter, TableColumn<Equipment, ?>> enumFilters = new HashMap<>();
    private final List<TextField> textFilters = new ArrayList<>();
    private final HashMap<CalculatedProperties.Names, TextField> calculatedFilters = new HashMap<>();
    
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

        for (CalculatedProperties.Names name : CalculatedProperties.Names.values()) {
            TableColumn<Equipment, Integer> col = new TableColumn<>(name.toString());
            col.setCellValueFactory(cell -> new ReadOnlyObjectWrapper(((Equipment) cell.getValue()).getCardProp(name)));
            TextField textFilter = new TextField();
            textFilter.setId(name + "Filter");
            calculatedFilters.put(name, textFilter);
            calculatedColumns.add(col);
            col.setContextMenu(new ContextMenu(new CustomMenuItem(textFilter,false)));
            
            InvalidationListener listener=observable-> {
                String f = textFilter.getText();
                boolean isSet = (f != null && !filter.isEmpty());
                
                col.setStyle(isSet ? "-fx-background-color: #DFD; -fx-text-fill: green; -fx-font-weight: bold;" : "");
            };
            
            textFilter.textProperty().addListener(listener);
        }
        
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
        nameFilter.setId("nameFilter");
        TextField qtyFilter=new TextField();
        qtyFilter.setId("qtyFilter");
        TextField usedFilter=new TextField();
        usedFilter.setId("usedFilter");
        TextField damageFilter = new TextField();
        damageFilter.setId("damageFilter");
        
        InvalidationListener nameListener=observable-> {
                String f = nameFilter.getText();
                boolean isSet = (f != null && !f.isEmpty());
                
                nameCol.setStyle(isSet ? "-fx-background-color: #DFD; -fx-text-fill: green; -fx-font-weight: bold;" : "");
            };
        nameFilter.textProperty().addListener(nameListener);
        
        InvalidationListener listener=observable->filter.setPredicate(item->
            rarityFilter.set.contains(item.rarity)
                &&tokenFilter.set.contains(Token.Pair.valueOf(item.token1,item.token2))
            &&slotFilter.set.contains(item.slot)
            &&setFilter.set.contains(item.set)
            &&item.name.toLowerCase().contains(nameFilter.getText() != null ? nameFilter.getText().toLowerCase() : "")
            &&evalCalculatedFilters(item));
        rarityFilter.set.addListener(listener);
        tokenFilter.set.addListener(listener);
        slotFilter.set.addListener(listener);
        setFilter.set.addListener(listener);
        nameFilter.textProperty().addListener(listener);
        qtyFilter.textProperty().addListener(listener);
        usedFilter.textProperty().addListener(listener);
        damageFilter.textProperty().addListener(listener);
        for (TextField field : calculatedFilters.values())
            field.textProperty().addListener(listener);
        
        nameCol.setContextMenu(new ContextMenu(new CustomMenuItem(nameFilter,false)));
        rarityCol.setContextMenu(new ContextMenu(new CustomMenuItem(rarityFilter,false)));
        tokenCol.setContextMenu(new ContextMenu(new CustomMenuItem(tokenFilter,false)));
        slotCol.setContextMenu(new ContextMenu(new CustomMenuItem(slotFilter,false)));
        setCol.setContextMenu(new ContextMenu(new CustomMenuItem(setFilter,false)));
        
        enumFilters.put(rarityFilter, rarityCol);
        enumFilters.put(tokenFilter, tokenCol);
        enumFilters.put(slotFilter, tokenCol);
        enumFilters.put(setFilter, setCol);
        
        enumFilters.keySet().stream().forEach((f) -> {
            TableColumn<Equipment, ?> col = enumFilters.get(f);
            
            InvalidationListener enumListener=(observable)-> {
                col.setStyle(f.isSet() ? "-fx-background-color: #DFD; -fx-text-fill: green; -fx-font-weight: bold;" : "");
            };
            
            f.set.addListener(enumListener);
        });
        
        textFilters.add(nameFilter);
        textFilters.add(qtyFilter);
        textFilters.add(usedFilter);
        textFilters.add(damageFilter);
        
        iconCol.setSortable(false);
        cardCol.setSortable(false);
        ObservableList<TableColumn<Equipment,?>>columns=getColumns();

        // TODO: figure out a way to statically order the calculated prop columns
        orderedColumns.add(iconCol);
        orderedColumns.add(nameCol);
        orderedColumns.add(rarityCol);
        orderedColumns.add(levelCol);
        orderedColumns.add(tokenCol);
        orderedColumns.add(cardCol);
        orderedColumns.add(slotCol);
        orderedColumns.add(setCol);
        
        for (TableColumn<Equipment,?> c : calculatedColumns)
            orderedColumns.add(c);
        
        for (TableColumn<Equipment,?> c : orderedColumns)
            columns.add(c);
			
        setOnMouseClicked(event->{
            if(MouseHelper.isRightClick(event) && !inHeader)
                new CardViewer(this.getSelectionModel().getSelectedItem()).show((Stage) this.getScene().getWindow());
        });
    }
        
    public void onShow() {
        // HACK: poor man's event suppression
        final Node header = this.lookup("TableHeaderRow");
        if (header != null) {
            header.setOnMouseEntered(event-> { this.inHeader = true; });
            header.setOnMouseExited(event-> { this.inHeader = false; });
        }
    }

    @Override
    public void refresh(){
        Equipment.CalculateStateDependentItemProps();
        
        // lol, java... http://stackoverflow.com/questions/11065140/javafx-2-1-tableview-refresh-items
        TableColumn<Equipment,?> c=getColumns().get(0);
        boolean vis=c.isVisible();
        c.setVisible(!vis);
        c.setVisible(vis);
    }

    @Override
    public void updateViewState() {
        // <editor-fold defaultstate="collapsed" desc="column order and sort">
        ObservableList<TableColumn<Equipment, ?>> columns = getColumns();
        String[] colOrder = new String[columns.size()];
        ArrayList<String> sortCols = new ArrayList<>();
        ArrayList<String> sortDirs = new ArrayList<>();
        
        for (int i = 0; i < columns.size(); ++i) {
          colOrder[i] = orderedColumns.indexOf(columns.get(i)) + "";
        }
        
        for (TableColumn<Equipment, ?> c : getSortOrder()) {
            sortCols.add(c.getText());
            sortDirs.add(c.getSortType().toString());
        }
        App.state().viewState.saveControlSetting(CONTROL_NAME, COL_ORDER, String.join(",", colOrder));
        App.state().viewState.saveControlSetting(CONTROL_NAME, SORTED_COLUMNS, String.join(",", sortCols));
        App.state().viewState.saveControlSetting(CONTROL_NAME, SORT_DIRECTIONS, String.join(",", sortDirs));
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="filters">
        for (EnumFilter f : enumFilters.keySet())
            f.updateViewState();
        
        for (TextField f : textFilters)
            App.state().viewState.saveControlSetting(CONTROL_NAME, f.getId(), f.getText());
        
        for (TextField f : calculatedFilters.values())
            App.state().viewState.saveControlSetting(CONTROL_NAME, f.getId(), f.getText());
        // </editor-fold>
    }
    
    @Override
    public void setViewFromState() {
        // <editor-fold defaultstate="collapsed" desc="column order">
        HashMap<String, String> settings = App.state().viewState.getControlSettings(CONTROL_NAME);
        ObservableList<TableColumn<Equipment, ?>> columns = getColumns();
        
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

            List<TableColumn<Equipment, ?>> sortOrder = getSortOrder();
            sortOrder.clear();

            for (int ix = 0; ix < sortCols.length; ix++) {
                for (TableColumn<Equipment, ?> c : orderedColumns) {
                    if (c.getText().equals(sortCols[ix])) {
                        sortOrder.add(c);
                        c.setSortType(TableColumn.SortType.valueOf(sortDirs[ix]));
                    }
                }
            }
        }
            
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="filters">
        for (EnumFilter f : enumFilters.keySet())
            f.setViewFromState();
        
        for (TextField f : textFilters)
            f.setText(App.state().viewState.getControlSetting(CONTROL_NAME, f.getId()));
        
        for (TextField f : calculatedFilters.values())
            f.setText(App.state().viewState.getControlSetting(CONTROL_NAME, f.getId()));
        // </editor-fold>
    }

    private boolean evalCalculatedFilters(Equipment item) {
        // TODO: write a multi-field eval so we don't spin up Formula.eval() in a loop
        for (CalculatedProperties.Names name : calculatedFilters.keySet()) {
            TextField field = calculatedFilters.get(name);
            String filter = field.getText();
            if (filter != null && !filter.isEmpty()) {
                int val = item.getCardProp(name);
                if (!Formula.eval(filter, val))
                    return false;
            }
        }
        
        return true;
    }
}