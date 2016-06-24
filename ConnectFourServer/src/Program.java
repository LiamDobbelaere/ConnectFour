import com.corundumstudio.socketio.listener.*;
import com.corundumstudio.socketio.*;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by Tom Dobbelaere on 22/06/2016.
 */
public class Program {
    public static void main(String[] args)
    {
        HashMap<String, LobbyInfo> lobbies = new HashMap<>();

        SessionIdentifierGenerator idgen = new SessionIdentifierGenerator();

        Configuration config = new Configuration();
        //Testing locally:
        //config.setHostname("localhost");
        config.setPort(3001);

        SocketIOServer server = new SocketIOServer(config);
        server.addConnectListener(new ConnectListener()
        {
            @Override
            public void onConnect(SocketIOClient socketIOClient)
            {
                System.out.println(socketIOClient.getRemoteAddress());
            }
        });

        server.addEventListener("create game", String.class, new DataListener<String>()
        {
            @Override
            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) throws Exception
            {
                JSONObject request = new JSONObject(s);

                String sessionID = idgen.nextSessionId();

                socketIOClient.sendEvent("create response", sessionID);

                socketIOClient.set("username", request.get("username").toString());
                socketIOClient.set("lobbyid", sessionID);
                socketIOClient.joinRoom(sessionID);

                System.out.println(socketIOClient.get("username") +  " is creating a new game");

                LobbyInfo newLobby = new LobbyInfo();
                newLobby.getPlayers()[0] = request.get("username").toString();
                newLobby.setType(0, request.get("type").toString());

                lobbies.put(sessionID, newLobby);
            }
        });

        server.addEventListener("join game", String.class, new DataListener<String>()
        {
            @Override
            public void onData(SocketIOClient socketIOClient, String s, AckRequest ackRequest) throws Exception
            {
                JSONObject request = new JSONObject(s);

                String sessionID = request.get("lobbyid").toString();

                socketIOClient.set("username", request.get("username").toString());
                socketIOClient.set("lobbyid", sessionID);
                socketIOClient.joinRoom(sessionID);

                System.out.println(socketIOClient.get("username") +  " is joining " + sessionID);

                lobbies.get(sessionID).getPlayers()[1] = request.get("username").toString();
                lobbies.get(sessionID).setType(1, request.get("type").toString());
                lobbies.get(sessionID).start();

                server.getRoomOperations(socketIOClient.get("lobbyid")).sendEvent("current player changed", lobbies.get(sessionID).getCurrentPlayer());
            }
        });

        server.addEventListener("drop piece", Integer.class, new DataListener<Integer>()
        {
            @Override
            public void onData(SocketIOClient socketIOClient, Integer x, AckRequest ackRequest) throws Exception
            {
                LobbyInfo lobbyInfo = lobbies.get(socketIOClient.get("lobbyid").toString());
                Board board = lobbyInfo.getBoard();

                int goalHeight = board.findGoalHeight(x);

                if (goalHeight != -1 && !lobbyInfo.isOver() && lobbyInfo.isStarted() && lobbyInfo.getCurrentPlayer().equals(socketIOClient.get("username")))
                {
                    board.dropPieceAt(x, goalHeight, lobbyInfo.getCurrentPlayerType());

                    JSONObject response = new JSONObject();
                    response.append("x", x);
                    response.append("y", goalHeight);
                    response.append("type", lobbyInfo.getCurrentPlayerType());

                    lobbyInfo.advancePlayer();

                    server.getRoomOperations(socketIOClient.get("lobbyid")).sendEvent("other dropped piece", response.toString());

                    String winner = lobbyInfo.checkWinner();

                    if (lobbyInfo.checkWinner() != null)
                    {
                        lobbyInfo.end();
                        server.getRoomOperations(socketIOClient.get("lobbyid")).sendEvent("game over", winner);
                    }
                    else
                    {
                        server.getRoomOperations(socketIOClient.get("lobbyid")).sendEvent("current player changed", lobbyInfo.getCurrentPlayer());
                    }
                }
            }
        });

        System.out.println("Starting server on 3001, enter stop to close");
        server.start();

        Scanner s = new Scanner(System.in);
        while (!s.nextLine().equals("stop"))
        {

        }

        System.out.println("Please wait while the server is being closed...");
        server.stop();
        System.out.println("Done");
    }

    private static String toJSON(Object o)
    {
        return new JSONObject(o).toString();
    }
}
