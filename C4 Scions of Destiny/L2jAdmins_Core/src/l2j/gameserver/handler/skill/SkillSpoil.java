package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.ai.enums.CtrlEventType;
import l2j.gameserver.model.actor.instance.L2MonsterInstance;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillType;
import l2j.gameserver.model.actor.manager.character.skills.stats.Formulas;
import l2j.gameserver.network.external.server.PlaySound.PlaySoundType;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author drunk_
 */
public class SkillSpoil implements ISkillHandler
{
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.SPOIL
		};
	}
	
	@Override
	public void useSkill(L2Character activeChar, Skill skill, List<L2Object> targets)
	{
		if (!(activeChar instanceof L2PcInstance))
		{
			return;
		}
		
		if (targets == null)
		{
			return;
		}
		
		for (var object : targets)
		{
			if (!(object instanceof L2MonsterInstance))
			{
				continue;
			}
			
			var monster = (L2MonsterInstance) object;
			
			if (monster.isSpoil())
			{
				activeChar.sendPacket(SystemMessage.ALREADY_SPOILED);
				continue;
			}
			
			if (!monster.isDead())
			{
				if (Formulas.calcMagicSuccess(activeChar, (L2Character) object, skill))
				{
					monster.setSpoil(true);
					monster.setIsSpoiledBy(activeChar.getObjectId());
					activeChar.sendPacket(SystemMessage.SPOIL_SUCCESS);
					activeChar.playSound(PlaySoundType.SYS_SPOIL_SUCCES);
				}
				else
				{
					activeChar.sendPacket(new SystemMessage(SystemMessage.C1_RESISTED_YOUR_S2).addString(monster.getName()).addSkillName(skill.getId()));
				}
				monster.getAI().notifyEvent(CtrlEventType.ATTACKED, activeChar);
			}
		}
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		// Check if the skill is Spoil type and if the target isn't already spoiled
		if (!(target instanceof L2MonsterInstance))
		{
			// Send a System Message to the L2PcInstance
			activeChar.sendPacket(SystemMessage.TARGET_IS_INCORRECT);
			return false;
		}
		return true;
	}
}
