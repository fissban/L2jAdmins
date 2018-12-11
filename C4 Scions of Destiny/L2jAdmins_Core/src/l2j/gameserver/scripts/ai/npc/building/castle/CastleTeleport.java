package l2j.gameserver.scripts.ai.npc.building.castle;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.MapRegionData;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.network.external.client.Say2.SayType;
import l2j.gameserver.network.external.server.CreatureSay;
import l2j.gameserver.scripts.Script;

/**
 * @author fissban
 */
public class CastleTeleport extends Script
{
	// Npc
	private static final int[] NPCS =
	{
		7491,
		12158,
		12165,
		12246,
		12297,
		12606,
		12824,
		12841,
	};
	// Html
	private static final String HTML_PATH = "data/html/castle/teleport/";
	// Misc
	private boolean task = false;
	
	public CastleTeleport()
	{
		super(-1, "ai/npc");
		
		addStartNpc(NPCS);
		addFirstTalkId(NPCS);
		addTalkId(NPCS);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = "";
		if (!getTask())
		{
			if (npc.getCastle().getSiege().isInProgress() && (npc.getCastle().getSiege().getControlTowerMngr().getCount() == 0))
			{
				htmltext = "MassGK-2.htm";
			}
			else
			{
				htmltext = "MassGK.htm";
			}
		}
		else
		{
			htmltext = "MassGK-1.htm";
		}
		
		return HTML_PATH + htmltext;
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("tele"))
		{
			int delay;
			
			if (!getTask())
			{
				if (npc.getCastle().getSiege().isInProgress() && (npc.getCastle().getSiege().getControlTowerMngr().getCount() == 0))
				{
					delay = 480000;
				}
				else
				{
					delay = 30000;
				}
				
				setTask(true);
				ThreadPoolManager.getInstance().schedule(() ->
				{
					try
					{
						int region = MapRegionData.getInstance().getMapRegion(npc.getX(), npc.getY());
						
						for (L2PcInstance p : L2World.getInstance().getAllPlayers())
						{
							if (region == MapRegionData.getInstance().getMapRegion(p.getX(), p.getY()))
							{
								p.sendPacket(new CreatureSay(npc, SayType.SHOUT, getName(), "The defenders of " + npc.getCastle().getName() + " castle will be teleported to the inner castle."));
							}
						}
						npc.getCastle().oustAllPlayers();
						setTask(false);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}, delay);
			}
			
			return HTML_PATH + "MassGK-1.htm";
		}
		
		return null;
	}
	
	protected boolean getTask()
	{
		return task;
	}
	
	protected void setTask(boolean state)
	{
		task = state;
	}
}
