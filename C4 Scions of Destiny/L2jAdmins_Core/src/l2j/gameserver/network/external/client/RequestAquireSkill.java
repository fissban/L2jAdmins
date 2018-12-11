package l2j.gameserver.network.external.client;

import java.util.List;

import l2j.Config;
import l2j.gameserver.data.SkillData;
import l2j.gameserver.data.SkillSpellbookData;
import l2j.gameserver.data.SkillTreeData;
import l2j.gameserver.illegalaction.IllegalAction;
import l2j.gameserver.illegalaction.enums.IllegalActionType;
import l2j.gameserver.model.actor.L2Npc;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.holder.SkillLearnHolder;
import l2j.gameserver.model.items.instance.ItemInstance;
import l2j.gameserver.model.shortcuts.PcShortCutsInstance;
import l2j.gameserver.model.shortcuts.PcShortCutsType;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.network.AClientPacket;
import l2j.gameserver.network.external.server.ExStorageMaxCount;
import l2j.gameserver.network.external.server.StatusUpdate;
import l2j.gameserver.network.external.server.StatusUpdate.StatusUpdateType;
import l2j.gameserver.network.external.server.SystemMessage;
import l2j.gameserver.scripts.ScriptState;

/**
 * This class ...
 * @version $Revision: 1.7.2.1.2.4 $ $Date: 2005/03/27 15:29:30 $
 */
public class RequestAquireSkill extends AClientPacket
{
	private int id;
	private int level;
	private int fisherman;
	
	@Override
	protected void readImpl()
	{
		id = readD();
		level = readD();
		fisherman = readD();
	}
	
	@Override
	public void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}
		
		L2Npc npc = player.getLastTalkNpc();
		if (npc == null)
		{
			return;
		}
		
		int npcid = npc.getId();
		
		if ((!player.isInsideRadius(npc, L2Npc.INTERACTION_DISTANCE, false, false)) && !player.isGM())
		{
			return;
		}
		
		player.setSkillLearningClassId(player.getClassId());
		
		if (player.getSkillLevel(id) >= level)
		{
			// already knows the skill with this level
			return;
		}
		
		Skill skill = SkillData.getInstance().getSkill(id, level);
		
		int counts = 0;
		int requiredSp = 10000000;
		
		if (fisherman == 0)
		{
			// Skill Learn bug Fix
			List<SkillLearnHolder> skills = SkillTreeData.getAvailableSkillsTrainer(player, player.getSkillLearningClassId());
			
			for (SkillLearnHolder s : skills)
			{
				Skill sk = SkillData.getInstance().getSkill(s.getId(), s.getLevel());
				if ((sk == null) || (sk != skill) || !sk.getCanLearn(player.getSkillLearningClassId()) || !sk.canTeachBy(npcid))
				{
					continue;
				}
				counts++;
				requiredSp = SkillTreeData.getSkillCost(player, skill);
			}
			
			if (counts == 0)
			{
				player.sendMessage("You are trying to learn skill that u can't..");
				IllegalAction.report(player, "Player " + player.getName() + " tried to learn skill that he can't!!!", IllegalActionType.PUNISH_KICK);
				return;
			}
			
			if (player.getSp() >= requiredSp)
			{
				if (Config.SP_BOOK_NEEDED)
				{
					int spbId = SkillSpellbookData.getInstance().getBookForSkill(skill);
					
					if ((skill.getLevel() == 1) && (spbId > -1))
					{
						ItemInstance spb = player.getInventory().getItemById(spbId);
						
						if (spb == null)
						{
							// Haven't spellbook
							player.sendPacket(new SystemMessage(SystemMessage.ITEM_OR_PREREQUISITES_MISSING_TO_LEARN_SKILL));
							return;
						}
						
						player.getInventory().destroyItem("Consume", spb, npc, true);
					}
				}
			}
			else
			{
				player.sendPacket(new SystemMessage(SystemMessage.NOT_ENOUGH_SP_TO_LEARN_SKILL));
				return;
			}
		}
		else if (fisherman == 1)
		{
			int costid = 0;
			int costcount = 0;
			// Skill Learn bug Fix
			
			for (SkillLearnHolder s : SkillTreeData.getAvailableSkillsFishing(player))
			{
				Skill sk = SkillData.getInstance().getSkill(s.getId(), s.getLevel());
				
				if ((sk == null) || (sk != skill))
				{
					continue;
				}
				
				counts++;
				costid = s.getIdCost();
				costcount = s.getCostCount();
				requiredSp = s.getSpCost();
			}
			
			if (counts == 0)
			{
				player.sendMessage("You are trying to learn skill that u can't..");
				IllegalAction.report(player, "Player " + player.getName() + " tried to learn skill that he can't!!!", IllegalActionType.PUNISH_KICK);
				return;
			}
			
			if (player.getSp() >= requiredSp)
			{
				if (!player.getInventory().destroyItemByItemId("Consume", costid, costcount, npc, false))
				{
					// Haven't spellbook
					player.sendPacket(SystemMessage.ITEM_OR_PREREQUISITES_MISSING_TO_LEARN_SKILL);
					return;
				}
				
				SystemMessage sm = new SystemMessage(SystemMessage.S2_S1_DISAPPEARED);
				sm.addNumber(costcount);
				sm.addItemName(costid);
				sendPacket(sm);
				sm = null;
			}
			else
			{
				player.sendPacket(new SystemMessage(SystemMessage.NOT_ENOUGH_SP_TO_LEARN_SKILL));
				return;
			}
		}
		else
		{
			LOG.warning("Recived Wrong Packet Data in Aquired Skill - unk1:" + fisherman);
			return;
		}
		
		player.addSkill(skill, true);
		
		if (Config.DEBUG)
		{
			LOG.fine("Learned skill " + id + " for " + requiredSp + " SP.");
		}
		
		player.setSp(player.getSp() - requiredSp);
		
		StatusUpdate su = new StatusUpdate(player.getObjectId());
		su.addAttribute(StatusUpdateType.SP, player.getSp());
		player.sendPacket(su);
		
		SystemMessage sm = new SystemMessage(SystemMessage.LEARNED_SKILL_S1);
		sm.addSkillName(id);
		player.sendPacket(sm);
		sm = null;
		
		// update toogles
		if (skill.isToggle())
		{
			if (player.getEffect(skill) != null)
			{
				player.getEffect(skill).exit(false);
				skill.getEffects(player, player);
			}
		}
		
		// update all the shortcuts to this skill
		if (level > 1)
		{
			for (PcShortCutsInstance sc : player.getShortCuts().getAllShortCuts())
			{
				if (sc == null)
				{
					continue;
				}
				
				if ((sc.getId() == id) && (sc.getType() == PcShortCutsType.SKILL))
				{
					player.getShortCuts().registerShortCut(new PcShortCutsInstance(sc.getSlot(), sc.getPage(), sc.getType(), sc.getId(), level, 1), true);
				}
			}
		}
		
		if (npc.isFisher())
		{
			ScriptState qs = player.getScriptState("Fisherman");
			if (qs != null)
			{
				qs.getQuest().notifyEvent("FishSkillList", null, player);
			}
		}
		else if (npc.isTrainer())
		{
			ScriptState qs = player.getScriptState("Trainer");
			if (qs != null)
			{
				qs.getQuest().notifyEvent("TrainerSkillList", npc, player);
			}
		}
		
		if ((id >= 1368) && (id <= 1372)) // if skill is expand sendpacket :)
		{
			player.sendPacket(new ExStorageMaxCount(player));
		}
	}
}
