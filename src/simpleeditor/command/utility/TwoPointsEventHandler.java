
package simpleeditor.command.utility;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import simpleeditor.utility.Constants;


public class TwoPointsEventHandler {
    
    private final EventHandler<MouseEvent> mouseClickedEventHandler;
    
    private boolean isFirstPoint;
    private int startX;
    private int startY;
    private int endX;
    private int endY;

    public TwoPointsEventHandler(FourIntConsumer consumer) {
        this.isFirstPoint = true;
        this.mouseClickedEventHandler = (event) -> {
            if (isFirstPoint) {
                startX = (int) (event.getX() / Constants.gridSpace);
                startY = (int) (event.getY() / Constants.gridSpace);
                isFirstPoint = false;
            } else {
                endX = (int) (event.getX() / Constants.gridSpace);
                endY = (int) (event.getY() / Constants.gridSpace);
                consumer.consume(startX, startY, endX, endY);
                isFirstPoint = true;
            }
            event.consume();
        };
    }
    
    public TwoPointsEventHandler(FourIntConsumer firstConsumer, FourIntConsumer secondConsumer) {
        this.isFirstPoint = true;
        this.mouseClickedEventHandler = (event) -> {
            if (isFirstPoint) {
                startX = (int) (event.getX() / Constants.gridSpace);
                startY = (int) (event.getY() / Constants.gridSpace);
                isFirstPoint = false;
            } else {
                endX = (int) (event.getX() / Constants.gridSpace);
                endY = (int) (event.getY() / Constants.gridSpace);
                if(event.isControlDown()) {
                    firstConsumer.consume(startX, startY, endX, endY);
                } else {
                    secondConsumer.consume(startX, startY, endX, endY);
                }
                isFirstPoint = true;
            }
            event.consume();
        };
    }

    public EventHandler<MouseEvent> getHandler() {
        return mouseClickedEventHandler;
    }

    public void setIsFirstPoint(boolean isFirstPoint) {
        this.isFirstPoint = isFirstPoint;
    }
    
}
