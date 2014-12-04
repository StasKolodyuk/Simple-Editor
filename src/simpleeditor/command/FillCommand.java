
package simpleeditor.command;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import simpleeditor.graphics.GraphicsManager;
import simpleeditor.utility.Constants;


public class FillCommand extends ToolCommand {
    
    private static FillCommand instance;

    private final Canvas canvas;
    private final EventHandler<MouseEvent> mousePressedEventHandler;
    
    private static int mode = 0;

    private FillCommand(Canvas canvas) {
        this.canvas = canvas;
        this.mousePressedEventHandler = (event) -> {
            int pressedX;
            int pressedY;
            pressedX = (int)event.getX() / Constants.gridSpace;
            pressedY = (int)event.getY() / Constants.gridSpace;
            GraphicsManager.newInstance(canvas).queueFloodFill(pressedX, pressedY);
        };
    }

    @Override
    public void enable() {
        canvas.setOnMousePressed(mousePressedEventHandler);
    }

    @Override
    public void disable() {
        canvas.setOnMousePressed(null);
    }
    
    public static void setMode(int mode) {
        FillCommand.mode = mode;
    }
                
    public static FillCommand newInstance(Canvas canvas) {
        if (instance == null) {
            instance = new FillCommand(canvas);
        }
        return instance;
    }

}
