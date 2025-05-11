import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener {
    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;
        char direction = 'U';
        int velocityX = 0;
        int velocityY = 0;

        Block(int x, int y, int width, int height, Image image) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.image = image;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            char lastDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;

            for(Block wall : walls) { // usa a direção anterior e evita que o bloco pare no meio do caminho
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = lastDirection;
                    updateVelocity();
                    break;
                }
            }
        }

        void updateVelocity() {
            if (this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -tileSize/4; // um quarto de célula a cada frame
            } else if (this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = tileSize/4;
            } else if (this.direction == 'L') {
                this.velocityX = -tileSize/4;
                this.velocityY = 0;
            } else if (this.direction == 'R') {
                this.velocityX = tileSize/4;
                this.velocityY = 0;
            }
        }

        void reset() {
            this.x = startX;
            this.y = startY;
        }
    }

    private final int rows = 21;
    private final int cols = 19;
    private final int tileSize = 32;
    private final int boardWidth = tileSize * cols;
    private final int boardHeight = tileSize * rows;

    private Image wallImg;
    private Image redGhostImg;
    private Image blueGhostImg;
    private Image pinkGhostImg;
    private Image orangeGhostImg;

    private Image pacmanUpImg;
    private Image pacmanDownImg;
    private Image pacmanLeftImg;
    private Image pacmanRightImg;

    //X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = { // criar mapa personalizado e implementar o teleporte
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "O       bpo       O",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    Timer gameLoop;
    char[] direction = {'U', 'D', 'L', 'R'};
    Random rand = new Random();
    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight)); // mesmo tamanho da janela
        setBackground(Color.darkGray); // prefiro do que preto, diminui eye strain
        addKeyListener(this);
         setFocusable(true);

        wallImg = new ImageIcon(getClass().getResource("./wall.png")).getImage();

        redGhostImg = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();
        blueGhostImg = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        pinkGhostImg = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        orangeGhostImg = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();

        pacmanUpImg = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImg = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImg = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImg = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

        loadMap();

        for (Block ghost : ghosts) {
            char newDirection = direction[rand.nextInt(4)];
            ghost.updateDirection(newDirection);
        }

        gameLoop = new Timer(50, this);
        gameLoop.start();
    }

    public void loadMap() {
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c*tileSize; // quantas células da esquerda
                int y = r*tileSize; // quantas células do topo

                if (tileMapChar == 'X') {
                    Block wall = new Block(x, y, tileSize, tileSize, wallImg);
                    walls.add(wall);
                } else if (tileMapChar == 'b') {
                    Block ghost = new Block(x, y, tileSize, tileSize, blueGhostImg);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'o') {
                    Block ghost = new Block(x, y, tileSize, tileSize, orangeGhostImg);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'p') {
                    Block ghost = new Block(x, y, tileSize, tileSize, pinkGhostImg);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'r') {
                    Block ghost = new Block(x, y, tileSize, tileSize, redGhostImg);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'P') {
                    pacman = new Block(x, y, tileSize, tileSize, pacmanRightImg);
                } else if (tileMapChar == ' ') {
                    Block food = new Block(x + 14, y + 14, 4, 4, null); // 14 + 4 + 14 = 32
                    foods.add(food);
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for (Block food : foods) {
            g.setColor(Color.yellow);
            g.fillRect(food.x, food.y, food.width, food.height);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        if (gameOver) {
            g.drawString("Game Over: " + score, boardWidth/2 - 100, boardHeight/2);
        } else {
            g.drawString(lives + "♥  " + "Score: " + score, tileSize/2, 24);
        }
    }

    public void move() {
        pacman.x += pacman.velocityX; // em qualquer movimento, 1 dos 2 vai ser 0
        pacman.y += pacman.velocityY; // assim, não precisa de um if-guard

        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        for (Block ghost : ghosts) {
            if (collision(pacman, ghost)) {
                lives--;

                if (lives == 0) {
                    gameOver = true;
                    return;
                }
                resetPositions();
            }

            if (ghost.y == tileSize*9 && ghost.direction != 'U' && ghost.direction != 'D') {
                ghost.updateDirection('U');
            }

            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            for (Block wall : walls) {
                if (collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = direction[rand.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }

        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);

        if (foods.isEmpty()) {
            loadMap();
            resetPositions();
        }
    }

    public boolean collision (Block pacman, Block obstacle) {
        return pacman.x < obstacle.x + obstacle.width &&
                pacman.x + pacman.width > obstacle.x &&
                pacman.y < obstacle.y + obstacle.height &&
                pacman.y + pacman.height > obstacle.y;
    }

    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0; // pacman não se move depois de resetar

        for (Block ghost : ghosts) {
            ghost.reset();
            char newDirection = direction[rand.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
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
    public void keyReleased(KeyEvent e) {
        if (gameOver && e.getKeyCode() == KeyEvent.VK_SPACE) {
            loadMap(); // cria a comida toda de novo
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            gameLoop.start();
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U');
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection('D');
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L');
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R');
        }

        if (pacman.direction == 'U') {
            pacman.image = pacmanUpImg;
        } else if (pacman.direction == 'D') {
            pacman.image = pacmanDownImg;
        } else if (pacman.direction == 'L') {
            pacman.image = pacmanLeftImg;
        } else if (pacman.direction == 'R') {
            pacman.image = pacmanRightImg;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
}
