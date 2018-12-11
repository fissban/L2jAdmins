package l2j.loginserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import l2j.Config;

public abstract class FloodProtectedListener extends Thread
{
	private static final Logger LOG = Logger.getLogger(FloodProtectedListener.class.getName());
	
	private final Map<String, ForeignConnection> floodProtection = new ConcurrentHashMap<>();
	private final ServerSocket serverSocket;
	
	public FloodProtectedListener(String listenIp, int port) throws IOException
	{
		if (listenIp.equals("*"))
		{
			serverSocket = new ServerSocket(port);
		}
		else
		{
			serverSocket = new ServerSocket(port, 50, InetAddress.getByName(listenIp));
		}
	}
	
	@Override
	public void run()
	{
		Socket connection = null;
		
		while (true)
		{
			try
			{
				connection = serverSocket.accept();
				if (Config.FLOOD_PROTECTION)
				{
					ForeignConnection fConnection = floodProtection.get(connection.getInetAddress().getHostAddress());
					if (fConnection != null)
					{
						fConnection.connectionNumber += 1;
						if (((fConnection.connectionNumber > Config.FAST_CONNECTION_LIMIT) && ((System.currentTimeMillis() - fConnection.lastConnection) < Config.NORMAL_CONNECTION_TIME)) || ((System.currentTimeMillis() - fConnection.lastConnection) < Config.FAST_CONNECTION_TIME)
							|| (fConnection.connectionNumber > Config.MAX_CONNECTION_PER_IP))
						{
							fConnection.lastConnection = System.currentTimeMillis();
							fConnection.connectionNumber -= 1;
							
							connection.close();
							
							if (!fConnection.isFlooding)
							{
								LOG.warning("Potential Flood from " + connection.getInetAddress().getHostAddress());
							}
							
							fConnection.isFlooding = true;
							continue;
						}
						
						if (fConnection.isFlooding) // if connection was flooding server but now passed the check
						{
							fConnection.isFlooding = false;
							LOG.info(connection.getInetAddress().getHostAddress() + " is not considered as flooding anymore.");
						}
						fConnection.lastConnection = System.currentTimeMillis();
					}
					else
					{
						fConnection = new ForeignConnection(System.currentTimeMillis());
						floodProtection.put(connection.getInetAddress().getHostAddress(), fConnection);
					}
				}
				addClient(connection);
			}
			catch (Exception e)
			{
				try
				{
					if (connection != null)
					{
						connection.close();
					}
				}
				catch (Exception e2)
				{
				}
				
				if (isInterrupted())
				{
					// shutdown?
					try
					{
						serverSocket.close();
					}
					catch (IOException io)
					{
						LOG.log(Level.INFO, "", io);
					}
					break;
				}
			}
		}
	}
	
	protected static class ForeignConnection
	{
		public int connectionNumber;
		public long lastConnection;
		public boolean isFlooding = false;
		
		public ForeignConnection(long time)
		{
			lastConnection = time;
			connectionNumber = 1;
		}
	}
	
	public abstract void addClient(Socket s);
	
	public void removeFloodProtection(String ip)
	{
		if (!Config.FLOOD_PROTECTION)
		{
			return;
		}
		
		ForeignConnection fConnection = floodProtection.get(ip);
		if (fConnection != null)
		{
			fConnection.connectionNumber -= 1;
			if (fConnection.connectionNumber == 0)
			{
				floodProtection.remove(ip);
			}
		}
		else
		{
			LOG.warning("Removing a flood protection for a GameServer that was not in the connection map??? :" + ip);
		}
	}
	
	public void close()
	{
		try
		{
			serverSocket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}