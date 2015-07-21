import java.util.EnumSet;
import java.util.function.Consumer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

//class responsible for building and displaying parties and their decks
public class PartyView extends VBox{
    private final CharPane pane1=new CharPane(),pane2=new CharPane(),pane3=new CharPane();
    public Consumer<Slot>onSlotClick=slot->{};
    public PartyView(){
        MenuItem copy=new MenuItem("Copy Party to Clipboard");
        MenuItem paste=new MenuItem("Paste Party from Clipboard");
        copy.setAccelerator(new KeyCharacterCombination("C",KeyCombination.SHORTCUT_DOWN));
        paste.setAccelerator(new KeyCharacterCombination("V",KeyCombination.SHORTCUT_DOWN));
        copy.setOnAction(event->{
            //deck export code goes here
        });
        paste.setOnAction(event->{
            //deck import code goes here
        });
        MenuBar menu=new MenuBar(new Menu("Edit",null,copy,paste));
        ScrollPane scroll=new ScrollPane(new VBox(pane1,pane2,pane3));
        scroll.setHbarPolicy(ScrollBarPolicy.NEVER);
        getChildren().addAll(menu,scroll);
    }
    
    //class responsible for representing a single equipped item
    public class Slot extends HBox{
        public static final byte width=82,height=70;
        private final EnumSet<Item.Slot>set;
        private Item.Token.Pair.View tokens;
        public final ObjectProperty<Item>itemProperty(){return itemProperty;}
        public final Item getItem(){return itemProperty.get();}
        public final void setItem(Item item){itemProperty.set(item);}
        public final ObjectProperty<Item>itemProperty=new SimpleObjectProperty<Item>(){
            @Override public void setValue(Item item){set(item);}
            @Override public void set(Item item){
                if(itemProperty.get()==item)super.set(set.iterator().next().dfault);
                else if(set.contains(item.slot))super.set(item);
            }
        };
        public Slot(Item defaultItem,Item.Slot...slots){this(defaultItem.slot.toString().replace(' ','\n'),defaultItem,slots);}
        public Slot(String string,Item defaultItem,Item.Slot...slots){
            set=EnumSet.of(defaultItem.slot,slots);
            itemProperty.set(defaultItem);
            setMinSize(Slot.width,Slot.height);
            setMaxSize(Slot.width,Slot.height);
            Button button=new Button(string);
            button.setMinSize(61,61);
            button.setMaxSize(61,61);
            ImageView view=new ImageView(defaultItem.icon);
            StackPane stack=new StackPane(button,view);
            getChildren().add(stack);
            button.setOnMouseClicked(event->onSlotClick.accept(this));
            view.setOnMouseClicked(event->onSlotClick.accept(this));
            itemProperty.addListener((observable,oldValue,newValue)->{
                view.setImage(newValue.icon);
                getChildren().remove(tokens);
                getChildren().add(tokens=newValue.getTokenView());
            });
        }
    }
    
    //internal class used to represent a selection of three characters as Tabs
    private class CharPane extends TabPane{
        public final Char warrior,priest,wizard;
        public Char getChar(){return(Char)getSelectionModel().getSelectedItem();};
        public CharPane(){
            warrior=new Char("Warrior",null,
                    new Slot(Item.Slot.Weapon.dfault),
                    new Slot(Item.Slot.Weapon.dfault),
                    null,
                    new Slot(Item.Slot.Helmet.dfault),
                    new Slot(Item.Slot.Weapon.dfault),
                    new Slot(Item.Slot.Shield.dfault),
                    null,null,
                    new Slot(Item.Slot.Heavy_Armor.dfault),
                    new Slot(Item.Slot.Boots.dfault),
                    null,null,null,
                    new Slot("Racial\nSkill",Item.Slot.Dwarf_Skill.dfault,Item.Slot.Elf_Skill,Item.Slot.Human_Skill),
                    new Slot(Item.Slot.Martial_Skill.dfault)
            );
            priest=new Char("Priest",null,
                    new Slot(Item.Slot.Divine_Weapon.dfault),
                    new Slot(Item.Slot.Divine_Weapon.dfault),
                    null,
                    new Slot(Item.Slot.Shield.dfault),
                    new Slot(Item.Slot.Divine_Armor.dfault),
                    new Slot(Item.Slot.Boots.dfault),
                    null,
                    new Slot(Item.Slot.Divine_Item.dfault),
                    new Slot(Item.Slot.Divine_Item.dfault),
                    new Slot(Item.Slot.Divine_Item.dfault),
                    null,null,null,
                    new Slot("Racial\nSkill",Item.Slot.Dwarf_Skill.dfault,Item.Slot.Elf_Skill,Item.Slot.Human_Skill),
                    new Slot(Item.Slot.Divine_Skill.dfault)
            );
            wizard=new Char("Wizard",
                    new Slot(Item.Slot.Staff.dfault),
                    new Slot(Item.Slot.Staff.dfault),
                    null,
                    new Slot(Item.Slot.Arcane_Item.dfault),
                    new Slot(Item.Slot.Arcane_Item.dfault),
                    new Slot(Item.Slot.Arcane_Item.dfault),
                    null,
                    new Slot(Item.Slot.Arcane_Item.dfault),
                    new Slot(Item.Slot.Robes.dfault),
                    new Slot(Item.Slot.Boots.dfault),
                    null,null,null,
                    new Slot("Racial\nSkill",Item.Slot.Dwarf_Skill.dfault,Item.Slot.Elf_Skill,Item.Slot.Human_Skill),
                    new Slot(Item.Slot.Arcane_Skill.dfault)
            );
            setMaxHeight(265);
            setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
            getTabs().addAll(warrior,priest,wizard);
        }
        
