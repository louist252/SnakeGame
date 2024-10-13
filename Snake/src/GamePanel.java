import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements ActionListener {
	
	/** Width of the game window */
	static final int SCREEN_WIDTH = 500;
	
	/** Height of the game window */
	static final int SCREEN_HEIGHT = 500;
	
	/** Size of each units in the game */
	static final int UNIT_SIZE = 25;
	
	/** Number of units can fit in the game */
	static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
	
	/** Delay for timer */
	static final int DELAY = 50;
	
	/** Array to hold the x coordinate of the snake */
	final int x[] = new int[GAME_UNITS];
	
	/** Array to hold the y coordinate of the snake */
	final int y[] = new int[GAME_UNITS];
	
	
	int bodyParts = 6;
	int applesEaten = 0;
	
	
	int appleX;
	int appleY;
	
	/** Initial direction of the next (going right) */
	char direction = 'R';
	
	boolean running;
	
	Timer timer;
	Random random;
	
	GamePanel() {
		running = false;
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new SnakeKey());
		startGame();
	}
	
	public void startGame() {
		makeApple();
		running = true;
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
			// Draw the grid for better game visualization
			for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
				g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE , SCREEN_HEIGHT);
				g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH , i * UNIT_SIZE);
				
			}
			
			// Draw the apple
			g.setColor(Color.red);
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
			
			//Draw the snake
			for (int i = 0; i < bodyParts; i++) {
				if (i == 0) {
					g.setColor(Color.green);
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				} else {
					g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}
			//Keeping track of score
			g.setColor(Color.blue);
			g.setFont(new Font("Ink Free", Font.BOLD, 30));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
		} else {
			gameOver(g);
		}
	}
	
	
	public void makeApple() {
		appleX = random.nextInt((int) SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
		appleY = random.nextInt((int) SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
		
		
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
			y[0] = y[0] - UNIT_SIZE;
			break;
		case 'D':
			y[0] = y[0] + UNIT_SIZE;
			break;
		case 'L':
			x[0] = x[0] - UNIT_SIZE;
			break;
		case 'R':
			x[0] = x[0] + UNIT_SIZE;
			break;
		}
	}
	
	public void checkApple() {
		if (x[0] == appleX && y[0] == appleY) {
			bodyParts++;
			applesEaten++;
			makeApple();
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
		if (x[0] < 0 || x[0] >= SCREEN_WIDTH ||
			y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
			running = false;
		}
		
		if (!running) {
			timer.stop();
		}
	}
	
	public void gameOver(Graphics g) {
		//Game Over text
		g.setColor(Color.orange);
		g.setFont(new Font("Ink Free", Font.BOLD, 75));
		FontMetrics metrics = getFontMetrics(g.getFont());
		g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
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
			}
		}
	}

}
