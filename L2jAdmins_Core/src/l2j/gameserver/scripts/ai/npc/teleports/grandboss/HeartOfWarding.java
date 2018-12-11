package l2j.gameserver.scripts.ai.npc.teleports.grandboss;

import l2j.gameserver.data.GrandBossSpawnData;
import l2j.gameserver.data.ScriptsData;
import l2j.gameserver.instancemanager.zone.ZoneGrandBossManager;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2GrandBossInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.ItemHolder;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ScriptState;
import l2j.util.Rnd;

/**
 * Teleport into Lair of Antharas
 * @author fissban
 */
public class HeartOfWarding extends Script
{
	// Npc
	private static final int NPC = 12250;
	// GrandBoss
	private static final int ANTHARAS = 12211;
	// Item
	private static final ItemHolder ITEM = new ItemHolder(3865, 1);
	// Loc
	private static final LocationHolder LOCATION = new LocationHolder(179700, 113800, -7709);
	// Html
	private static final String HTML_PATH = "data/html/teleporter/grandboss/heartOfWarding/";
	
	public HeartOfWarding()
	{
		super(-1, "ai/npc/teleports/grandboss");
		
		addStartNpc(NPC);
		addTalkId(NPC);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		ScriptState st = player.getScriptState(getName());
		
		if (event.equals("Enter"))
		{
			Script questAntharas = ScriptsData.get("Antharas");
			
			if (questAntharas == null)
			{
				return HTML_PATH + "cantConfront.htm";
			}
			
			if (!st.hasItems(ITEM.getId()))
			{
				return HTML_PATH + "noItems.htm";
			}
			
			switch (GrandBossSpawnData.getBossInfo(ANTHARAS).getStatus())
			{
				case 0:
					L2GrandBossInstance antharas = GrandBossSpawnData.getBossInfo(ANTHARAS).getBoss();
					questAntharas.startTimer("waiting", 1800000, antharas, null);
					GrandBossSpawnData.getBossInfo(ANTHARAS).setStatus(1);
				case 1:
					st.takeItems(ITEM.getId(), ITEM.getCount());
					ZoneGrandBossManager.getZone(LOCATION).allowPlayerEntry(player, 30);
					player.teleToLocation(LOCATION.getX() + Rnd.get(700), LOCATION.getY() + Rnd.get(2100), LOCATION.getZ());
					
					break;
				default:
					return HTML_PATH + "wait.htm";
			}
		}
		
		return null;
	}
}
