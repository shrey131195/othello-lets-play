package othelo;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Vector;

import othelo.Agent.MoveCoord;

public class Othello extends Observable{
	
	/** The unique instance of this class */
	private static Othello Instance;
	
	/** Game State */
	public static final int play = 0;
	public static final int end = 1;

	/** number of rows */
	public static final int board_size = 8;
	
	/** piece represents black */
	public static final char blck_piece = 'b';
	
	/** piece represents white */
	public static final char whte_piece = 'w';
	
	/** susggest piece for black */
	public static final char suggest_black = 'p';
	
	/** susggest piece for white */
	public static final char suggest_white = 'u';
	
	/** empty piece */
	public static final char empty = '-';
	
	/** move offset for row */
	private static final int[] move_row = {-1, -1, -1,  0,  0,  1,  1,  1};
	
	/** move offset for column */
	private static final int[] move_col = {-1,  0,  1, -1,  1, -1,  0,  1};
	
	/** board init */
	private static final char[][] init_board = {	
            { empty, empty, empty, empty, empty, empty, empty, empty },	// 1
            { empty, empty, empty, empty, empty, empty, empty, empty },// 2
            { empty, empty, empty, empty, empty, empty, empty, empty }, // 3
            { empty, empty, empty, blck_piece, whte_piece, empty, empty, empty },// 4
            { empty, empty, empty, whte_piece, blck_piece, empty, empty, empty }, // 5
            { empty, empty, empty, empty, empty, empty, empty, empty }, // 6
            { empty, empty, empty, empty, empty, empty, empty, empty }, // 7
            { empty, empty, empty, empty, empty, empty, empty, empty }};// 8
	 //      a      b     c      d       e       f      g    h
	
	/** whether it is black's turn to move */
	private boolean black_turn = false;
	
	/** whether it is computer's turn to move */
	private boolean comp_turn = true;
	
	/** the board */
	private char[][] array_board;
	
	/** score of black piece */
	private int Black_score;
	
	/** score of white piece */
	private int White_score;
	
	/** state of the game */
	private int State;
	
	/** AI agent */
	private Agent AI_Agent;
	
	/** new piece position */
	private int NewPieceRow;
	private int NewPieceCol;
	
	/** whether a piece is changed*/
	private boolean[][] EffectedPiece;
	
	private Vector<String> MoveList;
	
	/** Private constructor */
	private Othello() {
		init();
	}
	
	public static Othello getInstance() {
		if (Instance == null)
		Instance = new Othello();
		
		return Instance;
	}
	
	/** Initialize the board */
    private void init() {
    	// init board
		array_board = new char[board_size][board_size];
		
		// init effected pieces
		EffectedPiece = new boolean[board_size][board_size];
		
		// init move list
		MoveList = new Vector<String>();
		
		// set up AI agent
		AI_Agent = new ScoutAgent();
		
		// computer plays second for default
		comp_turn = false;
	}
    
    public char[][] getBoard() {
    	return array_board;
    }
    
    /** Gets game state */
    public int getGameState() {
    	return State;
    }
    
    /** Sets game state */
    public void setGameState(int state) {
    	State = state;
    }
    
    /** Set whether computer moves first */
    public void setIsCompTurn(boolean value) {
    	comp_turn = value;
    }
    
    /** Get whether computer moves first */
    public boolean getIsCompTurn() {
    	return comp_turn;
    }
    
    /** Get white's score */
    public int getWhiteScore() {
    	return White_score;
    }
    
    /** Get black's score */
    public int getBlackScore() {
    	return Black_score;
    }
    
    public boolean isNewPiece(int row, int col) {
    	return (NewPieceRow == row && NewPieceCol == col);
    }
    
    public Vector<String> getMoveList() {
    	return MoveList;
    }
    
    /** New game */
	public void newGame() {
		// reset the board
		resetBoard();		
		// reset effected pieces
		resetEffectedPieces();
		// white piece starts first
		black_turn = false;
		// set state
		State = play;
		stateChange();
		
		// get next move
		getNextMove();
	}
    
    /** Reset the board */
	private void resetBoard() {
		for (int i=0; i < board_size; ++i)
                    for (int j=0; j < board_size; ++j)
                        array_board[i][j] = init_board[i][j];
		
		Black_score = 2;
		White_score = 2;
		
		NewPieceRow = -1;
		NewPieceCol = -1;
		
	}
	
	public void resetEffectedPieces() {
		for (int i=0; i < board_size; ++i)
			for (int j=0; j < board_size; ++j)
				EffectedPiece[i][j] = false;
	}
	
	public void setEffectedPiece(int row, int col) {
		EffectedPiece[row][col] = true;
	}
	
	public boolean isEffectedPiece(int row, int col) {
		return EffectedPiece[row][col];
	}
	
