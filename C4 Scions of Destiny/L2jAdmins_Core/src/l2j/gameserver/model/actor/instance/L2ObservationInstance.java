package l2j.gameserver.model.actor.instance;

import java.util.StringTokenizer;

import l2j.gameserver.instancemanager.siege.SiegeManager;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.enums.InstanceType;
import l2j.gameserver.model.actor.manager.character.templates.NpcTemplate;
import l2j.gameserver.model.olympiad.OlympiadManager;
import l2j.gameserver.network.external.server.ActionFailed;
import l2j.gameserver.network.external.server.ItemList;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author  NightMarez
 * @version $Revision: 1.3.2.2.2.5 $ $Date: 2005/03/27 15:29:32 $
 */
public final class L2ObservationInstance extends L2Npc
{
	/**
	 * @param objectId
	 * @param template
	 */
	public L2ObservationInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setInstanceType(InstanceType.L2ObservationInstance);
	}
	
	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		if (player == null)
		{
			return;
		}
		
		if (OlympiadManager.getInstance().isRegisteredInComp(player))
		{
			return;
		}
		
		if (command.startsWith("observeSiege"))
		{
			String val = command.substring(13);
			StringTokenizer st = new StringTokenizer(val);
			st.nextToken(); // Bypass cost
			
			if (SiegeManager.getInstance().getSiege(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())) != null)
			{
				doObserve(player, val);
			}
			else
			{
				player.sendPacket(SystemMessage.ONLY_VIEW_SIEGE);
			}
			
		}
		else if (command.startsWith("observe"))
		{
			doObserve(player, command.substring(8));
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
		
		return "data/html/observation/" + pom + ".htm";
	}
	
	private void doObserve(L2PcInstance player, String val)
	{
		StringTokenizer st = new StringTokenizer(val);
		int cost = Integer.parseInt(st.nextToken());
		int x = Integer.parseInt(st.nextToken());
		int y = Integer.parseInt(st.nextToken());
		int z = Integer.parseInt(st.nextToken());
		if (player.getInventory().reduceAdena("Broadcast", cost, this, true))
		{
			// enter mode
			player.enterObserverMode(x, y, z);
			ItemList il = new ItemList(player, false);
			player.sendPacket(il);
		}
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
}
