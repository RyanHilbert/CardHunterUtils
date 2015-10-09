package card.hunter.fx;

import app.App;
import card.hunter.Build;
import card.hunter.Character;
import card.hunter.Hoard;
import card.hunter.Party;
import card.hunter.Race;
import card.hunter.Role;
import card.hunter.Token;
import card.hunter.collectible.Equipment;
import utils.FileUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.function.Consumer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
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
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

//class responsible for building and displaying parties and their decks
public class PartyView extends VBox{
    private final CharPane pane1=new CharPane(), pane2=new CharPane(), pane3=new CharPane();
    private final Clipboard clipboard=Clipboard.getSystemClipboard();
    private String characterTemplate = "", itemTemplate = "";

    public Consumer<Slot>onSlotClick=slot->{};
    public Consumer<Slot>onSlotRightClick=slot->{};
    public PartyView(){
        MenuItem viewCards=new MenuItem("View All Card Art");
        viewCards.setOnAction(event -> {
            ModalDialog.show(getStage(), "CardPane", new CardPane());
        });
        MenuItem viewHtmlCards=new MenuItem("View HTML Card Art");
        viewHtmlCards.setOnAction(event -> {
           new CardViewer().show(getStage()); 
        });
        
        CheckMenuItem openExternally=new CheckMenuItem("Open card view in external browser");
        openExternally.setOnAction(event -> {
           App.state().openExternally = openExternally.isSelected();
        });
        
        CheckMenuItem compareCardArt = new CheckMenuItem("Compare card art with wiki images");
        compareCardArt.setOnAction(event -> {
            App.state().compareCardArt = compareCardArt.isSelected();
        });
        
        MenuItem copy=new MenuItem("Copy Party to Clipboard");
        MenuItem paste=new MenuItem("Paste Party from Clipboard");
        copy.setAccelerator(new KeyCharacterCombination("C",KeyCombination.SHORTCUT_DOWN));
        paste.setAccelerator(new KeyCharacterCombination("V",KeyCombination.SHORTCUT_DOWN));
        copy.setOnAction(event->{
            ClipboardContent content = new ClipboardContent();
            
            content.putString(getCurrentPartyBBCode());
            clipboard.setContent(content);
        });
        paste.setOnAction(event->{
            setCurrentParty(clipboard.getString());
        });
        
        MenuItem pasteHoard=new MenuItem("Paste Hoard from Clipboard");
        pasteHoard.setOnAction(event->{
            Hoard.load(clipboard.getString());
        });

        MenuBar menu=new MenuBar(buildFileMenu(),new Menu("View",null,viewCards,viewHtmlCards,openExternally,compareCardArt),new Menu("Edit",null,copy,paste,pasteHoard));
        ScrollPane scroll=new ScrollPane(new VBox(pane1,pane2,pane3));
        scroll.setHbarPolicy(ScrollBarPolicy.NEVER);
        getChildren().addAll(menu,scroll);
    }

    private final Menu buildFileMenu(){
        MenuItem load=new MenuItem("Load party...");
        MenuItem save=new MenuItem("Save party as...");
        load.setAccelerator(new KeyCharacterCombination("O",KeyCombination.SHORTCUT_DOWN));
        save.setAccelerator(new KeyCharacterCombination("S",KeyCombination.SHORTCUT_DOWN));
        load.setOnAction(event -> {
            FileChooser fileChooser=new FileChooser();
            fileChooser.setTitle("Choose party file...");
            fileChooser.setInitialDirectory(
                new File(Paths.get(System.getProperty("user.dir"),"saved","parties").toString())
            );
            loadPartyFrom(fileChooser.showOpenDialog(getStage()));
        });
        save.setOnAction(event -> {
            FileChooser fileChooser=new FileChooser();
            fileChooser.setTitle("Choose party file...");
            fileChooser.setInitialDirectory(
                new File(Paths.get(System.getProperty("user.dir"),"saved","parties").toString())
            );
            savePartyTo(fileChooser.showSaveDialog(getStage()));
        });

        return new Menu("File",null,load,save);
    }

    private final Stage getStage(){
        return (Stage) this.getScene().getWindow();
    }
    
