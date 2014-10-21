
package simpleeditor.command;

import javafx.scene.canvas.Canvas;
import simpleeditor.command.utility.TwoPointsEventHandler;
import simpleeditor.graphics.GraphicsManager;


public final class DrawLineCommand extends ToolCommand {

    
    private static DrawLineCommand instance;
    
    private static int mode = 0;    
    private final Canvas canvas;
    private final TwoPointsEventHandler mouseClickedEventHandler;

    
    private DrawLineCommand(Canvas canvas) {
        super();
        this.canvas = canvas;
        this.mouseClickedEventHandler = new TwoPointsEventHandler(
                (x1, y1, x2, y2) -> GraphicsManager.newInstance(canvas).drawLine(mode, x1, y1, x2, y2)
        );
    }

    public static void setMode(int mode) {
        DrawLineCommand.mode = mode;
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

    public static DrawLineCommand newInstance(Canvas canvas) {
        if(instance == null) {
            instance = new DrawLineCommand(canvas);
        }
        return instance;
    }
    
}
