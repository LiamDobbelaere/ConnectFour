/**
 * Created by Tom Dobbelaere on 23/06/2016.
 */
public class Board
{
    private String[][] board;

    public Board() {
        board = new String[7][6];

        for (int y = 0; y < 6; y++)
        {
            for (int x = 0; x < 7; x++)
            {
                board[x][y] = "empty";
            }
        }
    }

    public int findGoalHeight(int x)
    {
        int selectedY = -1;

        for (int y = 0; y < 6; y++)
        {
            if (board[x][y].equals("empty"))
            {
                selectedY = y;
            }
        }

        return selectedY;
    }

    public void dropPieceAt(int x, int y, String type)
    {
        if (y != -1)
        {
            board[x][y] = type;
        }
    }

    public boolean isWinner(String type)
    {
        return verticalCheck(type) || horizontalCheck(type) || diagonalRightCheck(type) || diagonalLeftCheck(type);
    }

    private boolean verticalCheck(String type)
    {
        boolean hasWon = false;

        for (int x = 0; x < 7; x++)
        {
            for (int y = 5; y >= 3; y--)
            {
                boolean match = false;

                if (board[x][y].equals(type) && board[x][y - 1].equals(type) && board[x][y - 2].equals(type) && board[x][y - 3].equals(type))
                {
                    match = true;
                }

                if (match) hasWon = true;
            }
        }

        return hasWon;
    }

    private boolean horizontalCheck(String type)
    {
        boolean hasWon = false;

        for (int y = 0; y < 6; y++)
        {
            for (int x = 0; x <= 3; x++)
            {
                boolean match = false;

                if (board[x][y].equals(type) && board[x + 1][y].equals(type) && board[x + 2][y].equals(type) && board[x + 3][y].equals(type))
                {
                    match = true;
                }

                if (match) hasWon = true;
            }
        }

        return hasWon;
    }

    private boolean diagonalRightCheck(String type)
    {
        boolean hasWon = false;

        for (int y = 5; y >= 3; y--)
        {
            for (int x = 0; x <= 3; x++)
            {
                boolean match = false;

                if (board[x][y].equals(type) && board[x + 1][y - 1].equals(type) && board[x + 2][y - 2].equals(type) && board[x + 3][y - 3].equals(type))
                {
                    match = true;
                }

                if (match) hasWon = true;
            }
        }

        return hasWon;
    }

    private boolean diagonalLeftCheck(String type)
    {
        boolean hasWon = false;

        for (int y = 5; y >= 3; y--)
        {
            for (int x = 6; x >= 3; x--)
            {
                boolean match = false;

                if (board[x][y].equals(type) && board[x - 1][y - 1].equals(type) && board[x - 2][y - 2].equals(type) && board[x - 3][y - 3].equals(type))
                {
                    match = true;
                }

                if (match) hasWon = true;
            }
        }

        return hasWon;
    }
}