    private final String getCurrentPartyBBCode() {
        try {
            if (characterTemplate.isEmpty())
                characterTemplate = FileUtils.textFromFile("templates/character.bbcode");
        }
        catch (FileNotFoundException ex) {
            System.err.println("Uh oh - the character bbcode template wasn't there.");
            characterTemplate = "Uh oh - the character bbcode template wasn't there.";
        }

        try {
            if (itemTemplate.isEmpty())
                itemTemplate = FileUtils.textFromFile("templates/item.bbcode");
        }
        catch (FileNotFoundException ex) {
            System.err.println("Uh oh - the item bbcode template wasn't there.");
            itemTemplate = "Uh oh - the item bbcode template wasn't there.";
        }
        
        String partyBBCode = "";
        for (Character c : getChars()) {
            partyBBCode += String.format("%s%n%n", c.toBBCode(characterTemplate, itemTemplate));
        }

        return partyBBCode;
    }
    
    private final void setCurrentParty(String bbCode) {
        ArrayList<Character> chars=Character.allFromBBCode(bbCode);

        setChars(chars);
    }

    private final void setChars(ArrayList<Character> chars){
        if(chars.size()>0){
            pane1.setChar(chars.get(0));
        }

        if(chars.size()>1){
            pane2.setChar(chars.get(1));
        }

        if(chars.size()>2){
            pane3.setChar(chars.get(2));
        }
    }

    private final ArrayList<Character> getChars(){
        ArrayList<Character> chars=new ArrayList<Character>();

        CharPane[] panes=new CharPane[]{pane1,pane2,pane3};
        for(CharPane p : panes){
            CharPane.Char c=p.getChar();
            String n=c.getName();
            if(!(n==null||n.isEmpty())){
                chars.add(c.asCharacter());
            }
        }
        
        return chars;
    }
    
    public final void loadPartyFrom(File file) {
        if(file!=null)
            setCurrentParty(FileUtils.textFromFile(file));
    }
    
    public final void savePartyTo(File file) {
        if(file!=null)
            FileUtils.writeFile(file, getCurrentPartyBBCode());
    }
    
    //class responsible for representing a single equipped item
    public class Slot extends HBox{
        public static final byte width=82,height=70;
        private final EnumSet<card.hunter.Slot>set;
        private Token.Pair.View tokens;
        public final ObjectProperty<Equipment>itemProperty(){return itemProperty;}
        public final Equipment getItem(){return itemProperty.get();}
        public final void setItem(Equipment item){
            if(!isEmpty()&&!isDefault()){
                Party.remove(itemProperty.get());
            }
            itemProperty.set(item);
            Party.add(item);
            
            App.state().refresh();
        }

        public final boolean isHolding(Equipment item){
            return (itemProperty.get()==item);
        }

        public final boolean isEmpty() {
            return itemProperty.get() == null;
        }
        
        public final boolean isDefault() {
            return itemProperty.get() == set.iterator().next().getDefaultEquipment();
        }
        
        public final void empty(){
            setItem(set.iterator().next().getDefaultEquipment());
        }
        public final ObjectProperty<Equipment>itemProperty=new SimpleObjectProperty<Equipment>(){
            @Override public void setValue(Equipment item){set(item);}
            @Override public void set(Equipment item){
                if(set.contains(item.slot))super.set(item);
            }
        };
        public Slot(Equipment defaultItem,card.hunter.Slot...slots){this(defaultItem.slot.toString().replace(' ','\n'),defaultItem,slots);}
        public Slot(String string,Equipment defaultItem,card.hunter.Slot...slots){
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
            button.setOnMouseClicked(event-> { 
                if(MouseHelper.isRightClick(event)) 
                    onSlotRightClick.accept(this); 
                else onSlotClick.accept(this);
            } );
            view.setOnMouseClicked(event-> { 
                if(MouseHelper.isRightClick(event)) 
                    onSlotRightClick.accept(this); 
                else onSlotClick.accept(this);
            } );
            itemProperty.addListener((observable,oldValue,newValue)->{
                view.setImage(newValue.icon);
                getChildren().remove(tokens);
                tokens=(Token.Pair.View)newValue.getTokenView();
                getChildren().add((Node)tokens);
            });
        }
    }
    
