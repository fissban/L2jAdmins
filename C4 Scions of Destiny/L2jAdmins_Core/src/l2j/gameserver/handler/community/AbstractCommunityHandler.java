package l2j.gameserver.handler.community;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import l2j.Config;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.server.ShowBoard;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author fissban
 */
public abstract class AbstractCommunityHandler
{
	public static final Logger LOG = Logger.getLogger(AbstractCommunityHandler.class.getName());
	
	public static final String CB_PATH = "data/html/communityBoard/";
	
	public void parseCmd(String command, L2PcInstance activeChar)
	{
		if (!Config.COMMUNITY_ENABLE)
		{
			activeChar.sendPacket(SystemMessage.CB_OFFLINE);
			return;
		}
		
		try
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			
			useCommunityCommand(st, activeChar);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void parseWrite(String index, L2PcInstance activeChar, String ar1, String ar2, String ar3, String ar4, String ar5)
	{
		try
		{
			useCommunityWrite(activeChar, ar1, ar2, ar3, ar4, ar5);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Mostramos al player un html dentro del community.
	 * @param html
	 * @param player
	 */
	public static void separateAndSend(String html, L2PcInstance player)
	{
		if (html == null)
		{
			return;
		}
		
		if (html.length() < 8180)
		{
			player.sendPacket(new ShowBoard(html, "101"));
			player.sendPacket(new ShowBoard(null, "102"));
			player.sendPacket(new ShowBoard(null, "103"));
		}
		else if (html.length() < 16360)
		{
			player.sendPacket(new ShowBoard(html.substring(0, 8180), "101"));
			player.sendPacket(new ShowBoard(html.substring(8180, html.length()), "102"));
			player.sendPacket(new ShowBoard(null, "103"));
		}
		else if (html.length() < 24540)
		{
			player.sendPacket(new ShowBoard(html.substring(0, 8180), "101"));
			player.sendPacket(new ShowBoard(html.substring(8180, 16360), "102"));
			player.sendPacket(new ShowBoard(html.substring(16360, html.length()), "103"));
		}
	}
	
	/**
	 * @param html
	 * @param acha
	 */
	public static void send1001(String html, L2PcInstance acha)
	{
		if (html.length() < 8180)
		{
			acha.sendPacket(new ShowBoard(html, "1001"));
		}
	}
	
	/**
	 * @param acha
	 */
	public static void send1002(L2PcInstance acha)
	{
		send1002(acha, " ", " ", "0");
	}
	
	/**
	 * @param activeChar
	 * @param string
	 * @param string2
	 * @param string3
	 */
	public static void send1002(L2PcInstance activeChar, String string, String string2, String string3)
	{
		List<String> list = new ArrayList<>();
		list.add("0");
		list.add("0");
		list.add("0");
		list.add("0");
		list.add("0");
		list.add("0");
		list.add(activeChar.getName());
		list.add(Integer.toString(activeChar.getObjectId()));
		list.add(activeChar.getAccountName());
		list.add("9");
		list.add(string2);
		list.add(string2);
		list.add(string);
		list.add(string3);
		list.add(string3);
		list.add("0");
		list.add("0");
		activeChar.sendPacket(new ShowBoard(list));
	}
	
	/**
	 * @param activeChar
	 * @param st
	 */
	public abstract void useCommunityCommand(StringTokenizer st, L2PcInstance activeChar);
	
	public abstract void useCommunityWrite(L2PcInstance activeChar, String ar1, String ar2, String ar3, String ar4, String ar5);
	
	public abstract String[] getCmdList();
	
	public abstract String getWriteList();
}
