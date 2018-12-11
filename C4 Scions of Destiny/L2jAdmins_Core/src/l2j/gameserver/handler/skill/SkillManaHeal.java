package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.enums.ShotType;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.enums.SkillType;
import l2j.gameserver.model.skills.stats.enums.StatsType;
import l2j.gameserver.network.external.server.SystemMessage;

public class SkillManaHeal implements ISkillHandler
{
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.MANAHEAL,
			SkillType.MANARECHARGE
		};
	}
	
	@Override
	public void useSkill(L2Character activeChar, Skill skill, List<L2Object> targets)
	{
		var bss = activeChar.isChargedShot(ShotType.BLESSED_SPIRITSHOTS);
		
		for (L2Object object : targets)
		{
			var target = (L2Character) object;
			
			if (target == null)
			{
				return;
			}
			
			var mp = (skill.getSkillType() == SkillType.MANARECHARGE) ? target.calcStat(StatsType.RECHARGE_MP_RATE, skill.getPower(), null, null) : skill.getPower();
			target.setLastHealAmount((int) mp);
			target.setCurrentMp(mp + target.getCurrentMp());
			
			if (target instanceof L2PcInstance)
			{
				// Update current mp status on player
				((L2PcInstance) target).updateCurMp();
				
				if ((activeChar instanceof L2PcInstance) && (activeChar != target))
				{
					target.sendPacket(new SystemMessage(SystemMessage.S2_MP_RESTORED_BY_C1).addString(activeChar.getName()).addNumber((int) mp));
				}
				else
				{
					target.sendPacket(new SystemMessage(SystemMessage.S1_MP_RESTORED).addNumber((int) mp));
				}
			}
			
		}
		
		activeChar.setChargedShot(bss ? ShotType.BLESSED_SPIRITSHOTS : ShotType.SPIRITSHOTS, false);
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		return true;
	}
}
