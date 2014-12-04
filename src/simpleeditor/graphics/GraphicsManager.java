

package simpleeditor.graphics;


import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import simpleeditor.utility.Constants;


public class GraphicsManager {
    
    private static final Color DEFAULT_CANVAS_COLOR = Color.WHITE;
    private static final Color DEFAULT_PEN_COLOR = Color.BLACK;
    private final int CANVAS_CELL_HEIGHT;
    private final int CANVAS_CELL_WIDTH;
    
    private static GraphicsManager instance;

    private final Canvas canvas;
    private Color currentPenColor;
    private final boolean[][] filled;
    private final Color[][] colors;
    private final GraphicsContext graphicsContext;
    
    private GraphicsManager(Canvas canvas) {
        this.canvas = canvas;
        CANVAS_CELL_HEIGHT = (int)canvas.getHeight()/Constants.gridSpace;
        CANVAS_CELL_WIDTH = (int)canvas.getWidth()/Constants.gridSpace;
        System.out.println(CANVAS_CELL_HEIGHT);
        System.out.println(CANVAS_CELL_WIDTH);
        this.filled = new boolean[CANVAS_CELL_HEIGHT][CANVAS_CELL_WIDTH];
        this.colors = new Color[CANVAS_CELL_HEIGHT][CANVAS_CELL_WIDTH];
        this.currentPenColor = DEFAULT_PEN_COLOR;
        for(int i = 0; i < colors.length; i++) {
            for(int j = 0; j < colors[i].length; j++) {
                colors[i][j] = DEFAULT_CANVAS_COLOR;
            }
        }
        graphicsContext = canvas.getGraphicsContext2D();
    }
    
    public void drawDot(int i, int j) {
        if(checkBounds(i, j)) {
            filled[i][j] = true;
            colors[i][j] = currentPenColor;
            graphicsContext.setFill(currentPenColor);
            graphicsContext.fillRect(i*Constants.gridSpace, j*Constants.gridSpace, 
                                          Constants.gridSpace, Constants.gridSpace);
        }
    }
    
    public void drawLine(int mode, int x1, int y1, int x2, int y2) {
        switch(mode) {
            case 0: 
                drawBresenhamLine(x1, y1, x2, y2); 
                break;
            case 1: 
                drawDDALine(x1, y1, x2, y2); 
                break;
            default: throw new IllegalArgumentException("Wrong mode ");
        }
    }
    
    public void floodFill(int mode, int x, int y) {
        switch(mode) {
            case 0:
                queueFloodFill(x, y);
                break;
            case 1:
                recursiveFloodFill(x, y);
                break;
            case 2:
                xorFloodFill(x, y);
                break;
            default:
                throw new IllegalArgumentException("Wrong mode ");

        }
    }
    
    public void drawBresenhamLine(int x1, int y1, int x2, int y2) {
        int deltaX = Math.abs(x2 - x1);
        int deltaY = Math.abs(y2 - y1);
        int stepX = (x1 < x2) ? 1 : -1;
        int stepY = (y1 < y2) ? 1 : -1;
        int err = deltaX - deltaY;
        while (x1 != x2 || y1 != y2) {
            drawDot(x1,y1);
            int doubleError = 2 * err;
            if (doubleError > -deltaY) {
                err = err - deltaY;
                x1 = x1 + stepX;
            }
            if (doubleError < deltaX) {
                err = err + deltaX;
                y1 = y1 + stepY;
            }
        }
        drawDot(x2, y2);
    }
    
    public void drawDDALine(int x1, int y1, int x2, int y2) {
        int delta, dx, dy;
        int deltaX = Math.abs(x2 - x1);
        int deltaY = Math.abs(y2 - y1);
        if (deltaX > deltaY) {
            delta = deltaX;
        } else {
            delta = deltaY;
        }
        int pointX = (x1 << 12) + (1 << 11);
        int pointY = (y1 << 12) + (1 << 11);
        int tempX = (x2 << 12) + (1 << 11);
        int tempY = (y2 << 12) + (1 << 11);
        
        if (delta != 0) {
            dx = (tempX - pointX) / delta;
            dy = (tempY - pointY) / delta;
        } else {
            dx = 0;
            dy = 0;
        }
        for (int i = 0; i <= delta; i++) {
            drawDot(pointX >> 12, pointY >> 12);
            pointX += dx;
            pointY += dy;
        }
    }
    
