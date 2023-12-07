/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dotsnboxes;

// DoxBoard.java

// exported methods:
//
// DoxBoard                             constructor
// String toString()                    overrides Object.toString()
// int[] validMoves()                   returns a List of valid moves (empty spots)
// DoxBoard applyMove(move, whoseTurn)  returns updated board (does not update this)
// String getBoxOwner(r, c)             returns "A" or "B" or " "
// int numBoxRows()                     returns number of rows
// int numBoxCols()                     returns number of columns
// boolean gameIsOver()                 returns true if all squares are owned
// int numBoxesOwnedBy(player)          returns the count

import java.util.*;

class DoxBoard
{
	private Box[][] boxes;
	int boxRows, boxCols;   // rows and cols of boxes, not dots
	public boolean anotherMove;

	DoxBoard(int dotRows, int dotCols, String lines, String boxOwners)
	{
		boxRows = dotRows - 1;
		boxCols = dotCols - 1;
		anotherMove = false;    // turns true when a box is surrounded
		boxes = new Box[boxRows][boxCols];
		// the first q-1 chars in lines specify horizontal lines;
		// vertical lines are specified by chars starting at q
		int q = dotRows * (dotCols - 1);
		for (int r = 0; r<boxRows; r++)
		{
			for (int c = 0; c<boxCols; c++)
			{
				boolean t = lines.charAt(r * boxCols + c) == 'X';
				boolean b = lines.charAt((r+1) * boxCols + c) == 'X';
				//System.out.println("q: " + q + " r: " + r + " c: " + c + " -> " +
				//	(q + c * boxRows + r));
				boolean l = lines.charAt(q + c * boxRows + r) == 'X';
				boolean ri = lines.charAt(q + (c+1) * boxRows + r) == 'X';
				char owner = boxOwners.charAt(r * boxCols + c);
				String ownedBy = " ";
				if (owner == 'A')
					ownedBy = "A";
				else if (owner == 'B')
					ownedBy = "B";
				boxes[r][c] = new Box(t, ri, b, l, ownedBy);
			}
		}
	}

	// constructor for internal use only - does a deep clone
	private DoxBoard(DoxBoard b1)
	{
		boxRows = b1.boxRows;
		boxCols = b1.boxCols;
		anotherMove = false;
		boxes = new Box[boxRows][boxCols];
		for (int r = 0; r<boxRows; r++)
		{
			for (int c = 0; c<boxCols; c++)
			{
				Box b1Box = b1.boxes[r][c];
				boxes[r][c] = new Box(b1Box.top, b1Box.right, b1Box.bottom,
										b1Box.left, b1Box.ownedBy);
			}
		}
	}

