package main.engine.events.daily.normal.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2j.Config;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.instance.L2GrandBossInstance;
import l2j.gameserver.model.actor.instance.L2MonsterInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2RaidBossInstance;
import l2j.gameserver.model.actor.instance.enums.TeamType;
import l2j.gameserver.model.holder.ItemHolder;
import l2j.gameserver.model.skills.stats.enums.StatsType;
import l2j.util.Rnd;
import main.data.ConfigData;
import main.data.ObjectData;
import main.engine.events.daily.AbstractEvent;
import main.enums.ChampionType;
import main.enums.ExpSpType;
import main.enums.ItemDropType;
import main.holders.RewardHolder;
import main.holders.objects.CharacterHolder;
import main.holders.objects.NpcHolder;
import main.holders.objects.PlayerHolder;
import main.instances.NpcDropsInstance;
import main.instances.NpcExpInstance;
import main.util.Util;

/**
 * @author fissban
 */
public class Champions extends AbstractEvent
{
	public class ChampionInfoHolder
	{
		public ChampionType type;
		public int chanceToSpawn;
		public Map<StatsType, Double> allStats = new HashMap<>();
		public List<RewardHolder> rewards = new ArrayList<>();
	}
	
	// champions info
	private static final Map<ChampionType, ChampionInfoHolder> CHAMPIONS_INFO_STATS = new HashMap<>(3);
	{
		ChampionInfoHolder cih = null;
		
		cih = new ChampionInfoHolder();
		cih.type = ChampionType.WEAK_CHAMPION;
		cih.chanceToSpawn = ConfigData.CHANCE_SPAWN_WEAK;
		cih.allStats.putAll(ConfigData.CHAMPION_STAT_WEAK);
		cih.rewards.addAll(ConfigData.CHAMPION_REWARD_WEAK);
		CHAMPIONS_INFO_STATS.put(ChampionType.WEAK_CHAMPION, cih);
		
		cih = new ChampionInfoHolder();
		cih.type = ChampionType.SUPER_CHAMPION;
		cih.chanceToSpawn = ConfigData.CHANCE_SPAWN_SUPER;
		cih.allStats.putAll(ConfigData.CHAMPION_STAT_SUPER);
		cih.rewards.addAll(ConfigData.CHAMPION_REWARD_SUPER);
		CHAMPIONS_INFO_STATS.put(ChampionType.SUPER_CHAMPION, cih);
		
		cih = new ChampionInfoHolder();
		cih.type = ChampionType.HARD_CHAMPION;
		cih.chanceToSpawn = ConfigData.CHANCE_SPAWN_HARD;
		cih.allStats.putAll(ConfigData.CHAMPION_STAT_HARD);
		cih.rewards.addAll(ConfigData.CHAMPION_REWARD_HARD);
		CHAMPIONS_INFO_STATS.put(ChampionType.HARD_CHAMPION, cih);
	}
	
	public Champions()
	{
		registerEvent(ConfigData.ENABLE_Champions, ConfigData.CHAMPION_ENABLE_DAY);
	}
	
	@Override
	public void onModState()
	{
		switch (getState())
		{
			case START:
				//
				break;
			case END:
				ObjectData.getAll(NpcHolder.class).stream().filter(npc -> (npc.getInstance() != null) && npc.isChampion()).forEach(npc ->
				{
					// It becomes the npc to its original state (without team)
					npc.setTeam(TeamType.NONE);
				});
				break;
		}
	}
	
	@Override
	public void onSpawn(NpcHolder npc)
	{
		if (!checkNpcType(npc))
		{
			return;
		}
		
		for (var info : CHAMPIONS_INFO_STATS.values())
		{
			if (Rnd.get(100) < info.chanceToSpawn)
			{
				npc.setChampionType(info.type);
				// Custom effect
				npc.setTeam(TeamType.RED);
				// Custom Title
				npc.getInstance().setTitle(info.type.name().replace("_", " "));
				// Heal to max
				npc.getInstance().setCurrentHpMp(npc.getInstance().getStat().getMaxHp() * info.allStats.get(StatsType.MAX_HP), npc.getInstance().getStat().getMaxMp() * info.allStats.get(StatsType.MAX_MP));
				return;
			}
		}
	}
	
