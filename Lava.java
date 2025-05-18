/**
 * Write a description of class Lava here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
import java.awt.Color;
import java.awt.Graphics;

public class Lava {
    private int x, y, width, height;
    
    public Lava(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);
    }

    public boolean isCollision(Player p) {
        // Check if player is within lava bounds
        return p.x + Player.WIDTH > x && p.x < x + width && p.y + Player.HEIGHT > y && p.y < y + height;
    }
}
