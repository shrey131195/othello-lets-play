package othelo;

import java.util.ArrayList;
import java.util.PriorityQueue;
import othelo.Agent.MoveCoord;
import othelo.Agent.MoveScore;

public class Evaluation 
{
	
    private static final int board_size = Othello.board_size;
	
    private static int[][] board_value = 
    {
	{100, -1, 5, 2, 2, 5, -1, 100},
	{-1, -20,1, 1, 1, 1,-20, -1},
	{5 , 1,  1, 1, 1, 1,  1,  5},
	{2 , 1,  1, 0, 0, 1,  1,  2},
	{2 , 1,  1, 0, 0, 1,  1,  2},
	{5 , 1,  1, 1, 1, 1,  1,  5},
	{-1,-20, 1, 1, 1, 1,-20, -1},
	{100, -1, 5, 2, 2, 5, -1, 100}
    };
	
    public static int evaluateBoard(char[][] board, char piece, char oppPiece) 
    {
        int score = 0;
	for (int r = 0; r < board_size; ++r) 
        {
            for (int c = 0; c < board_size; ++c) 
            {
		if (board[r][c] == piece)
                {    
                    score += board_value[r][c];
                }
                else if (board[r][c] == oppPiece)
                {                 
                    score -= board_value[r][c];
                }
            }
	}
	return score;
    }
	
    public static ArrayList<MoveCoord> PriorityMoves(char[][] board, char piece) 
    {
	ArrayList<MoveCoord> moveList = Othello.findValidMove(board, piece, false);
	PriorityQueue<MoveScore> moveQueue = new PriorityQueue<MoveScore>();
		
	for (int i=0; i < moveList.size(); ++i) 
        {
            MoveCoord move = moveList.get(i);
            MoveScore moveScore = new MoveScore(move, board_value[move.getRow()][move.getCol()]);
            moveQueue.add(moveScore);
	}
		
	moveList = new ArrayList<MoveCoord>();
	while (!moveQueue.isEmpty()) 
        {
            moveList.add(moveQueue.poll().getMove());
	}		
	return moveList;
    }
}