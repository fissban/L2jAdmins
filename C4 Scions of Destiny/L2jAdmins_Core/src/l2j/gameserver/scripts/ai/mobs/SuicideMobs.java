package l2j.gameserver.scripts.ai.mobs;

import java.util.HashSet;
import java.util.Set;

import l2j.gameserver.ThreadPoolManager;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.scripts.Script;
import l2j.util.Rnd;

/**
 * @author fissban
 */
public class SuicideMobs extends Script
{
	private static final int SKILL_DEATH_BOMB = 4614;
	private static final int CHANCE_ON_SUICIDE = 30;
	private static final int HP_TO_SUICIDE = 20;
	// Mobs
	private static final Set<SuicideAuxList> SUICIDES = new HashSet<>();
	{
		// scarlet_stakato_worker
		SUICIDES.add(new SuicideAuxList(1376, 0, 0, 8));
		// scarlet_stakato_soldier
		SUICIDES.add(new SuicideAuxList(1377, 0, 0, 8));
		// scarlet_stakato_noble
		SUICIDES.add(new SuicideAuxList(1378, 0, 0, 8));
		// tephra_scorpion
		SUICIDES.add(new SuicideAuxList(1379, 0, 0, 8));
		// tephra_scarab
		SUICIDES.add(new SuicideAuxList(1380, 0, 0, 9));
		// destroyer_mercenary
		SUICIDES.add(new SuicideAuxList(1381, 0, 0, 9));
		// destroyer_knight
		SUICIDES.add(new SuicideAuxList(1382, 0, 0, 9));
		// destroyer_savant
		SUICIDES.add(new SuicideAuxList(1383, 0, 0, 9));
		// lavastone_golem
		SUICIDES.add(new SuicideAuxList(1384, 0, 0, 9));
		// magma_golem
		SUICIDES.add(new SuicideAuxList(1385, 0, 0, 9));
		// destroyer_iblis
		SUICIDES.add(new SuicideAuxList(1387, 0, 0, 9));
		// destroyer_balor
		SUICIDES.add(new SuicideAuxList(1388, 0, 0, 9));
		
		// lavasilisk
		SUICIDES.add(new SuicideAuxList(1390, 0, 0, 9));
		// blazing_ifrit
		SUICIDES.add(new SuicideAuxList(1391, 0, 0, 9));
		
		// destroyer_ahrimanes -> destroyer_ahrimanes_bs
		SUICIDES.add(new SuicideAuxList(1386, 1655, 5, 9));
		// destroyer_ashuras -> destroyer_ashuras_bs
		SUICIDES.add(new SuicideAuxList(1389, 1656, 5, 9));
		
		// scarlet_stakato_noble -> scarlet_stakato_noble_bs
		SUICIDES.add(new SuicideAuxList(1378, 1652, 5, 10));
		// assassin_beetle -> assassin_beetle_bs
		SUICIDES.add(new SuicideAuxList(1381, 1653, 5, 10));
		// destroyer_savant -> destroyer_savant_bs
		SUICIDES.add(new SuicideAuxList(1384, 1654, 5, 10));
		// destroyer_ahrimanes -> destroyer_ahrimanes_bs
		SUICIDES.add(new SuicideAuxList(1387, 1655, 5, 10));
		// destroyer_ashuras -> destroyer_ashuras_bs
		SUICIDES.add(new SuicideAuxList(1390, 1656, 5, 10));
		// magma_drake -> magma_drake_bs
		SUICIDES.add(new SuicideAuxList(1393, 1657, 5, 10));
	}
	
	private static final Set<Integer> mobs = new HashSet<>();
	
	public SuicideMobs()
	{
		super(-1, "ai/mobs");
		
		for (SuicideAuxList list : SUICIDES)
		{
			addAttackId(list.getNoble());
			addKillId(list.getNoble());
		}
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if (mobs.contains(npc.getObjectId()))
		{
			mobs.remove(npc.getObjectId());
		}
		
		return null;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		if (mobs.contains(npc.getObjectId()))
		{
			return null;
		}
		
		if (npc.isVisible() && !npc.isDead())
		{
			for (SuicideAuxList list : SUICIDES)
			{
				if (npc.getId() == list.getNoble())
				{
					if ((npc.getCurrentHp() <= ((npc.getStat().getMaxHp() * HP_TO_SUICIDE) / 100.0)))
					{
						if ((Rnd.get(100) < CHANCE_ON_SUICIDE))
						{
							// Stop any action
							npc.getAI().setIntention(CtrlIntentionType.IDLE);
							// Cast suicide skill
							npc.doCast(SkillData.getInstance().getSkill(SKILL_DEATH_BOMB, list.getSkillLevel()));
							
							if (list.getMinion() > 0)
							{
								L2Character originalAttacker = isSummon ? attacker.getPet() : attacker;
								
								// Noble
								newSpawn(list.getNoble(), 1, npc, originalAttacker);
								// Minions
								for (int i = 0; i < list.getCount(); i++)
								{
									newSpawn(list.getMinion(), list.getCount(), npc, originalAttacker);
								}
							}
							
							// Generate task for remove mob
							ThreadPoolManager.schedule(() -> npc.deleteMe(), 100);
						}
						else
						{
							mobs.add(npc.getObjectId());
						}
					}
					
					return null;
				}
			}
		}
		return null;
	}
	
	private final void newSpawn(int npcId, int count, L2Npc npc, L2Character attacker)
	{
		int x = npc.getX();
		int y = npc.getY();
		int z = npc.getZ();
		if (count > 1)
		{
			x += Rnd.get(-50, 50);
			y += Rnd.get(-50, 50);
		}
		L2Attackable newNpc = (L2Attackable) addSpawn(npcId, x, y, z + 10, npc.getHeading(), false, 0);
		
		newNpc.setRunning();
		newNpc.addDamageHate(attacker, 0, 999);
		newNpc.getAI().setIntention(CtrlIntentionType.ATTACK, attacker);
	}
	
	public class SuicideAuxList
	{
		private final int noble;
		private final int minion;
		private final int count;
		private final int skillLevel;
		
		/**
		 * @param nobleId
		 * @param minions
		 * @param count
		 * @param currentHp
		 * @param rate
		 * @param skillLevel
		 */
		SuicideAuxList(int nobleId, int minions, int count, int skillLevel)
		{
			noble = nobleId;
			minion = minions;
			this.count = count;
			this.skillLevel = skillLevel;
		}
		
		public int getNoble()
		{
			return noble;
		}
		
		public int getMinion()
		{
			return minion;
		}
		
		public int getCount()
		{
			return count;
		}
		
		public int getSkillLevel()
		{
			return skillLevel;
		}
	}
}
