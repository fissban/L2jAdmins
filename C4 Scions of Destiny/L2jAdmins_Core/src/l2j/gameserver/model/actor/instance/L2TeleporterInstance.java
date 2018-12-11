package l2j.gameserver.model.actor.instance;

import java.util.StringTokenizer;

import l2j.Config;
import l2j.gameserver.data.CastleData;
import l2j.gameserver.data.TeleportLocationData;
import l2j.gameserver.instancemanager.siege.SiegeManager;
import l2j.gameserver.instancemanager.zone.ZoneTownManager;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.enums.ConditionInteractNpcType;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.templates.NpcTemplate;
import l2j.gameserver.model.holder.LocationTeleportHolder;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.NpcHtmlMessage;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author  NightMarez
 * @version $Revision: 1.3.2.2.2.5 $ $Date: 2005/03/27 15:29:32 $
 */
public final class L2TeleporterInstance extends L2Npc
{
	public L2TeleporterInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setInstanceType(InstanceType.L2TeleporterInstance);
	}
	
	@Override
	public boolean isTeleport()
	{
		return true;
	}
	
	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		if (player.isAlikeDead())
		{
			return;
		}
		
		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken(); // Get actual command
		
		if (actualCommand.equalsIgnoreCase("goto"))
		{
			if (!st.hasMoreTokens())
			{
				return;
			}
			
			int id = Integer.parseInt(st.nextToken());
			
			LocationTeleportHolder list = TeleportLocationData.getInstance().getTemplate(id);
			if (list == null)
			{
				LOG.warning("No teleport destination with id:" + id);
				return;
			}
			
			switch (validateCondition(player))
			{
				case REGULAR:
				case CASTLE_OWNER:
					doTeleport(player, list);
					break;
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String pom = "";
		if (val == 0)
		{
			pom = "" + npcId;
		}
		else
		{
			pom = npcId + "-" + val;
		}
		
		return "data/html/teleporter/" + pom + ".htm";
	}
	
	@Override
	public void showChatWindow(L2PcInstance player)
	{
		String fileName = "";
		
		switch (validateCondition(player))
		{
			case BUSY_BECAUSE_OF_SIEGE:
				fileName = "data/html/teleporter/castleteleporter-busy.htm";
				break;
			case ALL_FALSE:
				fileName = "data/html/teleporter/castleteleporter-no.htm";
				break;
			case REGULAR:
			case CASTLE_OWNER:
				fileName = getHtmlPath(getId(), 0); // Owner message window
				break;
		}
		
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(fileName);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		html.replace("%npcname%", getName());
		player.sendPacket(html);
	}
	
	private void doTeleport(L2PcInstance player, LocationTeleportHolder list)
	{
		// siege checks
		// if teleport to castle
		if (SiegeManager.getInstance().getSiege(list.getX(), list.getY(), list.getZ()) != null)
		{
			player.sendPacket(SystemMessage.NO_PORT_THAT_IS_IN_SIGE);
			return;
		}
		// siege checks
		// if teleport to city
		if (ZoneTownManager.townHasCastleInSiege(list.getX(), list.getY()) && getIsInCastleTown())
		{
			player.sendPacket(SystemMessage.NO_PORT_THAT_IS_IN_SIGE);
			return;
		}
		
		if ((player.getKarma() > 0) && !Config.ALT_GAME_KARMA_PLAYER_CAN_USE_GK) // karma
		{
			player.sendMessage("Go away, you're not welcome here.");
			return;
		}
		
		if (Config.ALT_GAME_FREE_TELEPORT || player.getInventory().reduceAdena("Teleport", list.getPrice(), this, true))
		{
			player.teleToLocation(list.getX(), list.getY(), list.getZ(), true);
			return;
		}
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	private ConditionInteractNpcType validateCondition(L2PcInstance player)
	{
		if (CastleData.getInstance().getCastleId(this) < 0)
		{
			return ConditionInteractNpcType.REGULAR; // Regular access
		}
		
		if (getCastle().getSiege().isInProgress())
		{
			return ConditionInteractNpcType.BUSY_BECAUSE_OF_SIEGE; // Busy because of siege
		}
		
		if ((player.getClan() != null) && (getCastle().getOwnerId() == player.getClanId()))
		{
			return ConditionInteractNpcType.CASTLE_OWNER;
		}
		
		return ConditionInteractNpcType.ALL_FALSE;
	}
}
