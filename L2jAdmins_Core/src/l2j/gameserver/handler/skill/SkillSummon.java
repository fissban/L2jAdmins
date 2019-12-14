package l2j.gameserver.handler.skill;

import java.util.List;

import l2j.gameserver.data.ExperienceData;
import l2j.gameserver.data.NpcData;
import l2j.gameserver.handler.SkillHandler.ISkillHandler;
import l2j.gameserver.idfactory.IdFactory;
import l2j.gameserver.instancemanager.siege.SiegeManager;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.instance.L2SiegeSummonInstance;
import l2j.gameserver.model.actor.instance.L2SummonInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.actor.manager.character.skills.enums.SkillType;
import l2j.gameserver.network.external.server.SystemMessage;

/**
 * @author fissban
 */
public class SkillSummon implements ISkillHandler
{
	private static final int SKILL_SIEGE_GOLEN = 13;
	private static final int SKILL_SUMMON_WILD_HOG_CANON = 299;
	
	@Override
	public SkillType[] getSkillIds()
	{
		return new SkillType[]
		{
			SkillType.SUMMON
		};
	}
	
	@Override
	public void useSkill(L2Character activeChar, Skill skill, List<L2Object> targets)
	{
		if (activeChar.isAlikeDead() || !(activeChar instanceof L2PcInstance))
		{
			return;
		}
		
		if (!checkCondition((L2PcInstance) activeChar, skill))
		{
			return;
		}
		
		var player = (L2PcInstance) activeChar;
		
		var summonTemplate = NpcData.getInstance().getTemplate(skill.getSummonNpcId());
		
		L2SummonInstance summon;
		if (summonTemplate.isType("L2SiegeSummon"))
		{
			summon = new L2SiegeSummonInstance(IdFactory.getInstance().getNextId(), summonTemplate, player, skill);
		}
		else
		{
			summon = new L2SummonInstance(IdFactory.getInstance().getNextId(), summonTemplate, player, skill);
		}
		
		summon.setName(summonTemplate.getName());
		summon.setTitle(player.getName());
		summon.setExpPenalty(skill.getExpPenalty());
		if (summon.getLevel() >= ExperienceData.getInstance().getMaxLevel())
		{
			summon.getStat().setExp(ExperienceData.getInstance().getMaxLevel() - 1);
		}
		else
		{
			summon.getStat().setExp(ExperienceData.getInstance().getExpForLevel((summon.getLevel() % ExperienceData.getInstance().getMaxLevel())));
		}
		
		summon.setCurrentHp(summon.getStat().getMaxHp());
		summon.setCurrentMp(summon.getStat().getMaxMp());
		summon.setHeading(player.getHeading());
		summon.setRunning();
		player.setPet(summon);
		
		summon.spawnMe(player.getX() + 20, player.getY() + 20, player.getZ());
	}
	
	@Override
	public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill)
	{
		if ((activeChar.getPet() != null) || activeChar.isMounted())
		{
			activeChar.sendPacket(SystemMessage.SUMMON_ONLY_ONE);
			return false;
		}
		
		// If summon siege golem (13), Summon Wild Hog Cannon (299), check if its ok to summon
		if (((skill.getId() == SKILL_SIEGE_GOLEN) || (skill.getId() == SKILL_SUMMON_WILD_HOG_CANON)) && !SiegeManager.getInstance().checkIfOkToSummon(activeChar, false))
		{
			return false;
		}
		
		return true;
	}
	
	private static boolean checkCondition(L2PcInstance player, Skill skill)
	{
		// TODO Here we let this condition to prevent double invocation of summons because of a bugg we have about the casting of skills.
		if ((player.getPet() != null) || player.isMounted())
		{
			player.sendPacket(SystemMessage.SUMMON_ONLY_ONE);
			return false;
		}
		
		return true;
	}
}
