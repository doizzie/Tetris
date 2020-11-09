package tetris;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * 
 * Dominyka Lupeikytė, Prifs-18/5
 * Tetrio žaidimo programa skirta Programų sistemų projektavimo dalykui
 * Vilniaus Gedimino Technikos Universitetas
 * 2020 metai
 * 
 */

public class Tetris extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static int gameWidth = 20;
	private static int gameHeight = 25;
	
	public Tetris() {}
	
	private int[][][] shapes = { //first coordinate - y, second coordinate - x
            {{0, 2}, {1, 2}, {2, 2}, {3, 2}}, //SHAPE I - 0
            {{0, 2}, {1, 0}, {1, 1}, {1, 2}}, //SHAPE L - 1
            {{0, 0}, {1, 0}, {1, 1}, {1, 2}}, //SHAPE REVERSED L - 2
            {{0, 0}, {0, 1}, {1, 1}, {1, 2}}, //SHAPE Z - 3
            {{0, 1}, {0, 2}, {1, 0}, {1, 1}}, //SHAPE REVERSED Z - 4
            {{0, 0}, {0, 1}, {1, 0}, {1, 1}}, //SHAPE SQUARE - 5
            {{0, 1}, {1, 0}, {1, 1}, {1, 2}} //SHAPE T - 6
	};

	private int[] currentCoord;

	public int[] baseCoordinates = {2, 8};
	private int[][] currentShape; //contains an array describing a shape
	private int shapeNumber;
	
	private Color[][] board;
	public Color backgroundColor = Color.white;
	public Color fallingPieceColor = Color.black;
	public Color steadyPieceColor = Color.orange;
	
	private int score;	
	private boolean isGamePlayed;

	private void start() {
		board = new Color[gameWidth][gameHeight];
		for (int i = 0; i < gameWidth; i++) 
			for (int j = 0; j < gameHeight - 1; j++) 
				board[i][j] = backgroundColor;
		
		isGamePlayed = true;
		newPiece();
	}
	
	
	public void reset() {
		currentShape = new int[4][2];
		currentCoord = new int[2];
		baseCoordinates[0] = 2;
		baseCoordinates[1] = 8;
	}
	
	
	public void rotate() {
		int x;
		int y;
		for (int i=0; i<4; i++) {
			x = 2 - currentShape[i][0];
			y = currentShape[i][1];
			
			currentShape[i][1] = x;
			currentShape[i][0] = y;
		}		
		repaint();
	}
	
	public void newPiece() {
		
		reset();
		
		shapeNumber = -1;
		currentCoord = baseCoordinates;
		
		int rand = (int)(Math.random() * (6 + 1));
		shapeNumber = rand;
		currentShape = shapes[shapeNumber];
		
		if(board[baseCoordinates[1]][baseCoordinates[0]] != backgroundColor || board[currentCoord[1]][currentCoord[0]] == steadyPieceColor){
			isGamePlayed = false;
		}
	}

	public void move(int a) {
		boolean isAtBottom = false;
		int x = currentCoord[1] + a;
		int y = currentCoord[0];
		for (int i=0; i < 4; i++)
			if (board[currentShape[i][1] + x][currentShape[i][0] + y] != backgroundColor)
				isAtBottom = true;
		if (!isAtBottom && x >= 0 && x < gameWidth)
			currentCoord[1] += a;	

		repaint();
	}
	
	public void dropDown() {
		boolean isAtBottom = false;
		int x = currentCoord[1];
		int y = currentCoord[0] + 1;
		for (int i=0; i < 4; i++)
			if (board[currentShape[i][1] + x][currentShape[i][0] + y] != backgroundColor)
				isAtBottom = true;
		
		if (!isAtBottom) currentCoord[0]++;
		else changeBoard();
		
		repaint();
	}
	
	public void changeBoard() {
		for (int i=0; i < 4; i++){
			board[currentCoord[1] + currentShape[i][1]][currentCoord[0] + currentShape[i][0]] = steadyPieceColor;
		}
		rows();
		reset();
		newPiece();
	}
	
	public void rows() {
		int k = 0;
		for (int j = gameHeight - 2; j > 0; j--) {
			k = 0;
			for (int i = 1; i < gameWidth - 1; i++) {
				if (board[i][j] == backgroundColor) {
					k++;
					break;
				}
			}
			if (k == 0) {
				int row = j;
				for (int a = row-1; a > 0; a--)
					for (int z = 1; z < gameWidth-1; z++)
						board[z][a+1] = board[z][a];
				score++;
				j++;
			}
		}
	}
	
	
	@Override 
	public void paint(Graphics g){
		g.setColor(backgroundColor);
		g.fillRect(0, 0, 26*gameWidth, 26*gameHeight-1);
		for (int i = 0; i < gameWidth; i++) {
			for (int j = 0; j < gameHeight - 1; j++) {
				g.setColor(board[i][j]);
				g.fillRect(26*i, 26*j, 25, 25);
			}
		}
		
		if (!isGamePlayed) {
			g.setColor(fallingPieceColor);
			g.setFont(new Font("Arial", Font.BOLD, 50)); 
			g.drawString("GAME OVER", 5*gameWidth, gameHeight*10);
			g.setFont(new Font("Arial", Font.PLAIN, 15)); 
			g.drawString("Score: " + score, 11*gameWidth, gameHeight*11);
			
		}
		g.setColor(fallingPieceColor);
		for (int i=0; i < 4; i++)
			g.fillRect((currentShape[i][1] + currentCoord[1]) * 26, (currentShape[i][0] + currentCoord[0]) * 26, 25, 25);
	}

	public static void main(String[] args) {
		int frameWidth = gameWidth*26 + 15;
		int frameHeight = gameHeight*26 + 25;
		
		JFrame frame = new JFrame("TETRIS");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(frameWidth, frameHeight);
		frame.setVisible(true);
		
		Tetris game = new Tetris();
		game.start();
		frame.add(game);
		
		
		frame.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}
			
			
			public void keyPressed(KeyEvent e) {
				if (game.isGamePlayed)
					switch (e.getKeyCode()) {
					case KeyEvent.VK_UP:
						game.rotate();
						break;
					case KeyEvent.VK_DOWN:
						game.rotate();
						break;
					case KeyEvent.VK_LEFT:
						game.move(-1);
						break;
					case KeyEvent.VK_RIGHT:
						game.move(+1);
						break;
					case KeyEvent.VK_SPACE:
						game.dropDown();
						break;
					} 
			}
			
			public void keyReleased(KeyEvent e) {}
		});
		
		
		new Thread() {
			@Override public void run() {
				while (true) {
					try {
						if(game.board[game.baseCoordinates[1]][game.baseCoordinates[0]] != game.backgroundColor || 
								game.board[game.currentCoord[1]][game.currentCoord[0]] == game.steadyPieceColor){
							game.isGamePlayed = false;
						}
						if (game.isGamePlayed) {
							Thread.sleep(500);
							game.dropDown();
						}
					} 
					catch (InterruptedException error) {}
				}
			}
		}.start();
	}
}