	@Override
	public void onKill(CharacterHolder killer, CharacterHolder victim, boolean isPet)
	{
		if (victim instanceof NpcHolder)
		{
			var npc = (NpcHolder) victim;
			
			if (npc.getChampionType() == ChampionType.NONE)
			{
				return;
			}
			
			// Give Rewards
			CHAMPIONS_INFO_STATS.get(npc.getChampionType()).rewards.stream().filter(rewards -> Rnd.get(100) <= rewards.getRewardChance()).forEach(rewards ->
			{
				// Check if enable autolot or drop reward
				if ((victim.getInstance().isRaid() && Config.AUTO_LOOT_RAIDS) || (!victim.getInstance().isRaid() && Config.AUTO_LOOT))
				{
					killer.getInstance().getActingPlayer().doAutoLoot((L2Attackable) victim.getInstance(), new ItemHolder(rewards.getRewardId(), rewards.getRewardCount()));
				}
				else
				{
					((L2Attackable) victim.getInstance()).dropItem((L2PcInstance) killer.getInstance(), new ItemHolder(rewards.getRewardId(), rewards.getRewardCount()));
				}
			});
			
			npc.setChampionType(ChampionType.NONE);
			npc.setTeam(TeamType.NONE);
			npc.getInstance().setTitle(npc.getInstance().getTemplate().getTitle());
		}
	}
	
	@Override
	public double onStats(StatsType stat, CharacterHolder character, double value)
	{
		// Give stats por champions
		if (character instanceof NpcHolder)
		{
			var npc = (NpcHolder) character;
			
			if (npc.getChampionType() != ChampionType.NONE)
			{
				var map = CHAMPIONS_INFO_STATS.get(npc.getChampionType()).allStats;
				
				if (map.containsKey(stat))
				{
					return value * map.get(stat);
				}
			}
		}
		
		return value;
	}
	
	@Override
	public void onNpcExpSp(PlayerHolder killer, NpcHolder npc, NpcExpInstance instance)
	{
		// ExpSpBonusHolder (bonusType, amountBonus)
		// Example: 1.1 -> 110%
		// if you use 100% exp will be normal, to earn bonus use values greater than 100%.
		
		if (npc.isChampion())
		{
			// increase normal exp/sp amount
			instance.increaseRate(ExpSpType.EXP, ConfigData.CHAMPION_BONUS_RATE_EXP);
			instance.increaseRate(ExpSpType.SP, ConfigData.CHAMPION_BONUS_RATE_SP);
		}
	}
	
	@Override
	public void onNpcDrop(PlayerHolder killer, NpcHolder npc, NpcDropsInstance instance)
	{
		// DropBonusHolder (dropType, amountBonus, chanceBonus)
		// Example: 110 -> 110%
		// if you use 100% drop will be normal, to earn bonus use values greater than 100%.
		
		if (npc.isChampion())
		{
			// increase normal drop amount and chance
			instance.increaseDrop(ItemDropType.NORMAL, ConfigData.CHAMPION_BONUS_DROP, ConfigData.CHAMPION_BONUS_DROP);
			// increase spoil drop amount and chance
			instance.increaseDrop(ItemDropType.SPOIL, ConfigData.CHAMPION_BONUS_SPOIL, ConfigData.CHAMPION_BONUS_SPOIL);
			// increase seed drop amount and chance
			instance.increaseDrop(ItemDropType.SEED, ConfigData.CHAMPION_BONUS_SEED, ConfigData.CHAMPION_BONUS_SEED);
		}
	}
	
	/**
	 * The type of npc enabled to be "Champion" is checked.
	 * <li>L2RaidBossInstance -> NO!
	 * <li>L2GrandBossInstance -> NO!
	 * <li>L2MonsterInstance -> SI!
	 * @param  obj
	 * @return
	 */
	private static boolean checkNpcType(NpcHolder obj)
	{
		if (Util.areObjectType(L2RaidBossInstance.class, obj))
		{
			return false;
		}
		
		if (Util.areObjectType(L2GrandBossInstance.class, obj))
		{
			return false;
		}
		
		if (Util.areObjectType(L2MonsterInstance.class, obj))
		{
			return true;
		}
		
		return false;
	}
}
