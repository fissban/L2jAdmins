package l2j.loginserver.network.external.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import l2j.loginserver.GameServerTable;
import l2j.loginserver.model.ServerData;
import l2j.loginserver.network.ALoginServerPacket;
import l2j.loginserver.network.LoginClient;
import l2j.loginserver.network.internal.gameserver.ServerStatus;

public final class ServerList extends ALoginServerPacket
{
	private final List<ServerData> servers = new ArrayList<>();
	private final int lastServer;
	
	public ServerList(LoginClient client)
	{
		lastServer = client.getLastServer();
		
		for (var gsi : GameServerTable.getInstance().getRegisteredGameServers().values())
		{
			var status = (gsi.getStatus() != ServerStatus.STATUS_GM_ONLY) ? gsi.getStatus() : (client.getAccessLevel() > 0) ? gsi.getStatus() : ServerStatus.STATUS_DOWN;
			var hostName = gsi.getHostName();
			
			servers.add(new ServerData(status, hostName, gsi));
		}
	}
	
	@Override
	public void write()
	{
		writeC(0x04);
		writeC(servers.size());
		writeC(lastServer);
		
		for (ServerData server : servers)
		{
			writeC(server.getServerId());
			
			try
			{
				if (isInternalIP(getClient().getConnection().getInetAddress().getHostAddress()))
				{
					writeC(127);
					writeC(0);
					writeC(0);
					writeC(1);
				}
				else
				{
					var raw = InetAddress.getByName(server.getHostName()).getAddress();
					writeC(raw[0] & 0xff);
					writeC(raw[1] & 0xff);
					writeC(raw[2] & 0xff);
					writeC(raw[3] & 0xff);
				}
			}
			catch (UnknownHostException e)
			{
				e.printStackTrace();
				writeC(127);
				writeC(0);
				writeC(0);
				writeC(1);
			}
			
			writeD(server.getPort());
			writeC(server.getAgeLimit());
			writeC(server.isPvp() ? 0x01 : 0x00);
			writeH(server.getCurrentPlayers());
			writeH(server.getMaxPlayers());
			writeC(server.getStatus() == ServerStatus.STATUS_DOWN ? 0x00 : 0x01);
			
			var bits = 0;
			if (server.isTestServer())
			{
				bits |= 0x04;
			}
			
			if (server.isShowingClock())
			{
				bits |= 0x02;
			}
			
			writeD(bits);
			writeC(server.isShowingBrackets() ? 0x01 : 0x00);
		}
	}
	
	private static boolean isInternalIP(String ipAddress)
	{
		InetAddress addr = null;
		try
		{
			addr = InetAddress.getByName(ipAddress);
			return addr.isSiteLocalAddress() || addr.isLoopbackAddress();
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		return false;
	}
}