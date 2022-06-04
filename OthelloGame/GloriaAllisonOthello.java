
/* Gloria Jeng Allison Cheng 
 * A replication of Othello 
 */
import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class GloriaAllisonOthello extends JFrame implements ActionListener {

	private PicPanel[][] allPanels;

	private JButton skipButton;

	public final int[] VERTDISP = { 0, -1, -1, -1, 0, 1, 1, 1 }; // do not modify
	public final int[] HORZDISP = { -1, -1, 0, 1, 1, 1, 0, -1 };

	private BufferedImage whitePiece;
	private BufferedImage blackPiece;

	private JLabel blackCountLabel;
	private int blackCount = 2;

	private JLabel whiteCountLabel;
	private int whiteCount = 2;

	private JLabel turnLabel;
	private boolean blackTurn = true;

	public GloriaAllisonOthello() {

		setSize(1200, 950);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setTitle("Othello");
		getContentPane().setBackground(Color.white);

		allPanels = new PicPanel[8][8];

		JPanel gridPanel = new JPanel();
		gridPanel.setLayout(new GridLayout(8, 8, 2, 2));
		gridPanel.setBackground(Color.black);
		gridPanel.setBounds(95, 50, 800, 814);
		gridPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				allPanels[row][col] = new PicPanel(row, col, 76, 100);
				gridPanel.add(allPanels[row][col]);
			}
		}

		try {
			whitePiece = ImageIO.read(new File("white.jpg"));
			blackPiece = ImageIO.read(new File("black.jpg"));

		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(null, "Could not read in the pic");
			System.exit(0);
		}

		skipButton = new JButton("Skip Turn");
		skipButton.addActionListener(this);
		skipButton.setBounds(925, 475, 150, 50);

		blackCountLabel = new JLabel("Black: 2 ");
		blackCountLabel.setFont(new Font("Calibri", Font.PLAIN, 35));
		blackCountLabel.setBounds(925, 150, 275, 50);

		whiteCountLabel = new JLabel("White: 2 ");
		whiteCountLabel.setFont(new Font("Calibri", Font.PLAIN, 35));
		whiteCountLabel.setBounds(925, 225, 275, 50);

		turnLabel = new JLabel("Turn: Black ");
		turnLabel.setFont(new Font("Calibri", Font.PLAIN, 35));
		turnLabel.setBounds(925, 375, 275, 75);

		add(gridPanel);
		add(skipButton);
		add(blackCountLabel);
		add(whiteCountLabel);
		add(turnLabel);

		// insert four initial pieces
		allPanels[3][3].addPiece(Color.WHITE);
		allPanels[3][4].addPiece(Color.BLACK);
		allPanels[4][3].addPiece(Color.BLACK);
		allPanels[4][4].addPiece(Color.WHITE);

		setVisible(true);
	}

	private void updateLabels() {
		whiteCountLabel.setText("White: " + whiteCount);
		blackCountLabel.setText("Black: " + blackCount);

		String turn = "Black";

		if (!blackTurn)
			turn = "White";

		turnLabel.setText("Turn: " + turn);
	}

	// determines if the row/col loc is in bounds
	private boolean isValidCell(int row, int col) {

		return row >= 0 && row < allPanels.length && col >= 0 && col < allPanels.length;
	}

	// when "skip turn" is clicked, the turn is given to the other player and the
	// turn label is updated
	public void actionPerformed(ActionEvent ae) {
		blackTurn = !blackTurn;
		updateLabels();
	}

	class PicPanel extends JPanel implements MouseListener {

		private int row;
		private int col;

		private Color myColor;
		private ArrayList<Integer> neighbors; // neighboring cells that have pieces in them
		// each element corresponds to index of neighborhood array
		// used to reduce which neighbors need to be examined
		// when trying to add a new piece

		public PicPanel(int r, int c, int w, int h) {
			setBackground(Color.white);
			row = r;
			col = c;
			neighbors = new ArrayList<Integer>();
			myColor = null;
			this.addMouseListener(this);
			setLayout(null);
		}

		// this will draw the image
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			if (myColor == null) {
				setBackground(new Color(0, 108, 89));
			} else if (myColor == Color.white) {
				g.drawImage(whitePiece, 0, 0, this);
			} else {
				g.drawImage(blackPiece, 0, 0, this);
			}

		}

		// call this to place a piece into an empty cell
		// updates neighboring elements neighbors AL
		public void addPiece(Color pieceColor) {

			myColor = pieceColor;

			// for all 8 possible neighbors
			for (int i = 0; i < HORZDISP.length; i++) {
				int nextRow = row + VERTDISP[i];
				int nextCol = col + HORZDISP[i];

				// if a neighbor exists
				if (isValidCell(nextRow, nextCol)) {

					// we are updating the neighbor's AL so we want the opposite
					// direction of where we currently are
					// displacement arrays have been rigged so that the opposite direction is 4
					// indicies away
					int neighbor = i + 4;
					if (neighbor >= HORZDISP.length)
						neighbor = i - 4;

					allPanels[nextRow][nextCol].neighbors.add(neighbor);
				}

			}

			repaint();
		}

		// changes the color of an existing piece to the opposite
		public void flip() {
			if (myColor == null)
				return;
			if (myColor == Color.black)
				myColor = Color.white;
			else
				myColor = Color.black;

			repaint();
		}

		// reacts to the user clicking on the panel
		public void mouseClicked(MouseEvent me) {

			// check if what they clicked is a valid spot
			if (isValidCell(this.row, this.col) && allPanels[this.row][this.col].myColor == null) {

				neighbors = adjacent(this.row, this.col);
				if (neighbors.size() != 0) {
					// flip pieces and add their current move to the board, updates the piece
					// counters and changes to other player's turn
					if (validMoveAndFlip(this.row, this.col)) {
						this.addPiece(myColor);
						blackTurn = !blackTurn;
						updateLabels();
					}
					// if they clicked a spot with an adjacent piece but a piece of the opponents
					// color can't be flipped
					else {
						displayMessage("Can not flip a piece");
					}

				}
				// clicked a spot with no adjacent pieces
				else {
					displayMessage("Can not move here, no adjacent piece");
				}

			}

		}

		// check if valid move, if it is --> flip piece
		private boolean validMoveAndFlip(int r, int c) {

			// change color depending on whose color it is
			Color opsColor;
			if (blackTurn) {
				myColor = Color.BLACK;
				opsColor = Color.WHITE;
			} else {
				myColor = Color.WHITE;
				opsColor = Color.BLACK;
			}

			boolean goodMove = false;

			// loop through neighbors array and check every direction that has a neighbor
			for (int i = 0; i < neighbors.size(); i++) {
				int flipCount = 0;
				int next_r = r + HORZDISP[neighbors.get(i)];
				int next_c = c + VERTDISP[neighbors.get(i)];
				ArrayList<int[]> track = new ArrayList<int[]>();
				while (isValidCell(next_r, next_c) && allPanels[next_r][next_c].myColor != null
						&& allPanels[next_r][next_c].myColor.equals(opsColor)) {
					int point[] = { next_r, next_c };
					track.add(point);
					next_r += HORZDISP[neighbors.get(i)];
					next_c += VERTDISP[neighbors.get(i)];
				}

				// flip pieces if it is a valid move.
				if (isValidCell(next_r, next_c) && allPanels[next_r][next_c].myColor != null
						&& allPanels[next_r][next_c].myColor.equals(myColor) && track.size() != 0) {
					for (int j = 0; j < track.size(); j++) {
						allPanels[track.get(j)[0]][track.get(j)[1]].flip();
						// count how many pieces are flipped
						flipCount++;
					}

					// update counters based on how many pieces was flipped
					if (myColor.equals(Color.BLACK)) {
						blackCount += flipCount;
						whiteCount -= flipCount;
					} else {
						whiteCount += flipCount;
						blackCount -= flipCount;

					}
					goodMove = true;

				}

			}
			// if it is a good move, add another to pieceCount (depending on what color you
			// are) to account for the added piece
			if (goodMove) {
				if (myColor.equals(Color.BLACK)) {
					blackCount += 1;
				} else {
					whiteCount += 1;
				}
			}
			// reset color to null
			else {
				myColor = null;
			}
			return goodMove;

		}

		// find neighboring pieces for a certain pic panel
		private ArrayList<Integer> adjacent(int r, int c) {
			ArrayList<Integer> a = new ArrayList<Integer>();
			for (int i = 0; i < VERTDISP.length; i++) {
				// if there's neighbor, add to arrayList
				if (isValidCell(r + HORZDISP[i], c + VERTDISP[i])
						&& allPanels[r + HORZDISP[i]][c + VERTDISP[i]].myColor != null) {
					a.add(i);
				}
			}
			return a;
		}

		private void displayMessage(String message) {
			JOptionPane.showMessageDialog(null, message);
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// do not implement

		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// do not implement

		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// do not implement

		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// do not implement

		}
	}

	public static void main(String[] args) {
		new GloriaAllisonOthello();
	}
}
