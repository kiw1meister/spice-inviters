import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Objects;
import javax.swing.*;

public class SpiceInviters extends JPanel implements ActionListener, KeyListener {
    class Block {
        int x;
        int y;
        int width;
        int height;
        Image img;
        boolean alive = true; //for alien check
        boolean used = false; //for bullet check

        Block (int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }

    //the panel
    int tileSize = 32;
    int rows = 16;
    int columns = 16;
    int boardWidth = tileSize * columns;
    int boardHeight = tileSize * rows;

    Image shipImg;
    Image alienWhiteImg;
    Image alienCyanImg;
    Image alienMagentaImg;
    Image alienYellowImg;

    ArrayList<Image> alienImgArray;

    //ship data
    int shipWidth = tileSize * 2; //32 * 2 = 64px
    int shipHeight = tileSize; //32px
    int shipX = (tileSize * columns)/2 - tileSize; //centers horizontally ship on board
    int shipY = boardHeight - (tileSize * 2); //places ship near the bottom of the board
    int shipVelocity = tileSize;

    Block ship;

    Timer gameLoop;

    SpiceInviters() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        shipImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/ship.png"))).getImage();
        alienWhiteImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/alien.png"))).getImage();
        alienCyanImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/alien-cyan.png"))).getImage();
        alienMagentaImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/alien-magenta.png"))).getImage();
        alienYellowImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/alien-yellow.png"))).getImage();

        alienImgArray = new ArrayList<Image>();
        alienImgArray.add(alienWhiteImg);
        alienImgArray.add(alienCyanImg);
        alienImgArray.add(alienMagentaImg);
        alienImgArray.add(alienYellowImg);

        ship = new Block(shipX, shipY, shipWidth, shipHeight, shipImg);

        gameLoop = new Timer(1000/60, this); //refreshes 60fps
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(ship.img, ship.x, ship.y, ship.width, ship.height, null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            ship.x -= shipVelocity; //move left 1 tile
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            ship.x += shipVelocity; //move right 1 tile
        }
    }
}
