package gobblet;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

public class AIPlayer {

	public static final int LEVEL = 3;

	/**
	 * 
	 * @param board
	 *            chess board
	 * @param root
	 *            Game Tree root node
	 * @return best move
	 */
	public Move getMove(Board board, DefaultMutableTreeNode root) {
		// delete all the nodes that move
		root.removeAllChildren();

		// find the position the all possible move 
		int player = board.getCurrentPlayer();
		List<Move> moves = board.getAllMoves(player);

		// best move
		Move bestMove = null;

		if (player == Piece.RED) {
			// if current player is red, find the lowest score to move, the more score for the red, the red is betters
			int bestScore = Integer.MIN_VALUE;
			DefaultMutableTreeNode bestChild = null;
			for (Move move : moves) {
				Board newBoard = board.move(move);
				if (newBoard != null) {
					DefaultMutableTreeNode node = new DefaultMutableTreeNode(
							"red " + move);
					root.add(node);
					//using minmax algorithm to calculate the score
					int score = minmax(newBoard, LEVEL - 1, Piece.BLACK, node);
					if (score > bestScore) {
						bestScore = score;
						bestMove = move;
						bestChild = node;
					}
				}
			}
			// put a * on the best move
			if (bestChild != null) {
				bestChild.setUserObject("* "
						+ bestChild.getUserObject().toString());
			}
		} else {
			//if current player is black, find the lowest score to move, the more score for the black, the black is betters
			int bestScore = Integer.MAX_VALUE;
			DefaultMutableTreeNode bestChild = null;
			for (Move move : moves) {
				Board newBoard = board.move(move);
				if (newBoard != null) {
					DefaultMutableTreeNode node = new DefaultMutableTreeNode(
							"black " + move);
					root.add(node);
					// use the minmax algorithm to calculate the score
					int score = minmax(newBoard, LEVEL - 1, Piece.RED, node);
					if (score < bestScore) {
						bestScore = score;
						bestMove = move;
						bestChild = node;
					}
				}
			}
			// put a * on the best move
			if (bestChild != null) {
				bestChild.setUserObject("* "
						+ bestChild.getUserObject().toString());
			}
		}
		return bestMove;
	}

	
	/**
	 * MinMax algorithm,http://en.wikipedia.org/wiki/Minimax
	 * 
	 * 
	 * 
	 * @param board chess board
	 * @param level current move 
	 * @param player  current player
	 * @param node the node in the tree which you move
	 * @return score
	 */
	private int minmax(Board board, int level, int player,
			DefaultMutableTreeNode node) {
		// if the player win, or the last move, show the total score
		if (board.hasWon() || level == 0)
			return board.evaluate();

		//find the position which we can move
		List<Move> movements = board.getAllMoves(player);

		if (player == Piece.RED) {
			// if the current player is red. so we can find the highest score
			int bestScore = Integer.MIN_VALUE;
			DefaultMutableTreeNode bestChild = null;
			for (Move move : movements) {
				Board newBoard = board.move(move);
				if (newBoard != null) {
					DefaultMutableTreeNode child = new DefaultMutableTreeNode(
							"红 " + move);
					node.add(child);
					int score = minmax(newBoard, level - 1,
							player == Piece.RED ? Piece.BLACK : Piece.RED,
							child);
					if (score > bestScore) {
						bestScore = score;
						bestChild = child;
					}
				}
			}
			// find the best move to add *  
			if (bestChild != null) {
				bestChild.setUserObject("* "
						+ bestChild.getUserObject().toString());
			}
			return bestScore;
		} else {
			// if the current player is black, so we find the lowest score to move
			int bestScore = Integer.MAX_VALUE;
			DefaultMutableTreeNode bestChild = null;
			for (Move move : movements) {
				Board newBoard = board.move(move);
				if (newBoard != null) {
					DefaultMutableTreeNode child = new DefaultMutableTreeNode(
							"black " + move);
					node.add(child);
					int score = minmax(newBoard, level - 1,
							player == Piece.RED ? Piece.BLACK : Piece.RED,
							child);
					if (score < bestScore) {
						bestScore = score;
						bestChild = child;
					}
				}
			}
			//we add * on the best move
			if (bestChild != null) {
				bestChild.setUserObject("* "
						+ bestChild.getUserObject().toString());
			}
			return bestScore;
		}
	}

}
