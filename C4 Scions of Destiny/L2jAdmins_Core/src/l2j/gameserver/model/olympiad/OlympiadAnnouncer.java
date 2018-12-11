package l2j.gameserver.model.olympiad;

import java.util.ArrayList;
import java.util.List;

import l2j.gameserver.data.SpawnData;
import l2j.gameserver.model.spawn.Spawn;
import l2j.gameserver.network.external.client.Say2.SayType;
import l2j.gameserver.network.external.server.CreatureSay;

/**
 * @author DS
 */
public final class OlympiadAnnouncer implements Runnable
{
	private static final int OLY_MANAGER = 8688;
	
	private final List<Spawn> managers = new ArrayList<>();
	private int currentStadium = 0;
	
	public OlympiadAnnouncer()
	{
		for (Spawn spawn : SpawnData.getInstance().getSpawnTable())
		{
			if ((spawn != null) && (spawn.getNpcId() == OLY_MANAGER))
			{
				managers.add(spawn);
			}
		}
	}
	
	@Override
	public void run()
	{
		OlympiadGameTask task;
		for (int i = OlympiadGameManager.getInstance().getNumberOfStadiums(); --i >= 0; currentStadium++)
		{
			if (currentStadium >= OlympiadGameManager.getInstance().getNumberOfStadiums())
			{
				currentStadium = 0;
			}
			
			task = OlympiadGameManager.getInstance().getOlympiadTask(currentStadium);
			if ((task != null) && (task.getGame() != null) && task.needAnnounce())
			{
				String npcString;
				final String arenaId = String.valueOf(task.getGame().getStadiumId() + 1);
				switch (task.getGame().getType())
				{
					case NON_CLASSED:
						npcString = "Olympiad class-free individual match is going to begin in Arena " + arenaId + " in a moment.";
						break;
					
					case CLASSED:
						npcString = "Olympiad class individual match is going to begin in Arena " + arenaId + " in a moment.";
						break;
					
					default:
						continue;
				}
				
				managers.stream().filter(spawn -> spawn.getLastSpawn() != null).forEach(spawn -> spawn.getLastSpawn().broadcastPacket(new CreatureSay(spawn.getLastSpawn(), SayType.SHOUT, spawn.getLastSpawn().getName(), npcString)));
				break;
			}
		}
	}
}