	/** Get next move */
	private void getNextMove() {
		if (!comp_turn) {
			
                        char piece;
                        if (black_turn== true)
                        {
                            piece = blck_piece;
                        }
                        else
                        {
                            piece= whte_piece;
                        }
			if ((findValidMove(array_board, piece, true)).isEmpty()) 
                        {
                            char opPiece;
                            if(piece==blck_piece)
                            {
                                opPiece= whte_piece;
                            }
                            else
                            {
                                opPiece=blck_piece;
                            }
				
				if ((findValidMove(array_board, opPiece, false)).isEmpty())
				{
					State = end;
					stateChange();	
					return;
				}
				changeTurn();
				getNextMove();
			}	
		}
		else {
                        char piece;
                        if (black_turn== true)
                        {
                            piece = blck_piece;
                        }
                        else
                        {
                            piece= whte_piece;
                        }
			// clear all suggested pieces
			for (int i=0 ;i < board_size; ++i)
                            for (int j=0; j < board_size; ++j)
                                if (array_board[i][j] == suggest_black || array_board[i][j] == suggest_white)
                                    array_board[i][j] = empty;
			
			// copy board to temp
			char[][] tempBoard = new char[8][8];
			for (int i=0; i< board_size; ++i)
                            for (int j=0; j < board_size; ++j)
                                tempBoard[i][j] = array_board[i][j];
			
			// find optimal move
			MoveCoord move = AI_Agent.findMove(tempBoard, piece);
			if (move != null)
			{
				effectMove(array_board, piece, move.getRow(), move.getCol());
				addToMoveList(piece, move.getRow(), move.getCol());
				NewPieceRow = move.getRow();
				NewPieceCol = move.getCol();
				stateChange();
			}
			
			// next move
			changeTurn();
			getNextMove();
		}
	}
	
	/** add a move to move list */
	private void addToMoveList(char piece, int row, int col) {
		String str = String.format("%s:\t%s", String.valueOf(piece).toUpperCase(), MoveCoord.encode(row, col));
		MoveList.add(str);
	}
	
	/** change turn of playing */
	private void changeTurn() {
		black_turn = !black_turn;
		comp_turn = !comp_turn;
	}
	
	/** Calculate score */
	private void calScore() {
		Black_score = 0;
		White_score = 0;
		for (int i = 0; i < board_size; ++i)
			for (int j = 0; j < board_size; ++j)
			{
				if (array_board[i][j] == blck_piece)
					++Black_score;
				else if (array_board[i][j] == whte_piece)
					++White_score;
			}
	}
	
	public static ArrayList<MoveCoord> findValidMove(char[][] board, char piece, boolean isSuggest) {
		
                char suggestPiece;
                if(piece==blck_piece)
                {
                    suggestPiece=suggest_black;
                }
                else
                {
                    suggestPiece=suggest_white;
                }
		ArrayList<MoveCoord> moveList = new ArrayList<MoveCoord>();
		for (int i = 0; i < 8; ++i)
			for (int j = 0; j < 8; ++j) {
				// clean the suggest piece before
				if (board[i][j] == suggest_black || board[i][j] == suggest_white)
					board[i][j] = empty;
				
				if (isValidMove(board,piece, i, j))
				{
					moveList.add(new MoveCoord(i, j));
					
					// if we want suggestion, mark on board
					if (isSuggest)
						board[i][j] = suggestPiece;
				}
			}
		
		return moveList;
	}
	
	public static boolean isValidMove(char[][] board, char piece, int row, int col) {
		// check whether this square is empty
		if (board[row][col] != empty)
			return false;
		
		
                char oppPiece;
                if(piece==blck_piece)
                {
                    oppPiece=whte_piece;
                }
                else
                {
                    oppPiece=blck_piece;
                }
                
		boolean isValid = false;
		// check 8 directions
		for (int i = 0; i < 8; ++i) {
			int curRow = row + move_row[i];
			int curCol = col + move_col[i];
			boolean hasOppPieceBetween = false;
			while (curRow >=0 && curRow < 8 && curCol >= 0 && curCol < 8) {
				
				if (board[curRow][curCol] == oppPiece)
					hasOppPieceBetween = true;
				else if ((board[curRow][curCol] == piece) && hasOppPieceBetween)
				{
					isValid = true;
					break;
				}
				else
					break;
				
				curRow += move_row[i];
				curCol += move_col[i];
			}
			if (isValid)
				break;
		}
		
		return isValid;
	}
	
	public static char[][] effectMove(char[][] board, char piece, int row, int col) 
        {
		board[row][col] = piece;	
		Othello.getInstance().resetEffectedPieces();
		
		// check 8 directions
		for (int i = 0; i < 8; ++i) {
			int curRow = row + move_row[i];
			int curCol = col + move_col[i];
			boolean hasOppPieceBetween = false;
			while (curRow >=0 && curRow < 8 && curCol >= 0 && curCol < 8) {
				// if empty square, break
				if (board[curRow][curCol] == empty)
					break;
				
				if (board[curRow][curCol] != piece)
					hasOppPieceBetween = true;
				
				if ((board[curRow][curCol] == piece) && hasOppPieceBetween)
				{
					int effectPieceRow = row + move_row[i];
					int effectPieceCol = col + move_col[i];
					while (effectPieceRow != curRow || effectPieceCol != curCol)
					{
						Othello.getInstance().setEffectedPiece(effectPieceRow, effectPieceCol);
						board[effectPieceRow][effectPieceCol] = piece;
						effectPieceRow += move_row[i];
						effectPieceCol += move_col[i];
					}
					 
					break;
				}
				
				curRow += move_row[i];
				curCol += move_col[i];
			}
		}
		
		return board;
	}
	
	
	public void movePiece(int row, int col) {
		
                char piece;
                if(black_turn)
                {
                    piece=blck_piece;
                }
                else
                {
                    piece=whte_piece;
                }
		
                char suggestPiece;
                if(black_turn)
                {
                    suggestPiece=suggest_black;
                }
                else
                {
                    suggestPiece=suggest_white;
                }
                
		if (array_board[row][col] == suggestPiece)
		{
			effectMove(array_board, piece, row, col);
			NewPieceRow = row;
			NewPieceCol = col;
			
			// add to move list
			addToMoveList(piece, row, col);
			// notify the observer
			stateChange();
			
			// change turn
			changeTurn();
			getNextMove();
		}
	}
    
	private void stateChange() {
		calScore();
		setChanged();
		notifyObservers();
	}

}
