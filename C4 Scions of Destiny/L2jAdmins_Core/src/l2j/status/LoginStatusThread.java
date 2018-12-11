package l2j.status;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Properties;
import java.util.logging.Logger;

import l2j.Config;
import l2j.L2DatabaseFactory;
import l2j.loginserver.LoginServer;

public class LoginStatusThread extends Thread
{
	private static final Logger LOG = Logger.getLogger(LoginStatusThread.class.getName());
	
	private Socket csocket;
	
	private PrintWriter print;
	private BufferedReader read;
	
	private boolean redirectLogger;
	
	private String pass;
	
	private void telnetOutput(int type, String text)
	{
		if (type == 1)
		{
			System.out.println("TELNET | " + text);
		}
		else if (type == 2)
		{
			System.out.print("TELNET | " + text);
		}
		else if (type == 3)
		{
			System.out.print(text);
		}
		else if (type == 4)
		{
			System.out.println(text);
		}
		else
		{
			System.out.println("TELNET | " + text);
		}
	}
	
	private boolean isValidIP(Socket client)
	{
		boolean result = false;
		InetAddress ClientIP = client.getInetAddress();
		
		// convert IP to String, and compare with list
		String clientStringIP = ClientIP.getHostAddress();
		
		telnetOutput(1, "Connection from: " + clientStringIP);
		
		try (InputStream telnetIS = new FileInputStream(new File(Config.FILE_TELNET)))
		{
			Properties telnetSettings = new Properties();
			telnetSettings.load(telnetIS);
			
			String HostList = telnetSettings.getProperty("ListOfHosts", "127.0.0.1,localhost");
			
			// compare
			String ipToCompare = null;
			for (String ip : HostList.split(","))
			{
				if (!result)
				{
					ipToCompare = InetAddress.getByName(ip).getHostAddress();
					if (clientStringIP.equals(ipToCompare))
					{
						result = true;
					}
				}
			}
		}
		catch (IOException e)
		{
			telnetOutput(1, "Error: " + e);
		}
		
		return result;
	}
	
	public LoginStatusThread(Socket client, int uptime) throws IOException
	{
		csocket = client;
		
		print = new PrintWriter(csocket.getOutputStream());
		read = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
		
		if (isValidIP(client))
		{
			telnetOutput(1, client.getInetAddress().getHostAddress() + " accepted.");
			print.println("Welcome To The L2J Telnet Session.");
			print.println("Please Insert Your Login!");
			print.print("Login: ");
			print.flush();
			String tmpLine = read.readLine();
			if (tmpLine == null)
			{
				print.println("Error.");
				print.println("Disconnected...");
				print.flush();
				csocket.close();
				return;
			}
			if (!validLogin(tmpLine))
			{
				print.println("Incorrect Login!");
				print.println("Disconnected...");
				print.flush();
				csocket.close();
				return;
			}
			print.println("Login Correct!");
			print.flush();
			print.println("Please Insert Your Password!");
			print.print("Password: ");
			print.flush();
			tmpLine = read.readLine();
			if (tmpLine == null)
			{
				print.println("Error.");
				print.println("Disconnected...");
				print.flush();
				csocket.close();
			}
			else
			{
				if (!validPassword(tmpLine))
				{
					print.println("Incorrect Password!");
					print.println("Disconnected...");
					print.flush();
					csocket.close();
				}
				else
				{
					print.println("Password Correct!");
					print.println("[L2J]");
					print.print("");
					print.flush();
					start();
				}
			}
		}
		else
		{
			telnetOutput(1, "Connection attempt from " + client.getInetAddress().getHostAddress() + " rejected.");
			csocket.close();
		}
	}
	
	/**
	 * @param  password
	 * @return
	 */
	private boolean validPassword(String password)
	{
		byte[] expectedPass = Base64.getDecoder().decode(pass);
		MessageDigest md;
		try
		{
			md = MessageDigest.getInstance("SHA");
			byte[] raw = password.getBytes("UTF-8");
			byte[] hash = md.digest(raw);
			for (int i = 0; i < expectedPass.length; i++)
			{
				if (hash[i] != expectedPass[i])
				{
					return false;
				}
			}
			return true;
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException uee)
		{
			
		}
		return false;
	}
	
	/**
	 * @param  login
	 * @return
	 */
	private boolean validLogin(String login)
	{
		boolean ok = true;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT password, access_level FROM accounts WHERE login=?"))
		{
			ps.setString(1, login);
			
			try (ResultSet rset = ps.executeQuery())
			{
				if (rset.next())
				{
					if (rset.getInt("access_level") < 1)
					{
						ok = false;
					}
					else
					{
						pass = rset.getString("password");
					}
				}
			}
		}
		catch (SQLException sqle)
		{
			sqle.printStackTrace();
		}
		
		return ok;
	}
	
	@Override
	public void run()
	{
		String usrCommand = "";
		try
		{
			while ((usrCommand.compareTo("quit") != 0) && (usrCommand.compareTo("exit") != 0))
			{
				usrCommand = read.readLine();
				if (usrCommand == null)
				{
					csocket.close();
					break;
				}
				if (usrCommand.equals("help"))
				{
					print.println("The following is a list of all available commands: ");
					print.println("help                - shows this help.");
					print.println("status              - displays basic server statistics.");
					print.println("unblockip <ip>      - removes <ip> from hacking protection list.");
					print.println("shutdown			- shuts down server.");
					print.println("restart				- restarts the server.");
					print.println("RedirectLogger		- Telnet will give you some info about server in real time.");
					print.println("quit                - closes telnet session.");
					print.println("");
				}
				else if (usrCommand.equals("status"))
				{
					//
				}
				else if (usrCommand.startsWith("unblock"))
				{
					//
				}
				else if (usrCommand.startsWith("shutdown"))
				{
					LoginServer.getInstance().shutdown(false);
					print.println("Bye Bye!");
					print.flush();
					csocket.close();
				}
				else if (usrCommand.startsWith("restart"))
				{
					LoginServer.getInstance().shutdown(true);
					print.println("Bye Bye!");
					print.flush();
					csocket.close();
				}
				else if (usrCommand.equals("RedirectLogger"))
				{
					redirectLogger = true;
				}
				else if (usrCommand.equals("quit"))
				{ /* Do Nothing :p - Just here to save us from the "Command Not Understood" Text */
				}
				else if (usrCommand.length() == 0)
				{ /* Do Nothing Again - Same reason as the quit part */
				}
				else
				{
					print.println("Invalid Command");
				}
				print.print("");
				print.flush();
			}
			if (!csocket.isClosed())
			{
				print.println("Bye Bye!");
				print.flush();
				csocket.close();
			}
			telnetOutput(1, "Connection from " + csocket.getInetAddress().getHostAddress() + " was closed by client.");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void printToTelnet(String msg)
	{
		synchronized (print)
		{
			print.println(msg);
			print.flush();
		}
	}
	
	/**
	 * @return Returns the redirectLogger.
	 */
	public boolean isRedirectLogger()
	{
		return redirectLogger;
	}
}
