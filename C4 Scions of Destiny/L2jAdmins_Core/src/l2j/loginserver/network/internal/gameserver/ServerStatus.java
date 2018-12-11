package l2j.loginserver.network.internal.gameserver;

import java.util.logging.Logger;

import l2j.loginserver.GameServerTable;
import l2j.loginserver.model.GameServerInfo;
import l2j.loginserver.network.AClientPacket;

public class ServerStatus extends AClientPacket
{
	protected static Logger LOG = Logger.getLogger(ServerStatus.class.getName());
	
	public static final String[] STATUS_STRING =
	{
		"Auto",
		"Good",
		"Normal",
		"Full",
		"Down",
		"Gm Only"
	};
	
	public static final int STATUS = 0x01;
	public static final int CLOCK = 0x02;
	public static final int BRACKETS = 0x03;
	public static final int AGE_LIMIT = 0x04;
	public static final int TEST_SERVER = 0x05;
	public static final int PVP_SERVER = 0x06;
	public static final int MAX_PLAYERS = 0x07;
	
	public static final int STATUS_AUTO = 0x00;
	public static final int STATUS_GOOD = 0x01;
	public static final int STATUS_NORMAL = 0x02;
	public static final int STATUS_FULL = 0x03;
	public static final int STATUS_DOWN = 0x04;
	public static final int STATUS_GM_ONLY = 0x05;
	
	public static final int ON = 0x01;
	public static final int OFF = 0x00;
	
	public ServerStatus(byte[] decrypt, int serverId)
	{
		super(decrypt);
		
		GameServerInfo gsi = GameServerTable.getInstance().getRegisteredGameServers().get(serverId);
		if (gsi != null)
		{
			int size = readD();
			for (int i = 0; i < size; i++)
			{
				int type = readD();
				int value = readD();
				switch (type)
				{
					case STATUS:
						gsi.setStatus(value);
						break;
					
					case CLOCK:
						gsi.setShowingClock(value == ON);
						break;
					
					case BRACKETS:
						gsi.setShowingBrackets(value == ON);
						break;
					
					case AGE_LIMIT:
						gsi.setAgeLimit(value);
						break;
					
					case TEST_SERVER:
						gsi.setTestServer(value == ON);
						break;
					
					case PVP_SERVER:
						gsi.setPvp(value == ON);
						break;
					
					case MAX_PLAYERS:
						gsi.setMaxPlayers(value);
						break;
				}
			}
		}
	}
}