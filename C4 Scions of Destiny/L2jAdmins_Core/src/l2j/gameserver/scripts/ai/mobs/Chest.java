package l2j.gameserver.scripts.ai.mobs;

import java.util.List;

import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.ai.enums.CtrlIntentionType;
import l2j.gameserver.model.actor.instance.L2ChestInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.scripts.Script;
import l2j.util.Rnd;

/**
 * Original code in python by Fulminus
 * @author fissban
 */
public class Chest extends Script
{
	// Chest
	private static final int[] CHEST =
	{
		13100,
		13101,
		13102,
		13103,
		13104,
		13105,
		13106,
		13107,
		13108,
		13109,
		13110,
		13111,
		13112,
		13113,
		13114,
		13115,
		13116,
		13117,
		13118,
		13119,
		13120,
		13121,
		1801,
		1802,
		1803,
		1804,
		1805,
		1806,
		1807,
		1808,
		1809,
		1810,
		1671,
		1694,
		1717,
		1740,
		1763,
		1786,
		13213,
		13215,
		13217,
		13219,
		13221,
		13223,
		1811,
		1812,
		1813,
		1814,
		1815,
		1816,
		1817,
		1818,
		1819,
		1820,
		1821,
		1822,
	};
	// Skill
	private static final int SKILL_DELUXE_KEY = 2229;
	// Base chance for BOX to be opened
	private static final int BASE_CHANCE = 100;
	// Percent to decrease base chance when grade of DELUXE key not match
	private static final int LEVEL_DECREASE = 40;
	// Chance for a chest to actually be a BOX (as opposed to being a mimic).
	private static final int IS_BOX = 40;
	
	public Chest()
	{
		super(-1, "ai/mobs");
		
		addSkillSeeId(CHEST);
		addAttackId(CHEST);
	}
	
	@Override
	public String onSkillSee(L2Npc npc, L2PcInstance caster, Skill skill, List<L2Object> targets, boolean isSummon)
	{
		L2ChestInstance chest = ((L2ChestInstance) npc);
		
		if (!chest.isInteracted())
		{
			chest.setInteracted();
			
			if (Rnd.get(100) < IS_BOX)
			{
				if (skill.getId() == SKILL_DELUXE_KEY)
				{
					int levelDiff = (npc.getLevel() / 10) - skill.getLevel();
					
					if (levelDiff < 0)
					{
						levelDiff = levelDiff * -1;
					}
					
					int chance = BASE_CHANCE - (levelDiff * LEVEL_DECREASE);
					
					// success, pretend-death with rewards: npc.reduceCurrentHp(99999999, player)
					if (Rnd.get(100) < chance)
					{
						chest.setMustRewardExpSp(false);
						chest.setSpecialDrop();
						chest.reduceCurrentHp(99999999, caster);
						return null;
					}
				}
				// used a skill other than chest-key, or used a chest-key but failed to open: disappear with no rewards
				chest.deleteMe();
			}
			else
			{
				L2Character originalCaster = isSummon ? caster.getPet() : caster;
				chest.setRunning();
				chest.addDamageHate(originalCaster, 0, 999);
				chest.getAI().setIntention(CtrlIntentionType.ATTACK, originalCaster);
			}
		}
		return super.onSkillSee(npc, caster, skill, targets, isSummon);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		L2ChestInstance chest = ((L2ChestInstance) npc);
		// if this was a mimic, set the target, start the skills and become agro
		if (!chest.isInteracted())
		{
			chest.setInteracted();
			if (getRandom(100) < IS_BOX)
			{
				chest.deleteMe();
			}
			else
			{
				// if this weren't a box, upon interaction start the mimic behaviors...
				// TODO: perhaps a self-buff (skill id 4245) with random chance goes here?
				L2Character originalAttacker = isSummon ? attacker.getPet() : attacker;
				chest.setRunning();
				chest.addDamageHate(originalAttacker, 0, (damage * 100) / (chest.getLevel() + 7));
				chest.getAI().setIntention(CtrlIntentionType.ATTACK, originalAttacker);
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
}
