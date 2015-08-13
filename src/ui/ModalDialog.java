import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.*;

public class ModalDialog{

    public final static void show(Stage parent,ScrollPane dialogContent){
        final Stage dialog=new Stage(StageStyle.TRANSPARENT);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(parent);

        GridPane grid=new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(3);

        grid.add(dialogContent,0,0,3,1);
        HBox buttons=buildButtons(parent,dialog);
        buttons.setAlignment(Pos.BOTTOM_RIGHT);
        grid.add(buttons,2,1);

        ColumnConstraints columnConstraints=new ColumnConstraints();
        columnConstraints.setFillWidth(true);
        columnConstraints.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().add(columnConstraints);

        dialog.setScene(new Scene(grid, //800,600));
             Math.min(grid.widthProperty().intValue(),800),
             Math.min(grid.heightProperty().intValue(),600))
        );

        setMouseEvents(dialog);

        parent.getScene().getRoot().setEffect(new BoxBlur());
        dialog.show();
    }

    public static final HBox buildButtons(Stage parent,Stage dialog){
        return HBoxBuilder.create().children(
            ButtonBuilder.create().text("Close").defaultButton(true).onAction(
                new EventHandler<ActionEvent>(){
                    @Override
                    public void handle(ActionEvent actionEvent){
                        parent.getScene().getRoot().setEffect(null);
                        dialog.close();
                    }
                }).build()
        ).build();
    }

    public static final void setMouseEvents(Stage dialog){
        // allow the dialog to be dragged around.
        final Node root=dialog.getScene().getRoot();
        final Delta dragDelta=new Delta();
        root.setOnMousePressed(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent mouseEvent){
                // record a delta distance for the drag and drop operation.
                dragDelta.x=dialog.getX()-mouseEvent.getScreenX();
                dragDelta.y=dialog.getY()-mouseEvent.getScreenY();
            }
        });

        root.setOnMouseDragged(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent mouseEvent){
                dialog.setX(mouseEvent.getScreenX()+dragDelta.x);
                dialog.setY(mouseEvent.getScreenY()+dragDelta.y);
            }
        });
    }

    static class Delta{
        double x, y;
    }
}
