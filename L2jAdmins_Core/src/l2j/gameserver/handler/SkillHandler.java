package l2j.gameserver.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import l2j.gameserver.handler.skill.SkillBalanceLife;
import l2j.gameserver.handler.skill.SkillBeastFeed;
import l2j.gameserver.handler.skill.SkillBlow;
import l2j.gameserver.handler.skill.SkillBuffs;
import l2j.gameserver.handler.skill.SkillCharge;
import l2j.gameserver.handler.skill.SkillChargeDmg;
import l2j.gameserver.handler.skill.SkillCombatPointHeal;
import l2j.gameserver.handler.skill.SkillCpDamPercent;
import l2j.gameserver.handler.skill.SkillCraft;
import l2j.gameserver.handler.skill.SkillCreateItem;
import l2j.gameserver.handler.skill.SkillCubic;
import l2j.gameserver.handler.skill.SkillDebuff;
import l2j.gameserver.handler.skill.SkillDeluxeKey;
import l2j.gameserver.handler.skill.SkillDisablers;
import l2j.gameserver.handler.skill.SkillDrain;
import l2j.gameserver.handler.skill.SkillDrainSoul;
import l2j.gameserver.handler.skill.SkillDummy;
import l2j.gameserver.handler.skill.SkillEngrave;
import l2j.gameserver.handler.skill.SkillFishing;
import l2j.gameserver.handler.skill.SkillFishingSkill;
import l2j.gameserver.handler.skill.SkillHarvest;
import l2j.gameserver.handler.skill.SkillHeal;
import l2j.gameserver.handler.skill.SkillManaDam;
import l2j.gameserver.handler.skill.SkillManaHeal;
import l2j.gameserver.handler.skill.SkillMdam;
import l2j.gameserver.handler.skill.SkillPdam;
import l2j.gameserver.handler.skill.SkillRecall;
import l2j.gameserver.handler.skill.SkillSeed;
import l2j.gameserver.handler.skill.SkillSiegeFlag;
import l2j.gameserver.handler.skill.SkillSow;
import l2j.gameserver.handler.skill.SkillSpoil;
import l2j.gameserver.handler.skill.SkillStrSiegeAssault;
import l2j.gameserver.handler.skill.SkillSummon;
import l2j.gameserver.handler.skill.SkillSweep;
import l2j.gameserver.handler.skill.SkillUnlock;
import l2j.gameserver.handler.skill.TeleportPc;
import l2j.gameserver.model.L2Object;
import l2j.gameserver.model.actor.L2Character;
import l2j.gameserver.model.actor.instance.L2PcInstance;
import l2j.gameserver.model.skills.Skill;
import l2j.gameserver.model.skills.enums.SkillType;

public class SkillHandler
{
	// Interface
	public interface ISkillHandler
	{
		public void useSkill(L2Character activeChar, Skill skill, List<L2Object> targets);
		
		public boolean checkUseMagicConditions(L2PcInstance activeChar, L2Object target, Skill skill);
		
		public SkillType[] getSkillIds();
	}
	
	// Log
	public static final Logger LOG = Logger.getLogger(SkillHandler.class.getName());
	// Instance
	private static final Map<SkillType, ISkillHandler> datatable = new HashMap<>();
	
	/**
	 * Only used on load GameServer
	 */
	public void init()
	{
		registerHandler(new SkillBalanceLife());
		registerHandler(new SkillBeastFeed());
		registerHandler(new SkillBlow());
		registerHandler(new SkillBuffs());
		registerHandler(new SkillCharge());
		registerHandler(new SkillChargeDmg());
		registerHandler(new SkillCombatPointHeal());
		registerHandler(new SkillCpDamPercent());
		registerHandler(new SkillCraft());
		registerHandler(new SkillCreateItem());
		registerHandler(new SkillCubic());
		registerHandler(new SkillDebuff());
		registerHandler(new SkillDeluxeKey());
		registerHandler(new SkillDisablers());
		registerHandler(new SkillDrain());
		registerHandler(new SkillDrainSoul());
		registerHandler(new SkillDummy());
		registerHandler(new SkillEngrave());
		registerHandler(new SkillFishing());
		registerHandler(new SkillFishingSkill());
		registerHandler(new SkillHarvest());
		registerHandler(new SkillHeal());
		registerHandler(new SkillManaDam());
		registerHandler(new SkillManaHeal());
		registerHandler(new SkillMdam());
		registerHandler(new SkillPdam());
		registerHandler(new SkillRecall());
		registerHandler(new SkillSeed());
		registerHandler(new SkillSiegeFlag());
		registerHandler(new SkillSow());
		registerHandler(new SkillSpoil());
		registerHandler(new SkillStrSiegeAssault());
		registerHandler(new SkillSummon());
		registerHandler(new SkillSweep());
		registerHandler(new SkillUnlock());
		registerHandler(new TeleportPc());
		
		LOG.info("SkillHandler: load " + size() + " handlers");
	}
	
	public static void registerHandler(ISkillHandler handler)
	{
		for (SkillType t : handler.getSkillIds())
		{
			datatable.put(t, handler);
		}
	}
	
	public static ISkillHandler getHandler(SkillType skillType)
	{
		return datatable.get(skillType);
	}
	
	public static int size()
	{
		return datatable.size();
	}
	
	public static SkillHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SkillHandler INSTANCE = new SkillHandler();
	}
}