        //internal internal class used to represent a single character as a Tab
        private class Char extends Tab{
            public final StringProperty nameProperty;
            public final StringProperty nameProperty(){return nameProperty;}
            public final String getName(){return nameProperty.get();}
            public final void setName(String name){nameProperty.set(name);}
            public Char(final String name,final Slot...slots){
                super(name);
                GridPane grid=new GridPane();
                boolean newColumn=true;
                Label minorLabel=new Label("4",Item.Token.Minor.getView()),majorLabel=new Label("4",Item.Token.Major.getView());
                for(int i=0,column=0,row=0;i<slots.length;++i){
                    if(slots[i]==null){
                        if(newColumn)grid.add(new Rectangle(Slot.width,Slot.height/2,Color.TRANSPARENT),column,row++);
                        else{++column;row=0;newColumn=true;}
                    }else{
                        grid.add(slots[i],column,row,1,2);
                        newColumn=false;
                        row+=2;
                        slots[i].itemProperty.addListener((observable,oldValue,newValue)->{
                            final Item.Token oldToken1=oldValue.token1,oldToken2=oldValue.token2,newToken1=newValue.token1,newToken2=newValue.token2;
                            byte minorTotal=Byte.parseByte(minorLabel.getText()),majorTotal=Byte.parseByte(majorLabel.getText());
                            if(oldToken1==Item.Token.Minor)++minorTotal;
                            else if(oldToken1==Item.Token.Major)++majorTotal;
                            if(oldToken2==Item.Token.Minor)++minorTotal;
                            else if(oldToken2==Item.Token.Major)++majorTotal;
                            if(newToken1==Item.Token.Minor)--minorTotal;
                            else if(newToken1==Item.Token.Major)--majorTotal;
                            if(newToken2==Item.Token.Minor)--minorTotal;
                            else if(newToken2==Item.Token.Major)--majorTotal;
                            minorLabel.setText(String.valueOf(minorTotal));
                            majorLabel.setText(String.valueOf(majorTotal));
                            if(majorTotal<0||majorTotal+minorTotal<0){
                                minorLabel.setTextFill(Color.RED);
                                majorLabel.setTextFill(Color.RED);
                            }else{
                                minorLabel.setTextFill(Color.BLACK);
                                majorLabel.setTextFill(Color.BLACK);
                            }
                        });
                    }
                }
                Spinner<Integer>levelSpinner=new Spinner<>(6,24,18);
                levelSpinner.setMaxWidth(52);
                levelSpinner.valueProperty().addListener((observable,oldValue,newValue)->{
                    final int minorTotal=Byte.parseByte(minorLabel.getText())+Item.Token.Minor.getAmountAtLevel(newValue)-Item.Token.Minor.getAmountAtLevel(oldValue);
                    final int majorTotal=Byte.parseByte(majorLabel.getText())+Item.Token.Major.getAmountAtLevel(newValue)-Item.Token.Major.getAmountAtLevel(oldValue);
                    minorLabel.setText(String.valueOf(minorTotal));
                    majorLabel.setText(String.valueOf(majorTotal));
                    if(majorTotal<0||minorTotal+majorTotal<0){
                        minorLabel.setTextFill(Color.RED);
                        majorLabel.setTextFill(Color.RED);
                    }else{
                        minorLabel.setTextFill(Color.BLACK);
                        majorLabel.setTextFill(Color.BLACK);
                    }
                });
                TextField nameField=new TextField();
                HBox.setHgrow(nameField,Priority.ALWAYS);
                (this.nameProperty=nameField.textProperty()).addListener((observable,oldValue,newValue)->{
                    if(newValue.length()>25)nameProperty.set(newValue.substring(0,25));
                });
                nameField.setPromptText("Character Name (Optional)");
                setContent(new VBox(new HBox(nameField,minorLabel,majorLabel,levelSpinner),grid));
            }
        }
    }
}
