package l2j.gameserver.scripts.ai.mobs;

import l2j.gameserver.model.actor.L2Attackable;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.holder.SkillHolder;
import l2j.gameserver.scripts.Script;

/**
 * Fairy Trees AI.
 * @author Charus
 */
public class FairyTrees extends Script
{
	// Mobs
	private static final int[] MOBS =
	{
		5185,
		5186,
		5187,
		5188,
		5189,
	};
	// Gradually Reduce HP of player
	private static final SkillHolder VENOM_POISON = new SkillHolder(4243, 1);
	
	public FairyTrees()
	{
		super(-1, "ai/mobs");
		addKillId(MOBS);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		for (int i = 0; i < 20; i++)
		{
			L2Attackable newNpc = (L2Attackable) addSpawn(5189, npc.getX() + getRandom(50), npc.getY() + getRandom(50), npc.getZ(), 0, false, 30000);
			L2Character originalKiller = isSummon ? killer.getPet() : killer;
			newNpc.setRunning();
			newNpc.addDamageHate(originalKiller, 0, 999);
			newNpc.getAI().setIntention(CtrlIntentionType.ATTACK, originalKiller);
			
			if (getRandomBoolean())
			{
				Skill skill = VENOM_POISON.getSkill();
				
				if ((skill != null) && (originalKiller != null))
				{
					skill.getEffects(newNpc, originalKiller);
				}
			}
		}
		
		return super.onKill(npc, killer, isSummon);
	}
}
