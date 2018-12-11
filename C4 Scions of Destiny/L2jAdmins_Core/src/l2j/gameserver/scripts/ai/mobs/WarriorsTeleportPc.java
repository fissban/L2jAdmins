package l2j.gameserver.scripts.ai.mobs;

import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.SkillHolder;
import l2j.gameserver.scripts.Script;
import l2j.util.Rnd;

/**
 * @author fissban
 */
public class WarriorsTeleportPc extends Script
{
	// Mobs
	private static final int PORTA = 213;
	private static final int PERUM = 221;
	// Skill
	private static final SkillHolder TELEPORT_PC = new SkillHolder(4161, 1);
	
	public WarriorsTeleportPc()
	{
		super(-1, "ai/npc");
		
		addAttackId(PORTA, PERUM);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if ((npc.getScriptValue() == 0) && !npc.isInsideRadius(attacker, 300, true, false))
		{
			if (Rnd.nextBoolean())
			{
				npc.setScriptValue(1);
				npc.doCast(TELEPORT_PC.getSkill());
			}
		}
		
		return null;
	}
	
}
