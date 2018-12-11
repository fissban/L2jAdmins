package l2j.gameserver.scripts.ai.npc.teleports.grandboss;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.util.Rnd;

/**
 * Valakas TeleportCube<br>
 * Antharas TeleporCube<br>
 * Baium TeleportCube<br>
 * @author fissban
 */
public class Cube extends Script
{
	// Npc
	private static final int ANTHARAS_TELEPORT_CUBE = 12324;
	private static final int VALAKAS_TELEPORT_CUBE = 8759;
	private static final int BAIUM_TELEPORT_CUBE = 12078;
	
	public Cube()
	{
		super(-1, "ai/npc/teleports/grandboss");
		
		addStartNpc(VALAKAS_TELEPORT_CUBE, ANTHARAS_TELEPORT_CUBE, BAIUM_TELEPORT_CUBE);
		addTalkId(VALAKAS_TELEPORT_CUBE, ANTHARAS_TELEPORT_CUBE, BAIUM_TELEPORT_CUBE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equals("Exit"))
		{
			int x = 0;
			int y = 0;
			int z = 0;
			switch (npc.getId())
			{
				case ANTHARAS_TELEPORT_CUBE:
					x = 79800 + Rnd.get(600);
					y = 151200 + Rnd.get(1100);
					z = -3534;
					break;
				case VALAKAS_TELEPORT_CUBE:
					x = 150037 + Rnd.get(500);
					y = -57720 + Rnd.get(500);
					z = -2976;
					break;
				case BAIUM_TELEPORT_CUBE:
					switch (Rnd.get(3))
					{
						case 0:
							x = 108784 + Rnd.get(100);
							y = 16000 + Rnd.get(100);
							z = -4928;
							break;
						case 1:
							x = 113824 + Rnd.get(100);
							y = 10448 + Rnd.get(100);
							z = -5164;
							break;
						case 2:
							x = 115488 + Rnd.get(100);
							y = 22096 + Rnd.get(100);
							z = -5168;
							break;
					}
			}
			
			player.teleToLocation(x, y, z);
		}
		
		return null;
	}
}
