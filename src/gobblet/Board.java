package gobblet;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 *game board, main class 
 */
public class Board extends Observable {
	/**
	 * row number
	 */
	public static final int ROWS = 4;
	/**
	 * col number
	 */
	public static final int COLS = ROWS;

	/**
	 *the number of unused cell
	 */
	public static final int UNUSED_NUM = 3;

	/**
	 * chess board  background color
	 */
	public static final Color BACKGROUND = new Color(238, 238, 238);
	/**
	 * chess board foreground color
	 */
	public static final Color FOREGROUND = new Color(184, 202, 238);

	/**
	 * current player
	 */
	private int currentPlayer;

	/**
	 * middle 4*4 player
	 */
	private Cell[][] cells = new Cell[ROWS][COLS];

	/**
	 * put chess pieces on the used cell of black player
	 */
	private Cell[] blackUnusedCells = new Cell[UNUSED_NUM];
	/**
	 * put chess pieces on the used cell of red player
	 */
	private Cell[] aiUnusedCells = new Cell[UNUSED_NUM];

	/**
	 * current the checked chess
	 */
	private Cell selectedCell;

	/**
	 * winner
	 */
	private int winner;

	// currents

	public Board() {
		// black chess move first
		currentPlayer = Piece.BLACK;

		selectedCell = null;

		// board 4x4 grid
		for (int row = 0; row < cells.length; row++) {
			for (int col = 0; col < cells[row].length; col++) {
				cells[row][col] = new Cell(row, col);
			}
		}

		// human player put the piece which don't use
		for (int i = 0; i < blackUnusedCells.length; i++) {
			Cell cell = new Cell(i, COLS + 1);
			cell.push(new Piece(Piece.TINY, Piece.BLACK));
			cell.push(new Piece(Piece.SMALL, Piece.BLACK));
			cell.push(new Piece(Piece.MEDIUM, Piece.BLACK));
			cell.push(new Piece(Piece.BIG, Piece.BLACK));
			blackUnusedCells[i] = cell;
		}
		// AI player put the piece which don't use
		for (int i = 0; i < aiUnusedCells.length; i++) {
			Cell cell = new Cell(i, COLS);
			cell.push(new Piece(Piece.TINY, Piece.RED));
			cell.push(new Piece(Piece.SMALL, Piece.RED));
			cell.push(new Piece(Piece.MEDIUM, Piece.RED));
			cell.push(new Piece(Piece.BIG, Piece.RED));
			aiUnusedCells[i] = cell;
		}
	}


	public Board(Board board) {

		for (int row = 0; row < board.cells.length; row++) {
			for (int col = 0; col < board.cells[row].length; col++) {
				cells[row][col] = new Cell(board.cells[row][col]);
			}
		}

		// human player put the piece which don't use
		for (int i = 0; i < board.blackUnusedCells.length; i++) {
			blackUnusedCells[i] = new Cell(board.blackUnusedCells[i]);
		}
		// AI player put the piece which don't use
		for (int i = 0; i < board.aiUnusedCells.length; i++) {
			aiUnusedCells[i] = new Cell(board.aiUnusedCells[i]);
		}
	}

	/**
	 * draw the chess board
	 * 
	 * @param width
	 *            chess board withd
	 * @param height
	 *            chess board height
	 * @param g2
	 */
	public void paint(double width, double height, Graphics2D g2) {
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		double adjustedWidth = width - 1.0; // 减1调整边界，避免将网格线画到棋盘外边
		double adjustedHeight = height - 1.0;
		double cellWidth = adjustedWidth / (COLS + 2);
		double cellHeight = adjustedHeight / ROWS;

		
		g2.setColor(BACKGROUND);
		g2.fill(new Rectangle2D.Double(0, 0, adjustedWidth, adjustedHeight));

		
		g2.setColor(FOREGROUND);
		for (int i = 0; i <= ROWS; i++) {
			Line2D hSeparator = new Line2D.Double(0, i * cellHeight, COLS
					* cellWidth, i * cellHeight);
			g2.draw(hSeparator);
		}
		for (int i = 0; i <= COLS; i++) {
			Line2D vSeparator = new Line2D.Double(i * cellWidth, 0, i
					* cellWidth, adjustedHeight);
			g2.draw(vSeparator);
		}


		for (int row = 0; row < cells.length; row++) {
			for (int col = 0; col < cells[row].length; col++) {
				cells[row][col].draw(cellWidth, cellHeight, g2);
			}
		}
		for (int i = 0; i < blackUnusedCells.length; i++) {
			blackUnusedCells[i].draw(cellWidth, cellHeight, g2);
		}
		for (int i = 0; i < aiUnusedCells.length; i++) {
			aiUnusedCells[i].draw(cellWidth, cellHeight, g2);
		}
	}

