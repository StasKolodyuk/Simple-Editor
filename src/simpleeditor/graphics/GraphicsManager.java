

package simpleeditor.graphics;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import simpleeditor.utility.Constants;


public class GraphicsManager {
    
    private static GraphicsManager instance;

    private final Canvas canvas;
    private final GraphicsContext graphicsContext;
    
    private GraphicsManager(Canvas canvas) {
        this.canvas = canvas;
        graphicsContext = canvas.getGraphicsContext2D();
    }
    
    public void drawDot(int i, int j) {
         graphicsContext.fillRect(i*Constants.gridSpace, j*Constants.gridSpace, 
                                      Constants.gridSpace, Constants.gridSpace);
         
    }
    
    public void drawLine(int mode, int x1, int y1, int x2, int y2) {
        switch(mode) {
            case 0: drawBresenhamLine(x1, y1, x2, y2); break;
            case 1: drawDDALine(x1, y1, x2, y2); break;
            default: throw new IllegalArgumentException("Wrong mode ");
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
