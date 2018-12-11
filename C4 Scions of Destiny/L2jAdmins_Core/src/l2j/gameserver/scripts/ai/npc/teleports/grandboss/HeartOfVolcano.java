package l2j.gameserver.scripts.ai.npc.teleports.grandboss;

import l2j.gameserver.data.GrandBossSpawnData;
import l2j.gameserver.data.ScriptsData;
import l2j.gameserver.instancemanager.zone.ZoneGrandBossManager;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.scripts.Script;
import l2j.gameserver.scripts.ai.mobs.grandboss.Valakas;
import l2j.util.Rnd;

/**
 * TODO: falta agregar el limite de usuarios q pueden ingresar, q segun mi informacion es de 200<br>
 * Teleport into Hall of Flames
 * @author fissban
 */
public class HeartOfVolcano extends Script
{
	// Npc
	private static final int NPC = 8385;
	// GrandBoss
	private static final int VALAKAS = 12899;
	// GrandBoss State
	private static final byte DORMANT = 0; // Valakas is spawned and no one has entered yet. Entry is unlocked
	private static final byte WAITING = 1; // Valakas is spawned and someone has entered, triggering a 30 minute window for additional people to enter
	// Loc
	private static final LocationHolder LOCATION = new LocationHolder(204328, -111874, 70);
	// Html
	private static final String HTML_PATH = "data/html/teleport/grandboss/heartOfVolcano/";
	
	public HeartOfVolcano()
	{
		super(-1, "ai/npc/teleports/grandboss");
		
		addStartNpc(NPC);
		addTalkId(NPC);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equals("Enter"))
		{
			Script questValakas = ScriptsData.get(Valakas.class.getSimpleName());
			
			if (questValakas == null)
			{
				return HTML_PATH + "cantConfront.htm";
			}
			
			int status = GrandBossSpawnData.getBossInfo(VALAKAS).getStatus();
			// If entrance to see Antharas is unlocked (he is Dormant or Waiting)
			if ((status == DORMANT) || (status == WAITING))
			{
				ZoneGrandBossManager.getZone(LOCATION).allowPlayerEntry(player, 30);
				player.teleToLocation(LOCATION.getX() + Rnd.get(600), LOCATION.getY() + Rnd.get(600), LOCATION.getZ());
				if (status == DORMANT)
				{
					GrandBossSpawnData.getBossInfo(VALAKAS).setStatus(WAITING);
					questValakas.startTimer("waiting", 1800000, GrandBossSpawnData.getBossInfo(VALAKAS).getBoss(), null);
				}
			}
			else
			{
				return HTML_PATH + "isFull.htm";
			}
		}
		
		return null;
	}
}
