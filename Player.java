/**
 * Write a description of class Player here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
import java.awt.*;

import java.awt.*;

public class Player {
    public double x, y;
    public double velY = 0;
    public boolean jumping = false;
    public boolean isDead = false;
    public boolean atGoal = false;
    public final Color color;

    public static final int WIDTH = 16;
    public static final int HEIGHT = 16;

    public Player(int startX, int startY, Color color) {
        this.x = startX;
        this.y = startY;
        this.color = color;
    }

    public void draw(Graphics g) {
        if (!isDead) {
            g.setColor(color);
            g.fillOval((int)x, (int)y, WIDTH, HEIGHT);
        }
    }

    public int getGridRow(int cellSize) {
        return (int)((y + HEIGHT / 2) / cellSize);
    }

    public int getGridCol(int cellSize) {
        return (int)((x + WIDTH / 2) / cellSize);
    }
}
