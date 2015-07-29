
import java.util.Timer;
import java.util.TimerTask;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.Priority;
import javafx.stage.*;

public class ModalDialog{
    
    public final static void show(Stage parent, String title, ScrollPane dialogContent){
        show(parent, title, dialogContent, 1600, 400);
    }
    
    public final static void show(Stage parent, String title, ScrollPane dialogContent, int width, int height)
    {
        Dialog dialog=new Dialog();
        dialog.setTitle(title);
        dialog.setHeaderText(title);
        dialog.setResizable(true);

        GridPane grid=new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(3);
        grid.add(dialogContent.getContent(),0,0,3,1);

        ColumnConstraints columnConstraints=new ColumnConstraints();
        columnConstraints.setFillWidth(true);
        columnConstraints.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().add(columnConstraints);

        dialog.getDialogPane().setContent(grid);

        ButtonType close=new ButtonType("Close",ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(close);

        parent.getScene().getRoot().setEffect(new BoxBlur());
        dialog.setOnCloseRequest(event -> {
            parent.getScene().getRoot().setEffect(null);
        });

        dialog.show();
    }
}
