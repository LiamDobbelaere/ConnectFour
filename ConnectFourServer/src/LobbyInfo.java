import java.util.Random;

/**
 * Created by Tom Dobbelaere on 23/06/2016.
 */
public class LobbyInfo
{
    private String[] players;
    private String[] playerTypes;
    private Board board;
    private boolean started;
    private boolean over;
    private int currentPlayer;
    private Random random;

    public LobbyInfo()
    {
        random = new Random();
        currentPlayer = 0;
        players = new String[2];
        playerTypes = new String[2];
        playerTypes[0] = "moon";
        playerTypes[1] = "sun";

        started = false;
        board = new Board();
    }

    public Board getBoard()
    {
        return board;
    }

    public void start()
    {
        currentPlayer = random.nextInt(2);
        started = true;
    }

    public String getCurrentPlayer() {
        return players[currentPlayer];
    }

    public void advancePlayer() {
        currentPlayer += 1;

        if (currentPlayer > 1)
        {
            currentPlayer = 0;
        }
    }

    public boolean isStarted()
    {
        return started;
    }

    public boolean isOver()
    {
        return over;
    }

    public void end()
    {
        over = true;
    }

    public void setType(int player, String type)
    {
        playerTypes[player] = type;
    }

    public String[] getPlayers()
    {
        return players;
    }

    public String getCurrentPlayerType()
    {
        return playerTypes[currentPlayer];
    }

    public String checkWinner()
    {
        String winner = null;

        for (int i = 0; i < playerTypes.length; i++)
        {
            if (board.isWinner(playerTypes[i]))
            {
                winner = players[i];
            }
        }

        return winner;
    }
}
