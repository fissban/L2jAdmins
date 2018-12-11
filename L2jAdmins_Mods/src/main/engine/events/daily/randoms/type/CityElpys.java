package main.engine.events.daily.randoms.type;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import main.data.ConfigData;
import main.engine.AbstractMod;
import main.holders.RewardHolder;
import main.holders.objects.CharacterHolder;
import main.holders.objects.NpcHolder;
import main.packets.ObjectPosition;
import main.util.UtilInventory;
import main.util.UtilSpawn;
import l2j.gameserver.data.ItemData;
import l2j.gameserver.data.MapRegionData;
import l2j.gameserver.model.actor.instance.enums.TeamType;
import l2j.gameserver.model.holder.LocationHolder;
import l2j.gameserver.model.world.L2World;
import l2j.gameserver.util.Broadcast;
import l2j.util.Rnd;

/**
 * @author fissban
 */
public class CityElpys extends AbstractMod
{
	// lista de los elpys que se spawnean en el evento
	private static final List<NpcHolder> elpys = new CopyOnWriteArrayList<>();
	
	public CityElpys()
	{
		registerMod(false);
	}
	
	@Override
	public void onModState()
	{
		switch (getState())
		{
			case START:
				spawn();
				break;
			case END:
				unspawn();
				break;
		}
	}
	
	private void spawn()
	{
		// Se remueve todos los elpys del evento anterior
		unspawn();
		// Se obtiene un lugar random para el evento
		var loc = ConfigData.ELPY_LOC.get(Rnd.get(ConfigData.ELPY_LOC.size()));
		// Se anuncia donde se generaran los spawns
		var locName = MapRegionData.getInstance().getClosestTownName(loc.getX(), loc.getY());
		Broadcast.toAllOnlinePlayers("Elpys spawn near " + locName);
		// Se generan los nuevos spawns
		for (int i = 0; i < ConfigData.ELPY_COUNT; i++)
		{
			var x = loc.getX() + Rnd.get(-ConfigData.ELPY_RANGE_SPAWN, ConfigData.ELPY_RANGE_SPAWN);
			var y = loc.getY() + Rnd.get(-ConfigData.ELPY_RANGE_SPAWN, ConfigData.ELPY_RANGE_SPAWN);
			var z = loc.getZ();
			
			var nh = UtilSpawn.npc(ConfigData.ELPY, new LocationHolder(x, y, z + 20), 0, 0, TeamType.NONE, 0);
			
			if (nh != null)
			{
				elpys.add(nh);
			}
		}
		
		// Send packet ObjectPosition
		L2World.getInstance().getAllPlayers().forEach(p -> p.sendPacket(new ObjectPosition(elpys)));
	}
	
	@Override
	public void onKill(CharacterHolder killer, CharacterHolder victim, boolean isPet)
	{
		if (elpys.contains(victim))
		{
			elpys.remove(victim);
			for (RewardHolder reward : ConfigData.ELPY_REWARDS)
			{
				if (Rnd.get(100) <= reward.getRewardChance())
				{
					killer.getInstance().sendMessage("Have won " + reward.getRewardCount() + " " + ItemData.getInstance().getTemplate(reward.getRewardId()).getName());
					UtilInventory.giveItems(killer.getActingPlayer(), reward.getRewardId(), reward.getRewardCount(), 0);
				}
			}
		}
	}
	
	private static void unspawn()
	{
		for (var mob : elpys)
		{
			mob.getInstance().deleteMe();
		}
		// Se limpia la variable
		elpys.clear();
	}
}
