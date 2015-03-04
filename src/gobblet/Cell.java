package gobblet;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Stack;

/**
 * a cell in the chessboard, one can contain several pieces 
 */
public class Cell {

	/**
	 * one cell contains pieces
	 */
	private Stack<Piece> pieces;

	/**
	 * the position of one cell
	 */
	private int row;
	private int col;

	/**
	 * current style is selected
	 */
	private boolean selected;

	public Cell() {
		this(0, 0);
	}

	public Cell(int row, int col) {
		this.row = row;
		this.col = col;

		pieces = new Stack<Piece>();
		selected = false;
	}

	/**
	 * copy cell
	 * 
	 * @param cell
	 */
	public Cell(Cell cell) {
		row = cell.row;
		col = cell.col;
		pieces = new Stack<Piece>();
		pieces.addAll(cell.pieces);
	}

	/**
	 * draw cell
	 * 
	 * @param width
	 * @param height
	 * @param g2
	 */
	public void draw(double width, double height, Graphics2D g2) {

		if (!pieces.isEmpty()) {
			Piece piece = pieces.peek();

			// chess color
			Color color = piece.getColor() == Piece.RED ? Color.RED
					: Color.BLACK;
			g2.setColor(color);

			// draw chess cell
			int size = piece.getSize();
			int factor = 6;
			double cellWidth = width / factor;
			double cellHeight = height / factor;
			double pieceX = width * col + cellWidth * ((factor - size) / 2.0);
			double pieceY = height * row + cellHeight * ((factor - size) / 2.0);
			Ellipse2D ellipse = new Ellipse2D.Double(pieceX, pieceY, cellWidth
					* size, cellHeight * size);
			g2.fill(ellipse);

			//if cell is selected, show the board
			if (selected) {
				Rectangle2D rect = new Rectangle2D.Double(pieceX, pieceY,
						cellWidth * size, cellHeight * size);
				g2.draw(rect);
			}
		}
	}

	/**
	 * if current cell is selected, true, otherwise is false
	 * 
	 * @return
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * the state of the selected cell
	 * 
	 * @param selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * the cell is empty is true or not
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return pieces.isEmpty();
	}

	/**
	 *  put one piece on the chessboard
	 * 
	 * @param piece
	 * @return
	 */

	public boolean push(Piece piece) {
		if (pieces.isEmpty()) {
			pieces.push(piece);
			return true;
		} else if (pieces.peek().getSize() < piece.getSize()) {
			pieces.push(piece);
			return true;
		} else {
			return false;
		}
	}

	/**
	 *  move the top piece into the target piece
	 * 
	 * @param target
	 */
	public void move(Cell target) {
		if (canMove(target)) {
			target.pieces.push(pieces.pop());
		}
	}

	/**
	 * get the size of the highest piece
	 * 
	 * @return
	 */
	public int getTopPieceSize() {
		if (pieces.isEmpty())
			return 0;
		else
			return pieces.peek().getSize();
	}

	/**
	 * check the top piece is belong to player or computer
	 * 
	 * @param player
	 * @return
	 */
	public boolean isTopOwnBy(int player) {
		if (isEmpty()) {
			return false;
		}

		return pieces.peek().getColor() == player;
	}

	/**
	 * 检查是否可以将当前格子中的棋子移动到参数中的格子(cell), check whether or not we can move the cell
	 * 
	 * @param target
	 * @return
	 */
	public boolean canMove(Cell target) {
		//no piece can move
		if (isEmpty() || this.equals(target))
			return false;

		// target chess is empty
		if (target.isEmpty())
			return true;

		return pieces.peek().getSize() > target.pieces.peek().getSize();
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	/**
	 * decide two cell's col number and row number is same
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof Cell) {
			Cell cell = (Cell) other;
			return cell.row == row && cell.col == col;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		if (pieces.isEmpty())
			return "(     )";
		else {
			Piece piece = pieces.peek();
			return String.format("(%+d,%02d)", piece.getColor(),
					piece.getSize());
		}
	}
}
