
package simpleeditor.command;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;


public class HandCommand extends ToolCommand {

    private static HandCommand instance;
    
    private final Canvas canvas;
    
    private double pressedX;
    private double pressedY;
    
    private final EventHandler<MouseEvent> mousePressedEventHandler;
    private final EventHandler<MouseEvent> mouseDraggedEventHandler;

    
    public HandCommand(Canvas canvas) {
        this.canvas = canvas;
        mousePressedEventHandler = (event) -> {
            pressedX = event.getX();
            pressedY = event.getY();
        };
        
        mouseDraggedEventHandler = (event) -> {
            canvas.setTranslateX(canvas.getTranslateX() + event.getX() - pressedX);
            canvas.setTranslateY(canvas.getTranslateY() + event.getY() - pressedY);
            event.consume();
        };
    }
    
    @Override
    public void enable() {
        canvas.setOnMousePressed(mousePressedEventHandler);
        canvas.setOnMouseDragged(mouseDraggedEventHandler);
    }

    @Override
    public void disable() {
        canvas.setOnMousePressed(null);
        canvas.setOnMouseDragged(null);
    }
    
    public static HandCommand newInstance(Canvas canvas) {
        if (instance == null) {
            instance = new HandCommand(canvas);
        }
        return instance;
    }
    
}
