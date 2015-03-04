package gobblet;

/**
 * the class: move one step for the chess piece
 */
public class Move {
	//begin to move position
	private int fromRow;
	private int fromCol;
	
	//the position that you want to move
	private int toRow;
	private int toCol;

	public Move() {
		this(0, 0, 0, 0);
	}

	public Move(int fromRow, int fromCol, int toRow, int toCol) {
		this.fromRow = fromRow;
		this.fromCol = fromCol;
		this.toRow = toRow;
		this.toCol = toCol;
	}

	public int getFromRow() {
		return fromRow;
	}

	public void setFromRow(int fromRow) {
		this.fromRow = fromRow;
	}

	public int getFromCol() {
		return fromCol;
	}

	public void setFromCol(int fromCol) {
		this.fromCol = fromCol;
	}

	public int getToRow() {
		return toRow;
	}

	public void setToRow(int toRow) {
		this.toRow = toRow;
	}

	public int getToCol() {
		return toCol;
	}

	public void setToCol(int toCol) {
		this.toCol = toCol;
	}

	@Override
	public String toString() {
		return String.format("(%d,%d) -> (%d,%d)", fromRow, fromCol, toRow,
				toCol);
	}

}