    //internal class used to represent a selection of three characters as Tabs
    private class CharPane extends TabPane{
        public final Char warrior,priest,wizard;
        public Char getChar(){return(Char)getSelectionModel().getSelectedItem();};
        public CharPane(){
            warrior=new Char("Warrior",null,
                    new Slot(card.hunter.Slot.Weapon.getDefaultEquipment()),
                    new Slot(card.hunter.Slot.Weapon.getDefaultEquipment()),
                    null,
                    new Slot(card.hunter.Slot.Helmet.getDefaultEquipment()),
                    new Slot(card.hunter.Slot.Weapon.getDefaultEquipment()),
                    new Slot(card.hunter.Slot.Shield.getDefaultEquipment()),
                    null,null,
                    new Slot(card.hunter.Slot.Heavy_Armor.getDefaultEquipment()),
                    new Slot(card.hunter.Slot.Boots.getDefaultEquipment()),
                    null,null,null,
                    new Slot("Racial\nSkill",card.hunter.Slot.Dwarf_Skill.getDefaultEquipment(),card.hunter.Slot.Elf_Skill,card.hunter.Slot.Human_Skill),
                    new Slot(card.hunter.Slot.Martial_Skill.getDefaultEquipment())
            );
            priest=new Char("Priest",null,
                    new Slot(card.hunter.Slot.Divine_Weapon.getDefaultEquipment()),
                    new Slot(card.hunter.Slot.Divine_Weapon.getDefaultEquipment()),
                    null,
                    new Slot(card.hunter.Slot.Shield.getDefaultEquipment()),
                    new Slot(card.hunter.Slot.Divine_Armor.getDefaultEquipment()),
                    new Slot(card.hunter.Slot.Boots.getDefaultEquipment()),
                    null,
                    new Slot(card.hunter.Slot.Divine_Item.getDefaultEquipment()),
                    new Slot(card.hunter.Slot.Divine_Item.getDefaultEquipment()),
                    new Slot(card.hunter.Slot.Divine_Item.getDefaultEquipment()),
                    null,null,null,
                    new Slot("Racial\nSkill",card.hunter.Slot.Dwarf_Skill.getDefaultEquipment(),card.hunter.Slot.Elf_Skill,card.hunter.Slot.Human_Skill),
                    new Slot(card.hunter.Slot.Divine_Skill.getDefaultEquipment())
            );
            wizard=new Char("Wizard",
                    new Slot(card.hunter.Slot.Staff.getDefaultEquipment()),
                    new Slot(card.hunter.Slot.Staff.getDefaultEquipment()),
                    null,
                    new Slot(card.hunter.Slot.Arcane_Item.getDefaultEquipment()),
                    new Slot(card.hunter.Slot.Arcane_Item.getDefaultEquipment()),
                    new Slot(card.hunter.Slot.Arcane_Item.getDefaultEquipment()),
                    null,
                    new Slot(card.hunter.Slot.Arcane_Item.getDefaultEquipment()),
                    new Slot(card.hunter.Slot.Robes.getDefaultEquipment()),
                    new Slot(card.hunter.Slot.Boots.getDefaultEquipment()),
                    null,null,null,
                    new Slot("Racial\nSkill",card.hunter.Slot.Dwarf_Skill.getDefaultEquipment(),card.hunter.Slot.Elf_Skill,card.hunter.Slot.Human_Skill),
                    new Slot(card.hunter.Slot.Arcane_Skill.getDefaultEquipment())
            );
            setMaxHeight(265);
            setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
            getTabs().addAll(warrior,priest,wizard);
        }

        public void setChar(Character c){
            Char target=null;
            switch(c.role){
                default:
                case Warrior:target=warrior;break;
                case Priest:target=priest;break;
                case Wizard:target=wizard;break;
            }

            target.Load(c);
            this.getSelectionModel().select(target);
        }
        //internal internal class used to represent a single character as a Tab
        private class Char extends Tab{
            public final StringProperty nameProperty;
            public final StringProperty nameProperty(){return nameProperty;}
            public final String getName(){return nameProperty.get();}
            public final void setName(String name){
                nameProperty.set(name);
            }

            public final ReadOnlyObjectProperty<Integer> levelProperty;

            public final ReadOnlyObjectProperty<Integer> levelProperty(){
                return levelProperty;
            }

            public final Integer getLevel(){
                return levelProperty.get();
            }

            private final Slot[] itemSlots;