	/**
	 * find player all the positions that can move
	 * 
	 * @param play
	 * @return
	 */
	public List<Move> getAllMoves(int player) 
		List<Cell> ownCells = new ArrayList<Cell>();
		for (int row = 0; row < cells.length; row++) {
			for (int col = 0; col < cells[row].length; col++) {
				if (cells[row][col].isTopOwnBy(player)) {
					ownCells.add(cells[row][col]);
				}
			}
		}
		if (player == Piece.BLACK) {
			for (int i = 0; i < blackUnusedCells.length; i++) {
				if (blackUnusedCells[i].isTopOwnBy(player)) {
					ownCells.add(blackUnusedCells[i]);
				}
			}
		} else {
			for (int i = 0; i < aiUnusedCells.length; i++) {
				if (aiUnusedCells[i].isTopOwnBy(player)) {
					ownCells.add(aiUnusedCells[i]);
				}
			}
		}

	
		List<Move> moves = new ArrayList<Move>();
		for (Cell cell : ownCells) {
			for (int row = 0; row < cells.length; row++) {
				for (int col = 0; col < cells[row].length; col++) {
					if (cell.canMove(cells[row][col])) {
						moves.add(new Move(cell.getRow(), cell.getCol(),
								cells[row][col].getRow(), cells[row][col]
										.getCol()));
					}
				}
			}
		}

		return moves;
	}

	/**
	 * @param move
	 * @return copy the new chess boards
	 */
	public Board move(Move move) {
		int fromRow = move.getFromRow();
		int fromCol = move.getFromCol();
		int toRow = move.getToRow();
		int toCol = move.getToCol();

		//find the cell which the chess piece will move
		Cell fromCell = null;
		if (fromCol == COLS) {
			fromCell = aiUnusedCells[fromRow];
		} else if (fromCol == COLS + 1) {
			fromCell = blackUnusedCells[fromRow];
		} else {
			fromCell = cells[fromRow][fromCol];
		}

		// chess piece will move to cell
		Cell toCell = cells[toRow][toCol];

		//decide the chess piece can move fromCell to toCell or not
		if (fromCell.canMove(toCell)) {
			Board board = new Board(this);
			Cell newFromCell = null;
			if (fromCol == COLS) {
				newFromCell = board.aiUnusedCells[fromRow];
			} else if (fromCol == COLS + 1) {
				newFromCell = board.blackUnusedCells[fromRow];
			} else {
				newFromCell = board.cells[fromRow][fromCol];
			}
			newFromCell.move(board.cells[toRow][toCol]);
			return board;
		} else {
			return null;
		}
	}

