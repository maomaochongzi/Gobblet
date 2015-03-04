package gobblet;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * The interface for the game
 */
@SuppressWarnings("serial")
public class GameFrame extends JFrame {

	private BoardPanel board;
	private JButton startButton;
	private JTextField msgTextField;

	private DefaultMutableTreeNode root;
	private JTree gameTree;
	private JScrollPane scrollPane;

	public GameFrame() {
		// create member
		root = new DefaultMutableTreeNode("max-min tree");
		board = new BoardPanel(this, root);

		gameTree = new JTree(root);

		startButton = new JButton("play again");
		startButton.setEnabled(false);
		msgTextField = new JTextField();
		msgTextField.setEditable(false);

		// the layout for the interface 
		GridBagConstraints c = new GridBagConstraints();
		setLayout(new GridBagLayout());

		c.insets = new Insets(5, 5, 5, 5);

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		add(board, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(msgTextField, c);

		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.NONE;
		add(startButton, c);

		c.gridx = 2;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 2;
		c.weightx = 0.3;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		scrollPane = new JScrollPane(gameTree,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(scrollPane, c);

		//when you click the bottom the game start again 
		startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				restart();
			}

		});

		// other
		setTitle("gobblet chess");
		setSize(780, 450);
		setResizable(false);
		setLocationRelativeTo(null); // center

		updateMessage("please, black player to move");
	}

	/**
	 * refresh the new below the user interface
	 * 
	 * @param message
	 */
	public void updateMessage(String message) {
		msgTextField.setText(message);
	}

	/**
	 * stop using" player again button 
	 */
	public void disableStartButton() {
		startButton.setEnabled(false);
	}

	/**
	 * start using"  player again button
	 */
	public void enableStartButton() {
		startButton.setEnabled(true);
	}

	/**
	 * start again
	 */
	public void restart() {
		board.restart();
		disableStartButton();
		// get rid of the nodes of the max-min tree
		DefaultTreeModel model = (DefaultTreeModel) gameTree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		root.removeAllChildren();
		model.reload(root);
	}

	/**
	 * refresh the tree
	 */
	public void refreshTree() {
		DefaultTreeModel model = (DefaultTreeModel) gameTree.getModel();
		model.reload();
	}

	/**
	 * program start from here
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// try {
		// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		// } catch (Exception e) {
		// }
		JFrame frame = new GameFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
