package l2j.gameserver.scripts.ai.npc.teleports.grandboss;

import l2j.gameserver.data.DoorData;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;

/**
 * Opening some doors
 * @author fissban
 */
public class FireDragon extends Script
{
	// Npcs
	private static final int[] NPCS =
	{
		8384,
		8686,
		8687
	};
	
	public FireDragon()
	{
		super(-1, "ai/npc/teleports/grandboss");
		
		addStartNpc(NPCS);
		addTalkId(NPCS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equals("openDoor"))
		{
			switch (npc.getId())
			{
				case 8384:
					DoorData.getInstance().getDoor(24210004).openMe();
					break;
				case 8686:
					DoorData.getInstance().getDoor(24210006).openMe();
					break;
				case 8687:
					DoorData.getInstance().getDoor(24210005).openMe();
					break;
			}
		}
		return null;
	}
}
