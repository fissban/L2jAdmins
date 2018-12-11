package l2j.loginserver;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import l2j.Config;

public class GameServerListener extends FloodProtectedListener
{
	private static List<GameServerThread> gameServers = new ArrayList<>();
	
	public GameServerListener() throws IOException
	{
		super(Config.GAME_SERVER_LOGIN_HOST, Config.GAME_SERVER_LOGIN_PORT);
	}
	
	@Override
	public void addClient(Socket s)
	{
		gameServers.add(new GameServerThread(s));
	}
	
	public void removeGameServer(GameServerThread gst)
	{
		gameServers.remove(gst);
	}
}