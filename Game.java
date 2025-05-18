import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Game extends JPanel implements KeyListener, ActionListener {
    private final int ROWS = 32;
    private final int COLS = 32;
    private final int CELL_SIZE = 20;
    private final char[][] grid = new char[ROWS][COLS];

    private Player fireboy = new Player(1 * CELL_SIZE, 30 * CELL_SIZE - Player.HEIGHT, new Color(255, 50, 0));
    private Player watergirl = new Player(3 * CELL_SIZE, 30 * CELL_SIZE - Player.HEIGHT, new Color(0, 175, 255));

    private Lava lava1 = new Lava(10 * CELL_SIZE, 30 * CELL_SIZE, 60, CELL_SIZE);
    private Water water1 = new Water(14 * CELL_SIZE, 30 * CELL_SIZE, 60, CELL_SIZE);

    private boolean leftPressed = false, rightPressed = false;
    private boolean aPressed = false, dPressed = false;

    private Timer timer;
    private int elapsedTime = 0;

    public Game() {
        setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE));
        setFocusable(true);
        addKeyListener(this);

        initLevel();
        timer = new Timer(20, this); // 50 FPS
        timer.start();
    }

    private void initLevel() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (r == 0 || r == ROWS - 1 || c == 0 || c == COLS - 1) {
                    grid[r][c] = '#'; // Border walls
                } else {
                    grid[r][c] = '.';
                }
            }
        }

        for (int c = 2; c <= 10; c++) grid[28][c] = '#';
        for (int c = 6; c <= 14; c++) grid[24][c] = '#';
        for (int c = 10; c <= 18; c++) grid[20][c] = '#';
        for (int c = 14; c <= 22; c++) grid[16][c] = '#';
        for (int c = 18; c <= 26; c++) grid[12][c] = '#';

        grid[8][28] = 'G';
        grid[8][29] = 'G';
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                switch (grid[r][c]) {
                    case '#': g.setColor(new Color(90, 70, 40)); break;
                    case '^': g.setColor(Color.RED); break;
                    case '~': g.setColor(Color.CYAN); break;
                    case 'G': g.setColor(Color.LIGHT_GRAY); break;
                    default:  g.setColor(new Color(230, 210, 160));
                }
                g.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

        fireboy.draw(g);
        watergirl.draw(g);
        lava1.draw(g);
        water1.draw(g);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Time: " + (elapsedTime / 1000), COLS * CELL_SIZE / 2 - 30, 20);

        if (fireboy.atGoal && watergirl.atGoal) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("YOU WIN!", COLS * CELL_SIZE / 2 - 60, ROWS * CELL_SIZE / 2);
        }
    }

    private boolean isSolid(double x, double y) {
        int col = (int)(x / CELL_SIZE);
        int row = (int)(y / CELL_SIZE);

        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) return true;
        return grid[row][col] == '#';
    }

    private void applyPhysics(Player p, boolean left, boolean right, int jumpKey) {
        if (left && !isSolid(p.x - 3, p.y + Player.HEIGHT - 1)) p.x -= 3;
        if (right && !isSolid(p.x + Player.WIDTH + 3, p.y + Player.HEIGHT - 1)) p.x += 3;

        p.velY += 1.0;
        double nextY = p.y + p.velY;

        boolean hittingFloor = isSolid(p.x, nextY + Player.HEIGHT - 1) || isSolid(p.x + Player.WIDTH - 1, nextY + Player.HEIGHT - 1);
        boolean hittingCeiling = isSolid(p.x, nextY) || isSolid(p.x + Player.WIDTH - 1, nextY);

        if (p.velY > 0 && hittingFloor) {
            int row = (int)((nextY + Player.HEIGHT - 1) / CELL_SIZE);
            p.y = row * CELL_SIZE - Player.HEIGHT;
            p.velY = 0;
            p.jumping = false;
        } else if (p.velY < 0 && hittingCeiling) {
            p.velY = 0;
        } else {
            p.y = nextY;
        }

        p.x = Math.max(0, Math.min(COLS * CELL_SIZE - Player.WIDTH, p.x));
        p.y = Math.min(ROWS * CELL_SIZE - Player.HEIGHT, p.y);

        if (lava1.isCollision(p) && p != fireboy) p.isDead = true;
        if (water1.isCollision(p) && p != watergirl) p.isDead = true;

        int r = Math.max(0, Math.min(ROWS - 1, p.getGridRow(CELL_SIZE)));
        int c = Math.max(0, Math.min(COLS - 1, p.getGridCol(CELL_SIZE)));
        if (grid[r][c] == 'G') p.atGoal = true;
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) leftPressed = true;
        if (key == KeyEvent.VK_RIGHT) rightPressed = true;
        if (key == KeyEvent.VK_UP && (isSolid(fireboy.x, fireboy.y + Player.HEIGHT + 1) || isSolid(fireboy.x + Player.WIDTH - 1, fireboy.y + Player.HEIGHT + 1))) {
            fireboy.velY = -11;
            fireboy.jumping = true;
        }

        if (key == KeyEvent.VK_A) aPressed = true;
        if (key == KeyEvent.VK_D) dPressed = true;
        if (key == KeyEvent.VK_W && (isSolid(watergirl.x, watergirl.y + Player.HEIGHT + 1) || isSolid(watergirl.x + Player.WIDTH - 1, watergirl.y + Player.HEIGHT + 1))) {
            watergirl.velY = -11;
            watergirl.jumping = true;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) leftPressed = false;
        if (key == KeyEvent.VK_RIGHT) rightPressed = false;
        if (key == KeyEvent.VK_A) aPressed = false;
        if (key == KeyEvent.VK_D) dPressed = false;
    }

    public void keyTyped(KeyEvent e) {}

    public void actionPerformed(ActionEvent e) {
        elapsedTime += 20;

        if (!fireboy.isDead) applyPhysics(fireboy, leftPressed, rightPressed, KeyEvent.VK_UP);
        if (!watergirl.isDead) applyPhysics(watergirl, aPressed, dPressed, KeyEvent.VK_W);

        if (fireboy.isDead) fireboy = new Player(1 * CELL_SIZE, 30 * CELL_SIZE - Player.HEIGHT, new Color(255, 50, 0));
        if (watergirl.isDead) watergirl = new Player(3 * CELL_SIZE, 30 * CELL_SIZE - Player.HEIGHT, new Color(0, 175, 255));

        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Fireboy and Watergirl - Final Fixed Version");
        Game game = new Game();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(game);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
