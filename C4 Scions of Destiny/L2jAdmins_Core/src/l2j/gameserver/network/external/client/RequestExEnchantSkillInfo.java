package l2j.gameserver.network.external.client;

import java.util.List;

import l2j.Config;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.data.SkillTreeData;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.actor.manager.character.skills.Skill;
import l2j.gameserver.model.holder.EnchantSkillLearnHolder;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ExEnchantSkillInfo;

/**
 * Format chdd c: (id) 0xD0 h: (subid) 0x06 d: skill id d: skill lvl
 * @author -Wooden-
 */
public class RequestExEnchantSkillInfo extends AClientPacket
{
	private int skillId;
	private int skillLvl;
	
	@Override
	protected void readImpl()
	{
		skillId = readD();
		skillLvl = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		
		if (activeChar.getLevel() < 76)
		{
			return;
		}
		
		L2Npc trainer = activeChar.getLastTalkNpc();
		
		if ((!activeChar.isInsideRadius(trainer, L2Npc.INTERACTION_DISTANCE, false, false)) && !activeChar.isGM())
		{
			return;
		}
		
		if (trainer == null)
		{
			return;
		}
		
		Skill skill = SkillData.getInstance().getSkill(skillId, skillLvl);
		
		boolean canteach = false;
		
		if ((skill == null) || (skill.getId() != skillId))
		{
			return;
		}
		
		if (!trainer.getTemplate().canTeach(activeChar.getClassId()))
		{
			return; // cheater
		}
		
		List<EnchantSkillLearnHolder> skills = SkillTreeData.getAvailableEnchantSkills(activeChar);
		
		for (EnchantSkillLearnHolder s : skills)
		{
			if ((s.getId() == skillId) && (s.getLevel() == skillLvl))
			{
				canteach = true;
				break;
			}
		}
		
		if (!canteach)
		{
			return; // cheater
		}
		
		int requiredSp = SkillTreeData.getSkillSpCost(activeChar, skill);
		int requiredExp = SkillTreeData.getSkillExpCost(activeChar, skill);
		byte rate = SkillTreeData.getEnchantSkillRate(activeChar, skill);
		ExEnchantSkillInfo asi = new ExEnchantSkillInfo(skill.getId(), skill.getLevel(), requiredSp, requiredExp, rate);
		
		if (Config.ES_SP_BOOK_NEEDED && ((skill.getLevel() == 101) || (skill.getLevel() == 141)))
		{
			asi.addRequirement(4, 6622, 1, 0);
		}
		
		sendPacket(asi);
	}
}
