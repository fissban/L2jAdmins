package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.data.SkillSpellbookData;
import l2j.gameserver.data.SkillTreeData;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.SkillLearnHolder;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.AquireSkillInfo;

/**
 * This class ...
 * @version $Revision: 1.5.2.1.2.5 $ $Date: 2005/04/06 16:13:48 $
 */
public class RequestAquireSkillInfo extends AClientPacket
{
	private int id;
	private int level;
	private int fisherman;
	
	@Override
	protected void readImpl()
	{
		id = readD();
		level = readD();
		fisherman = readD();// normal(0) learn or fisherman(1)
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
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
		
		Skill skill = SkillData.getInstance().getSkill(id, level);
		
		boolean canteach = false;
		
		if (skill == null)
		{
			LOG.warning("skill id " + id + " level " + level + " is undefined. aquireSkillInfo failed.");
			return;
		}
		
		if (fisherman == 0)
		{
			if (!trainer.getTemplate().canTeach(activeChar.getSkillLearningClassId()))
			{
				return; // cheater
			}
			
			for (SkillLearnHolder s : SkillTreeData.getAvailableSkillsTrainer(activeChar, activeChar.getSkillLearningClassId()))
			{
				if ((s.getId() == id) && (s.getLevel() == level))
				{
					canteach = true;
					break;
				}
			}
			
			if (!canteach)
			{
				return; // cheater
			}
			
			int requiredSp = SkillTreeData.getSkillCost(activeChar, skill);
			AquireSkillInfo asi = new AquireSkillInfo(skill.getId(), skill.getLevel(), requiredSp, 0);
			
			if (Config.SP_BOOK_NEEDED)
			{
				int spbId = SkillSpellbookData.getInstance().getBookForSkill(skill);
				
				if ((skill.getLevel() == 1) && (spbId > -1))
				{
					asi.addRequirement(99, spbId, 1, 50);
				}
			}
			
			sendPacket(asi);
		}
		else
		// Common Skills
		{
			int costid = 0;
			int costcount = 0;
			int spcost = 0;
			
			for (SkillLearnHolder s : SkillTreeData.getAvailableSkillsFishing(activeChar))
			{
				Skill sk = SkillData.getInstance().getSkill(s.getId(), s.getLevel());
				
				if ((sk == null) || (sk != skill))
				{
					continue;
				}
				
				canteach = true;
				costid = s.getIdCost();
				costcount = s.getCostCount();
				spcost = s.getSpCost();
			}
			
			AquireSkillInfo asi = new AquireSkillInfo(skill.getId(), skill.getLevel(), spcost, 1);
			asi.addRequirement(4, costid, costcount, 0);
			sendPacket(asi);
		}
	}
}
