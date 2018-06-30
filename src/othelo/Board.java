package othelo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Board extends javax.swing.JPanel implements MouseListener {

	
	/******************************/
	/* CONSTANT FOR DRAWING BOARD */
	/******************************/
	/** size of board */
	private static final int board_size = Othello.board_size;
	
	/** border offset */
	private static final int boarder_offset = 30;
	
	/** board offset */
	private static final int board_offset = 20;
	
	/** border and board offset */
	private static final int border_board_offset = boarder_offset + board_offset;
	
	/** size of a square. */
	private static final int square_width = 45;
	
	/** size of the board in pixels */
	private static final int board_size_pixel = (board_offset + boarder_offset) * 2 + square_width * board_size;
	
	/** radius of a piece */
	private static final int piece_width = 35;
	
	/** offset piece inside a square */
	private static final int square_offset = (square_width - piece_width) >> 1;
	
	/** width of the border */
	private static final int border_width = 3;
	
	/** width of the division between square */
	private static final int divide_width = 1;
	
	/** board color */
	private static final Color board_color = new Color(130, 130,128, 255);
	
	/** squares' divide color */
	private static final Color divide_color = Color.white;
	
	/** color for susggest black piece */
	private static final Color suggest_black_color = new Color(0, 0, 0, 50);
	
	/** color for susggest white piece */
	private static final Color suggest_white_color = new Color(255, 255, 255, 80);
	
	/** color for board's texts */
	private static final Color board_text_color = Color.black;
	
	/** font for board's texts */
	private static final Font board_font = new Font("AXURE handwriting", Font.BOLD, 20);
	
	/** board column texts */
	private static final String[] col_text = {"A", "B", "C", "D", "E", "F", "G", "H"};
	
	/** board row texts */
	private static final String[] row_text = {"1", "2", "3", "4", "5", "6", "7", "8"};
	
	/***********************************/
	/* END CONSTANTS FOR DRAWING BOARD */
	/***********************************/
	
	/** image of black piece */
	public static BufferedImage BlackPieceImg = null;
	
	/** image of white piece */
	public static BufferedImage WhitePieceImg = null;
	
	
    
    public Board() {
        // add mouse listener
        addMouseListener(this);
        
        // set panel size to board size
        this.setPreferredSize(new Dimension(board_size_pixel, board_size_pixel));
        
        init();
    }
    
    private void init() {
    	// load image for pieces
		try {			
			BlackPieceImg = ImageIO.read(this.getClass().getResourceAsStream("/img/blackpiece.png"));
			WhitePieceImg = ImageIO.read(this.getClass().getResourceAsStream("/img/whitepiece.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	/**
	 * Draw the board
	 */
	@Override
	public void paint(Graphics g) {
		// draw the board with board color
		g.setColor(board_color);
		g.fillRect(0, 0, board_size_pixel, board_size_pixel);
		
		// draw board's texts
		g.setColor(board_text_color);
		g.setFont(board_font);
		Graphics2D g2d = (Graphics2D)g;
		FontMetrics fm = g2d.getFontMetrics();
		
		for (int i = 0; i < col_text.length; ++i)
		{
			g.drawString// draw A-H
                        (
                            col_text[i], border_board_offset + square_width * i + (square_width - fm.stringWidth(col_text[i]))/2, (boarder_offset + fm.getHeight()/2) / 2
                        );
			
			g.drawString// draw 1-8
                        (
                            row_text[i],(boarder_offset - fm.stringWidth(row_text[i])) / 2, border_board_offset + square_width * i +(square_width + fm.getHeight()) / 2
                        );
		}
		
		// draw border
		g.setColor(divide_color);
                //left line
		g.fillRect(boarder_offset, boarder_offset, border_width, board_size_pixel - boarder_offset * 2);
		//top line
                g.fillRect(boarder_offset, boarder_offset, board_size_pixel - boarder_offset * 2, border_width);
		//bottom line
                g.fillRect(boarder_offset, board_size_pixel - boarder_offset,board_size_pixel - boarder_offset * 2 + border_width, border_width);
		//right line
                g.fillRect(board_size_pixel - boarder_offset, boarder_offset,border_width, board_size_pixel - boarder_offset * 2 + border_width);
		
		// draw the squares' dividers
		// horizontal
		for (int i = 0; i <= 8; ++i) 
                {
			g.fillRect(i * square_width + border_board_offset, border_board_offset, divide_width, square_width * board_size);
		}
		// vertical
		for (int i = 0; i <= 8; ++i) 
                {
			g.fillRect(border_board_offset, i * square_width + border_board_offset, square_width * board_size, divide_width);
		}
		
		char[][] board = Othello.getInstance().getBoard();
		// draw pieces
                for (int r = 0; r < 8; ++r)
			for (int c = 0; c < 8; ++c) {
				if (Othello.getInstance().isNewPiece(r, c)) 
                                {
					drawNewPiece(g,r,c);
				}
				
				if (Othello.getInstance().isEffectedPiece(r, c)) 
                                {
					drawEffectedPiece(g, r, c);
				}
				
				if (board[r][c] == Othello.blck_piece) 
                                {
					//draw black piece
                                        drawPiece(g, BlackPieceImg, r, c);
					
				} else if (board[r][c] == Othello.whte_piece) 
                                {
                                    //draw white piece
					drawPiece(g, WhitePieceImg, r, c);
				}
				else if (board[r][c] == Othello.suggest_black) 
                                {
                                    //draw suggest for black
					g.setColor(suggest_black_color);
					drawSuggestedPiece(g,r,c);
				}
				else if (board[r][c] == Othello.suggest_white) 
                                {
                                    //draw suggest for white
					g.setColor(suggest_white_color);
					drawSuggestedPiece(g,r,c);
				}
				else
					continue;
				
			}
	}
	
	private void drawNewPiece(Graphics g, int row, int col) {
		g.setColor(new Color(255, 110, 147, 100));
		g.fillRect
                (
                        (col * square_width) + border_board_offset + divide_width, (row * square_width) + border_board_offset + divide_width,square_width - divide_width, square_width - divide_width
                );
	}
	
	private void drawEffectedPiece(Graphics g, int row, int col) {
		g.setColor(new Color(255, 110, 147, 80));
		g.fillRect
                    (
                        (col * square_width) + border_board_offset + divide_width, (row * square_width) + border_board_offset + divide_width, square_width - divide_width, square_width - divide_width
                    );
	}
	
	private void drawSuggestedPiece(Graphics g, int row, int col) {
		g.fillOval
                    (	
                        (col * square_width) + square_offset + border_board_offset, (row * square_width)+ square_offset + border_board_offset,piece_width, piece_width
                    );
	}
	
	private void drawPiece(Graphics g, BufferedImage img, int row, int col) {
		g.drawImage
                (
                        img, (col * square_width) + square_offset + border_board_offset, (row * square_width) + square_offset + border_board_offset, null
                );
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (Othello.getInstance().getGameState() == Othello.play && !Othello.getInstance().getIsCompTurn()) 
                {
			int col = (e.getX() - border_board_offset) / square_width;
			int row = (e.getY() - border_board_offset) / square_width;
			if (row >= 0 && row <8 && col >=0 && col < 8)
			Othello.getInstance().movePiece(row, col);
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
