import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements ActionListener {
	
	/** Width of the game window */
	static final int SCREEN_WIDTH = 300;
	
	/** Height of the game window */
	static final int SCREEN_HEIGHT = 325;
	
	/** Height of the header */
	static final int HEADER = 25;
	
	/** Actual game height */
	static final int GAME_HEIGHT = 300;

	/** Size of each units in the game */
	static final int UNIT_SIZE = 25;
	
	/** Number of units can fit in the game */
	static final int GAME_UNITS = (SCREEN_WIDTH * GAME_HEIGHT) / UNIT_SIZE;
	
	/** Delay for timer */
	static final int DELAY = 120;
	
	/** Score to determine victory */
	static final int  VICTORY = GAME_UNITS;
	
	/** Array to hold the x coordinate of the snake */
	final int x[] = new int[GAME_UNITS];
	
	/** Array to hold the y coordinate of the snake */
	final int y[] = new int[GAME_UNITS];
	
	/** Keep track of the number of the body parts */
	int bodyParts;
	
	/** Keep track of the number of apples eaten */
	int applesEaten;
	
	/** X coordinate of apple */
	int appleX;
	
	/** Y coordinate of apple */
	int appleY;
	
	/** Initial direction of the next (going right) */
	char direction;
	
	/** A flag to check for victory */
	boolean victory;
	
	/** A flag to check if the game is still runnong */
	boolean running;
	
	/** A timer object */
	Timer timer;
	
	/** A random object */
	Random random;
	
	GamePanel() {
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new SnakeKey());
		startGame();
	}
	
	public void startGame() {
		if (timer != null) {
	        timer.stop();
	    }
	    
		random = new Random();
	    bodyParts = 6;
	    applesEaten = 0;
	    direction = 'R';
	    
	    for (int i = 0; i < bodyParts; i++) {
	        x[i] = UNIT_SIZE * (bodyParts - 1 - i);
	        y[i] = HEADER + UNIT_SIZE; // Start below the header
	    }
	    
	    makeApple();
	    running = true;
	    victory = false;
	    
	    timer = new Timer(DELAY, this);
	    timer.start();
	}
	
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}
	
	/**
	 * Draw in the game components
	 * @param g the graphics
	 */
	public void draw(Graphics g) {
		if (running) {
			
			// Draw the apple
			g.setColor(Color.red);
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
			
			//Draw the snake
			for (int i = 0; i < bodyParts; i++) {
				if (i == 0) {
					g.setColor(Color.green);
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				} else {
					g.setColor(Color.cyan);
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}
			
			//Keeping track of score
			displayScore(g);
		} else if (victory) {
			victoryScreen(g);
		} else {
			gameOverScreen(g);
		}
	}
	
	
	public void makeApple() {
		appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
	    appleY = random.nextInt((GAME_HEIGHT / UNIT_SIZE)) * UNIT_SIZE + HEADER;
	    
	    // Check if the apple is on the snake
	    for (int i = 0; i < bodyParts; i++) {
	        if ((appleX == x[i]) && (appleY == y[i])) {
	            makeApple(); // Recursively call to generate a new position
	        }
	    }
		
	}
	
	
	
	public void checkApple() {
		if (x[0] == appleX && y[0] == appleY) {
	        bodyParts++;
	        applesEaten++;
	        if (applesEaten >= VICTORY) {
	            running = false;
	            victory = true;
	        } else {
	            makeApple();
	        }
	    }
		
	}
	
	/**
	 * Handle the movement of the snake
	 */
	public void move() {
		for (int i = bodyParts; i > 0; i--) {
			x[i] = x[i - 1];
			y[i] = y[i - 1];
		}
		
		switch (direction) {
		case 'U':
			y[0] = Math.max(y[0] - UNIT_SIZE, 0);
			break;
		case 'D':
			y[0] = Math.min(y[0] + UNIT_SIZE, SCREEN_HEIGHT - UNIT_SIZE);
			break;
		case 'L':
			x[0] = Math.max(x[0] - UNIT_SIZE, 0);
			break;
		case 'R':
			x[0] = Math.min(x[0] + UNIT_SIZE, SCREEN_WIDTH - UNIT_SIZE);
			break;
		}
	}
	
	
	public void checkCollisions() {
		//Check for head colliding with body
		for (int i = bodyParts; i > 0; i--) {
			if ((x[0] == x[i]) && (y[0] == y[i])) {
				running = false;
				
			}
		}
		//Check for colliding with border
		if (x[0] < 0 || x[0] > SCREEN_WIDTH ||
			y[0] < HEADER || y[0] > SCREEN_HEIGHT) {
			running = false;
		}
		
		if (!running) {
			timer.stop();
		}
	}
	
	
	public void gameOverScreen(Graphics g) {
		//Game Over text
		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD, 40));
		FontMetrics gameOver = getFontMetrics(g.getFont());
		g.drawString("Game Over", (SCREEN_WIDTH - gameOver.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
		//Option for restarting the game
		restartOption(g);
		
		//Displaying the score
		displayScore(g);
		
		
	}
	
	public void victoryScreen(Graphics g) {
	    g.setColor(Color.green);
	    g.setFont(new Font("Ink Free", Font.BOLD, 75));
	    FontMetrics metrics = getFontMetrics(g.getFont());
	    g.drawString("Victory!", (SCREEN_WIDTH - metrics.stringWidth("Victory!")) / 2, SCREEN_HEIGHT / 2);
	    //Option for restarting the game
	    restartOption(g);
	    
	    //Displaying the score
	    displayScore(g);
	}
	
	public void displayScore(Graphics g) {
		g.setColor(Color.DARK_GRAY);
	    g.fillRect(0, 0, SCREEN_WIDTH, HEADER);
		g.setColor(Color.orange);
		g.setFont(new Font("Ink Free", Font.BOLD, 20));
		FontMetrics score = getFontMetrics(g.getFont());
		g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - score.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
	}
	
	public void restartOption(Graphics g) {
		g.setColor(Color.white);
	    g.setFont(new Font("Ink Free", Font.BOLD, 20));
	    FontMetrics restart = getFontMetrics(g.getFont());
	    g.drawString("Press SPACE to Restart", (SCREEN_WIDTH - restart.stringWidth("Press SPACE to Restart")) / 2, SCREEN_HEIGHT / 2 + 50);
	}
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (running) { 
			move();
			checkApple();
			checkCollisions();    
		} 
		repaint(); 
		
	}
	
	/**
	 * Inner class to help with controlling the snake
	 * by pressing keys
	 */
	public class SnakeKey extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent k) {
			switch(k.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if (direction != 'R') {
					direction = 'L';
				}
				break;
			case KeyEvent.VK_RIGHT:
				if (direction != 'L') {
					direction = 'R';
				}
				break;
			case KeyEvent.VK_UP:
				if (direction != 'D') {
					direction = 'U';
				}
				break;
			case KeyEvent.VK_DOWN:
				if (direction != 'U') {
					direction = 'D';
				}
				break;
			case KeyEvent.VK_SPACE:
				if (!running) {
					startGame(); //Restart the game;
				}
				break;
			}
		}
	}

}
