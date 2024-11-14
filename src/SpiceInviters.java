import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Random;
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
    Image explosionImg;

    ArrayList<Image> alienImgArray;

    //ship data
    int shipWidth = tileSize * 2; //32 * 2 = 64px
    int shipHeight = tileSize; //32px
    int shipX = (tileSize * columns)/2 - tileSize; //centers horizontally ship on board
    int shipY = boardHeight - (tileSize * 2); //places ship near the bottom of the board
    int shipVelocity = tileSize;

    Block ship;

    //alien data
    ArrayList<Block> alienArray;
    int alienWidth = tileSize * 2;
    int alienHeight = tileSize;
    int alienX = tileSize;
    int alienY = tileSize;

    int alienRow = 2;
    int alienColumn = 3;
    int alienCount = 0;
    int alienVelocity = 1;

    //bullet data
    ArrayList<Block> bulletArray;
    int bulletWidth = tileSize / 8;
    int bulletHeight = tileSize / 2;
    int bulletVelocity = -10;

    Timer gameLoop;
    int score = 0;
    boolean gameOver = false;

    SpiceInviters() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        shipImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/cinnam16.png"))).getImage();
        alienWhiteImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/alien.png"))).getImage();
        alienCyanImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/alien-cyan.png"))).getImage();
        alienMagentaImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/alien-magenta.png"))).getImage();
        alienYellowImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/alien-yellow.png"))).getImage();
        explosionImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/explosion.png"))).getImage();

        alienImgArray = new ArrayList<Image>();
        alienImgArray.add(alienWhiteImg);
        alienImgArray.add(alienCyanImg);
        alienImgArray.add(alienMagentaImg);
        alienImgArray.add(alienYellowImg);

        ship = new Block(shipX, shipY, shipWidth, shipHeight, shipImg);
        alienArray = new ArrayList<Block>();
        bulletArray = new ArrayList<Block>();

        gameLoop = new Timer(1000/60, this); //refreshes 60fps
        createAliens();
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        //ship render
        g.drawImage(ship.img, ship.x, ship.y, ship.width, ship.height, null);

        //alien render
        for (Block alien : alienArray) {
            if (alien.alive) {
                g.drawImage(alien.img, alien.x, alien.y, alienWidth, alienHeight, null);
            }
        }

        //bullet render
        g.setColor(Color.white);
        for (Block bullet : bulletArray) {
            if (!bullet.used) {
                //for transparent bullets:
                //g.drawRect(bullet.x, bullet.y, bullet.width, bullet.height);
                //for solid bullets:
                g.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
            }
        }

        //render score
        g.setColor(Color.white);
        g.setFont(new Font("Menlo", Font.PLAIN, 20));
        if (gameOver) {
            g.drawString("Game Over! Final Score: " + String.valueOf(score), 10, 35);
        } else {
            g.drawString("Score: " + String.valueOf(score), 10, 35);
        }
    }

    public void move() {
        //alien move data
        for (Block alien : alienArray) {
            if (alien.alive) {
                //moves alien
                alien.x += alienVelocity;

                //if the alien touches the border, change course
                if (alien.x + alienWidth >= boardWidth || alien.x <= 0) {
                    alienVelocity *= -1;
                    alien.x += alienVelocity * 2;

                    //move aliens down one row
                    for (Block block : alienArray) {
                        block.y += alienHeight;
                    }
                }

                if (alien.y >= ship.y) {
                    gameOver = true;
                }
            }
        }

        //bullet move data
        for (Block bullet : bulletArray) {
            bullet.y += bulletVelocity;

            //bullet collision
            for (Block alien : alienArray) {
                if (!bullet.used && alien.alive && isAlienImageInArray(alien.img) && detectHit(bullet, alien)) {
                    alien.img = explosionImg;
                    bullet.used = true;
                    //alien.alive = false;
                    alienCount--;
                    score += 100;

                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    alien.alive = false; // Set alive to false after 500ms
                                    // You may also reset the image or handle cleanup here if needed
                                }
                            },
                            200 // Delay in milliseconds (500 ms = 0.5 seconds)
                    );
                }
            }
        }

        //clear trash bullets
        while (!bulletArray.isEmpty() && (bulletArray.getFirst().used || bulletArray.getFirst().y < 0)) { //when the bullet array is not empty + first element is used or pass the screen
            bulletArray.removeFirst(); //removes first element in the bullet array
        }

        //moves to next level
        if (alienCount == 0) {
            //Bonus points for clearing the round
            score += alienColumn * alienRow * 100;
            //increase column and rows of aliens
            alienColumn = Math.min(alienColumn + 1, columns / 2 - 2); //caps columns at 6
            alienRow = Math.min(alienRow + 1, rows - 3); //caps rows at 13
            alienArray.clear(); //cleans alien array
            bulletArray.clear(); //cleans bullet array
            alienVelocity = 1;
            createAliens(); //generates new aliens
        }
    }

    public void createAliens() {
        Random random = new Random();
        for (int r = 0; r < alienRow; r++) {
            for (int c = 0; c < alienColumn; c++) {
                int randomImgIndex = random.nextInt(alienImgArray.size());
                Block alien = new Block(
                        alienX + c * alienWidth,
                        alienY + r * alienHeight,
                        alienWidth,
                        alienHeight,
                        alienImgArray.get(randomImgIndex)
                );
                alienArray.add(alien);
            }
        }
        alienCount = alienArray.size();
    }

    private boolean detectHit(Block a, Block b) {
        return a.x < b.x + b.width && //top left of a doesn't reach top right of b
                a.x + a.width > b.x && //top right of a passes top left of b
                a.y < b.y + b.height && //top left of a doesn't reach bottom left of b
                a.y + a.height > b.y; //bottom left of a passes top left of b
    }

    private boolean isAlienImageInArray(Image img) {
        for (Image alienImage : alienImgArray) {
            if (alienImage.equals(img)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) {
            ship.x = shipX;
            alienArray.clear();
            bulletArray.clear();
            score = 0;
            alienVelocity = 1;
            alienColumn = 3;
            alienRow = 2;
            gameOver = false;
            createAliens();
            gameLoop.start();
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && ship.x - shipVelocity >= 0) {
            ship.x -= shipVelocity; //move left 1 tile
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && ship.x + ship.width + shipVelocity <= boardWidth) {
            ship.x += shipVelocity; //move right 1 tile
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            Block bullet = new Block(ship.x + shipWidth * 15 / 32, ship.y, bulletWidth, bulletHeight, null);
            bulletArray.add(bullet);
        }
    }
}
