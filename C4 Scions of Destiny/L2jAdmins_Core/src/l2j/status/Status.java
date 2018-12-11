package l2j.status;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import l2j.Config;
import l2j.Server;
import l2j.util.Rnd;

public class Status extends Thread
{
	private final ServerSocket statusServerSocket;
	
	private final int uptime;
	private final int statusPort;
	private String statusPW;
	private final int mode;
	private final List<LoginStatusThread> loginStatus;
	
	@Override
	public void run()
	{
		while (true)
		{
			try
			{
				Socket connection = statusServerSocket.accept();
				
				if (mode == Server.MODE_GAMESERVER)
				{
					new GameStatusThread(connection, uptime, statusPW);
				}
				else if (mode == Server.MODE_LOGINSERVER)
				{
					LoginStatusThread lst = new LoginStatusThread(connection, uptime);
					if (lst.isAlive())
					{
						loginStatus.add(lst);
					}
				}
				if (isInterrupted())
				{
					try
					{
						statusServerSocket.close();
					}
					catch (IOException io)
					{
						io.printStackTrace();
					}
					break;
				}
			}
			catch (IOException e)
			{
				if (isInterrupted())
				{
					try
					{
						statusServerSocket.close();
					}
					catch (IOException io)
					{
						io.printStackTrace();
					}
					break;
				}
			}
		}
	}
	
	public Status(int mode) throws IOException
	{
		super("Status");
		this.mode = mode;
		Properties telnetSettings = new Properties();
		
		try (InputStream is = new FileInputStream(new File(Config.FILE_TELNET)))
		{
			telnetSettings.load(is);
		}
		catch (Exception e)
		{
			System.out.println("Status: File " + Config.FILE_TELNET + " not found!");
		}
		
		statusPort = Integer.parseInt(telnetSettings.getProperty("StatusPort", "12345"));
		statusPW = telnetSettings.getProperty("StatusPW");
		if (mode == Server.MODE_GAMESERVER)
		{
			if (statusPW == null)
			{
				System.out.println("Status: Server's Telnet Function Has No Password Defined!");
				System.out.println("Status: A Password Has Been Automaticly Created!");
				statusPW = RndPW(10);
				System.out.println("Status: Password Has Been Set To: " + statusPW);
			}
			
			System.out.println("Status: Telnet StatusServer started successfully, listening on Port: " + statusPort);
		}
		else
		{
			if (statusPW != null)
			{
				System.out.println("Status: Telnet StatusServer started successfully, listening on Port: " + statusPort);
			}
			else
			{
				System.out.println("Status: StatusServer Started! - Listening on Port: " + statusPort);
			}
			System.out.println("Status: Password Has Been Set To: " + statusPW);
		}
		
		statusServerSocket = new ServerSocket(statusPort);
		uptime = (int) System.currentTimeMillis();
		loginStatus = new ArrayList<>();
	}
	
	private String RndPW(int length)
	{
		StringBuilder password = new StringBuilder();
		String lowerChar = "qwertyuiopasdfghjklzxcvbnm";
		String upperChar = "QWERTYUIOPASDFGHJKLZXCVBNM";
		String digits = "1234567890";
		
		for (int i = 0; i < length; i++)
		{
			int charSet = Rnd.nextInt(3);
			switch (charSet)
			{
				case 0:
					password.append(lowerChar.charAt(Rnd.nextInt(lowerChar.length() - 1)));
					break;
				case 1:
					password.append(upperChar.charAt(Rnd.nextInt(upperChar.length() - 1)));
					break;
				case 2:
					password.append(digits.charAt(Rnd.nextInt(digits.length() - 1)));
					break;
			}
		}
		return password.toString();
	}
	
	public void SendMessageToTelnets(String msg)
	{
		List<LoginStatusThread> lsToRemove = new ArrayList<>();
		for (LoginStatusThread ls : loginStatus)
		{
			if (ls.isInterrupted())
			{
				lsToRemove.add(ls);
			}
			else
			{
				ls.printToTelnet(msg);
			}
		}
	}
}