    public void drawCircle(int x1, int y1, int x2, int y2) {
        int radius = (int)Math.hypot(x2-x1, y2-y1);
        int x = 0;
        int y = radius;
        int delta = 2 - 2 * radius;
        int error;
        while (y >= 0) {
            drawDot(x1 + x, y1 + y);
            drawDot(x1 - x, y1 + y);
            drawDot(x1 + x, y1 - y);
            drawDot(x1 - x, y1 - y);

            error = 2 * (delta + y) - 1;
            if (delta < 0 && error <= 0) {
                ++x;
                delta += 2 * x + 1;
                continue;
            }
            error = 2 * (delta - x) - 1;
            if (delta > 0 && error > 0) {
                --y;
                delta += 1 - 2 * y;
                continue;
            }
            ++x;
            delta += 2 * (x - y);
            --y;
        }
    }
    
    public void drawEllipse(int x1, int y1, int x2, int y2) {
        int width = Math.abs(x2-x1);
        int height = Math.abs(y2 - y1);

        int a2 = width * width;
        int b2 = height * height;
        int fa2 = 4 * a2, fb2 = 4 * b2;
        int x, y, sigma;

        /* first half */
        for (x = 0, y = height, sigma = 2 * b2 + a2 * (1 - 2 * height); b2 * x <= a2 * y; x++) {
            drawDot(x1 + x, y1 + y);
            drawDot(x1 - x, y1 + y);
            drawDot(x1 + x, y1 - y);
            drawDot(x1 - x, y1 - y);
            if (sigma >= 0) {
                sigma += fa2 * (1 - y);
                y--;
            }
            sigma += b2 * ((4 * x) + 6);
        }

        /* second half */
        for (x = width, y = 0, sigma = 2 * a2 + b2 * (1 - 2 * width); a2 * y <= b2 * x; y++) {
            drawDot(x1 + x, y1 + y);
            drawDot(x1 - x, y1 + y);
            drawDot(x1 + x, y1 - y);
            drawDot(x1 - x, y1 - y);
            if (sigma >= 0) {
                sigma += fb2 * (1 - x);
                x--;
            }
            sigma += a2 * ((4 * y) + 6);
        }
    }
    
    private boolean checkBounds(int i, int j) {
        return i >= 0 && i < CANVAS_CELL_HEIGHT && j >=0 && j < CANVAS_CELL_WIDTH;
    }
    
    public void queueFloodFill(int x, int y) {
        LinkedList<Map.Entry<Integer, Integer>> queue = new LinkedList<>();
        queue.push(new AbstractMap.SimpleEntry<>(x,y));
        while(!queue.isEmpty()) {
            Entry<Integer, Integer> point = queue.pop();
            int pointX = point.getKey();
            int pointY = point.getValue();
            if(checkBounds(pointX, pointY) && !filled[pointX][pointY]) {
                colors[pointX][pointY] = currentPenColor;
                drawDot(pointX, pointY);
                queue.push(new AbstractMap.SimpleEntry<>(pointX + 1, pointY));
                queue.push(new AbstractMap.SimpleEntry<>(pointX - 1, pointY));
                queue.push(new AbstractMap.SimpleEntry<>(pointX, pointY + 1));
                queue.push(new AbstractMap.SimpleEntry<>(pointX, pointY - 1));
            }
        }
    }
    
    public void recursiveFloodFill(int x, int y) {
        if(!checkBounds(x, y) || filled[x][y]) return;
        recursiveFloodFill(x - 1, y);
        recursiveFloodFill(x + 1, y);
        recursiveFloodFill(x, y - 1);
        recursiveFloodFill(x, y + 1);
    }
    
    public void xorFloodFill(int x, int y) {
        queueFloodFill(x, y);
    }
    
    public void rotate(double angle) {
        
    }
    
    public void translate(int dx, int dy) {
        
    }
    
    public void drawGrid() {
        for (int i = 0; i <= canvas.getWidth(); i += Constants.gridSpace) {
            graphicsContext.strokeLine(i, 0, i, canvas.getHeight());
            graphicsContext.strokeLine(0, i, canvas.getWidth(), i);
        }
    }
    
    public static GraphicsManager newInstance(Canvas canvas) {
        if(instance == null) {
            instance = new GraphicsManager(canvas);
        }
        return instance;
    }
    
}
