
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class MouseHelper {
    public final static boolean isRightClick(MouseEvent event) {
        MouseButton button = event.getButton();
        
        if (button == MouseButton.SECONDARY)
            return true;
        else if (button == MouseButton.PRIMARY && event.isControlDown())
            return true;
        
        return false;
    }
}
