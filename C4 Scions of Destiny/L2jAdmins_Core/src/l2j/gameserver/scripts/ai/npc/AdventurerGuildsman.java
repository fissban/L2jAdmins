package l2j.gameserver.scripts.ai.npc;

import java.util.StringTokenizer;

import l2j.gameserver.data.RaidBossSpawnData;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.spawn.Spawn;
import l2j.gameserver.network.external.server.ExQuestInfo;
import l2j.gameserver.network.external.server.RadarControl;
import l2j.gameserver.scripts.Script;

/**
 * @author fissban
 */
public class AdventurerGuildsman extends Script
{
	// Npc
	private static final int[] NPC_1 =
	{
		8729,
		8738
	};
	private static final int[] NPC_2 =
	{
		8775,
		8841
	};
	// html
	private static final String HTML_PATH = "data/html/adventurerGuildsman/";
	
	public AdventurerGuildsman()
	{
		super(-1, "ai/npc");
		
		for (int npc = NPC_1[0]; npc <= NPC_1[1]; npc++)
		{
			addStartNpc(npc);
			addTalkId(npc);
		}
		for (int npc = NPC_2[0]; npc <= NPC_2[1]; npc++)
		{
			addStartNpc(npc);
			addTalkId(npc);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		StringTokenizer st = new StringTokenizer(event, " ");
		st.nextToken(); // actual command
		
		if (event.startsWith("level"))
		{
			return HTML_PATH + "level" + st.nextToken() + ".htm";
		}
		if (event.startsWith("raidInfo"))
		{
			if (st.hasMoreTokens())
			{
				return HTML_PATH + "level" + st.nextToken() + ".htm";
			}
			
			return HTML_PATH + "info.htm";
		}
		if (event.startsWith("npcfind_byid"))
		{
			if (st.hasMoreTokens())
			{
				int bossId = Integer.parseInt(st.nextToken());
				
				switch (RaidBossSpawnData.getInstance().getRaidBossStatusId(bossId))
				{
					case ALIVE:
					case DEAD:
						Spawn spawn = RaidBossSpawnData.getInstance().getSpawns().get(bossId);
						player.sendPacket(new RadarControl(2, 2, spawn.getX(), spawn.getY(), spawn.getZ()));
						player.sendPacket(new RadarControl(0, 1, spawn.getX(), spawn.getY(), spawn.getZ()));
						break;
					
					case UNDEFINED:
						player.sendMessage("This Boss isn't in game - notify L2jAdmins Team.");
						break;
				}
			}
		}
		if (event.equals("questList"))
		{
			player.sendPacket(ExQuestInfo.STATIC_PACKET);
		}
		return null;
	}
}
