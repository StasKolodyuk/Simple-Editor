
package simpleeditor.command;

import javafx.scene.canvas.Canvas;
import simpleeditor.command.utility.TwoPointsEventHandler;
import simpleeditor.graphics.GraphicsManager;


public class DrawEllipseCommand extends ToolCommand {
    
    private static DrawEllipseCommand instance;

    private final Canvas canvas;

    private final TwoPointsEventHandler mouseClickedEventHandler;

    public DrawEllipseCommand(Canvas canvas) {
        super();
        this.canvas = canvas;
        this.mouseClickedEventHandler = new TwoPointsEventHandler(
                (x1, y1, x2, y2) -> GraphicsManager.newInstance(canvas).drawCircle(x1, y1, x2, y2),
                (x1, y1, x2, y2) -> GraphicsManager.newInstance(canvas).drawEllipse(x1, y1, x2, y2)
        );
    }

    @Override
    public void enable() {
        mouseClickedEventHandler.setIsFirstPoint(true);
        canvas.setOnMouseClicked(mouseClickedEventHandler.getHandler());
    }

    @Override
    public void disable() {
        canvas.setOnMouseClicked(null);
    }

    public static DrawEllipseCommand newInstance(Canvas canvas) {
        if (instance == null) {
            instance = new DrawEllipseCommand(canvas);
        }
        return instance;
    }
    
}
