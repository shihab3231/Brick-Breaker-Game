import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BrickBreaker extends JPanel implements KeyListener, ActionListener {
    private boolean play = true;
    private int score = 0;
    private int totalBricks = 21;

    private Timer timer;
    private int delay = 8;

    private int paddleX = 310;
    private int ballX = 120, ballY = 250;
    private int ballDirX = -1, ballDirY = -2;

    private BrickGenerator bricks;

    // Constructor
    public BrickBreaker() {
        bricks = new BrickGenerator(3, 7);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.start();
    }

    // Draw the game elements
    public void paint(Graphics g) {
        // Background
        g.setColor(Color.BLACK);
        g.fillRect(1, 1, 692, 592);

        // Display Score at the Top
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 30, 30);

        // Bricks
        bricks.draw((Graphics2D) g);

        // Borders
        g.setColor(Color.YELLOW);
        g.fillRect(0, 0, 3, 592);
        g.fillRect(0, 0, 692, 3);
        g.fillRect(691, 0, 3, 592);

        // Paddle
        g.setColor(Color.BLUE);
        g.fillRect(paddleX, 550, 100, 8);

        // Ball
        g.setColor(Color.WHITE);
        g.fillOval(ballX, ballY, 20, 20);

        // Check if all bricks are destroyed (winner condition)
        if (totalBricks == 0) {
            play = false;  // Stop the game
            ballDirX = 0;
            ballDirY = 0;
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("You Win! Score: " + score, 190, 300);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Press ENTER to Restart", 230, 350);
        }

        // Game Over
        if (ballY > 570 && totalBricks > 0) {
            play = false;
            ballDirX = 0;
            ballDirY = 0;
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Game Over! Score: " + score, 190, 300);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Press ENTER to Restart", 230, 350);
        }

        g.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (play) {
            ballX += ballDirX;
            ballY += ballDirY;

            // Ball collision with walls
            if (ballX <= 0 || ballX >= 670) {
                ballDirX *= -1;
            }
            if (ballY <= 0) {
                ballDirY *= -1;
            }

            // Ball collision with paddle
            if (new Rectangle(ballX, ballY, 20, 20).intersects(new Rectangle(paddleX, 550, 100, 8))) {
                ballDirY *= -1;
            }

            // Ball collision with bricks
            A:
            for (int i = 0; i < bricks.map.length; i++) {
                for (int j = 0; j < bricks.map[0].length; j++) {
                    if (bricks.map[i][j] > 0) {
                        int brickX = j * bricks.brickWidth + 80;
                        int brickY = i * bricks.brickHeight + 50;
                        int brickWidth = bricks.brickWidth;
                        int brickHeight = bricks.brickHeight;

                        Rectangle brickRect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
                        Rectangle ballRect = new Rectangle(ballX, ballY, 20, 20);

                        if (ballRect.intersects(brickRect)) {
                            bricks.setBrickValue(0, i, j);  // Remove brick
                            totalBricks--;  // Decrease the number of remaining bricks
                            score += 5;  // Increase score

                            if (ballX + 19 <= brickRect.x || ballX + 1 >= brickRect.x + brickWidth) {
                                ballDirX *= -1;
                            } else {
                                ballDirY *= -1;
                            }
                            break A;
                        }
                    }
                }
            }
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT && paddleX > 10) {
            paddleX -= 30;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT && paddleX < 600) {
            paddleX += 30;
        }

        // Restart game on ENTER key press
        if (e.getKeyCode() == KeyEvent.VK_ENTER && !play) {
            resetGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    // Method to reset the game
    public void resetGame() {
        play = true;
        score = 0;
        totalBricks = 21;
        paddleX = 310;
        ballX = 120;
        ballY = 250;
        ballDirX = -1;
        ballDirY = -2;
        bricks = new BrickGenerator(3, 7);
        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        BrickBreaker game = new BrickBreaker();
        frame.setBounds(10, 10, 700, 600);
        frame.setTitle("Brick Breaker");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(game);
        frame.setVisible(true);
    }
}
