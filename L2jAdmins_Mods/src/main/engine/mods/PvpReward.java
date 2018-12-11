package main.engine.mods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.data.ConfigData;
import main.engine.AbstractMod;
import main.holders.objects.CharacterHolder;
import main.holders.objects.PlayerHolder;
import main.util.Util;
import main.util.UtilMessage;
import l2j.gameserver.data.ItemData;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.network.external.client.Say2.SayType;
import l2j.util.Rnd;

/**
 * @author fissban
 */
public class PvpReward extends AbstractMod
{
	public class PvPHolder
	{
		public int victim;
		public long time;
		
		public PvPHolder(int victim, long time)
		{
			this.victim = victim;
			this.time = time;
		}
	}
	
	// Variable in charge of carrying the victims and the time in which they died.
	private static Map<Integer, List<PvPHolder>> pvp = new HashMap<>();
	
	/**
	 * Constructor
	 */
	public PvpReward()
	{
		registerMod(ConfigData.ENABLE_PvpReward);
	}
	
	@Override
	public void onModState()
	{
		//
	}
	
	@Override
	public void onKill(CharacterHolder killer, CharacterHolder victim, boolean isPet)
	{
		if (!Util.areObjectType(L2PcInstance.class, victim) || killer.getActingPlayer() == null)
		{
			return;
		}
		
		var phKiller = killer.getActingPlayer();
		var phVictim = victim.getActingPlayer();
		
		// Check if this character won some PvP
		if (!pvp.containsKey(phKiller.getObjectId()))
		{
			// The list of victims is initialized
			pvp.put(phKiller.getObjectId(), new ArrayList<>());
		}
		
		// Check the list of killer victims and the time elapsed.
		for (var pvp : pvp.get(phKiller.getObjectId()))
		{
			// If you find that I ever kill this player, it checks how long it was.
			if (pvp.victim == phVictim.getObjectId())
			{
				if (pvp.time + ConfigData.PVP_TIME < System.currentTimeMillis())
				{
					// The prize is awarded
					giveRewards(phKiller);
					// The time is reset
					pvp.time = System.currentTimeMillis();
				}
				return;
			}
		}
		
		// If we get here it's because it's the first kill to this player.
		pvp.get(phKiller.getObjectId()).add(new PvPHolder(victim.getObjectId(), System.currentTimeMillis()));
	}
	
	/**
	 * Prizes are delivered and a custom message is sent for each prize.
	 * @param ph
	 * @param victim
	 */
	private static void giveRewards(PlayerHolder ph)
	{
		var player = ph.getInstance();
		
		for (var reward : ConfigData.PVP_REWARDS)
		{
			if (Rnd.get(100) <= reward.getRewardChance())
			{
				UtilMessage.sendCreatureMsg(ph, SayType.TELL, "", "Have won " + reward.getRewardCount() + " " + ItemData.getInstance().getTemplate(reward.getRewardId()).getName());
				player.getInventory().addItem("PvpReward", reward.getRewardId(), reward.getRewardCount(), player, null);
			}
		}
	}
}
