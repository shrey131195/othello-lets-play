package othelo;

import java.util.ArrayList;

public class ScoutAgent implements Agent
{        
    static final int infinity = 1000000;   
    private int MaxPly = 1;
	
    @Override
    public Agent.MoveCoord findMove(char[][] board, char piece) 
    {
	return scoutDecision(board, piece);
    }
	
    public Agent.MoveCoord scoutDecision(char[][] board, char piece)
    {
        Agent.MoveScore moveScore = scout(board,0,-infinity,infinity,piece);
        return moveScore.getMove();
    }

    public Agent.MoveScore scout(char[][] board, int ply, int alpha, int beta, char piece)
    {
    	char oppPiece;
        if(piece==Othello.blck_piece)
        {
            oppPiece=Othello.whte_piece;
        }
        else
        {
            oppPiece=Othello.blck_piece;
        }
    	// Check if we have done recursing
    	if (ply==MaxPly)
        {
            return new Agent.MoveScore(null, Evaluation.evaluateBoard(board, piece, oppPiece));
        }
    		
    	int currentScore;
    	int bestScore = -infinity;
    	Agent.MoveCoord bestMove = null;
    	int adaptiveBeta = beta; 	
        
        // Keep track the test window value    	
    	// Generates all possible moves
    	
        ArrayList<Agent.MoveCoord> moveList = Evaluation.PriorityMoves(board, piece);
    	if (moveList.isEmpty())
        {
            return new Agent.MoveScore(null, bestScore);
        }
        bestMove = moveList.get(0);
    	
    	// Go through each move
    	for(int i=0;i<moveList.size();i++)
        {
            Agent.MoveCoord move = moveList.get(i);
            char[][] newBoard = new char[8][8];
            for (int r = 0; r < 8; ++r)
            {
                for (int c=0; c < 8; ++c)
                {
                    newBoard[r][c] = board[r][c];
                }
            }
    		
            Othello.effectMove(newBoard, piece, move.getRow(), move.getCol());
    		
            // Recurse
            Agent.MoveScore current = scout(newBoard, ply+1, -adaptiveBeta, - Math.max(alpha,bestScore), oppPiece);
    	
            currentScore = - current.getScore();
    	
            // Update bestScore
            if (currentScore>bestScore)
            {
                if (adaptiveBeta == beta || ply>=(MaxPly-2))
                {
                    bestScore = currentScore;
                    bestMove = move;
    		}
                else
                { 
                    current = scout(newBoard, ply+1, -beta, -currentScore, oppPiece);
                    bestScore = - current.getScore();
                    bestMove = move;
                }
    			
    		if(bestScore>=beta)
                {
                    return new Agent.MoveScore(bestMove,bestScore);
        	}
        	adaptiveBeta = Math.max(alpha, bestScore) + 1;
            }
    	}
    	return new Agent.MoveScore(bestMove,bestScore);
    }
}