            public Char(final String name,final Slot... slots){
                super(name);
                itemSlots=slots;

                GridPane grid=new GridPane();
                boolean newColumn=true;
                Label minorLabel=new Label("4",Token.Minor.getView()),majorLabel=new Label("4",Token.Major.getView());
                for(int i=0,column=0,row=0;i<slots.length;++i){
                    if(slots[i]==null){
                        if(newColumn)grid.add(new Rectangle(Slot.width,Slot.height/2,Color.TRANSPARENT),column,row++);
                        else{++column;row=0;newColumn=true;}
                    }else{
                        grid.add(slots[i],column,row,1,2);
                        newColumn=false;
                        row+=2;
                        slots[i].itemProperty.addListener((observable,oldValue,newValue)->{
                            final Token oldToken1=oldValue.token1,oldToken2=oldValue.token2,newToken1=newValue.token1,newToken2=newValue.token2;
                            byte minorTotal=Byte.parseByte(minorLabel.getText()),majorTotal=Byte.parseByte(majorLabel.getText());
                            if(oldToken1==Token.Minor)++minorTotal;
                            else if(oldToken1==Token.Major)++majorTotal;
                            if(oldToken2==Token.Minor)++minorTotal;
                            else if(oldToken2==Token.Major)++majorTotal;
                            if(newToken1==Token.Minor)--minorTotal;
                            else if(newToken1==Token.Major)--majorTotal;
                            if(newToken2==Token.Minor)--minorTotal;
                            else if(newToken2==Token.Major)--majorTotal;
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
                (this.levelProperty=levelSpinner.valueProperty()).addListener((observable,oldValue,newValue) -> {
                    final int minorTotal=99; //Byte.parseByte(minorLabel.getText())+Token.Minor.getAmountAtLevel(newValue)-Token.Minor.getAmountAtLevel(oldValue);
                    final int majorTotal=99; //Byte.parseByte(majorLabel.getText())+Token.Major.getAmountAtLevel(newValue)-Token.Major.getAmountAtLevel(oldValue);
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

            private void Equip(Build equipment){
                if(this.levelProperty.get()<equipment.minLevel){
                    // todo: update level?  no-can-do message to user?
                }

                System.out.format("Cloning equipment: %s\n",equipment.items.toString());
                ArrayList<Equipment> copy=(ArrayList<Equipment>) equipment.items.clone();

                System.out.format("Equipping slots...\n");
                for(Slot s : itemSlots){
                    if(s!=null){
                        System.out.format("\tTrying to fill a %s slot...\n",s.set.toArray()[0].toString());
                        Equipment item=null;
                        for(Equipment i : copy){
                            System.out.format("\t\tConsidering %s, which is a %s...\n",i.name,i.slot.toString());
                            if(s.set.contains(i.slot)){
                                System.out.format("\t\tThat's a match!\n");
                                item=i;
                                break;
                            }
                        }

                        if(item!=null){
                            copy.remove(item);
                            System.out.format("\tTook %s from the clone pile, now what's left is: %s\n",item.name,copy.toString());
                            if(s.getItem()!=null){
                                System.out.format("\t\tReplacing %s with %s...\n",s.getItem().name,item.name);
                            }

                            s.setItem(item);
                            System.out.format("\tOk, there's your %s!\n",item.name);
                        }
                    }
                }
            }

            private void Load(Character c){
                System.out.format("\n\nLoading %s...\n--------\n",c.name);
                setName(c.name);

                Equip(c.equipment);
            }

            private Character asCharacter(){
                Character c=new Character(
                    this.getName(),this.getLevel(),
                    this.getRace(),this.getRole(),"todo: image",this.getItems()
                );

                return c;
            }

            private Race getRace(){
                // HACK: Racial is 2 from the end.
                Slot racialSlot=itemSlots[itemSlots.length-2];
                card.hunter.Slot type = racialSlot.getItem().slot;

                switch(type){
                    case Dwarf_Skill:
                        return Race.Dwarf;
                    case Elf_Skill:
                        return Race.Elf;
                    case Human_Skill:
                        return Race.Human;
                    default:
                        System.err.format("Weird - was looking at a racial skill slot, but got %s instead.", type);
                        return Race.Human;
                }
            }

            private Role getRole(){
                // HACK: Second slot always contains a weapon.
                Slot weaponSlot=itemSlots[1];
                card.hunter.Slot type = weaponSlot.getItem().slot;

                switch(type){
                    case Divine_Weapon:
                        return Role.Priest;
                    case Weapon:
                        return Role.Warrior;
                    case Staff:
                        return Role.Wizard;
                    default:
                        System.out.format("Weird - was looking at a weapon slot, but got %s instead.", type);
                        return Role.Warrior;
                }
            }

            private Equipment[] getItems(){
                ArrayList<Equipment> items = new ArrayList<Equipment>();
                
                for (Slot s : itemSlots)
                {
                   if (s != null && !s.isEmpty())
                   {
                       items.add(s.getItem());
                   }
                }
                
                return items.toArray(new Equipment[items.size()]);
            }
        }
    }
}