/*
Evaluate rules：
1.A line in the chess board, there is not chess piece, the grade is 0。
2.A line in the chess board, there is 1 red player piece(don’t has black player piece),grade is 1.
3. A line in the chess board, there is 2 red player(don’t has black player largest piece),grade is 10。
4. A line in the chess board, there is red player(don’t has black player largest piece),grade is 100.
5. A line in the chess board, there is red player(don’t has black player largest piece),grade is 1000.
6.A line in the chess board, there is 1 black player piece(don’t has black player piece),grade is 1.
7. A line in the chess board, there is 2 black player(don’t has red player largest piece),grade is 10。
8. A line in the chess board, there is balck player(don’t has red player largest piece),grade is 100.
9.A line in the chess board, there is balck player(don’t has red player largest piece),grade is 1000.
*/

	public int evaluate() {
	
		int score = 0;

		
		int redPieces;
		
		int blackPieces;
		
		int additionScore;
		double factor = 2;
		boolean redBig;
		boolean blackBig;

	
		for (int row = 0; row < ROWS; row++) {
			redPieces = 0;
			blackPieces = 0;
			additionScore = 0;
			redBig = false;
			blackBig = false;
			for (int col = 0; col < COLS; col++) {
				if (cells[row][col].isTopOwnBy(Piece.RED)) {
					redPieces++;
					if (cells[row][col].getTopPieceSize() == Piece.BIG) {
						redBig = true;
					}
					additionScore += (int) Math.pow(factor,
							cells[row][col].getTopPieceSize());
				} else if (cells[row][col].isTopOwnBy(Piece.BLACK)) {
					blackPieces++;
					if (cells[row][col].getTopPieceSize() == Piece.BIG) {
						blackBig = true;
					}
					additionScore -= (int) Math.pow(factor,
							cells[row][col].getTopPieceSize());
				}
			}
			score += (computeScore(redPieces, blackPieces, redBig, blackBig) + additionScore);
		}

		
		for (int col = 0; col < COLS; col++) {
			redPieces = 0;
			blackPieces = 0;
			additionScore = 0;
			redBig = false;
			blackBig = false;
			for (int row = 0; row < ROWS; row++) {
				if (cells[row][col].isTopOwnBy(Piece.RED)) {
					redPieces++;
					if (cells[row][col].getTopPieceSize() == Piece.BIG) {
						redBig = true;
					}
					additionScore += (int) Math.pow(factor,
							cells[row][col].getTopPieceSize());
				} else if (cells[row][col].isTopOwnBy(Piece.BLACK)) {
					blackPieces++;
					if (cells[row][col].getTopPieceSize() == Piece.BIG) {
						blackBig = true;
					}
					additionScore -= (int) Math.pow(factor,
							cells[row][col].getTopPieceSize());
				}
			}
			score += (computeScore(redPieces, blackPieces, redBig, blackBig) + additionScore);
		}

		redPieces = 0;
		blackPieces = 0;
		additionScore = 0;
		redBig = false;
		blackBig = false;
		for (int row = 0, col = 0; row < ROWS && col < COLS; row++, col++) {
			if (cells[row][col].isTopOwnBy(Piece.RED)) {
				redPieces++;
				if (cells[row][col].getTopPieceSize() == Piece.BIG) {
					redBig = true;
				}
				additionScore += (int) Math.pow(factor,
						cells[row][col].getTopPieceSize());
			} else if (cells[row][col].isTopOwnBy(Piece.BLACK)) {
				blackPieces++;
				if (cells[row][col].getTopPieceSize() == Piece.BIG) {
					blackBig = true;
				}
				additionScore -= (int) Math.pow(factor,
						cells[row][col].getTopPieceSize());
			}
		}
		score += (computeScore(redPieces, blackPieces, redBig, blackBig) + additionScore);

		redPieces = 0;
		blackPieces = 0;
		additionScore = 0;
		redBig = false;
		blackBig = false;
		for (int row = ROWS - 1, col = 0; row >= 0 && col < COLS; row--, col++) {
			if (cells[row][col].isTopOwnBy(Piece.RED)) {
				redPieces++;
				if (cells[row][col].getTopPieceSize() == Piece.BIG) {
					redBig = true;
				}
				additionScore += (int) Math.pow(factor,
						cells[row][col].getTopPieceSize());
			} else if (cells[row][col].isTopOwnBy(Piece.BLACK)) {
				blackPieces++;
				if (cells[row][col].getTopPieceSize() == Piece.BIG) {
					blackBig = true;
				}
				additionScore -= (int) Math.pow(factor,
						cells[row][col].getTopPieceSize());
			}
		}
		score += (computeScore(redPieces, blackPieces, redBig, blackBig) + additionScore);

		// unused chess piece include
		for (int i = 0; i < UNUSED_NUM; i++) {
			score += (int) Math.pow(factor, aiUnusedCells[i].getTopPieceSize());
			score -= (int) Math.pow(factor,
					blackUnusedCells[i].getTopPieceSize());
		}

		return score;
	}

	/**
	 *  function evaluate to count the score
	 * 
	 * @param redPieces
	 *            in one line the number of pieces of the AI player
	 * @param blackPieces
	 *            in one line the number of pieces of the human player
	 * @param redBig
	 *            the line have the big chess of red side
	 * @param blackBig
	 *            the line have the big chess of black side
	 * @return
	 */
	private int computeScore(int redPieces, int blackPieces, boolean redBig,
			boolean blackBig) {
		if (redPieces == 0) { // only black chess piece
			return -(int) Math.pow(10, blackPieces);
		} else if (blackPieces == 0) { // only red chess piece
			return (int) Math.pow(10, redPieces);
		} else if (!redBig && blackPieces > 1) { 
			return -(int) Math.pow(10, blackPieces);
		} else if (!blackBig && redPieces > 1) { 
			return (int) Math.pow(10, redPieces);
		} else {
			return 0;
		}
	}

	/**
	 * 
	 * @param row
	 * @param col
	 */
	public void select(int row, int col) {
		if (row >= 0 && row < ROWS && col >= 0 && col < COLS
				&& cells[row][col].isTopOwnBy(currentPlayer)) { // 4x4 grid
			if (selectedCell != null) {
				selectedCell.setSelected(false);
			}
			cells[row][col].setSelected(true);
			selectedCell = cells[row][col];
		} else if (col == COLS + 1 && row >= 0 && row < UNUSED_NUM
				&& blackUnusedCells[row].isTopOwnBy(currentPlayer)) { // black player
			if (selectedCell != null) {
				selectedCell.setSelected(false);
			}
			blackUnusedCells[row].setSelected(true);
			selectedCell = blackUnusedCells[row];
		} else if (col == COLS && row >= 0 && row < UNUSED_NUM
				&& aiUnusedCells[row].isTopOwnBy(currentPlayer)) { // red player
			if (selectedCell != null) {
				selectedCell.setSelected(false);
			}
			aiUnusedCells[row].setSelected(true);
			selectedCell = aiUnusedCells[row];
		}
	}

	/**
	 * move the checked piece
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public boolean move(int row, int col) {
		if (row >= 0 && row < ROWS && col >= 0 && col < COLS) {
			if (selectedCell != null && selectedCell.isTopOwnBy(currentPlayer)) { //player only can move own chess piece
				if (selectedCell.canMove(cells[row][col])) {
					selectedCell.move(cells[row][col]);
					//after move the chess piece still is checked
					selectedCell.setSelected(false);
					selectedCell = cells[row][col];
					selectedCell.setSelected(true);

					// change player
					currentPlayer = currentPlayer == Piece.BLACK ? Piece.RED
							: Piece.BLACK;
					return true;
				} else {
					setChanged();
					notifyObservers("you cannot to move to here");
					return false;
				}
			} else {
				setChanged();
				notifyObservers("you must choose you own chess piece, you can mobe");
				return false;
			}
		} else {
			setChanged();
			notifyObservers("you can not move to here");
			return false;
		}
	}

	/**
	 * get current player
	 * 
	 * @return
	 */
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	/**
	 * chage player
	 */
	public void switchCurrentPlayer() {
		currentPlayer = currentPlayer == Piece.BLACK ? Piece.RED : Piece.BLACK;
	}

	/**
	 * decide the player win or not, win return true, false return false
	 * 
	 * @return
	 */
	public boolean hasWon() {
		//a line how many piece Ai player numbers
		int aiPieces;
		// aline how many piece human playernumbers
		int humanPieces;

		boolean done = false;

		// 4 col
		for (int row = 0; row < ROWS && !done; row++) {
			aiPieces = 0;
			humanPieces = 0;
			for (int col = 0; col < COLS; col++) {
				if (cells[row][col].isTopOwnBy(Piece.RED)) {
					aiPieces++;
				} else if (cells[row][col].isTopOwnBy(Piece.BLACK)) {
					humanPieces++;
				}
			}
			if (aiPieces == ROWS && humanPieces == ROWS) {
				done = true;
				winner = currentPlayer;
			} else if (humanPieces == ROWS) {
				done = true;
				winner = Piece.BLACK;
			} else if (aiPieces == ROWS) {
				done = true;
				winner = Piece.RED;
			}
		}

		//four col
		for (int col = 0; col < COLS && !done; col++) {
			aiPieces = 0;
			humanPieces = 0;
			for (int row = 0; row < ROWS; row++) {
				if (cells[row][col].isTopOwnBy(Piece.RED)) {
					aiPieces++;
				} else if (cells[row][col].isTopOwnBy(Piece.BLACK)) {
					humanPieces++;
				}
			}
			if (aiPieces == ROWS && humanPieces == ROWS) {
				done = true;
				winner = currentPlayer;
			} else if (humanPieces == ROWS) {
				done = true;
				winner = Piece.BLACK;
			} else if (aiPieces == ROWS) {
				done = true;
				winner = Piece.RED;
			}
		}

		// diagonal line
		aiPieces = 0;
		humanPieces = 0;
		for (int row = 0, col = 0; row < ROWS && col < COLS && !done; row++, col++) {
			if (cells[row][col].isTopOwnBy(Piece.RED)) {
				aiPieces++;
			} else if (cells[row][col].isTopOwnBy(Piece.BLACK)) {
				humanPieces++;
			}
		}
		if (aiPieces == ROWS && humanPieces == ROWS) {
			done = true;
			winner = currentPlayer;
		} else if (humanPieces == ROWS) {
			done = true;
			winner = Piece.BLACK;
		} else if (aiPieces == ROWS) {
			done = true;
			winner = Piece.RED;
		}

		// diagonal line
		aiPieces = 0;
		humanPieces = 0;
		for (int row = ROWS - 1, col = 0; row >= 0 && col < COLS && !done; row--, col++) {
			if (cells[row][col].isTopOwnBy(Piece.RED)) {
				aiPieces++;
			} else if (cells[row][col].isTopOwnBy(Piece.BLACK)) {
				humanPieces++;
			}
		}
		if (aiPieces == ROWS && humanPieces == ROWS) {
			done = true;
			winner = currentPlayer;
		} else if (humanPieces == ROWS) {
			done = true;
			winner = Piece.BLACK;
		} else if (aiPieces == ROWS) {
			done = true;
			winner = Piece.RED;
		}

		return done;
	}

	public void select() {
		if (selectedCell != null) {
			selectedCell.setSelected(true);
		}
	}

	/**
	 * cancel selectedCell
	 */
	public void unselect() {
		if (selectedCell != null) {
			selectedCell.setSelected(false);
		}
	}

	/**
	 * get winner
	 * 
	 * @return
	 */
	public int getWinner() {
		return winner;
	}

	public void print() {
		for (int row = 0; row < cells.length; row++) {
			for (int col = 0; col < cells[row].length; col++) {
				System.out.print(String.format("%8s", cells[row][col]));
			}
			if (row < aiUnusedCells.length) {
				System.out.print(String.format("%8s", aiUnusedCells[row]));
			} else {
				System.out.print(String.format("%8s", ""));
			}

			if (row < blackUnusedCells.length) {
				System.out.print(String.format("%8s", blackUnusedCells[row]));
			} else {
				System.out.print(String.format("%8s", ""));
			}
			System.out.println();
		}
	}

}
