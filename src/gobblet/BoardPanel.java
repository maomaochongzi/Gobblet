package gobblet;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * show chess panel 
 */
@SuppressWarnings("serial")
public class BoardPanel extends JPanel implements Observer {

	/**
	 * chess board
	 */
	private Board board;

	/**
	 * Ai player
	 */
	private AIPlayer ai;

	/**
	 * current state for the game
	 */
	private boolean playing;

	/**
	 * thread pools, we can save time
	 */
	private ExecutorService service;

	/**
	 * game interface
	 */
	private GameFrame gameFrame;

	/**
	 * the leaf node of the tree
	 */
	private DefaultMutableTreeNode root;

	/**
	 * constructor
	 * 
	 * @param gameFrame
	 *            game ui
	 * @param root
	 *            root of the tree
	 */
	public BoardPanel(GameFrame gameFrame, DefaultMutableTreeNode root) {
		this.gameFrame = gameFrame;
		this.root = root;

		ai = new AIPlayer();
		board = new Board();

		service = Executors.newFixedThreadPool(1);
		board.addObserver(this);

		playing = true;

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// if current player is not human, it is not supported any mouse event
				if (!playing)
					return;
				double cellWidth = getWidth() / 6.0;
				double cellHeight = getHeight() / 4.0;
				int row = (int) (e.getY() / cellHeight);
				int col = (int) (e.getX() / cellWidth);

				if (e.getButton() == MouseEvent.BUTTON1) { //  left click
					board.select(row, col);
					repaint();
				} else if (e.getButton() == MouseEvent.BUTTON3) { // right click
					movePiece(row, col);
				}
			}
		});

	}

	/**
	 * using mouse to move chess
	 * 
	 * @param row
	 * @param col
	 */
	private void movePiece(int row, int col) {
		if (board.move(row, col)) { // human player move
			repaint();
			if (board.hasWon()) { // decide which one will move
				playing = false;
				gameFrame.enableStartButton();
				showWinner();
			} else {
				// if no one can move, so the player will use the algorithm to find next move
				aiMove();
			}
		}
	}

	/**
	 * AI player will move the chess
	 */
	private void aiMove() {
		// create a single thread will move
		final Future<Move> task = service.submit(new Callable<Move>() {
			@Override
			public Move call() throws Exception {
				return ai.getMove(board, root);
			}
		});

		// use the next algorithm to find the best move
		playing = false;
		gameFrame.updateMessage("Ai player is counting the position that will move");
		new Timer(400, new ActionListener() {
			private int count = 0;
			private Move move = null;

			@Override
			public void actionPerformed(ActionEvent e) {
				// check the AI player moved position thread is finished or not
				try {
					move = task.get();
					gameFrame.refreshTree();
					if (move == null) {
						gameFrame.updateMessage("current don't have the piece which you can't move");
						board.switchCurrentPlayer();
						((Timer) e.getSource()).stop();
						return;
					}
				} catch (Exception ex) {

				}
				if (move == null) {
					return;
				}
				// thread is finished, and return the move object, so the begin to move chess
				if (count < 3) { 
					if (count % 2 == 0) {
						board.select(move.getFromRow(), move.getFromCol());
						repaint();
					} else {
						board.unselect();
						repaint();
					}
				} else if (count == 4) { // move the chess piece
					board.move(move.getToRow(), move.getToCol());
					repaint();
					if (board.hasWon()) {
						playing = false;
						gameFrame.enableStartButton();
						showWinner();
						((Timer) e.getSource()).stop();
					}
				} else if (count <= 6) { 
					if (count % 2 == 0) {
						board.select();
						repaint();
					} else {
						board.unselect();
						repaint();
					}
				} else if (count > 6) { // the move is finished
					((Timer) e.getSource()).stop();
					playing = true;
					if (board.getCurrentPlayer() == Piece.BLACK) {
						gameFrame.updateMessage("please the black player move");
					} else {
						gameFrame.updateMessage("please the red player move");
					}
				}

				count++;
			}
		}).start();

	}

	/**
	 * when the game end, show the winner
	 */
	private void showWinner() {
		// because the after the move will change player, if current player is black, so the red plater will win.
		if (board.getWinner() == Piece.RED) {
			JOptionPane.showMessageDialog(gameFrame, "red player win", "Game Over",
					JOptionPane.INFORMATION_MESSAGE);
			gameFrame.updateMessage("red player win");
		} else {
			JOptionPane.showMessageDialog(gameFrame, "black player win", "Game Over",
					JOptionPane.INFORMATION_MESSAGE);
			gameFrame.updateMessage("black player win");
		}
	}

	/**
	 * draw the chess board
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		Graphics2D g2 = (Graphics2D) g;
		board.paint(getWidth(), getHeight(), g2);
	}

	/**
	 * restart
	 */
	public void restart() {
		board = new Board();
		playing = true;
		repaint();
	}

	/**
	 * update the news 
	 */
	@Override
	public void update(Observable o, Object arg) {
		gameFrame.updateMessage(arg.toString());
	}

}
