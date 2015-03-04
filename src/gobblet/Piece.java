package gobblet;

/**
 * piece
 */
public class Piece {

	//describe pieces properties
	public static final int BIG = 4;
	public static final int MEDIUM = 3;
	public static final int SMALL = 2;
	public static final int TINY = 1;

	//describe piece(red or black)
	public static final int RED = -1;
	public static final int BLACK = 1;

	/**
	 * piece size
	 */
	private int size;
	
	/**
	 * type of piece
	 */
	private int color;

	public Piece() {
		this(MEDIUM, BLACK);
	}

	public Piece(int size, int owner) {
		this.size = size;
		this.color = owner;
	}

	public int getSize() {
		return size;
	}

	public int getColor() {
		return color;
	}

}
