package l2j.gameserver.scripts.ai.npc.teleports.grandboss;

import l2j.gameserver.data.GrandBossSpawnData;
import l2j.gameserver.instancemanager.zone.ZoneGrandBossManager;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.ItemHolder;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.zone.type.BossZone;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;

/**
 * @author fissban
 */
public class AngelicVortex extends Script
{
	// Npc
	private static final int VORTEX = 12571;
	// GrandBoss
	private static final int LIVE_BAIUM = 12372;
	// State
	private static final int ASLEEP = 0;
	// Item
	private static final ItemHolder BLODY_FABRIC = new ItemHolder(4295, 1);
	// Loc
	private static final LocationHolder TELEPORT = new LocationHolder(114077, 15882, 10078);
	// Html
	private static final String HTML_PATH = "data/html/teleporter/grandboss/angelicVortex/";
	
	public AngelicVortex()
	{
		super(-1, "ai/npc/teleports/grandboss");
		
		addStartNpc(VORTEX);
		addTalkId(VORTEX);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		
		if (event.equals("enter"))
		{
			BossZone zone = ZoneGrandBossManager.getZone(12007);
			
			if (!st.hasItems(BLODY_FABRIC.getId()))
			{
				return HTML_PATH + "noItems.htm";
			}
			
			if (player.isFlying())
			{
				return HTML_PATH + "cantEnterFly.htm";
			}
			
			if (GrandBossSpawnData.getBossInfo(LIVE_BAIUM).getStatus() == ASLEEP)
			{
				st.takeItems(BLODY_FABRIC.getId(), BLODY_FABRIC.getCount());
				zone.allowPlayerEntry(player, 30);
				player.teleToLocation(TELEPORT.getX(), TELEPORT.getY(), TELEPORT.getZ());
			}
			else
			{
				return HTML_PATH + "cantEnter.htm";
			}
		}
		
		return null;
	}
}