	// Uses the same system to number the possible moves (lines)
	// as is used in input parameter #3
	List<Integer> validMoves()
	{
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int r = 0; r<boxRows; r++)
		{
			for (int c = 0; c<boxCols; c++)
			{
				if (!boxes[r][c].top)
					list.add(r*boxCols + c);
			}
		}
		// look at bottom row of dots
		for (int c = 0; c<boxCols; c++)
		{
			if (!boxes[boxRows-1][c].bottom)
				list.add(boxRows*boxCols + c);
		}
		int q = (boxRows+1) * boxCols;     // same q as in constructor
		for (int c = 0; c<boxCols; c++)
		{
			for (int r = 0; r<boxRows; r++)
			{
				if (!boxes[r][c].left)
					list.add(q + c*boxRows + r);
			}
		}
		// look at right column of dots
		for (int r = 0; r<boxRows; r++)
		{
			if (!boxes[r][boxCols-1].right)
				list.add(q+boxRows*boxCols + r);
		}
		return list;
	}

	// If this method prints out an error message, it's probably due to a bug.
	DoxBoard applyMove(int move, String whoseTurn)
	{
		DoxBoard newBoard = new DoxBoard(this);
		int q = (boxRows+1) * boxCols;     // same q as in constructor
		if (move < q)  // horizontal line
		{
			int col = move % boxCols;
			int row;
			if (move < boxCols) // on the top row
			{
				row = 0;
				if (newBoard.boxes[row][col].top)
					System.err.println("**Error 1 in applyMove, " +move+ " is not empty.");
				else
				{
					newBoard.boxes[row][col].top = true;
					if (newBoard.surrounded(row, col))
					{
						newBoard.boxes[row][col].ownedBy = whoseTurn;
						newBoard.anotherMove = true;
					}
				}
			}
			else if (move >= q - boxCols)	// on the bottom row
			{
				row = boxRows - 1;
				if (newBoard.boxes[row][col].bottom)
					System.err.println("**Error 2 in applyMove, " +move+ " is not empty.");
				else
				{
					newBoard.boxes[row][col].bottom = true;
					if (newBoard.surrounded(row, col))
					{
						newBoard.boxes[row][col].ownedBy = whoseTurn;
						newBoard.anotherMove = true;
					}
				}
			}
			else  // on a middle row, update two boxes
			{
				row = (move - col) / boxCols;
				if (newBoard.boxes[row][col].top)
					System.err.println("**Error 3 in applyMove, " +move+ " is not empty.");
				else
				{
					newBoard.boxes[row][col].top = true;
					if (newBoard.surrounded(row, col))
					{
						newBoard.boxes[row][col].ownedBy = whoseTurn;
						newBoard.anotherMove = true;
					}
				}
				row = row - 1;
				if (newBoard.boxes[row][col].bottom)
					System.err.println("**Error 4 in applyMove, " +move+ " is not empty.");
				else
				{
					newBoard.boxes[row][col].bottom = true;
					if (newBoard.surrounded(row, col))
					{
						newBoard.boxes[row][col].ownedBy = whoseTurn;
						newBoard.anotherMove = true;
					}
				}
			}
		}

		else // vertical line
		{
			int col;
			int row = (move - q) % boxRows;    // integer division, truncates
			if (move - q < boxRows) // on the left column
			{
				col = 0;
				if (newBoard.boxes[row][col].left)
					System.err.println("**Error 5 in applyMove, " +move+ " is not empty.");
				else
				{
					newBoard.boxes[row][col].left = true;
					if (newBoard.surrounded(row, col))
					{
						newBoard.boxes[row][col].ownedBy = whoseTurn;
						newBoard.anotherMove = true;
					}
				}
			}
			else if (move >= q + (boxRows * boxCols))	// on the right column
			{
				col = boxCols - 1;
				if (newBoard.boxes[row][col].right)
					System.err.println("**Error 6 in applyMove, " +move+ " is not empty.");
				else
				{
					newBoard.boxes[row][col].right = true;
					if (newBoard.surrounded(row, col))
					{
						newBoard.boxes[row][col].ownedBy = whoseTurn;
						newBoard.anotherMove = true;
					}
				}
			}
			else  // on a middle column, update two boxes
			{
				col = (move - q - row) / boxRows;
				if (newBoard.boxes[row][col].left)
					System.err.println("**Error 7 in applyMove, " +move+ " is not empty.");
				else
				{
					newBoard.boxes[row][col].left = true;
					if (newBoard.surrounded(row, col))
					{
						newBoard.boxes[row][col].ownedBy = whoseTurn;
						newBoard.anotherMove = true;
					}
				}
				col = col - 1;
				if (newBoard.boxes[row][col].right)
					System.err.println("**Error 8 in applyMove, " +move+ " is not empty.");
				else
				{
					newBoard.boxes[row][col].right = true;
					if (newBoard.surrounded(row, col))
					{
						newBoard.boxes[row][col].ownedBy = whoseTurn;
						newBoard.anotherMove = true;
					}
				}
			}
		}

		return newBoard;
	}

	// return true if the four walls of the specified box are all filled in
	private boolean surrounded(int r, int c)
	{
		return boxes[r][c].top && boxes[r][c].right &&
		       boxes[r][c].bottom && boxes[r][c].left;
	}

	public boolean gameIsOver()
	{
		for (int r = 0; r<boxRows; r++)
		{
			for (int c = 0; c<boxCols; c++)
			{
				if (boxes[r][c].ownedBy.equals(" "))
					return false;
			}
		}
		return true;  // all boxes are owned
	}

	public int numBoxesOwnedBy(String player)
	{
		int count = 0;
		for (int r = 0; r<boxRows; r++)
		{
			for (int c = 0; c<boxCols; c++)
			{
				if (boxes[r][c].ownedBy.equals(player))
					count++;
			}
		}
		return count;
	}



	String getBoxOwner(int r, int c)
	{
		return boxes[r][c].ownedBy;
	}


	public int numBoxRows() { return boxRows; }
	public int numBoxCols() { return boxCols; }

	/*
	The output of toString() will look like this
*---*---*    <- this is built in s1
|   |   |    <- this is built in s2
| A | B |    <- this is built in s3
*---*---*
|
|
*   *   *

	*/
	@Override
	public String toString()
	{
		String s = "", s1="", s2="", s3="";
		for (int r = 0; r<boxRows; r++)
		{
			s1 = s2 = s3 = "";
			for (int c = 0; c<boxCols; c++)
			{
				if (boxes[r][c].top)
					s1 += "*---";
				else
					s1 += "*   ";
				if (boxes[r][c].left)
				{
					s2 += "|   ";
					s3 += "| " + boxes[r][c].ownedBy + " ";
				}
				else
				{
					s2 += "    ";
					s3 += "    ";
				}
			}
			s1 += "*";
			if (boxes[r][boxCols-1].right)
			{
				s2 += "|";
				s3 += "|";
			}
			else
			{
				s2 += " ";
				s3 += " ";
			}
			s += s1 + "\n" + s2 + "\n" + s3 + "\n";
		}
		// now handle very bottom line
		s1 = "";
		for (int c = 0; c<boxCols; c++)
		{
			if (boxes[boxRows-1][c].bottom)
				s1 += "*---";
			else
				s1 += "*   ";
		}
		s += s1 + "*" + "\n";

		return s;
	}

	// inner class
	class Box
	{
		// four booleans which are true if the top, right, bottom,
		// and left sides of the box are lines.
		boolean top = false;
		boolean right = false;
		boolean bottom = false;
		boolean left = false;
		String ownedBy = "";   // can be "A" or "B" or " "

		// constructor
		Box(boolean t, boolean r, boolean b, boolean l, String o)
		{
			top = t;
			right = r;
			bottom = b;
			left = l;
			ownedBy = o;
		}

	}

}