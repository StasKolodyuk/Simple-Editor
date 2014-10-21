
package simpleeditor.view;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.input.ScrollEvent;
import simpleeditor.command.DrawEllipseCommand;
import simpleeditor.command.DrawLineCommand;
import simpleeditor.command.HandCommand;
import simpleeditor.command.ToolCommand;
import simpleeditor.graphics.GraphicsManager;


public class EditorViewController {
    
    /*** MENU ***/
    @FXML
    private MenuItem closeMenuItem;
    @FXML
    private Label label;
    
    /*** TOOLBAR ***/
    @FXML
    private ToolBar toolBar;
    /*** HAND BUTTON ***/
    @FXML
    private ToggleButton handButton;
    /*** LINE BUTTON ***/
    @FXML
    private ToggleButton lineDrawingButton;
    @FXML
    private CheckMenuItem bresenhamMenuItem;
    @FXML
    private CheckMenuItem ddaMenuItem;
    /*** CIRCLE & ELLIPSE BUTTONS ***/
    @FXML
    private ToggleButton ellipseDrawingButton;
 
    /*** CANVAS ***/
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Canvas canvas;
    
    /*** OTHER ***/
    private ToolCommand currentCommand;
    private ToggleButton currentToolButton;
    
    public void initialize() {
        GraphicsManager.newInstance(canvas).drawGrid();
        handleZoomInAndOut();
    }
    
    
    
    private void handleZoomInAndOut() {
        canvas.setOnScroll((ScrollEvent e) -> {
            double zoom = (e.getDeltaY() < 0) ? 1.1 : 0.9;
            double scale = canvas.getScaleX() * zoom;
            canvas.setScaleX(scale);
            canvas.setScaleY(scale);
            e.consume();
        });
    }
    
    private void enableTool(ToggleButton toggleButton, ToolCommand command) {
        if (toggleButton.isSelected()) {
            if (currentCommand != null) {
                currentCommand.disable();
                currentToolButton.setSelected(false);
            }
            currentCommand = command;
            currentToolButton = toggleButton;
            currentCommand.enable();
        } else if (currentCommand == command) {
            currentCommand.disable();
            currentCommand = null;
        }
    }
    
    @FXML
    public void ellipseDrawingButtonPressed(ActionEvent event) {
        enableTool(ellipseDrawingButton, DrawEllipseCommand.newInstance(canvas));
    }
    
    @FXML
    public void lineDrawingButtonPressed(ActionEvent event) {
        enableTool(lineDrawingButton, DrawLineCommand.newInstance(canvas));
    }
    
    @FXML
    public void bresenhamMenuItemClicked(ActionEvent event) {
        ddaMenuItem.setSelected(false);
        DrawLineCommand.setMode(0);
    }
    
    @FXML
    public void ddaMenuItemClicked(ActionEvent event) {
        bresenhamMenuItem.setSelected(false);
        DrawLineCommand.setMode(1);
    }
    
    @FXML
    public void handButtonPressed(ActionEvent event) {
        enableTool(handButton, HandCommand.newInstance(canvas));
    }
    
    @FXML
    public void closeMenuItemPressed(ActionEvent event) {
        Platform.exit();
    }
      
}
