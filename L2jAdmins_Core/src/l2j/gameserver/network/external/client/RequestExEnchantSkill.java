package l2j.gameserver.network.external.client;

import l2j.Config;
import l2j.gameserver.data.ExperienceData;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.data.SkillTreeData;
import l2j.gameserver.illegalaction.IllegalAction;
import l2j.gameserver.illegalaction.enums.IllegalActionType;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.EnchantSkillLearnHolder;
import l2j.gameserver.model.shortcuts.PcShortCutsInstance;
import l2j.gameserver.model.shortcuts.PcShortCutsType;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.network.external.server.UserInfo;
import l2j.gameserver.scripts.ScriptState;
import l2j.util.Rnd;

/**
 * Format chdd c: (id) 0xD0 h: (subid) 0x06 d: skill id d: skill lvl
 * @author -Wooden-
 */
public class RequestExEnchantSkill extends AClientPacket
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
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		L2Npc trainer = player.getLastTalkNpc();
		if (trainer == null)
		{
			return;
		}
		
		if ((!player.isInsideRadius(trainer, L2Npc.INTERACTION_DISTANCE, false, false)) && !player.isGM())
		{
			return;
		}
		
		if (player.getSkillLevel(skillId) >= skillLvl)
		{
			return;
		}
		
		if (player.getClassId().getId() < 88)
		{
			return;
		}
		
		if (player.getLevel() < 76)
		{
			return;
		}
		
		Skill skill = SkillData.getInstance().getSkill(skillId, skillLvl);
		
		int counts = 0;
		int requiredSp = 10000000;
		int requiredExp = 100000;
		byte rate = 0;
		int baseLvl = 1;
		
		int npcId = trainer.getId();
		
		for (EnchantSkillLearnHolder s : SkillTreeData.getAvailableEnchantSkills(player))
		{
			Skill sk = SkillData.getInstance().getSkill(s.getId(), s.getLevel());
			if ((sk == null) || (sk != skill) || !sk.getCanLearn(player.getClassId()) || !sk.canTeachBy(npcId))
			{
				continue;
			}
			
			counts++;
			requiredSp = s.getSpCost();
			requiredExp = s.getExp();
			rate = s.getRate(player);
			baseLvl = s.getBaseLevel();
		}
		
		if (counts == 0)
		{
			player.sendMessage("You are trying to learn a skill that you can't.");
			IllegalAction.report(player, "Player " + player.getName() + " tried to learn a skill that he can't!!!", IllegalActionType.PUNISH_KICK);
			return;
		}
		
		// Check SP
		if (player.getSp() < requiredSp)
		{
			player.sendPacket(SystemMessage.YOU_DONT_HAVE_ENOUGH_SP_TO_ENCHANT_THAT_SKILL);
			return;
		}
		
		// Check Exp
		long expAfter = player.getExp() - requiredExp;
		
		if ((player.getExp() < requiredExp) || (expAfter < ExperienceData.getInstance().getExpForLevel(player.getLevel())))
		{
			player.sendPacket(SystemMessage.YOU_DONT_HAVE_ENOUGH_EXP_TO_ENCHANT_THAT_SKILL);
			return;
		}
		
		// Check Spellbook
		if (Config.ES_SP_BOOK_NEEDED && ((skillLvl == 101) || (skillLvl == 141))) // only first lvl requires book
		{
			if (!player.getInventory().destroyItemByItemId("Enchant skill", 6622, 1, trainer, true))
			{
				player.sendPacket(SystemMessage.ITEM_OR_PREREQUISITES_MISSING_TO_LEARN_SKILL);
				return;
			}
		}
		
		if (Rnd.get(100) <= rate)
		{
			// remove exp
			player.getStat().removeExpAndSp(requiredExp, requiredSp, false);
			// add new skill
			player.addSkill(skill, true);
			// send message
			player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_SUCCEEDED_IN_ENCHANTING_THE_SKILL_S1).addSkillName(skillId));
		}
		else
		{
			if (skill.getLevel() > 100)
			{
				skillLvl = baseLvl;
				player.addSkill(SkillData.getInstance().getSkill(skillId, skillLvl), true, true);
				player.sendSkillList();
			}
			
			player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_FAILED_TO_ENCHANT_THE_SKILL_S1).addSkillName(skillId));
		}
		
		// update all the shortcuts to this skill
		updateShortCuts(player, skillId, skillLvl);
		// update user info
		player.sendPacket(new UserInfo(player));
		
		// send player skill list
		if (trainer.isTrainer())
		{
			ScriptState qs = player.getScriptState("Trainer");
			if (qs != null)
			{
				qs.getQuest().notifyEvent("EnchantSkillList", trainer, player);
			}
		}
	}
	
	/**
	 * Update all the shortcuts to this skill
	 * @param player
	 * @param id
	 * @param lvl
	 */
	private static void updateShortCuts(L2PcInstance player, int id, int lvl)
	{
		for (PcShortCutsInstance sc : player.getShortCuts().getAllShortCuts())
		{
			if (sc == null)
			{
				continue;
			}
			
			if ((sc.getId() == id) && (sc.getType() == PcShortCutsType.SKILL))
			{
				player.getShortCuts().registerShortCut(new PcShortCutsInstance(sc.getSlot(), sc.getPage(), sc.getType(), sc.getId(), lvl, 1), true);
			}
		}
	}
}
