import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class AllisonWordle extends JFrame implements KeyListener {

    public static final int GRID_ROWS = 5;
    public static final int GRID_COLS = 5;
    public static final int GAP = 5;
    public static final String WORD_FILE = "words5.txt";
    public static final Color WORDLE_GREEN = new Color(17, 253, 41);
    public static final Color WORDLE_YELLOW = new Color(247, 255, 62);
    public static final Color WORDLE_GREY = new Color(168, 168, 162);

    private LetterPanel[][] allPanels;
    private String solution;			// the word the user is trying to guess
    private String guess;               // the user's current row entry
    private ArrayList<String> allWords;
    private int row;					//the current row for a guess
    private int col;					//current col within a row

    public AllisonWordle() {

        setSize(500,745);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.white);
        setLayout(null);
        setTitle("Wordle");

        JPanel gridPanel = new JPanel();
        gridPanel.setBackground(Color.white);
        gridPanel.setLayout(new GridLayout(GRID_ROWS,GRID_COLS,GAP,GAP));

        allPanels = new LetterPanel[5][5];
        for(int r = 0; r < GRID_ROWS; r++) {
            for(int c = 0; c < GRID_COLS; c++) {
                allPanels[r][c] = new LetterPanel();
                allPanels[r][c].setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));
                gridPanel.add(allPanels[r][c]);
            }
        }

        gridPanel.setBounds(25,25,450,550);
        add(gridPanel);

        this.addKeyListener(this);
        setVisible(true);
        handleInputFile(WORD_FILE);
        setSolution();
        guess = new String();
    }

    public void setSolution() {
        int idx = (int) (Math.random() * allWords.size());
        solution = allWords.get(idx);
        //System.out.println("solution: " + solution);
    }

    //this is the only key listener method you will implement
    public void keyPressed(KeyEvent e) {
        int keyVal = e.getKeyCode();
        if (keyVal == KeyEvent.VK_ENTER){
            // ENTER = submit current row; ignore if less than 5 letters
            if (col < GRID_COLS) {  // ignore
                return;
            }

            // check submission
            // *  if the guess is not a real word, pop up msg '...should not be submitted as a guess
            if (!allWords.contains(guess)) {
                String msg = "The word: " + guess + " is not in the wordlist and should not be submitted.";
                JOptionPane.showMessageDialog(null, msg);
                return;
            }

            // set background color
            for (int i = 0; i < solution.length(); i++) {
                if (solution.substring(i, i+1).equals(guess.substring(i, i+1))) {
                    allPanels[row][i].setBackground(WORDLE_GREEN);
                } else if (solution.contains(guess.substring(i, i+1))) {
                    allPanels[row][i].setBackground(WORDLE_YELLOW);
                } else {
                    allPanels[row][i].setBackground(WORDLE_GREY);
                }
            }

            // *  if its correct, key listener should be removed to not allow any more attempts
            if (guess.equals(solution)) {
                this.removeKeyListener(this);
                return;
            }

            // if incorrect and not at last row, go to next row
            if (row < GRID_ROWS) {
                row++;
                col = 0;
                guess = "";
            } else {  // no more guesses left
                this.removeKeyListener(this);
            }
        }
        if (keyVal == KeyEvent.VK_BACK_SPACE){
            // backspace key will erase the current letter, no effect if current row is empty
            if (col == 0) {
                return;
            }

            col--;
            guess = guess.substring(0, guess.length()-1);  // remove last char of current guess
            allPanels[row][col].removeText();
            return;
        }
        char key = e.getKeyChar();

        if(Character.isAlphabetic(key)){
            // * once 5 letters are inputted, any additional letters will be ignored until submit
            if (col == GRID_COLS) {
                return;
            }

            // update the panel with the char, increment the col/row
            allPanels[row][col].updateText(String.valueOf(key));
            guess += String.valueOf(key);
            col++;
        }
    }

    public void keyReleased(KeyEvent e) {
        // DO NOT IMPLEMENT
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // DO NOT IMPLEMENT
    }

    public void handleInputFile(String fileName) {

        Scanner fileIn = null;
        allWords = new ArrayList<String>();
        try {
            fileIn = new Scanner(new File(fileName));
        } catch(FileNotFoundException e) {
            System.out.println("Not found");
            System.exit(-1);
        }

        while(fileIn.hasNextLine()) {
            String word = fileIn.nextLine();
            allWords.add(word);

            // continues until reach end of file
            fileIn.nextLine();
        }
    }

    public static void main(String[] args) {
        new AllisonWordle();
    }

    public class LetterPanel extends JPanel{
        private JLabel text;
        public LetterPanel(){
            setBackground(Color.white);
            setLayout(null);
        }

        //changes the panel to have the given number
        public void updateText(String val){

            text = new JLabel(val,SwingConstants.CENTER);
            text.setFont(new Font("Calibri",Font.PLAIN,55));
            text.setBounds(0,35,70,50);
            this.add(text);

            repaint();
        }

        public void removeText(){
            this.remove(text);
            repaint();
        }
    }


}